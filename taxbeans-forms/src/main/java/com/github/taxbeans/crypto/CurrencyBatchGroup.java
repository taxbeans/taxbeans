package com.github.taxbeans.crypto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;

import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CurrencyBatchGroup {

	private volatile MonetaryAmount zeroAmount;

	private static int batchGroupCount;

	final static Logger LOG = LoggerFactory.getLogger(CurrencyBatchGroup.class);

	private volatile Map<CurrencyCode, WeightedAverageCost> averageCosts =
			new HashMap<CurrencyCode, WeightedAverageCost>();
	
	public static CurrencyBatchGroup of() {
		batchGroupCount++;
		if (Configuration.shouldEnforceOneBatchGroupPerVM() && batchGroupCount > 1) {
			throw new AssertionError();
		}
		return new CurrencyBatchGroup();
	}

	public static CurrencyBatchGroup of(CurrencyUnit baseCurrency) {
		CurrencyBatchGroup batchGroup = new CurrencyBatchGroup();
		batchGroupCount++;
		if (Configuration.shouldEnforceOneBatchGroupPerVM() && batchGroupCount > 1) {
			throw new AssertionError();
		}
		batchGroup.baseCurrency = baseCurrency;
		batchGroup.currencyConversionStrategy = CurrencyConversionStrategy.RBNZ;
		return batchGroup;
	}

	public static CurrencyBatchGroup of(CurrencyUnit currency, CurrencyConversionStrategy rbnz) {
		CurrencyBatchGroup group = CurrencyBatchGroup.of(currency);
		group.currencyConversionStrategy = rbnz;
		return group;
	}

	private CurrencyUnit baseCurrency;

	private List<CurrencyBatch> batches = new ArrayList<CurrencyBatch>();

	private CurrencyConversionStrategy currencyConversionStrategy;

	public void add(CurrencyBatch batch) {
		batches.add(batch);
		batch.setBatchGroup(this);
		
		//Update the weighted average cost
		CurrencyCode currencyCode = batch.getInitialAmount().getCurrencyCode();
		WeightedAverageCost weightedAverageCost = averageCosts.get(currencyCode);
		MonetaryAmount totalCost = batch.getInitialCostInBaseCurrency();
		CurrencyAmount totalAmount = batch.getInitialAmount();
		if (weightedAverageCost == null) {
			weightedAverageCost = WeightedAverageCost.of(totalCost, totalAmount);
			averageCosts.put(currencyCode, weightedAverageCost);
		} else {
			weightedAverageCost.add(totalCost, totalAmount);
		}
	}

	public CurrencyBatchSet getAllBatches() {
		return CurrencyBatchSet.of(batches);
	}

	public CurrencyUnit getBaseCurrency() {
		return baseCurrency;
	}

	public CurrencyConversionStrategy getCurrencyConversionStrategy() {
		return currencyConversionStrategy;
	}

	public MonetaryAmount getBaseCurrencyZeroAmount() {
		//use lazy loading to miminize amount of GC garbage created for 
		//high volume trading applications:
		synchronized (this) {
			if (zeroAmount == null) {
				synchronized (this) {
					if (zeroAmount == null) {
						zeroAmount = Money.of(BigDecimal.ZERO, baseCurrency);
					}
				}
			}
		}
		return zeroAmount;
	}

	public MonetaryAmount getCostViaWAC(CurrencyBatch currencyBatch) {
		CurrencyCode code = currencyBatch.getInitialAmount().getCurrencyCode();
		WeightedAverageCost weightedAverageCost = averageCosts.get(code);
		return weightedAverageCost.getAverageCost().multiply(currencyBatch.getInitialAmount().getBigDecimal());
	}

	public void removeFromCost(CurrencyBatch batch) {
		CurrencyAmount initialAmount = batch.getInitialAmount();
		MonetaryAmount initialCost = batch.getInitialCostInBaseCurrency();
		this.removeFromCostInner(initialCost, initialAmount);
	}

	//TODO implement for FIFO too
	public void removeFromCostInner(MonetaryAmount initialCost, CurrencyAmount initialAmount) {
		CurrencyCode code = initialAmount.getCurrencyCode();
		WeightedAverageCost weightedAverageCost = averageCosts.get(code);
		MonetaryAmount averageCost = weightedAverageCost.getAverageCost();
		MonetaryAmount totalCost = averageCost.multiply(initialAmount.getBigDecimal());
		if (initialCost == null || Configuration.shouldSubtractAverageCost()) {
			weightedAverageCost.subtract(totalCost, initialAmount);
		} else {
			weightedAverageCost.subtract(initialCost, initialAmount);
		}
	}

	public MonetaryAmount getAverageCostPerUnit(CurrencyCode code) {
		return averageCosts.get(code).getAverageCost();
	}

	public void subtract(CurrencyAmount loss, ZonedDateTime when) {
		if (Configuration.subtractUsingLogicalTrade()) {
			CurrencyTrade logicalTrade = CurrencyTrade.of(this, 
					loss, 
					CurrencyAmount.getZeroNZD(), 
					when, 
					CurrencyFee.of(CurrencyAmount.getZeroNZD()));
			logicalTrade.calculateBaseCurrencyCost();
		} else {
			this.removeFromCostInner(null, loss);
		}
	}

	public void logWACs() {
		for (Entry<CurrencyCode, WeightedAverageCost> entry : averageCosts.entrySet()) {
			LOG.info(entry.toString());
		}
		
	}
}
