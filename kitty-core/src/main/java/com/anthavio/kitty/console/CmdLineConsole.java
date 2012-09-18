package com.anthavio.kitty.console;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anthavio.aspect.ApiPolicyOverride;
import com.anthavio.kitty.Kitty;
import com.anthavio.kitty.KittyException;
import com.anthavio.kitty.KittyOptions;
import com.anthavio.kitty.model.DirectoryItem;
import com.anthavio.kitty.model.DirectoryModel;

/**
 * @author vanek
 */
@ApiPolicyOverride
public class CmdLineConsole {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	private final Map<String, Command> cmdMap = new HashMap<String, Command>();

	//aktualni adresar
	private DirectoryModel actual;

	private KittyOptions options;

	private Kitty kitty;

	public CmdLineConsole(Kitty kitty) {
		this.kitty = kitty;
		this.options = kitty.getOptions();
	}

	public void addCommand(Command command) {
		List<String> keys = command.getInfo().getKeys();
		for (String key : keys) {
			if (this.cmdMap.containsKey(key)) {
				log.warn("Duplicate command key " + key + " old " + cmdMap.get(key) + " new " + command);
			} else {
				log.debug("Adding command key " + key + " for " + command);
				this.cmdMap.put(key, command);
			}
		}
	}

	/**
	 * Start Tool
	 */
	public void startConsole() {
		Map<String, Command> commands = kitty.getSpringContext().getBeansOfType(Command.class);
		for (Command command : commands.values()) {
			addCommand(command);
		}
		File initDir = options.getInitialDir();
		if (initDir == null || initDir.getName().equals("") || initDir.exists() == false) {
			options.setInitialDir(new File(System.getProperty("user.dir")));
		}
		actual = kitty.list(options.getInitialDir());

		printHelp();
		interactive();
	}

	private void interactive() {
		BufferedReader stdInReader = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			try {
				List<DirectoryItem> items = printDirList(actual);
				System.out.print("Enter command: ");

				String line = stdInReader.readLine();
				if (line != null) {
					line = line.trim();
					if ("".equals(line)) {
						continue; //ENTER pressed
					}
				} else {
					shutdown();//Ctrl+C -> exit
					//break; 
				}
				Scanner scanner = new Scanner(line);
				String cmdKey = scanner.next();
				if (builtinCmd(cmdKey, scanner)) {
					//handled already
				} else if (isNumber(cmdKey)) {
					//number direct input -> change directory or execute file
					int number = Integer.parseInt(cmdKey);
					if (number < 0 || number > items.size()) {
						System.out.println("Out of range [0 - " + items.size() + "]");
						continue;
					}
					if (number == 0) {
						actual = kitty.list(new File(actual.getPath(), ".."));
					} else {
						DirectoryItem item = items.get(number - 1);
						if (item.getFile().isDirectory()) {
							actual = kitty.list(item.getFile()); //just change directory
						} else {
							cmdMap.get(ExecuteCmd.KEY).execute(item, null); //execute file
						}
					}

				} else {
					Command command = cmdMap.get(cmdKey);
					if (command == null) {
						System.out.println("Unknown command '" + cmdKey + "'");
					} else {
						command.execute(new DirectoryItem(actual.getPath()), scanner);
					}
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
			/*
			System.out.print("Press ENTER: ");
			try {
				stdInReader.readLine(); //read ENTER
			} catch (IOException iox) {
				iox.printStackTrace();
			}
			*/
		}
	}

	private boolean builtinCmd(String cmdKey, Scanner scanner) {
		if (cmdKey.equals("cd")) {
			changeDirectory(scanner);
			return true;
		} else if (cmdKey.equals("q") || cmdKey.equals("quit")) {
			shutdown();
			return true;
		} else if (cmdKey.equals("o") || cmdKey.equals("options")) {
			System.out.println(options);
			return true;
		} else if (cmdKey.equals("h") || cmdKey.equals("help")) {
			printHelp();
			return true;
		} else {
			return false;
		}
	}

	private void changeDirectory(Scanner scanner) {
		String path = scanner.next();
		File target;
		if (path.startsWith("/") || path.charAt(1) == ':') {
			target = new File(path);
		} else {
			target = new File(actual.getPath(), path);
		}

		String canonicalPath;
		try {
			canonicalPath = target.getCanonicalPath();
		} catch (IOException iox) {
			throw new KittyException(iox);
		}

		if (target.exists() == false) {
			System.out.println("Target directory does not exist: " + canonicalPath);
			return;
		}
		if (target.isDirectory() == false) {
			System.out.println("Target is not a directory: " + canonicalPath);
			return;
		}
		//OK - update global state variable
		System.out.println("cd " + canonicalPath);
		actual = kitty.list(target);
	}

	public void shutdown() {
		System.out.println("Terminating Kitty");
		//ContextHelper.closeContext(springContext);
		System.exit(0);
	}

	private void printHelp() {
		System.out.println("Enter letter to run command or number to execute test scenario");
		System.out.println();
		System.out.println("Commmand list:");
		System.out.println("<number>\t - Execute scenario/Change directory");
		System.out.println("cd <path>\t - Change directory");
		System.out.println("h \t - Print this help");
		System.out.println("o \t - Print command line options");
		System.out.println("q \t - Quit Kitty");

		Collection<Command> commands = cmdMap.values();
		for (Command baseCmd : commands) {
			System.out.println(baseCmd.getInfo().getKeys().get(0) + " \t - " + baseCmd.getInfo().getDescription());
		}
	}

	private List<DirectoryItem> printDirList(File dir) throws IOException {
		if (!dir.exists()) {
			System.out.println("Directory does not exist " + dir.getAbsolutePath());
			dir = new File("."); //go to working dir
		}

		DirectoryModel model = kitty.list(dir);
		return printDirList(model);
	}

	private List<DirectoryItem> printDirList(DirectoryModel model) throws IOException {

		System.out.println(model.getPath().getCanonicalPath());
		System.out.println("0: [..]");

		//print numbers nicely aligned
		List<DirectoryItem> items = model.getItems();
		int maxNumbers = String.valueOf(items.size()).length();
		for (int i = 1; i <= items.size(); i++) {
			File file = items.get(i - 1).getFile();
			StringBuilder sb = new StringBuilder();
			int numbers = String.valueOf(i).length();
			while (maxNumbers > numbers) {
				sb.append(' ');
				++numbers;
			}
			sb.append(i);
			sb.append(": ");
			if (file.isDirectory()) {
				sb.append('[').append(file.getName()).append(']');
			} else {
				sb.append(file.getName());
			}
			System.out.println(sb.toString());
		}
		return items;
	}

	private boolean isNumber(String string) {
		if (string != null && string.length() != 0) {
			for (int i = 0; i < string.length(); ++i) {
				if (Character.isDigit(string.charAt(i)) == false) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}
}