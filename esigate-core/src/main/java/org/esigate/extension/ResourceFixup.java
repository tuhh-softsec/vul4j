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

import org.esigate.Driver;
import org.esigate.events.Event;
import org.esigate.events.EventDefinition;
import org.esigate.events.EventManager;
import org.esigate.events.IEventListener;
import org.esigate.events.impl.RenderEvent;
import org.esigate.renderers.ResourceFixupRenderer;
import org.esigate.util.Parameter;
import org.esigate.util.ParameterString;

/**
 * Rewrites URLs found inside html pages.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public class ResourceFixup implements Extension, IEventListener {

    private static final Parameter<String> FIX_MODE = new ParameterString("fixMode", "relative");
    private boolean absolute;

    @Override
    public boolean event(EventDefinition id, Event event) {

        RenderEvent renderEvent = (RenderEvent) event;
        // Fix resources
        String baseUrl = renderEvent.getOriginalRequest().getBaseUrl().toString();
        String visibleBaseUrl = renderEvent.getOriginalRequest().getVisibleBaseUrl().toString();
        ResourceFixupRenderer fixup =
                new ResourceFixupRenderer(baseUrl, renderEvent.getRemoteUrl(), renderEvent.getOriginalRequest()
                        .getDriver().getUrlRewriter(), visibleBaseUrl, absolute);

        // Add fixup renderer as first renderer.
        renderEvent.getRenderers().add(0, fixup);

        // Continue processing
        return true;
    }

    @Override
    public void init(Driver driver, Properties properties) {
        absolute = "absolute".equalsIgnoreCase(FIX_MODE.getValue(properties));
        driver.getEventManager().register(EventManager.EVENT_RENDER_PRE, this);
    }

}
