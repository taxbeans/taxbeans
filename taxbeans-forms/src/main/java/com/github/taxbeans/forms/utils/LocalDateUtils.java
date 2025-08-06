package com.github.taxbeans.forms.utils;

import java.util.Date;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class LocalDateUtils {
	
	public static LocalDate convert(Date date) {
		 return new java.sql.Date(date.getTime()).toLocalDate();
	}
	
	public static Date convert(LocalDate localDate) {
		 return  Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	public static String format(LocalDate date, String pattern) {
		return date.format(DateTimeFormatter.ofPattern(pattern));
	}

	public static int daysBetween(LocalDate periodFrom, LocalDate periodTo) {
		return Period.between(periodFrom, periodTo).getDays();
	}

	public static LocalDate parse(String dateString, DateTimeFormatter fmt) {
		return LocalDate.parse(dateString, fmt);
	}

	public static String formatDay(LocalDate dateOfBirth) {
		String day = String.valueOf(dateOfBirth.getDayOfMonth());
		if (day.length() == 1) {
			day = String.format("0%1$s", day);
		}
		return day;
	}

	public static String formatMonth(LocalDate dateOfBirth) {
		String month = String.valueOf(dateOfBirth.getMonthValue());
		if (month.length() == 1) {
			month = String.format("0%1$s", month);
		}
		return month;
	}

}
