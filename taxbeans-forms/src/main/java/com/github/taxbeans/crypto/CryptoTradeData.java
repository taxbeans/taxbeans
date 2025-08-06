package com.github.taxbeans.crypto;

import java.util.List;

public class CryptoTradeData {
	
	private BatchGroup batchGroup;
	
	private List<CryptoEvent> cryptoEvents;
	
	public static CryptoTradeData of(BatchGroup batchGroup, List<CryptoEvent> cryptoEvents) {
		CryptoTradeData cryptoTradeData = new CryptoTradeData();
		cryptoTradeData.batchGroup = batchGroup;
		cryptoTradeData.cryptoEvents = cryptoEvents;
		return cryptoTradeData;
	}

	public BatchGroup getBatchGroup() {
		return batchGroup;
	}

	public List<CryptoEvent> getCryptoEvents() {
		return cryptoEvents;
	}
	
	

}
