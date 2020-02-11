package com.github.taxbeans.forms.nz;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDNonTerminalField;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.taxbeans.exception.TaxBeansException;
import com.github.taxbeans.forms.IncludeFormatSpacing;
import com.github.taxbeans.forms.OmitCents;
import com.github.taxbeans.forms.Required;
import com.github.taxbeans.forms.RightAlign;
import com.github.taxbeans.forms.Skip;
import com.github.taxbeans.forms.SkipIfFalse;
import com.github.taxbeans.forms.Sum;
import com.github.taxbeans.forms.UseChildFields;
import com.github.taxbeans.forms.UseDayMonthYear;
import com.github.taxbeans.forms.UseTrueFalseMappings;
import com.github.taxbeans.forms.UseValueMappings;
import com.github.taxbeans.forms.utils.TaxReturnUtils;

public class IR10Form2018 {
	
	private int yearEnded;

	private String fullName;

	@Skip
	private String destinationDirectory;

	@RightAlign(9)
	private String irdNumber;

	@UseTrueFalseMappings
	private boolean multipleActivityRadio;

	@OmitCents
	private Money grossIncome;
	
	@OmitCents
	private Money openingStock;
	
	@OmitCents
	private Money purchases;
	
	@OmitCents
	private Money closingStock;
	
	@OmitCents
	@Required
	@Sum("grossIncome")
	private Money grossProfit;
	
	@OmitCents
	private Money interestReceived;
	
	@OmitCents
	private Money dividends;
	
	@OmitCents
	private Money leasePayments;
	
	@OmitCents
	private Money otherIncome;
	
	@OmitCents
	@Required
	@Sum({"grossProfit", "interestReceived", "dividends", "leasePayments",
			"otherIncome"})
	private Money totalIncome;
	
	@OmitCents
	private Money badDebts;
	
	@OmitCents
	private Money depreciation;
	
	@OmitCents
	private Money insurance;
	
	@OmitCents
	private Money interestExpenses;
	
	@OmitCents
	private Money consultingFees;
	
	@OmitCents
	private Money rates;

	@OmitCents
	private Money leasePaymentExpenses;
	
	@OmitCents
	private Money repairs;
	
	@OmitCents
	private Money researchAndDevelopment;
	
	@OmitCents
	private Money relatedPartyRenumeration;
	
	@OmitCents
	private Money salaryAndWages;
	
	@OmitCents
	private Money subcontractorPayments;
	
	@OmitCents
	private Money otherExpenses;
	
	@OmitCents
	@Required
	@Sum({"badDebts", "depreciation", "insurance", "interestExpenses",
			"consultingFees", "rates", "leasePaymentExpenses", "repairs",
			"researchAndDevelopment", "relatedPartyRenumeration", "salaryAndWages", "subcontractorPayments", 
			"otherExpenses"})			
	private Money totalExpenses;
	
	@OmitCents
	private Money exceptionalItems;
	
	@OmitCents
	@Required
	@Sum(value={"totalIncome", "exceptionalItems"}, negate="totalExpenses")
	private Money netProfitBeforeTax;
	
	@OmitCents
	private Money taxAdjustments;
	
	@OmitCents
	@Required
	@Sum(value="netProfitBeforeTax", negate="taxAdjustments")
	private Money taxableProfit;
	
	@OmitCents
	private Money accountsReceivable;
	
	@OmitCents
	private Money cashAndDeposits;
	
	@OmitCents
	private Money otherCurrentAssets;
	
	@OmitCents
	private Money vehicleAssets;
	
	@OmitCents
	private Money plantAssets;
	
	@OmitCents
	private Money furnitureAssets;
	
	@OmitCents
	private Money land;
	
	@OmitCents
	private Money buildings;
	
	@OmitCents
	private Money otherFixedAssets;
	
	@OmitCents
	private Money intangibles;
	
	@OmitCents
	private Money sharesAndDebentures;
	
	@OmitCents
	private Money termDeposits;
	
	@OmitCents
	private Money otherNonCurrentAssets;
	
