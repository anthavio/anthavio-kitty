package com.anthavio.kitty;

import java.io.File;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.kohsuke.args4j.Option;

/**
 * @author vanek
 *
 */
public class KittyOptions {

	@Option(name = "-initialDir", metaVar = "path", usage = "Iinitial directory with scenario configuration files")
	private File initialDir = new File(System.getProperty("kitty.initialDir", System.getProperty("user.dir")));

	@Option(name = "-scenarioPrefix", metaVar = "prefix", usage = "Scenario configuration file prefix")
	private String scenarioPrefix = System.getProperty("kitty.scenarioPrefix", "kt-");

	@Option(name = "-usePassedDir", metaVar = "true/false", usage = "Use passed dir")
	private boolean usePassedDir = getBoolSysProp("kitty.usePassedDir", false);

	@Option(name = "-passedDir", metaVar = "name", usage = "Directory for passed scenario files")
	private String passedDir = System.getProperty("kitty.passedDir", "!passed");

	@Option(name = "-useFailedDir", metaVar = "true/false", usage = "Use failed dir")
	private boolean useFailedDir = getBoolSysProp("kitty.useFailedDir", false);

	@Option(name = "-failedDir", metaVar = "name", usage = "Directory for failed scenario files")
	private String failedDir = System.getProperty("kitty.failedDir", "!failed");

	@Option(name = "-passedPause", metaVar = "seconds", usage = "Sleep time after passed scenario execution in seconds")
	private int passedPause = getIntSysProp("kitty.passedPause", 0);

	@Option(name = "-failedPause", metaVar = "seconds", usage = "Sleep time after failed scenario execution in seconds")
	private int failedPause = getIntSysProp("kitty.failedPause", 0);

	@Option(name = "-saveExecs", metaVar = "true/false", usage = "Store scenario execution results into database")
	private boolean saveExecs = getBoolSysProp("kitty.saveExecs", true);

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

	private int getIntSysProp(String envName, int defval) {
		return Integer.parseInt(System.getProperty(envName, String.valueOf(defval)));
	}

	private boolean getBoolSysProp(String envName, boolean defval) {
		return Boolean.parseBoolean(System.getProperty(envName, String.valueOf(defval)));
	}

	public String getPassedDir() {
		return passedDir;
	}

	public String getFailedDir() {
		return failedDir;
	}

	public File getInitialDir() {
		return initialDir;
	}

	public void setInitialDir(File configDir) {
		this.initialDir = configDir;
	}

	public int getPassedPause() {
		return passedPause;
	}

	public void setPassedPause(int passedPause) {
		this.passedPause = passedPause;
	}

	public int getFailedPause() {
		return failedPause;
	}

	public void setFailedPause(int failedPause) {
		this.failedPause = failedPause;
	}

	public String getScenarioPrefix() {
		return scenarioPrefix;
	}

	public void setScenarioPrefix(String scenarioPrefix) {
		this.scenarioPrefix = scenarioPrefix;
	}

	public boolean getSaveExecs() {
		return saveExecs;
	}

	public void setSaveExecs(boolean persist) {
		this.saveExecs = persist;
	}

	public boolean getUsePassedDir() {
		return usePassedDir;
	}

	public void setUsePassedDir(boolean usePassedDir) {
		this.usePassedDir = usePassedDir;
	}

	public boolean getUseFailedDir() {
		return useFailedDir;
	}

	public void setUseFailedDir(boolean useFailedDir) {
		this.useFailedDir = useFailedDir;
	}

	public void setPassedDir(String passedDir) {
		this.passedDir = passedDir;
	}

	public void setFailedDir(String failedDir) {
		this.failedDir = failedDir;
	}
}