package com.github.taxbeans.crypto;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.ZonedDateTime;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;

/**
 * A currency batch represents an amount of currency that has been bought on a particular date.
 *
 */
public class CurrencyBatch extends AbstractCryptoEvent {

	/*
	 * Note that a batch shouldn't have an exchange attribute, because of FIFO rules
	 * since amounts can be transferred from one exchange to another, but that shouldn't affect the batches
	 * so have to init the batches and balances manually, cannot use a list of trades!
	 */

	/**
	 * mostly immutable apart from leveraged and rowNum
	 * copy on write
	 */

	private volatile static int idToAssign = 1;

	/*
	 * Special constructor for CSV parsing
	 */
	public static CurrencyBatch of(CurrencyBatchGroup batchGroup, String id, ZonedDateTime datePurchased, CurrencyAmount cost, CurrencyAmount amount,
			String status, String parentID2, CurrencyAmount initialAmount) {
		CurrencyBatch batch = new CurrencyBatch();
		batch.batchGroup =  batchGroup;
		batch.initialAmount = initialAmount;
		batch.datePurchased = datePurchased;
		batch.amountRemaining = amount.copy();
		batch.amountUsed = CurrencyAmount.of(BigDecimal.ZERO, amount);
		batch.setInitialCost(cost.copy());
		batch.unitCost =  batch.initialCost.divide(batch.initialAmount);  //batch.initialAmount.divide(cost, MathContext.DECIMAL128);
		if ("USD".equals(batch.initialAmount.getCurrencyString())) {
			System.out.println("**USD cost per unit = " + batch.unitCost);
			/*
			 * throw an assertion error if greater than $2
			 */
			if (batch.unitCost.isGreaterThan("2")) {
				throw new AssertionError("Unexpected USD unit cost");
			}
		}
		batch.baseCurrencyAmountRemaining = batch.initialCost.copy();
		batch.id = Integer.parseInt(id);
		idToAssign = Math.max(idToAssign,  batch.id) + 1;
		return batch;
	}


	public static CurrencyBatch of(CurrencyBatchGroup batchGroup, CurrencyAmount currencyAmount,
			ZonedDateTime of, CurrencyAmount costBase) {
		CurrencyBatch batch = new CurrencyBatch();
		batch.batchGroup = batchGroup;
		batch.initialAmount = currencyAmount.copy();
		batch.datePurchased = of;
		batch.amountRemaining = currencyAmount.copy();
		batch.amountUsed = CurrencyAmount.of(BigDecimal.ZERO, currencyAmount);
		batch.setInitialCost(costBase.copy());
		batch.unitCost =  batch.initialCost.divide(batch.initialAmount);  //batch.initialAmount.divide(cost, MathContext.DECIMAL128);
		if ("USD".equals(batch.initialAmount.getCurrencyString())) {
			System.out.println("**USD cost per unit = " + batch.unitCost);
			/*
			 * throw an assertion error if greater than $2
			 */
			if (batch.unitCost.isGreaterThan("2")) {
				throw new AssertionError("Unexpected USD unit cost");
			}
		}
		batch.baseCurrencyAmountRemaining = batch.initialCost.copy();
		batch.id = idToAssign;
		idToAssign++;
		return batch;
	}

	public static CurrencyBatch of(CurrencyAmount currencyAmount, ZonedDateTime of, CurrencyAmount costBase) {
		CurrencyBatch batch = new CurrencyBatch();
		batch.initialAmount = currencyAmount.copy();
		batch.datePurchased = of;
		batch.amountRemaining = currencyAmount.copy();
		batch.amountUsed = CurrencyAmount.of(BigDecimal.ZERO, currencyAmount);
		batch.setInitialCost(costBase.copy());
		batch.unitCost =  batch.initialCost.divide(batch.initialAmount);  //batch.initialAmount.divide(cost, MathContext.DECIMAL128);
		if ("USD".equals(batch.initialAmount.getCurrencyString())) {
			System.out.println("**USD cost per unit = " + batch.unitCost);
			/*
			 * throw an assertion error if greater than $2
			 */
			if (batch.unitCost.isGreaterThan("2")) {
				throw new AssertionError("Unexpected USD unit cost");
			}
		}
		batch.baseCurrencyAmountRemaining = batch.initialCost.copy();
		batch.id = idToAssign;
		idToAssign++;
		return batch;
	}

