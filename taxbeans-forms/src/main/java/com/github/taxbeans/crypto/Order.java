package com.github.taxbeans.crypto;

import java.math.BigDecimal;

public class Order {
	
	@Override
	public String toString() {
		return "Order [reference=" + reference + ", commodityName=" + commodityName + ", amount=" + amount + "]";
	}

	private String reference;
	
	private String commodityName;
	
	private BigDecimal amount;
	
	private boolean executed;

	public boolean isExecuted() {
		return executed;
	}

	public void setExecuted(boolean executed) {
		this.executed = executed;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getCommodityName() {
		return commodityName;
	}

	public void setCommodityName(String commodityName) {
		this.commodityName = commodityName;
	}

}
