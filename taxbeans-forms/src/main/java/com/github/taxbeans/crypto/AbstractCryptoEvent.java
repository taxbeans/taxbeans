package com.github.taxbeans.crypto;

public abstract class AbstractCryptoEvent implements CryptoEvent {

	private static final String EMPTY_STRING = "";

	private CurrencyExchange currencyExchange;
	
	private String comment = EMPTY_STRING;
	
	private boolean fundsIntroduced;
	
	private boolean businessExpense;

	public CurrencyExchange getCurrencyExchange() {
		return currencyExchange;
	}

	public void setCurrencyExchange(CurrencyExchange currencyExchange) {
		this.currencyExchange = currencyExchange;
	}

	@Override
	public String getComment() {
		return comment;
	}
	
	@Override
	public void setComment(String comment) {
		this.comment = comment;
	}

	boolean isFundsIntroduced() {
		return fundsIntroduced;
	}

	public void setFundsIntroduced(boolean fundsIntroduced) {
		this.fundsIntroduced = fundsIntroduced;
	}

	boolean isBusinessExpense() {
		return businessExpense;
	}

	public void setBusinessExpense(boolean businessExpense) {
		this.businessExpense = businessExpense;
	}
}
