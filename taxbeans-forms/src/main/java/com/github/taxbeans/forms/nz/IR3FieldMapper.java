package com.github.taxbeans.forms.nz;

import java.io.InputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.taxbeans.model.nz.Salutation;

public class IR3FieldMapper {

	final static Logger logger = LoggerFactory.getLogger(IR3FieldMapper.class);

	private static volatile Map<String, String[]> map = null;

	public static String getFieldName(IR3Fields fieldName, int year) {
		return getFieldNameViaString(fieldName.name(), year);
	}
	
	private static String getFieldNameViaString(String fieldName, int year) {
		if (map == null) {
			synchronized (IR3FieldMapper.class) {
				if (map == null) {
					InputStream resource = 
							IR3FieldMapper.class.getClassLoader()
							.getResourceAsStream("ir3-fields.csv");
					map = IRFieldMapperUtils.populateMap(resource, year);
				}
			}
		}
		int i = year-2016;
		return map.get(fieldName)[i];
	}

	public static String getSalutationFieldValue(Salutation salutation, int year) {
		String fieldName = String.format("%1$s.%2$s", IR3Fields.salutation.name(), salutation.name());
		return getFieldNameViaString(fieldName, year);
	}
	
	public static String getBooleanFieldValue(String name, boolean value, int year) {
		String fieldName = String.format("%1$s.%2$s", name, String.valueOf(value));
		return getFieldNameViaString(fieldName, year);
	}
}
