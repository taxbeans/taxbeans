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
public class CurrencyTradeProfit extends AbstractCryptoEvent {

	final static Logger logger = LoggerFactory.getLogger(CurrencyTradeProfit.class);

	private CurrencyFee totalRolloverFees;
	
	private String group;
	
	private int rowNum;

	public static CurrencyTradeProfit of(CurrencyBatchGroup batchGroup, CurrencyAmount profit, 
			ZonedDateTime tradeDateTime, CurrencyFee totalRolloverFees) {
		CurrencyTradeProfit trade = new CurrencyTradeProfit();
		trade.batchGroup = batchGroup;
		trade.profit = profit;
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

	private CurrencyAmount profit;

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

	public CurrencyAmount getProfit() {
		return profit;
	}

	@Override
	public String toString() {
		return String.format("CurrencyTradeProfit [profit= %1$s]  [datetime=%2$s] [fee=%3$s] [rownum=%4$s]", profit, tradeDateTime, totalRolloverFees, rowNum);
	}

	public void freeMemory() {
		this.batchGroup = null;
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

}
