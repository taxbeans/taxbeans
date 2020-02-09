package com.github.taxbeans.forms.nz;

import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
}
