package com.github.taxbeans.model;

import org.javamoney.moneta.Money;

public class Inventory {
	
	private DigitalCurrency digitalCurrency;
	
	/*
	 * historical cost in ledger base currency
	 */
	private Money historicalCost;
	
	private ConversionType conversionType;

	public ConversionType getConversionType() {
		return conversionType;
	}

	public void setConversionType(ConversionType conversionType) {
		this.conversionType = conversionType;
	}

	public DigitalCurrency getDigitalCurrency() {
		return digitalCurrency;
	}

	public Money getHistoricalCost() {
		return historicalCost;
	}

	public void setDigitalCurrency(DigitalCurrency digitalCurrency) {
		this.digitalCurrency = digitalCurrency;
	}

	public void setHistoricalCost(Money historicalCost) {
		this.historicalCost = historicalCost;
	}

}
