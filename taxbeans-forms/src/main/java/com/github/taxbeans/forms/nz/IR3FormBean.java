package com.github.taxbeans.forms.nz;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.taxbeans.exception.TaxBeansException;
import com.github.taxbeans.forms.UseChildFields;
import com.github.taxbeans.forms.UseDayMonthYear;
import com.github.taxbeans.forms.UseTrueFalseMappings;
import com.github.taxbeans.forms.UseValueMappings;
import com.github.taxbeans.forms.utils.LocalDateUtils;
import com.github.taxbeans.forms.utils.TaxReturnUtils;
import com.github.taxbeans.model.nz.NZBankAccount;
import com.github.taxbeans.model.nz.Salutation;
import com.github.taxbeans.pdf.PDFAlignment;
import com.github.taxbeans.pdf.PDFUtils;

public class IR3FormBean {

	final Logger logger = LoggerFactory.getLogger(IR3FormBean.class);

	private int year = 2017;

	private String irdNumber;

	@UseValueMappings
	private Salutation salutation;

	private String firstname;

	private String surname;

	private String postalAddressLine1;

	private String postalAddressLine2;

	private String streetAddressLine1;

	private String streetAddressLine2;

	@UseDayMonthYear
	private LocalDate dateOfBirth;

	private String businessIndustryClassificationCode;

	private String phonePrefix;

	private String phoneNumberExcludingPrefix;

	@UseChildFields
	private NZBankAccount bankAccount;

	@UseTrueFalseMappings
	private boolean incomeAdjustmentsRequired;

	@UseTrueFalseMappings
	private boolean familyTaxCreditReceived;

	private Money familyTaxCreditAmount;

	@UseTrueFalseMappings
	private boolean incomeWithTaxDeductedReceived;

	private Money totalPAYEDeducted;

	private Money totalGrossIncome;

	private Money accEarnersLevy;

	private Money incomeNotLiableForAccEarnersLevy;

	private Money totalTaxDeducted;

	public String getIrdNumber() {
		return irdNumber;
	}

	public void setIrdNumber(String irdNumber) {
		this.irdNumber = irdNumber;
	}

	public Salutation getSalutation() {
		return salutation;
	}

