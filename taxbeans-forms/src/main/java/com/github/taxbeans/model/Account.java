package com.github.taxbeans.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.money.Monetary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.taxbeans.forms.utils.TaxRegion;
import com.github.taxbeans.model.assertions.BalanceAssertion;

public class Account {

	public static class AccountBuilder {
		private AccountClassification accountClassification;
		private String accountNumber;
		private AccountType accountType;
		private List<BalanceAssertion> balanceAssertions;
		private String commodityName;
		private BigDecimal commodityUnits;
		private boolean debitIncreases;
		private String description;
		private String guid;
		private String name;
		private Account parent;
		private boolean placeholder;
		private List<AccountEntry> splits;

		public Account build() {
			return new Account(parent, accountClassification, accountType, balanceAssertions, splits, guid, placeholder,
					accountNumber, name, description, debitIncreases, commodityUnits, commodityName);
		}

		public AccountBuilder withAccountClassification(AccountClassification accountClassification) {
			this.accountClassification = accountClassification;
			return this;
		}

		public AccountBuilder withAccountNumber(String accountNumber) {
			this.accountNumber = accountNumber;
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

		public AccountBuilder withCommodityName(String commodityName) {
			this.commodityName = commodityName;
			return this;
		}

		public AccountBuilder withCommodityUnits(BigDecimal commodityUnits) {
			this.commodityUnits = commodityUnits;
			return this;
		}

		public AccountBuilder withDebitIncreases(boolean debitIncreases) {
			this.debitIncreases = debitIncreases;
			return this;
		}

		public AccountBuilder withDescription(String description) {
			this.description = description;
			return this;
		}

		public AccountBuilder withGuid(String guid) {
			this.guid = guid;
			return this;
		}

		public AccountBuilder withName(String name) {
			this.name = name;
			return this;
		}

		public AccountBuilder withParent(Account parent) {
			this.parent = parent;
			if (this.accountType == null) {
				this.accountType = parent.getAccountType();
				this.debitIncreases = parent.debitIncreases;
			}
			return this;
		}

		public AccountBuilder withPlaceholder(boolean placeholder) {
			this.placeholder = placeholder;
			return this;
		}

		public AccountBuilder withSplits(List<AccountEntry> splits) {
			this.splits = splits;
			return this;
		}
	}

	final static Logger logger = LoggerFactory.getLogger(Account.class);

	public static AccountBuilder account() {
		return new AccountBuilder();
	}

	public static Account createFromGUID(String text) {
		return new Account(text);
	}

	public static Account createFromGUID(UUID randomUUID) {
		return Account.createFromGUID(randomUUID.toString());
	}

	/** Expense, income, asset or liability */
	private AccountClassification accountClassification;

	private List<AccountEntry> accountEntries = new ArrayList<AccountEntry>();

	private AccountType accountType;

	private List<BalanceAssertion> balanceAssertions = new ArrayList<BalanceAssertion>();

	/**
	 * Code for this account.
	 * 
	 * Every account should have a code
	 */
	private String code;

	// Optional field to hold commodityName
	private String commodityName;

	private boolean debitIncreases;

	private String description;

	private String guid;

	private String lastCurrencyCode;

	private String name;

	private Account parent;

	private boolean placeholder;

	public Account() {
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
		this.accountEntries = splits;
		if (this.accountEntries == null) {
			this.accountEntries = new ArrayList<>();
		}
		this.guid = guid;
		this.placeholder = placeholder;
		this.code = accountNumber;
		this.name = name;
		this.description = description;
		this.commodityName = commodityName;
	}

	public Account(AccountType accountType) {
		this.accountType = accountType;
	}

	private Account(String guid) {
		this.guid = guid;
	}

