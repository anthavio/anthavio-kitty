/**
 * 
 */
package com.anthavio.kitty.scenario;

/**
 * Just a base class of scenario exceptions
 * 
 * @author vanek
 *
 */
public class ScenarioException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ScenarioException() {
		super();
	}

	public ScenarioException(String message, Throwable cause) {
		super(message, cause);
	}

	public ScenarioException(String message) {
		super(message);
	}

	public ScenarioException(Throwable cause) {
		super(cause);
	}
}
