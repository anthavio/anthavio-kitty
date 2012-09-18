/**
 * 
 */
package com.anthavio.kitty;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.jdbc.config.SortedResourcesFactoryBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.CompositeDatabasePopulator;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.scheduling.backportconcurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anthavio.kitty.console.CmdLineConsole;
import com.anthavio.kitty.console.ExecuteCmd;
import com.anthavio.kitty.console.ValidateCmd;
import com.anthavio.kitty.model.ScenarioExecutor;
import com.anthavio.kitty.state.ExecutionDao;
import com.anthavio.kitty.state.StateService;
import com.anthavio.kitty.util.H2ServerFactory;
import com.anthavio.xml.jaxb.SimpleJaxbBinder;

/**
 * @author vanek
 * 
 * BeanPostProcessor example
 * @see org.springframework.scheduling.config.AnnotationDrivenBeanDefinitionParser
 * 
 * Simple Bean example
 * @see org.springframework.scheduling.config.ExecutorBeanDefinitionParser
 */
public class KittySpringXmlNamespaceHandler extends NamespaceHandlerSupport {

	public static final String H2SERVER = "KittyH2Server";

	public static final String SCENARIO_BINDER = "KittyScenarioBinder";

	public static final String TASK_EXECUTOR = "KittyTaskExecutor";

	public static final String DATA_SOURCE = "KittyDataSource";

	public static final String JDBC_TEMPLATE = "KittyJdbcTemplate";

	@Override
	public void init() {
		registerBeanDefinitionParser("kitty", new KittySpringXmlDefinitionParser());
	}

}

class KittySpringXmlDefinitionParser extends AbstractSingleBeanDefinitionParser {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private String kittyId = "Kitty";

