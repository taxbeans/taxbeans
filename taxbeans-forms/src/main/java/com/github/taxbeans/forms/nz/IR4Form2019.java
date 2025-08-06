package com.github.taxbeans.forms.nz;

import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.taxbeans.forms.IncludeFormatSpacing;
import com.github.taxbeans.forms.OmitCents;
import com.github.taxbeans.forms.RightAlign;
import com.github.taxbeans.forms.Skip;
import com.github.taxbeans.forms.UseChildFields;
import com.github.taxbeans.forms.UseTrueFalseMappings;
import com.github.taxbeans.forms.common.FormDestination;
import com.github.taxbeans.model.nz.NZBankAccount;

public class IR4Form2019 implements FormDestination {

	@Skip
	private String destinationDirectory;

	@RightAlign(9)
	private String irdNumber;
	
	private String companyTradingNameLine1;
	
	private String companyTradingNameLine2;
	
	private String postalAddressLine1;
	
	private String postalAddressLine2;
	
	private String streetAddressLine1;
	
	private String streetAddressLine2;
	
	private String bicCode;
	
	private String phonePrefix;
	
	private String phoneNumber;
	
	@UseChildFields
	private NZBankAccount bankAccount;
	
	@UseTrueFalseMappings
	private boolean nonResident;
	
	@UseTrueFalseMappings
	private boolean imputationReturnIncluded;
	
	@UseTrueFalseMappings
	private boolean imputationMonetaryEntries;
	
	@UseTrueFalseMappings
	private boolean companyCeased;
	
	@UseTrueFalseMappings
	private boolean schedularPayments;
	
	@UseTrueFalseMappings
	private boolean nzInterest;
	
	@UseTrueFalseMappings
	private boolean nzDividends;
	
	@UseTrueFalseMappings
	private boolean maoriDistributions;
	
	@UseTrueFalseMappings
	private boolean partnershipIncome;
	
	@RightAlign(11)
	private Money totalTaxCredits;
	
	@UseTrueFalseMappings
	private boolean overseasIncome;

	@UseTrueFalseMappings
	private boolean businessIncome;

	@UseTrueFalseMappings
	private boolean overseasPremiums;

	@UseTrueFalseMappings
	private boolean otherIncome;
	
	@RightAlign(11)
	private Money businessNetProfit;
	
	@RightAlign(11)
	private Money netProfitBeforeDonations;
	
	@UseTrueFalseMappings
	private boolean donations;
	
	@RightAlign(11)
	private Money netProfitAfterDonations;
	
	@UseTrueFalseMappings
	private boolean netLossesBroughtForward;
	
	@RightAlign(11)
	private Money netProfitAfterLossesBroughtForward;
	
	@UseTrueFalseMappings
	private boolean netLossesFromOtherCompanies;
	
	@RightAlign(11)
	private Money taxableIncome;
	
	@RightAlign(9)
	@OmitCents
	@IncludeFormatSpacing
	private Money copyOfTaxableIncome;
	
	@RightAlign(11)
	private Money totalTaxPayable;
	
	@RightAlign(11)
	private Money overseasTaxPaid;
	
	@RightAlign(11)
	private Money box29D;
	
	@RightAlign(11)
	private Money foreignInvestorTaxCredit;
	
	@RightAlign(11)
	private Money box29F;

	@RightAlign(11)
	private Money copyOfTotalImputationCredits;
	
	@RightAlign(11)
	private Money box29H;
	
	@RightAlign(11)
	private Money copyOfTotalTaxCredits;
	
	@RightAlign(11)
	private Money copyOfRLWTCredit;
	
	@RightAlign(11)
	private Money residualIncomeTax;
	
	@UseTrueFalseMappings
	private Boolean creditOrDebit;
	
	@RightAlign(11)
	private Money provisionalTaxPaid;
	
	@RightAlign(11)
	private Money taxAmountOwed;
	
	@UseTrueFalseMappings
	private Boolean refundOrTaxToPay;
	
	@UseTrueFalseMappings
	private boolean initialProvisionalTaxLiability;
	
	private String provisionalTaxOption;
	
