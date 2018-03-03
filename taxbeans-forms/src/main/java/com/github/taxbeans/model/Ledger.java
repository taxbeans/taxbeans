package com.github.taxbeans.model;

import java.util.ArrayList;
import java.util.List;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

public class Ledger {

	private Account assetsAccount = Account.account().withAccountType(AccountType.asset).withDebitIncreases(true).build();
	
	private Account liabilitiesAccount =  Account.account().withAccountType(AccountType.liability).build();
	
	private Account equityAccount = Account.account().withAccountType(AccountType.equity).build();
	
	private Account incomeAccount = Account.account().withAccountType(AccountType.income).build();
	
	private Account expensesAccount = Account.account().withAccountType(AccountType.expense).withDebitIncreases(true).build();
	
	private CurrencyUnit baseCurrency =  Monetary.getCurrency("NZD");
	
	//whether to auto convert transactions into the base currency
	private boolean autoTranslate = true;
	
	public CurrencyUnit getBaseCurrency() {
		return baseCurrency;
	}

	public void setBaseCurrency(CurrencyUnit baseCurrency) {
		this.baseCurrency = baseCurrency;
	}

	public boolean isAutoTranslate() {
		return autoTranslate;
	}

	public void setAutoTranslate(boolean autoTranslate) {
		this.autoTranslate = autoTranslate;
	}

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

	private List<Journal> journals = new ArrayList<Journal>();

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

	public void addJournal(Journal journal) {
		journals.add(journal);
		journal.setLedger(this);
	}

	public boolean isAutoNegativeSwitchesAccountSide() {
		return true;
	}

}
