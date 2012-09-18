package com.anthavio.kitty.scenario;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import com.anthavio.aspect.Logged;
import com.anthavio.kitty.KittyException;

/**
 * Base Scenario JAXB class designed to be subclassed
 *  
 * @author vanek
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Configurable
public abstract class Scenario {

	@XmlTransient
	protected final Logger log = LoggerFactory.getLogger(getClass());

	@XmlTransient
	private File scenarioFile;

	@XmlTransient
	private File directory;

	@XmlTransient
	private final ScenarioMultiListener listener = new ScenarioMultiListener();

	@XmlTransient
	private ScenarioExecution execution;

	@XmlTransient
	private boolean inited = false;

	@XmlAttribute
	private String id;

	public String getId() {
		return id;
	}

	protected Scenario() {
		// jaxb required
	}

	public Scenario(List<Step> steps, File directory) {
		this(null, steps, directory);
	}

	public Scenario(List<Step> steps) {
		this(null, steps);
	}

	public Scenario(String id, List<Step> steps, File directory) {
		this.id = id;
		this.setSteps(steps);
		Assert.notNull(directory);
		if (directory.exists() == false) {
			throw new IllegalArgumentException("Directory does not exist: " + directory.getAbsolutePath());
		}
		if (directory.isDirectory() == false) {
			throw new IllegalArgumentException("Not a directory " + directory.getAbsolutePath());
		}
		this.directory = directory;
		this.scenarioFile = new File(directory, "unsaved-" + id + ".xml");
	}

	public Scenario(String id, List<Step> steps) {
		this.id = id;
		this.setSteps(steps);
		this.directory = new File(".");
		this.scenarioFile = new File(directory, "unsaved-" + id + ".xml");
	}

	/**
	 * Descendant should provide list of concrete step implementations 
	 */
	public abstract List<Step> getSteps();

	/**
	 * Descendant should provide list of concrete step implementations
	 */
	public abstract void setSteps(List<Step> steps);

	public File getDirectory() {
		if (scenarioFile != null) {
			return scenarioFile.getParentFile();
		} else {
			return directory;
		}
	}

	public File getScenarioFile() {
		return scenarioFile;
	}

	public void setScenarioFile(File scenarioFile) {
		this.scenarioFile = scenarioFile;
		this.directory = scenarioFile.getParentFile();
	}

	public ScenarioExecution getExecution() {
		return execution;
	}

	public ScenarioContext getContext() {
		return execution.getContext();
	}

	public void addListener(ScenarioListener listener) {
		this.listener.addListener(listener);
	}

	public ScenarioExecution validate() {
		execution = new ScenarioExecution(this);
		try {
			init(execution);
			execution.setEnded(new Date());
		} catch (ScenarioInitException six) {
			execution.setException(six);
		}
		return execution;
	}

	public long init(ScenarioExecution execution) {
		execution.setStarted(new Date());
		listener.initStarted(execution);
		List<Step> steps = getSteps();
		if (steps == null) {
			throw new KittyException("Step list is null");
		}
		for (int i = 0; i < steps.size(); ++i) {
			Step step = steps.get(i);
			execution.setActiveStep(step);
			try {
				step.init(this);
			} catch (Exception x) {
				ScenarioInitException six = new ScenarioInitException(this, i, step, x);
				execution.setException(six);
				listener.initFailed(execution, six);
				throw six;
			}
		}
		this.inited = true;
		listener.initPassed(execution);
		long milis = System.currentTimeMillis() - execution.getStarted().getTime();
		return milis;
	}

	public ScenarioExecution execute() {
		execution = new ScenarioExecution(this);
		execute(execution);
		return execution;
	}

	@Logged
	public long execute(ScenarioExecution execution) {
		if (this.inited == false) {
			init(execution);
		}
		listener.executionStarted(execution);
		List<Step> steps = getSteps();
		for (int i = 0; i < steps.size(); ++i) {
			Step step = steps.get(i);
			try {
				executeStep(step, execution);
			} catch (ScenarioStepException ssx) {
				execution.setException(ssx);
				listener.executionFailed(execution, ssx);
				throw ssx; //rethrow ssx
			} catch (Exception x) {
				ScenarioFailedException sfx = new ScenarioFailedException(x, step);
				execution.setException(sfx);
				listener.executionFailed(execution, sfx);
				throw sfx;
			}
		}
		execution.setEnded(new Date());
		listener.executionPassed(execution);
		long milis = execution.getEnded().getTime() - execution.getStarted().getTime();
		return milis;
	}

	@Logged
	protected long executeStep(Step step, ScenarioExecution execution) {
		listener.stepStarted(execution, step);
		try {
			step.aroundExecute();
		} catch (Exception x) {
			throw new ScenarioFailedException(x, step);
		} catch (AssertionError ae) {
			//AssertionError only
			throw new ScenarioFailedException(ae, step);
		}
		listener.stepPassed(execution, step);
		long milis = step.getEnded().getTime() - step.getStarted().getTime();
		return milis;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}