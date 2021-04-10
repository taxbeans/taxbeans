package com.github.taxbeans.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSVParser {

    final static Logger logger = LoggerFactory.getLogger(CSVParser.class);

	private static final String DELIMITER_CODE = "____DELIMITER____";

	private String characterSet = "UTF-8";

	private boolean ignoreByteOrderMark;

	private char delimiter = ',';

	private boolean headerRow;

	private String preprocess(String s) {
		if (s.indexOf("\"") == -1)
			return s;
		char[] characters = s.toCharArray();
		boolean insideQuote = false;
		StringBuilder sb = new StringBuilder();
		for (char c : characters) {
			if (c == '"')
				insideQuote = !insideQuote;
			else if (insideQuote && c == delimiter)
				sb.append(DELIMITER_CODE + "");
			else
				sb.append(c);
		}
		return sb.toString();
	}

	public List<String[]> parseFile(String filename) {
		return this.parseFile(filename, false);
	}
	
	public List<String[]> parseFile(String filename, boolean skipHeader) {
		try {
			File file = new File(filename);
			if (!file.exists()) {
				throw new FileNotFoundException(file.toString());
			}
			return parseFile(new FileInputStream(file), skipHeader);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public List<String[]> parseFile(InputStream input, boolean skipHeader) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(input, characterSet));
			if (this.isIgnoreByteOrderMark())
				br.read();
			//BufferedReader br = new BufferedReader(new FileReader(filename));
			List<String[]> lines = new ArrayList<String[]>();
			String line = br.readLine();
			while (line != null) {
				if (!line.trim().equals(""))
					if (headerRow)
						headerRow = false;
					else {
						if (!skipHeader) {
							lines.add(parseCSV(line, false, true));
						} else {
							skipHeader = false;
						}
					}
				line = br.readLine();
			}
			return lines;
		} catch (Exception e ) {
			throw new IllegalStateException(e);
		}
	}

	public String[] parseCSV(String line, boolean escapeXML, boolean trim) {
		String preprocess = preprocess(line);
		//logger.info("about to split: " + preprocess);
		String[] fields = preprocess.split(delimiter + "", -1);  //-1 prevents trailing delimiters from being ignored
		//logger.info("num fields = " + fields.length);
		int count = 0;
		for (String token : fields) {
			fields[count] = token.replaceAll(DELIMITER_CODE, delimiter + "");
			if (escapeXML)
				fields[count] = fields[count].replaceAll("&","&amp;");
			if (trim)
			  fields[count] = fields[count].trim();
			count++;
		}
		return fields;
	}

	public void main(String[] args) {
		String test = "a,b,c,'d,&d',e".replaceAll("'", "\"");
		logger.info(String.valueOf(Arrays.asList(parseCSV(test, true, true))));
	}

	public String getCharacterSet() {
		return characterSet;
	}

	public void setCharacterSet(String characterSet) {
		this.characterSet = characterSet;
	}

	public boolean isIgnoreByteOrderMark() {
		return ignoreByteOrderMark;
	}

	public void setIgnoreByteOrderMark(boolean ignoreByteOrderMark) {
		this.ignoreByteOrderMark = ignoreByteOrderMark;
	}

	public char getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(char delimiter) {
		this.delimiter = delimiter;
	}

	public static CSVParser newInstance() {
		return new CSVParser();
	}

	public CSVParser withHeaderRow() {
		headerRow = true; return this;
	}
}
