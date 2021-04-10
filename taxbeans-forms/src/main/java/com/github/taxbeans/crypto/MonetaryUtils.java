package com.github.taxbeans.crypto;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;

import org.javamoney.moneta.Money;

public class MonetaryUtils {

	public static MonetaryAmount round(MonetaryAmount baseCurrencyCost) {
		CurrencyUnit unit = baseCurrencyCost.getCurrency();
		BigDecimal number = new BigDecimal(baseCurrencyCost.getNumber().toString());
		number = number.setScale(2, RoundingMode.HALF_UP);
		return Money.of(number, unit);
	}

	public static BigDecimal toRoundedBigDecimal(MonetaryAmount monetaryAmount, int scale) {
		return monetaryAmount.getNumber().numberValue(BigDecimal.class).setScale(scale, RoundingMode.HALF_UP);
	}
	
	public static Money floorToDollars(MonetaryAmount baseCurrencyCost) {
		CurrencyUnit unit = baseCurrencyCost.getCurrency();
		BigDecimal number = baseCurrencyCost.getNumber().numberValue(BigDecimal.class);
		number = number.setScale(0, RoundingMode.DOWN);
		return Money.of(number, unit);
	}

	public static Money floorToCents(Money money) {
		CurrencyUnit unit = money.getCurrency();
		BigDecimal number = money.getNumber().numberValue(BigDecimal.class);
		number = number.setScale(2, RoundingMode.DOWN);
		return Money.of(number, unit);
	}

}
