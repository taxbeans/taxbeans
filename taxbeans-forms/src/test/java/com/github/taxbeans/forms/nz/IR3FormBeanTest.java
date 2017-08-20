package com.github.taxbeans.forms.nz;

import java.time.LocalDate;

import org.junit.Test;

import com.github.taxbeans.model.nz.NZBankAccount;
import com.github.taxbeans.model.nz.Salutation;

public class IR3FormBeanTest {
	
	@Test
	public void test() {
		IR3FormBean bean = new IR3FormBean();
		bean.setIrdNumber("55555555");
		bean.setSalutation(Salutation.mr);
		bean.setFirstname("John");
		bean.setSurname("Smith");
		bean.setPostalAddressLine1("Postal Address Line 1");
		bean.setPostalAddressLine2("Postal Address Line 2");
		bean.setStreetAddressLine1("Street Address Line 1");
		bean.setStreetAddressLine2("Street Address Line 2");
		bean.setDateOfBirth(LocalDate.of(1996, 1, 1));
		bean.setBusinessIndustryClassificationCode("BICC");
		bean.setPhoneNumberPrefix("55");
		bean.setPhoneNumberExcludingPrefix("555555");
		NZBankAccount nzBankAccount = new NZBankAccount();
		nzBankAccount.setBankNumber("55");
		nzBankAccount.setBranchNumber("5555");
		nzBankAccount.setAccountNumber("5555555");
		nzBankAccount.setSuffix("55");
		bean.setAccount(nzBankAccount);
		bean.setIncomeAdjustmentsRequired(true);
		bean.publishDraft();
	}

}
