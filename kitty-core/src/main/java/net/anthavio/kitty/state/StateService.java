/**
 * 
 */
package net.anthavio.kitty.state;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import net.anthavio.kitty.Kitty;
import net.anthavio.kitty.scenario.ScenarioExecution;
import net.anthavio.kitty.scenario.ScenarioInitException;
import net.anthavio.kitty.scenario.ScenarioListener;
import net.anthavio.kitty.scenario.ScenarioStepException;
import net.anthavio.kitty.scenario.Step;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author vanek
 *
 */
//@Component
public class StateService implements ScenarioListener {

	public static Logger loadLog = LoggerFactory.getLogger("LOADLOG");

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	private ExecutionDao dao;

	@Inject
	private Kitty kitty;

	private MBeanServer jmxServer;

	private StatsData global = new StatsData("global");

	private ConcurrentHashMap<String, StatsData> scenarios = new ConcurrentHashMap<String, StatsData>();

	private ConcurrentHashMap<String, StatsData> steps = new ConcurrentHashMap<String, StatsData>();

	@PostConstruct
	public void init() throws Exception {
		jmxServer = ManagementFactory.getPlatformMBeanServer();
		ObjectName jmxName = new ObjectName("kitty.scenarios" + ":name=Summary");
		jmxServer.registerMBean(global, jmxName);
	}

	private boolean saveDb() {
		return kitty.getOptions().getSaveExecs();
	}

	@Override
	public void initStarted(ScenarioExecution execution) {
		if (saveDb()) {
			dao.execStart(execution);
		}
	}

	@Override
	public void stepPassed(ScenarioExecution execution, Step step) {
		long milis = step.getEnded().getTime() - step.getStarted().getTime();
		update(steps, step.getClass().getSimpleName(), milis, "kitty.steps");
	}

	@Override
	public void executionPassed(ScenarioExecution execution) {
		++global.xdone;
		long milis = execution.getEnded().getTime() - execution.getStarted().getTime();
		update(scenarios, execution.getScenario().getId(), milis, "kitty.scenarios");
		if (saveDb()) {
			dao.execEnded(execution);
		}
	}

	@Override
	public void executionFailed(ScenarioExecution execution, ScenarioStepException exception) {
		++global.xfail;
		String stepName = exception.getStep().getClass().getSimpleName();
		update(steps, stepName, -1, "kitty.steps");
		update(scenarios, exception.getScenario().getId(), -1, "kitty.scenarios");
		if (saveDb()) {
			dao.execEnded(execution);
		}
	}

	@Override
	public void initFailed(ScenarioExecution execution, ScenarioInitException exception) {
		++global.xfail;
		long milis = execution.getEnded().getTime() - execution.getStarted().getTime();
		update(scenarios, exception.getScenario().getId(), milis, "kitty.scenarios");
		if (saveDb()) {
			dao.execEnded(execution);
		}
	}

	private void update(Map<String, StatsData> list, String name, long milis, String jmxNameBase) {
		StatsData data = list.get(name);
		if (data == null) {
			data = new StatsData(name);
			list.put(name, data);
			try {
				ObjectName jmxName = new ObjectName(jmxNameBase + ":name=" + name);
				jmxServer.registerMBean(data, jmxName);
			} catch (Exception x) {
				log.warn("Selhal zapis do JMX", x);
			}
		}
		data.update(milis);
	}

	@Override
	public void initPassed(ScenarioExecution execution) {
		//nothing...
	}

	@Override
	public void executionStarted(ScenarioExecution execution) {
		//nothing...
	}

	@Override
	public void stepStarted(ScenarioExecution execution, Step step) {
		//nothing...
	}

}
