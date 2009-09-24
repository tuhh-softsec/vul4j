package net.webassembletool.esi;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tag {
	private final static Pattern TAG_NAME_PATTERN = Pattern
			.compile("<([\\S]*)\\s");
	private final static Pattern ATTRIBUTE_PATTERN = Pattern
			.compile("\\s([\\S]*)[\\s]*=[\\s]*([\"|'])([^'\"]*)\\2");
	private final static Pattern CLOSE_TAG = Pattern.compile("\\A</");
	private final static Pattern AUTO_CLOSE_TAG = Pattern
			.compile("/[\\s]*>\\z");

	private final String name;
	private final Map<String, String> attributes = new HashMap<String, String>();
	private final boolean closing;
	private final boolean openClosed;

	public Tag(String tag) {
		Matcher matcher = TAG_NAME_PATTERN.matcher(tag);
		if (matcher.find())
			name = matcher.group(1);
		else
			name = null;
		matcher = ATTRIBUTE_PATTERN.matcher(tag);
		while (matcher.find()) {
			attributes.put(matcher.group(1), matcher.group(3));
		}
		closing = CLOSE_TAG.matcher(tag).find();
		openClosed = AUTO_CLOSE_TAG.matcher(tag).find();
	}

	public String getName() {
		return name;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public boolean isClosing() {
		return closing;
	}

	public boolean isOpenClosed() {
		return openClosed;
	}

}
