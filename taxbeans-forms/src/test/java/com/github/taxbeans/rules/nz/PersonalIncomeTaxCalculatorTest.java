package com.github.taxbeans.rules.nz;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import com.github.taxbeans.forms.utils.TaxReturnUtils;

public class PersonalIncomeTaxCalculatorTest {

	@Test
	public void test() {
		int year = 2010;
		Assert.assertEquals(
				TaxReturnUtils.formatMoney(PersonalIncomeTaxCalculator.calculateTax(
						year, new BigDecimal("80000"))),
				TaxReturnUtils.formatMoney(new BigDecimal("19950")));
		Assert.assertEquals(
				TaxReturnUtils.formatMoney(PersonalIncomeTaxCalculator.calculateTax(
						year, new BigDecimal("70000"))),
				TaxReturnUtils.formatMoney(new BigDecimal("16150")));
		Assert.assertEquals(
				TaxReturnUtils.formatMoney(PersonalIncomeTaxCalculator.calculateTax(
						year, new BigDecimal("40000"))),
				TaxReturnUtils.formatMoney(new BigDecimal("7210")));
		Assert.assertEquals(
				TaxReturnUtils.formatMoney(PersonalIncomeTaxCalculator.calculateTax(year, 
						new BigDecimal("5000"))), 
				TaxReturnUtils.formatMoney(new BigDecimal("625")));
		
		year = 2011;
		Assert.assertEquals(
				TaxReturnUtils.formatMoney(PersonalIncomeTaxCalculator.calculateTax(
						year, new BigDecimal("80000"))),
				TaxReturnUtils.formatMoney(new BigDecimal("18635")));
		Assert.assertEquals(
				TaxReturnUtils.formatMoney(PersonalIncomeTaxCalculator.calculateTax(
						year, new BigDecimal("70000"))),
				TaxReturnUtils.formatMoney(new BigDecimal("15085")));
		Assert.assertEquals(
				TaxReturnUtils.formatMoney(PersonalIncomeTaxCalculator.calculateTax(
						year, new BigDecimal("40000"))),
				TaxReturnUtils.formatMoney(new BigDecimal("6615")));
		Assert.assertEquals(
				TaxReturnUtils.formatMoney(PersonalIncomeTaxCalculator.calculateTax(year, 
						new BigDecimal("5000"))), 
				TaxReturnUtils.formatMoney(new BigDecimal("575")));
		
		year = 2012;
		Assert.assertEquals(
				TaxReturnUtils.formatMoney(PersonalIncomeTaxCalculator.calculateTax(
						year, new BigDecimal("80000"))),
				TaxReturnUtils.formatMoney(new BigDecimal("17320")));
		Assert.assertEquals(
				TaxReturnUtils.formatMoney(PersonalIncomeTaxCalculator.calculateTax(
						year, new BigDecimal("70000"))),
				TaxReturnUtils.formatMoney(new BigDecimal("14020")));
		Assert.assertEquals(
				TaxReturnUtils.formatMoney(PersonalIncomeTaxCalculator.calculateTax(
						year, new BigDecimal("40000"))),
				TaxReturnUtils.formatMoney(new BigDecimal("6020")));
		Assert.assertEquals(
				TaxReturnUtils.formatMoney(PersonalIncomeTaxCalculator.calculateTax(year, 
						new BigDecimal("5000"))), 
				TaxReturnUtils.formatMoney(new BigDecimal("525")));
	}
}
