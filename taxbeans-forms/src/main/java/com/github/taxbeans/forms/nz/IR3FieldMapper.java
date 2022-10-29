package com.github.taxbeans.forms.nz;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.taxbeans.model.nz.Salutation;

public class IR3FieldMapper implements IRFieldMapper {

	private static final int START_YEAR_OFFSET_FOR_CSV = 2011;

	private static final int START_YEAR_OFFSET_FOR_CSV_LEGACY = 2016;

	final static Logger LOG = LoggerFactory.getLogger(IR3FieldMapper.class);

	private static volatile Map<Integer, Map<IRFieldMapKey, String[]>> yearMap = new ConcurrentHashMap<Integer, Map<IRFieldMapKey, String[]>>();

	private static String csvMappingFileName;

	public static String getFieldName(IR3Fields fieldName, int year) {
		return getFieldNameViaString(fieldName.name(), year);
	}

	private static String getFieldNameViaString(String fieldName, int year) {
		boolean newerVersion = year >= 2019 || year == 2017 || year == 2016 || year == 2015 || year == 2014
				|| year == 2013 || year == 2012;
		int startYearOffset = newerVersion ? START_YEAR_OFFSET_FOR_CSV : START_YEAR_OFFSET_FOR_CSV_LEGACY;
		Map<IRFieldMapKey, String[]> map = yearMap.get(year);
		if (map == null) {
			synchronized (IR3FieldMapper.class) {
				map = yearMap.get(year);
				if (map == null) {
					if (newerVersion) {
						csvMappingFileName = "ir3-fields-v2.csv";
					} else {
						csvMappingFileName = "ir3-fields.csv";
					}
					InputStream resource = IR3FieldMapper.class.getClassLoader()
							.getResourceAsStream(csvMappingFileName);
					map =  IRFieldMapUtils.populateMap(resource, year);
					yearMap.put(year, map);
				}
			}
		}
		int i = year - startYearOffset;
		String[] strings = map.get(IRFieldMapUtils.getMapKey(fieldName, year));
		if (strings == null) {
			for (Entry<IRFieldMapKey, String[]> entry : map.entrySet()) {
				LOG.debug(entry.getKey() + " -> " + entry.getValue());
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

	public Map<IRFieldMapKey, String> getPropertyToFieldMap(int year) {
		Map<IRFieldMapKey, String> map = new HashMap<IRFieldMapKey, String>();
		for (IR3Fields field : IR3Fields.values()) {
			if (year > 2018 && field.name().contains("2018")) {
				// workaround for field naming issue
				continue;
			}
			map.put(IRFieldMapUtils.getMapKey(field.name(), year), getFieldName(field, year));
		}
		return map;
	}

	public static String getCsvMappingFileName() {
		return csvMappingFileName;
	}

	public static IRFieldMapper instance() {
		return new IR3FieldMapper();
	}
}
