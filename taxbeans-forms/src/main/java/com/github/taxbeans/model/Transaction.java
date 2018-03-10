package com.github.taxbeans.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * In this model a transaction may have a date or time precision.
 * There will be a conversion algorithm and either one or the other may be set or both
 */
public class Transaction implements Comparable<Transaction>, Cloneable {

	final static Logger logger = LoggerFactory.getLogger(Transaction.class);

	private LocalDate date;

	private LocalDate dateEntered;
	
	private LocalDateTime dateTime;
	
	private LocalDateTime dateTimeEntered;

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}

	public LocalDateTime getDateTimeEntered() {
		return dateTimeEntered;
	}

	public void setDateTimeEntered(LocalDateTime dateTimeEntered) {
		this.dateTimeEntered = dateTimeEntered;
	}

	private String description;

	private String num;

	//TODO add a validate method to ensure total debits = total credits and rename TransactionSplit to Entry
	private List<AccountEntry> transactionSplits = new ArrayList<AccountEntry>();

//	public BigDecimal calculateIncome(List<Transaction> transactions, int year) {
//		BigDecimal sum = BigDecimal.ZERO;
//		for (Transaction transaction : transactions) {
//			if (!transaction.getCreditAccount().getAccountType().equals(AccountType.income))
//				continue;
//			if (transaction.getDate().compareTo(LocalDate.of(year-1, 3, 31)) > 0 &&
//					transaction.getDate().compareTo(LocalDate.of(year, 4, 1)) < 0) {
//				//logger.info("Date of transaction = " + transaction.getDate());
//				sum = sum.add(transaction.getAmount());
//				//		logger.info("Sum for " + year + "=" + sum);
//			}
//		}
//		logger.info("Income Sum for " + year + "=" + sum);
//		return sum;
//	}

	public Object clone() {
		try {
			return super.clone();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public Transaction cloneThis() {
		try {
			return (Transaction) super.clone();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public int compareTo(Transaction arg0) {
		return this.getDate().compareTo(arg0.getDate());
	}

	public final LocalDate getDate() {
		return this.date;
	}

	public LocalDate getDateEntered() {
		return dateEntered;
	}

	public final String getDescription() {
		return this.description;
	}

	public Object getMemo() {
		return getDescription();
	}

	public String getName() {
		return getDescription();
	}

	public String getNum() {
		return num;
	}

	public List<AccountEntry> getTransactionSplits() {
		return transactionSplits;
	}

	public boolean isInTaxYear(int year) {
		return (this.getDate().compareTo(LocalDate.of(year-1, 3, 31)) > 0 &&
				this.getDate().compareTo(LocalDate.of(year, 4, 1)) < 0);
	}

	public boolean isInTaxYearOrBefore(int year) {
		return this.getDate().compareTo(LocalDate.of(year, 4, 1)) < 0;
	}

	public final void setDate(final LocalDate argDate) {
		this.date = argDate;
	}

	public void setDateEntered(LocalDate dateEntered) {
		this.dateEntered = dateEntered;
	}

	public final void setDescription(final String argDescription) {
		this.description = argDescription;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public void setTransactionSplits(List<AccountEntry> transactionSplits) {
		this.transactionSplits = transactionSplits;
	}

	public String toString() {
		final String variableSeparator = "  ";
		final StringBuffer sb = new StringBuffer(1000);
		sb.append("date=").append(date).append(variableSeparator)
		  .append("description=").append(description)
		  .append(variableSeparator);
		transactionSplits.forEach(entry -> sb.append("\r\n\t" + entry));
		return sb.toString();
	}

	// adds a split to this transaction, throws an error if the split
	// already belongs to a different transaction
	public void addSplit(AccountEntry split) {
		this.getTransactionSplits().add(split);
		if (split.getTransaction() != null && split.getTransaction() != this) {
			throw new IllegalStateException("split already belongs to another tx");
		}
		split.setTransaction(this);
	}

	public void addSplitWithAmount(BigDecimal bigDecimal) {
		AccountEntry split = new AccountEntry();
		split.setAmount(bigDecimal);
		split.setDescription(this.getDescription());
	}

	public Transaction addEntry(AccountEntry entry) {
		entry.setTransaction(this);
		if (entry.getAccountSide() == AccountSide.BALANCE_EFFECT) {
			if (entry.getAccount().isDebitIncreases()) {
				entry.setAccountSide(entry.getAmount().signum() < 0 ? AccountSide.CREDIT : AccountSide.DEBIT);
				entry.setAmount(entry.getAmount().abs());
			} else {
				entry.setAccountSide(entry.getAmount().signum() < 0 ? AccountSide.DEBIT : AccountSide.CREDIT);
				entry.setAmount(entry.getAmount().abs());
			}
		}
		this.getTransactionSplits().add(entry);
		return this;
	}

	public Transaction add(AccountEntry split1) {
		this.addEntry(split1);
		split1.setTransaction(this);
		return this;
	}

	public Transaction withEntry(AccountEntry entry) {
		this.addEntry(entry);
		return this;
	}

}
