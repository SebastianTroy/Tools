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
	}