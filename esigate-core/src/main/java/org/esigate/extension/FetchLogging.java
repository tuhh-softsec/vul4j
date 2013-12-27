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
import org.esigate.Driver;
import org.esigate.Parameters;
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
 * Be sure to put this extension as the first extension in order to log the whole request time, including all extension
 * processing.
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
    private static final Logger LOG = LoggerFactory.getLogger(FetchLogging.class);

    @Override
    public void init(Driver driver, Properties properties) {
        driver.getEventManager().register(EventManager.EVENT_FETCH_POST, this);
        driver.getEventManager().register(EventManager.EVENT_FETCH_PRE, this);
    }

    @Override
    public boolean event(EventDefinition id, Event event) {

        FetchEvent e = (FetchEvent) event;

        if (EventManager.EVENT_FETCH_POST.equals(id)) {
            int statusCode = e.getHttpResponse().getStatusLine().getStatusCode();

            // Log only if info or issue
            if (LOG.isInfoEnabled() || statusCode >= HttpStatus.SC_BAD_REQUEST) {
                HttpRequest lastRequest = e.getHttpRequest();

                String url = lastRequest.getRequestLine().toString();
                String status = e.getHttpResponse().getStatusLine().toString();

                String reqHeaders = ArrayUtils.toString(lastRequest.getAllHeaders());
                String respHeaders = ArrayUtils.toString(e.getHttpResponse().getAllHeaders());

                HttpHost targetHost = e.getHttpContext().getTargetHost();

                long time = System.currentTimeMillis() - (Long) e.getHttpContext().removeAttribute(TIME);

                StringBuilder logMessage = new StringBuilder(Parameters.SMALL_BUFFER_SIZE);

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
                // Url
                logMessage.append(url);
                logMessage.append(" ");

                // request headers
                logMessage.append(reqHeaders);

                // Response status
                logMessage.append(" -> ");
                logMessage.append(status);

                // Time
                logMessage.append(" (");
                logMessage.append(time);
                logMessage.append(" ms) ");

                // Response headers
                logMessage.append(respHeaders);

                if (statusCode >= HttpStatus.SC_BAD_REQUEST) {
                    LOG.warn(logMessage.toString());
                } else {
                    LOG.info(logMessage.toString());
                }
            }
        } else {
            e.getHttpContext().setAttribute(TIME, System.currentTimeMillis());
        }

        // Continue processing
        return true;
    }

}
