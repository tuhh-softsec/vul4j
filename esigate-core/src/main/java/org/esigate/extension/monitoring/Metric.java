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

package org.esigate.extension.monitoring;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.Slf4jReporter;
import org.apache.http.HttpStatus;
import org.apache.http.client.cache.CacheResponseStatus;
import org.apache.http.client.cache.HttpCacheContext;
import org.esigate.Driver;
import org.esigate.events.Event;
import org.esigate.events.EventDefinition;
import org.esigate.events.EventManager;
import org.esigate.events.IEventListener;
import org.esigate.events.impl.FetchEvent;
import org.esigate.events.impl.ProxyEvent;
import org.esigate.extension.Extension;
import org.esigate.util.Parameter;
import org.esigate.util.ParameterInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * This extension will record proxy request, and backend request to generate statistics.
 * <p/>
 * Result will be logged using SLF4J in INFO level every 60 minutes. Period can be configured in driver properties :
 * <p/>
 * <code>metricPeriod=60</code>
 * <p/>
 * <p/>
 * Created by alexis on 20/03/14.
 */
public class Metric implements Extension, IEventListener {
    private static final Logger LOG = LoggerFactory.getLogger(Metric.class);
    private static final Parameter<Integer> PARAM_METRIC_PERIOD = new ParameterInteger("metricPeriod", 60);

    private MetricRegistry metric = new MetricRegistry();
    private ScheduledReporter reporter;
    private Driver driver;

    @Override
    public void init(Driver d, Properties properties) {
        this.driver = d;
        LOG.debug("Initialize Metric");
        driver.getEventManager().register(EventManager.EVENT_PROXY_POST, this);
        driver.getEventManager().register(EventManager.EVENT_FETCH_POST, this);

        reporter = Slf4jReporter
                .forRegistry(this.metric)
                .outputTo(LOG)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();

        reporter.start(PARAM_METRIC_PERIOD.getValue(properties), TimeUnit.SECONDS);
    }


    @Override
    public boolean event(EventDefinition id, Event event) {


        String timerName = MetricRegistry.name(this.getClass().getSimpleName(),
                driver.getConfiguration().getInstanceName(), id.getId());


        if (EventManager.EVENT_PROXY_POST.equals(id)) {
            if (((ProxyEvent) event).getErrorPage() != null) {
                timerName = MetricRegistry.name(timerName, "error");
            }
        } else if (EventManager.EVENT_FETCH_POST.equals(id)) {
            //Retrieve HTTP response status code and cache status
            FetchEvent e = (FetchEvent) event;
            int statusCode = e.getHttpResponse().getStatusLine().getStatusCode();
            CacheResponseStatus cacheResponseStatus = (CacheResponseStatus) e.getHttpContext().getAttribute(
                    HttpCacheContext.CACHE_RESPONSE_STATUS);

            //Adding status code when error
            if (statusCode >= HttpStatus.SC_BAD_REQUEST) {
                timerName = MetricRegistry.name(timerName, "error", String.valueOf(statusCode));
            }
            //Adding cache if not MISS
            if (cacheResponseStatus != null && !cacheResponseStatus.equals(CacheResponseStatus.CACHE_MISS)) {
                timerName = MetricRegistry.name(timerName, cacheResponseStatus.name().toLowerCase());
            }
        }

        metric.meter(timerName).mark();

        return true;
    }
}
