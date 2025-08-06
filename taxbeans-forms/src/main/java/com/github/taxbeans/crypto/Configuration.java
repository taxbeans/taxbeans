package com.github.taxbeans.crypto;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

public class Configuration {

	public static boolean shouldFreeMemory() {
		return true;
	}
	
	public static CurrencyUnit getBaseCurrency() {
		return Monetary.getCurrency("NZD"); 
	}
	
	public static UseMethod getCostMethod() {
		if ( UseMethod.FIFO.name().equals(System.getProperty("CurrencyTrading.CostMethod"))) {
			return UseMethod.FIFO;
		} else {
			return UseMethod.WAC;
		}
	}
	
	public static boolean shouldExcludeFiatFromTransferDetection() {
		return true;
	}

	public static boolean shouldDetectTransfersOutOfOrder() {
		return true;
	}

	public static boolean strictAverageCostCalc() {
		return false;
	}

	public static boolean calculateTradeLossAtInitialCost() {
		return true;
	}

	public static boolean shouldSubtractAverageCost() {
		return false;
	}

	public static boolean subtractUsingLogicalTrade() {
		return true;
	}

	public static boolean shouldEnforceOneBatchGroupPerVM() {
		if ("true".equals(System.getProperty("CurrencyTrading.allowMultipleBatchGroups"))) {
			return false;
		} else {
			return true;
		}
	}

}
