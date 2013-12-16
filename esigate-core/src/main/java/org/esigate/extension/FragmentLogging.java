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

import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.client.cache.CacheResponseStatus;
import org.apache.http.impl.client.cache.CachingHttpClient;
import org.apache.http.protocol.ExecutionContext;
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
 * Be sure to put this extension as the first extension in order to log the whole fragment processing time, including
 * all other extension processing.
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
    private static final Logger LOG = LoggerFactory.getLogger(FragmentLogging.class);
    private Driver driver;

    @Override
    public void init(Driver driver, Properties properties) {
        this.driver = driver;
        driver.getEventManager().register(EventManager.EVENT_FRAGMENT_POST, this);
        driver.getEventManager().register(EventManager.EVENT_FRAGMENT_PRE, this);
    }

    @Override
    public boolean event(EventDefinition id, Event event) {

        FragmentEvent e = (FragmentEvent) event;

        if (EventManager.EVENT_FRAGMENT_PRE.equals(id)) {
            // Keep track of the start time.
            e.httpContext.setAttribute(TIME, System.currentTimeMillis());
        } else {
            int statusCode = e.httpResponse.getStatusLine().getStatusCode();

            // Log only if info or issue
            if (LOG.isInfoEnabled() || statusCode >= HttpStatus.SC_BAD_REQUEST) {

                // Log last result only
                HttpRequest lastRequest = RedirectStrategy.getLastRequest(e.httpRequest, e.httpContext);

                // Create log message
                HttpHost targetHost = (HttpHost) e.httpContext.getAttribute(ExecutionContext.HTTP_TARGET_HOST);

                String requestLine = lastRequest.getRequestLine().toString();
                String statusLine = e.httpResponse.getStatusLine().toString();

                String reqHeaders = ArrayUtils.toString(lastRequest.getAllHeaders());
                String respHeaders = ArrayUtils.toString(e.httpResponse.getAllHeaders());

                String cache = "";
                CacheResponseStatus cacheResponseStatus = (CacheResponseStatus) e.httpContext
                        .getAttribute(CachingHttpClient.CACHE_RESPONSE_STATUS);
                if (cacheResponseStatus != null) {
                    cache = cacheResponseStatus.toString();
                }

                long time = System.currentTimeMillis() - (Long) e.httpContext.removeAttribute(TIME);

                StringBuilder logMessage = new StringBuilder();
                logMessage.append(driver.getConfiguration().getInstanceName());
                logMessage.append(" ");
                // Display target host, protocol and port
                if (targetHost != null) {
                    logMessage.append(targetHost.getSchemeName());
                    logMessage.append("://");
                    logMessage.append(targetHost.getHostName());

                    if (targetHost.getPort() != -1) {
                        logMessage.append(":");
                        logMessage.append(targetHost.getPort());
                    }

                    logMessage.append(" - ");
                }

                logMessage.append(requestLine + " " + reqHeaders + " -> " + statusLine + " (" + time + " ms) " + cache
                        + " " + respHeaders);

                // Log level according to status code.
                if (statusCode >= HttpStatus.SC_BAD_REQUEST) {
                    LOG.warn(logMessage.toString());
                } else {
                    LOG.info(logMessage.toString());
                }
            }
        }

        // Continue processing
        return true;
    }

}
