package org.esigate.extension;

import java.util.Properties;

import org.apache.commons.lang3.ArrayUtils;
import org.esigate.Driver;
import org.esigate.events.Event;
import org.esigate.events.EventDefinition;
import org.esigate.events.EventManager;
import org.esigate.events.IEventListener;
import org.esigate.events.impl.FetchEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This extension logs requests to remote systems.
 * <p>
 * Be sure to put this extension as the first extension in order to log the
 * whole request time, including all extension processing.
 * 
 * <p>
 * Log level is :
 * <ul>
 * <li>WARN for status codes >= 400</li>
 * <li>INFO for other codes</li>
 * </ul>
 * <p>
 * Logged data are :
 * 
 * <ul>
 * <li>Request status line</li>
 * <li>Request headers</li>
 * <li>Response status line</li>
 * <li>Response headers</li>
 * <li>Cache status (HIT, MISS, ...)</li>
 * <li>Request time</li>
 * 
 * </ul>
 * 
 * 
 * @author Nicolas Richeton
 * 
 */
public class FetchLogging implements Extension, IEventListener {
	private static final String TIME = "org.esigate.time.external";
	private static final Logger LOG = LoggerFactory
			.getLogger(FetchLogging.class);

	public void init(Driver driver, Properties properties) {
		driver.getEventManager().register(EventManager.EVENT_FETCH_POST, this);
		driver.getEventManager().register(EventManager.EVENT_FETCH_PRE, this);
	}

	public boolean event(EventDefinition id, Event event) {

		FetchEvent e = (FetchEvent) event;

		if (EventManager.EVENT_FETCH_POST.equals(id)) {
			int statusCode = e.httpResponse.getStatusLine().getStatusCode();

			// Log only if info or issue 
			if (LOG.isInfoEnabled() || statusCode >= 400) {
				org.apache.http.HttpRequest lastRequest =e.httpRequest;

				String url = lastRequest.getRequestLine().toString();
				String status = e.httpResponse.getStatusLine().toString();

				String reqHeaders = ArrayUtils.toString(lastRequest
						.getAllHeaders());
				String respHeaders = ArrayUtils.toString(e.httpResponse
						.getAllHeaders());

				long time = System.currentTimeMillis()
						- (Long) e.httpContext.removeAttribute(TIME);

				String logMessage = url + " " + reqHeaders + " -> " + status
						+ " (" + time + " ms) "  + " " + respHeaders;

				if (statusCode >= 400)
					LOG.warn(logMessage);
				else
					LOG.info(logMessage);
			}
		} else {
			e.httpContext.setAttribute(TIME, System.currentTimeMillis());
		}

		// Continue processing
		return true;
	}

}
