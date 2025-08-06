package com.github.taxbeans.crypto;

import java.math.BigDecimal;

public class CryptoAmount {
	
	public static CryptoAmount of(Crypto btc, BigDecimal bigDecimal) {
		CryptoAmount cryptoAmount = new CryptoAmount();
		cryptoAmount.crypto = btc;
		cryptoAmount.amount = bigDecimal;
		return cryptoAmount;
	}
	
	private Crypto crypto;

	private BigDecimal amount;

	public CryptoAmount nonMutatingAdd(CryptoAmount cryptoAmount) {
		if (cryptoAmount.getCrypto() != this.getCrypto()) {
			throw new AssertionError("Crypto mismatch, " + cryptoAmount.getAmount()
				+ " and this is " + this.getCrypto());
		}
		return CryptoAmount.of(crypto, this.amount.add(cryptoAmount.amount));
	}
	
	/*
	 * We can expect less GC overhead and better performance with this variant
	 */
	public void mutatingAdd(CryptoAmount cryptoAmount) {
		if (cryptoAmount.getCrypto() != this.getCrypto()) {
			throw new AssertionError("Crypto mismatch, " + cryptoAmount.getAmount()
				+ " and this is " + this.getCrypto());
		}
		this.amount = this.amount.add(cryptoAmount.amount);
	}
	
	public CryptoAmount add(CryptoAmount cryptoAmount) {
		return nonMutatingAdd(cryptoAmount);
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public Crypto getCrypto() {
		return crypto;
	}
	
	public CryptoAmount subtract(CryptoAmount cryptoAmount) {
		return nonMutatingSubtract(cryptoAmount);
	}

	public CryptoAmount nonMutatingSubtract(CryptoAmount cryptoAmount) {
		if (cryptoAmount.getCrypto() != this.getCrypto()) {
			throw new AssertionError("Crypto mismatch, " + cryptoAmount.getAmount()
				+ " and this is " + this.getCrypto());
		}
		return CryptoAmount.of(crypto, this.amount.subtract(cryptoAmount.amount));
	}
	
	public void mutatingSubtract(CryptoAmount cryptoAmount) {
		if (cryptoAmount.getCrypto() != this.getCrypto()) {
			throw new AssertionError("Crypto mismatch, " + cryptoAmount.getAmount()
				+ " and this is " + this.getCrypto());
		}
		this.amount = this.amount.subtract(cryptoAmount.amount);
	}

	@Override
	public String toString() {
		return crypto + " " + amount;
	}

	public int signum() {
		return this.getAmount().signum();
	}

	public boolean isLessThan(CryptoAmount cryptoAmount) {
		return this.amount.compareTo(cryptoAmount.amount) < 0;
	}

	public boolean isLessThanOrEqualTo(CryptoAmount cryptoAmount) {
		return this.amount.compareTo(cryptoAmount.amount) <= 0;
	}

	public CryptoAmount copy() {
		return CryptoAmount.of(this.getCrypto(), this.amount);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + ((crypto == null) ? 0 : crypto.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CryptoAmount other = (CryptoAmount) obj;
		if (amount == null) {
			if (other.amount != null)
				return false;
		} else if (!amount.equals(other.amount))
			return false;
		if (crypto != other.crypto)
			return false;
		return true;
	}
}
