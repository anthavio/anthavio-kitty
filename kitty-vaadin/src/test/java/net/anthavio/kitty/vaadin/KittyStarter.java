/**
 * 
 */
package net.anthavio.kitty.vaadin;

import java.io.File;

import net.anthavio.kitty.Kitty;
import net.anthavio.kitty.jetty.JettyWrapper;
import net.anthavio.kitty.server.ServerWrapper;


/**
 * @author vanek
 *
 */
public class KittyStarter {

	public static void main(String[] args) {
		try {
			getServer().start();
			Kitty kitty = Kitty.setup(args);
			kitty.startConsole();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static ServerWrapper getServer() {
		String jettyHome;
		if (new File("etc/webdefault.xml").exists()) {
			jettyHome = "."; //Standalone Kitty
		} else {
			jettyHome = "./src/main/jetty"; //Eclipse
		}
		return new JettyWrapper(jettyHome);
	}

}
