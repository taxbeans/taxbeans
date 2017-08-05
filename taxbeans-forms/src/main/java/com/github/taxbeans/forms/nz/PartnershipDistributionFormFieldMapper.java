package com.github.taxbeans.forms.nz;

import java.io.InputStream;
import java.util.Map;

public class PartnershipDistributionFormFieldMapper {

	private static volatile Map<String, String[]> map = null;

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
		return map.get(fieldName.name())[i];
	}
}
