package com.github.taxbeans.crypto;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.taxbeans.csv.CSVParser;
import com.github.taxbeans.currency.ExchangeRateUtils;
import com.github.taxbeans.forms.utils.LocalDateTimeUtils;
import com.github.taxbeans.model.Account;
import com.github.taxbeans.model.AccountEntry;
import com.github.taxbeans.model.AccountSide;
import com.github.taxbeans.model.BaseCurrencyAdapter;
import com.github.taxbeans.model.DigitalCurrency;
import com.github.taxbeans.model.DigitalCurrencyAdapter;
import com.github.taxbeans.model.DigitalCurrencyCode;
import com.github.taxbeans.model.DigitalCurrencyUtils;
import com.github.taxbeans.model.Inventory;
import com.github.taxbeans.model.Journal;
import com.github.taxbeans.model.Ledger;
import com.github.taxbeans.model.Transaction;

public class CEXTradeParser {

	private static final String REIMBURSED_EXCESS_FOR_ORDER = "Reimbursed excess for order #";
	final static Logger logger = LoggerFactory.getLogger(CEXTradeParser.class);

	// CEX exchange timezone in exported CSV is GMT - double check by making a new
	// transaction and checking new CSV export
	private static ZoneId zone = ZoneId.of("GMT");

	public static void main(String[] args) throws Exception {
		List<String[]> parsedFile = CSVParser.newInstance().parseFile(
				CEXTradeParser.class.getClassLoader().getResourceAsStream("tradehistory/cex-transactions.csv"), true);
		process(parsedFile);
	}

