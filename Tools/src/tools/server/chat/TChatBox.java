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
		private String lastSenderID = "0";

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
						if (new String(message.uniqueID).equals(lastSenderID))
								message.setDisplayHeading(false);
						else
							lastSenderID = new String(message.uniqueID);

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
				private final String uniqueID, user;
				private final String message;
				private String[] splitMessage;
				private boolean displayHeading = true;
				private int lineHeight;

				private Message(String message)
					{
						String[] subMessages = message.split(":", 3);
						uniqueID = subMessages[0];
						user = subMessages[1];
						this.message = subMessages[2];

						setWidth(messageDisplay.getWidthD() - 20);

						setlineIndices();
					}

				private final void setlineIndices()
					{
						FontMetrics fm = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).getGraphics().getFontMetrics(font);
						lineHeight = fm.getHeight();

						ArrayList<Integer> newLineIndices = new ArrayList<Integer>((fm.stringWidth(message) / (getWidthI() - 20)) + 1);

						newLineIndices.add(0);
						int currentLine = 1;
						while (true)
							{
								int tempIndex = newLineIndices.get(currentLine - 1);
								double length = 0;
								// Work out where to place the next 'new line' character
								while (length < getWidthI() - 22 && tempIndex < message.length())
									{
										length = fm.stringWidth(message.substring(newLineIndices.get(currentLine - 1), tempIndex));
										tempIndex++;
									}
								// If this isn't the last line
								if (tempIndex < message.length())
									{
										// Make sure the lines only wrap after spaces
										int lastSpaceIndex = message.substring(0, tempIndex).lastIndexOf(' ');
										// If a space was found && the space is on the most recent line
										if (lastSpaceIndex != -1 && lastSpaceIndex > newLineIndices.get(currentLine - 1))
											tempIndex = lastSpaceIndex + 1;

										newLineIndices.add(tempIndex);
										currentLine++;
									}
								else
									// We have finished processing the message
									{
										newLineIndices.add(tempIndex - 1);
										break;
									}
							}

						this.splitMessage = new String[newLineIndices.size() - 1];

						for (int i = 0; i < this.splitMessage.length; i++)
							{
								this.splitMessage[i] = message.substring(newLineIndices.get(i), newLineIndices.get(i + 1));
							}

						setHeight((this.splitMessage.length + 1) * lineHeight);
					}

				@Override
				public void render(Graphics2D g)
					{
						g.setFont(font);

						g.setColor(Color.GRAY);
						if (displayHeading)
							{
								g.drawString(user + ":", getXI() + 5, getYI() + lineHeight);
								g.drawLine(getXI() + 5, getYI() + 5, getXI() + getWidthI() - 10, getYI() + 5);
							}

						g.setColor(Color.BLACK);
						for (int i = 0; i < this.splitMessage.length;)
							g.drawString(splitMessage[i], getXI() + 10, getYI() + (displayHeading ?  lineHeight : 0) + (lineHeight * ++i));
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
						super.setWidth(width);
						setlineIndices();
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