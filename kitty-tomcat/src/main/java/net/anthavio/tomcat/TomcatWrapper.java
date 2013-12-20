package net.anthavio.tomcat;

import java.util.HashMap;
import java.util.Map;

import net.anthavio.kitty.server.ServerWrapper;

import org.apache.catalina.Container;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.core.StandardService;
import org.apache.catalina.startup.Catalina;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


/**
 * 
 * @author vanek
 * 
 * Tomcat 7 embedder
 */
public class TomcatWrapper implements ServerWrapper {

	private final Catalina catalina;

	public TomcatWrapper() {
		this(System.getProperty("catalina.base", System.getProperty("user.dir")));
	}

	public TomcatWrapper(String catalinaBase) {
		System.setProperty("catalina.home", catalinaBase);
		System.setProperty("catalina.base", catalinaBase);
		catalina = new Catalina();
		catalina.load();
	}

	public boolean isStarted() {
		return catalina != null && catalina.getServer().getState().isAvailable();
	}

	@Override
	public void start() {
		//catalina.setAwait(true);
		catalina.start();
	}

	@Override
	public void stop() {
		catalina.stop();
	}

	@Override
	public Map<String, ApplicationContext> getSpringContexts() {
		StandardServer server = (StandardServer) catalina.getServer();
		StandardService service = (StandardService) server.findService("Catalina");
		Container engine = service.getContainer();
		Container host = engine.findChild("localhost");
		Container[] children = host.findChildren();

		Map<String, ApplicationContext> springContexts = new HashMap<String, ApplicationContext>();
		for (Container container : children) {
			if (container instanceof StandardContext) {
				StandardContext webAppCtx = (StandardContext) container;
				WebApplicationContext springCtx = WebApplicationContextUtils.getWebApplicationContext(webAppCtx
						.getServletContext());
				springContexts.put(webAppCtx.getDisplayName(), springCtx);
			}
		}
		return springContexts;
	}

}
