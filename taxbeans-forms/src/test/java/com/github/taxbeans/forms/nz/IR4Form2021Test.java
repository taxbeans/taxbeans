package com.github.taxbeans.forms.nz;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.javamoney.moneta.Money;
import org.junit.Test;

import com.github.taxbeans.forms.common.FormProcessor;
import com.github.taxbeans.model.nz.NZBankAccount;

public class IR4Form2021Test {
	
	@Test
	public void test() {
		IR4Form2021 bean = new IR4Form2021();
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
		bean.setTotalTaxCredits(Money.of(new BigDecimal("5.55"), "NZD"));
		bean.setBusinessIncome(true);
		bean.setBusinessNetProfit(Money.of(new BigDecimal("555.55"), "NZD"));
		bean.setOtherIncome(false);
		bean.setNetProfitBeforeDonations(bean.getBusinessNetProfit());
		bean.setDonations(false);
		bean.setNetProfitAfterDonations(bean.getBusinessNetProfit());
		bean.setNetProfitAfterLossesBroughtForward(bean.getBusinessNetProfit());
		bean.setTaxableIncome(bean.getBusinessNetProfit());
		BigDecimal businessNetProfitOmitCents = bean.getBusinessNetProfit().getNumberStripped().setScale(0, RoundingMode.FLOOR);
		Money copyOfTaxableIncome = Money.of(businessNetProfitOmitCents, "NZD");
		bean.setCopyOfTaxableIncome(
				copyOfTaxableIncome);
		bean.setTotalTaxPayable(copyOfTaxableIncome.multiply(new BigDecimal("0.28")));
		
		BigDecimal overseasTaxPaid = BigDecimal.ZERO;
		bean.setOverseasTaxPaid(Money.of(overseasTaxPaid, "NZD"));
		bean.setBox29D(bean.getTotalTaxPayable().subtract(bean.getOverseasTaxPaid()));
		bean.setForeignInvestorTaxCredit(Money.of(BigDecimal.ZERO, "NZD"));
		bean.setBox29F(bean.getBox29D().subtract(bean.getForeignInvestorTaxCredit()));
		bean.setCopyOfTotalImputationCredits(Money.of(BigDecimal.ZERO, "NZD"));
		bean.setBox29H(bean.getBox29F().subtract(bean.getCopyOfTotalImputationCredits()));
		bean.setCopyOfTotalTaxCredits(bean.getTotalTaxCredits());
		bean.setCopyOfRLWTCredit(Money.of(BigDecimal.ZERO, "NZD"));
		bean.setResidualIncomeTax(bean.getBox29H().subtract(bean.getCopyOfTotalTaxCredits()
				.subtract(bean.getCopyOfRLWTCredit())));
		bean.setCreditOrDebit(true);
		bean.setProvisionalTaxPaid(Money.of(BigDecimal.ZERO, "NZD"));
		bean.setTaxAmountOwed(bean.getResidualIncomeTax().subtract(bean.getProvisionalTaxPaid()));
		bean.setRefundOrTaxToPay(true);
		bean.setProvisionalTaxOption("S");
		bean.setProvisionalTaxDue(Money.of(BigDecimal.ZERO, "NZD"));
		bean.setLowestEconomicInterests("10000");
		bean.setShareholder1IrdNumber("555555555");
		bean.setShareholder1Remuneration(Money.of(BigDecimal.ZERO, "NZD"));
		bean.setShareholder1ValueOfLoans(Money.of(BigDecimal.ZERO, "NZD"));
		bean.setShareholder1CurrentAccountBalance(Money.of(BigDecimal.ZERO, "NZD"));
		bean.setShareholder1LossOffsets(Money.of(BigDecimal.ZERO, "NZD"));
		bean.setShareholder1SubventionPayments(Money.of(BigDecimal.ZERO, "NZD"));
		bean.setShareholder1AimTaxCredits(Money.of(BigDecimal.ZERO, "NZD"));
		bean.setShareholder2IrdNumber("555555555");
		bean.setShareholder2Remuneration(Money.of(BigDecimal.ZERO, "NZD"));
		bean.setShareholder2ValueOfLoans(Money.of(BigDecimal.ZERO, "NZD"));
		bean.setShareholder2CurrentAccountBalance(Money.of(BigDecimal.ZERO, "NZD"));
		bean.setShareholder2LossOffsets(Money.of(BigDecimal.ZERO, "NZD"));
		bean.setShareholder2SubventionPayments(Money.of(BigDecimal.ZERO, "NZD"));	
		bean.setShareholder2AimTaxCredits(Money.of(BigDecimal.ZERO, "NZD"));
		
		FormProcessor.publishDraft(bean, 2021, "ir4-%1$s.pdf", IR4FieldMapper.instance(), 
	    		"Test", "ir4-%1$s-%2$s-draft.pdf");
	}

}
