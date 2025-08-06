package com.github.taxbeans.forms.nz;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IR7PFieldMapper implements IRFieldMapper {

	final static Logger logger = LoggerFactory.getLogger(IR7FieldMapper.class);
	
	private static volatile Map<Integer, Map<IRFieldMapKey, String[]>> yearMap = new ConcurrentHashMap<Integer, Map<IRFieldMapKey, String[]>>();

	public static String getFieldName(IR7PFields fieldName, int year) {
		Map<IRFieldMapKey, String[]> map = yearMap.get(year);
		if (map == null) {
			synchronized (IR7PFieldMapper.class) {
				map = yearMap.get(year);
				if (map == null) {
					InputStream resource = 
							IR7PFieldMapper.class.getClassLoader()
							.getResourceAsStream("ir7p-fields.csv");
					map = IRFieldMapUtils.populateMap(resource, year);
					yearMap.put(year, map);
				}
			}
		}
		int i = year-2009;
		String[] strings = map.get(IRFieldMapUtils.getMapKey(fieldName.name(), year));
		if (strings.length == 0) {
			logger.error("No matching fields in IR7P form {} for: {}", year, fieldName.name());
		}
		if (strings.length <= i) {
			logger.error("No mapping column in IR7P mapping for year: {}", year);
		}
		return strings[i];
	}

	public Map<IRFieldMapKey, String> getPropertyToFieldMap(int year) {
		Map<IRFieldMapKey, String> map = new HashMap<IRFieldMapKey, String>();
		for (IR7PFields field : IR7PFields.values()) {
			map.put(new IRFieldMapKey(field.name(), year), getFieldName(field, year));
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

	public static IRFieldMapper instance() {
		return new IR7PFieldMapper();
	}
}
