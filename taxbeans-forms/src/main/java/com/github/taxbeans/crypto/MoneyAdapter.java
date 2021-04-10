package com.github.taxbeans.crypto;

import java.math.BigDecimal;

import javax.money.Monetary;

import org.javamoney.moneta.Money;

public class MoneyAdapter {

	public static Money adapt(BigDecimal amount) {
	    return Money.of(amount, Monetary.getCurrency("NZD"));
	}

	public static BigDecimal adapt(Money value) {
		return new BigDecimal(value.getNumber().toString());
	}

}
