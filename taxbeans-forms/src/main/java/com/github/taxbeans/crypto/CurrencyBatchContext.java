package com.github.taxbeans.crypto;

public class CurrencyBatchContext {

	private static ThreadLocal<CurrencyBatchGroup> batchGroup = new InheritableThreadLocal<CurrencyBatchGroup>();

	public static CurrencyBatchGroup getBatchGroup() {
		return batchGroup.get();
	}

	public static void setBatchGroup(CurrencyBatchGroup batchGroup1) {
		batchGroup.set(batchGroup1);
	}
}
