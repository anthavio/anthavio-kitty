/**
 * 
 */
package com.anthavio.kitty.swing;

import java.awt.Component;
import java.io.File;

/**
 * @author vanek
 *
 */
public abstract class ToolTab {

	protected final File file;

	protected final ToolFrame toolFrame;

	public ToolTab(ToolFrame toolFrame, File file) {
		this.toolFrame = toolFrame;
		this.file = file;
	}

	public String getTitle() {
		return file.getName();
	}

	public abstract Component getContent();
}