	@RightAlign(11)
	private Money provisionalTaxDue;
	
	@UseTrueFalseMappings
	private boolean nonResidentPayments;
	
	@UseTrueFalseMappings
	private boolean cfcOrFifIncome;
	
	@UseTrueFalseMappings
	private boolean sharesRepurchased;
	
	@UseTrueFalseMappings
	private boolean foreignSourcedDividends;
	
	@UseTrueFalseMappings
	private boolean controlledByNonResidents;
	
	private String lowestEconomicInterests;
	
	private String shareholder1IrdNumber;
	
	@RightAlign(11)
	private Money shareholder1Remuneration;
	
	@RightAlign(11)
	private Money shareholder1ValueOfLoans;

	@RightAlign(11)
	private Money shareholder1CurrentAccountBalance;
	
	@RightAlign(11)
	private Money shareholder1LossOffsets;
	
	@RightAlign(11)
	private Money shareholder1SubventionPayments;
	
	private String shareholder2IrdNumber;
	
	@RightAlign(11)
	private Money shareholder2Remuneration;
	
	@RightAlign(11)
	private Money shareholder2ValueOfLoans;
	
	@RightAlign(11)
	private Money shareholder2CurrentAccountBalance;
	
	@RightAlign(11)
	private Money shareholder2LossOffsets;
	
	@RightAlign(11)
	private Money shareholder2SubventionPayments;
	
	final Logger logger = LoggerFactory.getLogger(IR4Form2019.class);

	@Skip
	private String personalisedNaming;

	private int year = 2019;
	
	public String calculateMinusSign(Money value) {
		return value.signum() < 0 ? "-" : "";
	}

	public String getDestinationDirectory() {
		return destinationDirectory;
	}

	public String getIrdNumber() {
		return irdNumber;
	}
	
	public String getPersonalisedNaming() {
		return personalisedNaming;
	}

	public int getYear() {
		return year;
	}

	public void setDestinationDirectory(String destinationDirectory) {
		this.destinationDirectory = destinationDirectory;
	}

	public void setIrdNumber(String irdNumber) {
		this.irdNumber = irdNumber;
	}

