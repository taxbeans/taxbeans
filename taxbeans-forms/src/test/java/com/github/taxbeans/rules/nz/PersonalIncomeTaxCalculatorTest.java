package com.github.taxbeans.rules.nz;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import com.github.taxbeans.forms.utils.TaxReturnUtils;

public class PersonalIncomeTaxCalculatorTest {

	@Test
	public void test() {
		Assert.assertEquals(
				TaxReturnUtils.formatMoney(PersonalIncomeTaxCalculator.calculateTax(
						2010, new BigDecimal("70000"))),
				TaxReturnUtils.formatMoney(new BigDecimal("16150")));
		Assert.assertEquals(
				TaxReturnUtils.formatMoney(PersonalIncomeTaxCalculator.calculateTax(2010, 
						new BigDecimal("5000"))), 
				TaxReturnUtils.formatMoney(new BigDecimal("625")));
	}
}
