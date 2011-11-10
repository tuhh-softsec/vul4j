package org.esigate.esi;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Tag {
	private final static Pattern TAG_NAME_PATTERN = Pattern.compile("<([\\S]*)\\s");
	private final static Pattern ATTRIBUTE_PATTERN = Pattern.compile("\\s([\\S]*)[\\s]*=[\\s]*([\"|'])([^'\"]*)\\2");
	private final static Pattern CLOSE_TAG = Pattern.compile("\\A</");
	private final static Pattern AUTO_CLOSE_TAG = Pattern.compile("/[\\s]*>\\z");

	private final String name;
	private final Map<String, String> attributes;
	private final boolean closing;
	private final boolean openClosed;

	public static Tag create(String tag) {
		Matcher nameMatcher = TAG_NAME_PATTERN.matcher(tag);
		String name = (nameMatcher.find()) ? nameMatcher.group(1) : null;

		Map<String, String> attributes = new HashMap<String, String>();
		Matcher attributesMatcher = ATTRIBUTE_PATTERN.matcher(tag);
		while (attributesMatcher.find()) {
			attributes.put(attributesMatcher.group(1), attributesMatcher.group(3));
		}
		boolean closing = CLOSE_TAG.matcher(tag).find();
		boolean openClosed = AUTO_CLOSE_TAG.matcher(tag).find();

		return new Tag(name, closing, openClosed, attributes);
	}

	Tag(String name, boolean closing, boolean openClosed, Map<String, String> attributes) {
		this.name = name;
		this.closing = closing;
		this.openClosed = openClosed;
		this.attributes = attributes;
	}

	public String getName() {
		return name;
	}

	public String getAttribute(String name) {
		return attributes.get(name);
	}

	public boolean isClosing() {
		return closing;
	}

	public boolean isOpenClosed() {
		return openClosed;
	}

}
