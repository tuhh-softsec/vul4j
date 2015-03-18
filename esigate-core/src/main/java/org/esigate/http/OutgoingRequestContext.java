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

import java.util.LinkedList;
import java.util.Queue;

import org.apache.http.HttpHost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.protocol.HttpContext;

public class OutgoingRequestContext extends HttpClientContext {
    private static final String PROXY = "PROXY";
    private static final String OUTGOING_REQUEST = "OUTGOING_REQUEST";
    private static final String PHYSICAL_HOST = "PHYSICAL_HOST";

    public static OutgoingRequestContext adapt(final HttpContext context) {
        if (context instanceof OutgoingRequestContext) {
            return (OutgoingRequestContext) context;
        } else {
            return new OutgoingRequestContext(context);
        }
    }

    private OutgoingRequestContext(final HttpContext context) {
        super(context);
    }

    OutgoingRequestContext() {
        super();
    }

    public boolean isProxy() {
        Boolean proxy = getAttribute(PROXY, Boolean.class);
        if (proxy == null) {
            return false;
        } else {
            return proxy.booleanValue();
        }
    }

    void setProxy(boolean proxy) {
        setAttribute(PROXY, proxy);
    }

    public OutgoingRequest getOutgoingRequest() {
        return getAttribute(OUTGOING_REQUEST, OutgoingRequest.class);
    }

    void setOutgoingRequest(OutgoingRequest outgoingRequest) {
        setAttribute(OUTGOING_REQUEST, outgoingRequest);
    }

    /**
     * 
     * @return physical host
     */
    HttpHost getPhysicalHost() {
        return getAttribute(PHYSICAL_HOST, HttpHost.class);
    }

    /**
     * 
     * @param httpHost
     *            host
     */
    void setPhysicalHost(HttpHost httpHost) {
        setAttribute(PHYSICAL_HOST, httpHost);
    }

    /**
     * Set attribute and save previous attribute value
     * 
     * @param id
     *            attribute name
     * @param obj
     *            value
     * @param save
     *            save previous attribute value to restore later
     */
    public void setAttribute(String id, Object obj, boolean save) {
        if (save) {
            String historyAttribute = id + "history";
            Queue<Object> history = (Queue<Object>) getAttribute(historyAttribute);
            if (history == null) {
                history = new LinkedList<Object>();
                setAttribute(historyAttribute, history);
            }
            if (this.getAttribute(id) != null) {
                history.add(getAttribute(id));
            }
        }
        setAttribute(id, obj);
    }

    /**
     * remove attribute and restore previous attribute value
     * 
     * @param id
     *            attribute name
     * @param restore
     *            restore previous attribute value
     * @return attribute value
     */
    public Object removeAttribute(String id, boolean restore) {
        Object value = removeAttribute(id);
        if (restore) {
            String historyAttribute = id + "history";
            Queue<Object> history = (Queue<Object>) getAttribute(historyAttribute);
            if (history != null && !history.isEmpty()) {
                Object previous = history.remove();
                setAttribute(id, previous);
            }
        }
        return value;
    }
}
