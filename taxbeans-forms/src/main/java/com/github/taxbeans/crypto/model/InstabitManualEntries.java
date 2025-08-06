package com.github.taxbeans.crypto.model;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.github.taxbeans.model.Account;
import com.github.taxbeans.model.AccountEntry;
import com.github.taxbeans.model.AccountSide;
import com.github.taxbeans.model.Journal;
import com.github.taxbeans.model.Ledger;
import com.github.taxbeans.model.Transaction;
import com.github.taxbeans.model.builder.TransactionBuilder;

public class InstabitManualEntries {

	public Journal getTransactions(Ledger ledger, Journal journal) {
		if (ledger == null) {
			ledger = new Ledger();
		}
		if (journal == null) {
			journal = new Journal();
		}
		
		Account breadWalletAccount = Account.account()
				.withName("Bread Wallet")
				.withParent(ledger.getAssetsAccount()).build();
		
		
		//Manual estimate for bread-wallet.csv - 29-12-15,912548.27
		{
			//Payment was made through coined, not instabit
			Transaction transaction = TransactionBuilder.transaction()
					.withDescription("Purchase 0.91254827 BTC by bank cash deposit for 600 NZD on Dec 29, 2015 at 12:10pm NZDT")
					.withDate(ZonedDateTime.of(2015, 12, 29, 12, 10, 0, 0, 
							ZoneId.of("Pacific/Auckland"))).build();
			AccountEntry entry = AccountEntry.accountEntry().withAmount(new BigDecimal("600"))
					.withCommodityName("BTC")
					.withCommodityUnits(new BigDecimal("0.91254827"))
					.withAccount(breadWalletAccount)
					.withAccountSide(AccountSide.BALANCE_EFFECT)
					.build();
			entry.setCryptoAddress("1B85KtJQs2ubsYhtfNREz2fgKvd5Q6jjyQ");
			transaction.addEntry(entry);
			Account cash = Account.account().withName("Cash")
					.withParent(ledger.getAssetsAccount()).build();
			entry = AccountEntry.accountEntry().withAmount(new BigDecimal("661.11"))
					.withAccount(cash)
					.withAccountSide(AccountSide.BALANCE_EFFECT)
					.build();
			transaction.addEntry(entry);
			journal.addTransaction(transaction);
		}
		
		//BTC-Gmail-Instabit-buy-order-activated-2016-01-06.pdf
		{
			Transaction transaction = TransactionBuilder.transaction()
					.withDescription("Purchase 2.00146376 BTC by bank cash deposit for 1450 on Jan 6, 2016 at 11:13am NZDT")
					.withDate(ZonedDateTime.of(2016, 1, 6, 11, 13, 0, 0, 
							ZoneId.of("Pacific/Auckland"))).build();
			AccountEntry entry = AccountEntry.accountEntry().withAmount(new BigDecimal("1450"))
					.withCommodityName("BTC")
					.withCommodityUnits(new BigDecimal("2.00146376"))
					.withAccount(breadWalletAccount)
					.withAccountSide(AccountSide.BALANCE_EFFECT)
					.build();
			entry.setCryptoAddress("1EQiUSwkXUPiHzJeFh7WnEcUjq5kqwa1is");
			transaction.addEntry(entry);
			Account cash = Account.account().withName("Cash")
					.withParent(ledger.getAssetsAccount()).build();
			entry = AccountEntry.accountEntry().withAmount(new BigDecimal("1450"))
					.withAccount(cash)
					.withAccountSide(AccountSide.BALANCE_EFFECT)
					.build();
			transaction.addEntry(entry);
			journal.addTransaction(transaction);
		}

		//BTC-Gmail-Instabit-buy-order-activated-2016-01-08.pdf
		{
			Transaction transaction = TransactionBuilder.transaction()
					.withDescription("Purchase 2.00048928 BTC by bank cash deposit for 1470 on Jan 8, 2016 at 2:42pm NZDT")
					.withDate(ZonedDateTime.of(2016, 1, 8, 14, 42, 0, 0, 
							ZoneId.of("Pacific/Auckland"))).build();
			AccountEntry entry = AccountEntry.accountEntry().withAmount(new BigDecimal("1470"))
					.withCommodityName("BTC")
					.withCommodityUnits(new BigDecimal("2.00048928"))
					.withAccount(breadWalletAccount)
					.withAccountSide(AccountSide.BALANCE_EFFECT)
					.build();
			entry.setCryptoAddress("12w8M5Ked3afEWPSWNe6rT4gpHQVVp4eX7");
			transaction.addEntry(entry);
			Account cash = Account.account().withName("Cash")
					.withParent(ledger.getAssetsAccount()).build();
			entry = AccountEntry.accountEntry().withAmount(new BigDecimal("1470"))
					.withAccount(cash)
					.withAccountSide(AccountSide.BALANCE_EFFECT)
					.build();
			transaction.addEntry(entry);
			journal.addTransaction(transaction);
		}
		
		//BTC-Gmail-Instabit-buy-order-activated-2016-06-17.pdf
		{
			Transaction transaction = TransactionBuilder.transaction()
					.withDescription("Purchase 0.93676347 BTC by bank cash deposit for 1000 on Jun 17, 2016 at 3:42pm NZDT")
					.withDate(ZonedDateTime.of(2016, 6, 17, 15, 42, 0, 0, 
							ZoneId.of("Pacific/Auckland"))).build();
			AccountEntry entry = AccountEntry.accountEntry().withAmount(new BigDecimal("1000"))
					.withCommodityName("BTC")
					.withCommodityUnits(new BigDecimal("0.93676347"))
					.withAccount(breadWalletAccount)
					.withAccountSide(AccountSide.BALANCE_EFFECT)
					.build();
			entry.setCryptoAddress("1FwD6qbbwSaG6iHqTpYaEpmKrutDZxob9Q");
			transaction.addEntry(entry);
			Account cash = Account.account().withName("Cash")
					.withParent(ledger.getAssetsAccount()).build();
			entry = AccountEntry.accountEntry().withAmount(new BigDecimal("1000"))
					.withAccount(cash)
					.withAccountSide(AccountSide.BALANCE_EFFECT)
					.build();
			transaction.addEntry(entry);
			journal.addTransaction(transaction);
		}

		journal.setLedger(ledger);
		return journal;

	}

}
