package net.anthavio.kitty.jetty;

import java.util.HashMap;
import java.util.Map;

import net.anthavio.kitty.server.ServerWrapper;

import org.mortbay.jetty.Handler;
import org.mortbay.jetty.handler.HandlerCollection;
import org.mortbay.jetty.webapp.WebAppContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


/**
 * Works with Jetty version 6
 * 
 * @author martin.vanek
 *
 */
public class Jetty6Wrapper extends net.anthavio.jetty.Jetty6Wrapper implements ServerWrapper {

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