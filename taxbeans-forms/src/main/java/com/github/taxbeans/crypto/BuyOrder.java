package com.github.taxbeans.crypto;

import java.math.BigDecimal;

public class BuyOrder extends Order {
	
	public BigDecimal getReimbursedAmount() {
		return reimbursedAmount;
	}

	public void setReimbursedAmount(BigDecimal reimbursedAmount) {
		this.reimbursedAmount = reimbursedAmount;
	}

	private BigDecimal reimbursedAmount;

	@Override
	public String toString() {
		return String.format(
				"BuyOrder [reimbursedAmount=%s, isExecuted()=%s, getAmount()=%s, getReference()=%s, getCommodityName()=%s]",
				reimbursedAmount, isExecuted(), getAmount(), getReference(), getCommodityName());
	}

}