	public void setPersonalisedNaming(String personalisedNaming) {
		this.personalisedNaming = personalisedNaming;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getCompanyTradingNameLine1() {
		return companyTradingNameLine1;
	}

	public void setCompanyTradingNameLine1(String companyTradingNameLine1) {
		this.companyTradingNameLine1 = companyTradingNameLine1;
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

	public String getBicCode() {
		return bicCode;
	}

	public void setBicCode(String bicCode) {
		this.bicCode = bicCode;
	}

	public String getPhonePrefix() {
		return phonePrefix;
	}

	public void setPhonePrefix(String phonePrefix) {
		this.phonePrefix = phonePrefix;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public NZBankAccount getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(NZBankAccount bankAccount) {
		this.bankAccount = bankAccount;
	}

	public boolean isNonResident() {
		return nonResident;
	}

	public void setNonResident(boolean nonResident) {
		this.nonResident = nonResident;
	}

	public boolean isImputationReturnIncluded() {
		return imputationReturnIncluded;
	}

	public void setImputationReturnIncluded(boolean imputationReturnIncluded) {
		this.imputationReturnIncluded = imputationReturnIncluded;
	}

	public boolean isImputationMonetaryEntries() {
		return imputationMonetaryEntries;
	}

	public void setImputationMonetaryEntries(boolean imputationMonetaryEntries) {
		this.imputationMonetaryEntries = imputationMonetaryEntries;
	}

	public boolean isCompanyCeased() {
		return companyCeased;
	}

	public void setCompanyCeased(boolean companyCeased) {
		this.companyCeased = companyCeased;
	}

	public boolean isSchedularPayments() {
		return schedularPayments;
	}

	public void setSchedularPayments(boolean schedularPayments) {
		this.schedularPayments = schedularPayments;
	}

	public boolean isNzInterest() {
		return nzInterest;
	}

	public void setNzInterest(boolean nzInterest) {
		this.nzInterest = nzInterest;
	}

	public boolean isNzDividends() {
		return nzDividends;
	}

	public void setNzDividends(boolean nzDividends) {
		this.nzDividends = nzDividends;
	}

	public boolean isMaoriDistributions() {
		return maoriDistributions;
	}

	public void setMaoriDistributions(boolean maoriDistributions) {
		this.maoriDistributions = maoriDistributions;
	}

	public boolean isPartnershipIncome() {
		return partnershipIncome;
	}

	public void setPartnershipIncome(boolean partnershipIncome) {
		this.partnershipIncome = partnershipIncome;
	}

	public Money getTotalTaxCredits() {
		return totalTaxCredits;
	}

	public void setTotalTaxCredits(Money totalTaxCredits) {
		this.totalTaxCredits = totalTaxCredits;
	}

	public boolean isOverseasIncome() {
		return overseasIncome;
	}

	public void setOverseasIncome(boolean overseasIncome) {
		this.overseasIncome = overseasIncome;
	}

	public boolean isBusinessIncome() {
		return businessIncome;
	}

	public void setBusinessIncome(boolean businessIncome) {
		this.businessIncome = businessIncome;
	}

	public boolean isOverseasPremiums() {
		return overseasPremiums;
	}

	public void setOverseasPremiums(boolean overseasPremiums) {
		this.overseasPremiums = overseasPremiums;
	}

	public boolean isOtherIncome() {
		return otherIncome;
	}

	public void setOtherIncome(boolean otherIncome) {
		this.otherIncome = otherIncome;
	}

	public Money getBusinessNetProfit() {
		return businessNetProfit;
	}

	public void setBusinessNetProfit(Money businessNetProfit) {
		this.businessNetProfit = businessNetProfit;
	}

	public Money getNetProfitBeforeDonations() {
		return netProfitBeforeDonations;
	}

	public void setNetProfitBeforeDonations(Money netProfitBeforeDonations) {
		this.netProfitBeforeDonations = netProfitBeforeDonations;
	}

	public boolean isDonations() {
		return donations;
	}

	public void setDonations(boolean donations) {
		this.donations = donations;
	}

	public Money getNetProfitAfterDonations() {
		return netProfitAfterDonations;
	}

	public void setNetProfitAfterDonations(Money netProfitAfterDonations) {
		this.netProfitAfterDonations = netProfitAfterDonations;
	}

	public boolean isNetLossesBroughtForward() {
		return netLossesBroughtForward;
	}

	public void setNetLossesBroughtForward(boolean netLossesBroughtForward) {
		this.netLossesBroughtForward = netLossesBroughtForward;
	}

	public Money getNetProfitAfterLossesBroughtForward() {
		return netProfitAfterLossesBroughtForward;
	}

	public void setNetProfitAfterLossesBroughtForward(Money netProfitAfterLossesBroughtForward) {
		this.netProfitAfterLossesBroughtForward = netProfitAfterLossesBroughtForward;
	}

	public boolean isNetLossesFromOtherCompanies() {
		return netLossesFromOtherCompanies;
	}

	public void setNetLossesFromOtherCompanies(boolean netLossesFromOtherCompanies) {
		this.netLossesFromOtherCompanies = netLossesFromOtherCompanies;
	}

	public Money getTaxableIncome() {
		return taxableIncome;
	}

	public void setTaxableIncome(Money taxableIncome) {
		this.taxableIncome = taxableIncome;
	}

	public Money getCopyOfTaxableIncome() {
		return copyOfTaxableIncome;
	}

	public void setCopyOfTaxableIncome(Money copyOfTaxableIncome) {
		this.copyOfTaxableIncome = copyOfTaxableIncome;
	}

	public Money getTotalTaxPayable() {
		return totalTaxPayable;
	}

	public void setTotalTaxPayable(Money totalTaxPayable) {
		this.totalTaxPayable = totalTaxPayable;
	}

	public Money getOverseasTaxPaid() {
		return overseasTaxPaid;
	}

	public void setOverseasTaxPaid(Money overseasTaxPaid) {
		this.overseasTaxPaid = overseasTaxPaid;
	}

	public Money getBox29D() {
		return box29D;
	}

	public void setBox29D(Money box29d) {
		box29D = box29d;
	}

	public Money getForeignInvestorTaxCredit() {
		return foreignInvestorTaxCredit;
	}

	public void setForeignInvestorTaxCredit(Money foreignInvestorTaxCredit) {
		this.foreignInvestorTaxCredit = foreignInvestorTaxCredit;
	}

	public Money getBox29F() {
		return box29F;
	}

	public void setBox29F(Money box29f) {
		box29F = box29f;
	}

	public Money getCopyOfTotalImputationCredits() {
		return copyOfTotalImputationCredits;
	}

	public void setCopyOfTotalImputationCredits(Money copyOfTotalImputationCredits) {
		this.copyOfTotalImputationCredits = copyOfTotalImputationCredits;
	}

	public Money getBox29H() {
		return box29H;
	}

	public void setBox29H(Money box29h) {
		box29H = box29h;
	}

	public Money getCopyOfTotalTaxCredits() {
		return copyOfTotalTaxCredits;
	}

	public void setCopyOfTotalTaxCredits(Money copyOfTotalTaxCredits) {
		this.copyOfTotalTaxCredits = copyOfTotalTaxCredits;
	}

	public Money getCopyOfRLWTCredit() {
		return copyOfRLWTCredit;
	}

	public void setCopyOfRLWTCredit(Money copyOfRLWTCredit) {
		this.copyOfRLWTCredit = copyOfRLWTCredit;
	}

	public Money getResidualIncomeTax() {
		return residualIncomeTax;
	}

	public void setResidualIncomeTax(Money residualIncomeTax) {
		this.residualIncomeTax = residualIncomeTax;
	}

	public Money getProvisionalTaxPaid() {
		return provisionalTaxPaid;
	}

	public void setProvisionalTaxPaid(Money provisionalTaxPaid) {
		this.provisionalTaxPaid = provisionalTaxPaid;
	}

	public Money getTaxAmountOwed() {
		return taxAmountOwed;
	}

	public void setTaxAmountOwed(Money taxAmountOwed) {
		this.taxAmountOwed = taxAmountOwed;
	}

	public boolean isInitialProvisionalTaxLiability() {
		return initialProvisionalTaxLiability;
	}

	public void setInitialProvisionalTaxLiability(boolean initialProvisionalTaxLiability) {
		this.initialProvisionalTaxLiability = initialProvisionalTaxLiability;
	}

	public String getProvisionalTaxOption() {
		return provisionalTaxOption;
	}

	public void setProvisionalTaxOption(String provisionalTaxOption) {
		this.provisionalTaxOption = provisionalTaxOption;
	}

	public Money getProvisionalTaxDue() {
		return provisionalTaxDue;
	}

	public void setProvisionalTaxDue(Money provisionalTaxDue) {
		this.provisionalTaxDue = provisionalTaxDue;
	}

	public boolean isNonResidentPayments() {
		return nonResidentPayments;
	}

	public void setNonResidentPayments(boolean nonResidentPayments) {
		this.nonResidentPayments = nonResidentPayments;
	}

	public boolean isCfcOrFifIncome() {
		return cfcOrFifIncome;
	}

	public void setCfcOrFifIncome(boolean cfcOrFifIncome) {
		this.cfcOrFifIncome = cfcOrFifIncome;
	}

	public boolean isSharesRepurchased() {
		return sharesRepurchased;
	}

	public void setSharesRepurchased(boolean sharesRepurchased) {
		this.sharesRepurchased = sharesRepurchased;
	}

	public boolean isForeignSourcedDividends() {
		return foreignSourcedDividends;
	}

	public void setForeignSourcedDividends(boolean foreignSourcedDividends) {
		this.foreignSourcedDividends = foreignSourcedDividends;
	}

	public boolean isControlledByNonResidents() {
		return controlledByNonResidents;
	}

	public void setControlledByNonResidents(boolean controlledByNonResidents) {
		this.controlledByNonResidents = controlledByNonResidents;
	}

	public String getLowestEconomicInterests() {
		return lowestEconomicInterests;
	}

	public void setLowestEconomicInterests(String lowestEconomicInterests) {
		this.lowestEconomicInterests = lowestEconomicInterests;
	}

	public String getShareholder1IrdNumber() {
		return shareholder1IrdNumber;
	}

	public void setShareholder1IrdNumber(String shareholder1IrdNumber) {
		this.shareholder1IrdNumber = shareholder1IrdNumber;
	}

	public Money getShareholder1Remuneration() {
		return shareholder1Remuneration;
	}

	public void setShareholder1Remuneration(Money shareholder1Remuneration) {
		this.shareholder1Remuneration = shareholder1Remuneration;
	}

	public Money getShareholder1ValueOfLoans() {
		return shareholder1ValueOfLoans;
	}

	public void setShareholder1ValueOfLoans(Money shareholder1ValueOfLoans) {
		this.shareholder1ValueOfLoans = shareholder1ValueOfLoans;
	}

	public Money getShareholder1CurrentAccountBalance() {
		return shareholder1CurrentAccountBalance;
	}

	public void setShareholder1CurrentAccountBalance(Money shareholder1CurrentAccountBalance) {
		this.shareholder1CurrentAccountBalance = shareholder1CurrentAccountBalance;
	}

	public Money getShareholder1LossOffsets() {
		return shareholder1LossOffsets;
	}

	public void setShareholder1LossOffsets(Money shareholder1LossOffsets) {
		this.shareholder1LossOffsets = shareholder1LossOffsets;
	}

	public Money getShareholder1SubventionPayments() {
		return shareholder1SubventionPayments;
	}

	public void setShareholder1SubventionPayments(Money shareholder1SubventionPayments) {
		this.shareholder1SubventionPayments = shareholder1SubventionPayments;
	}

	public String getShareholder2IrdNumber() {
		return shareholder2IrdNumber;
	}

	public void setShareholder2IrdNumber(String shareholder2IrdNumber) {
		this.shareholder2IrdNumber = shareholder2IrdNumber;
	}

	public Money getShareholder2Remuneration() {
		return shareholder2Remuneration;
	}

	public void setShareholder2Remuneration(Money shareholder2Remuneration) {
		this.shareholder2Remuneration = shareholder2Remuneration;
	}

	public Money getShareholder2ValueOfLoans() {
		return shareholder2ValueOfLoans;
	}

	public void setShareholder2ValueOfLoans(Money shareholder2ValueOfLoans) {
		this.shareholder2ValueOfLoans = shareholder2ValueOfLoans;
	}

	public Money getShareholder2CurrentAccountBalance() {
		return shareholder2CurrentAccountBalance;
	}

	public void setShareholder2CurrentAccountBalance(Money shareholder2CurrentAccountBalance) {
		this.shareholder2CurrentAccountBalance = shareholder2CurrentAccountBalance;
	}

	public Money getShareholder2LossOffsets() {
		return shareholder2LossOffsets;
	}

	public void setShareholder2LossOffsets(Money shareholder2LossOffsets) {
		this.shareholder2LossOffsets = shareholder2LossOffsets;
	}

	public Money getShareholder2SubventionPayments() {
		return shareholder2SubventionPayments;
	}

	public void setShareholder2SubventionPayments(Money shareholder2SubventionPayments) {
		this.shareholder2SubventionPayments = shareholder2SubventionPayments;
	}

	public String getCompanyTradingNameLine2() {
		return companyTradingNameLine2;
	}

	public void setCompanyTradingNameLine2(String companyTradingNameLine2) {
		this.companyTradingNameLine2 = companyTradingNameLine2;
	}

	public Boolean getCreditOrDebit() {
		return creditOrDebit;
	}

	public void setCreditOrDebit(Boolean creditOrDebit) {
		this.creditOrDebit = creditOrDebit;
	}

	public Boolean getRefundOrTaxToPay() {
		return refundOrTaxToPay;
	}

	public void setRefundOrTaxToPay(Boolean refundOrTaxToPay) {
		this.refundOrTaxToPay = refundOrTaxToPay;
	}
}
