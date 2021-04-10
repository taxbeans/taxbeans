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
public class TradeProfit extends AbstractCryptoEvent {

	final static Logger logger = LoggerFactory.getLogger(TradeProfit.class);

	private Fee totalRolloverFees;
	
	private String group;
	
	private int rowNum;

	public static TradeProfit of(BatchGroup batchGroup, CurrencyAmount profit, 
			ZonedDateTime tradeDateTime, Fee totalRolloverFees) {
		TradeProfit trade = new TradeProfit();
		trade.batchGroup = batchGroup;
		trade.profit = profit;
		trade.tradeDateTime = tradeDateTime;
		trade.totalRolloverFees = totalRolloverFees;
		return trade;
	}

	public Fee getTotalRolloverFees() {
		return totalRolloverFees;
	}

	public void setTotalRolloverFees(Fee totalRolloverFees) {
		this.totalRolloverFees = totalRolloverFees;
	}

	private CurrencyAmount profit;

	private BatchGroup batchGroup;

	private ZonedDateTime tradeDateTime;

	private TradeGroup tradeGroup;

	@Override
	public int compareTo(CryptoEvent o) {
		return this.getWhen().compareTo(o.getWhen());
	}

	public ZonedDateTime getWhen() {
		return tradeDateTime;
	}

	public void setTradeGroup(TradeGroup tradeGroup) {
		this.tradeGroup = tradeGroup;
	}

	public CurrencyAmount getProfit() {
		return profit;
	}

	@Override
	public String toString() {
		return "TradeProfit [profit=" + profit + ", batchGroup=" + batchGroup + ", tradeDateTime=" + tradeDateTime
				+ ", tradeGroup=" + tradeGroup + ", isActioned=" + "]";
	}

	public void freeMemory() {
		this.batchGroup = null;
		;
		this.profit = null;
		this.tradeDateTime = null;
	}

	public MonetaryAmount getProfitInBaseCurrency() {
		ConversionUtils conversionUtils = ConversionUtils.of();
		MonetaryAmount result = conversionUtils.convert(profit, tradeDateTime);
		return conversionUtils.convert(Monetary.getCurrency("NZD"), result, tradeDateTime);
	}
	
	public MonetaryAmount getTotalRolloverFeesInBaseCurrency() {
		ConversionUtils conversionUtils = ConversionUtils.of();
		if (totalRolloverFees.isCrypto()) {
			MonetaryAmount result = conversionUtils.convert(totalRolloverFees.getCryptoAmount(), tradeDateTime);
			return conversionUtils.convert(Monetary.getCurrency("NZD"), result, tradeDateTime);
		} else {
			return conversionUtils.convert(Monetary.getCurrency("NZD"), totalRolloverFees.getAmount(), tradeDateTime);
		}
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
