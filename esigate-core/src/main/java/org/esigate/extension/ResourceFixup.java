package org.esigate.extension;

import java.util.Properties;

import org.esigate.Driver;
import org.esigate.events.Event;
import org.esigate.events.EventDefinition;
import org.esigate.events.EventManager;
import org.esigate.events.IEventListener;
import org.esigate.events.impl.RenderEvent;
import org.esigate.renderers.ResourceFixupRenderer;

public class ResourceFixup implements Extension, IEventListener {

    @Override
    public boolean event(EventDefinition id, Event event) {

        RenderEvent renderEvent = (RenderEvent) event;
        // Fix resources
        String baseUrl = renderEvent.getOriginalRequest().getBaseUrl().toString();
        String visibleBaseUrl = renderEvent.getOriginalRequest().getVisibleBaseUrl().toString();
        ResourceFixupRenderer fixup =
                new ResourceFixupRenderer(baseUrl, renderEvent.getRemoteUrl(), renderEvent.getOriginalRequest()
                        .getDriver().getUrlRewriter(), visibleBaseUrl);

        // Add fixup renderer as first renderer.
        renderEvent.getRenderers().add(0, fixup);

        // Continue processing
        return true;
    }

    @Override
    public void init(Driver driver, Properties properties) {
        driver.getEventManager().register(EventManager.EVENT_RENDER_PRE, this);
    }

}
