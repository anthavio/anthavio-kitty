/**
 * 
 */
package net.anthavio.kitty;

import net.anthavio.kitty.scenario.Scenario;
import net.anthavio.xml.jaxb.SimpleJaxbBinder;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;


/**
 * @author vanek
 *
 * I don't want public setters on Kitty so this FactoryBean will do the job in package visibility...
 */
public class KittyFactoryBean implements FactoryBean<Kitty>, InitializingBean {

	private KittyOptions options;

	private SimpleJaxbBinder<Scenario> scenarioBinder;

	private Kitty kitty;

	@Override
	public void afterPropertiesSet() throws Exception {
		kitty = new Kitty(options, scenarioBinder);
	}

	@Override
	public Kitty getObject() throws Exception {
		return kitty;
	}

	@Override
	public Class<?> getObjectType() {
		return Kitty.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public void setOptions(KittyOptions options) {
		this.options = options;
	}

	public void setScenarioBinder(SimpleJaxbBinder<Scenario> scenarioBinder) {
		this.scenarioBinder = scenarioBinder;
	}

}
