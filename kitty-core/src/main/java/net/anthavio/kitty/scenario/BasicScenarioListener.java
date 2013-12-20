/**
 * 
 */
package net.anthavio.kitty.scenario;

import java.util.Date;


/**
 * @author vanek
 *
 */
public class BasicScenarioListener implements ScenarioListener {

	private Scenario scenario;

	private Date initStarted;

	private Date initStopped;

	private Date stepStarted;

	private Date stepStopped;

	private Date executionStarted;

	private Date executionStopped;

	private ScenarioStepException exception;

	@Override
	public void initStarted(ScenarioExecution execution) {
		this.scenario = execution.getScenario();
		this.initStarted = new Date();
	}

	@Override
	public void initFailed(ScenarioExecution execution, ScenarioInitException exception) {
		this.exception = exception;
		this.initStopped = new Date();
	}

	@Override
	public void initPassed(ScenarioExecution execution) {
		this.initStopped = new Date();
	}

	@Override
	public void executionStarted(ScenarioExecution execution) {
		this.scenario = execution.getScenario();
		this.executionStarted = new Date();
	}

	@Override
	public void stepStarted(ScenarioExecution execution, Step step) {
		this.stepStarted = new Date();
	}

	@Override
	public void stepPassed(ScenarioExecution execution, Step step) {
		this.stepStopped = new Date();
	}

	@Override
	public void executionPassed(ScenarioExecution execution) {
		this.executionStopped = new Date();
	}

	@Override
	public void executionFailed(ScenarioExecution execution, ScenarioStepException exception) {
		this.exception = exception;
		this.executionStopped = new Date();
	}

	public Scenario getScenario() {
		return scenario;
	}

	public Date getInitStarted() {
		return initStarted;
	}

	public Date getInitStopped() {
		return initStopped;
	}

	public Date getStepStarted() {
		return stepStarted;
	}

	public Date getStepStopped() {
		return stepStopped;
	}

	public Date getExecutionStarted() {
		return executionStarted;
	}

	public Date getExecutionStopped() {
		return executionStopped;
	}

	public ScenarioStepException getException() {
		return exception;
	}

}
