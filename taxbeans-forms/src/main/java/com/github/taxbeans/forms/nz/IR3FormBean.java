package com.github.taxbeans.forms.nz;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDNonTerminalField;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.taxbeans.exception.TaxBeansException;
import com.github.taxbeans.forms.IncludeFormatSpacing;
import com.github.taxbeans.forms.OmitCents;
import com.github.taxbeans.forms.RightAlign;
import com.github.taxbeans.forms.UseChildFields;
import com.github.taxbeans.forms.UseDayMonthYear;
import com.github.taxbeans.forms.UseTrueFalseMappings;
import com.github.taxbeans.forms.UseValueMappings;
import com.github.taxbeans.forms.utils.LocalDateUtils;
import com.github.taxbeans.forms.utils.TaxReturnUtils;
import com.github.taxbeans.model.nz.NZBankAccount;
import com.github.taxbeans.model.nz.Salutation;
import com.github.taxbeans.pdf.PDFAlignment;
import com.github.taxbeans.pdf.PDFUtils;

public class IR3FormBean {

	final Logger logger = LoggerFactory.getLogger(IR3FormBean.class);

	private int year = 2017;

	@RightAlign(9)
	private String irdNumber;

	@UseValueMappings
	private Salutation salutation;

	private String firstname;

	private String surname;

	private String postalAddressLine1;

	private String postalAddressLine2;

	private String streetAddressLine1;

	private String streetAddressLine2;

	@UseDayMonthYear
	private LocalDate dateOfBirth;

	private String businessIndustryClassificationCode;

	private String phonePrefix;

	private String phoneNumberExcludingPrefix;

	@UseChildFields
	private NZBankAccount bankAccount;

	@UseTrueFalseMappings
	private boolean incomeAdjustmentsRequired;

	@UseTrueFalseMappings
	private boolean familyTaxCreditReceived;

	@RightAlign(11)
	private Money familyTaxCreditAmount;

	@UseTrueFalseMappings
	private boolean incomeWithTaxDeductedReceived;

	@RightAlign(11)
	private Money totalPAYEDeducted;

	@RightAlign(11)
	private Money totalGrossIncome;

	@RightAlign(11)
	private Money accEarnersLevy;

	@RightAlign(11)
	private Money incomeNotLiableForAccEarnersLevy;

	@RightAlign(11)
	private Money totalTaxDeducted;
	
	@UseTrueFalseMappings
	private boolean schedularPaymentsReceived;
	
	@UseTrueFalseMappings
	private boolean interestFromNZReceived;
	
	@UseTrueFalseMappings
	private boolean dividendsFromNZReceived;

	@UseTrueFalseMappings
	private boolean taxableDistributionsFromMaoriAuthorityReceived;
	
	@UseTrueFalseMappings
	private boolean trustOrEstateIncomeFromNZReceived;
	
	@UseTrueFalseMappings
	private boolean overseasIncomeReceived;

	@UseTrueFalseMappings
	private boolean partnershipIncomeReceived;

	@UseTrueFalseMappings
	private boolean incomeFromLTCReceived;

	@UseTrueFalseMappings
	private boolean salaryShareholderEmployeeNotTaxed;

	@UseTrueFalseMappings
	private boolean rentsReceived;

	@UseTrueFalseMappings
	private boolean taxOnTaxableIncomeIsCredit;
	
	@UseTrueFalseMappings
	private boolean interestFromEligibleEntitiesReceived;
	
	@UseTrueFalseMappings
	private boolean unpaidMajorWorkingShareholderWfFTCELigible;
	
	@UseValueMappings
	private int reasonForTaxReturnPartYear;
	
	public int getReasonForTaxReturnPartYear() {
		return reasonForTaxReturnPartYear;
	}

	public void setReasonForTaxReturnPartYear(int reasonForTaxReturnPartYear) {
		this.reasonForTaxReturnPartYear = reasonForTaxReturnPartYear;
	}

	public boolean isUnpaidMajorWorkingShareholderWfFTCELigible() {
		return unpaidMajorWorkingShareholderWfFTCELigible;
	}

	public void setUnpaidMajorWorkingShareholderWfFTCELigible(boolean unpaidMajorWorkingShareholderWfFTCELigible) {
		this.unpaidMajorWorkingShareholderWfFTCELigible = unpaidMajorWorkingShareholderWfFTCELigible;
	}


	@UseTrueFalseMappings
	private boolean superannuationSchemeIncomeFromOverseas;
	
	
	public boolean isSuperannuationSchemeIncomeFromOverseas() {
		return superannuationSchemeIncomeFromOverseas;
	}

	public void setSuperannuationSchemeIncomeFromOverseas(boolean superannuationSchemeIncomeFromOverseas) {
		this.superannuationSchemeIncomeFromOverseas = superannuationSchemeIncomeFromOverseas;
	}


	@UseTrueFalseMappings
	private boolean shareholderEmployeeSalaryOnlyInFuture;
	
	public boolean isShareholderEmployeeSalaryOnlyInFuture() {
		return shareholderEmployeeSalaryOnlyInFuture;
	}

	public void setShareholderEmployeeSalaryOnlyInFuture(boolean shareholderEmployeeSalaryOnlyInFuture) {
		this.shareholderEmployeeSalaryOnlyInFuture = shareholderEmployeeSalaryOnlyInFuture;
	}


	@UseTrueFalseMappings
	private boolean dividendsFromEligibleEntitiesReceived;
	
	public boolean isDividendsFromEligibleEntitiesReceived() {
		return dividendsFromEligibleEntitiesReceived;
	}

	public void setDividendsFromEligibleEntitiesReceived(boolean dividendsFromEligibleEntitiesReceived) {
		this.dividendsFromEligibleEntitiesReceived = dividendsFromEligibleEntitiesReceived;
	}

	public boolean isInterestFromEligibleEntitiesReceived() {
		return interestFromEligibleEntitiesReceived;
	}

	public void setInterestFromEligibleEntitiesReceived(boolean interestFromEligibleEntitiesReceived) {
		this.interestFromEligibleEntitiesReceived = interestFromEligibleEntitiesReceived;
	}

	public boolean isTaxOnTaxableIncomeIsCredit() {
		return taxOnTaxableIncomeIsCredit;
	}

