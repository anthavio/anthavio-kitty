/**
 * 
 */
package com.anthavio.kitty.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;

import com.anthavio.kitty.Kitty;
import com.anthavio.kitty.scenario.Scenario;
import com.anthavio.kitty.scenario.ScenarioException;
import com.anthavio.kitty.scenario.ScenarioExecution;
import com.anthavio.kitty.scenario.ScenarioListener;
import com.anthavio.kitty.state.StateService;

/**
 * @author vanek
 *
 */
//@Component
public class ScenarioExecutor {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	private boolean stopFlag = false;

	private boolean scenarioRunning = false;

	private boolean batchRunning = false;

	private List<ScenarioListener> listeners = new ArrayList<ScenarioListener>();

	@Inject
	private StateService stateService;

	@Inject
	@Named("KittyTaskExecutor")
	private TaskExecutor taskExecutor;

	@Inject
	private Kitty tool;

	public void stopAsync() {
		this.stopFlag = true;
	}

	public boolean isRunning() {
		return scenarioRunning || batchRunning;
	}

	public boolean isScenarioRunning() {
		return scenarioRunning;
	}

	public boolean isBatchRunning() {
		return batchRunning;
	}

	public void addListener(ScenarioListener listener) {
		this.listeners.add(listener);
	}

	public void removeListener(ScenarioListener listener) {
		this.listeners.remove(listener);
	}

	public void clearListeners() {
		this.listeners.clear();
	}

