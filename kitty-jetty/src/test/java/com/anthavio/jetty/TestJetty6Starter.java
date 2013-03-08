package com.anthavio.jetty;

public class TestJetty6Starter {

	public static void main(String[] args) {
		System.setProperty("jetty.port", "13131");
		Jetty6Wrapper wrapper = new Jetty6Wrapper("src/test/jetty6");
		wrapper.start();
		//wrapper.stop();
	}
}
