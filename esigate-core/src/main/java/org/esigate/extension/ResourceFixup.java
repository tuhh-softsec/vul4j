package org.esigate.extension;

import java.util.Properties;

import org.esigate.Driver;
import org.esigate.DriverConfiguration;
import org.esigate.events.Event;
import org.esigate.events.EventDefinition;
import org.esigate.events.EventManager;
import org.esigate.events.IEventListener;
import org.esigate.events.impl.RenderEvent;
import org.esigate.renderers.ResourceFixupRenderer;
import org.esigate.util.HttpRequestHelper;

public class ResourceFixup implements Extension, IEventListener {
    private DriverConfiguration config;

    @Override
    public boolean event(EventDefinition id, Event event) {

        RenderEvent renderEvent = (RenderEvent) event;
        // Fix resources
        if (config.isFixResources()) {
            String baseUrl = HttpRequestHelper.getBaseUrl(renderEvent.originalRequest).toString();
            ResourceFixupRenderer fixup = new ResourceFixupRenderer(baseUrl, config.getVisibleBaseURL(baseUrl),
                    renderEvent.remoteUrl, config.getFixMode());

            // Add fixup renderer as first renderer.
            renderEvent.renderers.add(0, fixup);
        }

        // Continue processing
        return true;
    }

    @Override
    public void init(Driver driver, Properties properties) {
        this.config = driver.getConfiguration();
        driver.getEventManager().register(EventManager.EVENT_RENDER_PRE, this);
    }

}
