package net.anthavio.kitty.server;

import java.util.Map;

import org.springframework.context.ApplicationContext;

/**
 * 
 * @author vanek
 *
 */
public interface ServerWrapper {

	public void start();

	public void stop();

	public boolean isStarted();

	public Map<String, ApplicationContext> getSpringContexts();

}
