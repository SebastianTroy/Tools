package tools;

import java.awt.Graphics;

public class DrawTools
	{
		public static void drawArrow(double x1, double y1, double x2, double y2, Graphics g, int arrowLineLength)
			{
				g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);

				double dy = y2 - y1;
				double dx = x2 - x1;
				double theta = Math.atan2(dy, dx);

				double x, y, rho = theta + Math.toRadians(40);
				for (int j = 0; j < 2; j++)
					{
						x = x2 - 20 * Math.cos(rho);
						y = y2 - 20 * Math.sin(rho);
						g.drawLine((int) x2, (int) y2, (int) x, (int) y);
						rho = theta - Math.toRadians(40);
					}
			}
	}