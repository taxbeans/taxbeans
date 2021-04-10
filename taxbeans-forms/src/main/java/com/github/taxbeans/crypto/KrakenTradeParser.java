package com.github.taxbeans.crypto;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.UnknownCurrencyException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.taxbeans.crypto.model.CoinMarketCapBtcEod;
import com.github.taxbeans.crypto.model.CoinMarketCapEthEod;
import com.github.taxbeans.crypto.model.InstabitManualEntries;
import com.github.taxbeans.crypto.model.KrakenLedger;
import com.github.taxbeans.crypto.model.KrakenTrade;
import com.github.taxbeans.csv.CSVParser;
import com.github.taxbeans.currency.RBNZHistoricalExchangeRatesReader;
import com.github.taxbeans.forms.utils.LocalDateTimeUtils;
import com.github.taxbeans.model.Account;
import com.github.taxbeans.model.AccountEntry;
import com.github.taxbeans.model.AccountSide;
import com.github.taxbeans.model.Journal;
import com.github.taxbeans.model.Ledger;
import com.github.taxbeans.model.Transaction;
import com.github.taxbeans.model.commodity.Commodity;
import com.github.taxbeans.model.commodity.CommodityAmount;
import com.github.taxbeans.model.commodity.CommodityExchangeRate;
import com.github.taxbeans.model.commodity.CommodityPair;

public class KrakenTradeParser {

	private static final String REIMBURSED_EXCESS_FOR_ORDER = "Reimbursed excess for order #";
	final static Logger logger = LoggerFactory.getLogger(KrakenTradeParser.class);

	// Kraken exchange timezone in exported CSV is GMT - double check by making a new transaction and checking new CSV export
	private static ZoneId zone = ZoneId.of("GMT");

	private static Map<String, AccountEntry> entriesByTradeRef = new HashMap<>();

	private static Map<Account, MarginPosition> marginPositions = new HashMap<>();

