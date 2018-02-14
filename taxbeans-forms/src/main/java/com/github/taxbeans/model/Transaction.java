package com.github.taxbeans.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.convert.ConversionQuery;
import javax.money.convert.ConversionQueryBuilder;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.MonetaryConversions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Transaction implements Comparable<Transaction>, Cloneable {

	final static Logger logger = LoggerFactory.getLogger(Transaction.class);

	private BigDecimal amount;

	private Account creditAccount;

	private LocalDate date;

	private LocalDate dateEntered;

	private Account debitAccount;

	private String description;

	private String num;

	private List<TransactionSplit> transactionSplits = new ArrayList<TransactionSplit>();
	
	public void translate(LocalDate translationDate, CurrencyUnit from, CurrencyUnit to) {
		CurrencyUnit baseCurrency = Monetary.getCurrency("USD");
		ConversionQuery conversionQuery = ConversionQueryBuilder.of()
                .setProviderName("ECB")
                .setBaseCurrency(baseCurrency)
                .setTermCurrency(Monetary.getCurrency("NZD"))
                .set(LocalDate.class, LocalDate.of(2017, Month.SEPTEMBER, 10))
                .build();
		CurrencyConversion currencyConversion = 
				MonetaryConversions.getExchangeRateProvider().getCurrencyConversion(conversionQuery);
		MonetaryAmount oneDollar = Monetary.getDefaultAmountFactory().setCurrency(baseCurrency).setNumber(this.amount).create();
		oneDollar.with(currencyConversion);
		amount = new BigDecimal(oneDollar.getNumber().toString());
	}

	public BigDecimal calculateIncome(List<Transaction> transactions, int year) {
		BigDecimal sum = BigDecimal.ZERO;
		for (Transaction transaction : transactions) {
			if (!transaction.getCreditAccount().getAccountType().equals(AccountType.income))
				continue;
			if (transaction.getDate().compareTo(LocalDate.of(year-1, 3, 31)) > 0 &&
					transaction.getDate().compareTo(LocalDate.of(year, 4, 1)) < 0) {
				//logger.info("Date of transaction = " + transaction.getDate());
				sum = sum.add(transaction.getAmount());
				//		logger.info("Sum for " + year + "=" + sum);
			}
		}
		logger.info("Income Sum for " + year + "=" + sum);
		return sum;
	}

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

	public final BigDecimal getAmount() {
		return this.amount;
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

	public final void setAmount(final BigDecimal argAmount) {
		this.amount = argAmount;
	}

	public final void setCreditAccount(final Account argCreditAccount) {
		this.creditAccount = argCreditAccount;
	}

	public final void setDate(final LocalDate argDate) {
		this.date = argDate;
	}

	public void setDateEntered(LocalDate dateEntered) {
		this.dateEntered = dateEntered;
	}

	public final void setDebitAccount(final Account argDebitAccount) {
		this.debitAccount = argDebitAccount;
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
		sb.append("amount=").append(amount);
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

}
