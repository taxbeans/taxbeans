package com.github.taxbeans.crypto;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.money.Monetary;
import javax.money.MonetaryAmount;

import org.apache.commons.lang3.StringUtils;
import org.javamoney.moneta.Money;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.taxbeans.csv.CSVParser;
import com.github.taxbeans.csv.CSVWriter;
import com.github.taxbeans.forms.utils.ZonedDateTimeUtils;


public class CurrencyTradingParser {
	
	private static final String EUR = "EUR";

	private static final String XRP = "XRP";

	private static final String NANO = "NANO";

	private static final String BSV = "BSV";

	private static final String BCH = "BCH";

	private static final String BTC = "BTC";

	private static final String ETH = "ETH";

	private static final String LTC = "LTC";

	private static boolean loadKrakenTradeCSV = true;

	private static final String SPEND = "Spend";

	private static final String TRADE = "Trade";

	private static final String WITHDRAWAL = "Withdrawal";

	private static final String DEPOSIT = "Deposit";
	
	private static final String REPAYMENT = "Repayment";

	private static final String LOAN = "Loan";

	private static final String LOST = "Lost";

	private static final String INCOME = "Income";

	private static final String ROLLOVER = "Rollover";

	private static final String MARGIN = "Margin";

	private static final Object EXPENSE = "Expense";
	
	final static Logger logger = LoggerFactory.getLogger(CurrencyTradingParser.class);

	private static final Integer[] YEARS = {2016, 2017, 2018, 2019, 2020};
	
	private static Map<Integer, MonetaryAmount> runningProfit = new HashMap<Integer, MonetaryAmount>();
	
	private static MonetaryAmount previousRunningProfit;
	
	private static CurrencyBalance currencyBalance = null;
	
	private static Map<Integer, CurrencyBalance> currencyBalances = new HashMap<Integer, CurrencyBalance>();
	
	private static CurrencyBalance previousCryptoBalance;
	
	private static Map<Integer, CurrencyBalance> depositBalances = new HashMap<Integer, CurrencyBalance>();
	
	private static CurrencyBalance previousDepositBalance;
	
	private static Map<Integer, CurrencyBalance> withdrawalBalances = new HashMap<Integer, CurrencyBalance>();
	
	private static CurrencyBalance previousWithdrawalBalance;
	
	private static Map<Integer, CurrencyBalance> ledgerBalances = new HashMap<Integer, CurrencyBalance>();
	
	private static CurrencyBalance previousLedgerBalance;
	
	private static Map<Integer, MonetaryAmount> costBasis = new HashMap<Integer, MonetaryAmount>();
	
	private static Map<Integer, MonetaryAmount> baseDrawings = new HashMap<Integer, MonetaryAmount>();
	
	private static MonetaryAmount previousDrawingsBalance = null;
	
	private static Map<Integer, MonetaryAmount> baseFundsIntroduced = new HashMap<Integer, MonetaryAmount>();
	
	private static MonetaryAmount previousFundsIntroducedBalance = null;
	
	private static Map<Integer, MonetaryAmount> baseNetDrawings = new HashMap<Integer, MonetaryAmount>();
	
	private static MonetaryAmount previousNetDrawingsBalance = null;
	
	private static MonetaryAmount previousTotalCostBasis;
	
	/*
	 * Return the financial year end, which is typically on the 31st March of the given year
	 */
	static ZonedDateTime getYearEnd(int year) {
		return ZonedDateTime.of(
				LocalDate.of(year, 3, 31), LocalTime.of(0,0,0) , ZoneId.of("Pacific/Auckland"));
	}

	private static ZonedDateTime previousWhen;

	private static CryptoEvent previousEvent;

	private static int duplicateCurrencyConversions;

	private static int specialCaseMarginGain;

	private static int sellAmountEqualsFeeAmountForKrakenMargin;

	private static int buyAmountEqualsFeeAmountForKrakenMargin;

	private static int incomeTypeCount;

	private static int lossTypeCount;

	private static int bitMexAPILabelNotSet;

	private static int rowsWithSameDateTimeAndAmounts;

	private static int deposits;

	private static int withdrawals;

	private static int eventCount;

	private static int krakenCount;

	private static int krakenDepositCount;

	private static int krakenWithdrawalCount;

	private static int bothKrakenTradeSidesHaveFees;

	private static int krakenMarginCount;

	private static int krakenRolloverCount;

	private static int krakenTradeCount;

	private static int conversionHalfCount;
	
	private static int poloniexDepositCount;
	
	private static int poloniexWithdrawalCount;

	private static int poloniexCount;

	private static int poloniexTradeCount;
	
	//used to limit for testing reconciliation
	private static int testRowLimit = -1;
	
	private static boolean testKrakenOnly = false;
	
	private static boolean testPoloniexOnly = false;
	
	private static boolean testBitfinexOnly = false;

	private static boolean testExitEarly = false;
	
	private static boolean testReconcileKraken = false;

//	private static boolean testIncludeDeposits = true;
//
//	private static boolean testIncludeFiatDeposits = true;
//
//	private static boolean testIncludeSpecialCaseLTCDeposit = true;
//
//	private static boolean testIncludeBCHDeposits = true;

	private static boolean testReconcilePoloniex;

	private static boolean loadPoloniexTradeCSV = true;

	private static boolean loadPoloniexDepositCSV = true;
	
	private static boolean loadBitfinexTradeCSV = true;

	private static int bitfinexCount;

	private static int bitfinexDepositCount;

	private static int bitfinexWithdrawalCount;

	private static int bitfinexTradeCount;

	private static boolean testReconcileBitfinex;

	private static int explicitDrawingsCount;

	private static int explicitFundsIntroducedCount;

	//private static ZoneId zone = ZoneId.of("GMT");

