package com.github.taxbeans.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import javax.money.CurrencyUnit;

import org.javamoney.moneta.Money;

import com.github.taxbeans.currency.ExchangeRateUtils;

public class BaseCurrencyAdapter {
	
	private CurrencyUnit baseCurrency;
	
	private ConversionType conversionTypeUsed;

	public ConversionType getConversionTypeUsed() {
		return conversionTypeUsed;
	}

	public Money adapt(ZonedDateTime translationDate, BigDecimal amount, CurrencyUnit currency) {
		if (baseCurrency.equals(currency)) {
			conversionTypeUsed = ConversionType.NO_CONVERSION_REQUIRED;
			return Money.of(amount, currency);
		} else {
			BigDecimal result = ExchangeRateUtils.exchange(translationDate, currency, baseCurrency, amount);
			conversionTypeUsed = ConversionType.REFERENCE;
			return Money.of(result, baseCurrency);
		}
		//throw new AssertionError("Could not convert from " + currency + " to " + baseCurrency);
	}

	public CurrencyUnit getBaseCurrency() {
		return baseCurrency;
	}

	public void setBaseCurrency(CurrencyUnit baseCurrency) {
		this.baseCurrency = baseCurrency;
	}

}
