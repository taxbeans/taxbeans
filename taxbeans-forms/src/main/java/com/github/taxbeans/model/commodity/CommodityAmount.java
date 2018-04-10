package com.github.taxbeans.model.commodity;

import java.math.BigDecimal;

public class CommodityAmount {

	private Commodity commodity;

	public CommodityAmount(Commodity commodity, BigDecimal amount) {
		super();
		this.commodity = commodity;
		this.amount = amount;
	}

	private BigDecimal amount;

	public static class CommodityAmountBuilder {
		private Commodity commodity;
		private BigDecimal amount;

		public CommodityAmountBuilder withCommodity(Commodity commodity) {
			this.commodity = commodity;
			return this;
		}

		public CommodityAmountBuilder withAmount(BigDecimal amount) {
			this.amount = amount;
			return this;
		}

		public CommodityAmount build() {
			return new CommodityAmount(commodity, amount);
		}
	}

	public static CommodityAmountBuilder commodityAmount() {
		return new CommodityAmountBuilder();
	}

	public Commodity getCommodity() {
		return commodity;
	}

	public void setSymbol(Commodity commodity) {
		this.commodity = commodity;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
}
