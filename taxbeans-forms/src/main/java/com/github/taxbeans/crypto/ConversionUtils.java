package com.github.taxbeans.crypto;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;

import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.taxbeans.crypto.model.BchUsdEod;
import com.github.taxbeans.crypto.model.BsvUsdEod;
import com.github.taxbeans.crypto.model.BsvUsdEodCoinCodex;
import com.github.taxbeans.crypto.model.BsvUsdEodCryptoCompare;
import com.github.taxbeans.crypto.model.BtcUsdEod;
import com.github.taxbeans.crypto.model.BtgUsdEod;
import com.github.taxbeans.crypto.model.EthUsdEod;
import com.github.taxbeans.crypto.model.LtcUsdEod;
import com.github.taxbeans.crypto.model.NanoUsdCoinCodex;
import com.github.taxbeans.crypto.model.NanoUsdEod;
import com.github.taxbeans.crypto.model.XrpUsdEod;
import com.github.taxbeans.currency.RBNZHistoricalExchangeRatesReader;

public class ConversionUtils {
//	
//	private static volatile Map<LocalDate, CoinMarketCapBtcEod> btcEODData;
//	
//	private static Object btcEODDataLock = new Object();
	
	final static Logger logger = LoggerFactory.getLogger(ConversionUtils.class);

	public static ConversionUtils of(CurrencyConversionStrategy currencyConversionStrategy) {
		ConversionUtils conversionUtils = new ConversionUtils();
		conversionUtils.currencyConversionStrategy = currencyConversionStrategy;
		return conversionUtils;
	}
	

	public static ConversionUtils of() {
		ConversionUtils conversionUtils = new ConversionUtils();
		conversionUtils.currencyConversionStrategy = CurrencyConversionStrategy.RBNZ;
		return conversionUtils;
	}

	private CurrencyConversionStrategy currencyConversionStrategy = CurrencyConversionStrategy.RBNZ;
	
	public MonetaryAmount convert(CryptoAmount cryptoAmount, ZonedDateTime when) {
//		if (btcEODData == null) {
//			synchronized(btcEODDataLock) {
//				if (btcEODData == null) {
//					btcEODData = CoinMarketCapBtcEod.loadFromCSV();
//				}
//			}
//		}
//		//TODO convert zonedDateTime to UDC
//		 CoinMarketCapBtcEod coinMarketCapBtcEod = btcEODData.get(zonedDateTime.toLocalDate());
//		 coinMarketCapBtcEod.getClose(zonedDateTime)
		ZonedDateTime switchoverDate =
			    ZonedDateTime.of(2020, 02, 19, 12, 00, 00, 0, ZoneId.of("UTC"));		
		if (cryptoAmount.getCrypto() == Crypto.BTC) {
			return BtcUsdEod.getClose(when).multiply(cryptoAmount.getAmount());
		} else if (cryptoAmount.getCrypto() == Crypto.ETH) {
			return EthUsdEod.getClose(when).multiply(cryptoAmount.getAmount());
		} else if (cryptoAmount.getCrypto() == Crypto.BCH) {
			return BchUsdEod.getClose(when).multiply(cryptoAmount.getAmount());
		} else if (cryptoAmount.getCrypto() == Crypto.LTC) {
			return LtcUsdEod.getClose(when).multiply(cryptoAmount.getAmount());
		} else if (cryptoAmount.getCrypto() == Crypto.XRP) {
			return XrpUsdEod.getClose(when).multiply(cryptoAmount.getAmount());
		} else if (cryptoAmount.getCrypto() == Crypto.NANO) {
			if (when.isBefore(switchoverDate)) {
				//to preserve existing assertions from yahoo price source
				return NanoUsdEod.getClose(when).multiply(cryptoAmount.getAmount());				
			} else {
				return NanoUsdCoinCodex.getClose(when).multiply(cryptoAmount.getAmount());
			}
		} else if (cryptoAmount.getCrypto() == Crypto.BTG) {
            return BtgUsdEod.getClose(when).multiply(cryptoAmount.getAmount());
		} else if (cryptoAmount.getCrypto() == Crypto.BSV) {
			if (when.isBefore(switchoverDate)) {
				//to preserve existing assertions from yahoo price source
				return BsvUsdEodCryptoCompare.getClose(when).multiply(cryptoAmount.getAmount());			
			} else {
				return BsvUsdEodCoinCodex.getClose(when).multiply(cryptoAmount.getAmount());
			}
		} else if (cryptoAmount.getCrypto() == Crypto.OMG) {
			logger.warn("Please find the exchange rate for {} on {}", Crypto.OMG.name(), when);
			return BchUsdEod.getClose(when).multiply(cryptoAmount.getAmount());
		} else {
		     throw new AssertionError("Only BTC, BCH, LTC, XRP, BTG or ETH accepted, not " + cryptoAmount.getCrypto());
		}
	}

