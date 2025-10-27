package com.github.taxbeans.crypto.test;

public class JavaMoneyFix {

	public static void fix() {
		java.util.logging.Logger.getLogger("org.javamoney.moneta.DefaultMonetaryContextFactory")
				.setLevel(java.util.logging.Level.SEVERE);
		java.util.logging.Logger.getLogger("org.javamoney.moneta.Money").setLevel(java.util.logging.Level.SEVERE);
		java.util.logging.Logger.getLogger("org.javamoney").setLevel(java.util.logging.Level.SEVERE);
	}

}
