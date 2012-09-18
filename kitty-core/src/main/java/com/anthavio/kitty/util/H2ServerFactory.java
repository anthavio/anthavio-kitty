/**
 * 
 */
package com.anthavio.kitty.util;

import java.net.BindException;
import java.sql.SQLException;

import org.h2.jdbc.JdbcSQLException;
import org.h2.server.TcpServer;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author vanek
 *
 */
public class H2ServerFactory {

	private static final Logger log = LoggerFactory.getLogger(H2ServerFactory.class);

	public static Server startTcpServer(String... args) throws SQLException {
		TcpServer service = new TcpServer();
		Server server = new Server(service, args);
		//service.setShutdownHandler(server);
		try {
			server.start();
		} catch (JdbcSQLException jsx) {
			if (jsx.getCause() instanceof BindException) {
				log.info("BindException occured. H2 server is probably already running");
			}
		}
		return server;
	}
}
