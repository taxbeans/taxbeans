package com.github.taxbeans.crypto;

import java.time.ZonedDateTime;

public class CurrencyTransfer {

	CurrencyExchange from;
	
	CurrencyExchange to;
	
	CurrencyAmount amount;
	
	ZonedDateTime when;
	
	private CurrencyTransfer(com.github.taxbeans.crypto.CurrencyExchange from,
			com.github.taxbeans.crypto.CurrencyExchange to, CurrencyAmount amount, ZonedDateTime when) {
		super();
		this.from = from;
		this.to = to;
		this.amount = amount;
		this.when = when;
	}

	public static CurrencyTransfer of(CurrencyExchange from, CurrencyExchange to,
			CurrencyAmount amount, ZonedDateTime when) {
		return new CurrencyTransfer(from, to, amount, when);
	}

	@Override
	public String toString() {
		return String.format("CurrencyTransfer [from=%s, to=%s, amount=%s, when=%s]", from, to, amount, when);
	}

}
