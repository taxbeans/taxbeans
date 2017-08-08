package com.github.taxbeans.model;

import java.util.List;

public class BookOfAccounts {

	private List<Account> accounts;

	private List<Transaction> transactions;

	public List<Account> getAccounts() {
		return accounts;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

}
