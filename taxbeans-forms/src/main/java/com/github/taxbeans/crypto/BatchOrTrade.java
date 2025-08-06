package com.github.taxbeans.crypto;

import java.time.ZonedDateTime;

public class BatchOrTrade implements Comparable<BatchOrTrade> {
	
	private Batch batch;
	
	private Trade trade;
	
	private boolean isBatch;
	
	public static BatchOrTrade of(Batch batch) {
		BatchOrTrade batchOrTrade = new BatchOrTrade();
		batchOrTrade.batch = batch;
		batchOrTrade.isBatch = true;
		return batchOrTrade;
	}
	
	public static BatchOrTrade of(Trade trade) {
		BatchOrTrade batchOrTrade = new BatchOrTrade();
		batchOrTrade.trade = trade;
		return batchOrTrade;
	}

	public Batch getBatch() {
		if (!isBatch) {
			throw new AssertionError("this is not a batch");
		}
		return batch;
	}

	public void setBatch(Batch batch) {
		this.batch = batch;
	}

	public Trade getTrade() {
		if (isBatch) {
			throw new AssertionError("this is not a trade");
		}
		return trade;
	}

	public void setTrade(Trade trade) {
		this.trade = trade;
	}
	
	public ZonedDateTime getWhen() {
		if (isBatch) {
			return batch.getWhen();
		} else {
			return trade.getWhen();
		}
	}

	@Override
	public int compareTo(BatchOrTrade o) {
		return this.getWhen().compareTo(o.getWhen());
	}
}