	public void setTaxOnTaxableIncomeIsCredit(boolean taxOnTaxableIncomeIsCredit) {
		this.taxOnTaxableIncomeIsCredit = taxOnTaxableIncomeIsCredit;
	}


	@UseTrueFalseMappings
	private boolean residualIncomeTaxIsCredit;

	public boolean isResidualIncomeTaxIsCredit() {
		return residualIncomeTaxIsCredit;
	}

	public void setResidualIncomeTaxIsCredit(boolean residualIncomeTaxIsCredit) {
		this.residualIncomeTaxIsCredit = residualIncomeTaxIsCredit;
	}

	public boolean isRentsReceived() {
		return rentsReceived;
	}

	public void setRentsReceived(boolean rentsReceived) {
		this.rentsReceived = rentsReceived;
	}


	@UseTrueFalseMappings
	private boolean incomeFromSelfEmploymentReceived;


	public boolean isIncomeFromSelfEmploymentReceived() {
		return incomeFromSelfEmploymentReceived;
	}

	public void setIncomeFromSelfEmploymentReceived(boolean incomeFromSelfEmploymentReceived) {
		this.incomeFromSelfEmploymentReceived = incomeFromSelfEmploymentReceived;
	}


	@UseTrueFalseMappings
	private boolean incomeOtherReceived;


	public boolean isIncomeOtherReceived() {
		return incomeOtherReceived;
	}

	public void setIncomeOtherReceived(boolean incomeOtherReceived) {
		this.incomeOtherReceived = incomeOtherReceived;
	}


	@UseTrueFalseMappings
	private boolean expensesOtherReceived;


	public boolean isExpensesOtherReceived() {
		return expensesOtherReceived;
	}

	public void setExpensesOtherReceived(boolean expensesOtherReceived) {
		this.expensesOtherReceived = expensesOtherReceived;
	}


	@UseTrueFalseMappings
	private boolean netLossesBroughtForwardClaimed;


	public boolean isNetLossesBroughtForwardClaimed() {
		return netLossesBroughtForwardClaimed;
	}

	public void setNetLossesBroughtForwardClaimed(boolean netLossesBroughtForwardClaimed) {
		this.netLossesBroughtForwardClaimed = netLossesBroughtForwardClaimed;
	}


	@UseTrueFalseMappings
	private boolean independentEarnerTaxCreditEligible;


	public boolean isIndependentEarnerTaxCreditEligible() {
		return independentEarnerTaxCreditEligible;
	}

	public void setIndependentEarnerTaxCreditEligible(boolean independentEarnerTaxCreditEligible) {
		this.independentEarnerTaxCreditEligible = independentEarnerTaxCreditEligible;
	}


	@UseTrueFalseMappings
	private boolean excessImputationCreditsBroughtForwardEligible;


	public boolean isExcessImputationCreditsBroughtForwardEligible() {
		return excessImputationCreditsBroughtForwardEligible;
	}

	public void setExcessImputationCreditsBroughtForwardEligible(boolean excessImputationCreditsBroughtForwardEligible) {
		this.excessImputationCreditsBroughtForwardEligible = excessImputationCreditsBroughtForwardEligible;
	}


	@UseTrueFalseMappings
	private boolean earlyPaymentDiscountEntitled;


	public boolean isEarlyPaymentDiscountEntitled() {
		return earlyPaymentDiscountEntitled;
	}

	public void setEarlyPaymentDiscountEntitled(boolean earlyPaymentDiscountEntitled) {
		this.earlyPaymentDiscountEntitled = earlyPaymentDiscountEntitled;
	}


	@UseTrueFalseMappings
	private boolean transferRefundToSomeoneElsesIncomeTaxAccount;


	public boolean isTransferRefundToSomeoneElsesIncomeTaxAccount() {
		return transferRefundToSomeoneElsesIncomeTaxAccount;
	}

	public void setTransferRefundToSomeoneElsesIncomeTaxAccount(boolean transferRefundToSomeoneElsesIncomeTaxAccount) {
		this.transferRefundToSomeoneElsesIncomeTaxAccount = transferRefundToSomeoneElsesIncomeTaxAccount;
	}


	@UseTrueFalseMappings
	private boolean transferRefundToSomeoneElsesStudentLoan;


	public boolean isTransferRefundToSomeoneElsesStudentLoan() {
		return transferRefundToSomeoneElsesStudentLoan;
	}

	public void setTransferRefundToSomeoneElsesStudentLoan(boolean transferRefundToSomeoneElsesStudentLoan) {
		this.transferRefundToSomeoneElsesStudentLoan = transferRefundToSomeoneElsesStudentLoan;
	}


	@UseTrueFalseMappings
	private boolean residualIncomeTaxDebitHigherThan2500Dollars;


	public boolean isResidualIncomeTaxDebitHigherThan2500Dollars() {
		return residualIncomeTaxDebitHigherThan2500Dollars;
	}

	public void setResidualIncomeTaxDebitHigherThan2500Dollars(boolean residualIncomeTaxDebitHigherThan2500Dollars) {
		this.residualIncomeTaxDebitHigherThan2500Dollars = residualIncomeTaxDebitHigherThan2500Dollars;
	}


	@UseTrueFalseMappings
	private boolean disclosureRequiredToHoldRightsDuringIncomeYear;


	public boolean isDisclosureRequiredToHoldRightsDuringIncomeYear() {
		return disclosureRequiredToHoldRightsDuringIncomeYear;
	}

	public void setDisclosureRequiredToHoldRightsDuringIncomeYear(boolean disclosureRequiredToHoldRightsDuringIncomeYear) {
		this.disclosureRequiredToHoldRightsDuringIncomeYear = disclosureRequiredToHoldRightsDuringIncomeYear;
	}


	@UseTrueFalseMappings
	private boolean returnForPartYear;


	public boolean isReturnForPartYear() {
		return returnForPartYear;
	}

	public void setReturnForPartYear(boolean returnForPartYear) {
		this.returnForPartYear = returnForPartYear;
	}


	@RightAlign(11)
	private Money totalSchedularTaxDeducted;
	
	@RightAlign(11)
	private Money totalSchedularGrossPayments;
	
	@RightAlign(11)
	private Money schedularPaymentExpenses;
	
	@RightAlign(11)
	private Money netSchedularPayments;
	
	@RightAlign(11)
	private Money totalRWT;
	
