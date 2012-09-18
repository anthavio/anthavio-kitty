package com.anthavio.kitty.step;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlTransient;

import com.anthavio.kitty.scenario.Step;
import com.anthavio.kitty.web.HtmlUnitWrapper;
import com.anthavio.kitty.web.WebDriverWrapper;

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
