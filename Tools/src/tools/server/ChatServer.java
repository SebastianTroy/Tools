package tools.server;

/**
 * A simple example implementation of a {@link TServer}.
 * 
 * @author Sebastian Troy
 */
public class ChatServer extends TServer
	{
		public static final int PORT = 10301;

		public ChatServer()
			{
				super(PORT);
			}

		/**
		 * @param notify
		 *            - if <code>true</code> A message is sent to all connected clients, notifying them that the server has closed.
		 */
		public final void closeServer(boolean notify)
			{
				if (notify)
					sendToAll("Server has disconnected");

				super.closeServer();
			}

		/**
		 * Not interested in tracking which IP's have joined to us, simply return that we are happy for more clients to join.
		 */
		@Override
		protected boolean clientConnected(String clientIP)
			{
				return true;
			}

		/**
		 * Tell everyone someone disconnected
		 */
		@Override
		protected void clientDisconnected(String clientIP)
			{
				sendToAll(clientIP + "::||disconnected|||");
			}
	}