	@RightAlign(11)
	private Money totalGrossInterest;
	
	@RightAlign(11)
	private Money totalDividendImputationCredits;
	
	@RightAlign(11)
	private Money totalDividendRWTAndPaymentsForForeignDividends;
	
	@RightAlign(11)
	private Money totalGrossDividends;
	
	@RightAlign(11)
	private Money totalMaoriAuthorityCredits;

	@RightAlign(11)
	private Money totalMaoriAuthorityDistributions;

	@RightAlign(11)
	private Money totalTaxPaidByTrustees;

	@RightAlign(11)
	private Money totalEstateOrTrustIncome;

	@RightAlign(11)
	private Money totalTaxableDistributionsFromNonComplyingTrusts;
	
	@RightAlign(11)
	private Money totalOverseasTaxPaid;
	
	@RightAlign(11)
	private Money totalOverseasIncome;
	
	@RightAlign(11)
	private Money totalPartnershipTaxCredits;
	
	@RightAlign(11)
	private Money totalActivePartnershipIncome;

	@RightAlign(11)
	private Money totalLTCTaxCredits;
	
	@RightAlign(11)
	private Money totalActiveLTCIncome;
	
	@RightAlign(11)
	private Money nonAllowableDeductionsThisYear;

	@RightAlign(11)
	private Money priorYearsNonAllowableDeductionsClaimedThisYear;
	
	@RightAlign(11)
	private Money adjustedLTCIncome;
	
	@RightAlign(11)
	private Money taxCreditSubtotal;
	
	@RightAlign(11)
	private Money incomeSubtotal;

	@RightAlign(11)
	private Money totalShareholderEmployeeSalary;
	
	@RightAlign(11)
	private Money netRents;
	
	@RightAlign(11)
	private Money selfEmployedNetIncome;
	
	@RightAlign(11)
	private Money totalOtherNetIncome;

	@RightAlign(11)
	private Money residentialLandWithholdingTaxCredit;

	@RightAlign(11)
	private Money totalIncome;

	@RightAlign(11)
	private Money totalOtherExpensesClaimed;

	@RightAlign(11)
	private Money incomeAfterExpenses;
	
	@RightAlign(11)
	private Money amountBroughtForward;
	
	@RightAlign(11)
	private Money amountClaimedThisYear;
	
	@RightAlign(11)
	private Money taxableIncome;

	@RightAlign(11)
	private Money excessImputationCreditsBroughtForward;
	
	@RightAlign(11)
	private Money taxOnTaxableIncome;
	
	@RightAlign(11)
	private Money residualIncomeTax;
	
	@RightAlign(11)
	private Money taxCalculationResult;
	
	@RightAlign(11)
	private Money refundCopied;
	
	@RightAlign(11)
	private Money refundOverpayment2018;
	
	@RightAlign(11)
	private Money refundCopiedPlusOverpayment2018;
	
	@RightAlign(11)
	private Money refundTransferTo2018;
	
	@RightAlign(11)
	private Money refundTransferToStudentLoan;
	
	@RightAlign(11)
	private Money refundTotal;
	
	@RightAlign(11)
	@OmitCents
	@IncludeFormatSpacing
	private Money taxPayment2018;
	
	@UseDayMonthYear
	private LocalDate dateStart2018TaxReturn;
	
	@UseDayMonthYear
	private LocalDate dateEnd2018TaxReturn;
	
	@UseDayMonthYear
	private LocalDate dateStartExcludedOverseasIncome;
	
	@UseDayMonthYear
	private LocalDate dateEndExcludedOverseasIncome;
	
	public boolean isIncomeFromLTCReceived() {
		return incomeFromLTCReceived;
	}

	public void setIncomeFromLTCReceived(boolean incomeFromLTCReceived) {
		this.incomeFromLTCReceived = incomeFromLTCReceived;
	}

	public boolean isPartnershipIncomeReceived() {
		return partnershipIncomeReceived;
	}

	public void setPartnershipIncomeReceived(boolean partnershipIncomeReceived) {
		this.partnershipIncomeReceived = partnershipIncomeReceived;
	}

	public boolean isSalaryShareholderEmployeeNotTaxed() {
		return salaryShareholderEmployeeNotTaxed;
	}

	public void setSalaryShareholderEmployeeNotTaxed(boolean salaryShareholderEmployeeNotTaxed) {
		this.salaryShareholderEmployeeNotTaxed = salaryShareholderEmployeeNotTaxed;
	}

	public boolean isOverseasIncomeReceived() {
		return overseasIncomeReceived;
	}

	public void setOverseasIncomeReceived(boolean overseasIncomeReceived) {
		this.overseasIncomeReceived = overseasIncomeReceived;
	}

	public boolean isTrustOrEstateIncomeFromNZReceived() {
		return trustOrEstateIncomeFromNZReceived;
	}

	public void setTrustOrEstateIncomeFromNZReceived(boolean trustOrEstateIncomeFromNZReceived) {
		this.trustOrEstateIncomeFromNZReceived = trustOrEstateIncomeFromNZReceived;
	}

	public boolean isTaxableDistributionsFromMaoriAuthorityReceived() {
		return taxableDistributionsFromMaoriAuthorityReceived;
	}

	public void setTaxableDistributionsFromMaoriAuthorityReceived(boolean taxableDistributionsFromMaoriAuthorityReceived) {
		this.taxableDistributionsFromMaoriAuthorityReceived = taxableDistributionsFromMaoriAuthorityReceived;
	}

	public boolean isDividendsFromNZReceived() {
		return dividendsFromNZReceived;
	}

	public void setDividendsFromNZReceived(boolean dividendsFromNZReceived) {
		this.dividendsFromNZReceived = dividendsFromNZReceived;
	}

	public LocalDate getDateEndExcludedOverseasIncome() {
		return dateEndExcludedOverseasIncome;
	}

	public void setDateEndExcludedOverseasIncome(LocalDate dateEndExcludedOverseasIncome) {
		this.dateEndExcludedOverseasIncome = dateEndExcludedOverseasIncome;
	}

	public LocalDate getDateStartExcludedOverseasIncome() {
		return dateStartExcludedOverseasIncome;
	}

	public void setDateStartExcludedOverseasIncome(LocalDate dateStartExcludedOverseasIncome) {
		this.dateStartExcludedOverseasIncome = dateStartExcludedOverseasIncome;
	}

