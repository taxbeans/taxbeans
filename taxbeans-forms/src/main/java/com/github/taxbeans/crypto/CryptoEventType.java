package com.github.taxbeans.crypto;

public enum CryptoEventType {
	//enum comparator order is simply the order they are declared{
	Deposit,
	Trade,
	Withdrawal,
	Profit,
	Loss,
	ExchangeBalance,
	Loan,
	Repayment,
	Undefined;

	@Override
	public String toString() {
		return this.name();
	}
}
