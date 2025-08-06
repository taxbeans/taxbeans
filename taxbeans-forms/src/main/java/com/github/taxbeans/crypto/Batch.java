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
 * A batch represents an of crypto that has been bought on a particular date.
 *
 */
public class Batch extends AbstractCryptoEvent {
	
	public static Batch of(CryptoAmount cryptoAmount, ZonedDateTime of, MonetaryAmount money) {
		Batch batch = new Batch();
		batch.initialAmount = cryptoAmount.copy();
		batch.datePurchased = of;
		batch.amountRemaining = cryptoAmount.copy();
		batch.amountUsed = CryptoAmount.of(cryptoAmount.getCrypto(), BigDecimal.ZERO);
		batch.initialCost = money;
		return batch;
	}
	
	private String group;
	
	private ZonedDateTime datePurchased;
	
	private CryptoAmount amountUsed;
	
	private CryptoAmount amountRemaining;
	
	private CryptoAmount initialAmount;
	
	private MonetaryAmount initialCost;

	private BatchGroup batchGroup;

	private BatchSet usedSubBatches = BatchSet.of();
	
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
	
	public boolean isSameCrypto(CryptoAmount cryptoAmount) {
		return getAmountRemaining().getCrypto().equals(cryptoAmount.getCrypto());
	}
	
	public boolean canUse(CryptoAmount cryptoAmount) {
		if (!getAmountRemaining().getCrypto().equals(cryptoAmount.getCrypto())) {
			return false;
		}
		//return getAmountRemaining().subtract(cryptoAmount).signum() >= 0;
		return cryptoAmount.isLessThanOrEqualTo(getAmountRemaining());
	}

	@Override
	public int compareTo(CryptoEvent o) {
		return this.getWhen().compareTo(o.getWhen());
	}
	
	public CryptoAmount getAmountRemaining() {
		return amountRemaining;
	}
	
	public MonetaryAmount getInitialCost() {
		if (leveraged) {
			return batchGroup.getZeroAmount();
		}
		return initialCost;
	}

	public MonetaryAmount getInitialCostInBaseCurrency() {
		if (leveraged) {
			return batchGroup.getZeroAmount();
		}
		assertBatchGroupSet();
		CurrencyUnit baseCurrency = batchGroup.getBaseCurrency();
		MonetaryAmount baseCurrencyCost = ConversionUtils.of(batchGroup.getCurrencyConversionStrategy())
				.convert(baseCurrency, initialCost, datePurchased);
		return baseCurrencyCost;
	}

	public MonetaryAmount getUnitInitialCost() {
		assertInitialAmountNotNull();
		return initialCost.divide(initialAmount.getAmount());
	}

	public MonetaryAmount getUnitInitialCostInBaseCurrency() {
		assertInitialAmountNotNull();
		return getInitialCostInBaseCurrency().divide(initialAmount.getAmount());
	}

	public ZonedDateTime getWhen() {
		return datePurchased;
	}

	public boolean isFullyUsed() {
		return amountRemaining.getAmount().equals(BigDecimal.ZERO);
	}

	public void setBatchGroup(BatchGroup batchGroup2) {
		this.batchGroup = batchGroup2;
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
				+ amountRemaining + ", initialAmount=" + initialAmount + ", initialCost=" + 
				format.format(initialCost)
				+ ", batchGroup=" + batchGroup + ", usedSubBatches=" + usedSubBatches + "]";
	}

	public BatchSet use(CryptoAmount cryptoAmount) {
		if (!canUse(cryptoAmount)) {
			throw new AssertionError(cryptoAmount + " is greater than amount remaining of " + getAmountRemaining());
		}
		//amountRemaining.mutatingSubtract(cryptoAmount);
		amountUsed.mutatingAdd(cryptoAmount);
		amountRemaining = amountRemaining.subtract(cryptoAmount);
		//amountUsed = amountUsed.add(cryptoAmount);
		BatchSet localUsedSubBatches = BatchSet.of();
		Batch usedSubBatch = Batch.of(CryptoAmount.of(this.getAmountRemaining().getCrypto(), cryptoAmount.getAmount()), 
				this.getWhen(), getUnitInitialCostInBaseCurrency().multiply(cryptoAmount.getAmount()));
		usedSubBatch.amountUsed = amountRemaining.copy();
		usedSubBatch.amountRemaining = CryptoAmount.of(this.amountUsed.getCrypto(), BigDecimal.ZERO);
		usedSubBatch.setBatchGroup(this.batchGroup);
		usedSubBatches.add(usedSubBatch);
		localUsedSubBatches.add(usedSubBatch);
		return localUsedSubBatches;
	}

	public CryptoAmount getInitialAmount() {
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

	public int getRowNum() {
		return rowNum;
	}

	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}
}