	public LocalDate getDateEnd2018TaxReturn() {
		return dateEnd2018TaxReturn;
	}

	public void setDateEnd2018TaxReturn(LocalDate dateEnd2018TaxReturn) {
		this.dateEnd2018TaxReturn = dateEnd2018TaxReturn;
	}

	public LocalDate getDateStart2018TaxReturn() {
		return dateStart2018TaxReturn;
	}

	public void setDateStart2018TaxReturn(LocalDate dateStart2018TaxReturn) {
		this.dateStart2018TaxReturn = dateStart2018TaxReturn;
	}

	public Money getTaxPayment2018() {
		return taxPayment2018;
	}

	public void setTaxPayment2018(Money taxPayment2018) {
		this.taxPayment2018 = taxPayment2018;
	}

	public Money getRefundTotal() {
		return refundTotal;
	}

	public void setRefundTotal(Money refundTotal) {
		this.refundTotal = refundTotal;
	}

	public Money getRefundTransferToStudentLoan() {
		return refundTransferToStudentLoan;
	}

	public void setRefundTransferToStudentLoan(Money refundTransferToStudentLoan) {
		this.refundTransferToStudentLoan = refundTransferToStudentLoan;
	}

	public Money getRefundTransferTo2018() {
		return refundTransferTo2018;
	}

	public void setRefundTransferTo2018(Money refundTransferTo2018) {
		this.refundTransferTo2018 = refundTransferTo2018;
	}

	public Money getRefundOverpayment2018() {
		return refundOverpayment2018;
	}

	public void setRefundOverpayment2018(Money refundOverpayment2018) {
		this.refundOverpayment2018 = refundOverpayment2018;
	}

	public Money getRefundCopied() {
		return refundCopied;
	}

	public void setRefundCopied(Money refundCopied) {
		this.refundCopied = refundCopied;
	}

	public Money getTaxCalculationResult() {
		return taxCalculationResult;
	}

	public void setTaxCalculationResult(Money taxCalculationResult) {
		this.taxCalculationResult = taxCalculationResult;
	}

	public Money getResidualIncomeTax() {
		return residualIncomeTax;
	}

	public void setResidualIncomeTax(Money residualIncomeTax) {
		this.residualIncomeTax = residualIncomeTax;
	}

	public Money getTaxOnTaxableIncome() {
		return taxOnTaxableIncome;
	}

	public void setTaxOnTaxableIncome(Money taxOnTaxableIncome) {
		this.taxOnTaxableIncome = taxOnTaxableIncome;
	}

	public Money getTotalOtherNetIncome() {
		return totalOtherNetIncome;
	}

	public void setTotalOtherNetIncome(Money totalOtherNetIncome) {
		this.totalOtherNetIncome = totalOtherNetIncome;
	}

	public Money getResidentialLandWithholdingTaxCredit() {
		return residentialLandWithholdingTaxCredit;
	}

	public void setResidentialLandWithholdingTaxCredit(Money residentialLandWithholdingTaxCredit) {
		this.residentialLandWithholdingTaxCredit = residentialLandWithholdingTaxCredit;
	}

	public Money getTotalIncome() {
		return totalIncome;
	}

	public void setTotalIncome(Money totalIncome) {
		this.totalIncome = totalIncome;
	}

	public Money getTotalOtherExpensesClaimed() {
		return totalOtherExpensesClaimed;
	}

	public void setTotalOtherExpensesClaimed(Money totalOtherExpensesClaimed) {
		this.totalOtherExpensesClaimed = totalOtherExpensesClaimed;
	}

	public Money getIncomeAfterExpenses() {
		return incomeAfterExpenses;
	}

	public void setIncomeAfterExpenses(Money incomeAfterExpenses) {
		this.incomeAfterExpenses = incomeAfterExpenses;
	}

	public Money getAmountBroughtForward() {
		return amountBroughtForward;
	}

	public void setAmountBroughtForward(Money amountBroughtForward) {
		this.amountBroughtForward = amountBroughtForward;
	}

	public Money getAmountClaimedThisYear() {
		return amountClaimedThisYear;
	}

	public void setAmountClaimedThisYear(Money amountClaimedThisYear) {
		this.amountClaimedThisYear = amountClaimedThisYear;
	}

	public Money getTaxableIncome() {
		return taxableIncome;
	}

	public void setTaxableIncome(Money taxableIncome) {
		this.taxableIncome = taxableIncome;
	}

	public Money getExcessImputationCreditsBroughtForward() {
		return excessImputationCreditsBroughtForward;
	}

	public void setExcessImputationCreditsBroughtForward(Money excessImputationCreditsBroughtForward) {
		this.excessImputationCreditsBroughtForward = excessImputationCreditsBroughtForward;
	}

	public Money getSelfEmployedNetIncome() {
		return selfEmployedNetIncome;
	}

	public void setSelfEmployedNetIncome(Money selfEmployedNetIncome) {
		this.selfEmployedNetIncome = selfEmployedNetIncome;
	}

	public Money getNetRents() {
		return netRents;
	}

	public void setNetRents(Money netRents) {
		this.netRents = netRents;
	}

	public Money getTotalShareholderEmployeeSalary() {
		return totalShareholderEmployeeSalary;
	}

	public void setTotalShareholderEmployeeSalary(Money totalShareholderEmployeeSalary) {
		this.totalShareholderEmployeeSalary = totalShareholderEmployeeSalary;
	}

	public Money getincomeSubtotal() {
		return incomeSubtotal;
	}

	public void setIncomeSubtotal(Money incomeSubtotal) {
		this.incomeSubtotal = incomeSubtotal;
	}

	public Money getTaxCreditSubtotal() {
		return taxCreditSubtotal;
	}

	public void setTaxCreditSubtotal(Money taxCreditSubtotal) {
		this.taxCreditSubtotal = taxCreditSubtotal;
	}

	public Money getAdjustedLTCIncome() {
		return adjustedLTCIncome;
	}

	public void setAdjustedLTCIncome(Money adjustedLTCIncome) {
		this.adjustedLTCIncome = adjustedLTCIncome;
	}

	public Money getPriorYearsNonAllowableDeductionsClaimedThisYear() {
		return priorYearsNonAllowableDeductionsClaimedThisYear;
	}