	@OmitCents
	@Required
	@Sum({"accountsReceivable", "cashAndDeposits", "otherCurrentAssets", "vehicleAssets",
			"plantAssets", "furnitureAssets", "land", "buildings",
			"otherFixedAssets", "intangibles", "sharesAndDebentures", "termDeposits",
			"otherNonCurrentAssets"})
	private Money totalAssets;
	
	@OmitCents
	private Money provisions;
	
	@OmitCents
	private Money accountsPayable;
	
	@OmitCents
	private Money currentLoans;
	
	@OmitCents
	private Money otherCurrentLiabilities;
	
	@OmitCents
	@Required
	@Sum({"provisions", "accountsPayable", "currentLoans", "otherCurrentLiabilities"})
	private Money totalCurrentLiabilities;
	
	@OmitCents
	private Money nonCurrentLiabilities;
	
	@OmitCents
	@Required
	@Sum({"totalCurrentLiabilities", "nonCurrentLiabilities"})
	private Money totalLiabilities;
	
	@OmitCents
	@Required
	@Sum(value="totalAssets", negate="totalLiabilities")
	private Money ownersEquity;
	
	@OmitCents
	private Money taxDepreciation;
	
	@OmitCents
	private Money untaxedRealisedGains;
	
	@OmitCents
	private Money additionsToFixedAssets;
	
	@OmitCents
	private Money disposalsOfFixedAssets;
	
	@OmitCents
	private Money dividendsPaid;
	
	@OmitCents
	private Money drawings;
	
	@OmitCents
	private Money currentAccountClosingBalance;

	@OmitCents
	private Money deductibleLossOnDisposal;


	final static Logger logger = LoggerFactory.getLogger(IR10FormPublishedMarch2019.class);

	private int year = 2018;

	@Skip
	private String personalisedNaming;

//	private String calculateMinusSign(Money value) {
//		return value.signum() < 0 ? "-" : "";
//	}

	private Map<String, String> getPropertyToFieldMap() {
		return IR10FieldMapper.getPropertyToFieldMap(year);
	}

	public int getYear() {
		return year;
	}

	public void processField(PDAcroForm acroForm, String fieldName, Object value, Field f) throws IOException {
		PDField pdField = acroForm.getField(fieldName);
		if (pdField == null) {
			logger.error(fieldName + "->" + pdField);
		}
		if (f.getAnnotation(Skip.class) != null) {
			return;
		}
		if (value == null) {
			logger.warn("Null value - may indicate either blank field or issue");
			return;
		}
		if (value instanceof Money) {
			if (f.getAnnotation(OmitCents.class) != null) {
				value = TaxReturnUtils.formatDollarsField((Money) value);
				if (f.getAnnotation(IncludeFormatSpacing.class) != null) {
					String valueText = (String) value;
					if (valueText.length() >= 4) {
						valueText = valueText.substring(0, valueText.length() - 3) + " "
								+ valueText.substring(valueText.length() - 3);
						value = valueText;
					}
				}
			} else {
				value = TaxReturnUtils.formatMoneyField((Money) value);
			}
		}
		if (f.getAnnotation(RightAlign.class) != null) {
			int size = f.getAnnotation(RightAlign.class).value();
			value = StringUtils.leftPad(String.valueOf(value), size);
		}
		if (f.getAnnotation(UseValueMappings.class) != null && pdField instanceof PDNonTerminalField) {
			PDNonTerminalField nonTerminalField = (PDNonTerminalField) pdField;
			nonTerminalField.getChildren().get(Integer.parseInt(String.valueOf(value))).setValue("a");
		} else if (f.getAnnotation(UseValueMappings.class) != null) {
			if (pdField instanceof PDCheckBox) {
				pdField.setValue(String.valueOf(value));
				return;
			}
		}
		if (pdField == null) {
			List<PDField> fields = acroForm.getFields();
			for (PDField field1 : fields) {
				System.out.println("Candidate field: " + field1.getFullyQualifiedName());
			}
			System.out.println("An issue occurred searching for field: " + fieldName);
		}
		pdField.setValue(String.valueOf(value));
	}

