/**
 * 
 */
package com.anthavio.tap.test;

import java.net.URL;
import java.util.Map;

import javax.inject.Inject;

import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableSpringConfigured;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.anthavio.kitty.KittyOptions;
import com.anthavio.spring.ssl.JksSslSocketFactory;
import com.anthavio.spring.ws.HttpClient4Sender;
import com.anthavio.xml.jaxb.SimpleJaxbBinder;

/**
 * @author vanek
 *
 */
@Configuration
@EnableSpringConfigured
@PropertySource("kitty-tap.properties")
@ImportResource("classpath:spring/kitty-core.xml")
@ComponentScan(basePackages = "com.anthavio.tap.test", excludeFilters = @ComponentScan.Filter(Configuration.class))
public class SpringKittyConfig {

	@Inject
	private Environment env;

	@Inject
	private Map<String, Object> jaxbProperties;

	@Inject
	private WebServiceMessageFactory soapMessageFactory;

	@Bean(name = "KittyScenarioPrefix")
	public String scenarioPrefix() {
		return "ki-";
	}

	@Bean(name = "KittyScenarioBinder")
	public SimpleJaxbBinder<TapScenario> scenarioBinder() {
		return new SimpleJaxbBinder<TapScenario>(TapScenario.class, false);
	}

	@Bean(name = "KittyOptions")
	public KittyOptions kittyOptions() {
		return new KittyOptions();
	}

	@Bean(name = "KittyTaskExecutor")
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor bean = new ThreadPoolTaskExecutor();
		bean.setThreadNamePrefix("kitty-");
		bean.setAllowCoreThreadTimeOut(true);
		bean.setCorePoolSize(1);
		bean.setMaxPoolSize(5);
		bean.setQueueCapacity(0);
		return bean;
	}

	@Bean
	public TapWebClient webClient() {
		return new TapWebClient(env.getRequiredProperty("example.webui.url"));
	}

	@Bean
	public TapWebDriver webDriver() {
		return new TapWebDriver(env.getRequiredProperty("example.webui.url"));
	}

	@Bean(name = "ExampleMessagesBinder")
	public Jaxb2Marshaller messagesJaxb2Marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setSchema(new ClassPathResource("schema/PingMessages.xsd"));
		marshaller.setContextPath(com.anthavio.example.messages.ObjectFactory.class.getPackage().getName());
		marshaller.setMarshallerProperties(jaxbProperties);
		marshaller.setMtomEnabled(true);
		return marshaller;
	}

	@Bean
	public WebServiceTemplate WebServiceTemplate() {
		WebServiceTemplate template = new WebServiceTemplate(soapMessageFactory);
		template.setMessageSender(httpClient4Sender());
		template.setMarshaller(messagesJaxb2Marshaller());
		template.setUnmarshaller(messagesJaxb2Marshaller());
		template.setDefaultUri(env.getRequiredProperty("example.wsvc.url"));
		return template;
	}

	@Bean
	public HttpClient4Sender httpClient4Sender() {
		HttpClient4Sender sender = new HttpClient4Sender();
		sender.setConnectionTimeout(env.getRequiredProperty("example.wsvc.connect.timeout", Integer.class));
		sender.setReadTimeout(env.getRequiredProperty("example.wsvc.read.timeout", Integer.class));
		sender.setMaxTotalConnections(env.getRequiredProperty("example.wsvc.connections", Integer.class));

		String username = env.getRequiredProperty("example.wsvc.username");
		String password = env.getRequiredProperty("example.wsvc.password");
		Credentials credentials = new UsernamePasswordCredentials(username, password);
		sender.setCredentials(credentials);

		sender.setSslSocketFactory(jksSslSocketFactory());
		return sender;
	}

	@Bean
	public JksSslSocketFactory jksSslSocketFactory() {
		String path = env.getRequiredProperty("example.wsvc.jks") + ".jks";
		URL keystoreUrl = getClass().getClassLoader().getResource(path);
		JksSslSocketFactory factory = new JksSslSocketFactory(keystoreUrl, "kokosak", "kokosak");
		return factory;
	}
}
