/**
 * 
 */
package net.anthavio.kitty.scenario;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import net.anthavio.kitty.KittyException;
import net.anthavio.kitty.model.DirectoryItem;


/**
 * @author vanek
 *
 */
public class ScenarioExecution {

	//Unique database generated ID
	private Integer id;

	//File from Scenario will be loaded
	private File file;

	private Date started;

	private Date ended;

	private String errorStep;

	private String errorMessage;

	private transient ScenarioStepException exception;

	private transient Scenario scenario;

	private transient Step activeStep;

	private transient ScenarioContext context;

	public ScenarioExecution() {
		//
	}

	public ScenarioExecution(DirectoryItem item, Date started) {
		this(item.getFile(), started);
	}

	public ScenarioExecution(File path, Date started) {
		this.file = path;
		this.started = started;
	}

	public ScenarioExecution(File path, Date started, Date ended, String errorStep, String errorMessage) {
		this(path, started);
		this.ended = ended;
		this.errorStep = errorStep;
		this.errorMessage = errorMessage;
	}

	public ScenarioExecution(Scenario scenario) {
		this.file = scenario.getScenarioFile();
		this.scenario = scenario;
	}

	public File getFile() {
		return file;
	}

	public String getFileCannonicalPath() {
		try {
			return file.getCanonicalPath();
		} catch (IOException iox) {
			throw new KittyException(iox);
		}
	}

	public void setFile(File path) {
		this.file = path;
	}

	public Date getStarted() {
		return started;
	}

	public void setStarted(Date started) {
		this.context = new ScenarioContext(this);
		this.started = started;
	}

	public ScenarioContext getContext() {
		return context;
	}

	protected void setActiveStep(Step step) {
		this.activeStep = step;
	}

	public Step getActiveStep() {
		return activeStep;
	}

	public Date getEnded() {
		return ended;
	}

	public void setEnded(Date ended) {
		this.ended = ended;
	}

	public String getErrorStep() {
		return errorStep;
	}

	public void setErrorStep(String errorStep) {
		this.errorStep = errorStep;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean isRunning() {
		return started != null && ended == null && errorMessage == null;
	}

	public boolean isPassed() {
		return started != null && ended != null && errorMessage == null;
	}

	public boolean isFailed() {
		return started != null && ended != null && errorMessage != null;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Scenario getScenario() {
		return this.scenario;
	}

	public void setException(ScenarioStepException stepException) {
		this.exception = stepException;
		this.ended = new Date();
		this.errorMessage = stepException.toString();
		this.errorStep = stepException.getStep().getPosition() + " " + stepException.getStep();
	}

	public ScenarioStepException getException() {
		return exception;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ScenarioExecution [");
		if (id != null) {
			sb.append("id=");
			sb.append(id);
			sb.append(", ");
		}

		sb.append("started=");
		sb.append(started);

		if (ended != null) {
			sb.append(", ");
			sb.append("ended=");
			sb.append(ended);
		}

		if (exception != null) {
			sb.append(", ");
			sb.append("exception=");
			sb.append(exception);
		}

		sb.append("]");
		return "ScenarioExecution [id=" + id + ", started=" + started + ", ended=" + ended + "]";
	}

}
