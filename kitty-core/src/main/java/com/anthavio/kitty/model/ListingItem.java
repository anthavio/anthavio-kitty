/**
 * 
 */
package com.anthavio.kitty.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author vanek
 *
 */
public class ListingItem {

	private final File path;

	private final ListingItem parent;

	private List<ListingItem> children;

	private boolean loaded = false;

	private ListingItem(File file, ListingItem parent) {
		if (file == null) {
			throw new IllegalArgumentException("File must not be null");
		}
		if (file.exists()) {
			throw new IllegalArgumentException("Path does not exist " + file);
		}
		this.path = file;
		this.parent = parent;
		if (file.isDirectory()) {
			loaded = false;
		} else {
			loaded = true;
		}
	}

	private ListingItem(File file, ListingItem parent, List<ListingItem> children) {
		this(file, parent);

		if (!file.isDirectory()) {
			throw new IllegalArgumentException("Only directory may have children");
		}

		this.children = children;
		this.loaded = true;
	}

	public List<ListingItem> getChildren() {
		if (!loaded) {
			children = new ArrayList<ListingItem>();
			//items
		}
		return children;
	}

	public File getPath() {
		return path;
	}

	public ListingItem getParent() {
		return parent;
	}

	public boolean isLoaded() {
		return loaded;
	}

	public boolean isDirectory() {
		return path.isDirectory();
	}

}
