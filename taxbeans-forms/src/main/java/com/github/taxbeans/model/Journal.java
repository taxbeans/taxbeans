package com.github.taxbeans.model;

import java.util.ArrayList;
import java.util.List;

public class Journal {
	
	private List<Transaction> transactions = new ArrayList<>();

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

}
