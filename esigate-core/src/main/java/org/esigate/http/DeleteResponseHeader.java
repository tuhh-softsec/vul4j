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

package org.esigate.http;

import org.esigate.events.Event;
import org.esigate.events.EventDefinition;
import org.esigate.events.IEventListener;
import org.esigate.events.impl.ProxyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Proxy event listener which deletes an header.
 * <p>
 * Delete from both response and error page.
 * 
 * @author Nicolas Richeton
 * 
 */
public class DeleteResponseHeader implements IEventListener {
    private static final Logger LOG = LoggerFactory.getLogger(DeleteResponseHeader.class);

    private final String name;

    /**
     * Delete header 'name'.
     * 
     * @param name
     *            name of header to delete.
     */
    public DeleteResponseHeader(String name) {
        this.name = name;
    }

    @Override
    public boolean event(EventDefinition id, Event event) {
        ProxyEvent fEvent = (ProxyEvent) event;
        if (fEvent.getResponse() != null && fEvent.getResponse().containsHeader(this.name)) {
            LOG.info("Deleting header {} ", this.name);
            fEvent.getResponse().removeHeaders(this.name);
        }
        if (fEvent.getErrorPage() != null && fEvent.getErrorPage().getHttpResponse() != null
                && fEvent.getErrorPage().getHttpResponse().containsHeader(this.name)) {
            LOG.info("Deleting header {} ", this.name);
            fEvent.getErrorPage().getHttpResponse().removeHeaders(this.name);
        }

        return true;
    }

}
