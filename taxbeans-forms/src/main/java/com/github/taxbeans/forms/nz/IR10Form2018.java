package com.github.taxbeans.forms.nz;

import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.taxbeans.forms.Required;
import com.github.taxbeans.forms.RightAlign;
import com.github.taxbeans.forms.RoundToDollars;
import com.github.taxbeans.forms.RoundedSum;
import com.github.taxbeans.forms.Skip;
import com.github.taxbeans.forms.UseTrueFalseMappings;

public class IR10Form2018 {
	
	private int yearEnded;

	private String fullName;

	@Skip
	private String destinationDirectory;

	@RightAlign(9)
	private String irdNumber;

	@UseTrueFalseMappings
	private boolean multipleActivityRadio;

	@RoundToDollars
	private Money grossIncome;
	
	@RoundToDollars
	private Money openingStock;
	
	@RoundToDollars
	private Money purchases;
	
	@RoundToDollars
	private Money closingStock;
	
	@RoundToDollars
	@Required
	@RoundedSum("grossIncome")
	private Money grossProfit;
	
	@RoundToDollars
	private Money interestReceived;
	
	@RoundToDollars
	private Money dividends;
	
	@RoundToDollars
	private Money leasePayments;
	
	@RoundToDollars
	private Money otherIncome;
	
	@RoundToDollars
	@Required
	@RoundedSum({"grossProfit", "interestReceived", "dividends", "leasePayments",
			"otherIncome"})
	private Money totalIncome;
	
	@RoundToDollars
	private Money badDebts;
	
	@RoundToDollars
	private Money depreciation;
	
	@RoundToDollars
	private Money insurance;
	
	@RoundToDollars
	private Money interestExpenses;
	
	@RoundToDollars
	private Money consultingFees;
	
	@RoundToDollars
	private Money rates;

	@RoundToDollars
	private Money leasePaymentExpenses;
	
	@RoundToDollars
	private Money repairs;
	
	@RoundToDollars
	private Money researchAndDevelopment;
	
	@RoundToDollars
	private Money relatedPartyRenumeration;
	
	@RoundToDollars
	private Money salaryAndWages;
	
	@RoundToDollars
	private Money subcontractorPayments;
	
	@RoundToDollars
	private Money otherExpenses;
	
	@RoundToDollars
	@Required
	@RoundedSum({"badDebts", "depreciation", "insurance", "interestExpenses",
			"consultingFees", "rates", "leasePaymentExpenses", "repairs",
			"researchAndDevelopment", "relatedPartyRenumeration", "salaryAndWages", "subcontractorPayments", 
			"otherExpenses"})			
	private Money totalExpenses;
	
	@RoundToDollars
	private Money exceptionalItems;
	
	@RoundToDollars
	@Required
	@RoundedSum(value={"totalIncome", "exceptionalItems"}, negate="totalExpenses")
	private Money netProfitBeforeTax;
	
	@RoundToDollars
	private Money taxAdjustments;
	
	@RoundToDollars
	@Required
	@RoundedSum(value="netProfitBeforeTax", negate="taxAdjustments")
	private Money taxableProfit;
	
	@RoundToDollars
	private Money accountsReceivable;
	
	@RoundToDollars
	private Money cashAndDeposits;
	
	@RoundToDollars
	private Money otherCurrentAssets;
	
	@RoundToDollars
	private Money vehicleAssets;
	
	@RoundToDollars
	private Money plantAssets;
	
	@RoundToDollars
	private Money furnitureAssets;
	
	@RoundToDollars
	private Money land;
	
	@RoundToDollars
	private Money buildings;
	
	@RoundToDollars
	private Money otherFixedAssets;
	
	@RoundToDollars
	private Money intangibles;
	
	@RoundToDollars
	private Money sharesAndDebentures;
	
	@RoundToDollars
	private Money termDeposits;
	
	@RoundToDollars
	private Money otherNonCurrentAssets;
	
	@RoundToDollars
	@Required
	@RoundedSum({"accountsReceivable", "cashAndDeposits", "otherCurrentAssets", "vehicleAssets",
			"plantAssets", "furnitureAssets", "land", "buildings",
			"otherFixedAssets", "intangibles", "sharesAndDebentures", "termDeposits",
			"otherNonCurrentAssets"})
	private Money totalAssets;
	
	@RoundToDollars
	private Money provisions;
	
	@RoundToDollars
	private Money accountsPayable;
	
	@RoundToDollars
	private Money currentLoans;
	
	@RoundToDollars
	private Money otherCurrentLiabilities;
	
	@RoundToDollars
	@Required
	@RoundedSum({"provisions", "accountsPayable", "currentLoans", "otherCurrentLiabilities"})
	private Money totalCurrentLiabilities;
	
