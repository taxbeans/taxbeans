package com.github.taxbeans.crypto;

import javax.money.MonetaryAmount;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeightedAverageCost {

	final static Logger LOG = LoggerFactory.getLogger(WeightedAverageCost.class);
	
	private MonetaryAmount totalCostInBaseCurrency;

	private CurrencyAmount totalCurrencyAmount;

	private MonetaryAmount cachedAverageCost;
	
	public static WeightedAverageCost of(MonetaryAmount totalCostInBaseCurrency,
			CurrencyAmount totalCurrencyAmount) {
		WeightedAverageCost object = new WeightedAverageCost(totalCostInBaseCurrency,
				totalCurrencyAmount);
		return object;
	}

	private WeightedAverageCost(MonetaryAmount totalCostInBaseCurrency, CurrencyAmount totalCurrencyAmount) {
		super();
		this.totalCostInBaseCurrency = totalCostInBaseCurrency;
		this.totalCurrencyAmount = totalCurrencyAmount;
	}

	public MonetaryAmount getAverageCost() {
		if (totalCurrencyAmount.isZero()) {
			if (Configuration.strictAverageCostCalc()) {
				Assert.fail();
			}
			LOG.warn("totalCurrencyAmount is zero");
			return cachedAverageCost;
		}
		cachedAverageCost = totalCurrencyAmount.swapDivide(
				totalCostInBaseCurrency);
		LOG.info(this.toString());
		return cachedAverageCost;
	}

	public void add(MonetaryAmount totalCost, CurrencyAmount totalAmount) {
		this.totalCostInBaseCurrency = this.totalCostInBaseCurrency.add(totalCost);
		this.totalCurrencyAmount = this.totalCurrencyAmount.add(totalAmount);
	}

	public void subtract(MonetaryAmount totalCost, CurrencyAmount totalAmount) {
		if (this.totalCostInBaseCurrency.isLessThan(totalCost) ||
				this.totalCurrencyAmount.isLessThan(totalAmount)) {
			if (Configuration.strictAverageCostCalc()) {
				Assert.fail();
			}
			LOG.warn("values to small");
		}
		this.totalCostInBaseCurrency = this.totalCostInBaseCurrency.subtract(totalCost);
		this.totalCurrencyAmount = this.totalCurrencyAmount.subtract(totalAmount);
	}

	@Override
	public String toString() {
		return String.format("WeightedAverageCost [totalCostInBaseCurrency=%s, totalCurrencyAmount=%s, Average=%s]",
				MonetaryUtils.round(totalCostInBaseCurrency), 
				totalCurrencyAmount, 
				cachedAverageCost == null ? 
						MonetaryUtils.round(totalCurrencyAmount.swapDivide(totalCostInBaseCurrency)) :
						MonetaryUtils.round(cachedAverageCost));
	}

}
