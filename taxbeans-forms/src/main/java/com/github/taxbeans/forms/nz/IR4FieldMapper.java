package com.github.taxbeans.forms.nz;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.taxbeans.model.nz.Salutation;

public class IR4FieldMapper {

	final static Logger logger = LoggerFactory.getLogger(IR4FieldMapper.class);

	private static volatile Map<String, String[]> map = null;

	public static String getFieldName(IR4Fields fieldName, int year) {
		return getFieldNameViaString(fieldName.name(), year);
	}

	private static String getFieldNameViaString(String fieldName, int year) {
		if (map == null) {
			synchronized (IR4FieldMapper.class) {
				if (map == null) {
					InputStream resource = 
							IR4FieldMapper.class.getClassLoader()
							.getResourceAsStream("ir4-fields.csv");
					map = IRFieldMapperUtils.populateMap(resource, year);
				}
			}
		}
		int i = year-2016;
		String[] strings = map.get(fieldName);
		if (strings == null) {
			System.err.println(fieldName + " resulted in null Strings");
			for (Entry<String, String[]> entry : map.entrySet()) {
				System.err.println(entry.getKey() + " -> " + Arrays.asList(entry.getValue()));
			}
			throw new IllegalStateException();
		}
		return strings[i];
	}

	public static String getBooleanFieldValue(String name, boolean value, int year) {
		String fieldName = String.format("%1$s.%2$s", name, String.valueOf(value));
		return getFieldNameViaString(fieldName, year);
	}

	public static Map<String, String> getPropertyToFieldMap(int year) {
		Map<String, String> map = new HashMap<String, String>();
		for (IR4Fields field : IR4Fields.values()) {
			map.put(field.name(), getFieldName(field, year));
		}
		return map;
	}

	public static Map<String, String> getFieldToPropertyMap(int year) {
		Map<String, String> map = new HashMap<String, String>();
		for (IR4Fields field : IR4Fields.values()) {
			map.put(getFieldName(field, year), field.name());
		}
		return map;
	}
}
