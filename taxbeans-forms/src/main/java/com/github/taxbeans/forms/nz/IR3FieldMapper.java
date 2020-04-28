package com.github.taxbeans.forms.nz;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.taxbeans.model.nz.Salutation;

public class IR3FieldMapper {

	private static final int START_YEAR_OFFSET_FOR_CSV = 2014;

	final static Logger LOG = LoggerFactory.getLogger(IR3FieldMapper.class);

	private static volatile Map<String, String[]> map = null;

	private static String csvMappingFileName;

	public static String getFieldName(IR3Fields fieldName, int year) {
		return getFieldNameViaString(fieldName.name(), year);
	}

	private static String getFieldNameViaString(String fieldName, int year) {
		if (map == null) {
			synchronized (IR3FieldMapper.class) {
				if (map == null) {
					csvMappingFileName = (year == 2019 || year == 2017 || year == 2016 || year == 2015)
							? "ir3-fields-v2.csv" : "ir3-fields.csv";
					InputStream resource = 
							IR3FieldMapper.class.getClassLoader()
							.getResourceAsStream(csvMappingFileName);
					map = IRFieldMapperUtils.populateMap(resource, year);
				}
			}
		}
		int i = year-START_YEAR_OFFSET_FOR_CSV;
		String[] strings = map.get(fieldName);
		if (strings == null) {
			for (Entry<String, String[]> entry : map.entrySet()) {
				LOG.warn(entry.getKey() + " -> " + entry.getValue());
			}
			LOG.warn(fieldName + " resulted in null Strings, mapping to null");
			return null;
		}
		return strings[i];
	}

	public static String getSalutationFieldValue(Salutation salutation, int year) {
		String fieldName = String.format("%1$s.%2$s", IR3Fields.salutation.name(), salutation.name());
		return getFieldNameViaString(fieldName, year);
	}

	public static String getBooleanFieldValue(String name, boolean value, int year) {
		String fieldName = String.format("%1$s.%2$s", name, String.valueOf(value));
		return getFieldNameViaString(fieldName, year);
	}

	public static Map<String, String> getPropertyToFieldMap(int year) {
		Map<String, String> map = new HashMap<String, String>();
		for (IR3Fields field : IR3Fields.values()) {
			if (year > 2018 && field.name().contains("2018")) {
				//workaround for field naming issue
				continue;
			}
			map.put(field.name(), getFieldName(field, year));
		}
		return map;
	}

	public static Map<String, String> getFieldToPropertyMap(int year) {
		Map<String, String> map = new HashMap<String, String>();
		for (IR3Fields field : IR3Fields.values()) {
			if (year > 2018 && field.name().contains("2018")) {
				//workaround for field naming issue
				continue;
			}
			map.put(getFieldName(field, year), field.name());
		}
		return map;
	}

	public static String getCsvMappingFileName() {
		return csvMappingFileName;
	}
}
