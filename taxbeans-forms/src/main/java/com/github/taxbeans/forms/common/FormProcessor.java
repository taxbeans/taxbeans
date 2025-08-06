package com.github.taxbeans.forms.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDButton;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDNonTerminalField;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.taxbeans.exception.TaxBeansException;
import com.github.taxbeans.forms.AutoMinusField;
import com.github.taxbeans.forms.IncludeFormatSpacing;
import com.github.taxbeans.forms.LeftAlign;
import com.github.taxbeans.forms.OmitCents;
import com.github.taxbeans.forms.Percent2DecimalPlaces;
import com.github.taxbeans.forms.RelativeFieldName;
import com.github.taxbeans.forms.Required;
import com.github.taxbeans.forms.RightAlign;
import com.github.taxbeans.forms.RoundToDollars;
import com.github.taxbeans.forms.RoundedSum;
import com.github.taxbeans.forms.Skip;
import com.github.taxbeans.forms.SkipIfFalse;
import com.github.taxbeans.forms.Sum;
import com.github.taxbeans.forms.Unbounded;
import com.github.taxbeans.forms.UseChildFields;
import com.github.taxbeans.forms.UseDayMonthYear;
import com.github.taxbeans.forms.UseSeparateYesNoCheckboxes;
import com.github.taxbeans.forms.UseTrueFalseMappings;
import com.github.taxbeans.forms.UseValueMappings;
import com.github.taxbeans.forms.nz.IRFieldMapKey;
import com.github.taxbeans.forms.nz.IRFieldMapper;
import com.github.taxbeans.forms.utils.TaxReturnUtils;

public class FormProcessor {

	final static Logger LOG = LoggerFactory.getLogger(FormProcessor.class);

	private static String fileName;

	private static String csvMappingFileName;

	private static String key;

