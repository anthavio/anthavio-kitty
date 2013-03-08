package com.anthavio.jetty.junit;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.UnhandledException;

import com.anthavio.jetty.Jetty6Wrapper;
import com.anthavio.jetty.spring.Jetty6Loader;
import com.anthavio.jetty.test.Jetty6Manager;
import com.anthavio.jetty.test.Jetty6Manager.JettySetupData;
import com.anthavio.jetty.test.JettyConfig;

/**
 * 
 * @author martin.vanek
 *
 */
//@RunWith(Jetty6ClassRunner.class)
public abstract class Jetty6JunitTest {

	private Jetty6Manager manager = Jetty6Manager.i();

	protected final Jetty6Wrapper jetty;

	protected final URL url;

	public Jetty6JunitTest() {

		String systemUrl = System.getProperty("gateway.url");//FIXME parametrize system property name
		try {
			if (systemUrl != null) {
				URL url = new URL(systemUrl);
				if (url.getHost().equals("localhost")) {
					//start jetty with URL port
					JettyConfig[] configs = Jetty6Loader.getJettyConfigs(getClass());
					JettyConfig config = configs[0];
					JettySetupData jsd = new JettySetupData(config.home(), url.getPort(), config.configs(), config.cache());
					jetty = manager.startJetty(jsd);
					if (url.getPort() == 0) {
						//jetty dynamic port
						int port = jetty.getPort();
						this.url = new URL(url.getProtocol(), url.getHost(), port, url.getFile());
					} else {
						this.url = url;
					}
				} else {
					//non localhost url -> remote test
					this.url = url;
					this.jetty = null;
				}
			} else {
				//no system property url -> start jetty
				JettyConfig[] configs = Jetty6Loader.getJettyConfigs(getClass());
				JettyConfig config = configs[0];
				JettySetupData jsd = new JettySetupData(config.home(), config.port(), config.configs(), config.cache());
				jetty = manager.startJetty(jsd);
				int port = jetty.getPort();
				this.url = new URL("http://localhost:" + port);
			}
		} catch (MalformedURLException mux) {
			throw new UnhandledException(mux);
		}
	}

	public URL getURL() {
		return url;
	}
}