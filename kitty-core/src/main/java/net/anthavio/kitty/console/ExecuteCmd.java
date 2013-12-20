package net.anthavio.kitty.console;

import java.io.File;

import net.anthavio.kitty.scenario.Scenario;


/**
 * @author vanek
 * 
 * Executes scenario if file is input
 * Runs recursively if input is directory 
 */
public class ExecuteCmd extends RecursiveCmd {

	public static final String KEY = "[number]";

	@Override
	public CmdInfo getInfo() {
		return new CmdInfo(KEY, "Execute scenario / Change directory");
	}

	@Override
	protected void executeFile(File file) throws Exception {
		Scenario scenario = kitty.loadScenario(file, true);
		scenario.execute();
	}
	/*
		public void executeScenario(String xml) throws Exception {
			Scenario scenario = loadScenario(xml);
			scenario.execute();
		}
	*/
}