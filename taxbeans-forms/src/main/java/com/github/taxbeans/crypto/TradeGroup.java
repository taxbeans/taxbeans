package com.github.taxbeans.crypto;

import java.util.Set;
import java.util.TreeSet;

import javax.money.CurrencyUnit;

public class TradeGroup {
	
	public static TradeGroup of(CurrencyUnit baseCurrency) {
		TradeGroup tradeGroup = new TradeGroup();
		tradeGroup.baseCurrency = baseCurrency;
		return tradeGroup;
	}
	
	private CurrencyUnit baseCurrency;
	
	private Set<Trade> trades = new TreeSet<Trade>();
	
	public void add(Trade trade) {
		trades.add(trade);
		trade.setTradeGroup(this);
	}

}
