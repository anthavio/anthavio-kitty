package com.anthavio.jetty;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import junit.framework.Assert;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * 
 * @author martin.vanek
 *
 */
public class JettyWrapperTest {

	private URL url;

	private JettyWrapper jetty;

	@BeforeClass
	public void beforeClass() throws Exception {
		int port = 13131;
		url = new URL("http://localhost:" + port + "/halleluyah.html");

		jetty = new JettyWrapper("src/test/jetty8", port);
		jetty.start();
	}

	@AfterClass
	public void afterClass() throws Exception {
		if (jetty != null && jetty.isStarted()) {
			jetty.stop();
		}
	}

	@Test
	public void test() throws Exception {
		InputStream stream = (InputStream) url.getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String line = reader.readLine();
		Assert.assertEquals("Halleluyah!", line);
	}
}
