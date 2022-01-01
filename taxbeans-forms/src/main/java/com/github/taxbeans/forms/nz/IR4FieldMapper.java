package com.github.taxbeans.forms.nz;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IR4FieldMapper {

	final static Logger logger = LoggerFactory.getLogger(IR4FieldMapper.class);

	private static volatile Map<String, String[]> map = null;

	private static boolean alreadyWarned = false;

	public static String getFieldName(IR4Fields fieldName, int year) {
		return getFieldNameViaString(fieldName.name(), year);
	}

	private static String getFieldNameViaString(String fieldName, int year) {
		logger.info("Processing field: {}:{}", year, fieldName);
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
			logger.trace(fieldName + " resulted in null Strings");
			for (Entry<String, String[]> entry : map.entrySet()) {
				if (alreadyWarned) {
					logger.trace(entry.getKey() + " -> " + Arrays.asList(entry.getValue()));	
				} else {
					logger.warn(entry.getKey() + " -> " + Arrays.asList(entry.getValue()));
				}
			}
			alreadyWarned = true;
			logger.trace(fieldName + " resulted in null Strings, mapping to null");
			return null;
		}
		if (i >= strings.length) {
			throw new AssertionError("Column for year " + year + " is missing in CSV template.");
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
