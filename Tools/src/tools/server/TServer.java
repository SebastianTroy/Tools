package tools.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

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

		private final ArrayList<Connection> clients = new ArrayList<Connection>();

		private final Thread thread;
		private boolean running = true;

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
					}
				catch (IOException e)
					{
						WindowTools.informationWindow("Server failed to start", "Error");
						running = false;
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
						try
							{
								// Wait for someone to connect to us
								Socket socket = serverSocket.accept();
								// Create a new connection
								Connection connection = new Connection(socket);
								clients.add(connection);

								// Start a thread to deal with this new connection
								Thread thread = new Thread(new Connection(socket));
								thread.start();
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
						thread.join();
					}
				catch (IOException | InterruptedException e)
					{
						e.printStackTrace();
					}
			}

		/**
		 * One of these classes is created for each client that connects to the chat server. It listens for input continuously and when it recieves a message,
		 * sends it out to every connected client.
		 * 
		 * @author Sebastian Troy
		 */
		private class Connection extends Thread
			{
				private final Socket socket;

				private Connection(Socket socket)
					{
						this.socket = socket;
					}

				@Override
				public final void run()
					{
						try
							{
								while (true)
									{
										// prepare to receive String inputs from the clients
										ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

										// Wait for another object to be sent then pass to every client (including the one that sent it)
										sendToAll(ois.readObject());
									}
							}
						catch (SocketException e)
							{
								// Do nothing, this is expected to occur whenever the server is stopped
							}
						catch (IOException | ClassNotFoundException e)
							{
								e.printStackTrace();
							}
					}
			}
	}
