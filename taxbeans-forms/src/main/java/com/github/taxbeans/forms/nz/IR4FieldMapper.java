package com.github.taxbeans.forms.nz;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IR4FieldMapper implements IRFieldMapper {

	final static Logger logger = LoggerFactory.getLogger(IR4FieldMapper.class);

	//private static volatile Map<String, String[]> map = null;
	
	private static volatile Map<Integer, Map<IRFieldMapKey, String[]>> yearMap = new ConcurrentHashMap<Integer, Map<IRFieldMapKey, String[]>>();

	private static boolean alreadyWarned = false;

	public static String getFieldName(IR4Fields fieldName, int year) {
		return getFieldNameViaString(fieldName.name(), year);
	}

	private static String getFieldNameViaString(String fieldName, int year) {
		Map<IRFieldMapKey, String[]> map = yearMap.get(year);
		if (map == null) {
			synchronized (IR4FieldMapper.class) {
				map = yearMap.get(year);
				if (map == null) {
					InputStream resource = 
							IR4FieldMapper.class.getClassLoader()
							.getResourceAsStream("ir4-fields.csv");
					map = IRFieldMapUtils.populateMap(resource, year);
					yearMap.put(year, map);
				}
			}
		}
		int i = year-2016;
		String[] strings = map.get(new IRFieldMapKey(fieldName, year));		
		
		if (strings == null) {
			logger.trace(fieldName + " resulted in null Strings");
			for (Entry<IRFieldMapKey, String[]> entry : map.entrySet()) {
				if (alreadyWarned) {
					logger.trace(entry.getKey() + " -> " + Arrays.asList(entry.getValue()));	
				} else {
					logger.warn(entry.getKey() + " -> " + Arrays.asList(entry.getValue()));
				}
			}
			alreadyWarned = true;
			logger.info("Processing field: {}:{}", year, fieldName);
			logger.trace(fieldName + " resulted in null Strings, mapping to null");
			return null;
		}
		if (i >= strings.length) {
			logger.info("Processing field: {}:{}", year, fieldName);
			throw new AssertionError("Column for year " + year + " is missing in CSV template.");
		}
		logger.info("Processing field: {}:{} -> {}", year, fieldName, strings[i]);
		return strings[i];
	}

	public static String getBooleanFieldValue(String name, boolean value, int year) {
		String fieldName = String.format("%1$s.%2$s", name, String.valueOf(value));
		return getFieldNameViaString(fieldName, year);
	}

	public Map<IRFieldMapKey, String> getPropertyToFieldMap(int year) {
		Map<IRFieldMapKey, String> map = new HashMap<IRFieldMapKey, String>();
		for (IR4Fields field : IR4Fields.values()) {
			map.put(new IRFieldMapKey(field.name(), year), getFieldName(field, year));
		}
		return map;
	}

//	public static Map<String, String> getFieldToPropertyMap(int year) {
//		Map<String, String> map = new HashMap<String, String>();
//		for (IR4Fields field : IR4Fields.values()) {
//			map.put(getFieldName(field, year), field.name());
//		}
//		return map;
//	}

	public static IRFieldMapper instance() {
		return new IR4FieldMapper();
	}
}
