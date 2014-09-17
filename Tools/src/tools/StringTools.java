package tools;

import java.awt.FontMetrics;
import java.util.ArrayList;

public class StringTools
	{
		/**
		 * Takes a string and splits it into lines with a maximum length. It attempts to split the string on space characters however if a
		 * single word is longer than a line it will be split over multiple lines.
		 * 
		 * @param fm
		 *            - The font metrics used to draw the string
		 * @param string
		 *            - The string to be split into multiple lines
		 * @param lineWidth
		 *            - The maximum width, in pixels that a line can be
		 * @return An array of {@link String}s, each equal to or less than the maximum line width. If they are all concatenated together,
		 *         the result is identical to the original String.
		 */
		public static final String[] wrapString(FontMetrics fm, String string, double lineWidth)
			{
				String splitString[];

				ArrayList<Integer> newLineIndices = new ArrayList<Integer>((fm.stringWidth(string) / (int) lineWidth) + 1);

				newLineIndices.add(0);
				int currentLine = 1;
				while (true)
					{
						int tempIndex = newLineIndices.get(currentLine - 1);
						double length = 0;
						// Work out where to place the next 'new line' character
						while (tempIndex < string.length() && length - 2 < lineWidth)
								{
									length = fm.stringWidth(string.substring(newLineIndices.get(currentLine - 1), tempIndex));
									tempIndex++;
								}
						// If this isn't the last line
						if (tempIndex < string.length())
							{
								// Make sure the lines only wrap after spaces
								int lastSpaceIndex = string.substring(0, tempIndex).lastIndexOf(' ');
								// If a space was found && the space is on the most recent line
								if (lastSpaceIndex != -1 && lastSpaceIndex > newLineIndices.get(currentLine - 1))
									tempIndex = lastSpaceIndex + 1;

								newLineIndices.add(tempIndex);
								currentLine++;
							}
						else
							// We have finished processing the message
							{
								newLineIndices.add(tempIndex);
								break;
							}
					}

				splitString = new String[newLineIndices.size() - 1];

				for (int i = 0; i < splitString.length; i++)
					{
						splitString[i] = string.substring(newLineIndices.get(i), newLineIndices.get(i + 1));
					}

				return splitString;
			}
	}
