/**
 * 
 */
package net.anthavio.tap.svc;

import java.util.Map;

import javax.inject.Inject;

import net.anthavio.spring.JavaConfigurationSupport;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author vanek
 *
 */
//@Configuration
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
@ComponentScan(basePackageClasses = SpringConfig.class)
@Import(SpringSecurityConfig.class)
@PropertySource("file:${tap.ext.dir}/tap-boot.properties")
public class SpringConfig extends JavaConfigurationSupport {

	@Inject
	private Map<String, Object> jaxbProperties;

	@Bean(name = "TapMessagesBinder")
	public Jaxb2Marshaller messagesJaxb2Marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setSchema(new ClassPathResource("schema/PingMessages.xsd"));
		marshaller.setContextPath("net.anthavio.example.messages");
		marshaller.setMarshallerProperties(jaxbProperties);
		marshaller.setMtomEnabled(true);
		return marshaller;
	}

	//ReloadableResourceBundleMessageSource v @Configuration nejak zpusobuje cyklicke zavislosti. V xml ne
	//@Bean(name = "messageSource")
	public ReloadableResourceBundleMessageSource messageSource2() {
		ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
		source.setBasename("classpath:example-messages");
		source.setDefaultEncoding("utf-8");
		source.setFallbackToSystemLocale(false);
		source.setCacheSeconds(10);
		return source;
	}

}
