package com.github.taxbeans.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

public class Ledger {

	private Account assetsAccount = Account.account().withAccountType(AccountType.asset).withDebitIncreases(true).build();

	private Account liabilitiesAccount =  Account.account().withAccountType(AccountType.liability).build();

	private Account equityAccount = Account.account().withAccountType(AccountType.equity).build();

	private Account incomeAccount = Account.account().withAccountType(AccountType.income).build();

	private Account expensesAccount = Account.account().withAccountType(AccountType.expense).withDebitIncreases(true).build();

	private CurrencyUnit baseCurrency =  Monetary.getCurrency("NZD");

	private Map<String, Account> accountsByName = new HashMap<String, Account>();

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

	//private List<Account> accounts;

	private List<Transaction> transactions;

	private List<Journal> journals = new ArrayList<Journal>();

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

	public void setAccounts(List<Account> accounts) {
		//this.accounts = accounts;
		accounts.forEach(account -> this.accountsByName.put(account.getName(), account));
	}

	public void addJournal(Journal journal) {
		journals.add(journal);
		journal.setLedger(this);
	}

	public boolean isAutoNegativeSwitchesAccountSide() {
		return true;
	}

	public Account getAccountByName(String string) {
		return this.accountsByName.get(string);
	}

	public void addAccountIfRequired(Account value) {
		if (!accountsByName.containsKey(value.getName())) {
			this.accountsByName.put(value.getName(), value);
			//if the account doesn't have a guid yet then set it now
			if (value.getGuid() == null) {
				value.generateRandomGuid();
			}
		}
	}

	public Account getTradingGainAccount() {
		Account account = accountsByName.get("Trading Gain");
		if (account == null) {
			account = Account.account().withName("Trading Gain").withGuid(UUID.randomUUID().toString()).withParent(incomeAccount).build();
		}
		return account;
	}
	
	public Account getTradingLossAccount() {
		Account account = accountsByName.get("Trading Loss");
		if (account == null) {
			account = Account.account().withName("Trading Loss").withGuid(UUID.randomUUID().toString()).withParent(this.expensesAccount).build();
		}
		return account;
	}

	/**
	 * This will typically account for fees on sale, since fees on purchase are integrated into the historical cost
	 */
	public Account getTradeFeeAccount() {
		Account account = accountsByName.get("Trading Fees");
		if (account == null) {
			account = Account.account().withName("Trading Fees").withGuid(UUID.randomUUID().toString()).withParent(this.expensesAccount).build();
		}
		return account;
	}

	public List<Account> getAccounts() {
		return new ArrayList<Account>(this.accountsByName.values());
	}
}
