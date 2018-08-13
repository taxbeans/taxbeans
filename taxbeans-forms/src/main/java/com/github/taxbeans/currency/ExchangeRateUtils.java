package com.github.taxbeans.currency;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

public class ExchangeRateUtils {

	public static BigDecimal exchange(ZonedDateTime translationDate, CurrencyUnit from, CurrencyUnit to,
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
		ZoneId zone = ZoneId.of("Pacific/Auckland");
		LocalDate pacificAucklandDate = ZonedDateTime.ofInstant(translationDate.toInstant(), zone).toLocalDate();
		if (from.equals(Monetary.getCurrency("USD")) && to.equals(Monetary.getCurrency("NZD"))) {
			rate = RBNZHistoricalExchangeRatesReader.getForeignToNZDRate(pacificAucklandDate, "USD");
		} else if (from.equals(Monetary.getCurrency("NZD")) && to.equals(Monetary.getCurrency("USD"))) {
			rate = RBNZHistoricalExchangeRatesReader.getNZDtoForeignRate(pacificAucklandDate, "USD");
		} else if (from.equals(Monetary.getCurrency("EUR")) && to.equals(Monetary.getCurrency("NZD"))) {
			rate = RBNZHistoricalExchangeRatesReader.getForeignToNZDRate(pacificAucklandDate, "EUR");
		} else if (from.equals(Monetary.getCurrency("NZD")) && to.equals(Monetary.getCurrency("EUR"))) {
			rate = RBNZHistoricalExchangeRatesReader.getNZDtoForeignRate(pacificAucklandDate, "EUR");
		}
		return rate.multiply(amount);
	}

}
