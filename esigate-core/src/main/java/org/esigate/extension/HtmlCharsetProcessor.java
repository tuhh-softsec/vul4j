package org.esigate.extension;

import java.nio.charset.Charset;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.esigate.Driver;
import org.esigate.events.Event;
import org.esigate.events.EventDefinition;
import org.esigate.events.EventManager;
import org.esigate.events.IEventListener;
import org.esigate.events.impl.EncodingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtmlCharsetProcessor implements Extension, IEventListener {
	private static final Logger LOG = LoggerFactory
			.getLogger(DefaultCharset.class);

	private final static Pattern PATTERN_META_HTML5 = Pattern.compile(
			".*<head>.*<meta[^>]+charset=\"([^>^\"]+)\"[^>]*/?>.*</head>.*",
			Pattern.CASE_INSENSITIVE);
	private final static Pattern PATTERN_META_HTML4_XHTML = Pattern.compile(
			".*<head>.*<meta[^>]+charset=([^>^\"]+)\"[^>]*/?>.*</head>.*",
			Pattern.CASE_INSENSITIVE);

	public boolean event(EventDefinition id, Event event) {
		EncodingEvent encodingEvent = (EncodingEvent) event;
		Charset charset = null;

		LOG.debug("Content mime type is {}", encodingEvent.mimeType);

		// Detect on supported MIME types.
		if ("text/html".equals(encodingEvent.mimeType)
				|| "application/xhtml+xml".equals(encodingEvent.mimeType)) {
			LOG.debug("Supported MIME type, parsing content");

			Matcher m = PATTERN_META_HTML5.matcher(encodingEvent.entityContent);
			if (m.matches()) {
				LOG.debug("Found HTML5 charset");
				charset = Charset.forName(m.group(1));
			}

			m = PATTERN_META_HTML4_XHTML.matcher(encodingEvent.entityContent);
			if (m.matches()) {
				LOG.debug("Found HTML/XHTML charset");
				charset = Charset.forName(m.group(1));
			}
		}

		// If another charset was found, update String object
		if (charset != null && !charset.equals(encodingEvent.charset)) {
			LOG.debug("Changing charset fom {} to {}", encodingEvent.charset,
					charset);
			encodingEvent.charset = charset;
			encodingEvent.entityContent = new String(encodingEvent.rawEntityContent,
					encodingEvent.charset);
		}

		return true;
	}

	public void init(Driver driver, Properties properties) {
		driver.getEventManager().register(EventManager.EVENT_ENCODING, this);
	}

}
