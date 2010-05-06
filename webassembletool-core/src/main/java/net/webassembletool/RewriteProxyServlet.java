package net.webassembletool;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Reverse Proxy servlet with rewrite abilities. It will usually be mapped on *
 * in a webapp deployed on the ROOT context. Other configurations will also
 * work.
 * 
 * <p>
 * Configuration in
 * <strong>net/webassembletool/rewrite-proxy.properties</strong> : <br/>
 * <code>
 * # Rule 1<br/> 
 * <br/>
 * # Regexp for url matching.<br/>
 * 01.pattern=^/([a-z]{2})/application1/(.*)$<br/>
 * # Regexp for query matching.<br/>
 * 01.queryPattern=^(.+)$<br/>
 * # Rewrite to the following url. $1 - $n are available from the previously matched <br/>
 * # pattern.<br/>
 * 01.rewrite=$2<br/>
 * # Rewrite query string to the following. $1 - $n are available from the previously<br/>
 * # matched pattern and $QUERY is the original query.<br/>
 * 01.queryRewrite=$QUERY&lang=$1<br/>
 * # Target providers (must be configured in driver.properties) </br>
 * 01.provider=application1<br/>
 * <br/>
 * # Rule 2 <br/>
 * 02.pattern=/.*<br/>
 * 02.provider=cms<br/>
 * </code>
 * </p>
 * 
 * @author Nicolas Richeton
 */
public class RewriteProxyServlet extends HttpServlet {

	/**
	 * Holds configuration for one single rule.
	 * 
	 * @author Nicolas Richeton
	 */
	public class ReverseConfiguration {

		private Pattern urlMatchPattern;
		private String provider;
		private Pattern queryMatchPattern;
		private String queryRewrite;
		private String urlRewrite;

		public Pattern getUrlMatchPattern() {
			return urlMatchPattern;
		}

		public String getProvider() {
			return provider;
		}

		public Pattern getQueryMatchPattern() {
			return queryMatchPattern;
		}

		public String getQueryRewrite() {
			return queryRewrite;
		}

		public String getUrlRewrite() {
			return urlRewrite;
		}

		public void setUrlMatchPattern(Pattern matchPattern) {
			this.urlMatchPattern = matchPattern;
		}

		public void setProvider(String provider) {
			this.provider = provider;
		}

		public void setQueryMatchPattern(Pattern queryMatchPattern) {
			this.queryMatchPattern = queryMatchPattern;
		}

		public void setQueryRewrite(String queryRewrite) {
			this.queryRewrite = queryRewrite;
		}

		public void setUrlRewrite(String rewrite) {
			this.urlRewrite = rewrite;
		}

	}

	/**
	 * Servlet request wrapper used to inject the rewritten query string.
	 * 
	 * @author Nicolas Richeton
	 * 
	 */
	public class ReverseHttpRequest extends HttpServletRequestWrapper {

		String queryString;

		/**
		 * Create a new HttpServletRequest by wrapping a previous one.
		 * 
		 * @param request
		 *            Request to wrap.
		 * @param queryString
		 *            New query string.
		 */
		public ReverseHttpRequest(HttpServletRequest request, String queryString) {
			super(request);
			this.queryString = queryString;
		}

		/**
		 * @see javax.servlet.http.HttpServletRequestWrapper#getQueryString()
		 */
		@Override
		public String getQueryString() {
			return queryString;
		}

	}

	private static Log logger = LogFactory.getLog(RewriteProxyServlet.class);

	private static final long serialVersionUID = 8479657871058986339L;

	private final ArrayList<ReverseConfiguration> configuration = new ArrayList<ReverseConfiguration>();

	/**
	 * {@inheritDoc}
	 * 
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		TreeMap<String, ReverseConfiguration> confTree = new TreeMap<String, ReverseConfiguration>();
		try {
			// Open configuration file.
			InputStream propertiesInput = RewriteProxyServlet.class
					.getResourceAsStream("/net/webassembletool/rewrite-proxy.properties");

			// Load properties
			Properties properties = new Properties();
			properties.load(propertiesInput);
			propertiesInput.close();

			// Loop on properties.
			for (Object key : properties.keySet()) {

				// Get line content.
				String[] keySplitted = ((String) key).split("\\.");
				String rule = keySplitted[0];
				String attribute = keySplitted[1];
				String value = properties.getProperty((String) key);

				// Create configuration instance if necessary.
				ReverseConfiguration currentConf = confTree.get(rule);
				if (currentConf == null) {
					currentConf = new ReverseConfiguration();
				}

				// Set values.
				if ("provider".equals(attribute)) {
					currentConf.setProvider(value);
				} else if ("pattern".equals(attribute)) {
					currentConf.setUrlMatchPattern(Pattern.compile(value));
				} else if ("rewrite".equals(attribute)) {
					currentConf.setUrlRewrite(value);
				} else if ("queryPattern".equals(attribute)) {
					currentConf.setQueryMatchPattern(Pattern.compile(value));
				} else if ("queryRewrite".equals(attribute)) {
					currentConf.setQueryRewrite(value);
				}

				// Save configuration instance.
				confTree.put(rule, currentConf);
			}

			// Add all instances to global configuration.
			configuration.addAll(confTree.values());
		} catch (IOException e) {
			throw new ServletException(e);
		}
	}

	/**
	 * {@inheritDoc} javax.servlet.http.HttpServlet#service(javax.servlet.http.
	 * HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// Build request url
		String relUrl = request.getRequestURI();
		relUrl = relUrl.substring(request.getContextPath().length());
		if (request.getServletPath() != null) {
			relUrl = relUrl.substring(request.getServletPath().length());
		}

		// Look for rule.
		for (ReverseConfiguration conf : configuration) {
			// Match url
			Matcher urlMatcher = conf.getUrlMatchPattern().matcher(relUrl);

			// Match query if necessary
			Matcher queryMatcher = null;
			if (conf.getQueryMatchPattern() != null) {
				queryMatcher = conf.getQueryMatchPattern().matcher(
						getStringNotNull(request.getQueryString()));
			}

			if (urlMatcher.matches()
					&& (queryMatcher == null || queryMatcher.matches())) {
				// Rule matched.

				// Create new URL
				String newUrl = relUrl;
				if (conf.getUrlRewrite() != null) {
					newUrl = conf.getUrlRewrite();
				}

				for (int i = 1; i < urlMatcher.groupCount() + 1; i++) {
					newUrl = newUrl.replace("$" + i, urlMatcher.group(i));
				}

				String targetQueryString = null;

				// Process Query string
				if (queryMatcher != null) {
					// Create new query string
					targetQueryString = getStringNotNull(conf.getQueryRewrite());

					// Do replacements.
					for (int i = 1; i < queryMatcher.groupCount() + 1; i++) {
						targetQueryString = targetQueryString.replace("$" + i,
								queryMatcher.group(i));
					}

					// clear query string if empty
					if ("".equals(targetQueryString)) {
						targetQueryString = null;
					}
				} else {
					targetQueryString = request.getQueryString();
				}

				// Nice log
				if (logger.isDebugEnabled()) {
					logger.debug("Proxying " + relUrl + " to " + newUrl
							+ " w/ query " + targetQueryString);
				}

				// Proxy request and return.
				try {
					DriverFactory.getInstance(conf.getProvider()).proxy(newUrl,
							new ReverseHttpRequest(request, targetQueryString),
							response);
					return;
				} catch (HttpErrorPage e) {
					throw new ServletException(e);
				}
			}
		}
	}

	private String getStringNotNull(String str) {
		if (str == null) {
			return "";
		}
		return str;
	}
}
