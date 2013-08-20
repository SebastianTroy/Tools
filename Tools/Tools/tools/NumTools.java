package tools;

public class NumTools
	{
		public static double distance(double x1, double y1, double x2, double y2)
			{
				return (Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2)));
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