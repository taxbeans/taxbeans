package com.github.taxbeans.crypto.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.taxbeans.csv.CSVParser;

public class KrakenLedger {

	private String transactionId;
	
	private String tradeTxId;
	
	private BigDecimal amount;
	
	private String commodityCode;
	
	private BigDecimal fee;

	public static Map<String, KrakenLedger> loadFromCSV() {
		List<String[]> parsedFile = CSVParser.newInstance().parseFile(
				"target/classes/trades/kraken-ledgers-all.csv", true);
		Map<String, KrakenLedger> map = new HashMap<>();
		parsedFile.forEach(line -> addToMap(map, line));
		return map;
	}

	private static Object addToMap(Map<String, KrakenLedger> map, String[] line) {
		KrakenLedger ledger = new KrakenLedger();
		ledger.transactionId = line[0];
		ledger.amount = new BigDecimal(line[6].trim());
		ledger.commodityCode = line[5].trim().substring(1);
		ledger.commodityCode = "XBT".equals(ledger.commodityCode) ? "BTC" : ledger.commodityCode;
		ledger.tradeTxId = line[1].trim();
		ledger.fee = new BigDecimal(line[7].trim());
		map.put(ledger.transactionId, ledger);
		return map;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	public String getCommodityCode() {
		return commodityCode;
	}

	public void setCommodityCode(String commodityCode) {
		this.commodityCode = commodityCode;
	}

	public String getTradeTxId() {
		return tradeTxId;
	}

	public void setTradeTxId(String tradeTxId) {
		this.tradeTxId = tradeTxId;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	@Override
	public String toString() {
		return String.format("KrakenLedger [transactionId=%s, tradeTxId=%s, amount=%s, commodityCode=%s, fee=%s]",
				transactionId, tradeTxId, amount, commodityCode, fee);
	}

}
