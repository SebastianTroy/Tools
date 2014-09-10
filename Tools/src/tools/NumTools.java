package tools;

public class NumTools
	{
		public static double distance(double x1, double y1, double x2, double y2)
			{
				return (Math.sqrt(((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2))));
			}

		public static double distanceSquare(double x1, double y1, double x2, double y2)
			{

				return ((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2));
			}

		public static double distance(double x1, double y1, double z1, double x2, double y2, double z2)
			{
				return (Math.sqrt(((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2)) + ((z1 - z2) * (z1 - z2))));
			}

		public static double distanceSquare(double x1, double y1, double z1, double x2, double y2, double z2)
			{

				return ((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2)) + ((z1 - z2) * (z1 - z2));
			}

		public static double[] getVector(double angle)
			{
				angle = Math.toRadians(angle % 360);

				double[] vector = { Math.sin(angle), Math.cos(angle) };

				return vector;
			}

		public static double getAngle(double vectorX, double vectorY)
			{
				return Math.atan2(0.5, 0.5) * 180.0 / Math.PI;
			}

		public static int getSumOfList(int[] list)
			{
				int total = 0;
				for (int i = 0; i < list.length; i++)
					total += list[i];
				return total;
			}

		public static double getSumOfList(double[] list)
			{
				double total = 0;
				for (int i = 0; i < list.length; i++)
					total += list[i];
				return total;
			}

		/**
		 * @param list
		 *            - The list of values to be moderated.
		 * @param value
		 *            - The value that the sum of the list should be.
		 * @return A copy of the list where all values have been equally moderated up or down so that they summate to the given value.
		 */
		public static double[] makeSumOfListEqual(double[] list, double value)
			{
				return makeSumOfListEqual(list, value, 0);
			}

		/**
		 * @param list
		 *            - The list of values to be moderated.
		 * @param value
		 *            - The value that the sum of the list should be.
		 * @param startIndex
		 *            - The operation will only apply to members of the list after this index.
		 * @return A copy of the list where all values have been equally moderated up or down so that they summate to the given value.
		 */
		public static double[] makeSumOfListEqual(double[] list, double value, int startIndex)
			{
				if (startIndex < 0)
					startIndex = 0;
				else if (startIndex > list.length)
					startIndex = list.length;

				// Calculate the total
				double total = 0;
				for (int i = startIndex; i < list.length; i++)
					total += list[i];
				// Modify the total so that next operation makes the values summate to value (instead of 1)
				total *= (1.0 / value);
				// Now make all the proportions add to value
				for (int i = startIndex; i < list.length; i++)
					list[i] /= total;

				return list;
			}

		public static int[] getDigitArrayFromInt(int number)
			{
				int numCopy = number;
				int numDigits = 0;
				while (numCopy > 0)
					{
						numCopy /= 10;
						numDigits++;
					}
				if (number == 0)
					{
						int[] zeroArray = { 0 };
						return zeroArray;
					}

				int[] numberArray = new int[numDigits];

				for (int i = numDigits - 1; i > -1; i--)
					{
						numberArray[i] = number % 10;
						number /= 10;
					}

				return numberArray;
			}
	}