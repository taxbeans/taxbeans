package com.github.taxbeans.forms.nz;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IR10FieldMapper {

	final static Logger logger = LoggerFactory.getLogger(IR10FieldMapper.class);

	private static volatile Map<String, String[]> map = null;

	public static String getFieldName(IR10Fields fieldName, int year) {
		if (map == null) {
			synchronized (IR10FieldMapper.class) {
				if (map == null) {
					InputStream resource = 
							IR10FieldMapper.class.getClassLoader()
							.getResourceAsStream("ir10-fields.csv");
					map = IRFieldMapperUtils.populateMap(resource, year);
				}
			}
		}
		return map.get(fieldName.name())[year-2009];
	}	

	public static Map<String, String> getPropertyToFieldMap(int year) {
		Map<String, String> map = new HashMap<String, String>();
		for (IR10Fields field : IR10Fields.values()) {
			map.put(field.name(), getFieldName(field, year));
		}
		return map;
	}

	public static Map<String, String> getFieldToPropertyMap(int year) {
		Map<String, String> map = new HashMap<String, String>();
		for (IR10Fields field : IR10Fields.values()) {
			map.put(getFieldName(field, year), field.name());
		}
		return map;
	}
}
