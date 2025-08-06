package com.github.taxbeans.forms.nz;

import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.taxbeans.forms.LeftAlign;
import com.github.taxbeans.forms.RightAlign;
import com.github.taxbeans.forms.Skip;
import com.github.taxbeans.forms.UseTrueFalseMappings;
import com.github.taxbeans.forms.common.FormDestination;

public class IR7Form2019 implements FormDestination {
	
	@Skip
	private int yearEnded;

	@Skip
	private String fullName;

	@Skip
	private String destinationDirectory;

	@RightAlign(9)
	private String irdNumber;

	final static Logger logger = LoggerFactory.getLogger(IR7Form2019.class);

	private int year = 2019;

	@Skip
	private String personalisedNaming;
	
	private String partnershipNameLine1;
	
	private String partnershipNameLine2;
	
	private String partnershipTradingNameLine1;
	
	private String partnershipTradingNameLine2;
	
	private String postalAddressLine1;
	
	private String postalAddressLine2;
	
	private String physicalAddressLine1;
	
	private String physicalAddressLine2;
	
	private String bicCode;
	
	private String daytimePhoneNumberPrefix;
	
	@LeftAlign(9)
	private String daytimePhoneNumberSuffix;
	
	@UseTrueFalseMappings
	private boolean firstReturnRadio;
	
	@UseTrueFalseMappings
	private boolean partnershipCeasedRadio;
	
	@UseTrueFalseMappings
	private boolean schedularPaymentsRadio;

	@UseTrueFalseMappings
	private boolean nzInterestRadio;
	
	@UseTrueFalseMappings
	private boolean dividendsRadio;
	
	@UseTrueFalseMappings
	private boolean maoriTaxableDistributions;
	
	@UseTrueFalseMappings
	private boolean incomeFromAnotherPartnership;
	
	@UseTrueFalseMappings
	private boolean incomeFromAnotherLTC;
	
	@UseTrueFalseMappings
	private boolean overseasIncome;
	
	@UseTrueFalseMappings
	private boolean businessIncome;
	
	@UseTrueFalseMappings
	private boolean rentalIncomeRadio;
	
	@UseTrueFalseMappings
	private boolean otherIncomeRadio;
	
	@UseTrueFalseMappings
	private boolean expenseClaimRadio;

	
	@UseTrueFalseMappings
	private boolean partnershipOrLTCRadio;
	
	@UseTrueFalseMappings
	private boolean partnershipCFCRadio;
	
	@UseTrueFalseMappings
	private boolean laqcTransitionRadio;
	
	@RightAlign(11)
	private Money netProfitOrLoss;
	
	@RightAlign(11)
	private Money totalIncome;
	
	@RightAlign(11)
	private Money totalIncomeAfterExpenses;

//	private String calculateMinusSign(Money value) {
//		return value.signum() < 0 ? "-" : "";
//	}

	public int getYear() {
		return year;
	}

	public int getYearEnded() {
		return yearEnded;
	}

