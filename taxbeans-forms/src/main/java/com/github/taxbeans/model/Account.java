package com.github.taxbeans.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.taxbeans.model.assertions.BalanceAssertion;

public class Account {

	final static Logger logger = LoggerFactory.getLogger(Account.class);

	public static Account createFromGUID(UUID randomUUID) {
		return Account.createFromGUID(randomUUID.toString());
	}

	public static Account createFromGUID(String text) {
		return new Account(text);
	}

	private Account parent;

	public Account getParent() {
		return parent;
	}

	public void setParent(Account parent) {
		this.parent = parent;
		this.debitIncreases = parent.debitIncreases;
		if (this.accountType == null) {
			this.accountType = parent.getAccountType();
			this.debitIncreases = parent.debitIncreases;
		}
	}

	/** Expense, income, asset or liability */
	private AccountClassification accountClassification;

	private AccountType accountType;

	private List<BalanceAssertion> balanceAssertions = new ArrayList<BalanceAssertion>();

	private List<AccountEntry> splits = new ArrayList<AccountEntry>();

	private String guid;

	private boolean placeholder;

	public boolean isPlaceholder() {
		return placeholder;
	}

	public void setPlaceholder(boolean placeholder) {
		this.placeholder = placeholder;
	}

	private String accountNumber;

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	private String name;

	private String description;

	private boolean debitIncreases;

	public boolean isDebitIncreases() {
		return debitIncreases;
	}

	public void setDebitIncreases(boolean debitIncreases) {
		this.debitIncreases = debitIncreases;
	}

	// Optional field to hold commodityName
	private String commodityName;

	public Account() {
	}

	public Account(AccountType accountType) {
		this.accountType = accountType;
	}

	private Account(String guid) {
		this.guid = guid;
	}

	public Account(Account parent, AccountClassification accountClassification, AccountType accountType,
			List<BalanceAssertion> balanceAssertions, List<AccountEntry> splits, String guid, boolean placeholder,
			String accountNumber, String name, String description, boolean debitIncreases, BigDecimal commodityUnits,
			String commodityName) {
		super();
		this.parent = parent;
		this.accountClassification = accountClassification;
		this.accountType = accountType;
		if (this.accountType == null && parent != null) {
			this.accountType = parent.getAccountType();
			if (this.accountType == AccountType.asset) {
				this.setDebitIncreases(true);
			} else {
				this.setDebitIncreases(parent.debitIncreases);
			}
		} else {
			this.debitIncreases = debitIncreases;
		}
		this.balanceAssertions = balanceAssertions;
		this.splits = splits;
		this.guid = guid;
		this.placeholder = placeholder;
		this.accountNumber = accountNumber;
		this.name = name;
		this.description = description;
		this.commodityName = commodityName;
	}

	public void checkBalanceAssertions() {
		Map<LocalDate, List<BalanceAssertion>> balanceAssertionMap = new HashMap<LocalDate, List<BalanceAssertion>>();
		for (BalanceAssertion balanceAssertion : balanceAssertions) {
			logger.debug("### adding to map: " + balanceAssertion.getDate() + "->" + balanceAssertion.getBalance());
			List<BalanceAssertion> balanceAssertions = balanceAssertionMap.get(balanceAssertion.getDate());
			if (balanceAssertions == null)
				balanceAssertions = new ArrayList<BalanceAssertion>();
			balanceAssertions.add(balanceAssertion);
			balanceAssertionMap.put(balanceAssertion.getDate(), balanceAssertions);
		}
		logger.debug("transaction splits size = " + splits.size());
		Collections.sort(splits);
		BigDecimal balance = BigDecimal.ZERO;
		int transactionNum = -1;
		for (AccountEntry split : splits) {
			transactionNum++;
			logger.debug("amount = " + split.getAmount());
			balance = balance.add(split.getAmount());
			logger.debug("balance = " + balance);
			LocalDate transactionDate = split.getTransaction().getDate();
			List<BalanceAssertion> balanceAssertions = balanceAssertionMap.get(transactionDate);
			logger.debug("Transaction date = " + transactionDate);
			if (balanceAssertions == null)
				continue;
			boolean found = false;
			for (BalanceAssertion balanceAssertion : balanceAssertions) {
				BigDecimal object = balanceAssertion.getBalance();
				if (object != null && object.compareTo(balance) == 0) {
					found = true;
				}
			}
			//			try {
			//				Transaction nextTransaction = transactions.get(transactionNum+1);
			//				if (nextTransaction != null && transaction.getDate().equals(nextTransaction.getDate()))
			//					continue;
			//			} catch (IndexOutOfBoundsException e) {
			//				//last transaction anyway, so continue
			//			}
			if (!found) {
				System.out.flush();
				try {
					Thread.sleep(1000L);
				} catch (InterruptedException e) {
					logger.warn("Interrupted", e);
				}
				StringBuilder sb = new StringBuilder();
				for (BalanceAssertion balanceAssertion : balanceAssertions)
					sb.append(balanceAssertion.getBalance() + ", ");
				throw new AssertionError(
						"Balance of " + balance + " is incorrect and should be one of: " + sb.toString());

			}
		}

	}

	public AccountClassification getAccountClassification() {
		return accountClassification;
	}

	public final AccountType getAccountType() {
		return this.accountType;
	}

	public List<BalanceAssertion> getBalanceAssertions() {
		return balanceAssertions;
	}

	public BigDecimal getClosingBalanceForTaxYear(int year) {
		return getOpeningBalanceForTaxYear(year + 1);
	}
	
