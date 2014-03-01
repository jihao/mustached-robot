package schedule.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalendarUtils {
	private static final SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * 
	 * @param string
	 *            format "yyyy-MM-dd"
	 * @return
	 */
	public static Date parseDate(String string) {
		Date date = null;
		try {
			date = simpleFormat.parse(string);
		} catch (ParseException e) {
			throw new IllegalArgumentException("String format not corret, should be yyyy-MM-dd", e);
		}
		return date;
	}
	
	public static String formatDate(Date date) {
		return simpleFormat.format(date);
	}
	/**
	 * 
	 * @param date
	 *            format "yyyy-MM-dd"
	 * @return
	 */
	public static Date mondayOfTheWeek(String date) {
		// get week of year
		Calendar calendar = getCalendarOfDate(date);
		int startWeekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
		calendar.setWeekDate(calendar.get(Calendar.YEAR), startWeekOfYear, Calendar.MONDAY);
		return calendar.getTime();
	}

	public static String getPrevMondayDate(String mondayDate) {
		Date date = CalendarUtils.parseDate(mondayDate);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.WEEK_OF_YEAR, -1);

		String prevMondayDate = simpleFormat.format(calendar.getTime());
		return prevMondayDate;
	}

	public static int weekOfYear(String date) {
		// get week of year
		Calendar calendar = getCalendarOfDate(date);
		int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
		return weekOfYear;
	}

	/**
	 * 
	 * @param c
	 * @return 1-7
	 * @throws Exception
	 */
	public static int dayOfWeek(Calendar c) throws Exception {
		int dayForWeek = 0;
		if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			dayForWeek = 7;
		} else {
			dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
		}
		return dayForWeek;
	}

	private static Calendar getCalendarOfDate(String date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(parseDate(date));
		return calendar;
	}

	public static String getNextMondayDate(String mondayDate) {
		Date date = CalendarUtils.parseDate(mondayDate);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.WEEK_OF_YEAR, 1);

		String prevMondayDate = simpleFormat.format(calendar.getTime());
		return prevMondayDate;
	}

	public static String mondayOfCurrentDate() {
		Calendar calendar = Calendar.getInstance();
		String date = simpleFormat.format(calendar.getTime());
		Date monday = mondayOfTheWeek(date);
		return simpleFormat.format(monday);
	}

	/**
	 * Assuming Monday is always the first day of the week
	 * 
	 * @param date
	 * @return
	 */
	public static Date sundayOfTheWeek(String date) {
		// get week of year
		Calendar calendar = getCalendarOfDate(date);
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		int startWeekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
		calendar.setWeekDate(calendar.get(Calendar.YEAR), startWeekOfYear, Calendar.SUNDAY);
		return calendar.getTime();
	}

}
