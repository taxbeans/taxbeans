package com.github.taxbeans.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

import com.github.taxbeans.currency.ExhangeRateUtils;

public class TransactionSplit implements Comparable<TransactionSplit> {

	public TransactionSplit() {
		super();
		uuid = UUID.randomUUID();
	}

	public UUID getUuid() {
		return uuid;
	}

	public static List<Transaction> adaptToMergedTransactionList(List<TransactionSplit> transactionSplits, Account debitAccount, Account creditAccount) {
		List<Transaction> transactionList = new ArrayList<Transaction>();
		for (TransactionSplit transactionSplit : transactionSplits) {
			transactionList.add(transactionSplit.adaptToMergedTransaction(debitAccount, creditAccount));
		}
		return transactionList;
	}
	
	private UUID uuid;

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
		if (debitAccount != null) {
			TransactionSplit debitSplit = new TransactionSplit();
			debitSplit.setTransaction(tx);
			debitSplit.setAmount(this.getAmount());
			debitSplit.setAccount(debitAccount);
			tx.getTransactionSplits().add(debitSplit);
		}
		if (creditAccount != null) {
			TransactionSplit creditSplit = new TransactionSplit();
			creditSplit.setTransaction(tx);
			creditSplit.setAmount(this.getAmount().negate());
			creditSplit.setAccount(creditAccount);
			tx.getTransactionSplits().add(creditSplit);
		}
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

	//automatically assigns the split to the account object as well
	public void setAccount(Account account) {
		if (account == null) {
			throw new IllegalArgumentException("Account may not be null");
		}
		this.account = account;
		//automatically assign the split to the account to
		this.account.assignSplit(this);
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
	}

	@Override
	public String toString() {
		return "TransactionSplit [currencyUnit=" + currency + ", amount=" + amount + ", account=" + account
				+ ", date = " + transaction.getDate() +  ", description = " + this.getDescription() + 
				", commodityName = " + this.getCommodityName() + ", commodityUnits = " + this.getCommodityUnits() + "]";
	}
}
