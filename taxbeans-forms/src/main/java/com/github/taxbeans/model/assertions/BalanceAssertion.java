package com.github.taxbeans.model.assertions;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BalanceAssertion {

	private LocalDate date;

	private BigDecimal balance;

	//transaction number on that day
	private int number;

	public final LocalDate getDate() {
		return this.date;
	}

	public final void setDate(final LocalDate argDate) {
		this.date = argDate;
	}

	public final BigDecimal getBalance() {
		return this.balance;
	}

	public final void setBalance(final BigDecimal argBalance) {
		this.balance = argBalance;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
}