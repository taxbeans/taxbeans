package com.github.taxbeans.forms.nz;

import java.math.BigDecimal;

import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.taxbeans.forms.LeftAlign;
import com.github.taxbeans.forms.Percent2DecimalPlaces;
import com.github.taxbeans.forms.RightAlign;
import com.github.taxbeans.forms.Skip;
import com.github.taxbeans.forms.UseTrueFalseMappings;
import com.github.taxbeans.forms.common.FormDestination;

public class IR7PForm2018 implements FormDestination {
	
	@Skip
	private int yearEnded;

	@Skip
	private String fullName;

	@Skip
	private String destinationDirectory;

	@RightAlign(9)
	private String irdNumber;

	final static Logger logger = LoggerFactory.getLogger(IR7PForm2018.class);

	private int year = 2018;

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
	
	private Money partnersLAQCLoss1;
	
	private Money partnersTotalIncome1;
	
	private Money partnersExtinguishedLosses1;
	
	private Money partnersOverseasTaxPaid1;
	
	private Money partnersImputationCredits1;
	
	private Money partnersOtherTaxCredits1;

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
		partnersIRDNumber1 = partnersIRDNumber1.replace("-", "");
		return partnersIRDNumber1;
	}

	public void setPartnersIRDNumber1(String partnersIRDNumber1) {
		this.partnersIRDNumber1 = partnersIRDNumber1;
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
}
