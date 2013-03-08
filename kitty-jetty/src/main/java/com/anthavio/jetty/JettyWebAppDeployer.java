/**
 * 
 */
package com.anthavio.jetty;

import org.eclipse.jetty.server.handler.ContextHandlerCollection;

/**
 * Deploys single web application. Jetty 7/8 version
 * 
 * For usage example see src/test/jetty8/etc/jetty.xml
 * 
 * @author vanek
 *
 */
public class JettyWebAppDeployer extends org.eclipse.jetty.webapp.WebAppContext {

	public JettyWebAppDeployer(ContextHandlerCollection parent) {
		//Crucial part! Without this, we will not be started/stopped
		parent.addHandler(this);
	}

}
