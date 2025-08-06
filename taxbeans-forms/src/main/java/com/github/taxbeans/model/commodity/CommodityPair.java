package com.github.taxbeans.model.commodity;

public class CommodityPair {

	private Commodity left;

	private Commodity right;

	public CommodityPair(Commodity left, Commodity right) {
		super();
		this.left = left;
		this.right = right;
	}

	public Commodity getLeft() {
		return left;
	}

	public void setLeft(Commodity left) {
		this.left = left;
	}

	@Override
	public String toString() {
		return String.format("CommodityPair [left=%s, right=%s]", left, right);
	}

	public Commodity getRight() {
		return right;
	}

	public void setRight(Commodity right) {
		this.right = right;
	}

	public static class CommodityPairBuilder {
		private Commodity left;
		private Commodity right;

		public CommodityPairBuilder withLeft(Commodity left) {
			this.left = left;
			return this;
		}

		public CommodityPairBuilder withRight(Commodity right) {
			this.right = right;
			return this;
		}

		public CommodityPair build() {
			return new CommodityPair(left, right);
		}
	}

	public static CommodityPairBuilder commodityPair() {
		return new CommodityPairBuilder();
	}

}
