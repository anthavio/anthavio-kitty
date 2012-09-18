package com.anthavio.kitty.webmvc;

/**
 * 
 */

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.anthavio.kitty.Kitty;
import com.anthavio.kitty.KittyException;
import com.anthavio.kitty.model.DirectoryItem;
import com.anthavio.kitty.model.DirectoryModel;
import com.anthavio.kitty.model.ScenarioExecutor;
import com.anthavio.kitty.scenario.Scenario;
import com.anthavio.kitty.scenario.Step;

/**
 * @author vanek
 *
 */
@Controller("KittyScenarioControler")
@RequestMapping("/scenario")
@SessionAttributes(BaseScenarioController.BROWSE_DIR_KEY)
public class BaseScenarioController {

	public static final String BROWSE_DIR_KEY = "BrowseDir";

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	protected Kitty tool;

	@Inject
	protected ScenarioExecutor executor;

	@Inject
	protected Kitty kitty;

	@InitBinder("directoryModel")
	public void initBinder(WebDataBinder binder) {
		//better to log message if we were called
		log.debug("WebDataBinder overriden");
		binder.setAutoGrowNestedPaths(true);
		binder.setAutoGrowCollectionLimit(2048);
	}

	@RequestMapping("/browse")
	public String browse(@RequestParam(value = "path", required = false) String path,
			@ModelAttribute(BROWSE_DIR_KEY) File browseDir, Model model) {

		if (path != null) {
			File file = absolutize(path, browseDir);
			if (file.exists()) {
				if (file.isDirectory()) {
					browseDir = file;
					model.addAttribute(BROWSE_DIR_KEY, browseDir);
				} else {
					return "redirect:view";//file -> scenario view
				}
			} else {
				log.error("Path does not exist " + file.getAbsolutePath());
			}
		}

		DirectoryModel directoryModel = tool.list(browseDir);
		return common(browseDir, directoryModel, model);
	}

	@RequestMapping(value = "/browse", params = "index")
	public String browseIndex(@RequestParam(value = "index") Integer index,
			@ModelAttribute(BROWSE_DIR_KEY) File browseDir, Model model) {

		DirectoryModel directoryModel = tool.list(browseDir);
		DirectoryItem item = directoryModel.getItems().get(index);
		return browse(item.getFile().getName(), browseDir, model);

	}

	@RequestMapping(value = "/view", params = "file")
	public void view(@RequestParam("file") String fileName, @ModelAttribute(BROWSE_DIR_KEY) File browseDir, Model model)
			throws IOException {
		File file = absolutize(fileName, browseDir);
		String scenarioXml = FileUtils.readFileToString(file, "utf-8");
		model.addAttribute("ScenarioXml", scenarioXml);
		model.addAttribute("File", file);

		try {
			Scenario scenario = kitty.getScenarioBinder().load(scenarioXml);
			scenario.validate();
		} catch (Exception x) {
			model.addAttribute("XmlError", x);
		}
		addBrowsePaths(browseDir, model);
		addJaxbStepInfo(model);
	}

	@RequestMapping(value = "/view", params = "index")
	public void viewIndex(@RequestParam("index") Integer index, @ModelAttribute(BROWSE_DIR_KEY) File browseDir,
			Model model) throws IOException {
		DirectoryModel directoryModel = tool.list(browseDir);
		DirectoryItem item = directoryModel.getItems().get(index);
		view(item.getFile().getCanonicalPath(), browseDir, model);
	}

	@RequestMapping(value = "/save", params = "cancel")
	public String cancel(@RequestParam("file") String fileName, @ModelAttribute(BROWSE_DIR_KEY) File browseDir,
			Model model) throws IOException {
		view(fileName, browseDir, model);
		return "scenario/view";
	}

