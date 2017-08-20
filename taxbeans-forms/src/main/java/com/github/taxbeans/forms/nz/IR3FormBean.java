package com.github.taxbeans.forms.nz;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.taxbeans.forms.utils.LocalDateUtils;
import com.github.taxbeans.model.nz.NZBankAccount;
import com.github.taxbeans.model.nz.Salutation;

public class IR3FormBean {
	
	final Logger logger = LoggerFactory.getLogger(IR3FormBean.class);

	private int year = 2017;

	private String irdNumber;
	
	private Salutation salutation;
	
	private String firstname;
	
	private String surname;
	
	private String postalAddressLine1;
	
	private String postalAddressLine2;
	
	private String streetAddressLine1;
	
	private String streetAddressLine2;
	
	private LocalDate dateOfBirth;
	
	private String businessIndustryClassificationCode;
	
	private String phoneNumberPrefix;
	
	private String phoneNumberExcludingPrefix;
	
	private NZBankAccount account;
	
	private boolean incomeAdjustmentsRequired;

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

	public String getPhoneNumberPrefix() {
		return phoneNumberPrefix;
	}

	public void setPhoneNumberPrefix(String phoneNumberPrefix) {
		this.phoneNumberPrefix = phoneNumberPrefix;
	}

	public String getPhoneNumberExcludingPrefix() {
		return phoneNumberExcludingPrefix;
	}

	public void setPhoneNumberExcludingPrefix(String phoneNumberExcludingPrefix) {
		this.phoneNumberExcludingPrefix = phoneNumberExcludingPrefix;
	}

	public NZBankAccount getBankAccount() {
		return account;
	}

	public void setAccount(NZBankAccount account) {
		this.account = account;
	}

	public boolean isIncomeAdjustmentsRequired() {
		return incomeAdjustmentsRequired;
	}

	public void setIncomeAdjustmentsRequired(boolean incomeAdjustmentsRequired) {
		this.incomeAdjustmentsRequired = incomeAdjustmentsRequired;
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
						IR3Fields.birthDay, year))) {
					field.setValue(LocalDateUtils.formatDay(this.getDateOfBirth()));
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.birthMonth, year))) {
					field.setValue(LocalDateUtils.formatMonth(this.getDateOfBirth()));
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.birthYear, year))) {
					field.setValue(String.valueOf(this.getDateOfBirth().getYear()));
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.businessIndustryClassificationCode, year))) {
					field.setValue(this.getBusinessIndustryClassificationCode());
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.phonePrefix, year))) {
					field.setValue(this.getPhoneNumberPrefix());
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
					field.setValue(this.getBankAccount().getAccountNumber());
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.bankSuffix, year))) {
					field.setValue(this.getBankAccount().getSuffix());
				} else if (f.contains(IR3FieldMapper.getFieldName(
						IR3Fields.incomeAdjustmentsRequired, year))) {
					field.setValue(IR3FieldMapper.getBooleanFieldValue(IR3Fields.incomeAdjustmentsRequired.name(), 
							this.incomeAdjustmentsRequired, year));
				}
			}
			File ir3DraftForm = new File(
					new File(System.getProperty("user.home"), "Downloads"),
					String.format("ir3-%1$s-draft.pdf", year));
			pdfTemplate.save(ir3DraftForm);
			pdfTemplate.close();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
}
