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
package net.webassembletool.vars;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.webassembletool.ConfigurationException;
import net.webassembletool.Driver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Manage variables replacement
 * 
 * @author Alexis Thaveau
 */
public class VariablesResolver {

	private static final Log LOG = LogFactory.getLog(VariablesResolver.class);

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
	 * Loads variables according to default configuration file
	 * net/webassembletool/vars.properties.
	 */
	public final static void configure() {
		InputStream inputStream = null;
		try {
			inputStream = Driver.class.getResourceAsStream("vars.properties");
			URL url = Driver.class.getResource("vars.properties");
			if (LOG.isDebugEnabled()) {
				LOG.debug("Loading vars.properties file " + url);
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
	 * Replace all ESI variables found in strVars by their matching value in
	 * vars.properties
	 * 
	 * @param strVars
	 *            a String containing variables.
	 * @return The resulting String
	 */
	public static String replaceAllVariables(String strVars) {
		String result = strVars;
		if (VariablesResolver.containsVariable(strVars)) {
			Matcher matcher = VAR_PATTERN.matcher(strVars);

			while (matcher.find()) {
				String group = matcher.group();
				String var = group.substring(2, group.length() - 1);
				String value = getProperty(var);
				if (value != null) {
					result = result.replace(group, value);
				}
			}

		}
		return result;
	}

	private static String getProperty(String var) {
		String result = null;
		if (properties != null) {
			result = properties.getProperty(var, null);
		}
		LOG.debug("Resolve property $(" + var + ")=" + result);
		return result;
	}
}
