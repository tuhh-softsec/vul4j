package org.esigate;


/**
 * Reverse Proxy servlet with rewrite abilities. It will usually be mapped on *
 * in a webapp deployed on the ROOT context. Other configurations will also
 * work.
 * 
 * <p>
 * Configuration in
 * <strong>org/esigate/rewrite-proxy.properties</strong> : <br/>
 * <code>
 * # Rule 1<br/> 
 * <br/>
 * # Regexp for url matching.<br/>
 * 01.pattern=^/([a-z]{2})/application1/(.*)$<br/>
 * # Regexp for query matching.<br/>
 * 01.queryPattern=^(.+)$<br/>
 * # Pattern for scheme matching, only http or https<br/>
 * 01.schemePattern=http<br/>
 * # Pattern for port matching<br/>
 * 01.portPattern=8080<br/>
 * # Rewrite to the following url. $1 - $n are available from the previously matched <br/>
 * # pattern.<br/>
 * 01.rewrite=$2<br/>
 * # Rewrite query string to the following. $1 - $n are available from the previously<br/>
 * # matched pattern and $QUERY is the original query.<br/>
 * 01.queryRewrite=$QUERY&lang=$1<br/>
 * # Rewrite scheme to the following. Only http or https are allowed.<br/>
 * 01.schemeRewrite<br/>
 * # Rewrite port to the following.<br/>
 * 01.portRewrite=8443<br/>
 * # Target providers (must be configured in driver.properties) </br>
 * 01.provider=application1<br/>
 * #If no provider has been specified, the request will be redirected (default 302) to the rewrited location. You can specified a specific response code like this :<br/>
 * 01.redirect=301 : the response code must be in the range 300-400.<br/>
 * <br/>
 * # Rule 2 <br/>
 * 02.pattern=/.*<br/>
 * 02.provider=cms<br/>
 * </code>
 * </p>
 * 
 * @author Nicolas Richeton
 * @author Guillaume Mary
 * @deprecated use {@linkplain org.esigate.servlet.RewriteProxyServlet} instead.
 */
public class RewriteProxyServlet extends org.esigate.servlet.RewriteProxyServlet {
	private static final long serialVersionUID = 1L;
}
