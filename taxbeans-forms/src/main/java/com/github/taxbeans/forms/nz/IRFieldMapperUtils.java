package com.github.taxbeans.forms.nz;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class IRFieldMapperUtils {

	public static Map<String, String[]> populateMap(InputStream csv, int year) {
		Map<String, String[]> map = new HashMap<>();
		Reader in;
		try {
			in = new BufferedReader(new InputStreamReader(csv));
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
		return map;
	}
}
