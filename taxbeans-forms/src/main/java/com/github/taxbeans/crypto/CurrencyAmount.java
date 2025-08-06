package com.github.taxbeans.crypto;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.ZonedDateTime;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;

import org.javamoney.moneta.Money;

public class CurrencyAmount {

	public static CurrencyAmount of(MonetaryAmount amount) {
		return CurrencyAmount.of(getBigDecimal(amount), getCurrencyString(amount));
	}


	public static CurrencyAmount of(BigDecimal amount, CurrencyCode currencyCode) {
		return CurrencyAmount.of(amount, currencyCode.getCurrencyCode());
	}


	public static CurrencyAmount of(CurrencyAmount amount, CurrencyCode currency) {
		if (amount.getCurrencyString().contentEquals(currency.getCurrencyString())) {
			return CurrencyAmount.of(amount.getBigDecimal(), currency.getCurrencyString());
		}
		throw new AssertionError(String.format("Currency mismatch of this %1$s and %2$s",
				amount.getCurrencyString(), currency.getCurrencyString()));
	}

	public static CurrencyAmount of(String amount, String currencyCode) {
		return CurrencyAmount.of(new BigDecimal(amount), currencyCode);
	}

	public static CurrencyAmount of(BigDecimal amount, CurrencyAmount currency) {
		return CurrencyAmount.of(amount, currency.getCurrencyString());
	}

	public static CurrencyAmount of(BigDecimal amount, String currency) {
		CurrencyAmount currencyAmount = new CurrencyAmount();
		return currencyAmount.init(amount, currency);
	}

	CurrencyAmount init(BigDecimal amount, String currency) {
		try {
			//seems to be tradeable for DOGET hence the following conversion:
			if ("DOGET-GDOEVDDBU6OBWKL7VHDAOKD77UP4DKHQYKOKJJT5PR3WRDBTX35HUEUX".equals(currency)) {
				currency = "DOGET";
			}
			Crypto crypto = Crypto.valueOf(currency.toUpperCase().replace("-", "_"));
			this.cryptoAmount = CryptoAmount.of(crypto, amount);
			this.isCrypto = true;
			return this;
		} catch (IllegalArgumentException e) {
			MonetaryAmount monetaryAmount = Money.of(amount, Monetary.getCurrency(currency));
			this.monetaryAmount = monetaryAmount;
			return this;
		}
	}

	private static BigDecimal getBigDecimal(MonetaryAmount monetaryAmount) {
		return monetaryAmount.getNumber().numberValue(BigDecimal.class);
	}

	private static String getCurrencyString(MonetaryAmount monetaryAmount) {
		return monetaryAmount.getCurrency().getCurrencyCode();
	}

	public BigDecimal getBigDecimal() {
		return this.isCrypto ? cryptoAmount.getAmount()
				: monetaryAmount.getNumber().numberValue(BigDecimal.class);
	}

	String getCurrencyString() {
		return this.isCrypto ? cryptoAmount.getCrypto().name()
				: monetaryAmount.getCurrency().getCurrencyCode();
	}

	public CurrencyAmount copy() {
		return CurrencyAmount.of(getBigDecimal(), getCurrencyString()).copyBaseCurrencyAmount(this);
	}

	private static CurrencyAmount zeroNZD = CurrencyAmount.of(BigDecimal.ZERO, "NZD");

	private boolean baseCurrencyAmountSet;

	private CryptoAmount cryptoAmount;

	private MonetaryAmount monetaryAmount;

	private boolean isCrypto;

	private MonetaryAmount baseCurrencyAmount = Money.of(BigDecimal.ZERO, Monetary.getCurrency("NZD"));

	public CryptoAmount getCryptoAmount() {
		return cryptoAmount;
	}

	public MonetaryAmount getMonetaryAmount() {
		return monetaryAmount;
	}

	public boolean isCrypto() {
		return isCrypto;
	}

	public boolean isMonetary() {
		return !isCrypto();
	}

	public static CurrencyAmount getZeroNZD() {
		return zeroNZD;
	}

	public boolean isSameCurrency(CurrencyAmount currencyAmount) {
		return this.getCurrencyString().equals(currencyAmount.getCurrencyString());
	}

	public boolean isSameCurrency(CurrencyCode code) {
		return this.getCurrencyString().equals(code.getCurrencyString());
	}

