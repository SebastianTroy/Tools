package tools;

public class RandTools
	{
		static final java.util.Random r = new java.util.Random();

		public final static void seedRandom(long seed)
			{
				r.setSeed(seed);
			}

		/**
		 * @return A random boolean value. (either true or false)
		 */
		public final static Boolean getBool()
			{
				Boolean bool;
				int rnd = (int) (r.nextDouble() * 2) + 1;
				if (rnd == 2)
					bool = true;
				else
					bool = false;
				return bool;
			}

		/**
		 * @return A random double between 0 and 100
		 */
		public static final double randPercent()
			{
				double rnd = (r.nextDouble() * 100.001);
				return rnd > 100 ? 100 : rnd;
			}

		/**
		 * Returns a random integer within a specified range.
		 * 
		 * @param low
		 *            - the lower end of the return range
		 * @param high
		 *            - the upper end of the return range
		 * @return An integer greater than or equal to the low parameter, and
		 *         less than or equal to the high parameter.
		 */
		public static final int getInt(int low, int high)
			{
				return (int) (r.nextDouble() * (high - low + 1)) + low;
			}

		/**
		 * Returns a random float within a specified range.
		 * 
		 * @param low
		 *            - the lower end of the return range
		 * @param high
		 *            - the upper end of the return range
		 * @return A float greater than or equal to the low parameter, and less
		 *         than or equal to the high parameter.
		 */
		public static final float getFloat(float low, float high)
			{
				float rnd = (float) (r.nextDouble() * (high - low + 0.001)) + low;
				return rnd > high ? high : rnd;
			}

		/**
		 * Returns a random double within a specified range.
		 * 
		 * @param low
		 *            - the lower end of the return range
		 * @param high
		 *            - the upper end of the return range
		 * @return A double greater than or equal to the low parameter, and less
		 *         than or equal to the high parameter.
		 */
		public static final double getDouble(double low, double high)
			{
				double rnd = (r.nextDouble() * (high - low + 0.001)) + low;
				return rnd > high ? high : rnd;
			}

		/**
		 * Returns a random {@link long} within a specified range.
		 * 
		 * @param low
		 *            - the lower end of the return range
		 * @param high
		 *            - the upper end of the return range
		 * @return A long greater than or equal to the low parameter, and less
		 *         than or equal to the high parameter.
		 */
		public static final long randLong(long low, long high)
			{
				return (long) ((r.nextDouble() * (high - low + 1)) + low);
			}
	}