package com.anthavio.kitty.step;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author vanek
 *
 */
public class NtlmCredentialsStep extends WebStepBase {

	@XmlElement(required = true)
	protected String username;

	@XmlElement(required = true)
	protected String password;

	@XmlElement
	protected String clientHost;

	@XmlElement
	protected String clientDomain;

	@XmlElement
	protected String host;

	@XmlElement
	protected Integer port;

	protected NtlmCredentialsStep() {
		//jaxb
	}

	public NtlmCredentialsStep(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public NtlmCredentialsStep(String username, String password, String clientHost, String clientDomain) {
		this.username = username;
		this.password = password;
		this.clientHost = clientHost;
		this.clientDomain = clientDomain;
	}

	public NtlmCredentialsStep(String username, String password, String clientHost, String clientDomain, String host,
			int port) {
		this.username = username;
		this.password = password;
		this.clientHost = clientHost;
		this.clientDomain = clientDomain;
		this.host = host;
		this.port = port;
	}

	@Override
	public void execute() throws Exception {
		int port = this.port != null ? this.port : -1;
		String clientHost = this.clientHost != null ? this.clientHost : "";
		String clientDomain = this.clientDomain != null ? this.clientDomain : "";

		getClient().setNTLMCredentials(username, password, host, port, clientHost, clientDomain);
		getDriver().setNTLMCredentials(username, password, host, port, clientHost, clientDomain);
	}
}