	public void setPriorYearsNonAllowableDeductionsClaimedThisYear(Money priorYearsNonAllowableDeductionsClaimedThisYear) {
		this.priorYearsNonAllowableDeductionsClaimedThisYear = priorYearsNonAllowableDeductionsClaimedThisYear;
	}

	public Money getNonAllowableDeductionsThisYear() {
		return nonAllowableDeductionsThisYear;
	}

	public void setNonAllowableDeductionsThisYear(Money nonAllowableDeductionsThisYear) {
		this.nonAllowableDeductionsThisYear = nonAllowableDeductionsThisYear;
	}

	public Money getTotalActiveLTCIncome() {
		return totalActiveLTCIncome;
	}

	public void setTotalActiveLTCIncome(Money totalActiveLTCIncome) {
		this.totalActiveLTCIncome = totalActiveLTCIncome;
	}

	public Money getTotalLTCTaxCredits() {
		return totalLTCTaxCredits;
	}

	public void setTotalLTCtaxcredits(Money totalLTCTaxCredits) {
		this.totalLTCTaxCredits = totalLTCTaxCredits;
	}

	public Money getTotalActivePartnershipIncome() {
		return totalActivePartnershipIncome;
	}

	public void setTotalActivePartnershipIncome(Money totalActivePartnershipIncome) {
		this.totalActivePartnershipIncome = totalActivePartnershipIncome;
	}

	public Money getTotalPartnershipTaxCredits() {
		return totalPartnershipTaxCredits;
	}

	public void setTotalPartnershipTaxCredits(Money totalPartnershipTaxCredits) {
		this.totalPartnershipTaxCredits = totalPartnershipTaxCredits;
	}
	
	public Money getTotalOverseasIncome() {
		return totalOverseasIncome;
	}

	public void setTotalOverseasIncome(Money totalOverseasIncome) {
		this.totalOverseasIncome = totalOverseasIncome;
	}
	
	public Money getTotalOverseasTaxPaid() {
		return totalOverseasTaxPaid;
	}

	public void setTotalOverseasTaxPaid(Money totalOverseasTaxPaid) {
		this.totalOverseasTaxPaid = totalOverseasTaxPaid;
	}

	public Money getTotalMaoriAuthorityCredits() {
		return totalMaoriAuthorityCredits;
	}

	public void setTotalMaoriAuthorityCredits(Money totalMaoriAuthorityCredits) {
		this.totalMaoriAuthorityCredits = totalMaoriAuthorityCredits;
	}
	
	public Money getTotalMaoriAuthorityDistributions() {
		return totalMaoriAuthorityDistributions;
	}

	public void setTotalMaoriAuthorityDistributions(Money totalMaoriAuthorityDistributions) {
		this.totalMaoriAuthorityDistributions = totalMaoriAuthorityDistributions;
	}

	public Money getTotalTaxPaidByTrustees() {
		return totalTaxPaidByTrustees;
	}

	public void setTotalTaxPaidByTrustees(Money totalTaxPaidByTrustees) {
		this.totalTaxPaidByTrustees = totalTaxPaidByTrustees;
	}
	
	public Money getTotalEstateOrTrustIncome() {
		return totalEstateOrTrustIncome;
	}

	public void setTotalEstateOrTrustIncome(Money totalEstateOrTrustIncome) {
		this.totalEstateOrTrustIncome = totalEstateOrTrustIncome;
	}
	
	public Money getTotalTaxableDistributionsFromNonComplyingTrusts() {
		return totalTaxableDistributionsFromNonComplyingTrusts;
	}

	public void setTotalTaxableDistributionsFromNonComplyingTrusts(Money totalTaxableDistributionsFromNonComplyingTrusts) {
		this.totalTaxableDistributionsFromNonComplyingTrusts = totalTaxableDistributionsFromNonComplyingTrusts;
	}

	public Money getTotalGrossDividends() {
		return totalGrossDividends;
	}

	public void setTotalGrossDividends(Money totalGrossDividends) {
		this.totalGrossDividends = totalGrossDividends;
	}

	public Money getTotalDividendRWTAndPaymentsForForeignDividends() {
		return totalDividendRWTAndPaymentsForForeignDividends;
	}

	public void setTotalDividendRWTAndPaymentsForForeignDividends(Money totalDividendRWTAndPaymentsForForeignDividends) {
		this.totalDividendRWTAndPaymentsForForeignDividends = totalDividendRWTAndPaymentsForForeignDividends;
	}

	public Money getTotalDividendImputationCredits() {
		return totalDividendImputationCredits;
	}

	public void setTotalDividendImputationCredits(Money totalDividendImputationCredits) {
		this.totalDividendImputationCredits = totalDividendImputationCredits;
	}

	public Money getTotalGrossInterest() {
		return totalGrossInterest;
	}

	public void setTotalGrossInterest(Money totalGrossInterest) {
		this.totalGrossInterest = totalGrossInterest;
	}

	public String getIrdNumber() {
		return irdNumber;
	}

	public Money getTotalRWT() {
		return totalRWT;
	}

	public void setTotalRWT(Money totalRWT) {
		this.totalRWT = totalRWT;
	}

	public void setIrdNumber(String irdNumber) {
		this.irdNumber = irdNumber;
	}

	public Salutation getSalutation() {
		return salutation;
	}