	/*
	 * used to establish a batch from trade profits etc
	 */
	public static CurrencyBatch ofNonBaseCurrency(CurrencyAmount currencyAmount, ZonedDateTime of, CurrencyAmount costBase, CurrencyBatchGroup group) {
		CurrencyBatch batch = new CurrencyBatch();
		batch.batchGroup = group;
		batch.initialAmount = currencyAmount.copy();
		batch.datePurchased = of;
		batch.amountRemaining = currencyAmount.copy();
		batch.amountUsed = CurrencyAmount.of(BigDecimal.ZERO, currencyAmount);
		if (costBase.isSameCurrency(batch.batchGroup.getBaseCurrency())) {
			batch.setInitialCost(costBase);
		} else {
			/*
			 * When it's not the same currency, the bought amount is used for the valuation of course!
			 * the sold amount doesn't provide the actual valuation of the batch
			 */
			ConversionUtils converter = ConversionUtils.of(batch.batchGroup.getCurrencyConversionStrategy());
			MonetaryAmount baseCurrencyCost = converter.convert(batch.batchGroup.getBaseCurrency(),
					currencyAmount.copy(), of);
			batch.setInitialCost(CurrencyAmount.of(baseCurrencyCost));
		}
		batch.baseCurrencyAmountRemaining = batch.initialCost.copy();
		batch.id = idToAssign;
		idToAssign++;
		return batch;
	}

	public static CurrencyBatch of(CurrencyAmount currencyAmount, ZonedDateTime of, CurrencyAmount costBase, CurrencyBatchGroup group) {
		CurrencyBatch batch = new CurrencyBatch();
		batch.batchGroup = group;
		batch.initialAmount = currencyAmount.copy();
		batch.datePurchased = of;
		batch.amountRemaining = currencyAmount.copy();
		batch.amountUsed = CurrencyAmount.of(BigDecimal.ZERO, currencyAmount);
		batch.assertBatchGroupSet();
		batch.setInitialCost(costBase.copy());
		batch.baseCurrencyAmountRemaining = batch.initialCost.copy();
		batch.id = idToAssign;
		idToAssign++;
		return batch;
	}

	public CurrencyBatch copy() {
		CurrencyBatch batch = new CurrencyBatch();
		batch.initialAmount = this.initialAmount;
		batch.datePurchased = this.datePurchased;
		batch.amountRemaining = this.amountRemaining;
		batch.amountUsed = this.amountUsed;
		batch.batchGroup = this.batchGroup;
		batch.baseCurrencyAmountRemaining = this.baseCurrencyAmountRemaining;
		batch.setInitialCost(this.initialCost);
		batch.id = idToAssign;
		idToAssign++;
		//System.err.println("Created batch copy: " + batch);
		return batch;
	}

	int id;

	private String group;

	ZonedDateTime datePurchased;

	private CurrencyAmount amountUsed;

	private CurrencyAmount amountRemaining;

	/*
	 * this amount should never change, even if the batch is split
	 * this is the cost of 1 unit, e.g. 1 USD, not the amount for 1 NZD
	 */
	private CurrencyAmount unitCost;

	CurrencyAmount initialAmount;

	CurrencyAmount initialCost;

	private CurrencyBatchGroup batchGroup;

	private CurrencyBatchSet usedSubBatches = CurrencyBatchSet.of();

	private boolean leveraged;

	private int rowNum;

	private CurrencyAmount baseCurrencyAmountRemaining;

	boolean fullyUsed;

	//private boolean newBatch;

	boolean split;

	int parentId;

	private void assertBatchGroupSet() {
		if (batchGroup == null) {
			throw new AssertionError("Batch group not set, you must set the batch group first");
		}
	}

	private void assertInitialAmountNotNull() {
		if (initialAmount == null) {
			throw new AssertionError("Initial amount not set, you must set the initial amount");
		}
	}

	@Override
	public int compareTo(CryptoEvent o) {
		return this.getWhen().compareTo(o.getWhen());
	}

	public boolean canUse(CurrencyAmount currencyAmount) {
		CurrencyAmount amountRemaining2 = getAmountRemaining();
		if (!amountRemaining2.isSameCurrency(currencyAmount)) {
			return false;
		}
		return currencyAmount.isLessThanOrEqualTo(amountRemaining2);
	}

	public CurrencyAmount getAmountRemaining() {
		return amountRemaining;
	}

