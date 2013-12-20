package net.anthavio.kitty.jetty;

import java.util.HashMap;
import java.util.Map;

import net.anthavio.kitty.server.ServerWrapper;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


/**
 * @author vanek
 * 
 * Works with Jetty version 7 and 8
 * 
 * Be aware that those versions do NOT use System properties (jetty.home,jetty.port,...) anymore.
 */
public class JettyWrapper extends net.anthavio.jetty.JettyWrapper implements ServerWrapper {

	public JettyWrapper() {

	}

	public JettyWrapper(String jettyHome) {
		super(jettyHome);
	}

	@Override
	public Map<String, ApplicationContext> getSpringContexts() {
		HandlerCollection handlers = (HandlerCollection) getServer().getHandler();
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