	public void setYearEnded(int yearEnded) {
		this.yearEnded = yearEnded;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullname(String fullName) {
		this.fullName = fullName;
	}

	public void setIrdNumber(String irdNumber) {
		irdNumber = irdNumber.replace("-", "");
		this.irdNumber = irdNumber;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getIrdNumber() {
		return irdNumber;
	}

	public String getDestinationDirectory() {
		return destinationDirectory;
	}

	public void setDestinationDirectory(String destinationDirectory) {
		this.destinationDirectory = destinationDirectory;
	}

	public String getPersonalisedNaming() {
		return personalisedNaming;
	}

	public void setPersonalisedNaming(String personalisedNaming) {
		this.personalisedNaming = personalisedNaming;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getPartnershipNameLine1() {
		return partnershipNameLine1;
	}

	public void setPartnershipNameLine1(String partnershipNameLine1) {
		this.partnershipNameLine1 = partnershipNameLine1;
	}

	public String getPartnershipNameLine2() {
		return partnershipNameLine2;
	}

	public void setPartnershipNameLine2(String partnershipNameLine2) {
		this.partnershipNameLine2 = partnershipNameLine2;
	}

	public String getPartnershipTradingNameLine1() {
		return partnershipTradingNameLine1;
	}

	public void setPartnershipTradingNameLine1(String partnershipTradingNameLine1) {
		this.partnershipTradingNameLine1 = partnershipTradingNameLine1;
	}

	public String getPartnershipTradingNameLine2() {
		return partnershipTradingNameLine2;
	}

	public void setPartnershipTradingNameLine2(String partnershipTradingNameLine2) {
		this.partnershipTradingNameLine2 = partnershipTradingNameLine2;
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

	public String getPhysicalAddressLine2() {
		return physicalAddressLine2;
	}

	public void setPhysicalAddressLine2(String physicalAddressLine2) {
		this.physicalAddressLine2 = physicalAddressLine2;
	}

	public String getPhysicalAddressLine1() {
		return physicalAddressLine1;
	}

	public void setPhysicalAddressLine1(String physicalAddressLine1) {
		this.physicalAddressLine1 = physicalAddressLine1;
	}

	public String getBicCode() {
		return bicCode;
	}

	public void setBicCode(String bicCode) {
		this.bicCode = bicCode;
	}

	public String getDaytimePhoneNumberPrefix() {
		return daytimePhoneNumberPrefix;
	}

	public void setDaytimePhoneNumberPrefix(String daytimePhoneNumberPrefix) {
		this.daytimePhoneNumberPrefix = daytimePhoneNumberPrefix;
	}

	public String getDaytimePhoneNumberSuffix() {
		return daytimePhoneNumberSuffix;
	}

	public void setDaytimePhoneNumberSuffix(String daytimePhoneNumberSuffix) {
		this.daytimePhoneNumberSuffix = daytimePhoneNumberSuffix;
	}

	public boolean isFirstReturnRadio() {
		return firstReturnRadio;
	}

	public void setFirstReturnRadio(boolean firstReturnRadio) {
		this.firstReturnRadio = firstReturnRadio;
	}

	public boolean isPartnershipCeasedRadio() {
		return partnershipCeasedRadio;
	}

	public void setPartnershipCeasedRadio(boolean partnershipCeasedRadio) {
		this.partnershipCeasedRadio = partnershipCeasedRadio;
	}

	public boolean isSchedularPaymentsRadio() {
		return schedularPaymentsRadio;
	}

	public void setSchedularPaymentsRadio(boolean schedularPaymentsRadio) {
		this.schedularPaymentsRadio = schedularPaymentsRadio;
	}

	public boolean isNzInterestRadio() {
		return nzInterestRadio;
	}

	public void setNzInterestRadio(boolean nzInterestRadio) {
		this.nzInterestRadio = nzInterestRadio;
	}

	public boolean isDividendsRadio() {
		return dividendsRadio;
	}

	public void setDividendsRadio(boolean dividendsRadio) {
		this.dividendsRadio = dividendsRadio;
	}

	public boolean isMaoriTaxableDistributions() {
		return maoriTaxableDistributions;
	}

	public void setMaoriTaxableDistributions(boolean maoriTaxableDistributions) {
		this.maoriTaxableDistributions = maoriTaxableDistributions;
	}

	public boolean isIncomeFromAnotherPartnership() {
		return incomeFromAnotherPartnership;
	}

	public void setIncomeFromAnotherPartnership(boolean incomeFromAnotherPartnership) {
		this.incomeFromAnotherPartnership = incomeFromAnotherPartnership;
	}

	public boolean isIncomeFromAnotherLTC() {
		return incomeFromAnotherLTC;
	}

	public void setIncomeFromAnotherLTC(boolean incomeFromAnotherLTC) {
		this.incomeFromAnotherLTC = incomeFromAnotherLTC;
	}

	public boolean isBusinessIncome() {
		return businessIncome;
	}

	public void setBusinessIncome(boolean businessIncome) {
		this.businessIncome = businessIncome;
	}

	public boolean isOverseasIncome() {
		return overseasIncome;
	}

	public void setOverseasIncome(boolean overseasIncome) {
		this.overseasIncome = overseasIncome;
	}

	public boolean isRentalIncomeRadio() {
		return rentalIncomeRadio;
	}

	public void setRentalIncomeRadio(boolean rentalIncomeRadio) {
		this.rentalIncomeRadio = rentalIncomeRadio;
	}

	public boolean isOtherIncomeRadio() {
		return otherIncomeRadio;
	}

	public void setOtherIncomeRadio(boolean otherIncomeRadio) {
		this.otherIncomeRadio = otherIncomeRadio;
	}

	public boolean isExpenseClaimRadio() {
		return expenseClaimRadio;
	}

	public void setExpenseClaimRadio(boolean expenseClaimRadio) {
		this.expenseClaimRadio = expenseClaimRadio;
	}

	public boolean isPartnershipOrLTCRadio() {
		return partnershipOrLTCRadio;
	}

	/**
	 * true means it's a partnership
	 */
	public void setPartnershipOrLTCRadio(boolean partnershipOrLTCRadio) {
		this.partnershipOrLTCRadio = partnershipOrLTCRadio;
	}

	public boolean isPartnershipCFCRadio() {
		return partnershipCFCRadio;
	}

	public void setPartnershipCFCRadio(boolean partnershipCFCRadio) {
		this.partnershipCFCRadio = partnershipCFCRadio;
	}

	public boolean isLaqcTransitionRadio() {
		return laqcTransitionRadio;
	}

	public void setLaqcTransitionRadio(boolean laqcTransitionRadio) {
		this.laqcTransitionRadio = laqcTransitionRadio;
	}

	public Money getTotalIncome() {
		return totalIncome;
	}

	public void setTotalIncome(Money totalIncome) {
		this.totalIncome = totalIncome;
	}

	public Money getTotalIncomeAfterExpenses() {
		return totalIncomeAfterExpenses;
	}

	public void setTotalIncomeAfterExpenses(Money totalIncomeAfterExpenses) {
		this.totalIncomeAfterExpenses = totalIncomeAfterExpenses;
	}

	public Money getNetProfitOrLoss() {
		return netProfitOrLoss;
	}

	public void setNetProfitOrLoss(Money netProfitOrLoss) {
		this.netProfitOrLoss = netProfitOrLoss;
	}

}
