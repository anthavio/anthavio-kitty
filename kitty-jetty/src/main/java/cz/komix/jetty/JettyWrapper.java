package com.anthavio.jetty;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.xml.XmlConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.anthavio.kitty.server.ServerWrapper;

/**
 * @author vanek
 * 
 * Startovani jetty serveru pro testovaci ucely. Lze i v debug modu. Rychle a efektivni.
 * 
 * Pro spusteni je nutne zmenit ${user.dir} na ${project_loc}/src/jetty
 * Konkretne:
 * Eclipse>Run>Run Configurations...>ServerMain>Arguments>Working directory>Other>${project_loc}/src/jetty
 * 
 * Druha moznost je pridat JVM argument -Djetty.home=src/jetty
 * Konkretne:
 * Eclipse>Run>Run Configurations...>ServerMain>Arguments>VM Agruments>-Djetty.home=src/jetty
 * 
 */
public class JettyWrapper implements ServerWrapper {

	private final Server server;

	public JettyWrapper() {
		this(System.getProperty("jetty.home", System.getProperty("user.dir")));
	}

	public JettyWrapper(List<String> jettyXmls) {
		this(System.getProperty("jetty.home", System.getProperty("user.dir")), jettyXmls.toArray(new String[jettyXmls
				.size()]));
	}

	public JettyWrapper(String homeDir, List<String> jettyXmls) {
		this(homeDir, jettyXmls.toArray(new String[jettyXmls.size()]));
	}

	public JettyWrapper(String homeDir, String... jettyXmls) {
		File fHomeDir = new File(homeDir);
		File fEtcDir = new File(fHomeDir, "etc");
		if (!fEtcDir.exists()) {
			throw new RuntimeException("Jetty directory does not exist " + fEtcDir.getAbsolutePath());
		}

		System.setProperty("jetty.home", homeDir);

		server = new Server();
		Map<String, Object> id_map = new HashMap<String, Object>();
		Map<String, String> properties = new HashMap<String, String>();
		id_map.put("Server", server);

		for (String jettyXml : jettyXmls) {
			File fJettyXml = new File(fEtcDir, jettyXml);
			if (fJettyXml.exists() == false) {
				throw new RuntimeException("Jetty config does not exist " + fJettyXml.getAbsolutePath());
			}
			try {
				XmlConfiguration config = new XmlConfiguration(fJettyXml.toURI().toURL());
				config.getIdMap().putAll(id_map);
				config.getProperties().putAll(properties);
				config.configure(server);
				id_map = config.getIdMap();
			} catch (Exception x) {
				if (x instanceof RuntimeException) {
					throw (RuntimeException) x;
				}
				throw new RuntimeException("Jetty failed to configure", x);
			}
		}
	}

	public JettyWrapper(String homeDir) {
		this(homeDir, "jetty.xml");
	}

	public boolean isStarted() {
		return server != null && server.isRunning();
	}

	public void start() {
		if (!server.isRunning()) {
			try {
				server.start();
			} catch (Exception x) {
				if (x instanceof RuntimeException) {
					throw (RuntimeException) x;
				}
				throw new RuntimeException("Jetty failed to start", x);
			}
		} else {
			throw new IllegalStateException("Server is already running");
		}
	}

	public void stop() {
		if (server.isRunning()) {
			try {
				server.stop();
			} catch (Exception x) {
				if (x instanceof RuntimeException) {
					throw (RuntimeException) x;
				}
				throw new RuntimeException("Jetty failed to stop", x);
			}
		} else {
			throw new IllegalStateException("Server is not running");
		}
	}

	public Server getServer() {
		return server;
	}

	public Map<String, ApplicationContext> getSpringContexts() {
		HandlerCollection handlers = (HandlerCollection) server.getHandler();
		Handler[] webAppContexts = handlers.getChildHandlersByClass(WebAppContext.class);
		HashMap<String, ApplicationContext> springContexts = new HashMap<String, ApplicationContext>();
		for (Handler handler : webAppContexts) {
			WebAppContext webAppCtx = (WebAppContext) handler;
			WebApplicationContext springCtx = WebApplicationContextUtils.getWebApplicationContext(webAppCtx
					.getServletContext());
			springContexts.put(webAppCtx.getDisplayName(), springCtx);
		}
		return springContexts;
	}
}
