package com.github.taxbeans.forms.nz;

import java.math.BigDecimal;

import org.javamoney.moneta.Money;
import org.junit.Test;

import com.github.taxbeans.forms.common.FormProcessor;

public class IR7PForm2019Test {

	@Test
	public void test() {
		IR7PForm2019 bean = new IR7PForm2019();
		bean.setYearEnded(2019);
		bean.setIrdNumber("888-888-888");
		bean.setFullname("Example Partnership");
		bean.setPartnershipName("Example Partnership Name");
		bean.setPartnersName1("Jack Johnson");
		bean.setPartnersIRDNumber1("888-888-881");
		bean.setPartnersProportion1(new BigDecimal("99"));
		bean.setPartnersInterest1(Money.of(new BigDecimal("88.88"), "NZD"));
		bean.setPartnersDividends1(Money.of(new BigDecimal("88.88"), "NZD"));
		bean.setPartnersMA1(Money.of(new BigDecimal("88.88"), "NZD"));
		bean.setPartnersOA1(Money.of(new BigDecimal("88.88"), "NZD"));
		bean.setPartnersRentalIncome1(Money.of(new BigDecimal("88.88"), "NZD"));
		bean.setPartnersPassiveIncome1(Money.of(new BigDecimal("88.88"), "NZD"));
		bean.setPartnersOtherIncome1(Money.of(new BigDecimal("88.88"), "NZD"));
		bean.setPartnersTotalIncome1(Money.of(new BigDecimal("88.88"), "NZD"));
		bean.setPartnersExtinguishedLosses1(Money.of(new BigDecimal("88.88"), "NZD"));
		bean.setPartnersOverseasTaxPaid1(Money.of(new BigDecimal("88.88"), "NZD"));
		bean.setPartnersImputationCredits1(Money.of(new BigDecimal("88.88"), "NZD"));
		bean.setPartnersOtherTaxCredits1(Money.of(new BigDecimal("88.88"), "NZD"));
		
		bean.setPartnersName2("Jay Johnson");
		bean.setPartnersIRDNumber2("888-888-882");
		bean.setPartnersProportion2(new BigDecimal("1"));
		bean.setPartnersInterest2(Money.of(new BigDecimal("80.88"), "NZD"));
		bean.setPartnersDividends2(Money.of(new BigDecimal("8.88"), "NZD"));
		bean.setPartnersMA2(Money.of(new BigDecimal("80.88"), "NZD"));
		bean.setPartnersOA2(Money.of(new BigDecimal("8.88"), "NZD"));
		bean.setPartnersRentalIncome2(Money.of(new BigDecimal("8.88"), "NZD"));
		bean.setPartnersPassiveIncome2(Money.of(new BigDecimal("8.88"), "NZD"));
		bean.setPartnersOtherIncome2(Money.of(new BigDecimal("88.88"), "NZD"));
		bean.setPartnersTotalIncome2(Money.of(new BigDecimal("88.88"), "NZD"));
		bean.setPartnersExtinguishedLosses2(Money.of(new BigDecimal("88.88"), "NZD"));
		bean.setPartnersOverseasTaxPaid2(Money.of(new BigDecimal("88.88"), "NZD"));
		bean.setPartnersImputationCredits2(Money.of(new BigDecimal("88.88"), "NZD"));
		bean.setPartnersOtherTaxCredits2(Money.of(new BigDecimal("88.88"), "NZD"));
		
		FormProcessor.publishDraft(bean, 2019, "ir7p/ir7p-%1$s.pdf", IR7PFieldMapper.instance(), 
				"Example Partnership", "ir7p-%1$s-%2$s-draft.pdf");
	}

}