	@RoundToDollars
	private Money nonCurrentLiabilities;
	
	@RoundToDollars
	@Required
	@RoundedSum({"totalCurrentLiabilities", "nonCurrentLiabilities"})
	private Money totalLiabilities;
	
	@RoundToDollars
	@Required
	@RoundedSum(value="totalAssets", negate="totalLiabilities")
	private Money ownersEquity;
	
	@RoundToDollars
	private Money taxDepreciation;
	
	@RoundToDollars
	private Money untaxedRealisedGains;
	
	@RoundToDollars
	private Money additionsToFixedAssets;
	
	@RoundToDollars
	private Money disposalsOfFixedAssets;
	
	@RoundToDollars
	private Money dividendsPaid;
	
	@RoundToDollars
	private Money drawings;
	
	@RoundToDollars
	private Money currentAccountClosingBalance;

	@RoundToDollars
	private Money deductibleLossOnDisposal;
	
	@Required
	private String currentAccountMinusSign;

	final static Logger logger = LoggerFactory.getLogger(IR10FormPublishedMarch2019.class);

	private int year = 2018;

	@Skip
	private String personalisedNaming;

	public int getYear() {
		return year;
	}

	public int getYearEnded() {
		return yearEnded;
	}

	public void setYearEnded(int yearEnded) {
		this.yearEnded = yearEnded;
	}

	public Money getTotalExpenses() {
		return totalExpenses;
	}

