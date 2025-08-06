package com.github.taxbeans.forms.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Locale;

import org.javamoney.moneta.Money;

import com.github.taxbeans.model.Transaction;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TaxReturnUtils {

	public static BigDecimal allocatePercentage(BigDecimal amount, BigDecimal percentage) {
		return amount.multiply(percentage).divide(new BigDecimal("100"), MathContext.DECIMAL128);
	}

	public static String formatDate(LocalDate date) {
		return date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
	}

	public static String formatDayString(String s) {
		if (s.length() == 1)
			return "0" + s;
		return s;
	}

	public static String formatDollarsField(BigDecimal amount) {
		if (BigDecimal.ZERO.compareTo(amount) == 0)
			return "0";
		return String.valueOf(amount.setScale(0, RoundingMode.HALF_UP));
	}

	public static String formatExportMoney(BigDecimal amount) {
		String result = String.format(Locale.ENGLISH, "%,.2f", amount.setScale(2, RoundingMode.HALF_UP)); 
		if (result.contains(",")) {
			return "\"" + result + "\"";
		}
		return result;
	}

	public static String formatExportMoneyWithSymbol(BigDecimal amount) {
		String result = (amount.signum() < 0 ? "-" : "") + "NZ$" + String.format(Locale.ENGLISH, "%,.2f", amount.setScale(2, RoundingMode.HALF_UP));
		if (result.contains(",")) {
			return "\"" + result + "\"";
		}
		return result;
	}

	public static String formatMoney(BigDecimal amount) {
		return String.valueOf(amount.setScale(2, RoundingMode.HALF_UP));
	}

	public static String formatMoneyField(BigDecimal amount) {
		if (BigDecimal.ZERO.compareTo(amount) == 0)
			return "000";
		String prefix = "";
		if (new BigDecimal("0.1").compareTo(amount) > 0 && amount.signum() > 0) {
			prefix =  "00";
		} else if (BigDecimal.ONE.compareTo(amount) > 0 && amount.signum() > 0) {
			prefix =  "0";
		}
		return prefix + String.valueOf(amount.multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP));
	}

	public static String formatMoneyFieldWithCommas(BigDecimal amount) {
		if (amount.signum() == 0)
			return "000";
		boolean negative = amount.signum() < 0;
		String result = String.valueOf(amount.abs().multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP));
		if (result.length() > 5) {
			result = result.substring(0,  result.length()-5) + "," + result.substring(result.length()-5);
		}
		return (negative ? "-" : "") + result;
	}

	public static String formatMoneyFloor(BigDecimal amount) {
		return String.valueOf(amount.setScale(2, RoundingMode.FLOOR));
	}


	public static String formatNegativeMoneyField(BigDecimal amount) {
		return "-" + TaxReturnUtils.formatMoneyField(amount);
	}

	public static boolean isWithinThisTaxYear(int year, LocalDate firstDateOfAccessibleIncome) {
		return firstDateOfAccessibleIncome.compareTo(LocalDate.of(year-1, 3, 31)) > 0 &&
				firstDateOfAccessibleIncome.compareTo(LocalDate.of(year, 4, 1)) < 0;
	}
	
	// N.B. that this only uses the amount from the first split
	public static String formatAsOFXString(Transaction transaction) {
		/*
		              <STMTTRN>
              <TRNTYPE>OTHER
              <DTPOSTED>20070709
              <DTUSER>20070709
              <TRNAMT>100.00
              <FITID>980309001
                <CHECKNUM>1025
              <NAME>John Hancock M000010 STO
            </STMTTRN>
		 */
		StringBuilder sb = new StringBuilder();
		sb.append("<STMTTRN>\n");
		sb.append("<TRNTYPE>OTHER\n");
		sb.append(String.format("<DTPOSTED>%1$s\n", TaxReturnUtils.formatDate(transaction.getDate())));
		sb.append(String.format("<DTUSER>%1$s\n", TaxReturnUtils.formatDate(transaction.getDate())));
		sb.append(String.format("<TRNAMT>%1$s\n", TaxReturnUtils.formatMoney(transaction.getAccountEntries().get(0).getAmount())));
		sb.append("<FITID>" + System.currentTimeMillis() + "\n"); //980310001\n");
		sb.append(String.format("<NAME>%1$s\n", transaction.getName()));
		sb.append(String.format("<MEMO>%1$s\n", transaction.getMemo()));
		sb.append("</STMTTRN>\n");
		return sb.toString();
	}

	private static String formatDate(ZonedDateTime date) {
		ZoneId zone = TaxRegion.getDefault().getZone();
		LocalDate localDate = ZonedDateTime.ofInstant(date.toInstant(), zone).toLocalDate();
		return formatDate(localDate);
	}

	public static String formatMoneyField(Money amount) {
		return TaxReturnUtils.formatMoneyField(amount.getNumberStripped());
	}
	
	public static String formatDollarsField(Money amount) {
		return TaxReturnUtils.formatDollarsField(amount.getNumberStripped());
	}
	
	public static String formatDollarsFieldRounded(Money amount) {
		return TaxReturnUtils.formatDollarsField(amount.getNumberStripped().setScale(0, RoundingMode.HALF_UP));
	}
}
