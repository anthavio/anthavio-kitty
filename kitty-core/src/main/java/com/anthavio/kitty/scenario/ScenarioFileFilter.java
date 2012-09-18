package com.anthavio.kitty.scenario;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

public class ScenarioFileFilter extends javax.swing.filechooser.FileFilter implements FileFilter, FilenameFilter {

	private final String prefix;
	private final boolean directories;

	public ScenarioFileFilter(String prefix, boolean directories) {
		this.prefix = prefix;
		this.directories = directories;
	}

	@Override
	public boolean accept(File file) {
		String name = file.getName();
		if (file.isFile() && name.startsWith(prefix) && name.endsWith("xml")) {
			return true;
		}
		if (file.isDirectory() && directories) {
			return name.equals("CVS") == false && name.equals(".svn") == false;
		}
		return false;
	}

	@Override
	public boolean accept(File dir, String name) {
		return accept(new File(dir, name));
	}

	@Override
	public String getDescription() {
		return "Kitty scenario";
	}

}
