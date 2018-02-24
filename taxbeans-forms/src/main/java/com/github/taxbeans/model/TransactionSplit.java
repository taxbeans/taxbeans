package com.github.taxbeans.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Stack;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.convert.ConversionQuery;
import javax.money.convert.ConversionQueryBuilder;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.MonetaryConversions;

import com.github.taxbeans.currency.ExhangeRateUtils;

import java.time.LocalDate;
import java.time.Month;

public class TransactionSplit implements Comparable<TransactionSplit> {

	public static List<Transaction> adaptToMergedTransactionList(List<TransactionSplit> transactionSplits, Account debitAccount, Account creditAccount) {
		List<Transaction> transactionList = new ArrayList<Transaction>();
		for (TransactionSplit transactionSplit : transactionSplits) {
			transactionList.add(transactionSplit.adaptToMergedTransaction(debitAccount, creditAccount));
		}
		return transactionList;
	}

	private Account account;

	private BigDecimal amount;

	/**
	 * the outer transaction
	 */
	private Transaction transaction;
	
	private String description;
	
	private BigDecimal commodityUnits;
	
	private String commodityName;
	
	private CurrencyUnit currency = Monetary.getCurrency("NZD");
	
	public CurrencyUnit getCurrency() {
		return currency;
	}

	public void setCurrency(CurrencyUnit currency) {
		this.currency = currency;
	}

	private Stack<CurrencyTranslation> currencyTranslations = new Stack<CurrencyTranslation>();

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	private Transaction adaptToMergedTransaction(Account debitAccount, Account creditAccount) {
		Transaction tx = this.transaction.cloneThis();
		tx.setAmount(this.getAmount());
		tx.setDebitAccount(debitAccount);
		tx.setCreditAccount(creditAccount);
		return tx;
	}

	public int compareTo(TransactionSplit split) {
		LocalDate splitDate = split.getTransaction().getDate();
		return this.getTransaction().getDate().compareTo(splitDate);
	}

	public Account getAccount() {
		return account;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public final Transaction getTransaction() {
		return this.transaction;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public final void setTransaction(final Transaction argTransaction) {
		this.transaction = argTransaction;
	}
	

	public BigDecimal getCommodityUnits() {
		return commodityUnits;
	}

	public void setCommodityUnits(BigDecimal commodityUnits) {
		this.commodityUnits = commodityUnits;
	}

	public String getCommodityName() {
		return commodityName;
	}

	public void setCommodityName(String commodityName) {
		this.commodityName = commodityName;
	}
	
	public boolean isCommodity() {
		return commodityUnits != null || commodityName != null;
	}
	
	public void translate(LocalDate translationDate, CurrencyUnit from, CurrencyUnit to) {
		CurrencyTranslation translation = new CurrencyTranslation();
		translation.setOriginalCurrency(from);
		translation.setTranslatedCurrency(to);
		translation.setOriginalAmount(amount);
		amount = ExhangeRateUtils.exchange(translationDate, from, to, amount);
		currency = to;
		translation.setTranslatedAmount(amount);
		currencyTranslations.push(translation);
//		CurrencyUnit baseCurrency = from;
//		ConversionQuery conversionQuery = ConversionQueryBuilder.of()
//                .setProviderName("ECB")
//                .setBaseCurrency(from)
//                .setTermCurrency(to)
//                .set(LocalDate.class, translationDate)
//                .build();
//		CurrencyConversion currencyConversion = 
//				MonetaryConversions.getExchangeRateProvider().getCurrencyConversion(conversionQuery);
//		MonetaryAmount oneDollar = Monetary.getDefaultAmountFactory().setCurrency(baseCurrency).setNumber(this.amount).create();
//		oneDollar.with(currencyConversion);
//		amount = new BigDecimal(oneDollar.getNumber().toString());
	}

	@Override
	public String toString() {
		return "TransactionSplit [currencyUnit=" + currency + ", amount=" + amount + ", account=" + account
				+ ", date = " + transaction.getDate() +  ", description = " + transaction.getDescription() + "]";
	}
}
