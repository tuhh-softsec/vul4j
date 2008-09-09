/**
 * 
 */
package net.webassembletool.webapptests.http.jetty;

import org.mortbay.jetty.webapp.Configuration;
import org.mortbay.jetty.webapp.WebAppClassLoader;
import org.mortbay.jetty.webapp.WebAppContext;

/**
 * <b>Description : </b>
 * <p>
 * This class adds to the WebappContext extra classpath entries that are read
 * form the system environement property fr.smile.webapptest.extraclasspath"
 * </p>
 * <hr>
 * 
 * @author Omar BENHAMID, 20 août 2008
 */
public class ClassPathConfig implements Configuration {
    private static final long serialVersionUID = 1L;

    WebAppContext _ctx;

    /**
     * @see org.mortbay.jetty.webapp.Configuration#configureDefaults()
     */
    public void configureDefaults() throws Exception {

    }

    /**
     * Configures WebAppContext's classloader with extra entries. Extra entries
     * are read from the webapptest.extraclasspath system property to each
     * context that uses this configuration object
     * 
     * @exception Exception
     *                as defined in {@link Configuration}
     */
    public void configureClassLoader() throws Exception {
	((WebAppClassLoader) _ctx.getClassLoader()).addClassPath(System
		.getProperty("webapptest.extraclasspath"));

    }

    /**
     * @see org.mortbay.jetty.webapp.Configuration#configureWebApp()
     */
    public void configureWebApp() throws Exception {

    }

    /**
     * @see org.mortbay.jetty.webapp.Configuration#deconfigureWebApp()
     */
    public void deconfigureWebApp() throws Exception {

    }

    /**
     * @see org.mortbay.jetty.webapp.Configuration#getWebAppContext()
     */
    public WebAppContext getWebAppContext() {
	return _ctx;
    }

    /**
     * @see org.mortbay.jetty.webapp.Configuration#setWebAppContext(org.mortbay.jetty.webapp.WebAppContext)
     */
    public void setWebAppContext(WebAppContext ctx) {
	_ctx = ctx;
    }

}
