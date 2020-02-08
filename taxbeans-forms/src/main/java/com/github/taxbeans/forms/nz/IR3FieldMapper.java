package com.github.taxbeans.forms.nz;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
							.getResourceAsStream(year == 2019 ? "ir3-fields-v2.csv" : "ir3-fields.csv");
					map = IRFieldMapperUtils.populateMap(resource, year);
				}
			}
		}
		int i = year-2016;
		String[] strings = map.get(fieldName);
		if (strings == null) {
			System.out.println(fieldName + " resulted in null Strings");
			for (Entry<String, String[]> entry : map.entrySet()) {
				System.out.println(entry.getKey() + " -> " + entry.getValue());
			}
			throw new IllegalStateException();
		}
		return strings[i];
	}

	public static String getSalutationFieldValue(Salutation salutation, int year) {
		String fieldName = String.format("%1$s.%2$s", IR3Fields.salutation.name(), salutation.name());
		return getFieldNameViaString(fieldName, year);
	}

	public static String getBooleanFieldValue(String name, boolean value, int year) {
		String fieldName = String.format("%1$s.%2$s", name, String.valueOf(value));
		return getFieldNameViaString(fieldName, year);
	}

	public static Map<String, String> getPropertyToFieldMap(int year) {
		Map<String, String> map = new HashMap<String, String>();
		for (IR3Fields field : IR3Fields.values()) {
			if (year > 2018 && field.name().contains("2018")) {
				//workaround for field naming issue
				continue;
			}
			map.put(field.name(), getFieldName(field, year));
		}
		return map;
	}

	public static Map<String, String> getFieldToPropertyMap(int year) {
		Map<String, String> map = new HashMap<String, String>();
		for (IR3Fields field : IR3Fields.values()) {
			if (year > 2018 && field.name().contains("2018")) {
				//workaround for field naming issue
				continue;
			}
			map.put(getFieldName(field, year), field.name());
		}
		return map;
	}
}
