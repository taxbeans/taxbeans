package com.github.taxbeans.forms.nz;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.javamoney.moneta.Money;
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
		bean.setPhonePrefix("55");
		bean.setPhoneNumberExcludingPrefix("555555");
		NZBankAccount nzBankAccount = new NZBankAccount();
		nzBankAccount.setBankNumber("55");
		nzBankAccount.setBranchNumber("5555");
		nzBankAccount.setBankAccountNumber("5555555");
		nzBankAccount.setBankSuffix("55");
		bean.setAccount(nzBankAccount);
		bean.setIncomeAdjustmentsRequired(true);
		bean.setFamilyTaxCreditReceived(true);
		bean.setFamilyTaxCreditAmount(Money.of(new BigDecimal("5555.55"), "NZD"));
		bean.setIncomeWithTaxDeductedReceived(true);
		bean.setTotalPAYEDeducted(Money.of(new BigDecimal("7777.77"), "NZD"));
		bean.setTotalGrossIncome(Money.of(new BigDecimal("88888.88"), "NZD"));
		bean.setAccEarnersLevy(Money.of(new BigDecimal("999.99"), "NZD"));
		bean.setIncomeNotLiableForAccEarnersLevy(Money.of(new BigDecimal("55555.55"), "NZD"));
		bean.setTotalTaxDeducted(Money.of(new BigDecimal("22222.22"), "NZD"));
		bean.setSchedularPaymentsReceived(true);
		bean.setTotalSchedularTaxDeducted(Money.of(new BigDecimal("33333.33"), "NZD"));
		bean.setTotalSchedularGrossPayments(Money.of(new BigDecimal("44444.44"), "NZD"));
		bean.setNetSchedularPayments(Money.of(new BigDecimal("11111.11"), "NZD"));
		bean.setSchedularPaymentExpenses(Money.of(new BigDecimal("12121.12"), "NZD"));
		bean.setTotalRWT(Money.of(new BigDecimal("14323.12"), "NZD"));
		bean.setTotalGrossInterest(Money.of(new BigDecimal("11312.12"), "NZD"));
		bean.setTotalDividendImputationCredits(Money.of(new BigDecimal("11312.12"), "NZD"));
		bean.setTotalDividendRWTAndPaymentsForForeignDividends(Money.of(new BigDecimal("10561.12"), "NZD"));
		bean.setTotalGrossDividends(Money.of(new BigDecimal("15242.55"), "NZD"));
		bean.setTotalMaoriAuthorityCredits(Money.of(new BigDecimal("12635.17"), "NZD"));
		bean.setTotalMaoriAuthorityDistributions(Money.of(new BigDecimal("14257.02"), "NZD"));
		bean.setTotalTaxPaidByTrustees(Money.of(new BigDecimal("10100.27"), "NZD"));
		bean.setTotalEstateOrTrustIncome(Money.of(new BigDecimal("11300.91"), "NZD"));
		bean.setTotalTaxableDistributionsFromNonComplyingTrusts(Money.of(new BigDecimal("15657.22"), "NZD"));
		bean.setTotalOverseasTaxPaid(Money.of(new BigDecimal("16999.25"), "NZD"));
		bean.setTotalOverseasIncome(Money.of(new BigDecimal("13222.10"), "NZD"));
		bean.setTotalPartnershipTaxCredits(Money.of(new BigDecimal("15657.22"), "NZD"));
		bean.setTotalActivePartnershipIncome(Money.of(new BigDecimal("11117.11"), "NZD"));
		bean.setTotalLTCtaxcredits(Money.of(new BigDecimal("9315.50"), "NZD"));
		bean.setTotalActiveLTCIncome(Money.of(new BigDecimal("11342.30"), "NZD"));
		bean.setNonAllowableDeductionsThisYear(Money.of(new BigDecimal("15657.22"), "NZD"));
		bean.setPriorYearsNonAllowableDeductionsClaimedThisYear(Money.of(new BigDecimal("11099.34"), "NZD"));
		bean.setAdjustedLTCIncome(Money.of(new BigDecimal("12545.64"), "NZD"));
		bean.setTaxCreditSubtotal(Money.of(new BigDecimal("17099.30"), "NZD"));
		bean.setIncomeSubtotal(Money.of(new BigDecimal("18224.78"), "NZD"));
		bean.setTotalShareholderEmployeeSalary(Money.of(new BigDecimal("14800.15"), "NZD"));
		bean.setNetRents(Money.of(new BigDecimal("12630.39"), "NZD"));
		bean.setSelfEmployedNetIncome(Money.of(new BigDecimal("10754.21"), "NZD"));
		bean.setTotalOtherNetIncome(Money.of(new BigDecimal("1200.60"), "NZD"));
		bean.setResidentialLandWithholdingTaxCredit(Money.of(new BigDecimal("13223.26"), "NZD"));
		bean.setTotalIncome(Money.of(new BigDecimal("18354.64"), "NZD"));
		bean.setTotalOtherExpensesClaimed(Money.of(new BigDecimal("14556.90"), "NZD"));
		bean.setIncomeAfterExpenses(Money.of(new BigDecimal("13455.86"), "NZD"));
		bean.setAmountBroughtForward(Money.of(new BigDecimal("14667.46"), "NZD"));
		bean.setAmountClaimedThisYear(Money.of(new BigDecimal("15323.75"), "NZD"));
		bean.setTaxableIncome(Money.of(new BigDecimal("14344.56"), "NZD"));
		bean.setExcessImputationCreditsBroughtForward(Money.of(new BigDecimal("14553.64"), "NZD"));
		bean.setTaxOnTaxableIncome(Money.of(new BigDecimal("14333.43"), "NZD"));
		bean.setResidualIncomeTax(Money.of(new BigDecimal("13343.65"), "NZD"));
		bean.setTaxCalculationResult(Money.of(new BigDecimal("14268.24"), "NZD"));
		bean.setRefundCopied(Money.of(new BigDecimal("2453.23"), "NZD"));
		bean.setRefundOverpayment2018(Money.of(new BigDecimal("1265.44"), "NZD"));
		bean.setRefundTransferTo2018(Money.of(new BigDecimal("13523.54"), "NZD"));
		bean.setRefundTransferToStudentLoan(Money.of(new BigDecimal("1345.24"), "NZD"));
		bean.setRefundTotal(Money.of(new BigDecimal("3248.24"), "NZD"));
		bean.setTaxPayment2018(Money.of(new BigDecimal("18258.89"), "NZD"));
		bean.setDateStart2018TaxReturn(LocalDate.of(2017, 9, 15));
		bean.setDateEnd2018TaxReturn(LocalDate.of(2018, 2, 26));
		bean.setDateStartExcludedOverseasIncome(LocalDate.of(2017, 5, 13));
		bean.setDateEndExcludedOverseasIncome(LocalDate.of(2018, 2, 16));
		bean.publishDraft();
	}

}
