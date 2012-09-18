/**
 * 
 */
package com.anthavio.kitty.scenario;

import java.util.ArrayList;
import java.util.List;


/**
 * @author vanek
 *
 */
public class ScenarioMultiListener implements ScenarioListener {

	private List<ScenarioListener> listeners;

	public void addListener(ScenarioListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<ScenarioListener>();
		}
		listeners.add(listener);
	}

	public void removeListener(ScenarioListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void initStarted(ScenarioExecution execution) {
		if (listeners != null) {
			for (ScenarioListener listener : listeners) {
				listener.initStarted(execution);
			}
		}
	}

	@Override
	public void initFailed(ScenarioExecution execution, ScenarioInitException exception) {
		if (listeners != null) {
			for (ScenarioListener listener : listeners) {
				listener.initFailed(execution, exception);
			}
		}
	}

	@Override
	public void initPassed(ScenarioExecution execution) {
		if (listeners != null) {
			for (ScenarioListener listener : listeners) {
				listener.initPassed(execution);
			}
		}
	}

	@Override
	public void executionStarted(ScenarioExecution execution) {
		if (listeners != null) {
			for (ScenarioListener listener : listeners) {
				listener.executionStarted(execution);
			}
		}
	}

	@Override
	public void stepStarted(ScenarioExecution execution, Step step) {
		if (listeners != null) {
			for (ScenarioListener listener : listeners) {
				listener.stepStarted(execution, step);
			}
		}
	}

	@Override
	public void stepPassed(ScenarioExecution execution, Step step) {
		if (listeners != null) {
			for (ScenarioListener listener : listeners) {
				listener.stepPassed(execution, step);
			}
		}
	}

	@Override
	public void executionPassed(ScenarioExecution execution) {
		if (listeners != null) {
			for (ScenarioListener listener : listeners) {
				listener.executionPassed(execution);
			}
		}
	}

	@Override
	public void executionFailed(ScenarioExecution execution, ScenarioStepException exception) {
		if (listeners != null) {
			for (ScenarioListener listener : listeners) {
				listener.executionFailed(execution, exception);
			}
		}
	}

}