package com.github.taxbeans.rules.nz;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersonalIncomeTaxCalculator {

	final static Logger logger = LoggerFactory.getLogger(PersonalIncomeTaxCalculator.class);

	public static BigDecimal calculateTax(int year, BigDecimal amount) {
		//page 46 of guide
		BigDecimal one = amount;
		RoundingMode roundingMode = RoundingMode.DOWN;
		one = one.setScale(0, roundingMode);
		if (one.compareTo(new BigDecimal("14000")) < 1) {
			BigDecimal two = one.multiply(new BigDecimal(year == 2010 ? "0.125" : (year == 2011 ? "0.115" : "0.105")));
			return two.setScale(2, roundingMode);
		} else if (one.compareTo(new BigDecimal("48000")) < 1) {
			BigDecimal four = new BigDecimal(year == 2010 ? "1750" : (year == 2011 ? "1610" : "1470"));
			BigDecimal three = one.subtract(new BigDecimal("14000"));
			BigDecimal five = three.multiply(new BigDecimal(year == 2010 ? "0.21" : (year == 2011 ? "0.1925" : "0.175")));
			BigDecimal six = four.add(five);
			return six.setScale(2, roundingMode);
		} else if (one.compareTo(new BigDecimal("70000")) < 1) {
			BigDecimal two = new BigDecimal("48000");  //constant on form
			BigDecimal three = one.subtract(two).setScale(0, roundingMode);  //form indicates to round to nearest dollar
			BigDecimal four = new BigDecimal(year == 2010 ? "8890" : (year == 2011 ? "8155" : "7420"));
			BigDecimal five = three.multiply(new BigDecimal(year == 2010 ? "0.33" : (year == 2011 ? "0.315" : "0.30")), MathContext.DECIMAL128);
			BigDecimal six = four.add(five).setScale(2, roundingMode);
			return six;
		} else {
			BigDecimal two = new BigDecimal("70000");  //constant on form
			BigDecimal three = one.subtract(two).setScale(0, roundingMode);  //form indicates to round to nearest dollar
			BigDecimal four = new BigDecimal(year == 2010 ? "16150" : (year == 2011 ? "15085" : "14020"));
			BigDecimal five = three.multiply(new BigDecimal(year == 2010 ? "0.38" : (year == 2011 ? "0.355" : "0.33")), MathContext.DECIMAL128);
			BigDecimal six = four.add(five).setScale(2, roundingMode);
			return six;
		}
	}
}
