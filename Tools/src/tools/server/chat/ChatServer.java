package tools.server.chat;

import java.util.ArrayList;

import tools.server.TServer;

/**
 * A simple example implementation of a {@link TServer}.
 * 
 * @author Sebastian Troy
 */
public class ChatServer extends TServer<String>
	{
		public static final int PORT = 10301;
		
		/**
		 * Keep a list of all messages sent this session so we can send them to clients as they join.
		 */
		private final ArrayList<String> messageLog = new ArrayList<String>();

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
					sendToAll(0L, "Server has disconnected");

				super.closeServer();
			}

		/**
		 * Send a private message to the client, informing it of the private ID it was allocated. Now the client will always send out messages starting with its
		 * privateID.
		 */
		@Override
		protected boolean clientConnected(long uniqueID)
			{
				// Inform the client of its uniqueID (0L is an ID reserved for the server).
				sendToClient(0L, "" + uniqueID, uniqueID);
				// Get the client up to date with past messages
				for (String s : messageLog)
					sendToClient(-1L, s, uniqueID);
				return true;
			}

		/**
		 * Do nothing, rely on users to say goodbye
		 */
		@Override
		protected void clientDisconnected(long uniqueID)
			{}

		@Override
		protected void processObject(long senderID, String message)
			{
				messageLog.add(message);
				sendToAll(senderID, message);
			}
	}