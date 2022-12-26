package com.github.taxbeans.cache;

import java.io.File;
import java.io.FilenameFilter;

public class SearchFiles implements FilenameFilter {

	private String extension;

	public SearchFiles(String ext) {
		this.extension = ext;
	}

	@Override
	public boolean accept(File folder, String name) {
		if (name.lastIndexOf('.') > 0) {
			// get last index for '.'
			int lastIndex = name.lastIndexOf('.');

			// get extension
			String str = name.substring(lastIndex);

			// matching extension
			if (str.equalsIgnoreCase(extension)){
				return true;
			}
		}
		return false;
	}
}
