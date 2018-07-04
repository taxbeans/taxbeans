package com.github.taxbeans.forms.nz;

import java.io.InputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IR7FieldMapper {

	final static Logger logger = LoggerFactory.getLogger(IR7FieldMapper.class);

	private static volatile Map<String, String[]> map = null;

	public static String getFieldName(IR7Fields fieldName, int year) {
		if (map == null) {
			synchronized (IR7FieldMapper.class) {
				if (map == null) {
					InputStream resource = 
							IR7FieldMapper.class.getClassLoader()
							.getResourceAsStream("ir7-fields.csv");
					map = IRFieldMapperUtils.populateMap(resource, year);
				}
			}
		}
		int i = year-2009;
		String[] strings = map.get(fieldName.name());
		if (strings.length == 0) {
			logger.error("No matching fields in IR7 {} for: {}", year, fieldName.name());
		}
		if (strings.length <= i) {
			logger.error("No mapping column in IR7 mapping for year: {}", year);
		}
		return strings[i];
	}
}
