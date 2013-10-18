package org.esigate.extension;

import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

import java.util.Properties;

import org.esigate.Driver;
import org.esigate.esi.EsiRenderer;
import org.esigate.events.Event;
import org.esigate.events.EventDefinition;
import org.esigate.events.EventManager;
import org.esigate.events.IEventListener;
import org.esigate.events.impl.RenderEvent;
import org.esigate.extension.surrogate.CapabilitiesEvent;
import org.esigate.extension.surrogate.Surrogate;

/**
 * This extension processes ESI directives, like :
 * <p>
 * &lt;esi:include src="$(PROVIDER{cms})/news" fragment="news_1"/>
 * 
 * 
 * @author Nicolas Richeton
 */
public class Esi implements Extension, IEventListener {

	@Override
	public boolean event(EventDefinition id, Event event) {
		RenderEvent renderEvent = (RenderEvent) event;
		boolean doEsi = true;

		// ensure we should process esi
		if (renderEvent.httpResponse != null
				&& renderEvent.httpResponse.containsHeader(Surrogate.HEADER_ENABLED_CAPABILITIES)) {
			String capabilities = renderEvent.httpResponse.getFirstHeader(Surrogate.HEADER_ENABLED_CAPABILITIES)
					.getValue();

			if (!containsIgnoreCase(capabilities, "ESI/1.0") && !containsIgnoreCase(capabilities, "ESI-Inline/1.0")
					&& !containsIgnoreCase(capabilities, "ESIGATE/4.0")) {
				doEsi = false;
			}
		}

		if (doEsi) {
			renderEvent.renderers.add(new EsiRenderer());
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
				capEvent.capabilities.add("ESI/1.0");
				capEvent.capabilities.add("ESI-Inline/1.0");
				capEvent.capabilities.add("ESIGATE/4.0");
				return true;
			}
		});
	}

}
