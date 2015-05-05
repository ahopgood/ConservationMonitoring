package com.alexander.archive;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ReadingTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	private String expectedDateFormat = "2015-03-11 11:42:05";

//	private String expectedDateFormat = 	"2015-03-11 11:42:05";
	private String expectedDateFormatMidday = 	"2015-03-11 12:12:05";
//	private String expectedDateFormat = 	"2015-03-11 12:42:05";
//	private String expectedDateFormat = 	"2015-03-11 13:12:05";
	
	@Test
	public void testDateFormat() {
		Calendar cal = getCalendar(2015,2,11,11,42,05);
		String formattedDate = Reading.getDateFormat().format(cal.getTime());
//		System.out.println(formattedDate);
		assertEquals(expectedDateFormat, formattedDate);
	}

	@Test
	public void testDateFormat_Midday(){
		Calendar cal = getCalendar(2015,2,11,12,12,05);
		String formattedDate = Reading.getDateFormat().format(cal.getTime());
		System.out.println(formattedDate);
		assertEquals(expectedDateFormatMidday, formattedDate);
	}
	
	private Calendar getCalendar(int year, int month, int dayOfMonth, int hour, int minute, int seconds){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, seconds);
		return cal;
	}
}











