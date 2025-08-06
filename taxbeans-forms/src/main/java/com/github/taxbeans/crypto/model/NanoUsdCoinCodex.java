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

//BsvUsdEod.java
//18th feb 2020 is 19th feb 2020 on yahoo, which is attributable to the timezone difference
//assume this one is nz time and yahoo is utc
//https://coincodex.com/crypto/nano/historical-data/
public class NanoUsdCoinCodex {

	private static final String PATH = "target/classes/trades/NANO-USD-coin-codex.csv";

	private static final String NANO = "NANO";

	private static final String USD = "USD";

	private static final String UTC = "UTC";

	private static final String STANDARD_DATE_FORMAT = "MMM-dd-yyyy";
	
	private static final boolean ADJUST_FROM_NZT_TO_UTC = true;

	//CSV file dates are UTC but LocalDate will do
	private LocalDate date;

	private BigDecimal close;
	
	private static Object mapLock = new Object();

	final static Logger logger = LoggerFactory.getLogger(NanoUsdCoinCodex.class);

	private static Map<LocalDate, NanoUsdCoinCodex> map;

	public static Map<LocalDate, NanoUsdCoinCodex> loadFromCSV() {
		List<String[]> parsedFile = CSVParser.newInstance().parseFile(PATH, true);
		Map<LocalDate, NanoUsdCoinCodex> map = new HashMap<>();
		parsedFile.forEach(line -> addToMap(map , line));
		NanoUsdCoinCodex.map = map;
		return map;
	}

	private static Object addToMap(Map<LocalDate, NanoUsdCoinCodex> map, String[] line) {
		NanoUsdCoinCodex dataPoint = new NanoUsdCoinCodex();
		dataPoint.date = LocalDate.parse(line[0], DateTimeFormatter.ofPattern(STANDARD_DATE_FORMAT));
		if (ADJUST_FROM_NZT_TO_UTC) {
			dataPoint.date = dataPoint.date.plusDays(1L);  //adjust from nzdt to utc
		}
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
		NanoUsdCoinCodex coinMarketCapEthEod = map.get(utcZonedDateTime.toLocalDate());
		if (coinMarketCapEthEod == null) {			
			throw new AssertionError(String.format("No %1$s data available on date: %2$s", NANO, utcZonedDateTime.toLocalDate()));
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
		Commodity digitalCommodity = Commodity.commodity().withSymbol(NANO).build();
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