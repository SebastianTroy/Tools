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
		 * Returns a random integer between low (inclusive) and high (exclusive). Note that strange things may happen if low > high.
		 * 
		 * @param low
		 *            - the lower end of the return range
		 * @param high
		 *            - the upper end of the return range
		 * @return An integer greater than or equal to the low parameter, and less than or equal to the high parameter.
		 */
		public static final int int_(int low, int high)
			{
				if (low == high)
					return low;
				return low + r.nextInt(Math.abs(high - low));
			}

		/**
		 * Returns a random float within a specified range.
		 * 
		 * @param low
		 *            - the lower end of the return range
		 * @param high
		 *            - the upper end of the return range
		 * @return A float greater than or equal to the low parameter, and less than or equal to the high parameter.
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
		 * @return A double greater than or equal to the low parameter, and less than or equal to the high parameter.
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
		 * @return A long greater than or equal to the low parameter, and less than or equal to the high parameter.
		 */
		public static final long long_(long low, long high)
			{
				return low + (long) (r.nextDouble() * (high - low));
			}
	}