	@RequestMapping(value = "/save", params = "save")
	public String save(@RequestParam("file") String fileName, @RequestParam("scenarioXml") String scenarioXml,
			@ModelAttribute(BROWSE_DIR_KEY) File browseDir, Model model) throws IOException {
		File file = absolutize(fileName, browseDir);
		model.addAttribute("ScenarioXml", scenarioXml);
		model.addAttribute("File", file);
		addBrowsePaths(browseDir, model);
		addJaxbStepInfo(model);

		try {
			Scenario scenario = kitty.getScenarioBinder().load(scenarioXml);
			scenario.validate();
			//save only loaded and validated
			FileUtils.writeStringToFile(file, scenarioXml, "utf-8");
		} catch (Exception x) {
			//x.printStackTrace();
			model.addAttribute("XmlError", x);
			return "scenario/view";
		}
		return "scenario/view";
	}

	@RequestMapping(value = "/validate", params = "index")
	public String validateIdx(@RequestParam("index") Integer index, @ModelAttribute(BROWSE_DIR_KEY) File browseDir,
			Model model) {
		DirectoryModel directoryModel = tool.list(browseDir);
		DirectoryItem item = directoryModel.getItems().get(index);
		executor.validate(item);
		common(browseDir, directoryModel, model);
		return "redirect:browse";
	}

	/*
		@RequestMapping(value = "/validate", params = "file")
		public String validate(@RequestParam("file") String fileName, @ModelAttribute(BROWSE_DIR_KEY) File browseDir,
				Model model) {
			File file = absolutize(fileName, browseDir);
			DirectoryModel directoryModel = tool.list(browseDir);
			DirectoryItem item = directoryModel.getItemByFile(file);
			executor.validate(item);
			common(browseDir, directoryModel, model);
			return "redirect:browse";
		}
	*/

	@RequestMapping(value = "/execute", params = "index")
	public String executeIdx(@RequestParam("index") Integer index, @ModelAttribute(BROWSE_DIR_KEY) File browseDir,
			Model model) {
		DirectoryModel directoryModel = tool.list(browseDir);
		DirectoryItem item = directoryModel.getItems().get(index);
		executor.executeAsync(item);
		common(browseDir, directoryModel, model);
		return "redirect:browse";
	}

	/*
		@RequestMapping(value = "/execute", params = "file")
		public String execute(@RequestParam("file") String fileName, @ModelAttribute(BROWSE_DIR_KEY) File browseDir,
				Model model) {
			File file = absolutize(fileName, browseDir);
			DirectoryModel directoryModel = tool.list(browseDir);
			DirectoryItem item = directoryModel.getItemByFile(file);
			executor.executeAsync(item);
			common(browseDir, directoryModel, model);
			return "redirect:browse";
		}
	*/
	/*
	@RequestMapping(value = "/tableAction", params = "validate", method = RequestMethod.POST)
	public String validateBatch(@ModelAttribute DirectoryModel directoryModel, BindingResult result,
			@ModelAttribute(BROWSE_DIR_KEY) File browseDir, Model model) {

		tool.update(directoryModel);
		executor.validate(directoryModel);
		common(browseDir, directoryModel, model);
		return "redirect:browse";
	}
	*/

	@RequestMapping(value = "/tableAction", params = "validate", method = RequestMethod.POST)
	public String validateBatch(@ModelAttribute DirectoryModel directoryModel, BindingResult result,
			@ModelAttribute(BROWSE_DIR_KEY) File browseDir, Model model) {

		directoryModel = tool.setSelected(directoryModel);
		executor.validate(directoryModel);
		common(browseDir, directoryModel, model);
		return "redirect:browse";
	}

	@RequestMapping(value = "/tableAction", params = "execute", method = RequestMethod.POST)
	public String executeBatch(@ModelAttribute DirectoryModel directoryModel, BindingResult result,
			@ModelAttribute(BROWSE_DIR_KEY) File browseDir, Model model) {
		directoryModel = tool.setSelected(directoryModel);
		executor.validate(directoryModel);
		//ScenarioListener listener = new ExcelScenarioListener(directoryModel.getPath(), directoryModel.getSelectedFiles());
		executor.executeAsync(directoryModel, kitty.getOptions().getUsePassedDir(), kitty.getOptions().getUseFailedDir());
		common(browseDir, directoryModel, model);
		return "redirect:browse";
	}