	@SuppressWarnings("unused")
	public void setTotalExpenses(Money totalExpenses) {
		if (true) {
			throw new IllegalArgumentException("this is a derived property");
		}
		this.totalExpenses = totalExpenses;
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

	public boolean isMultipleActivityRadio() {
		return multipleActivityRadio;
	}

	public void setMultipleActivityRadio(boolean multipleActivityRadio) {
		this.multipleActivityRadio = multipleActivityRadio;
	}

	public String getIrdNumber() {
		return irdNumber;
	}

	public Money getGrossIncome() {
		return grossIncome;
	}

	public void setGrossIncome(Money grossIncome) {
		this.grossIncome = grossIncome;
	}

	public Money getOpeningStock() {
		return openingStock;
	}

	public void setOpeningStock(Money openingStock) {
		this.openingStock = openingStock;
	}

	public Money getPurchases() {
		return purchases;
	}

	public void setPurchases(Money purchases) {
		this.purchases = purchases;
	}

	public Money getClosingStock() {
		return closingStock;
	}

	public void setClosingStock(Money closingStock) {
		this.closingStock = closingStock;
	}

	public Money getGrossProfit() {
		return grossProfit;
	}

	public void setGrossProfit(Money grossProfit) {
		this.grossProfit = grossProfit;
	}

	public String getDestinationDirectory() {
		return destinationDirectory;
	}

	public void setDestinationDirectory(String destinationDirectory) {
		this.destinationDirectory = destinationDirectory;
	}

	public Money getInterestReceived() {
		return interestReceived;
	}

	public void setInterestReceived(Money interestReceived) {
		this.interestReceived = interestReceived;
	}

	public Money getDividends() {
		return dividends;
	}

	public void setDividends(Money dividends) {
		this.dividends = dividends;
	}

	public Money getLeasePayments() {
		return leasePayments;
	}

	public void setLeasePayments(Money leasePayments) {
		this.leasePayments = leasePayments;
	}

	public Money getOtherIncome() {
		return otherIncome;
	}

	public void setOtherIncome(Money otherIncome) {
		this.otherIncome = otherIncome;
	}

	public Money getTotalIncome() {
		return totalIncome;
	}

	@SuppressWarnings("unused")
	public void setTotalIncome(Money totalIncome) {
		if (true) {
			throw new IllegalArgumentException("this is a derived property");
		}
		this.totalIncome = totalIncome;
	}

	public Money getBadDebts() {
		return badDebts;
	}

	public void setBadDebts(Money badDebts) {
		this.badDebts = badDebts;
	}

	public Money getDepreciation() {
		return depreciation;
	}

	public void setDepreciation(Money depreciation) {
		this.depreciation = depreciation;
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

	public Money getInsurance() {
		return insurance;
	}

	public void setInsurance(Money insurance) {
		this.insurance = insurance;
	}

	public Money getInterestExpenses() {
		return interestExpenses;
	}

	public void setInterestExpenses(Money interestExpenses) {
		this.interestExpenses = interestExpenses;
	}

	public Money getConsultingFees() {
		return consultingFees;
	}

	public void setConsultingFees(Money consultingFees) {
		this.consultingFees = consultingFees;
	}

	public Money getRates() {
		return rates;
	}

	public void setRates(Money rates) {
		this.rates = rates;
	}

	public Money getLeasePaymentExpenses() {
		return leasePaymentExpenses;
	}

	public void setLeasePaymentExpenses(Money leasePaymentExpenses) {
		this.leasePaymentExpenses = leasePaymentExpenses;
	}

	public Money getRepairs() {
		return repairs;
	}

	public void setRepairs(Money repairs) {
		this.repairs = repairs;
	}

	public Money getResearchAndDevelopment() {
		return researchAndDevelopment;
	}

	public void setResearchAndDevelopment(Money researchAndDevelopment) {
		this.researchAndDevelopment = researchAndDevelopment;
	}

	public Money getRelatedPartyRenumeration() {
		return relatedPartyRenumeration;
	}

	public void setRelatedPartyRenumeration(Money relatedPartyRenumeration) {
		this.relatedPartyRenumeration = relatedPartyRenumeration;
	}

	public Money getSalaryAndWages() {
		return salaryAndWages;
	}

	public void setSalaryAndWages(Money salaryAndWages) {
		this.salaryAndWages = salaryAndWages;
	}

	public Money getSubcontractorPayments() {
		return subcontractorPayments;
	}

	public void setSubcontractorPayments(Money subcontractorPayments) {
		this.subcontractorPayments = subcontractorPayments;
	}

	public Money getOtherExpenses() {
		return otherExpenses;
	}

	public void setOtherExpenses(Money otherExpenses) {
		this.otherExpenses = otherExpenses;
	}

	public Money getExceptionalItems() {
		return exceptionalItems;
	}

	public void setExceptionalItems(Money exceptionalItems) {
		this.exceptionalItems = exceptionalItems;
	}

	public Money getNetProfitBeforeTax() {
		return netProfitBeforeTax;
	}

	@SuppressWarnings("unused")
	public void setNetProfitBeforeTax(Money netProfitBeforeTax) {
		if (true) {
			throw new IllegalArgumentException("this is a derived property");
		}
		this.netProfitBeforeTax = netProfitBeforeTax;
	}

	public Money getTaxAdjustments() {
		return taxAdjustments;
	}

	public void setTaxAdjustments(Money taxAdjustments) {
		this.taxAdjustments = taxAdjustments;
	}

	public Money getTaxableProfit() {
		return taxableProfit;
	}

	@SuppressWarnings("unused")
	public void setTaxableProfit(Money taxableProfit) {
		if (true) {
			throw new IllegalArgumentException("this is a derived property");
		}
		this.taxableProfit = taxableProfit;
	}

	public Money getAccountsReceivable() {
		return accountsReceivable;
	}

	public void setAccountsReceivable(Money accountsReceivable) {
		this.accountsReceivable = accountsReceivable;
	}

	public Money getOtherCurrentAssets() {
		return otherCurrentAssets;
	}

	public void setOtherCurrentAssets(Money otherCurrentAssets) {
		this.otherCurrentAssets = otherCurrentAssets;
	}

	public Money getVehicleAssets() {
		return vehicleAssets;
	}

	public void setVehicleAssets(Money vehicleAssets) {
		this.vehicleAssets = vehicleAssets;
	}

	public Money getPlantAssets() {
		return plantAssets;
	}

	public void setPlantAssets(Money plantAssets) {
		this.plantAssets = plantAssets;
	}

	public Money getFurnitureAssets() {
		return furnitureAssets;
	}

	public void setFurnitureAssets(Money furnitureAssets) {
		this.furnitureAssets = furnitureAssets;
	}

	public Money getCashAndDeposits() {
		return cashAndDeposits;
	}

	public void setCashAndDeposits(Money cashAndDeposits) {
		this.cashAndDeposits = cashAndDeposits;
	}

	public Money getLand() {
		return land;
	}

	public void setLand(Money land) {
		this.land = land;
	}

	public Money getBuildings() {
		return buildings;
	}

	public void setBuildings(Money buildings) {
		this.buildings = buildings;
	}

	public Money getOtherFixedAssets() {
		return otherFixedAssets;
	}

	public void setOtherFixedAssets(Money otherFixedAssets) {
		this.otherFixedAssets = otherFixedAssets;
	}

	public Money getIntangibles() {
		return intangibles;
	}

	public void setIntangibles(Money intangibles) {
		this.intangibles = intangibles;
	}

	public Money getSharesAndDebentures() {
		return sharesAndDebentures;
	}

	public void setSharesAndDebentures(Money sharesAndDebentures) {
		this.sharesAndDebentures = sharesAndDebentures;
	}

	public Money getTermDeposits() {
		return termDeposits;
	}

	public void setTermDeposits(Money termDeposits) {
		this.termDeposits = termDeposits;
	}

	public Money getOtherNonCurrentAssets() {
		return otherNonCurrentAssets;
	}

	public void setOtherNonCurrentAssets(Money otherNonCurrentAssets) {
		this.otherNonCurrentAssets = otherNonCurrentAssets;
	}

	public Money getTotalAssets() {
		return totalAssets;
	}

	public void setTotalAssets(Money totalAssets) {
		this.totalAssets = totalAssets;
	}

	public Money getProvisions() {
		return provisions;
	}

	public void setProvisions(Money provisions) {
		this.provisions = provisions;
	}

	public Money getAccountsPayable() {
		return accountsPayable;
	}

	public void setAccountsPayable(Money accountsPayable) {
		this.accountsPayable = accountsPayable;
	}

	public Money getCurrentLoans() {
		return currentLoans;
	}

	public void setCurrentLoans(Money currentLoans) {
		this.currentLoans = currentLoans;
	}

	public Money getOtherCurrentLiabilities() {
		return otherCurrentLiabilities;
	}

	public void setOtherCurrentLiabilities(Money otherCurrentLiabilities) {
		this.otherCurrentLiabilities = otherCurrentLiabilities;
	}

	public Money getTotalCurrentLiabilities() {
		return totalCurrentLiabilities;
	}

	@SuppressWarnings("unused")
	public void setTotalCurrentLiabilities(Money totalCurrentLiabilities) {
		if (true) {
			throw new IllegalArgumentException("this is a derived property");
		}
		this.totalCurrentLiabilities = totalCurrentLiabilities;
	}

	public Money getNonCurrentLiabilities() {
		return nonCurrentLiabilities;
	}

	public void setNonCurrentLiabilities(Money nonCurrentLiabilities) {
		this.nonCurrentLiabilities = nonCurrentLiabilities;
	}

	public Money getTotalLiabilities() {
		return totalLiabilities;
	}

	@SuppressWarnings("unused")
	public void setTotalLiabilities(Money totalLiabilities) {
		if (true) {
			throw new IllegalArgumentException("this is a derived property");
		}
		this.totalLiabilities = totalLiabilities;
	}

	public Money getOwnersEquity() {
		return ownersEquity;
	}

	@SuppressWarnings("unused")
	public void setOwnersEquity(Money ownersEquity) {
		if (true) {
			throw new IllegalArgumentException("this is a derived property");
		}
		this.ownersEquity = ownersEquity;
	}

	public Money getTaxDepreciation() {
		return taxDepreciation;
	}

	public void setTaxDepreciation(Money taxDepreciation) {
		this.taxDepreciation = taxDepreciation;
	}

	public Money getUntaxedRealisedGains() {
		return untaxedRealisedGains;
	}

	public void setUntaxedRealisedGains(Money untaxedRealisedGains) {
		this.untaxedRealisedGains = untaxedRealisedGains;
	}

	public Money getAdditionsToFixedAssets() {
		return additionsToFixedAssets;
	}

	public void setAdditionsToFixedAssets(Money additionsToFixedAssets) {
		this.additionsToFixedAssets = additionsToFixedAssets;
	}

	public Money getDisposalsOfFixedAssets() {
		return disposalsOfFixedAssets;
	}

	public void setDisposalsOfFixedAssets(Money disposalsOfFixedAssets) {
		this.disposalsOfFixedAssets = disposalsOfFixedAssets;
	}

	public Money getDividendsPaid() {
		return dividendsPaid;
	}

	public void setDividendsPaid(Money dividendsPaid) {
		this.dividendsPaid = dividendsPaid;
	}

	public Money getDrawings() {
		return drawings;
	}

	public void setDrawings(Money drawings) {
		this.drawings = drawings;
	}

	public Money getCurrentAccountClosingBalance() {
		return currentAccountClosingBalance;
	}

	public void setCurrentAccountClosingBalance(Money currentAccountClosingBalance) {
		this.currentAccountClosingBalance = currentAccountClosingBalance;
	}

	public Money getDeductibleLossOnDisposal() {
		return deductibleLossOnDisposal;
	}

	public void setDeductibleLossOnDisposal(Money deductibleLossOnDisposal) {
		this.deductibleLossOnDisposal = deductibleLossOnDisposal;
	}

	String getCurrentAccountMinusSign() {
		return currentAccountMinusSign;
	}

	void setCurrentAccountMinusSign(String currentAccountMinusSign) {
		this.currentAccountMinusSign = currentAccountMinusSign;
	}
}
