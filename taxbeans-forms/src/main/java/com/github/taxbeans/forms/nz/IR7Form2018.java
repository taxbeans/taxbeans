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
import com.github.taxbeans.forms.LeftAlign;
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

public class IR7Form2018 {
	
	@Skip
	private int yearEnded;

	@Skip
	private String fullName;

	@Skip
	private String destinationDirectory;

	@RightAlign(9)
	private String irdNumber;

	final static Logger logger = LoggerFactory.getLogger(IR7Form2018.class);

	private int year = 2018;

	@Skip
	private String personalisedNaming;
	
	private String partnershipNameLine1;
	
	private String partnershipNameLine2;
	
	private String partnershipTradingNameLine1;
	
	private String partnershipTradingNameLine2;
	
	private String postalAddressLine1;
	
	private String postalAddressLine2;
	
	private String physicalAddressLine1;
	
	private String physicalAddressLine2;
	
	private String bicCode;
	
	private String daytimePhoneNumberPrefix;
	
	@LeftAlign(9)
	private String daytimePhoneNumberSuffix;
	
	@UseTrueFalseMappings
	private boolean firstReturnRadio;
	
	@UseTrueFalseMappings
	private boolean partnershipCeasedRadio;
	
	@UseTrueFalseMappings
	private boolean schedularPaymentsRadio;

	@UseTrueFalseMappings
	private boolean nzInterestRadio;
	
	@UseTrueFalseMappings
	private boolean dividendsRadio;
	
	@UseTrueFalseMappings
	private boolean maoriTaxableDistributions;
	
	@UseTrueFalseMappings
	private boolean incomeFromAnotherPartnership;
	
	@UseTrueFalseMappings
	private boolean incomeFromAnotherLTC;
	
	@UseTrueFalseMappings
	private boolean overseasIncome;
	
	@UseTrueFalseMappings
	private boolean businessIncome;
	
	@UseTrueFalseMappings
	private boolean rentalIncomeRadio;
	
	@UseTrueFalseMappings
	private boolean otherIncomeRadio;
	
	@UseTrueFalseMappings
	private boolean expenseClaimRadio;

	
	@UseTrueFalseMappings
	private boolean partnershipOrLTCRadio;
	
	@UseTrueFalseMappings
	private boolean partnershipCFCRadio;
	
	@UseTrueFalseMappings
	private boolean laqcTransitionRadio;
	
	@RightAlign(11)
	private Money totalIncome;
	
	@RightAlign(11)
	private Money totalIncomeAfterExpenses;

//	private String calculateMinusSign(Money value) {
//		return value.signum() < 0 ? "-" : "";
//	}

