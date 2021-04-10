package com.github.taxbeans.crypto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;

import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarginGain extends AbstractCryptoEvent {
	
	final static Logger logger = LoggerFactory.getLogger(MarginGain.class);

	public static MarginGain of(BatchGroup batchGroup, 
			CryptoAmount cryptoAmount, 
			ZonedDateTime tradeDateTime, 
			Fee fee) {
		MarginGain trade = new MarginGain();
		trade.batchGroup = batchGroup;
		trade.cryptoAmount = cryptoAmount.copy();
		trade.tradeDateTime = tradeDateTime;
		trade.fee = fee;
		return trade;
	}

	private String group;
	
	private CryptoAmount cryptoAmount;

	private BatchGroup batchGroup;

	private MonetaryAmount money;

	private ZonedDateTime tradeDateTime;

	private Fee fee;

	private TradeGroup tradeGroup;

	private boolean isActioned;
	
	private BatchSet batchesUsedToAction = BatchSet.of();
	
	private boolean warnOnly = true;

	private MonetaryAmount costBasis;
	
	private int rowNum;

	public BatchSet action() {
		//update the amount remaining in the batch
		BatchSet batches = determineBatchesToUse();
		CryptoAmount cryptoAmount = batches.sum(this.cryptoAmount.getCrypto());
		if (cryptoAmount.isLessThan(this.cryptoAmount)) {
		//if (cryptoAmount.subtract(this.cryptoAmount).signum() < 0) {
			if (warnOnly) {
				logger.warn("Not enough " + cryptoAmount + " in batch set and the trade amount\n is "
						+ this.cryptoAmount);
			} else {
				throw new AssertionError("Not enough " + cryptoAmount + " in batch set and the trade amount\n is "
						+ this.cryptoAmount);
			}
		}
		batchesUsedToAction = batches.use(this.cryptoAmount);
		isActioned = true;
		return batchesUsedToAction;
//		for (Batch batch : batches) {
//			if (batch.getAmountRemaining().subtract(this.cryptoAmount).getAmount().signum() < 0) {
//				throw new AssertionError("Not enough " + this.cryptoAmount + " in batch and the amount remaining\n is "
//						+ batch.getAmountRemaining() + ", but in the whole batch set, there is " + cryptoAmount);
//			}
//			//CryptoAmount leftOver = batch.use(cryptoAmount);
//		}
		
	}

	public MonetaryAmount calculateBaseCurrencyCost() {
		if (!isActioned) {
			this.action();
		}
		MonetaryAmount baseCurrencyCost = Money.of(BigDecimal.ZERO, batchGroup.getBaseCurrency());
		for (Batch batch : batchesUsedToAction) {
			if (!batch.isFullyUsed()) {
				throw new AssertionError("Batch isn't fully used");
			}
			baseCurrencyCost = baseCurrencyCost.add(batch.getInitialCostInBaseCurrency());
		}
		costBasis = baseCurrencyCost;
		return baseCurrencyCost;
	}

	public MonetaryAmount getCostBasis() {
		return costBasis;
	}

	public MonetaryAmount calculateFeeInBaseCurrency() {
		if (fee.isCrypto()) {
			MonetaryAmount usdAmount = ConversionUtils.of(batchGroup.getCurrencyConversionStrategy())
					.convert(fee.getCryptoAmount(), tradeDateTime);
			return ConversionUtils.of(batchGroup.getCurrencyConversionStrategy()).convert(getBaseCurrency(), 
					usdAmount,
					tradeDateTime);
		}
		return ConversionUtils.of(batchGroup.getCurrencyConversionStrategy()).convert(getBaseCurrency(), 
				fee.getAmount(),
				tradeDateTime);
	}

	public MonetaryAmount calculateInitialCost() {
		if (!isActioned) {
			this.action();
		}
		MonetaryAmount initialCost = Money.of(BigDecimal.ZERO, batchGroup.getBaseCurrency());
		for (Batch batch : batchesUsedToAction) {
			if (!batch.isFullyUsed()) {
				throw new AssertionError("Batch isn't fully used");
			}
			initialCost = initialCost.add(batch.getInitialCost());
		}
		return initialCost;
	}

	public MonetaryAmount calculateProfit() {
		if (tradeDateTime == null) {
			throw new AssertionError("Unknown date of trade");
		}
		return calculateProfitExcludingFee().subtract(calculateFeeInBaseCurrency());
	}

	public MonetaryAmount calculateProfitExcludingFee() {
		return calculateResultInBaseCurrency().subtract(calculateBaseCurrencyCost());
	}

	public MonetaryAmount calculateResultInBaseCurrency() {
		return ConversionUtils.of(batchGroup.getCurrencyConversionStrategy())
				.convert(getBaseCurrency(), money, tradeDateTime);
	}

	@Override
	public int compareTo(CryptoEvent o) {
		return this.getWhen().compareTo(o.getWhen());
	}

	public BatchSet determineBatchesToUse() {
		return batchGroup.getAllBatches();
	}

	public CurrencyUnit getBaseCurrency() {
		return batchGroup.getBaseCurrency();
	}
	
	public BigDecimal getBaseCurrencyExchangeRate() {
		MonetaryAmount oneUnitConverted = ConversionUtils.of(batchGroup.getCurrencyConversionStrategy())
				.convert(getBaseCurrency(), Money.of(BigDecimal.ONE, money.getCurrency()), tradeDateTime);
		return new BigDecimal(oneUnitConverted.getNumber().toString());
	}
	
	public ZonedDateTime getWhen() {
		return tradeDateTime;
	}

	public void setTradeGroup(TradeGroup tradeGroup) {
		this.tradeGroup = tradeGroup;
	}
	

	public CryptoAmount getCryptoAmount() {
		return cryptoAmount;
	}

	@Override
	public String toString() {
		return "Trade [cryptoAmount=" + cryptoAmount + ", batchGroup=" + batchGroup + ", money=" + money
				+ ", tradeDateTime=" + tradeDateTime + ", fee=" + fee + ", tradeGroup=" + tradeGroup + ", isActioned="
				+ isActioned + ", batchesUsedToAction=" + batchesUsedToAction + "]";
	}

	public void freeMemory() {
		this.batchGroup = null;;
		this.cryptoAmount = null;
		this.money = null;
		this.tradeDateTime = null;
		this.fee = null;
	}

	public CryptoAmount getFee() {
		CryptoAmount cryptoAmount2  = this.fee.getCryptoAmount();
		return cryptoAmount2 == null ? CryptoAmount.of(cryptoAmount.getCrypto(), BigDecimal.ZERO) : cryptoAmount2;
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
