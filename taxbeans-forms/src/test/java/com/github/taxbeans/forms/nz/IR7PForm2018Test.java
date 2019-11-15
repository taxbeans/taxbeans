package com.github.taxbeans.forms.nz;

import java.math.BigDecimal;

import org.javamoney.moneta.Money;

import com.github.taxbeans.forms.common.FormProcessor;

public class IR7PForm2018Test {

	public static void main(String[] args) {
		// MJHL IR7
		IR7PForm2018 bean = new IR7PForm2018();
		bean.setYearEnded(2018);
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
		
		FormProcessor.publishDraft(bean, 2018, "ir7p/ir7p-%1$s.pdf", IR7PFieldMapper.getPropertyToFieldMap(2018), 
				"Example Partnership", "ir7p-%1$s-%2$s-draft.pdf");
	}

}
