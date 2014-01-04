package tools;

import java.awt.Color;

public class ColTools
	{
		/**
		 * Returns a completely random {@link Color} with no alpha value.
		 * 
		 * @return a {@link Color} object which holds a random colour.
		 */
		public static final Color randColour()
			{
				int red = (int) (RandTools.r.nextDouble() * 256);
				int green = (int) (RandTools.r.nextDouble() * 256);
				int blue = (int) (RandTools.r.nextDouble() * 256);
				Color randomColour = new Color(red, green, blue);
				return randomColour;
			}

		/**
		 * Returns a completely random {@link Color} with an alpha value.
		 * 
		 * @return a {@link Color} object which holds a random colour.
		 */
		public static final Color randAlphaColour()
			{
				int red = (int) (RandTools.r.nextDouble() * 256);
				int green = (int) (RandTools.r.nextDouble() * 256);
				int blue = (int) (RandTools.r.nextDouble() * 256);
				int alpha = (int) (RandTools.r.nextDouble() * 256);
				Color randomColour = new Color(red, green, blue, alpha);
				return randomColour;
			}

		/**
		 * This method takes three integer values and checks that they are
		 * within 0 - 255 before returning a {@link Color} composed of these
		 * values.
		 * 
		 * @param red
		 *            - the red component to be checked.
		 * @param green
		 *            - the green component to be checked.
		 * @param blue
		 *            - the blue component to be checked.
		 * @return - A RGB {@link Color}, composed of the above integers.
		 */
		public static final Color checkColour(int red, int green, int blue)
			{
				if (red < 0)
					red = 0;
				else if (red > 255)
					red = 255;

				if (green < 0)
					green = 0;
				else if (green > 255)
					green = 255;

				if (blue < 0)
					blue = 0;
				else if (blue > 255)
					blue = 255;

				return new Color(red, green, blue);
			}

		/**
		 * This method takes four integer values and checks that they are within
		 * 0 - 255 before returning a {@link Color} composed of these values.
		 * 
		 * @param red
		 *            - the red component to be checked.
		 * @param green
		 *            - the green component to be checked.
		 * @param blue
		 *            - the blue component to be checked.
		 * @param alpha
		 *            - the alpha component to be checked.
		 * @return - A RGBA {@link Color}, composed of the above integers.
		 */
		public static final Color checkAlphaColour(int red, int green, int blue, int alpha)
			{
				if (red < 0)
					red = 0;
				else if (red > 255)
					red = 255;

				if (green < 0)
					green = 0;
				else if (green > 255)
					green = 255;

				if (blue < 0)
					blue = 0;
				else if (blue > 255)
					blue = 255;

				if (alpha < 0)
					alpha = 0;
				else if (alpha > 255)
					alpha = 255;

				return new Color(red, green, blue, alpha);
			}

		public static Color interpolateColours(Color colOne, Color colTwo)
			{
				return new Color(((colOne.getRed() + colTwo.getRed()) / 2), ((colOne.getGreen() + colTwo.getGreen()) / 2),
						((colOne.getBlue() + colTwo.getBlue()) / 2), ((colOne.getAlpha() + colTwo.getAlpha()) / 2));
			}
	}