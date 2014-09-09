package tools.server;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import tools.Rand;

/**
 * A simple example implementation of {@link TClient}.
 * <p>
 * This class represents a very simple client for an instant messenger. It sends and receives Strings from the server but relies on another class to provide
 * them and to process/display them.
 * 
 * @author Sebastian Troy
 */
public class ChatClient extends TClient<String>
	{
		private LinkedBlockingQueue<String> messages = new LinkedBlockingQueue<String>();
		private String clientName, clientID;

		/**
		 * Connect to the host at the specified address and give the client a random 5 letter digit as a name.
		 * 
		 * @param hostAddress
		 *            - The ip address of the host you wish to connect to.
		 */
		public ChatClient(String hostAddress)
			{
				this(hostAddress, "" + Rand.int_(10000, 100000));
			}

		/**
		 * Connect to the specified host and give the client a specified name
		 * 
		 * @param hostAddress
		 *            - The ip address of the host you wish to connect to.
		 * @param clientName
		 *            - The String that will be prepended onto every message in the form "clientName:message"
		 */
		public ChatClient(String hostAddress, String clientName)
			{
				super(hostAddress, ChatServer.PORT);
				clientName.replace(':', '.');
				this.clientName = clientName;
			}

		/**
		 * Whenever we receive a String from the server, stick it into an array until it is asked for.
		 */
		@Override
		protected final void processObject(long senderID, String message, boolean personal)
			{
				// If server is contacting only us, the message is our unique ID
				if (personal)
					clientID = message;
				else
					messages.add(message);
			}

		/**
		 * Sends a message to the server, prepending the ip address and the {@link ChatClient#clientName} to the message.
		 * <p>
		 * "uniqueID:client name:message"
		 * 
		 * @param message
		 *            - The message to be sent.
		 * @note - ignores empty and null Strings
		 */
		public final void sendMessage(String message)
			{
				// Ignore null or empty messages
				if (message == null || message.length() < 2)
					return;

				message = clientID + ":" + clientName + ":" + message;
				sendObject(message);
			}

		/**
		 * @return - Each String returned represents a unique message. The format of each message is: "ipaddress:clientname:\n message"
		 */
		public final ArrayList<String> getMessages()
			{
				ArrayList<String> m = new ArrayList<String>();
				messages.drainTo(m);
				return m;
			}

		public final void setClientName(String clientName)
			{
				this.clientName = clientName;
			}
	}