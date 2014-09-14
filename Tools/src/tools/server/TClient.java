package tools.server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import tools.WindowTools;

/**
 * A simple client which will connect to a {@link TServer} and listen for objects from the server, until the server closes.
 * 
 * @author Sebastian Troy
 *
 * @param <DataType>
 *            - The class of object which the client will expect from the {@link TServer}.
 */
public abstract class TClient<DataType> implements Runnable
	{
		private Socket socket;
		private boolean isConnected = true;
		private long uniqueID = -1L;

		/**
		 * 
		 * @param hostAddress
		 *            - The ip address of the host you wish to connect to.
		 * @param port
		 *            - The port at which the {@link TServer} is listening.
		 */
		public TClient(String hostAddress, int port)
			{
				try
					{
						// Connect t server
						socket = new Socket(hostAddress, port);
						// Check that the server actually
						ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
						oos.writeObject(new TString("Testing"));
						oos.flush();
					}
				catch (ConnectException e)
					{
						WindowTools.informationWindow("Server refused connection:\n\nMake sure that the server is running &\nif connecting to an external IP address,\nthat they have port forwading set up.\n\nHelp:\nhttp://troydev.proggle.net/projects-hex-nations.php", "Warning");
						isConnected = false;
					}
				catch (UnknownHostException e)
					{
						/* Do nothing, incorrect host name entered */
						isConnected = false;
						WindowTools.debugWindow("Invalid host name.");
					}
				catch (Exception e)
					{
						isConnected = false;
						WindowTools.debugWindow("Could not connect to server.");
						e.printStackTrace();
					}

				if (isConnected)
					new Thread(this).start();
			}

		/**
		 * While connected to the server, wait for messages, then add them to a queue so that they can be requested by another class.
		 */
		@SuppressWarnings("unchecked")
		@Override
		public final void run()
			{
				try
					{
						while (true)
							{
								ObjectInputStream stream_reader = new ObjectInputStream(socket.getInputStream());

								TPacket packet = (TPacket) stream_reader.readObject();
								Object object = packet.object;

								// If the object is a secret message from the server
								if (packet.personal && object instanceof TString)
									{
										TString objectString = (TString) object;
										// These pings are the servers way of knowing we are still here
										if (objectString.string.equals("Server: Are_You_There?"))
											// Confirms presence of client
											sendObject(new TString("Client_Still_Here_0123456789"));
										// This is the servers way of sending us our uniqueID when we join
										if (objectString.string.startsWith("ID:"))
											// Extract the unique ID number from the string
											uniqueID = Long.parseLong(objectString.string.substring(3));
										continue;
									}

								processObject(packet.uniqueID, (DataType) object, packet.personal);
							}
					}
				catch (EOFException | SocketException e)
					{
						// If the client has disconnected while we are waiting for an object
						if (e.toString().equals("java.net.SocketException: Connection reset"))
							WindowTools.informationWindow("Server has Disconnected", "WARNING");
					}
				catch (IOException | UncheckedIOException | ClassNotFoundException e)
					{
						e.printStackTrace();
					}
			}

		/**
		 * Sends an object to a {@link TServer}. Each {@link TClient} linked to the server will receive a copy of this object, including
		 * this client.
		 * 
		 * @param object
		 *            - The object to send to the server
		 */
		protected final void sendObject(Object object)
			{
				// Ignore null objects
				if (object == null)
					return;
				try
					{
						ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
						oos.writeObject(object);
						oos.flush();
					}
				catch (IOException e)
					{
						e.printStackTrace();
					}
			}

		/**
		 * Each time an object is returned from the server, this method is called.
		 * 
		 * @param object
		 *            - The object sent to the client from the server
		 */
		protected abstract void processObject(long senderID, DataType object, boolean personal);

		/**
		 * @return - true if there is a valid connection to a {@link TServer} socket.
		 */
		public final boolean isConnected()
			{
				return (isConnected && socket.isConnected());
			}

		public final void disconnect()
			{
				sendObject(new TString("Client_Disconnected_0123456789"));
			}

		/**
		 * @return - The uniqueID assigned to this client by the server (or -1L if not yet recieved).
		 */
		public final long getUniqueID()
			{
				return uniqueID;
			}

		/**
		 * @return - Returns the ip address of the {@link TClient} socket as a String.
		 */
		public final String getClientAddress()
			{
				return socket.getLocalAddress().getHostAddress();
			}

		/**
		 * @return - Returns the ip address of the {@link TServer} socket as a String.
		 */
		public final String getServerAddress()
			{
				return socket.getInetAddress().getHostAddress();
			}
	}