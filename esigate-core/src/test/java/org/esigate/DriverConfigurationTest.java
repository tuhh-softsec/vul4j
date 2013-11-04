/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.esigate;

import java.util.Properties;

import junit.framework.TestCase;

import org.esigate.url.IpHashBaseUrlRetrieveStrategy;
import org.esigate.url.RoundRobinBaseUrlRetrieveStrategy;
import org.esigate.url.SingleBaseUrlRetrieveStrategy;
import org.esigate.url.StickySessionBaseUrlRetrieveStrategy;

/**
 * DriverConfiguration test case.
 * 
 * @author Alexis Thaveau
 * 
 */
public class DriverConfigurationTest extends TestCase {

    public void testBaseUrl() {
        Properties properties = new Properties();

        properties.setProperty(Parameters.REMOTE_URL_BASE.getName(), "http://example.com");
        DriverConfiguration config = new DriverConfiguration("test-baseurl", properties);
        assertTrue(config.getBaseUrlRetrieveStrategy() instanceof SingleBaseUrlRetrieveStrategy);

        properties.setProperty(Parameters.REMOTE_URL_BASE.getName(), "http://example.com, http://example1.com");
        config = new DriverConfiguration("test-baseurl", properties);
        assertTrue(config.getBaseUrlRetrieveStrategy() instanceof RoundRobinBaseUrlRetrieveStrategy);

        properties.setProperty(Parameters.REMOTE_URL_BASE.getName(), "http://example.com, http://example1.com");
        properties.setProperty(Parameters.REMOTE_URL_BASE_STRATEGY.getName(), "roundrobin");
        config = new DriverConfiguration("test-baseurl", properties);
        assertTrue(config.getBaseUrlRetrieveStrategy() instanceof RoundRobinBaseUrlRetrieveStrategy);

        properties.setProperty(Parameters.REMOTE_URL_BASE.getName(), "http://example.com, http://example1.com");
        properties.setProperty(Parameters.REMOTE_URL_BASE_STRATEGY.getName(), "iphash");
        config = new DriverConfiguration("test-baseurl", properties);
        assertTrue(config.getBaseUrlRetrieveStrategy() instanceof IpHashBaseUrlRetrieveStrategy);

        properties.setProperty(Parameters.REMOTE_URL_BASE.getName(), "http://example.com, http://example1.com");
        properties.setProperty(Parameters.REMOTE_URL_BASE_STRATEGY.getName(), "stickysession");
        config = new DriverConfiguration("test-baseurl", properties);
        assertTrue(config.getBaseUrlRetrieveStrategy() instanceof StickySessionBaseUrlRetrieveStrategy);

        try {
            properties.setProperty(Parameters.REMOTE_URL_BASE.getName(), "http://example.com, http://example1.com");
            properties.setProperty(Parameters.REMOTE_URL_BASE_STRATEGY.getName(), "invalid_strategy");
            config = new DriverConfiguration("test-baseurl", properties);
            fail();
        } catch (ConfigurationException e) {
            assertTrue(e.getMessage().contains("invalid_strategy"));
        } catch (Exception e) {
            fail();
        }

        try {
            properties.setProperty(Parameters.REMOTE_URL_BASE.getName(), "http://example.com, ://1.com");
            config = new DriverConfiguration("test-baseurl", properties);
            fail();
        } catch (ConfigurationException e) {
            // Expected behavior
        } catch (Exception e) {
            fail();
        }

    }
}
