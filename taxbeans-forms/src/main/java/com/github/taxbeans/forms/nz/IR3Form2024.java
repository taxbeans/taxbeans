package com.github.taxbeans.forms.nz;

import java.time.LocalDate;

import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.taxbeans.forms.AutoMinusField;
import com.github.taxbeans.forms.IncludeFormatSpacing;
import com.github.taxbeans.forms.OmitCents;
import com.github.taxbeans.forms.RightAlign;
import com.github.taxbeans.forms.Skip;
import com.github.taxbeans.forms.SkipIfFalse;
import com.github.taxbeans.forms.Sum;
import com.github.taxbeans.forms.UseChildFields;
import com.github.taxbeans.forms.UseDayMonthYear;
import com.github.taxbeans.forms.UseSeparateYesNoCheckboxes;
import com.github.taxbeans.forms.UseTrueFalseMappings;
import com.github.taxbeans.forms.UseValueMappings;
import com.github.taxbeans.forms.common.FormDestination;
import com.github.taxbeans.model.nz.NZBankAccount;
import com.github.taxbeans.model.nz.PortfolioMethod;
import com.github.taxbeans.model.nz.ResidentialPropertyInterestClaimedReason;
import com.github.taxbeans.model.nz.Salutation;

public class IR3Form2024 implements FormDestination {

	private static final String PORTFOLIO_INVESTMENT_ENTITY_INCOME_RECEIVED = "portfolioInvestmentEntityIncomeReceived";

	private static final String RESIDENTIAL_PROPERTY_INTEREST_INCURRED = "residentialPropertyInterestIncurred";

	private static final String GOVERNMENT_SUBSIDY_RECEIVED = "governmentSubsidyReceived";

	private static final String EXCESS_IMPUTATION_CREDITS_BROUGHT_FORWARD_ELIGIBLE = "excessImputationCreditsBroughtForwardEligible";

	private static final String EXPENSES_OTHER_RECEIVED = "expensesOtherReceived";

	private static final String INCOME_FROM_LTC_RECEIVED = "incomeFromLTCReceived";

	private static final String INTEREST_FROM_NZ_RECEIVED = "interestFromNZReceived";

	private static final String IS_INCOME_FROM_SELF_EMPLOYMENT_RECEIVED = "incomeFromSelfEmploymentReceived";

	private static final String IS_INCOME_OTHER_RECEIVED = "incomeOtherReceived";

	private static final String OTHER_INCOME_RECEIVED = "incomeOtherReceived";

	private static final String RENTS_RECEIVED = "rentsReceived";

	private static final String residentialPropertyIncomeReceivedField = "residentialPropertyIncomeReceived";

	private static final String incomeFromTaxablePropertySalesReceivedField = "incomeFromTaxablePropertySalesReceived";

	private static final String SCHEDULAR_PAYMENTS_RECEIVED = "schedularPaymentsReceived";

	private static final String TRUST_OR_ESTATE_INCOME_FROM_NZ_RECEIVED = "trustOrEstateIncomeFromNZReceived";

	@RightAlign(11)
	private Money accEarnersLevy;

	@RightAlign(11)
	@SkipIfFalse(INCOME_FROM_LTC_RECEIVED)
	private Money adjustedLTCIncome;

	@SkipIfFalse(EXPENSES_OTHER_RECEIVED)
	private String alternativePersonFirstNamesCompletedReturn = "";

	@SkipIfFalse(EXPENSES_OTHER_RECEIVED)
	private String alternativePersonSurnameCompletedReturn = "";

	@RightAlign(11)
	@SkipIfFalse("netLossesBroughtForwardClaimed")
	private Money amountBroughtForward;

	@RightAlign(11)
	@SkipIfFalse("netLossesBroughtForwardClaimed")
	private Money amountClaimedThisYear;

	@UseChildFields
	private NZBankAccount bankAccount;

	private String businessIndustryClassificationCode;

	@UseDayMonthYear
	private LocalDate dateEndCurrentYearTaxReturn;

	@UseDayMonthYear
	@SkipIfFalse("excludedOverseasIncomeReceived")
	private LocalDate dateEndExcludedOverseasIncome;

	@UseDayMonthYear
	private LocalDate dateOfBirth;

	@UseDayMonthYear
	private LocalDate dateStartCurrentYearTaxReturn;

	@UseDayMonthYear
	@SkipIfFalse("excludedOverseasIncomeReceived")
	private LocalDate dateStartExcludedOverseasIncome;

	@Skip
	private String destinationDirectory;

	@UseTrueFalseMappings
	private boolean disclosureRequiredToHoldRightsDuringIncomeYear;

	@UseTrueFalseMappings
	private boolean dividendsFromEligibleEntitiesReceived;

	@UseTrueFalseMappings
	private boolean dividendsFromNZReceived;

	@UseTrueFalseMappings
	private boolean earlyPaymentDiscountEntitled;

	@RightAlign(11)
	@SkipIfFalse(EXCESS_IMPUTATION_CREDITS_BROUGHT_FORWARD_ELIGIBLE)
	private Money excessImputationCreditsBroughtForward;

	@UseTrueFalseMappings
	private boolean excessImputationCreditsBroughtForwardEligible;

	@Skip
	private boolean excludedOverseasIncomeReceived;

	@UseTrueFalseMappings
	private boolean expensesOtherReceived;

	@RightAlign(11)
	@SkipIfFalse("familyTaxCreditReceived")
	private Money familyTaxCreditAmount;

	@UseTrueFalseMappings
	private boolean familyTaxCreditReceived;

	private String firstname;

	@UseTrueFalseMappings
	private boolean incomeAdjustmentsRequired;

	@RightAlign(11)
	private Money incomeAfterExpenses;

	@UseTrueFalseMappings
	private boolean incomeFromLTCReceived;

	@UseTrueFalseMappings(fieldName="25 no/yes", falseValue="1", trueValue="0")
	private boolean incomeFromSelfEmploymentReceived;

	@UseTrueFalseMappings(fieldName="26 no/yes")
	private boolean incomeFromTaxablePropertySalesReceived;

	@RightAlign(11)
	private Money incomeNotLiableForAccEarnersLevy;

	@UseTrueFalseMappings
	private boolean incomeOtherReceived;

	@RightAlign(11)
	private Money incomeSubtotal;

	@UseTrueFalseMappings
	private boolean incomeWithTaxDeductedReceived;

	@UseTrueFalseMappings
	private boolean independentEarnerTaxCreditEligible;

	@UseTrueFalseMappings
	private boolean interestFromEligibleEntitiesReceived;

	@UseTrueFalseMappings
	private boolean interestFromNZReceived;

	@UseTrueFalseMappings
	private boolean residentialPropertyInterestIncurred;

	@RightAlign(9)
	private String irdNumber;

	final Logger logger = LoggerFactory.getLogger(IR3Form2024.class);

	@SkipIfFalse(INCOME_FROM_LTC_RECEIVED)
	private String minusSignForAdjustedLTCIncome;

	private String minusSignForIncomeAfterExpenses;

	private String minusSignForIncomeSubtotal;

	@SkipIfFalse(RENTS_RECEIVED)
	@RightAlign(value=1, fieldName="box 24 minus")
	private String minusSignForNetRents;

	@SkipIfFalse(TRUST_OR_ESTATE_INCOME_FROM_NZ_RECEIVED)
	private String minusSignForNZTotalEstateOrCompliantTrustIncome;

	@SkipIfFalse(TRUST_OR_ESTATE_INCOME_FROM_NZ_RECEIVED)
	private String minusSignForNZTotalTaxableDistrbutionsNonCompliantTrust;

	@Skip
	private String minusSignForRLWTTaxCredit;

	@SkipIfFalse(incomeFromTaxablePropertySalesReceivedField)
	private String minusSignForProfitFromSaleOfProperty;

	@SkipIfFalse(SCHEDULAR_PAYMENTS_RECEIVED)
	private String minusSignForSchedularNetPayments;

	@SkipIfFalse(SCHEDULAR_PAYMENTS_RECEIVED)
	private String minusSignForSchedularPaymentsExpenses;

	@SkipIfFalse(IS_INCOME_FROM_SELF_EMPLOYMENT_RECEIVED)
	private String minusSignForSelfEmployedNetIncome;

	private String minusSignForTaxableIncome;

	@SkipIfFalse(INCOME_FROM_LTC_RECEIVED)
	private String minusSignForTotalActiveLTCIncome;

	private String minusSignForTotalActivePartnershipIncome;

	@SkipIfFalse(INTEREST_FROM_NZ_RECEIVED)
	private String minusSignForTotalGrossInterestReceivedFromEligibleEntities;

	private String minusSignForTotalIncome;

	@SkipIfFalse(IS_INCOME_OTHER_RECEIVED)
	private String minusSignForTotalOtherNetIncome;

	private String minusSignForTotalOverseasIncome;