	public BigDecimal getClosingCommodityBalanceForTaxYear(int year) {
		return getOpeningCommodityBalanceForTaxYear(year + 1);
	}

	public List<AccountEntry> getSplits() {
		return splits;
	}

	public String getGuid() {
		return guid;
	}

	public final String getName() {
		return this.name;
	}

	public BigDecimal getOpeningBalanceForTaxYear(int year) {
		Collections.sort(splits);
		BigDecimal balance = BigDecimal.ZERO;

		for (AccountEntry split : splits) {
			if (split.getTransaction().getDate().compareTo(LocalDate.of(year - 1, 3, 31)) > 0)
				return balance;
			logger.debug("amount = " + split.getAmount());
			balance = balance.add(split.getAmount());
			logger.debug("balance = " + balance);
		}
		return balance;
	}
	
	public BigDecimal getOpeningCommodityBalanceForTaxYear(int year) {
		Collections.sort(splits);
		BigDecimal balance = BigDecimal.ZERO;

		for (AccountEntry split : splits) {
			if (split.getTransaction().getDate().compareTo(LocalDate.of(year - 1, 3, 31)) > 0)
				return balance;
			logger.debug("amount = " + split.getCommodityUnits());
			balance = balance.add(split.getCommodityUnits());
			logger.debug("balance = " + balance);
		}
		return balance;
	}


	public BigDecimal getTotalForTaxYear(int year) {
		Collections.sort(splits);
		BigDecimal balance = BigDecimal.ZERO;
		for (AccountEntry split : splits) {
			if (split.getTransaction().getDate().compareTo(LocalDate.of(year, 3, 31)) > 0)
				return balance;
			if (!split.getTransaction().isInTaxYear(year))
				continue;
			logger.debug("amount = " + split.getAmount());
			balance = balance.add(split.getAmount());
			logger.debug("balance = " + balance);

		}
		return balance;
	}

	public void printTransactions() {
		for (AccountEntry split : splits) {
			logger.debug("tx: " + split);
		}
	}

	public void setAccountClassification(AccountClassification accountClassification) {
		this.accountClassification = accountClassification;
	}

	public final void setAccountType(final AccountType argAccountType) {
		this.accountType = argAccountType;
	}

	public void setBalanceAssertions(List<BalanceAssertion> balanceAssertions) {
		this.balanceAssertions = balanceAssertions;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public final Account setName(final String argName) {
		this.name = argName;
		return this;
	}

	public String getCommodityName() {
		return commodityName;
	}

	public void setCommodityName(String commodityName) {
		this.commodityName = commodityName;
	}

	public boolean isCommodity() {
		return commodityName != null;
	}

	@Override
	public String toString() {
		return "Account [accountClassification=" + accountClassification + ", guid=" + guid + ", accountType="
				+ accountType + ", name=" + name + "]";
	}

	public void addEntry(AccountEntry transactionSplit) {
		if (splits == null) {
			splits = new ArrayList<AccountEntry>();
		}
		this.splits.add(transactionSplit);
	}

	public void setSplits(List<AccountEntry> list) {
		if (splits != null && splits.size() > 0) {
			throw new IllegalStateException("Existing splits may not be overridden");
		}
		this.splits = list;
	}

	public static class AccountBuilder {
		private Account parent;
		private AccountClassification accountClassification;
		private AccountType accountType;
		private List<BalanceAssertion> balanceAssertions;
		private List<AccountEntry> splits;
		private String guid;
		private boolean placeholder;
		private String accountNumber;
		private String name;
		private String description;
		private boolean debitIncreases;
		private BigDecimal commodityUnits;
		private String commodityName;

		public AccountBuilder withParent(Account parent) {
			this.parent = parent;
			if (this.accountType == null) {
				this.accountType = parent.getAccountType();
				this.debitIncreases = parent.debitIncreases;
			}
			return this;
		}

		public AccountBuilder withAccountClassification(AccountClassification accountClassification) {
			this.accountClassification = accountClassification;
			return this;
		}

		public AccountBuilder withAccountType(AccountType accountType) {
			this.accountType = accountType;
			return this;
		}

		public AccountBuilder withBalanceAssertions(List<BalanceAssertion> balanceAssertions) {
			this.balanceAssertions = balanceAssertions;
			return this;
		}

		public AccountBuilder withSplits(List<AccountEntry> splits) {
			this.splits = splits;
			return this;
		}

		public AccountBuilder withGuid(String guid) {
			this.guid = guid;
			return this;
		}

		public AccountBuilder withPlaceholder(boolean placeholder) {
			this.placeholder = placeholder;
			return this;
		}

		public AccountBuilder withAccountNumber(String accountNumber) {
			this.accountNumber = accountNumber;
			return this;
		}

		public AccountBuilder withName(String name) {
			this.name = name;
			return this;
		}

		public AccountBuilder withDescription(String description) {
			this.description = description;
			return this;
		}

		public AccountBuilder withDebitIncreases(boolean debitIncreases) {
			this.debitIncreases = debitIncreases;
			return this;
		}

		public AccountBuilder withCommodityUnits(BigDecimal commodityUnits) {
			this.commodityUnits = commodityUnits;
			return this;
		}

		public AccountBuilder withCommodityName(String commodityName) {
			this.commodityName = commodityName;
			return this;
		}

		public Account build() {
			return new Account(parent, accountClassification, accountType, balanceAssertions, splits, guid, placeholder,
					accountNumber, name, description, debitIncreases, commodityUnits, commodityName);
		}
	}

	public static AccountBuilder account() {
		return new AccountBuilder();
	}

}
