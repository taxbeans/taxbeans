package com.github.taxbeans.crypto.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.money.Monetary;
import javax.money.MonetaryAmount;

import com.github.taxbeans.csv.CSVParser;
import com.github.taxbeans.model.commodity.Commodity;
import com.github.taxbeans.model.commodity.CommodityExchangeRate;
import com.github.taxbeans.model.commodity.CommodityPair;


public class CoinMarketCapBtcEod {

	//CSV file dates are UTC but LocalDate will do
	private LocalDate date;
	
	private BigDecimal close;
	
	private static volatile Map<LocalDate, CoinMarketCapBtcEod> map;
	
	private static Object mapLock = new Object();
	
	public static Map<LocalDate, CoinMarketCapBtcEod> loadFromCSV() {
		List<String[]> parsedFile = CSVParser.newInstance().parseFile(
				"target/classes/tradehistory/coin-market-cap-btc-eod.csv", true);
		Map<LocalDate, CoinMarketCapBtcEod> map = new HashMap<>();
		parsedFile.forEach(line -> addToMap(map , line));
		CoinMarketCapBtcEod.map = map;
		return map;
	}

	private static Object addToMap(Map<LocalDate, CoinMarketCapBtcEod> map, String[] line) {
		CoinMarketCapBtcEod dataPoint = new CoinMarketCapBtcEod();
		dataPoint.date = LocalDate.parse(line[0], DateTimeFormatter.ofPattern("MMM dd, yyyy"));
		dataPoint.close = new BigDecimal(line[4].trim());
		map.put(dataPoint.date, dataPoint);
		return map;
	}
	
	private static void lazyLoad() {
		if (map == null) {
			synchronized (mapLock) {
				if (map == null) {
					loadFromCSV();
				}
			}
		}
	}

	public LocalDate getDate() {
		return date;
	}

	public BigDecimal getClose() {
		return close;
	}

	/**
	 * @return The closing price in USD on that day
	 */
	public static MonetaryAmount getClose(ZonedDateTime zonedDateTime) {
		lazyLoad();
		ZonedDateTime utcZonedDateTime = ZonedDateTime.ofInstant(zonedDateTime.toInstant(), ZoneId.of("UTC"));
		BigDecimal close = map.get(utcZonedDateTime.toLocalDate()).getClose();
		return Monetary.getDefaultAmountFactory().setNumber(close).setCurrency("USD").create();
	}
	
	/**
	 * Gets the close as a CommodityExchangeRateObject
	 * @param zonedDateTime
	 * @return
	 */
	public static CommodityExchangeRate getRate(ZonedDateTime zonedDateTime) {
		ZonedDateTime utcZonedDateTime = ZonedDateTime.ofInstant(zonedDateTime.toInstant(), ZoneId.of("UTC"));
		BigDecimal close = map.get(utcZonedDateTime.toLocalDate()).getClose();
		
		Commodity usdCommodity = Commodity.commodity().withSymbol("USD").build();
		Commodity btcCommodity = Commodity.commodity().withSymbol("BTC").build();
		CommodityPair commodityPair = CommodityPair.commodityPair()
				.withLeft(btcCommodity)
				.withRight(usdCommodity)
				.build();
		CommodityExchangeRate rate = CommodityExchangeRate.commodityExchangeRate()
				.withCommodityPair(commodityPair)
				.withRate(close)
				.withDateTime(zonedDateTime).build();
		return rate;
	}
}