/**
 * 
 */
package com.anthavio.kitty.scenario;


/**
 * @author vanek
 *
 */
public interface ScenarioListener {

	public void initStarted(ScenarioExecution execution);

	public void initFailed(ScenarioExecution execution, ScenarioInitException exception);

	public void initPassed(ScenarioExecution execution);

	public void executionStarted(ScenarioExecution execution);

	public void stepStarted(ScenarioExecution execution, Step step);

	public void stepPassed(ScenarioExecution execution, Step step);

	public void executionPassed(ScenarioExecution execution);

	public void executionFailed(ScenarioExecution execution, ScenarioStepException exception);
}
