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
package org.esigate.extension;

import java.util.HashMap;


import org.esigate.ConfigurationException;
import org.esigate.DriverConfiguration;
import org.esigate.authentication.AuthenticationHandler;
import org.esigate.cookie.CustomCookieStore;
import org.esigate.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for all ESIGate extension classes (authenticator, filters, cookie store).
 * 
 * @author Nicolas Richeton
 * 
 */
public class ExtensionFactory {
	private static final Logger LOG = LoggerFactory.getLogger(ExtensionFactory.class);

	private static final String EXTENSION_WAT_FILTER = Filter.class.getName();
	private static final String EXTENSION_COOKIE_STORE = CustomCookieStore.class
			.getName();
	private static final String EXTENSION_AUTHENTIFICATION_HANDLER = AuthenticationHandler.class
			.getName();

	// Store class name for each Extension (Filter,Cookie,AuthenticationHandler)
	private final HashMap<String, String> classes = new HashMap<String, String>();

	private final DriverConfiguration conf;

	/**
	 * Build an extension factory using the given properties. These properties
	 * will be forwarded to each extension in
	 * {@link Extension#init(java.util.Properties)}.
	 * 
	 * @param conf
	 */
	public ExtensionFactory(DriverConfiguration conf) {
		this.conf = conf;
		classes.put(EXTENSION_COOKIE_STORE, conf.getCookieStore());
		classes.put(EXTENSION_AUTHENTIFICATION_HANDLER, conf
				.getAuthenticationHandler());
		classes.put(EXTENSION_WAT_FILTER, conf.getFilter());
	}

	/**
	 * Get an extension using its interface.
	 * 
	 * @param <T>
	 *            class which extends Extension
	 * @param clazz
	 *            class which extends Extension
	 * @return instance of {@link Extension} or null.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Extension> T getExtension(Class<T> clazz) {
		T result = null;

		String className = classes.get(clazz.getName());

		if (className != null) {
			try {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Creating  " + className + " as "
							+ clazz.getName());
				}
				result = (T) Class.forName(className).newInstance();
				result.init(conf.getProperties());
			} catch (InstantiationException e) {
				throw new ConfigurationException(e);
			} catch (IllegalAccessException e) {
				throw new ConfigurationException(e);
			} catch (ClassNotFoundException e) {
				throw new ConfigurationException(e);
			}
		}
		return result;
	}
}