	public void executeAsync(final DirectoryModel dirModel, boolean usePassedDir, boolean useFailedDir) {

		final File[] resultDirs = prepareResultDirs(dirModel.getPath(), usePassedDir, useFailedDir);

		dirModel.setStarted();
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				execute(dirModel, resultDirs);
			}
		});
	}

	private void execute(final DirectoryModel dirModel, File[] resultDirs) {

		final List<DirectoryItem> items = dirModel.getItems();
		try {
			this.stopFlag = false;
			this.batchRunning = true;
			for (int i = 0; i < items.size(); ++i) {
				if (stopFlag) {
					break;
				}
				DirectoryItem item = items.get(i);
				if (item.isSelected()) {
					if (item.getFile().isDirectory()) {
						executeDir(item);
					} else {
						executeFile(item);
					}
					try {
						if (resultDirs[0] != null && item.getExecution().getErrorMessage() == null) {
							FileUtils.moveFileToDirectory(item.getFile(), resultDirs[0], false);
						}
						if (resultDirs[1] != null && item.getExecution().getErrorMessage() != null) {
							FileUtils.moveFileToDirectory(item.getFile(), resultDirs[1], false);
						}
					} catch (IOException iox) {
						log.warn("Cannot move completed scenario " + item.getFile(), iox);
					}
				}
			}
		} finally {
			this.batchRunning = false;
		}
	}

	private void executeDir(DirectoryItem dirItem) {
		DirectoryModel directoryModel = tool.list(dirItem);
		dirItem.setStarted();
		try {
			this.stopFlag = false;
			this.batchRunning = true;
			for (DirectoryItem subItem : directoryModel.getItems()) {
				if (stopFlag) {
					break;
				}
				if (subItem.getFile().isDirectory()) {
					executeDir(subItem);
				} else {
					executeFile(subItem);
				}
			}
		} finally {
			this.batchRunning = false;
		}

		int errCnt = 0;
		for (DirectoryItem subItem : directoryModel.getItems()) {
			if (subItem.getExecution() != null && subItem.getExecution().getErrorMessage() != null) {
				++errCnt;
			}
		}
		if (errCnt > 0) {
			dirItem.setFailed(errCnt + " execution errors");
		} else {
			dirItem.setPassed();
		}

	}

	public void executeAsync(final DirectoryItem item) {
		taskExecutor.execute(new Runnable() {

			@Override
			public void run() {
				execute(item);
			}
		});
	}

	public void execute(final DirectoryItem item) {
		if (item.getFile().isDirectory()) {
			executeDir(item);
		} else {
			executeFile(item);
		}
	}

	private void executeFile(DirectoryItem item) {
		Scenario scenario = load(item);
		if (scenario == null) {
			return;
		}
		try {
			this.scenarioRunning = true;
			scenario.addListener(item);
			scenario.addListener(stateService);
			scenario.execute();
		} catch (Exception x) {
			log.error("Failed Scenario Execution " + item.getFile(), x);
		} finally {
			this.scenarioRunning = false;
		}
	}

	public void validate(final DirectoryModel dirModel) {
		List<DirectoryItem> items = dirModel.getItems();
		this.stopFlag = false;
		for (int i = 0; i < items.size(); ++i) {
			DirectoryItem item = items.get(i);
			if (stopFlag) {
				break;
			}
			if (item.isSelected()) {
				if (item.getFile().isDirectory()) {
					validateDir(item);
				} else {
					validateFile(item);
				}
			}
		}
	}

	public void validateAsync(final DirectoryModel dirModel) {
		dirModel.setStarted();
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				validate(dirModel);
			}
		});
	}

	public void validateAsync(final DirectoryItem item) {
		taskExecutor.execute(new Runnable() {

			@Override
			public void run() {
				validate(item);
			}
		});
	}

	public void validate(final DirectoryItem item) {
		if (item.getFile().isDirectory()) {
			validateDir(item);
		} else {
			validateFile(item);
		}
	}

	public void validateFile(final DirectoryItem item) {
		Scenario scenario = load(item);
		if (scenario == null) {
			return;
		}
		try {
			scenario.addListener(item);
			ScenarioExecution execution = scenario.validate();
			item.setExecution(execution);
		} catch (ScenarioException sx) {
			//ignore
			item.setFailed(sx);
		} catch (Exception x) {
			item.setFailed(x);
			log.error("Failed Scenario Validation " + item.getFile(), x);
		}
	}

	private void validateDir(DirectoryItem dirItem) {
		DirectoryModel directoryModel = tool.list(dirItem);
		dirItem.setStarted();

		List<DirectoryItem> items = directoryModel.getItems();
		for (int i = 0; i < items.size(); ++i) {
			DirectoryItem item = items.get(i);
			if (stopFlag) {
				break;
			}
			if (item.getFile().isDirectory()) {
				validateDir(item);
			} else {
				validateFile(item);
			}
		}

		int errCnt = 0;
		for (DirectoryItem subItem : directoryModel.getItems()) {
			if (subItem.getExecution() != null && subItem.getExecution().getErrorMessage() != null) {
				++errCnt;
			}
		}
		if (errCnt > 0) {
			dirItem.setFailed(errCnt + " validation errors");
		} else {
			dirItem.setMessage("Validation OK");
		}

	}

	private File[] prepareResultDirs(File dirPath, boolean usePassedDir, boolean useFailedDir) {
		final File[] resultDirs = new File[2];
		File passedDir = null;
		if (usePassedDir) {
			passedDir = new File(dirPath, tool.getPassedDirName());
			if (passedDir.exists()) {
				if (passedDir.isFile()) {
					log.error("Cannot create directory " + passedDir + " because file of same name exist");
					passedDir = null;
				}
			} else {
				passedDir.mkdir();
			}
		}
		resultDirs[0] = passedDir;

		File failedDir = null;
		if (useFailedDir) {
			failedDir = new File(dirPath, tool.getFailedDirName());
			if (failedDir.exists()) {
				if (failedDir.isFile()) {
					log.error("Cannot create directory " + failedDir + " because file of same name exist");
					failedDir = null;
				}
			} else {
				failedDir.mkdir();
			}
		}
		resultDirs[1] = failedDir;
		return resultDirs;
	}

	private Scenario load(DirectoryItem item) {
		if (item == null) {
			throw new IllegalArgumentException("DirectoryItem  must not be null");
		}
		try {
			return tool.loadScenario(item.getFile(), false);
		} catch (Exception x) {
			Date now = new Date();
			ScenarioExecution execution = new ScenarioExecution(item, now);
			execution.setEnded(now);
			execution.setErrorMessage(String.valueOf(x));
			item.setExecution(execution);
			return null;
		}
	}
}
