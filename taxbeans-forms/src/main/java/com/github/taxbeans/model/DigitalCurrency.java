package com.github.taxbeans.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DigitalCurrency {
	
	private BigDecimal amount;

	private DigitalCurrencyCode code;

	private List<Inventory> inventory = new ArrayList<>();
	
	public DigitalCurrency(BigDecimal amount, DigitalCurrencyCode code) {
		super();
		this.amount = amount;
		this.code = code;
	}

	public DigitalCurrency() {
	}

	public void add(Inventory inventoryItem) {
		inventory.add(inventoryItem);
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public DigitalCurrencyCode getCode() {
		return code;
	}

	public List<Inventory> getInventory() {
		return inventory;
	}
	
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public void setCode(DigitalCurrencyCode code) {
		this.code = code;
	}

}
