/**
 * 
 */
package net.anthavio.kitty.scenario;

/**
 * @author vanek
 *
 */
public class ScenarioStepException extends ScenarioException {

	private static final long serialVersionUID = 1L;

	private final Step step;

	public ScenarioStepException(Step step, String message, Throwable cause) {
		super(message, cause);
		this.step = step;
	}

	public ScenarioStepException(Step step, String message) {
		super(message);
		this.step = step;
	}

	public ScenarioStepException(Step step, Throwable cause) {
		super(cause);
		this.step = step;
	}

	public Step getStep() {
		return step;
	}

	public Scenario getScenario() {
		return step.getScenario();
	}

	public int getStepIndex() {
		return getScenario().getSteps().indexOf(step);
	}

	@Override
	public String getMessage() {
		return "Step " + (getStepIndex() + 1) + " " + step + " " + super.getMessage();
	}

}
