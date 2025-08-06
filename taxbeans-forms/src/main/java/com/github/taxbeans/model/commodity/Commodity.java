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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Commodity other = (Commodity) obj;
		if (symbol == null) {
			if (other.symbol != null)
				return false;
		} else if (!symbol.equals(other.symbol))
			return false;
		return true;
	}

}
