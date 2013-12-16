package org.esigate.extension;

import java.util.Properties;

import org.esigate.Driver;
import org.esigate.events.Event;
import org.esigate.events.EventDefinition;
import org.esigate.events.EventManager;
import org.esigate.events.IEventListener;
import org.esigate.events.impl.FragmentEvent;

/**
 * Forwards original request (as received by Esigate) in a request header. Can be used to help building canonical urls.
 * <p>
 * This extension is not enabled by default.
 * <p>
 * <b>Sample</b>:
 * <p>
 * X-Esigate-Request: http://localhost:8080
 * 
 * <p>
 * <b>Note</b>: copying the complete request URI in a header may cause issues if the URI is big because most servers
 * have a limit to the total size of http headers.
 * <p>
 * <b>See</b>: https://sourceforge.net/apps/mantisbt/webassembletool/view.php?id=114
 * 
 * @author Nicolas Richeton
 * 
 */
public class ForwardOriginalUrl implements Extension, IEventListener {

    @Override
    public boolean event(EventDefinition id, Event event) {
        // Cast event
        FragmentEvent fe = (FragmentEvent) event;

        // Add header
        fe.httpRequest.addHeader("X-Esigate-Request", fe.originalRequest.getRequestLine().getUri());

        // Continue processing.
        return true;
    }

    @Override
    public void init(Driver driver, Properties properties) {
        driver.getEventManager().register(EventManager.EVENT_FRAGMENT_PRE, this);
    }

}