	// assumes the forms are in the user's Downloads folder
	public void publishDraft() {
		try {
			File ir10Form = new File(new File("target/classes"), // new File(System.getProperty("user.home"),
																	// "Downloads"),
					String.format("ir10-%1$s.pdf", year));
			PDDocument pdfTemplate = PDDocument.load(ir10Form);

			PDDocumentCatalog docCatalog = pdfTemplate.getDocumentCatalog();
			PDAcroForm acroForm = docCatalog.getAcroForm();
			Map<String, Object> describe = PropertyUtils.describe(this);
			Map<String, String> propertyToFieldMap = this.getPropertyToFieldMap();
			String key = null;
			try {
				for (Map.Entry<String, Object> entry : describe.entrySet()) {
					key = entry.getKey();
					Object value = entry.getValue();
					if ("describeForm".equals(value)) {
						List<PDField> fieldList = acroForm.getFields();

						String[] fieldArray = new String[fieldList.size()];
						int i = 0;
						for (PDField sField : fieldList) {
							fieldArray[i] = sField.getFullyQualifiedName();
							i++;
						}
						for (String f : fieldArray) {
							logger.info("Field name is: " + f);
						}
						throw new AssertionError("Exiting due to issue with fields");
					}
					System.out.println(key + "->" + value);
					if (key.equals("reasonForTaxReturnPartYear")) {
						System.out.println("incomeOtherReceived");
					}
					if (key.equals("class") || key.equals("year")) {
						// todo exclude fields by annotation
						continue;
					}
					Field f = this.getClass().getDeclaredField(key);
					f.setAccessible(true);
					Object field = f.get(this);
					SkipIfFalse annotation = f.getAnnotation(SkipIfFalse.class);
					if (annotation != null) {
						Field declaredField = this.getClass().getDeclaredField(annotation.value());
						declaredField.setAccessible(true);
						if (!(boolean) declaredField.get(this)) {
							continue;
						}
					}
					if (f.getAnnotation(UseChildFields.class) != null) {
						Map<String, Object> describeChild = PropertyUtils.describe(field);
						for (Map.Entry<String, Object> childEntry : describeChild.entrySet()) {
							String childKey = childEntry.getKey();
							if ("class".equals(childKey)) {
								continue;
							}
							Object childValue = childEntry.getValue();
							String fieldName = propertyToFieldMap.get(childKey);
							PDField pdField = acroForm.getField(fieldName);
							System.out.println(fieldName + "->" + pdField);
							pdField.setValue(String.valueOf(childValue));
							System.out.println(fieldName + "->" + pdField);
						}
					} else if (f.getAnnotation(Sum.class) != null) {
						logger.trace("Defer to second pass");				
					} else if (f.getAnnotation(UseDayMonthYear.class) != null) {
							LocalDate localDate = (LocalDate) value;
							if (value == null) {
								// leave the field blank
								continue;
							}
							int dayOfMonth = localDate.getDayOfMonth();
							processField(acroForm, propertyToFieldMap.get(key + "_day"),
									dayOfMonth >= 10 ? dayOfMonth : "0" + dayOfMonth, f);
							int monthValue = localDate.getMonthValue();
							processField(acroForm, propertyToFieldMap.get(key + "_month"),
									monthValue >= 10 ? monthValue : "0" + monthValue, f);
							int year2 = localDate.getYear();
							processField(acroForm, propertyToFieldMap.get(key + "_year"),
									year2 >= 10 ? year2 : "0" + year2, f);
					} else if (f.getAnnotation(UseTrueFalseMappings.class) != null) {
							String mappedValue = (Boolean) value ? propertyToFieldMap.get(key + "_true")
									: propertyToFieldMap.get(key + "_false");
							String fieldName = propertyToFieldMap.get(key);
							if (fieldName == null || mappedValue == null) {
								propertyToFieldMap.entrySet().forEach(action -> logger
										.error(String.format("%s -> %s", action.getKey(), action.getValue())));
								throw new AssertionError(String.format("Boolean field: %s mapped to null, possible "
										+ "cause is missing Enum field in IR10Fields", key));
							}
							processField(acroForm, fieldName, mappedValue, f);
					} else if (f.getAnnotation(UseValueMappings.class) != null) {
							String mappedValue = propertyToFieldMap.get(key + "_" + value);
							processField(acroForm, propertyToFieldMap.get(key), mappedValue, f);
					} else {
							processField(acroForm, propertyToFieldMap.get(key), value, f);					
					}
				}
				// Second pass:		
				int maxPasses = 10;
				for (int i=0;i<maxPasses;i++ ) {
					loopThroughFields:
					for (Map.Entry<String, Object> entry : describe.entrySet()) {
						key = entry.getKey();
						Object value = entry.getValue();
						if (key.equals("class") || key.equals("year")) {
							// todo exclude fields by annotation
							continue;
						}
						System.err.println("key = " + key);
						Field f = this.getClass().getDeclaredField(key);
						f.setAccessible(true);
						Object field = f.get(this);
						if (f.getAnnotation(Sum.class) != null) {
							String[] fields = f.getAnnotation(Sum.class).value();
							String[] negate = f.getAnnotation(Sum.class).negate();
							Money sumMoney = Money.of(BigDecimal.ZERO, "NZD");
							for (String formField : fields) {
								Field f2 = this.getClass().getDeclaredField(formField);
								f2.setAccessible(true);
								Money money = (Money)f2.get(this);
								try {
									sumMoney = sumMoney.add(money == null && f2.getAnnotation(Required.class) == null ? Money.of(BigDecimal.ZERO, "NZD") : money);
								} catch (NullPointerException e) {
									if (i <= (maxPasses-1)) {
										//3 passes required for derived field of derived field
										continue loopThroughFields;
									}
									logger.error("Form field = " + formField);
									logger.error("Form field value= " + money);
									throw e;
								}
							}
							for (String formField : negate) {
								Money money = null;
								Field f2 = this.getClass().getDeclaredField(formField);
								f2.setAccessible(true);
								money = (Money)f2.get(this);
								try {
									sumMoney = sumMoney.subtract(money == null && f2.getAnnotation(Required.class) == null ? Money.of(BigDecimal.ZERO, "NZD") : money);
								} catch (NullPointerException e) {
									if (i <= (maxPasses-1)) {
										//3 passes required for derived field of derived field
										continue loopThroughFields;
									}
									logger.error("Form field = " + formField);
									logger.error("Form field value= " + money);
									throw e;
								}
							}
							f.set(this, sumMoney);
							processField(acroForm, propertyToFieldMap.get(key), sumMoney, f);
						}
					}
					}
			} catch (NullPointerException e) {
				logger.error("Error processing: {}", key);
				throw e;
			}
			File parent = destinationDirectory != null ? new File(destinationDirectory) : new File("target"); // new
																												// File(System.getProperty("user.home"),
																												// "Downloads");
			String lowerCase = this.getFullName().split(" ")[0].toLowerCase();
			lowerCase = personalisedNaming != null ? personalisedNaming : lowerCase;
			File ir10DraftForm = new File(parent, String.format("ir10-%1$s-%2$s-draft.pdf", year, lowerCase));
			// flattening causes fields to disappear
//			acroForm.setNeedAppearances(false);
//			
//			for (PDPage page : pdfTemplate.getPages()) {
//				for (PDAnnotation annot : page.getAnnotations()) {
//					annot.setPage(page);
//				}
//			}
//			
//			// Add the missing resources to the form
//			PDResources dr = new PDResources();		
//			dr.put(COSName.getPDFName("Courier"), PDType1Font.COURIER);
//			dr.put(COSName.getPDFName("Helvetica"), PDType1Font.HELVETICA);
//			
//			acroForm.setDefaultResources(dr);
//			
//			acroForm.flatten();
			acroForm.setXFA(null);
			acroForm.setNeedAppearances(true);
			pdfTemplate.save(ir10DraftForm);
			pdfTemplate.close();
			logger.info("IR10 Form Completed Successfully: " + ir10DraftForm);
		} catch (Exception e) {
			throw new TaxBeansException("Is field in the enum?", e);
		}
	}

