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

package org.esigate.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map.Entry;
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

import org.apache.http.HttpStatus;
import org.esigate.ConfigurationException;
import org.esigate.DriverFactory;
import org.esigate.HttpErrorPage;
import org.esigate.util.UriUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reverse Proxy servlet with rewrite abilities. It will usually be mapped on * in a webapp deployed on the ROOT
 * context. Other configurations will also work.
 * 
 * <p>
 * Configuration in <strong>org/esigate/rewrite-proxy.properties</strong> : <br/>
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
 * #If no provider has been specified, the request will be redirected (default 302) to the rewrited location.
 * You can specified a specific response code like this :<br/>
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
 */
public class RewriteProxyServlet extends HttpServlet {
    private static final long serialVersionUID = 8479657871058986339L;
    private static final Logger LOG = LoggerFactory.getLogger(RewriteProxyServlet.class);

    /**
     * Holds configuration for one single rule.
     * 
     * @author Nicolas Richeton
     */
    private static class ReverseConfiguration {

        private Pattern urlMatchPattern;
        private String provider;
        private Pattern queryMatchPattern;
        private String queryRewrite;
        private String urlRewrite;
        private String schemePattern;
        private String schemeRewrite;
        private Integer redirect;
        private Integer portPattern;
        private Integer portRewrite;

        public Integer getPortPattern() {
            return portPattern;
        }

