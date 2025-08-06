package com.github.taxbeans.model;

public enum AccountClassification {
	Expense, Income, Asset, Liability;

	public static AccountClassification fromString(String text) {
		return "EXPENSE".equals(text) ? Expense : Income;
	}
}
