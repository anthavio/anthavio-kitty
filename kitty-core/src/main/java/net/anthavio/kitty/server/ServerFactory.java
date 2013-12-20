package net.anthavio.kitty.server;

/**
 * @author vanek
 *
 */
public interface ServerFactory {

	/**
	 * @return ServerWrapper implementation
	 */
	public ServerWrapper getServer();

}
