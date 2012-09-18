package com.anthavio.kitty.web;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.impl.client.AbstractHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.HttpWebConnection;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlFileInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

import com.anthavio.spring.ntlm.NTLMSchemeFactory;

/**
 * @author vanek
 *
 * Wraper nad HtmlUnit WebClientem. Zjednodusuje vyplnovani inputu.
 */
public class HtmlUnitWrapper {

	public static final String EMPTY = "";

	public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

	protected final Logger log = LoggerFactory.getLogger(getClass());

	private String urlBase;

	protected WebClient webClient;

	protected HtmlPage page;

	protected HtmlForm form;

	/**
	 * Inicializace z uz existujiciho WebClienta
	 */
	public HtmlUnitWrapper(WebClient webClient, String urlBase) {
		this.webClient = webClient;
		this.urlBase = urlBase;
	}

	public HtmlUnitWrapper(String urlBase) {
		this();
		this.urlBase = urlBase;
	}

	public HtmlUnitWrapper() {
		webClient = new WebClient(BrowserVersion.INTERNET_EXPLORER_7);
		webClient.setJavaScriptEnabled(true);
		webClient.setThrowExceptionOnScriptError(false);
		webClient.setRedirectEnabled(true);
		webClient.setCssEnabled(true);
		webClient.setPrintContentOnFailingStatusCode(true);
		webClient.setThrowExceptionOnFailingStatusCode(true);
		try {
			webClient.setUseInsecureSSL(true);
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
	}

	public WebClient getWebClient() {
		return webClient;
	}

	public void setWebClient(WebClient webClient) {
		this.webClient = webClient;
	}

	public String getUrlBase() {
		return urlBase;
	}

	public void setUrlBase(String urlBase) {
		this.urlBase = urlBase;
	}

	public void clearCredentials() {
		webClient.setCredentialsProvider(new DefaultCredentialsProvider());
	}

	public void setNTLMCredentials(final String username, final String password, final String host, final int port,
			final String clientHost, final String clientDomain) {

		//htmlunit 2.8 is using httpclient 4.x missing ntlm support by default
		webClient.setWebConnection(new HttpWebConnection(webClient) {

			@Override
			protected synchronized AbstractHttpClient getHttpClient() {
				AbstractHttpClient httpClient = super.getHttpClient();
				httpClient.getAuthSchemes().register("ntlm", new NTLMSchemeFactory());
				return httpClient;
			}
		});

		//httpclient 3.x and 4.x
		DefaultCredentialsProvider provider = new DefaultCredentialsProvider();
		provider.addNTLMCredentials(username, password, host, port, clientHost, clientDomain);
		webClient.setCredentialsProvider(provider);
	}

	public HtmlPage setWorkingPage(String urlOffset) throws IOException {
		log.debug("setWorkingPage " + urlOffset);
		String url = buildUrl(urlOffset);
		page = (HtmlPage) webClient.getPage(url);
		form = null;
		return page;
	}

	public HtmlPage gotoPage(String urlOffset) throws IOException {
		return setWorkingPage(urlOffset);
	}

	private String buildUrl(String urlOffset) {
		String url;
		if (urlBase.endsWith("/") == false) {
			if (urlOffset.startsWith("/") == false) {
				//zadne lomitko -> vmezerime tamjedno
				url = urlBase + "/" + urlOffset;
			} else {
				url = urlBase + urlOffset;
			}
		} else {
			if (urlOffset.startsWith("/") == false) {
				url = urlBase + urlOffset;
			} else {
				//dvoje lomitka -> jedno ubereme
				url = urlBase + urlOffset.substring(1);
			}
		}
		return url;
	}

	public HtmlForm setWorkingForm(String nameOrId) {
		log.debug("setWorkingForm " + nameOrId);
		HtmlForm form = null;
		List<HtmlElement> elements = page.getElementsByIdAndOrName(nameOrId);
		for (HtmlElement htmlElement : elements) {
			if (htmlElement instanceof HtmlForm) {
				form = (HtmlForm) htmlElement;
				break;
			}
		}

		if (form == null) {
			throw new AssertionError("Form element not found by name or id: " + nameOrId);
		}
		this.form = form;
		return form;
	}

	public HtmlPage submit(String name) throws IOException {
		log.debug("submit " + name);
		checkForm();
		HtmlSubmitInput submit = form.getInputByName(name);
		page = (HtmlPage) submit.click();
		form = null;
		return page;
	}

	private void checkForm() {
		if (form == null) {
			throw new IllegalStateException("Working form is not set");
		}
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getElementsByNameOrId(String nameOrId, Class<T> expectedType) {
		List<T> results = new ArrayList<T>();
		List<HtmlElement> elements = page.getElementsByIdAndOrName(nameOrId);

		for (HtmlElement htmlElement : elements) {
			if (htmlElement.getClass().equals(expectedType)) {
				results.add((T) htmlElement);
			}
		}
		return results;
	}

	public void clickLinkId(String id) throws IOException {
		log.debug("clickLinkId " + id);
		HtmlAnchor link = (HtmlAnchor) page.getHtmlElementById(id);
		click(link);
	}

	public void clickLinkText(String value) throws IOException {
		List<HtmlAnchor> anchors = page.getAnchors();
		for (HtmlAnchor anchor : anchors) {
			if (anchor.getTextContent().trim().equals(value)) {
				click(anchor);
				return;
			}
		}
		throw new ElementNotFoundException("*", "*", value);

	}

	public void click(HtmlElement element) throws IOException {
		log.debug("click on " + element.asText());
		Page clickedPage = element.click();
		//link muze byt jen javascriptova funkce
		//prelezli jsme na nove url?
		if (clickedPage instanceof HtmlPage) {
			HtmlPage newPage = (HtmlPage) clickedPage;
			//System.out.println(newPage.asXml());
			URL newUrl = newPage.getWebResponse().getWebRequest().getUrl();
			URL oldUrl = page.getWebResponse().getWebRequest().getUrl();
			boolean isDiffUrl = !newUrl.equals(oldUrl);
			boolean isNewHttp = newUrl.getProtocol().startsWith("http");
			if (isNewHttp && isDiffUrl) {
				log.debug("new url " + newUrl.getPath());
				this.page = newPage;
				this.form = null;
			}
		}
	}

	public void sleep(int millis) {
		log.debug("sleep " + millis);
		try {
			Thread.sleep(millis);
		} catch (InterruptedException ix) {
			log.warn("Sleep interrupted");
		}
	}

	public String getFormInputValue(String name) {
		HtmlInput input = form.getInputByName(name);
		String valueAttribute = input.getValueAttribute();
		return valueAttribute.trim();
	}

	public HtmlTextInput assertTexFieldValue(String id, String value) {
		HtmlTextInput textInput = (HtmlTextInput) page.getHtmlElementById(id);
		Assert.assertEquals(textInput.getValueAttribute().trim(), value);
		return textInput;
	}

	public void assertTextPresent(String text) {
		if (!page.asText().contains(text)) {
			throw new AssertionError("Text not present '" + text + "'");
		}
	}

	public void assertTextNotPresent(String text) {
		String pageText = page.asText();
		int index = page.asText().indexOf(text);

		if (index != -1) {
			int startIdx = index - 50 < 0 ? 0 : index - 50;
			int endIdx = index + 50 > pageText.length() ? pageText.length() : index + 50;
			throw new AssertionError("Text '" + text + "' present on index " + index + "\n" + "..."
					+ pageText.substring(startIdx, endIdx) + "...");
		}
	}

	public String getInputValue(String nameOrId) {
		HtmlInput element = (HtmlInput) assertElementPresent(nameOrId);
		return element.getValueAttribute();
	}

	public void assertInputValue(String nameOrId, String value) {
		HtmlInput element = (HtmlInput) assertElementPresent(nameOrId);
		Assert.assertEquals(element.getValueAttribute().trim(), value, "Element " + element.asXml());
	}

	public void assertElementValue(String nameOrId, String value) {
		HtmlElement element = assertElementPresent(nameOrId);
		Assert.assertEquals(element.getTextContent().trim(), value, "Element " + element.asXml());
	}

	public HtmlElement assertElementPresent(String nameOrId) {
		List<HtmlElement> elements = page.getElementsByIdAndOrName(nameOrId);
		if (elements.size() == 0) {
			throw new AssertionError("Page does not contain element with name or id: " + nameOrId);
		}
		return elements.get(0);
	}

	public void assertElementNotPresent(String nameOrId) {
		List<HtmlElement> elements = page.getElementsByIdAndOrName(nameOrId);
		if (elements.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (HtmlElement htmlElement : elements) {
				sb.append('\n');
				sb.append(htmlElement.asXml());
			}
			throw new AssertionError("Page contains unwanted element(s) with name or id: " + nameOrId + sb.toString());
		}
	}

	public String getByXPath(String xpath) {
		List<?> list = page.getByXPath(xpath);
		if (list.isEmpty()) {
			throw new AssertionError("Nothing found with xpath " + xpath);
		} else {
			return ((DomText) list.get(0)).asText();
		}
	}

	public HtmlCheckBoxInput checkCheckbox(String name) {
		log.debug("checkCheckbox " + name);
		checkForm();
		HtmlCheckBoxInput checkBox = (HtmlCheckBoxInput) form.getInputByName(name);
		checkBox.setChecked(true);
		return checkBox;
	}

	public HtmlOption selectOptionText(String name, String value) throws IOException {
		log.debug("selectOptionText " + name + " " + value);
		checkForm();
		HtmlSelect select = form.getSelectByName(name);
		List<HtmlOption> options = select.getOptions();

		for (HtmlOption option : options) {
			if (option.getText().trim().equals(value)) {
				select.setSelectedAttribute(option, true);
				return option;
			}
		}
		throw new AssertionError("Option of select with name " + name + " and text " + value + " not found");
	}

	public HtmlOption selectOption(String name, String value) throws IOException {
		log.debug("selectOption " + name + " " + value);
		checkForm();
		HtmlSelect select = form.getSelectByName(name);

		// zrusit defaultni vyber
		for (HtmlOption option : select.getOptions()) {
			select.setSelectedAttribute(option, false);
		}

		// vybrat polozku dle value
		HtmlOption option = select.getOptionByValue(value);
		select.setSelectedAttribute(option, true);

		// simulace kliknuti a prechod na jinou stranku
		// tohle je nezbytne, v pripade, ze je na select navazan js (onchange)
		click(select);
		return option;
	}

	public HtmlOption selectOption(String name, Object value) throws IOException {
		if (value != null) {
			if (value instanceof String) {
				return selectOption(name, (String) value);
			} else {
				return selectOption(name, String.valueOf(value));
			}
		} else {
			return selectOption(name, EMPTY);
		}
	}

	/**
	 * @param Atribut name elementu select
	 * @return vsechny options selected i neselected
	 */
	public List<HtmlOption> getSelectOptions(String name) {
		checkForm();
		HtmlSelect select = form.getSelectByName(name);
		return select.getOptions();
	}

	/**
	 * @param Atribut name elementu select
	 * @return pouze selected options
	 */
	public List<HtmlOption> getSelectedOptions(String name) {
		checkForm();
		HtmlSelect select = form.getSelectByName(name);
		return select.getSelectedOptions();
	}

	public String getSelectedOptionValue(String name) {
		List<HtmlOption> selectedOptions = getSelectedOptions(name);
		if (selectedOptions.size() == 0) {
			return null;
		} else if (selectedOptions.size() > 1) {
			throw new AssertionError("Multiple options selected");
		} else {
			return selectedOptions.get(0).getNodeValue();
		}
	}

	public HtmlPasswordInput setPasswordField(String name, String value) {
		log.debug("setPasswordField " + name + " " + value);
		checkForm();
		HtmlPasswordInput passwordInput = (HtmlPasswordInput) form.getInputByName(name);
		passwordInput.setValueAttribute(value);
		return passwordInput;
	}

	public void waitForTextPresent(String text, int timeout) {
		log.debug("waitForTextPresent " + text + " " + timeout);
		long startTime = System.currentTimeMillis();

		while (!page.asXml().contains(text)) {
			if (startTime + timeout < System.currentTimeMillis()) {
				throw new AssertionError("Expected text (" + text + ") not found on page until timeout " + timeout);
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException ix) {
				throw new AssertionError("Got interrupted while waiting for text on page");
			}
		}
	}

	public void waitForValuePresent(String name, int timeout) {
		log.debug("waitForValuePresent " + name + " " + timeout);
		checkForm();
		HtmlInput input = form.getInputByName(name);
		String value = input.getValueAttribute();
		long startTime = System.currentTimeMillis();
		while (value == null || EMPTY.equals(value)) {
			//log.info("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
			if (startTime + timeout < System.currentTimeMillis()) {
				throw new AssertionError("Input " + name + " did not became enabled until timeout " + timeout);
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException ix) {
				throw new AssertionError("Got interrupted while waiting for Input " + name);
			}
		}
	}

	public void waitForEnabled(String name, int timeout) {
		log.debug("waitForEnabled " + name + " " + timeout);
		checkForm();
		HtmlInput input = form.getInputByName(name);
		long startTime = System.currentTimeMillis();
		while (input.isDisabled() || input.isReadOnly()) {
			//log.info("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
			if (startTime + timeout < System.currentTimeMillis()) {
				throw new AssertionError("Input " + name + " did not became enabled until timeout " + timeout);
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException ix) {
				throw new AssertionError("Got interrupted while waiting for Input " + name);
			}
		}
	}

	public HtmlTextInput setTextField(String name, Double value) {
		if (value != null) {
			return setTextField(name, value.toString().replace('.', ','));
		} else {
			return setTextField(name, EMPTY);
		}
	}

	public HtmlTextArea setTextArea(String name, String value) {
		log.debug("setTextArea " + name + " " + value);
		checkForm();
		HtmlTextArea textArea = form.getTextAreaByName(name);
		if (value != null) {
			textArea.setText(value);
		} else {
			textArea.setText(EMPTY);
		}
		return textArea;
	}

	public HtmlTextInput setTextField(String name, String value) {
		log.debug("setTextField " + name + " " + value);
		checkForm();
		HtmlTextInput textInput = (HtmlTextInput) form.getInputByName(name);
		if (value != null) {
			textInput.setValueAttribute(value);
		} else {
			textInput.setValueAttribute(EMPTY);
		}
		return textInput;
	}

	public HtmlTextInput setTextField(String name, Object value) {
		if (value != null) {
			if (value instanceof String) {
				return setTextField(name, (String) value);
			} else {
				return setTextField(name, String.valueOf(value));
			}
		} else {
			return setTextField(name, EMPTY);
		}
	}

	/*
		public HtmlRadioButtonInput clickRadioButton(String name) throws IOException {
			HtmlRadioButtonInput radio = (HtmlRadioButtonInput) form.getInputByName(name);
			radio.click();
			return radio;
		}
	 */
	public HtmlRadioButtonInput clickRadioButton(String name, Integer value) throws IOException {
		return clickRadioButton(name, String.valueOf(value));
	}

	public HtmlRadioButtonInput clickRadioButton(String name, Boolean value) throws IOException {
		return clickRadioButton(name, String.valueOf(value));
	}

	public HtmlRadioButtonInput clickRadioButton(String name, String value) throws IOException {
		log.debug("clickRadioButton " + name + " " + value);
		checkForm();
		List<HtmlRadioButtonInput> radioButtons = form.getRadioButtonsByName(name);
		for (HtmlRadioButtonInput radio : radioButtons) {
			if (radio.getValueAttribute().equals(value)) {
				radio.click();
				return radio;
			}
		}
		throw new AssertionError("Radio with name " + name + " and value " + value + " not found");
	}

	public HtmlPage getPage() {
		return page;
	}

	public HtmlFileInput setFileField(String name, byte[] data) {
		log.debug("setFileField " + name + " passing in byte[] directly");
		checkForm();
		HtmlFileInput fileInput = (HtmlFileInput) form.getInputByName(name);
		if (data != null) {
			fileInput.setValueAttribute("mem.txt");
			fileInput.setContentType("binary");
			fileInput.setData(data);
		} else {
			fileInput.setValueAttribute("mem.txt");
			fileInput.setContentType("binary");
			fileInput.setData(EMPTY_BYTE_ARRAY);
		}
		return fileInput;
	}

	public HtmlFileInput setFileField(String name, String fileName) {
		log.debug("setFileField " + name + " file " + fileName);
		checkForm();
		HtmlFileInput fileInput = (HtmlFileInput) form.getInputByName(name);
		if (fileName != null) {
			fileInput.setValueAttribute(fileName);
		} else {
			fileInput.setValueAttribute("mem.txt");
			fileInput.setContentType("binary");
			fileInput.setData(EMPTY_BYTE_ARRAY);
		}
		return fileInput;
	}
}