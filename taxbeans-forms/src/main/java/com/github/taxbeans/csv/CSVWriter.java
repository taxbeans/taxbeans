package com.github.taxbeans.csv;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

public class CSVWriter {

    public static void writeCSV(File file, List<String[]> parseFile) {
        try {
        FileWriter fw = new FileWriter(file);
        PrintWriter pw = new PrintWriter(fw);
        for (Object[] rows : parseFile) {
            int i = 0;
            for (Object column : rows) {
                i++;
                pw.print(String.valueOf(column));
                if (i < rows.length)
                  pw.print(",");
            }
            pw.println();
        }
        pw.close();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static void writeCSV(String filename, List<String[]> parseFile) {
        writeCSV(new File("target/" + filename), parseFile);
    }

    public static void writeCSV(String filename, Object[][] parseFile) {
        try {
        FileWriter fw = new FileWriter("target/" + filename);
        PrintWriter pw = new PrintWriter(fw);
        for (Object[] rows : parseFile) {
            int i = 0;
            for (Object column : rows) {
                i++;
                pw.print(String.valueOf(column));
                if (i < rows.length)
                  pw.print(",");
            }
            pw.println();
        }
        pw.close();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

	public static String formatString(String[] titles) {
		String result = titles[0];
		int count = 0;
		for (String s : titles) {
			count++;
			if (count == 1)
				continue;
			result = result + "," + s;
        }
		return result;
	}
}
