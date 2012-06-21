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

import java.util.Properties;

import org.esigate.ConfigurationException;
import org.esigate.util.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for all ESIGate extension classes (authenticator, filters, cookie store).
 * 
 * @author Nicolas Richeton
 * @author Francois-Xavier Bonnet
 * 
 */
public class ExtensionFactory {
	private static final Logger LOG = LoggerFactory.getLogger(ExtensionFactory.class);

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
	public final static <T extends Extension> T getExtension(Properties properties, Parameter parameter, Class<T> clazz) {
		T result = null;
		String className = parameter.getValueString(properties);
		if (className == null)
			return null;
		try {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Creating  extension " + className + " as " + clazz.getName());
			}
			result = (T) Class.forName(className).newInstance();
			result.init(properties);
		} catch (InstantiationException e) {
			throw new ConfigurationException(e);
		} catch (IllegalAccessException e) {
			throw new ConfigurationException(e);
		} catch (ClassNotFoundException e) {
			throw new ConfigurationException(e);
		}
		return result;
	}
}
