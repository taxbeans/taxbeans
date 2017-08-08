package com.github.taxbeans.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import java.time.LocalDate;

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

	@Override
	public String toString() {
		return "TransactionSplit [amount=" + amount + ", account=" + account
				+ ", date = " + transaction.getDate() +  ", description = " + transaction.getDescription() + "]\n";
	}
}
