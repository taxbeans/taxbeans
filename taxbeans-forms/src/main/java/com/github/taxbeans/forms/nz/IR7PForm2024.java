package com.github.taxbeans.forms.nz;

import java.math.BigDecimal;

import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.taxbeans.forms.Percent2DecimalPlaces;
import com.github.taxbeans.forms.RightAlign;
import com.github.taxbeans.forms.Skip;
import com.github.taxbeans.forms.common.FormDestination;

public class IR7PForm2024 implements FormDestination {

	@Skip
	private int yearEnded;

	@Skip
	private String fullName;

	@Skip
	private String destinationDirectory;

	@RightAlign(9)
	private String irdNumber;

	final static Logger logger = LoggerFactory.getLogger(IR7PForm2024.class);

	private int year = 2024;

	@Skip
	private String personalisedNaming;
//
	private String partnershipName;

	private String partnersName1;

	private String partnersIRDNumber1;

	@Percent2DecimalPlaces
	private BigDecimal partnersProportion1;

	private Money partnersInterest1;

	private Money partnersDividends1;

	private Money partnersMA1;

	private Money partnersOA1;

	private Money partnersRentalIncome1;

	private Money partnersPassiveIncome1;

	private Money partnersOtherIncome1;

	private Money partnersTotalResidentialIncome1;

	private Money partnersTotalResidentialIncome2;

	private Money residentialRentalDeductions1;

	private Money residentialRentalDeductions2;

	private Money partnersLAQCLoss1;

	private Money partnersTotalIncome1;

	private Money partnersExtinguishedLosses1;

	private Money partnersOverseasTaxPaid1;

	private Money partnersImputationCredits1;

	private Money partnersOtherTaxCredits1;

	private String partnersName2,

	partnersIRDNumber2;

	private Money partnersInterest2,

	partnersDividends2,

	partnersMA2,

	partnersOA2,

	partnersRentalIncome2,

	partnersPassiveIncome2,

	partnersOtherIncome2,

	partnersLAQCLoss2,

	partnersTotalIncome2,

	partnersExtinguishedLosses2,

	partnersOverseasTaxPaid2,

	partnersImputationCredits2,

	partnersOtherTaxCredits2;

	@Percent2DecimalPlaces
	private BigDecimal partnersProportion2;

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

	public String getPartnershipName() {
		return partnershipName;
	}

	public void setPartnershipName(String partnershipName) {
		this.partnershipName = partnershipName;
	}

	public String getPartnersName1() {
		return partnersName1;
	}

	public void setPartnersName1(String partnersName1) {
		this.partnersName1 = partnersName1;
	}

	public String getPartnersIRDNumber1() {
		return partnersIRDNumber1;
	}

	public void setPartnersIRDNumber1(String partnersIRDNumber1) {
		this.partnersIRDNumber1 = partnersIRDNumber1.replace("-", "");
	}

	public BigDecimal getPartnersProportion1() {
		return partnersProportion1;
	}

	public void setPartnersProportion1(BigDecimal partnersProportion1) {
		this.partnersProportion1 = partnersProportion1;
	}

	public Money getPartnersInterest1() {
		return partnersInterest1;
	}

	public void setPartnersInterest1(Money partnersInterest1) {
		this.partnersInterest1 = partnersInterest1;
	}

	public Money getPartnersDividends1() {
		return partnersDividends1;
	}

	public void setPartnersDividends1(Money partnersDividends1) {
		this.partnersDividends1 = partnersDividends1;
	}

	public Money getPartnersMA1() {
		return partnersMA1;
	}

	public void setPartnersMA1(Money partnersMA1) {
		this.partnersMA1 = partnersMA1;
	}

	public Money getPartnersOA1() {
		return partnersOA1;
	}

	public void setPartnersOA1(Money partnersOA1) {
		this.partnersOA1 = partnersOA1;
	}

	public Money getPartnersRentalIncome1() {
		return partnersRentalIncome1;
	}

	public void setPartnersRentalIncome1(Money partnersRentalIncome1) {
		this.partnersRentalIncome1 = partnersRentalIncome1;
	}

	public Money getPartnersPassiveIncome1() {
		return partnersPassiveIncome1;
	}

	public void setPartnersPassiveIncome1(Money partnersPassiveIncome1) {
		this.partnersPassiveIncome1 = partnersPassiveIncome1;
	}

	public Money getPartnersOtherIncome1() {
		return partnersOtherIncome1;
	}

	public void setPartnersOtherIncome1(Money partnersOtherIncome1) {
		this.partnersOtherIncome1 = partnersOtherIncome1;
	}

	public Money getPartnersLAQCLoss1() {
		return partnersLAQCLoss1;
	}

	public void setPartnersLAQCLoss1(Money partnersLAQCLoss1) {
		this.partnersLAQCLoss1 = partnersLAQCLoss1;
	}

	public Money getPartnersTotalIncome1() {
		return partnersTotalIncome1;
	}

	public void setPartnersTotalIncome1(Money partnersTotalIncome1) {
		this.partnersTotalIncome1 = partnersTotalIncome1;
	}

	public Money getPartnersExtinguishedLosses1() {
		return partnersExtinguishedLosses1;
	}

	public void setPartnersExtinguishedLosses1(Money partnersExtinguishedLosses1) {
		this.partnersExtinguishedLosses1 = partnersExtinguishedLosses1;
	}

	public Money getPartnersOverseasTaxPaid1() {
		return partnersOverseasTaxPaid1;
	}

