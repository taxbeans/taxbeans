package com.github.taxbeans.crypto;

import java.util.List;

public class CurrencyTradeData {
	
	private CurrencyBatchGroup batchGroup;
	
	private List<CryptoEvent> cryptoEvents;
	
	public static CurrencyTradeData of(CurrencyBatchGroup batchGroup, List<CryptoEvent> cryptoEvents) {
		CurrencyTradeData currencyTradeData = new CurrencyTradeData();
		currencyTradeData.batchGroup = batchGroup;
		currencyTradeData.cryptoEvents = cryptoEvents;
		return currencyTradeData;
	}

	public CurrencyBatchGroup getBatchGroup() {
		return batchGroup;
	}

	public List<CryptoEvent> getCryptoEvents() {
		return cryptoEvents;
	}
}
