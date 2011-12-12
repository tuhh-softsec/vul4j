/*
 *  Copyright 2010 altha.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package org.esigate.vars;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.esigate.ConfigurationException;
import org.esigate.Driver;
import org.esigate.api.Cookie;
import org.esigate.api.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manage variables replacement
 * 
 * @author Alexis Thaveau
 */
public class VariablesResolver {

	private static final Logger LOG = LoggerFactory.getLogger(VariablesResolver.class);

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
	 * Loads variables according to default configuration file org/esigate/vars.properties.
	 */
	public final static void configure() {
		InputStream inputStream = null;
		try {
			LOG.debug("Loading vars.properties file {}", Driver.class.getResource("vars.properties"));
			inputStream = Driver.class.getResourceAsStream("vars.properties");
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
	 * Regexp to find variables
	 */
	private static Pattern VAR_PATTERN = Pattern.compile("\\$\\((.*?)\\)");

	/**
	 * 
	 * @param strVars
	 *            a String that may contain variables.
	 * @return
	 */
	/* package */static boolean containsVariable(String strVars) {
		return strVars.contains("$(") && strVars.contains(")");
	}

	/**
	 * Replace all ESI variables found in strVars by their matching value in vars.properties
	 * 
	 * @param strVars
	 *            a String containing variables.
	 * @return The resulting String
	 */
	public static String replaceAllVariables(String strVars) {
		return replaceAllVariables(strVars, null);
	}

	/**
	 * Replace all ESI variables found in strVars by their matching value in vars.properties
	 * 
	 * @param strVars
	 *            a String containing variables.
	 * @return The resulting String
	 */
	public static String replaceAllVariables(String strVars, HttpRequest request) {
		String result = strVars;
		if (VariablesResolver.containsVariable(strVars)) {
			Matcher matcher = VAR_PATTERN.matcher(strVars);

			while (matcher.find()) {
				String group = matcher.group();
				String var = group.substring(2, group.length() - 1);
				String arg = null;
				// try to find argument
				try {
					arg = var.substring(var.indexOf('{') + 1, var.indexOf('}'));
				} catch (Exception e) {
				}

				String value = getProperty(var, arg, request);
				if (value != null) {
					result = result.replace(group, value);
				}
			}

		}
		return result;
	}

	private static String getProperty(String var, String arg, HttpRequest request) {
		String result = processVar(var, arg, request);
		if (properties != null) {
			result = properties.getProperty(var, result);
		}
		LOG.debug("Resolve property $(" + var + ")=" + result);
		return result;
	}

	private static String processVar(String var, String arg, HttpRequest request) {
		String res = null;
		if (var.indexOf("QUERY_STRING") != -1) {
			if (arg == null) {
				res = request.getQueryString();
			} else {
				res = request.getParameter(arg);
			}
		} else if (var.indexOf("HTTP_ACCEPT_LANGUAGE") != -1) {
			String langs = request.getHeader("Accept-Language");
			if (arg == null) {
				res = langs;
			} else if (langs.indexOf(arg) == -1) {
				res = "false";
			} else {
				res = "true";
			}
		} else if (var.indexOf("HTTP_HOST") != -1) {
			res = request.getHeader("Host");
		} else if (var.indexOf("HTTP_REFERER") != -1) {
			res = request.getHeader("Referer");
		} else if (var.indexOf("HTTP_COOKIE") != -1) {
			if (arg == null) {
				res = request.getHeader("Cookies");
			} else {
				Cookie[] cookies = request.getCookies();
				for (Cookie c : cookies) {
					if (c.getName().equals(arg)) {
						res = c.getValue();
						break;
					}
				}
			}
		} else if (var.indexOf("HTTP_USER_AGENT") != -1) {
			if (arg == null) {
				res = request.getHeader("User-agent");
			} else {
				String userAgent = request.getHeader("User-Agent").toLowerCase();
				if (arg.equals("os")) {
					if (userAgent.indexOf("unix") != -1) {
						res = "UNIX";
					} else if (userAgent.indexOf("mac") != -1) {
						res = "MAC";
					} else if (userAgent.indexOf("windows") != -1) {
						res = "WIN";
					} else {
						res = "OTHER";
					}
				} else if (arg.equals("browser")) {
					if (userAgent.indexOf("msie") != -1) {
						res = "MSIE";
					} else {
						res = "MOZILLA";
					}
				}
			}
		}
		return res;
	}

}
