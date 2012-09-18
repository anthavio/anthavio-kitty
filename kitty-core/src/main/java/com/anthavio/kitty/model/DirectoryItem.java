/**
 * 
 */
package com.anthavio.kitty.model;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import com.anthavio.kitty.Kitty;
import com.anthavio.kitty.KittyException;
import com.anthavio.kitty.scenario.ScenarioExecution;
import com.anthavio.kitty.scenario.ScenarioInitException;
import com.anthavio.kitty.scenario.ScenarioListener;
import com.anthavio.kitty.scenario.ScenarioStepException;
import com.anthavio.kitty.scenario.Step;

/**
 * @author vanek
 *
 */
public class DirectoryItem implements ScenarioListener {

	private File file;

	private boolean selected;

	private ScenarioExecution execution;

	private String message;

	public DirectoryItem() {
		//default
	}

	public DirectoryItem(File file) {
		this.file = file;
	}

	public ScenarioExecution getExecution() {
		return execution;
	}

	public void setExecution(ScenarioExecution execution) {
		this.execution = execution;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getFileCannonicalPath() {
		try {
			return file.getCanonicalPath();
		} catch (IOException iox) {
			throw new KittyException(iox);
		}
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getMessage() {
		if (execution != null) {
			if (execution.getErrorStep() != null) {
				return execution.getErrorStep();
			} else if (execution.getErrorMessage() != null) {
				return execution.getErrorMessage();
			}
		}
		return message;
	}

	public void setMessage(String message) {
		if (execution != null && execution.getErrorMessage() != null) {
			//TODO maybe just ignore and do not throw exception
			throw new IllegalStateException("Item has error execution. Cannot set info message");
		} else {
			this.message = message;
		}
	}

	public boolean isRunning() {
		if (execution != null) {
			return execution.isRunning();
		} else {
			return false;
		}
	}

	public ScenarioExecution setStarted() {
		Date now = new Date();
		this.execution = new ScenarioExecution(this, now);
		this.message = "Started: " + Kitty.formatDateTime(now);
		return this.execution;
	}

	public Date setPassed() {
		Date now = setPassed("Passed");
		this.message = "Passed: " + Kitty.formatDateTime(now);
		return now;
	}

	public Date setPassed(String message) {
		if (execution == null) {
			throw new IllegalStateException("Not started");
		}
		if (execution.getEnded() != null) {
			throw new IllegalStateException("Already ended");
		}
		Date now = new Date();
		execution.setEnded(now);

		this.message = message;

		return now;
	}

	public Date setFailed(String errorMessage) {
		Date now = setPassed();
		execution.setErrorMessage(errorMessage);
		this.message = "Failed: " + Kitty.formatDateTime(now);
		return now;
	}

	public Date setFailed(Exception x) {
		Date now = setFailed(String.valueOf(x));
		return now;
	}

	public Date setFailed(ScenarioStepException ssx) {
		Date now = setFailed((Exception) ssx);
		String stepmsg = "Failed: Step " + (ssx.getStepIndex() + 1) + " " + ssx.getStep();
		execution.setErrorStep(stepmsg);
		return now;
	}

	@Override
	public void initStarted(ScenarioExecution execution) {
		this.execution = execution;
		this.message = "Init Started: " + Kitty.formatDateTime(execution.getStarted());
	}

	@Override
	public void initFailed(ScenarioExecution execution, ScenarioInitException exception) {
		this.message = "Init Failed: " + Kitty.formatDateTime(new Date());
	}

	@Override
	public void initPassed(ScenarioExecution execution) {
		this.message = "Init Passed: " + Kitty.formatDateTime(new Date());
	}

	@Override
	public void executionStarted(ScenarioExecution execution) {
		this.execution = execution;
		this.message = "Started: " + Kitty.formatDateTime(new Date());
	}

	@Override
	public void stepStarted(ScenarioExecution execution, Step step) {
		this.message = "Step " + step.getPosition() + " Started: " + Kitty.formatDateTime(new Date());
	}

	@Override
	public void stepPassed(ScenarioExecution execution, Step step) {
		//nothing...
	}

	@Override
	public void executionPassed(ScenarioExecution execution) {
		this.message = "Passed: " + Kitty.formatDateTime(execution.getEnded());
	}

	@Override
	public void executionFailed(ScenarioExecution execution, ScenarioStepException ssx) {
		String stepmsg = "Failed: Step " + (ssx.getStepIndex() + 1) + " " + ssx.getStep();
		execution.setErrorStep(stepmsg);
	}

	@Override
	public String toString() {
		return "DirectoryItem [file=" + file + ", selected=" + selected + ", message=" + message + ", execution="
				+ execution + "]";
	}

}
