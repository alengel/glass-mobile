package com.brandwatch.glassmobile.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateHelper {
	public static String getDateSevenDaysAgo() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -7);
		return DateHelper.getDateFormat(cal.getTime());
	}

	public static String getDateFormat(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		return sdf.format(date);
	}
}
