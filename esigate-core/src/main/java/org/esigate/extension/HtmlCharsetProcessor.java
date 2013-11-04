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
import org.esigate.events.impl.ReadEntityEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This extension reads html and xhtml documents, and ensure document has been read using the right charset. This
 * prevents charset issues when the remote server provides a wrong charset or no charset at all in HTTP headers even if
 * document is not ISO-8859-1 (the default).
 * 
 * <p>
 * To be processed by this extension, documents must have one of the following MIME types:
 * <ul>
 * <li>text/html</li>
 * <li>application/xhtml+xml</li>
 * </ul>
 * ... and this MIME type must be declared as parsableContentTypes in configuration file (esigate.properties).
 * 
 * @see <a href="http://www.esigate.org/reference.html#Configuration_file">Configuration file</a>
 * 
 * @author Nicolas Richeton
 * 
 */
public class HtmlCharsetProcessor implements Extension, IEventListener {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultCharset.class);

    private static final Pattern PATTERN_META_HTML5 = Pattern.compile(
            ".*<head>.*<meta[^>]+charset=\"([^>^\"]+)\"[^>]*/?>.*</head>.*", Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTERN_META_HTML4_XHTML = Pattern.compile(
            ".*<head>.*<meta[^>]+charset=([^>^\"]+)\"[^>]*/?>.*</head>.*", Pattern.CASE_INSENSITIVE);

    @Override
    public boolean event(EventDefinition id, Event event) {
        ReadEntityEvent readEntityEvent = (ReadEntityEvent) event;
        Charset charset = null;

        LOG.debug("Content mime type is {}", readEntityEvent.mimeType);

        // Detect on supported MIME types.
        // ReadEntityEvent is only sent when esigate tries to parse a document.
        if ("text/html".equals(readEntityEvent.mimeType) || "application/xhtml+xml".equals(readEntityEvent.mimeType)) {
            LOG.debug("Supported MIME type, parsing content");

            Matcher m = PATTERN_META_HTML5.matcher(readEntityEvent.entityContent);
            if (m.matches()) {
                LOG.debug("Found HTML5 charset");
                charset = Charset.forName(m.group(1));
            }

            m = PATTERN_META_HTML4_XHTML.matcher(readEntityEvent.entityContent);
            if (m.matches()) {
                LOG.debug("Found HTML/XHTML charset");
                charset = Charset.forName(m.group(1));
            }
        }

        // If another charset was found, update String object
        if (charset != null && !charset.equals(readEntityEvent.charset)) {
            LOG.debug("Changing charset fom {} to {}", readEntityEvent.charset, charset);
            readEntityEvent.charset = charset;
            readEntityEvent.entityContent = new String(readEntityEvent.rawEntityContent, readEntityEvent.charset);
        }

        return true;
    }

    @Override
    public void init(Driver driver, Properties properties) {
        driver.getEventManager().register(EventManager.EVENT_READ_ENTITY, this);
    }

}
