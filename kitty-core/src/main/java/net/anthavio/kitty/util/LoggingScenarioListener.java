/**
 * 
 */
package net.anthavio.kitty.util;

import java.util.List;

import net.anthavio.kitty.scenario.BasicScenarioListener;
import net.anthavio.kitty.scenario.ScenarioExecution;
import net.anthavio.kitty.scenario.ScenarioInitException;
import net.anthavio.kitty.scenario.ScenarioStepException;
import net.anthavio.kitty.scenario.Step;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author vanek
 *
 */
public class LoggingScenarioListener extends BasicScenarioListener {

	protected static final Logger log = LoggerFactory.getLogger(LoggingScenarioListener.class);

	@Override
	public void initStarted(ScenarioExecution execution) {
		super.initStarted(execution);
		log.info("SCENARIO INIT STARTED: " + execution.getScenario().getId());
	}

	@Override
	public void initFailed(ScenarioExecution execution, ScenarioInitException exception) {
		super.initFailed(execution, exception);
		log.error("SCENARIO INIT FAILED: " + exception.getScenario().getId());
		String message = exception.getMessage();
		if (message.length() > 40 || message.indexOf('\n') != -1) {
			log.error(
					exception.getCause().getClass().getSimpleName() + " in step " + exception.getStep() + " "
							+ exception.getMessage(), exception);
		} else {
			log.error(exception.getMessage() + " in step " + exception.getStep(), exception);
		}
	}

	@Override
	public void initPassed(ScenarioExecution execution) {
		super.initPassed(execution);
		log.info("SCENARIO INIT PASSED: " + execution.getScenario().getId());
	}

	@Override
	public void executionStarted(ScenarioExecution execution) {
		super.executionStarted(execution);
		log.info("SCENARIO EXECUTION STARTED: " + execution.getScenario().getId());
	}

	@Override
	public void stepStarted(ScenarioExecution execution, Step step) {
		super.stepStarted(execution, step);
		int stepNum = step.getScenario().getSteps().indexOf(step) + 1;
		int stepCnt = step.getScenario().getSteps().size();
		log.info("STEP STARTED " + stepNum + "/" + stepCnt + " " + step);
	}

	@Override
	public void stepPassed(ScenarioExecution execution, Step step) {
		super.stepPassed(execution, step);
		long milis = step.getEnded().getTime() - step.getStarted().getTime();
		int stepNum = step.getScenario().getSteps().indexOf(step) + 1;
		int stepCnt = step.getScenario().getSteps().size();
		log.info("STEP PASSED " + stepNum + "/" + stepCnt + " " + step + " in " + milis + " ms");
	}

	@Override
	public void executionPassed(ScenarioExecution execution) {
		super.executionPassed(execution);
		long milis = execution.getEnded().getTime() - execution.getStarted().getTime();
		log.info("SCENARIO EXECUTION PASSED: " + execution.getScenario().getId() + " in " + milis + " ms");
	}

	@Override
	public void executionFailed(ScenarioExecution execution, ScenarioStepException exception) {
		super.executionFailed(execution, exception);
		long milis = execution.getEnded().getTime() - execution.getStarted().getTime();
		int stepIdx = exception.getScenario().getSteps().indexOf(exception.getStep());
		int stepCnt = exception.getScenario().getSteps().size();
		List<Step> steps = exception.getScenario().getSteps();
		log.error("SCENARIO EXECUTION FAILED: " + exception.getScenario().getId() + " in " + milis + " ms");
		log.error(exception.toString());
		log.error("FAILED STEP: " + (stepIdx + 1) + " " + steps.get(stepIdx));
		if (stepIdx > 0) {
			log.info("Completed steps: " + stepIdx + " of " + stepCnt);
			for (int j = 0; j < stepIdx; ++j) {
				log.info("Completed step " + (j + 1) + ": " + steps.get(j));
			}
		}
	}

}
