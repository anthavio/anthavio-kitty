/**
 * 
 */
package com.anthavio.jetty.spring;

import java.util.Arrays;

import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.context.access.ContextSingletonBeanFactoryLocator;
import org.springframework.context.support.AbstractApplicationContext;

import com.anthavio.jetty.test.JettyConfig;
import com.anthavio.jetty.test.JettyConfigs;
import com.anthavio.spring.test.ContextRefLoader;

/**
 * FIXME upgrade to use JettyManager to manage instances
 * 
 * @author vanek
 *
 */
public class JettyLoader extends ContextRefLoader {

	@Override
	public String[] processLocations(Class<?> testClass, String... locations) {
		JettyConfigs annotations = testClass.getAnnotation(JettyConfigs.class);
		JettyConfig annotation = testClass.getAnnotation(JettyConfig.class);
		if ((annotations == null || annotations.value().length == 0) && annotation == null) {
			throw new IllegalArgumentException("Annotation @JettyConfig(s) not present on " + testClass.getName());
		}

		if (annotations != null && annotations.value().length == 0 && annotation != null) {
			throw new IllegalArgumentException("Both Annotations @JettyConfig(s) are present on " + testClass.getName());
		}

		if (annotation != null) {
			startJetty(annotation);
		}

		if (annotations != null) {
			for (JettyConfig item : annotations.value()) {
				startJetty(item);
			}
		}

		return super.processLocations(testClass, locations);
	}

	/**
	 * We must to override this because jetty shutdowns contexts and our own 
	 * ContextRefLoader context shutdown hooks make only mess 
	 */
	@Override
	public AbstractApplicationContext loadContext(String... locations) throws Exception {
		logger.info("Loading " + locations[0] + " context with selector " + locations[1]);
		BeanFactoryLocator locator = ContextSingletonBeanFactoryLocator.getInstance(locations[1]);
		BeanFactoryReference reference = locator.useBeanFactory(locations[0]);
		AbstractApplicationContext context = (AbstractApplicationContext) reference.getFactory();
		return context;
	}

	/**
	 * @param annotation
	 */
	private void startJetty(JettyConfig annotation) {
		String jettyHome = annotation.home();
		String[] configs = annotation.configs();
		logger.info("Starting jetty " + jettyHome + " " + Arrays.asList(configs));
		//new JettyWrapper(jettyHome, configs).start();
	}

}
