package org.esigate.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.esigate.ConfigurationException;

public class UriMapping {
	private final static Pattern MAPPING_PATTERN = Pattern
			.compile("((http://|https://)[^/:]*(:([0-9]*))?)?(/[^*]*)?(\\*?)([^*]*)");
	private final String host;
	private final String path;
	private final String extension;
	private final int weight;

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
	 * @return
	 * @throws ConfigurationException
	 */
	public static UriMapping create(String mapping) throws ConfigurationException {
		Matcher matcher = MAPPING_PATTERN.matcher(mapping);
		if (!matcher.matches())
			throw new ConfigurationException("Unrecognized URI pattern: " + mapping);
		String host = matcher.group(1);
		if ("".equals(host))
			host = null;
		String path = matcher.group(5);
		if ("".equals(path))
			path = null;
		if (path != null && !path.startsWith("/"))
			throw new ConfigurationException("Unrecognized URI pattern: " + mapping
					+ " Mapping path should start with / was: " + path);
		String extension = matcher.group(7);
		if ("".equals(extension))
			extension = null;
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
	 * @return
	 */
	public int getWeight() {
		return this.weight;
	}

	public String getExtension() {
		return this.extension;
	}

	public String getPath() {
		return this.path;
	}

	public String getHost() {
		return this.host;
	}
}
