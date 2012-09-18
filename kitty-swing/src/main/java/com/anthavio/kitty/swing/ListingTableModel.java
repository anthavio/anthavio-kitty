/**
 * 
 */
package com.anthavio.kitty.swing;

import java.io.File;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.anthavio.kitty.model.DirectoryItem;
import com.anthavio.kitty.model.DirectoryModel;

/**
 * @author vanek
 *
 */
public class ListingTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private String[] COLUMN_NAMES = { "Select", "Name", "Count" };

	private Class<?>[] COLUMN_CLASSES = { Boolean.class, File.class, Integer.class };

	private int[] counts;

	private DirectoryModel directoryModel;

	public ListingTableModel(DirectoryModel directoryModel) {
		this.directoryModel = directoryModel;
		List<DirectoryItem> items = directoryModel.getItems();
		this.counts = new int[items.size()];
		for (int i = 0; i < items.size(); ++i) {
			this.counts[i] = 0;
		}
	}

	@Override
	public int getRowCount() {
		return directoryModel.getItems().size();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return COLUMN_NAMES[columnIndex];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return COLUMN_CLASSES[columnIndex];
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 0;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return directoryModel.getItems().get(rowIndex).isSelected();
		case 1:
			return directoryModel.getItems().get(rowIndex).getFile();
		case 2:
			return counts[rowIndex];
		}
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			DirectoryItem item = directoryModel.getItems().get(rowIndex);
			Boolean b = (Boolean) aValue;
			if (b != null && b.booleanValue()) {
				item.setSelected(true);
			} else {
				item.setSelected(false);
			}
		} else {
			throw new IllegalArgumentException("Column " + COLUMN_NAMES[columnIndex] + " is not editable");
		}
	}

}
