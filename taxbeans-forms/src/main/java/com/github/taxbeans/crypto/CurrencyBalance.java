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

public class CurrencyBalance {
	
	Map<CurrencyCode, CurrencyAmount> balances = new TreeMap<CurrencyCode, CurrencyAmount>();
	
	Map<CurrencyExchange, Map<CurrencyCode, CurrencyAmount>> exchangeBalances = 
			new TreeMap<CurrencyExchange, Map<CurrencyCode, CurrencyAmount>>();
	
	private ZonedDateTime balanceDate;
	
	private boolean valueAtTransactionDatetime = true;
	
	Logger logger = LoggerFactory.getLogger(CurrencyBalance.class);

	private MonetaryAmount baseCurrencyBalanceAtTransaction;

	private MonetaryAmount baseCurrencyFiatBalanceAtTransaction;

	private boolean summaryAmounts;
	
	public void add(CurrencyAmount currencyAmount, CurrencyExchange exchange) {
		CurrencyAmount currencyAmount2 = balances.get(currencyAmount.getCurrencyCode());
		CurrencyCode currencyCode = currencyAmount.getCurrencyCode();
		if (currencyAmount2 == null) {
			currencyAmount2 = CurrencyAmount.of(BigDecimal.ZERO, currencyCode);
		}
		currencyAmount2 = currencyAmount2.add(currencyAmount);
		balances.put(currencyCode, currencyAmount2);
		
		//by exchange:
		Map<CurrencyCode, CurrencyAmount> exchangeBalance = exchangeBalances.get(exchange);
		Map<CurrencyCode, CurrencyAmount> map;
		if (exchangeBalance == null) {
			map = new TreeMap<CurrencyCode, CurrencyAmount>();
			exchangeBalances.put(exchange, map);
		} else {
			map = exchangeBalances.get(exchange);
		}
		CurrencyAmount currencyAmount3 = map.get(currencyAmount.getCurrencyCode());
		if (currencyAmount3 == null) {
			currencyAmount3 = CurrencyAmount.of(BigDecimal.ZERO, currencyCode);
		}
		currencyAmount3 = currencyAmount3.add(currencyAmount);
		map.put(currencyCode, currencyAmount3);
	}
	
	public void subtract(CurrencyAmount currencyAmount, CurrencyExchange exchange) {
		CurrencyAmount currencyAmount2 = balances.get(currencyAmount.getCurrencyCode());
		CurrencyCode currencyCode = currencyAmount.getCurrencyCode();
		if (currencyAmount2 == null) {
			currencyAmount2 = CurrencyAmount.of(BigDecimal.ZERO, currencyCode);
		}
		currencyAmount2 = currencyAmount2.subtract(currencyAmount);
		balances.put(currencyCode, currencyAmount2);
		
		//by exchange:
		Map<CurrencyCode, CurrencyAmount> exchangeBalance = exchangeBalances.get(exchange);
		Map<CurrencyCode, CurrencyAmount> map;
		if (exchangeBalance == null) {
			map = new TreeMap<CurrencyCode, CurrencyAmount>();
			exchangeBalances.put(exchange, map);
		} else {
			map = exchangeBalances.get(exchange);
		}
		CurrencyAmount currencyAmount3 = map.get(currencyAmount.getCurrencyCode());
		if (currencyAmount3 == null) {
			currencyAmount3 = CurrencyAmount.of(BigDecimal.ZERO, currencyCode);
		}
		currencyAmount3 = currencyAmount3.subtract(currencyAmount);
		map.put(currencyCode, currencyAmount3);
	}
	
