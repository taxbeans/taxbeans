package com.github.taxbeans.forms.nz;

import org.javamoney.moneta.Money;

import com.github.taxbeans.forms.RightAlign;
import com.github.taxbeans.forms.Skip;
import com.github.taxbeans.forms.UseTrueFalseMappings;
import com.github.taxbeans.forms.common.FormDestination;

public class IR3GForm2020 implements FormDestination {

	@Skip
	private String destinationDirectory;

	private int year = 2020;

	@RightAlign(value = 11, fieldName = "pg38")
	private Money taxableIncome;

	@RightAlign(value = 11, fieldName = "pg38z.0")
	private Money taxOnTaxableIncome;

	@RightAlign(value = 11, fieldName = "pg38z.1")
	private Money taxCreditFromBox32;

	@RightAlign(value = 11, fieldName = "pg38z.2")
	private Money netTaxAfterCredits;

	@RightAlign(value = 11, fieldName = "pg38z.3")
	private Money overseasTaxPaid;

	@RightAlign(value = 11, fieldName = "pg38z.4")
	private Money netTaxAfterOverseasCredits;

	@RightAlign(value = 11, fieldName = "pg38z.5")
	private Money imputationCredits;

	@RightAlign(value = 11, fieldName = "pg38z.6")
	private Money excessImputationCreditsBroughtForward;

	@RightAlign(value = 11, fieldName = "pg38z.7")
	private Money totalImputationCredits;

	@RightAlign(value = 11, fieldName = "pg38z.8")
	private Money taxAfterImputationCredits;

	@RightAlign(value = 11, fieldName = "pg38z.9")
	private Money taxCreditSubtotal;

	@RightAlign(value = 11, fieldName = "pg38z.10.0")
	private Money residualIncomeTax;

	@UseTrueFalseMappings(fieldName = "Tick one", trueValue = "Credit", falseValue = "Debit")
	private boolean residualIncomeTaxIsCredit;

	@RightAlign(value = 11, fieldName = "pg38z.10.1")
	private Money provisionalTaxPaid;

	@RightAlign(value = 11, fieldName = "pg38z.10.2")
	private Money taxCalculationResult;

	@UseTrueFalseMappings(fieldName = "Tick one_2", trueValue = "Refund", falseValue = "Tax to pay")
	private boolean taxCalculationResultIsRefund;

	@Override
	public String getDestinationDirectory() {
		return destinationDirectory;
	}

	public int getYear() {
		return year;
	}

	public Money getTaxableIncome() {
		return taxableIncome;
	}

	public Money getTaxOnTaxableIncome() {
		return taxOnTaxableIncome;
	}

	public Money getTaxCreditFromBox32() {
		return taxCreditFromBox32;
	}

	public Money getNetTaxAfterCredits() {
		return netTaxAfterCredits;
	}

	public Money getOverseasTaxPaid() {
		return overseasTaxPaid;
	}

	public Money getNetTaxAfterOverseasCredits() {
		return netTaxAfterOverseasCredits;
	}

	public Money getImputationCredits() {
		return imputationCredits;
	}

	public Money getExcessImputationCreditsBroughtForward() {
		return excessImputationCreditsBroughtForward;
	}

	public Money getTotalImputationCredits() {
		return totalImputationCredits;
	}

	public Money getTaxAfterImputationCredits() {
		return taxAfterImputationCredits;
	}

	public Money getTaxCreditSubtotal() {
		return taxCreditSubtotal;
	}

	public Money getResidualIncomeTax() {
		return residualIncomeTax;
	}

	public boolean isResidualIncomeTaxIsCredit() {
		return residualIncomeTaxIsCredit;
	}

	public Money getProvisionalTaxPaid() {
		return provisionalTaxPaid;
	}

	public Money getTaxCalculationResult() {
		return taxCalculationResult;
	}

	public boolean isTaxCalculationResultIsRefund() {
		return taxCalculationResultIsRefund;
	}

	public void setDestinationDirectory(String destinationDirectory) {
		this.destinationDirectory = destinationDirectory;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public void setTaxableIncome(Money taxableIncome) {
		this.taxableIncome = taxableIncome;
	}

	public void setTaxOnTaxableIncome(Money taxOnTaxableIncome) {
		this.taxOnTaxableIncome = taxOnTaxableIncome;
	}

	public void setTaxCreditFromBox32(Money taxCreditFromBox32) {
		this.taxCreditFromBox32 = taxCreditFromBox32;
	}

	public void setNetTaxAfterCredits(Money netTaxAfterCredits) {
		this.netTaxAfterCredits = netTaxAfterCredits;
	}

	public void setOverseasTaxPaid(Money overseasTaxPaid) {
		this.overseasTaxPaid = overseasTaxPaid;
	}

	public void setNetTaxAfterOverseasCredits(Money netTaxAfterOverseasCredits) {
		this.netTaxAfterOverseasCredits = netTaxAfterOverseasCredits;
	}

	public void setImputationCredits(Money imputationCredits) {
		this.imputationCredits = imputationCredits;
	}

	public void setExcessImputationCreditsBroughtForward(Money excessImputationCreditsBroughtForward) {
		this.excessImputationCreditsBroughtForward = excessImputationCreditsBroughtForward;
	}

	public void setTotalImputationCredits(Money totalImputationCredits) {
		this.totalImputationCredits = totalImputationCredits;
	}

	public void setTaxAfterImputationCredits(Money taxAfterImputationCredits) {
		this.taxAfterImputationCredits = taxAfterImputationCredits;
	}

	public void setTaxCreditSubtotal(Money taxCreditSubtotal) {
		this.taxCreditSubtotal = taxCreditSubtotal;
	}

	public void setResidualIncomeTax(Money residualIncomeTax) {
		this.residualIncomeTax = residualIncomeTax;
	}

	public void setResidualIncomeTaxIsCredit(boolean residualIncomeTaxIsCredit) {
		this.residualIncomeTaxIsCredit = residualIncomeTaxIsCredit;
	}

	public void setProvisionalTaxPaid(Money provisionalTaxPaid) {
		this.provisionalTaxPaid = provisionalTaxPaid;
	}

	public void setTaxCalculationResult(Money taxCalculationResult) {
		this.taxCalculationResult = taxCalculationResult;
	}

	public void setTaxCalculationResultIsRefund(boolean taxCalculationResultIsRefund) {
		this.taxCalculationResultIsRefund = taxCalculationResultIsRefund;
	}
}
