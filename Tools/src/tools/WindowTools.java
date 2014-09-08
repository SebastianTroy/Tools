package tools;

import javax.swing.JOptionPane;

public class WindowTools
	{
		/**
		 * @param message
		 *            - The message/instructions to display to the user.
		 * @param initialValue
		 *            - The initial text to insert into the text field.
		 * @return - A user defined String, or if the user cancels the operation, the initialValue.
		 */
		public static String getUserStringWindow(String message, String initialValue)
			{
				String userValue = JOptionPane.showInputDialog(null, message, initialValue);

				if (userValue == null)
					return initialValue;
				else
					return userValue;
			}

		/**
		 * @param message
		 *            - The yes/no question to ask the user
		 * @param heading
		 *            - The message to display in the titlebar of the window
		 * @return - <code>true</code> if the user selects 'yes' otherwise <code>false</code>.
		 */
		public static boolean confirmationWindow(String message, String heading)
			{
				int answer = JOptionPane.showConfirmDialog(null, message, heading, JOptionPane.YES_NO_OPTION);

				if (answer == JOptionPane.YES_OPTION)
					{
						return true;
					}
				else
					return false;
			}

		/**
		 * This method simply displays a message to the user until they acknowledge it.
		 * 
		 * @param message - Message to display
		 * @param title - Title of the window
		 */
		public static void informationWindow(String message, String title)
			{
				JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
			}

		/**
		 * Pops up a message describing the problem and giving the location of the problem.
		 * 
		 * @param message
		 *            - The message should convey any information helpful to a debugger
		 */
		public static void debugWindow(String message)
			{
				JOptionPane.showMessageDialog(null, message, Thread.currentThread().getStackTrace()[3].toString(), JOptionPane.ERROR_MESSAGE);

				System.out.println("DEBUG: " + Thread.currentThread().getStackTrace()[3]);
				System.out.println(message);
			}

		/**
		 * A message pops up asking the user if they wish to exit the program, if they do then the program exits, if they do not the message disappears.
		 */
		public static void exitConfirmationWindow()
			{
				int answer = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit?", "Exit program?", JOptionPane.YES_NO_OPTION);
				if (answer == JOptionPane.YES_OPTION)
					{
						System.exit(0);
					}
			}
	}
