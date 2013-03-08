/**
 * 
 */
package com.anthavio.jetty.spring;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.context.access.ContextSingletonBeanFactoryLocator;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.StaticApplicationContext;

import com.anthavio.jetty.test.Jetty6Manager;
import com.anthavio.jetty.test.Jetty6Manager.JettySetupData;
import com.anthavio.jetty.test.JettyConfig;
import com.anthavio.jetty.test.JettyConfigs;
import com.anthavio.spring.test.ContextRefLoader;

/**
 * Spring test context loader starting Jetty instances
 * 
 * Cannot shutdown these instances though
 * 
 * @author vanek
 *
 */
public class Jetty6Loader extends ContextRefLoader {

	private Jetty6Manager manager = Jetty6Manager.i();

	@Override
	public String[] processLocations(Class<?> testClass, String... locations) {

		JettyConfig[] jettyConfigs = getJettyConfigs(testClass);

		for (JettyConfig item : jettyConfigs) {
			JettySetupData jettyId = new JettySetupData(item.home(), item.port(), item.configs(), item.cache());
			manager.startJetty(jettyId);
		}
		if (locations.length == 0) {
			//no spring context locations -> jetty start only
			return locations;
		} else {
			return super.processLocations(testClass, locations);
		}
	}

	/**
	 * We must to override this because jetty shutdowns contexts and our own 
	 * ContextRefLoader context shutdown hooks make only mess 
	 */
	@Override
	public AbstractApplicationContext loadContext(String... locations) throws Exception {
		if (locations.length == 0) {
			//no spring context locations -> jetty start only
			logger.info("No location are specified. Returning empty context");
			return new StaticApplicationContext();
		} else {
			logger.info("Loading " + locations[0] + " context with selector " + locations[1]);
			BeanFactoryLocator locator = ContextSingletonBeanFactoryLocator.getInstance(locations[1]);
			BeanFactoryReference reference = locator.useBeanFactory(locations[0]);
			AbstractApplicationContext context = (AbstractApplicationContext) reference.getFactory();
			return context;
		}
	}

	public static JettyConfig[] getJettyConfigs(Class<?> testClass) {
		JettyConfigs multiConfig = testClass.getAnnotation(JettyConfigs.class);
		JettyConfig singleConfig = testClass.getAnnotation(JettyConfig.class);
		if ((multiConfig == null || multiConfig.value().length == 0) && singleConfig == null) {
			throw new IllegalArgumentException("Annotation @JettyConfig(s) not present on " + testClass.getName());
		}

		if (multiConfig != null && multiConfig.value().length != 0 && singleConfig != null) {
			throw new IllegalArgumentException("Both Annotations @JettyConfig(s) are present on " + testClass.getName());
		}
		List<JettyConfig> list = new LinkedList<JettyConfig>();
		if (singleConfig != null) {
			list.add(singleConfig);
		}
		if (multiConfig != null) {
			JettyConfig[] configs = multiConfig.value();
			for (JettyConfig config : configs) {
				list.add(config);
			}
		}
		return list.toArray(new JettyConfig[list.size()]);
	}
}
