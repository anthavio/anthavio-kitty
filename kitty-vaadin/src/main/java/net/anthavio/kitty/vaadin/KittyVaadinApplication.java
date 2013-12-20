/**
 * 
 */
package net.anthavio.kitty.vaadin;

import java.io.File;

import com.vaadin.Application;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.FilesystemContainer;
import com.vaadin.data.util.TextFileProperty;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Tree;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;

/**
 * @author vanek
 *
 */
public class KittyVaadinApplication extends Application {

	FilesystemContainer container = new FilesystemContainer(new File(System.getProperty("user.dir")), false);
	Table table = new Table(null, container);
	Tree tree = new Tree(null, container);
	TreeTable treetable = new TreeTable("Here's my File System", container);

	Label viewer = new Label("Select test scenario", Label.CONTENT_RAW);

	Button edit = new Button("Edit");

	@Override
	public void init() {
		final Window mainWindow = new Window("Kitty", new VerticalSplitPanel());
		//mainWindow.addComponent(tree);
		mainWindow.addComponent(treetable);
		VerticalLayout lo = new VerticalLayout();
		lo.addComponent(viewer);
		lo.addComponent(edit);
		mainWindow.addComponent(lo);
		setMainWindow(mainWindow);

		//tree.setItemCaptionPropertyId(FilesystemContainer.PROPERTY_NAME);

		edit.addListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				Window dialog = new Window("Edit selected", new DocumentEditor(viewer.getPropertyDataSource()));
				dialog.setModal(true);
				mainWindow.addWindow(dialog);
			}
		});

		treetable.setImmediate(true);
		treetable.setSizeFull();
		//treetable.setMultiSelect(true);
		treetable.setSelectable(true);
		/*
				treetable.addListener(new ItemClickEvent.ItemClickListener() {

					@Override
					public void itemClick(ItemClickEvent event) {
						Set<?> values = (Set<?>) treetable.getValue();
						if (values.size() > 0) {
							viewer.setValue(values.iterator().next());
						}
					}
				});
		*/
		treetable.addListener(new Property.ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				viewer.setPropertyDataSource(new TextFileProperty((File) treetable.getValue()));
			}
		});

		table.setImmediate(true); //send immediately to server
		table.setSizeFull();
		table.setSelectable(true);
		table.addListener(new Property.ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				viewer.setPropertyDataSource(new TextFileProperty((File) table.getValue()));
			}
		});
	}
}
