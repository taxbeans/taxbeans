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
	}

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
	
	private String name;
	
	private String description;
	
	private boolean debitIncreases;
	
	public boolean isDebitIncreases() {
		return debitIncreases;
	}

	public void setDebitIncreases(boolean debitIncreases) {
		this.debitIncreases = debitIncreases;
	}
	
	public Account withDebitIncreases(boolean debitIncreases) {
		this.debitIncreases = debitIncreases;
		return this;
	}

	// Optional field to hold commodityUnits
	private BigDecimal commodityUnits;
	
	// Optional field to hold commodityName
	private String commodityName;

	public Account() {}

	public Account(AccountType accountType) {
		this.accountType = accountType;
	}

	private Account(String guid) {
		this.guid = guid;
	}

	public void checkBalanceAssertions() {
		Map<LocalDate, List<BalanceAssertion>> balanceAssertionMap  = new HashMap<LocalDate, List<BalanceAssertion>>();
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
				throw new AssertionError("Balance of " + balance + " is incorrect and should be one of: " + sb.toString());

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
		return getOpeningBalanceForTaxYear(year+1);
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
			if (split.getTransaction().getDate().compareTo(LocalDate.of(year-1, 3, 31)) > 0)
				return balance;
			logger.debug("amount = " + split.getAmount());
			balance = balance.add(split.getAmount());
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

	@Override
	public String toString() {
		return "Account [accountClassification=" + accountClassification
				+ ", guid=" + guid + ", accountType=" + accountType + ", name="
				+ name + "]";
	}

	public Account withName(String string) {
		this.setName(string);
		return this;
	}

	public void assignSplit(AccountEntry transactionSplit) {
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

	public Account withParent(Account assets) {
		setParent(assets);
		return this;
	}
}

