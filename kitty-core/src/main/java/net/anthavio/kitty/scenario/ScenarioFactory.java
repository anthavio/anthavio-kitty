package net.anthavio.kitty.scenario;

import net.anthavio.spring.ContextHelper;
import net.anthavio.util.ReflectUtil;
import net.anthavio.xml.jaxb.SimpleJaxbBinder;

import org.springframework.context.ApplicationContext;

/**
 * 
 * @author vanek
 * 
 * @deprecated Kitty is now spring configured
 */
@Deprecated
public class ScenarioFactory<T extends Scenario> {

	public static final String KITTY_CTX_NAME = "kitty-lib";

	private String kittyContextName = KITTY_CTX_NAME;

	private boolean validatingBinder = false;

	public String getKittyContextName() {
		return kittyContextName;
	}

	public void setKittyContextName(String contextName) {
		this.kittyContextName = contextName;
	}

	public ApplicationContext getKittyContext() {
		return ContextHelper.i.locateContext(getKittyContextName());
	}

	public void releaseKittyContext() {
		ContextHelper.i.releaseContext(getKittyContextName());
	}

	public boolean isValidatingBinder() {
		return validatingBinder;
	}

	public void setValidatingBinder(boolean validatingBinder) {
		this.validatingBinder = validatingBinder;
	}

	public SimpleJaxbBinder<T> getScenarioBinder() {
		return new SimpleJaxbBinder<T>(getScenarioClass(), validatingBinder);
	}

	@SuppressWarnings("unchecked")
	public Class<T> getScenarioClass() {
		return (Class<T>) ReflectUtil.getTypeArguments(ScenarioFactory.class, this.getClass()).get(0);
	}

}
