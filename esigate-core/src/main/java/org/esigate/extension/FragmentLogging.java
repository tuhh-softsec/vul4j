package org.esigate.extension;

import java.util.Properties;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.client.cache.CacheResponseStatus;
import org.apache.http.impl.client.cache.CachingHttpClient;
import org.esigate.Driver;
import org.esigate.events.Event;
import org.esigate.events.EventDefinition;
import org.esigate.events.EventManager;
import org.esigate.events.IEventListener;
import org.esigate.events.impl.FragmentEvent;
import org.esigate.http.RedirectStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This extension logs fragments usage.
 * <p>
 * Be sure to put this extension as the first extension in order to log the
 * whole fragment processing time, including all other extension processing.
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
 * <li>Provider name</li>
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
public class FragmentLogging implements Extension, IEventListener {
	private static final String TIME = "org.esigate.time";
	private static final Logger LOG = LoggerFactory
			.getLogger(FragmentLogging.class);
	private Driver driver;

	public void init(Driver driver, Properties properties) {
		this.driver = driver;
		driver.getEventManager().register(EventManager.EVENT_FRAGMENT_POST,
				this);
		driver.getEventManager().register(EventManager.EVENT_FRAGMENT_PRE,
				this); 
	}

	public boolean event(EventDefinition id, Event event) {

		FragmentEvent e = (FragmentEvent) event;

		if (EventManager.EVENT_FRAGMENT_PRE.equals(id)) {
			// Keep track of the start time.
			e.httpContext.setAttribute(TIME, System.currentTimeMillis());
		} else {
			int statusCode = e.httpResponse.getStatusLine().getStatusCode();

			// Log only if info or issue
			if (LOG.isInfoEnabled() || statusCode >= 400) {
				
				// Log last result only
				org.apache.http.HttpRequest lastRequest = RedirectStrategy
						.getLastRequest(e.httpRequest, e.httpContext);

				// Create log message
				String requestLine = lastRequest.getRequestLine().toString();
				String statusLine = e.httpResponse.getStatusLine().toString();

				String reqHeaders = ArrayUtils.toString(lastRequest
						.getAllHeaders());
				String respHeaders = ArrayUtils.toString(e.httpResponse
						.getAllHeaders());

				String cache = "";
				CacheResponseStatus cacheResponseStatus = (CacheResponseStatus) e.httpContext
						.getAttribute(CachingHttpClient.CACHE_RESPONSE_STATUS);
				if (cacheResponseStatus != null) {
					cache = cacheResponseStatus.toString();
				}

				long time = System.currentTimeMillis()
						- (Long) e.httpContext.removeAttribute(TIME);

				String logMessage = driver.getConfiguration().getInstanceName()
						+ " " + requestLine + " " + reqHeaders + " -> "
						+ statusLine + " (" + time + " ms) " + cache + " "
						+ respHeaders;

				// Log level according to status code.
				if (statusCode >= 400)
					LOG.warn(logMessage);
				else
					LOG.info(logMessage);
			}
		}

		// Continue processing
		return true;
	}

}
