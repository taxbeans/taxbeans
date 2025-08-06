package com.github.taxbeans.crypto;

import java.time.ZonedDateTime;

import javax.money.MonetaryAmount;

public class BusinessExpense {
	
	public static BusinessExpense of(MonetaryAmount monetaryAmount, ZonedDateTime when, String comment) {
		return new BusinessExpense(monetaryAmount, when, comment);
	}
	
	MonetaryAmount monetaryAmount;
	
	ZonedDateTime when;
	
	String comment;

	private BusinessExpense(MonetaryAmount monetaryAmount, ZonedDateTime when, String comment) {
		this.monetaryAmount = monetaryAmount;
		this.when = when;
		this.comment = comment;
	}

	@Override
	public String toString() {
		return String.format("BusinessExpense [monetaryAmount=%s, when=%s, comment=%s]", 
				MonetaryUtils.toRoundedBigDecimal(monetaryAmount, 2), when, comment);
	}

}
