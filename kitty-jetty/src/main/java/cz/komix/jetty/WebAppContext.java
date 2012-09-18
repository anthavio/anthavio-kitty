/**
 * 
 */
package com.anthavio.jetty;

import org.eclipse.jetty.server.handler.ContextHandlerCollection;

/**
 * @author vanek
 *
 */
public class WebAppContext extends org.eclipse.jetty.webapp.WebAppContext {

	public WebAppContext(ContextHandlerCollection parent) {
		//Crucial part! Without this, we will not be started/stopped
		parent.addHandler(this);
	}

}
