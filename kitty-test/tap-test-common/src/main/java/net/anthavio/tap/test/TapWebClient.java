package net.anthavio.tap.test;

import java.io.IOException;

import net.anthavio.kitty.web.HtmlUnitWrapper;

import org.apache.commons.lang.time.FastDateFormat;

import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


/**
 * @author vanek
 *
 */
public class TapWebClient extends HtmlUnitWrapper {

	//neni thread safe, ale to nam tady nevadi. jen formatujeme
	public static final FastDateFormat DF_DATE = FastDateFormat.getInstance("dd.MM.yyyy");

	public TapWebClient(String urlBase) {
		super(urlBase);
	}

	@Override
	public void clearCredentials() {
		webClient.setCredentialsProvider(new DefaultCredentialsProvider());
	}

	@Override
	public void setNTLMCredentials(final String username, final String password, final String host, final int port,
			final String clientHost, final String clientDomain) {
		DefaultCredentialsProvider provider = new DefaultCredentialsProvider();
		provider.addNTLMCredentials(username, password, host, port, clientHost, clientDomain);
		webClient.setCredentialsProvider(provider);
	}

	@Override
	public HtmlPage setWorkingPage(String urlOffset) throws IOException {
		HtmlPage page = super.setWorkingPage(urlOffset);
		assertNotNkcErrors();
		return page;
	}

	@Override
	public HtmlPage submit(String name) throws IOException {
		HtmlPage page = super.submit(name);
		assertNotNkcErrors();
		return page;
	}

	@Override
	public void clickLinkText(String value) throws IOException {
		super.clickLinkText(value);
		assertNotNkcErrors();
	}

	@Override
	public void clickLinkId(String id) throws IOException {
		super.clickLinkId(id);
		assertNotNkcErrors();
	}

	public void assertNotNkcErrors() {
		assertElementNotPresent("error"); // chyba validace
		assertElementNotPresent("error_detail"); // chyba 500
		assertTextNotPresent("???"); //chybi lokalizace
	}

	public void login(String username, String password) throws Exception {
		logout();

		setWorkingPage("/account/login.htm");
		setWorkingForm("LoginForm");
		setTextField("j_username", username);
		setPasswordField("j_password", password);

		submit("submit");
		//log.debug(page.asXml());

		//assertElementPresent("appInfo");
	}

	public void logout() throws Exception {
		setWorkingPage("/account/logout");
	}
}
