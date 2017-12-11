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

import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
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
import org.esigate.util.ParameterInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This extension processes ESI directives, like :
 * <p>
 * &lt;esi:include src="$(PROVIDER{cms})/news" fragment="news_1"/>
 * <p>
 * This extension implements multi-threaded processing, aka Parallel ESI.
 * <p>
 * It uses the following parameters :
 * <ul>
 * <li>esi_max_threads</li> : Maximum number of thread allocated to run parallel esi requests. Default is 0 : parallel
 * esi is disabled.
 * <li>esi_min_threads</li> : Minimun number of allocated threads.
 * <li>esi_max_idle :</li> : Release threads after X seconds of idle.
 * <li>esi_max_queue : Maximum waiting esi requests (waiting for threads). When the limit is reached, new requests are
 * refused.</li>
 * </ul>
 * 
 * @author Nicolas Richeton
 */
public class Esi implements Extension, IEventListener {
    private static final Logger LOG = LoggerFactory.getLogger(Esi.class);
    // esi_max_threads = 0 -> linear execution
    public static final Parameter<Integer> MAX_THREADS = new ParameterInteger("esi_max_threads", 0);
    public static final Parameter<Integer> MIN_THREADS = new ParameterInteger("esi_min_threads", 0);
    public static final Parameter<Integer> IDLE = new ParameterInteger("esi_max_idle", 60);
    public static final Parameter<Integer> MAX_QUEUE = new ParameterInteger("esi_max_queue", 10000);
    private Executor executor;
    public static final String[] CAPABILITIES = new String[] { "ESI/1.0", "ESI-Inline/1.0", "X-ESI-Fragment/1.0",
            "X-ESI-Replace/1.0", "X-ESI-XSLT/1.0", "ESIGATE/4.0" };

    @Override
    public boolean event(EventDefinition id, Event event) {
        RenderEvent renderEvent = (RenderEvent) event;

        boolean doEsi = true;

        // ensure we should process esi
        if (renderEvent.getHttpResponse() != null
                && renderEvent.getHttpResponse().containsHeader(Surrogate.H_X_ENABLED_CAPABILITIES)) {
            String enabledCapabilities = renderEvent.getHttpResponse()
                    .getFirstHeader(Surrogate.H_X_ENABLED_CAPABILITIES).getValue();

            doEsi = false;
            for (String capability : CAPABILITIES) {
                if (StringUtils.contains(enabledCapabilities, capability)) {
                    doEsi = true;
                    break;
                }
            }

        }

        if (doEsi) {
            renderEvent.getRenderers().add(new EsiRenderer(this.executor));
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
                for (String capability : CAPABILITIES) {
                    capEvent.getCapabilities().add(capability);
                }
                return true;
            }
        });

        // Load configuration
        int maxThreads = MAX_THREADS.getValue(properties);
        int minThreads = MIN_THREADS.getValue(properties);
        int idle = IDLE.getValue(properties);
        int maxQueue = MAX_QUEUE.getValue(properties);

        if (maxThreads == 0) {
            this.executor = null;
            LOG.info("Linear ESI processing enabled.");
        } else {
            this.executor = new ThreadPoolExecutor(minThreads, maxThreads, idle, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>(maxQueue));

            LOG.info("Multi-threaded ESI processing enabled. Thread limit: {}, max idle {}.",
                    String.valueOf(maxThreads), String.valueOf(idle));
        }

    }

}
