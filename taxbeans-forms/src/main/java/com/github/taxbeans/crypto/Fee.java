package com.github.taxbeans.crypto;

import javax.money.MonetaryAmount;

public class Fee {

	private MonetaryAmount amount;

	private CryptoAmount cryptoAmount;

	private boolean isCrypto;

	public static Fee of(MonetaryAmount amount) {
		Fee fee = new Fee();
		fee.amount = amount;
		return fee;
	}

	public static Fee of(CryptoAmount amount) {
		Fee fee = new Fee();
		fee.cryptoAmount = amount;
		fee.isCrypto = true;
		return fee;
	}

	public MonetaryAmount getAmount() {
		if (isCrypto) {
			throw new AssertionError("the fee is crypto based");
		}
		return amount;
	}

	public boolean isCrypto() {
		return isCrypto;
	}

	public CryptoAmount getCryptoAmount() {
		return cryptoAmount;
	}

	public static Fee of(CurrencyAmount feeAmount) {
		if (feeAmount.isCrypto()) {
			return Fee.of(feeAmount.getCryptoAmount());
		}
		return Fee.of(feeAmount.getMonetaryAmount());
	}
}
