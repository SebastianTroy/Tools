package tools.server;

import java.io.Serializable;

/**
 * This class bundles the Objects being sent from the {@link TServer} to the {@link TClient} in a handy way so that clients can know extra information about the
 * Object they just recieved. Namely whether it was sent to only them and the Unique ID of the sender.
 * 
 * @author Sebastian Troy
 */
public class TPacket implements Serializable
	{
		private static final long serialVersionUID = 1L;

		final long uniqueID;
		final Object object;
		final boolean personal;

		/**
		 * 
		 * @param uniqueID
		 *            - The unique ID of the sender.
		 * @param object
		 *            - The Object to be sent to the client.
		 * @param personal
		 *            - <code>true</code> if the (T)Object was sent only to this client.
		 */
		TPacket(long uniqueID, Object object, boolean personal)
			{
				this.uniqueID = uniqueID;
				this.object = object;
				this.personal = personal;
			}
	}