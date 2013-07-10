/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.esigate.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.ProtectionDomain;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import org.esigate.server.metrics.InstrumentedServerConnector;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jetty9.InstrumentedHandler;
import com.codahale.metrics.jetty9.InstrumentedQueuedThreadPool;

/**
 * The bootstrap code for esigate-server, using jetty.
 * 
 * 
 * <p>
 * Inspiration from Ole Christian Rynning
 * (http://open.bekk.no/embedded-jetty-7-webapp-executable-with-maven/)
 * 
 * @author Nicolas Richeton
 * 
 */
public class EsigateServer {

	private static String contextPath;
	protected static int controlPort;
	private static String extraClasspath;
	private static long idleTimeout = 0;
	private static int maxThreads = 0;
	private static int minThreads = 0;
	private static int outputBufferSize = 0;
	private static int port;
	private final static int PROPERTY_DEFAULT_CONTROL_PORT = 8081;
	private final static int PROPERTY_DEFAULT_HTTP_PORT = 8080;
	private final static String PROPERTY_PREFIX = "server.";

	/**
	 * Get an integer from System properties
	 * 
	 * @param prefix
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	private static int getProperty(String prefix, String name, int defaultValue) {
		int result = defaultValue;

		try {
			result = Integer.parseInt(System.getProperty(prefix + name));
		} catch (NumberFormatException e) {
			System.err.println("Value for " + prefix + name + " must be an integer. Using default " + defaultValue);
		}
		return result;
	}

	/**
	 * Get String from System properties
	 * 
	 * @param prefix
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	private static String getProperty(String prefix, String name, String defaultValue) {
		return System.getProperty(prefix + name, defaultValue);
	}

	public static void init() {

		// Get configuration

		// Read from "server.properties" or custom file.
		String configFile = null;
		try {
			configFile = System.getProperty(PROPERTY_PREFIX + "config", "server.properties");
			System.out.println("Loading server configuration from " + configFile);

			Properties serverProperties = new Properties();

			try (InputStream is = new FileInputStream(configFile);) {
				serverProperties.load(is);
			}

			for (Object prop : serverProperties.keySet()) {
				String serverPropertyName = (String) prop;
				System.setProperty(PROPERTY_PREFIX + serverPropertyName,
						serverProperties.getProperty(serverPropertyName));
			}
		} catch (FileNotFoundException e) {
			System.out.println(configFile + " not found.");
		} catch (IOException e) {
			System.out.println("Unexpected error reading " + configFile);
		}

		// Read system properties
		System.out.println("Using configuration provided using '-D' parameter and/or default values");
		EsigateServer.port = getProperty(PROPERTY_PREFIX, "port", PROPERTY_DEFAULT_HTTP_PORT);
		EsigateServer.controlPort = getProperty(PROPERTY_PREFIX, "controlPort", PROPERTY_DEFAULT_CONTROL_PORT);
		EsigateServer.contextPath = getProperty(PROPERTY_PREFIX, "contextPath", "/");
		EsigateServer.extraClasspath = getProperty(PROPERTY_PREFIX, "extraClasspath", null);
		EsigateServer.maxThreads = getProperty(PROPERTY_PREFIX, "maxThreads", 500);
		EsigateServer.minThreads = getProperty(PROPERTY_PREFIX, "minThreads", 40);
		EsigateServer.outputBufferSize = getProperty(PROPERTY_PREFIX, "outputBufferSize", 8 * 1024);
		EsigateServer.idleTimeout = getProperty(PROPERTY_PREFIX, "idleTimeout", 30 * 1000);
	}

	/**
	 * Esigate Server entry point.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		if (args.length < 1) {
			EsigateServer.usage();
			return;
		}

		switch (args[0]) {
		case "start":
			EsigateServer.init();
			EsigateServer.start();
			break;

		case "stop":
			EsigateServer.stop();
			break;

		default:
			EsigateServer.usage();
			break;
		}
	}

	private static void resetTempDirectory(WebAppContext context, String currentDir) throws IOException {
		File workDir;
		// Currently disabled because this may be dangerous.
		// if (EsigateServer.workPath != null) {
		// workDir = new File(EsigateServer.workPath);
		// } else {
		workDir = new File(currentDir, "work");
		// }
		FileUtils.deleteDirectory(workDir);
		context.setTempDirectory(workDir);
	}

	/**
	 * Create and start server.
	 * 
	 * @throws Exception
	 */
	private static void start() throws Exception {
		MetricRegistry registry = new MetricRegistry();
		QueuedThreadPool threadPool = new InstrumentedQueuedThreadPool(registry);
		threadPool.setName("esigate");
		threadPool.setMaxThreads(maxThreads);
		threadPool.setMinThreads(minThreads);

		Server srv = new Server(threadPool);
		srv.setStopAtShutdown(true);
		srv.setStopTimeout(5000);

		// HTTP Configuration
		HttpConfiguration http_config = new HttpConfiguration();
		http_config.setOutputBufferSize(outputBufferSize);
		http_config.setSendServerVersion(false);

		try (ServerConnector connector = new InstrumentedServerConnector("main", EsigateServer.port, srv, registry,
				new HttpConnectionFactory(http_config)); ServerConnector controlConnector = new ServerConnector(srv)) {

			// Main connector
			connector.setIdleTimeout(EsigateServer.idleTimeout);
			connector.setSoLingerTime(-1);
			connector.setName("main");

			// Control connector
			controlConnector.setHost("127.0.0.1");
			controlConnector.setPort(EsigateServer.controlPort);
			controlConnector.setName("control");

			srv.setConnectors(new Connector[] { connector, controlConnector });
			// War
			ProtectionDomain protectionDomain = EsigateServer.class.getProtectionDomain();
			String warFile = protectionDomain.getCodeSource().getLocation().toExternalForm();
			String currentDir = new File(protectionDomain.getCodeSource().getLocation().getPath()).getParent();

			WebAppContext context = new WebAppContext(warFile, EsigateServer.contextPath);
			context.setServer(srv);

			// Add extra classpath (allows to add extensions).
			if (EsigateServer.extraClasspath != null) {
				context.setExtraClasspath(EsigateServer.extraClasspath);
			}
			resetTempDirectory(context, currentDir);

			// Add the handlers
			HandlerCollection handlers = new HandlerList();
			// control handler must be the first one.
			// Work in progress, currently disabled.
			handlers.addHandler(new ControlHandler(registry));
			InstrumentedHandler ih = new InstrumentedHandler(registry);
			ih.setName("main");
			ih.setHandler(context);
			handlers.addHandler(ih);

			srv.setHandler(handlers);

			srv.start();
			srv.join();
		}

	}

	/**
	 * Send a shutdown request to esigate server
	 */
	private static void stop() {
		ControlHandler.shutdown(EsigateServer.controlPort);
	}

	/**
	 * Display usage informations.
	 */
	private static void usage() {
		StringBuffer usageText = new StringBuffer();
		usageText.append("Usage: java -Desigate.config=esigate.properties -jar esigate-server.jar [start|stop]\n\t");
		usageText.append("start    Start the server (default)\n\t");
		usageText.append("stop     Stop the server gracefully\n\t");

		System.out.println(usageText.toString());
		System.exit(-1);
	}
}