package com.anthavio.kitty.console;

import java.io.File;
import java.util.Scanner;

import javax.inject.Inject;

import com.anthavio.kitty.Kitty;
import com.anthavio.kitty.model.DirectoryItem;
import com.anthavio.kitty.model.DirectoryModel;

/**
 * @author vanek
 */
public abstract class Command {

	protected final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(getClass());

	@Inject
	protected Kitty kitty;

	public abstract void execute(DirectoryItem item, Scanner scanner) throws Exception;

	public abstract CmdInfo getInfo();

	protected File[] listFiles(DirectoryItem item) {
		DirectoryModel list = kitty.list(item);
		//console does not support file selecting
		return list.getAllFiles();
	}

	protected File[] listFiles(File file) {
		DirectoryModel list = kitty.list(file);
		//console does not support file selecting
		return list.getAllFiles();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
