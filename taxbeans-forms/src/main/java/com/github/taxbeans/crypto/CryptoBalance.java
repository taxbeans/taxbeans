package com.github.taxbeans.crypto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.money.Monetary;
import javax.money.MonetaryAmount;

import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CryptoBalance {
	
	Map<Crypto, CryptoAmount> balances = new TreeMap<Crypto, CryptoAmount>();
	
	private ZonedDateTime balanceDate;

	final static Logger logger = LoggerFactory.getLogger(CryptoBalance.class);
	
	public void add(CryptoAmount cryptoAmount) {
		CryptoAmount cryptoAmount2 = balances.get(cryptoAmount.getCrypto());
		Crypto crypto = cryptoAmount.getCrypto();
		if (cryptoAmount2 == null) {
			cryptoAmount2 = CryptoAmount.of(crypto, BigDecimal.ZERO);
		}
		cryptoAmount2.mutatingAdd(cryptoAmount);
		balances.put(crypto, cryptoAmount2);
	}
	
	public void subtract(CryptoAmount cryptoAmount) {
		CryptoAmount cryptoAmount2 = balances.get(cryptoAmount.getCrypto());
		Crypto crypto = cryptoAmount.getCrypto();
		if (cryptoAmount2 == null) {			
			cryptoAmount2 = CryptoAmount.of(crypto, BigDecimal.ZERO);
		}
		cryptoAmount2.mutatingSubtract(cryptoAmount);
		balances.put(crypto, cryptoAmount2);
	}
	
	public void print() {
		for (Entry<Crypto, CryptoAmount> entry : balances.entrySet()) {
			logger.info(String.valueOf(entry.getValue()));
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		MonetaryAmount total = Money.of(BigDecimal.ZERO, "NZD");
		for (Entry<Crypto, CryptoAmount> entry : balances.entrySet()) {
			CryptoAmount value = entry.getValue();
			sb.append(value);
			if (balanceDate != null) {
				ConversionUtils conversionUtils = ConversionUtils.of();
				MonetaryAmount usd = conversionUtils.convert(value, this.balanceDate);
				MonetaryAmount nzd = conversionUtils.convert(Monetary.getCurrency("NZD"), usd, this.balanceDate);
				sb.append(", NZD $: " + FormatUtils.format(nzd));
				total = total.add(nzd);
			}
			sb.append("\n");
		}
		sb.append("Total NZD: " + FormatUtils.format(total) + "\n");
		return sb.toString();
	}

	public static CryptoBalance of() {
		return new CryptoBalance();
	}

	public CryptoBalance copy() {
		CryptoBalance balance = CryptoBalance.of();
		for (Entry<Crypto, CryptoAmount> entry : this.balances.entrySet()) {
			balance.balances.put(entry.getKey(), entry.getValue().copy());
		}
		return balance;
	}

	public ZonedDateTime getBalanceDate() {
		return balanceDate;
	}

	public void setBalanceDate(ZonedDateTime balanceDate) {
		this.balanceDate = balanceDate;
	}
}