	public CurrencyAmount add(CurrencyAmount amount) {
		if (amount.getCurrencyString().equals(this.getCurrencyString())) {
			CurrencyAmount amount2 = CurrencyAmount.of(this.getBigDecimal().add(amount.getBigDecimal()), amount.getCurrencyString());
			amount2.baseCurrencyAmount = this.baseCurrencyAmount.add(amount.baseCurrencyAmount);
			amount2.baseCurrencyAmountSet = true;
			return amount2;
		}
		throw new AssertionError(String.format("Currency mismatch of this %1$s and %2$s",
				this.getCurrencyString(), amount.getCurrencyString()));
	}

	CurrencyAmount copyBaseCurrencyAmount(CurrencyAmount amount) {
		if (amount.baseCurrencyAmountSet) {
			this.setBaseCurrencyAmount(amount.getBaseCurrencyAmount());
			this.baseCurrencyAmountSet = true;
		}
		return this;
	}


	public CurrencyAmount subtract(CurrencyAmount amount) {
		if (amount.getCurrencyString().equals(this.getCurrencyString())) {
			CurrencyAmount amount2 = CurrencyAmount.of(this.getBigDecimal().subtract(amount.getBigDecimal()), amount.getCurrencyString());
			amount2.baseCurrencyAmount = this.baseCurrencyAmount.subtract(amount.baseCurrencyAmount);
			return amount2;
		}
		throw new AssertionError(String.format("Currency mismatch of this %1$s and %2$s",
				this.getCurrencyString(), amount.getCurrencyString()));
	}

	//	public void mutatingAdd(CurrencyAmount amount) {
	//		if (amount.getCurrencyString().equals(this.getCurrencyString())) {
	//			this.getBigDecimal().add(amount.getBigDecimal());
	//			return;
	//		}
	//		throw new AssertionError(String.format("Currency mismatch of this %1$s and %2$s",
	//				this.getCurrencyString(), amount.getCurrencyString()));
	//	}

	public MonetaryAmount multiply(MonetaryAmount monetaryAmount) {
		return monetaryAmount.multiply(this.getBigDecimal());
	}

	public boolean isLessThanOrEqualTo(CurrencyAmount amount) {
		if (amount.getCurrencyString().equals(this.getCurrencyString())) {
			return this.getBigDecimal().compareTo(amount.getBigDecimal()) <= 0;
		}
		throw new AssertionError(String.format("Currency mismatch of this %1$s and %2$s",
				this.getCurrencyString(), amount.getCurrencyString()));
	}

	public boolean isLessThan(CurrencyAmount amount) {
		if (amount.getCurrencyString().equals(this.getCurrencyString())) {
			return this.getBigDecimal().compareTo(amount.getBigDecimal()) < 0;
		}
		throw new AssertionError(String.format("Currency mismatch of this %1$s and %2$s",
				this.getCurrencyString(), amount.getCurrencyString()));
	}

	public boolean isZero() {
		return this.getBigDecimal().equals(BigDecimal.ZERO);
	}

	public boolean isZeroOrLess() {
		return this.getBigDecimal().compareTo(BigDecimal.ZERO) <= 0;
	}

	public MonetaryAmount swapDivide(MonetaryAmount initialCostInBaseCurrency) {
		return initialCostInBaseCurrency.divide(this.getBigDecimal());
	}

	/*
	 * divides by an amount, but remains in the same currency
	 */
	public CurrencyAmount divide(CurrencyAmount amount) {
		BigDecimal result = this.getBigDecimal().divide(amount.getBigDecimal(), MathContext.DECIMAL128);
		return CurrencyAmount.of(result, this.getCurrencyCode());
	}

	public static String format(MonetaryAmount value) {
		BigDecimal amount = value.getNumber().numberValue(BigDecimal.class).setScale(2, RoundingMode.HALF_UP);
		return String.format("%,.2f %2$s", amount, value.getCurrency().getCurrencyCode());
	}

	public CurrencyCode getCurrencyCode() {
		return CurrencyCode.of(this.getCurrencyString());
	}


	//	public void mutatingSubtract(CurrencyAmount amount) {
	//		String currencyString = amount.getCurrencyString();
	//		String currencyString2 = this.getCurrencyString();
	//		if (currencyString.equals(currencyString2)) {
	//			this.getBigDecimal().subtract(amount.getBigDecimal());
	//			return;
	//		}
	//		throw new AssertionError(String.format("Currency mismatch of this %1$s and %2$s",
	//				currencyString2, amount.getCurrencyString()));
	//	}


	@Override
	public String toString() {
		return String.format("%1$s %2$s", this.getBigDecimal().stripTrailingZeros().toPlainString(),
				this.getCurrencyString());
	}


