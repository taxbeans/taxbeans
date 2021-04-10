package com.github.taxbeans.crypto;

import java.time.ZonedDateTime;

public class CurrencyConversion extends AbstractCryptoEvent {
	
	/*
	 * The default behaviour of this is to treat it as a sale
	 * then a purchase
	 */
	
	private CurrencyAmount from;
	private CurrencyAmount to;
	private CurrencyFee fee;
	private ZonedDateTime dateTime;
	private CurrencyBatchGroup batchGroup;
	private CurrencyTrade trade;  //internally generated trade side of the conversion
	private CurrencyBatch batch;  //internally generated batch side of the conversion
	private boolean leveraged;
	private String group;
	private int rowNum;
	private boolean incomplete;  //whether the other half of the trade needs more data parsing
	
	@Override
	public int compareTo(CryptoEvent o) {
		return this.getWhen().compareTo(o.getWhen());
	}

	@Override
	public ZonedDateTime getWhen() {
		return dateTime;
	}

	public static CurrencyConversion of(CurrencyBatchGroup batchGroup,
			CurrencyAmount to, ZonedDateTime dateTime,
			CurrencyAmount from, CurrencyFee fee) {
		CurrencyConversion a = new CurrencyConversion();
		a.from = from;
		a.to = to;
		a.dateTime = dateTime;
		a.fee = fee;
		a.batchGroup = batchGroup;
		return a;
	}
	
	public void deriveTradeAndBatch() {
		trade = CurrencyTrade.of(batchGroup, from, to, dateTime, fee);
		trade.setRowNum(this.getRowNum());
		batch = CurrencyBatch.of(to, dateTime, from);
		batch.setRowNum(this.getRowNum());
		if (this.leveraged) {
			trade.setLeveraged(true);
			batch.setLeveraged(true);
		}
	}

	public CurrencyAmount getFrom() {
		return from;
	}

	public CurrencyAmount getTo() {
		return to;
	}

	public CurrencyFee getFee() {
		return fee;
	}

	public CurrencyTrade getTrade() {
		if (trade == null) {
			throw new AssertionError("Trade and batch not derived yet");
		}
		return trade;
	}

	public CurrencyBatch getBatch() {
		if (batch == null) {
			throw new AssertionError("Trade and batch not derived yet");
		}
		return batch;
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
	
	public String toString() {
		return String.format("%1$s -> %2$s [datetime=%3$s] [rownum=%4$s]", from, to, dateTime, rowNum);
	}

	public int getRowNum() {
		return rowNum;
	}

	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}

	public boolean isIncomplete() {
		return incomplete;
	}

	public void setIncomplete(boolean incomplete) {
		this.incomplete = incomplete;
	}

	void setFrom(CurrencyAmount from) {
		this.from = from;
	}

	void setTo(CurrencyAmount to) {
		this.to = to;
	}

	void setFee(CurrencyFee fee) {
		this.fee = fee;
	}

}
