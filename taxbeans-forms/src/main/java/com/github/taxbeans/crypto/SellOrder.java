package com.github.taxbeans.crypto;

public class SellOrder extends Order {

	@Override
	public String toString() {
		return "SellOrder [getAmount()=" + getAmount() + ", getReference()=" + getReference() + ", getCommodityName()="
				+ getCommodityName() + "]";
	}

}
