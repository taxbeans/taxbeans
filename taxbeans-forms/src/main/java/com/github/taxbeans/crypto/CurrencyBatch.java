package com.github.taxbeans.crypto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Locale;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.format.AmountFormatQueryBuilder;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;

import org.javamoney.moneta.format.CurrencyStyle;

/**
 * A currency batch represents an amount of currency that has been bought on a particular date.
 *
 */
public class CurrencyBatch extends AbstractCryptoEvent {
	
	public static CurrencyBatch of(CurrencyAmount currencyAmount, ZonedDateTime of, CurrencyAmount costBase) {
		CurrencyBatch batch = new CurrencyBatch();
		batch.initialAmount = currencyAmount.copy();
		batch.datePurchased = of;
		batch.amountRemaining = currencyAmount.copy();
		batch.amountUsed = CurrencyAmount.of(BigDecimal.ZERO, currencyAmount);
		batch.initialCost = costBase.copy();
		return batch;
	}
	
	private String group;
	
	private ZonedDateTime datePurchased;
	
	private CurrencyAmount amountUsed;
	
	private CurrencyAmount amountRemaining;
	
	private CurrencyAmount initialAmount;
	
	private CurrencyAmount initialCost;

	private CurrencyBatchGroup batchGroup;

	private CurrencyBatchSet usedSubBatches = CurrencyBatchSet.of();
	
	private boolean leveraged;
	
	private int rowNum;
	
	private void assertBatchGroupSet() {
		if (batchGroup == null) {
			throw new AssertionError("Batch group not set, you must set the batch group first");
		}
	}

	private void assertInitialAmountNotNull() {
		if (initialAmount == null) {
			throw new AssertionError("Initial amount not set, you must set the initial amount");
		}
	}
	
	public boolean canUse(CurrencyAmount currencyAmount) {
		CurrencyAmount amountRemaining2 = getAmountRemaining();
		if (!amountRemaining2.isSameCurrency(currencyAmount)) {
			return false;
		}
		return currencyAmount.isLessThanOrEqualTo(amountRemaining2);
	}

	@Override
	public int compareTo(CryptoEvent o) {
		return this.getWhen().compareTo(o.getWhen());
	}
	
	public CurrencyAmount getAmountRemaining() {
		return amountRemaining;
	}

	public MonetaryAmount getInitialCostInBaseCurrency() {
		if (leveraged) {
			return batchGroup.getBaseCurrencyZeroAmount();
		}
		CurrencyUnit baseCurrency = batchGroup.getBaseCurrency();
		if (this.initialAmount.getCurrencyCode().isSameCurrency(baseCurrency)) {
			return this.initialAmount.getMonetaryAmount();
		} else if (this.initialCost.getCurrencyCode().isSameCurrency(baseCurrency)) {
			return this.initialCost.getMonetaryAmount();
		} else {
			assertBatchGroupSet();
			ConversionUtils converter = ConversionUtils.of(batchGroup.getCurrencyConversionStrategy());
			MonetaryAmount baseCurrencyCost = converter.convert(baseCurrency, initialAmount, datePurchased);
			return baseCurrencyCost;
		}
	}

	public MonetaryAmount getUnitInitialCostInBaseCurrency() {
		assertInitialAmountNotNull();
		return initialAmount.swapDivide(getInitialCostInBaseCurrency());  //.divide(initialAmount.getAmount());
	}

	public ZonedDateTime getWhen() {
		return datePurchased;
	}

	public boolean isFullyUsed() {
		return amountRemaining.isZeroOrLess();
	}

	public void setBatchGroup(CurrencyBatchGroup batchGroup) {
		this.batchGroup = batchGroup;
	}

	@Override
	public String toString() {
		final MonetaryAmountFormat format = MonetaryFormats.getAmountFormat(
			      AmountFormatQueryBuilder.of(Locale.US)
			        .set(CurrencyStyle.SYMBOL)
			        .build()
			    );
		return "Batch [datePurchased=" + datePurchased + ", amountUsed=" + amountUsed
				+ ", amountRemaining="
				+ amountRemaining + ", initialAmount=" + initialAmount + ", initialCostInBaseCurrency=" + 
				format.format(this.getInitialCostInBaseCurrency())
				+ ", batchGroup=" + batchGroup + ", usedSubBatches=" + usedSubBatches + "]";
	}

	public CurrencyBatchSet use(CurrencyAmount currencyAmount) {
		if (!canUse(currencyAmount)) {
			throw new AssertionError(currencyAmount + " is greater than amount remaining of " + getAmountRemaining());
		}
		//amountRemaining.mutatingSubtract(cryptoAmount);
		amountUsed = amountUsed.add(currencyAmount);
		amountRemaining = amountRemaining.subtract(currencyAmount);
		//amountUsed = amountUsed.add(cryptoAmount);
		CurrencyBatchSet localUsedSubBatches = CurrencyBatchSet.of();
		CurrencyBatch usedSubBatch = CurrencyBatch.of(CurrencyAmount.of(currencyAmount, this.amountRemaining), 
				this.getWhen(), CurrencyAmount.of(currencyAmount.multiply(getUnitInitialCostInBaseCurrency())));
		usedSubBatch.amountUsed = amountRemaining.copy();
		usedSubBatch.amountRemaining = CurrencyAmount.of(BigDecimal.ZERO, this.amountUsed);
		usedSubBatch.setBatchGroup(this.batchGroup);
		usedSubBatches.add(usedSubBatch);
		localUsedSubBatches.add(usedSubBatch);
		return localUsedSubBatches;
	}

	public CurrencyAmount getInitialAmount() {
		return initialAmount;
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

	public boolean isSameCurrency(CurrencyAmount currencyAmount) {
		return amountRemaining.isSameCurrency(currencyAmount);
	}

	public int getRowNum() {
		return rowNum;
	}

	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}

	public MonetaryAmount getAverageCost() {
		return batchGroup.getCostViaWAC(this);
	}
}
