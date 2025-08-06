package com.github.taxbeans.pdf;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

import com.github.taxbeans.exception.TaxBeansException;

public class PDFUtils {

	public static void setFieldValue(PDField field, String text, PDFAlignment right, int length) {
		try {
			field.setValue(StringUtils.leftPad(text, length, " "));
		} catch (IOException e) {
			throw new TaxBeansException(e);
		}
	}
}
