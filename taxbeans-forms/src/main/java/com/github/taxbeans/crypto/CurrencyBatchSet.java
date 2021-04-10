package com.github.taxbeans.crypto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CurrencyBatchSet implements Iterable<CurrencyBatch> {
	
	private static Map<CurrencyCode, ZonedDateTime> lastBatchDates = new HashMap<CurrencyCode, ZonedDateTime>();
	
	public static CurrencyBatchSet of() {
		return CurrencyBatchSet.of(new ArrayList<CurrencyBatch>());
	}

	public static CurrencyBatchSet of(List<CurrencyBatch> batches) {
		CurrencyBatchSet batchSet = new CurrencyBatchSet();
		batchSet.batches = batches;
		return batchSet;
	}

	UseMethod useMethod = Configuration.getCostMethod();

	private List<CurrencyBatch> batches;

	public void add(CurrencyBatch batch) {
		batches.add(batch);
	}

	private void add(CurrencyBatchSet batchSet) {
		batches.addAll(batchSet.batches);
	}
 
	@Override
	public Iterator<CurrencyBatch> iterator() {
		return batches.iterator();
	}

	/*
	 * @param currencyAmount Used only to define the currency to use (amount ignored)
	 */
	public CurrencyAmount sum(CurrencyAmount currencyAmount) {
		CurrencyAmount amount = CurrencyAmount.of(BigDecimal.ZERO, currencyAmount);
		for (CurrencyBatch batch : batches) {
			if (batch.getAmountRemaining().isSameCurrency(currencyAmount)) {
				amount = batch.getAmountRemaining().add(amount);
			}
		}
		return amount;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (CurrencyBatch batch : batches) {
			sb.append(batch + "\n");
		}
		return sb.toString();
	}

	public CurrencyBatchSet use(CurrencyAmount currencyAmount) {
		CurrencyBatchSet batchSet = CurrencyBatchSet.of();
		if (useMethod == UseMethod.FIFO ||
				useMethod == UseMethod.WAC) {		
			List<CurrencyBatch> batchesToRemove = new ArrayList<CurrencyBatch>();
			for (CurrencyBatch batch : batches) {
				if (batch.isFullyUsed()) {
					//batch is already fully used
					batchesToRemove.add(batch);
					continue;
				}
				if (!batch.isSameCurrency(currencyAmount)) {
					continue;
				}
				if (batch.canUse(currencyAmount)) {
					batchSet.add(batch.use(currencyAmount));
					if (batch.isFullyUsed()) {
						batchesToRemove.add(batch);
					}
					break;
				} else {
					if (!batch.canUse(batch.getAmountRemaining())) {
						throw new AssertionError("amount remaining, " + batch.getAmountRemaining() + " cannot be used");
					} else {
						CurrencyAmount amountUsed = batch.getAmountRemaining();
						batchSet.add(batch.use(amountUsed));
						CurrencyCode currencyCode = amountUsed.getCurrencyCode();
						ZonedDateTime lastBatchDate = lastBatchDates.get(currencyCode);
						if (lastBatchDate != null && lastBatchDate.compareTo(batch.getWhen()) > 0) {
							throw new AssertionError(String.format("Batch out of order %s, %s for %s", 
									lastBatchDate, batch.getWhen(), currencyCode));
						}
						lastBatchDate = batch.getWhen();
						lastBatchDates.put(currencyCode, lastBatchDate);
						currencyAmount = currencyAmount.subtract(amountUsed);
					}
				}
				if (batch.isFullyUsed()) {
					batchesToRemove.add(batch);
				}
			}
			batches.removeAll(batchesToRemove);
		}
		return batchSet;
	}
}
