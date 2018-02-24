package com.github.taxbeans.currency;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.taxbeans.forms.nz.IR3FormBean;

public class RBNZHistoricalExchangeRatesReader {

	final static Logger logger = LoggerFactory.getLogger(IR3FormBean.class);
	
	private static volatile ExchangeRateInfo exchangeRateInfo;
	
	

	public static void main(String[] args) throws IOException {
		logger.info(String.format("1 NZD is %1$s USD", getUSDtoNZDRate(new Date(117,1,17))));
		//loadAllRates();
	}

	private static ExchangeRateInfo loadAllRates() {
		Workbook wb;
		try {
			InputStream resourceAsStream = RBNZHistoricalExchangeRatesReader.class
				.getResourceAsStream("/rbnz-historical-exchange-rates.xlsx");
			wb = new XSSFWorkbook(new BufferedInputStream(resourceAsStream));
		} catch (IOException e) {
			throw new IllegalStateException("could not load RBNZ rates XLS");
		}
		Sheet sheet = wb.getSheetAt(0);

		ExchangeRateInfo exchangeRateInfo = new ExchangeRateInfo();
		Map<Date, BigDecimal> exchangeRates = new HashMap<>();
		// suppose your formula is in B3
		for (int rowNum = 6;rowNum<2000;rowNum++) {
			CellReference cellReference = new CellReference(String.format("C%1$s", rowNum)); 
			Row row = sheet.getRow(cellReference.getRow());	
			if (row == null) {
				logger.info("NZD/USD Rate Loading Complete, number of rates loaded = " + exchangeRates.size());
				break;
			}
			Cell cell = row.getCell(cellReference.getCol());
			
			BigDecimal rate = new BigDecimal(cell.getNumericCellValue()+"");
			logger.trace("rate = " + rate);

			cellReference = new CellReference(String.format("A%1$s", rowNum)); 
			row = sheet.getRow(cellReference.getRow());	
			cell = row.getCell(cellReference.getCol());
			Date dateCellValue = cell.getDateCellValue();
			logger.trace("date = " + dateCellValue);
			exchangeRates.put(dateCellValue, rate);
		}
		try {
		wb.close();
		} catch (IOException e) {
			throw new IllegalStateException("could not load RBNZ rates XLS");
		}
		exchangeRateInfo.setExchangeRates(exchangeRates);
		exchangeRateInfo.setWeekdaysOnly(true);
		return exchangeRateInfo;
	}

	public static BigDecimal getUSDtoNZDRate(Date date) {
		
		return BigDecimal.ONE.divide(getNZDtoUSDRate(date), 6, RoundingMode.HALF_UP);
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

	public static BigDecimal getNZDtoUSDRate(Date date) {
		logger.info("date = " + date);
		logger.info("Day of week = " + date.getDay());
		if (exchangeRateInfo == null) {
			exchangeRateInfo = loadAllRates();
		}// TODO Auto-generated method stub
		if (exchangeRateInfo.isWeekdaysOnly()) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
			if (dayOfWeek == Calendar.SUNDAY) {
				logger.warn("Date requested for Sunday, rewinding to Friday");
				calendar.add(calendar.DAY_OF_MONTH, -2);
			} else if (dayOfWeek == Calendar.SATURDAY) {
				logger.warn("Date requested for Saturday, rewinding to Friday");
				calendar.add(calendar.DAY_OF_MONTH, -1);
			}
			date = calendar.getTime();
		}
		logger.warn("Date = " + date);
		return exchangeRateInfo.getExchangeRates().get(date);
	}

}
