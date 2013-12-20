/**
 * 
 */
package net.anthavio.tap.db;

import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author vanek
 *
 */
//@Configuration
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
@ComponentScan("net.anthavio.tap.db")
public class SpringConfig {

	@Inject
	@Named("PdbDataSource")
	private DataSource pdbDataSource;

	@Inject
	private Properties jpaProperties;

	@Inject
	private JpaVendorAdapter jpaVendorAdapter;

	@Bean(name = "PdbJdbcTemplate")
	public JdbcTemplate tapJdbcTemplate() {
		return new JdbcTemplate(pdbDataSource);
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
		factoryBean.setPersistenceUnitName("PdbUnit");
		factoryBean.setPersistenceXmlLocation("classpath:META-INF/persistence-pdb.xml");
		//factoryBean.setPackagesToScan(new String[] { "net.anthavio.tap.db" });
		factoryBean.setDataSource(pdbDataSource);
		factoryBean.setJpaVendorAdapter(jpaVendorAdapter);
		factoryBean.setJpaProperties(jpaProperties);
		return factoryBean;
	}

	/*
	@Bean
	public PersistenceAnnotationBeanPostProcessor persistenceAnnotationBeanPostProcessor() {
		PersistenceAnnotationBeanPostProcessor postProcessor = new PersistenceAnnotationBeanPostProcessor();
		postProcessor.setDefaultPersistenceUnitName("PdbUnit");
		return postProcessor;
	}
	 */
	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		PersistenceExceptionTranslationPostProcessor postProcessor = new PersistenceExceptionTranslationPostProcessor();
		return postProcessor;
	}
}
