package net.anthavio.kitty.console;

import java.io.File;
import java.util.Scanner;

import net.anthavio.aspect.ApiPolicyOverride;
import net.anthavio.kitty.model.DirectoryItem;

import org.testng.Assert;


/**
 * @author vanek
 * 
 * Executes file if file is input
 * Runs recursively if input is directory 
 */
public abstract class RecursiveCmd extends Command {

	@Override
	@ApiPolicyOverride
	public void execute(DirectoryItem item, Scanner scanner) throws Exception {
		if (item.getFile().isFile()) {
			executeFile(item.getFile());

		} else if (scanner.hasNext() == false) {
			execute(item.getFile());

		} else {
			File[] files = listFiles(item);
			int idx = scanner.nextInt();
			if (idx > 0 || idx < files.length) {
				File file = files[idx];
				execute(file);
			} else {
				System.out.println("Outside range [1 - " + (files.length - 1) + "]");
			}
		}
	}

	private File execute(File file) throws Exception {
		if (!file.exists()) {
			log.error("Path does not exist: " + file.getAbsolutePath());
			Assert.fail("Path does not exist: " + file.getAbsolutePath());
		}
		if (file.isDirectory()) {
			File[] files = listFiles(file);
			log.debug("Directory " + file.getName() + " contains " + files.length
					+ " configuration files (or subdirectories)");
			for (File fileItem : files) {
				execute(fileItem);
			}
		} else {
			executeFile(file);
		}
		return file;
	}

	protected abstract void executeFile(File file) throws Exception;

}