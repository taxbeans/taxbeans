package com.github.taxbeans.forms.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class ZonedDateTimeUtils {
	
	public static ZonedDateTime convert(Date date) {
		return ZonedDateTime.ofInstant(date.toInstant(), TaxRegion.getDefault().getZone());
	}
	
	public static ZonedDateTime convert(LocalDate date) {
		return ZonedDateTime.of(date, LocalTime.NOON, TaxRegion.getDefault().getZone());
	}
	
	public static Date convert(ZonedDateTime date, ZoneId zone) {
		return Date.from(date.toInstant());
	}

	public static LocalDate convertToDate(LocalDateTime time) {
		return time.toLocalDate();
	}

}
