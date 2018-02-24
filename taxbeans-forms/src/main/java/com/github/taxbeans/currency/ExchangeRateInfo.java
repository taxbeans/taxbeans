package com.github.taxbeans.currency;

import java.math.BigDecimal;
import java.util.Date;
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

	public Map<Date, BigDecimal> getExchangeRates() {
		return exchangeRates;
	}

	public void setExchangeRates(Map<Date, BigDecimal> exchangeRates) {
		this.exchangeRates = exchangeRates;
	}

	Map<Date, BigDecimal> exchangeRates = new HashMap<>();

}
