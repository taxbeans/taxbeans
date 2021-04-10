package com.github.taxbeans.crypto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;

import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CurrencyTrade extends AbstractCryptoEvent {
	
	final static Logger LOG = LoggerFactory.getLogger(CurrencyTrade.class);

	public static CurrencyTrade of(CurrencyBatchGroup batchGroup, 
			CurrencyAmount fromCurrencyAmount, 
			CurrencyAmount toCurrencyAmount, 
			ZonedDateTime tradeDateTime, 
			CurrencyFee fee) {
		CurrencyTrade trade = new CurrencyTrade();
		trade.batchGroup = batchGroup;
		trade.fromCurrencyAmount = fromCurrencyAmount.copy();
		trade.toCurrencyAmount = toCurrencyAmount;
		trade.tradeDateTime = tradeDateTime;
		trade.fee = fee;
		return trade;
	}

	private CurrencyAmount fromCurrencyAmount;

	private CurrencyBatchGroup batchGroup;
	
	private String group;

	private CurrencyAmount toCurrencyAmount;

	private ZonedDateTime tradeDateTime;

	private CurrencyFee fee;

	private TradeGroup tradeGroup;

	private boolean isActioned;
	
	private boolean isLeveraged;
	
	private CurrencyBatchSet batchesUsedToAction = CurrencyBatchSet.of();
	
	private boolean warnOnly = false;

	private MonetaryAmount costBasis;
	
	private int rowNum;

	private boolean testStrict = false;

	public CurrencyBatchSet action() {
		//update the amount remaining in the batch
		CurrencyBatchSet batches = determineBatchesToUse();
		CurrencyAmount currencyAmount = batches.sum(this.fromCurrencyAmount);
		if (currencyAmount.isLessThan(this.fromCurrencyAmount)) {
		//if (cryptoAmount.subtract(this.cryptoAmount).signum() < 0) {
			if (fromCurrencyAmount.getCurrencyCode().isSameCurrency(batchGroup.getBaseCurrency())) {
				LOG.warn("From is the base currency, so no batch required");
				isActioned = true;
				return CurrencyBatchSet.of();
			} else if (warnOnly) {
				LOG.warn("Not enough " + currencyAmount + " in batch set and the trade amount\n is "
						+ this.fromCurrencyAmount);
			} else {
				if (testStrict ) {
					throw new AssertionError("Not enough " + currencyAmount + " in batch set and the trade amount\n is "
						+ this.fromCurrencyAmount + " (this will inflate profits artificially), rowNum = " + this.getRowNum());
				}
			}
		}
		batchesUsedToAction = batches.use(this.fromCurrencyAmount);
		isActioned = true;
		return batchesUsedToAction;
//		for (Batch batch : batches) {
//			if (batch.getAmountRemaining().subtract(this.cryptoAmount).getAmount().signum() < 0) {
//				throw new AssertionError("Not enough " + this.cryptoAmount + " in batch and the amount remaining\n is "
//						+ batch.getAmountRemaining() + ", but in the whole batch set, there is " + cryptoAmount);
//			}
//			//CurrencyAmount leftOver = batch.use(cryptoAmount);
//		}
		
	}

	public MonetaryAmount calculateBaseCurrencyCost() {
		if (!isActioned) {
			this.action();
		}
		MonetaryAmount baseCurrencyCost = Money.of(BigDecimal.ZERO, batchGroup.getBaseCurrency());
		for (CurrencyBatch batch : batchesUsedToAction) {
			if (!batch.isFullyUsed()) {
				throw new AssertionError("Batch isn't fully used");
			}
			if (Configuration.getCostMethod() == UseMethod.FIFO) {
				baseCurrencyCost = baseCurrencyCost.add(batch.getInitialCostInBaseCurrency());
			} else if (Configuration.getCostMethod() == UseMethod.WAC) {
				baseCurrencyCost = baseCurrencyCost.add(batch.getAverageCost());
				if (batchGroup.getBaseCurrency().getCurrencyCode().equals(
						this.fromCurrencyAmount.getCurrencyCode().getCurrencyCode())) {
					LOG.info("From is the base currency so no need to remove from cost here");
				} else {
					batchGroup.removeFromCost(batch);
				}
			} else {
				throw new AssertionError("Please add support for cost method: " + 
						Configuration.getCostMethod());
			}
		}
		costBasis = baseCurrencyCost;
		return baseCurrencyCost;
	}

	public MonetaryAmount getCostBasis() {
		if (isLeveraged) {
			//leveraged trades have 0 cost basis by definition
			return batchGroup.getBaseCurrencyZeroAmount();
		}
		return costBasis;
	}

	public MonetaryAmount calculateFeeInBaseCurrency() {
		return ConversionUtils.of(batchGroup.getCurrencyConversionStrategy()).convert(getBaseCurrency(), 
				fee.getAmount(),
				tradeDateTime);
	}

	public MonetaryAmount calculateInitialCost() {
		if (!isActioned) {
			this.action();
		}
		MonetaryAmount initialCost = Money.of(BigDecimal.ZERO, batchGroup.getBaseCurrency());
		for (CurrencyBatch batch : batchesUsedToAction) {
			if (!batch.isFullyUsed()) {
				throw new AssertionError("Batch isn't fully used");
			}
			initialCost = initialCost.add(batch.getInitialCostInBaseCurrency());
		}
		return initialCost;
	}

	public MonetaryAmount calculateProfit() {
		if (tradeDateTime == null) {
			throw new AssertionError("Unknown date of trade");
		}
		MonetaryAmount calculateProfitExcludingFee = calculateProfitExcludingFee();
		if (fromCurrencyAmount.getCurrencyCode().isSameCurrency(batchGroup.getBaseCurrency())) {
			calculateProfitExcludingFee = Money.of(BigDecimal.ZERO, this.getBaseCurrency());
		}
		return calculateProfitExcludingFee.subtract(calculateFeeInBaseCurrency());
	}

	public MonetaryAmount calculateProfitExcludingFee() {
		final MonetaryAmount baseCurrencyResult = calculateResultInBaseCurrency();
		final MonetaryAmount baseCurrencyCost = calculateBaseCurrencyCost();
		return baseCurrencyResult.subtract(baseCurrencyCost);
	}

	public MonetaryAmount calculateResultInBaseCurrency() {
		final MonetaryAmount baseCurrencyAmount = ConversionUtils.of(batchGroup.getCurrencyConversionStrategy())
				.convert(getBaseCurrency(), toCurrencyAmount, tradeDateTime);
		return baseCurrencyAmount;
	}

	@Override
	public int compareTo(CryptoEvent o) {
		int result = this.getWhen().compareTo(o.getWhen());
		if (result == 0) {
			return -(o.getRowNum() - this.getRowNum());
		}
		return result;
	}

	public CurrencyBatchSet determineBatchesToUse() {
		return batchGroup.getAllBatches();
	}

	public CurrencyUnit getBaseCurrency() {
		return batchGroup.getBaseCurrency();
	}
	
	public BigDecimal getBaseCurrencyExchangeRate() {
		MonetaryAmount oneUnitConverted = ConversionUtils.of(batchGroup.getCurrencyConversionStrategy())
				.convert(getBaseCurrency(), toCurrencyAmount, tradeDateTime);
		return new BigDecimal(oneUnitConverted.getNumber().toString());
	}
	
	public ZonedDateTime getWhen() {
		return tradeDateTime;
	}

	public void setTradeGroup(TradeGroup tradeGroup) {
		this.tradeGroup = tradeGroup;
	}
	

	public CurrencyAmount getCurrencyAmount() {
		return fromCurrencyAmount;
	}

	@Override
	public String toString() {
		return "Trade [fromCurrencyAmount=" + fromCurrencyAmount + ", batchGroup=" + batchGroup + ", toCurrencyAmount=" + toCurrencyAmount
				+ ", tradeDateTime=" + tradeDateTime + ", fee=" + fee + ", tradeGroup=" + tradeGroup + ", isActioned="
				+ isActioned + ", batchesUsedToAction=" + batchesUsedToAction + "]";
	}

	public void freeMemory() {
		this.batchGroup = null;;
		this.fromCurrencyAmount = null;
		this.toCurrencyAmount = null;
		this.tradeDateTime = null;
		this.fee = null;
	}

	public CurrencyAmount getFee() {
		return this.fee.getAmount();
	}

	public boolean isLeveraged() {
		return isLeveraged;
	}

	public void setLeveraged(boolean isLeveraged) {
		this.isLeveraged = isLeveraged;
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
