package com.github.taxbeans.forms.nz;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IR10FieldMapper implements IRFieldMapper {

	final static Logger logger = LoggerFactory.getLogger(IR10FieldMapper.class);

	//private static volatile Map<String, String[]> map = null;
	
	private static volatile Map<Integer, Map<IRFieldMapKey, String[]>> yearMap = new ConcurrentHashMap<Integer, Map<IRFieldMapKey, String[]>>();

	public static String getFieldName(IR10Fields fieldName, int year) {
		Map<IRFieldMapKey, String[]> map = yearMap.get(year);
		if (map == null) {
			synchronized (IR10FieldMapper.class) {
				if (map == null) {
					InputStream resource = 
							IR10FieldMapper.class.getClassLoader()
							.getResourceAsStream("ir10-fields-v2.csv");
					yearMap.put(year, IRFieldMapUtils.populateMap(resource, year));
					map = yearMap.get(year);
				}
			}
		}
		return map.get(new IRFieldMapKey(fieldName.name(), year))[year-2009];
	}	

	public Map<IRFieldMapKey, String> getPropertyToFieldMap(int year) {
		Map<IRFieldMapKey, String> map = new HashMap<IRFieldMapKey, String>();
		for (IR10Fields field : IR10Fields.values()) {
			map.put(new IRFieldMapKey(field.name(), year), getFieldName(field, year));
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

	public static IRFieldMapper instance() {
		return new IR10FieldMapper();
	}
}
