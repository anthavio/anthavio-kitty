/**
 * 
 */
package com.anthavio.kitty.scenario;


/**
 * @author vanek
 *
 */
public class ContextException extends ScenarioStepException {

	private static final long serialVersionUID = 1L;

	public ContextException(Step step, String message, Throwable cause) {
		super(step, message, cause);
	}

	public ContextException(Step step, String message) {
		super(step, message);
	}

	public ContextException(Step step, Throwable cause) {
		super(step, cause);
	}

}
