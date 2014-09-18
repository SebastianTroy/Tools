package tools.server.chat;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import tComponents.TComponent;
import tComponents.components.TCollection;
import tComponents.components.TMenu;
import tComponents.components.TTextField;
import tools.StringTools;
import tools.server.TClient;
import tools.server.TServer;

/**
 * A class designed to work with the {@link TServer} & {@link TClient} example classes. It is also a {@link TComponent} and can therefore
 * easily be included in any program built around the TCode library.
 * 
 * @author Sebastian Troy
 */
public class TChatBox extends TCollection implements KeyListener
	{
		// Chat variables
		private MessageDisplay messageDisplay;
		private TTextField messageInput;
		private String lastSenderID = "0", lastSenderName = "xxx";

		private final ChatClient client;
		private Font font;

		public TChatBox(ChatClient client)
			{
				this(0, 0, 100, 100, client);
			}

		public TChatBox(double x, double y, double width, double height, ChatClient client)
			{
				super(x, y, width, height);

				this.client = client;
				font = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).getGraphics().getFont();

				messageDisplay = new MessageDisplay(x, y, width, height - 23, TMenu.VERTICAL);
				messageDisplay.setBackgroundColour(Color.WHITE);
				messageDisplay.setTComponentAlignment(TMenu.ALIGN_START);
				messageDisplay.setBorderSize(0);
				messageDisplay.setTComponentSpacing(0);

				messageInput = new TTextField(getXD() - 1, getYI() + (getHeightD() - 20), getWidthD() + 1, 20, "Click here to type");

				add(messageDisplay);
				add(messageInput);
			}

		@Override
		protected void addedToComponent()
			{
				super.addedToComponent();
				parentComponent.addKeyListener(this);
			}

		@Override
		protected void removedFromComponent()
			{
				super.removedFromComponent();
				parentComponent.removeKeyListener(this);
			}

		public final void setFont(Font newFont)
			{
				font = newFont;
				setWidth(getWidthD());
			}

		@Override
		public final void setDimensions(double width, double height)
			{
				setWidth(width);
				setHeight(height);
			}

		@Override
		public final void setWidth(double width)
			{
				super.setWidth(width);
				messageDisplay.setWidth(width);
				messageInput.setWidth(width);
			}

		@Override
		public final void setHeight(double height)
			{
				super.setHeight(height);
				messageDisplay.setHeight(height - 23);
				messageInput.setY(getXD() + (height - messageInput.getHeightD()));
			}

		@Override
		public final void render(Graphics2D g)
			{
				// Get any messages sent since last tick
				for (String str : client.getMessages())
					{
						Message message = new Message(str);
						if (message.uniqueID.equals(lastSenderID))
							// User has changed their name
							if (!message.senderName.equals(lastSenderName))
								{
									// Save the new senderName
									String newName = new String(message.senderName);
									// Give the message a name that indicates the change
									message.senderName = lastSenderName + " -> " + message.senderName;
									// Look for the new name next message
									lastSenderName = newName;
								}
							else
								message.setDisplayHeading(false);
						else
							{
								lastSenderID = message.uniqueID;
								lastSenderName = message.senderName;
							}

						messageDisplay.add(message, false);
						messageDisplay.setScrollBarScrollPercent(100);
					}

				messageDisplay.render(g);
				messageInput.render(g);
			}

		@Override
		public final void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					{
						client.sendMessage(messageInput.getText());
						messageInput.clearText();
					}
			}

		@Override
		public void keyTyped(KeyEvent e)
			{}

		@Override
		public void keyReleased(KeyEvent e)
			{}

		private class Message extends TComponent
			{
				private final String uniqueID, message;
				private String senderName;
				private String[] splitMessage;
				private boolean displayHeading = true;
				private int lineHeight;

				private Message(String message)
					{
						String[] subMessages = message.split(":", 3);
						uniqueID = subMessages[0];
						senderName = subMessages[1];
						this.message = subMessages[2];

						setWidth(messageDisplay.getWidthD());

						wrapMessage();
					}

				private final void wrapMessage()
					{
						FontMetrics fm = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).getGraphics().getFontMetrics(font);
						lineHeight = fm.getHeight();

						splitMessage = StringTools.wrapString(fm, message, getWidthI() - 25);

						setHeight((splitMessage.length + 1) * lineHeight);
					}

				@Override
				public void render(Graphics2D g)
					{
						g.setFont(font);

						g.setColor(Color.GRAY);
						if (displayHeading)
							{
								g.drawString(senderName + ":", getXI() + 5, getYI() + lineHeight);
								g.drawLine(getXI() + 5, getYI() + 5, getXI() + getWidthI() - 10, getYI() + 5);
							}

						g.setColor(Color.BLACK);
						for (int i = 0; i < this.splitMessage.length;)
							g.drawString(splitMessage[i], getXI() + 10, getYI() + (displayHeading ? lineHeight - 1 : -1) + (lineHeight * ++i));
					}

				private final void setDisplayHeading(boolean displayHeading)
					{
						// If this is a change, adjust the height of the message accordingly
						if (this.displayHeading != displayHeading)
							if (displayHeading)
								setHeight(getHeightD() + lineHeight);
							else
								setHeight(getHeightD() - (lineHeight));
						this.displayHeading = displayHeading;
					}

				@Override
				public final void setWidth(double width)
					{
						super.setWidth(width - 20);
						wrapMessage();
					}

				@Override
				protected void addedToComponent()
					{}

				@Override
				protected void removedFromComponent()
					{}
			}

		private class MessageDisplay extends TMenu
			{

				public MessageDisplay(double x, double y, double width, double height, boolean isVertical)
					{
						super(x, y, width, height, isVertical);
					}

				@Override
				public final void add(TComponent component, boolean resize)
					{
						super.add(component, resize);
						setWidth(getWidthD());
					}

				@Override
				public final void setDimensions(double width, double height)
					{
						setWidth(width);
						super.setHeight(height);
					}

				@Override
				public final void setWidth(double width)
					{
						super.setWidth(width);
						ArrayList<TComponent> messages = new ArrayList<TComponent>();
						for (int i = 0; i < messages.size(); i++)
							messages.get(i).setWidth(width - (usingScrollBar ? 20 : 0));
					}
			}
	}