	public static BigDecimal format(BigDecimal bigDecimal) {
		return bigDecimal.setScale(2, RoundingMode.HALF_UP);
	}


	public static CurrencyAmount of(CurrencyAmount amount, ZonedDateTime when) {
		CurrencyAmount currencyAmount = new CurrencyAmount();
		currencyAmount.init(amount.getBigDecimal(), amount.getCurrencyCode().getCurrencyCode());
		if (!amount.isCrypto() && amount.baseCurrencyAmountSet && amount.getBaseCurrencyAmount() != null) {
			currencyAmount.setBaseCurrencyAmount(amount.getBaseCurrencyAmount());
			currencyAmount.baseCurrencyAmountSet = true;
		} else {
			ConversionUtils conversionUtils = ConversionUtils.of();
			MonetaryAmount usd = conversionUtils.convert(amount, when);
			currencyAmount.baseCurrencyAmount = conversionUtils.convert(Monetary.getCurrency("NZD"), usd, when);
			currencyAmount.baseCurrencyAmountSet = true;
		}
		return currencyAmount;
	}


	public boolean isSameCurrency(CurrencyUnit baseCurrency) {
		return this.getCurrencyString().equals(baseCurrency.getCurrencyCode());
	}


	public MonetaryAmount add(MonetaryAmount amount) {
		if (this.isCrypto) {
			throw new AssertionError("Cannot add a monetary amount to a digital currency");
		}
		return this.monetaryAmount.add(amount);
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cryptoAmount == null) ? 0 : cryptoAmount.hashCode());
		result = prime * result + (isCrypto ? 1231 : 1237);
		result = prime * result + ((monetaryAmount == null) ? 0 : monetaryAmount.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CurrencyAmount other = (CurrencyAmount) obj;
		if (cryptoAmount == null) {
			if (other.cryptoAmount != null)
				return false;
		} else if (!cryptoAmount.equals(other.cryptoAmount))
			return false;
		if (isCrypto != other.isCrypto)
			return false;
		if (monetaryAmount == null) {
			if (other.monetaryAmount != null)
				return false;
		} else if (!monetaryAmount.equals(other.monetaryAmount))
			return false;
		return true;
	}

	public MonetaryAmount getBaseCurrencyAmount() {
		if (!this.baseCurrencyAmountSet) {
			throw new AssertionError("Check your code because the base currency amount has not been set " +
					"probably due to not reconstructing CurrencyAmount with a datetime");
		}
		return baseCurrencyAmount;
	}


	public void setBaseCurrencyAmount(MonetaryAmount baseCurrencyAmount) {
		this.baseCurrencyAmount = baseCurrencyAmount;
		this.baseCurrencyAmountSet = true;
	}

	public boolean isBaseCurrencyAmountSet() {
		return this.baseCurrencyAmountSet;
	}

	public CurrencyAmount negate() {
		return CurrencyAmount.of(this.getBigDecimal().negate(), this.getCurrencyCode());
	}

	public boolean isPositive() {
		return this.getBigDecimal().signum() > 0;
	}

	public boolean isNegative() {
		return this.getBigDecimal().signum() < 0;
	}


	public CurrencyAmount abs() {
		return CurrencyAmount.of(this.getBigDecimal().abs(), this.getCurrencyCode());
	}

	public String getBigDecimalPlainString() {
		return this.getBigDecimal().stripTrailingZeros().toPlainString();
	}

	public String getBaseCurrencyAmountPlainString() {
		return this.getBaseCurrencyAmount().getNumber().numberValue(BigDecimal.class).stripTrailingZeros().toPlainString();
	}

	public String getBaseCurrencyPlainString() {
		return this.getBaseCurrencyAmount().getCurrency().toString();
	}


	public void setBaseCurrencyAmountSet(boolean baseCurrencyAmountSet) {
		this.baseCurrencyAmountSet = baseCurrencyAmountSet;
	}


	public boolean isFiat() {
		return !isCrypto();
	}

	// Expects string in the form:  "AMOUNT CURRENCY"
	public static CurrencyAmount parse(String balance) {
		String[] parts = balance.split(" ");
		return CurrencyAmount.of(parts[0], parts[1]);
	}


	public boolean isGreaterThan(String string) {
		return this.getBigDecimal().compareTo(new BigDecimal(string)) > 0;
	}


	public String toRoundedString() {
		return String.format("%1$s %2$s", this.getBigDecimal().setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString(),
				this.getCurrencyString());
	}
}
