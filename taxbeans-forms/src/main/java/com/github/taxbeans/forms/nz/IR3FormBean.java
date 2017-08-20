package com.github.taxbeans.forms.nz;

import java.io.File;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IR3FormBean {
	
	final Logger logger = LoggerFactory.getLogger(IR3FormBean.class);

	private int year = 2017;

	private String irdNumber;
	
	private Salutation salutation;

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
