package net.anthavio.kitty.test;

import net.anthavio.kitty.Kitty;
import net.anthavio.kitty.server.ServerWrapper;
import net.anthavio.tomcat.TomcatEmbedWrapper;


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
