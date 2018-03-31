package com.github.taxbeans.model;

import java.util.ArrayList;
import java.util.List;

public class Journal {
	
	private List<Transaction> transactions = new ArrayList<>();
	
	private Ledger ledger;

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

	public Ledger getLedger() {
		return ledger;
	}

	public void setLedger(Ledger ledger) {
		this.ledger = ledger;
	}
	
	private void translateIfRequired(AccountEntry entry) {
		Ledger ledger2 = this.getLedger();
		if (ledger2 == null) {
			throw new IllegalStateException("Ledger may not be null");
		}
		//assume null indicates the entry has the base currency
		if (entry.getCurrency() != null && !entry.getCurrency().equals(ledger2.getBaseCurrency())) {
			entry.translate(entry.getTransaction().getDate(), entry.getCurrency(), 
					this.getLedger().getBaseCurrency());
		}
	}
	
	private void switchSideIfRequired(AccountEntry entry) {
		Ledger ledger2 = this.getLedger();
		if (ledger2 == null) {
			throw new IllegalStateException("Ledger may not be null");
		}
		//assume null indicates the entry has the base currency
		if (entry.getAccountSide() == AccountSide.BALANCE_EFFECT) {
			if (entry.getAmount().signum() < 0) {
				entry.setAccountSide(entry.getAccount().isDebitIncreases() ? AccountSide.CREDIT : AccountSide.DEBIT);
				entry.setAmount(entry.getAmount().abs());
			} else {
				entry.setAccountSide(entry.getAccount().isDebitIncreases() ? AccountSide.DEBIT : AccountSide.CREDIT);
			}
		} else {
			if (entry.getAmount().signum() < 0) {
				throw new IllegalArgumentException(entry.getAmount() + " " + entry.getAccountSide());
			}
		}
	}

	public void addTransaction(Transaction transaction) {
		this.transactions.add(transaction);
		transaction.getAccountEntries().forEach(entry -> entry.setTransaction(transaction));
		transaction.getAccountEntries().forEach(entry -> addAccountToLedger(entry));
		if (this.getLedger().isAutoTranslate()) {
			transaction.getAccountEntries().forEach(entry -> translateIfRequired(entry));
		}
		if (this.getLedger().isAutoNegativeSwitchesAccountSide()) {
			transaction.getAccountEntries().forEach(entry -> switchSideIfRequired(entry));
		}
	}

	private void addAccountToLedger(AccountEntry entry) {
		this.getLedger().addAccountIfRequired(entry.getAccount());
	}
}
