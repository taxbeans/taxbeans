package com.github.taxbeans.crypto;

public class FormattedComment {

	private CurrencyAmount balance1;

	private CurrencyAmount balance2;

	private String note;

	public CurrencyAmount getBalance1() {
		return balance1;
	}

	public void setBalance1(CurrencyAmount balance1) {
		this.balance1 = balance1;
	}

	public CurrencyAmount getBalance2() {
		return balance2;
	}

	public void setBalance2(CurrencyAmount balance2) {
		this.balance2 = balance2;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Override
	public String toString() {
		return String.format("[Bal.1=%s:Bal.2=%s:Note=%s]", nullAsNA(balance1), nullAsNA(balance2), note);
	}

	private String nullAsNA(CurrencyAmount balance) {
		if (balance == null) {
			return "NA";
		} else {
			return String.valueOf(balance);
		}
	}

	public static FormattedComment of(CurrencyAmount balance1, CurrencyAmount balance2, String note) {
		FormattedComment comment = new FormattedComment();
		comment.setBalance1(balance1);
		comment.setBalance2(balance2);
		comment.setNote(note);
		return comment;
	}

}
