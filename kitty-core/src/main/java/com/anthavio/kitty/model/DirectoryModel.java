/**
 * 
 */
package com.anthavio.kitty.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.anthavio.aspect.ApiPolicyOverride;
import com.anthavio.kitty.KittyException;

/**
 * @author vanek
 *
 */
public class DirectoryModel {

	private File path;

	private List<DirectoryItem> items;

	public DirectoryModel() {
		//this.items = LazyList.decorate(new ArrayList<DirListItem>(), FactoryUtils.instantiateFactory(DirListItem.class));
		this.items = new ArrayList<DirectoryItem>();
	}

	public DirectoryModel(File path, File[] files) {
		this.path = path;
		this.items = new ArrayList<DirectoryItem>(files.length);
		for (File file : files) {
			this.items.add(new DirectoryItem(file));
		}
	}

	public DirectoryModel(File path, List<DirectoryItem> items) {
		this.path = path;
		this.items = items;
	}

	public boolean isRunning() {
		for (DirectoryItem item : items) {
			if (item.isRunning()) {
				return true; // unfinished item = unfinished model 
			}
		}
		return false;
	}

	public void setStarted() {
		boolean firstSelected = false;
		for (int i = 0; i < items.size(); ++i) {
			DirectoryItem item = items.get(i);
			if (item.isSelected()) {
				if (firstSelected) {
					item.setExecution(null);
				} else {
					item.setStarted();
					firstSelected = true;
				}
				item.setExecution(null);
			}
		}
	}

	public File[] getSelectedFiles() {
		List<File> fileList = new ArrayList<File>(items.size());
		for (DirectoryItem item : items) {
			if (item.isSelected()) {
				fileList.add(item.getFile());
			}
		}
		File[] files = new File[fileList.size()];
		return fileList.toArray(files);
	}

	public File[] getAllFiles() {
		List<File> fileList = new ArrayList<File>(items.size());
		for (DirectoryItem item : items) {
			fileList.add(item.getFile());
		}
		File[] files = new File[fileList.size()];
		return fileList.toArray(files);
	}

	/**
	 * @return Started Date of the first selected Item
	 */
	public Date getStarted() {
		for (DirectoryItem item : items) {
			if (item.isSelected() && item.getExecution() != null) {
				return item.getExecution().getStarted();
			}
		}
		return null;
	}

	/**
	 * @return Finished Date of the last selected Item
	 */
	public Date getFinished() {
		Date lastFinished = null;
		for (DirectoryItem item : items) {
			if (item.isSelected() && item.getExecution() != null) {
				if (item.getExecution().getStarted() != null && item.getExecution().getEnded() == null) {
					return null; // unfinished item = unfinished model 
				} else {
					lastFinished = item.getExecution().getEnded();
				}
			}
		}
		return lastFinished;
	}

	public DirectoryItem getItemByFile(File file) {
		for (DirectoryItem item : items) {
			if (item.getFile().equals(file)) {
				return item;
			}
		}
		return null;
	}

	@ApiPolicyOverride
	public List<DirectoryItem> getItems() {
		return items;
	}

	public void setItems(List<DirectoryItem> items) {
		this.items = items;
	}

	public File getPath() {
		return path;
	}

	public void setPath(File path) {
		this.path = path;
	}

	public String getCannonicalPath() {
		try {
			return path.getCanonicalPath();
		} catch (IOException iox) {
			throw new KittyException(iox);
		}
	}

}
