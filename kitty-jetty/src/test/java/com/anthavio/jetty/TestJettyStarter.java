package com.anthavio.jetty;

public class TestJettyStarter {

	public static void main(String[] args) {
		System.setProperty("jetty.port", "13131");
		JettyWrapper wrapper = new JettyWrapper("src/test/jetty8", "jetty.xml", "jetty-deploy.xml", "jetty-contexts.xml");
		wrapper.start();
		//wrapper.stop();
	}
}
