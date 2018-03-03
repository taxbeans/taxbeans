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

public class AccountEntry implements Comparable<AccountEntry> {

	public AccountEntry(UUID uuid, Account account, BigDecimal amount, AccountSide accountSide, Transaction transaction,
			String description, BigDecimal commodityUnits, String commodityName, CurrencyUnit currency,
			Stack<CurrencyTranslation> currencyTranslations) {
		super();
		this.uuid = uuid;
		this.account = account;
		this.amount = amount;
		this.accountSide = accountSide;
		this.transaction = transaction;
		this.description = description;
		this.commodityUnits = commodityUnits;
		this.commodityName = commodityName;
		this.currency = currency;
		//no need to build the stack, do not include in builder when generated next
		//this.currencyTranslations = currencyTranslations;
	}

	public AccountEntry() {
		super();
		uuid = UUID.randomUUID();
	}

	public UUID getUuid() {
		return uuid;
	}

	public static List<Transaction> adaptToMergedTransactionList(List<AccountEntry> transactionSplits,
			Account debitAccount, Account creditAccount) {
		List<Transaction> transactionList = new ArrayList<Transaction>();
		for (AccountEntry transactionSplit : transactionSplits) {
			transactionList.add(transactionSplit.adaptToMergedTransaction(debitAccount, creditAccount));
		}
		return transactionList;
	}

	private UUID uuid;

	private Account account;

	private BigDecimal amount;

	// Whether this entry is a debit or credit
	private AccountSide accountSide;

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
			AccountEntry debitSplit = new AccountEntry();
			debitSplit.setTransaction(tx);
			debitSplit.setAmount(this.getAmount());
			debitSplit.setAccount(debitAccount);
			tx.getTransactionSplits().add(debitSplit);
		}
		if (creditAccount != null) {
			AccountEntry creditSplit = new AccountEntry();
			creditSplit.setTransaction(tx);
			creditSplit.setAmount(this.getAmount().negate());
			creditSplit.setAccount(creditAccount);
			tx.getTransactionSplits().add(creditSplit);
		}
		return tx;
	}

	public int compareTo(AccountEntry split) {
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

	public AccountSide getAccountSide() {
		return accountSide;
	}

	public void setAccountSide(AccountSide accountSide) {
		this.accountSide = accountSide;
	}

	//+ve increases balance and -ve decreases balance so debit/credit accordingly
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public AccountEntry withAmount(BigDecimal amount) {
		this.amount = amount;
		return this;
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
		return "AccountEntry [" + amount + " " + currency + " " + accountSide + ", account=" + account + ", date = "
				+ (transaction == null ? null : transaction.getDate()) + ", description = " + this.getDescription()
				+ ", commodityName = " + this.getCommodityName() + ", commodityUnits = " + this.getCommodityUnits()
				+ "]";
	}

	public AccountEntry withCommodityUnits(BigDecimal amount2) {
		this.setCommodityUnits(amount2);
		return this;
	}

	public AccountEntry withCommodityName(String currencyCode) {
		this.setCommodityName(currencyCode);
		return this;
	}

	public AccountEntry withDescription(String string) {
		this.setDescription(string);
		return this;
	}

	public AccountEntry withAccount(Account buyOrderAccount) {
		this.setAccount(buyOrderAccount);
		return this;
	}

	public static class AccountEntryBuilder {
		private UUID uuid;
		private Account account;
		private BigDecimal amount;
		private AccountSide accountSide;
		private Transaction transaction;
		private String description;
		private BigDecimal commodityUnits;
		private String commodityName;
		private CurrencyUnit currency;
		private Stack<CurrencyTranslation> currencyTranslations;

		public AccountEntryBuilder withUuid(UUID uuid) {
			this.uuid = uuid;
			return this;
		}

		public AccountEntryBuilder withAccount(Account account) {
			this.account = account;
			return this;
		}

		public AccountEntryBuilder withAmount(BigDecimal amount) {
			this.amount = amount;
			return this;
		}

		public AccountEntryBuilder withAccountSide(AccountSide accountSide) {
			this.accountSide = accountSide;
			return this;
		}

		public AccountEntryBuilder withTransaction(Transaction transaction) {
			this.transaction = transaction;
			return this;
		}

		public AccountEntryBuilder withDescription(String description) {
			this.description = description;
			return this;
		}

		public AccountEntryBuilder withCommodityUnits(BigDecimal commodityUnits) {
			this.commodityUnits = commodityUnits;
			return this;
		}

		public AccountEntryBuilder withCommodityName(String commodityName) {
			this.commodityName = commodityName;
			return this;
		}

		public AccountEntryBuilder withCurrency(CurrencyUnit currency) {
			this.currency = currency;
			return this;
		}

		public AccountEntryBuilder withCurrencyTranslations(Stack<CurrencyTranslation> currencyTranslations) {
			this.currencyTranslations = currencyTranslations;
			return this;
		}

		public AccountEntry build() {
			return new AccountEntry(uuid, account, amount, accountSide, transaction, description, commodityUnits,
					commodityName, currency, currencyTranslations);
		}
	}

	public static AccountEntryBuilder accountEntry() {
		return new AccountEntryBuilder();
	}

}
