package net.anthavio.kitty.step;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlTransient;

import net.anthavio.kitty.scenario.Step;
import net.anthavio.kitty.web.HtmlUnitWrapper;
import net.anthavio.kitty.web.WebDriverWrapper;


/**
 * @author vanek
 *
 */
public abstract class WebStepBase extends Step {

	@Inject
	@XmlTransient
	private HtmlUnitWrapper client;

	@Inject
	@XmlTransient
	private WebDriverWrapper driver;

	public HtmlUnitWrapper getClient() {
		return client;
	}

	public WebDriverWrapper getDriver() {
		return driver;
	}
}
