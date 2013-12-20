package net.anthavio.tap.test;

import net.anthavio.kitty.Kitty;

/**
 * @author vanek
 *
 */
public class TapJettyMain {

	public static void main(String[] args) {
		getServer().start();
		Kitty kitty = Kitty.setup(args);
		kitty.startConsole();
	}

	private static JettyWrapper getServer() {
		JettyWrapper server = new JettyWrapper(System.getProperty("user.dir") + "/src/main/jetty");
		return server;
	}

}