	public static void main(String[] args) throws Exception {
		List<String[]> parsedFile = CSVParser.newInstance().parseFile(
				CurrencyTradingParser.class.getClassLoader().getResourceAsStream("trades-all-v3.csv"), true);
				//"trades-1st-April-2016-31-March-2017.csv"), true);
				
		
		CurrencyBatchGroup batchGroup = CurrencyBatchGroup.of(Monetary.getCurrency("NZD"));
		
		List<CryptoEvent> cryptoEvents = new ArrayList<CryptoEvent>();
		
		List<CryptoEvent> poloniexEvents = new ArrayList<CryptoEvent>();
		
		List<CryptoEvent> krakenEvents = new ArrayList<CryptoEvent>();
		
		List<CryptoEvent> bitfinexEvents = new ArrayList<CryptoEvent>();
		
		if (!testBitfinexOnly && CurrencyTradingParser.loadPoloniexDepositCSV) {
			List<String[]> poloniexCSV = CSVParser.newInstance().parseFile(
					CurrencyTradingParser.class.getClassLoader().getResourceAsStream("trades/poloniex-deposits-all.csv"), true);
	
			processPoloniexDeposits(poloniexCSV, poloniexEvents, batchGroup);
		}
		if (!testBitfinexOnly && CurrencyTradingParser.loadPoloniexTradeCSV ) {
			List<String[]> poloniexCSV = CSVParser.newInstance().parseFile(
					CurrencyTradingParser.class.getClassLoader().getResourceAsStream("trades/poloniex-trades-all.csv"), true);
	
			processPoloniex(poloniexCSV, poloniexEvents, batchGroup);
		}
		if (CurrencyTradingParser.loadBitfinexTradeCSV ) {
			List<String[]> bitfinexCSV = CSVParser.newInstance().parseFile(
					CurrencyTradingParser.class.getClassLoader().getResourceAsStream("trades/bitfinex-trades-all.csv"), true);
	
			processBitfinex(bitfinexCSV, bitfinexEvents, batchGroup);
		}
		if (!testBitfinexOnly && !testPoloniexOnly && CurrencyTradingParser.loadKrakenTradeCSV) {
			List<String[]> krakenCSV = CSVParser.newInstance().parseFile(
					CurrencyTradingParser.class.getClassLoader().getResourceAsStream("trades/kraken-ledgers-all.csv"), true);
	
			processKraken(krakenCSV, krakenEvents, batchGroup);
		}
		if (!testBitfinexOnly) {
			cryptoEvents.addAll(poloniexEvents);
		}
		if (!testKrakenOnly) {
			cryptoEvents.addAll(bitfinexEvents);
		}
		
		CurrencyTradeData currencyTradeData;
		if (testBitfinexOnly) {
			Collections.sort(cryptoEvents);			
			currencyTradeData = CurrencyTradeData.of(batchGroup, bitfinexEvents);	
		} else if (testPoloniexOnly) {
			Collections.sort(cryptoEvents);			
			currencyTradeData = CurrencyTradeData.of(batchGroup, cryptoEvents);	
		} else {
			cryptoEvents.addAll(krakenEvents);
			if (testKrakenOnly ) {			
				Collections.sort(cryptoEvents);			
				currencyTradeData = CurrencyTradeData.of(batchGroup, cryptoEvents);	
			} else {
				currencyTradeData = process(parsedFile, cryptoEvents, batchGroup);
			}
		}
		CurrencyBalance depositBalance = CurrencyBalance.of();
		CurrencyBalance withdrawalBalance = CurrencyBalance.of();
		CurrencyBalance ledgerBalance = CurrencyBalance.of();
		
		CurrencyEventProcessorSession session = new CurrencyEventProcessorSession(batchGroup);

		MonetaryAmount totalCostBasis = Money.of(BigDecimal.ZERO, batchGroup.getBaseCurrency());
		MonetaryAmount totalProfit = Money.of(BigDecimal.ZERO, batchGroup.getBaseCurrency());
		
		Collections.sort(currencyTradeData.getCryptoEvents());	
		for (CryptoEvent event : currencyTradeData.getCryptoEvents()) {
			logger.info(event.getWhen() + "");

			session.process(event);
			currencyBalance = session.getCurrencyBalance();
			depositBalance = session.getDepositBalance();
			withdrawalBalance = session.getWithdrawalBalance();
			ledgerBalance = session.getLedgerBalance();
			totalCostBasis = session.getTotalCostBasis();
			logger.info("Total cost basis after {}", CurrencyAmount.format(totalCostBasis));
			
			MonetaryAmount profitOfThisTrade = session.getProfitOfLast();
			logger.info("Profit of this trade {} is {}", event.toString(), CurrencyAmount.format(profitOfThisTrade));
			
			totalProfit = session.getTotalProfit();
			logger.info("Total profit: {}", CurrencyAmount.format(totalProfit));
			
			MonetaryAmount totalAssumedDeposits = session.getTotalAssumedBaseCurrencyDeposits();
			logger.info("Total assumed deposits: {}", CurrencyAmount.format(totalAssumedDeposits));
			
			if (event.getCurrencyExchange() == CurrencyExchange.KRAKEN) {
				krakenCount++;
				if (event instanceof CurrencyDeposit) {
					krakenDepositCount++;
				} else if (event instanceof CurrencyWithdrawal) {
					krakenWithdrawalCount++;
				} else if (event instanceof CurrencyTradeProfit) {
					krakenMarginCount++;
				} else if (event instanceof CurrencyTradeLoss) {
					CurrencyTradeLoss currencyTradeLoss = (CurrencyTradeLoss) event;
					if (currencyTradeLoss.isRollover()) {
						krakenRolloverCount++;
					} else {
						krakenMarginCount++;
					}
				} else if (event instanceof CurrencyConversion) {
					krakenTradeCount++;
				}
			} else if (event.getCurrencyExchange() == CurrencyExchange.POLONIEX) {
				poloniexCount++;
				if (event instanceof CurrencyDeposit) {
					poloniexDepositCount++;
				} else if (event instanceof CurrencyWithdrawal) {
					poloniexWithdrawalCount++;				
				} else if (event instanceof CurrencyConversion) {
					poloniexTradeCount++;
				}
			} else if (event.getCurrencyExchange() == CurrencyExchange.BITFINEX) {
				bitfinexCount++;
				if (event instanceof CurrencyDeposit) {
					bitfinexDepositCount++;
				} else if (event instanceof CurrencyWithdrawal) {
					bitfinexWithdrawalCount++;				
				} else if (event instanceof CurrencyConversion) {
					bitfinexTradeCount++;
				}
			}
			ZonedDateTime when = event.getWhen();
			if (when.equals(previousWhen)) {
				boolean valid = false;
				logger.warn("Previous event: {}", String.valueOf(previousEvent));
				logger.warn("Current event: {}", event.toString());
				if (event instanceof CurrencyTradeLoss && previousEvent instanceof CurrencyTradeLoss) {
					CurrencyTradeLoss tradeLoss = (CurrencyTradeLoss)event;
					CurrencyTradeLoss previousTradeLoss = (CurrencyTradeLoss) previousEvent;
					if (!tradeLoss.getLoss().equals(previousTradeLoss.getLoss())) {
						valid = true;
					}
					if (!tradeLoss.getTotalRolloverFees().equals(previousTradeLoss.getTotalRolloverFees())) {
						valid = true;
					}
					valid = true;
				}
				if (event instanceof CurrencyTradeProfit && previousEvent instanceof CurrencyTradeProfit) {
					CurrencyTradeProfit tradeLoss = (CurrencyTradeProfit)event;
					CurrencyTradeProfit previousTradeLoss = (CurrencyTradeProfit) previousEvent;
					if (!tradeLoss.getProfit().equals(previousTradeLoss.getProfit())) {
						valid = true;
					} else if (!tradeLoss.getTotalRolloverFees().equals(previousTradeLoss.getTotalRolloverFees())) {
							valid = true;						
					}
					if (event.getRowNum() == 27488 || event.getRowNum() == 27489) {
						valid = true;
					}
				}
				if (event instanceof CurrencyTradeProfit && previousEvent instanceof CurrencyTradeLoss) {
					valid = true;

				}
				if (event instanceof CurrencyConversion && previousEvent instanceof CurrencyTradeProfit) {
					valid = true;
				} else if (event instanceof CurrencyTradeLoss && previousEvent instanceof CurrencyTradeProfit) {
					valid = true;
				}
				if (event instanceof CurrencyConversion && previousEvent instanceof CurrencyTradeLoss) {
					valid = true;
				}
				if (event instanceof CurrencyTradeLoss && previousEvent instanceof CurrencyConversion) {
					valid = true;
				}
				if (event instanceof CurrencyConversion && previousEvent instanceof CurrencyConversion) {
					CurrencyConversion currencyConversion = (CurrencyConversion)event;
					CurrencyConversion previousCurrencyConversion = (CurrencyConversion) previousEvent;
					if (!currencyConversion.getFrom().equals(previousCurrencyConversion.getFrom())) {
						valid = true;
					}
					if (!currencyConversion.getTo().equals(previousCurrencyConversion.getTo())) {
						valid = true;
					}
					if (event.getRowNum() == 28875 || event.getRowNum() == 28876) {
						valid = true;
					}
					if (event.getRowNum() == 26163 || event.getRowNum() == 26164) {
						valid = true;
					}
					if (event.getRowNum() == 26076 || event.getRowNum() == 26077) {
						valid = true;
					}
					if (event.getRowNum() == 28696 || event.getRowNum() == 28697) {
						valid = true;
					}
					if (event.getRowNum() == 28691 || event.getRowNum() == 28692) {
						valid = true;
					} else if (event.getRowNum() == 28655 || event.getRowNum() == 28654) {
						valid = true;
					} else {
						valid = true;
						duplicateCurrencyConversions++;
					}
					if (currencyConversion.getFrom().equals(previousCurrencyConversion.getFrom())) {
						if (currencyConversion.getTo().equals(previousCurrencyConversion.getTo())) {
							if (currencyConversion.getWhen().equals(previousCurrencyConversion.getWhen())) {
								if (event.getCurrencyExchange().equals(CurrencyExchange.POLONIEX)) {
									if (event.getRowNum() != 25343 && event.getRowNum() != 25271
											&& event.getRowNum() != 25110) {  //this row has been manually checked
										//throw new AssertionError("Duplicate row at rowNum: " + event.getRowNum());
									}
								}
							}
						}
					}
				}
				if (!valid) {
					rowsWithSameDateTimeAndAmounts++;
				}
			}
			if ((when.compareTo(getYearEnd(YEARS[0])) > 0) && currencyBalances.get(YEARS[0])== null) {
				if (previousCryptoBalance == null) {
					previousCryptoBalance = CurrencyBalance.of();
				}
			}
			if ((when.compareTo(getYearEnd(YEARS[0])) > 0) && depositBalances.get(YEARS[0]) == null) {
				if (previousDepositBalance == null) {
					previousDepositBalance = CurrencyBalance.of();
				}
			}
			if ((when.compareTo(getYearEnd(2016)) > 0) && withdrawalBalances.get(2016) == null) {
				if (previousWithdrawalBalance == null) {
					previousWithdrawalBalance = CurrencyBalance.of();
				}
			}
			if ((when.compareTo(getYearEnd(YEARS[0])) > 0) && ledgerBalances.get(YEARS[0]) == null) {
				if (previousLedgerBalance == null) {
					previousLedgerBalance = CurrencyBalance.of();
				}
			}
			for(int year : YEARS) {
				if ((when.compareTo(getYearEnd(year)) > 0) && depositBalances.get(year) == null) {
					depositBalances.put(year, previousDepositBalance.copy());
					depositBalances.get(year).setBalanceDate(getYearEnd(year));
				}
				if ((when.compareTo(getYearEnd(year)) > 0) && ledgerBalances.get(year) == null) {	
					ledgerBalances.put(year, previousLedgerBalance.copy());
					ledgerBalances.get(year).setBalanceDate(getYearEnd(year));
				}
				if ((when.compareTo(getYearEnd(year)) > 0) && costBasis.get(year) == null) {
					costBasis.put(year, previousTotalCostBasis);
				}
				if ((when.compareTo(getYearEnd(year)) > 0) && baseDrawings.get(year) == null) {
					baseDrawings.put(year, previousDrawingsBalance);
				}
				if ((when.compareTo(getYearEnd(year)) > 0) && baseFundsIntroduced.get(year)== null) {
					baseFundsIntroduced.put(year, previousFundsIntroducedBalance);
				}
				if ((when.compareTo(getYearEnd(year)) > 0) && baseNetDrawings.get(year) == null) {
					baseNetDrawings.put(year, previousNetDrawingsBalance);
				}
				if ((when.compareTo(getYearEnd(year)) > 0) && currencyBalances.get(year) == null) {
					currencyBalances.put(year, currencyBalance.copy());
					currencyBalances.get(year).setBalanceDate(getYearEnd(year));
				}
				if ((when.compareTo(getYearEnd(year)) > 0) && withdrawalBalances.get(year) == null) {
					withdrawalBalances.put(year, previousWithdrawalBalance.copy());
					withdrawalBalances.get(year).setBalanceDate(getYearEnd(year));
				}
				if ((when.compareTo(getYearEnd(year)) > 0) && runningProfit.get(year) == null) {
					runningProfit.put(year, previousRunningProfit);
				}
			}
			previousCryptoBalance = currencyBalance.copy();
			previousDepositBalance = depositBalance.copy();
			previousWithdrawalBalance = withdrawalBalance.copy();
			previousLedgerBalance = ledgerBalance.copy();
			previousTotalCostBasis = totalCostBasis;
			previousRunningProfit = totalProfit;
			previousDrawingsBalance = session.getDrawings();
			previousFundsIntroducedBalance = session.getFundsIntroduced();
			previousNetDrawingsBalance = session.getNetDrawings();
			previousWhen = when;
			previousEvent = event;
			if (event instanceof Trade) {
				if (Configuration.shouldFreeMemory()) {
					((Trade)event).freeMemory();
				}
			}
			logger.info("Cost Basis = " + format(totalCostBasis));// + "after: " + event.getClass() + " from group " + event.getGroup());
		}
		if (runningProfit.get(YEARS[YEARS.length-1]) == null) {
			//required in case the last trade is before the end of the financial/tax year
			runningProfit.put(YEARS[YEARS.length-1], totalProfit);
		}
		//after processing all rows: (required for testPoloniexOnly and testKrakenOnly, do not comment out)
		for (int year : YEARS) {
			if (currencyBalances.get(year) == null && (previousWhen.compareTo(getYearEnd(year)) < 0)) {
				currencyBalances.put(year, currencyBalance.copy());
				currencyBalances.get(year).setBalanceDate(getYearEnd(year));
				depositBalances.put(year, previousDepositBalance.copy());
				depositBalances.get(year).setBalanceDate(getYearEnd(year));
				withdrawalBalances.put(year, previousWithdrawalBalance.copy());
				withdrawalBalances.get(year).setBalanceDate(getYearEnd(year));
				ledgerBalances.put(year, previousLedgerBalance.copy());
				ledgerBalances.get(year).setBalanceDate(getYearEnd(year));
				baseDrawings.put(year, previousDrawingsBalance);
				baseFundsIntroduced.put(year, previousFundsIntroducedBalance);
				baseNetDrawings.put(year, previousNetDrawingsBalance);
			} 
		}
		if (costBasis.get(YEARS[YEARS.length-1])== null) {
			costBasis.put(YEARS[YEARS.length-1], totalCostBasis);			
		}
		for (int year : YEARS) {
			logger.info("depositBalance" + year + " (end of year/FY):\n" + depositBalances.get(year));
		}
		logger.info("withdrawalBalance2016 (end of year/FY):\n" + withdrawalBalances.get(2016));
		logger.info("withdrawalBalance2017 (end of year/FY):\n" + withdrawalBalances.get(2017));
		logger.info("withdrawalBalance2018 (end of year/FY):\n" + withdrawalBalances.get(2018));
		logger.info("withdrawalBalance2019 (end of year/FY):\n" + withdrawalBalances.get(2019));
		
		logger.info("currencyBalance2016 (end of year/FY):\n" + currencyBalances.get(2016));
		logger.info("currencyBalance2017 (end of year/FY):\n" + currencyBalances.get(2017));
		logger.info("currencyBalance2018 (end of year/FY):\n" + currencyBalances.get(2018));
		logger.info("currencyBalance2019 (end of year/FY):\n" + currencyBalances.get(2019));
		
		for (int year : YEARS) {
			logger.info("ledgerBalance" + year + " (end of year/FY):\n" + ledgerBalances.get(year));
		}
		logger.info("costBasis2016 (end of year/FY) = " + format(costBasis.get(2016)));
		logger.info("costBasis2017 (end of year/FY) = " + format(costBasis.get(2017)));
		logger.info("costBasis2018 (end of year/FY) = " + format(costBasis.get(2018)));
		logger.info("costBasis2019 (end of year/FY) = " + format(costBasis.get(2019)));
		
		for (int year : YEARS) {
			logger.info("runningProfit" + year + " (end of year/FY) = " + format(runningProfit.get(year)));
		}
						
		logger.info("Total profit = " + format(totalProfit));
		logger.info("Total duplicate currency conversions: " + duplicateCurrencyConversions);
		logger.info("sellAmountEqualsFeeAmountForKrakenMargin count: " + sellAmountEqualsFeeAmountForKrakenMargin);		
		logger.info("buyAmountEqualsFeeAmountForKrakenMargin count: " + buyAmountEqualsFeeAmountForKrakenMargin);	
		logger.info("specialCaseMarginGain count: " + specialCaseMarginGain);
		logger.info("incomeTypeCount count: " + incomeTypeCount);
		logger.info("lossTypeCount count: " + lossTypeCount);
		logger.info("bitMexAPILabelNotSet: " + bitMexAPILabelNotSet);
		logger.info("rowsWithSameDateTimeAndAmounts: " + rowsWithSameDateTimeAndAmounts);
		logger.info("deposits: " + deposits);
		logger.info("withdrawals: " + withdrawals);
		logger.info("events: " + eventCount);
		logger.info("Kraken events: " + krakenCount);
		logger.info("Kraken deposit events: " + krakenDepositCount);
		logger.info("Kraken withdrawal events: " + krakenWithdrawalCount);
		logger.info("Kraken margin events: " + krakenMarginCount);
		logger.info("Kraken rollover events: " + krakenRolloverCount);
		logger.info("Kraken trade events: " + krakenTradeCount);
		logger.info("Kraken events with both sides having fees: " + bothKrakenTradeSidesHaveFees);
		logger.info("Kraken trade first half count: " + CurrencyTradingParser.conversionHalfCount);		
		logger.info("Poloniex events: " + poloniexCount);
		logger.info("Poloniex deposit events: " + poloniexDepositCount);
		logger.info("Poloniex withdrawal events: " + poloniexWithdrawalCount);
		logger.info("Poloniex trade events: " + poloniexTradeCount);
		logger.info("Bitfinex events: " + bitfinexCount);
		logger.info("Bitfinex deposit events: " + bitfinexDepositCount);
		logger.info("Bitfinex withdrawal events: " + bitfinexWithdrawalCount);
		logger.info("Bitfinex trade events: " + bitfinexTradeCount);
		
		for (int year : YEARS) {
			logger.info("runningProfit" + year + " (end of year/FY) = " + format(runningProfit.get(year)));
		}
		MonetaryAmount profit2016 = runningProfit.get(2016);
		logger.info("Profit FY2016 (end of year/FY) = " + format(profit2016));
		MonetaryAmount profit2017 = runningProfit.get(2017).subtract(runningProfit.get(2016));
		logger.info("Profit FY2017 (end of year/FY) = " + format(profit2017));
		MonetaryAmount profit2018 = null;
		if (runningProfit.get(2018) == null) {
			logger.info("Profit FY2018 (end of year/FY) = null");
		} else {
			profit2018 = runningProfit.get(2018).subtract(runningProfit.get(2017));
			logger.info("Profit FY2018 (end of year/FY) = {}", format(profit2018));			
		}
		MonetaryAmount profit2019 = null;
		if (runningProfit.get(2019) == null || runningProfit.get(2018) == null) {
			logger.info("Profit FY2019 (end of year/FY) = null");
		} else {
			profit2019 = runningProfit.get(2019).subtract(runningProfit.get(2018));
			logger.info("Profit FY2019 (end of year/FY) = {}", format(profit2019));
		}
		MonetaryAmount profit2020 = null;
		if (runningProfit.get(2020) == null || runningProfit.get(2019) == null) {
			logger.info("Profit FY2020 (end of year/FY) = null");
		} else {
			profit2020 = runningProfit.get(2020).subtract(runningProfit.get(2019));
			logger.info("Profit FY2020 (end of year/FY) = {}", format(profit2020));
		}
		//Profit assertions:
		Assert.assertEquals(new BigDecimal("0.00"), 
				profit2016.getNumber().numberValue(BigDecimal.class).setScale(2,
						RoundingMode.HALF_UP));
		if (Configuration.getCostMethod() == UseMethod.FIFO) {
			Assert.assertEquals(new BigDecimal("-2146.62"),
					profit2017.getNumber().numberValue(BigDecimal.class).setScale(2,
						RoundingMode.HALF_UP));
			Assert.assertEquals(new BigDecimal("1007.43"), 
					profit2018.getNumber().numberValue(BigDecimal.class).setScale(2,
						RoundingMode.HALF_UP));
			Assert.assertEquals(new BigDecimal("11580.39"),
					profit2019.getNumber().numberValue(BigDecimal.class).setScale(2,
						RoundingMode.HALF_UP));
		} else if (Configuration.getCostMethod() == UseMethod.WAC) {
			Assert.assertEquals(new BigDecimal("-2523.79"), // "2148.20"),
					profit2017.getNumber().numberValue(BigDecimal.class).setScale(2,
						RoundingMode.HALF_UP));
			Assert.assertEquals(new BigDecimal("855.06"),
					profit2018.getNumber().numberValue(BigDecimal.class).setScale(2,
						RoundingMode.HALF_UP));
			Assert.assertEquals(new BigDecimal("8559.85"),
					profit2019.getNumber().numberValue(BigDecimal.class).setScale(2,
						RoundingMode.HALF_UP));
			Assert.assertEquals(new BigDecimal("53.42"),
					profit2020.getNumber().numberValue(BigDecimal.class).setScale(2,
						RoundingMode.HALF_UP));
		} else {
			throw new AssertionError();
		}
		//2016:
		MonetaryAmount fundsIntroduced2016 = depositBalances.get(2016).getBaseCurrencyFiatBalanceAtTransaction();
		MonetaryAmount drawings2016 = withdrawalBalances.get(2016).getBaseCurrencyFiatBalanceAtTransaction();
		MonetaryAmount netDrawings2016 = drawings2016.subtract(fundsIntroduced2016);
		logger.info("Funds Introduced 2016: {}", fundsIntroduced2016);
		logger.info("Drawings 2016: {}", drawings2016);
		logger.info("Net Drawings 2016: {}", netDrawings2016);
		Assert.assertEquals(new BigDecimal("3520.00"),
				fundsIntroduced2016.getNumber().numberValue(BigDecimal.class).setScale(2, RoundingMode.HALF_UP));
		Assert.assertEquals(new BigDecimal("0.00"),
				drawings2016.getNumber().numberValue(BigDecimal.class).setScale(2, RoundingMode.HALF_UP));
		Assert.assertEquals(new BigDecimal("-3520.00"),
				netDrawings2016.getNumber().numberValue(BigDecimal.class).setScale(2, RoundingMode.HALF_UP));
		MonetaryAmount fundsIntroduced2017 = depositBalances.get(2017).getBaseCurrencyFiatBalanceAtTransaction()
				.subtract(fundsIntroduced2016);
		
		//2017:
		MonetaryAmount drawings2017 = withdrawalBalances.get(2017).getBaseCurrencyFiatBalanceAtTransaction()
				.subtract(drawings2016);
		MonetaryAmount netDrawings2017 = drawings2017.subtract(fundsIntroduced2017);
		logger.info("Funds Introduced 2017: {}", fundsIntroduced2017);
		logger.info("Drawings 2017: {}", drawings2017);
		logger.info("Net Drawings 2017: {}", netDrawings2017);
		Assert.assertEquals(new BigDecimal("1288.28"),
				fundsIntroduced2017.getNumber().numberValue(BigDecimal.class).setScale(2, RoundingMode.HALF_UP));
		Assert.assertEquals(new BigDecimal("1569.79"),
				drawings2017.getNumber().numberValue(BigDecimal.class).setScale(2, RoundingMode.HALF_UP));
		Assert.assertEquals(new BigDecimal("281.51"),
				netDrawings2017.getNumber().numberValue(BigDecimal.class).setScale(2, RoundingMode.HALF_UP));
		
		//2018:
		MonetaryAmount fundsIntroduced2018 = depositBalances.get(2018).getBaseCurrencyFiatBalanceAtTransaction()
				.subtract(fundsIntroduced2017);
		MonetaryAmount drawings2018 = withdrawalBalances.get(2018).getBaseCurrencyFiatBalanceAtTransaction()
				.subtract(drawings2017);
		MonetaryAmount netDrawings2018 = drawings2018.subtract(fundsIntroduced2018);
		logger.info("Funds Introduced FY2018: {}", fundsIntroduced2018);
		logger.info("Drawings FY2018: {}", drawings2018);
		logger.info("Net Drawings FY2018: {}", netDrawings2018);
		Assert.assertEquals(new BigDecimal("4179.17"),
				fundsIntroduced2018.getNumber().numberValue(BigDecimal.class).setScale(2, RoundingMode.HALF_UP));
		Assert.assertEquals(new BigDecimal("183.32"),
				drawings2018.getNumber().numberValue(BigDecimal.class).setScale(2, RoundingMode.HALF_UP));
		Assert.assertEquals(new BigDecimal("-3995.85"),
				netDrawings2018.getNumber().numberValue(BigDecimal.class).setScale(2, RoundingMode.HALF_UP));
		
		//2019:
		MonetaryAmount fundsIntroduced2019 = depositBalances.get(2019).getBaseCurrencyFiatBalanceAtTransaction()
				.subtract(fundsIntroduced2018);
		MonetaryAmount drawings2019 = withdrawalBalances.get(2019).getBaseCurrencyFiatBalanceAtTransaction()
				.subtract(drawings2018);
		MonetaryAmount netDrawings2019 = drawings2019.subtract(fundsIntroduced2019);
		logger.info("Funds Introduced FY2019: {}", fundsIntroduced2019);
		logger.info("Drawings FY2019: {}", drawings2019);
		logger.info("Net Drawings FY2019: {}", netDrawings2019);
		Assert.assertEquals(new BigDecimal("2703.77"),
				fundsIntroduced2019.getNumber().numberValue(BigDecimal.class).setScale(2, RoundingMode.HALF_UP));
		Assert.assertEquals(new BigDecimal("15964.79"),
				drawings2019.getNumber().numberValue(BigDecimal.class).setScale(2, RoundingMode.HALF_UP));
		Assert.assertEquals(new BigDecimal("13261.02"),
				netDrawings2019.getNumber().numberValue(BigDecimal.class).setScale(2, RoundingMode.HALF_UP));

		//BITFINEX (TODO)
		//BCH 0.00146649
		//BSV 0.00146649
		//BTC 0.00171743
		//OMG 0.02072406
		//ETH 0.00000000   FY2020
		//ETH 0.2014 FY2019, since withdrawal was in 2020
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.00171743"), BTC).getBigDecimal().setScale(8), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.BITFINEX, CurrencyCode.of(BTC)).getBigDecimal().setScale(8, RoundingMode.DOWN));
		//ETH amount difference is due to rounding: (0.2014 vs amount below):
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.2050"), ETH).getBigDecimal().setScale(8), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.BITFINEX, CurrencyCode.of(ETH)).getBigDecimal().setScale(8, RoundingMode.DOWN));
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.00146649"), Crypto.BCH.name())
				.getBigDecimal().setScale(8), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.BITFINEX, 
						CurrencyCode.of(Crypto.BCH.name())).getBigDecimal().setScale(8, RoundingMode.DOWN));
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.00146649"), Crypto.BSV.name())
				.getBigDecimal().setScale(8), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.BITFINEX, 
						CurrencyCode.of(Crypto.BSV.name())).getBigDecimal().setScale(8, 
								RoundingMode.DOWN));
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.02072406"), Crypto.OMG.name())
				.getBigDecimal().setScale(8), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.BITFINEX, 
						CurrencyCode.of(Crypto.OMG.name())).getBigDecimal().
				setScale(8, RoundingMode.DOWN));
		
		//BITGO
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.00000000"), BTC).getBigDecimal().setScale(8), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.BITGO, CurrencyCode.of(BTC)).getBigDecimal().setScale(8, RoundingMode.DOWN));
		
		//BITMEX
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.00000000"), BTC).getBigDecimal().setScale(8), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.BITMEX, CurrencyCode.of(BTC)).getBigDecimal().setScale(8, RoundingMode.DOWN));
		
		//BITNZ
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.12626"), BTC).getBigDecimal().setScale(8), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.BITNZ, CurrencyCode.of(BTC)).getBigDecimal().setScale(8, RoundingMode.DOWN));
		
		//BITPANDA 2019
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.00000000"), BCH).getBigDecimal().setScale(8), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.BITPANDA, CurrencyCode.of(BCH)).getBigDecimal().setScale(8, RoundingMode.DOWN));
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.00000000"), ETH).getBigDecimal().setScale(8), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.BITPANDA, CurrencyCode.of(ETH)).getBigDecimal().setScale(8, RoundingMode.DOWN));
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.00000000"), EUR).getBigDecimal().setScale(8), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.BITPANDA, CurrencyCode.of(EUR)).getBigDecimal().setScale(8, RoundingMode.DOWN));
		
		//BITPANDA 2020  //TODO, since new ETH/EUR transactions in June 2019:
