package org.esigate.extension;

import java.util.Properties;

import org.esigate.Driver;
import org.esigate.aggregator.AggregateRenderer;
import org.esigate.events.Event;
import org.esigate.events.EventDefinition;
import org.esigate.events.EventManager;
import org.esigate.events.IEventListener;
import org.esigate.events.impl.RenderEvent;

/**
 * This extension processes the old esigate directives based on html comments,
 * like :
 * <p>
 * &lt;!--$includeblock$aggregated2$block.html$myblock$-->
 * </p>
 * see : http://www.esigate.org/html-comments.html for complete syntax.
 * 
 * @author Nicolas Richeton
 * @deprecated These directives are replaced by the ESI syntax and extension.
 */
public class Aggregate implements Extension, IEventListener {

	public boolean event(EventDefinition id, Event event) {
		RenderEvent renderEvent = (RenderEvent) event;
		renderEvent.renderers.add(new AggregateRenderer());

		// Continue processing
		return true;
	}

	public void init(Driver driver, Properties properties) {
		driver.getEventManager().register(EventManager.EVENT_RENDER_PRE, this);
	}

}
