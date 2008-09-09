/**
 * 
 */
package net.webassembletool.webapptests.http;

import net.webassembletool.webapptests.ExtensibleTestCase;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


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

public class WebAppTestCase extends ExtensibleTestCase {

	static final Log log = LogFactory.getLog(WebAppTestCase.class);
	JettyWebappContainerFeature jettyStarter;
	
	public WebAppTestCase(String webappsRoot) {
		super();
		jettyStarter = new JettyWebappContainerFeature(webappsRoot);
		addFeature(jettyStarter);
	}

	/**
	 * Converts a relative url to a url on the test server.
	 * 
	 * @param relativeURL
	 *            the relative URL to append to server name
	 * @return returns the absolute URL as a string
	 */
	public String getAbsoluteURL(String relativeURL) {
		return jettyStarter.getAbsoluteURL(relativeURL);
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
		return jettyStarter.createGet(url);
	}
	
	/**
	 * Get the server part of url to this container
	 * @return url ending with /
	 */
	public String getServerURLPrefix() {
		return jettyStarter.getServerURLPrefix();
	}

}