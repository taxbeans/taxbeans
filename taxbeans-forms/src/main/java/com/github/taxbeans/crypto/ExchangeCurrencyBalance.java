package com.github.taxbeans.crypto;

import java.time.ZonedDateTime;

/**
 * This records the current balance of a currency at an Exchange as an event.
 * This is useful for automatic reconciliation purposes
 *
 */
public class ExchangeCurrencyBalance extends AbstractCryptoEvent {

	private CurrencyAmount balance;

	private ZonedDateTime when;

	private String group;

	private int rowNum;

	public ZonedDateTime getWhen() {
		return when;
	}

	public void setWhen(ZonedDateTime when) {
		this.when = when;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public int getRowNum() {
		return rowNum;
	}

	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}

	public CurrencyAmount getBalance() {
		return balance;
	}

	public void setBalance(CurrencyAmount balance) {
		this.balance = balance;
	}

	@Override
	public String toString() {
		return String.format("ExchangeCurrencyBalance [balance=%s, when=%s, exchange=%s, rowNum=%s]", balance, when, this.getCurrencyExchange(),
				rowNum);
	}
//
//	@Override
//	public int compareTo(CryptoEvent o) {
//		int result = super.compareTo(o);
//		if (result == 0) {
//			return this.getBalance().getCurrencyString()
//					.compareTo(((ExchangeCurrencyBalance)o).getBalance().getCurrencyString());
//		}
//		return result;
//	}
}

