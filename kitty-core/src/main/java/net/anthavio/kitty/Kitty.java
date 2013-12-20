package net.anthavio.kitty;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.anthavio.aspect.ApiPolicyOverride;
import net.anthavio.io.FileUtils;
import net.anthavio.kitty.console.CmdLineConsole;
import net.anthavio.kitty.model.DirectoryItem;
import net.anthavio.kitty.model.DirectoryModel;
import net.anthavio.kitty.scenario.Scenario;
import net.anthavio.kitty.scenario.ScenarioFileFilter;
import net.anthavio.kitty.tool.ToolUtils;
import net.anthavio.spring.ContextHelper;
import net.anthavio.util.DateUtil;
import net.anthavio.xml.jaxb.SimpleJaxbBinder;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


/**
 * Common Scenario, Tool and Embedded Server Launcher
 * 
 * @author vanek
 *
 */
public class Kitty implements ApplicationContextAware {

	private static final Logger log = LoggerFactory.getLogger(Kitty.class);

	public static final String FMT_DATETIME = "yyyy-MM-dd HH:mm:ss";

	public static final String formatDateTime() {
		return DateUtil.format(new Date(), Kitty.FMT_DATETIME);
	}

	public static final String formatDateTime(Date date) {
		return DateUtil.format(date, Kitty.FMT_DATETIME);
	}

	//stav adresaru si budeme drzet v globalni promenne
	private Map<File, DirectoryModel> dirMap = new HashMap<File, DirectoryModel>();

	private final SimpleJaxbBinder<Scenario> scenarioBinder;

	private final KittyOptions options;

	public Kitty(KittyOptions options, SimpleJaxbBinder<Scenario> scenarioBinder) {
		if (options == null) {
			throw new IllegalArgumentException("Null Kitty options");
		}
		this.options = options;

		if (scenarioBinder == null) {
			throw new IllegalArgumentException("Null scenario binder");
		}
		this.scenarioBinder = scenarioBinder;
	}

	private CmdLineConsole console;

	private ApplicationContext springContext;

	public KittyOptions getOptions() {
		return options;
	}

