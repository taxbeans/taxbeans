package com.github.taxbeans.forms.nz;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.javamoney.moneta.Money;
import org.junit.Test;

import com.github.taxbeans.forms.common.FormProcessor;
import com.github.taxbeans.model.nz.NZBankAccount;
import com.github.taxbeans.model.nz.Salutation;

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


		FormProcessor.publishDraft(bean, 2019, "ir4-%1$s.pdf", IR4FieldMapper.getPropertyToFieldMap(2019), 
	    		"Test", "ir4-%1$s-%2$s-draft.pdf");
	}

}
