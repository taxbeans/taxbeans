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

	public Commodity getRight() {
		return right;
	}

	public void setRight(Commodity right) {
		this.right = right;
	}

}
