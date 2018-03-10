package com.github.taxbeans.forms.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TaxRegion {
	
	private ZoneId zone = ZoneId.of("Pacific/Auckland");

	public static TaxRegion getDefault() {
		return new TaxRegion();
	}

	public ZoneId getZone() {
		return zone;
	}

	public void setZone(ZoneId zone) {
		this.zone = zone;
	}
	
	/**
	 * Exactly when the tax year started
	 */
	public ZonedDateTime getStartOfTaxYear(int year) {
		ZoneId zone = ZoneId.of("Pacific/Auckland");
		return ZonedDateTime.of(year, 4, 1, 0, 0, 0, 0, zone);
	}	
}
