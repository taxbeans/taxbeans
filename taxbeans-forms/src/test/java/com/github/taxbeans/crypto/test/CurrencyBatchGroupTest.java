package com.github.taxbeans.crypto.test;

import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.money.Monetary;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.taxbeans.crypto.CurrencyAmount;
import com.github.taxbeans.crypto.CurrencyBatch;
import com.github.taxbeans.crypto.CurrencyBatchGroup;
import com.github.taxbeans.crypto.CurrencyCode;
import com.github.taxbeans.crypto.CurrencyConversionStrategy;

public class CurrencyBatchGroupTest {

	final static Logger logger = LoggerFactory.getLogger(CurrencyBatchGroupTest.class);

	@Test
	public void testCurrencyBatchGroup() throws ParseException {

		System.setProperty("CurrencyTrading.CostMethod", "FIFO");
		System.setProperty("CurrencyTrading.allowMultipleBatchGroups", "true");

		CurrencyBatchGroup batchGroup = CurrencyBatchGroup.of(Monetary.getCurrency("NZD"),
				CurrencyConversionStrategy.RBNZ);

		ZonedDateTime when = ZonedDateTime.of(2019, 12, 1, 0, 0, 0, 0, ZoneId.of("Pacific/Auckland"));
		batchGroup.add(
				CurrencyBatch.of(batchGroup, CurrencyAmount.of("100", "USD"), when, CurrencyAmount.of("50", "NZD")));
		batchGroup.getAllBatches().use(CurrencyAmount.of("10", "USD"));
		CurrencyAmount sum = batchGroup.getAllBatches().sum(CurrencyCode.of("USD"));
		Assert.assertEquals(CurrencyAmount.of("90", "USD"), sum);
		batchGroup.getAllBatches().use(CurrencyAmount.of("10", "USD"));
		sum = batchGroup.getAllBatches().sum(CurrencyCode.of("USD"));
		Assert.assertEquals(CurrencyAmount.of("80", "USD"), sum);
		batchGroup.getAllBatches().use(CurrencyAmount.of("10", "USD"));
		sum = batchGroup.getAllBatches().sum(CurrencyCode.of("USD"));
		Assert.assertEquals(CurrencyAmount.of("70", "USD"), sum);
		batchGroup.getAllBatches().use(CurrencyAmount.of("10", "USD"));
		sum = batchGroup.getAllBatches().sum(CurrencyCode.of("USD"));
		Assert.assertEquals(CurrencyAmount.of("60", "USD"), sum);
		batchGroup.getAllBatches().use(CurrencyAmount.of("10", "USD"));
		sum = batchGroup.getAllBatches().sum(CurrencyCode.of("USD"));
		Assert.assertEquals(CurrencyAmount.of("50", "USD"), sum);

		batchGroup.getAllBatches().use(CurrencyAmount.of("10", "USD"));
		Assert.assertEquals(CurrencyAmount.of("40", "USD"), batchGroup.getAllBatches().sum(CurrencyCode.of("USD")));


		batchGroup.getAllBatches().use(CurrencyAmount.of("10", "USD"));
		Assert.assertEquals(CurrencyAmount.of("30", "USD"), batchGroup.getAllBatches().sum(CurrencyCode.of("USD")));

		batchGroup.getAllBatches().use(CurrencyAmount.of("10", "USD"));
		Assert.assertEquals(CurrencyAmount.of("20", "USD"), batchGroup.getAllBatches().sum(CurrencyCode.of("USD")));

		batchGroup.getAllBatches().use(CurrencyAmount.of("10", "USD"));
		Assert.assertEquals(CurrencyAmount.of("10", "USD"), batchGroup.getAllBatches().sum(CurrencyCode.of("USD")));

		batchGroup.getAllBatches().use(CurrencyAmount.of("10", "USD"));
		Assert.assertEquals(CurrencyAmount.of("0", "USD"), batchGroup.getAllBatches().sum(CurrencyCode.of("USD")));
//
//		batchGroup.getAllBatches().use(CurrencyAmount.of("10", "USD"));
//		Assert.assertEquals(CurrencyAmount.of("-10", "USD"), batchGroup.getAllBatches().sum(CurrencyCode.of("USD")));
	}

}