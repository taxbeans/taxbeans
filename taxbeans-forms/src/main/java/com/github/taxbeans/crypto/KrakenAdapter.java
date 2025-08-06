package com.github.taxbeans.crypto;

import java.util.HashMap;
import java.util.Map;

public class KrakenAdapter {

	volatile static Map<String, String> typeMap = null;
	
	public static String adapt(String value) {
		if (typeMap == null) {
			synchronized(KrakenAdapter.class) {
				if (typeMap == null) {
					typeMap = new HashMap<String, String>();
					typeMap.put("deposit", "Deposit");
					typeMap.put("withdrawal", "Withdrawal");
					typeMap.put("margin", "Margin");
					//treat settled like Margin:
					typeMap.put("settled", "Margin");
					//treat transfer like Margin: (transfer=airdrop)
					typeMap.put("transfer", "Margin");
					typeMap.put("rollover", "Rollover");
					typeMap.put("trade", "Trade");
				}
			}
		}
		return typeMap.get(value);
	}

	public static String adaptCurrency(String krakenCurrency) {
		if (krakenCurrency.equals("XXBT")) {
			return "BTC";
		} else {
			if (krakenCurrency.startsWith("X") || krakenCurrency.startsWith("Z")) {
				return krakenCurrency.substring(1);
			} else {
				return krakenCurrency;
			}
		}
	}

}
