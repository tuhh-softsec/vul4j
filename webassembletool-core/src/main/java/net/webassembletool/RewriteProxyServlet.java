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

		private Pattern matchPattern;
		private String provider;
		private String queryRewrite;
		private String rewrite;

		public Pattern getMatchPattern() {
			return matchPattern;
		}

		public String getProvider() {
			return provider;
		}

		public String getQueryRewrite() {
			return queryRewrite;
		}

		public String getRewrite() {
			return rewrite;
		}

		public void setMatchPattern(Pattern matchPattern) {
			this.matchPattern = matchPattern;
		}

		public void setProvider(String provider) {
			this.provider = provider;
		}

		public void setQueryRewrite(String queryRewrite) {
			this.queryRewrite = queryRewrite;
		}

		public void setRewrite(String rewrite) {
			this.rewrite = rewrite;
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
		 *      {@inheritDoc}
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
					currentConf.setMatchPattern(Pattern.compile(value));
				} else if ("rewrite".equals(attribute)) {
					currentConf.setRewrite(value);
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
			Matcher m = conf.getMatchPattern().matcher(relUrl);

			if (m.matches()) {
				// Rule matched.

				// Create new URL
				String newUrl = relUrl;
				if (conf.getRewrite() != null) {
					newUrl = conf.getRewrite();
				}

				String targetQueryString = null;

				for (int i = 1; i < m.groupCount() + 1; i++) {
					newUrl = newUrl.replace("$" + i, m.group(i));
				}

				// Process Query string
				if (conf.getQueryRewrite() != null) {
					// Create new query string
					targetQueryString = conf.getQueryRewrite();

					// Get query string.
					String originalQueryString = request.getQueryString();
					if (originalQueryString == null) {
						originalQueryString = "";
					}

					// Do replacements.
					targetQueryString = targetQueryString.replace("$QUERY",
							originalQueryString);
					for (int i = 1; i < m.groupCount() + 1; i++) {

						targetQueryString = targetQueryString.replace("$" + i,
								m.group(i));
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
}
