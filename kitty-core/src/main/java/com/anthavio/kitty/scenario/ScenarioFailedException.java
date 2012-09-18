/**
 * 
 */
package com.anthavio.kitty.scenario;


/**
 * RuntimeException wrapper. Replaces own stackTrace
 * 
 * @author vanek
 *
 */
public class ScenarioFailedException extends ScenarioStepException {

	private static final long serialVersionUID = 1L;

	ScenarioFailedException(Throwable cause, Step step) {
		super(step, cause.getClass().getName() + " " + cause.getMessage());
		setStackTrace(cause.getStackTrace());
	}

	ScenarioFailedException(String message, Step step) {
		super(step, message);
	}

}