	public void setPartnersOverseasTaxPaid1(Money partnersOverseasTaxPaid1) {
		this.partnersOverseasTaxPaid1 = partnersOverseasTaxPaid1;
	}

	public Money getPartnersImputationCredits1() {
		return partnersImputationCredits1;
	}

	public void setPartnersImputationCredits1(Money partnersImputationCredits1) {
		this.partnersImputationCredits1 = partnersImputationCredits1;
	}

	public Money getPartnersOtherTaxCredits1() {
		return partnersOtherTaxCredits1;
	}

	public void setPartnersOtherTaxCredits1(Money partnersOtherTaxCredits1) {
		this.partnersOtherTaxCredits1 = partnersOtherTaxCredits1;
	}

	public String getPartnersName2() {
		return partnersName2;
	}

	public void setPartnersName2(String partnersName2) {
		this.partnersName2 = partnersName2;
	}

	public String getPartnersIRDNumber2() {
		return partnersIRDNumber2;
	}

	public void setPartnersIRDNumber2(String partnersIRDNumber2) {
		this.partnersIRDNumber2 = partnersIRDNumber2.replace("-", "");
	}

	public Money getPartnersInterest2() {
		return partnersInterest2;
	}

	public void setPartnersInterest2(Money partnersInterest2) {
		this.partnersInterest2 = partnersInterest2;
	}

	public Money getPartnersDividends2() {
		return partnersDividends2;
	}

	public void setPartnersDividends2(Money partnersDividends2) {
		this.partnersDividends2 = partnersDividends2;
	}

	public Money getPartnersMA2() {
		return partnersMA2;
	}

	public void setPartnersMA2(Money partnersMA2) {
		this.partnersMA2 = partnersMA2;
	}

	public Money getPartnersOA2() {
		return partnersOA2;
	}

	public void setPartnersOA2(Money partnersOA2) {
		this.partnersOA2 = partnersOA2;
	}

	public Money getPartnersRentalIncome2() {
		return partnersRentalIncome2;
	}

	public void setPartnersRentalIncome2(Money partnersRentalIncome2) {
		this.partnersRentalIncome2 = partnersRentalIncome2;
	}

	public Money getPartnersPassiveIncome2() {
		return partnersPassiveIncome2;
	}

	public void setPartnersPassiveIncome2(Money partnersPassiveIncome2) {
		this.partnersPassiveIncome2 = partnersPassiveIncome2;
	}

	public Money getPartnersOtherIncome2() {
		return partnersOtherIncome2;
	}

	public void setPartnersOtherIncome2(Money partnersOtherIncome2) {
		this.partnersOtherIncome2 = partnersOtherIncome2;
	}

	public Money getPartnersLAQCLoss2() {
		return partnersLAQCLoss2;
	}

	public void setPartnersLAQCLoss2(Money partnersLAQCLoss2) {
		this.partnersLAQCLoss2 = partnersLAQCLoss2;
	}

	public Money getPartnersTotalIncome2() {
		return partnersTotalIncome2;
	}

	public void setPartnersTotalIncome2(Money partnersTotalIncome2) {
		this.partnersTotalIncome2 = partnersTotalIncome2;
	}

	public Money getPartnersExtinguishedLosses2() {
		return partnersExtinguishedLosses2;
	}

	public void setPartnersExtinguishedLosses2(Money partnersExtinguishedLosses2) {
		this.partnersExtinguishedLosses2 = partnersExtinguishedLosses2;
	}

	public Money getPartnersOverseasTaxPaid2() {
		return partnersOverseasTaxPaid2;
	}

	public void setPartnersOverseasTaxPaid2(Money partnersOverseasTaxPaid2) {
		this.partnersOverseasTaxPaid2 = partnersOverseasTaxPaid2;
	}

	public Money getPartnersImputationCredits2() {
		return partnersImputationCredits2;
	}

	public void setPartnersImputationCredits2(Money partnersImputationCredits2) {
		this.partnersImputationCredits2 = partnersImputationCredits2;
	}

	public Money getPartnersOtherTaxCredits2() {
		return partnersOtherTaxCredits2;
	}

	public void setPartnersOtherTaxCredits2(Money partnersOtherTaxCredits2) {
		this.partnersOtherTaxCredits2 = partnersOtherTaxCredits2;
	}

	public BigDecimal getPartnersProportion2() {
		return partnersProportion2;
	}

	public void setPartnersProportion2(BigDecimal partnersProportion2) {
		this.partnersProportion2 = partnersProportion2;
	}

	public Money getPartnersTotalResidentialIncome2() {
		return partnersTotalResidentialIncome2;
	}

	public void setPartnersTotalResidentialIncome2(Money partnersTotalResidentialIncome2) {
		this.partnersTotalResidentialIncome2 = partnersTotalResidentialIncome2;
	}

	public Money getPartnersTotalResidentialIncome1() {
		return partnersTotalResidentialIncome1;
	}

	public void setPartnersTotalResidentialIncome1(Money partnersTotalResidentialIncome1) {
		this.partnersTotalResidentialIncome1 = partnersTotalResidentialIncome1;
	}

	public Money getResidentialRentalDeductions1() {
		return residentialRentalDeductions1;
	}

	public void setResidentialRentalDeductions1(Money residentialRentalDeductions1) {
		this.residentialRentalDeductions1 = residentialRentalDeductions1;
	}

	public Money getResidentialRentalDeductions2() {
		return residentialRentalDeductions2;
	}

	public void setResidentialRentalDeductions2(Money residentialRentalDeductions2) {
		this.residentialRentalDeductions2 = residentialRentalDeductions2;
	}
}
