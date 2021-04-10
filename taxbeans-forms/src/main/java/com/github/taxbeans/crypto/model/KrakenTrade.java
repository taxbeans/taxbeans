package com.github.taxbeans.crypto.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.taxbeans.csv.CSVParser;
import com.github.taxbeans.model.commodity.Commodity;

public class KrakenTrade {

	private String transactionId;

	private BigDecimal cost = BigDecimal.ZERO;

	private BigDecimal volume;

	private String formattedPair;

	private String type;

	private String leftCommodity;

	private String rightCommodity;

	private BigDecimal price;

	private BigDecimal margin = BigDecimal.ZERO;

	/* Derived from cost / margin */
	private BigDecimal leverage;

	private boolean closing;

	public static Map<String, KrakenTrade> loadFromCSV() {
		List<String[]> parsedFile = CSVParser.newInstance().parseFile(
				"target/classes/tradehistory/kraken-trades.csv", true);
		Map<String, KrakenTrade> map = new HashMap<>();
		parsedFile.forEach(line -> addToMap(map , line));
		return map;
	}

	private static Object addToMap(Map<String, KrakenTrade> map, String[] line) {
		KrakenTrade trade = new KrakenTrade();
		trade.transactionId = line[0];
		trade.cost = new BigDecimal(line[7].trim());
		trade.formattedPair = line[2].trim();
		trade.leftCommodity = trade.formattedPair.substring(1,4);
		trade.leftCommodity = "XBT".equals(trade.leftCommodity) ? "BTC" : trade.leftCommodity;
		trade.rightCommodity = trade.formattedPair.substring(5,8);
		trade.rightCommodity = "XBT".equals(trade.rightCommodity) ? "BTC" : trade.rightCommodity;
		trade.type = line[4].trim();
		trade.volume = new BigDecimal(line[9].trim());
		trade.price = new BigDecimal(line[6].trim());
		trade.closing = "closing".equals(line[11].trim());
		trade.margin = new BigDecimal(line[10].trim());
		if (trade.margin.compareTo(BigDecimal.ZERO) != 0) {
			trade.leverage = trade.cost.divide(trade.margin, MathContext.DECIMAL128);
		}
		map.put(line[0], trade);
		return map;
	}

	public void recalculateLeverage() {
		if (margin.compareTo(BigDecimal.ZERO) != 0) {
			leverage = cost.divide(margin, MathContext.DECIMAL128);
		}
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public String getFormattedPair() {
		return formattedPair;
	}

	public void setFormattedPair(String formattedPair) {
		this.formattedPair = formattedPair;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLeftCommodity() {
		return leftCommodity;
	}

	public void setLeftCommodity(String leftCommodity) {
		this.leftCommodity = leftCommodity;
	}

	public String getRightCommodity() {
		return rightCommodity;
	}

	public void setRightCommodity(String rightCommodity) {
		this.rightCommodity = rightCommodity;
	}

	public BigDecimal getVolume() {
		return volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public boolean isClosing() {
		return closing;
	}

	public void setClosing(boolean closing) {
		this.closing = closing;
	}

	@Override
	public String toString() {
		return String.format(
				"KrakenTrade [transactionId=%s, cost=%s, volume=%s, formattedPair=%s, type=%s, leftCommodity=%s, rightCommodity=%s, price=%s, margin=%s, leverage=%s, closing=%s]",
				transactionId, cost, volume, formattedPair, type, leftCommodity, rightCommodity, price, margin,
				leverage, closing);
	}

	public BigDecimal getMargin() {
		return margin;
	}

	public void setMargin(BigDecimal margin) {
		this.margin = margin;
	}

	public Commodity getLeft() {
		return Commodity.commodity()
				.withSymbol(getLeftCommodity()).build();
	}
	
	public Commodity getRight() {
		return Commodity.commodity()
				.withSymbol(getRightCommodity()).build();
	}
}
