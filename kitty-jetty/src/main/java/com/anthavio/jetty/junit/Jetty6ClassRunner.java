package com.anthavio.jetty.junit;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import com.anthavio.jetty.Jetty6Wrapper;
import com.anthavio.jetty.spring.Jetty6Loader;
import com.anthavio.jetty.test.Jetty6Manager;
import com.anthavio.jetty.test.Jetty6Manager.JettySetupData;
import com.anthavio.jetty.test.JettyConfig;

/**
 * JUnit ClassRunner launching Jetty instance
 * 
 * Same can be done with @BeforeClass and @AfterClass annotations
 * 
 * @author martin.vanek
 *
 */
public class Jetty6ClassRunner extends BlockJUnit4ClassRunner {

	private JettyConfig[] configs;

	private Jetty6Manager manager = Jetty6Manager.i();

	private Jetty6Wrapper jetty;

	public Jetty6ClassRunner(Class<?> testClass) throws InitializationError {
		super(testClass);
		this.configs = Jetty6Loader.getJettyConfigs(testClass);
	}

	public Jetty6Wrapper getJetty() {
		return jetty;
	}

	@Override
	protected Statement withBeforeClasses(Statement statement) {

		for (JettyConfig config : configs) {
			JettySetupData jsd = new JettySetupData(config.home(), config.port(), config.configs(), config.cache());
			//FIXME maybe take first one and print warning
			jetty = manager.startJetty(jsd);
		}
		return super.withBeforeClasses(statement);
	}

	@Override
	protected Statement withAfterClasses(Statement statement) {
		Statement after = super.withAfterClasses(statement);
		//stop jettys
		//for (JettyWithConfig jettyWithConfig : jettys) {
		//	stopJetty(jettyWithConfig);
		//}
		return after;
	}

}