	private String minusSignForTotalShareholderEmployeeSalary;

	@UseTrueFalseMappings
	private boolean netLossesBroughtForwardClaimed;

	@RightAlign(11)
	@SkipIfFalse(RENTS_RECEIVED)
	private Money netRents;

	@RightAlign(11)
	@SkipIfFalse(residentialPropertyIncomeReceivedField)
	private Money totalResidentialIncome;

	@RightAlign(11)
	@SkipIfFalse(RESIDENTIAL_PROPERTY_INTEREST_INCURRED)
	private Money totalInterestOnResidentialProperty;

	@RightAlign(11)
	@SkipIfFalse(RESIDENTIAL_PROPERTY_INTEREST_INCURRED)
	private Money residentialPropertInterestClaimed;

	@RightAlign(11)
	@SkipIfFalse(residentialPropertyIncomeReceivedField)
	private Money residentialRentalDeductions;

	@RightAlign(11)
	@SkipIfFalse(residentialPropertyIncomeReceivedField)
	private Money excessResidentialRentalDeductionsBroughtForward;

	@RightAlign(11)
	@SkipIfFalse(residentialPropertyIncomeReceivedField)
	private Money residentialRentalDeductionsClaimed;

	@RightAlign(11)
	@SkipIfFalse(residentialPropertyIncomeReceivedField)
	private Money netResidentialRentalIncome;

	@RightAlign(11)
	@SkipIfFalse(residentialPropertyIncomeReceivedField)
	private Money excessResidentialRentalDeductionsCarriedForward;

	@RightAlign(11)
	@SkipIfFalse(SCHEDULAR_PAYMENTS_RECEIVED)
	private Money netSchedularPayments;

	@RightAlign(11)
	@SkipIfFalse(INCOME_FROM_LTC_RECEIVED)
	private Money nonAllowableDeductionsThisYear;

	@Skip
	private boolean noOtherIncomeReceived = true;

	@SkipIfFalse(OTHER_INCOME_RECEIVED)
	private String otherIncomePayer;

	@SkipIfFalse(OTHER_INCOME_RECEIVED)
	private String otherIncomeType;

	@UseTrueFalseMappings
	private boolean overseasIncomeReceived;

	@UseTrueFalseMappings
	private boolean partnershipIncomeReceived;

	@Skip
	private String personalisedNaming;

	private String phoneNumberExcludingPrefix;

	private String phonePrefix;

	private String postalAddressLine1;

	private String postalAddressLine2;

	@RightAlign(11)
	@SkipIfFalse(INCOME_FROM_LTC_RECEIVED)
	private Money priorYearsNonAllowableDeductionsClaimedThisYear;

	@UseValueMappings
	@SkipIfFalse("returnForPartYear")
	private int reasonForTaxReturnPartYear;

	@RightAlign(11)
	@SkipIfFalse("refundDue")
	private Money refundCopied;

	@Skip
	private Money refundCopiedPlusOverpaymentCurrentYear;

	@Skip
	private boolean refundDue;

	@UseTrueFalseMappings
	private boolean refundIsTransferredToCurrentYear;

	@UseTrueFalseMappings
	@SkipIfFalse("refundDue")
	private boolean refundIsTransferredToOther;


	@UseTrueFalseMappings
	private boolean refundIsTransferredToSomeoneElsesStudentLoan;

	@UseTrueFalseMappings
	private boolean refundIsTransferredToSomeoneElsesTaxAccount;

	@UseTrueFalseMappings
	private boolean refundIsTransferredToStudentLoan;

	@RightAlign(11)
	@SkipIfFalse("refundIsTransferredToSomeoneElsesStudentLoan")
	private Money refundOtherStudentLoanReceiverAmount;

	@RightAlign(9)
	@SkipIfFalse("refundIsTransferredToSomeoneElsesStudentLoan")
	private String refundOtherStudentLoanReceiverIRD;

	@SkipIfFalse("refundIsTransferredToSomeoneElsesStudentLoan")
	private String refundOtherStudentLoanReceiverName;

	@RightAlign(11)
	@SkipIfFalse("refundIsTransferredToSomeoneElsesTaxAccount")
	private Money refundOtherTaxAccountReceiverAmount;

	@RightAlign(9)
	@SkipIfFalse("refundIsTransferredToSomeoneElsesTaxAccount")
	private String refundOtherTaxAccountReceiverIRD;

	@SkipIfFalse("refundIsTransferredToSomeoneElsesTaxAccount")
	private String refundOtherTaxAccountReceiverName;

	@RightAlign(4)
	@SkipIfFalse("refundIsTransferredToSomeoneElsesTaxAccount")
	private String refundOtherTaxAccountReceiverYearEnded31March;

	@Skip
	private Money refundOverpaymentCurrentYear;

	@RightAlign(11)
	@SkipIfFalse("refundDue")
	private Money refundTotal;

	@RightAlign(11)
	@SkipIfFalse("refundIsTransferredToCurrentYear")
	private Money refundTransferToCurrentYear;

	@RightAlign(11)
	@SkipIfFalse("refundIsTransferredToStudentLoan")
	private Money refundTransferToStudentLoan;

	@UseTrueFalseMappings
	private boolean residentialPropertyIncomeReceived;

	@UseSeparateYesNoCheckboxes
	private boolean rentsReceived;

	@UseTrueFalseMappings(fieldName="27 no/yes")
	private boolean governmentSubsidyReceived;

	@UseTrueFalseMappings
	private boolean portfolioInvestmentEntityIncomeReceived;

	@RightAlign(11)
	@SkipIfFalse(PORTFOLIO_INVESTMENT_ENTITY_INCOME_RECEIVED)
	@AutoMinusField(fieldName="36A minus")
	private Money totalPIEDeductions;

	@RightAlign(11)
	@SkipIfFalse(PORTFOLIO_INVESTMENT_ENTITY_INCOME_RECEIVED)
	@AutoMinusField(fieldName="36B minus")
	private Money totalPIEIncome;

	@RightAlign(11)
	@SkipIfFalse(PORTFOLIO_INVESTMENT_ENTITY_INCOME_RECEIVED)
	@AutoMinusField(fieldName="36C minus")
	private Money pieCalculationOutcome;

	@UseValueMappings
	@SkipIfFalse(residentialPropertyIncomeReceivedField)
	private PortfolioMethod portfolioMethod;

	//@UseValueMappings
	@Skip  //TODO: handle 2023 version(residentialPropertyIncomeReceivedField)
	private ResidentialPropertyInterestClaimedReason residentialPropertyInterestClaimedReason;

	@RightAlign(11)
	@SkipIfFalse(incomeFromTaxablePropertySalesReceivedField)
	private Money residentialLandWithholdingTaxCredit;

	@RightAlign(11)
	@SkipIfFalse(incomeFromTaxablePropertySalesReceivedField)
	private Money profitFromSaleOfProperty;

	@RightAlign(value=11)
	@SkipIfFalse(GOVERNMENT_SUBSIDY_RECEIVED)
	private Money totalGovernmentSubsidy;

	@RightAlign(11)
	private Money residualIncomeTax;

	@UseTrueFalseMappings
	private boolean residualIncomeTaxDebitHigherThan2500Dollars;

	@UseTrueFalseMappings
	private boolean residualIncomeTaxIsCredit;

	@UseTrueFalseMappings
	private boolean returnForPartYear;

	@UseTrueFalseMappings
	private boolean salaryShareholderEmployeeNotTaxed;

	@UseValueMappings
	private Salutation salutation;

	@RightAlign(11)
	@SkipIfFalse(SCHEDULAR_PAYMENTS_RECEIVED)
	private Money schedularPaymentExpenses;

	@UseTrueFalseMappings
	private boolean schedularPaymentsReceived;

	@RightAlign(11)
	@SkipIfFalse(IS_INCOME_FROM_SELF_EMPLOYMENT_RECEIVED)
	private Money selfEmployedNetIncome;

	@UseTrueFalseMappings
	private boolean shareholderEmployeeSalaryOnlyInFuture;

	private String streetAddressLine1;

	private String streetAddressLine2;

	@UseTrueFalseMappings
	private boolean superannuationSchemeIncomeFromOverseas;

	private String surname;

	@UseTrueFalseMappings
	private boolean taxableDistributionsFromMaoriAuthorityReceived;

	@RightAlign(11)
	private Money taxableIncome;

	@RightAlign(11)
	private Money taxCalculationResult;

	@RightAlign(2)
	@SkipIfFalse("excludedOverseasIncomeReceived")
	private String taxCreditQualifyingMonthsNumber;

	@RightAlign(11)
	private Money taxCreditSubtotal;

	@RightAlign(5)
	@SkipIfFalse("excludedOverseasIncomeReceived")
	private Money taxCreditValue;

	@RightAlign(11)
	private Money taxOnTaxableIncome;

	@UseTrueFalseMappings
	private boolean taxOnTaxableIncomeIsCredit;

