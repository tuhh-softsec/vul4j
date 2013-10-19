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
package org.esigate.extension.parallelesi;

import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

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
import org.esigate.extension.surrogate.CapabilitiesEvent;
import org.esigate.extension.surrogate.Surrogate;
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

		boolean doEsi = true;

		// ensure we should process esi
		if (renderEvent.httpResponse != null && renderEvent.httpResponse.containsHeader(Surrogate.HEADER_ENABLED_CAPABILITIES)) {
			String capabilities = renderEvent.httpResponse.getFirstHeader(Surrogate.HEADER_ENABLED_CAPABILITIES)
					.getValue();

			if (!containsIgnoreCase(capabilities, "ESI/1.0") && !containsIgnoreCase(capabilities, "ESI-Inline/1.0")
					&& !containsIgnoreCase(capabilities, "ESIGATE/4.0")) {
				doEsi = false;
			}
		}

		if (doEsi) {
			renderEvent.renderers.add(new EsiRenderer(this.executor));
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
