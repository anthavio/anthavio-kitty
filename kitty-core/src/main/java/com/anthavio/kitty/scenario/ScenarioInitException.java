/**
 * 
 */
package com.anthavio.kitty.scenario;


/**
 * @author vanek
 *
 */
public class ScenarioInitException extends ScenarioStepException {

	private static final long serialVersionUID = 1L;

	private final int index;

	public ScenarioInitException(Scenario scenario, int index, Step step, Throwable cause) {
		super(step, cause);
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

}
