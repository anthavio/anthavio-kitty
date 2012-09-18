package com.anthavio.kitty.scenario;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import com.anthavio.io.FileUtils;

/**
 * @author vanek
 * 
 * Step is in scenario package because we need package visibility modifiers to set various stuff... 
 */
@Configurable
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class Step {

	@XmlTransient
	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	@XmlTransient
	private Scenario scenario;

	@XmlTransient
	private Date started;

	@XmlTransient
	private Date ended;

	/**
	 * Internal
	 */
	public final void init(Scenario scenario) throws Exception {
		this.scenario = scenario;
		init();
	}

	/**
	 * Empty default implementation. Subclasses can override		
	 */
	public void init() throws Exception {

	}

	/**
	 * Internal
	 */
	final void aroundExecute() throws Exception {
		this.started = new Date();
		try {
			execute();
		} finally {
			this.ended = new Date();
		}
	}

	public abstract void execute() throws Exception;

	public Scenario getScenario() {
		return scenario;
	}

	protected ScenarioExecution getExecution() {
		return this.scenario.getExecution();
	}

	protected ScenarioContext getContext() {
		return this.scenario.getExecution().getContext();
	}

	//data context helper method shortcuts

	public void contextPut(Object object) {
		getContext().put(object);
	}

	public void contextPut(String key, Object value) {
		getContext().put(key, value);
	}

	public <T> T contextGet(Class<T> expected) {
		return getContext().get(expected);
	}

	public Object contextGet(String key) {
		return getContext().get(key);
	}

	public <T> T contextGet(String key, Class<T> expected) {
		return (T) getContext().get(key);
	}

	public Object contextGet(Object key) {
		if (key instanceof String) {
			return getContext().get((String) key);
		} else if (key instanceof Class) {
			return getContext().get((Class<?>) key);
		} else {
			return getContext().get(key.getClass());
		}
	}

	public <T> boolean contextHas(Class<T> expected) {
		return getContext().containsKey(expected);
	}

	/**
	 * Pokud je na vstupu relativni cesta, pak se dopocita podle BaseDir scenare 
	 */
	protected String readFile(String filePath) {
		if (filePath == null || filePath.equals("")) {
			throw new IllegalArgumentException("File path is invalid " + filePath);
		}
		try {
			if (filePath.startsWith("/") || filePath.charAt(1) == ':' || filePath.startsWith("file:")) {
				return FileUtils.readFile(new File(filePath));
			} else {
				return FileUtils.readFile(new File(scenario.getDirectory(), filePath));
			}
		} catch (IOException iox) {
			throw new AssertionError("IOException while reading " + iox);
		}
	}

	/**
	 * Pokud je prvni parametr not null, tak jej vrati. Jinak vrati druhy
	 * Shortcut for: String x = this.x != null ? this.x : "default"
	 */
	protected <T> T value(T value, T defVal) {
		return value != null ? value : defVal;
	}

	/**
	 * Pokud je @param value != null, pak jej primo vrati
	 * Pokud je null, pak zkusi vratit z kontextu hodnotu podle @param defCtxClass
	 */
	protected <T> T value(T value, Class<T> defCtxClass) {
		return value != null ? value : contextGet(defCtxClass);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

	/**
	 * @return position (1,n) of this step in scenario
	 */
	public int getPosition() {
		return getIndex() + 1;
	}

	/**
	 * @return index (0,n) of this step in scenario
	 */
	public int getIndex() {
		List<Step> steps = scenario.getSteps();
		for (int i = 0; i < steps.size(); i++) {
			if (steps.get(i).equals(this)) {
				return i;
			}
		}
		throw new IllegalStateException("This step does not belong to it's scenario");
	}

	public Date getStarted() {
		return started;
	}

	public Date getEnded() {
		return ended;
	}

}