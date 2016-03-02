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

import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * DNS resolver that will resolve the host names against a collection held in-memory and fallback to an OS resolution
 * 
 * @author Alexis Thaveau on 26/01/16.
 */
public class CustomizableDNSResolver extends SystemDefaultDnsResolver {

    /**
     * Logger associated to this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(CustomizableDNSResolver.class);

    /**
     * In-memory collection that will hold the associations between a host name and an array of InetAddress instances.
     */
    private final Map<String, InetAddress[]> dnsMap = new HashMap<>();

    /**
     *
     */
    public CustomizableDNSResolver() {
    }

    /**
     * Associates the given array of IP addresses to the given host in this DNS overrider. The IP addresses are assumed
     * to be already resolved.
     * 
     * @param host
     *            The host name to be associated with the given IP.
     * @param ips
     *            array of IP addresses to be resolved by this DNS overrider to the given host name.
     */
    public void add(final String host, final InetAddress... ips) {
        dnsMap.put(host, ips);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InetAddress[] resolve(final String host) throws UnknownHostException {
        InetAddress[] resolvedAddresses = dnsMap.get(host);
        if (LOG.isInfoEnabled()) {
            LOG.info("Resolving {} to {}", host, Arrays.deepToString(resolvedAddresses));
        }
        if (resolvedAddresses == null) {
            resolvedAddresses = super.resolve(host);
        }
        return resolvedAddresses;
    }

}
