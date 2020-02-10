package com.github.taxbeans.forms.nz;

import java.math.BigDecimal;

import org.javamoney.moneta.Money;
import org.junit.Test;

import com.github.taxbeans.forms.common.FormProcessor;
import com.github.taxbeans.model.nz.NZBankAccount;

public class IR4Form2019Test {
	
	@Test
	public void test() {
		IR4Form2019 bean = new IR4Form2019();
		bean.setIrdNumber("55555555");
		NZBankAccount nzBankAccount = new NZBankAccount();
		nzBankAccount.setBankNumber("55");
		nzBankAccount.setBranchNumber("5555");
		nzBankAccount.setBankAccountNumber("5555555");
		nzBankAccount.setBankSuffix("55");
		
		bean.setCompanyTradingNameLine1("Company Name Line 1");
		bean.setPostalAddressLine1("Postal address 1");
		bean.setPostalAddressLine2("Postal address 2");
		bean.setStreetAddressLine1("Street address 1");
		bean.setStreetAddressLine2("Street address 2");
		bean.setBicCode("5555555");
		bean.setPhonePrefix("555");
		bean.setPhoneNumber("5555555555");
		bean.setBankAccount(nzBankAccount);
		bean.setNonResident(false);
		bean.setImputationReturnIncluded(true);
		bean.setImputationMonetaryEntries(false);
		bean.setCompanyCeased(false);
		bean.setSchedularPayments(false);
		bean.setNzInterest(false);
		bean.setNzDividends(false);
		bean.setTotalTaxCredits(Money.of(BigDecimal.ZERO, "NZD"));
		bean.setBusinessIncome(true);
		bean.setBusinessNetProfit(Money.of(new BigDecimal("555.55"), "NZD"));

		FormProcessor.publishDraft(bean, 2019, "ir4-%1$s.pdf", IR4FieldMapper.getPropertyToFieldMap(2019), 
	    		"Test", "ir4-%1$s-%2$s-draft.pdf");
	}

}