        public Integer getPortRewrite() {
            return portRewrite;
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

        public Integer getRedirect() {
            return redirect;
        }

        public String getSchemePattern() {
            return schemePattern;
        }

        public String getSchemeRewrite() {
            return schemeRewrite;
        }

        public Pattern getUrlMatchPattern() {
            return urlMatchPattern;
        }

        public String getUrlRewrite() {
            return urlRewrite;
        }

        public void setPortPattern(Integer portPattern) {
            this.portPattern = portPattern;
        }

        public void setPortRewrite(Integer portRewrite) {
            this.portRewrite = portRewrite;
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

        public void setRedirect(Integer redirect) {
            this.redirect = redirect;
        }

        public void setSchemePattern(String schemePattern) {
            this.schemePattern = schemePattern;
        }

        public void setSchemeRewrite(String schemeRewrite) {
            this.schemeRewrite = schemeRewrite;
        }

        public void setUrlMatchPattern(Pattern matchPattern) {
            this.urlMatchPattern = matchPattern;
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
    private static class ReverseHttpRequest extends HttpServletRequestWrapper {
        private final String queryString;

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

    private final ArrayList<ReverseConfiguration> configuration = new ArrayList<ReverseConfiguration>();

    private String getStringNotNull(String str) {
        if (str == null) {
            return "";
        }
        return str;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        TreeMap<String, ReverseConfiguration> confTree = new TreeMap<String, ReverseConfiguration>();
        InputStream propertiesInput = null;
        try {
            // Open configuration file.
            propertiesInput = RewriteProxyServlet.class.getResourceAsStream("/org/esigate/rewrite-proxy.properties");

            // Load properties
            Properties properties = new Properties();
            properties.load(propertiesInput);
            propertiesInput.close();

            // Loop on properties.
            for (Entry<Object, Object> entry : properties.entrySet()) {

                String key = (String) entry.getKey();
                // Get line content.
                String[] keySplitted = key.split("\\.");
                String rule = keySplitted[0];
                String attribute = keySplitted[1];
                String value = (String) entry.getValue();

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
                } else if ("schemePattern".equals(attribute)) {
                    currentConf.setSchemePattern(value);
                } else if ("schemeRewrite".equals(attribute)) {
                    currentConf.setSchemeRewrite(value);
                } else if ("redirect".equals(attribute)) {
                    currentConf.setRedirect(Integer.valueOf(value));
                } else if ("portPattern".equals(attribute)) {
                    currentConf.setPortPattern(Integer.valueOf(value));
                } else if ("portRewrite".equals(attribute)) {
                    currentConf.setPortRewrite(Integer.valueOf(value));
                }

                validateConfiguration(rule, currentConf);

                // Save configuration instance.
                confTree.put(rule, currentConf);
            }

            // Add all instances to global configuration.
            configuration.addAll(confTree.values());
        } catch (IOException e) {
            throw new ServletException(e);
        } finally {
            if (propertiesInput != null) {
                try {
                    propertiesInput.close();
                } catch (IOException e) {
                    LOG.error("failed to close stream", e);
                }
            }
        }
    }

    /**
     * {@inheritDoc} javax.servlet.http.HttpServlet#service(javax.servlet.http. HttpServletRequest,
     * javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
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

            if (urlMatcher.matches()) {
                // Match query if necessary
                Matcher queryMatcher = null;
                if (conf.getQueryMatchPattern() != null) {
                    queryMatcher = conf.getQueryMatchPattern().matcher(getStringNotNull(request.getQueryString()));
                }

                // Match scheme if necessary
                Boolean schemeMatcher = null;
                if (conf.getSchemePattern() != null) {
                    schemeMatcher = conf.getSchemePattern().equals(request.getScheme()) ? true : false;
                }

                // Match port if necessary
                Boolean portMatcher = null;
                if (conf.getPortPattern() != null) {
                    portMatcher = conf.getPortPattern().equals(request.getServerPort()) ? true : false;
                }

                if ((queryMatcher == null || queryMatcher.matches()) && (schemeMatcher == null || schemeMatcher)
                        && (portMatcher == null || portMatcher)) {
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
                            targetQueryString = targetQueryString.replace("$" + i, queryMatcher.group(i));
                        }

                        // clear query string if empty
                        if ("".equals(targetQueryString)) {
                            targetQueryString = null;
                        }
                    } else {
                        targetQueryString = request.getQueryString();
                    }

                    // Process Scheme string
                    String targetScheme = null;
                    if (schemeMatcher != null && conf.getSchemeRewrite() != null) {
                        targetScheme = conf.getSchemeRewrite();
                    } else {
                        targetScheme = request.getScheme();
                    }

                    // Process Port
                    Integer targetPort = null;
                    if (portMatcher != null && conf.getPortRewrite() != null) {
                        targetPort = conf.getPortRewrite();
                    } else {
                        targetPort = request.getServerPort();
                    }

                    if (conf.getProvider() != null) {
                        // Proxy request and return.
                        HttpServletMediator mediator = new HttpServletMediator(new ReverseHttpRequest(request,
                                targetQueryString), response, getServletContext());
                        try {
                            // Nice log
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Proxying " + relUrl + " to " + newUrl + " w/ query " + targetQueryString);
                            }
                            DriverFactory.getInstance(conf.getProvider()).proxy(newUrl, mediator.getHttpRequest());
                            return;
                        } catch (HttpErrorPage e) {
                            mediator.sendResponse(e.getHttpResponse());
                        }
                    } else {
                        // Create target
                        String target = null;
                        String targetFile = null;
                        if (targetQueryString != null) {
                            targetFile = newUrl + "?" + targetQueryString;
                        } else {
                            targetFile = newUrl;
                        }
                        target = UriUtils.createURI(targetScheme, request.getServerName(), targetPort, targetFile,
                                null, null).toString();

                        // Redirect request and return;
                        int redirectCode = HttpServletResponse.SC_MOVED_PERMANENTLY;
                        if (conf.getRedirect() != null) {
                            redirectCode = conf.getRedirect();
                        }

                        // Nice log
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Redirecting " + relUrl + " to " + target + ". Code=" + redirectCode);
                        }
                        response.setStatus(redirectCode);
                        response.setHeader("Location", target);
                        return;
                    }
                }
            }
        }
    }

    private void validateConfiguration(String rule, ReverseConfiguration conf) {
        // validate the schemePatttern
        if (conf.getSchemePattern() != null) {
            if (!conf.getSchemePattern().matches("http|https")) {
                ConfigurationException e = new ConfigurationException("Rule : " + rule
                        + " had a none expected scheme pattern : " + conf.getSchemePattern()
                        + " expected scheme pattern : http or https");
                throw e;
            }
        }
        // validate the schemeRewrite
        if (conf.getSchemeRewrite() != null) {
            if (!conf.getSchemeRewrite().matches("http|https")) {
                ConfigurationException e = new ConfigurationException("Rule : " + rule
                        + " had a none expected rewrite scheme : " + conf.getSchemeRewrite()
                        + " expected rewrite scheme : http or https");
                throw e;
            }
        }
        // validate the redirect code
        if (conf.getRedirect() != null) {
            if (conf.getRedirect() < HttpStatus.SC_MULTIPLE_CHOICES || conf.getRedirect() > HttpStatus.SC_BAD_REQUEST) {
                ConfigurationException e = new ConfigurationException("Rule : " + rule
                        + " had a none expected redirect code range : " + conf.getRedirect()
                        + " expected range : 300-400");
                throw e;
            }
        }
    }
}
