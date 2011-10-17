package org.arp.javautil.fileutils.meb;


import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

/**
 * The methods...
 *     
 *     getLastModified(...)
 *     
 * ... are useful for finding the latest file in a directory when creating multiple files with 
 * similar naming convention. For example, if creating several files of the format:
 * 
 *     filename_hhmmddyyyy.txt
 * 
 * ... and you want to use the last one. Then use code like these:
 * 
 *     File file = FileUtil.getLastModified("/etc", "labs_", "txt");
 *     
 *     String filename = FileUtil.getLastModifiedFilename("/etc", "labs_", "txt");
 * 
 * @author Michael E. Brown
 * 
 */
public class FileUtil {

	/**
	 * Returns a File object for the last modified file in a directory.
	 * 
	 * @param directory
	 *            the directory to search
	 * @param regex
	 *            the regular expression that will be used to match filename
	 * @return returns a File of the latest file
	 */
	public static File getLastModified(String directory, String regex) {
	
		File dir = new File(directory);
		File[] files = dir.listFiles(new DirFilter(regex));
		File rtnFile = null;

		long lastModified = 0;
		for (File file : files) {
			if (file.lastModified() > lastModified) {
				lastModified = file.lastModified();
				rtnFile = file;
			}
		}
		return rtnFile;
	}

	/**
	 * Returns the filename of the last modified file in a directory.
	 * 
	 * @param directory
	 *            the directory to search
	 * @param prefix
	 *            the 'startswith' text for the filename
	 * @param extension
	 *            the extension of the filename (without '.')
	 * @return 
	 */
	public static String getLastModifiedFilename(String directory, String regex) {
		return getLastModified(directory, regex).getName();
	}

	/**
	 * Returns a File object for the last modified file in a directory.
	 * 
	 * @param directory
	 *            the directory to search
	 * @param prefix
	 *            the 'startswith' text for the filename
	 * @param extension
	 *            the extension of the filename (without '.')
	 * @return 
	 */
	public static File getLastModified(String directory, String prefix,
			String extension) {
		String regex = prefix + ".+\\." + extension;
		return getLastModified(directory, regex);
	}

	/**
	 * Returns the filename of the last modified file in a directory.
	 * 
	 * @param directory
	 *            the directory to search
	 * @param prefix
	 *            the 'startswith' text for the filename
	 * @param extension
	 *            the extension of the filename (without '.')
	 * @return 
	 */
	public static String getLastModifiedFilename(String directory, String prefix,
			String extension) {
		return getLastModified(directory, prefix, extension).getName();
	}

	static class DirFilter implements FilenameFilter {
		
		private Pattern pattern;

		public DirFilter(String regex) {
			pattern = Pattern.compile(regex);
		}

		public boolean accept(File dir, String name) {
			return pattern.matcher(new File(name).getName()).matches();
		}
	}

}
