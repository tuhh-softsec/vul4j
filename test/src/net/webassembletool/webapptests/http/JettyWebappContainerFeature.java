/**
 * 
 */
package net.webassembletool.webapptests.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import junit.framework.TestCase;
import junit.framework.TestResult;
import net.webassembletool.webapptests.TestCaseFeature;
import net.webassembletool.webapptests.http.jetty.CommonsLoggingJettyLogger;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.Server;
import org.mortbay.xml.XmlConfiguration;

/**
 * <b>Description : </b>
 * <p>
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
 * </p>
 * <hr>
 * 
 * @author OMBEN, 20 août 2008
 */

public class JettyWebappContainerFeature implements TestCaseFeature {

    static final Log log = LogFactory.getLog(JettyWebappContainerFeature.class);

    private Server svr = null;
    private int serverPort = 8080;
    private String webappsRoot = null;
    private String extraClassPath = "";

    /**
     * Construct webapp engine. Webapp server has default configuration.
     * 
     * @param webappsRoot : the root where to find webapps that should be
     *            started up or static content.
     */
    public JettyWebappContainerFeature(String webappsRoot) {
	this.webappsRoot = webappsRoot;
    }

    /**
     * Construct webapp engine. Webapp server has default configuration.
     * 
     * @param webappsRoot The root of webapps tu run
     * @param port the port on which to start this featuer
     */
    public JettyWebappContainerFeature(String webappsRoot, int port) {
	this.webappsRoot = webappsRoot;
	this.serverPort = port;
    }

    /**
     * Used to set port, must be done before tests are run usually in subclass
     * constructor.
     * 
     * @param port
     */
    protected void setServerPort(int port) {
	this.serverPort = port;
    }

    /**
     * Sets extra classpath entries that are available for each webapp This is
     * useful to add classes that are to be tested in webapp context
     * 
     * @param extraClassPath
     */

    protected void setExtraClassPath(String extraClassPath) {
	this.extraClassPath = extraClassPath;
    }

    /**
     * Starts the jettyEngine
     * 
     * @throws Exception
     */
    protected void startJetty() throws Exception {
	log.info("Starting the jetty");
	System.setProperty("org.mortbay.log.class",
		CommonsLoggingJettyLogger.class.getName());
	svr = new Server(serverPort);
	System.setProperty("webapptest.webapproot", webappsRoot);
	System.setProperty("webapptest.extraclasspath",
		extraClassPath == null ? "" : extraClassPath);

	XmlConfiguration xml = new XmlConfiguration(
		JettyWebappContainerFeature.class
			.getResourceAsStream("jetty/jetty.xml"));
	xml.configure(svr);

	log.info("Starting jetty as : " + getServerURLPrefix());
	svr.start();
    }

    /**
     * Get the server part of url to this container
     * 
     * @return url ending with /
     */
    public String getServerURLPrefix() {
	return "http://localhost:" + serverPort + "/";
    }

    /**
     * Stops running jetty engine.
     * 
     * @throws Exception
     */
    protected void stopJetty() throws Exception {
	log.info("Stopping the jetty");
	svr.stop();
	svr = null;

    }

    /**
     * Start jetty server
     * 
     * @see net.webassembletool.webapptests.TestCaseFeature#beforeRun(junit.framework.TestCase,
     *      junit.framework.TestResult)
     */
    public void beforeRun(TestCase c, TestResult res) {
	try {
	    startJetty();
	} catch (Exception e) {
	    throw new RuntimeException("Unable to start servlet engine", e);
	}
    }

    /**
     * Stop jetty server
     * 
     * @see net.webassembletool.webapptests.TestCaseFeature#afterRun(junit.framework.TestCase,
     *      junit.framework.TestResult)
     */
    public void afterRun(TestCase c, TestResult res) {
	try {
	    stopJetty();
	} catch (Exception e) {
	    throw new RuntimeException("Unable to stop servlet engine", e);
	}
    }

    /**
     * Converts a relative url to a url on the test server.
     * 
     * @param relativeURL the relative URL to append to server name
     * @return returns the absolute URL as a string
     */
    public String getAbsoluteURL(String relativeURL) {
	return getServerURLPrefix()
		+ (relativeURL.startsWith("/") ? relativeURL.substring(1)
			: relativeURL);
    }

    /**
     * Utility method to execute get requests on local http applicaiton server.
     * 
     * @param url the url of the ressource to query. The url can either be
     *            absolute or relative, in which case it is evalueated agains
     *            this test's server
     * @return the HttpMethod object.
     */
    public HttpMethod createGet(String url) {
	HttpMethod m;
	if (url.indexOf("://") >= 0) { // Avoid absolute urls
	    m = new GetMethod(url);
	} else {
	    m = new GetMethod(getAbsoluteURL(url));
	}
	return m;
    }

    /**
     * Run jetty as if it were in test
     * 
     * @param args command line arguments : 1 unique argument : path to webapps.
     */
    public static void main(String[] args) {
	if (args.length != 1) {
	    System.err.println("Needs one argument : webapps root");
	    return;
	}
	File wr = new File(args[0]);
	if (!wr.isDirectory()) {
	    System.err.println("The argument is not a valid directory");
	    return;
	}

	TestCaseFeature f = new JettyWebappContainerFeature(wr
		.getAbsolutePath());

	LineNumberReader in = new LineNumberReader(new InputStreamReader(
		System.in));
	boolean shutdown = false;

	do {
	    f.beforeRun(null, null);
	    System.out.println("Jetty is started serving webapps in "
		    + wr.getAbsolutePath());
	    while (true) {
		try {
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
		    System.out.println("I don't understand !");
		} catch (IOException ex) {
		    throw new RuntimeException("Failed reading command line");
		}
	    }
	    f.afterRun(null, null);
	    try {
		Thread.sleep(500);
	    } catch (InterruptedException e) {
		// Nothing to do
	    }
	} while (!shutdown);
	System.out.println("Jetty shutdown.");
    }

}