package tools.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.LinkedBlockingQueue;

import tools.Rand;
import tools.WindowTools;

/**
 * Once started this Server will continue to listen for {@link TClient}s until:
 * <ul>
 * <li>The program which evoked it calls <code>System.exit(0);</code></li>
 * <li>The program calls {@link TServer#closeServer()}.</li>
 * </ul>
 * 
 * @author Sebastian Troy
 */
public abstract class TServer<DataType> implements Runnable
	{
		private ServerSocket serverSocket;

		protected final LinkedBlockingQueue<Connection> clients = new LinkedBlockingQueue<Connection>();

		private final Thread thread;
		private boolean running = false;
		protected boolean allowConnections = true;

		/**
		 * Starts the server with a {@link ServerSocket} listening to the specified port.
		 * 
		 * @param port
		 *            - The port to which {@link TClient}s should connect.
		 */
		protected TServer(int port)
			{
				try
					{
						serverSocket = new ServerSocket();
						serverSocket.setReuseAddress(true);
						serverSocket.bind(new InetSocketAddress(port));
						running = true;
					}
				catch (IOException e)
					{
						WindowTools.informationWindow("Server failed to start", "Error");
						e.printStackTrace();
					}

				if (running)
					{
						// Start the chat server on a new thread
						thread = new Thread(this);
						thread.start();
					}
				else
					thread = null;
			}

		/**
		 * <strong>Warning: </strong>This method should not be called explicitly.<br/>
		 * Listens out for new {@link TClient}s forming a connection, and allocates them a new {@link Connection}.
		 */
		@Override
		public final void run()
			{
				while (running)
					try
						{
							// Wait for someone to connect to us
							Socket socket = serverSocket.accept();

							// The first connection is always allowed, after then, each connection decides if another is allowed
							if (allowConnections)
								{
									Connection connection = new Connection(socket);
									while (true)
										{
											for (Connection c : clients)
												if (c.uniqueID == connection.uniqueID)
													{
														connection = new Connection(socket);
														continue;
													}
											break;
										}
									// Start a thread to deal with this new connection
									Thread thread = new Thread(connection);
									thread.start();

									// Add the new connection
									clients.add(connection);

									// Send a message to the client to inform it of its unique ID
									sendToClient(new TString("ID:" + connection.uniqueID), connection.uniqueID);

									// Notify the server of the new connection and ask if another connection is allowed
									allowConnections = clientConnected(connection.uniqueID);
								}
							else
								socket.close();
						}
					catch (SocketException e)
						{
							// Do nothing, this is expected to occur whenever the server is stopped
						}
					catch (IOException e)
						{
							e.printStackTrace();
						}
			}

		/**
		 * @return - <code>true</code> if the {@link TServer} started successfully and has not yet been closed.
		 */
		public final boolean isRunning()
			{
				return running;
			}

		/**
		 * @return - <code>true</code> if this server is currently listening for and connecting to {@link TClient}s.
		 */
		public final boolean isAcceptingClients()
			{
				return allowConnections;
			}

		/**
		 * @param accept
		 *            - <code>true</code> if this server should listen for and connect to {@link TClient}s.
		 */
		public final void setAcceptClients(boolean accept)
			{
				allowConnections = accept;
			}

		/**
		 * If you want to force a client to disconnect, call this method
		 * 
		 * @param clientID
		 *            - The UniqueID of the client you wish to kick from the server.
		 * @param reason
		 *            - A final message to send to the client, explaining why they were disconnected.
		 */
		public final void kickClient(long clientID, String reason)
			{
				sendToClient(new TString("Kicked: " + reason), clientID);
			}

		/**
		 * <strong>Warning: </strong>This method is called by multiple threads so when dealing with the object, steps should be made to
		 * ensure safe concurrency.
		 * 
		 * @param senderID
		 *            - The uniqueID of the client that sent the object.
		 * @param object
		 *            - The object sent to the server by the client.
		 */
		protected abstract void processObject(long senderID, DataType object);

		/**
		 * This method causes the server to finish sending its current message and then to finish executing.
		 */
		protected final void closeServer()
			{
				// Tell the Server to stop processing new connections
				running = false;

				try
					{
						// Close the sever (will cause
						serverSocket.close();
						for (Connection c : clients)
							c.socket.close();

						// Join this thread to the one that called this method
						if (thread != null)
							thread.join();
					}
				catch (IOException | InterruptedException e)
					{
						e.printStackTrace();
					}
			}

		/**
		 * This method sends a copy of specified object to all connected {@link TClient}s. This method is thread safe.
		 * 
		 * @param object
		 *            - An Object which each {@link TClient} will receive a copy of.
		 * @note - TClient&ltT>s can only accept Objects of type T.
		 */
		protected final synchronized void sendToAll(long senderID, DataType object)
			{
				for (Connection c : clients)
					if (c.acceptingObjects)
						try
							{
								ObjectOutputStream oos = new ObjectOutputStream(c.socket.getOutputStream());
								oos.writeObject(new TPacket(senderID, object, false));
								oos.flush();
							}
						catch (Exception e)
							{
								e.printStackTrace();
							}
			}

		/**
		 * This method sends a copy of specified object to a specified {@link TClient}. This method is thread safe.
		 * 
		 * @param object
		 *            - An Object which the {@link TClient} will receive a copy of.
		 * @param clientID
		 *            - The unique ID of the {@link TClient} to which the object should be sent.
		 */
		protected final synchronized void sendToClient(long senderID, DataType object, long clientID)
			{
				for (Connection c : clients)
					if (c.uniqueID == clientID && c.acceptingObjects)
						try
							{
								ObjectOutputStream oos = new ObjectOutputStream(c.socket.getOutputStream());
								oos.writeObject(new TPacket(senderID, object, true));
								oos.flush();
								break;
							}
						catch (Exception e)
							{
								e.printStackTrace();
							}
			}

		/**
		 * This method is used only by the server to communicate with the {@link TClient} in a way that is hidden from the end user. This
		 * method is thread safe.
		 * 
		 * @param hiddenMessage
		 *            - The message to be sent to the client
		 * @param clientID
		 *            - The uniqueID of the client which will receive the message
		 */
		private final synchronized void sendToClient(TString hiddenMessage, long clientID)
			{
				for (Connection c : clients)
					if (c.uniqueID == clientID && c.acceptingObjects)
						try
							{
								ObjectOutputStream oos = new ObjectOutputStream(c.socket.getOutputStream());
								oos.writeObject(new TPacket(0L, hiddenMessage, true));
								oos.flush();
								break;
							}
						catch (Exception e)
							{
								e.printStackTrace();
							}
			}

		/**
		 * Each time a client connects, this method is called. If the server should be ready for a new connection immediately, this should
		 * return <code>true</code>. If not the server can be told to accept connections again at a later time.
		 * 
		 * @param clientIP
		 *            - A string of the IP address of the client
		 * @return - <code>true</code> if the server should listen for a new connection.
		 */
		protected abstract boolean clientConnected(long uniqueID);

		/**
		 * Each time a client disconnects this method is called.
		 * 
		 * @param clientIP
		 */
		protected abstract void clientDisconnected(long uniqueID);

		/**
		 * One of these classes is created for each client that connects to the chat server. It listens for input continuously and when it
		 * receives a message, sends it out to every connected client.
		 * 
		 * @author Sebastian Troy
		 */
		protected class Connection extends Thread
			{
				public final long uniqueID = Rand.long_(1L, Long.MAX_VALUE);
				protected final Socket socket;
				private boolean acceptingObjects = true, confirmedConnection = true;

				private Connection(Socket socket)
					{
						this.socket = socket;
					}

				private final void disconnected()
					{
						acceptingObjects = false;
						clients.remove(this);

						clientDisconnected(uniqueID);
					}

				@SuppressWarnings("unchecked")
				@Override
				public final void run()
					{
						try
							{
								// If no data is recieved for 5 seconds, stop waiting
								socket.setSoTimeout(5000);

								while (true)
									try
										{
											// prepare to receive String inputs from the clients
											ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

											Object object = ois.readObject();

											// Check to see if client has disconnected or is confirming presence
											if (object instanceof TString)
												{
													TString objectString = (TString) object;
													if (objectString.string.equals("Client_Disconnected_0123456789"))
														{
															disconnected();
															socket.close();
															break;
														}
													else if (objectString.string.equals("Client_Still_Here_0123456789"))
														{
															confirmedConnection = true;
														}
													continue;
												}
											// Tell the server that an object was sent, and by whom
											processObject(uniqueID, (DataType) object);

											confirmedConnection = true;
										}
									// We are expecting these every 5 seconds or so, and don't want to leave the while loop
									catch (SocketTimeoutException e)
										{
											// no data recieved for 5 seconds, check if client still connected
											if (confirmedConnection)
												{
													confirmedConnection = false;
													sendToClient(new TString("Server: Are_You_There?"), uniqueID);
												}
											else
												{
													disconnected();
												}
										}
							}
						catch (SocketException e)
							{
								// If the client has disconnected while we are waiting for an object
								if (e.toString().equals("java.net.SocketException: Connection reset"))
									disconnected();
								// This is expected to occur whenever the server is stopped
								// Only print if server should still be running
								else if (running)
									e.printStackTrace();
							}
						catch (IOException | ClassNotFoundException e)
							{
								e.printStackTrace();
							}
					}
			}
	}