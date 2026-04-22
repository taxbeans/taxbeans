package com.github.taxbeans.forms.nz;

import java.io.File;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

import org.javamoney.moneta.Money;
import org.junit.Assert;
import org.junit.Test;

import com.github.taxbeans.forms.common.FormProcessor;

public class IR3GForm2020Test {

	@Test
	public void testFillTaxCalculationWorksheetPage44() {
		IR3GForm2020 bean = new IR3GForm2020();
		bean.setTaxableIncome(Money.of(new BigDecimal("85000.00"), "NZD"));
		bean.setTaxOnTaxableIncome(Money.of(new BigDecimal("17520.00"), "NZD"));
		bean.setTaxCreditFromBox32(Money.of(new BigDecimal("1200.00"), "NZD"));
		bean.setNetTaxAfterCredits(Money.of(new BigDecimal("16320.00"), "NZD"));
		bean.setOverseasTaxPaid(Money.of(new BigDecimal("400.00"), "NZD"));
		bean.setNetTaxAfterOverseasCredits(Money.of(new BigDecimal("15920.00"), "NZD"));
		bean.setImputationCredits(Money.of(new BigDecimal("500.00"), "NZD"));
		bean.setExcessImputationCreditsBroughtForward(Money.of(new BigDecimal("100.00"), "NZD"));
		bean.setTotalImputationCredits(Money.of(new BigDecimal("600.00"), "NZD"));
		bean.setTaxAfterImputationCredits(Money.of(new BigDecimal("15320.00"), "NZD"));
		bean.setTaxCreditSubtotal(Money.of(new BigDecimal("120.00"), "NZD"));
		bean.setResidualIncomeTax(Money.of(new BigDecimal("15200.00"), "NZD"));
		bean.setResidualIncomeTaxIsCredit(false); // debit
		bean.setProvisionalTaxPaid(Money.of(new BigDecimal("14000.00"), "NZD"));
		bean.setTaxCalculationResult(Money.of(new BigDecimal("1200.00"), "NZD"));
		bean.setTaxCalculationResultIsRefund(false); // tax to pay

		IRFieldMapper annotationOnlyMapper = new IRFieldMapper() {
			@Override
			public Map<IRFieldMapKey, String> getPropertyToFieldMap(int year) {
				return Collections.emptyMap();
			}
		};

		File output = FormProcessor.publishDraft(
				bean,
				2020,
				"ir3g-%1$s.pdf",
				annotationOnlyMapper,
				"Test User",
				"ir3g-%1$s-%2$s-draft.pdf");

		Assert.assertTrue(output.exists());
		Assert.assertTrue(output.length() > 0);
	}
}
