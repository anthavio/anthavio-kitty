package net.anthavio.tap.test;

import net.anthavio.kitty.web.WebDriverWrapper;

import org.openqa.selenium.WebDriver;


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
