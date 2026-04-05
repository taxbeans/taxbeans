package com.github.taxbeans.forms.nz;

import java.math.BigDecimal;

import org.javamoney.moneta.Money;
import org.junit.Test;
import static org.junit.Assert.*;

import com.github.taxbeans.forms.common.FormProcessor;

public class IR10Form2026Test {

	private static final int FINANCIAL_YEAR = 2026;

	@Test
	public void testBeanInstantiation() {
		IR10Form2026 bean = new IR10Form2026();
		assertNotNull(bean);
		assertEquals(2026, bean.getYear());
		
		// Test basic setup
		bean.setYearEnded(FINANCIAL_YEAR);
		bean.setIrdNumber("888-888-888");
		bean.setFullname("Example Partnership");
		bean.setMultipleActivityRadio(false);
		bean.setGrossIncome(Money.of(new BigDecimal("888.88"), "NZD"));
		
		assertEquals(FINANCIAL_YEAR, bean.getYearEnded());
		assertEquals("888888888", bean.getIrdNumber()); // dashes are stripped
		assertEquals("Example Partnership", bean.getFullName());
	}
}