	public void print() {
		for (Entry<CurrencyCode, CurrencyAmount> entry : balances.entrySet()) {
			logger.info(String.valueOf(entry.getValue()));
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Summary: ");
		summaryAmounts = true;
		sb.append(toStringInner(balances));
		summaryAmounts =  false;
		for (Entry<CurrencyExchange, Map<CurrencyCode, CurrencyAmount>> entry : exchangeBalances.entrySet()) {
			sb.append("\n\n");
			sb.append(entry.getKey());
			sb.append("\n");
			sb.append(toStringInner(entry.getValue()));
		}
		return sb.toString();
	}
	
	public String toStringInner(Map<CurrencyCode, CurrencyAmount> map) {
		StringBuilder sb = new StringBuilder();
		MonetaryAmount baseCurrencyBalanceAtTransaction = Money.of(BigDecimal.ZERO, "NZD");
		MonetaryAmount baseCurrencyFiatBalanceAtTransaction = Money.of(BigDecimal.ZERO, "NZD");
		MonetaryAmount valuationTotal = Money.of(BigDecimal.ZERO, "NZD");
		for (Entry<CurrencyCode, CurrencyAmount> entry : map.entrySet()) {
			CurrencyAmount value = entry.getValue();
			sb.append(value);
			if (isValueAtTransactionDatetime()) {				
				MonetaryAmount nzd = value.isBaseCurrencyAmountSet() ? value.getBaseCurrencyAmount() : 
					CurrencyAmount.getZeroNZD().getMonetaryAmount();				
				sb.append(", NZD $: " + FormatUtils.format(nzd));
				baseCurrencyBalanceAtTransaction = baseCurrencyBalanceAtTransaction.add(nzd);
				if (!value.isCrypto()) {
					baseCurrencyFiatBalanceAtTransaction = baseCurrencyFiatBalanceAtTransaction.add(nzd);
				}
				if (balanceDate != null) {
					ConversionUtils conversionUtils = ConversionUtils.of();
					MonetaryAmount usdAtBalanceDate = conversionUtils.convert(value, this.balanceDate);
					MonetaryAmount nzdAtBalanceDate = conversionUtils.convert(Monetary.getCurrency("NZD"), usdAtBalanceDate, balanceDate);
					sb.append(", NZD at balance date $: " + FormatUtils.format(nzdAtBalanceDate));
					valuationTotal = valuationTotal.add(nzdAtBalanceDate);
				}
			} else if (balanceDate != null && !isValueAtTransactionDatetime()) {
				ConversionUtils conversionUtils = ConversionUtils.of();
				MonetaryAmount usdAtBalanceDate = conversionUtils.convert(value, this.balanceDate);
				MonetaryAmount nzdAtBalanceDate = conversionUtils.convert(Monetary.getCurrency("NZD"), usdAtBalanceDate, balanceDate);
				sb.append(", NZD valuation at balance date $: " + FormatUtils.format(nzdAtBalanceDate));
				valuationTotal = valuationTotal.add(nzdAtBalanceDate);
			} else {
				throw new AssertionError("Either valuation date time or balance date need to be set");
			}
			sb.append("\n");
		}
		sb.append("Total NZD (at transaction datetime): " + FormatUtils.format(baseCurrencyBalanceAtTransaction) + "\n");
		sb.append("Total NZD (ISO only) (at transaction datetime): " + FormatUtils.format(baseCurrencyFiatBalanceAtTransaction) + "\n");
		sb.append("Total NZD (at valuation): " + FormatUtils.format(valuationTotal) + "\n");
		if (summaryAmounts) {
			this.baseCurrencyBalanceAtTransaction = baseCurrencyBalanceAtTransaction;
			this.baseCurrencyFiatBalanceAtTransaction = baseCurrencyFiatBalanceAtTransaction;
		}
		return sb.toString();
	}

	public static CurrencyBalance of() {
		return new CurrencyBalance();
	}

	public CurrencyBalance copy() {
		CurrencyBalance balance = CurrencyBalance.of();
		for (Entry<CurrencyCode, CurrencyAmount> entry : this.balances.entrySet()) {
			balance.balances.put(entry.getKey(), entry.getValue().copy());
		}
		//Map<CurrencyCode, CurrencyAmount>> entry = new TreeMap<CurrencyCode, CurrencyAmount>
		for (Entry<CurrencyExchange, Map<CurrencyCode, CurrencyAmount>> entry2 : this.exchangeBalances.entrySet()) {
			TreeMap<CurrencyCode, CurrencyAmount> treeMap = new TreeMap<CurrencyCode, CurrencyAmount>();
			balance.exchangeBalances.put(entry2.getKey(), treeMap);
			Map<CurrencyCode, CurrencyAmount> value = entry2.getValue();
			for (Entry<CurrencyCode, CurrencyAmount> entry : value.entrySet()) {
				treeMap.put(entry.getKey(), entry.getValue().copy());
			}			
		}
		return balance;
	}

	public ZonedDateTime getBalanceDate() {
		return balanceDate;
	}

	public void setBalanceDate(ZonedDateTime balanceDate) {
		this.balanceDate = balanceDate;
	}

	public boolean isValueAtTransactionDatetime() {
		return valueAtTransactionDatetime;
	}

	public void setValueAtTransactionDatetime(boolean valueAtTransactionDatetime) {
		this.valueAtTransactionDatetime = valueAtTransactionDatetime;
	}

	public CurrencyBalance valueAtTransactionDatetime() {
		this.setValueAtTransactionDatetime(true);
		return this;
	}

	public CurrencyAmount getBalance(CurrencyExchange exchange, CurrencyCode currencyCode) {
		Map<CurrencyCode, CurrencyAmount> map = this.exchangeBalances.get(exchange);
		if (map == null) {
			return null;
		}
		return map.get(currencyCode);
	}

	MonetaryAmount getBaseCurrencyBalanceAtTransaction() {
		return this.baseCurrencyFiatBalanceAtTransaction;
	}

	MonetaryAmount getBaseCurrencyFiatBalanceAtTransaction() {
		return this.baseCurrencyFiatBalanceAtTransaction;
	}
}