	public static Ledger process(List<String[]> csv) throws ParseException {
		Journal cexJournal = new Journal();
		Ledger cexLedger = new Ledger();
		cexLedger.addJournal(cexJournal);
		Account assets = cexLedger.getAssetsAccount();
		Map<String, Account> commodityHoldings = new HashMap<>();
		Account btcHoldings = Account.account().withGuid(UUID.randomUUID().toString()).withName("BTC Holdings")
				.withParent(assets).build();

		// Map of bitcoin addresses to accounts
		Map<String, Account> addressedBTCHoldings = new HashMap<>(); // Account.account().withGuid(UUID.randomUUID().toString()).withName("CEX
																		// XRP
																		// Holdings").withParent(btcHoldings).build();,,
		List<Inventory> inventory = new ArrayList<>();
		Account cexUSDHoldings = Account.account().withGuid(UUID.randomUUID().toString()).withName("CEX USD Holdings")
				.withParent(assets).build();
		Account cexXRPHoldings = Account.account().withGuid(UUID.randomUUID().toString()).withName("CEX XRP Holdings")
				.withParent(btcHoldings).build();
		Account cexBTCHoldings = Account.account().withGuid(UUID.randomUUID().toString()).withName("CEX BTC Holdings")
				.withParent(assets).build();
		Account cexBCHHoldings = Account.account().withGuid(UUID.randomUUID().toString()).withName("CEX BCH Holdings")
				.withParent(assets).build();
		Account cexEURHoldings = Account.account().withGuid(UUID.randomUUID().toString()).withName("CEX EUR Holdings")
				.withParent(assets).build();
		Account cexETHHoldings = Account.account().withGuid(UUID.randomUUID().toString()).withName("CEX ETH Holdings")
				.withParent(assets).build();
		Account cexBTGHoldings = Account.account().withGuid(UUID.randomUUID().toString()).withName("CEX BTG Holdings")
				.withParent(assets).build();
		Account fundsIntroduced = Account.account().withGuid(UUID.randomUUID().toString())
				.withName("Funds Introduced - Martin").withParent(cexLedger.getEquityAccount()).build();
		Account drawings = Account.account().withGuid(UUID.randomUUID().toString()).withName("Drawings - Martin")
				.withParent(cexLedger.getEquityAccount()).build();

		// Use one account for all buy orders, instead of one account for each buy order
//		Account buyOrderAccount = Account.account().withGuid(UUID.randomUUID().toString()).withName("CEX Buy Orders")
//				.withParent(assets).build();
//		Account sellOrderAccount = Account.account().withGuid(UUID.randomUUID().toString()).withName("CEX Sell Orders")
//				.withParent(assets).build();

		commodityHoldings.put("USD", cexUSDHoldings);
		commodityHoldings.put("XRP", cexXRPHoldings);
		commodityHoldings.put("BTC", cexBTCHoldings);
		commodityHoldings.put("BCH", cexBCHHoldings);
		commodityHoldings.put("EUR", cexEURHoldings);
		commodityHoldings.put("ETH", cexETHHoldings);
		commodityHoldings.put("BTG", cexBTGHoldings);

		// Financial transactions must be in forward date order to efficiently process
		// Consider historical cost, etc.
		List<DatedRow> rows = new ArrayList<>();
		for (String[] columns : csv) {
			SimpleDateFormat format = new SimpleDateFormat("y" + "yyy-MM-dd HH:mm:ss");
			Date parse = format.parse(columns[0]);
			LocalDateTime localDate = LocalDateTimeUtils.convert(parse);
			rows.add(new DatedRow(localDate, columns));
		}
		DatedRow[] sortedRows = rows.toArray(new DatedRow[rows.size()]);
		Arrays.sort(sortedRows);

		List<BuyOrder> buyOrders = new ArrayList<>();
		List<SellOrder> sellOrders = new ArrayList<>();
		Map<String, BuyOrder> referenceToBuyOrder = new HashMap<>();

		for (DatedRow row : sortedRows) {
			String[] columns = row.getColumns();
			System.out.println("line = " + Arrays.asList(columns));
			Transaction transaction = new Transaction();
			SimpleDateFormat format = new SimpleDateFormat("y" + "yyy-MM-dd HH:mm:ss");
			Date parse = format.parse(columns[0]);
			// LocalDate localDate = LocalDateUtils.convert(parse);
			long timestamp = parse.getTime();
			transaction.setDate(ZonedDateTime.ofInstant(parse.toInstant(), zone));
			BigDecimal amount = new BigDecimal(columns[1]);
			String cryptoTransfer = columns[4].trim();
			String description = columns[8].trim();
			String currencyCode = columns[2].trim();
			String foreignCurrencyCode = columns[6];
			List<AccountEntry> transactionSplits = new ArrayList<>();
			AccountEntry split = new AccountEntry();
			split.setAmount(amount);

			if ((description.startsWith("up") || description.startsWith("f"))
					&& cryptoTransfer.startsWith("withdraw")) {
				// Digital currency withdrawal from CEX:
				BigDecimal commodityAmount = new BigDecimal(columns[1]);
				AccountEntry entry1 = new AccountEntry();
				logger.info("currency code:" + currencyCode);
				if (currencyCode == "ETH") {
					System.out.println("debug this!");
				}
				Account cryptoAccount = commodityHoldings.get(currencyCode);
				entry1.setAccount(cryptoAccount);
				entry1.setDescription("Credit CEX " + currencyCode + " Holdings");
				entry1.setCommodityName(currencyCode);
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
				entry2.setCommodityName(currencyCode);
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
				// Free BTG was provided based on BTC, at $0 cost
				// so treat it as a $0 cost asset transfer with $0 as the BTG cost basis
				BigDecimal commodityAmount = new BigDecimal(columns[1]);
				AccountEntry entry1 = new AccountEntry();
				Account cryptoAccount = commodityHoldings.get(currencyCode);
				entry1.setAccount(cryptoAccount);
				entry1.setDescription("Debit CEX " + foreignCurrencyCode + " Holdings");
				entry1.setTransaction(transaction);
				entry1.setCommodityName(foreignCurrencyCode);
				entry1.setCommodityUnits(commodityAmount.abs());
				entry1.setAccountSide(AccountSide.DEBIT);
				AccountEntry entry2 = new AccountEntry();
				entry2.setAccount(cexBTCHoldings);
				List<AccountEntry> splits = new ArrayList<>();
				entry2.setDescription("Free BTG at 90/10 ratio");
				entry2.setCommodityName("BTC");
				entry2.setAmount(BigDecimal.ZERO); // free
				entry2.setAccountSide(AccountSide.CREDIT);
				entry2.setCommodityUnits(BigDecimal.ZERO);
				entry2.setTransaction(transaction);
				splits.add(entry1);
				splits.add(entry2);
				transaction.setTransactionSplits(splits);
				transaction.setDescription(currencyCode + " Digital Currency Deposit");
			} else if (description.startsWith("Confirmed") && cryptoTransfer.startsWith("deposit")) {
				// Digital currency withdrawal from CEX:
				BigDecimal commodityAmount = new BigDecimal(columns[1]);
				AccountEntry entry1 = new AccountEntry();
				logger.info("currency code:" + currencyCode);
				Account cryptoAccount = commodityHoldings.get(currencyCode);
				entry1.setAccount(cryptoAccount);
				entry1.setDescription("Debit CEX " + currencyCode + " Holdings");
				entry1.setTransaction(transaction);
				entry1.setCommodityName(currencyCode);
				entry1.setCommodityUnits(commodityAmount.abs());
				entry1.setAccountSide(AccountSide.DEBIT);
				DigitalCurrency digitalCurrency = new DigitalCurrency(commodityAmount.abs(), DigitalCurrencyCode.valueOf(currencyCode));
				Inventory inventory2 = new Inventory();
				inventory2.setDigitalCurrency(digitalCurrency);
				//TODO obtain historical cost from crypto address:
				inventory2.setHistoricalCost(Money.of(new BigDecimal(100), Monetary.getCurrency("NZD")));
				digitalCurrency.add(inventory2);
				entry1.setDigitalCurrency(digitalCurrency);
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
				entry2.setCommodityName(currencyCode);
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
				logger.trace(order.toString());
				continue;
			} else if (description.startsWith(REIMBURSED_EXCESS_FOR_ORDER)) {
				String orderReference = description.substring(REIMBURSED_EXCESS_FOR_ORDER.length()).trim();
				logger.trace("ref: " + orderReference);
				BuyOrder order = referenceToBuyOrder.get(orderReference);
				order.setExecuted(true);
				order.setReimbursedAmount(amount);
				logger.trace(order.toString());
				continue;
			} else if (description.startsWith("Sell Order #")) {
				/*
				 * Example memorandum entry: "On May 1, 2013 a 2-for-1 stock split was declared
				 * for the common stockholders of record as of the end of the day May 22, 2013.
				 * The stock split will result in the number of issued and outstanding shares of
				 * common shares increasing from 200,000 shares to 400,000 shares."
				 */
				String orderReference = description.substring(11);
				SellOrder order = new SellOrder();
				order.setReference(orderReference);
				order.setCommodityName(currencyCode);
				order.setAmount(amount.abs());
				sellOrders.add(order);
				logger.trace(order.toString());
				continue;
			} else if (description.startsWith("Bought")) {
				String[] descriptionSplit = description.split(" ");
				BigDecimal units = new BigDecimal(descriptionSplit[1]);
				BigDecimal currencyAmount = new BigDecimal(descriptionSplit[4]);
				BigDecimal foreignCurrencyAmount = units.multiply(currencyAmount);
				CurrencyUnit currency = Monetary.getCurrency(foreignCurrencyCode);
				AccountEntry entry = AccountEntry.accountEntry().withAccount(commodityHoldings.get(currencyCode))
						.withCurrency(currency).withAmount(foreignCurrencyAmount.abs()).withCommodityUnits(amount)
						.withCommodityName(currencyCode).withDescription("Debit CEX " + currencyCode + " Holdings")
						.build();
				// Account buyOrderAccount = Account.account()
				// .withGuid(UUID.randomUUID().toString())
				// .withParent(assets)
				// .withName(currencyCode + " Buy Orders").build();
				AccountEntry entry2 = AccountEntry.accountEntry()
						.withAccount(commodityHoldings.get(foreignCurrencyCode))
						.withAmount(foreignCurrencyAmount.negate()).withCurrency(currency)
						.withCommodityName(foreignCurrencyCode).withCommodityUnits(foreignCurrencyAmount.negate())
						.withDescription("Credit " + currencyCode + " Buy Orders").build();
				transaction.withEntry(entry).withEntry(entry2);
				transaction.setDescription("Purchase " + currencyCode);
				Inventory inventoryItem = new Inventory();
				BaseCurrencyAdapter baseCurrencyAdapter = new BaseCurrencyAdapter();
				baseCurrencyAdapter.setBaseCurrency(cexLedger.getBaseCurrency());
				inventoryItem.setHistoricalCost(
						baseCurrencyAdapter.adapt(transaction.getDate(), foreignCurrencyAmount.negate(), currency));
				inventory.add(inventoryItem);
				try {
					DigitalCurrency digitalCurrency = DigitalCurrencyAdapter.adapt(currencyCode,
							new BigDecimal(columns[1]));
					inventoryItem.setDigitalCurrency(digitalCurrency);
					digitalCurrency.add(inventoryItem);
					entry2.setDigitalCurrency(digitalCurrency);
					entry.setDigitalCurrency(digitalCurrency);
				} catch (Throwable e) {
					logger.error("Currency code = " + currencyCode);
					logger.error("Currency amount = " + foreignCurrencyAmount);
					logger.error("Columns = " + Arrays.asList(columns));
					logger.error("Columns = " + Arrays.asList(columns).get(1));
					logger.error("Currency = " + currency + ", foreignCurrency = " + foreignCurrencyCode);
					throw e;
				}
				cexJournal.addTransaction(transaction);
			} else if (description.startsWith("Sold")) {
				String[] descriptionSplit = description.split(" ");
				BigDecimal units = new BigDecimal(descriptionSplit[1]);
				BigDecimal currencyAmount = new BigDecimal(descriptionSplit[4]);
				BigDecimal foreignCurrencyAmount = units.multiply(currencyAmount);
				CurrencyUnit isoCurrency = Monetary.getCurrency(foreignCurrencyCode);
				AccountEntry entry = AccountEntry.accountEntry().withAccount(commodityHoldings.get(currencyCode))
						.withCurrency(isoCurrency).withAmount(foreignCurrencyAmount.abs()).withCommodityUnits(amount)
						.withCommodityName(currencyCode).withDescription("Credit CEX " + currencyCode + " Holdings")
						.build();
				// DigitalCurrency digitalCurrency = DigitalCurrencyAdapter.adapt(currencyCode,
				// amount);
				// entry.setDigitalCurrency(digitalCurrency);
				AccountEntry entry2 = AccountEntry.accountEntry()
						.withAccount(commodityHoldings.get(foreignCurrencyCode))
						.withAmount(foreignCurrencyAmount.negate()).withCurrency(isoCurrency)
						.withCommodityName(foreignCurrencyCode).withCommodityUnits(foreignCurrencyAmount.negate())
						.withDescription("Debit " + currencyCode + " Sell Orders").build();
				BigDecimal amountSold = new BigDecimal(descriptionSplit[1]);
				String digitalCurrencyCode = descriptionSplit[2];
				List<Inventory> btcInventory = null;
				try {
					DigitalCurrency digitalCurrency2 = DigitalCurrencyAdapter.adapt(digitalCurrencyCode,
							amountSold);
					entry2.setDigitalCurrency(digitalCurrency2);
					btcInventory = DigitalCurrencyUtils.getInventory(commodityHoldings.get(digitalCurrencyCode));
				} catch (Throwable t) {
					logger.error("Currency code = " + currencyCode);
					logger.error("descriptionSplit3 (digital currency symbol) = " + descriptionSplit[2]);
					logger.error("descriptionSplit1 (digital currency amount) = " + descriptionSplit[1]);
					
					throw t;
				}
				Inventory inventory1 = btcInventory.get(0);
				BigDecimal amountAvailable = inventory1.getDigitalCurrency().getAmount();
				
				if (amountAvailable.subtract(amountSold).signum() < 0) {
					logger.error("Amount sold = " + amountSold);
					logger.error("Amount available = " + amountAvailable);
					throw new AssertionError("No more BTC inventory available for FIFO processing");
				}
				
				BigDecimal costBasis = amountSold.divide(amountAvailable).multiply(MoneyAdapter.adapt(inventory1.getHistoricalCost()));
				costBasis = costBasis.abs();
				BigDecimal isoCurrencyAmount = new BigDecimal(columns[1]);
				BigDecimal baseCurrencyAmount = ExchangeRateUtils.exchange(transaction.getDate(), 
						isoCurrency, cexLedger.getBaseCurrency(), isoCurrencyAmount);
				BigDecimal netTaxableProfit = baseCurrencyAmount.subtract(costBasis);
//				BigDecimal netTaxableProfitRounded = netTaxableProfit.setScale(14, RoundingMode.HALF_UP);
//				if (netTaxableProfitRounded.compareTo(new BigDecimal("2.80682062055957111")) != 0) {
//					logger.error("Euro amount = " + isoCurrencyAmount);
//					logger.error("Base currency amount = " + 
//							baseCurrencyAmount);
//					logger.error("Cost basis = " + costBasis);
//					logger.error("Transaction ISO currency = " + isoCurrency);
//					logger.error("netTaxableProfit = " + netTaxableProfit);
//					logger.error("netTaxableProfitRounded = " + netTaxableProfitRounded);
//					throw new AssertionError("Temp failure, profit incorrect: " + netTaxableProfit);
//				}
				Account relevantAccount;
				if (netTaxableProfit.signum() < 0) {
					relevantAccount = cexLedger.getTradingLossAccount();
				} else {
					relevantAccount = cexLedger.getTradingGainAccount();
				}
				AccountEntry entry3 = AccountEntry.accountEntry()
						.withAccount(relevantAccount)
						.withAmount(netTaxableProfit)
						.withCommodityName(foreignCurrencyCode).withCommodityUnits(foreignCurrencyAmount.negate())
						.withDescription("Net taxable profit on trade").build();
				transaction.withEntry(entry).withEntry(entry2).withEntry(entry3);
				transaction.setDescription("Sold " + currencyCode);
				cexJournal.addTransaction(transaction);
			} else if (description.startsWith("Credit card:") && "withdraw".equals(columns[4])) {
				BigDecimal currencyAmount = new BigDecimal(columns[3]);
				String transactionCurrency = columns[2].trim();
				AccountEntry entry = AccountEntry.accountEntry()
						.withAccount("USD".equals(transactionCurrency) ? cexUSDHoldings : cexEURHoldings)
						.withCurrency(Monetary.getCurrency(transactionCurrency)).withAmount(currencyAmount)
						.withAccountSide(AccountSide.CREDIT).withCommodityName(transactionCurrency)
						.withCommodityUnits(currencyAmount)
						.withDescription(String.format("Credit CEX %1$s Holdings", transactionCurrency)).build();
				AccountEntry entry2 = AccountEntry.accountEntry().withAccount(drawings)
						.withAccountSide(AccountSide.DEBIT).withAmount(currencyAmount)
						.withDescription("Debit Drawings - Martin")
						.withCurrency(Monetary.getCurrency(transactionCurrency)).build();
				transaction.add(entry).add(entry2);
				transaction.setDescription(
						String.format("Credit CEX %1$s Holdings via Credit Card Withdrawal", transactionCurrency));
				cexJournal.addTransaction(transaction);
			} else if (description.startsWith("Credit card:")) {
				BigDecimal currencyAmount = new BigDecimal(columns[3]);
				String transactionCurrency = columns[2].trim();
				AccountEntry entry = AccountEntry.accountEntry()
						.withAccount("USD".equals(transactionCurrency) ? cexUSDHoldings : cexEURHoldings)
						.withCurrency(Monetary.getCurrency(transactionCurrency)).withAmount(currencyAmount)
						.withCommodityName(transactionCurrency).withCommodityUnits(currencyAmount)
						.withDescription(String.format("Debit CEX %1$s Holdings", transactionCurrency)).build();
				AccountEntry entry2 = AccountEntry.accountEntry().withAccount(fundsIntroduced)
						.withAmount(currencyAmount).withDescription("Credit Funds Introduced - Martin")
						.withCurrency(Monetary.getCurrency(transactionCurrency)).build();
				transaction.add(entry);
				transaction.add(entry2);
				transaction
						.setDescription(String.format("Credit CEX %1$s Holdings by Credit Card", transactionCurrency));
				cexJournal.addTransaction(transaction);
			} else if (timestamp == 1513820172000L && description.startsWith("fa56c186")) {
				// Digital currency withdrawal from CEX:
				BigDecimal commodityAmount = new BigDecimal(columns[1]);
				AccountEntry split1 = new AccountEntry();
				split1.setAccount(cexBCHHoldings);
				split1.setDescription("Debit CEX BCH Holdings");
				split1.setTransaction(transaction);
				split1.setCommodityName("BCH");
				split1.setCommodityUnits(commodityAmount);
				AccountEntry split2 = new AccountEntry();
				Account pendingBCHWithdrawals = Account.account().withGuid(UUID.randomUUID().toString()).build();
				pendingBCHWithdrawals.setName("CEX BCH Withdrawals Pending");
				split2.setAccount(pendingBCHWithdrawals);
				List<AccountEntry> splits = new ArrayList<>();
				split2.setDescription("Credit CEX BCH Withdrawals Pending");
				split2.setCommodityName("BCH");
				split2.setCommodityUnits(commodityAmount);
				split2.setTransaction(transaction);
				splits.add(split1);
				splits.add(split2);
				transaction.setTransactionSplits(splits);
				transaction.setDescription("BCH Digital Currency Withdrawal");
			} else {
				split.setAccount(cexUSDHoldings); // need to convert using NZD/USD daily rate
				split.setAmount(new BigDecimal("28.99"));
				split.setDescription("Spent 28.99 USD to place a buy order");
				split.setTransaction(transaction);
				transactionSplits.add(split);
				transaction.setTransactionSplits(transactionSplits);
			}
			logger.trace("transaction = " + transaction);
		}
		logger.trace("Commodity holdings summary:");

		Integer[] years = new Integer[] { 2016, 2017, 2018 };
		Arrays.asList(years).forEach(
				yearToLog -> commodityHoldings.forEach((name, account) -> logClosingBalance(yearToLog, name, account)));
		return cexLedger;
	}

	private static void logClosingBalance(int year, String name, Account account) {
		// logger.info("Account name: " + account.getName());
		// logger.info("-----------------------------------");
		logger.trace(
				name + " CEX Closing balance for " + year + " tax year: " + account.getClosingBalanceForTaxYear(year));
		logger.trace(name + " CEX Commodity Closing balance for " + year + " tax year: "
				+ account.getClosingCommodityBalanceForTaxYear(year));
	}

}
