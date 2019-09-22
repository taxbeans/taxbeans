package com.github.taxbeans.forms.nz;

import java.math.BigDecimal;

import org.javamoney.moneta.Money;

import com.github.taxbeans.forms.OmitCents;

public class IR10Form2018Test {

	public static void main(String[] args) {
		// MJHL IR10
		IR10Form2018 bean = new IR10Form2018();
		bean.setIrdNumber("888-888-888");
		bean.setFullname("Example Partnership");
		bean.setMultipleActivityRadio(false);
		bean.setGrossIncome(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setOpeningStock(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setPurchases(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setClosingStock(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setGrossProfit(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setInterestReceived(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setDividends(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setLeasePayments(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setOtherIncome(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setTotalIncome(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setBadDebts(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setDepreciation(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setInsurance(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setInterestExpenses(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setConsultingFees(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setRates(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setLeasePaymentExpenses(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setRepairs(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setResearchAndDevelopment(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setRelatedPartyRenumeration(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setSalaryAndWages(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setSubcontractorPayments(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setOtherExpenses(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setTotalExpenses(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setExceptionalItems(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setNetProfitBeforeTax(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setTaxAdjustments(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setTaxableProfit(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setAccountsReceivable(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setCashAndDeposits(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setOtherCurrentAssets(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setVehicleAssets(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setPlantAssets(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setFurnitureAssets(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setLand(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setBuildings(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setOtherFixedAssets(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setIntangibles(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setSharesAndDebentures(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setTermDeposits(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setOtherNonCurrentAssets(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setTotalAssets(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setProvisions(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setAccountsPayable(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setCurrentLoans(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setOtherCurrentLiabilities(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setTotalCurrentLiabilities(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setNonCurrentLiabilities(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setTotalLiabilities(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setOwnersEquity(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setTaxDepreciation(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setUntaxedRealisedGains(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setTaxDepreciation(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setUntaxedRealisedGains(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setAdditionsToFixedAssets(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setDisposalsOfFixedAssets(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setDividendsPaid(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setDrawings(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setCurrentAccountClosingBalance(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.setDeductibleLossOnDisposal(Money.of(new BigDecimal("888.88"), "NZD"));
		bean.publishDraft();
	}

}