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
import org.apache.http.conn.HttpClientConnectionManager;
import org.esigate.Driver;
import org.esigate.Parameters;
import org.esigate.test.TestUtils;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Alexis Thaveau on 26/01/16.
 */
public class DNSTest extends TestCase {

    public void testInit() throws Exception {
        Properties properties = new Properties();
        properties.put(Parameters.EXTENSIONS.getName(), "org.esigate.extension.http.DNS");

        // Test one IP and one host
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://myvirtualhost/");
        properties.put(DNS.REMOTE_IP.getName(), "127.0.0.1");

        Driver driver = TestUtils.createMockDriver(properties, (HttpClientConnectionManager) null);
        DNS dns = new DNS();

        // Test one IP and one host
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://myvirtualhost/");
        properties.put(DNS.REMOTE_IP.getName(), "127.0.0.1");

        dns.init(driver, properties);
        InetAddress[] inetAddresses = dns.getDnsResolver().resolve("myvirtualhost");
        assertEquals(1, inetAddresses.length);
        assertEquals("127.0.0.1", inetAddresses[0].getHostAddress());

        // Test one IP and two host
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://myvirtualhost1/,http://myvirtualhost2/");
        properties.put(DNS.REMOTE_IP.getName(), "127.0.0.1");
        dns = new DNS();
        dns.init(driver, properties);

        inetAddresses = dns.getDnsResolver().resolve("myvirtualhost1");
        assertEquals(1, inetAddresses.length);
        assertEquals("127.0.0.1", inetAddresses[0].getHostAddress());

        inetAddresses = dns.getDnsResolver().resolve("myvirtualhost2");
        assertEquals(1, inetAddresses.length);
        assertEquals("127.0.0.1", inetAddresses[0].getHostAddress());

        // Test two IP and one host
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://myvirtualhost/");
        properties.put(DNS.REMOTE_IP.getName(), "127.0.0.1,127.0.0.0");
        dns = new DNS();
        dns.init(driver, properties);

        inetAddresses = dns.getDnsResolver().resolve("myvirtualhost");
        assertEquals(2, inetAddresses.length);
        List<String> ips = toIPs(inetAddresses);
        assertTrue("IP 127.0.0.1 should be resolved", ips.contains("127.0.0.1"));
        assertTrue("IP 127.0.0.0 should be resolved", ips.contains("127.0.0.0"));

        // Test two IP and two host
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://myvirtualhost1/,http://myvirtualhost2/");
        properties.put(DNS.REMOTE_IP.getName(), "127.0.0.1,127.0.0.0");
        dns = new DNS();
        dns.init(driver, properties);

        inetAddresses = dns.getDnsResolver().resolve("myvirtualhost1");
        assertEquals(2, inetAddresses.length);
        assertTrue("IP 127.0.0.1 should be resolved", ips.contains("127.0.0.1"));

        inetAddresses = dns.getDnsResolver().resolve("myvirtualhost2");
        assertEquals(2, inetAddresses.length);
        assertTrue("IP 127.0.0.0 should be resolved", ips.contains("127.0.0.0"));

        // Test two IP and two host
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://myvirtualhost1/,http://myvirtualhost2/");
        properties.remove(DNS.REMOTE_IP.getName());
        dns = new DNS();
        dns.init(driver, properties);
        try {
            dns.getDnsResolver().resolve("myvirtualhost1");
            fail("Should throw exception");
        } catch (Exception e) {

        }

    }

    private List<String> toIPs(InetAddress[] inetAddresses) {
        List<String> ips = new ArrayList<>();
        for (InetAddress inetAddress : inetAddresses) {
            ips.add(inetAddress.getHostAddress());

        }
        return ips;
    }

    public void testEvent() throws Exception {

    }
}
