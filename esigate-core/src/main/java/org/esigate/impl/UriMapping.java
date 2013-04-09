package org.esigate.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.ConfigurationException;

class UriMapping {
	private final static Pattern MAPPING_PATTERN = Pattern.compile("((http://|https://)[^/:]*(:([0-9]*))?)?(/[^*]*)?(\\*?)([^*]*)");
	final String host;
	final String path;
	final String extension;
	final int weight;

	private UriMapping(String host, String path, String extension) {
		this.host = host;
		this.path = path;
		this.extension = extension;
		int weight = 0;
		if (this.host != null)
			weight += 1000;
		if (this.path != null)
			weight += this.path.length() * 10;
		if (this.extension != null)
			weight += this.extension.length();
		this.weight = weight;
	}

	static UriMapping create(String mapping) throws ConfigurationException {
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
			throw new ConfigurationException("Unrecognized URI pattern: " + mapping + " Mapping path should start with / was: " + path);
		String extension = matcher.group(7);
		if ("".equals(extension))
			extension = null;
		if (extension != null && !extension.startsWith("."))
			throw new ConfigurationException("Unrecognized URI pattern: " + mapping + " Mapping extension should start with . was: " + extension);
		return new UriMapping(host, path, extension);
	}

	boolean matches(String uri) {
		// TODO
		return false;
	}

}
