package com.github.taxbeans.forms.nz;

import java.math.BigDecimal;

import org.javamoney.moneta.Money;
import org.junit.Test;

import com.github.taxbeans.forms.common.FormProcessor;

public class IR7Form2022Test {

	@Test
	public void test() {
		// MJHL IR7
		IR7Form2022 bean = new IR7Form2022();
		bean.setYearEnded(2022);
		bean.setIrdNumber("888-888-888");
		bean.setFullname("Example Partnership");
		bean.setPartnershipNameLine1("Example Partnership");
		bean.setPartnershipNameLine2("Example Partnership Line 2");
		bean.setPartnershipTradingNameLine1("Example Partnership");
		bean.setPartnershipTradingNameLine2("Example Partnership Line 2");
		bean.setPostalAddressLine1("100 Queen Street");
		bean.setPostalAddressLine2("Auckland CBD");
		bean.setPhysicalAddressLine1("100 Queen Street");
		bean.setPhysicalAddressLine2("Auckland CBD");
		bean.setBicCode("Bic101");
		bean.setDaytimePhoneNumberPrefix("021");
		bean.setDaytimePhoneNumberSuffix("8888888");
		bean.setFirstReturnRadio(false);
		bean.setPartnershipCeasedRadio(false);
		bean.setSchedularPaymentsRadio(false);
		bean.setNzInterestRadio(false);
		bean.setDividendsRadio(false);
		bean.setMaoriTaxableDistributions(false);
		bean.setIncomeFromAnotherPartnership(false);
		bean.setIncomeFromAnotherLTC(false);
		bean.setOverseasIncome(false);
		bean.setBusinessIncome(true);
		bean.setRentalIncomeRadio(false);
		bean.setExpenseClaimRadio(false);
		//bean.setLaqcRadio(false);
		bean.setPartnershipOrLTCRadio(true);
		bean.setPartnershipCFCRadio(false);
		bean.setLaqcTransitionRadio(false);
		bean.setNetProfitOrLoss(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setTotalIncome(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setTotalIncomeAfterExpenses(Money.of(new BigDecimal("888.88"), "NZD"));
		//bean.setOtherIncomeRadio(false);
		
		FormProcessor.publishDraft(bean, 2022, "ir7-%1$s.pdf", IR7FieldMapper.instance(), 
				"Example Partnership", "ir7-%1$s-%2$s-draft.pdf");
	}

}
