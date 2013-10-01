package org.esigate.extension.parallelesi;

import java.util.Properties;

import org.esigate.Driver;
import org.esigate.events.Event;
import org.esigate.events.EventDefinition;
import org.esigate.events.EventManager;
import org.esigate.events.IEventListener;
import org.esigate.events.impl.RenderEvent;
import org.esigate.extension.Extension;

/**
 * This extension processes ESI directives, like :
 * <p>
 * &lt;esi:include src="$(PROVIDER{cms})/news" fragment="news_1"/>
 * <p>
 * This extension implements multi-threaded processing, aka Parallel ESI.
 * 
 * @author Nicolas Richeton
 */
public class Esi implements Extension, IEventListener {

	@Override
	public boolean event(EventDefinition id, Event event) {
		RenderEvent renderEvent = (RenderEvent) event;
		renderEvent.renderers.add(new EsiRenderer());

		// Continue processing
		return true;
	}

	@Override
	public void init(Driver driver, Properties properties) {
		driver.getEventManager().register(EventManager.EVENT_RENDER_PRE, this);
	}

}
