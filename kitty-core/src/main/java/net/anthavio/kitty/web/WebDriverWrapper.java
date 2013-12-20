package net.anthavio.kitty.web;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.testng.Assert;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.WebClient;

/**
 * 
 * @author vanek
 * 
 * Pokus o obdobu {@link HtmlUnitWrapper} s WebDriverem. Uvidime...
 */
public class WebDriverWrapper {

	private final WebDriver driver;

	private String urlBase;

	public WebDriverWrapper() {
		driver = new HtmlUnitDriverX(BrowserVersion.INTERNET_EXPLORER_7);
	}

	public WebDriverWrapper(WebDriver driver, String urlBase) {
		this.driver = driver;
		this.urlBase = urlBase;
	}

	public WebDriverWrapper(WebDriver driver) {
		this.driver = driver;
	}

	public WebDriverWrapper(String urlBase) {
		this();
		this.urlBase = urlBase;
	}

	public void destroy() {
		if (driver != null) {
			driver.quit();
		}
	}

	protected WebElement findByName(String name) {
		WebElement element = driver.findElement(By.name(name));
		Assert.assertNotNull(element);
		return element;
	}

	protected WebElement findById(String id) {
		WebElement element = driver.findElement(By.id(id));
		Assert.assertNotNull(element);
		return element;
	}

	public void setNTLMCredentials(String username, String password, String host, int port, String clientHost,
			String clientDomain) {
		if(driver instanceof HtmlUnitDriverX) {
			DefaultCredentialsProvider creds = new DefaultCredentialsProvider();
			creds.addNTLMCredentials(username, password, host, port, clientHost, clientDomain);
			((HtmlUnitDriverX)driver).getWebClient().setCredentialsProvider(creds);
		} else {
			//log.warn("Cannot set NTLMCredentials to driver "+driver.getClass().getName());
		}
	}

	/**
	 * @author vanek
	 * 
	 * Only purpose id to make internal WebClient accesible
	 */
	static class HtmlUnitDriverX extends HtmlUnitDriver {

		public HtmlUnitDriverX(BrowserVersion version) {
			super(version);
		}

		@Override
		public WebClient getWebClient() {
			return super.getWebClient();
		}
	}
}
