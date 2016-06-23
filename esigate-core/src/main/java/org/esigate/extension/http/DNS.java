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
package org.esigate.extension.http;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpHost;
import org.apache.http.conn.DnsResolver;
import org.apache.http.impl.client.HttpClientBuilder;
import org.esigate.Driver;
import org.esigate.Parameters;
import org.esigate.events.Event;
import org.esigate.events.EventDefinition;
import org.esigate.events.EventManager;
import org.esigate.events.IEventListener;
import org.esigate.events.impl.HttpClientBuilderEvent;
import org.esigate.extension.Extension;
import org.esigate.util.Parameter;
import org.esigate.util.ParameterCollection;
import org.esigate.util.UriUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DNS extension that allow to associates IP addresses to the given host in a DNS overrider. The IP addresses are
 * assumed to be already resolved.
 * 
 * @author Alexis Thaveau on 26/01/16.
 */
public class DNS implements Extension, IEventListener {
    private static final Logger LOG = LoggerFactory.getLogger(DNS.class);

    // Core parameters
    public static final Parameter<Collection<String>> REMOTE_IP = new ParameterCollection("remoteIP");

    private DnsResolver dnsResolver;

    public DnsResolver getDnsResolver() {
        return dnsResolver;
    }

    @Override
    public void init(Driver driver, Properties properties) {
        Collection<String> ips = REMOTE_IP.getValue(properties);

        if (ips.isEmpty()) {
            LOG.error("Missing configuration properties for driver {}. Property {}", driver.getConfiguration()
                    .getInstanceName(), REMOTE_IP.getName());
        } else {
            String[] remoteURLS = Parameters.REMOTE_URL_BASE.getValue(properties);
            List<InetAddress> inetAddresses = new ArrayList<>();
            CustomizableDNSResolver customizableDNSResolver = new CustomizableDNSResolver();
            dnsResolver = customizableDNSResolver;
            for (String ip : ips) {
                try {
                    inetAddresses.add(InetAddress.getByName(ip));
                } catch (UnknownHostException e) {
                    LOG.error("Unable to resolve InetAddress [{}]", ip, e);
                }
            }

            for (String remoteURL : remoteURLS) {

                HttpHost host = UriUtils.extractHost(remoteURL);
                customizableDNSResolver.add(host.getHostName(), inetAddresses.toArray(new InetAddress[] {}));
            }

            driver.getEventManager().register(EventManager.EVENT_HTTP_BUILDER_INITIALIZATION, this);
        }
    }

    @Override
    public boolean event(EventDefinition id, Event event) {
        HttpClientBuilderEvent httpClientBuilderEvent = (HttpClientBuilderEvent) event;
        HttpClientBuilder httpClientBuilder = httpClientBuilderEvent.getHttpClientBuilder();
        httpClientBuilder.setDnsResolver(dnsResolver);
        return false;
    }
}