	public void addEntry(AccountEntry transactionSplit) {
		if (accountEntries == null) {
			accountEntries = new ArrayList<AccountEntry>();
		}
		this.accountEntries.add(transactionSplit);
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
		logger.debug("transaction splits size = " + accountEntries.size());
		Collections.sort(accountEntries);
		BigDecimal balance = BigDecimal.ZERO;
		//int transactionNum = -1;
		for (AccountEntry split : accountEntries) {
			//transactionNum++;
			logger.debug("amount = " + split.getAmount());
			balance = balance.add(split.getAmount());
			logger.debug("balance = " + balance);
			LocalDate transactionDate = split.getTransaction().getDate().toLocalDate();
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Account other = (Account) obj;
		if (guid == null) {
			if (other.guid != null)
				return false;
		} else if (!guid.equals(other.guid))
			return false;
		return true;
	}

	public void generateRandomGuid() {
		this.guid = UUID.randomUUID().toString();
	}

	public AccountClassification getAccountClassification() {
		return accountClassification;
	}

	public final AccountType getAccountType() {
		return this.accountType;
	}

	public BigDecimal getBalanceAsAt(ZonedDateTime time) {
		return getBalanceAsAtOrBefore(time, true);
	}

	public BigDecimal getBalanceAsAtOrBefore(ZonedDateTime time, boolean asAt) {
		if (accountEntries == null || accountEntries.size() == 0) {
			return BigDecimal.ZERO;
		}
		Collections.sort(accountEntries);
		BigDecimal balance = BigDecimal.ZERO;

		//TODO convert all transaction dates to times or have a common object
		//if transaction has a date but not a time then the time can be as at 5:00pm on that day
		//but where possible the time of each transaction should be estimated
		String firstCurrencyCode = null;
		for (AccountEntry entry : accountEntries) {
			
			if (entry.getCurrency() == null) {
				//temporary workaround until currency handling is fixed:
				entry.setCurrency(Monetary.getCurrency("NZD"));
			}
			if (firstCurrencyCode == null) {
				firstCurrencyCode = entry.getCurrency().getCurrencyCode();
			}
			String currency = entry.getCurrency().getCurrencyCode();
			if (!currency.equals(firstCurrencyCode)) {
				throw new AssertionError(String.format("Currency: %s inconsistent with first currency: %s", currency, firstCurrencyCode));
			}
			lastCurrencyCode = currency;
			boolean condition = asAt ? entry.getTransaction().getDate().compareTo(time) > 0 : 
				entry.getTransaction().getDate().compareTo(time) >= 0;
				
			if (condition)
				return balance;
			logger.debug("amount = " + entry.getAmount());
			boolean increase = (entry.getAccountSide() == AccountSide.DEBIT && this.isDebitIncreases()) ||
					(entry.getAccountSide() == AccountSide.CREDIT && !this.isDebitIncreases());
			balance = balance.add(increase ? entry.getAmount().abs() : entry.getAmount().abs().negate());
			logger.debug("balance = " + balance);
		}
		return balance;
	}

	public List<BalanceAssertion> getBalanceAssertions() {
		return balanceAssertions;
	}

	public BigDecimal getBalanceBefore(ZonedDateTime time) {
		return getBalanceAsAtOrBefore(time, false);
	}

	public BigDecimal getClosingBalanceForTaxYear(int year) {
		return getOpeningBalanceForTaxYear(year + 1);
	}

	public BigDecimal getClosingCommodityBalanceForTaxYear(int year) {
		return getOpeningCommodityBalanceForTaxYear(year + 1);
	}

	public String getCode() {
		return code;
	}
	
	public BigDecimal getCommodityBalanceAsAt(ZonedDateTime time) {
		return getCommodityBalanceAsAtOrBefore(time, true);
	}

	public BigDecimal getCommodityBalanceAsAtOrBefore(ZonedDateTime time, boolean asAt) {
		if (accountEntries == null || accountEntries.size() == 0) {
			return BigDecimal.ZERO;
		}
		Collections.sort(accountEntries);
		BigDecimal balance = BigDecimal.ZERO;

		for (AccountEntry entry : accountEntries) {
			boolean condition = asAt ? entry.getTransaction().getDate().compareTo(time) > 0 : 
				entry.getTransaction().getDate().compareTo(time) >= 0;
			if (condition)
				return balance;
			logger.debug("amount = " + entry.getCommodityUnits());
			
			boolean increase = (entry.getAccountSide() == AccountSide.DEBIT && this.isDebitIncreases()) ||
					(entry.getAccountSide() == AccountSide.CREDIT && !this.isDebitIncreases());
			balance = balance.add(increase ? entry.getCommodityUnits().abs() : entry.getCommodityUnits().abs().negate());
			logger.debug("balance = " + balance);
		}
		return balance;	
	}

	public BigDecimal getCommodityBalanceBefore(ZonedDateTime time) {
		return getCommodityBalanceAsAtOrBefore(time, false);
	}

	public String getCommodityName() {
		return commodityName;
	}

	/**
	 * Historical cost as at certain time.
	 * 
	 * This finds the historical cost at a certain time per unit commodity
	 * by dividing the balance in base currency by the commodity balance.
	 * 
	 * @param time The time to find the historical cost
	 * @return The historical cost per unit
	 */
	public BigDecimal getCostPerUnitAsAt(ZonedDateTime time) {
		return getBalanceAsAt(time).divide(getCommodityBalanceAsAt(time),  MathContext.DECIMAL128);
	}
	
	public BigDecimal getCostPerUnitBefore(ZonedDateTime time) {
		return getBalanceBefore(time).divide(getCommodityBalanceBefore(time), MathContext.DECIMAL128);
	}
	
	public String getDescription() {
		return description;
	}
	

	public String getGuid() {
		return guid;
	}
	
	public String getLastCurrencyCode() {
		return lastCurrencyCode;
	}
	
	public final String getName() {
		return this.name;
	}

	public BigDecimal getOpeningBalanceForTaxYear(int year) {
		if (accountEntries == null) {
			logger.warn("Account entries are null for account: " + this);
			return BigDecimal.ZERO;
		}
		Collections.sort(accountEntries);
		BigDecimal balance = BigDecimal.ZERO;

		for (AccountEntry split : accountEntries) {
			if (split.getTransaction().getDate().compareTo(
					TaxRegion.getDefault().getStartOfTaxYear(year)) >= 0)
				return balance;
			logger.debug("amount = " + split.getAmount());
			balance = balance.add(split.getAmount());
			logger.debug("balance = " + balance);
		}
		return balance;
	}
	
	public BigDecimal getOpeningCommodityBalanceForTaxYear(int year) {
		Collections.sort(accountEntries);
		BigDecimal balance = BigDecimal.ZERO;

		for (AccountEntry split : accountEntries) {
			if (split.getTransaction().getDate().compareTo(
					TaxRegion.getDefault().getStartOfTaxYear(year)) >= 0)
				return balance;
			logger.debug("amount = " + split.getCommodityUnits());
			balance = balance.add(split.getCommodityUnits());
			logger.debug("balance = " + balance);
		}
		return balance;
	}
	
	public Account getParent() {
		return parent;
	}
	
	public List<AccountEntry> getSplits() {
		return accountEntries;
	}
	
	public BigDecimal getTotalForTaxYear(int year) {
		Collections.sort(accountEntries);
		BigDecimal balance = BigDecimal.ZERO;
		for (AccountEntry split : accountEntries) {
			if (split.getTransaction().getDate().compareTo(
					TaxRegion.getDefault().getStartOfTaxYear(year+1)) >= 0)
				return balance;
			if (!split.getTransaction().isInNewZealandTaxYear(year))
				continue;
			logger.debug("amount = " + split.getAmount());
			balance = balance.add(split.getAmount());
			logger.debug("balance = " + balance);

		}
		return balance;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((guid == null) ? 0 : guid.hashCode());
		return result;
	}

	public boolean isCommodity() {
		return commodityName != null;
	}

	public boolean isDebitIncreases() {
		return debitIncreases;
	}

	public boolean isPlaceholder() {
		return placeholder;
	}

	public void printTransactions() {
		for (AccountEntry split : accountEntries) {
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

	public void setCode(String accountNumber) {
		this.code = accountNumber;
	}

	public void setCommodityName(String commodityName) {
		this.commodityName = commodityName;
	}

	public void setDebitIncreases(boolean debitIncreases) {
		this.debitIncreases = debitIncreases;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public final Account setName(final String argName) {
		this.name = argName;
		return this;
	}

	public void setParent(Account parent) {
		this.parent = parent;
		this.debitIncreases = parent.debitIncreases;
		if (this.accountType == null) {
			this.accountType = parent.getAccountType();
			this.debitIncreases = parent.debitIncreases;
		}
	}

	public void setPlaceholder(boolean placeholder) {
		this.placeholder = placeholder;
	}

	public void setSplits(List<AccountEntry> list) {
		if (accountEntries != null && accountEntries.size() > 0) {
			throw new IllegalStateException("Existing splits may not be overridden");
		}
		this.accountEntries = list;
	}

	@Override
	public String toString() {
		return "Account [name=" + name + ", guid=" + guid + ", accountType="
				+ accountType + "]";
	}


}
