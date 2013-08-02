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
package org.esigate.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.esigate.ConfigurationException;

/**
 * UriMapping holds provider mapping definition.
 * 
 * <p>
 * Mappings are based on (all optional) :
 * <ul>
 * <li>A Scheme, host and port</li>
 * <li>a starting path</li>
 * <li>an extension</li>
 * </ul>
 * <p>
 * Default (all values set to null) mappings are allowed.
 * 
 * @author Francois-Xavier Bonnet
 * @author Nicolas Richeton
 * 
 */
public class UriMapping {
	private final static Pattern MAPPING_PATTERN = Pattern
			.compile("((http://|https://)[^/:]*(:([0-9]*))?)?(/[^*]*)?(\\*?)([^*]*)");
	private final String host;
	private final String path;
	private final String extension;
	private final int weight;

	/**
	 * Creates UriMapping object and compute its weight.
	 * 
	 * @param host
	 * @param path
	 * @param extension
	 */
	private UriMapping(String host, String path, String extension) {
		this.host = host;
		this.path = path;
		this.extension = extension;
		int targetWeight = 0;
		if (this.host != null)
			targetWeight += 1000;
		if (this.path != null)
			targetWeight += this.path.length() * 10;
		if (this.extension != null)
			targetWeight += this.extension.length();
		this.weight = targetWeight;
	}

	/**
	 * Creates a UriMapping instance based on the mapping definition given as
	 * parameter.
	 * <p>
	 * Mapping is split in 3 parts :
	 * <ul>
	 * <li>Host, including the scheme and port : http://www.example:8080</li>
	 * <li>path, left part before the wildcard caracter *</li>
	 * <li>extension, right part after the wildcard caracter *</li>
	 * </ul>
	 * 
	 * @param mapping
	 *            the mapping expression as string
	 * @return the uri mapping object
	 * @throws ConfigurationException
	 */
	public static UriMapping create(String mapping) throws ConfigurationException {
		Matcher matcher = MAPPING_PATTERN.matcher(mapping);
		if (!matcher.matches())
			throw new ConfigurationException("Unrecognized URI pattern: " + mapping);
		String host = StringUtils.trimToNull(matcher.group(1));
		String path = StringUtils.trimToNull(matcher.group(5));

		if (path != null && !path.startsWith("/"))
			throw new ConfigurationException("Unrecognized URI pattern: " + mapping
					+ " Mapping path should start with / was: " + path);
		String extension = StringUtils.trimToNull(matcher.group(7));
		if (extension != null && !extension.startsWith("."))
			throw new ConfigurationException("Unrecognized URI pattern: " + mapping
					+ " Mapping extension should start with . was: " + extension);
		return new UriMapping(host, path, extension);
	}

	/**
	 * Check this matching rule against a request.
	 * 
	 * @param schemeParam
	 * @param hostParam
	 * @param uriParam
	 * @return true if the rule matches the request
	 */
	public boolean matches(String schemeParam, String hostParam, String uriParam) {
		// If URI mapping enforce a host, ensure it is the one used.
		if (this.host != null && !this.host.equalsIgnoreCase(schemeParam + "://" + hostParam)) {
			return false;
		}

		if (this.extension != null && !uriParam.endsWith(this.extension)) {
			return false;
		}

		if (this.path != null && !uriParam.startsWith(this.path)) {
			return false;
		}
		return true;
	}

	/**
	 * The weight of this URI matching. Larger weights must be evaluated first.
	 * 
	 * @return the weight
	 */
	public int getWeight() {
		return this.weight;
	}

	/**
	 * Get the extension of this URI matching.
	 * 
	 * @return the extension
	 */
	public String getExtension() {
		return this.extension;
	}

	/**
	 * Get the path of this URI matching.
	 * 
	 * @return the path
	 */
	public String getPath() {
		return this.path;
	}

	/**
	 * Get the host of this URI matching.
	 * 
	 * @return the host
	 */
	public String getHost() {
		return this.host;
	}
}
