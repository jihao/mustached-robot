package schedule.utils;

import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class CalendarUtilsTest {

//	@Test
//	public void testParseDate() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testMondayOfTheWeek() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetPrevMondayDate() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testWeekOfYear() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testDayOfWeek() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetNextMondayDate() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testMondayOfCurrentDate() {
//		fail("Not yet implemented");
//	}

	@Test
	public void testSundayOfTheWeek() {
		
		Date sunday = CalendarUtils.sundayOfTheWeek("2014-03-27");
		Date expect = CalendarUtils.parseDate("2014-03-30");
		Assert.assertTrue(expect.equals(sunday));
		
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(CalendarUtils.parseDate("2014-03-27"));
		calendar.setFirstDayOfWeek(Calendar.SUNDAY);
		int startWeekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
		calendar.setWeekDate(calendar.get(Calendar.YEAR), startWeekOfYear, Calendar.SUNDAY);
		Date expect2 = CalendarUtils.parseDate("2014-03-23");
		Assert.assertTrue(expect2.equals(calendar.getTime()));
		
	}

}