	public void setSalutation(Salutation salutation) {
		this.salutation = salutation;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
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

	public String getStreetAddressLine1() {
		return streetAddressLine1;
	}

	public void setStreetAddressLine1(String streetAddressLine1) {
		this.streetAddressLine1 = streetAddressLine1;
	}

	public String getStreetAddressLine2() {
		return streetAddressLine2;
	}

	public void setStreetAddressLine2(String streetAddressLine2) {
		this.streetAddressLine2 = streetAddressLine2;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getBusinessIndustryClassificationCode() {
		return businessIndustryClassificationCode;
	}

	public void setBusinessIndustryClassificationCode(String businessIndustryClassificationCode) {
		this.businessIndustryClassificationCode = businessIndustryClassificationCode;
	}

	public String getPhonePrefix() {
		return phonePrefix;
	}

	public void setPhonePrefix(String phoneNumberPrefix) {
		this.phonePrefix = phoneNumberPrefix;
	}

	public String getPhoneNumberExcludingPrefix() {
		return phoneNumberExcludingPrefix;
	}

	public void setPhoneNumberExcludingPrefix(String phoneNumberExcludingPrefix) {
		this.phoneNumberExcludingPrefix = phoneNumberExcludingPrefix;
	}

	public NZBankAccount getBankAccount() {
		return bankAccount;
	}

	public void setAccount(NZBankAccount account) {
		this.bankAccount = account;
	}

	public boolean isIncomeAdjustmentsRequired() {
		return incomeAdjustmentsRequired;
	}

	public void setIncomeAdjustmentsRequired(boolean incomeAdjustmentsRequired) {
		this.incomeAdjustmentsRequired = incomeAdjustmentsRequired;
	}

	public boolean isFamilyTaxCreditReceived() {
		return familyTaxCreditReceived;
	}

	public void setFamilyTaxCreditReceived(boolean familyTaxCreditReceived) {
		this.familyTaxCreditReceived = familyTaxCreditReceived;
	}

	public Money getFamilyTaxCreditAmount() {
		return familyTaxCreditAmount;
	}

	public void setFamilyTaxCreditAmount(Money familyTaxCreditAmount) {
		this.familyTaxCreditAmount = familyTaxCreditAmount;
	}

	public boolean isIncomeWithTaxDeductedReceived() {
		return incomeWithTaxDeductedReceived;
	}

	public void setIncomeWithTaxDeductedReceived(boolean incomeWithTaxDeductedReceived) {
		this.incomeWithTaxDeductedReceived = incomeWithTaxDeductedReceived;
	}

	public Money getTotalPAYEDeducted() {
		return totalPAYEDeducted;
	}

	public void setTotalPAYEDeducted(Money totalPAYEDeducted) {
		this.totalPAYEDeducted = totalPAYEDeducted;
	}

	public Money getTotalGrossIncome() {
		return totalGrossIncome;
	}

	public void setTotalGrossIncome(Money totalGrossIncome) {
		this.totalGrossIncome = totalGrossIncome;
	}

	public Money getAccEarnersLevy() {
		return accEarnersLevy;
	}

	public void setAccEarnersLevy(Money accEarnersLevy) {
		this.accEarnersLevy = accEarnersLevy;
	}

	public Money getIncomeNotLiableForAccEarnersLevy() {
		return incomeNotLiableForAccEarnersLevy;
	}

	public void setIncomeNotLiableForAccEarnersLevy(Money incomeNotLiableForAccEarnersLevy) {
		this.incomeNotLiableForAccEarnersLevy = incomeNotLiableForAccEarnersLevy;
	}

	public Money getTotalTaxDeducted() {
		return totalTaxDeducted;
	}

	public void setTotalTaxDeducted(Money totalTaxDeducted) {
		this.totalTaxDeducted = totalTaxDeducted;
	}

	private Map<String, String> getPropertyToFieldMap() {
		return IR3FieldMapper.getPropertyToFieldMap(year);
	}

	private Map<String, String> getFieldToPropertyMap() {
		return IR3FieldMapper.getFieldToPropertyMap(year);
	}

	//assumes the forms are in the user's Downloads folder
	public void publishDraft() {
		try {
			File ir3Form = new File(
					new File(System.getProperty("user.home"), "Downloads"),
					String.format("ir3-%1$s.pdf", year));
			PDDocument pdfTemplate = PDDocument.load(ir3Form);

			PDDocumentCatalog docCatalog = pdfTemplate.getDocumentCatalog();
			PDAcroForm acroForm = docCatalog.getAcroForm();
			Map<String, Object> describe = PropertyUtils.describe(this);
			Map<String, String> propertyToFieldMap = this.getPropertyToFieldMap();
			for (Map.Entry<String, Object> entry : describe.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				System.out.println(key + "->" + value);
				if (key.equals("bankAccount")) {
					System.out.println("bank account");
				}
				if (key.equals("familyTaxCreditReceived")) {
					System.out.println("bank account");
				}
				if (key.equals("class") || key.equals("year")) {
					//todo exclude fields by annotation
					continue;
				}
				Field f = this.getClass().getDeclaredField(key);
				f.setAccessible(true);
				Object field = f.get(this);
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
					String fieldName = propertyToFieldMap.get(key);
					if (f.getAnnotation(UseDayMonthYear.class) != null) {
						processField(acroForm, propertyToFieldMap.get(key + "_day"), value);
						processField(acroForm, propertyToFieldMap.get(key + "_month"), value);
						processField(acroForm, propertyToFieldMap.get(key + "_year"), value);
					} else if (f.getAnnotation(UseTrueFalseMappings.class) != null) {
						String mappedValue = (Boolean) value ? propertyToFieldMap.get(key + "_true") : propertyToFieldMap.get(key + "_false");
						processField(acroForm, propertyToFieldMap.get(key), mappedValue);
					} else if (f.getAnnotation(UseValueMappings.class) != null) {
						String mappedValue = propertyToFieldMap.get(key + "_" + value);
						processField(acroForm, propertyToFieldMap.get(key), mappedValue);
					} else {
						if (fieldName == null) {
							throw new IllegalStateException("No field mapping for: " + key);
						}
						PDField pdField = acroForm.getField(fieldName);
						System.out.println(fieldName + "->" + pdField);
						if (pdField == null) {
							List<PDField> fields = acroForm.getFields();
							for (PDField field1 : fields) {
								System.out.println("Candidate field: " + field1.getFullyQualifiedName());
							}
						}
						pdField.setValue(String.valueOf(value));
						System.out.println(fieldName + "->" + pdField);
					}
				}
			}
			System.out.println("done");
			File ir3DraftForm = new File(
					new File(System.getProperty("user.home"), "Downloads"),
					String.format("ir3-%1$s-draft.pdf", year));
			pdfTemplate.save(ir3DraftForm);
			pdfTemplate.close();
			logger.info("IR3 Form Completed Successfully");
		} catch (Exception e) {
			throw new TaxBeansException(e);
		}
	}

	public void processField(PDAcroForm acroForm, String fieldName, Object value) throws IOException {
		PDField pdField = acroForm.getField(fieldName);
		System.out.println(fieldName + "->" + pdField);
		if (pdField == null) {
			List<PDField> fields = acroForm.getFields();
			for (PDField field1 : fields) {
				System.out.println("Candidate field: " + field1.getFullyQualifiedName());
			}
		}
		pdField.setValue(String.valueOf(value));
	}


	//assumes the forms are in the user's Downloads folder
	public void publishDraftV1() {
		try {
			File ir3Form = new File(
					new File(System.getProperty("user.home"), "Downloads"),
					String.format("ir3-%1$s.pdf", year));
			PDDocument pdfTemplate = PDDocument.load(ir3Form);

			PDDocumentCatalog docCatalog = pdfTemplate.getDocumentCatalog();
			PDAcroForm acroForm = docCatalog.getAcroForm();

			//acroForm.get
			List<PDField> fieldList = acroForm.getFields();

			String[] fieldArray = new String[fieldList.size()];
			int i = 0;
			for (PDField sField : fieldList) {
				fieldArray[i] = sField.getFullyQualifiedName();
				i++;
			}
			for (String f : fieldArray) {
				PDField field = acroForm.getField(f);
				logger.info("Field name is: " + f);
				if (f.contains(IR3FieldMapper.getFieldName(IR3Fields.irdNumber, year))) {
					String irdNumber2 = this.getIrdNumber();
					if (irdNumber2.length() == 8) {
						irdNumber2 = String.format(" %1$s", irdNumber2);
					}
					field.setValue(irdNumber2);
				} else if (f.contains(IR3FieldMapper.getFieldName(IR3Fields.salutation, year))) {
					PDCheckBox radioButton = (PDCheckBox) field;
					String salutationValue =
							IR3FieldMapper.getSalutationFieldValue(this.getSalutation(), year);
					radioButton.setValue(salutationValue);
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.firstname, year))) {
					field.setValue(this.getFirstname());
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.surname, year))) {
					field.setValue(this.getSurname());
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.postalAddressLine1, year))) {
					field.setValue(this.getPostalAddressLine1());
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.postalAddressLine2, year))) {
					field.setValue(this.getPostalAddressLine2());
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.streetAddressLine1, year))) {
					field.setValue(this.getStreetAddressLine1());
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.streetAddressLine2, year))) {
					field.setValue(this.getStreetAddressLine2());
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.dateOfBirth_day, year))) {
					field.setValue(LocalDateUtils.formatDay(this.getDateOfBirth()));
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.dateOfBirth_month, year))) {
					field.setValue(LocalDateUtils.formatMonth(this.getDateOfBirth()));
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.dateOfBirth_year, year))) {
					field.setValue(String.valueOf(this.getDateOfBirth().getYear()));
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.businessIndustryClassificationCode, year))) {
					field.setValue(this.getBusinessIndustryClassificationCode());
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.phonePrefix, year))) {
					field.setValue(this.getPhonePrefix());
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.phoneNumberExcludingPrefix, year))) {
					field.setValue(this.getPhoneNumberExcludingPrefix());
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.bankNumber, year))) {
					field.setValue(this.getBankAccount().getBankNumber());
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.branchNumber, year))) {
					field.setValue(this.getBankAccount().getBranchNumber());
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.bankAccountNumber, year))) {
					field.setValue(this.getBankAccount().getBankAccountNumber());
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.bankSuffix, year))) {
					field.setValue(this.getBankAccount().getBankSuffix());
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.incomeAdjustmentsRequired, year))) {
					field.setValue(IR3FieldMapper.getBooleanFieldValue(IR3Fields.incomeAdjustmentsRequired.name(), 
							this.incomeAdjustmentsRequired, year));
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.familyTaxCreditReceived, year))) {
					field.setValue(IR3FieldMapper.getBooleanFieldValue(IR3Fields.familyTaxCreditReceived.name(), 
							this.familyTaxCreditReceived, year));
				} else if (f.equals(IR3FieldMapper.getFieldName(
						IR3Fields.familyTaxCreditAmount, year))) {
					PDFUtils.setFieldValue(field, TaxReturnUtils.formatMoneyField(this.familyTaxCreditAmount), PDFAlignment.RIGHT, 11);
				} else if (f.equals(IR3FieldMapper.getFieldName(
						IR3Fields.incomeWithTaxDeductedReceived, year))) {
					field.setValue(IR3FieldMapper.getBooleanFieldValue(IR3Fields.incomeWithTaxDeductedReceived.name(), 
							this.incomeWithTaxDeductedReceived, year));
				} else if (f.equals(IR3FieldMapper.getFieldName(
						IR3Fields.totalPAYEDeducted, year))) {
					PDFUtils.setFieldValue(field, TaxReturnUtils.formatMoneyField(this.totalPAYEDeducted), PDFAlignment.RIGHT, 11);
				} else if (f.equals(IR3FieldMapper.getFieldName(
						IR3Fields.totalGrossIncome, year))) {
					PDFUtils.setFieldValue(field, TaxReturnUtils.formatMoneyField(this.totalGrossIncome), PDFAlignment.RIGHT, 11);
				} else if (f.equals(IR3FieldMapper.getFieldName(
						IR3Fields.accEarnersLevy, year))) {
					PDFUtils.setFieldValue(field, TaxReturnUtils.formatMoneyField(this.accEarnersLevy), PDFAlignment.RIGHT, 11);
				} else if (f.equals(IR3FieldMapper.getFieldName(
						IR3Fields.incomeNotLiableForAccEarnersLevy, year))) {
					PDFUtils.setFieldValue(field, TaxReturnUtils.formatMoneyField(this.incomeNotLiableForAccEarnersLevy), PDFAlignment.RIGHT, 11);
				} else if (f.equals(IR3FieldMapper.getFieldName(
						IR3Fields.totalTaxDeducted, year))) {
					PDFUtils.setFieldValue(field, TaxReturnUtils.formatMoneyField(this.totalTaxDeducted), PDFAlignment.RIGHT, 11);
				} 
			}
			File ir3DraftForm = new File(
					new File(System.getProperty("user.home"), "Downloads"),
					String.format("ir3-%1$s-draft.pdf", year));
			pdfTemplate.save(ir3DraftForm);
			pdfTemplate.close();
			logger.info("IR3 Form Completed Successfully");
		} catch (Exception e) {
			throw new TaxBeansException(e);
		}
	}
}