	public MonetaryAmount getInitialCostInBaseCurrency() {
		if (leveraged) {
			return batchGroup.getBaseCurrencyZeroAmount();
		}
		if (batchGroup == null) {
			throw new IllegalStateException("Batch group must be set");
		}
		CurrencyUnit baseCurrency = batchGroup.getBaseCurrency();
		if (baseCurrency == null) {
			throw new IllegalStateException("Base currency must be set");
		}
		if (this.initialAmount.getCurrencyCode().isSameCurrency(baseCurrency)) {
			return this.initialAmount.getMonetaryAmount();
		} else if (this.initialCost.getCurrencyCode().isSameCurrency(baseCurrency)) {
			return this.initialCost.getMonetaryAmount();
		} else {
			assertBatchGroupSet();
			ConversionUtils converter = ConversionUtils.of(batchGroup.getCurrencyConversionStrategy());
			MonetaryAmount baseCurrencyCost = converter.convert(baseCurrency, initialAmount, datePurchased);
			return baseCurrencyCost;
		}
	}

	public MonetaryAmount getUnitInitialCostInBaseCurrency() {
		assertInitialAmountNotNull();
		return initialAmount.swapDivide(getInitialCostInBaseCurrency());  //.divide(initialAmount.getAmount());
	}

	public ZonedDateTime getWhen() {
		return datePurchased;
	}

	public boolean isFullyUsed() {
		return fullyUsed ||  amountRemaining.isZeroOrLess();
	}

	public void setBatchGroup(CurrencyBatchGroup batchGroup) {
		this.batchGroup = batchGroup;
	}

	@Override
	public String toString() {
//		final MonetaryAmountFormat format = MonetaryFormats.getAmountFormat(
//			      AmountFormatQueryBuilder.of(Locale.US)
//			        .set(CurrencyStyle.SYMBOL)
//			        .build()
//			    );
		return "Batch[id=" + this.id + ",created=" + datePurchased + ",used=" + amountUsed
				+ ",left="
				+ amountRemaining + ",initial=" + initialAmount + "/" +
				this.getInitialCostInBaseCurrencySafe()
				+ ",group=" + batchGroup + ",parentID=" + parentId + ",usedSubBatches=" + usedSubBatches + "]";
	}

