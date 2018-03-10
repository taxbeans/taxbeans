package com.github.taxbeans.forms.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class LocalDateTimeUtils {
	
	public static LocalDateTime convert(Date date) {
		 return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
	}
	
	public static Date convert(LocalDateTime date) {
		return Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
	}

	public static LocalDate convertToDate(LocalDateTime time) {
		return time.toLocalDate();
	}

}
