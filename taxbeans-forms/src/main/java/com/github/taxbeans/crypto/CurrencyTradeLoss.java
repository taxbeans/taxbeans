package com.github.taxbeans.crypto;

import java.time.ZonedDateTime;

import javax.money.Monetary;
import javax.money.MonetaryAmount;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used for margin trades etc., where the result is often solely reported as a
 * profit or loss.
 */
public class CurrencyTradeLoss extends AbstractCryptoEvent {

	final static Logger logger = LoggerFactory.getLogger(CurrencyTradeLoss.class);

	private CurrencyFee totalRolloverFees;
	
	private String group;
	
	private int rowNum;
	
	private boolean rollover;

	public static CurrencyTradeLoss of(CurrencyBatchGroup batchGroup, CurrencyAmount loss, 
			ZonedDateTime tradeDateTime, CurrencyFee totalRolloverFees) {
		CurrencyTradeLoss trade = new CurrencyTradeLoss();
		trade.batchGroup = batchGroup;
		trade.loss = loss;
		trade.tradeDateTime = tradeDateTime;
		trade.totalRolloverFees = totalRolloverFees;
		return trade;
	}

	public CurrencyFee getTotalRolloverFees() {
		return totalRolloverFees;
	}

	public void setTotalRolloverFees(CurrencyFee totalRolloverFees) {
		this.totalRolloverFees = totalRolloverFees;
	}

	private CurrencyAmount loss;

	@SuppressWarnings("unused")
	private CurrencyBatchGroup batchGroup;

	private ZonedDateTime tradeDateTime;

	@Override
	public int compareTo(CryptoEvent o) {
		return this.getWhen().compareTo(o.getWhen());
	}

	public ZonedDateTime getWhen() {
		return tradeDateTime;
	}

	public CurrencyAmount getLoss() {
		return loss;
	}

	@Override
	public String toString() {
		return String.format("CurrencyTradeLoss [loss=%1$s][datetime=%2$s][fee=%3$s][rowNum=%4$s]", loss, tradeDateTime, 
				totalRolloverFees, rowNum);
	}

	public void freeMemory() {
		this.batchGroup = null;
		this.loss = null;
		this.tradeDateTime = null;
	}

	public MonetaryAmount getLossInBaseCurrency() {
		ConversionUtils conversionUtils = ConversionUtils.of();
		MonetaryAmount result = conversionUtils.convert(loss, tradeDateTime);
		return conversionUtils.convert(Monetary.getCurrency("NZD"), result, tradeDateTime).negate();
	}
	
	public MonetaryAmount getTotalRolloverFeesInBaseCurrency() {
		ConversionUtils conversionUtils = ConversionUtils.of();
		MonetaryAmount result = conversionUtils.convert(Monetary.getCurrency("NZD"), totalRolloverFees.getAmount(), tradeDateTime);
		return result;
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

	boolean isRollover() {
		return rollover;
	}

	void setRollover(boolean rollover) {
		this.rollover = rollover;
	}


}
