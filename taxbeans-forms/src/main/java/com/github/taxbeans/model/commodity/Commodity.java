package com.github.taxbeans.model.commodity;

import java.util.Arrays;

public class Commodity {

	private String symbol;

	public Commodity(String symbol) {
		super();
		this.symbol = symbol;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public static class CommodityBuilder {
		private String symbol;

		public CommodityBuilder withSymbol(String symbol) {
			this.symbol = symbol;
			return this;
		}

		public Commodity build() {
			return new Commodity(symbol);
		}
	}

	public static CommodityBuilder commodity() {
		return new CommodityBuilder();
	}

	public boolean isDigitalCurrency() {
		return Arrays.asList(new String[]{"ETH", "ETC", "BTC"}).contains(this.symbol);
	}

	@Override
	public String toString() {
		return String.format("Commodity [symbol=%s]", symbol);
	}

}
