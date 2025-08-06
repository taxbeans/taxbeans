package com.github.taxbeans.crypto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CurrencyBatchSet implements Iterable<CurrencyBatch> {

	/*
	 * N.B. this class is just a stateless wrapper
	 * actual state should be added to currency batch group
	 */

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

	private List<CurrencyBatch> archivedSplitBatches = new ArrayList<CurrencyBatch>();

	private List<CurrencyBatch> archivedUsedBatches =  new ArrayList<CurrencyBatch>();

	private List<CurrencyAmount> lending = new ArrayList<CurrencyAmount>();

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

	public CurrencyAmount sum(CurrencyCode currencyAmount) {
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

	/**
	 * Returns all fully used batches
	 */
	public CurrencyBatchSet use(CurrencyAmount currencyAmount) {
		CurrencyBatchSet fullyUsedBatches = CurrencyBatchSet.of();
		if (useMethod == UseMethod.FIFO ||
				useMethod == UseMethod.WAC) {
			if ("USD".equals(currencyAmount.getCurrencyString())) {
				System.out.println("code = " + "USD");
			}
			List<CurrencyBatch> batchesToRemove = new ArrayList<CurrencyBatch>();
			List<CurrencyBatch> batchesToAdd = new ArrayList<CurrencyBatch>();

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
					CurrencyBatchSet result = batch.use(currencyAmount);
					List<CurrencyBatch> resultBatches = result.batches;
					for (CurrencyBatch resultBatch : resultBatches) {
						if (resultBatch.split && resultBatch.fullyUsed) {
							archivedSplitBatches.add(resultBatch);
							batchesToRemove.add(resultBatch);
						} else if (resultBatch.fullyUsed) {
							archivedUsedBatches.add(resultBatch);
							fullyUsedBatches.add(resultBatch);
						} else {
							//batchesToAdd.add(resultBatch);
						}
					}
					break;
				} else {
					if (!batch.canUse(batch.getAmountRemaining())) {
						throw new AssertionError("amount remaining, " + batch.getAmountRemaining() + " cannot be used");
					} else {
						CurrencyAmount amountUsed = batch.getAmountRemaining();

						CurrencyBatchSet result = batch.use(amountUsed);
						List<CurrencyBatch> resultBatches = result.batches;
						for (CurrencyBatch resultBatch : resultBatches) {
							if (resultBatch.split && resultBatch.fullyUsed) {
								archivedSplitBatches.add(resultBatch);
								batchesToRemove.add(resultBatch);
							} else if (resultBatch.fullyUsed) {
								archivedUsedBatches.add(resultBatch);
								fullyUsedBatches.add(resultBatch);
							} else {
								//batchesToAdd.add(resultBatch);
							}
						}
						CurrencyCode currencyCode = amountUsed.getCurrencyCode();
						ZonedDateTime lastBatchDate = lastBatchDates.get(currencyCode);
						//TODO fix FIFO order
						if (lastBatchDate != null && lastBatchDate.isAfter(batch.getWhen())) {
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
			batches.addAll(batchesToAdd);
		}
		return fullyUsedBatches;
	}

	public void lend(CurrencyAmount amountLent) {
		System.err.println("Lending amount of: " + amountLent);
		lending.add(amountLent);
	}
}
