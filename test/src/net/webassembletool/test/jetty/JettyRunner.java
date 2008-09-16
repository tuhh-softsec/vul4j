package net.webassembletool.test.jetty;

import java.io.File;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.Server;
import org.mortbay.xml.XmlConfiguration;

/**
 * 
 * This is a TestCase class that must be subclassed to create WebApp tests.
 * Tests are guarenteed to have a running Server (Jetty) with specified webapps
 * on specified port during session.
 * 
 * This can be used in two ways : 1) subclassing it, in which case, jetty is
 * started and all tests are run 2) using it as a wrapper test that is : <code>
 * TestCase myTestCase = XXXXXXX;
 * ....
 * new WebAppTestCase("path/to/webapps",myTestCase).run(); //myTestCase will be run after jetty is started
 * </code>
 * wat.jetty.webapproot wat.jetty.home wat.jetty.webapp.classpath
 * 
 * @author Omar BENHAMID
 */

public class JettyRunner {
    private static final Log log = LogFactory.getLog(JettyRunner.class);
    private static Server server = null;
    private static int serverPort = 8080;
    private static String webappsRoot = null;

    public static int getServerPort() {
	return serverPort;
    }

    public static void setServerPort(int serverPort) {
	JettyRunner.serverPort = serverPort;
    }

    public static String getWebappsRoot() {
	return webappsRoot;
    }

    public static void setWebappsRoot(String webappsRoot) {
	JettyRunner.webappsRoot = webappsRoot;
    }

    private JettyRunner() {
	// Do not use
    }

    /**
     * Starts the jettyEngine
     * 
     * @throws Exception If a problem occurs
     */
    public final static void startJetty() throws Exception {
	if (webappsRoot == null)
	    webappsRoot = System.getenv("webappsRoot");
	log.info("Starting jetty");
	server = new Server(serverPort);
	System.setProperty("webapptest.webapproot", webappsRoot);
	XmlConfiguration xml = new XmlConfiguration(JettyRunner.class
		.getResourceAsStream("jetty.xml"));
	xml.configure(server);
	server.start();
    }

    /**
     * Stops running jetty engine.
     * 
     * @throws Exception If a problem occurs
     */
    public final static void stopJetty() throws Exception {
	log.info("Stopping jetty");
	server.stop();
	server = null;

    }

    /**
     * Run jetty as if it were in test
     * 
     * @param args command line arguments : 1 unique argument : path to webapps.
     * @throws Exception If a problem occurs
     */
    public final static void main(String[] args) throws Exception {
	if (args.length != 2) {
	    throw new Exception(
		    "Needs 2 argument : webapps root and server port");
	}
	File webappsRootFolder = new File(args[0]);
	if (!webappsRootFolder.isDirectory()) {
	    throw new Exception("The argument is not a valid directory");
	}
	webappsRoot = webappsRootFolder.getAbsolutePath();
	serverPort = Integer.parseInt(args[1]);
	LineNumberReader in = new LineNumberReader(new InputStreamReader(
		System.in));
	boolean shutdown = false;
	do {
	    startJetty();
	    System.out.println("Jetty started serving webapps in "
		    + webappsRoot);
	    while (true) {
		System.out
			.println("Type q[uit] to shutdown server, r[estart] to restart it :");
		String ln = in.readLine().toLowerCase().trim();
		if (ln.length() == 0)
		    continue;
		if ("quit".startsWith(ln)) {
		    shutdown = true;
		    break;
		}
		if ("restart".startsWith(ln)) {
		    break;
		}
		System.err.println("I don't understand !");
	    }
	    stopJetty();
	    Thread.sleep(500);
	} while (!shutdown);
	System.out.println("Jetty shutdown.");
    }

}