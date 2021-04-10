package com.github.taxbeans.crypto;

import java.time.ZonedDateTime;

import javax.money.MonetaryAmount;

public class MonetaryConversion extends AbstractCryptoEvent {
	
	private MonetaryAmount from;
	private MonetaryAmount to;
	private ZonedDateTime dateTime;
	private boolean leveraged;
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

	public static MonetaryConversion of(MonetaryAmount from, ZonedDateTime dateTime,
			MonetaryAmount to) {
		MonetaryConversion a = new MonetaryConversion();
		a.from = from;
		a.to = to;
		a.dateTime = dateTime;
		return a;
	}

	public MonetaryAmount getFrom() {
		return from;
	}

	public MonetaryAmount getTo() {
		return to;
	}

	public boolean isLeveraged() {
		return leveraged;
	}

	public void setLeveraged(boolean leveraged) {
		this.leveraged = leveraged;
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

}
