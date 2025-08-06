package com.github.taxbeans.crypto;

import java.time.ZonedDateTime;

public class CurrencyDeposit extends AbstractCryptoEvent {

	private CurrencyAmount amount;
	
	private CurrencyFee fee;
	
	private ZonedDateTime dateTime;
	
	@SuppressWarnings("unused")
	private CurrencyBatchGroup batchGroup;
	
	private String group;
	
	private int rowNum;
	
	@Override
	public int compareTo(CryptoEvent o) {
		return this.getWhen().compareTo(o.getWhen());
	}

	@Override
	public ZonedDateTime getWhen() {
		return dateTime;
	}

	public static CurrencyDeposit of(CurrencyBatchGroup batchGroup,
			CurrencyAmount amount, ZonedDateTime dateTime,
			CurrencyFee fee) {
		CurrencyDeposit a = new CurrencyDeposit();
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
		return String.format("[Deposit=%1$s][Fee=%2$s][datetime=%3$s][rownum=%4$s][exchange=%5$s]", 
				amount, fee, dateTime, rowNum, this.getCurrencyExchange());
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
}