	@RequestMapping(value = "/tableAction", params = "reset", method = RequestMethod.POST)
	public String reset(@ModelAttribute DirectoryModel directoryModel, BindingResult result,
			@ModelAttribute(BROWSE_DIR_KEY) File browseDir, Model model) {

		DirectoryModel directoryModel2 = tool.list(browseDir);
		List<DirectoryItem> items = directoryModel2.getItems();
		for (DirectoryItem item : items) {
			item.setExecution(null);
			item.setMessage(null);
		}

		//tool.setSelected(directoryModel);
		return "redirect:browse";
	}

	@RequestMapping("/batchStart")
	public String batchStart(@ModelAttribute(BROWSE_DIR_KEY) File browseDir, Model model) throws IOException {
		DirectoryModel directoryModel = tool.list(browseDir);
		return common(browseDir, directoryModel, model);
	}

	@RequestMapping("/batchStop")
	public String batchStop() {
		executor.stopAsync();
		return "redirect:browse";
	}

	@RequestMapping("/download")
	public void downloadReport(@RequestParam("file") String fileName, @ModelAttribute(BROWSE_DIR_KEY) File browseDir,
			HttpServletResponse response, Model model) throws IOException {
		File file = new File(fileName);
		response.setHeader("Pragma", "private");
		response.setHeader("Cache-Control", "private, must-revalidate");
		response.setContentType("application/vnd.ms-excel");
		//response.setContentLength(content.length);
		response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
		IOUtils.copy(new FileInputStream(file), response.getOutputStream());
	}

	@RequestMapping("/delete")
	public String deleteReport(@RequestParam("file") String fileName, @ModelAttribute(BROWSE_DIR_KEY) File browseDir,
			Model model) {
		new File(fileName).delete();
		DirectoryModel directoryModel = tool.list(browseDir);
		return common(browseDir, directoryModel, model);
	}

	private File absolutize(String fileName, File browseDir) {
		File file = new File(fileName);
		if (file.isAbsolute() == false) {
			file = new File(browseDir, fileName);
		}
		return file;
	}

	/*
		@RequestMapping(value = "/tableAction", method = RequestMethod.POST)
		public String tableRowAction(@ModelAttribute(BROWSE_DIR_KEY) File browseDir,
				@ModelAttribute DirectoryModel directoryModel, BindingResult result, Model model, ServletRequest req) {
			int rowValidate = -1;
			int rowExecute = -1;
			Enumeration<String> parameterNames = req.getParameterNames();
			while (parameterNames.hasMoreElements()) {
				String paramName = parameterNames.nextElement();
				if (paramName.startsWith("rowValidate")) {
					rowValidate = Integer.parseInt(paramName.substring("rowValidate".length() + 1));
				} else if (paramName.startsWith("rowExecute")) {
					rowExecute = Integer.parseInt(paramName.substring("rowExecute".length() + 1));
				}
			}
			if (rowValidate != -1) {
				return rowValidate(rowValidate, browseDir, directoryModel, model);
			}
			if (rowExecute != -1) {
				return rowExecute(rowExecute, browseDir, directoryModel, model);
			}

			throw new IllegalArgumentException("Unidentified Row Table action");
		}
		
		private String rowValidate(int row, File browseDir, DirectoryModel directoryModel, Model model) {
			DirectoryItem item = directoryModel.getItems().get(row);
			dirListMap.put(browseDir, directoryModel);
			validate(item);
			return common(browseDir, directoryModel, model);
		}
		
		private String rowExecute(int row, File browseDir, DirectoryModel directoryModel, Model model) {
			DirectoryItem item = directoryModel.getItems().get(row);
			dirListMap.put(browseDir, directoryModel);
			execute(item);
			return common(browseDir, directoryModel, model);
		}
	*/

	protected String common(File browseDir, DirectoryModel directoryModel, Model model) {
		model.addAttribute("DirectoryModel", directoryModel);
		addBrowsePaths(browseDir, model);
		addReportFiles(browseDir, model);
		if (executor.isRunning()) {
			return "scenario/running";
		} else {
			return "scenario/browse";
		}
	}

