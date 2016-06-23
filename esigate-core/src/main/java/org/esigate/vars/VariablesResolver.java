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

package org.esigate.vars;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.cookie.Cookie;
import org.esigate.ConfigurationException;
import org.esigate.Driver;
import org.esigate.DriverFactory;
import org.esigate.http.IncomingRequest;
import org.esigate.impl.DriverRequest;
import org.esigate.util.HttpRequestHelper;
import org.esigate.util.UriUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manage variables replacement.
 * 
 * @author Alexis Thaveau
 * @author Nicolas Richeton
 */
public final class VariablesResolver {

    private static final Logger LOG = LoggerFactory.getLogger(VariablesResolver.class);
    private static Pattern userAgentVersion = Pattern.compile("^[A-Za-z]+/([0-9]+\\.[0-9]+)");

    static {
        // Load default settings
        configure();
    }
    /**
     * Properties file
     */
    private static Properties properties;

    private VariablesResolver() {
    }

    /**
     * Loads variables from properties.
     * 
     * @param props
     *            properties
     */
    public static void configure(Properties props) {
        properties = props;
    }

    /**
     * Loads variables according to default configuration file org/esigate/vars.properties.
     */
    public static void configure() {
        InputStream inputStream = null;
        try {
            LOG.debug("Loading esigate-vars.properties file");
            inputStream = Driver.class.getResourceAsStream("/esigate-vars.properties");
            if (inputStream == null) {
                inputStream = Driver.class.getResourceAsStream("vars.properties");
            }
            if (inputStream != null) {
                properties = new Properties();
                properties.load(inputStream);
            }
        } catch (IOException e) {
            throw new ConfigurationException(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // nothing to do
                }
            }
        }
    }

    /**
     * @return The URL of the variables file.
     */
    public static URL getVariablessUrl() {
        URL varsUrl = Driver.class.getResource("/esigate-vars.properties");
        if (varsUrl == null) {
            varsUrl = Driver.class.getResource("vars.properties");
        }
        return varsUrl;
    }

    /**
     * Regexp to find variables
     */
    private static final Pattern VAR_PATTERN = Pattern.compile("\\$\\((.*?)\\)");

    /**
     * 
     * @param strVars
     *            a String that may contain variables.
     * @return true if contains variables
     */
    static boolean containsVariable(String strVars) {
        return strVars.contains("$(") && strVars.contains(")");
    }

    /**
     * Replace all ESI variables found in strVars by their matching value in vars.properties.
     * 
     * @param strVars
     *            a String containing variables.
     * @return The resulting String
     */
    public static String replaceAllVariables(String strVars) {
        return replaceAllVariables(strVars, null);
    }

    /**
     * Replace all ESI variables found in strVars by their matching value in vars.properties.
     * 
     * @param strVars
     *            a String containing variables.
     * @param request
     * @return The resulting String
     */
    public static String replaceAllVariables(String strVars, DriverRequest request) {
        String result = strVars;
        if (VariablesResolver.containsVariable(strVars)) {
            Matcher matcher = VAR_PATTERN.matcher(strVars);

            while (matcher.find()) {
                String group = matcher.group();
                String var = group.substring(2, group.length() - 1);
                String arg = null;

                // try to find argument
                int argIndex = var.indexOf('{');
                if (argIndex != -1) {
                    arg = VarUtils.removeSimpleQuotes(var.substring(argIndex + 1, var.indexOf('}')));
                }

                // try to find default value
                // ESI 1.0 spec :
                // 4.2 Variable Default Values
                // Variables whose values are empty, nonexistent variables and
                // undefined substructures of variables will evaluate to an
                // empty string when they are accessed.
                String defaultValue = StringUtils.EMPTY;
                int defaultValueIndex = var.indexOf('|');
                if (defaultValueIndex != -1) {
                    defaultValue = VarUtils.removeSimpleQuotes(var.substring(defaultValueIndex + 1));
                }

                String value = getProperty(var, arg, request);

                if (value == null) {
                    value = defaultValue;
                }

                result = result.replace(group, value);

            }

        }
        return result;
    }

    private static String getProperty(String var, String arg, DriverRequest request) {
        String result = processVar(var, arg, request);
        if (properties != null) {
            result = properties.getProperty(var, result);
        }
        LOG.debug("Resolve property $({})={}", var, result);
        return result;
    }

    private static String processVar(String var, String arg, DriverRequest request) {
        IncomingRequest incomingRequest = null;
        if (request != null) {
            incomingRequest = request.getOriginalRequest();
        }
        String res = null;
        if (var.contains("QUERY_STRING")) {
            if (arg == null) {
                res = UriUtils.getRawQuery(incomingRequest.getRequestLine().getUri());
            } else {
                res = HttpRequestHelper.getParameter(request, arg);
            }
        } else if (var.contains("HTTP_ACCEPT_LANGUAGE")) {
            String langs = HttpRequestHelper.getFirstHeader("Accept-Language", incomingRequest);
            if (arg == null) {
                res = langs;
            } else {
                res = String.valueOf(!(langs == null || !langs.contains(arg)));
            }
        } else if (var.contains("HTTP_HEADER")) {
            res = HttpRequestHelper.getFirstHeader(arg, incomingRequest);
        } else if (var.contains("HTTP_HOST")) {
            res = HttpRequestHelper.getFirstHeader("Host", incomingRequest);
        } else if (var.contains("HTTP_REFERER")) {
            res = HttpRequestHelper.getFirstHeader("Referer", incomingRequest);
        } else if (var.contains("HTTP_COOKIE")) {
            if (arg == null) {
                // Add cookies
                // In request header
                String cookieHeaderValue = StringUtils.EMPTY;
                for (Cookie c : request.getOriginalRequest().getCookies()) {
                    if (StringUtils.isNotBlank(cookieHeaderValue)) {
                        cookieHeaderValue = cookieHeaderValue + "; ";
                    }
                    cookieHeaderValue = cookieHeaderValue + c.getName() + "=" + c.getValue();
                }
                if (StringUtils.isNotBlank(cookieHeaderValue)) {
                    res = cookieHeaderValue;
                }
            } else {
                Cookie[] cookies = request.getOriginalRequest().getCookies();
                for (Cookie c : cookies) {
                    if (c.getName().equals(arg)) {
                        res = c.getValue();
                        break;
                    }
                }
            }
        } else if (var.contains("HTTP_USER_AGENT")) {
            if (arg == null) {
                res = HttpRequestHelper.getFirstHeader("User-agent", incomingRequest);
            } else {
                String userAgent =
                        StringUtils.defaultString(HttpRequestHelper.getFirstHeader("User-Agent", incomingRequest))
                                .toLowerCase();
                if (arg.equals("os")) {
                    if (userAgent.contains("unix")) {
                        res = "UNIX";
                    } else if (userAgent.contains("mac")) {
                        res = "MAC";
                    } else if (userAgent.contains("windows")) {
                        res = "WIN";
                    } else {
                        res = "OTHER";
                    }
                } else if (arg.equals("browser")) {
                    if (userAgent.contains("msie")) {
                        res = "MSIE";
                    } else {
                        res = "MOZILLA";
                    }
                } else if (arg.equals("version")) {
                    Matcher m = userAgentVersion.matcher(userAgent);

                    if (m.find()) {
                        res = m.group(1);
                    }
                }
            }
        } else if (var.contains("PROVIDER")) {
            String providerUrl = StringUtils.EMPTY;
            try {
                Driver driver = DriverFactory.getInstance(arg);
                providerUrl =
                        driver.getConfiguration().getBaseUrlRetrieveStrategy().getBaseURL(request.getOriginalRequest());
            } catch (Exception e) {
                // No driver available for this id.
            }

            return providerUrl;

        }
        return res;
    }
}
