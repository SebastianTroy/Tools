package tools.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.net.Socket;

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
						socket = new Socket(hostAddress, port);
					}
				catch (Exception e)
					{
						WindowTools.debugWindow("Could not connect to server/n");
						e.printStackTrace();
					}

				if (socket.isConnected())
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
						ObjectInputStream stream_reader = new ObjectInputStream(socket.getInputStream());
						while (true)
							{
								processObject((DataType) stream_reader.readObject());
							}
					}
				catch (IOException | UncheckedIOException | ClassNotFoundException e)
					{
						e.printStackTrace();
					}
			}

		/**
		 * Sends an object to a {@link TServer}. Each {@link TClient} linked to the server will receive a copy of this object, including this client.
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
		protected abstract void processObject(DataType object);

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