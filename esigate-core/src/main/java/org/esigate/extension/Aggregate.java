package org.esigate.extension;

import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

import java.util.Properties;

import org.esigate.Driver;
import org.esigate.aggregator.AggregateRenderer;
import org.esigate.events.Event;
import org.esigate.events.EventDefinition;
import org.esigate.events.EventManager;
import org.esigate.events.IEventListener;
import org.esigate.events.impl.RenderEvent;
import org.esigate.extension.surrogate.CapabilitiesEvent;
import org.esigate.extension.surrogate.Surrogate;

/**
 * This extension processes the old esigate directives based on html comments, like :
 * <p>
 * &lt;!--$includeblock$aggregated2$block.html$myblock$-->
 * </p>
 * see : http://www.esigate.org/html-comments.html for complete syntax.
 * 
 * @author Nicolas Richeton
 * @deprecated These directives are replaced by the ESI syntax and extension.
 */
public class Aggregate implements Extension, IEventListener {

    @Override
    public boolean event(EventDefinition id, Event event) {
        RenderEvent renderEvent = (RenderEvent) event;

        boolean doAggregate = true;

        // ensure we should process esi
        if (renderEvent.httpResponse != null
                && renderEvent.httpResponse.containsHeader(Surrogate.H_X_ENABLED_CAPABILITIES)) {
            String capabilities = renderEvent.httpResponse.getFirstHeader(Surrogate.H_X_ENABLED_CAPABILITIES)
                    .getValue();

            if (!containsIgnoreCase(capabilities, "Aggregator/1.0")) {
                doAggregate = false;
            }
        }

        if (doAggregate) {
            renderEvent.renderers.add(new AggregateRenderer());
        }
        // Continue processing
        return true;
    }

    @Override
    public void init(Driver driver, Properties properties) {
        driver.getEventManager().register(EventManager.EVENT_RENDER_PRE, this);

        driver.getEventManager().register(Surrogate.EVENT_SURROGATE_CAPABILITIES, new IEventListener() {
            @Override
            public boolean event(EventDefinition id, Event event) {
                CapabilitiesEvent capEvent = (CapabilitiesEvent) event;
                capEvent.capabilities.add("Aggregator/1.0");
                return true;
            }
        });
    }

}
