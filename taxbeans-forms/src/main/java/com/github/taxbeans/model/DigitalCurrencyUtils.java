package com.github.taxbeans.model;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DigitalCurrencyUtils {

	final static Logger logger = LoggerFactory.getLogger(DigitalCurrencyUtils.class);
	
	public static List<Inventory> getInventory(Account account) {
		List<AccountEntry> entries = account.getSplits();
		List<Inventory> inventory = new ArrayList<>();
		for (AccountEntry entry : entries) {
			try {
				inventory.addAll(entry.getDigitalCurrency().getInventory());
			} catch (Exception e) {
				logger.error("Problematic entry = " + entry);
				throw e;
			}
		}
		return inventory;
	}

}
