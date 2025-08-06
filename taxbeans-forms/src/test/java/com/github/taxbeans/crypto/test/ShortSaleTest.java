package com.github.taxbeans.crypto.test;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.money.Monetary;
import javax.money.MonetaryAmount;

import org.javamoney.moneta.Money;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.taxbeans.crypto.CurrencyAmount;
import com.github.taxbeans.crypto.CurrencyBatchGroup;
import com.github.taxbeans.crypto.CurrencyConversion;
import com.github.taxbeans.crypto.CurrencyConversionStrategy;
import com.github.taxbeans.crypto.CurrencyEventProcessorSession;
import com.github.taxbeans.crypto.CurrencyExchange;
import com.github.taxbeans.crypto.CurrencyFee;
import com.github.taxbeans.crypto.CurrencyTradeLoss;


public class ShortSaleTest {

	final static Logger logger = LoggerFactory.getLogger(ShortSaleTest.class);

	@Test
	public void test() {

		System.setProperty("CurrencyTrading.allowMultipleBatchGroups", "true");
		System.setProperty("CurrencyTrading.CostMethod", "FIFO");

		CurrencyBatchGroup batchGroup = CurrencyBatchGroup.of(Monetary.getCurrency("NZD"),
				CurrencyConversionStrategy.RBNZ);

		CurrencyEventProcessorSession session = new CurrencyEventProcessorSession(batchGroup);

		CurrencyConversion currencyConversion = CurrencyConversion.of(batchGroup,
				CurrencyAmount.of("1", "ETH"),
				ZonedDateTime.of(2019,12,1,0,0,0,0, ZoneId.of("Pacific/Auckland")),
				CurrencyAmount.of("150", "NZD"),
				CurrencyFee.of(CurrencyAmount.of("1", "NZD")));
		currencyConversion.setCurrencyExchange(CurrencyExchange.CEX);
		session.process(currencyConversion);

		MonetaryAmount totalCostBasisAfter = session.getTotalCostBasis();
		Assert.assertEquals(Money.of(new BigDecimal("150"), Monetary.getCurrency("NZD")), totalCostBasisAfter);

		MonetaryAmount profitOfThisTrade = session.getProfitOfLast();
		Assert.assertEquals(Money.of(new BigDecimal("-1"), Monetary.getCurrency("NZD")), profitOfThisTrade);

		MonetaryAmount totalProfit = session.getTotalProfit();
		Assert.assertEquals(Money.of(new BigDecimal("-1"), Monetary.getCurrency("NZD")), totalProfit);

		MonetaryAmount totalAssumedDeposits = session.getTotalAssumedBaseCurrencyDeposits();
		Assert.assertEquals(Money.of(new BigDecimal("150"), Monetary.getCurrency("NZD")), totalAssumedDeposits);

		CurrencyConversion currencyConversion2 = CurrencyConversion.of(batchGroup,
				CurrencyAmount.of("1", "BTC"),
				ZonedDateTime.of(2019,12,1,0,0,0,0, ZoneId.of("Pacific/Auckland")),
				CurrencyAmount.of("1", "ETH"),
				CurrencyFee.of(CurrencyAmount.of("1", "NZD")));
		currencyConversion2.setCurrencyExchange(CurrencyExchange.CEX);
		session.process(currencyConversion2);

		totalCostBasisAfter = session.getTotalCostBasis();
		Assert.assertEquals("11,792.54 NZD", CurrencyAmount.format(totalCostBasisAfter));

		profitOfThisTrade = session.getProfitOfLast();
		Assert.assertEquals("11,641.54 NZD", CurrencyAmount.format(profitOfThisTrade));

		totalProfit = session.getTotalProfit();
		Assert.assertEquals("11,640.54 NZD", CurrencyAmount.format(totalProfit));

		totalAssumedDeposits = session.getTotalAssumedBaseCurrencyDeposits();
		Assert.assertEquals(Money.of(new BigDecimal("150"), Monetary.getCurrency("NZD")), totalAssumedDeposits);

		/*
		 * Looks like at first the loss is valued at the current market value of 10 ETH
		 */
		CurrencyTradeLoss currencyTradeLoss3 = CurrencyTradeLoss.of(batchGroup,
				CurrencyAmount.of("10", "ETH"),
				ZonedDateTime.of(2019,12,1,0,0,0,0, ZoneId.of("Pacific/Auckland")),
				CurrencyFee.of(CurrencyAmount.of("100", "NZD")));
		currencyTradeLoss3.setCurrencyExchange(CurrencyExchange.CEX);
		session.process(currencyTradeLoss3);

		totalCostBasisAfter = session.getTotalCostBasis();
		Assert.assertEquals("9,416.16 NZD", CurrencyAmount.format(totalCostBasisAfter));

		profitOfThisTrade = session.getProfitOfLast();
		Assert.assertEquals("-2,376.38 NZD", CurrencyAmount.format(profitOfThisTrade));

		totalProfit = session.getTotalProfit();
		Assert.assertEquals("9,264.16 NZD", CurrencyAmount.format(totalProfit));

		/**
		 * If 10 ETH is bought back here then the loss needs to be revalued as part of the short sale
		 * repayment, according to the rate at the time of the buy back
		 */

		totalAssumedDeposits = session.getTotalAssumedBaseCurrencyDeposits();
		Assert.assertEquals(Money.of(new BigDecimal("150"), Monetary.getCurrency("NZD")), totalAssumedDeposits);
		/*
org.junit.ComparisonFailure: expected:<[11,402.90] NZD> but was:<[9,264.16] NZD>
	at org.junit.Assert.assertEquals(Assert.java:117)
	at org.junit.Assert.assertEquals(Assert.java:146)
	at com.github.taxbeans.crypto.test.ShortSaleTest.test(ShortSaleTest.java:95)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:566)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:59)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:56)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
	at org.junit.runners.ParentRunner$3.evaluate(ParentRunner.java:306)
	at org.junit.runners.BlockJUnit4ClassRunner$1.evaluate(BlockJUnit4ClassRunner.java:100)
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:366)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:103)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:63)
	at org.junit.runners.ParentRunner$4.run(ParentRunner.java:331)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:79)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:329)
	at org.junit.runners.ParentRunner.access$100(ParentRunner.java:66)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:293)
	at org.junit.runners.ParentRunner$3.evaluate(ParentRunner.java:306)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:413)
	at org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:89)
	at org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:41)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:542)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:770)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:464)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:210)



		*/


	}
}

