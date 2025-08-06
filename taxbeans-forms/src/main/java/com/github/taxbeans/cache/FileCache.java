package com.github.taxbeans.cache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileCache {

	private static volatile String cacheName = "taxbeans";

	public static volatile File sourceControlPath = null;

	public static Logger LOGGER = LoggerFactory.getLogger(FileCache.class);

	public static void setCacheName(String cacheName1) {
		cacheName = cacheName1;
	}

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
		File cacheLocation = new File(cacheDir, cacheName);
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

	public static File obtainNewOrExistingFileFromCache(String name) {
		File obtained = new File(getCacheLocation(), name);
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

	public static void copyFileIntoCache(String s) {
		copyFileIntoCache(new File(s));
	}

	public static void copyFileIntoCache(File file) {
		File f = new File(getCacheLocation(), file.getName());
		try {
			Files.copy(file.toPath(), f.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void copyFileFromCacheToSourceControl(String file) {
		copyFileFromCacheToSourceControl(new File(file));
	}

	public static void copyFileFromCacheToSourceControl(File file) {
		File f = new File(getCacheLocation(), file.getName());
		try {
			File destination = new File(sourceControlPath, file.getName());
			Files.copy(f.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static File newOrReplaceExistingFileInCache(String name) {
		File obtained = new File(getCacheLocation(), name);
		return obtained;
	}

	public static void populateCacheFromCurrentDirectory(String string) {
		copyFileIntoCache(new File(string));
	}

	public static void copyAllJsonFilesToSourceControl(boolean verbose) {
		SearchFiles searchFiles = new SearchFiles(".json");
		File folder = getCacheLocation();

		String[] files = folder.list(searchFiles);
		for (String f : files) {
			if (verbose) {
				LOGGER.info("Copying {} to source control: {}", f, getSourceControlPath());
			}
			copyFileFromCacheToSourceControl(f);
		}
	}

	public static File getSourceControlPath() {
		return sourceControlPath;
	}

	public static void setSourceControlPath(File sourceControlPath) {
		FileCache.sourceControlPath = sourceControlPath;
	}

	public static void setSourceControlPath(String sourceControlPath) {
		setSourceControlPath(new File(sourceControlPath));
	}

	public static File writeStreamToCache(String templateFile, InputStream stream) {
		File folder = getCacheLocation();
		File form = new File(folder, templateFile);
		try {
			Files.copy(stream, form.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return form;
	}

	public static File getUserHome() {
		String os = System.getProperty("os.name").toLowerCase();
		boolean isWindows = os.contains("win");
		File location = new File(System.getProperty("user.home"));
		if (isWindows) {
			location = new File(System.getenv("USERPROFILE"));
		}
		return location;
	}

	public static void setSourceControlPathRelativeToHome(String string) {
		File f = new File(getUserHome(), string);
		setSourceControlPath(f);
	}

	public static File getHomeFolder() {
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("win")) {
			return new File(System.getenv("USERPROFILE"));
		} else {
			return new File(System.getProperty("user.home"));
		}
	}
}
