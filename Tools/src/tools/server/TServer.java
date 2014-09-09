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
public abstract class TServer implements Runnable
	{
		private ServerSocket serverSocket;

		private final LinkedBlockingQueue<Connection> clients = new LinkedBlockingQueue<Connection>();

		private final Thread thread;
		private boolean running = false, allowConnections = true;

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
					{
						// The first connection is always allowed, after then, each connection decides if another is allowed
						if (allowConnections)
							try
								{
									// Wait for someone to connect to us
									Socket socket = serverSocket.accept();
									
									Connection connection = new Connection(socket);
									
									// Create a new connection
									clients.add(connection);

									// Start a thread to deal with this new connection
									Thread thread = new Thread(connection);
									thread.start();
									// Notify the server of the new connection and ask if another connection is acceptable
									allowConnections = clientConnected(socket.getInetAddress().toString());
								}
							catch (SocketException e)
								{
									// Do nothing, this is expected to occur whenever the server is stopped
								}
							catch (IOException e)
								{
									e.printStackTrace();
								}
						else
							// Wait to see if we can accept connections in 5 seconds
							try
								{
									Thread.sleep(5000);
								}
							catch (InterruptedException e)
								{
									e.printStackTrace();
								}
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
		 * This method appends the ip address of the sender to the start of the message and then sends it to all connected clients.
		 * 
		 * @param object
		 *            - The message recieved from a client, which is to be pushed to all connected clients.
		 */
		protected final synchronized void sendToAll(Object object)
			{
				for (Connection c : clients)
					{
						if (c.acceptingObjects)
							try
								{
									ObjectOutputStream oos = new ObjectOutputStream(c.socket.getOutputStream());
									oos.writeObject(object);
									oos.flush();
								}
							catch (Exception e)
								{
									e.printStackTrace();
								}
					}
			}

		/**
		 * Each time a client connects, this method is called. If the server should be ready for a new connection immediately, this should return
		 * <code>true</code>. If not the server can be told to accept connections again at a later time.
		 * 
		 * @param clientIP
		 *            - A string of the IP address of the client
		 * @return - <code>true</code> if the server should listen for a new connection.
		 */
		protected abstract boolean clientConnected(String clientIP);

		/**
		 * Each time a client disconnects this method is called.
		 * 
		 * @param clientIP
		 */
		protected abstract void clientDisconnected(String clientIP);

		/**
		 * One of these classes is created for each client that connects to the chat server. It listens for input continuously and when it receives a message,
		 * sends it out to every connected client.
		 * 
		 * @author Sebastian Troy
		 */
		private class Connection extends Thread
			{
				private final Socket socket;
				private final String ipString;
				private boolean acceptingObjects = true, confirmedConnection = true;

				private Connection(Socket socket)
					{
						this.socket = socket;
						ipString = socket.getInetAddress().toString();
					}

				private final void disconnected()
					{
						acceptingObjects = false;
						clients.remove(this);
						
						clientDisconnected(ipString);
					}

				@Override
				public final void run()
					{
						try
							{
								// If no data is recieved for 5 seconds, stop waiting
								socket.setSoTimeout(5000);

								while (true)
									{
										try
											{
												if (!acceptingObjects)
													System.out.println("oops");
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
												// Wait for another object to be sent then pass to every client (including the one that sent it)
												sendToAll(object);

												confirmedConnection = true;
											}
										// We are expecting these every 5 seconds or so, and don't want to leave the while loop
										catch (SocketTimeoutException e)
											{
												// no data recieved for 5 seconds, check if client still connected
												if (confirmedConnection)
													{
														confirmedConnection = false;
														sendToAll(new TString("Server: Are_You_There?"));
													}
												else
													{
														disconnected();
													}
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
