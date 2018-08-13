package com.github.taxbeans.model;

import java.math.BigDecimal;

public class DigitalCurrencyAdapter {

	public static DigitalCurrency adapt(String foreignCurrencyCode, BigDecimal abs) {
		DigitalCurrencyCode currencyCode = DigitalCurrencyCode.valueOf(foreignCurrencyCode);
		DigitalCurrency digitalCurrency = new DigitalCurrency();
		digitalCurrency.setCode(currencyCode);
		digitalCurrency.setAmount(abs);
		return digitalCurrency;
	}

}