	public String toCSVString() {
		String status = this.split ? "split" : (this.fullyUsed ? "used" : "new");
		BigDecimal unitCost = this.initialCost.getBigDecimal().divide(this.initialAmount.getBigDecimal(), MathContext.DECIMAL128);
		CurrencyAmount currencyUnitCost = CurrencyAmount.of(unitCost, initialCost.getCurrencyCode());
		return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s", this.id, this.datePurchased, this.initialCost, this.amountRemaining,
				status, currencyUnitCost, this.parentId, this.initialAmount, this.deriveBaseCurrencyAmountRemaining());
	}

	private MonetaryAmount getInitialCostInBaseCurrencySafe() {
	//	try {
			return getInitialCostInBaseCurrency();
//		} catch (NullPointerException e) {
//			return null;
//		}
	}

	public CurrencyBatchSet use(CurrencyAmount currencyAmount) {
		if (!canUse(currencyAmount)) {
			throw new AssertionError(currencyAmount + " is greater than amount remaining of " + getAmountRemaining());
		}

		//make batches immutable and log and split them
		CurrencyBatchSet splitBatches = CurrencyBatchSet.of();

		//amountRemaining.mutatingSubtract(cryptoAmount);
		CurrencyBatch splitBatch = this.copy();
		splitBatch.split = true;
		splitBatches.add(splitBatch);

		this.amountUsed = this.amountUsed.add(currencyAmount);
		this.amountRemaining = this.amountRemaining.subtract(currencyAmount);

		BigDecimal initialCostInBaseCurrency = this.getInitialCostInBaseCurrency().getNumber().numberValue(BigDecimal.class);
		BigDecimal multiplicand = splitBatch.amountUsed.getBigDecimal().multiply(initialCostInBaseCurrency);
		BigDecimal baseCurrencyAmountUsed = multiplicand.divide(initialAmount.getBigDecimal(), MathContext.DECIMAL128);
		CurrencyAmount baseCurrencyAmountUsedObject = CurrencyAmount.of(baseCurrencyAmountUsed,
				CurrencyCode.of(batchGroup.getBaseCurrency().toString()));
		this.baseCurrencyAmountRemaining = this.initialCost.subtract(baseCurrencyAmountUsedObject);
		//this.newBatch = true;
		//splitBatch.setInitialCost(splitBatch.baseCurrencyAmountRemaining);
		//splitBatch.baseCurrencyAmountRemaining = splitBatch.initialCost;
		//splitBatch.amountUsed = CurrencyAmount.of(BigDecimal.ZERO, splitBatch.amountUsed.getCurrencyCode());
		//splitBatch.initialAmount = splitBatch.amountRemaining;
		//splitBatch.amountUsed = 0;
		//splitBatch.newBatch = true;
		//splitBatch.parentId = this.id;
		//this.split = true;
		//System.err.println("Parent=" + this);
		//System.err.println("Parent CSV=" + this.toCSVString());
		//System.err.println("Split=" + splitBatch);
		//System.err.println("Split CSV=" + splitBatch.toCSVString());
		//splitBatches.add(splitBatch);

		//amountUsed = amountUsed.add(cryptoAmount);
		CurrencyAmount multiplicand2 = CurrencyAmount.of(currencyAmount.multiply(getUnitInitialCostInBaseCurrency()));
		CurrencyBatch usedSubBatch = CurrencyBatch.of(CurrencyAmount.of(currencyAmount, this.amountRemaining.getCurrencyCode()),
				this.getWhen(), multiplicand2, batchGroup);
		usedSubBatch.amountUsed = amountRemaining.copy();
		usedSubBatch.amountRemaining = CurrencyAmount.of(BigDecimal.ZERO, this.amountUsed);
		usedSubBatch.fullyUsed = true;
		usedSubBatch.parentId = this.id;
		usedSubBatch.setBatchGroup(this.batchGroup);
		//System.err.println("Used=" + usedSubBatch);
		//System.err.println("Used CSV=" + usedSubBatch.toCSVString());

		usedSubBatches.add(usedSubBatch);
		splitBatches.add(usedSubBatch);
		return splitBatches;
	}

	public CurrencyAmount getInitialAmount() {
		return initialAmount;
	}

	public boolean isLeveraged() {
		return leveraged;
	}

	public void setLeveraged(boolean leveraged) {
		this.leveraged = leveraged;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public boolean isSameCurrency(CurrencyAmount currencyAmount) {
		return amountRemaining.isSameCurrency(currencyAmount);
	}

	public int getRowNum() {
		return rowNum;
	}

	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}

	public MonetaryAmount getAverageCost() {
		return batchGroup.getCostViaWAC(this);
	}

	public int getId() {
		return id;
	}

	public void setInitialCost(CurrencyAmount initialCost) {
		if (batchGroup == null) {
			batchGroup = CurrencyBatchContext.getBatchGroup();
			if (batchGroup == null) {
				throw new IllegalStateException("batch group must be set first");
			}
		}
		if (!initialCost.getCurrencyCode().isSameCurrency(batchGroup.getBaseCurrency())) {
			throw new IllegalStateException("currency mismatch");
		}
		this.initialCost = initialCost;
	}

	public CurrencyAmount getBaseCurrencyAmountRemaining() {
		return baseCurrencyAmountRemaining;
	}


	public static CurrencyBatch parse(String[] row, CurrencyBatchGroup batchGroup2) {
		/*
		 * ID,Date Purchased,Cost,Amount,Status,Cost Per Unit,Parent ID
		 */
		String id = row[0];
		ZonedDateTime datePurchased = ZonedDateTime.parse(row[1]);
		CurrencyAmount cost = CurrencyAmount.parse(row[2]);
		CurrencyAmount amount = CurrencyAmount.parse(row[3]);
//		if ("BTC".equals(amount.getCurrencyCode().toString())) {
//			totalBTC = totalBTC.add(amount);
//			System.out.println("*******total BTC= " + totalBTC);
//		}
		String status = row[4];
		String parentID = row[6];
		CurrencyAmount initialAmount = CurrencyAmount.parse(row[7]);
		//ID,Date Purchased,Cost,Amount Remaining,Status,Cost Per Unit,Parent ID,Initial Amount,Running Total BTC
		//129925,2017-01-01T08:51Z[UTC],30.91 NZD,1.0122 USD,new,1.5455 NZD,0,20 USD,,,1.3723243196 BTC
		CurrencyBatch batch = CurrencyBatch.of(batchGroup2, id, datePurchased, cost, amount, status, parentID, initialAmount);
		return batch;
	}


	public CurrencyAmount deriveBaseCurrencyAmountRemaining() {
		BigDecimal amountRemainingBigDecimal = amountRemaining.getBigDecimal();
		BigDecimal unitCost = this.initialCost.getBigDecimal().divide(this.initialAmount.getBigDecimal(), MathContext.DECIMAL128);
		BigDecimal baseAmountRemainingBigDecimal = amountRemainingBigDecimal.multiply(unitCost);
		return CurrencyAmount.of(baseAmountRemainingBigDecimal, "NZD");
	}
}