	@Override
	protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext)
			throws BeanDefinitionStoreException {
		String id = super.resolveId(element, definition, parserContext);
		if (StringUtils.hasText(id)) {
			kittyId = id;
		}
		return kittyId;
	}

	@Override
	protected String getBeanClassName(Element element) {
		return KittyFactoryBean.class.getName();
	}

	@Override
	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {

		configureExecutor(element, parserContext);

		//SimpleJaxbBinder<Scenario> scenarioBinder = getScenarioBinder(element, parserContext);
		configureScenarioBinder(element, parserContext);
		builder.addPropertyReference("scenarioBinder", KittySpringXmlNamespaceHandler.SCENARIO_BINDER);

		KittyOptions options = getOptions(element);
		builder.addPropertyValue("options", options);

		configurePersistence(element, parserContext);

		configureConsole(getChildElement(element, "console", parserContext), parserContext);
	}

	private void configureConsole(Element eConsole, ParserContext parserContext) {
		if (eConsole != null) {

		}
		register(ExecuteCmd.class, parserContext);
		register(ValidateCmd.class, parserContext);

		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(CmdLineConsole.class);
		builder.addConstructorArgReference(kittyId);
		parserContext.getRegistry().registerBeanDefinition(CmdLineConsole.class.getSimpleName(),
				builder.getBeanDefinition());
	}

	private void register(Class<?> clazz, ParserContext parserContext) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
		parserContext.getRegistry().registerBeanDefinition(clazz.getSimpleName(), builder.getBeanDefinition());
	}

	private void register(Class<?> clazz, String beanName, ParserContext parserContext) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
		parserContext.getRegistry().registerBeanDefinition(beanName, builder.getBeanDefinition());
	}

	protected ClassPathBeanDefinitionScanner configureScanner(ParserContext parserContext, Element element) {
		XmlReaderContext readerContext = parserContext.getReaderContext();

		// Delegate bean definition registration to scanner class.
		ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(readerContext.getRegistry(), true);
		scanner.setResourceLoader(readerContext.getResourceLoader());
		scanner.setEnvironment(parserContext.getDelegate().getEnvironment());
		scanner.setBeanDefinitionDefaults(parserContext.getDelegate().getBeanDefinitionDefaults());
		scanner.setAutowireCandidatePatterns(parserContext.getDelegate().getAutowireCandidatePatterns());

		return scanner;
	}

	private void configureExecutor(Element element, ParserContext parserContext) {
		String taskExecutor = element.getAttribute("executor");
		if (StringUtils.hasText(taskExecutor)) {
			if (!taskExecutor.equals(KittySpringXmlNamespaceHandler.TASK_EXECUTOR)) {
				parserContext.getRegistry().registerAlias(taskExecutor, KittySpringXmlNamespaceHandler.TASK_EXECUTOR);
			}
		} else {
			configureTaskExecutor(element, parserContext);
		}

		register(ScenarioExecutor.class, "KittyScenarioExecutor", parserContext);

		register(StateService.class, "KittyStateService", parserContext);

		register(ExecutionDao.class, "KittyExecutionDao", parserContext);

	}

	private void configurePersistence(Element element, ParserContext parserContext) {
		Element ePersistence = getChildElement(element, "persistence", parserContext);
		String initScript = "classpath:kitty-schema.sql";

		if (ePersistence == null) {
			int tcpPort = configureH2Server(parserContext, null);
			configureDataSource(parserContext, null, tcpPort);

		} else {
			Element eEmbeddedH2 = getChildElement(ePersistence, "embedded-h2", parserContext);
			Element eDataSourceRef = getChildElement(ePersistence, "data-source-ref", parserContext);
			Element eDataSourceDef = getChildElement(ePersistence, "data-source", parserContext);
			initScript = ePersistence.getAttribute("init-script");

			if (eDataSourceRef != null) {
				String dsRefName = eDataSourceRef.getAttribute("name");
				if (!dsRefName.equals(KittySpringXmlNamespaceHandler.DATA_SOURCE)) {
					parserContext.getRegistry().registerAlias(dsRefName, KittySpringXmlNamespaceHandler.DATA_SOURCE);
				}
			}

			if (eDataSourceDef != null) {
				configureDataSource(parserContext, eDataSourceDef, -1);
			}

			if (eEmbeddedH2 != null) {
				int tcpPort = configureH2Server(parserContext, eEmbeddedH2);
				configureDataSource(parserContext, null, tcpPort);
			}
		}

		configureJdbcTemplate(parserContext);

		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DataSourceInitializer.class);
		builder.addPropertyReference("dataSource", KittySpringXmlNamespaceHandler.DATA_SOURCE);
		builder.addPropertyValue("enabled", true);

		//DatabasePopulatorConfigUtils.setDatabasePopulator(element, builder);
		builder.addPropertyValue("databasePopulator", createDatabasePopulator(initScript));

		builder.getRawBeanDefinition().setSource(parserContext.extractSource(element));
		parserContext.getRegistry().registerBeanDefinition("KittyDbInit", builder.getBeanDefinition());
	}

	private BeanDefinition createDatabasePopulator(String scriptLocation) {

		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(CompositeDatabasePopulator.class);
		ManagedList<BeanMetadataElement> delegates = new ManagedList<BeanMetadataElement>();

		BeanDefinitionBuilder delegate = BeanDefinitionBuilder.genericBeanDefinition(ResourceDatabasePopulator.class);
		delegate.addPropertyValue("ignoreFailedDrops", true);
		delegate.addPropertyValue("continueOnError", false);

		BeanDefinitionBuilder resourcesFactory = BeanDefinitionBuilder
				.genericBeanDefinition(SortedResourcesFactoryBean.class);
		resourcesFactory.addConstructorArgValue(new TypedStringValue(scriptLocation));

		delegate.addPropertyValue("sqlScriptEncoding", new TypedStringValue("utf-8"));
		//delegate.addPropertyValue("separator", new TypedStringValue(scriptElement.getAttribute("separator")));
		delegate.addPropertyValue("scripts", resourcesFactory.getBeanDefinition());

		delegates.add(delegate.getBeanDefinition());

		builder.addPropertyValue("populators", delegates);
		return builder.getBeanDefinition();
	}

	private void configureJdbcTemplate(ParserContext parserContext) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(JdbcTemplate.class);
		builder.addConstructorArgReference(KittySpringXmlNamespaceHandler.DATA_SOURCE);
		parserContext.getRegistry().registerBeanDefinition(KittySpringXmlNamespaceHandler.JDBC_TEMPLATE,
				builder.getBeanDefinition());
	}

	private void configureDataSource(ParserContext parserContext, Element eDataSourceDef, int h2tcpPort) {
		String driverClassName = "org.h2.Driver";
		String url = "jdbc:h2:tcp://localhost:" + h2tcpPort + "/kitty";
		String username = "sa";
		String password = "sa";
		int maxActive = 8;

		if (eDataSourceDef != null) {
			Element eDriverClassName = getChildElement(eDataSourceDef, "driverClassName", parserContext);
			driverClassName = eDriverClassName.getTextContent().trim();

			Element eUrl = getChildElement(eDataSourceDef, "url", parserContext);
			url = eUrl.getTextContent().trim();

			Element eUsername = getChildElement(eDataSourceDef, "username", parserContext);
			username = eUsername.getTextContent().trim();

			Element ePassword = getChildElement(eDataSourceDef, "password", parserContext);
			password = ePassword.getTextContent().trim();

			Element eMaxActive = getChildElement(eDataSourceDef, "maxActive", parserContext);
			if (eMaxActive != null) {
				maxActive = Integer.parseInt(eMaxActive.getTextContent().trim());
			}

		}
		/*
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(DriverManagerDataSource.class);
		builder.addPropertyValue("driverClassName", driverClassName);
		builder.addPropertyValue("url", url);
		builder.addPropertyValue("username", username);
		builder.addPropertyValue("password", password);
		*/

		BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(BasicDataSource.class);
		builder.addPropertyValue("driverClassName", driverClassName);
		builder.addPropertyValue("url", url);
		builder.addPropertyValue("username", username);
		builder.addPropertyValue("password", password);
		builder.addPropertyValue("maxActive", maxActive);

		if (eDataSourceDef == null) {
			builder.addDependsOn(KittySpringXmlNamespaceHandler.H2SERVER);
		}

		builder.setDestroyMethodName("close");
		parserContext.getRegistry().registerBeanDefinition(KittySpringXmlNamespaceHandler.DATA_SOURCE,
				builder.getBeanDefinition());
	}

	private KittyOptions getOptions(Element element) {
		KittyOptions options;
		String className = element.getAttribute("options-class");
		if (StringUtils.hasText(className)) {
			try {
				options = (KittyOptions) Class.forName(className).newInstance();
			} catch (Exception x) {
				throw new IllegalArgumentException("Kitty option class is wrong " + className);
			}
		} else {
			options = new KittyOptions();
		}
		return options;
	}

	private void configureScenarioBinder(Element element, ParserContext parserContext) {
		String className = element.getAttribute("scenario-class");
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(SimpleJaxbBinder.class);
		builder.addConstructorArgValue(className);
		builder.addConstructorArgValue(false);
		parserContext.getRegistry().registerBeanDefinition(KittySpringXmlNamespaceHandler.SCENARIO_BINDER,
				builder.getBeanDefinition());
	}

	/*
	private SimpleJaxbBinder<Scenario> getScenarioBinder(Element element, ParserContext parserContext) {
		String className = element.getAttribute("scenario-class");
		Class<Scenario> clazz;
		try {
			clazz = (Class<Scenario>) Class.forName(className);
			SimpleJaxbBinder<Scenario> scenarioBinder = new SimpleJaxbBinder<Scenario>(clazz, false);
			return scenarioBinder;
		} catch (ClassNotFoundException cnfx) {
			//parserContext.getReaderContext().error("scenario-class " + className + " not found", element);
			throw new IllegalArgumentException("Scenario class not found: " + className);
		}
	}
	*/
	private void configureTaskExecutor(Element element, ParserContext parserContext) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ThreadPoolTaskExecutor.class);
		builder.addPropertyValue("threadNamePrefix", "kitty-");
		builder.addPropertyValue("corePoolSize", 1);
		builder.addPropertyValue("maxPoolSize", 5);
		builder.addPropertyValue("queueCapacity", 0);
		builder.addPropertyValue("allowCoreThreadTimeOut", true);
		//builder.setDestroyMethodName("destroy");
		parserContext.getRegistry().registerBeanDefinition(KittySpringXmlNamespaceHandler.TASK_EXECUTOR,
				builder.getBeanDefinition());
	}

	private int configureH2Server(ParserContext parserContext, Element eEmbeddedH2) {
		//default values
		int tcpPort = 9002;
		String baseDir = "target/h2db";

		if (eEmbeddedH2 != null) {
			String aTcpPort = eEmbeddedH2.getAttribute("tcpPort");
			if (StringUtils.hasText(aTcpPort)) {
				tcpPort = Integer.parseInt(aTcpPort);
			}
			String aBaseDir = eEmbeddedH2.getAttribute("baseDir");
			if (StringUtils.hasText(aBaseDir)) {
				baseDir = aBaseDir;
			}
		}
		log.info("Configuring H2Server on port: " + tcpPort + ", baseDir: " + baseDir);

		String args = "-tcp,-tcpAllowOthers,-tcpDaemon,-tcpPort," + tcpPort + ",-baseDir," + baseDir;
		//Server h2server = H2ServerFactory.startTcpServer();
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(H2ServerFactory.class.getName(),
				"startTcpServer");
		//builder.setFactoryMethod("startTcpServer");
		builder.addConstructorArgValue(args);
		//builder2.setInitMethodName("start");
		builder.setDestroyMethodName("shutdown");
		//builder2.getRawBeanDefinition().setSource(h2server);
		parserContext.getRegistry().registerBeanDefinition(KittySpringXmlNamespaceHandler.H2SERVER,
				builder.getBeanDefinition());
		return tcpPort;
	}

	private Element getChildElement(Element parent, String childName, ParserContext parserContext) {
		NodeList childNodes = parent.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node child = childNodes.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE && childName.equals(parserContext.getDelegate().getLocalName(child))) {
				return (Element) child;
			}
		}
		return null;
	}
}
