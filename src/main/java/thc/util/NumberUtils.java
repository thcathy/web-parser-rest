package thc.util;


public final class NumberUtils {
	//private static Logger logger = LoggerFactory.getLogger(NumberUtils.class);
	
	// private constructor prevents instantiation
	private NumberUtils() { throw new UnsupportedOperationException(); }

	public static double extractDouble(String str) {
		try {
			return Double.parseDouble(extractNumber(str));
		} catch (Exception e) {
			e.printStackTrace();
			return 0.0;
		}
		
	}
	
	public static String extractNumber(String str) {
		return str.replaceAll("[^\\.\\-0123456789]", "");
	}
}