	@RightAlign(11)
	@OmitCents
	@IncludeFormatSpacing
	@SkipIfFalse("residualIncomeTaxDebitHigherThan2500Dollars")
	private Money taxPaymentCurrentYear;

	@RightAlign(1)
	@SkipIfFalse("residualIncomeTaxDebitHigherThan2500Dollars")
	private String taxPaymentSEROptionCurrentYear;


	@RightAlign(11)
	@SkipIfFalse(INCOME_FROM_LTC_RECEIVED)
	private Money totalActiveLTCIncome;

	@RightAlign(11)
	private Money totalActivePartnershipIncome;

	@RightAlign(11)
	@SkipIfFalse("dividendsFromNZReceived")
	private Money totalDividendImputationCredits;

	@RightAlign(11)
	@SkipIfFalse("dividendsFromNZReceived")
	private Money totalDividendRWTAndPaymentsForForeignDividends;

	@RightAlign(11)
	@SkipIfFalse(TRUST_OR_ESTATE_INCOME_FROM_NZ_RECEIVED)
	private Money totalEstateOrTrustIncome;

	@RightAlign(11)
	@SkipIfFalse("dividendsFromNZReceived")
	private Money totalGrossDividends;

	@RightAlign(11)
	private Money totalGrossIncome;

	@RightAlign(11)
	@SkipIfFalse(INTEREST_FROM_NZ_RECEIVED)
	private Money totalGrossInterest;

	@RightAlign(11)
	private Money totalIncome;

	@RightAlign(11)
	@SkipIfFalse(INCOME_FROM_LTC_RECEIVED)
	private Money totalLTCTaxCredits;

	@RightAlign(11)
	@SkipIfFalse("taxableDistributionsFromMaoriAuthorityReceived")
	private Money totalMaoriAuthorityCredits;

	@RightAlign(11)
	@SkipIfFalse("taxableDistributionsFromMaoriAuthorityReceived")
	private Money totalMaoriAuthorityDistributions;

	@RightAlign(11)
	@SkipIfFalse(EXPENSES_OTHER_RECEIVED)
	private Money totalOtherExpensesClaimed;

	@RightAlign(11)
	@SkipIfFalse(IS_INCOME_OTHER_RECEIVED)
	private Money totalOtherNetIncome;

	@RightAlign(11)
	@SkipIfFalse("overseasIncomeReceived")
	private Money totalOverseasIncome;

	@RightAlign(11)
	@SkipIfFalse("overseasIncomeReceived")
	private Money totalOverseasTaxPaid;

	@RightAlign(11)
	private Money totalPartnershipTaxCredits;

	@RightAlign(11)
	private Money totalPAYEDeducted;

	@RightAlign(11)
	@SkipIfFalse(INTEREST_FROM_NZ_RECEIVED)
	private Money totalRWT;

	@RightAlign(11)
	@SkipIfFalse(SCHEDULAR_PAYMENTS_RECEIVED)
	private Money totalSchedularGrossPayments;

	@RightAlign(11)
	@SkipIfFalse(SCHEDULAR_PAYMENTS_RECEIVED)
	private Money totalSchedularTaxDeducted;

	@RightAlign(11)
	private Money totalShareholderEmployeeSalary;

	@RightAlign(11)
	private Money shareholderAIMTaxCreditAmount;

	@RightAlign(11)
	@SkipIfFalse(TRUST_OR_ESTATE_INCOME_FROM_NZ_RECEIVED)
	private Money totalTaxableDistributionsFromNonComplyingTrusts;

	@RightAlign(11)
	private Money totalTaxDeducted;

	@RightAlign(11)
	@SkipIfFalse(TRUST_OR_ESTATE_INCOME_FROM_NZ_RECEIVED)
	private Money totalTaxPaidByTrustees;

	@UseTrueFalseMappings
	@SkipIfFalse("refundDue")
	private boolean transferRefundToSomeoneElsesIncomeTaxAccountAssociated;

	@UseTrueFalseMappings
	@SkipIfFalse("refundDue")
	private boolean transferRefundToSomeoneElsesStudentLoanAssociated;

	@UseTrueFalseMappings
	private boolean trustOrEstateIncomeFromNZReceived;

	@UseTrueFalseMappings
	private boolean unpaidMajorWorkingShareholderWfFTCELigible;

	private int year = 2024;

	private String calculateMinusSign(Money value) {
		return value.signum() < 0 ? "-" : "";
	}

	public Money getAccEarnersLevy() {
		return accEarnersLevy;
	}

	public Money getAdjustedLTCIncome() {
		return adjustedLTCIncome;
	}

	public String getAlternativePersonFirstNamesCompletedReturn() {
		return alternativePersonFirstNamesCompletedReturn;
	}

	public String getAlternativePersonSurnameCompletedReturn() {
		return alternativePersonSurnameCompletedReturn;
	}

	public Money getAmountBroughtForward() {
		return amountBroughtForward;
	}

	public Money getAmountClaimedThisYear() {
		return amountClaimedThisYear;
	}

	public NZBankAccount getBankAccount() {
		return bankAccount;
	}

	public String getBusinessIndustryClassificationCode() {
		return businessIndustryClassificationCode;
	}

	public LocalDate getDateEndCurrentYearTaxReturn() {
		return dateEndCurrentYearTaxReturn;
	}

