package com.github.taxbeans.forms.common;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
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
import com.github.taxbeans.forms.RoundedSum;
import com.github.taxbeans.forms.Unbounded;
import com.github.taxbeans.forms.IncludeFormatSpacing;
import com.github.taxbeans.forms.LeftAlign;
import com.github.taxbeans.forms.OmitCents;
import com.github.taxbeans.forms.Percent2DecimalPlaces;
import com.github.taxbeans.forms.Required;
import com.github.taxbeans.forms.RightAlign;
import com.github.taxbeans.forms.RoundToDollars;
import com.github.taxbeans.forms.Skip;
import com.github.taxbeans.forms.SkipIfFalse;
import com.github.taxbeans.forms.Sum;
import com.github.taxbeans.forms.UseChildFields;
import com.github.taxbeans.forms.UseDayMonthYear;
import com.github.taxbeans.forms.UseTrueFalseMappings;
import com.github.taxbeans.forms.UseValueMappings;
import com.github.taxbeans.forms.utils.TaxReturnUtils;

public class FormProcessor {

	final static Logger LOG = LoggerFactory.getLogger(FormProcessor.class);

	private static String fileName;

	private static String csvMappingFileName;

	private static String key;

	public static void processField(PDAcroForm acroForm, String fieldName, Object value, Field f) throws IOException {
		PDField pdField = acroForm.getField(fieldName);
		if (pdField == null) {
			LOG.error(fieldName + "->" + pdField);
		}
		if (f.getAnnotation(Skip.class) != null) {
			return;
		}
		if (value == null) {
			LOG.warn("Null value - may indicate either blank field or issue");
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
			} else if (f.getAnnotation(RoundToDollars.class) != null) {
				value = TaxReturnUtils.formatDollarsFieldRounded((Money) value);
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
		} else if (f.getAnnotation(Percent2DecimalPlaces.class) != null) {
			BigDecimal bigDecimal = (BigDecimal) value;
			bigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_UP);
			DecimalFormat df = new DecimalFormat();
			df.setMaximumFractionDigits(2);
			df.setMinimumFractionDigits(2);
			df.setGroupingUsed(false);
			value = df.format(bigDecimal).replace(".", "");
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
				LOG.warn("Candidate field: " + field1.getFullyQualifiedName());
			}
			String issue = String.format("An issue occurred searching for %s (%s) in the PDF (acroForm) named: %s",
					fieldName, key, fileName);
			LOG.warn(issue);
			LOG.warn("Perhaps field name not in enum");
			LOG.warn("Perhaps field in bean but not in mapping CSV: {}", csvMappingFileName);
			if (f.getAnnotation(Unbounded.class) == null) {
				throw new TaxBeansException(issue);
			}
		}
		if (f.getAnnotation(Unbounded.class) == null) {
			pdField.setValue(String.valueOf(value));
		}
	}

	public static void publishDraft(Object pojo, int year, String fileNameTemplate,
			Map<String, String> propertyToFieldMap, String fullName, String outputFormat) {
		try {
			fileName = String.format(fileNameTemplate, year);
			File ir7Form = new File(new File("target/classes"), // new File(System.getProperty("user.home"),
																// "Downloads"),
					fileName); // "ir7-%1$s.pdf", year));
			PDDocument pdfTemplate = PDDocument.load(ir7Form);

			PDDocumentCatalog docCatalog = pdfTemplate.getDocumentCatalog();
			PDAcroForm acroForm = docCatalog.getAcroForm();
			Map<String, Object> describe = PropertyUtils.describe(pojo);
			// Map<String, String> propertyToFieldMap = pojo.getPropertyToFieldMap();
			key = null;
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
							LOG.info("Field name is: " + f);
						}
						throw new AssertionError("Exiting due to issue with fields");
					}
					LOG.debug(key + "->" + value);
					if (key.equals("reasonForTaxReturnPartYear")) {
						System.out.println("incomeOtherReceived");
					}
					if (key.equals("class") || key.equals("year")) {
						// todo exclude fields by annotation
						continue;
					}
					Field f = pojo.getClass().getDeclaredField(key);
					f.setAccessible(true);
					Object field = f.get(pojo);
					SkipIfFalse annotation = f.getAnnotation(SkipIfFalse.class);
					if (annotation != null) {
						Field declaredField = pojo.getClass().getDeclaredField(annotation.value());
						declaredField.setAccessible(true);
						if (!(boolean) declaredField.get(pojo)) {
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
						LOG.trace("Defer to second pass");
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
						processField(acroForm, propertyToFieldMap.get(key + "_year"), year2 >= 10 ? year2 : "0" + year2,
								f);
					} else if (f.getAnnotation(UseTrueFalseMappings.class) != null) {
						String mappedValue = (Boolean) value ? propertyToFieldMap.get(key + "_true")
								: propertyToFieldMap.get(key + "_false");
						String fieldName = propertyToFieldMap.get(key);
						if (fieldName == null || mappedValue == null) {
							propertyToFieldMap.entrySet().forEach(
									action -> LOG.error(String.format("%s -> %s", action.getKey(), action.getValue())));
							throw new AssertionError(String.format("Boolean field: %s mapped to null, possible "
									+ "cause is missing Enum field (or enum true and false suffixes) in IR7Fields",
									key));
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
				for (int i = 0; i < maxPasses; i++) {
					loopThroughFields: for (Map.Entry<String, Object> entry : describe.entrySet()) {
						key = entry.getKey();
						// Object value = entry.getValue();
						if (key.equals("class") || key.equals("year")) {
							// todo exclude fields by annotation
							continue;
						}
						System.err.println("key = " + key);
						Field f = pojo.getClass().getDeclaredField(key);
						f.setAccessible(true);
						// Object field = f.get(pojo);
						String[] fields = null;
						String[] negate = null;
						if (f.getAnnotation(Sum.class) != null || f.getAnnotation(RoundedSum.class) != null) {
							boolean round = false;
							Sum sumAnnotation = f.getAnnotation(Sum.class);
							if (sumAnnotation == null) {
								round = true;
								RoundedSum roundSumAnnotation = f.getAnnotation(RoundedSum.class);
								fields = roundSumAnnotation.value();
								negate = roundSumAnnotation.negate();
							} else {
								fields = sumAnnotation.value();
								negate = sumAnnotation.negate();
							}
							Money sumMoney = Money.of(BigDecimal.ZERO, "NZD");
							for (String formField : fields) {
								Field f2 = pojo.getClass().getDeclaredField(formField);
								f2.setAccessible(true);
								Money money = (Money) f2.get(pojo);
								try {
									if (money == null && f2.getAnnotation(Required.class) == null) {
										money = Money.of(BigDecimal.ZERO, "NZD");
									} else {
										if (round) {
											BigDecimal roundBigDecimal = money.getNumberStripped().setScale(0,
													RoundingMode.HALF_UP);
											money = Money.of(roundBigDecimal, money.getCurrency().getCurrencyCode());
										}
									}
									sumMoney = sumMoney.add(money);
								} catch (NullPointerException e) {
									if (i <= (maxPasses - 1)) {
										// 3 passes required for derived field of derived field
										continue loopThroughFields;
									}
									LOG.error("Form field = " + formField);
									LOG.error("Form field value= " + money);
									throw e;
								}
							}
							for (String formField : negate) {
								Money money = null;
								Field f2 = pojo.getClass().getDeclaredField(formField);
								f2.setAccessible(true);
								money = (Money) f2.get(pojo);
								try {
									if (money == null && f2.getAnnotation(Required.class) == null) {
										money = Money.of(BigDecimal.ZERO, "NZD");
									} else {

										money = round
												? Money.of(money.getNumberStripped().setScale(0, RoundingMode.FLOOR),
														money.getCurrency().getCurrencyCode())
												: money;
									}
									sumMoney = sumMoney.subtract(money);
								} catch (NullPointerException e) {
									if (i <= (maxPasses - 1)) {
										// 3 passes required for derived field of derived field
										continue loopThroughFields;
									}
									LOG.error("Form field = " + formField);
									LOG.error("Form field value= " + money);
									throw e;
								}
							}
							f.set(pojo, sumMoney);
							processField(acroForm, propertyToFieldMap.get(key), sumMoney, f);
						}
					}
				}
			} catch (NullPointerException | IllegalArgumentException e) {
				LOG.error("Error processing: {}", key);
				throw e;
			}
			String destinationDirectory = ((FormDestination) pojo).getDestinationDirectory();
			File parent = destinationDirectory != null ? new File(destinationDirectory) : new File("target");
			String lowerCase = fullName.split(" ")[0].toLowerCase();

			String personalisedNaming = ((FormDestination) pojo).getDestinationDirectory();
			lowerCase = personalisedNaming != null ? personalisedNaming : lowerCase;
			File ir7DraftForm = new File(parent, String.format(outputFormat, year, lowerCase));
			acroForm.setXFA(null);
			acroForm.setNeedAppearances(true);
			pdfTemplate.save(ir7DraftForm);
			pdfTemplate.close();
			LOG.info("Form Completed Successfully: " + ir7DraftForm);
		} catch (Exception e) {
			throw new TaxBeansException("Is field in the enum?", e);
		}
	}

	public static void setCsvMappingFileName(String csvMappingFileName) {
		FormProcessor.csvMappingFileName = csvMappingFileName;
	}

}