	private void addReportFiles(File browseDir, Model model) {
		File[] excelFiles = browseDir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return file.isFile() && file.getName().endsWith(".xls");
			}
		});
		model.addAttribute("ReportFiles", excelFiles);
	}

	private void addJaxbStepInfo(Model model) {
		Class<Scenario> jaxbClass = kitty.getScenarioBinder().getJaxbClass();
		XmlElements xmlElements;
		XmlAccessorType xmlAccessorType = jaxbClass.getAnnotation(XmlAccessorType.class);
		try {
			if (xmlAccessorType != null && xmlAccessorType.value() == XmlAccessType.FIELD) {
				Field field = jaxbClass.getDeclaredField("steps");
				xmlElements = field.getAnnotation(XmlElements.class);
			} else {
				Method method = jaxbClass.getDeclaredMethod("getSteps");
				xmlElements = method.getAnnotation(XmlElements.class);
			}

		} catch (Exception x) {
			throw new KittyException("Step metadata extraction failed", x);
		}

		XmlElement[] elements = xmlElements.value();
		List<StepXmlElement> list = new ArrayList<StepXmlElement>(elements.length);
		for (XmlElement element : elements) {
			List<StepXmlElement> fields = new ArrayList<StepXmlElement>();
			addJaxbFields(element.type(), fields);
			list.add(new StepXmlElement(element.name(), element.type(), element.required(), fields));
		}
		model.addAttribute("StepInfoList", list);
	}

	private void addJaxbFields(Class<?> stepClass, List<StepXmlElement> fields) {
		//parent fields first
		if (stepClass.getSuperclass() != Step.class) {
			addJaxbFields(stepClass.getSuperclass(), fields);
		}

		Field[] declaredFields = stepClass.getDeclaredFields();
		//XXX this supports only XmlAccessType.FIELD
		for (Field field : declaredFields) {
			if (Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			XmlTransient xmlTransient = field.getAnnotation(XmlTransient.class);
			if (xmlTransient != null) {
				continue;
			}
			XmlAttribute xmlAttribute = field.getAnnotation(XmlAttribute.class);
			if (xmlAttribute != null) {
				String name = xmlAttribute.name();
				if (name.equals("##default")) {
					name = field.getName();
				}
				fields.add(new StepXmlElement(name, field.getType(), xmlAttribute.required(), null));
				continue;
			}
			XmlElement xmlElement = field.getAnnotation(XmlElement.class);
			if (xmlElement != null) {
				String name = xmlElement.name();
				if (name.equals("##default")) {
					name = field.getName();
				}
				fields.add(new StepXmlElement(name, field.getType(), xmlElement.required(), null));
				continue;
			}

			fields.add(new StepXmlElement(field.getName(), field.getType(), false, null));
		}
	}

	private void addBrowsePaths(File browseDir, Model model) {
		List<File> browseParents = new ArrayList<File>();
		browseParents.add(browseDir);
		File parentFile = browseDir;
		while ((parentFile = parentFile.getParentFile()) != null) {
			browseParents.add(0, parentFile);
		}
		model.addAttribute("BrowseParents", browseParents);
	}

	public static class StepXmlElement {

		private String name;
		private Class<?> type;
		private boolean required;
		private List<StepXmlElement> fields;

		public StepXmlElement(String name, Class<?> type, boolean required, List<StepXmlElement> jaxbFields) {
			this.name = name;
			this.type = type;
			this.required = required;
			this.fields = jaxbFields;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Class<?> getType() {
			return type;
		}

		public void setType(Class<?> type) {
			this.type = type;
		}

		public boolean isRequired() {
			return required;
		}

		public void setRequired(boolean required) {
			this.required = required;
		}

		public List<StepXmlElement> getFields() {
			return fields;
		}

		public void setFields(List<StepXmlElement> fields) {
			this.fields = fields;
		}

	}
}