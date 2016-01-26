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

import junit.framework.TestCase;

import java.net.InetAddress;

/**
 * @author Alexis Thaveau on 26/01/16.
 */
public class CustomizableDNSResolverTest extends TestCase {

    public void testResolve() throws Exception {

        CustomizableDNSResolver customizableDNSResolver = new CustomizableDNSResolver();
        String localhost = InetAddress.getLocalHost().getHostName();
        InetAddress[] addresses = customizableDNSResolver.resolve(localhost);
        assertNotNull("Host " + localhost + " should be resolved", addresses);
        String ip = "10.5.6.7";
        String host = "myVirtualHost";
        customizableDNSResolver.add(host, InetAddress.getByName(ip));

        assertNotNull("Host " + host + " should be resolved", customizableDNSResolver.resolve(host));
        assertEquals("Host " + host + " should be resolved to" + ip, ip,
                customizableDNSResolver.resolve(host)[0].getHostAddress());

        try {
            host = "random" + System.currentTimeMillis();
            customizableDNSResolver.resolve(host);
            fail("Should throw an exception when resolvind an unknown host  [" + host + "]");

        } catch (Exception e) {

        }

    }
}
