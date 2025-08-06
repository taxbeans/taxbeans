package com.github.taxbeans.crypto;

public class CurrencyFee {
	
	private CurrencyAmount amount;


	public static CurrencyFee of(CurrencyAmount amount) {
		CurrencyFee fee = new CurrencyFee();
		fee.amount = amount;
		return fee;
	}

	public CurrencyAmount getAmount() {
		return amount;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
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
		CurrencyFee other = (CurrencyFee) obj;
		if (amount == null) {
			if (other.amount != null)
				return false;
		} else if (!amount.equals(other.amount))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CurrencyFee [amount=" + amount + "]";
	}

	public static CurrencyFee ofZero() {
		return CurrencyFee.of(CurrencyAmount.getZeroNZD());
	}
}