	public LocalDate getDateEndExcludedOverseasIncome() {
		return dateEndExcludedOverseasIncome;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public LocalDate getDateStartCurrentYearTaxReturn() {
		return dateStartCurrentYearTaxReturn;
	}

	public LocalDate getDateStartExcludedOverseasIncome() {
		return dateStartExcludedOverseasIncome;
	}

	public String getDestinationDirectory() {
		return destinationDirectory;
	}

	public Money getExcessImputationCreditsBroughtForward() {
		return excessImputationCreditsBroughtForward;
	}

	public Money getFamilyTaxCreditAmount() {
		return familyTaxCreditAmount;
	}

	public String getFirstname() {
		return firstname;
	}

	public Money getIncomeAfterExpenses() {
		return incomeAfterExpenses;
	}

	public Money getIncomeNotLiableForAccEarnersLevy() {
		return incomeNotLiableForAccEarnersLevy;
	}

	public Money getincomeSubtotal() {
		return incomeSubtotal;
	}

	public String getIrdNumber() {
		return irdNumber;
	}

	public String getMinusSignForAdjustedLTCIncome() {
		return minusSignForAdjustedLTCIncome;
	}

	public String getMinusSignForIncomeAfterExpenses() {
		return minusSignForIncomeAfterExpenses;
	}

	public String getMinusSignForIncomeSubtotal() {
		return minusSignForIncomeSubtotal;
	}

	public String getMinusSignForNetRents() {
		return minusSignForNetRents;
	}

	public String getMinusSignForNZTotalEstateOrCompliantTrustIncome() {
		return minusSignForNZTotalEstateOrCompliantTrustIncome;
	}

	public String getMinusSignForNZTotalTaxableDistrbutionsNonCompliantTrust() {
		return minusSignForNZTotalTaxableDistrbutionsNonCompliantTrust;
	}

	public String getMinusSignForRLWTTaxCredit() {
		return minusSignForRLWTTaxCredit;
	}

	public String getMinusSignForProfitFromSaleOfProperty() {
		return minusSignForProfitFromSaleOfProperty;
	}

	public void setMinusSignForProfitFromSaleOfProperty(String minusSignForProfitFromSaleOfProperty) {
		this.minusSignForProfitFromSaleOfProperty = minusSignForProfitFromSaleOfProperty;
	}

	public String getMinusSignForSchedularNetPayments() {
		return minusSignForSchedularNetPayments;
	}

	public String getMinusSignForSchedularPaymentsExpenses() {
		return minusSignForSchedularPaymentsExpenses;
	}

	public String getMinusSignForSelfEmployedNetIncome() {
		return minusSignForSelfEmployedNetIncome;
	}

	public String getMinusSignForTaxableIncome() {
		return minusSignForTaxableIncome;
	}

	public String getMinusSignForTotalActiveLTCIncome() {
		return minusSignForTotalActiveLTCIncome;
	}

	public String getMinusSignForTotalActivePartnershipIncome() {
		return minusSignForTotalActivePartnershipIncome;
	}

	public String getMinusSignForTotalGrossInterestReceivedFromEligibleEntities() {
		return minusSignForTotalGrossInterestReceivedFromEligibleEntities;
	}

	public String getMinusSignForTotalIncome() {
		return minusSignForTotalIncome;
	}

	public String getMinusSignForTotalOtherNetIncome() {
		return minusSignForTotalOtherNetIncome;
	}

	public String getMinusSignForTotalOverseasIncome() {
		return minusSignForTotalOverseasIncome;
	}

	public String getMinusSignForTotalShareholderEmployeeSalary() {
		return minusSignForTotalShareholderEmployeeSalary;
	}

	public Money getNetRents() {
		return netRents;
	}

	public Money getResidentialRentalDeductions() {
		return residentialRentalDeductions;
	}

	public Money getExcessResidentialRentalDeductionsBroughtForward() {
		return excessResidentialRentalDeductionsBroughtForward;
	}

	public Money getResidentialRentalDeductionsClaimed() {
		return residentialRentalDeductionsClaimed;
	}

	public Money getNetResidentialRentalIncome() {
		return netResidentialRentalIncome;
	}

	public Money getExcessResidentialRentalDeductionsCarriedForward() {
		return excessResidentialRentalDeductionsCarriedForward;
	}

	public void setResidentialRentalDeductions(Money residentialRentalDeductions) {
		this.residentialRentalDeductions = residentialRentalDeductions;
	}

	public void setExcessResidentialRentalDeductionsBroughtForward(Money excessResidentialRentalDeductionsBroughtForward) {
		this.excessResidentialRentalDeductionsBroughtForward = excessResidentialRentalDeductionsBroughtForward;
	}

	public void setResidentialRentalDeductionsClaimed(Money residentialRentalDeductionsClaimed) {
		this.residentialRentalDeductionsClaimed = residentialRentalDeductionsClaimed;
	}

	public void setNetResidentialRentalIncome(Money netResidentialRentalIncome) {
		this.netResidentialRentalIncome = netResidentialRentalIncome;
	}

	public void setExcessResidentialRentalDeductionsCarriedForward(Money excessResidentialRentalDeductionsCarriedForward) {
		this.excessResidentialRentalDeductionsCarriedForward = excessResidentialRentalDeductionsCarriedForward;
	}

	public Money getTotalResidentialIncome() {
		return totalResidentialIncome;
	}

	public void setTotalResidentialIncome(Money totalResidentialIncome) {
		this.totalResidentialIncome = totalResidentialIncome;
	}

	public Money getNetSchedularPayments() {
		return netSchedularPayments;
	}

	public Money getNonAllowableDeductionsThisYear() {
		return nonAllowableDeductionsThisYear;
	}

	public String getOtherIncomePayer() {
		return otherIncomePayer;
	}

	public String getOtherIncomeType() {
		return otherIncomeType;
	}

	public String getPersonalisedNaming() {
		return personalisedNaming;
	}

	public String getPhoneNumberExcludingPrefix() {
		return phoneNumberExcludingPrefix;
	}

	public String getPhonePrefix() {
		return phonePrefix;
	}

	public String getPostalAddressLine1() {
		return postalAddressLine1;
	}

	public String getPostalAddressLine2() {
		return postalAddressLine2;
	}

	public Money getPriorYearsNonAllowableDeductionsClaimedThisYear() {
		return priorYearsNonAllowableDeductionsClaimedThisYear;
	}

	public int getReasonForTaxReturnPartYear() {
		return reasonForTaxReturnPartYear;
	}

	public Money getRefundCopied() {
		return refundCopied;
	}

	public Money getRefundCopiedPlusOverpaymentCurrentYear() {
		return refundCopiedPlusOverpaymentCurrentYear;
	}

	public Money getRefundOtherStudentLoanReceiverAmount() {
		return refundOtherStudentLoanReceiverAmount;
	}

	public String getRefundOtherStudentLoanReceiverIRD() {
		return refundOtherStudentLoanReceiverIRD;
	}

	public String getRefundOtherStudentLoanReceiverName() {
		return refundOtherStudentLoanReceiverName;
	}

	public Money getRefundOtherTaxAccountReceiverAmount() {
		return refundOtherTaxAccountReceiverAmount;
	}

	public String getRefundOtherTaxAccountReceiverIRD() {
		return refundOtherTaxAccountReceiverIRD;
	}

	public String getRefundOtherTaxAccountReceiverName() {
		return refundOtherTaxAccountReceiverName;
	}

	public String getRefundOtherTaxAccountReceiverYearEnded31March() {
		return refundOtherTaxAccountReceiverYearEnded31March;
	}

	public Money getRefundOverpaymentCurrentYear() {
		return refundOverpaymentCurrentYear;
	}

	public Money getRefundTotal() {
		return refundTotal;
	}

	public Money getRefundTransferToCurrentYear() {
		return refundTransferToCurrentYear;
	}

	public Money getRefundTransferToStudentLoan() {
		return refundTransferToStudentLoan;
	}

	public Money getResidentialLandWithholdingTaxCredit() {
		return residentialLandWithholdingTaxCredit;
	}

	public Money getProfitFromSaleOfProperty() {
		return profitFromSaleOfProperty;
	}

	public Money getTotalGovernmentSubsidy() {
		return totalGovernmentSubsidy;
	}

	public void setTotalGovernmentSubsidy(Money totalGovernmentSubsidy) {
		this.totalGovernmentSubsidy = totalGovernmentSubsidy;
	}

	public void setProfitFromSaleOfProperty(Money profitFromSaleOfProperty) {
		this.profitFromSaleOfProperty = profitFromSaleOfProperty;
	}

	public boolean isGovernmentSubsidyReceived() {
		return governmentSubsidyReceived;
	}

	public void setGovernmentSubsidyReceived(boolean governmentSubsidyReceived) {
		this.governmentSubsidyReceived = governmentSubsidyReceived;
	}

	public Money getResidualIncomeTax() {
		return residualIncomeTax;
	}

	public Salutation getSalutation() {
		return salutation;
	}

	public Money getSchedularPaymentExpenses() {
		return schedularPaymentExpenses;
	}

	public Money getSelfEmployedNetIncome() {
		return selfEmployedNetIncome;
	}

	public boolean isIncomeFromTaxablePropertySalesReceived() {
		return incomeFromTaxablePropertySalesReceived;
	}

	public void setIncomeFromTaxablePropertySalesReceived(boolean incomeFromTaxablePropertySalesReceived) {
		this.incomeFromTaxablePropertySalesReceived = incomeFromTaxablePropertySalesReceived;
	}

	public String getStreetAddressLine1() {
		return streetAddressLine1;
	}

	public String getStreetAddressLine2() {
		return streetAddressLine2;
	}

	public String getSurname() {
		return surname;
	}

	public Money getTaxableIncome() {
		return taxableIncome;
	}

	public Money getTaxCalculationResult() {
		return taxCalculationResult;
	}

	public String getTaxCreditQualifyingMonthsNumber() {
		return taxCreditQualifyingMonthsNumber;
	}

	public Money getTaxCreditSubtotal() {
		return taxCreditSubtotal;
	}

	public Money getTaxCreditValue() {
		return taxCreditValue;
	}

	public Money getTaxOnTaxableIncome() {
		return taxOnTaxableIncome;
	}

	public Money getTaxPaymentCurrentYear() {
		return taxPaymentCurrentYear;
	}

	public String getTaxPaymentSEROptionCurrentYear() {
		return taxPaymentSEROptionCurrentYear;
	}

	public Money getTotalActiveLTCIncome() {
		return totalActiveLTCIncome;
	}

	public Money getTotalActivePartnershipIncome() {
		return totalActivePartnershipIncome;
	}

	public Money getTotalDividendImputationCredits() {
		return totalDividendImputationCredits;
	}

	public Money getTotalDividendRWTAndPaymentsForForeignDividends() {
		return totalDividendRWTAndPaymentsForForeignDividends;
	}

	public Money getTotalEstateOrTrustIncome() {
		return totalEstateOrTrustIncome;
	}

	public Money getTotalGrossDividends() {
		return totalGrossDividends;
	}

	public Money getTotalGrossIncome() {
		return totalGrossIncome;
	}

	public Money getTotalGrossInterest() {
		return totalGrossInterest;
	}

	public Money getTotalIncome() {
		return totalIncome;
	}

	public Money getTotalLTCTaxCredits() {
		return totalLTCTaxCredits;
	}

	public Money getTotalMaoriAuthorityCredits() {
		return totalMaoriAuthorityCredits;
	}

	public Money getTotalMaoriAuthorityDistributions() {
		return totalMaoriAuthorityDistributions;
	}

	public Money getTotalOtherExpensesClaimed() {
		return totalOtherExpensesClaimed;
	}

	public Money getTotalOtherNetIncome() {
		return totalOtherNetIncome;
	}

	public Money getTotalOverseasIncome() {
		return totalOverseasIncome;
	}

	public Money getTotalOverseasTaxPaid() {
		return totalOverseasTaxPaid;
	}

	public Money getTotalPartnershipTaxCredits() {
		return totalPartnershipTaxCredits;
	}

	public Money getTotalPAYEDeducted() {
		return totalPAYEDeducted;
	}

	public Money getTotalRWT() {
		return totalRWT;
	}

	public Money getTotalSchedularGrossPayments() {
		return totalSchedularGrossPayments;
	}

	public Money getTotalSchedularTaxDeducted() {
		return totalSchedularTaxDeducted;
	}

	public Money getTotalShareholderEmployeeSalary() {
		return totalShareholderEmployeeSalary;
	}

	public Money getTotalTaxableDistributionsFromNonComplyingTrusts() {
		return totalTaxableDistributionsFromNonComplyingTrusts;
	}

	public Money getTotalTaxDeducted() {
		return totalTaxDeducted;
	}

	public Money getTotalTaxPaidByTrustees() {
		return totalTaxPaidByTrustees;
	}

	public int getYear() {
		return year;
	}

	public boolean isDisclosureRequiredToHoldRightsDuringIncomeYear() {
		return disclosureRequiredToHoldRightsDuringIncomeYear;
	}

	public boolean isDividendsFromEligibleEntitiesReceived() {
		return dividendsFromEligibleEntitiesReceived;
	}

	public boolean isDividendsFromNZReceived() {
		return dividendsFromNZReceived;
	}

	public boolean isEarlyPaymentDiscountEntitled() {
		return earlyPaymentDiscountEntitled;
	}

	public boolean isExcessImputationCreditsBroughtForwardEligible() {
		return excessImputationCreditsBroughtForwardEligible;
	}

	public boolean isExpensesOtherReceived() {
		return expensesOtherReceived;
	}

	public boolean isFamilyTaxCreditReceived() {
		return familyTaxCreditReceived;
	}

	public boolean isIncomeAdjustmentsRequired() {
		return incomeAdjustmentsRequired;
	}

	public boolean isIncomeFromLTCReceived() {
		return incomeFromLTCReceived;
	}

	public boolean isIncomeFromSelfEmploymentReceived() {
		return incomeFromSelfEmploymentReceived;
	}

	public boolean isIncomeOtherReceived() {
		return incomeOtherReceived;
	}

	public boolean isIncomeWithTaxDeductedReceived() {
		return incomeWithTaxDeductedReceived;
	}

	public boolean isIndependentEarnerTaxCreditEligible() {
		return independentEarnerTaxCreditEligible;
	}

	public boolean isInterestFromEligibleEntitiesReceived() {
		return interestFromEligibleEntitiesReceived;
	}

	public boolean isInterestFromNZReceived() {
		return interestFromNZReceived;
	}

	public boolean isNetLossesBroughtForwardClaimed() {
		return netLossesBroughtForwardClaimed;
	}

	public boolean isNoOtherIncomeReceived() {
		return noOtherIncomeReceived;
	}

	public boolean isOverseasIncomeReceived() {
		return overseasIncomeReceived;
	}

	public boolean isPartnershipIncomeReceived() {
		return partnershipIncomeReceived;
	}

	public boolean isRefundDue() {
		return refundDue;
	}

	public boolean isRefundIsTransferredToCurrentYear() {
		return refundIsTransferredToCurrentYear;
	}

	public boolean isRefundIsTransferredToOther() {
		return refundIsTransferredToOther;
	}

	public boolean isRefundIsTransferredToSomeoneElsesStudentLoan() {
		return refundIsTransferredToSomeoneElsesStudentLoan;
	}

	public boolean isRefundIsTransferredToSomeoneElsesTaxAccount() {
		return refundIsTransferredToSomeoneElsesTaxAccount;
	}

	public boolean isRefundIsTransferredToStudentLoan() {
		return refundIsTransferredToStudentLoan;
	}

	public boolean isRentsReceived() {
		return rentsReceived;
	}

	public boolean isResidentialPropertyIncomeReceived() {
		return residentialPropertyIncomeReceived;
	}

	public boolean isExcludedOverseasIncomeReceived() {
		return excludedOverseasIncomeReceived;
	}

	public void setExcludedOverseasIncomeReceived(boolean excludedOverseasIncomeReceived) {
		this.excludedOverseasIncomeReceived = excludedOverseasIncomeReceived;
	}

	public void setResidentialPropertyIncomeReceived(boolean residentialPropertyIncomeReceived) {
		this.residentialPropertyIncomeReceived = residentialPropertyIncomeReceived;
	}

	public boolean isResidualIncomeTaxDebitHigherThan2500Dollars() {
		return residualIncomeTaxDebitHigherThan2500Dollars;
	}

	public boolean isResidualIncomeTaxIsCredit() {
		return residualIncomeTaxIsCredit;
	}

	public boolean isReturnForPartYear() {
		return returnForPartYear;
	}

	public boolean isSalaryShareholderEmployeeNotTaxed() {
		return salaryShareholderEmployeeNotTaxed;
	}

	public boolean isSchedularPaymentsReceived() {
		return schedularPaymentsReceived;
	}

	public boolean isShareholderEmployeeSalaryOnlyInFuture() {
		return shareholderEmployeeSalaryOnlyInFuture;
	}

	public boolean isSuperannuationSchemeIncomeFromOverseas() {
		return superannuationSchemeIncomeFromOverseas;
	}

	public boolean isTaxableDistributionsFromMaoriAuthorityReceived() {
		return taxableDistributionsFromMaoriAuthorityReceived;
	}

	public boolean isTaxOnTaxableIncomeIsCredit() {
		return taxOnTaxableIncomeIsCredit;
	}

	public boolean isTransferRefundToSomeoneElsesIncomeTaxAccountAssociated() {
		return transferRefundToSomeoneElsesIncomeTaxAccountAssociated;
	}

	public boolean isTransferRefundToSomeoneElsesStudentLoanAssociated() {
		return transferRefundToSomeoneElsesStudentLoanAssociated;
	}

	public boolean isTrustOrEstateIncomeFromNZReceived() {
		return trustOrEstateIncomeFromNZReceived;
	}

	public boolean isUnpaidMajorWorkingShareholderWfFTCELigible() {
		return unpaidMajorWorkingShareholderWfFTCELigible;
	}

	public boolean isResidentialPropertyInterestIncurred() {
		return residentialPropertyInterestIncurred;
	}

	public void setResidentialPropertyInterestIncurred(boolean residentialPropertyInterestIncurred) {
		this.residentialPropertyInterestIncurred = residentialPropertyInterestIncurred;
	}

	public void setAccEarnersLevy(Money accEarnersLevy) {
		this.accEarnersLevy = accEarnersLevy;
	}

	public void setAccount(NZBankAccount account) {
		this.bankAccount = account;
	}

	public void setAdjustedLTCIncome(Money adjustedLTCIncome) {
		this.adjustedLTCIncome = adjustedLTCIncome;
	}

	public void setAlternativePersonFirstNamesCompletedReturn(String alternativePersonFirstNamesCompletedReturn) {
		this.alternativePersonFirstNamesCompletedReturn = alternativePersonFirstNamesCompletedReturn;
	}

	public void setAlternativePersonSurnameCompletedReturn(String alternativePersonSurnameCompletedReturn) {
		this.alternativePersonSurnameCompletedReturn = alternativePersonSurnameCompletedReturn;
	}

	public void setAmountBroughtForward(Money amountBroughtForward) {
		this.amountBroughtForward = amountBroughtForward;
	}

	public void setAmountClaimedThisYear(Money amountClaimedThisYear) {
		this.amountClaimedThisYear = amountClaimedThisYear;
	}

	public void setBusinessIndustryClassificationCode(String businessIndustryClassificationCode) {
		this.businessIndustryClassificationCode = businessIndustryClassificationCode;
	}

	public void setDateEndCurrentYearTaxReturn(LocalDate dateEndCurrentYearTaxReturn) {
		this.dateEndCurrentYearTaxReturn = dateEndCurrentYearTaxReturn;
	}

	public void setDateEndExcludedOverseasIncome(LocalDate dateEndExcludedOverseasIncome) {
		this.dateEndExcludedOverseasIncome = dateEndExcludedOverseasIncome;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public void setDateStartCurrentYearTaxReturn(LocalDate dateStartCurrentYearTaxReturn) {
		this.dateStartCurrentYearTaxReturn = dateStartCurrentYearTaxReturn;
	}

	public void setDateStartExcludedOverseasIncome(LocalDate dateStartExcludedOverseasIncome) {
		this.dateStartExcludedOverseasIncome = dateStartExcludedOverseasIncome;
	}

	public void setDestinationDirectory(String destinationDirectory) {
		this.destinationDirectory = destinationDirectory;
	}

	public void setDisclosureRequiredToHoldRightsDuringIncomeYear(boolean disclosureRequiredToHoldRightsDuringIncomeYear) {
		this.disclosureRequiredToHoldRightsDuringIncomeYear = disclosureRequiredToHoldRightsDuringIncomeYear;
	}

	public void setDividendsFromEligibleEntitiesReceived(boolean dividendsFromEligibleEntitiesReceived) {
		this.dividendsFromEligibleEntitiesReceived = dividendsFromEligibleEntitiesReceived;
	}

	public void setDividendsFromNZReceived(boolean dividendsFromNZReceived) {
		this.dividendsFromNZReceived = dividendsFromNZReceived;
	}

	public void setEarlyPaymentDiscountEntitled(boolean earlyPaymentDiscountEntitled) {
		this.earlyPaymentDiscountEntitled = earlyPaymentDiscountEntitled;
	}

	public void setExcessImputationCreditsBroughtForward(Money excessImputationCreditsBroughtForward) {
		this.excessImputationCreditsBroughtForward = excessImputationCreditsBroughtForward;
	}

	public void setExcessImputationCreditsBroughtForwardEligible(boolean excessImputationCreditsBroughtForwardEligible) {
		this.excessImputationCreditsBroughtForwardEligible = excessImputationCreditsBroughtForwardEligible;
	}

	public void setExpensesOtherReceived(boolean expensesOtherReceived) {
		this.expensesOtherReceived = expensesOtherReceived;
	}

	public void setFamilyTaxCreditAmount(Money familyTaxCreditAmount) {
		this.familyTaxCreditAmount = familyTaxCreditAmount;
	}

	public void setFamilyTaxCreditReceived(boolean familyTaxCreditReceived) {
		this.familyTaxCreditReceived = familyTaxCreditReceived;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public void setIncomeAdjustmentsRequired(boolean incomeAdjustmentsRequired) {
		this.incomeAdjustmentsRequired = incomeAdjustmentsRequired;
	}

	public void setIncomeAfterExpenses(Money incomeAfterExpenses) {
		this.incomeAfterExpenses = incomeAfterExpenses;
		this.setMinusSignForIncomeAfterExpenses(calculateMinusSign(incomeAfterExpenses));;
	}

	public void setIncomeFromLTCReceived(boolean incomeFromLTCReceived) {
		this.incomeFromLTCReceived = incomeFromLTCReceived;
	}

	public void setIncomeFromSelfEmploymentReceived(boolean incomeFromSelfEmploymentReceived) {
		this.incomeFromSelfEmploymentReceived = incomeFromSelfEmploymentReceived;
	}

	public void setIncomeNotLiableForAccEarnersLevy(Money incomeNotLiableForAccEarnersLevy) {
		this.incomeNotLiableForAccEarnersLevy = incomeNotLiableForAccEarnersLevy;
	}

	public void setIncomeOtherReceived(boolean incomeOtherReceived) {
		this.incomeOtherReceived = incomeOtherReceived;
	}

	public void setIncomeSubtotal(Money incomeSubtotal) {
		this.incomeSubtotal = incomeSubtotal;
		this.minusSignForIncomeSubtotal = incomeSubtotal.signum() < 0 ? "-" : "";
	}

	public void setIncomeWithTaxDeductedReceived(boolean incomeWithTaxDeductedReceived) {
		this.incomeWithTaxDeductedReceived = incomeWithTaxDeductedReceived;
	}

	public void setIndependentEarnerTaxCreditEligible(boolean independentEarnerTaxCreditEligible) {
		this.independentEarnerTaxCreditEligible = independentEarnerTaxCreditEligible;
	}

	public void setInterestFromEligibleEntitiesReceived(boolean interestFromEligibleEntitiesReceived) {
		this.interestFromEligibleEntitiesReceived = interestFromEligibleEntitiesReceived;
	}

	public void setInterestFromNZReceived(boolean interestFromNZReceived) {
		this.interestFromNZReceived = interestFromNZReceived;
	}

	public void setIrdNumber(String irdNumber) {
		this.irdNumber = irdNumber;
	}

	public void setMinusSignForAdjustedLTCIncome(String minusSignForAdjustedLTCIncome) {
		this.minusSignForAdjustedLTCIncome = minusSignForAdjustedLTCIncome;
	}

	public void setMinusSignForIncomeAfterExpenses(String minusSignForIncomeAfterExpenses) {
		this.minusSignForIncomeAfterExpenses = minusSignForIncomeAfterExpenses;
	}

	public void setMinusSignForIncomeSubtotal(String minusSignForIncomeSubtotal) {
		this.minusSignForIncomeSubtotal = minusSignForIncomeSubtotal;
	}

	public void setMinusSignForNetRents(String minusSignForNetRents) {
		this.minusSignForNetRents = minusSignForNetRents;
	}

	public void setMinusSignForNZTotalEstateOrCompliantTrustIncome(String minusSignForNZTotalEstateOrCompliantTrustIncome) {
		this.minusSignForNZTotalEstateOrCompliantTrustIncome = minusSignForNZTotalEstateOrCompliantTrustIncome;
	}

	public void setMinusSignForNZTotalTaxableDistrbutionsNonCompliantTrust(
			String minusSignForNZTotalTaxableDistrbutionsNonCompliantTrust) {
		this.minusSignForNZTotalTaxableDistrbutionsNonCompliantTrust = minusSignForNZTotalTaxableDistrbutionsNonCompliantTrust;
	}

	public void setMinusSignForRLWTTaxCredit(String minusSignForRLWTTaxCredit) {
		this.minusSignForRLWTTaxCredit = minusSignForRLWTTaxCredit;
	}

	public void setMinusSignForSchedularNetPayments(String minusSignForSchedularNetPayments) {
		this.minusSignForSchedularNetPayments = minusSignForSchedularNetPayments;
	}

	public void setMinusSignForSchedularPaymentsExpenses(String minusSignForSchedularPaymentsExpenses) {
		this.minusSignForSchedularPaymentsExpenses = minusSignForSchedularPaymentsExpenses;
	}

	public void setMinusSignForSelfEmployedNetIncome(String minusSignForSelfEmployedNetIncome) {
		this.minusSignForSelfEmployedNetIncome = minusSignForSelfEmployedNetIncome;
	}

	public void setMinusSignForTaxableIncome(String minusSignForTaxableIncome) {
		this.minusSignForTaxableIncome = minusSignForTaxableIncome;
	}

	public void setMinusSignForTotalActiveLTCIncome(String minusSignForTotalActiveLTCIncome) {
		this.minusSignForTotalActiveLTCIncome = minusSignForTotalActiveLTCIncome;
	}

	public void setMinusSignForTotalActivePartnershipIncome(String minusSignForTotalActivePartnershipIncome) {
		this.minusSignForTotalActivePartnershipIncome = minusSignForTotalActivePartnershipIncome;
	}

	public void setMinusSignForTotalGrossInterestReceivedFromEligibleEntities(
			String minusSignForTotalGrossInterestReceivedFromEligibleEntities) {
		this.minusSignForTotalGrossInterestReceivedFromEligibleEntities = minusSignForTotalGrossInterestReceivedFromEligibleEntities;
	}

	public void setMinusSignForTotalIncome(String minusSignForTotalIncome) {
		this.minusSignForTotalIncome = minusSignForTotalIncome;
	}

	public void setMinusSignForTotalOtherNetIncome(String minusSignForTotalOtherNetIncome) {
		this.minusSignForTotalOtherNetIncome = minusSignForTotalOtherNetIncome;
	}

	public void setMinusSignForTotalOverseasIncome(String minusSignForTotalOverseasIncome) {
		this.minusSignForTotalOverseasIncome = minusSignForTotalOverseasIncome;
	}

	public void setMinusSignForTotalShareholderEmployeeSalary(String minusSignForTotalShareholderEmployeeSalary) {
		this.minusSignForTotalShareholderEmployeeSalary = minusSignForTotalShareholderEmployeeSalary;
	}

	public void setNetLossesBroughtForwardClaimed(boolean netLossesBroughtForwardClaimed) {
		this.netLossesBroughtForwardClaimed = netLossesBroughtForwardClaimed;
	}

	public void setNetRents(Money netRents) {
		this.netRents = netRents;
	}

	public void setNetSchedularPayments(Money netSchedularPayments) {
		this.netSchedularPayments = netSchedularPayments;
	}

	public void setNonAllowableDeductionsThisYear(Money nonAllowableDeductionsThisYear) {
		this.nonAllowableDeductionsThisYear = nonAllowableDeductionsThisYear;
	}

	public void setNoOtherIncomeReceived(boolean noOtherIncomeReceived) {
		this.noOtherIncomeReceived = noOtherIncomeReceived;
	}

	public void setOtherIncomePayer(String otherIncomePayer) {
		this.otherIncomePayer = otherIncomePayer;
	}

	public void setOtherIncomeType(String otherIncomeType) {
		this.otherIncomeType = otherIncomeType;
	}

	public void setOverseasIncomeReceived(boolean overseasIncomeReceived) {
		this.overseasIncomeReceived = overseasIncomeReceived;
	}

	public void setPartnershipIncomeReceived(boolean partnershipIncomeReceived) {
		this.partnershipIncomeReceived = partnershipIncomeReceived;
	}

	public void setPersonalisedNaming(String personalisedNaming) {
		this.personalisedNaming = personalisedNaming;
	}

	public void setPhoneNumberExcludingPrefix(String phoneNumberExcludingPrefix) {
		this.phoneNumberExcludingPrefix = phoneNumberExcludingPrefix;
	}

	public void setPhonePrefix(String phoneNumberPrefix) {
		this.phonePrefix = phoneNumberPrefix;
	}

	public void setPostalAddressLine1(String postalAddressLine1) {
		this.postalAddressLine1 = postalAddressLine1;
	}

	public void setPostalAddressLine2(String postalAddressLine2) {
		this.postalAddressLine2 = postalAddressLine2;
	}

	public void setPriorYearsNonAllowableDeductionsClaimedThisYear(Money priorYearsNonAllowableDeductionsClaimedThisYear) {
		this.priorYearsNonAllowableDeductionsClaimedThisYear = priorYearsNonAllowableDeductionsClaimedThisYear;
	}

	public void setReasonForTaxReturnPartYear(int reasonForTaxReturnPartYear) {
		this.reasonForTaxReturnPartYear = reasonForTaxReturnPartYear;
	}

	public void setRefundCopied(Money refundCopied) {
		this.refundCopied = refundCopied;
	}

	public void setRefundCopiedPlusOverpaymentCurrentYear(Money refundCopiedPlusOverpaymentCurrentYear) {
		this.refundCopiedPlusOverpaymentCurrentYear = refundCopiedPlusOverpaymentCurrentYear;
	}

	public void setRefundDue(boolean refundDue) {
		this.refundDue = refundDue;
	}

	public void setRefundIsTransferredToCurrentYear(boolean refundIsTransferredToCurrentYear) {
		this.refundIsTransferredToCurrentYear = refundIsTransferredToCurrentYear;
	}

	public void setRefundIsTransferredToOther(boolean refundIsTransferredToOther) {
		this.refundIsTransferredToOther = refundIsTransferredToOther;
	}

	public void setRefundIsTransferredToSomeoneElsesStudentLoan(boolean refundIsTransferredToSomeoneElsesStudentLoan) {
		this.refundIsTransferredToSomeoneElsesStudentLoan = refundIsTransferredToSomeoneElsesStudentLoan;
	}

	public void setRefundIsTransferredToSomeoneElsesTaxAccount(boolean refundIsTransferredToSomeoneElsesTaxAccount) {
		this.refundIsTransferredToSomeoneElsesTaxAccount = refundIsTransferredToSomeoneElsesTaxAccount;
	}

	public void setRefundIsTransferredToStudentLoan(boolean refundIsTransferredToStudentLoan) {
		this.refundIsTransferredToStudentLoan = refundIsTransferredToStudentLoan;
	}

	public void setRefundOtherStudentLoanReceiverAmount(Money refundOtherStudentLoanReceiverAmount) {
		this.refundOtherStudentLoanReceiverAmount = refundOtherStudentLoanReceiverAmount;
	}

	public void setRefundOtherStudentLoanReceiverIRD(String refundOtherStudentLoanReceiverIRD) {
		this.refundOtherStudentLoanReceiverIRD = refundOtherStudentLoanReceiverIRD;
	}

	public void setRefundOtherStudentLoanReceiverName(String refundOtherStudentLoanReceiverName) {
		this.refundOtherStudentLoanReceiverName = refundOtherStudentLoanReceiverName;
	}

	public void setRefundOtherTaxAccountReceiverAmount(Money refundOtherTaxAccountReceiverAmount) {
		this.refundOtherTaxAccountReceiverAmount = refundOtherTaxAccountReceiverAmount;
	}

	public void setRefundOtherTaxAccountReceiverIRD(String refundOtherTaxAccountReceiverIRD) {
		this.refundOtherTaxAccountReceiverIRD = refundOtherTaxAccountReceiverIRD;
	}

	public void setRefundOtherTaxAccountReceiverName(String refundOtherTaxAccountReceiverName) {
		this.refundOtherTaxAccountReceiverName = refundOtherTaxAccountReceiverName;
	}

	public void setRefundOtherTaxAccountReceiverYearEnded31March(String refundOtherTaxAccountReceiverYearEnded31March) {
		this.refundOtherTaxAccountReceiverYearEnded31March = refundOtherTaxAccountReceiverYearEnded31March;
	}

	public void setRefundOverpaymentCurrentYear(Money refundOverpaymentCurrentYear) {
		this.refundOverpaymentCurrentYear = refundOverpaymentCurrentYear;
	}

	public void setRefundTotal(Money refundTotal) {
		this.refundTotal = refundTotal;
	}

	public void setRefundTransferToCurrentYear(Money refundTransferToCurrentYear) {
		this.refundTransferToCurrentYear = refundTransferToCurrentYear;
	}

	public void setRefundTransferToStudentLoan(Money refundTransferToStudentLoan) {
		this.refundTransferToStudentLoan = refundTransferToStudentLoan;
	}

	public void setRentsReceived(boolean rentsReceived) {
		this.rentsReceived = rentsReceived;
	}

	public PortfolioMethod getPortfolioMethod() {
		return portfolioMethod;
	}

	public void setPortfolioMethod(PortfolioMethod portfolioMethod) {
		this.portfolioMethod = portfolioMethod;
	}

	public void setResidentialLandWithholdingTaxCredit(Money residentialLandWithholdingTaxCredit) {
		this.residentialLandWithholdingTaxCredit = residentialLandWithholdingTaxCredit;
	}

	public void setResidualIncomeTax(Money residualIncomeTax) {
		this.residualIncomeTax = residualIncomeTax;
		if (this.residualIncomeTax.signum() < 0) {
			this.setResidualIncomeTaxIsCredit(true);
			this.residualIncomeTax = this.residualIncomeTax.abs();
		}
	}

	public void setResidualIncomeTaxDebitHigherThan2500Dollars(boolean residualIncomeTaxDebitHigherThan2500Dollars) {
		this.residualIncomeTaxDebitHigherThan2500Dollars = residualIncomeTaxDebitHigherThan2500Dollars;
	}

	public void setResidualIncomeTaxIsCredit(boolean residualIncomeTaxIsCredit) {
		this.residualIncomeTaxIsCredit = residualIncomeTaxIsCredit;
	}

	public void setReturnForPartYear(boolean returnForPartYear) {
		this.returnForPartYear = returnForPartYear;
	}

	public void setSalaryShareholderEmployeeNotTaxed(boolean salaryShareholderEmployeeNotTaxed) {
		this.salaryShareholderEmployeeNotTaxed = salaryShareholderEmployeeNotTaxed;
	}

	public void setSalutation(Salutation salutation) {
		this.salutation = salutation;
	}

	public void setSchedularPaymentExpenses(Money schedularPaymentExpenses) {
		this.schedularPaymentExpenses = schedularPaymentExpenses;
	}

	public void setSchedularPaymentsReceived(boolean schedularPaymentsReceived) {
		this.schedularPaymentsReceived = schedularPaymentsReceived;
	}

	public void setSelfEmployedNetIncome(Money selfEmployedNetIncome) {
		this.selfEmployedNetIncome = selfEmployedNetIncome;
	}

	public void setShareholderEmployeeSalaryOnlyInFuture(boolean shareholderEmployeeSalaryOnlyInFuture) {
		this.shareholderEmployeeSalaryOnlyInFuture = shareholderEmployeeSalaryOnlyInFuture;
	}

	public void setStreetAddressLine1(String streetAddressLine1) {
		this.streetAddressLine1 = streetAddressLine1;
	}

	public void setStreetAddressLine2(String streetAddressLine2) {
		this.streetAddressLine2 = streetAddressLine2;
	}

	public void setSuperannuationSchemeIncomeFromOverseas(boolean superannuationSchemeIncomeFromOverseas) {
		this.superannuationSchemeIncomeFromOverseas = superannuationSchemeIncomeFromOverseas;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public void setTaxableDistributionsFromMaoriAuthorityReceived(boolean taxableDistributionsFromMaoriAuthorityReceived) {
		this.taxableDistributionsFromMaoriAuthorityReceived = taxableDistributionsFromMaoriAuthorityReceived;
	}

	public void setTaxableIncome(Money taxableIncome) {
		this.taxableIncome = taxableIncome;
		this.setMinusSignForTaxableIncome(this.calculateMinusSign(taxableIncome));
	}

	public void setTaxCalculationResult(Money taxCalculationResult) {
		this.taxCalculationResult = taxCalculationResult;
		if (this.taxCalculationResult.signum() < 0) {
			this.setTaxOnTaxableIncomeIsCredit(true);
			this.taxCalculationResult = this.taxCalculationResult.abs();
		}
	}

	public void setTaxCreditQualifyingMonthsNumber(String taxCreditQualifyingMonthsNumber) {
		this.taxCreditQualifyingMonthsNumber = taxCreditQualifyingMonthsNumber;
	}

	public void setTaxCreditSubtotal(Money taxCreditSubtotal) {
		this.taxCreditSubtotal = taxCreditSubtotal;
	}

	public void setTaxCreditValue(Money taxCreditValue) {
		this.taxCreditValue = taxCreditValue;
	}

	public void setTaxOnTaxableIncome(Money taxOnTaxableIncome) {
		this.taxOnTaxableIncome = taxOnTaxableIncome;
	}

	public void setTaxOnTaxableIncomeIsCredit(boolean taxOnTaxableIncomeIsCredit) {
		this.taxOnTaxableIncomeIsCredit = taxOnTaxableIncomeIsCredit;
	}

	public void setTaxPaymentCurrentYear(Money taxPaymentCurrentYear) {
		this.taxPaymentCurrentYear = taxPaymentCurrentYear;
	}

	public void setTaxPaymentSEROptionCurrentYear(String taxPaymentSEROptionCurrentYear) {
		this.taxPaymentSEROptionCurrentYear = taxPaymentSEROptionCurrentYear;
	}

	public void setTotalActiveLTCIncome(Money totalActiveLTCIncome) {
		this.totalActiveLTCIncome = totalActiveLTCIncome;
	}

	public void setTotalActivePartnershipIncome(Money totalActivePartnershipIncome) {
		this.totalActivePartnershipIncome = totalActivePartnershipIncome;
		this.setMinusSignForTotalActivePartnershipIncome(
				totalActivePartnershipIncome.signum() < 0 ? "-" : "");
	}

	public void setTotalDividendImputationCredits(Money totalDividendImputationCredits) {
		this.totalDividendImputationCredits = totalDividendImputationCredits;
	}

	public void setTotalDividendRWTAndPaymentsForForeignDividends(Money totalDividendRWTAndPaymentsForForeignDividends) {
		this.totalDividendRWTAndPaymentsForForeignDividends = totalDividendRWTAndPaymentsForForeignDividends;
	}

	public void setTotalEstateOrTrustIncome(Money totalEstateOrTrustIncome) {
		this.totalEstateOrTrustIncome = totalEstateOrTrustIncome;
	}

	public void setTotalGrossDividends(Money totalGrossDividends) {
		this.totalGrossDividends = totalGrossDividends;
	}

	public void setTotalGrossIncome(Money totalGrossIncome) {
		this.totalGrossIncome = totalGrossIncome;
	}

	public void setTotalGrossInterest(Money totalGrossInterest) {
		this.totalGrossInterest = totalGrossInterest;
	}

	public void setTotalIncome(Money totalIncome) {
		this.totalIncome = totalIncome;
		this.setMinusSignForTotalIncome(calculateMinusSign(totalIncome));
	}

	public void setTotalLTCtaxcredits(Money totalLTCTaxCredits) {
		this.totalLTCTaxCredits = totalLTCTaxCredits;
	}

	public void setTotalMaoriAuthorityCredits(Money totalMaoriAuthorityCredits) {
		this.totalMaoriAuthorityCredits = totalMaoriAuthorityCredits;
	}

	public void setTotalMaoriAuthorityDistributions(Money totalMaoriAuthorityDistributions) {
		this.totalMaoriAuthorityDistributions = totalMaoriAuthorityDistributions;
	}

	public void setTotalOtherExpensesClaimed(Money totalOtherExpensesClaimed) {
		this.totalOtherExpensesClaimed = totalOtherExpensesClaimed;
	}

	public void setTotalOtherNetIncome(Money totalOtherNetIncome) {
		this.totalOtherNetIncome = totalOtherNetIncome;
	}

	public void setTotalOverseasIncome(Money totalOverseasIncome) {
		this.totalOverseasIncome = totalOverseasIncome;
	}

	public void setTotalOverseasTaxPaid(Money totalOverseasTaxPaid) {
		this.totalOverseasTaxPaid = totalOverseasTaxPaid;
	}

	public void setTotalPartnershipTaxCredits(Money totalPartnershipTaxCredits) {
		this.totalPartnershipTaxCredits = totalPartnershipTaxCredits;
	}

	public void setTotalPAYEDeducted(Money totalPAYEDeducted) {
		this.totalPAYEDeducted = totalPAYEDeducted;
	}

	public void setTotalRWT(Money totalRWT) {
		this.totalRWT = totalRWT;
	}

	public void setTotalSchedularGrossPayments(Money totalSchedularGrossPayments) {
		this.totalSchedularGrossPayments = totalSchedularGrossPayments;
	}

	public void setTotalSchedularTaxDeducted(Money totalSchedularTaxDeducted) {
		this.totalSchedularTaxDeducted = totalSchedularTaxDeducted;
	}

	public void setTotalShareholderEmployeeSalary(Money totalShareholderEmployeeSalary) {
		this.totalShareholderEmployeeSalary = totalShareholderEmployeeSalary;
		this.minusSignForTotalShareholderEmployeeSalary = totalShareholderEmployeeSalary.signum() < 0 ? "-" : "";
	}

	public Money getTotalInterestOnResidentialProperty() {
		return totalInterestOnResidentialProperty;
	}

	public Money getResidentialPropertInterestClaimed() {
		return residentialPropertInterestClaimed;
	}

	public void setTotalInterestOnResidentialProperty(Money totalInterestOnResidentialProperty) {
		this.totalInterestOnResidentialProperty = totalInterestOnResidentialProperty;
	}

	public void setResidentialPropertInterestClaimed(Money residentialPropertInterestClaimed) {
		this.residentialPropertInterestClaimed = residentialPropertInterestClaimed;
	}

	public Money getShareholderAIMTaxCreditAmount() {
		return shareholderAIMTaxCreditAmount;
	}

	public void setShareholderAIMTaxCreditAmount(Money shareholderAIMTaxCreditAmount) {
		this.shareholderAIMTaxCreditAmount = shareholderAIMTaxCreditAmount;
	}

	public void setTotalTaxableDistributionsFromNonComplyingTrusts(Money totalTaxableDistributionsFromNonComplyingTrusts) {
		this.totalTaxableDistributionsFromNonComplyingTrusts = totalTaxableDistributionsFromNonComplyingTrusts;
	}

	public void setTotalTaxDeducted(Money totalTaxDeducted) {
		this.totalTaxDeducted = totalTaxDeducted;
	}

	public void setTotalTaxPaidByTrustees(Money totalTaxPaidByTrustees) {
		this.totalTaxPaidByTrustees = totalTaxPaidByTrustees;
	}

	public void setTransferRefundToSomeoneElsesIncomeTaxAccountAssociated(boolean transferRefundToSomeoneElsesIncomeTaxAccountAssociated) {
		this.transferRefundToSomeoneElsesIncomeTaxAccountAssociated = transferRefundToSomeoneElsesIncomeTaxAccountAssociated;
	}

	public void setTransferRefundToSomeoneElsesStudentLoanAssociated(boolean transferRefundToSomeoneElsesStudentLoanAssociated) {
		this.transferRefundToSomeoneElsesStudentLoanAssociated = transferRefundToSomeoneElsesStudentLoanAssociated;
	}

	public void setTrustOrEstateIncomeFromNZReceived(boolean trustOrEstateIncomeFromNZReceived) {
		this.trustOrEstateIncomeFromNZReceived = trustOrEstateIncomeFromNZReceived;
	}

	public void setUnpaidMajorWorkingShareholderWfFTCELigible(boolean unpaidMajorWorkingShareholderWfFTCELigible) {
		this.unpaidMajorWorkingShareholderWfFTCELigible = unpaidMajorWorkingShareholderWfFTCELigible;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public ResidentialPropertyInterestClaimedReason getResidentialPropertyInterestClaimedReason() {
		return residentialPropertyInterestClaimedReason;
	}

	public void setResidentialPropertyInterestClaimedReason(
			ResidentialPropertyInterestClaimedReason residentialPropertyInterestClaimedReason) {
		this.residentialPropertyInterestClaimedReason = residentialPropertyInterestClaimedReason;
	}

	public boolean isPortfolioInvestmentEntityIncomeReceived() {
		return portfolioInvestmentEntityIncomeReceived;
	}

	public void setPortfolioInvestmentEntityIncomeReceived(boolean portfolioInvestmentEntityIncomeReceived) {
		this.portfolioInvestmentEntityIncomeReceived = portfolioInvestmentEntityIncomeReceived;
	}

	public Money getTotalPIEDeductions() {
		return totalPIEDeductions;
	}

	public Money getTotalPIEIncome() {
		return totalPIEIncome;
	}

	public Money getPieCalculationOutcome() {
		return pieCalculationOutcome;
	}

	public void setTotalPIEDeductions(Money totalPIEDeductions) {
		this.totalPIEDeductions = totalPIEDeductions;
	}

	public void setTotalPIEIncome(Money totalPIEIncome) {
		this.totalPIEIncome = totalPIEIncome;
	}

	public void setPieCalculationOutcome(Money pieCalculationOutcome) {
		this.pieCalculationOutcome = pieCalculationOutcome;
	}
}
