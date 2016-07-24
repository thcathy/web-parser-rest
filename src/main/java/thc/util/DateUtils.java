package thc.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class DateUtils {
	//private static Logger logger = LoggerFactory.getLogger(DateUtils.class);
	
	// private constructor prevents instantiation
	private DateUtils() { throw new UnsupportedOperationException(); }

	public static boolean isWeekEnd(Calendar c) {
		return (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY);
	}

	public static boolean notWeekEnd(Calendar c) {
		return !isWeekEnd(c);
	}
	
	public static boolean isOverMonth(Date d1, Date d2, int numOfMonth) {
		return Math.abs(d1.getTime() - d2.getTime()) > (long)86400000 * 31 * numOfMonth;
	}
	
	public static String toString(Date d, String pattern) {
		return new SimpleDateFormat(pattern).format(d);
	}
}
