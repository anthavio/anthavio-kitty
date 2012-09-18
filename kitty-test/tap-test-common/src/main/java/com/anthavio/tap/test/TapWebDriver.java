package com.anthavio.tap.test;

import org.openqa.selenium.WebDriver;

import com.anthavio.kitty.web.WebDriverWrapper;

/**
 * @author vanek
 *
 */
public class TapWebDriver extends WebDriverWrapper {

	public TapWebDriver() {
		super();
	}

	public TapWebDriver(WebDriver driver, String urlBase) {
		super(driver, urlBase);
	}

	public TapWebDriver(WebDriver driver) {
		super(driver);
	}

	public TapWebDriver(String urlBase) {
		super(urlBase);
	}

}
