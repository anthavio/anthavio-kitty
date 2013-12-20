package net.anthavio.kitty.test;

import net.anthavio.kitty.Kitty;
import net.anthavio.tomcat.TomcatWrapper;


/**
 * @author vanek
 *
 */
public class ExampleToolTomcatMain {

	public static void main(String[] args) {

		TomcatWrapper server = new TomcatWrapper(System.getProperty("user.dir") + "/src/test/tomcat");
		server.start();

		Kitty kitty = Kitty.setup(args);
		kitty.startConsole();
	}

}
