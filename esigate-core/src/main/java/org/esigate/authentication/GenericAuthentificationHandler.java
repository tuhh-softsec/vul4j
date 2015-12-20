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

package org.esigate.authentication;

import java.util.Properties;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.esigate.Driver;
import org.esigate.HttpErrorPage;
import org.esigate.events.Event;
import org.esigate.events.EventDefinition;
import org.esigate.events.EventManager;
import org.esigate.events.IEventListener;
import org.esigate.events.impl.FragmentEvent;
import org.esigate.events.impl.ProxyEvent;
import org.esigate.extension.Extension;
import org.esigate.http.IncomingRequest;
import org.esigate.http.OutgoingRequest;

/**
 * This class is an adapter from the old ESIgate 2.X/3.X API used for AuthentificationHandlers to the new extension
 * systems.
 * 
 * <p>
 * The following events are mapped on init :
 * <uL>
 * <li> {@link EventManager#EVENT_PROXY_PRE} is mapped to {@link #beforeProxy(HttpRequest)}</li>
 * <li>{@link EventManager#EVENT_FRAGMENT_PRE} is mapped to {@link #preRequest(OutgoingRequest, IncomingRequest)}</li>
 * <li> {@link EventManager#EVENT_FRAGMENT_POST} is mapped to
 * {@link #needsNewRequest(HttpResponse, OutgoingRequest, IncomingRequest)}</li>
 * </ul>
 * 
 * <p>
 * To update an old AuthentificationHandler :
 * <ul>
 * <li>Remote "implements AuthentificationHandler" from class definition</li>
 * <li>Add "extends GenericAuthentificationHandler" to class definition</li>
 * </ul>
 * 
 * @author Nicolas Richeton
 * 
 */
public abstract class GenericAuthentificationHandler implements IEventListener, Extension {

    private Driver driver;

    /**
     * Method called before proxying a request
     * 
     * This method can ask the users credentials by sending an authentication page or a 401 code or redirect to a login
     * page. If so the method must return false in order to stop further processing.
     * 
     * @param httpRequest
     *            the incoming request
     * @return true if the processing must continue, false if the response has already been sent to the client.
     */
    public abstract boolean beforeProxy(HttpRequest httpRequest);

    /**
     * Method called before sending a request to the destination server.
     * 
     * This method can be used to add user credentials to the request
     * 
     * @param outgoingRequest
     * @param incomingRequest
     */
    public abstract void preRequest(OutgoingRequest outgoingRequest, IncomingRequest incomingRequest);

    /**
     * Method called after the response has been obtained from the destination server.
     * 
     * This method can be used to ask for a new request if the destination server uses a challenge-based authentication
     * mechanism with an arbitrary number of steps.
     * 
     * @param response
     * @param outgoingRequest
     * @param incomingRequest
     * @return true if a new request is needed
     */
    public abstract boolean needsNewRequest(HttpResponse response, OutgoingRequest outgoingRequest,
            IncomingRequest incomingRequest);

    /*
     * (non-Javadoc)
     * 
     * @see org.esigate.extension.Extension#init(org.esigate.Driver, java.util.Properties)
     */
    @Override
    public final void init(Driver d, Properties properties) {
        this.driver = d;
        this.driver.getEventManager().register(EventManager.EVENT_PROXY_PRE, this);
        this.driver.getEventManager().register(EventManager.EVENT_FRAGMENT_PRE, this);
        this.driver.getEventManager().register(EventManager.EVENT_FRAGMENT_POST, this);

        init(properties);
    }

    public abstract void init(Properties properties);

    @Override
    public boolean event(EventDefinition id, Event event) {

        if (EventManager.EVENT_FRAGMENT_PRE.equals(id)) {
            FragmentEvent e = (FragmentEvent) event;
            preRequest(e.getHttpRequest(), e.getOriginalRequest());
        } else if (EventManager.EVENT_FRAGMENT_POST.equals(id)) {
            FragmentEvent e = (FragmentEvent) event;

            while (needsNewRequest(e.getHttpResponse(), e.getHttpRequest(), e.getOriginalRequest())) {
                EntityUtils.consumeQuietly(e.getHttpResponse().getEntity());
                preRequest(e.getHttpRequest(), e.getOriginalRequest());
                try {
                    e.setHttpResponse(this.driver.getRequestExecutor().execute(e.getHttpRequest()));
                } catch (HttpErrorPage e1) {
                    e.setHttpResponse(e1.getHttpResponse());
                }
            }
        } else if (EventManager.EVENT_PROXY_PRE.equals(id)) {
            ProxyEvent e = (ProxyEvent) event;
            e.setExit(!beforeProxy(e.getOriginalRequest()));
        }

        return true;
    }

    public Driver getDriver() {
        return driver;
    }
}
