package com.github.taxbeans.crypto;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.money.CurrencyUnit;

public class CurrencyCode implements Comparable<CurrencyCode> {

	private static Map<String, CurrencyCode> map = new ConcurrentHashMap<String, CurrencyCode>();

	public static final CurrencyCode USD = CurrencyCode.of("USD");

	public static final CurrencyCode BTC = CurrencyCode.of("BTC");

	public static final CurrencyCode ETH = CurrencyCode.of("ETH");

	public static final CurrencyCode NZD = CurrencyCode.of("NZD");

	public static CurrencyCode of(String currencyString) {
		CurrencyCode currencyCode2 = map.get(currencyString);
		if (currencyCode2 == null) {
			CurrencyCode code = new CurrencyCode();
			code.currencyCode = currencyString;
			map.put(currencyString, code);
			return code;
		};
		return currencyCode2;
	}

	private String currencyCode;

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	String getCurrencyString() {
		return getCurrencyCode();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((currencyCode == null) ? 0 : currencyCode.hashCode());
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
		CurrencyCode other = (CurrencyCode) obj;
		if (currencyCode == null) {
			if (other.currencyCode != null)
				return false;
		} else if (!currencyCode.equals(other.currencyCode))
			return false;
		return true;
	}

	@Override
	public int compareTo(CurrencyCode o) {
		return this.currencyCode.compareTo(o.currencyCode);
	}

	public boolean isSameCurrency(CurrencyUnit baseCurrency) {
		return this.currencyCode.equals(baseCurrency.getCurrencyCode());
	}

	@Override
	public String toString() {
		return String.format(currencyCode);
	}

	public boolean isSameCurrency(CurrencyCode currencyCode) {
		return this.currencyCode.equals(currencyCode.currencyCode);
	}
}