	public static void processField(PDAcroForm acroForm, String fieldName, Object value, Field field) throws IOException {
		PDField pdfField = acroForm.getField(fieldName);
		if (pdfField == null) {
			LOG.error(fieldName + "->" + pdfField);
		}
		if (field.getAnnotation(Skip.class) != null) {
			return;
		}
		if (value == null) {
			LOG.warn("Null value - may indicate either blank field or issue");
			return;
		}
		boolean isNegativeMoney = false;
		String overrideFieldName = "";
		if (value instanceof Money) {
			if (((Money) value).isNegative()) {
				isNegativeMoney = true;
			}
			if (field.getAnnotation(OmitCents.class) != null) {
				value = TaxReturnUtils.formatDollarsField((Money) value);
				if (field.getAnnotation(IncludeFormatSpacing.class) != null) {
					String valueText = (String) value;
					if (valueText.length() >= 4) {
						valueText = valueText.substring(0, valueText.length() - 3) + " "
								+ valueText.substring(valueText.length() - 3);
						value = valueText;
						//PDTextField pdfTextField = (PDTextField) pdfField;
						//Also, I found this:Cour -> Courier CoBo -> Courier-Bold CoOb -> Courier-Oblique CoBO ->
						//Courier-BoldOblique Helv -> Helvetica HeBo -> Helvetica-Bold HeOb -> Helvetica-Oblique HeBO ->
						//Helvetica-BoldOblique Symb -> Symbol TiRo -> Times-Roman TiBo -> Times-Bold TiIt -> Times-Italic TiBI ->
						//Times-BoldItalic ZaDb -> ZapfDingbats. I used HeBo and it worked fine to bold the font
						//(instead of /Helv I used /HeBo – user972391 May 16 '15 at 4:45
						//pdfTextField.setDefaultAppearance("/HeBo 14 Tf 0 g");
						//COSDictionary cosObject = pdfTextField.getCOSObject();
						//cosObject.
						//pdfTextField.setDefaultStyleString(defaultStyleString);
					}

				}
			} else if (field.getAnnotation(RoundToDollars.class) != null) {
				value = TaxReturnUtils.formatDollarsFieldRounded((Money) value);
				if (field.getAnnotation(IncludeFormatSpacing.class) != null) {
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
		if (field.getAnnotation(RightAlign.class) != null) {
			if (field.getName().equals("overseasTaxPaid")) {
				LOG.trace("Example of where to place a breakpoint for debugging purposes");
			}
			RightAlign annotation = field.getAnnotation(RightAlign.class);
			int size = annotation.value();
			value = StringUtils.leftPad(String.valueOf(value), size);
			overrideFieldName = annotation.fieldName();
			if (!"".equals(overrideFieldName)) {
				pdfField = acroForm.getField(overrideFieldName);
				if (pdfField == null) {
					String s = String.format("Annotation set for field: %s -> %s, but not in form metadata", field.getName(), overrideFieldName);
					LOG.error(s);
					throw new IllegalStateException(s);
				}
			}
		} else if (field.getAnnotation(LeftAlign.class) != null) {
			int size = field.getAnnotation(LeftAlign.class).value();
			value = StringUtils.rightPad(String.valueOf(value), size);
		} else if (field.getAnnotation(Percent2DecimalPlaces.class) != null) {
			BigDecimal bigDecimal = (BigDecimal) value;
			bigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_UP);
			DecimalFormat df = new DecimalFormat();
			df.setMaximumFractionDigits(2);
			df.setMinimumFractionDigits(2);
			df.setGroupingUsed(false);
			value = df.format(bigDecimal).replace(".", "");
		}
		if (field.getAnnotation(UseValueMappings.class) != null && pdfField instanceof PDNonTerminalField) {
			PDNonTerminalField nonTerminalField = (PDNonTerminalField) pdfField;
			nonTerminalField.getChildren().get(Integer.parseInt(String.valueOf(value))).setValue("a");
		} else if (field.getAnnotation(UseValueMappings.class) != null) {
			if (pdfField instanceof PDCheckBox) {
				pdfField.setValue(String.valueOf(value));
				return;
			}
		}
		if (pdfField == null) {
			List<PDField> fields = acroForm.getFields();
			for (PDField field1 : fields) {
				LOG.warn("Candidate field: " + field1.getFullyQualifiedName());
			}
			String issue = String.format("An issue occurred searching for %s (%s) in the PDF (acroForm) named: %s",
					fieldName, key, fileName);
			LOG.warn(issue);
			LOG.warn("Perhaps field name not in enum");
			LOG.warn("Perhaps field in bean but not in mapping CSV: {}", csvMappingFileName);
			if (field.getAnnotation(Unbounded.class) == null) {
				throw new TaxBeansException(issue);
			}
		}
		if (field.getAnnotation(Unbounded.class) == null) {
			try {
				LOG.debug(String.format("Setting PDF Field for %s: %s to %s", field.getName(), pdfField.getFullyQualifiedName(), value));
				if (!"".equals(overrideFieldName)) {
					pdfField = acroForm.getField(overrideFieldName);
				}
				pdfField.setValue(String.valueOf(value));
				if (isNegativeMoney) {
					AutoMinusField annotation = field.getAnnotation(AutoMinusField.class);
					if (annotation != null) {
						String fieldName2 = annotation.fieldName();
						acroForm.getField(fieldName2).setValue("-");
					}
				}
			} catch (IllegalArgumentException e) {
				//auto detect the checkbox if possible:
				boolean handled = false;
				String replacementValue = "";
				if (pdfField instanceof PDButton) {
					PDButton button = (PDButton) pdfField;
					if ("Yes".equals(value)  || "1".equals(value)) {
						for (String s : button.getOnValues()) {
							button.setValue(s);
							replacementValue = s;
						}
						handled = true;
					} else if ("No".equals(value)) {
						replacementValue =  "Off";
						button.setValue(replacementValue);
						handled = true;
					}
				}
				if (!handled) {
					throw e;
				} else {
					LOG.warn("Auto-converted invalid value: {} to: {} for field: {}", value, replacementValue, pdfField.getFullyQualifiedName());
				}
			}
		}
	}

	public static File publishDraft(Object pojo, int year, String fileNameTemplate,
			IRFieldMapper fieldMapper, String fullName, String outputFormat) {
		Map.Entry<String, Object> currentEntry = null;
		Map<IRFieldMapKey, String> propertyToFieldMap = fieldMapper.getPropertyToFieldMap(year);
		try {
			fileName = String.format(fileNameTemplate, year);
			File form = new File(new File("target/classes"), // new File(System.getProperty("user.home"),
																// "Downloads"),
					fileName); // "ir7-%1$s.pdf", year));
			if (!form.exists()) {
				File homeDir = new File(System.getProperty("user.home"));
				File cacheDir = new File(homeDir, ".cache");
				File taxBeansCache = new File(cacheDir, "taxbeans");
				form = new File(taxBeansCache, fileName);
				if (!form.exists()) {
					//load from classpath
					InputStream stream = FormProcessor.class.getClassLoader().getResourceAsStream(fileName);
					form.getParentFile().mkdirs();  //create any required folders on demand
					if (stream == null) {
						throw new AssertionError("File doesn't exist: " + fileName);
					}
					Files.copy(stream, form.toPath());
					if (!form.exists()) {
						throw new AssertionError("Form should exist after loading into cache from classpath");
					}
				}
			}
			LOG.info("Loading: " + form.getAbsolutePath());
			PDDocument pdfTemplate = PDDocument.load(form);
			PDDocumentCatalog docCatalog = pdfTemplate.getDocumentCatalog();
			PDAcroForm acroForm = docCatalog.getAcroForm();
			Map<String, Object> describe = PropertyUtils.describe(pojo);
			key = null;
			try {
				for (Map.Entry<String, Object> entry : describe.entrySet()) {
					currentEntry = entry;
					key = entry.getKey();
					if ("currentAccountMinusSign".equals(key)) {
						LOG.info("currentAccountMinusSign entry");
					}
					Object value = entry.getValue();
					if ("describeForm".equals(System.getProperty("nzsd.descibeFormInDetail"))) {
						List<PDField> fieldList = acroForm.getFields();
						for (PDField sField : fieldList) {
							LOG.info(sField.getFullyQualifiedName());
							for (Entry<COSName, COSBase> f : sField.getCOSObject().entrySet()) {
								LOG.info(f.getKey() + " -> " + f.getValue());
							}
						}
						throw new AssertionError("Exiting due to issue with fields");
					}
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
							String fieldName = getValue(propertyToFieldMap, year, childKey);
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
						processField(acroForm, getValue(propertyToFieldMap, year, key + "_day"),
								dayOfMonth >= 10 ? dayOfMonth : "0" + dayOfMonth, f);
						int monthValue = localDate.getMonthValue();
						processField(acroForm, getValue(propertyToFieldMap, year, key + "_month"),
								monthValue >= 10 ? monthValue : "0" + monthValue, f);
						int year2 = localDate.getYear();
						processField(acroForm, getValue(propertyToFieldMap, year, key + "_year"), year2 >= 10 ? year2 : "0" + year2,
								f);
					} else if (f.getAnnotation(UseTrueFalseMappings.class) != null) {
						if (value == null) {
							//field is not applicable, so continue;
							continue;
						}
						String overrideFieldName = f.getAnnotation(UseTrueFalseMappings.class).fieldName();
						String trueValue = f.getAnnotation(UseTrueFalseMappings.class).trueValue();
						String falseValue = f.getAnnotation(UseTrueFalseMappings.class).falseValue();
						String mappedKey = (Boolean) value ? (key + "_true")
								: (key + "_false");
						String mappedValue = getValue(propertyToFieldMap, year, mappedKey);
						if (!trueValue.equals("") && !falseValue.equals("")) {
							mappedValue = (Boolean) value ? trueValue : falseValue;
						}
						String fieldName = getValue(propertyToFieldMap, year, key);
						if (!overrideFieldName.equals("")) {
							fieldName = overrideFieldName;
						}
						if (fieldName == null || mappedValue == null) {
							propertyToFieldMap.entrySet().forEach(
									action -> LOG.error(String.format("%s -> %s", action.getKey(), action.getValue())));
							throw new AssertionError(String.format("Boolean field: %s mapped to null, possible "
									+ "cause is missing Enum field (or enum true and false suffixes) in the IRFields enum",
									mappedKey));
						}
						processField(acroForm, fieldName, mappedValue, f);
					} else if (f.getAnnotation(UseSeparateYesNoCheckboxes.class) != null) {
						if (value == null) {
							//field is not applicable, so continue;
							continue;
						}
						UseSeparateYesNoCheckboxes annotation2 = f.getAnnotation(UseSeparateYesNoCheckboxes.class);
						String fieldName = annotation2.fieldName();
						String mappedKeyFieldName = (Boolean) value ? (key + "_yes_fieldname")
								: (key + "_no_fieldname");
						String mappedValueKey = (Boolean) value ? (key + "_yes_fieldname_true")
								: (key + "_no_fieldname_true");
						String mappedValue = getValue(propertyToFieldMap, year, mappedValueKey);
						if ("".equals(fieldName)) {
							fieldName = getValue(propertyToFieldMap, year, mappedKeyFieldName);
						}
						if (fieldName == null || mappedValue == null) {
							propertyToFieldMap.entrySet().forEach(
									action -> LOG.error(String.format("%s -> %s", action.getKey(), action.getValue())));
							throw new AssertionError(String.format("Boolean seperate yes/no checkbox field: %s mapped to null, possible "
									+ "cause is missing Enum field (or enum true and false suffixes) in the IRFields enum",
									mappedKeyFieldName));
						}
						processField(acroForm, fieldName, mappedValue, f);
					} else if (f.getAnnotation(UseValueMappings.class) != null) {
						String mappedValue = getValue(propertyToFieldMap, year, key + "_" + value);

						// check for annotation override
						if (value instanceof Enum) {
							Annotation annotation2 =  value.getClass().getField(((Enum<?>) value).name()).getAnnotation(RelativeFieldName.class);
							RelativeFieldName relativeFieldName = (RelativeFieldName) annotation2;
							if (relativeFieldName != null) {
								String relativeFieldNameValue = relativeFieldName.value();
								if (!"".equals(relativeFieldNameValue)) {
									mappedValue = relativeFieldNameValue;
								}
							}
						}
						String value2 = getValue(propertyToFieldMap, year, key);
						LOG.info("For field name value: "+ value2 + ", Mapped Value = " + mappedValue);
						processField(acroForm, value2, mappedValue, f);
					} else {
						processField(acroForm, getValue(propertyToFieldMap, year, key), value, f);
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
						LOG.debug("key = " + key);
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
							processField(acroForm, getValue(propertyToFieldMap, year, key), sumMoney, f);
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
			if (personalisedNaming != null && personalisedNaming.contains("/")) {
				personalisedNaming = null;  //reset to null if it is solely a destination directory
			}
			lowerCase = personalisedNaming != null ? personalisedNaming : lowerCase;
			if (!parent.exists()) {
				throw new IllegalStateException("parent directory doesn't exist: " + parent.getAbsolutePath());
			}
			File result = new File(parent, String.format(outputFormat, year, lowerCase));
			if (result.exists()) {
				result.delete();
			}
			acroForm.setXFA(null);
			acroForm.setNeedAppearances(true);
			pdfTemplate.save(result);
			pdfTemplate.close();
			LOG.info("Form Completed Successfully: " + result);
			return result;
		} catch (Exception e) {
			throw new TaxBeansException("Is field in the enum? Entry: " + currentEntry, e);
		}
	}

	private static String getValue(Map<IRFieldMapKey, String> map, int year, String childKey) {
		IRFieldMapKey key = new IRFieldMapKey(childKey, year);
		return map.get(key);
	}

	public static void setCsvMappingFileName(String csvMappingFileName) {
		FormProcessor.csvMappingFileName = csvMappingFileName;
	}

}
