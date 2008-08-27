/**
 * 
 */
package net.webassembletool.webapptest;


import junit.framework.TestCase;
import junit.framework.TestResult;

import net.webassembletool.webapptest.jetty.CommonsLoggingJettyLogger;

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
 * wat.jetty.webapproot wat.jetty.home wat.jetty.webapp.classpath
 * </p>
 * <hr>
 * 
 * @author OMBEN, 20 août 2008
 */

public class WebAppTestCase extends TestCase {

	static final Log log = LogFactory.getLog(WebAppTestCase.class);

	private Server svr = null;
	private int serverPort = 8080;
	private String webappsRoot = null;
	private String extraClassPath = "";

	/**
	 * Construct webapp engine. Webapp server has default configuration.
	 * 
	 * @param webappsRoot
	 *            : the root where to find webapps that should be started up or
	 *            static content.
	 */
	protected WebAppTestCase(String webappsRoot) {
		this.webappsRoot = webappsRoot;
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
		System.setProperty("org.mortbay.log.class", CommonsLoggingJettyLogger.class
				.getCanonicalName());
		svr = new Server(serverPort);
		System.setProperty("webapptest.webapproot", webappsRoot);
		System.setProperty("webapptest.extraclasspath",
				extraClassPath == null ? "" : extraClassPath);

		XmlConfiguration xml = new XmlConfiguration(WebAppTestCase.class
				.getResourceAsStream("jetty/jetty.xml"));
		xml.configure(svr);

		log.info("Starting jetty as : " + getServerURLPrefix());
		svr.start();
	}

	private String getServerURLPrefix() {
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
	 * Overrides run method to ensure Jetty is started before tests.
	 */
	@Override
	public void run(TestResult result) {
		try {
			startJetty();
		} catch (Exception e) {
			throw new RuntimeException("Unable to start servlet engine", e);
		}
		super.run(result);
		try {
			stopJetty();
		} catch (Exception e) {
			throw new RuntimeException("Unable to stop servlet engine", e);
		}
	}

	/**
	 * Converts a relative url to a url on the test server.
	 * 
	 * @param relativeURL
	 *            the relative URL to append to server name
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
	 * @param url
	 *            the url of the ressource to query. The url can either be
	 *            absolute or relative, in which case it is evalueated agains
	 *            this test's server
	 * @return the HttpMethod object.
	 */
	public HttpMethod createGet(String url) {
		HttpMethod m;
		if (url.contains("://")) {
			m = new GetMethod(url);
		} else {
			m = new GetMethod(getAbsoluteURL(url));
		}
		return m;
	}

}