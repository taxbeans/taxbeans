package com.github.taxbeans.crypto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.money.MonetaryAmount;

import org.javamoney.moneta.Money;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CurrencyEventProcessorSession {

	CurrencyBalance currencyBalance;
	
	CurrencyBalance depositBalance = CurrencyBalance.of().valueAtTransactionDatetime();
	
	CurrencyBalance withdrawalBalance = CurrencyBalance.of().valueAtTransactionDatetime();
	
	CurrencyBalance ledgerBalance = CurrencyBalance.of().valueAtTransactionDatetime();
	
	MonetaryAmount profit;
	
	MonetaryAmount profitOfLast;
	
	MonetaryAmount totalCostBasis;
	
	MonetaryAmount totalAssumedDeposits;
	
	MonetaryAmount drawings = Money.of(BigDecimal.ZERO, "NZD");
	
	MonetaryAmount fundsIntroduced = Money.of(BigDecimal.ZERO, "NZD");
	
	MonetaryAmount netDrawings = Money.of(BigDecimal.ZERO, "NZD");

	private CurrencyBatchGroup batchGroup;
	
	private static boolean testIncludeDeposits = false;

	private static boolean testIncludeFiatDeposits = true;

	//found it - cryptopia, leaving it here as an example
	private static boolean testIncludeSpecialCaseLTCDeposit = false;

	//batch issue could be timezone issue //TODO: check on that
	private static boolean testIncludeBCHDeposits = false;
	
	private Map<BigDecimal, CurrencyWithdrawal> transferMap = 
			new HashMap<BigDecimal, CurrencyWithdrawal>();
	
	private Map<BigDecimal, CurrencyDeposit> depositTransferMap = 
			new HashMap<BigDecimal, CurrencyDeposit>();
	
	private List<CurrencyTransfer> transfers = new ArrayList<CurrencyTransfer>();
	
	private List<CurrencyDeposit> deposits = new ArrayList<CurrencyDeposit>();
	
	final static Logger LOG = LoggerFactory.getLogger(CurrencyEventProcessorSession.class);
	
	int tradeCount = 0;

	private List<BusinessExpense> businessExpenseList = new ArrayList<BusinessExpense>();
	
	public CurrencyEventProcessorSession(CurrencyBatchGroup batchGroup) {
		this.batchGroup = batchGroup;
		currencyBalance = CurrencyBalance.of().valueAtTransactionDatetime();
		profit = Money.of(BigDecimal.ZERO, batchGroup.getBaseCurrency());
		totalCostBasis = Money.of(BigDecimal.ZERO, batchGroup.getBaseCurrency());
		totalAssumedDeposits = Money.of(BigDecimal.ZERO, batchGroup.getBaseCurrency());
	}
	
	public void process(CryptoEvent cryptoEvent) {
		if (cryptoEvent instanceof CurrencyTradeProfit) {
			process((CurrencyTradeProfit) cryptoEvent, cryptoEvent.getCurrencyExchange());
		} else if (cryptoEvent instanceof CurrencyTradeLoss) {
			process((CurrencyTradeLoss) cryptoEvent, cryptoEvent.getCurrencyExchange());
		} else if (cryptoEvent instanceof CurrencyConversion) {
			CurrencyConversion currencyConversion = (CurrencyConversion) cryptoEvent;
			process(currencyConversion, cryptoEvent.getWhen());
		} else {
			CurrencyExchange currencyExchange = cryptoEvent.getCurrencyExchange();
			if (cryptoEvent instanceof CurrencyDeposit) {
				CurrencyDeposit currencyDeposit = (CurrencyDeposit) cryptoEvent;
				CurrencyAmount amount = currencyDeposit.getAmount();
				CurrencyAmount depositAmount = CurrencyAmount.of(amount, cryptoEvent.getWhen());
				depositBalance.add(depositAmount, currencyExchange);
				ledgerBalance.add(depositAmount, currencyExchange);
				//fee has no affect on the balance since it is simply taken
				//by the network and the amount deposited is net of that fee
				//except for LocalBitcoins where the amount is gross
				CurrencyAmount feeAmount = currencyDeposit.getFee().getAmount();
				if (currencyExchange.equals(CurrencyExchange.LOCALBITCOINS)) {
					depositBalance.subtract(feeAmount, currencyExchange);
					ledgerBalance.subtract(feeAmount, currencyExchange);
				}
				//if (feeAmount.equals(ZERO_NZD)) {
				//	throw new AssertionError(
				//			String.format("fee was not zero for deposit in row %1$s", 
				//					cryptoEvent.getRowNum()));
				//}
				profitOfLast = CurrencyAmount.of(feeAmount, cryptoEvent.getWhen())
						.getBaseCurrencyAmount().negate();
				Assert.assertTrue(profitOfLast.isNegative() || profitOfLast.isZero());
				profit = profit.add(profitOfLast);
				
				if (!feeAmount.isZero()) {
					batchGroup.subtract(feeAmount, cryptoEvent.getWhen());
				}
				
				if (testIncludeDeposits) {
					batchGroup.add(CurrencyBatch.of(amount, currencyDeposit.getWhen(), amount));
				} else if (testIncludeFiatDeposits && !amount.isCrypto()) {
					batchGroup.add(CurrencyBatch.of(amount, currencyDeposit.getWhen(), amount));
				} else if (testIncludeBCHDeposits  && amount.getCurrencyCode().equals(CurrencyCode.of("BCH"))) {
					batchGroup.add(CurrencyBatch.of(amount, currencyDeposit.getWhen(), amount));
					//TODO find out where the following special cases came from:
				} else if (testIncludeSpecialCaseLTCDeposit && amount.isCrypto()
						&& amount.getCurrencyCode().equals(CurrencyCode.of("LTC"))
						&& (amount.getBigDecimal().compareTo(new BigDecimal("0.13720906")) == 0)) {
					batchGroup.add(CurrencyBatch.of(amount, currencyDeposit.getWhen(), amount));					
				}
				//transfer detection
				if (!transferMap.isEmpty()) {
					boolean skip = Configuration.shouldExcludeFiatFromTransferDetection();
					skip = skip && !amount.isCrypto();
					BigDecimal decimal = depositAmount.getBigDecimal().setScale(8, RoundingMode.HALF_UP);
					CurrencyWithdrawal currencyWithdrawal = transferMap.get(decimal);
					if (currencyWithdrawal != null && !skip) {
						LOG.info("Detected transfer from {} to {} of {} on {}",
								currencyWithdrawal.getCurrencyExchange(),
								currencyDeposit.getCurrencyExchange(),
								decimal,
								currencyDeposit.getWhen());
						transferMap.remove(decimal);
						CurrencyTransfer currencyTransfer = CurrencyTransfer.of(currencyWithdrawal.getCurrencyExchange(),
								currencyDeposit.getCurrencyExchange(),
								CurrencyAmount.of(decimal, amount.getCurrencyCode()),
								currencyDeposit.getWhen());
						transfers.add(currencyTransfer);
					} else {
						deposits.add(currencyDeposit);
					}
				}
				//transfer detection
				CurrencyAmount transferAmount;
				if (feeAmount.getBigDecimal().compareTo(BigDecimal.ZERO) == 0) {
					//handle case of currency mismatch, but fee is zero:
					transferAmount = depositAmount;
				} else {
					transferAmount = depositAmount;
				}
				BigDecimal transferDecimal = transferAmount.getBigDecimal().setScale(8, RoundingMode.HALF_UP);
				depositTransferMap.put(transferDecimal, currencyDeposit);
				
				//tracking of drawings:
				if (!depositAmount.isCrypto()) {
					MonetaryAmount monetaryAmount = depositAmount.getBaseCurrencyAmount();
					fundsIntroduced = fundsIntroduced.add(monetaryAmount);
					netDrawings = netDrawings.subtract(monetaryAmount);
				}

			} else if (cryptoEvent instanceof CurrencyWithdrawal) {
				CurrencyWithdrawal currencyWithdrawal = (CurrencyWithdrawal) cryptoEvent;
				CurrencyAmount amount = currencyWithdrawal.getAmount();
				if (amount == null) {
					throw new AssertionError("amount is null for rowNum: " + currencyWithdrawal.getRowNum());
				}
				CurrencyAmount withdrawalAmount = CurrencyAmount.of(amount, cryptoEvent.getWhen());
				//reconstruct with datetime to formulate baseCurrencyAmount
				CurrencyAmount feeAmount = CurrencyAmount.of(currencyWithdrawal.getFee().getAmount(), cryptoEvent.getWhen());
				withdrawalBalance.add(withdrawalAmount, currencyExchange);
				ledgerBalance.subtract(withdrawalAmount, currencyExchange);
				//fee has no affect on the balance since it is simply taken
				//by the exchange out of the amount withdrawn
				//except for LocalBitcoins where it is on top
				if (currencyExchange.equals(CurrencyExchange.LOCALBITCOINS)) {
					withdrawalBalance.add(feeAmount, currencyExchange);
					ledgerBalance.subtract(feeAmount, currencyExchange);
				}
				//withdrawalBalance.add(feeAmount, currencyExchange);
				//ledgerBalance.subtract(feeAmount, currencyExchange);
				//fees affects profits
				profitOfLast = CurrencyAmount.of(feeAmount, cryptoEvent.getWhen())
						.getBaseCurrencyAmount().negate();
				Assert.assertTrue(profitOfLast.isNegative() || profitOfLast.isZero());
				profit = profit.add(profitOfLast);
				batchGroup.subtract(feeAmount, cryptoEvent.getWhen());

				if (Configuration.shouldDetectTransfersOutOfOrder()) {
					//detect transfers with deposits before withdrawals (possible timezone issues)
					if (!transferMap.isEmpty()) {
						boolean skip = Configuration.shouldExcludeFiatFromTransferDetection();
						skip = skip && !withdrawalAmount.isCrypto();
						BigDecimal decimal = withdrawalAmount.getBigDecimal().setScale(8, RoundingMode.HALF_UP);
						CurrencyDeposit currencyDeposit = depositTransferMap.get(decimal);

						if (currencyDeposit == null && 
								feeAmount.getBigDecimal().compareTo(BigDecimal.ZERO) != 0) {
							decimal = withdrawalAmount.getBigDecimal().subtract(feeAmount.getBigDecimal()).setScale(8, RoundingMode.HALF_UP);
							currencyDeposit = depositTransferMap.get(decimal);
						}

						if (currencyDeposit != null && !skip) {
							CurrencyExchange from = currencyWithdrawal.getCurrencyExchange();
							CurrencyExchange to = currencyDeposit.getCurrencyExchange();
							if (!from.equals(to)) {
								LOG.info("Detected transfer from {} to {} of {} on {}",
										from,
										to,
										decimal,
										currencyDeposit.getWhen());
								depositTransferMap.remove(decimal);
								CurrencyTransfer currencyTransfer = CurrencyTransfer.of(from,
										to,
										CurrencyAmount.of(decimal, currencyDeposit.getAmount().getCurrencyCode()),
										currencyDeposit.getWhen());
								LOG.warn("Detected deposit before withdrawal");
								transfers.add(currencyTransfer);
							}
						}
					}
				}

				//transfer detection
				CurrencyAmount transferAmount;
				if (feeAmount.getBigDecimal().compareTo(BigDecimal.ZERO) == 0) {
					//handle case of currency mismatch, but fee is zero:
					transferAmount = withdrawalAmount;
				} else {
					transferAmount = withdrawalAmount.subtract(feeAmount);
				}
				BigDecimal transferDecimal = transferAmount.getBigDecimal().setScale(8, RoundingMode.HALF_UP);
				transferMap.put(transferDecimal, currencyWithdrawal);
				
				//tracking of drawings:
				MonetaryAmount monetaryAmount = withdrawalAmount.getBaseCurrencyAmount();
				if (!withdrawalAmount.isCrypto() && !currencyWithdrawal.isBusinessExpense()) {
					drawings = drawings.add(monetaryAmount);
					netDrawings = netDrawings.add(monetaryAmount);
				} else if (withdrawalAmount.isCrypto() && currencyWithdrawal.isDrawings()) {
					drawings = drawings.add(monetaryAmount);
					netDrawings = netDrawings.add(monetaryAmount);
					batchGroup.subtract(currencyWithdrawal.getAmount(), currencyWithdrawal.getWhen());
				} else if (currencyWithdrawal.isBusinessExpense()) {
					Assert.assertTrue(profitOfLast.isNegative());
					profitOfLast = profitOfLast.subtract(monetaryAmount);
					Assert.assertTrue(profitOfLast.isNegative());
					Assert.assertTrue(monetaryAmount.isPositive());
					profit = profit.subtract(monetaryAmount);
					businessExpenseList.add(BusinessExpense.of(monetaryAmount, currencyWithdrawal.getWhen(), 
							currencyWithdrawal.getComment()));
					batchGroup.subtract(withdrawalAmount, cryptoEvent.getWhen());
				}
			}	
		}
	}
	
	public void process(CurrencyTradeLoss currencyTradeLoss, CurrencyExchange currencyExchange) {
		MonetaryAmount thisLoss;
		if (Configuration.calculateTradeLossAtInitialCost()) {
			CurrencyTrade logicalTrade = CurrencyTrade.of(batchGroup, 
					currencyTradeLoss.getLoss(), 
					CurrencyAmount.getZeroNZD(), 
					currencyTradeLoss.getWhen(), 
					CurrencyFee.of(CurrencyAmount.getZeroNZD()));
			thisLoss = logicalTrade.calculateBaseCurrencyCost();
			thisLoss = currencyTradeLoss.getLossInBaseCurrency();
		} else {
			thisLoss = currencyTradeLoss.getLossInBaseCurrency();
			//currency lost must be removed from the batches
			batchGroup.subtract(currencyTradeLoss.getLoss(), currencyTradeLoss.getWhen());
		}
		
		//MonetaryAmount rolloverFees = currencyTradeLoss.getTotalRolloverFeesInBaseCurrency();
		//fee is informational and seems to be already included in profit/loss reported
		profitOfLast = thisLoss;  //.negate().subtract(rolloverFees);
		Assert.assertTrue(profitOfLast.isNegative());
		profit = profit.add(profitOfLast);
		ledgerBalance.subtract(currencyTradeLoss.getLoss(), currencyExchange);
		currencyBalance.subtract(currencyTradeLoss.getLoss(), currencyExchange);
		//ledgerBalance.subtract(currencyTradeLoss.getTotalRolloverFees().getAmount(), currencyExchange);
		//currencyBalance.subtract(currencyTradeLoss.getTotalRolloverFees().getAmount(), currencyExchange);
		totalCostBasis = totalCostBasis.add(thisLoss);
		tradeCount++;
		
		if (currencyTradeLoss.isBusinessExpense()) {
			businessExpenseList.add(BusinessExpense.of(thisLoss.negate(), currencyTradeLoss.getWhen(), 
					currencyTradeLoss.getComment()));
		}
	}
	
	public void process(CurrencyTradeProfit currencyTradeProfit, CurrencyExchange currencyExchange) {
		MonetaryAmount thisProfit = currencyTradeProfit.getProfitInBaseCurrency();
		//MonetaryAmount rolloverFees = currencyTradeProfit.getTotalRolloverFeesInBaseCurrency();
		//fee is informational and seems to be already included in profit/loss reported
		profitOfLast = thisProfit;  //.subtract(rolloverFees);

		//assertion code block:
		LOG.trace("Overall profit can be negative due to fees");
		MonetaryAmount feeInBaseCurrency = ConversionUtils.of()
				.convert(batchGroup.getBaseCurrency(),
						currencyTradeProfit.getTotalRolloverFees().getAmount(),
				currencyTradeProfit.getWhen());
		MonetaryAmount profitExcludingFee = thisProfit.add(feeInBaseCurrency);
		Assert.assertTrue(profitExcludingFee.isPositive());

		profit = profit.add(profitOfLast);
		ledgerBalance.add(currencyTradeProfit.getProfit(), currencyExchange);
		currencyBalance.add(currencyTradeProfit.getProfit(), currencyExchange);
		if (currencyTradeProfit.getProfit().getCurrencyCode().equals(CurrencyCode.of("BCHSV")) ) {
			LOG.info("BCHSV found");
		}
		//ledgerBalance.add(currencyTradeProfit.getTotalRolloverFees().getAmount(), currencyExchange);
		//currencyBalance.add(currencyTradeProfit.getTotalRolloverFees().getAmount(), currencyExchange);
		totalCostBasis = totalCostBasis.add(thisProfit);
		tradeCount++;
		if (currencyTradeProfit.getProfit().getBigDecimal().signum() > 0) {
			batchGroup.add(CurrencyBatch.of(currencyTradeProfit.getProfit(), currencyTradeProfit.getWhen(), currencyTradeProfit.getProfit()));
		} else if (currencyTradeProfit.getProfit().getBigDecimal().signum() < 0){
			//remove from the batches
			CurrencyAmount amountToSubtract = currencyTradeProfit.getProfit().negate();
			Assert.assertTrue(amountToSubtract.isPositive());
			batchGroup.subtract(amountToSubtract, currencyTradeProfit.getWhen());
		}
	}

	public void process(CurrencyConversion currencyConversion, ZonedDateTime when) {
		if (currencyConversion.getFrom().isSameCurrency(batchGroup.getBaseCurrency())) {
			totalAssumedDeposits = currencyConversion.getFrom().add(totalAssumedDeposits);
		}
		currencyConversion.deriveTradeAndBatch();
		CurrencyTrade trade = currencyConversion.getTrade();
		CurrencyExchange currencyExchange = currencyConversion.getCurrencyExchange();
		
		CurrencyAmount sellAmount = CurrencyAmount.of(trade.getCurrencyAmount(), when);
		//CurrencyAmount feeAmount = currencyConversion.getFee().getAmount();
		//expect some NZD here for purchases of BTC with NZD
		//leveraged transactions affect balance!
		ledgerBalance.subtract(sellAmount, currencyExchange);
		currencyBalance.subtract(sellAmount, currencyExchange);
		
		//the fee is already subtracted from the ledger
		//by means of the sell amount
		//the fee is included in the trade profit calculation
		//by means of deriveTradeAndBatch
		//ledgerBalance.subtract(feeAmount, currencyExchange);
		//currencyBalance.subtract(feeAmount, currencyExchange);
		
		//if (!trade.isLeveraged()) {
			//expect some NZD here for purchases of BTC with NZD
			
		//}
		//cryptoBalance.subtract(trade.getFee());  //fee already included in crypto amount above
		processTradeEvent(trade);
		if (!trade.isLeveraged()) {
			totalCostBasis = totalCostBasis.subtract(trade.getCostBasis());
		}
		CurrencyBatch batch = currencyConversion.getBatch();				
		batchGroup.add(batch);
		
		CurrencyAmount buyAmount = CurrencyAmount.of(batch.getInitialAmount(), when);
		currencyBalance.add(buyAmount, currencyExchange);
		ledgerBalance.add(buyAmount, currencyExchange);
		if (!batch.isLeveraged()) {			
			totalCostBasis = totalCostBasis.add(batch.getInitialCostInBaseCurrency());
		}
	}

	public MonetaryAmount getTotalCostBasis() {
		return totalCostBasis;
	}
	
	public void processTradeEvent(CurrencyTrade trade) {
		CurrencyBatchSet batchesUsedToAction = trade.action();
		if (LOG.isTraceEnabled()) {
			//will cause oom
			LOG.trace("Batches used to action = " + batchesUsedToAction);
			LOG.trace(trade + "");
		}
		profitOfLast = trade.calculateProfit();

		profit = profit.add(profitOfLast);
		LOG.info("running profit " + CurrencyAmount.format(profit));
		tradeCount++;
		LOG.info("number of trades = " + tradeCount);
	}

	public MonetaryAmount getProfitOfLast() {
		return profitOfLast;
	}

	public MonetaryAmount getTotalProfit() {
		return profit;
	}

	public MonetaryAmount getTotalAssumedBaseCurrencyDeposits() {
		return totalAssumedDeposits;
	}

	public CurrencyBalance getCurrencyBalance() {
		return currencyBalance;
	}

	public CurrencyBalance getDepositBalance() {
		return depositBalance;
	}

	public void setDepositBalance(CurrencyBalance depositBalance) {
		this.depositBalance = depositBalance;
	}

	public CurrencyBalance getWithdrawalBalance() {
		return withdrawalBalance;
	}

	public void setWithdrawalBalance(CurrencyBalance withdrawalBalance) {
		this.withdrawalBalance = withdrawalBalance;
	}

	public CurrencyBalance getLedgerBalance() {
		return ledgerBalance;
	}

	List<CurrencyTransfer> getTransfers() {
		return transfers;
	}

	void setTransfers(List<CurrencyTransfer> transfers) {
		this.transfers = transfers;
	}

	List<BusinessExpense> getBusinessExpenseList() {
		return businessExpenseList;
	}

	public MonetaryAmount getDrawings() {
		return drawings;
	}

	public MonetaryAmount getFundsIntroduced() {
		return fundsIntroduced;
	}

	public MonetaryAmount getNetDrawings() {
		return netDrawings;
	}

	public Map<BigDecimal, CurrencyDeposit> getDepositTransferMap() {
		return depositTransferMap;
	}

	public Map<BigDecimal, CurrencyWithdrawal> getTransferMap() {
		return transferMap;
	}

	public List<CurrencyDeposit> getDeposits() {
		return deposits;
	}

	public void setDeposits(List<CurrencyDeposit> deposits) {
		this.deposits = deposits;
	}
}