	public MonetaryAmount convert(CurrencyUnit baseCurrency, MonetaryAmount amount, ZonedDateTime zonedDateTime) {
		if (baseCurrency.equals(amount.getCurrency())) {
			return amount;
		}
		boolean rbnzCompatible = amount.getCurrency().equals(Monetary.getCurrency("USD")) ||
				amount.getCurrency().equals(Monetary.getCurrency("EUR"));
		if (baseCurrency.equals(Monetary.getCurrency("NZD"))
				&& rbnzCompatible) {
			switch (currencyConversionStrategy) {
			case SIMPLE:
				//if (!amount.getCurrency().equals(Monetary.getCurrency("USD")))
				//	throw new AssertionError("Expecting USD");
			//	else
					throw new AssertionError("Do not use simple strategy");
				//return simpleNZDUSDConvert(amount);
			case RBNZ:
				return convertOtherCurrencyTONZD(amount, zonedDateTime, amount.getCurrency());
			default:
				throw new AssertionError("Do not use simple strategy");
			}

		}
		throw new AssertionError(
				"conversion not possible from " + amount.getCurrency() + " to " + baseCurrency.getCurrencyCode());
	}
	
	public BigDecimal getRate(ZonedDateTime zonedDateTime, CurrencyUnit foreignCurrencyUnit) {
		return RBNZHistoricalExchangeRatesReader.getForeignToNZDRate(zonedDateTime, foreignCurrencyUnit);
	}

	public MonetaryAmount convertOtherCurrencyTONZD(MonetaryAmount initialCost, ZonedDateTime zonedDateTime,
			CurrencyUnit foreignCurrencyUnit) {
		BigDecimal rate = getRate(zonedDateTime, foreignCurrencyUnit);
		BigDecimal convertedAmount = new BigDecimal(initialCost.getNumber().toString()).multiply(rate);
		return Money.of(convertedAmount, Monetary.getCurrency("NZD"));
	}
//
//	private MonetaryAmount simpleNZDUSDConvert(MonetaryAmount initialCost) {
//		BigDecimal convertedAmount = new BigDecimal(initialCost.getNumber().toString()).multiply(new BigDecimal("1.5"));
//		return Money.of(convertedAmount, Monetary.getCurrency("NZD"));
//	}


	public MonetaryAmount convert(CurrencyAmount amount, ZonedDateTime zonedDateTime) {
		if (amount.isCrypto()) {
			//TODO perhaps change to return NZD
			return convert(amount.getCryptoAmount(), zonedDateTime);
		} else {
			return convert(Monetary.getCurrency("NZD"), amount.getMonetaryAmount(), zonedDateTime);
		}
	}


	public MonetaryAmount convert(CurrencyUnit targetCurrency, CurrencyAmount amountToConvert, ZonedDateTime zonedDateTime) {
		if (amountToConvert.isCrypto()) {
			MonetaryAmount monetaryAmount = convert(amountToConvert.getCryptoAmount(), zonedDateTime);
			return convert(Monetary.getCurrency("NZD"), monetaryAmount, zonedDateTime);
		} else {
			return convert(Monetary.getCurrency("NZD"), amountToConvert.getMonetaryAmount(), zonedDateTime);
		}
	}

	public BigDecimal getRate(ZonedDateTime zonedDateTime, String currencyCode) {
		try {
			Crypto crypto = Crypto.valueOf(currencyCode);
			MonetaryAmount monetaryAmount = convert(CryptoAmount.of(crypto, BigDecimal.ONE), zonedDateTime);
			return this.convert(Monetary.getCurrency("NZD"), monetaryAmount, zonedDateTime).getNumber().numberValue(BigDecimal.class);
		} catch (IllegalArgumentException e) {
			CurrencyUnit currency = Monetary.getCurrency(currencyCode);
			return convertOtherCurrencyTONZD(Money.of(BigDecimal.ONE, currency), zonedDateTime,
					currency).getNumber().numberValue(BigDecimal.class);
		}
		
	}
}
