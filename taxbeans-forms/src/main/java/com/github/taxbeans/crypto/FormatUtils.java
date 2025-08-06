package com.github.taxbeans.crypto;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.money.MonetaryAmount;

public class FormatUtils {

	static String format(MonetaryAmount value) {
		BigDecimal amount = value.getNumber().numberValue(BigDecimal.class).setScale(2, RoundingMode.HALF_UP);
		return String.format("%,.2f", amount); //not required, since it always matched :" (original: " + amount + ")";
	}
}