	public void init(String[] args) {
		CmdLineParser parser = new CmdLineParser(this.options);
		try {
			parser.parseArgument(args);
		} catch (CmdLineException clx) {
			parser.printUsage(System.err);
			throw new KittyException("Wrong program arguments: " + clx.getMessage());
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.springContext = applicationContext;
	}

	public ApplicationContext getSpringContext() {
		return this.springContext;
	}

	public void startConsole() {
		if (console == null) {
			console = new CmdLineConsole(this);
		}
		console.startConsole();
	}

	public void saveScenario(Scenario scenario, Writer writer) {
		scenarioBinder.save(scenario, writer);
	}

	public Scenario loadScenario(File file, boolean print) {
		String xml;
		try {
			if (print) {
				log.info("Loading scenario: " + file.getCanonicalPath());
			}
			xml = FileUtils.readFile(file);
			if (print) {
				log.info(xml);
			}
		} catch (IOException iox) {
			throw new KittyException("Error reading scenario " + file, iox);
		}
		Scenario scenario = scenarioBinder.load(xml);
		scenario.setScenarioFile(file);
		return scenario;
	}

	public DirectoryModel setSelected(DirectoryModel selectionModel) {
		DirectoryModel existing = dirMap.get(selectionModel.getPath());
		List<DirectoryItem> items = selectionModel.getItems();
		for (int i = 0; i < items.size(); ++i) {
			DirectoryItem item = items.get(i);
			DirectoryItem itemByFile = existing.getItems().get(i);
			itemByFile.setSelected(item.isSelected());
		}
		return existing;
	}

	public DirectoryModel list(DirectoryModel model) {
		return list(model.getPath());
	}

	public DirectoryModel list(DirectoryItem item) {
		return list(item.getFile());
	}

	public DirectoryModel list(File directory) {
		if (!directory.exists()) {
			throw new IllegalArgumentException("Target does not exist: " + directory.getAbsolutePath());
		}
		if (!directory.isDirectory()) {
			throw new IllegalArgumentException("Target is not directory: " + directory.getAbsolutePath());
		}
		DirectoryModel model = dirMap.get(directory);
		FileFilter filter = new ScenarioFileFilter(options.getScenarioPrefix(), true);
		File[] fileList = ToolUtils.fileList(directory, filter);
		if (model == null) {
			model = new DirectoryModel(directory, fileList);
			dirMap.put(directory, model);
		} else {
			List<DirectoryItem> items = model.getItems();
			mergeDirList(fileList, items);
		}
		return model;
	}

	private void mergeDirList(File[] listFiles, List<DirectoryItem> items) {
		for (int i = 0; i < listFiles.length; ++i) {
			File nFile = listFiles[i];
			if (i < items.size()) {
				File sFile = items.get(i).getFile();
				if (!nFile.equals(sFile)) {
					if (isIn(nFile, items, i)) {
						//sFile ubyl - ubrat zevnitr
						items.remove(i);
					} else {
						//nFile pribyl - pridat dovnitr
						items.add(i, new DirectoryItem(nFile));
					}
				}
			} else {
				//pridat na konec
				items.add(new DirectoryItem(nFile));
			}
		}
		//ubrat prebyvajici z konce
		int delLast = items.size() - listFiles.length;
		for (int i = 0; i < delLast; ++i) {
			items.remove(items.size() - 1);
		}
	}

	private boolean isIn(File nFile, List<DirectoryItem> items, int idxStart) {
		for (int i = idxStart; i < items.size(); ++i) {
			if (nFile.equals(items.get(i).getFile())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Start Embedded Server, Execute Scenario and exit VM
	 
	public static <T extends ScenarioBase> void execute(ServerFactory serverFactory, ScenarioFactory<T> scenarioFactory,
			T scenario) {
		startServer(serverFactory);
		execute(scenarioFactory, scenario);
	}
	*/

	public static Kitty setup(String[] args) {
		return setup("kitty-lib", args);
	}

	public static Kitty setup(String contextName, String[] args) {
		ApplicationContext spring = ContextHelper.i().locateContext(contextName);
		Kitty kitty = spring.getBean(Kitty.class);
		kitty.setApplicationContext(spring);
		kitty.init(args);
		return kitty;
	}

	/**
	 * Execute Scenario created by @param factory and exit VM
	 
	public static <T extends ScenarioBase> void execute(ScenarioFactory<T> factory, T scenario) {
		log.info("Booting Kitty spring context");
		factory.getKittyContext();
		SimpleJaxbBinder<T> scenarioBinder = factory.getScenarioBinder();
		execute(scenarioBinder, scenario);
	}
	 */

	@ApiPolicyOverride
	public static <T extends Scenario> void execute(T scenario) {
		ApplicationContext spring = ContextHelper.i().locateContext("kitty-lib");
		Kitty kitty = spring.getBean(Kitty.class);
		SimpleJaxbBinder<Scenario> binder = kitty.getScenarioBinder();
		execute(binder, scenario);
	}

	public SimpleJaxbBinder<Scenario> getScenarioBinder() {
		return scenarioBinder;
	}

	@ApiPolicyOverride
	public static <T extends Scenario> void execute(SimpleJaxbBinder<T> scenarioBinder, T scenario) {
		StringWriter out = new StringWriter();
		//marshall
		scenarioBinder.save(scenario, out);
		String xml = out.toString();
		//print before execution
		System.out.println(xml);
		//unmarshall
		T marshalled = scenarioBinder.load(xml);
		//execute
		scenario.execute();
		//print after execution
		scenarioBinder.save(marshalled, System.out);
	}

	public String getFailedDirName() {
		return "!failed";
	}

	public String getPassedDirName() {
		return "!passed";
	}

}
