package com.github.taxbeans.currency;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.money.CurrencyUnit;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RBNZHistoricalExchangeRatesReader {

	final static Logger logger = LoggerFactory.getLogger(RBNZHistoricalExchangeRatesReader.class);

	private static volatile Map<String, ExchangeRateInfo> exchangeRateInfo = new HashMap<>();

	public static void main(String[] args) throws IOException {
		logger.trace(String.format("1 NZD is %1$s USD", getForeignToNZDRate(LocalDate.of(2017,1,17), "USD")));
		//loadAllRates();
	}

	private static ExchangeRateInfo loadAllRates(String currency) {
		ExchangeRateInfo exchangeRateInfo = new ExchangeRateInfo();
		Map<LocalDate, BigDecimal> exchangeRates = new HashMap<>();
		loadAllRates(currency, exchangeRates, "/rbnz-historical-exchange-rates.xlsx");
		loadAllRates(currency, exchangeRates, "/rbnz-historical-exchange-rates-2018-and-newer.xlsx");
		exchangeRateInfo.setExchangeRates(exchangeRates);
		exchangeRateInfo.setWeekdaysOnly(true);
		return exchangeRateInfo;
	}

	private static void loadAllRates(String currency, Map<LocalDate, BigDecimal> exchangeRates, String filename) {
		String currencyColumn;
		if (currency.equals("USD")) {
			currencyColumn = "C";
		} else if (currency.equals("EUR")) {
			currencyColumn = "G";
		} else {
 			throw new AssertionError("Unsupported currency: " + currency);
		}
		Workbook wb;
		try {
			InputStream resourceAsStream = RBNZHistoricalExchangeRatesReader.class
				.getResourceAsStream(filename);
			wb = new XSSFWorkbook(new BufferedInputStream(resourceAsStream));
		} catch (IOException e) {
			throw new IllegalStateException("could not load RBNZ rates XLS: " + filename, e);
		}
		Sheet sheet = wb.getSheetAt(0);


		// suppose your formula is in B3
		for (int rowNum = 6;rowNum<2000;rowNum++) {
			CellReference cellReference = new CellReference(String.format("%1$s%2$s", currencyColumn, rowNum));
			Row row = sheet.getRow(cellReference.getRow());
			if (row == null) {
				logger.trace("NZD/USD Rate Loading Complete, number of rates loaded = " + exchangeRates.size());
				break;
			}
			Cell cell = row.getCell(cellReference.getCol());

			BigDecimal rate = new BigDecimal(cell.getNumericCellValue()+"");
			logger.trace("rate = " + rate);

			cellReference = new CellReference(String.format("A%1$s", rowNum));
			row = sheet.getRow(cellReference.getRow());
			cell = row.getCell(cellReference.getCol());
			//spreadsheet is in Pacific/Auckland timezone
			LocalDate dateCellValue = ZonedDateTime.ofInstant(cell.getDateCellValue().toInstant(), ZoneId.of("Pacific/Auckland")).toLocalDate();
			logger.trace("date = " + dateCellValue);
			exchangeRates.put(dateCellValue, rate);
		}
		try {
			wb.close();
		} catch (IOException e) {
			throw new IllegalStateException("could not load RBNZ rates XLS");
		}
	}

	public static BigDecimal getCrossRate(LocalDate date, String fxFrom, String fxTo) {

		return getForeignToNZDRate(date, fxFrom).multiply(getNZDtoForeignRate(date, fxTo));
		//FileInputStream fis = new FileInputStream("/rbnz-historical-exchange-rates.xlsx");
//		Workbook wb = new XSSFWorkbook("./target/classes/rbnz-historical-exchange-rates.xlsx");
//		Sheet sheet = wb.getSheetAt(0);
//		FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
//
//		// suppose your formula is in B3
//		CellReference cellReference = new CellReference("C6");
//		Row row = sheet.getRow(cellReference.getRow());
//		Cell cell = row.getCell(cellReference.getCol());
//		return new BigDecimal(cell.getNumericCellValue()+"");
	}

	@Deprecated
	public static BigDecimal getForeignToNZDRate(LocalDate date, String foreignCurrency) {

		return BigDecimal.ONE.divide(getNZDtoForeignRate(date, foreignCurrency), MathContext.DECIMAL128);
		//FileInputStream fis = new FileInputStream("/rbnz-historical-exchange-rates.xlsx");
//		Workbook wb = new XSSFWorkbook("./target/classes/rbnz-historical-exchange-rates.xlsx");
//		Sheet sheet = wb.getSheetAt(0);
//		FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
//
//		// suppose your formula is in B3
//		CellReference cellReference = new CellReference("C6");
//		Row row = sheet.getRow(cellReference.getRow());
//		Cell cell = row.getCell(cellReference.getCol());
//		return new BigDecimal(cell.getNumericCellValue()+"");
	}

	@Deprecated
	public static BigDecimal getNZDtoForeignRate(LocalDate date, String foreignCurrency) {
		logger.trace("date = " + date);
		logger.trace("Day of week = " + date.getDayOfWeek());
		ExchangeRateInfo exchangeRateInfo2 = exchangeRateInfo.get(foreignCurrency);
		if (exchangeRateInfo2 == null) {
			exchangeRateInfo.put(foreignCurrency, (exchangeRateInfo2 = loadAllRates(foreignCurrency)));
		}
		if (exchangeRateInfo2.isWeekdaysOnly()) {
			if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
				logger.trace("Date requested for Sunday, rewinding to Friday");
				date = date.minusDays(2);
			} else if (date.getDayOfWeek() == DayOfWeek.SATURDAY) {
				logger.trace("Date requested for Saturday, rewinding to Friday");
				date = date.minusDays(1);
			}
		}
		BigDecimal exchangeRate = exchangeRateInfo2.getExchangeRates().get(date);
		if (exchangeRate == null) {
			logger.trace("Assuming {} is a public holiday since no exchange rate is available", date);
			exchangeRate = getNZDtoForeignRate(date.minusDays(1), foreignCurrency);
		}
		return exchangeRate;
	}

	public static BigDecimal getForeignToNZDRate(ZonedDateTime zonedDateTime, CurrencyUnit currency) {
		LocalDate localDate = zonedDateTime.toLocalDate();
		return getForeignToNZDRate(localDate, currency.getCurrencyCode());

	}

}