	public void setSalutation(Salutation salutation) {
		this.salutation = salutation;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getPostalAddressLine1() {
		return postalAddressLine1;
	}

	public void setPostalAddressLine1(String postalAddressLine1) {
		this.postalAddressLine1 = postalAddressLine1;
	}

	public String getPostalAddressLine2() {
		return postalAddressLine2;
	}

	public void setPostalAddressLine2(String postalAddressLine2) {
		this.postalAddressLine2 = postalAddressLine2;
	}

	public String getStreetAddressLine1() {
		return streetAddressLine1;
	}

	public void setStreetAddressLine1(String streetAddressLine1) {
		this.streetAddressLine1 = streetAddressLine1;
	}

	public String getStreetAddressLine2() {
		return streetAddressLine2;
	}

	public void setStreetAddressLine2(String streetAddressLine2) {
		this.streetAddressLine2 = streetAddressLine2;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getBusinessIndustryClassificationCode() {
		return businessIndustryClassificationCode;
	}

	public void setBusinessIndustryClassificationCode(String businessIndustryClassificationCode) {
		this.businessIndustryClassificationCode = businessIndustryClassificationCode;
	}

	public String getPhonePrefix() {
		return phonePrefix;
	}

	public void setPhonePrefix(String phoneNumberPrefix) {
		this.phonePrefix = phoneNumberPrefix;
	}

	public String getPhoneNumberExcludingPrefix() {
		return phoneNumberExcludingPrefix;
	}

	public void setPhoneNumberExcludingPrefix(String phoneNumberExcludingPrefix) {
		this.phoneNumberExcludingPrefix = phoneNumberExcludingPrefix;
	}

	public NZBankAccount getBankAccount() {
		return bankAccount;
	}

	public void setAccount(NZBankAccount account) {
		this.bankAccount = account;
	}

	public boolean isIncomeAdjustmentsRequired() {
		return incomeAdjustmentsRequired;
	}

	public void setIncomeAdjustmentsRequired(boolean incomeAdjustmentsRequired) {
		this.incomeAdjustmentsRequired = incomeAdjustmentsRequired;
	}

	public boolean isFamilyTaxCreditReceived() {
		return familyTaxCreditReceived;
	}

	public void setFamilyTaxCreditReceived(boolean familyTaxCreditReceived) {
		this.familyTaxCreditReceived = familyTaxCreditReceived;
	}

	public Money getFamilyTaxCreditAmount() {
		return familyTaxCreditAmount;
	}

	public void setFamilyTaxCreditAmount(Money familyTaxCreditAmount) {
		this.familyTaxCreditAmount = familyTaxCreditAmount;
	}

	public boolean isIncomeWithTaxDeductedReceived() {
		return incomeWithTaxDeductedReceived;
	}

	public void setIncomeWithTaxDeductedReceived(boolean incomeWithTaxDeductedReceived) {
		this.incomeWithTaxDeductedReceived = incomeWithTaxDeductedReceived;
	}

	public Money getTotalPAYEDeducted() {
		return totalPAYEDeducted;
	}

	public void setTotalPAYEDeducted(Money totalPAYEDeducted) {
		this.totalPAYEDeducted = totalPAYEDeducted;
	}

	public Money getTotalGrossIncome() {
		return totalGrossIncome;
	}

	public void setTotalGrossIncome(Money totalGrossIncome) {
		this.totalGrossIncome = totalGrossIncome;
	}

	public Money getAccEarnersLevy() {
		return accEarnersLevy;
	}

	public void setAccEarnersLevy(Money accEarnersLevy) {
		this.accEarnersLevy = accEarnersLevy;
	}

	public Money getIncomeNotLiableForAccEarnersLevy() {
		return incomeNotLiableForAccEarnersLevy;
	}

	public void setIncomeNotLiableForAccEarnersLevy(Money incomeNotLiableForAccEarnersLevy) {
		this.incomeNotLiableForAccEarnersLevy = incomeNotLiableForAccEarnersLevy;
	}

	public Money getTotalTaxDeducted() {
		return totalTaxDeducted;
	}

	public void setTotalTaxDeducted(Money totalTaxDeducted) {
		this.totalTaxDeducted = totalTaxDeducted;
	}
	
	public boolean isInterestFromNZReceived() {
		return interestFromNZReceived;
	}

	public void setInterestFromNZReceived(boolean interestFromNZReceived) {
		this.interestFromNZReceived = interestFromNZReceived;
	}

	public boolean isSchedularPaymentsReceived() {
		return schedularPaymentsReceived;
	}

	public void setSchedularPaymentsReceived(boolean schedularPaymentsReceived) {
		this.schedularPaymentsReceived = schedularPaymentsReceived;
	}

	public Money getTotalSchedularTaxDeducted() {
		return totalSchedularTaxDeducted;
	}

	public void setTotalSchedularTaxDeducted(Money totalSchedularTaxDeducted) {
		this.totalSchedularTaxDeducted = totalSchedularTaxDeducted;
	}

	public Money getTotalSchedularGrossPayments() {
		return totalSchedularGrossPayments;
	}

	public void setTotalSchedularGrossPayments(Money totalSchedularGrossPayments) {
		this.totalSchedularGrossPayments = totalSchedularGrossPayments;
	}

	public Money getSchedularPaymentExpenses() {
		return schedularPaymentExpenses;
	}

	public void setSchedularPaymentExpenses(Money schedularPaymentExpenses) {
		this.schedularPaymentExpenses = schedularPaymentExpenses;
	}

	public Money getNetSchedularPayments() {
		return netSchedularPayments;
	}

	public void setNetSchedularPayments(Money netSchedularPayments) {
		this.netSchedularPayments = netSchedularPayments;
	}

	private Map<String, String> getPropertyToFieldMap() {
		return IR3FieldMapper.getPropertyToFieldMap(year);
	}

	private Map<String, String> getFieldToPropertyMap() {
		return IR3FieldMapper.getFieldToPropertyMap(year);
	}

	//assumes the forms are in the user's Downloads folder
	public void publishDraft() {
		try {
			File ir3Form = new File(
					new File(System.getProperty("user.home"), "Downloads"),
					String.format("ir3-%1$s.pdf", year));
			PDDocument pdfTemplate = PDDocument.load(ir3Form);

			PDDocumentCatalog docCatalog = pdfTemplate.getDocumentCatalog();
			PDAcroForm acroForm = docCatalog.getAcroForm();
			Map<String, Object> describe = PropertyUtils.describe(this);
			Map<String, String> propertyToFieldMap = this.getPropertyToFieldMap();
			for (Map.Entry<String, Object> entry : describe.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				if ("describeForm".equals(value)) {
					//acroForm.get
					List<PDField> fieldList = acroForm.getFields();

					String[] fieldArray = new String[fieldList.size()];
					int i = 0;
					for (PDField sField : fieldList) {
						fieldArray[i] = sField.getFullyQualifiedName();
						i++;
					}
					for (String f : fieldArray) {
						//PDField field = acroForm.getField(f);
						logger.info("Field name is: " + f);
					}
					System.exit(0);
				}
				System.out.println(key + "->" + value);
				if (key.equals("reasonForTaxReturnPartYear")) {
					System.out.println("incomeOtherReceived");
				}
				if (key.equals("class") || key.equals("year")) {
					//todo exclude fields by annotation
					continue;
				}
				Field f = this.getClass().getDeclaredField(key);
				f.setAccessible(true);
				Object field = f.get(this);
				if (f.getAnnotation(UseChildFields.class) != null) {
					Map<String, Object> describeChild = PropertyUtils.describe(field);
					for (Map.Entry<String, Object> childEntry : describeChild.entrySet()) {
						String childKey = childEntry.getKey();
						if ("class".equals(childKey)) {
							continue;
						}
						Object childValue = childEntry.getValue();
						String fieldName = propertyToFieldMap.get(childKey);
						PDField pdField = acroForm.getField(fieldName);
						System.out.println(fieldName + "->" + pdField);
						pdField.setValue(String.valueOf(childValue));
						System.out.println(fieldName + "->" + pdField);
					}
				} else {
					//String fieldName = propertyToFieldMap.get(key);
					if (f.getAnnotation(UseDayMonthYear.class) != null) {
						LocalDate localDate = (LocalDate) value;
						int dayOfMonth = localDate.getDayOfMonth();
						processField(acroForm, propertyToFieldMap.get(key + "_day"), 
								dayOfMonth >= 10 ? dayOfMonth : "0" + dayOfMonth,
								f);
						int monthValue = localDate.getMonthValue();
						processField(acroForm, propertyToFieldMap.get(key + "_month"),
								monthValue >= 10 ? monthValue : "0" + monthValue,
								f);
						int year2 = localDate.getYear();
						processField(acroForm, propertyToFieldMap.get(key + "_year"),
								year2 >= 10 ? year2 : "0" + year2,
								f);
					} else if (f.getAnnotation(UseTrueFalseMappings.class) != null) {
						String mappedValue = (Boolean) value ? propertyToFieldMap.get(key + "_true") : propertyToFieldMap.get(key + "_false");
						processField(acroForm, propertyToFieldMap.get(key), mappedValue, f);
					} else if (f.getAnnotation(UseValueMappings.class) != null) {
						String mappedValue = propertyToFieldMap.get(key + "_" + value);
						processField(acroForm, propertyToFieldMap.get(key), mappedValue, f);
					} else {
						processField(acroForm, propertyToFieldMap.get(key), value, f);
						//						if (fieldName == null) {
						//							throw new IllegalStateException("No field mapping for: " + key);
						//						}
						//						PDField pdField = acroForm.getField(fieldName);
						//						System.out.println(fieldName + "->" + pdField);
						//						if (pdField == null) {
						//							List<PDField> fields = acroForm.getFields();
						//							for (PDField field1 : fields) {
						//								System.out.println("Candidate field: " + field1.getFullyQualifiedName());
						//							}
						//						}
						//						pdField.setValue(String.valueOf(value));
						//						System.out.println(fieldName + "->" + pdField);
					}
				}
			}
			System.out.println("done");
			File ir3DraftForm = new File(
					new File(System.getProperty("user.home"), "Downloads"),
					String.format("ir3-%1$s-draft.pdf", year));
			pdfTemplate.save(ir3DraftForm);
			pdfTemplate.close();
			logger.info("IR3 Form Completed Successfully");
		} catch (Exception e) {
			throw new TaxBeansException(e);
		}
	}

	public void processField(PDAcroForm acroForm, String fieldName, Object value, Field f) throws IOException {
		PDField pdField = acroForm.getField(fieldName);
		System.out.println(fieldName + "->" + pdField);
		if (value instanceof Money) {
			if (f.getAnnotation(OmitCents.class) != null) {
				value = TaxReturnUtils.formatDollarsField((Money) value);
				if (f.getAnnotation(IncludeFormatSpacing.class) != null) {
					String valueText = (String)value;
					if (valueText.length() >= 4) {
						valueText = valueText.substring(0, valueText.length()-3) + " " + 
								valueText.substring(valueText.length()-3);
						value = valueText;
					}
				}
			} else {
				value = TaxReturnUtils.formatMoneyField((Money) value);
			}
		}
		if (f.getAnnotation(RightAlign.class) != null) {
			int size = f.getAnnotation(RightAlign.class).value();
			value = StringUtils.leftPad(String.valueOf(value), size);
		}
		if (f.getAnnotation(UseValueMappings.class) != null && pdField instanceof PDNonTerminalField) {
			PDNonTerminalField nonTerminalField = (PDNonTerminalField) pdField;
			nonTerminalField.getChildren().get(Integer.parseInt(String.valueOf(value))).setValue("a");
		}
		if (pdField == null) {
			List<PDField> fields = acroForm.getFields();
			for (PDField field1 : fields) {
				System.out.println("Candidate field: " + field1.getFullyQualifiedName());
			}
		}
		pdField.setValue(String.valueOf(value));
	}


	//assumes the forms are in the user's Downloads folder
	public void publishDraftV1() {
		try {
			File ir3Form = new File(
					new File(System.getProperty("user.home"), "Downloads"),
					String.format("ir3-%1$s.pdf", year));
			PDDocument pdfTemplate = PDDocument.load(ir3Form);

			PDDocumentCatalog docCatalog = pdfTemplate.getDocumentCatalog();
			PDAcroForm acroForm = docCatalog.getAcroForm();

			//acroForm.get
			List<PDField> fieldList = acroForm.getFields();

			String[] fieldArray = new String[fieldList.size()];
			int i = 0;
			for (PDField sField : fieldList) {
				fieldArray[i] = sField.getFullyQualifiedName();
				i++;
			}
			for (String f : fieldArray) {
				PDField field = acroForm.getField(f);
				logger.info("Field name is: " + f);
				if (f.contains(IR3FieldMapper.getFieldName(IR3Fields.irdNumber, year))) {
					String irdNumber2 = this.getIrdNumber();
					if (irdNumber2.length() == 8) {
						irdNumber2 = String.format(" %1$s", irdNumber2);
					}
					field.setValue(irdNumber2);
				} else if (f.contains(IR3FieldMapper.getFieldName(IR3Fields.salutation, year))) {
					PDCheckBox radioButton = (PDCheckBox) field;
					String salutationValue =
							IR3FieldMapper.getSalutationFieldValue(this.getSalutation(), year);
					radioButton.setValue(salutationValue);
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.firstname, year))) {
					field.setValue(this.getFirstname());
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.surname, year))) {
					field.setValue(this.getSurname());
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.postalAddressLine1, year))) {
					field.setValue(this.getPostalAddressLine1());
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.postalAddressLine2, year))) {
					field.setValue(this.getPostalAddressLine2());
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.streetAddressLine1, year))) {
					field.setValue(this.getStreetAddressLine1());
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.streetAddressLine2, year))) {
					field.setValue(this.getStreetAddressLine2());
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.dateOfBirth_day, year))) {
					field.setValue(LocalDateUtils.formatDay(this.getDateOfBirth()));
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.dateOfBirth_month, year))) {
					field.setValue(LocalDateUtils.formatMonth(this.getDateOfBirth()));
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.dateOfBirth_year, year))) {
					field.setValue(String.valueOf(this.getDateOfBirth().getYear()));
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.dateStartExcludedOverseasIncome_day, year))) {
					field.setValue(LocalDateUtils.formatDay(this.getDateStartExcludedOverseasIncome()));
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.dateStartExcludedOverseasIncome_month, year))) {
					field.setValue(LocalDateUtils.formatMonth(this.getDateStartExcludedOverseasIncome()));
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.dateStartExcludedOverseasIncome_year, year))) {
					field.setValue(String.valueOf(this.getDateStartExcludedOverseasIncome().getYear()));
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.dateEndExcludedOverseasIncome_day, year))) {
					field.setValue(LocalDateUtils.formatDay(this.getDateEndExcludedOverseasIncome()));
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.dateEndExcludedOverseasIncome_month, year))) {
					field.setValue(LocalDateUtils.formatMonth(this.getDateEndExcludedOverseasIncome()));
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.dateEndExcludedOverseasIncome_year, year))) {
					field.setValue(String.valueOf(this.getDateEndExcludedOverseasIncome().getYear()));
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.dateStart2018TaxReturn_day, year))) {
					field.setValue(LocalDateUtils.formatDay(this.getDateStart2018TaxReturn()));
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.dateStart2018TaxReturn_month, year))) {
					field.setValue(LocalDateUtils.formatMonth(this.getDateStart2018TaxReturn()));
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.dateStart2018TaxReturn_year, year))) {
					field.setValue(String.valueOf(this.getDateStart2018TaxReturn().getYear()));
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.dateEnd2018TaxReturn_day, year))) {
					field.setValue(LocalDateUtils.formatDay(this.getDateEnd2018TaxReturn()));
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.dateEnd2018TaxReturn_month, year))) {
					field.setValue(LocalDateUtils.formatMonth(this.getDateEnd2018TaxReturn()));
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.dateEnd2018TaxReturn_year, year))) {
					field.setValue(String.valueOf(this.getDateEnd2018TaxReturn().getYear()));
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.businessIndustryClassificationCode, year))) {
					field.setValue(this.getBusinessIndustryClassificationCode());
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.phonePrefix, year))) {
					field.setValue(this.getPhonePrefix());
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.phoneNumberExcludingPrefix, year))) {
					field.setValue(this.getPhoneNumberExcludingPrefix());
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.bankNumber, year))) {
					field.setValue(this.getBankAccount().getBankNumber());
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.branchNumber, year))) {
					field.setValue(this.getBankAccount().getBranchNumber());
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.bankAccountNumber, year))) {
					field.setValue(this.getBankAccount().getBankAccountNumber());
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.bankSuffix, year))) {
					field.setValue(this.getBankAccount().getBankSuffix());
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.incomeAdjustmentsRequired, year))) {
					field.setValue(IR3FieldMapper.getBooleanFieldValue(IR3Fields.incomeAdjustmentsRequired.name(), 
							this.incomeAdjustmentsRequired, year));
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.familyTaxCreditReceived, year))) {
					field.setValue(IR3FieldMapper.getBooleanFieldValue(IR3Fields.familyTaxCreditReceived.name(), 
							this.familyTaxCreditReceived, year));
				} else if (f.equals(IR3FieldMapper.getFieldName(
						IR3Fields.familyTaxCreditAmount, year))) {
					PDFUtils.setFieldValue(field, TaxReturnUtils.formatMoneyField(this.familyTaxCreditAmount), PDFAlignment.RIGHT, 11);
				} else if (f.equals(IR3FieldMapper.getFieldName(
						IR3Fields.incomeWithTaxDeductedReceived, year))) {
					field.setValue(IR3FieldMapper.getBooleanFieldValue(IR3Fields.incomeWithTaxDeductedReceived.name(), 
							this.incomeWithTaxDeductedReceived, year));
				} else if (f.equals(IR3FieldMapper.getFieldName(
						IR3Fields.totalPAYEDeducted, year))) {
					PDFUtils.setFieldValue(field, TaxReturnUtils.formatMoneyField(this.totalPAYEDeducted), PDFAlignment.RIGHT, 11);
				} else if (f.equals(IR3FieldMapper.getFieldName(
						IR3Fields.totalGrossIncome, year))) {
					PDFUtils.setFieldValue(field, TaxReturnUtils.formatMoneyField(this.totalGrossIncome), PDFAlignment.RIGHT, 11);
				} else if (f.equals(IR3FieldMapper.getFieldName(
						IR3Fields.accEarnersLevy, year))) {
					PDFUtils.setFieldValue(field, TaxReturnUtils.formatMoneyField(this.accEarnersLevy), PDFAlignment.RIGHT, 11);
				} else if (f.equals(IR3FieldMapper.getFieldName(
						IR3Fields.incomeNotLiableForAccEarnersLevy, year))) {
					PDFUtils.setFieldValue(field, TaxReturnUtils.formatMoneyField(this.incomeNotLiableForAccEarnersLevy), PDFAlignment.RIGHT, 11);
				} else if (f.equals(IR3FieldMapper.getFieldName(
						IR3Fields.totalTaxDeducted, year))) {
					PDFUtils.setFieldValue(field, TaxReturnUtils.formatMoneyField(this.totalTaxDeducted), PDFAlignment.RIGHT, 11);
				} 
			}
			File ir3DraftForm = new File(
					new File(System.getProperty("user.home"), "Downloads"),
					String.format("ir3-%1$s-draft.pdf", year));
			pdfTemplate.save(ir3DraftForm);
			pdfTemplate.close();
			logger.info("IR3 Form Completed Successfully");
		} catch (Exception e) {
			throw new TaxBeansException(e);
		}
	}
}
