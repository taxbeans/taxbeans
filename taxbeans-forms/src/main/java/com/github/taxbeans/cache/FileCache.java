package com.github.taxbeans.cache;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class FileCache {

	/*
	 * Gets the cache location
	 * (creates the directory structure if it doesn't exit)
	 */
	public static File getCacheLocation() {
		String userHome = System.getProperty("user.home");
		File cacheDir = new File(new File(userHome), ".cache");
		if (!cacheDir.exists()) {
			cacheDir.mkdir();
		}
		File cacheLocation = new File(cacheDir, "taxbeans");
		if (!cacheLocation.exists()) {
			cacheLocation.mkdir();
		}
		return cacheLocation;
	}
	
	public static File obtainFileFromCache(String name) {
		File obtained = new File(getCacheLocation(), name);
		if (!obtained.exists()) {
			System.out.println(Arrays.asList(getCacheLocation().list()));
			throw new RuntimeException("File does not exist");
		}
		return obtained;
	}

	public static void writeStringToCache(String filename, String data) {
		File f = new File(getCacheLocation(), filename);
		try {
			f.createNewFile();
			Files.writeString(f.toPath(), data, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public static void copyFileIntoCache(File file) {
		File f = new File(getCacheLocation(), file.getName());
		try {
			Files.copy(file.toPath(),f.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static File newOrReplaceExistingFileInCache(String name) {
		File obtained = new File(getCacheLocation(), name);
		return obtained;
	}
}
