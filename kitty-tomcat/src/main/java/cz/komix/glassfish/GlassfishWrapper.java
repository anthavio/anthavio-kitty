package com.anthavio.glassfish;

import com.anthavio.kitty.server.ServerWrapper;

public abstract class GlassfishWrapper implements ServerWrapper {
	/*
		private final GlassFish glassfish;

		public GlassfishWrapper() {
			File fDomainXml = new File("domain.xml");
			if (!fDomainXml.exists()) {
				throw new RuntimeException("Jetty directory does not exist " + fDomainXml.getAbsolutePath());
			}

			try {
				GlassFishProperties glassfishProperties = new GlassFishProperties();
				glassfishProperties.setConfigFileURI(new File("domain.xml").toURI().toString()); // this is the only change to the above code.
				glassfish = GlassFishRuntime.bootstrap().newGlassFish(glassfishProperties);
			} catch (Exception x) {
				if (x instanceof RuntimeException) {
					throw (RuntimeException) x;
				}
				throw new RuntimeException("Glassfish failed to configure", x);
			}

		}

		@Override
		public void start() {
			glassfish.start();
			//TODO glassfish.getStatus()
		}

		@Override
		public void stop() {
			glassfish.stop();
			glassfish.dispose();
		}

		@Override
		public Map<String, ApplicationContext> getSpringContexts() {
			//http://wikis.sun.com/display/GlassFish/3.1EmbeddedOnePager
			try {
				Deployer deployer = glassfish.getDeployer();
				String appName = deployer.deploy(new File("hello.war"), "--contextroot=hello", "--name=app1");

				return null;
			} catch (Exception x) {
				if (x instanceof RuntimeException) {
					throw (RuntimeException) x;
				}
				throw new RuntimeException("Glassfish failed to configure", x);
			}
		}
	*/
}
