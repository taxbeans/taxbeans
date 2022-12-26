package com.github.taxbeans.forms.nz;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.javamoney.moneta.Money;
import org.junit.Test;

import com.github.taxbeans.forms.common.FormProcessor;
import com.github.taxbeans.model.nz.NZBankAccount;
import com.github.taxbeans.model.nz.Salutation;

public class IR3Form2020Test {
	
	@Test
	public void test() {
		IR3Form2020 bean = new IR3Form2020();
		bean.setIrdNumber("55555555");
		bean.setSalutation(Salutation.mr);
		bean.setFirstname("JOHN");
		bean.setSurname("SMITH");
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
		bean.setInterestFromNZReceived(true);
		bean.setDividendsFromNZReceived(true);
		bean.setTaxableDistributionsFromMaoriAuthorityReceived(true);
		bean.setTrustOrEstateIncomeFromNZReceived(true);
		bean.setOverseasIncomeReceived(true);
		bean.setPartnershipIncomeReceived(true);
		bean.setTaxableDistributionsFromMaoriAuthorityReceived(true);
		bean.setIncomeFromLTCReceived(true);
		bean.setTrustOrEstateIncomeFromNZReceived(true);
		bean.setSalaryShareholderEmployeeNotTaxed(true);
		bean.setRentsReceived(true);
		bean.setIncomeFromSelfEmploymentReceived(true);
		bean.setIncomeOtherReceived(true);
		bean.setExpensesOtherReceived(true);
		bean.setNetLossesBroughtForwardClaimed(true);
		bean.setIndependentEarnerTaxCreditEligible(true);
		bean.setExcessImputationCreditsBroughtForwardEligible(true);
		bean.setEarlyPaymentDiscountEntitled(true);
		bean.setTransferRefundToSomeoneElsesIncomeTaxAccountAssociated(true);
		bean.setTransferRefundToSomeoneElsesStudentLoanAssociated(true);
		bean.setResidualIncomeTaxDebitHigherThan2500Dollars(true);
		bean.setDisclosureRequiredToHoldRightsDuringIncomeYear(true);
		bean.setReturnForPartYear(true);
		bean.setTaxOnTaxableIncomeIsCredit(true);
		bean.setResidualIncomeTaxIsCredit(true);
		bean.setInterestFromEligibleEntitiesReceived(true);
		bean.setDividendsFromEligibleEntitiesReceived(true);
		bean.setSuperannuationSchemeIncomeFromOverseas(true);
		bean.setInterestFromEligibleEntitiesReceived(true);
		bean.setUnpaidMajorWorkingShareholderWfFTCELigible(true);
		bean.setTotalSchedularTaxDeducted(bean.getTotalTaxDeducted());
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
		bean.setRefundCopied(bean.getTaxCalculationResult());
		bean.setRefundOverpaymentCurrentYear(Money.of(new BigDecimal("1265.44"), "NZD"));
		bean.setRefundTransferToCurrentYear(Money.of(new BigDecimal("13523.54"), "NZD"));
		bean.setRefundTransferToStudentLoan(Money.of(new BigDecimal("1345.24"), "NZD"));
		bean.setRefundIsTransferredToSomeoneElsesStudentLoan(true);
		bean.setTransferRefundToSomeoneElsesIncomeTaxAccountAssociated(true);
		bean.setRefundTotal(Money.of(new BigDecimal("3248.24"), "NZD"));
		bean.setTaxPaymentCurrentYear(Money.of(new BigDecimal("18258.89"), "NZD"));
		bean.setDateStartCurrentYearTaxReturn(LocalDate.of(2019, 9, 15));
		bean.setDateEndCurrentYearTaxReturn(LocalDate.of(2020, 2, 26));
		bean.setDateStartExcludedOverseasIncome(LocalDate.of(2019, 5, 13));
		bean.setDateEndExcludedOverseasIncome(LocalDate.of(2020, 2, 16));
		bean.setReasonForTaxReturnPartYear(2);
		bean.setAlternativePersonFirstNamesCompletedReturn("JOHN JEFF");
		bean.setAlternativePersonSurnameCompletedReturn("DOE");
		bean.setOtherIncomePayer("JOHN JEFF JOE");
		bean.setOtherIncomeType("SALARY");
		bean.setTaxCreditValue(Money.of(new BigDecimal("234.67"), "NZD"));;
		bean.setTaxCreditQualifyingMonthsNumber("7");
		bean.setRefundCopiedPlusOverpaymentCurrentYear(Money.of(new BigDecimal("3618.67"), "NZD"));;
		bean.setTaxPaymentSEROptionCurrentYear("S");
		bean.setRefundIsTransferredToCurrentYear(true);
		bean.setRefundIsTransferredToStudentLoan(true);
		bean.setRefundIsTransferredToSomeoneElsesTaxAccount(true);
		bean.setRefundIsTransferredToSomeoneElsesStudentLoan(true);
		bean.setRefundIsTransferredToOther(false);
		bean.setRefundOtherTaxAccountReceiverName("JAKE JEFF DOE");
		bean.setRefundOtherTaxAccountReceiverIRD("55 555555");
		bean.setRefundOtherTaxAccountReceiverAmount(Money.of(new BigDecimal("12231.53"), "NZD"));;;
		bean.setRefundOtherTaxAccountReceiverYearEnded31March("2020");
		bean.setRefundOtherStudentLoanReceiverName("JAKE JEFF DOE");
		bean.setRefundOtherStudentLoanReceiverIRD("55 555555");
		bean.setRefundOtherStudentLoanReceiverAmount(Money.of(new BigDecimal("342.34"), "NZD"));;;
		bean.setMinusSignForSchedularPaymentsExpenses("-");
		bean.setMinusSignForSchedularNetPayments("-");
		bean.setMinusSignForTotalGrossInterestReceivedFromEligibleEntities("-");
		bean.setMinusSignForNZTotalEstateOrCompliantTrustIncome("-");
		bean.setMinusSignForNZTotalTaxableDistrbutionsNonCompliantTrust("-");
		bean.setMinusSignForTotalOverseasIncome("-");
		bean.setMinusSignForTotalActivePartnershipIncome("-");
		bean.setMinusSignForTotalActiveLTCIncome("-");
		bean.setMinusSignForAdjustedLTCIncome("-");
		bean.setMinusSignForIncomeSubtotal("-");
		bean.setMinusSignForTotalShareholderEmployeeSalary("-");
		bean.setMinusSignForNetRents("-");
		bean.setMinusSignForSelfEmployedNetIncome("-");
		bean.setMinusSignForTotalOtherNetIncome("-");
		bean.setMinusSignForRLWTTaxCredit("-");
		bean.setMinusSignForTotalIncome("-");
		bean.setMinusSignForIncomeAfterExpenses("-");
		bean.setMinusSignForTaxableIncome("-");
		FormProcessor.publishDraft(bean, 2020, "ir3-%1$s.pdf", IR3FieldMapper.instance(), 
	    		"Test", "ir3-%1$s-%2$s-draft.pdf");
	}

}
