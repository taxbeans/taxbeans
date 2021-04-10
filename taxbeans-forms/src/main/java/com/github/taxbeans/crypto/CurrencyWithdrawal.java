package com.github.taxbeans.crypto;

import java.time.ZonedDateTime;

public class CurrencyWithdrawal extends AbstractCryptoEvent {

	private CurrencyAmount amount;
	private CurrencyFee fee;
	private ZonedDateTime dateTime;
	@SuppressWarnings("unused")
	private CurrencyBatchGroup batchGroup;
	private String group;
	private int rowNum;

	//Fiat withdrawals are implicitly drawings
	//This is to explicitly mark the withdrawal as drawings
	private boolean drawings;

	//This is to explicitly mark the withdrawal as a business expense
	private boolean businessExpense;

	@Override
	public int compareTo(CryptoEvent o) {
		return this.getWhen().compareTo(o.getWhen());
	}

	@Override
	public ZonedDateTime getWhen() {
		return dateTime;
	}

	public static CurrencyWithdrawal of(CurrencyBatchGroup batchGroup,
			CurrencyAmount amount, ZonedDateTime dateTime,
			CurrencyFee fee) {
		CurrencyWithdrawal a = new CurrencyWithdrawal();
		a.amount = amount;
		a.dateTime = dateTime;
		a.fee = fee;
		a.batchGroup = batchGroup;
		return a;
	}

	public CurrencyFee getFee() {
		return fee;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}
	
	public String toString() {
		return String.format("[Withdrawal=%1$s][Fee=%2$s][datetime=%3$s][rownum=%4$s]", amount, fee, dateTime, rowNum);
	}

	public int getRowNum() {
		return rowNum;
	}

	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}

	public CurrencyAmount getAmount() {
		return amount;
	}

	public void setAmount(CurrencyAmount amount) {
		this.amount = amount;
	}

	boolean isDrawings() {
		return drawings;
	}

	void setDrawings(boolean drawings) {
		this.drawings = drawings;
	}

	boolean isBusinessExpense() {
		return businessExpense;
	}

	public void setBusinessExpense(boolean businessExpense) {
		this.businessExpense = businessExpense;
	}
}
