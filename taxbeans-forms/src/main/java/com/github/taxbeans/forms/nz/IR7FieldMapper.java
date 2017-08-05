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
		return map.get(fieldName.name())[i];
	}
}
