package com.github.taxbeans.crypto;

import java.time.ZonedDateTime;

import javax.money.MonetaryAmount;

public class CryptoConversion extends AbstractCryptoEvent {
	
	/*
	 * The default behaviour of this is to treat it as a sale
	 * then a purchase
	 */
	
	private CryptoAmount from;
	private CryptoAmount to;
	private Fee fee;
	private ZonedDateTime dateTime;
	private BatchGroup batchGroup;
	private Trade trade;  //internally generated trade side of the conversion
	private Batch batch;  //internally generated batch side of the conversion
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

	public static CryptoConversion of(BatchGroup batchGroup,
			CryptoAmount to, ZonedDateTime dateTime,
			CryptoAmount from, Fee fee) {
		CryptoConversion a = new CryptoConversion();
		a.from = from;
		a.to = to;
		a.dateTime = dateTime;
		a.fee = fee;
		a.batchGroup = batchGroup;
		return a;
	}
	
	public void deriveTradeAndBatch() {
		MonetaryAmount money = ConversionUtils.of(CurrencyConversionStrategy.RBNZ)
				.convert(from, dateTime);
		trade = Trade.of(batchGroup, from, money, dateTime, fee);
		batch = Batch.of(to, dateTime, money);
		if (this.leveraged) {
			trade.setLeveraged(true);
			batch.setLeveraged(true);
		}
	}

	public CryptoAmount getFrom() {
		return from;
	}

	public CryptoAmount getTo() {
		return to;
	}

	public Fee getFee() {
		return fee;
	}

	public Trade getTrade() {
		if (trade == null) {
			throw new AssertionError("Trade and batch not derived yet");
		}
		return trade;
	}

	public Batch getBatch() {
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

	public int getRowNum() {
		return rowNum;
	}

	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}
}
