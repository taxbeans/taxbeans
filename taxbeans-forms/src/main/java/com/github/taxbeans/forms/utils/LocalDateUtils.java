package com.github.taxbeans.forms.utils;

import java.util.Date;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class LocalDateUtils {
	
	public static LocalDate convert(Date date) {
		 return new java.sql.Date(date.getTime()).toLocalDate();
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

}
