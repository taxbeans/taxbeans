package com.github.taxbeans.forms.nz;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IR10FieldMapper {

	final static Logger logger = LoggerFactory.getLogger(IR10FieldMapperTest.class);

	private static volatile Map<String, String[]> map = null;

	public static String getFieldName(IR10Fields fieldName, int year) {
		if (map == null) {
			synchronized (IR10FieldMapperTest.class) {
				if (map == null) {
					map = new HashMap<String, String[]>();
					Reader in;
					try {
						in = new FileReader("target/classes/ir10-fields.csv");
						Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
						for (CSVRecord record : records) {
							List<String> values = new ArrayList<String>();
							for (String column : record) {
								values.add(column);
							}
							String[] line = values.toArray(new String[values.size()]);
							map.put(line[0], line);
						}
					} catch (Exception e) {
						throw new IllegalStateException(e);
					}
				}
			}
		}
		return map.get(fieldName.name())[year-2009];
	}
}
