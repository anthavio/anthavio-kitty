package net.anthavio.kitty.console;

import java.io.File;

import net.anthavio.kitty.scenario.Scenario;


/**
 * @author vanek
 *
 */
public class ValidateCmd extends RecursiveCmd {

	@Override
	public CmdInfo getInfo() {
		return new CmdInfo("v", "Validate scenarios");
	}

	@Override
	protected void executeFile(File file) throws Exception {
		try {
			Scenario config = kitty.loadScenario(file, false);
			config.validate();
		} catch (Exception x) {
			log.warn("Scenar " + file.getAbsolutePath() + " obsahuje chybu" + x.getMessage());
			log.info("Scenar " + file.getAbsolutePath() + " obsahuje chybu", x);
		}
	}
	/*
		public void validate(String xml) throws Exception {
			Scenario scenario = loadScenario(xml);
			scenario.init();
		}
	*/
}
