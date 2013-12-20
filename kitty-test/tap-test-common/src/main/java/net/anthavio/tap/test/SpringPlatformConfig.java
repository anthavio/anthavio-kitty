/**
 * 
 */
package net.anthavio.tap.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.xml.bind.Marshaller;

import net.anthavio.xml.NamespaceMapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.ws.soap.axiom.AxiomSoapMessageFactory;

import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jdbc.AtomikosDataSourceBean;

/**
 * @author vanek
 *
 */
@Configuration
@PropertySource("kitty-tap.properties")
public class SpringPlatformConfig {

	@Inject
	private Environment env;

	/*
	@Bean
	public static PropertyPlaceholderConfigurer properties() {
		//PropertySourcesPlaceholderConfigurer
		PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
		final Resource[] resources = new ClassPathResource[] { new ClassPathResource("example-platform.properties") };
		ppc.setLocations(resources);
		ppc.setFileEncoding("utf-8");
		ppc.setSystemPropertiesMode(PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_OVERRIDE);
		ppc.setSearchSystemEnvironment(true);
		return ppc;
	}
	 */

	@Bean(name = "PdbDataSource", initMethod = "init", destroyMethod = "close")
	public DataSource tapDataSource() {
		AtomikosDataSourceBean dataSource = new AtomikosDataSourceBean();
		dataSource.setXaDataSourceClassName(env.getRequiredProperty("pdb.db.driver"));
		dataSource.setUniqueResourceName("xaPdbDS");
		dataSource.setMinPoolSize(1);
		dataSource.setMaxPoolSize(5);
		Properties properties = new Properties();
		properties.put("URL", env.getRequiredProperty("pdb.db.url"));
		properties.put("user", env.getRequiredProperty("pdb.db.username"));
		properties.put("password", env.getRequiredProperty("pdb.db.password"));
		dataSource.setXaProperties(properties);
		return dataSource;
	}

	@Bean(name = "AnotherDataSource", initMethod = "init", destroyMethod = "close")
	public DataSource anotherDataSource() {
		AtomikosDataSourceBean dataSource = new AtomikosDataSourceBean();
		dataSource.setXaDataSourceClassName(env.getRequiredProperty("pdb.db.driver"));
		dataSource.setUniqueResourceName("xaAnotherDS");
		dataSource.setMinPoolSize(1);
		dataSource.setMaxPoolSize(5);
		Properties properties = new Properties();
		properties.put("URL", env.getRequiredProperty("pdb.db.url"));
		properties.put("user", env.getRequiredProperty("pdb.db.username"));
		properties.put("password", env.getRequiredProperty("pdb.db.password"));
		dataSource.setXaProperties(properties);
		return dataSource;
	}

	// JPA - Hibernate

	@Bean(name = "JpaProperties")
	public Properties jpaProperties() {
		Properties properties = new Properties();
		properties.put("hibernate.transaction.manager_lookup_class",
				"com.atomikos.icatch.jta.hibernate3.TransactionManagerLookup");
		//org.hibernate.transaction.BTMTransactionManagerLookup http://docs.codehaus.org/display/BTM/Hibernate2x#Hibernate2x-Transactionmanagerlookupclass
		properties.put("hibernate.hbm2ddl.auto", "create-drop");
		return properties;
	}

	@Bean(name = "JpaVendorAdapter")
	public JpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
		jpaVendorAdapter.setDatabasePlatform(env.getRequiredProperty("pdb.db.dialect"));
		jpaVendorAdapter.setGenerateDdl(Boolean.valueOf(env.getRequiredProperty("pdb.db.showSql")));
		jpaVendorAdapter.setShowSql(Boolean.valueOf(env.getRequiredProperty("pdb.db.generateDdl")));
		return jpaVendorAdapter;
	}

	// JTA - Atomikos

	@Bean(name = "transactionManager")
	public PlatformTransactionManager transactionManager() {
		JtaTransactionManager transactionManager = new JtaTransactionManager();
		transactionManager.setDefaultTimeout(60);
		transactionManager.setTransactionManager(atomikosTM());
		transactionManager.setUserTransaction(atomikosUT());
		return transactionManager;
	}

	@Bean(name = "AtomikosTransactionService", initMethod = "init", destroyMethod = "shutdownWait")
	public UserTransactionServiceImp atomikosTs() {
		Properties properties = new Properties();
		properties.put("com.atomikos.icatch.service", "com.atomikos.icatch.standalone.UserTransactionServiceFactory");
		properties.put(AbstractUserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME, "target/atomikos");
		properties.put(AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME, "target/atomikos");
		properties.put(AbstractUserTransactionServiceFactory.CONSOLE_FILE_NAME_PROPERTY_NAME, "kitty_tm.out");
		properties.put(AbstractUserTransactionServiceFactory.CONSOLE_LOG_LEVEL_PROPERTY_NAME, "INFO");
		properties.put(AbstractUserTransactionServiceFactory.LOG_BASE_NAME_PROPERTY_NAME, "kitty_tm.log");
		properties.put(AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME, "kitty_tm");
		//properties.put(AbstractUserTransactionServiceFactory.DEFAULT_JTA_TIMEOUT_PROPERTY_NAME, 180);
		UserTransactionServiceImp uts = new UserTransactionServiceImp(properties);
		//uts.setInitialLogAdministrators(Arrays.asList(new LocalLogAdministrator()));
		return uts;
	}

	@DependsOn("AtomikosTransactionService")
	@Bean(name = "AtomikosTransactionManager", initMethod = "init", destroyMethod = "close")
	public UserTransactionManager atomikosTM() {
		UserTransactionManager atm = new UserTransactionManager();
		atm.setForceShutdown(true);
		return atm;
	}

	@DependsOn("AtomikosTransactionService")
	@Bean(name = "AtomikosUserTransaction")
	public UserTransactionImp atomikosUT() {
		UserTransactionImp aut = new UserTransactionImp();
		return aut;
	}

	//JAXB & WS

	@Bean(name = "messageFactory")
	public AxiomSoapMessageFactory axiomSoapMessageFactory() {
		AxiomSoapMessageFactory messageFactory = new AxiomSoapMessageFactory();
		return messageFactory;
	}

	@Bean(name = "NamespaceMapper")
	public NamespaceMapper namespaceMapper() {
		NamespaceMapper mapper = new NamespaceMapper();
		Map<String, String> map = new HashMap<String, String>();
		map.put("xmsg", "http://example.komix.cz/messages");
		map.put("xdtp", "http://example.komix.cz/types");
		mapper.setPefixToUriMap(map);
		return mapper;
	}

	@Bean(name = "JaxbMarshallerProperties")
	public Map<String, Object> jaxbProperties() {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(Marshaller.JAXB_ENCODING, "utf-8");
		properties.put(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		properties.put("com.sun.xml.bind.namespacePrefixMapper", namespaceMapper());
		return properties;
	}
}
