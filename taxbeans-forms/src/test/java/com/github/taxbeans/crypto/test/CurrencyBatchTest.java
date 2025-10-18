package com.github.taxbeans.crypto.test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.money.Monetary;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.taxbeans.crypto.AbstractCryptoEvent;
import com.github.taxbeans.crypto.CryptoEvent;
import com.github.taxbeans.crypto.CurrencyAmount;
import com.github.taxbeans.crypto.CurrencyBatchContext;
import com.github.taxbeans.crypto.CurrencyBatchGroup;
import com.github.taxbeans.crypto.CurrencyConversion;
import com.github.taxbeans.crypto.CurrencyConversionStrategy;
import com.github.taxbeans.crypto.CurrencyEventProcessorSession;
import com.github.taxbeans.crypto.CurrencyExchange;
import com.github.taxbeans.crypto.CurrencyFee;
import com.github.taxbeans.crypto.ExchangeCurrencyBalance;

public class CurrencyBatchTest {

	final static Logger logger = LoggerFactory.getLogger(CurrencyBatchTest.class);

	@Test
	public void testCurrencyBatch() throws ParseException {

		System.setProperty("CurrencyTrading.CostMethod", "FIFO");
		System.setProperty("CurrencyTrading.allowMultipleBatchGroups", "true");

		CurrencyBatchGroup batchGroup = CurrencyBatchGroup.of(Monetary.getCurrency("NZD"),
				CurrencyConversionStrategy.RBNZ);

		List<CryptoEvent> cryptoEvents = new ArrayList<CryptoEvent>();

		CurrencyConversion currencyConversion = CurrencyConversion.of(batchGroup,
				CurrencyAmount.of("100", "USD"),
				ZonedDateTime.of(2019,12,1,0,0,0,0, ZoneId.of("Pacific/Auckland")),
				CurrencyAmount.of("150", "NZD"),
				CurrencyFee.of(CurrencyAmount.of("1", "NZD")));
		currencyConversion.setCurrencyExchange(CurrencyExchange.CEX);
		System.err.println("currencyConversion1 = " + currencyConversion);
		cryptoEvents.add(currencyConversion);

		System.out.println(batchGroup.getAllBatches());

		ExchangeCurrencyBalance balance = new ExchangeCurrencyBalance();
		balance.setBalance(CurrencyAmount.of(new BigDecimal("100"), "USD"));
		balance.setCurrencyExchange(CurrencyExchange.CEX);
		cryptoEvents.add(balance);

		CurrencyConversion currencyConversion2 = CurrencyConversion.of(batchGroup,
				CurrencyAmount.of("78", "NZD"),
				ZonedDateTime.of(2019,12,1,0,0,0,0, ZoneId.of("Pacific/Auckland")),
				CurrencyAmount.of("55", "USD"),
				CurrencyFee.of(CurrencyAmount.of("1", "NZD")));
		currencyConversion2.setCurrencyExchange(CurrencyExchange.CEX);
		System.err.println("currencyConversion2 = " + currencyConversion2);
		cryptoEvents.add(currencyConversion2);

		System.out.println(batchGroup.getAllBatches());

		CurrencyEventProcessorSession session = new CurrencyEventProcessorSession(batchGroup);
		for (CryptoEvent event : cryptoEvents) {
			CurrencyBatchContext.setBatchGroup(batchGroup);
			session.process(event);
			System.out.println(batchGroup.getAllBatches());
		}
	}

}