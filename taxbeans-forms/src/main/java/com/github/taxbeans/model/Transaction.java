package com.github.taxbeans.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Transaction implements Comparable<Transaction>, Cloneable {

	final static Logger logger = LoggerFactory.getLogger(Transaction.class);

	private Account creditAccount;

	private LocalDate date;

	private LocalDate dateEntered;

	private Account debitAccount;

	private String description;

	private String num;

	private List<TransactionSplit> transactionSplits = new ArrayList<TransactionSplit>();

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

	public final Account getCreditAccount() {
		return this.creditAccount;
	}

	public final LocalDate getDate() {
		return this.date;
	}

	public LocalDate getDateEntered() {
		return dateEntered;
	}

	public final Account getDebitAccount() {
		return this.debitAccount;
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

	public List<TransactionSplit> getTransactionSplits() {
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

	public void setTransactionSplits(List<TransactionSplit> transactionSplits) {
		this.transactionSplits = transactionSplits;
	}

	public String toString() {
		final int sbSize = 1000;
		final String variableSeparator = "  ";
		final StringBuffer sb = new StringBuffer(sbSize);

		sb.append("date=").append(date);
		sb.append(variableSeparator);
		sb.append("description=").append(description);
		sb.append(variableSeparator);
		sb.append("creditAccount=").append(creditAccount);
		sb.append(variableSeparator);
		sb.append("debitAccount=").append(debitAccount);
		for (TransactionSplit transactionSplit : transactionSplits) {
			sb.append("\r\n");
			sb.append("\t" + transactionSplit);
		}
		return sb.toString();
	}

	// adds a split to this transaction, throws an error if the split
	// already belongs to a different transaction
	public void addSplit(TransactionSplit split) {
		this.getTransactionSplits().add(split);
		if (split.getTransaction() != null && split.getTransaction() != this) {
			throw new IllegalStateException("split already belongs to another tx");
		}
		split.setTransaction(this);
	}

	public void addSplitWithAmount(BigDecimal bigDecimal) {
		TransactionSplit split = new TransactionSplit();
		split.setAmount(bigDecimal);
		split.setDescription(this.getDescription());
	}

}