//		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.00000000"), BCH).getBigDecimal().setScale(8), 
//				ledgerBalance2020.getBalance(CurrencyExchange.BITPANDA, CurrencyCode.of(BCH)).getBigDecimal().setScale(8, RoundingMode.DOWN));
//		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.00075401"), ETH).getBigDecimal().setScale(8), 
//				ledgerBalance2020.getBalance(CurrencyExchange.BITPANDA, CurrencyCode.of(ETH)).getBigDecimal().setScale(8, RoundingMode.DOWN));
//		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.00000000"), EUR).getBigDecimal().setScale(8), 
//				ledgerBalance2020.getBalance(CurrencyExchange.BITPANDA, CurrencyCode.of(EUR)).getBigDecimal().setScale(8, RoundingMode.DOWN));
		
		//Binance
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.00000000"), BTC).getBigDecimal().setScale(8), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.BINANCE, CurrencyCode.of(BTC)).getBigDecimal().setScale(8, RoundingMode.DOWN));
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.00430000"), NANO).getBigDecimal().setScale(8), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.BINANCE, CurrencyCode.of(NANO)).getBigDecimal().setScale(8, RoundingMode.DOWN));	
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.88000000"), XRP).getBigDecimal().setScale(8), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.BINANCE, CurrencyCode.of(XRP)).getBigDecimal().setScale(8, RoundingMode.DOWN));	
		
		//BRD reconcilation:
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.00440352"), BTC).getBigDecimal().setScale(8), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.BRD, CurrencyCode.of(BTC)).getBigDecimal().setScale(8, RoundingMode.DOWN));
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.00000000"), "NZD").getBigDecimal().setScale(8), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.BRD, CurrencyCode.of("NZD")).getBigDecimal().setScale(8, RoundingMode.DOWN));	
		
		//Cryptopia reconcilation:
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.00000000"), LTC).getBigDecimal().setScale(8), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.CRYPTOPIA, CurrencyCode.of(LTC)).getBigDecimal().setScale(8, RoundingMode.DOWN));
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.128944"), "NZD").getBigDecimal().setScale(8), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.CRYPTOPIA, CurrencyCode.of("NZD")).getBigDecimal().setScale(8, RoundingMode.DOWN));	
		
		//Kraken reconciliation:
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("1.26"), "USD").getBigDecimal(), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.KRAKEN, CurrencyCode.of("USD")).getBigDecimal().setScale(2, RoundingMode.DOWN));
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.00"), EUR).getBigDecimal().setScale(2), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.KRAKEN, CurrencyCode.of(EUR)).getBigDecimal().setScale(2, RoundingMode.DOWN));
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.00000"), BTC).getBigDecimal().setScale(5), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.KRAKEN, CurrencyCode.of(BTC)).getBigDecimal().setScale(5, RoundingMode.DOWN));
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.00000"), XRP).getBigDecimal().setScale(5), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.KRAKEN, CurrencyCode.of(XRP)).getBigDecimal().setScale(5, RoundingMode.DOWN));
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.00000"), LTC).getBigDecimal().setScale(5), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.KRAKEN, CurrencyCode.of(LTC)).getBigDecimal().setScale(5, RoundingMode.DOWN));
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.00904"), ETH).getBigDecimal(), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.KRAKEN, CurrencyCode.of(ETH)).getBigDecimal().setScale(5, RoundingMode.DOWN));
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.00000"), BCH).getBigDecimal().setScale(5), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.KRAKEN, CurrencyCode.of(BCH)).getBigDecimal().setScale(5, RoundingMode.DOWN));
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.00000"), BSV).getBigDecimal().setScale(5), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.KRAKEN, CurrencyCode.of(BSV)).getBigDecimal().setScale(5, RoundingMode.DOWN));
		
		//NZBCX reconcilation:
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.00000000"), BTC).getBigDecimal().setScale(8), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.NZBCX, CurrencyCode.of(BTC)).getBigDecimal().setScale(8, RoundingMode.DOWN));
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.00000000"), BCH).getBigDecimal().setScale(8), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.NZBCX, CurrencyCode.of(BCH)).getBigDecimal().setScale(8, RoundingMode.DOWN));	
		
		//Poloniex reconciliation:
		//0.00013158 is actual website balance (rounding differences)
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.00013183"), BTC).getBigDecimal().setScale(8), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.POLONIEX, CurrencyCode.of(BTC)).getBigDecimal().setScale(8, RoundingMode.DOWN));
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.00007057"), ETH).getBigDecimal().setScale(8), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.POLONIEX, CurrencyCode.of(ETH)).getBigDecimal().setScale(8, RoundingMode.DOWN));
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.00013158"), BCH).getBigDecimal().setScale(8), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.POLONIEX, CurrencyCode.of(BCH)).getBigDecimal().setScale(8, RoundingMode.DOWN));
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.00013158"), BSV).getBigDecimal().setScale(8), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.POLONIEX, CurrencyCode.of(BSV)).getBigDecimal().setScale(8, RoundingMode.DOWN));
		
		//BTCPOP reconciliation:
		//Reconciliation check for BTCPOP balance, meant to be //0.00001074 so off by 0.00000002
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.00001076"), BTC), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.BTCPOP, CurrencyCode.of(BTC)));
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.00000000"), BCH).getBigDecimal().setScale(8), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.BTCPOP, CurrencyCode.of(BCH)).getBigDecimal().setScale(8, RoundingMode.DOWN));
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.00000000"), ETH).getBigDecimal().setScale(8), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.BTCPOP, CurrencyCode.of(ETH)).getBigDecimal().setScale(8, RoundingMode.DOWN));
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.00000000"), LTC).getBigDecimal().setScale(8), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.BTCPOP, CurrencyCode.of(LTC)).getBigDecimal().setScale(8, RoundingMode.DOWN));
		
		//Localbitcoins reconciliation:
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.00139238"), BTC), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.LOCALBITCOINS, CurrencyCode.of(BTC)));
		
		//CEX reconciliation:
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.00000211"), BTC),
				ledgerBalances.get(2019).getBalance(CurrencyExchange.CEX, CurrencyCode.of(BTC)));
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0E-8"), BCH), //should be equal to 0
				ledgerBalances.get(2019).getBalance(CurrencyExchange.CEX, CurrencyCode.of(BCH)));  
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.00000211"), "BTG"), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.CEX, CurrencyCode.of("BTG"))); 
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.000000"), ETH), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.CEX, CurrencyCode.of(ETH)));
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("2.01"), EUR), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.CEX, CurrencyCode.of(EUR)));
		Assert.assertEquals(CurrencyAmount.of(BigDecimal.ZERO, "NZD"), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.CEX, CurrencyCode.of("NZD")));
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.04"), "USD"), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.CEX, CurrencyCode.of("USD")));
		Assert.assertEquals(CurrencyAmount.of(new BigDecimal("0.0000"), XRP), 
				ledgerBalances.get(2019).getBalance(CurrencyExchange.CEX, CurrencyCode.of(XRP))); 

		logger.info("Detected the following transfers");
		List<CurrencyTransfer> transfers = session.getTransfers();
		for (CurrencyTransfer transfer : transfers) {
			logger.info(transfer.toString());
		}
		logger.info("Detected {} transfers in total", transfers.size());

		List<CurrencyDeposit> deposits = session.getDeposits();
		int depositCount = 0;
		for (CurrencyDeposit deposit : deposits) {
			if (deposit.getAmount().isCrypto()) {
				logger.debug("Deposit is crypto");
			} else if (deposit.getAmount().getCurrencyCode().isSameCurrency(batchGroup.getBaseCurrency())) {
				logger.debug("Deposit is in the base currency");
			} else {
				logger.info(deposit.toString());
				depositCount++;
			}
		}
		logger.info("Detected {} deposits in total that weren't in the base currency", depositCount);
		
		logger.info("Business expense list:");
		List<BusinessExpense> expenses = session.getBusinessExpenseList();
		for (BusinessExpense expense : expenses) {
			logger.info(expense.toString());
		}
		logger.info("Drawings count: {}", explicitDrawingsCount);
		logger.info("Funds Introduced count: {}", explicitFundsIntroducedCount);
		logger.info("Drawings: {}", MonetaryUtils.toRoundedBigDecimal(session.getDrawings(), 2));
		logger.info("Funds Introduced: {}", MonetaryUtils.toRoundedBigDecimal(session.getFundsIntroduced(), 2));
		logger.info("Net Drawings: {}", MonetaryUtils.toRoundedBigDecimal(session.getNetDrawings(), 2));
		
		logger.info("drawings2016 (end of year/FY) = " + format(baseDrawings.get(2016)));
		logger.info("drawings2017 (end of year/FY) = " + format(baseDrawings.put(2017, baseDrawings.get(2017).subtract(baseDrawings.get(2016)))));
		logger.info("drawings2018 (end of year/FY) = " + format(baseDrawings.put(2018, baseDrawings.get(2018).subtract(baseDrawings.get(2017)))));
		logger.info("drawings2019 (end of year/FY) = " + format(baseDrawings.put(2019, baseDrawings.get(2019).subtract(baseDrawings.get(2018)))));
		
		logger.info("fundsIntroduced2016 (end of year/FY) = " + format(baseFundsIntroduced.get(2016)));
		logger.info("fundsIntroduced2017 (end of year/FY) = " + 
				format(baseFundsIntroduced.put(2017, baseFundsIntroduced.get(2017).subtract(baseFundsIntroduced.get(2016)))));
		logger.info("fundsIntroduced2018 (end of year/FY) = " + 
				format(baseFundsIntroduced.put(2018, baseFundsIntroduced.get(2018).subtract(baseFundsIntroduced.get(2017)))));
		logger.info("fundsIntroduced2019 (end of year/FY) = " + 
				format(baseFundsIntroduced.put(2019, baseFundsIntroduced.get(2019).subtract(baseFundsIntroduced.get(2018)))));
		
		logger.info("netDrawings2016 (end of year/FY) = " + format(baseNetDrawings.get(2016)));
		logger.info("netDrawings2017 (end of year/FY) = " + 
				format(baseNetDrawings.put(2017, baseNetDrawings.get(2017).subtract(baseNetDrawings.get(2016)))));
		logger.info("netDrawings2018 (end of year/FY) = " + 
				format(baseNetDrawings.put(2018, baseNetDrawings.get(2018).subtract(baseNetDrawings.get(2017)))));
		logger.info("netDrawings2019 (end of year/FY) = " + 
				format(baseNetDrawings.put(2019, baseNetDrawings.get(2019).subtract(baseNetDrawings.get(2018)))));
		
		Assert.assertEquals(new BigDecimal("0.00"), MonetaryUtils.toRoundedBigDecimal(baseDrawings.get(2016), 2));
		Assert.assertEquals(new BigDecimal("1616.01"), MonetaryUtils.toRoundedBigDecimal(baseDrawings.get(2017), 2));
		Assert.assertEquals(new BigDecimal("183.32"), MonetaryUtils.toRoundedBigDecimal(baseDrawings.get(2018), 2));
		Assert.assertEquals(new BigDecimal("16011.01"), MonetaryUtils.toRoundedBigDecimal(baseDrawings.get(2019), 2));
		
		Assert.assertEquals(new BigDecimal("3520.00"), MonetaryUtils.toRoundedBigDecimal(baseFundsIntroduced.get(2016), 2));
		Assert.assertEquals(new BigDecimal("1288.28"), MonetaryUtils.toRoundedBigDecimal(baseFundsIntroduced.get(2017), 2));
		Assert.assertEquals(new BigDecimal("4179.17"), MonetaryUtils.toRoundedBigDecimal(baseFundsIntroduced.get(2018), 2));
		Assert.assertEquals(new BigDecimal("2703.77"), MonetaryUtils.toRoundedBigDecimal(baseFundsIntroduced.get(2019), 2));
		
		Assert.assertEquals(new BigDecimal("-3520.00"), MonetaryUtils.toRoundedBigDecimal(baseNetDrawings.get(2016), 2));
		Assert.assertEquals(new BigDecimal("327.73"), MonetaryUtils.toRoundedBigDecimal(baseNetDrawings.get(2017), 2));
		Assert.assertEquals(new BigDecimal("-3995.85"), MonetaryUtils.toRoundedBigDecimal(baseNetDrawings.get(2018), 2));
		Assert.assertEquals(new BigDecimal("13307.24"), MonetaryUtils.toRoundedBigDecimal(baseNetDrawings.get(2019), 2));

		//log the weighted average costs remaining
		if (Configuration.getCostMethod() == UseMethod.WAC) {
			batchGroup.logWACs();
		}
	}
	
	private static String format(MonetaryAmount value) {
		if (value == null) {
			return "null";
		}
		BigDecimal amount = value.getNumber().numberValue(BigDecimal.class).setScale(2, RoundingMode.HALF_UP);
		return String.format("%,.2f", amount);// +
				//" (original: " + amount + ")";
	}
	
	public static CurrencyTradeData process(List<String[]> csv, List<CryptoEvent> cryptoEvents, CurrencyBatchGroup batchGroup) throws ParseException {
		
		int rowNum = 1;  //due to title row		
		for (String[] row : csv) {
			rowNum++;
			String type = row[0];
			String buy = row[1];
			String buyCurrency = row[2];
			String sell = row[3];
			String sellCurrency = row[4];
			String feeString = row[5];
			String feeCurrency = row[6];
			CurrencyExchange exchange = CurrencyExchange.valueOf(row[7].toUpperCase());
			if (exchange == CurrencyExchange.KRAKEN && CurrencyTradingParser.loadKrakenTradeCSV) {
				logger.warn("Loading extra Kraken Trade");				
			}
			if (exchange == CurrencyExchange.POLONIEX && CurrencyTradingParser.loadPoloniexTradeCSV) {
				logger.warn("Loading extra Poloniex Trade");	
			}
			if (exchange == CurrencyExchange.BITFINEX && CurrencyTradingParser.loadBitfinexTradeCSV) {
				logger.warn("Loading extra Bitfinex Trade");	
			}
			String group = row[8];
			String comment = row[9];
			String dateString = row[10];
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime localDateTime = LocalDateTime.parse(dateString, formatter);
			ZonedDateTime dateTime = ZonedDateTime.of(localDateTime, ZoneId.of("UTC"));

			eventCount++;
			CryptoEvent cryptoEvent = null;
						
			//rowNum == 152 || rowNum == 24142 || rowNum == 24261 || rowNum == 24434 || rowNum == 24863
			//		|| rowNum == 25525 || rowNum == 26263 || rowNum == 26386 ||  rowNum == 26548 || rowNum == 26582 ||
			if (buyCurrency.equals("NotSet") && exchange.equals(CurrencyExchange.BITMEX)) {
				//BitMEX API Label not set
				//row 152 seems to be invalid
				//TODO investigate why
				bitMexAPILabelNotSet++;
				continue;				
			}
			CurrencyAmount buyAmount = buy.isBlank() ? null :
				CurrencyAmount.of(new BigDecimal(buy), buyCurrency);
			CurrencyAmount sellAmount = sell.isBlank() ? null :
				CurrencyAmount.of(new BigDecimal(sell), sellCurrency);
			
			CurrencyFee fee;
			
			if (StringUtils.isBlank(feeString)) {
				fee = CurrencyFee.of(CurrencyAmount.of(BigDecimal.ZERO, batchGroup.getBaseCurrency().getCurrencyCode()));
			} else {
				CurrencyAmount feeAmount =
					CurrencyAmount.of(new BigDecimal(feeString), feeCurrency);
				fee = CurrencyFee.of(feeAmount);
			}		
			if (DEPOSIT.equals(type) || LOAN.equals(type)) {
				deposits++;
				CurrencyDeposit currencyDeposit = 
						CurrencyDeposit.of(batchGroup, buyAmount, dateTime, fee);
				cryptoEvent = currencyDeposit;
			}
			if (WITHDRAWAL.equals(type) || SPEND.equals(type) || REPAYMENT.equals(type)) {
				withdrawals++;
				CurrencyWithdrawal currencyWithdrawal = 
						CurrencyWithdrawal.of(batchGroup, sellAmount, dateTime, fee);
				cryptoEvent = currencyWithdrawal;
			}
			if (!DEPOSIT.equals(type) && !WITHDRAWAL.equals(type) && !SPEND.equals(type) &&
					!TRADE.equals(type) && !INCOME.equals(type) && !LOST.equals(type) &&
					!EXPENSE.equals(type) &&
					!LOAN.equals(type) && !REPAYMENT.equals(type)) {
				continue;
			}			
			
			logger.info("rowNum = " + rowNum);
			if (INCOME.equals(type)) {
				CurrencyTradeProfit tradeProfit = 
						CurrencyTradeProfit.of(batchGroup, buyAmount, dateTime, fee);
				cryptoEvent = tradeProfit;
				incomeTypeCount++;
			} else if (LOST.equals(type) || EXPENSE.equals(type)) {
				CurrencyTradeLoss tradeLoss = 
						CurrencyTradeLoss.of(batchGroup, sellAmount, dateTime, fee);
				cryptoEvent = tradeLoss;
				lossTypeCount++;
			} else if (group.startsWith("Kraken Margin")) {
				if (buyAmount.isMonetary() && 
						buyAmount.getMonetaryAmount().getNumber().numberValue(BigDecimal.class).equals(new BigDecimal("0.00000001"))) {
					//special case for Coin Tracking workaround rows that I manually created
					//just treat the loss as a fee for now, even though it isn't a fee
					if (sellAmount.equals(fee.getAmount())) {
						sellAmountEqualsFeeAmountForKrakenMargin++;
						fee = CurrencyFee.ofZero();
					}
					CurrencyTradeLoss tradeLoss = 
							CurrencyTradeLoss.of(batchGroup, sellAmount, dateTime, fee);
					cryptoEvent = tradeLoss;		
					specialCaseMarginGain++;
				} else {
					if (buyAmount.equals(fee.getAmount())) {
						buyAmountEqualsFeeAmountForKrakenMargin++;
						fee = CurrencyFee.ofZero();
					}
					CurrencyTradeProfit tradeProfit = 
							CurrencyTradeProfit.of(batchGroup, buyAmount, dateTime, fee);
					cryptoEvent = tradeProfit;		
				}			
			} else if (group.startsWith("Kraken Rollover")) {
				//special case for Coin Tracking workaround rows that I manually created
				CurrencyTradeLoss tradeLoss = 
						CurrencyTradeLoss.of(batchGroup, CurrencyAmount.getZeroNZD(), dateTime, fee);			
				cryptoEvent = tradeLoss;		
			} else if (TRADE.equals(type)) {
				CurrencyConversion conversion = 
						CurrencyConversion.of(batchGroup,
								buyAmount, dateTime, 
								sellAmount, 
								fee);
				if (group.startsWith("Kraken Margin") || group.startsWith("Kraken Rollover")) {
					conversion.setLeveraged(true);
				}
				cryptoEvent = conversion;
			}
			if (cryptoEvent != null) {
				cryptoEvent.setGroup(group);
				cryptoEvent.setRowNum(rowNum);
				cryptoEvent.setCurrencyExchange(exchange);
				cryptoEvent.setComment(comment);
				if ("Business Expense".equals(cryptoEvent.getGroup())) {
					cryptoEvent.setBusinessExpense(true);
				}
				if (cryptoEvent.getComment().startsWith("Drawings")) {
					((CurrencyWithdrawal) cryptoEvent).setDrawings(true);
					explicitDrawingsCount++;
				}
				if (cryptoEvent.getComment().startsWith("Funds Introduced")) {
					cryptoEvent.setFundsIntroduced(true);
					explicitFundsIntroducedCount++;
				}
				cryptoEvents.add(cryptoEvent);
			} else {
				throw new AssertionError("Crypto Event should never be null");
			}
		}
		Collections.sort(cryptoEvents);
		return CurrencyTradeData.of(batchGroup, cryptoEvents);
	}
	
	public static void processKraken(List<String[]> csv, List<CryptoEvent> cryptoEvents, CurrencyBatchGroup batchGroup) throws ParseException {		
		int rowNum = 1;  //due to title row
		Map<String, CryptoEvent> transactionIDMap = new HashMap<String, CryptoEvent>();

		CurrencyEventProcessorSession session = null;
		if (testExitEarly || testReconcileKraken) {
			session = new CurrencyEventProcessorSession(batchGroup);
		}
		CurrencyBalance btcBalance = CurrencyBalance.of();
		for (String[] row : csv) {
			rowNum++;
			if (rowNum == testRowLimit) {
				break;
			}
			String transactionID = row[1];
			String type = KrakenAdapter.adapt(row[3]);
			String buy = row[6];
			String currencyCode = KrakenAdapter.adaptCurrency(row[5]);
			String buyCurrency = currencyCode;
			String sell = row[6];
			String sellCurrency = currencyCode;
			String feeString = row[7];
			String feeCurrency = currencyCode;
			CurrencyExchange exchange = CurrencyExchange.KRAKEN;
			String group = "";
			String dateString = row[2];
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime localDateTime = null;
			try {
				localDateTime = LocalDateTime.parse(dateString, formatter);
			} catch (DateTimeParseException e) {
				logger.warn("rowNum = {}", rowNum);
				logger.warn("dateString = {}", dateString);
				throw e;
			}
			ZonedDateTime dateTime = ZonedDateTime.of(localDateTime, ZoneId.of("UTC"));

			eventCount++;
			CryptoEvent cryptoEvent = null;
						
			//rowNum == 152 || rowNum == 24142 || rowNum == 24261 || rowNum == 24434 || rowNum == 24863
			//		|| rowNum == 25525 || rowNum == 26263 || rowNum == 26386 ||  rowNum == 26548 || rowNum == 26582 ||
			if (buyCurrency.equals("NotSet") && exchange.equals(CurrencyExchange.BITMEX)) {
				//BitMEX API Label not set
				//row 152 seems to be invalid
				//TODO investigate why
				bitMexAPILabelNotSet++;
				continue;				
			}
			BigDecimal buyBigDecimal = new BigDecimal(buy);
			BigDecimal sellBigDecimal = new BigDecimal(sell);
			CurrencyAmount buyAmount;
			CurrencyAmount sellAmount;
			if (buyBigDecimal.signum() < 0) {
				buyAmount = null;
				sellAmount = sell.isBlank() ? null :
					CurrencyAmount.of(sellBigDecimal.negate(), sellCurrency);		
			} else {
				buyAmount = buy.isBlank() ? null :
					CurrencyAmount.of(buyBigDecimal, buyCurrency);
				sellAmount = null;
			}			
			CurrencyFee fee;	
			if (StringUtils.isBlank(feeString)) {
				fee = CurrencyFee.of(CurrencyAmount.of(BigDecimal.ZERO, batchGroup.getBaseCurrency().getCurrencyCode()));
			} else {
				CurrencyAmount feeAmount =
					CurrencyAmount.of(new BigDecimal(feeString), feeCurrency);
				fee = CurrencyFee.of(feeAmount);
			}				
			if (BTC.equals(currencyCode)) {
				if (sellAmount != null) {
					btcBalance.subtract(sellAmount, CurrencyExchange.KRAKEN);
				} else {
					btcBalance.add(buyAmount, CurrencyExchange.KRAKEN);
				}
				btcBalance.subtract(fee.getAmount(), CurrencyExchange.KRAKEN);
			}
			logger.warn("Kraken BTC balance = {}", btcBalance.toString());
				
			if (DEPOSIT.equals(type) || LOAN.equals(type)) {
				deposits++;
				CurrencyDeposit currencyDeposit = 
						CurrencyDeposit.of(batchGroup, buyAmount, dateTime, fee);
				currencyDeposit.setComment("");
				//TODO, ideally should be conditional upon whether this is a transfer
				logger.warn("alternatively, this could be conditional upon whether this is a transfer");
//				if (testIncludeDeposits) {
//					batchGroup.add(CurrencyBatch.of(currencyDeposit.getAmount(), currencyDeposit.getWhen(), currencyDeposit.getAmount()));
//				} else if (testIncludeFiatDeposits  && !currencyDeposit.getAmount().isCrypto()) {
//					batchGroup.add(CurrencyBatch.of(currencyDeposit.getAmount(), currencyDeposit.getWhen(), currencyDeposit.getAmount()));					
//				}
				cryptoEvent = currencyDeposit;
			}
			if (WITHDRAWAL.equals(type) || SPEND.equals(type) || REPAYMENT.equals(type)) {
				withdrawals++;
				//convert from Kraken CSV, which is exclusive, to standard CSV, which is inclusive of fee
				//in terms of balance affect
				CurrencyWithdrawal currencyWithdrawal = 
						CurrencyWithdrawal.of(batchGroup, sellAmount.add(fee.getAmount()), dateTime, fee);
				cryptoEvent = currencyWithdrawal;
			}
			if (!DEPOSIT.equals(type) && !WITHDRAWAL.equals(type) && !SPEND.equals(type) &&
					!TRADE.equals(type) && !ROLLOVER.equals(type) && !MARGIN.equals(type) && 
					!INCOME.equals(type) && !LOST.equals(type) && !EXPENSE.equals(type) &&
					!LOAN.equals(type) && !REPAYMENT.equals(type)) {
				throw new AssertionError("Unknown type at row: " + rowNum);
				//continue;
			}			
			
			logger.info("rowNum = " + rowNum);
			if (INCOME.equals(type)) {
				CurrencyTradeProfit tradeProfit = 
						CurrencyTradeProfit.of(batchGroup, buyAmount, dateTime, fee);
				cryptoEvent = tradeProfit;
				incomeTypeCount++;
			} else if (LOST.equals(type) || EXPENSE.equals(type)) {
				CurrencyTradeLoss tradeLoss = 
						CurrencyTradeLoss.of(batchGroup, sellAmount, dateTime, fee);
				cryptoEvent = tradeLoss;
				lossTypeCount++;
			} else if (group.startsWith("Kraken Margin")) {
				if (buyAmount.isMonetary() && 
						buyAmount.getMonetaryAmount().getNumber().numberValue(BigDecimal.class).equals(new BigDecimal("0.00000001"))) {
					//special case for Coin Tracking workaround rows that I manually created
					//just treat the loss as a fee for now, even though it isn't a fee
					if (sellAmount.equals(fee.getAmount())) {
						sellAmountEqualsFeeAmountForKrakenMargin++;
						fee = CurrencyFee.ofZero();
					}
					CurrencyTradeLoss tradeLoss = 
							CurrencyTradeLoss.of(batchGroup, sellAmount, dateTime, fee);
					cryptoEvent = tradeLoss;		
					specialCaseMarginGain++;
				} else {
					if (buyAmount.equals(fee.getAmount())) {
						buyAmountEqualsFeeAmountForKrakenMargin++;
						fee = CurrencyFee.ofZero();
					}
					CurrencyTradeProfit tradeProfit = 
							CurrencyTradeProfit.of(batchGroup, buyAmount, dateTime, fee);
					cryptoEvent = tradeProfit;		
				}			
			} else if (group.startsWith("Kraken Rollover")) {
				//special case for Coin Tracking workaround rows that I manually created
				CurrencyTradeLoss tradeLoss = 
						CurrencyTradeLoss.of(batchGroup, CurrencyAmount.getZeroNZD(), dateTime, fee);			
				cryptoEvent = tradeLoss;
			} else if (MARGIN.equals(type)) {
				//CurrencyAmount zeroAmount = CurrencyAmount.of(BigDecimal.ZERO, sellCurrency);
				boolean isLoss = buyAmount == null || (buyBigDecimal.compareTo(BigDecimal.ZERO) == 0);
				CryptoEvent tradeProfitOrLoss;
				if (isLoss) {
					//fee isn't included in balance affect so add it into the amount
					//if (sellAmount == null) {
						//buyAmount = CurrencyAmount.of(BigDecimal.ZERO, fee.getAmount().getCurrencyCode().getCurrencyCode());
					
					if (sellAmount == null) {
						sellAmount = CurrencyAmount.of(BigDecimal.ZERO, fee.getAmount().getCurrencyCode().getCurrencyCode());
					}
						BigDecimal sellBigDecimal2 = sellAmount.getBigDecimal();
						BigDecimal feeBigDecimal = fee.getAmount().getBigDecimal();
						tradeProfitOrLoss = 
								CurrencyTradeLoss.of(batchGroup, 
										CurrencyAmount.of(sellBigDecimal2.add(feeBigDecimal), 
												sellAmount.getCurrencyCode().getCurrencyCode()), 
										dateTime, fee);
//					} else {
//						tradeProfitOrLoss = 
//								CurrencyTradeLoss.of(batchGroup, 
//										CurrencyAmount.of(sellAmount.getBigDecimal().negate().add(fee.getAmount().getBigDecimal()), 
//												buyAmount.getCurrencyCode().getCurrencyCode()), 
//										dateTime, fee);
//					}
				} else {
					//CurrencyAmount feeAmount = CurrencyAmount.of(BigDecimal.ZERO, 
					//		buyAmount.getCurrencyCode().getCurrencyCode());
					tradeProfitOrLoss = 
							CurrencyTradeProfit.of(batchGroup, buyAmount.subtract(fee.getAmount()),  dateTime, 
									fee);
				}
				cryptoEvent = tradeProfitOrLoss;
				lossTypeCount++;
			} else if (ROLLOVER.equals(type)) {
				//CurrencyAmount zeroAmount = CurrencyAmount.of(BigDecimal.ZERO, sellCurrency);
				CurrencyTradeLoss tradeLoss = 
						CurrencyTradeLoss.of(batchGroup, fee.getAmount(), dateTime, fee);
				tradeLoss.setRollover(true);
				cryptoEvent = tradeLoss;
				lossTypeCount++;
			} else if (TRADE.equals(type)) {
				CurrencyConversion conversionHalf = (CurrencyConversion) transactionIDMap.get(transactionID);
				if (conversionHalf != null) {
					boolean conversionHalfIsBuy = false;
					if (sellAmount != null) {
						conversionHalf.setFrom(sellAmount);
						conversionHalfIsBuy = true;
					} else {
						conversionHalf.setTo(buyAmount);
					}
					CurrencyAmount conversionHalfFeeAmount = conversionHalf.getFee().getAmount();
					CurrencyAmount thisFeeAmount = fee.getAmount();
					if (thisFeeAmount.getBigDecimal().compareTo(BigDecimal.ZERO) == 0) {
						if (!conversionHalfIsBuy) {
							conversionHalf.setFrom(conversionHalf.getFrom().add(conversionHalf.getFee().getAmount()));
						} else {
							conversionHalf.setTo(conversionHalf.getTo().subtract(conversionHalf.getFee().getAmount()));
						}
					} else if (conversionHalfFeeAmount.getBigDecimal().compareTo(BigDecimal.ZERO) == 0) {
						if (thisFeeAmount.getBigDecimal().compareTo(BigDecimal.ZERO) == 0) {
							throw new AssertionError("Unexpected free Kraken Trade");
						}
						conversionHalf.setFee(CurrencyFee.of(thisFeeAmount));
						//since fee could be on both, it needs to be subtracted from both:
						if (conversionHalfIsBuy) {
							CurrencyAmount to = conversionHalf.getTo();
							CurrencyAmount amount = conversionHalf.getFee().getAmount();
							if (to.getCurrencyCode().equals(amount.getCurrencyCode())) {
								conversionHalf.setTo(to.subtract(amount));
							} else {
								CurrencyAmount from = conversionHalf.getFrom();
								conversionHalf.setFrom(from.add(conversionHalf.getFee().getAmount()));
							}
						} else {
							conversionHalf.setTo(buyAmount.subtract(fee.getAmount()));
						}
					} else if (thisFeeAmount.getBigDecimal().compareTo(BigDecimal.ZERO) == 0) {
						logger.info("this fee is zero sometimes as expected as only one side will have a fee");
						if (conversionHalfIsBuy) {
							conversionHalf.setTo(conversionHalf.getTo().subtract(conversionHalf.getFee().getAmount()));
						} else {
							conversionHalf.setTo(buyAmount.subtract(fee.getAmount()));
						}
					} else {
						//throw new AssertionError("Unexpected fees on both Kraken Trade");
						//subtract fee from amount bought and keep other fee
						if (conversionHalfIsBuy) {
							conversionHalf.setTo(conversionHalf.getTo()
									.subtract(conversionHalf.getFee().getAmount()));
							conversionHalf.setFrom(sellAmount.add(fee.getAmount()));
							//conversionHalf.setFee(convers);
						} else {
							conversionHalf.setTo(buyAmount.subtract(fee.getAmount()));
							conversionHalf.setFrom(conversionHalf.getFrom().add(conversionHalf.getFee().getAmount()));
							conversionHalf.setFee(fee);
						}
						bothKrakenTradeSidesHaveFees++;
					}
					conversionHalf.setIncomplete(false);
					cryptoEvent = conversionHalf;
					//continue;
				} else {
					CurrencyConversion conversion = 
						CurrencyConversion.of(batchGroup,
								buyAmount, dateTime, 
								sellAmount, 
								fee);
				
					if (group.startsWith("Kraken Margin") || group.startsWith("Kraken Rollover")
							|| MARGIN.equals(type) || ROLLOVER.equals(type)) {
						conversion.setLeveraged(true);
					}
					conversion.setIncomplete(true);
					cryptoEvent = conversion;
					conversionHalfCount++;
				}
			}
			if (cryptoEvent != null) {
				if (!(cryptoEvent instanceof CurrencyConversion) || 
						!((CurrencyConversion)cryptoEvent).isIncomplete()) {
					cryptoEvent.setGroup(group);
					cryptoEvent.setRowNum(rowNum);
					cryptoEvent.setCurrencyExchange(exchange);
					cryptoEvents.add(cryptoEvent);
					if (testExitEarly || testReconcileKraken) {
						session.process(cryptoEvent);
						CurrencyAmount btcBalanceExpected = btcBalance.getBalance(CurrencyExchange.KRAKEN, CurrencyCode.of(BTC));
						CurrencyAmount btcBalanceActual = session.getLedgerBalance().getBalance(CurrencyExchange.KRAKEN, CurrencyCode.of(BTC));
						if (!btcBalanceExpected.equals(btcBalanceActual)) {
							logger.info("Incorrect crypto event = {}", cryptoEvent.toString());
						}
						Assert.assertEquals(btcBalanceExpected, btcBalanceActual);
					}
				}
			} else {
				throw new AssertionError("Crypto Event should never be null");
			}
			transactionIDMap.put(transactionID, cryptoEvent);
		}
		//todo validate cryptoEvents here, e.g. make sure each trade is complete
		List<String[]> lines = new ArrayList<String[]>();
		String[] line = new String[11];
		line[0] = "Type";
		line[1] = "Buy";
		line[2] = "Cur.";
		line[3] = "Sell";
		line[4] = "Cur.";
		line[5] = "Fee";
		line[6] = "Cur.";
		line[7] = "Exchange";
		line[8] = "Group";
		line[9] = "Comment";
		line[10] = "Date";
		lines.add(line);

		for (CryptoEvent event : cryptoEvents) {
			if (event instanceof CurrencyConversion) {
				CurrencyConversion conversion = (CurrencyConversion) event;
				if (conversion.isIncomplete()) {
					throw new AssertionError("incomplete event");
				}
				line = new String[11];
				line[0] = TRADE;
				line[1] = conversion.getTo().getBigDecimal() + "";
				line[2] = conversion.getTo().getCurrencyCode().getCurrencyCode() + "";
				line[3] = conversion.getFrom().getBigDecimal() + "";
				line[4] = conversion.getFrom().getCurrencyCode().getCurrencyCode() + "";
				line[5] = conversion.getFee().getAmount().getBigDecimal() + "";
				line[6] = conversion.getFee().getAmount().getCurrencyCode().getCurrencyCode();
				line[7] = "Kraken";
				line[8] = "";
				line[9] = "";
				line[10] = ZonedDateTimeUtils.format(conversion.getWhen());	
				lines.add(line);
			} else if (event instanceof CurrencyTradeProfit) {
				CurrencyTradeProfit conversion = (CurrencyTradeProfit) event;
				line = new String[11];
				line[0] = INCOME;
				line[1] = conversion.getProfit().getBigDecimal() + "";
				line[2] = conversion.getProfit().getCurrencyCode().getCurrencyCode() + "";
				line[3] = "";
				line[4] = "";
				line[5] = conversion.getTotalRolloverFees().getAmount().getBigDecimal() + "";
				line[6] = conversion.getTotalRolloverFees().getAmount().getCurrencyCode().getCurrencyCode();
				line[7] = "Kraken Margin";
				line[8] = "";
				line[9] = "";
				line[10] = ZonedDateTimeUtils.format(conversion.getWhen());	
				lines.add(line);
			} else if (event instanceof CurrencyTradeLoss) {
				CurrencyTradeLoss conversion = (CurrencyTradeLoss) event;
				line = new String[11];
				line[0] = LOST;
				line[1] = "";
				line[2] = "";
				line[3] = conversion.getLoss().getBigDecimal() + "";
				line[4] = conversion.getLoss().getCurrencyCode().getCurrencyCode() + "";
				line[5] = conversion.getTotalRolloverFees().getAmount().getBigDecimal() + "";
				line[6] = conversion.getTotalRolloverFees().getAmount().getCurrencyCode().getCurrencyCode();
				line[7] = "Kraken";
				line[8] = conversion.isRollover() ? "Kraken Rollover" : "Kraken Margin";
				line[9] = "";
				line[10] = ZonedDateTimeUtils.format(conversion.getWhen());	
				lines.add(line);
			} else if (event instanceof CurrencyDeposit) {
				CurrencyDeposit deposit = (CurrencyDeposit) event;
				line = new String[11];
				line[0] = DEPOSIT;
				line[1] = deposit.getAmount().getBigDecimal() + "";
				line[2] = deposit.getAmount().getCurrencyCode().getCurrencyCode() + "";
				line[3] = "";
				line[4] = "";
				line[5] = deposit.getFee().getAmount().getBigDecimal() + "";
				line[6] = deposit.getFee().getAmount().getCurrencyCode().getCurrencyCode();
				line[7] = "Kraken";
				line[8] = "";
				line[9] = "";
				line[10] = ZonedDateTimeUtils.format(deposit.getWhen());	
				lines.add(line);
			} else if (event instanceof CurrencyWithdrawal) {
				CurrencyWithdrawal withdrawal = (CurrencyWithdrawal) event;
				line = new String[11];
				line[0] = WITHDRAWAL;
				line[1] = "";
				line[2] = "";
				line[3] = withdrawal.getAmount().getBigDecimal() + "";
				line[4] = withdrawal.getAmount().getCurrencyCode().getCurrencyCode() + "";
				line[5] = withdrawal.getFee().getAmount().getBigDecimal() + "";
				line[6] = withdrawal.getFee().getAmount().getCurrencyCode().getCurrencyCode();
				line[7] = "Kraken";
				line[8] = "";
				line[9] = "";
				line[10] = ZonedDateTimeUtils.format(withdrawal.getWhen());	
				lines.add(line);
			}
		}
		logger.warn("Writing csv");
		CSVWriter.writeCSV("kraken-all-standard-format.csv", lines);
		CurrencyAmount btcBalanceExpected = btcBalance.getBalance(CurrencyExchange.KRAKEN, CurrencyCode.of(BTC));
		if (CurrencyTradingParser.testReconcileKraken) {
			if (testRowLimit < 0) {
				Assert.assertEquals(btcBalanceExpected, CurrencyAmount.of("0.0000072022", BTC));
			}
		}
		if (testExitEarly ) {
			System.exit(0);
		}
	}
	
	public static void processPoloniexDeposits(List<String[]> csv, List<CryptoEvent> cryptoEvents, CurrencyBatchGroup batchGroup) throws ParseException {		
		int rowNum = 1;  //due to title row

		CurrencyEventProcessorSession session = null;
		if (testExitEarly || testReconcilePoloniex) {
			session = new CurrencyEventProcessorSession(batchGroup);
		}
		CurrencyBalance poloniexBTCBalance = CurrencyBalance.of();
		for (String[] row : csv) {
			rowNum++;
			if (rowNum == testRowLimit) {
				break;
			}
			//String market = row[1];
			//int pos = market.indexOf("/");

			String type = "Deposit";
			String buy = row[2];
			String buyCurrency = row[1];
			String sell = row[2];
			String sellCurrency = row[1];
			String feeString = BigDecimal.ZERO.setScale(12).toPlainString();
			String feeCurrency = sellCurrency;
			CurrencyExchange exchange = CurrencyExchange.POLONIEX;
			String group = "";
			String dateString = row[0];
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime localDateTime = null;
			try {
				localDateTime = LocalDateTime.parse(dateString, formatter);
			} catch (DateTimeParseException e) {
				logger.warn("rowNum = {}", rowNum);
				logger.warn("dateString = {}", dateString);
				throw e;
			}
			ZonedDateTime dateTime = ZonedDateTime.of(localDateTime, ZoneId.of("UTC"));

			eventCount++;
			CryptoEvent cryptoEvent = null;

			BigDecimal buyBigDecimal = new BigDecimal(buy);
			BigDecimal sellBigDecimal = new BigDecimal(sell);
			CurrencyAmount buyAmount = buy.isBlank() ? null :
				CurrencyAmount.of(buyBigDecimal, buyCurrency);
			CurrencyAmount sellAmount = sell.isBlank() ? null :
					CurrencyAmount.of(sellBigDecimal, sellCurrency);		
		
			CurrencyFee fee;	
			if (StringUtils.isBlank(feeString)) {
				fee = CurrencyFee.of(CurrencyAmount.of(BigDecimal.ZERO, batchGroup.getBaseCurrency().getCurrencyCode()));
			} else {
				CurrencyAmount feeAmount =
					CurrencyAmount.of(new BigDecimal(feeString), feeCurrency);
				fee = CurrencyFee.of(feeAmount);
			}				
			if (true) {
				if (sellAmount != null) {
					poloniexBTCBalance.subtract(sellAmount, CurrencyExchange.POLONIEX);
				} else {
					poloniexBTCBalance.add(buyAmount, CurrencyExchange.POLONIEX);
				}
				poloniexBTCBalance.subtract(fee.getAmount(), CurrencyExchange.POLONIEX);
			}
			logger.warn("Poloniex BTC balance = {}", poloniexBTCBalance.toString());
				
			if (DEPOSIT.equals(type) || LOAN.equals(type)) {
				deposits++;
				CurrencyDeposit currencyDeposit = 
						CurrencyDeposit.of(batchGroup, buyAmount, dateTime, fee);
				//TODO, ideally should be conditional upon whether this is a transfer
				logger.warn("alternatively, this could be conditional upon whether this is a transfer");
				cryptoEvent = currencyDeposit;
			}
			if (WITHDRAWAL.equals(type) || SPEND.equals(type) || REPAYMENT.equals(type)) {
				withdrawals++;
				//convert from Poloniex CSV, which is exclusive, to standard CSV, which is inclusive of fee
				//in terms of balance affect
				CurrencyWithdrawal currencyWithdrawal = 
						CurrencyWithdrawal.of(batchGroup, sellAmount.add(fee.getAmount()), dateTime, fee);
				cryptoEvent = currencyWithdrawal;
			}
			if (!DEPOSIT.equals(type) && !WITHDRAWAL.equals(type) && !SPEND.equals(type) &&
					!TRADE.equals(type) && !ROLLOVER.equals(type) && !MARGIN.equals(type) && 
					!INCOME.equals(type) && !LOST.equals(type) && !EXPENSE.equals(type) &&
					!LOAN.equals(type) && !REPAYMENT.equals(type)) {
				throw new AssertionError("Unknown type at row: " + rowNum);
				//continue;
			}			
			
			logger.info("rowNum = " + rowNum);
			if (TRADE.equals(type)) {
					CurrencyConversion conversion = 
						CurrencyConversion.of(batchGroup,
								buyAmount, dateTime, 
								sellAmount, 
								fee);
					cryptoEvent = conversion;
				
			}
			if (cryptoEvent != null) {
					cryptoEvent.setGroup(group);
					cryptoEvent.setRowNum(rowNum);
					cryptoEvent.setCurrencyExchange(exchange);
					cryptoEvents.add(cryptoEvent);
					if (testExitEarly || testReconcilePoloniex) {
						session.process(cryptoEvent);
						CurrencyAmount poloniexBTCBalanceExpected = poloniexBTCBalance.getBalance(CurrencyExchange.POLONIEX, CurrencyCode.of(BTC));
						CurrencyAmount poloniexBTCBalanceActual = session.getLedgerBalance().getBalance(CurrencyExchange.POLONIEX, CurrencyCode.of(BTC));
						if (!poloniexBTCBalanceExpected.equals(poloniexBTCBalanceActual)) {
							logger.info("Incorrect crypto event = {}", cryptoEvent.toString());
						}
						Assert.assertEquals(poloniexBTCBalanceExpected, poloniexBTCBalanceActual);
					}
			} else {
				throw new AssertionError("Crypto Event should never be null");
			}
		}
		//todo validate cryptoEvents here, e.g. make sure each trade is complete
		List<String[]> lines = new ArrayList<String[]>();
		String[] line = new String[11];
		line[0] = "Type";
		line[1] = "Buy";
		line[2] = "Cur.";
		line[3] = "Sell";
		line[4] = "Cur.";
		line[5] = "Fee";
		line[6] = "Cur.";
		line[7] = "Exchange";
		line[8] = "Group";
		line[9] = "Comment";
		line[10] = "Date";
		lines.add(line);

		for (CryptoEvent event : cryptoEvents) {
			if (event instanceof CurrencyConversion) {
				CurrencyConversion conversion = (CurrencyConversion) event;
				if (conversion.isIncomplete()) {
					throw new AssertionError("incomplete event");
				}
				line = new String[11];
				line[0] = TRADE;
				line[1] = conversion.getTo().getBigDecimal() + "";
				line[2] = conversion.getTo().getCurrencyCode().getCurrencyCode() + "";
				CurrencyAmount from = conversion.getFrom();
				line[3] = from == null ? "" : from.getBigDecimal() + "";
				line[4] = from == null ? "" : from.getCurrencyCode().getCurrencyCode() + "";
				line[5] = conversion.getFee().getAmount().getBigDecimal() + "";
				line[6] = conversion.getFee().getAmount().getCurrencyCode().getCurrencyCode();
				line[7] = "Poloniex";
				line[8] = "";
				line[9] = "";
				line[10] = ZonedDateTimeUtils.format(conversion.getWhen());	
				lines.add(line);
			} else if (event instanceof CurrencyDeposit) {
				CurrencyDeposit deposit = (CurrencyDeposit) event;
				line = new String[11];
				line[0] = DEPOSIT;
				line[1] = deposit.getAmount().getBigDecimal() + "";
				line[2] = deposit.getAmount().getCurrencyCode().getCurrencyCode() + "";
				line[3] = "";
				line[4] = "";
				line[5] = deposit.getFee().getAmount().getBigDecimal() + "";
				line[6] = deposit.getFee().getAmount().getCurrencyCode().getCurrencyCode();
				line[7] = "Poloniex";
				line[8] = "";
				line[9] = "";
				line[10] = ZonedDateTimeUtils.format(deposit.getWhen());	
				lines.add(line);
			} else if (event instanceof CurrencyWithdrawal) {
				CurrencyWithdrawal withdrawal = (CurrencyWithdrawal) event;
				line = new String[11];
				line[0] = WITHDRAWAL;
				line[1] = "";
				line[2] = "";
				line[3] = withdrawal.getAmount().getBigDecimal() + "";
				line[4] = withdrawal.getAmount().getCurrencyCode().getCurrencyCode() + "";
				line[5] = withdrawal.getFee().getAmount().getBigDecimal() + "";
				line[6] = withdrawal.getFee().getAmount().getCurrencyCode().getCurrencyCode();
				line[7] = "Poloniex";
				line[8] = "";
				line[9] = "";
				line[10] = ZonedDateTimeUtils.format(withdrawal.getWhen());	
				lines.add(line);
			}
		}
		logger.warn("Writing csv");
		CSVWriter.writeCSV("poloniex-deposits-standard-format.csv", lines);
		CurrencyAmount poloniexBTCBalanceExpected = poloniexBTCBalance.getBalance(CurrencyExchange.POLONIEX, CurrencyCode.of(BTC));
		if (CurrencyTradingParser.testReconcilePoloniex) {
			if (testRowLimit < 0) {
				Assert.assertEquals(poloniexBTCBalanceExpected, CurrencyAmount.of("0.0000072022", BTC));
			}
		}
		if (testExitEarly ) {
			System.exit(0);
		}
	}
	
	public static void processPoloniex(List<String[]> csv, List<CryptoEvent> cryptoEvents, CurrencyBatchGroup batchGroup) throws ParseException {		
		int rowNum = 1;  //due to title row

		CurrencyEventProcessorSession session = null;
		if (testExitEarly || testReconcilePoloniex) {
			session = new CurrencyEventProcessorSession(batchGroup);
		}
		CurrencyBalance poloniexBTCBalance = CurrencyBalance.of();
		for (String[] row : csv) {
			rowNum++;
			if (rowNum == testRowLimit) {
				break;
			}
			String market = row[1];
			int pos = market.indexOf("/");
			String code1 = market.substring(0, pos);
			String code2 = market.substring(pos+1);
			boolean isSale = "Sell".equals(row[3]);
			String type = "Trade";
			String buy = isSale ? row[6] : row[5];
			String buyCurrency = isSale ? code2 : code1;
			String sell = isSale ? row[5] : row[6];
			String sellCurrency = isSale ? code1 : code2;
			String feePercent = row[7];
			BigDecimal feeDecimal = new BigDecimal(feePercent.trim().replace("%", "")).divide(BigDecimal.valueOf(100), MathContext.DECIMAL128);
			String feeString;
			String feeCurrency;
			String feeStringAssert;
			//boolean feeIsFromBuyAmount = true;			
			if (isSale) {
				feeStringAssert = new BigDecimal(buy).multiply(feeDecimal).setScale(8, RoundingMode.DOWN).toPlainString();
				//feeString = new BigDecimal(row[6]).subtract(new BigDecimal(row[9])).toPlainString();
				feeString = feeStringAssert;
				Assert.assertEquals(feeStringAssert, feeString);
				feeCurrency = buyCurrency;
			} else {
				feeStringAssert = new BigDecimal(buy).multiply(feeDecimal).setScale(8, RoundingMode.DOWN).toPlainString();
				//feeString = new BigDecimal(row[5]).subtract(new BigDecimal(row[10])).toPlainString();
				feeString = feeStringAssert;
				feeCurrency = buyCurrency;
				Assert.assertEquals(feeStringAssert, feeString);
			}
//			if (feeIsAlwaysBTC) {
//				Assert.assertEquals("BTC", feeCurrency);
//			}
			CurrencyExchange exchange = CurrencyExchange.POLONIEX;
			String group = "";
			String dateString = row[0];
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime localDateTime = null;
			try {
				localDateTime = LocalDateTime.parse(dateString, formatter);
			} catch (DateTimeParseException e) {
				logger.warn("rowNum = {}", rowNum);
				logger.warn("dateString = {}", dateString);
				throw e;
			}
			ZonedDateTime dateTime = ZonedDateTime.of(localDateTime, ZoneId.of("UTC"));

			eventCount++;
			CryptoEvent cryptoEvent = null;

			CurrencyFee fee;
			CurrencyAmount feeAmount = CurrencyAmount.of(BigDecimal.ZERO, batchGroup.getBaseCurrency().getCurrencyCode());
			if (StringUtils.isBlank(feeString)) {
				fee = CurrencyFee.of(feeAmount);
			} else {
				feeAmount = CurrencyAmount.of(new BigDecimal(feeString), feeCurrency);
				fee = CurrencyFee.of(feeAmount);
			}
			
			BigDecimal buyBigDecimal = new BigDecimal(buy).subtract(new BigDecimal(feeString));
			BigDecimal sellBigDecimal = new BigDecimal(sell);
			CurrencyAmount buyAmount = buy.isBlank() ? null :
				CurrencyAmount.of(buyBigDecimal, buyCurrency);
			CurrencyAmount sellAmount = sell.isBlank() ? null :
					CurrencyAmount.of(sellBigDecimal, sellCurrency);
//			if (!isSale) {
//				sellAmount = sellAmount; //.add(feeAmount);
//			} else {
				//buyAmount = buyAmount.subtract(feeAmount);
			//}
						
			if (true) {
				if (sellAmount != null) {
					poloniexBTCBalance.subtract(sellAmount, CurrencyExchange.POLONIEX);
				} else {
					poloniexBTCBalance.add(buyAmount, CurrencyExchange.POLONIEX);
				}
				poloniexBTCBalance.subtract(fee.getAmount(), CurrencyExchange.POLONIEX);
			}
			logger.warn("Poloniex BTC balance = {}", poloniexBTCBalance.toString());
				
			if (DEPOSIT.equals(type) || LOAN.equals(type)) {
				deposits++;
				CurrencyDeposit currencyDeposit = 
						CurrencyDeposit.of(batchGroup, buyAmount, dateTime, fee);
				//TODO, ideally should be conditional upon whether this is a transfer
				logger.warn("alternatively, this could be conditional upon whether this is a transfer");
//				if (testIncludeDeposits) {
//					//TODO: this puts the batch out of order
//					//integrate transactions back intro main spreadsheet
//					//or move this code to the CurrencyEventProcessorSession
//					batchGroup.add(CurrencyBatch.of(currencyDeposit.getAmount(), currencyDeposit.getWhen(), currencyDeposit.getAmount()));
//				} else if (testIncludeFiatDeposits  && !currencyDeposit.getAmount().isCrypto()) {
//					batchGroup.add(CurrencyBatch.of(currencyDeposit.getAmount(), currencyDeposit.getWhen(), currencyDeposit.getAmount()));					
//				}
				cryptoEvent = currencyDeposit;
			}
			if (WITHDRAWAL.equals(type) || SPEND.equals(type) || REPAYMENT.equals(type)) {
				withdrawals++;
				//convert from Poloniex CSV, which is exclusive, to standard CSV, which is inclusive of fee
				//in terms of balance affect
				CurrencyWithdrawal currencyWithdrawal = 
						CurrencyWithdrawal.of(batchGroup, sellAmount.add(fee.getAmount()), dateTime, fee);
				cryptoEvent = currencyWithdrawal;
			}
			if (!DEPOSIT.equals(type) && !WITHDRAWAL.equals(type) && !SPEND.equals(type) &&
					!TRADE.equals(type) && !ROLLOVER.equals(type) && !MARGIN.equals(type) && 
					!INCOME.equals(type) && !LOST.equals(type) && !EXPENSE.equals(type) &&
					!LOAN.equals(type) && !REPAYMENT.equals(type)) {
				throw new AssertionError("Unknown type at row: " + rowNum);
				//continue;
			}			
			
			logger.info("rowNum = " + rowNum);
			if (TRADE.equals(type)) {
					CurrencyConversion conversion = 
						CurrencyConversion.of(batchGroup,
								buyAmount, dateTime, 
								sellAmount, 
								fee);
					cryptoEvent = conversion;
				
			}
			if (cryptoEvent != null) {
					cryptoEvent.setGroup(group);
					cryptoEvent.setRowNum(rowNum);
					cryptoEvent.setCurrencyExchange(exchange);
					cryptoEvents.add(cryptoEvent);
					if (testExitEarly || testReconcilePoloniex) {
						session.process(cryptoEvent);
						CurrencyAmount poloniexBTCBalanceExpected = poloniexBTCBalance.getBalance(CurrencyExchange.POLONIEX, CurrencyCode.of(BTC));
						CurrencyAmount poloniexBTCBalanceActual = session.getLedgerBalance().getBalance(CurrencyExchange.POLONIEX, CurrencyCode.of(BTC));
						if (!poloniexBTCBalanceExpected.equals(poloniexBTCBalanceActual)) {
							logger.info("Incorrect crypto event = {}", cryptoEvent.toString());
						}
						Assert.assertEquals(poloniexBTCBalanceExpected, poloniexBTCBalanceActual);
					}
			} else {
				throw new AssertionError("Crypto Event should never be null");
			}
		}
		//todo validate cryptoEvents here, e.g. make sure each trade is complete
		List<String[]> lines = new ArrayList<String[]>();
		String[] line = new String[11];
		line[0] = "Type";
		line[1] = "Buy";
		line[2] = "Cur.";
		line[3] = "Sell";
		line[4] = "Cur.";
		line[5] = "Fee";
		line[6] = "Cur.";
		line[7] = "Exchange";
		line[8] = "Group";
		line[9] = "Comment";
		line[10] = "Date";
		lines.add(line);

		for (CryptoEvent event : cryptoEvents) {
			if (event instanceof CurrencyConversion) {
				CurrencyConversion conversion = (CurrencyConversion) event;
				if (conversion.isIncomplete()) {
					throw new AssertionError("incomplete event");
				}
				line = new String[11];
				line[0] = TRADE;
				line[1] = conversion.getTo().getBigDecimal() + "";
				line[2] = conversion.getTo().getCurrencyCode().getCurrencyCode() + "";
				CurrencyAmount from = conversion.getFrom();
				line[3] = from == null ? "" : from.getBigDecimal() + "";
				line[4] = from == null ? "" : from.getCurrencyCode().getCurrencyCode() + "";
				line[5] = conversion.getFee().getAmount().getBigDecimal() + "";
				line[6] = conversion.getFee().getAmount().getCurrencyCode().getCurrencyCode();
				line[7] = "Poloniex";
				line[8] = "";
				line[9] = "";
				line[10] = ZonedDateTimeUtils.format(conversion.getWhen());	
				lines.add(line);
			} else if (event instanceof CurrencyDeposit) {
				CurrencyDeposit deposit = (CurrencyDeposit) event;
				line = new String[11];
				line[0] = DEPOSIT;
				line[1] = deposit.getAmount().getBigDecimal() + "";
				line[2] = deposit.getAmount().getCurrencyCode().getCurrencyCode() + "";
				line[3] = "";
				line[4] = "";
				line[5] = deposit.getFee().getAmount().getBigDecimal() + "";
				line[6] = deposit.getFee().getAmount().getCurrencyCode().getCurrencyCode();
				line[7] = "Poloniex";
				line[8] = "";
				line[9] = "";
				line[10] = ZonedDateTimeUtils.format(deposit.getWhen());	
				lines.add(line);
			} else if (event instanceof CurrencyWithdrawal) {
				CurrencyWithdrawal withdrawal = (CurrencyWithdrawal) event;
				line = new String[11];
				line[0] = WITHDRAWAL;
				line[1] = "";
				line[2] = "";
				line[3] = withdrawal.getAmount().getBigDecimal() + "";
				line[4] = withdrawal.getAmount().getCurrencyCode().getCurrencyCode() + "";
				line[5] = withdrawal.getFee().getAmount().getBigDecimal() + "";
				line[6] = withdrawal.getFee().getAmount().getCurrencyCode().getCurrencyCode();
				line[7] = "Poloniex";
				line[8] = "";
				line[9] = "";
				line[10] = ZonedDateTimeUtils.format(withdrawal.getWhen());	
				lines.add(line);
			}
		}
		logger.warn("Writing csv");
		CSVWriter.writeCSV("poloniex-all-standard-format.csv", lines);
		CurrencyAmount poloniexBTCBalanceExpected = poloniexBTCBalance.getBalance(CurrencyExchange.POLONIEX, CurrencyCode.of(BTC));
		if (CurrencyTradingParser.testReconcilePoloniex) {
			if (testRowLimit < 0) {
				Assert.assertEquals(poloniexBTCBalanceExpected, CurrencyAmount.of("0.0000072022", BTC));
			}
		}
		if (testExitEarly ) {
			System.exit(0);
		}
	}
	
	public static void processBitfinex(List<String[]> csv, List<CryptoEvent> cryptoEvents, CurrencyBatchGroup batchGroup) throws ParseException {		
		int rowNum = 1;  //due to title row

		CurrencyEventProcessorSession session = null;
		if (testExitEarly || testReconcileBitfinex) {
			session = new CurrencyEventProcessorSession(batchGroup);
		}
		CurrencyBalance bitfinexBTCBalance = CurrencyBalance.of();
		
		//Round down to match behaviour
		//Set of switches required for balance reconciliation:
		boolean shouldSetScaleForBTC = true;
		int scale = 8;
		RoundingMode roundingModeForScale = RoundingMode.HALF_UP;
		//Bitfinex Trade Export has a defect with the fee calculation/inclusion in amounts
		boolean shouldSubtractFeeTwice = true;
		
		for (String[] row : csv) {
			rowNum++;
			if (rowNum == testRowLimit) {
				break;
			}
			String market = row[1];
			int pos = market.indexOf("/");
			String code1 = market.substring(0, pos);
			String code2 = market.substring(pos+1);
			BigDecimal csvAmount = new BigDecimal(row[2]);
			BigDecimal price = new BigDecimal(row[3]);
			boolean isSale = csvAmount.signum() < 0;
			String type = "Trade";
			BigDecimal buy = isSale ? csvAmount.multiply(price).negate() : csvAmount;
			String buyCurrency = isSale ? code2 : code1;
			BigDecimal sell = isSale ? csvAmount.negate() : csvAmount.multiply(price);
			String sellCurrency = isSale ? code1 : code2;

			String feeString = row[4];
			String feeCurrency = row[5];
			
			CurrencyExchange exchange = CurrencyExchange.BITFINEX;
			String group = "";
			String dateString = row[6];
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss");
			LocalDateTime localDateTime = null;
			try {
				localDateTime = LocalDateTime.parse(dateString, formatter);
			} catch (DateTimeParseException e) {
				logger.warn("rowNum = {}", rowNum);
				logger.warn("dateString = {}", dateString);
				throw e;
			}
			ZonedDateTime dateTime = ZonedDateTime.of(localDateTime, ZoneId.of("UTC"));
			eventCount++;
			CryptoEvent cryptoEvent = null;

			CurrencyFee fee;
			CurrencyAmount feeAmount = CurrencyAmount.of(BigDecimal.ZERO, batchGroup.getBaseCurrency().getCurrencyCode());
			if (StringUtils.isBlank(feeString)) {
				fee = CurrencyFee.of(feeAmount);
			} else {
				feeAmount = CurrencyAmount.of(new BigDecimal(feeString).negate(), feeCurrency);
				fee = CurrencyFee.of(feeAmount);
			}
			BigDecimal buyBigDecimal = buy.subtract(new BigDecimal(feeString));
			if (shouldSetScaleForBTC && "BTC".contentEquals(buyCurrency)) {
				buyBigDecimal = buyBigDecimal.setScale(scale, roundingModeForScale);
			}
			BigDecimal sellBigDecimal = sell;
			if (shouldSetScaleForBTC && "BTC".contentEquals(sellCurrency)) {
				sellBigDecimal = sellBigDecimal.setScale(scale, roundingModeForScale);
			}
			CurrencyAmount buyAmount = 
				CurrencyAmount.of(buyBigDecimal, buyCurrency);
			CurrencyAmount sellAmount = 
					CurrencyAmount.of(sellBigDecimal, sellCurrency);
			boolean subtractFeeFromBuy = true;
			if (subtractFeeFromBuy && BTC.equals(buyAmount.getCurrencyCode().getCurrencyCode())) {
				buyAmount = buyAmount.subtract(feeAmount);
				if (shouldSubtractFeeTwice) {
					buyAmount = buyAmount.subtract(feeAmount);
				}
			}
			if (subtractFeeFromBuy && ETH.equals(buyAmount.getCurrencyCode().getCurrencyCode())) {
				buyAmount = buyAmount.subtract(feeAmount);
			}
			if (true) {
				if (sellAmount != null) {
					bitfinexBTCBalance.subtract(sellAmount, CurrencyExchange.BITFINEX);
				} else {
					bitfinexBTCBalance.add(buyAmount, CurrencyExchange.BITFINEX);
				}
				bitfinexBTCBalance.subtract(fee.getAmount(), CurrencyExchange.BITFINEX);
			}
			logger.warn("Bitfinex BTC balance = {}", bitfinexBTCBalance.toString());
				
			if (DEPOSIT.equals(type) || LOAN.equals(type)) {
				deposits++;
				CurrencyDeposit currencyDeposit = 
						CurrencyDeposit.of(batchGroup, buyAmount, dateTime, fee);
				//TODO, ideally should be conditional upon whether this is a transfer
				logger.warn("alternatively, this could be conditional upon whether this is a transfer");
				cryptoEvent = currencyDeposit;
			}
			if (WITHDRAWAL.equals(type) || SPEND.equals(type) || REPAYMENT.equals(type)) {
				withdrawals++;
				//convert from Bitfinex CSV, which is exclusive, to standard CSV, which is inclusive of fee
				//in terms of balance affect
				CurrencyWithdrawal currencyWithdrawal = 
						CurrencyWithdrawal.of(batchGroup, sellAmount.add(fee.getAmount()), dateTime, fee);
				cryptoEvent = currencyWithdrawal;
			}
			if (!DEPOSIT.equals(type) && !WITHDRAWAL.equals(type) && !SPEND.equals(type) &&
					!TRADE.equals(type) && !ROLLOVER.equals(type) && !MARGIN.equals(type) && 
					!INCOME.equals(type) && !LOST.equals(type) && !EXPENSE.equals(type) &&
					!LOAN.equals(type) && !REPAYMENT.equals(type)) {
				throw new AssertionError("Unknown type at row: " + rowNum);
				//continue;
			}			
			
			logger.info("rowNum = " + rowNum);
			if (TRADE.equals(type)) {
					CurrencyConversion conversion = 
						CurrencyConversion.of(batchGroup,
								buyAmount, dateTime, 
								sellAmount, 
								fee);
					cryptoEvent = conversion;
				
			}
			if (cryptoEvent != null) {
					cryptoEvent.setGroup(group);
					cryptoEvent.setRowNum(rowNum);
					cryptoEvent.setCurrencyExchange(exchange);
					cryptoEvents.add(cryptoEvent);
					if (testExitEarly || testReconcileBitfinex) {
						session.process(cryptoEvent);
						CurrencyAmount bitfinexBTCBalanceExpected = bitfinexBTCBalance.getBalance(CurrencyExchange.BITFINEX, CurrencyCode.of(BTC));
						CurrencyAmount bitfinexBTCBalanceActual = session.getLedgerBalance().getBalance(CurrencyExchange.BITFINEX, CurrencyCode.of(BTC));
						if (!bitfinexBTCBalanceExpected.equals(bitfinexBTCBalanceActual)) {
							logger.info("Incorrect crypto event = {}", cryptoEvent.toString());
						}
						Assert.assertEquals(bitfinexBTCBalanceExpected, bitfinexBTCBalanceActual);
					}
			} else {
				throw new AssertionError("Crypto Event should never be null");
			}
		}
		//todo validate cryptoEvents here, e.g. make sure each trade is complete
		List<String[]> lines = new ArrayList<String[]>();
		String[] line = new String[11];
		line[0] = "Type";
		line[1] = "Buy";
		line[2] = "Cur.";
		line[3] = "Sell";
		line[4] = "Cur.";
		line[5] = "Fee";
		line[6] = "Cur.";
		line[7] = "Exchange";
		line[8] = "Group";
		line[9] = "Comment";
		line[10] = "Date";
		lines.add(line);

		for (CryptoEvent event : cryptoEvents) {
			if (event instanceof CurrencyConversion) {
				CurrencyConversion conversion = (CurrencyConversion) event;
				if (conversion.isIncomplete()) {
					throw new AssertionError("incomplete event");
				}
				line = new String[11];
				line[0] = TRADE;
				line[1] = conversion.getTo().getBigDecimal() + "";
				line[2] = conversion.getTo().getCurrencyCode().getCurrencyCode() + "";
				CurrencyAmount from = conversion.getFrom();
				line[3] = from == null ? "" : from.getBigDecimal() + "";
				line[4] = from == null ? "" : from.getCurrencyCode().getCurrencyCode() + "";
				line[5] = conversion.getFee().getAmount().getBigDecimal() + "";
				line[6] = conversion.getFee().getAmount().getCurrencyCode().getCurrencyCode();
				line[7] = "Bitfinex";
				line[8] = "";
				line[9] = "";
				line[10] = ZonedDateTimeUtils.format(conversion.getWhen());	
				lines.add(line);
			} else if (event instanceof CurrencyDeposit) {
				CurrencyDeposit deposit = (CurrencyDeposit) event;
				line = new String[11];
				line[0] = DEPOSIT;
				line[1] = deposit.getAmount().getBigDecimal() + "";
				line[2] = deposit.getAmount().getCurrencyCode().getCurrencyCode() + "";
				line[3] = "";
				line[4] = "";
				line[5] = deposit.getFee().getAmount().getBigDecimal() + "";
				line[6] = deposit.getFee().getAmount().getCurrencyCode().getCurrencyCode();
				line[7] = "Bitfinex";
				line[8] = "";
				line[9] = "";
				line[10] = ZonedDateTimeUtils.format(deposit.getWhen());	
				lines.add(line);
			} else if (event instanceof CurrencyWithdrawal) {
				CurrencyWithdrawal withdrawal = (CurrencyWithdrawal) event;
				line = new String[11];
				line[0] = WITHDRAWAL;
				line[1] = "";
				line[2] = "";
				line[3] = withdrawal.getAmount().getBigDecimal() + "";
				line[4] = withdrawal.getAmount().getCurrencyCode().getCurrencyCode() + "";
				line[5] = withdrawal.getFee().getAmount().getBigDecimal() + "";
				line[6] = withdrawal.getFee().getAmount().getCurrencyCode().getCurrencyCode();
				line[7] = "Bitfinex";
				line[8] = "";
				line[9] = "";
				line[10] = ZonedDateTimeUtils.format(withdrawal.getWhen());	
				lines.add(line);
			}
		}
		logger.warn("Writing csv");
		CSVWriter.writeCSV("bitfinex-all-standard-format.csv", lines);
		CurrencyAmount bitfinexBTCBalanceExpected = bitfinexBTCBalance.getBalance(CurrencyExchange.BITFINEX, CurrencyCode.of(BTC));
		if (CurrencyTradingParser.testReconcileBitfinex) {
			if (testRowLimit < 0) {
				Assert.assertEquals(bitfinexBTCBalanceExpected, CurrencyAmount.of("0.0000072022", BTC));
			}
		}
		if (testExitEarly ) {
			System.exit(0);
		}
	}
}
