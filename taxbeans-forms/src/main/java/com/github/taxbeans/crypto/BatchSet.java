package com.github.taxbeans.crypto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BatchSet implements Iterable<Batch> {
	
	public static BatchSet of() {
		return BatchSet.of(new ArrayList<Batch>());
	}

	public static BatchSet of(List<Batch> batches) {
		BatchSet batchSet = new BatchSet();
		batchSet.batches = batches;
		return batchSet;
	}

	UseMethod useMethod = UseMethod.FIFO;

	private List<Batch> batches;

	public void add(Batch batch) {
		batches.add(batch);
	}

	private void add(BatchSet batchSet) {
		batches.addAll(batchSet.batches);
	}

	@Override
	public Iterator<Batch> iterator() {
		return batches.iterator();
	}

	public CryptoAmount sum(Crypto crypto) {
		CryptoAmount amount = CryptoAmount.of(crypto,  BigDecimal.ZERO);
		for (Batch batch : batches) {
			if (batch.getAmountRemaining().getCrypto() == crypto) {
				amount = batch.getAmountRemaining().add(amount);
			}
		}
		return amount;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Batch batch : batches) {
			sb.append(batch + "\n");
		}
		return sb.toString();
	}

	public BatchSet use(CryptoAmount cryptoAmount) {
		BatchSet batchSet = BatchSet.of();
		if (useMethod == useMethod.FIFO) {		
			List<Batch> batchesToRemove = new ArrayList<Batch>();
			for (Batch batch : batches) {
				if (!batch.isSameCrypto(cryptoAmount)) {
					continue;
				}
				if (batch.canUse(cryptoAmount)) {
					batchSet.add(batch.use(cryptoAmount));
					if (batch.isFullyUsed()) {
						batchesToRemove.add(batch);
					}
					break;
				} else {
					if (!batch.canUse(batch.getAmountRemaining())) {
						throw new AssertionError("amount remaining, " + batch.getAmountRemaining() + " cannot be used");
					} else {
						CryptoAmount amountUsed = batch.getAmountRemaining();
						batchSet.add(batch.use(amountUsed));
						cryptoAmount = cryptoAmount.subtract(amountUsed);
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