	private Map<String, String> getPropertyToFieldMap() {
		return IR7FieldMapper.getPropertyToFieldMap(year);
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
		} else if (f.getAnnotation(LeftAlign.class) != null) {
			int size = f.getAnnotation(LeftAlign.class).value();
			value = StringUtils.rightPad(String.valueOf(value), size);
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
			File ir7Form = new File(new File("target/classes"), // new File(System.getProperty("user.home"),
																	// "Downloads"),
					String.format("ir7-%1$s.pdf", year));
			PDDocument pdfTemplate = PDDocument.load(ir7Form);

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
										+ "cause is missing Enum field (or enum true and false suffixes) in IR7Fields", key));
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
			} catch (NullPointerException | IllegalArgumentException e) {
				logger.error("Error processing: {}", key);
				throw e;
			}
			File parent = destinationDirectory != null ? new File(destinationDirectory) : new File("target"); // new
																												// File(System.getProperty("user.home"),
																												// "Downloads");
			String lowerCase = this.getFullName().split(" ")[0].toLowerCase();
			lowerCase = personalisedNaming != null ? personalisedNaming : lowerCase;
			File ir7DraftForm = new File(parent, String.format("ir7-%1$s-%2$s-draft.pdf", year, lowerCase));
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
			pdfTemplate.save(ir7DraftForm);
			pdfTemplate.close();
			logger.info("IR7 Form Completed Successfully: " + ir7DraftForm);
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

	public String getIrdNumber() {
		return irdNumber;
	}

	public String getDestinationDirectory() {
		return destinationDirectory;
	}

	public void setDestinationDirectory(String destinationDirectory) {
		this.destinationDirectory = destinationDirectory;
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

	public String getPartnershipNameLine1() {
		return partnershipNameLine1;
	}

	public void setPartnershipNameLine1(String partnershipNameLine1) {
		this.partnershipNameLine1 = partnershipNameLine1;
	}

	public String getPartnershipNameLine2() {
		return partnershipNameLine2;
	}

	public void setPartnershipNameLine2(String partnershipNameLine2) {
		this.partnershipNameLine2 = partnershipNameLine2;
	}

	public String getPartnershipTradingNameLine1() {
		return partnershipTradingNameLine1;
	}

	public void setPartnershipTradingNameLine1(String partnershipTradingNameLine1) {
		this.partnershipTradingNameLine1 = partnershipTradingNameLine1;
	}

	public String getPartnershipTradingNameLine2() {
		return partnershipTradingNameLine2;
	}

	public void setPartnershipTradingNameLine2(String partnershipTradingNameLine2) {
		this.partnershipTradingNameLine2 = partnershipTradingNameLine2;
	}

	public String getPostalAddressLine1() {
		return postalAddressLine1;
	}

	public void setPostalAddressLine1(String postalAddressLine1) {
		this.postalAddressLine1 = postalAddressLine1;
	}

	public String getPostalAddressLine2() {
		return postalAddressLine2;
	}

	public void setPostalAddressLine2(String postalAddressLine2) {
		this.postalAddressLine2 = postalAddressLine2;
	}

	public String getPhysicalAddressLine2() {
		return physicalAddressLine2;
	}

	public void setPhysicalAddressLine2(String physicalAddressLine2) {
		this.physicalAddressLine2 = physicalAddressLine2;
	}

	public String getPhysicalAddressLine1() {
		return physicalAddressLine1;
	}

	public void setPhysicalAddressLine1(String physicalAddressLine1) {
		this.physicalAddressLine1 = physicalAddressLine1;
	}

	public String getBicCode() {
		return bicCode;
	}

	public void setBicCode(String bicCode) {
		this.bicCode = bicCode;
	}

	public String getDaytimePhoneNumberPrefix() {
		return daytimePhoneNumberPrefix;
	}

	public void setDaytimePhoneNumberPrefix(String daytimePhoneNumberPrefix) {
		this.daytimePhoneNumberPrefix = daytimePhoneNumberPrefix;
	}

	public String getDaytimePhoneNumberSuffix() {
		return daytimePhoneNumberSuffix;
	}

	public void setDaytimePhoneNumberSuffix(String daytimePhoneNumberSuffix) {
		this.daytimePhoneNumberSuffix = daytimePhoneNumberSuffix;
	}

	public boolean isFirstReturnRadio() {
		return firstReturnRadio;
	}

	public void setFirstReturnRadio(boolean firstReturnRadio) {
		this.firstReturnRadio = firstReturnRadio;
	}

	public boolean isPartnershipCeasedRadio() {
		return partnershipCeasedRadio;
	}

	public void setPartnershipCeasedRadio(boolean partnershipCeasedRadio) {
		this.partnershipCeasedRadio = partnershipCeasedRadio;
	}

	public boolean isSchedularPaymentsRadio() {
		return schedularPaymentsRadio;
	}

	public void setSchedularPaymentsRadio(boolean schedularPaymentsRadio) {
		this.schedularPaymentsRadio = schedularPaymentsRadio;
	}

	public boolean isNzInterestRadio() {
		return nzInterestRadio;
	}

	public void setNzInterestRadio(boolean nzInterestRadio) {
		this.nzInterestRadio = nzInterestRadio;
	}

	public boolean isDividendsRadio() {
		return dividendsRadio;
	}

	public void setDividendsRadio(boolean dividendsRadio) {
		this.dividendsRadio = dividendsRadio;
	}

	public boolean isMaoriTaxableDistributions() {
		return maoriTaxableDistributions;
	}

	public void setMaoriTaxableDistributions(boolean maoriTaxableDistributions) {
		this.maoriTaxableDistributions = maoriTaxableDistributions;
	}

	public boolean isIncomeFromAnotherPartnership() {
		return incomeFromAnotherPartnership;
	}

	public void setIncomeFromAnotherPartnership(boolean incomeFromAnotherPartnership) {
		this.incomeFromAnotherPartnership = incomeFromAnotherPartnership;
	}

	public boolean isIncomeFromAnotherLTC() {
		return incomeFromAnotherLTC;
	}

	public void setIncomeFromAnotherLTC(boolean incomeFromAnotherLTC) {
		this.incomeFromAnotherLTC = incomeFromAnotherLTC;
	}

	public boolean isBusinessIncome() {
		return businessIncome;
	}

	public void setBusinessIncome(boolean businessIncome) {
		this.businessIncome = businessIncome;
	}

	public boolean isOverseasIncome() {
		return overseasIncome;
	}

	public void setOverseasIncome(boolean overseasIncome) {
		this.overseasIncome = overseasIncome;
	}

	public boolean isRentalIncomeRadio() {
		return rentalIncomeRadio;
	}

	public void setRentalIncomeRadio(boolean rentalIncomeRadio) {
		this.rentalIncomeRadio = rentalIncomeRadio;
	}

	public boolean isOtherIncomeRadio() {
		return otherIncomeRadio;
	}

	public void setOtherIncomeRadio(boolean otherIncomeRadio) {
		this.otherIncomeRadio = otherIncomeRadio;
	}

	public boolean isExpenseClaimRadio() {
		return expenseClaimRadio;
	}

	public void setExpenseClaimRadio(boolean expenseClaimRadio) {
		this.expenseClaimRadio = expenseClaimRadio;
	}

	public boolean isPartnershipOrLTCRadio() {
		return partnershipOrLTCRadio;
	}

	/**
	 * true means it's a partnership
	 */
	public void setPartnershipOrLTCRadio(boolean partnershipOrLTCRadio) {
		this.partnershipOrLTCRadio = partnershipOrLTCRadio;
	}

	public boolean isPartnershipCFCRadio() {
		return partnershipCFCRadio;
	}

	public void setPartnershipCFCRadio(boolean partnershipCFCRadio) {
		this.partnershipCFCRadio = partnershipCFCRadio;
	}

	public boolean isLaqcTransitionRadio() {
		return laqcTransitionRadio;
	}

	public void setLaqcTransitionRadio(boolean laqcTransitionRadio) {
		this.laqcTransitionRadio = laqcTransitionRadio;
	}

	public Money getTotalIncome() {
		return totalIncome;
	}

	public void setTotalIncome(Money totalIncome) {
		this.totalIncome = totalIncome;
	}

	public Money getTotalIncomeAfterExpenses() {
		return totalIncomeAfterExpenses;
	}

	public void setTotalIncomeAfterExpenses(Money totalIncomeAfterExpenses) {
		this.totalIncomeAfterExpenses = totalIncomeAfterExpenses;
	}

}
