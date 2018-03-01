package com.github.taxbeans.model;

import java.util.List;

public class Ledger {

	private Account assetsAccount = new Account().withDebitIncreases(true);
	
	private Account liabilitiesAccount = new Account();
	
	private Account equityAccount = new Account();
	
	private Account incomeAccount = new Account();
	
	private Account expensesAccount = new Account().withDebitIncreases(true);
	
	public Account getAssetsAccount() {
		return assetsAccount;
	}

	public void setAssetsAccount(Account assetsAccount) {
		this.assetsAccount = assetsAccount;
	}

	public Account getLiabilitiesAccount() {
		return liabilitiesAccount;
	}

	public void setLiabilitiesAccount(Account liabilitiesAccount) {
		this.liabilitiesAccount = liabilitiesAccount;
	}

	public Account getEquityAccount() {
		return equityAccount;
	}

	public void setEquityAccount(Account equityAccount) {
		this.equityAccount = equityAccount;
	}

	public Account getIncomeAccount() {
		return incomeAccount;
	}

	public void setIncomeAccount(Account incomeAccount) {
		this.incomeAccount = incomeAccount;
	}

	public Account getExpensesAccount() {
		return expensesAccount;
	}

	public void setExpensesAccount(Account expensesAccount) {
		this.expensesAccount = expensesAccount;
	}

	private List<Account> accounts;

	private List<Transaction> transactions;

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

	public List<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}

}
