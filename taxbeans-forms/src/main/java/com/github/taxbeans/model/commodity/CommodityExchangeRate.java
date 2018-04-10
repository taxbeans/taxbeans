package com.github.taxbeans.model.commodity;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class CommodityExchangeRate {

	@Override
	public String toString() {
		return String.format("CommodityExchangeRate [commodityPair=%s, dateTime=%s, rate=%s]", commodityPair, dateTime,
				rate);
	}

	private CommodityPair commodityPair;

	private ZonedDateTime dateTime;

	private BigDecimal rate;

	public CommodityExchangeRate(CommodityPair commodityPair, ZonedDateTime dateTime, BigDecimal rate) {
		super();
		this.commodityPair = commodityPair;
		this.dateTime = dateTime;
		this.rate = rate;
	}

	public CommodityPair getCommodityPair() {
		return commodityPair;
	}

	public void setCommodityPair(CommodityPair commodityPair) {
		this.commodityPair = commodityPair;
	}

	public ZonedDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(ZonedDateTime dateTime) {
		this.dateTime = dateTime;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public static class CommodityExchangeRateBuilder {
		private CommodityPair commodityPair;
		private ZonedDateTime dateTime;
		private BigDecimal rate;

		public CommodityExchangeRateBuilder withCommodityPair(CommodityPair commodityPair) {
			this.commodityPair = commodityPair;
			return this;
		}

		public CommodityExchangeRateBuilder withDateTime(ZonedDateTime dateTime) {
			this.dateTime = dateTime;
			return this;
		}

		public CommodityExchangeRateBuilder withRate(BigDecimal rate) {
			this.rate = rate;
			return this;
		}

		public CommodityExchangeRate build() {
			return new CommodityExchangeRate(commodityPair, dateTime, rate);
		}
	}

	public static CommodityExchangeRateBuilder commodityExchangeRate() {
		return new CommodityExchangeRateBuilder();
	}

}
