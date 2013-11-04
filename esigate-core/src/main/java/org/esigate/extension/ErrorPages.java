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

import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.esigate.Driver;
import org.esigate.events.Event;
import org.esigate.events.EventDefinition;
import org.esigate.events.EventManager;
import org.esigate.events.IEventListener;
import org.esigate.events.impl.FragmentEvent;
import org.esigate.test.http.HttpResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This extension handles internal error pages served directly by esigate.
 * <p>
 * Supported pages :
 * <ul>
 * <li>http://esigate/no-mapping/</li>
 * </ul>
 * 
 * @author Nicolas Richeton
 * 
 */
public class ErrorPages implements Extension, IEventListener {

    private static final StringEntity NO_MAPPING = new StringEntity(
            "<html><body><h1>Esigate cannot process this request.</h1>"
                    + "<h2>No mapping defined for this url.</h2></body></html>", ContentType.create("text/html",
                    "utf-8"));
    private static final Logger LOG = LoggerFactory.getLogger(ErrorPages.class);

    @Override
    public void init(Driver driver, Properties properties) {
        driver.getEventManager().register(EventManager.EVENT_FRAGMENT_PRE, this);
    }

    @Override
    public boolean event(EventDefinition id, Event event) {

        FragmentEvent e = (FragmentEvent) event;

        if (EventManager.EVENT_FRAGMENT_PRE.equals(id)) {
            LOG.error(e.httpRequest.getRequestLine().getUri());

            if ("esigate".equals(((HttpHost) e.httpRequest.getParams().getParameter("TARGET_HOST")).getHostName())) {
                if ("http://esigate/no-mapping/".equals(e.httpRequest.getRequestLine().getUri())) {
                    e.httpResponse = new HttpResponseBuilder().status(HttpStatus.SC_NOT_FOUND)
                            .reason("No mapping defined").entity(NO_MAPPING).build();
                    return false;
                }
            }
        }

        // Continue processing
        return true;
    }
}
