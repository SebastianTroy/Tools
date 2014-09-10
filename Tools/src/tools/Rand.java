package tools;

public class Rand
	{
		static final java.util.Random r = new java.util.Random();

		public final static void seed(long seed)
			{
				r.setSeed(seed);
			}

		/**
		 * @return A random boolean value. (either true or false)
		 */
		public final static Boolean bool()
			{
				return r.nextBoolean();
			}

		/**
		 * @return A random double between 0 and 100
		 */
		public static final double percent()
			{
				double rnd = (r.nextDouble() * (100.0 + Double.MIN_VALUE));
				return rnd > 100 ? 100 : rnd;
			}

		/**
		 * @return - Returns rng.nextInt().
		 */
		public static final int int_()
			{
				return r.nextInt();
			}

		/**
		 * Returns a random integer between 0 (inclusive) and high (exclusive).
		 * 
		 * @param high
		 *            - the upper end of the return range
		 * @return An integer greater than or equal to 0, and less than the high parameter.
		 */
		public static final int int_(int high)
			{
				if (high < 0)
					return -r.nextInt(-high);
				else
					return r.nextInt(high);
			}

		/**
		 * Returns a random integer between low (inclusive) and high (exclusive). Note that strange things may happen if low > high.
		 * 
		 * @param low
		 *            - the lower end of the return range
		 * @param high
		 *            - the upper end of the return range
		 * @return An integer greater than or equal to the low parameter, and less than the high parameter.
		 */
		public static final int int_(int low, int high)
			{
				if (low == high)
					return low;
				return low + r.nextInt(Math.abs(high - low));
			}

		/**
		 * Generates an array of random integers bound between a min and a max value, with a specified average.
		 * 
		 * @param numInts
		 *            - The number of integers to generate
		 * @param min
		 *            - The minimum value for an integer
		 * @param max
		 *            - The maximum value for an integer
		 * @param average
		 *            - The average values for the integers
		 * @return An array containing the specified number of random integers, bound with a set range, with an exact average
		 */
		public static final int[] getIntsWithAverage(int numInts, int min, int max, int average) throws IllegalArgumentException
			{
				// If we have been asked to do something impossible, make a fuss
				if (min > max)
					throw new IllegalArgumentException("min (" + min + ") must be smaller than max(" + max + ").");
				if (average < min)
					throw new IllegalArgumentException("Random numbers between " + min + " & " + max + " cannot average less than " + min + ". (Average specified: " + average + ")");
				if (average > max)
					throw new IllegalArgumentException("Random numbers between " + min + " & " + max + " cannot average more than " + max + ". (Average specified: " + average + ")");

				/*
				 * Attempt to increase the spread of results by making sure that a massive range with an average close to either bound
				 * doesn't produce {min, min, min, max, min, min} type arrays.
				 */
				// If specified average is smaller than the mean of the range
				if (average < (max - min) / 2)
					{
						// If max is higher than a value could ever take in the array, make it smaller
						max = Math.min(max, average + ((numInts - 1) * (average - min)));
					}
				// If specified average is greater than the mean of the range
				else
					{
						// If min is smaller than a value could ever take in the array, make it bigger
						min = Math.max(min, average - ((numInts - 1) * (max - average)));

					}
				/*
				 * Set all of the numbers to return to their minimum value
				 */
				int[] intArray = new int[numInts];
				for (int i = 0; i < numInts; i++)
					intArray[i] = int_(min, max + 1);
				/*
				 * Calculate how many integers need to be added to or subtracted from the values to make them average the average
				 */
				int total = NumTools.getSumOfList(intArray);
				int stack = (average * numInts) - total;
				/*
				 * Randomly allocate the integers, making sure we never make any individual number greater than the max value
				 */
				while (stack != 0)
					{
						int index = int_(numInts);
						if (stack > 0)
							{
								if (intArray[index] < max)
									{
										stack--;
										intArray[index]++;
									}
							}
						else
							{
								if (intArray[index] > min)
									{
										stack++;
										intArray[index]--;
									}
							}
					}

				return intArray;
			}

		/**
		 * Returns a random float within a specified range.
		 * 
		 * @param low
		 *            - the lower end of the return range
		 * @param high
		 *            - the upper end of the return range
		 * @return A float greater than or equal to the low parameter, and less than the high parameter.
		 */
		public static final float float_(float low, float high)
			{
				return low + (r.nextFloat() * (high - low));
			}

		/**
		 * Returns a random double within a specified range.
		 * 
		 * @param low
		 *            - the lower end of the return range
		 * @param high
		 *            - the upper end of the return range
		 * @return A double greater than or equal to the low parameter, and less than the high parameter.
		 */
		public static final double double_(double low, double high)
			{
				return low + (r.nextDouble() * (high - low));
			}

		/**
		 * Returns a random {@link long} within a specified range.
		 * 
		 * @param low
		 *            - the lower end of the return range
		 * @param high
		 *            - the upper end of the return range
		 * @return A long greater than or equal to the low parameter, and less than the high parameter.
		 */
		public static final long long_(long low, long high)
			{
				return low + (long) (r.nextDouble() * (high - low));
			}
	}
