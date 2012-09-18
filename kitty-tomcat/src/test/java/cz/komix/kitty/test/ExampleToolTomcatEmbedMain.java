package com.anthavio.kitty.test;

import com.anthavio.kitty.Kitty;
import com.anthavio.kitty.server.ServerWrapper;
import com.anthavio.tomcat.TomcatEmbedWrapper;

/**
 * @author vanek
 *
 */
public class ExampleToolTomcatEmbedMain {

	public static void main(String[] args) {
		getServer().start();
		Kitty kitty = Kitty.setup(args);
		kitty.startConsole();
	}

	private static ServerWrapper getServer() {
		String catalinaBase = System.getProperty("user.dir") + "/target/tomcat";
		String webAppBase = System.getProperty("user.dir");
		TomcatEmbedWrapper server = new TomcatEmbedWrapper(5959, catalinaBase, webAppBase);
		server.addWebApp("/example", "./src/test/webapp");
		return server;
	}
}
