package com.anthavio.jetty.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anthavio.jetty.Jetty6Wrapper;

/**
 * Jetty 6 instance manager
 * 
 * @author martin.vanek
 *
 */
public class Jetty6Manager {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private Map<JettySetupData, Jetty6Wrapper> cache = new HashMap<JettySetupData, Jetty6Wrapper>();

	private static Jetty6Manager singleton = new Jetty6Manager();

	public static Jetty6Manager i() {
		return singleton;
	}

	private Jetty6Manager() {

	}

	private void housekeeping(JettySetupData newJsd) {
		Set<JettySetupData> keySet = cache.keySet();
		for (JettySetupData jsd : keySet) {
			Jetty6Wrapper jetty = null;
			switch (jsd.cache) {
			case NEVER:
				//remove abandoned entries
				jetty = cache.remove(jsd);
				break;
			case CHANGE:
				//remove change driven entries
				if (!newJsd.equals(jsd)) {
					jetty = cache.remove(jsd);
				}
				break;
			}
			if (jetty != null) {
				logger.debug("Cache remove " + jsd);
				try {
					if (jetty.isStarted()) {
						jetty.stop();
					}
				} catch (Exception x) {
					logger.warn("Exception while stopping Jetty " + x);
				}
			}
		}
	}

	/*
	public Jetty6Wrapper startJetty(JettySetupData... jsd) {
		//houskeeping first...
			housekeeping(jsd);
	}
	*/
	public Jetty6Wrapper startJetty(JettySetupData jsd) {
		//houskeeping first...
		housekeeping(jsd);

		Jetty6Wrapper jetty = cache.get(jsd);
		if (jetty == null) {
			logger.debug("Cache miss " + jsd);
			jetty = new Jetty6Wrapper(jsd.jettyHome, jsd.port, jsd.configs);
			jetty.start();
			cache.put(jsd, jetty);
		} else {
			logger.debug("Cache hit " + jsd);
		}

		return jetty;
	}

	/**
	 * For a good citizents that are cleaning after themselves
	 */
	public void stopJetty(JettySetupData jsd) {
		if (jsd.cache == Cache.NEVER) {
			Jetty6Wrapper jetty = cache.remove(jsd);
			if (jetty == null) {
				throw new IllegalArgumentException("Jetty not found in cache " + jsd);
			}
			try {
				logger.debug("Cache remove " + jsd);
				if (jetty.isStarted()) {
					jetty.stop();
				}
			} catch (Exception x) {
				logger.warn("Exception while stopping Jetty " + x);
			}
		}
	}

	public static enum Cache {
		NEVER, //don't cache
		FOREVER, //cache forever
		CHANGE; //cache until different instance is requested
	}

	/**
	 * This class duplicates @JettyConfig annotation simple because Java annotation 
	 * is not a regular class and cannot have methods
	 * 
	 * @author martin.vanek
	 *
	 */
	public static class JettySetupData {

		private final String jettyHome;

		private final int port;

		private final String[] configs;

		private final Cache cache;

		public JettySetupData(String jettyHome, int port, String[] configs, Cache cache) {
			this.jettyHome = jettyHome;
			this.port = port;
			this.configs = configs;
			this.cache = cache;
		}

		@Override
		public String toString() {
			return jettyHome + " " + port + " " + Arrays.asList(configs) + " " + cache;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(configs);
			result = prime * result + ((jettyHome == null) ? 0 : jettyHome.hashCode());
			result = prime * result + port;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			JettySetupData other = (JettySetupData) obj;
			if (!Arrays.equals(configs, other.configs))
				return false;
			if (jettyHome == null) {
				if (other.jettyHome != null)
					return false;
			} else if (!jettyHome.equals(other.jettyHome))
				return false;
			if (port != other.port)
				return false;
			return true;
		}

	}
}
