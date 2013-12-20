/**
 * 
 */
package net.anthavio.kitty.step;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.annotation.XmlElement;

import net.anthavio.kitty.scenario.ScenarioStepException;
import net.anthavio.kitty.scenario.Step;

import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.util.Assert;


/**
 * @author vanek
 *
 */
public class ParalelStep extends Step {

	@XmlElement(name = "step")
	private List<Step> steps;

	@Inject
	@Named("KittyTaskExecutor")
	private transient AsyncTaskExecutor taskExecutor;

	public ParalelStep() {
		//jaxb
	}

	public ParalelStep(List<Step> steps) {
		Assert.notEmpty(steps);
		this.steps = steps;
	}

	@Override
	public void init() throws Exception {
		Assert.notEmpty(steps);
		for (Step step : steps) {
			step.init(getScenario());
		}
	}

	@Override
	public void execute() throws Exception {
		final List<Future<?>> futures = new ArrayList<Future<?>>();
		final Map<Step, Exception> failedSteps = new HashMap<Step, Exception>();
		for (final Step step : steps) {
			Future<?> future = taskExecutor.submit(new Runnable() {

				@Override
				public void run() {
					try {
						step.execute();
					} catch (Exception x) {
						failedSteps.put(step, x);
					}
				}
			});
			futures.add(future);
		}

		for (Future<?> future : futures) {
			future.get(); //block main thread
		}
		if (failedSteps.isEmpty() == false) {
			Set<Entry<Step, Exception>> entrySet = failedSteps.entrySet();
			for (Entry<Step, Exception> entry : entrySet) {
				log.error("Failed paralel step: " + entry.getKey(), entry.getValue());
			}
			throw new ScenarioStepException(this, "Some of paralel steps failed: " + steps);
		}
	}

}
