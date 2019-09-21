package com.github.taxbeans.forms.nz;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
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
import com.github.taxbeans.forms.RightAlign;
import com.github.taxbeans.forms.Skip;
import com.github.taxbeans.forms.SkipIfFalse;
import com.github.taxbeans.forms.UseChildFields;
import com.github.taxbeans.forms.UseDayMonthYear;
import com.github.taxbeans.forms.UseTrueFalseMappings;
import com.github.taxbeans.forms.UseValueMappings;
import com.github.taxbeans.forms.utils.TaxReturnUtils;

public class IR10Form2018 {

	private String fullName;
	
	@Skip
	private String destinationDirectory;

	@RightAlign(9)
	private String irdNumber;
	
	@UseTrueFalseMappings
	private boolean multipleActivityRadio;
	
	public boolean isMultipleActivityRadio() {
		return multipleActivityRadio;
	}

	public void setMultipleActivityRadio(boolean multipleActivityRadio) {
		this.multipleActivityRadio = multipleActivityRadio;
	}

	final Logger logger = LoggerFactory.getLogger(IR10Form2018.class);

	private int year = 2018;
	
	@Skip
	private String personalisedNaming;
	
	private String calculateMinusSign(Money value) {
		return value.signum() < 0 ? "-" : "";
	}
		
	public String getIrdNumber() {
		return irdNumber;
	}

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
		if (value instanceof Money) {
			if (f.getAnnotation(OmitCents.class) != null) {
				value = TaxReturnUtils.formatDollarsField((Money) value);
				if (f.getAnnotation(IncludeFormatSpacing.class) != null) {
					String valueText = (String)value;
					if (valueText.length() >= 4) {
						valueText = valueText.substring(0, valueText.length()-3) + " " + 
								valueText.substring(valueText.length()-3);
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

	//assumes the forms are in the user's Downloads folder
	public void publishDraft() {
		try {
			File ir10Form = new File(
					new File("target/classes"), //new File(System.getProperty("user.home"), "Downloads"),
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
						// acroForm.get
						List<PDField> fieldList = acroForm.getFields();

						String[] fieldArray = new String[fieldList.size()];
						int i = 0;
						for (PDField sField : fieldList) {
							fieldArray[i] = sField.getFullyQualifiedName();
							i++;
						}
						for (String f : fieldArray) {
							// PDField field = acroForm.getField(f);
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
					} else {
						// String fieldName = propertyToFieldMap.get(key);
						if (f.getAnnotation(UseDayMonthYear.class) != null) {
							LocalDate localDate = (LocalDate) value;
							if (value == null) {
								//leave the field blank
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
								propertyToFieldMap.entrySet().forEach(action -> 
									logger.error(String.format("%s -> %s", action.getKey(), action.getValue())));
								throw new AssertionError(String.format("Boolean field: %s mapped to null, possible " + 
									"cause is missing Enum field in IR10Fields", key));
							}
							processField(acroForm, fieldName, mappedValue, f);
						} else if (f.getAnnotation(UseValueMappings.class) != null) {
							String mappedValue = propertyToFieldMap.get(key + "_" + value);
							processField(acroForm, propertyToFieldMap.get(key), mappedValue, f);
						} else {
							processField(acroForm, propertyToFieldMap.get(key), value, f);
						}
					}
				}
			} catch (NullPointerException e) {
				logger.error("Error processing: {}", key);
				throw e;
			}
			File parent = destinationDirectory != null ? new File(destinationDirectory) 
					: new File("target"); //new File(System.getProperty("user.home"), "Downloads");
			String lowerCase = this.getFullName().split(" ")[0].toLowerCase();
			lowerCase = personalisedNaming != null ? personalisedNaming : lowerCase;
			File ir10DraftForm = new File(
					parent,
					String.format("ir10-%1$s-%2$s-draft.pdf", year, lowerCase));
			//flattening causes fields to disappear
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
			throw new TaxBeansException(e);
		}
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
}
