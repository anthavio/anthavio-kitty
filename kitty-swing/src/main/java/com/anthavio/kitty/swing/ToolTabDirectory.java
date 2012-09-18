/**
 * 
 */
package com.anthavio.kitty.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.anthavio.kitty.model.DirectoryModel;

/**
 * @author vanek
 *
 */
public class ToolTabDirectory extends ToolTab {

	private JScrollPane scrollPane;
	private JTable table;

	public ToolTabDirectory(final ToolFrame toolFrame, File file) {
		super(toolFrame, file);

		DirectoryModel directoryModel = toolFrame.tool.list(file);
		ListingTableModel tableModel = new ListingTableModel(directoryModel);

		table = new JTable(tableModel) {

			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component cell = super.prepareRenderer(renderer, row, column);
				if (isCellSelected(row, column)) {
					cell.setBackground(new Color(0.4f, 0.4f, 0.4f));
				} else {
					if (row % 2 == 0) {
						cell.setBackground(Color.WHITE);
					} else {
						cell.setBackground(new Color(0.9f, 0.9f, 0.9f));
					}
				}
				Object value = getValueAt(row, column);
				if (value instanceof File) {
					Font font = cell.getFont();
					if (((File) value).isDirectory()) {
						cell.setFont(new Font(font.getFamily(), Font.BOLD, font.getSize()));
					} else {
						cell.setFont(new Font(font.getFamily(), Font.PLAIN, font.getSize()));
					}
				}
				return cell;
			};

		};
		scrollPane = new JScrollPane(table);

		final JPopupMenu popupSelect = new JPopupMenu();
		//TODO implement Select popup actions
		popupSelect.add("Select All");
		popupSelect.add("Select Files");
		popupSelect.add("Select Directories");
		popupSelect.add("Clear Selection");

		final JPopupMenu popupFile = new JPopupMenu();
		//TODO implement File popup actions
		popupFile.add(toolFrame.new OpenAction());
		popupFile.add("Rename");
		popupFile.add("Execute");
		popupFile.add("Validate");

		final JPopupMenu popupDirectory = new JPopupMenu();
		//TODO implement Directory popup actions
		popupDirectory.add(toolFrame.new OpenAction());
		popupDirectory.add("Rename");
		popupDirectory.add("Statictics");

		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					JTable table = (JTable) e.getSource();
					int row = table.rowAtPoint(e.getPoint());
					int column = table.columnAtPoint(e.getPoint());
					Object value = table.getValueAt(row, column);
					if (value instanceof File) {
						File file = (File) value;
						toolFrame.openFile(file);
					}
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {

				if (e.isPopupTrigger()) {
					JTable table = (JTable) e.getSource();

					int row = table.rowAtPoint(e.getPoint());
					int column = table.columnAtPoint(e.getPoint());

					if (!table.isRowSelected(row)) {
						table.changeSelection(row, column, true, false);
					}
					if (column == 0) {
						popupSelect.show(e.getComponent(), e.getX(), e.getY());
					} else {
						Object value = table.getValueAt(row, column);
						if (value instanceof File) {
							File file = (File) value;
							if (file.isDirectory()) {
								popupDirectory.show(e.getComponent(), e.getX(), e.getY());
							} else {
								popupFile.show(e.getComponent(), e.getX(), e.getY());
							}
						}
					}
				}
			}
		});

		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(table.getModel());
		table.setRowSorter(sorter);
		table.setRowSelectionAllowed(false);
		table.setColumnSelectionAllowed(false);
		table.getColumnModel().getColumn(0).setMaxWidth(45);
		table.getColumnModel().getColumn(2).setMaxWidth(40);
	}

	@Override
	public Component getContent() {
		return scrollPane;
	}

}
