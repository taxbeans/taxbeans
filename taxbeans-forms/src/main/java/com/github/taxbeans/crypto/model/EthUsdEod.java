package com.github.taxbeans.crypto.model;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.money.Monetary;
import javax.money.MonetaryAmount;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.taxbeans.csv.CSVParser;
import com.github.taxbeans.model.commodity.Commodity;
import com.github.taxbeans.model.commodity.CommodityExchangeRate;
import com.github.taxbeans.model.commodity.CommodityPair;

public class EthUsdEod {

	private static final String ETH = "ETH";

	private static final String USD = "USD";

	private static final String UTC = "UTC";

	//CSV file dates are UTC but LocalDate will do
	private LocalDate date;

	private BigDecimal close;
	
	private static Object mapLock = new Object();

	final static Logger logger = LoggerFactory.getLogger(EthUsdEod.class);

	private static Map<LocalDate, EthUsdEod> map;

	public static Map<LocalDate, EthUsdEod> loadFromCSV() {
		List<String[]> parsedFile = CSVParser.newInstance().parseFile(
				"target/classes/trades/ETH-USD.csv", true);
		Map<LocalDate, EthUsdEod> map = new HashMap<>();
		parsedFile.forEach(line -> addToMap(map , line));
		EthUsdEod.map = map;
		return map;
	}

	private static Object addToMap(Map<LocalDate, EthUsdEod> map, String[] line) {
		EthUsdEod dataPoint = new EthUsdEod();
		dataPoint.date = LocalDate.parse(line[0], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		try {
			dataPoint.close = new BigDecimal(String.valueOf(
					NumberFormat.getNumberInstance(java.util.Locale.US).parse(line[4].trim())));
		} catch (ParseException e) {
			logger.error(line[4].trim());
			throw new IllegalStateException(e);
		}
		map.put(dataPoint.date, dataPoint);
		return map;
	}

	public LocalDate getDate() {
		return date;
	}

	public BigDecimal getClose() {
		return close;
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

	/**
	 * @return The closing price in USD on that day
	 */
	public static MonetaryAmount getClose(ZonedDateTime zonedDateTime) {
		lazyLoad();
		ZonedDateTime utcZonedDateTime = ZonedDateTime.ofInstant(zonedDateTime.toInstant(), ZoneId.of(UTC));
		EthUsdEod coinMarketCapEthEod = map.get(utcZonedDateTime.toLocalDate());
		if (coinMarketCapEthEod == null) {
			throw new AssertionError(String.format("No %1$s data available on date: %2$s", ETH, utcZonedDateTime.toLocalDate()));
		}
		BigDecimal close = coinMarketCapEthEod.getClose();
		return Monetary.getDefaultAmountFactory().setNumber(close).setCurrency(USD).create();
	}

	/**
	 * Gets the close as a CommodityExchangeRateObject
	 * @param zonedDateTime
	 * @return
	 */
	public static CommodityExchangeRate getRate(ZonedDateTime zonedDateTime) {
		ZonedDateTime utcZonedDateTime = ZonedDateTime.ofInstant(zonedDateTime.toInstant(), ZoneId.of(UTC));
		if (map == null) {
			loadFromCSV();
		}
		BigDecimal close = map.get(utcZonedDateTime.toLocalDate()).getClose();

		Commodity usdCommodity = Commodity.commodity().withSymbol(USD).build();
		Commodity digitalCommodity = Commodity.commodity().withSymbol(ETH).build();
		CommodityPair commodityPair = CommodityPair.commodityPair()
				.withLeft(digitalCommodity)
				.withRight(usdCommodity)
				.build();
		CommodityExchangeRate rate = CommodityExchangeRate.commodityExchangeRate()
				.withCommodityPair(commodityPair)
				.withRate(close)
				.withDateTime(zonedDateTime).build();
		return rate;
	}
}