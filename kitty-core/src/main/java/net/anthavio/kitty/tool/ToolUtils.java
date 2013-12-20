/**
 * 
 */
package net.anthavio.kitty.tool;

import java.io.File;
import java.io.FileFilter;
import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;

/**
 * @author vanek
 *
 */
public class ToolUtils {

	private static final ToolFileComparator TFC = new ToolFileComparator();

	public static File[] fileList(File dir, FileFilter filter) {
		File[] fileList = dir.listFiles(filter);
		Arrays.sort(fileList, TFC);
		return fileList;
	}

	private static class ToolFileComparator implements Comparator<File> {
		private final Collator c = Collator.getInstance();

		@Override
		public int compare(File file1, File file2) {

			if (file1 == file2) {
				return 0;
			}

			if (file1.isDirectory()) {
				if (file2.isDirectory()) {
					//both directories
					return c.compare(file1.getName(), file2.getName());
				} else {
					return -1; //directory is less then file
				}
			} else {
				if (file2.isDirectory()) {
					return 1; //file is more then directory
				} else {
					//both files
					return c.compare(file1.getName(), file2.getName());
				}
			}
		}
	}
}
