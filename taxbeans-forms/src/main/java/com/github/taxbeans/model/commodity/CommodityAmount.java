package com.github.taxbeans.model.commodity;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommodityAmount {
	
	final static Logger logger = LoggerFactory.getLogger(CommodityAmount.class);

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

	/**
	 * Returns a new CommodityAmount
	 */
	public CommodityAmount convertTo(Commodity commodity, CommodityExchangeRate rate) {
		if (!this.getCommodity().toString().equals(rate.getCommodityPair().getLeft().toString())) {
			throw new IllegalStateException("Left/from doesn't match: " + this.getCommodity() + ", " + rate.getCommodityPair().getLeft());
		}
		if (!commodity.toString().equals(rate.getCommodityPair().getRight().toString())) {
			throw new IllegalStateException("Right/to doesn't match");
		}
		CommodityAmount newCommodityAmount = CommodityAmount.commodityAmount()
				.withCommodity(commodity)
				.withAmount(this.amount.multiply(rate.getRate())).build();
		return newCommodityAmount;
	}

	public void add(CommodityAmount other) {
		if (this.getCommodity().getSymbol() != null && !this.getCommodity().equals(other.getCommodity())) {
			throw new IllegalStateException("Commodities don't match: " + this.getCommodity() + ", " + other.getCommodity());
		}
		logger.info("this amount before = {}", this.amount);
		logger.info("other amount = {}", other.amount);
		this.amount = this.amount.add(other.getAmount());
		logger.info("this amount after = {}", this.amount);
		if (this.getCommodity().getSymbol() == null) {
			this.getCommodity().setSymbol(other.getCommodity().getSymbol());
		}
	}

	@Override
	public String toString() {
		return String.format("%s %s", amount, commodity.getSymbol());
	}
}
