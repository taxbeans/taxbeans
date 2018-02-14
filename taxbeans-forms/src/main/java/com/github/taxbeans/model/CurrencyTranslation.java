package com.github.taxbeans.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Currency;

public class CurrencyTranslation {
	
	private BigDecimal originalAmount;
	
	private Currency originalCurrency;
	
	private BigDecimal translatedAmount;
	
	private Currency translatedCurrency;
	
	private BigDecimal translationSpotRate;
	
	private Date spotRateDate;
	
	private BigDecimal spotRate;
	
	// true = time based (intraday), false = date based (end of day, average, or published dailyRate)
	private boolean timeBased;
	
	private String rateSource;

	public BigDecimal getOriginalAmount() {
		return originalAmount;
	}

	public void setOriginalAmount(BigDecimal originalAmount) {
		this.originalAmount = originalAmount;
	}

	public Currency getOriginalCurrency() {
		return originalCurrency;
	}

	public void setOriginalCurrency(Currency originalCurrency) {
		this.originalCurrency = originalCurrency;
	}

	public BigDecimal getTranslatedAmount() {
		return translatedAmount;
	}

	public void setTranslatedAmount(BigDecimal translatedAmount) {
		this.translatedAmount = translatedAmount;
	}

	public Currency getTranslatedCurrency() {
		return translatedCurrency;
	}

	public void setTranslatedCurrency(Currency translatedCurrency) {
		this.translatedCurrency = translatedCurrency;
	}

	public BigDecimal getTranslationSpotRate() {
		return translationSpotRate;
	}

	public void setTranslationSpotRate(BigDecimal translationSpotRate) {
		this.translationSpotRate = translationSpotRate;
	}

	public Date getSpotRateDate() {
		return spotRateDate;
	}

	public void setSpotRateDate(Date spotRateDate) {
		this.spotRateDate = spotRateDate;
	}

	public BigDecimal getSpotRate() {
		return spotRate;
	}

	public void setSpotRate(BigDecimal spotRate) {
		this.spotRate = spotRate;
	}

	public boolean isTimeBased() {
		return timeBased;
	}

	public void setTimeBased(boolean timeBased) {
		this.timeBased = timeBased;
	}

	public String getRateSource() {
		return rateSource;
	}

	public void setRateSource(String rateSource) {
		this.rateSource = rateSource;
	}

}