	public static void main(String[] args) throws ParseException {
		Map<String, KrakenTrade> trades = KrakenTrade.loadFromCSV();
		Map<LocalDate, CoinMarketCapBtcEod> eodData = CoinMarketCapBtcEod.loadFromCSV();
		List<String[]> parsedFile = CSVParser.newInstance().parseFile(
				"target/classes/tradehistory/kraken-ledgers.csv", true);

		Journal krakenJournal = new Journal();
		Ledger krakenLedger = new Ledger();

		krakenLedger.addJournal(krakenJournal);

		//Add manual entries from Instabit to establish historical cost
		new InstabitManualEntries().getTransactions(krakenLedger, krakenJournal);


		Account krakenLoans = Account.account().withName("Kraken Loans").withParent(krakenLedger.getLiabilitiesAccount()).build();

		Account assets = krakenLedger.getAssetsAccount();
		Map<String, Account> commodityHoldings = new HashMap<>();
		//Account btcHoldings = Account.account().withGuid(UUID.randomUUID().toString()).withName("BTC Holdings").withParent(assets).build();

		// Map of bitcoin addresses to accounts
		Map<String, Account> addressedBTCHoldings = new HashMap<>();  //Account.account().withGuid(UUID.randomUUID().toString()).withName("kraken XRP Holdings").withParent(btcHoldings).build();,,

		Account krakenUSDHoldings = Account.account().withGuid(UUID.randomUUID().toString()).withName("Kraken USD Holdings").withParent(assets).build();
		//Account krakenXRPHoldings = Account.account().withGuid(UUID.randomUUID().toString()).withName("Kraken XRP Holdings").withParent(btcHoldings).build();
		Account krakenBTCHoldings = Account.account().withGuid(UUID.randomUUID().toString()).withName("Kraken BTC Holdings").withParent(assets).build();
		//Account krakenBCHHoldings = Account.account().withGuid(UUID.randomUUID().toString()).withName("Kraken BCH Holdings").withParent(assets).build();
		Account krakenEURHoldings = Account.account().withGuid(UUID.randomUUID().toString()).withName("Kraken EUR Holdings").withParent(assets).build();
		Account krakenETHHoldings = Account.account().withGuid(UUID.randomUUID().toString()).withName("Kraken ETH Holdings").withParent(assets).build();
		//BTG was not held:
		//Account krakenBTGHoldings = Account.account().withGuid(UUID.randomUUID().toString()).withName("Kraken BTG Holdings").withParent(assets).build();
		Account fundsIntroduced = Account.account().withGuid(UUID.randomUUID().toString()).withName("Funds Introduced - Martin")
				.withParent(krakenLedger.getEquityAccount()).build();
		Account drawings = Account.account().withGuid(UUID.randomUUID().toString()).withName("Drawings - Martin")
				.withParent(krakenLedger.getEquityAccount()).build();

		// Use one account for all buy orders, instead of one account for each buy order
		//		Account buyOrderAccount = Account.account().withGuid(UUID.randomUUID().toString()).withName("Kraken Buy Orders")
		//				.withParent(assets).build();
		//		Account sellOrderAccount = Account.account().withGuid(UUID.randomUUID().toString()).withName("Kraken Sell Orders")
		//				.withParent(assets).build();

		commodityHoldings.put("USD", krakenUSDHoldings);
		//commodityHoldings.put("XRP", krakenXRPHoldings);
		commodityHoldings.put("BTC", krakenBTCHoldings);
		commodityHoldings.put("XBT", krakenBTCHoldings);
		//commodityHoldings.put("BCH", krakenBCHHoldings);
		commodityHoldings.put("EUR", krakenEURHoldings);
		commodityHoldings.put("ETH", krakenETHHoldings);
		//commodityHoldings.put("BTG", krakenBTGHoldings);

		// Financial transactions must be in forward date order to efficiently process
		// Consider historical cost, etc.
		List<DatedRow> rows = new ArrayList<>();
		for (String[] columns : parsedFile) {
			SimpleDateFormat format = new SimpleDateFormat("y"
					+ "yyy-MM-dd HH:mm:ss");
			Date parse = format.parse(columns[2]);
			LocalDateTime localDate = LocalDateTimeUtils.convert(parse);
			rows.add(new DatedRow(localDate, columns));
		}
		DatedRow[] sortedRows = rows.toArray(new DatedRow[rows.size()]);
		Arrays.sort(sortedRows);

		List<BuyOrder> buyOrders = new ArrayList<>();
		List<SellOrder> sellOrders = new ArrayList<>();
		Map<String, BuyOrder> referenceToBuyOrder = new HashMap<>();

		int missingTradeCount = 0;

		Map<String, KrakenLedger> ledgers = KrakenLedger.loadFromCSV();  //migration to object model
		for (DatedRow row : sortedRows) {
			String[] columns = row.getColumns();
			KrakenLedger ledger = ledgers.get(columns[0]);
			logger.info("line = " + Arrays.asList(columns));
			Transaction transaction = new Transaction();
			SimpleDateFormat format = new SimpleDateFormat("y"
					+ "yyy-MM-dd HH:mm:ss");
			Date parse = format.parse(columns[2]);
			//LocalDate localDate = LocalDateUtils.convert(parse);
			//long timestamp = parse.getTime();
			transaction.setDate(ZonedDateTime.ofInstant(parse.toInstant(), zone));
			BigDecimal amount = new BigDecimal(columns[6]);
			String cryptoTransfer = columns[4].trim();
			String description = columns[3].trim();
			//boolean isDigitalCurrency = columns[5].trim().startsWith("X");
			String currencyCode = columns[5].trim().substring(1);
			String foreignCurrencyCode = currencyCode;
			List<AccountEntry> transactionSplits = new ArrayList<>();
			AccountEntry split = new AccountEntry();
			split.setAmount(amount);

			if (description.startsWith("deposit") && currencyCode.startsWith("X")) {
				// Digital currency withdrawal from kraken:
				BigDecimal commodityAmount = new BigDecimal(columns[6]);
				AccountEntry entry1 = new AccountEntry();
				Account cryptoAccount = commodityHoldings.get(currencyCode);
				entry1.setAccount(cryptoAccount);
				entry1.setDescription("Debit or credit Kraken "+foreignCurrencyCode+" Holdings");
				entry1.setCommodityName(foreignCurrencyCode);
				entry1.setCommodityUnits(commodityAmount.abs());
				entry1.setTransaction(transaction);

				//Obtain bread wallet, which may be used to find the historical cost:
				Account digitalWallet;
				String address = "UnknownAddress";
				if (true) {  //"LUXX4T-CTBG2-3GHSLD".equals(columns[0]) || "LEJNV3-NUNRX-SHB3G2".equals(columns[0])) {
					digitalWallet = krakenLedger.getAccountByName("Bread Wallet");
				} else {
					digitalWallet = Account.account().withGuid(UUID.randomUUID().toString()).build();


					logger.info("Crypto address: " + address);
					digitalWallet.setName("Crypto Address: " + address);
					digitalWallet.setParent(cryptoAccount.getParent());
				}

				//Historical cost for this BTC purchased is based on Bread Wallet historical cost:
				BigDecimal historicalCost = digitalWallet.getCostPerUnitAsAt(transaction.getDate());
				entry1.setAmount(commodityAmount.abs().multiply(historicalCost));
				entry1.setAccountSide(AccountSide.DEBIT);
				AccountEntry entry2 = new AccountEntry();

				//account established manually

				entry2.setAccount(digitalWallet);
				List<AccountEntry> splits = new ArrayList<>();
				entry2.setDescription("Debit " + digitalWallet.getName());
				entry2.setCommodityName(foreignCurrencyCode);
				entry2.setAccountSide(AccountSide.CREDIT);
				entry2.setCommodityUnits(commodityAmount.abs());
				entry2.setAmount(entry1.getAmount());
				entry2.setTransaction(transaction);
				splits.add(entry1);
				splits.add(entry2);
				addressedBTCHoldings.put(address, digitalWallet);
				transaction.setTransactionSplits(splits);
				transaction.setDescription(currencyCode + " Digital Currency Kraken Deposit");
			} else if (description.startsWith("trade")) {

				BigDecimal volume = new BigDecimal(columns[6].trim());
				String commodityCode = columns[5].trim().substring(1);

				String tradeRef = columns[1];
				BigDecimal tradeFee = new BigDecimal(columns[7].trim());

				KrakenTrade trade = trades.get(tradeRef);

				if (entriesByTradeRef.containsKey(tradeRef)) {
					logger.info("Found 2nd half of trade for: " + tradeRef);
					AccountEntry firstSide = entriesByTradeRef.get(tradeRef);

					AccountEntry entry = AccountEntry.accountEntry()
							.withAccount(commodityHoldings.get(commodityCode))
							.withCurrency("USD".equals(commodityCode) ? Monetary.getCurrency(commodityCode) : null)
							.withAmount(volume)
							.withCommodityUnits(volume)
							.withCommodityName(commodityCode)
							.withDescription("Debit or credit Kraken " + currencyCode + " Holdings").build();
					entry.setTradeFee(tradeFee);
					entriesByTradeRef.remove(tradeRef);

					AccountEntry isoCurrencyEntry = null;
					AccountEntry digitalCurrencyEntry = null;

					if ("USD".equals(commodityCode) || "EUR".equals(commodityCode)) {
						isoCurrencyEntry = entry;
						digitalCurrencyEntry = firstSide;
					} else if ("USD".equals(firstSide.getCommodityName()) || "EUR".equals(firstSide.getCommodityName())) {
						isoCurrencyEntry = firstSide;
						digitalCurrencyEntry = entry;
					}

					transaction.withEntry(firstSide).withEntry(entry);
					transaction.setDescription("Trade");

					if (isoCurrencyEntry != null && isoCurrencyEntry.getAccountSide() == AccountSide.DEBIT) {
						//we are selling a commodity to iso currency, e.g. selling XBT for USD
						//so amount is based on historical cost in the XBT commodity account in this example
						logger.info("Selling a commodity e.g. BTC to ISO currency, e.g. USD, so need to work out historical cost");
						logger.info("Then book profit/loss to Trading losses/gains");
						BigDecimal historicalCostPerUnit = digitalCurrencyEntry.getAccount().getCostPerUnitBefore(transaction.getDate());
						logger.info("Historical cost per unit = " + historicalCostPerUnit);
						digitalCurrencyEntry.setAmount(digitalCurrencyEntry.getCommodityUnits().abs().multiply(historicalCostPerUnit));
						digitalCurrencyEntry.setCurrency(krakenLedger.getBaseCurrency());
						transaction.setDescription("Sold " + digitalCurrencyEntry.getCommodityName() + " for USD");
						krakenJournal.translate(transaction);

						//account for fee first
						BigDecimal fee = isoCurrencyEntry.getTradeFee();
						AccountEntry feeEntry = AccountEntry.accountEntry().withAccount(krakenLedger.getTradeFeeAccount())
								.withDescription("Digital currency trading fees")
								.withCurrency(isoCurrencyEntry.getCurrency())
								.withAmount(fee).build();
						transaction.add(feeEntry);
						krakenJournal.translate(transaction);

						if (isoCurrencyEntry.getAmount().compareTo(digitalCurrencyEntry.getAmount()) > 0) {
							//trading gain (taxable)
							Account gain = krakenLedger.getTradingGainAccount();
							//the fee is already accounted for in the ledger amounts, so reintroduce as a separate line item by adding
							BigDecimal gainAmount = isoCurrencyEntry.getAmount().subtract(digitalCurrencyEntry.getAmount()).add(fee);
							AccountEntry gainEntry = AccountEntry.accountEntry().withAccount(gain).withDescription("Digital currency trading gain")
									.withCurrency(krakenLedger.getBaseCurrency())
									.withAmount(gainAmount).build();
							transaction.add(gainEntry);
						} else {
							//trading loss (taxable)
							BigDecimal lossAmount = digitalCurrencyEntry.getAmount().subtract(isoCurrencyEntry.getAmount()).subtract(fee);
							Account loss = krakenLedger.getTradingLossAccount();
							AccountEntry lossEntry = AccountEntry.accountEntry().withAccount(loss).withDescription("Digital currency trading loss")
									.withCurrency(krakenLedger.getBaseCurrency())
									.withAccountSide(AccountSide.DEBIT)
									.withAmount(lossAmount).build();
							transaction.add(lossEntry);
						}
						krakenJournal.audit(transaction);
					} else if (isoCurrencyEntry != null) {
						// Assume we are buying digital currency with an ISO currency

						//we are buying a commodity
						//so historical cost will be updated automatically
						logger.info("Buying a commodity so historical cost will be updated automatically and purchase price includes the fee");
						//amount is just amount of ISO currency for other side of trade
						digitalCurrencyEntry.setAmount(isoCurrencyEntry.getAmount());
						digitalCurrencyEntry.setCurrency(isoCurrencyEntry.getCurrency());
						transaction.setDescription("Bought " + digitalCurrencyEntry.getCommodityName() + " with USD");
					} else {
						//Buying a digital currency with a digital currency, so use historical cost
						//no profit or loss realized in this case
						BigDecimal historicalCostPerUnit = entry.getAccount().getCostPerUnitBefore(transaction.getDate());
						entry.setAmount(historicalCostPerUnit.multiply(trade.getCost()).abs());
						entry.setCurrency(krakenLedger.getBaseCurrency());
						firstSide.setAmount(entry.getAmount());
						firstSide.setCurrency(entry.getCurrency());
						transaction.setDescription("Bought " + firstSide.getCommodityName() + " with " + 
								entry.getCommodityName());

					}
					krakenJournal.translate(transaction);  //auto translates to base currency
					krakenJournal.addTransaction(transaction);
				} else {
					AccountEntry entry = AccountEntry.accountEntry()
							.withAccount(commodityHoldings.get(commodityCode))
							.withCurrency("USD".equals(commodityCode) ? Monetary.getCurrency(commodityCode) : null)
							.withAmount(volume)
							.withCommodityUnits(volume)
							.withCommodityName(commodityCode)
							.withDescription("Debit or credit Kraken " + currencyCode + " Holdings").build();
					entry.setTradeFee(tradeFee);
					entriesByTradeRef.put(tradeRef, entry);
					logger.info("Waiting for 2nd half of trade for " + tradeRef + "...");
				}

			} else if (description.startsWith("margin")) {
				String tradeTxId = ledger.getTradeTxId();
				KrakenTrade trade = trades.get(tradeTxId);

				if (trade == null) {
					missingTradeCount++;
					continue;
				}
				if (trade.isClosing()) {
					//Closing the margin position:

					//the fee is already accounted for in the ledger amounts, so reintroduce as a separate line item by adding
					BigDecimal gainAmount = ledger.getAmount();
					Account gainOrLoss;
					if (gainAmount.signum() < 0) {
						gainOrLoss = krakenLedger.getMarginTradingLossAccount();
					} else {
						//trading gain (taxable)
						gainOrLoss = krakenLedger.getMarginTradingGainAccount();
					}
					CurrencyUnit isoCurrency = null;
					try {
						isoCurrency = Monetary.getCurrency(ledger.getCommodityCode());
					} catch (UnknownCurrencyException e) {
						//isoCurrency is null
					}
					MonetaryAmount btcPrice = null;
					MonetaryAmount marginGainLoss = null;
					Commodity left = Commodity.commodity()
							.withSymbol(trade.getLeftCommodity()).build();
					Commodity right = Commodity.commodity()
							.withSymbol(trade.getRightCommodity()).build();	

					btcPrice = CoinMarketCapBtcEod.getClose(transaction.getDate());
					marginGainLoss = btcPrice.multiply(ledger.getAmount());

					CommodityAmount ledgerCommodityAmount = CommodityAmount.commodityAmount()
							.withCommodity(Commodity.commodity().withSymbol(ledger.getCommodityCode()).build())
							.withAmount(ledger.getAmount()).build();

					if (left.isDigitalCurrency() && right.isDigitalCurrency()) {
						logger.info("Digital currency: Found closing {} margin trade", trade.getFormattedPair());
						AccountEntry gainEntry = AccountEntry.accountEntry().withAccount(gainOrLoss).withDescription("Margin trading gain")
								.withCurrencyAmount(marginGainLoss)
								.withCommodityAmount(ledgerCommodityAmount)
								.build();
						transaction.add(gainEntry);
					} else {
						logger.info("Found closing {} margin trade", trade.getFormattedPair());
						AccountEntry gainEntry = AccountEntry.accountEntry().withAccount(gainOrLoss).withDescription("Margin trading gain")
								.withCurrency(isoCurrency != null ? isoCurrency : Monetary.getCurrency(trade.getRightCommodity()))
								.withAmount(isoCurrency != null ? gainAmount : gainAmount.multiply(trade.getPrice()))
								.build();
						transaction.add(gainEntry);
					}

					//Position reduction:
					boolean closingSell = "sell".equals(trade.getType());
					Account position = krakenLedger.createMarginPosition("Position: " + (closingSell ? "buy" : "sell") + " : " 
							+ trade.getFormattedPair());

					AccountEntry entry2;
					MonetaryAmount marginAmount = null;
					Commodity btcCommodity = Commodity.commodity().withSymbol("BTC").build();
					if (left.isDigitalCurrency() && right.isDigitalCurrency() && closingSell) {
						marginAmount = btcPrice.multiply(trade.getCost());
						Commodity commodity = Commodity.commodity().withSymbol(trade.getLeftCommodity()).build();
						CommodityAmount commodityAmount = CommodityAmount.commodityAmount().withCommodity(commodity)
								.withAmount(trade.getVolume()).build();
						CommodityPair pair = CommodityPair.commodityPair().withLeft(commodity).withRight(right).build();
						CommodityExchangeRate rate = CommodityExchangeRate.commodityExchangeRate().withCommodityPair(pair).withRate(trade.getPrice())
								.withDateTime(transaction.getDate()).build();
						entry2 = AccountEntry.accountEntry()
								.withAccount(position)
								.withCurrencyAmount(marginAmount)
								.withAccountSide(AccountSide.CREDIT)
								.withCommodityAmount(commodityAmount)
								.withDescription("Effective asset held:  " + commodity)
								.build();				
						logger.info("Position BTC amount = {}", commodityAmount.convertTo(btcCommodity, rate).getAmount());
						logger.info("Rollover cost = {}", KrakenTradeParser.marginPositions.get(position).getRolloverCost());
					} else {
						entry2 = AccountEntry.accountEntry()
								.withAccount(position)
								.withCurrency(krakenLedger.getBaseCurrency())  //historical cost is always in base currency units
								.withAmount(position.getCostPerUnitAsAt(transaction.getDate()).multiply(trade.getVolume()).abs())
								.withAccountSide(AccountSide.CREDIT)
								.withCommodityUnits(closingSell ? trade.getVolume() : trade.getCost())
								.withCommodityName(closingSell ? trade.getLeftCommodity() : trade.getRightCommodity())
								.withDescription("Effective asset held:  " + 
										(closingSell ? trade.getLeftCommodity() : trade.getRightCommodity()))
								.build();
					}
					//Margin loan repayment:
					boolean sell = "sell".equals(trade.getType());

					BigDecimal multiplicand = BigDecimal.ONE;
					String symbol = trade.getRightCommodity();
					MonetaryAmount usdAmount = null;
					AccountEntry entry3 = null;
					if (left.isDigitalCurrency() && right.isDigitalCurrency()) {
						if (closingSell) {	
							MarginPosition marginPosition = marginPositions.get(position);
							KrakenTrade totalPosition = marginPosition.getTotalPosition();

							BigDecimal fractionOfPosition = trade.getMargin()
									.divide(totalPosition.getMargin(), MathContext.DECIMAL128);
							BigDecimal btcBorrowed = totalPosition.getCost().multiply(fractionOfPosition);
							MonetaryAmount currencyEquivalentBorrowed = btcPrice.multiply(btcBorrowed);

							CommodityAmount commodityAmount = CommodityAmount.commodityAmount()
									.withCommodity(Commodity.commodity().withSymbol("BTC").build())
									.withAmount(btcBorrowed).build();

							//derive the loan amount from margin gain/loss and position
							//cannot derive from original loan amount because we don't have the %repaid variable
							//unless it's the margin, perhaps add opening margin/margin price to the Position object
							entry3 = AccountEntry.accountEntry()
									.withAccount(krakenLoans)
									.withCurrencyAmount(currencyEquivalentBorrowed)
									.withAccountSide(AccountSide.DEBIT)
									.withCommodityAmount(commodityAmount)
									.withDescription("Repay loan from Kraken in BTC").build();
							marginPosition.addClosingTrade(trade);
						} else {
							ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(transaction.getDate().toInstant(), ZoneId.of("UTC"));
							multiplicand = eodData.get(zonedDateTime.toLocalDate()).getClose();
							symbol = "USD";
							BigDecimal calculatedAmount = trade.getVolume().multiply(trade.getPrice()).abs().multiply(multiplicand);
							usdAmount = Monetary.getDefaultAmountFactory().setNumber(calculatedAmount).setCurrency("USD").create();
						}						
					} 
					//amount is historical cost of liability

					if (entry3 == null) {
						if (usdAmount == null) {
							entry3 = AccountEntry.accountEntry()
									.withAccount(krakenLoans)
									.withCurrency(isoCurrency != null ? isoCurrency : Monetary.getCurrency(symbol))
									.withAmount(trade.getVolume().multiply(trade.getPrice()).abs().multiply(multiplicand))
									.withAccountSide(AccountSide.DEBIT)
									.withCommodityUnits(closingSell ? trade.getCost() : trade.getVolume())
									.withCommodityName(closingSell ? trade.getRightCommodity() : trade.getLeftCommodity())
									.withDescription("Credit loan from Kraken in  " + (sell ? trade.getRightCommodity() : trade.getLeftCommodity())).build();
						} else {
							entry3 = AccountEntry.accountEntry()
									.withAccount(krakenLoans)
									.withCurrencyAmount(usdAmount)
									.withAccountSide(AccountSide.DEBIT)
									.withCommodityUnits(closingSell ? trade.getCost() : trade.getVolume())
									.withCommodityName(closingSell ? trade.getRightCommodity() : trade.getLeftCommodity())
									.withDescription("Credit loan from Kraken in  " + (sell ? trade.getRightCommodity() : trade.getLeftCommodity())).build();					
						}
					}

					//Difference between calculated gain/loss in NZD and amount provided by Kraken is attributable to forex gain/loss and fees
					//so calculate forex manually from forex price at start and end
					//					AccountEntry entry4 = AccountEntry.accountEntry()
					//							.withAccount(krakenLoans)
					//							.withCurrency(isoCurrency != null ? isoCurrency : Monetary.getCurrency(symbol))
					//							.withAmount(trade.getVolume().multiply(trade.getPrice()).multiply(multiplicand))
					//							.withAccountSide(AccountSide.DEBIT)
					//							.withCommodityUnits(closingSell ? trade.getCost() : trade.getVolume())
					//							.withCommodityName(closingSell ? trade.getRightCommodity() : trade.getLeftCommodity())
					//							.withDescription("Credit loan from Kraken in  " + (sell ? trade.getRightCommodity() : trade.getLeftCommodity())).build();

					transaction.add(entry2).add(entry3);
					krakenJournal.addTransaction(transaction);
					logger.info("transaction = " + transaction);
					continue;
				}



				//String commodityCode = ledger.getCommodityCode();
				String formattedPair = trade.getFormattedPair();

				boolean sell = "sell".equals(trade.getType());
				//if type is sell then loan is left commodity, since that commodity was loaned to be sold
				BigDecimal value = trade.getCost();  //.add(ledger.getFee());
				CurrencyUnit isoCurrency = extractRightCommodity(trade);

				MonetaryAmount btcPrice;
				MonetaryAmount altPrice;
				MonetaryAmount currencyAmount;
				AccountEntry entry;
				Commodity left = Commodity.commodity().withSymbol(trade.getLeftCommodity()).build();
				Commodity right = Commodity.commodity().withSymbol(trade.getRightCommodity()).build();
				if (left.isDigitalCurrency()) { // && right.isDigitalCurrency()) {
					logger.info("Found opening {} margin trade", trade.getFormattedPair());
					btcPrice = CoinMarketCapBtcEod.getClose(transaction.getDate());
					altPrice = btcPrice.multiply(trade.getPrice());
					currencyAmount = altPrice.multiply(trade.getVolume());
					String entryMemo = "Loan from Kraken in  " + (sell ? trade.getLeftCommodity() : trade.getRightCommodity());
					CommodityAmount commodityAmount = CommodityAmount.commodityAmount().withCommodity(right).withAmount(trade.getCost()).build();
					entry = AccountEntry.accountEntry()
							.withAccount(krakenLoans)
							.withAccountSide(AccountSide.CREDIT)
							.withCurrencyAmount(currencyAmount)
							.withCommodityAmount(commodityAmount)
							.withDescription(entryMemo)
							.build();					
				} else {
					entry = AccountEntry.accountEntry()
							.withAccount(krakenLoans)
							.withCurrency(isoCurrency)
							.withAmount(value)
							.withCommodityUnits(sell ? trade.getVolume() : trade.getCost())
							.withCommodityName(sell ? trade.getLeftCommodity() : trade.getRightCommodity())
							.withDescription("Credit loan from Kraken in  " + (sell ? trade.getLeftCommodity() : trade.getRightCommodity())).build();
				}

				//if the price is unavailable as at that time, then use historical cost
				//materiality!
				boolean skipGainLoss = false;
				if (!ledger.getCommodityCode().equals(trade.getLeftCommodity())) {
					skipGainLoss = true;
				}
				BigDecimal historicalCostPerUnit = commodityHoldings.get(ledger.getCommodityCode()).getCostPerUnitAsAt(transaction.getDate());

				AccountEntry feeEntry = AccountEntry.accountEntry()
						.withAccount(krakenLedger.getMarginFeeAccount())
						.withCurrency(skipGainLoss ? krakenLedger.getBaseCurrency() : isoCurrency)
						.withAmount(ledger.getFee().multiply(skipGainLoss ? historicalCostPerUnit : trade.getPrice()))  //price will serve as the exchange rate
						.withCommodityUnits(ledger.getFee())
						.withCommodityName(ledger.getCommodityCode())
						.withDescription("Margin entry fee").build();


				logger.info("Historical cost per unit = " + historicalCostPerUnit);
				logger.info("Current market price per unit as per trade price = " + trade.getPrice() 
				+ " ( for: " + trade.getFormattedPair() + ")");

				AccountEntry deductFeeFromAssetEntry = AccountEntry.accountEntry()
						.withAccount(commodityHoldings.get(ledger.getCommodityCode()))
						.withCurrency(krakenLedger.getBaseCurrency())
						//historical cost per unit * commodityAmount
						.withAmount(ledger.getFee().multiply(historicalCostPerUnit))
						.withAccountSide(AccountSide.CREDIT)
						.withCommodityUnits(ledger.getFee())
						.withCommodityName(ledger.getCommodityCode())
						.withDescription("Margin entry fee").build();

				//Position USD is the asset, selling XBT for USD is equivalent of buying USD with XBT, so asset is USD held
				//not historical cost
				//if buying ETH with USD, effective asset held is ETH and Kraken lent me USD
				Account position = krakenLedger.createMarginPosition("Position: " + trade.getType() + " : " + formattedPair);
				AccountEntry entry2;
				if (left.isDigitalCurrency()) { // && right.isDigitalCurrency()) {
					logger.info("Found opening {} margin trade", trade.getFormattedPair());
					btcPrice = CoinMarketCapBtcEod.getClose(transaction.getDate());
					altPrice = btcPrice.multiply(trade.getPrice());
					currencyAmount = altPrice.multiply(trade.getVolume());
					String entryMemo = "Asset borrowed: " + (sell ? trade.getLeftCommodity() : trade.getRightCommodity());
					CommodityAmount commodityAmount = CommodityAmount.commodityAmount().withCommodity(left).withAmount(trade.getVolume()).build();
					entry2 = AccountEntry.accountEntry()
							.withAccount(position)
							.withAccountSide(AccountSide.DEBIT)
							.withCurrencyAmount(currencyAmount)
							.withCommodityAmount(commodityAmount)
							.withDescription(entryMemo)
							.build();
					MarginPosition marginPosition = marginPositions.get(position);
					if (marginPosition == null) {
						marginPosition = new MarginPosition();
						marginPositions.put(position, marginPosition);
					}
					marginPosition.addOpeningTrade(trade);				
				} else {
					entry2 = AccountEntry.accountEntry()
							.withAccount(position)
							.withCurrency(isoCurrency)
							.withAmount(value)
							.withCommodityUnits(trade.getVolume())
							.withCommodityName(trade.getLeftCommodity())
							.withDescription("Asset borrowed: " +
									(sell ? trade.getRightCommodity() : trade.getLeftCommodity()))
							.build();
				}

				transaction.withEntry(entry).withEntry(feeEntry).withEntry(deductFeeFromAssetEntry).withEntry(entry2);
				krakenJournal.addTransaction(transaction);
				if (!skipGainLoss) {
					if (deductFeeFromAssetEntry.getAmount().compareTo(feeEntry.getAmount()) < 0) {
						//trading gain (taxable)
						Account gain = krakenLedger.getTradingGainAccount();
						//the fee is already accounted for in the ledger amounts, so reintroduce as a separate line item by adding
						BigDecimal gainAmount = feeEntry.getAmount().subtract(deductFeeFromAssetEntry.getAmount());
						AccountEntry gainEntry = AccountEntry.accountEntry().withAccount(gain).withDescription("Digital currency trading gain")
								.withCurrency(krakenLedger.getBaseCurrency())
								.withAmount(gainAmount).build();
						transaction.add(gainEntry);
					} else {
						//trading loss
						//trading loss (taxable)
						BigDecimal lossAmount = deductFeeFromAssetEntry.getAmount().subtract(feeEntry.getAmount());
						Account loss = krakenLedger.getTradingLossAccount();
						AccountEntry lossEntry = AccountEntry.accountEntry().withAccount(loss).withDescription("Digital currency trading loss")
								.withCurrency(krakenLedger.getBaseCurrency())
								.withAccountSide(AccountSide.DEBIT)
								.withAmount(lossAmount).build();
						transaction.add(lossEntry);
					}
				}
				krakenJournal.audit(transaction);
			} else if (description.startsWith("rollover")) {
				String tradeTxId = ledger.getTradeTxId();
				KrakenTrade trade = trades.get(tradeTxId);

				BigDecimal historicalCostPerUnit = commodityHoldings.get(ledger.getCommodityCode()).getCostPerUnitAsAt(transaction.getDate());

				BigDecimal rolloverCost = ledger.getFee();
				Commodity rolloverCostCommodity = Commodity.commodity().withSymbol(ledger.getCommodityCode()).build();
				CommodityAmount rolloverCostAmount = CommodityAmount.commodityAmount()
						.withCommodity(rolloverCostCommodity)
						.withAmount(rolloverCost).build();
				AccountEntry feeEntry = AccountEntry.accountEntry()
						.withAccount(krakenLedger.getTradeFeeAccount())
						.withCurrency(krakenLedger.getBaseCurrency())
						.withAmount(ledger.getFee().multiply(historicalCostPerUnit))  //price will serve as the exchange rate
						.withCommodityUnits(ledger.getFee())
						.withCommodityName(ledger.getCommodityCode())
						.withDescription("Rollover fee").build();

				logger.info("Historical cost per unit = " + historicalCostPerUnit);
				logger.info("Current market price per unit as per trade price = " + trade.getPrice()
				+ " ( for: " + trade.getFormattedPair() + ")");

				Commodity left = trade.getLeft();
				Commodity right = trade.getRight();
				if (left.isDigitalCurrency() && right.isDigitalCurrency()) {
					Commodity usdCommodity = Commodity.commodity().withSymbol("USD").build();
					Commodity btcCommodity = Commodity.commodity().withSymbol("BTC").build();
					Account position = krakenLedger.createMarginPosition("Position: " + trade.getType() + " : " + trade.getFormattedPair());
					CommodityExchangeRate rate = rolloverCostAmount.getCommodity().equals(btcCommodity)
							? CoinMarketCapBtcEod.getRate(transaction.getDate())
									: CoinMarketCapEthEod.getRate(transaction.getDate());
							KrakenTradeParser.marginPositions.get(position)
							.addRolloverCost(rolloverCostAmount.convertTo(usdCommodity, rate));
				} else if (left.isDigitalCurrency() && !right.isDigitalCurrency()) {
					Commodity usdCommodity = Commodity.commodity().withSymbol("USD").build();
					Commodity eurCommodity = Commodity.commodity().withSymbol("EUR").build();
					Commodity btcCommodity = Commodity.commodity().withSymbol("BTC").build();
					Account position = krakenLedger.createMarginPosition("Position: " + trade.getType() + " : " + trade.getFormattedPair());
					CommodityExchangeRate rate;
					if (!rolloverCostAmount.getCommodity().equals(eurCommodity)) {
						rate = rolloverCostAmount.getCommodity().equals(btcCommodity)
								? CoinMarketCapBtcEod.getRate(transaction.getDate())
										: CoinMarketCapEthEod.getRate(transaction.getDate());
								//TODO convert timezone first:
								logger.error("TODO convert timezone first in ZDT to LD conversion");
					} else {
						BigDecimal crossRate = RBNZHistoricalExchangeRatesReader.getCrossRate(transaction.getDate().toLocalDate(), "EUR", "USD");
						CommodityPair eurUSDPair = CommodityPair.commodityPair().withLeft(eurCommodity).withRight(usdCommodity).build();
						rate = CommodityExchangeRate.commodityExchangeRate().withCommodityPair(eurUSDPair).withRate(crossRate)
								.withDateTime(transaction.getDate()).build();
					}
					CommodityAmount convertTo = rolloverCostAmount.convertTo(usdCommodity, rate);
					logger.info("Converted from: " + rolloverCostAmount);
					logger.info("Rate: " + rate);
					logger.info("Converted to: " + convertTo);
					KrakenTradeParser.marginPositions.get(position)
						.addRolloverCost(convertTo);
				}
				AccountEntry deductFeeFromAssetEntry = AccountEntry.accountEntry()
						.withAccount(commodityHoldings.get(ledger.getCommodityCode()))
						.withCurrency(krakenLedger.getBaseCurrency())
						//historical cost per unit * commodityAmount
						.withAmount(ledger.getFee().multiply(historicalCostPerUnit))
						.withAccountSide(AccountSide.CREDIT)
						.withCommodityUnits(ledger.getFee())
						.withCommodityName(ledger.getCommodityCode())
						.withDescription("Rollover fee").build();

				transaction.withEntry(feeEntry).withEntry(deductFeeFromAssetEntry);
				krakenJournal.addTransaction(transaction);
				krakenJournal.audit(transaction);
			}




			//TODO tidy up the following old CEX conditions:

			else if ((description.startsWith("up") || description.startsWith("f")) && cryptoTransfer.startsWith("withdraw")) {
				// Digital currency withdrawal from kraken:
				BigDecimal commodityAmount = new BigDecimal(columns[1]);
				AccountEntry entry1 = new AccountEntry();
				logger.info("currency code:" + currencyCode);
				Account cryptoAccount = commodityHoldings.get(currencyCode);
				entry1.setAccount(cryptoAccount);
				entry1.setDescription("Credit Kraken "+foreignCurrencyCode+" Holdings");
				entry1.setCommodityName(foreignCurrencyCode);
				entry1.setCommodityUnits(commodityAmount.abs());
				entry1.setTransaction(transaction);
				BigDecimal historicalCost = cryptoAccount.getCostPerUnitAsAt(transaction.getDate());
				entry1.setAmount(commodityAmount.abs().multiply(historicalCost));
				entry1.setAccountSide(AccountSide.CREDIT);
				AccountEntry entry2 = new AccountEntry();
				Account cryptoAddress = Account.account().withGuid(UUID.randomUUID().toString()).build();
				String address = description.startsWith("f") ? description : description.split(" ")[2];
				int indexOf = address.indexOf(".");
				if (indexOf != -1)
					address = address.substring(0, indexOf);
				logger.info("Crypto address: " + address);
				cryptoAddress.setName("Crypto Address: " + address);
				cryptoAddress.setParent(cryptoAccount.getParent());
				entry2.setAccount(cryptoAddress);
				List<AccountEntry> splits = new ArrayList<>();
				entry2.setDescription("Debit " + cryptoAddress.getName());
				entry2.setCommodityName(foreignCurrencyCode);
				entry2.setAccountSide(AccountSide.DEBIT);
				entry2.setCommodityUnits(commodityAmount.abs());
				entry2.setAmount(entry1.getAmount());
				entry2.setTransaction(transaction);
				splits.add(entry1);
				splits.add(entry2);
				addressedBTCHoldings.put(address, cryptoAddress);
				transaction.setTransactionSplits(splits);
				transaction.setDescription(currencyCode + " Digital Currency Withdrawal");
			} else if (description.startsWith("Confirmed BTC-BTG split event")) {
				//Free BTG was provided based on BTC, at $0 cost
				//so treat it as a $0 cost asset transfer with $0 as the BTG cost basis
				BigDecimal commodityAmount = new BigDecimal(columns[1]);
				AccountEntry entry1 = new AccountEntry();
				Account cryptoAccount = commodityHoldings.get(currencyCode);
				entry1.setAccount(cryptoAccount);
				entry1.setDescription("Debit kraken "+foreignCurrencyCode+" Holdings");
				entry1.setTransaction(transaction);
				entry1.setCommodityName(foreignCurrencyCode);
				entry1.setCommodityUnits(commodityAmount.abs());
				entry1.setAccountSide(AccountSide.DEBIT);
				AccountEntry entry2 = new AccountEntry();
				entry2.setAccount(krakenBTCHoldings);
				List<AccountEntry> splits = new ArrayList<>();
				entry2.setDescription("Free BTG at 90/10 ratio");
				entry2.setCommodityName("BTC");
				entry2.setAmount(BigDecimal.ZERO);  //free
				entry2.setAccountSide(AccountSide.CREDIT);
				entry2.setCommodityUnits(BigDecimal.ZERO);
				entry2.setTransaction(transaction);
				splits.add(entry1);
				splits.add(entry2);
				transaction.setTransactionSplits(splits);
				transaction.setDescription(currencyCode + " Digital Currency Deposit");
			} else if (description.startsWith("Confirmed") && cryptoTransfer.startsWith("deposit")) {
				// Digital currency withdrawal from kraken:
				BigDecimal commodityAmount = new BigDecimal(columns[1]);
				AccountEntry entry1 = new AccountEntry();
				logger.info("currency code:" + currencyCode);
				Account cryptoAccount = commodityHoldings.get(currencyCode);
				entry1.setAccount(cryptoAccount);
				entry1.setDescription("Debit kraken "+foreignCurrencyCode+" Holdings");
				entry1.setTransaction(transaction);
				entry1.setCommodityName(foreignCurrencyCode);
				entry1.setCommodityUnits(commodityAmount.abs());
				entry1.setAccountSide(AccountSide.DEBIT);
				AccountEntry entry2 = new AccountEntry();
				Account cryptoAddress = Account.account().withGuid(UUID.randomUUID().toString()).build();
				String address = description.split(" ")[1];
				int indexOf = address.indexOf(".");
				if (indexOf != -1)
					address = address.substring(0, indexOf);
				logger.info("Crypto address: " + address);
				cryptoAddress.setName("Crypto Address: " + address);
				cryptoAddress.setParent(cryptoAccount.getParent());
				entry2.setAccount(cryptoAddress);
				List<AccountEntry> splits = new ArrayList<>();
				entry2.setDescription("Credit " + cryptoAddress.getName());
				entry2.setCommodityName(foreignCurrencyCode);
				entry2.setAccountSide(AccountSide.CREDIT);
				entry2.setCommodityUnits(commodityAmount.abs());
				entry2.setTransaction(transaction);
				splits.add(entry1);
				splits.add(entry2);
				transaction.setTransactionSplits(splits);
				transaction.setDescription(currencyCode + " Digital Currency Deposit");
			} else if (description.startsWith("Buy Order #")) {
				String orderReference = description.substring(11).trim();
				logger.info("ref: " + orderReference);
				BuyOrder order = new BuyOrder();
				order.setReference(orderReference);
				order.setCommodityName(currencyCode);
				order.setAmount(amount.abs());
				buyOrders.add(order);
				referenceToBuyOrder.put(orderReference, order);
				logger.info(order.toString());
				continue;
			} else if (description.startsWith(REIMBURSED_EXCESS_FOR_ORDER)) {
				String orderReference = description.substring(REIMBURSED_EXCESS_FOR_ORDER.length()).trim();
				logger.info("ref: " + orderReference);
				BuyOrder order = referenceToBuyOrder.get(orderReference);
				order.setExecuted(true);
				order.setReimbursedAmount(amount);
				logger.info(order.toString());
				continue;
			} else if (description.startsWith("Sell Order #")) {
				/*
				 * Example memorandum entry:
				 * "On May 1, 2013 a 2-for-1 stock split was declared for the common stockholders
				 *  of record as of the end of the day May 22, 2013. The stock split will result 
				 *  in the number of issued and outstanding shares of common shares increasing 
				 *  from 200,000 shares to 400,000 shares."
				 */
				String orderReference = description.substring(11);
				SellOrder order = new SellOrder();
				order.setReference(orderReference);
				order.setCommodityName(currencyCode);
				order.setAmount(amount.abs());
				sellOrders.add(order);
				logger.info(order.toString());
				continue;
			} else if (description.startsWith("Bought")) {
				String[] descriptionSplit = description.split(" ");
				BigDecimal units = new BigDecimal(descriptionSplit[1]);
				BigDecimal currencyAmount = new BigDecimal(descriptionSplit[4]);
				BigDecimal foreignCurrencyAmount = units.multiply(currencyAmount);
				CurrencyUnit currency = Monetary.getCurrency(foreignCurrencyCode);
				AccountEntry entry = AccountEntry.accountEntry()
						.withAccount(commodityHoldings.get(currencyCode))
						.withCurrency(currency)
						.withAmount(foreignCurrencyAmount.abs())
						.withCommodityUnits(amount)
						.withCommodityName(currencyCode)
						.withDescription("Debit kraken " + currencyCode + " Holdings").build();
				//Account buyOrderAccount = Account.account()
				//		.withGuid(UUID.randomUUID().toString())
				//		.withParent(assets)
				//		.withName(currencyCode + " Buy Orders").build();
				AccountEntry entry2 = AccountEntry.accountEntry()
						.withAccount(commodityHoldings.get(foreignCurrencyCode))
						.withAmount(foreignCurrencyAmount.negate())
						.withCurrency(currency)
						.withCommodityName(foreignCurrencyCode)
						.withCommodityUnits(foreignCurrencyAmount.negate())
						.withDescription("Credit " + currencyCode + " Buy Orders").build();
				transaction.withEntry(entry).withEntry(entry2);
				transaction.setDescription("Purchase " + currencyCode);
				krakenJournal.addTransaction(transaction);
			} else if (description.startsWith("Sold")) {
				String[] descriptionSplit = description.split(" ");
				BigDecimal units = new BigDecimal(descriptionSplit[1]);
				BigDecimal currencyAmount = new BigDecimal(descriptionSplit[4]);
				BigDecimal foreignCurrencyAmount = units.multiply(currencyAmount);
				CurrencyUnit currency = Monetary.getCurrency(foreignCurrencyCode);
				AccountEntry entry = AccountEntry.accountEntry()
						.withAccount(commodityHoldings.get(currencyCode))
						.withCurrency(currency)
						.withAmount(foreignCurrencyAmount.abs())
						.withCommodityUnits(amount)
						.withCommodityName(currencyCode)
						.withDescription("Credit kraken " + currencyCode + " Holdings").build();
				AccountEntry entry2 = AccountEntry.accountEntry()
						.withAccount(commodityHoldings.get(foreignCurrencyCode))
						.withAmount(foreignCurrencyAmount.negate())
						.withCurrency(currency)
						.withCommodityName(foreignCurrencyCode)
						.withCommodityUnits(foreignCurrencyAmount.negate())
						.withDescription("Debit " + currencyCode + " Sell Orders").build();
				transaction.withEntry(entry).withEntry(entry2);
				transaction.setDescription("Sold " + currencyCode);
				krakenJournal.addTransaction(transaction);
			} else if (description.startsWith("Credit card:") && "withdraw".equals(columns[4])) {
				BigDecimal currencyAmount = new BigDecimal(columns[3]);
				String transactionCurrency = columns[2].trim();
				AccountEntry entry = AccountEntry.accountEntry()
						.withAccount("USD".equals(transactionCurrency) ? krakenUSDHoldings : krakenEURHoldings)
						.withCurrency(Monetary.getCurrency(transactionCurrency))
						.withAmount(currencyAmount)
						.withAccountSide(AccountSide.CREDIT)
						.withCommodityName(transactionCurrency)
						.withCommodityUnits(currencyAmount)
						.withDescription(String.format("Credit kraken %1$s Holdings", transactionCurrency)).build();
				AccountEntry entry2 = AccountEntry.accountEntry()
						.withAccount(drawings)
						.withAccountSide(AccountSide.DEBIT)
						.withAmount(currencyAmount).withDescription("Debit Drawings - Martin")
						.withCurrency(Monetary.getCurrency(transactionCurrency)).build();
				transaction.add(entry).add(entry2);
				transaction.setDescription(String.format("Credit kraken %1$s Holdings via Credit Card Withdrawal", transactionCurrency));
				krakenJournal.addTransaction(transaction);
			} else if (description.startsWith("Credit card:")) {
				BigDecimal currencyAmount = new BigDecimal(columns[3]);
				String transactionCurrency = columns[2].trim();
				AccountEntry entry = AccountEntry.accountEntry()
						.withAccount("USD".equals(transactionCurrency) ? krakenUSDHoldings : krakenEURHoldings)
						.withCurrency(Monetary.getCurrency(transactionCurrency))
						.withAmount(currencyAmount)
						.withCommodityName(transactionCurrency)
						.withCommodityUnits(currencyAmount)
						.withDescription(String.format("Debit kraken %1$s Holdings", transactionCurrency)).build();
				AccountEntry entry2 = AccountEntry.accountEntry()
						.withAccount(fundsIntroduced)
						.withAmount(currencyAmount).withDescription("Credit Funds Introduced - Martin")
						.withCurrency(Monetary.getCurrency(transactionCurrency)).build();
				transaction.add(entry);
				transaction.add(entry2);
				transaction.setDescription(String.format("Credit kraken %1$s Holdings by Credit Card", transactionCurrency));
				krakenJournal.addTransaction(transaction);
			} 
			//			else if (timestamp == 1513820172000L && description.startsWith("fa56c186")) {
			//				// Digital currency withdrawal from kraken:
			//				BigDecimal commodityAmount = new BigDecimal(columns[1]);
			//				AccountEntry split1 = new AccountEntry();
			//				split1.setAccount(krakenBCHHoldings);
			//				split1.setDescription("Debit kraken BCH Holdings");
			//				split1.setTransaction(transaction);
			//				split1.setCommodityName("BCH");
			//				split1.setCommodityUnits(commodityAmount);
			//				AccountEntry split2 = new AccountEntry();
			//				Account pendingBCHWithdrawals = Account.account().withGuid(UUID.randomUUID().toString()).build();
			//				pendingBCHWithdrawals.setName("Kraken BCH Withdrawals Pending");
			//				split2.setAccount(pendingBCHWithdrawals);
			//				List<AccountEntry> splits = new ArrayList<>();
			//				split2.setDescription("Credit kraken BCH Withdrawals Pending");
			//				split2.setCommodityName("BCH");
			//				split2.setCommodityUnits(commodityAmount);
			//				split2.setTransaction(transaction);
			//				splits.add(split1);
			//				splits.add(split2);
			//				transaction.setTransactionSplits(splits);
			//				transaction.setDescription("BCH Digital Currency Withdrawal");
			//			} 
			else {
				split.setAccount(krakenUSDHoldings);   //need to convert using NZD/USD daily rate
				split.setAmount(new BigDecimal("28.99"));
				split.setDescription("Spent 28.99 USD to place a buy order");
				split.setTransaction(transaction);
				transactionSplits.add(split);
				transaction.setTransactionSplits(transactionSplits);
			}
			logger.info("transaction = " + transaction);
		}
		logger.info("Commodity holdings summary:");

		//only 2017 is important
		Integer[] years = new Integer[] {2017};
		Arrays.asList(years).forEach(yearToLog -> commodityHoldings.forEach((name, account) -> logClosingBalance(yearToLog, name, account)));

		logClosingBalance(2017, "Loss:",  krakenLedger.getTradingLossAccount());
		logClosingBalance(2017, "Gain:",  krakenLedger.getTradingGainAccount());
		
		logger.info("Missing Trade Count: " + missingTradeCount);
	}

	private static CurrencyUnit extractRightCommodity(KrakenTrade trade) {
		return "USD".equals(trade.getRightCommodity()) || "EUR".equals(trade.getRightCommodity())
				? Monetary.getCurrency(trade.getRightCommodity()) : null;
	}

	private static void logClosingBalance(int year, String name, Account account) {
		//logger.info("Account name: " + account.getName());
		//logger.info("-----------------------------------");
		logger.info(name + " kraken Closing balance for " + year + " tax year: " + account.getClosingBalanceForTaxYear(year) + " " + account.getLastCurrencyCode());
		logger.info(name + " kraken Commodity Closing balance for " + year + " tax year: " + account.getClosingCommodityBalanceForTaxYear(year));
	}

}
