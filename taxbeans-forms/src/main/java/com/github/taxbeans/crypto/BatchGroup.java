package com.github.taxbeans.crypto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;

import org.javamoney.moneta.Money;

public class BatchGroup {
	
	private volatile MonetaryAmount zeroAmount;
	
	public static BatchGroup of() {
		return new BatchGroup();
	}
	
	public static BatchGroup of(CurrencyUnit baseCurrency) {
		BatchGroup batchGroup = new BatchGroup();
		batchGroup.baseCurrency = baseCurrency;
		batchGroup.currencyConversionStrategy = CurrencyConversionStrategy.RBNZ;
		return batchGroup;
	}

	public static BatchGroup of(CurrencyUnit currency, CurrencyConversionStrategy rbnz) {
		BatchGroup group = BatchGroup.of(currency);
		group.currencyConversionStrategy = rbnz;
		return group;
	}

	private CurrencyUnit baseCurrency;

	private List<Batch> batches = new ArrayList<Batch>();
	
	private CurrencyConversionStrategy currencyConversionStrategy;

	public void add(Batch batch) {
		batches.add(batch);
		batch.setBatchGroup(this);
	}

	public BatchSet getAllBatches() {
		return BatchSet.of(batches);
	}

	public CurrencyUnit getBaseCurrency() {
		return baseCurrency;
	}

	public CurrencyConversionStrategy getCurrencyConversionStrategy() {
		return currencyConversionStrategy;
	}

	public Trade trade(CryptoAmount cryptoAmount, 
			Money money, 
			Fee fee,
			ZonedDateTime tradeDate) {
		Trade trade = Trade.of(this,
				cryptoAmount,
				money,
				tradeDate,
				fee);
		return trade;
	}
	
	public CryptoConversion convert(CryptoAmount cryptoAmount, 
			CryptoAmount toAmount, 
			Fee fee,
			ZonedDateTime tradeDate) {
		CryptoConversion conversion = CryptoConversion.of(this,
				cryptoAmount,
				tradeDate,
				toAmount,
				fee);
		return conversion;
	}

	public MonetaryAmount getZeroAmount() {
		//use lazy loading to miminize amount of GC garbage created for 
		//high volume trading applications:
		synchronized (this) {
			if (zeroAmount == null) {
				synchronized (this) {
					if (zeroAmount == null) {
						zeroAmount = Money.of(BigDecimal.ZERO, baseCurrency);
					}
				}
			}
		}
		return zeroAmount;
	}

}
