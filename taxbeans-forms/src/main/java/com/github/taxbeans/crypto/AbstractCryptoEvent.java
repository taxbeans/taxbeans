package com.github.taxbeans.crypto;

public abstract class AbstractCryptoEvent implements CryptoEvent {

	private static final String EMPTY_STRING = "";

	private CurrencyExchange currencyExchange;

	private String comment = EMPTY_STRING;

	private FormattedComment formattedComment = null;

	private boolean fundsIntroduced;

	private boolean businessExpense;

	private String sourceCSVLine;

	private boolean leveraged;

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

	public FormattedComment getFormattedComment() {
		return formattedComment;
	}

	public void setFormattedComment(FormattedComment formattedComment) {
		this.formattedComment = formattedComment;
	}

	@Override
	public int compareTo(CryptoEvent o) {
		int result = this.getWhen().compareTo(o.getWhen());
		if (result == 0) {
//			if (!(this instanceof ExchangeCurrencyBalance) && (o instanceof ExchangeCurrencyBalance)) {
//				return -1;
//			} else if ((this instanceof ExchangeCurrencyBalance) && !(o instanceof ExchangeCurrencyBalance)) {
//				return 1;
//			} else {
				CryptoEventType cryptoEventType = this.getCryptoEventType();
				CryptoEventType otherCryptoEventType = o.getCryptoEventType();
				result = cryptoEventType.compareTo(otherCryptoEventType);
		//	}
		}
		//TODO order by nominal amount before rownum
		if (result == 0) {
			return Integer.valueOf(this.getRowNum()).compareTo(Integer.valueOf(o.getRowNum()));
		}
		return result;
	}

	@Override
	public CryptoEventType getCryptoEventType() {
		return CryptoEventType.Undefined;
	}

	public String getSourceCSVLine() {
		return sourceCSVLine;
	}

	public void setSourceCSVLine(String sourceCSVLine) {
		this.sourceCSVLine = sourceCSVLine;
	}

	public boolean isLeveraged() {
		return leveraged;
	}

	public void setLeveraged(boolean leveraged) {
		this.leveraged = leveraged;
	}
}
