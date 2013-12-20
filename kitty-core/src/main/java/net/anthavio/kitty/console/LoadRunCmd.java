/**
 * 
 */
package net.anthavio.kitty.console;

import java.io.File;
import java.util.Random;
import java.util.Scanner;

import net.anthavio.kitty.model.DirectoryItem;
import net.anthavio.kitty.scenario.Scenario;

import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


/**
 * @author vanek
 *
 */
public abstract class LoadRunCmd extends Command {

	//thread control flag
	protected boolean stopFlag;

	protected File[] scenarios;

	private StarterThread thread;

	public boolean isStarted() {
		return thread != null;
	}

	@Override
	public void execute(DirectoryItem item, Scanner scanner) {
		if (thread != null) {
			stop();
		} else {
			scenarios = listFiles(item);
			log.info("Scenario count: " + scenarios.length);
			if (scenarios == null || scenarios.length == 0) {
				throw new IllegalArgumentException("No scenario found in " + item.getFile().getAbsolutePath());
			}
			stopFlag = false;
			thread = new StarterThread(scenarios, getRps(), getSequential());
			thread.start();
		}
	}

	public void stop() {
		if (thread == null) {
			throw new IllegalStateException("Not started");
		}
		stopFlag = true;
		thread = null;
	}

	protected abstract ThreadPoolTaskExecutor getTaskExecutor();

	/**
	 * Requests Per Second
	 */
	public abstract float getRps();

	/**
	 * Run sequentialy or randomly
	 */
	public abstract boolean getSequential();

	/**
	 * Override this if required
	 */
	protected void executeScenario(File file) {
		getTaskExecutor().execute(new ScenarioRunnable(file));
	}

	public class StarterThread extends Thread {

		private Random random;

		private float rps;

		private ThreadPoolTaskExecutor executor;

		private File[] scenarios;

		int idxScenario = -1; //first will be zero if sequential == true

		public StarterThread(File[] scenarios, float rps, boolean sequential) {
			this.scenarios = scenarios;
			this.rps = rps;

			if (sequential == false) {
				random = new Random(System.currentTimeMillis());
			}
		}

		protected synchronized File getNextScenario() {
			if (random != null) {
				idxScenario = random.nextInt(scenarios.length);
			} else {
				idxScenario++;
				if (idxScenario >= scenarios.length) {
					idxScenario = 0;
				}
			}
			return scenarios[idxScenario];
		}

		@Override
		public void run() {
			int delta = (int) ((1 / rps) * 1000);
			while (!stopFlag) {
				try {
					File next = getNextScenario();
					executeScenario(next);
				} catch (TaskRejectedException trx) {
					log.error("Threads exhausted " + executor.getMaxPoolSize());
				} catch (Exception x) {
					log.error("Bad thing happend", x);
				}
				try {
					Thread.sleep(delta);
				} catch (InterruptedException ix) {
					log.debug("Got interrupted...");
					//break;
				}
			}
		}

	}

	public class ScenarioRunnable implements Runnable {

		private File fConfig;

		public ScenarioRunnable(File fConfig) {
			this.fConfig = fConfig;
		}

		public void run() {
			Scenario config = null;
			try {
				config = kitty.loadScenario(fConfig, true);
				config.execute();
			} catch (Exception x) {
				String scname = config != null ? config.getId() : "LOAD_ERROR " + fConfig;
				log.error("Scenar " + scname + " failed", x);
			}
		}
	}

}
