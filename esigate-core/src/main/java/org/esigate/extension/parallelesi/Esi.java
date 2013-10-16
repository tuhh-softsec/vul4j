package org.esigate.extension.parallelesi;

import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.esigate.Driver;
import org.esigate.events.Event;
import org.esigate.events.EventDefinition;
import org.esigate.events.EventManager;
import org.esigate.events.IEventListener;
import org.esigate.events.impl.RenderEvent;
import org.esigate.extension.Extension;
import org.esigate.util.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private static final Logger LOG = LoggerFactory.getLogger(Esi.class);
	// esi_max_threads = 0 -> linear execution
	private static Parameter THREADS = new Parameter("esi_max_threads", "0");
	private static Parameter IDLE = new Parameter("esi_max_idle", "60");
	private int maxThreads;
	private int idle;
	private Executor executor;

	@Override
	public boolean event(EventDefinition id, Event event) {
		RenderEvent renderEvent = (RenderEvent) event;
		renderEvent.renderers.add(new EsiRenderer(this.executor));

		// Continue processing
		return true;
	}

	@Override
	public void init(Driver driver, Properties properties) {
		driver.getEventManager().register(EventManager.EVENT_RENDER_PRE, this);

		// Load configuration
		this.maxThreads = THREADS.getValueInt(properties);
		this.idle = IDLE.getValueInt(properties);

		if (this.maxThreads == 0) {
			this.executor = null;
			LOG.info("Linear ESI processing enabled.");
		} else {
			this.executor = new ThreadPoolExecutor(0, this.maxThreads, this.idle, TimeUnit.SECONDS,
					new SynchronousQueue<Runnable>());

			LOG.info("Multi-threaded ESI processing enabled. Thread limit: {}, max idle {}.",
					String.valueOf(this.maxThreads), String.valueOf(this.idle));
		}

	}

}
