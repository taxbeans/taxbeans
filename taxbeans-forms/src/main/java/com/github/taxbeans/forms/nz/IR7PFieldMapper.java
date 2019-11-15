package com.github.taxbeans.forms.nz;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IR7PFieldMapper {

	private static volatile Map<String, String[]> map = null;
	
	final static Logger logger = LoggerFactory.getLogger(IR7FieldMapper.class);

	public static String getFieldName(IR7PFields fieldName, int year) {
		if (map == null) {
			synchronized (IR7PFieldMapper.class) {
				if (map == null) {
					InputStream resource = 
							IR7PFieldMapper.class.getClassLoader()
							.getResourceAsStream("ir7p-fields.csv");
					map = IRFieldMapperUtils.populateMap(resource, year);
				}
			}
		}
		int i = year-2009;
		String[] strings = map.get(fieldName.name());
		if (strings.length == 0) {
			logger.error("No matching fields in IR7P form {} for: {}", year, fieldName.name());
		}
		if (strings.length <= i) {
			logger.error("No mapping column in IR7P mapping for year: {}", year);
		}
		return strings[i];
	}

	public static Map<String, String> getPropertyToFieldMap(int year) {
		Map<String, String> map = new HashMap<String, String>();
		for (IR7PFields field : IR7PFields.values()) {
			map.put(field.name(), getFieldName(field, year));
		}
		return map;
	}

	public static Map<String, String> getFieldToPropertyMap(int year) {
		Map<String, String> map = new HashMap<String, String>();
		for (IR7PFields field : IR7PFields.values()) {
			map.put(getFieldName(field, year), field.name());
		}
		return map;
	}
}