	public int getYearEnded() {
		return yearEnded;
	}

	public void setYearEnded(int yearEnded) {
		this.yearEnded = yearEnded;
	}

	public Money getTotalExpenses() {
		return totalExpenses;
	}

	@SuppressWarnings("unused")
	public void setTotalExpenses(Money totalExpenses) {
		if (true) {
			throw new IllegalArgumentException("this is a derived property");
		}
		this.totalExpenses = totalExpenses;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullname(String fullName) {
		this.fullName = fullName;
	}

	public void setIrdNumber(String irdNumber) {
		irdNumber = irdNumber.replace("-", "");
		this.irdNumber = irdNumber;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public boolean isMultipleActivityRadio() {
		return multipleActivityRadio;
	}

	public void setMultipleActivityRadio(boolean multipleActivityRadio) {
		this.multipleActivityRadio = multipleActivityRadio;
	}

	public String getIrdNumber() {
		return irdNumber;
	}

	public Money getGrossIncome() {
		return grossIncome;
	}

	public void setGrossIncome(Money grossIncome) {
		this.grossIncome = grossIncome;
	}

	public Money getOpeningStock() {
		return openingStock;
	}

	public void setOpeningStock(Money openingStock) {
		this.openingStock = openingStock;
	}

	public Money getPurchases() {
		return purchases;
	}

	public void setPurchases(Money purchases) {
		this.purchases = purchases;
	}

	public Money getClosingStock() {
		return closingStock;
	}

	public void setClosingStock(Money closingStock) {
		this.closingStock = closingStock;
	}

	public Money getGrossProfit() {
		return grossProfit;
	}

	public void setGrossProfit(Money grossProfit) {
		this.grossProfit = grossProfit;
	}

	public String getDestinationDirectory() {
		return destinationDirectory;
	}

	public void setDestinationDirectory(String destinationDirectory) {
		this.destinationDirectory = destinationDirectory;
	}

	public Money getInterestReceived() {
		return interestReceived;
	}

	public void setInterestReceived(Money interestReceived) {
		this.interestReceived = interestReceived;
	}

	public Money getDividends() {
		return dividends;
	}

	public void setDividends(Money dividends) {
		this.dividends = dividends;
	}

	public Money getLeasePayments() {
		return leasePayments;
	}

	public void setLeasePayments(Money leasePayments) {
		this.leasePayments = leasePayments;
	}

	public Money getOtherIncome() {
		return otherIncome;
	}

	public void setOtherIncome(Money otherIncome) {
		this.otherIncome = otherIncome;
	}

	public Money getTotalIncome() {
		return totalIncome;
	}

	@SuppressWarnings("unused")
	public void setTotalIncome(Money totalIncome) {
		if (true) {
			throw new IllegalArgumentException("this is a derived property");
		}
		this.totalIncome = totalIncome;
	}

	public Money getBadDebts() {
		return badDebts;
	}

	public void setBadDebts(Money badDebts) {
		this.badDebts = badDebts;
	}

	public Money getDepreciation() {
		return depreciation;
	}

	public void setDepreciation(Money depreciation) {
		this.depreciation = depreciation;
	}

	public String getPersonalisedNaming() {
		return personalisedNaming;
	}

	public void setPersonalisedNaming(String personalisedNaming) {
		this.personalisedNaming = personalisedNaming;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Money getInsurance() {
		return insurance;
	}

	public void setInsurance(Money insurance) {
		this.insurance = insurance;
	}

	public Money getInterestExpenses() {
		return interestExpenses;
	}

	public void setInterestExpenses(Money interestExpenses) {
		this.interestExpenses = interestExpenses;
	}

	public Money getConsultingFees() {
		return consultingFees;
	}

	public void setConsultingFees(Money consultingFees) {
		this.consultingFees = consultingFees;
	}

	public Money getRates() {
		return rates;
	}

	public void setRates(Money rates) {
		this.rates = rates;
	}

	public Money getLeasePaymentExpenses() {
		return leasePaymentExpenses;
	}

	public void setLeasePaymentExpenses(Money leasePaymentExpenses) {
		this.leasePaymentExpenses = leasePaymentExpenses;
	}

	public Money getRepairs() {
		return repairs;
	}

	public void setRepairs(Money repairs) {
		this.repairs = repairs;
	}

	public Money getResearchAndDevelopment() {
		return researchAndDevelopment;
	}

	public void setResearchAndDevelopment(Money researchAndDevelopment) {
		this.researchAndDevelopment = researchAndDevelopment;
	}

	public Money getRelatedPartyRenumeration() {
		return relatedPartyRenumeration;
	}

	public void setRelatedPartyRenumeration(Money relatedPartyRenumeration) {
		this.relatedPartyRenumeration = relatedPartyRenumeration;
	}

	public Money getSalaryAndWages() {
		return salaryAndWages;
	}

	public void setSalaryAndWages(Money salaryAndWages) {
		this.salaryAndWages = salaryAndWages;
	}

	public Money getSubcontractorPayments() {
		return subcontractorPayments;
	}

	public void setSubcontractorPayments(Money subcontractorPayments) {
		this.subcontractorPayments = subcontractorPayments;
	}

	public Money getOtherExpenses() {
		return otherExpenses;
	}

	public void setOtherExpenses(Money otherExpenses) {
		this.otherExpenses = otherExpenses;
	}

	public Money getExceptionalItems() {
		return exceptionalItems;
	}

	public void setExceptionalItems(Money exceptionalItems) {
		this.exceptionalItems = exceptionalItems;
	}

	public Money getNetProfitBeforeTax() {
		return netProfitBeforeTax;
	}

	@SuppressWarnings("unused")
	public void setNetProfitBeforeTax(Money netProfitBeforeTax) {
		if (true) {
			throw new IllegalArgumentException("this is a derived property");
		}
		this.netProfitBeforeTax = netProfitBeforeTax;
	}

	public Money getTaxAdjustments() {
		return taxAdjustments;
	}

	public void setTaxAdjustments(Money taxAdjustments) {
		this.taxAdjustments = taxAdjustments;
	}

	public Money getTaxableProfit() {
		return taxableProfit;
	}

	@SuppressWarnings("unused")
	public void setTaxableProfit(Money taxableProfit) {
		if (true) {
			throw new IllegalArgumentException("this is a derived property");
		}
		this.taxableProfit = taxableProfit;
	}

	public Money getAccountsReceivable() {
		return accountsReceivable;
	}

	public void setAccountsReceivable(Money accountsReceivable) {
		this.accountsReceivable = accountsReceivable;
	}

	public Money getOtherCurrentAssets() {
		return otherCurrentAssets;
	}

	public void setOtherCurrentAssets(Money otherCurrentAssets) {
		this.otherCurrentAssets = otherCurrentAssets;
	}

	public Money getVehicleAssets() {
		return vehicleAssets;
	}

	public void setVehicleAssets(Money vehicleAssets) {
		this.vehicleAssets = vehicleAssets;
	}

	public Money getPlantAssets() {
		return plantAssets;
	}

	public void setPlantAssets(Money plantAssets) {
		this.plantAssets = plantAssets;
	}

	public Money getFurnitureAssets() {
		return furnitureAssets;
	}

	public void setFurnitureAssets(Money furnitureAssets) {
		this.furnitureAssets = furnitureAssets;
	}

	public Money getCashAndDeposits() {
		return cashAndDeposits;
	}

	public void setCashAndDeposits(Money cashAndDeposits) {
		this.cashAndDeposits = cashAndDeposits;
	}

	public Money getLand() {
		return land;
	}

	public void setLand(Money land) {
		this.land = land;
	}

	public Money getBuildings() {
		return buildings;
	}

	public void setBuildings(Money buildings) {
		this.buildings = buildings;
	}

	public Money getOtherFixedAssets() {
		return otherFixedAssets;
	}

	public void setOtherFixedAssets(Money otherFixedAssets) {
		this.otherFixedAssets = otherFixedAssets;
	}

	public Money getIntangibles() {
		return intangibles;
	}

	public void setIntangibles(Money intangibles) {
		this.intangibles = intangibles;
	}

	public Money getSharesAndDebentures() {
		return sharesAndDebentures;
	}

	public void setSharesAndDebentures(Money sharesAndDebentures) {
		this.sharesAndDebentures = sharesAndDebentures;
	}

	public Money getTermDeposits() {
		return termDeposits;
	}

	public void setTermDeposits(Money termDeposits) {
		this.termDeposits = termDeposits;
	}

	public Money getOtherNonCurrentAssets() {
		return otherNonCurrentAssets;
	}

	public void setOtherNonCurrentAssets(Money otherNonCurrentAssets) {
		this.otherNonCurrentAssets = otherNonCurrentAssets;
	}

	public Money getTotalAssets() {
		return totalAssets;
	}

	public void setTotalAssets(Money totalAssets) {
		this.totalAssets = totalAssets;
	}

	public Money getProvisions() {
		return provisions;
	}

	public void setProvisions(Money provisions) {
		this.provisions = provisions;
	}

	public Money getAccountsPayable() {
		return accountsPayable;
	}

	public void setAccountsPayable(Money accountsPayable) {
		this.accountsPayable = accountsPayable;
	}

	public Money getCurrentLoans() {
		return currentLoans;
	}

	public void setCurrentLoans(Money currentLoans) {
		this.currentLoans = currentLoans;
	}

	public Money getOtherCurrentLiabilities() {
		return otherCurrentLiabilities;
	}

	public void setOtherCurrentLiabilities(Money otherCurrentLiabilities) {
		this.otherCurrentLiabilities = otherCurrentLiabilities;
	}

	public Money getTotalCurrentLiabilities() {
		return totalCurrentLiabilities;
	}

	@SuppressWarnings("unused")
	public void setTotalCurrentLiabilities(Money totalCurrentLiabilities) {
		if (true) {
			throw new IllegalArgumentException("this is a derived property");
		}
		this.totalCurrentLiabilities = totalCurrentLiabilities;
	}

	public Money getNonCurrentLiabilities() {
		return nonCurrentLiabilities;
	}

	public void setNonCurrentLiabilities(Money nonCurrentLiabilities) {
		this.nonCurrentLiabilities = nonCurrentLiabilities;
	}

	public Money getTotalLiabilities() {
		return totalLiabilities;
	}

	@SuppressWarnings("unused")
	public void setTotalLiabilities(Money totalLiabilities) {
		if (true) {
			throw new IllegalArgumentException("this is a derived property");
		}
		this.totalLiabilities = totalLiabilities;
	}

	public Money getOwnersEquity() {
		return ownersEquity;
	}

	@SuppressWarnings("unused")
	public void setOwnersEquity(Money ownersEquity) {
		if (true) {
			throw new IllegalArgumentException("this is a derived property");
		}
		this.ownersEquity = ownersEquity;
	}

	public Money getTaxDepreciation() {
		return taxDepreciation;
	}

	public void setTaxDepreciation(Money taxDepreciation) {
		this.taxDepreciation = taxDepreciation;
	}

	public Money getUntaxedRealisedGains() {
		return untaxedRealisedGains;
	}

	public void setUntaxedRealisedGains(Money untaxedRealisedGains) {
		this.untaxedRealisedGains = untaxedRealisedGains;
	}

	public Money getAdditionsToFixedAssets() {
		return additionsToFixedAssets;
	}

	public void setAdditionsToFixedAssets(Money additionsToFixedAssets) {
		this.additionsToFixedAssets = additionsToFixedAssets;
	}

	public Money getDisposalsOfFixedAssets() {
		return disposalsOfFixedAssets;
	}

	public void setDisposalsOfFixedAssets(Money disposalsOfFixedAssets) {
		this.disposalsOfFixedAssets = disposalsOfFixedAssets;
	}

	public Money getDividendsPaid() {
		return dividendsPaid;
	}

	public void setDividendsPaid(Money dividendsPaid) {
		this.dividendsPaid = dividendsPaid;
	}

	public Money getDrawings() {
		return drawings;
	}

	public void setDrawings(Money drawings) {
		this.drawings = drawings;
	}

	public Money getCurrentAccountClosingBalance() {
		return currentAccountClosingBalance;
	}

	public void setCurrentAccountClosingBalance(Money currentAccountClosingBalance) {
		this.currentAccountClosingBalance = currentAccountClosingBalance;
	}

	public Money getDeductibleLossOnDisposal() {
		return deductibleLossOnDisposal;
	}

	public void setDeductibleLossOnDisposal(Money deductibleLossOnDisposal) {
		this.deductibleLossOnDisposal = deductibleLossOnDisposal;
	}
}
