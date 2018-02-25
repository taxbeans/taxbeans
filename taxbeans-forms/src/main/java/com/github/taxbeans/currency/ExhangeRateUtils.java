package com.github.taxbeans.currency;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

import com.github.taxbeans.forms.utils.LocalDateUtils;

public class ExhangeRateUtils {

	public static BigDecimal exchange(LocalDate translationDate, CurrencyUnit from, CurrencyUnit to,
			BigDecimal amount) {
		if (!from.equals(Monetary.getCurrency("USD")) && !from.equals(Monetary.getCurrency("EUR"))
				&& !from.equals(Monetary.getCurrency("NZD"))) {
			throw new IllegalStateException("Source currency not yet supported");
		} 
		if (!from.equals(Monetary.getCurrency("NZD")) && !from.equals(Monetary.getCurrency("EUR"))
				&& !from.equals(Monetary.getCurrency("USD"))) {
			throw new IllegalStateException("Target currency not yet supported");
		}
		BigDecimal rate = null;
		if (from.equals(Monetary.getCurrency("USD")) && to.equals(Monetary.getCurrency("NZD"))) {
			rate = RBNZHistoricalExchangeRatesReader.getForeignToNZDRate(LocalDateUtils.convert(translationDate), "USD");
		} else if (from.equals(Monetary.getCurrency("NZD")) && to.equals(Monetary.getCurrency("USD"))) {
			rate = RBNZHistoricalExchangeRatesReader.getNZDtoForeignRate(LocalDateUtils.convert(translationDate), "USD");
		} else if (from.equals(Monetary.getCurrency("EUR")) && to.equals(Monetary.getCurrency("NZD"))) {
			rate = RBNZHistoricalExchangeRatesReader.getForeignToNZDRate(LocalDateUtils.convert(translationDate), "EUR");
		} else if (from.equals(Monetary.getCurrency("NZD")) && to.equals(Monetary.getCurrency("EUR"))) {
			rate = RBNZHistoricalExchangeRatesReader.getNZDtoForeignRate(LocalDateUtils.convert(translationDate), "EUR");
		}
		return rate.multiply(amount);
	}

}
