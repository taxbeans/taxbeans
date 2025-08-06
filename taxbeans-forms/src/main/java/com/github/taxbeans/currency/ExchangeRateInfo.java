package com.github.taxbeans.currency;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ExchangeRateInfo {
	
	boolean weekdaysOnly;
	
	public boolean isWeekdaysOnly() {
		return weekdaysOnly;
	}

	public void setWeekdaysOnly(boolean weekdaysOnly) {
		this.weekdaysOnly = weekdaysOnly;
	}

	public Map<LocalDate, BigDecimal> getExchangeRates() {
		return exchangeRates;
	}

	public void setExchangeRates(Map<LocalDate, BigDecimal> exchangeRates2) {
		this.exchangeRates = exchangeRates2;
	}

	Map<LocalDate, BigDecimal> exchangeRates = new HashMap<>();

}
