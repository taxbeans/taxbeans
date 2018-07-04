package com.github.taxbeans.forms.nz;

import java.io.InputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PartnershipDistributionFormFieldMapper {

	private static volatile Map<String, String[]> map = null;
	
	final static Logger logger = LoggerFactory.getLogger(IR7FieldMapper.class);

	public static String getFieldName(PartnershipDistributionFormFieldEnum fieldName, int year) {
		if (map == null) {
			synchronized (IR7FieldMapper.class) {
				if (map == null) {
					InputStream resource = 
							IR7FieldMapper.class.getClassLoader()
							.getResourceAsStream("ir7p-fields.csv");
					map = IRFieldMapperUtils.populateMap(resource, year);
				}
			}
		}
		int i = year-2009;
		String[] strings = map.get(fieldName.name());
		if (strings.length == 0) {
			logger.error("No matching fields in IR7 Distribution form {} for: {}", year, fieldName.name());
		}
		if (strings.length <= i) {
			logger.error("No mapping column in IR7 Distribution mapping for year: {}", year);
		}
		return strings[i];
	}
}
