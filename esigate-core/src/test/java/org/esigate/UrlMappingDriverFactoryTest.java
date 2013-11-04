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

package org.esigate;

import java.util.Properties;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Unit test for :
 * 
 * <pre>
 * 0000175: Externalize Provider configuration. 
 * https://sourceforge.net/apps/mantisbt/webassembletool/view.php?id=175
 * </pre>
 * 
 * @author nricheto
 * 
 */
public class UrlMappingDriverFactoryTest extends TestCase {

    /**
     * Test simple mappings /provider1/* and /provider2/* , no host.
     * 
     * @throws HttpErrorPage
     */
    public void testBasicUrlMapping() throws HttpErrorPage {
        Properties properties = new Properties();

        // Setup provider1
        properties.setProperty("provider1." + Parameters.REMOTE_URL_BASE.getName(), "http://example1.com");
        properties.setProperty("provider1." + Parameters.MAPPINGS.getName(), "/provider1/*");

        // Setup provider1
        properties.setProperty("provider2." + Parameters.REMOTE_URL_BASE.getName(), "http://example2.com");
        properties.setProperty("provider2." + Parameters.MAPPINGS.getName(), "/provider2/*");

        // Configure Esigate using the previous configuration
        DriverFactory.configure(properties);

        // Assert requests go to the right provider
        Assert.assertEquals("provider1", DriverFactory.getInstanceFor("http", "localhost:8080", "/provider1/test")
                .getLeft().getConfiguration().getInstanceName());
        Assert.assertEquals("provider2", DriverFactory.getInstanceFor("http", "localhost:8080", "/provider2/test")
                .getLeft().getConfiguration().getInstanceName());

    }

    /**
     * Ensure virtual-host marching.
     * 
     * @throws HttpErrorPage
     */
    public void testHostUrlMapping() throws HttpErrorPage {
        Properties properties = new Properties();

        // Setup provider1
        properties.setProperty("provider1." + Parameters.REMOTE_URL_BASE.getName(), "http://example1.com");
        properties.setProperty("provider1." + Parameters.MAPPINGS.getName(), "http://www.remote.com/provider*");

        // Setup provider1
        properties.setProperty("provider2." + Parameters.REMOTE_URL_BASE.getName(), "http://example2.com");
        properties.setProperty("provider2." + Parameters.MAPPINGS.getName(), "http://localhost:8080/provider*");

        // Configure Esigate using the previous configuration
        DriverFactory.configure(properties);

        // Assert requests go to the right provider
        Assert.assertEquals("provider1", DriverFactory.getInstanceFor("http", "www.remote.com", "/provider2/test")
                .getLeft().getConfiguration().getInstanceName());
        Assert.assertEquals("provider2", DriverFactory.getInstanceFor("http", "localhost:8080", "/provider1/test")
                .getLeft().getConfiguration().getInstanceName());

    }

    /**
     * Ensure a default, "catch-all" mapping can be defined with.
     * 
     * @throws HttpErrorPage
     *             *
     */
    public void testExplicitDefaultMapping() throws HttpErrorPage {
        Properties properties = new Properties();

        // Setup provider1
        properties.setProperty("provider1." + Parameters.REMOTE_URL_BASE.getName(), "http://example1.com");
        properties.setProperty("provider1." + Parameters.MAPPINGS.getName(), "*");

        // Setup provider1
        properties.setProperty("provider2." + Parameters.REMOTE_URL_BASE.getName(), "http://example2.com");
        properties.setProperty("provider2." + Parameters.MAPPINGS.getName(), "/provider2/*");

        // Configure Esigate using the previous configuration
        DriverFactory.configure(properties);

        // Assert requests go to the right provider
        Assert.assertEquals("provider1", DriverFactory.getInstanceFor("http", "www.remote.com", "/notMatching")
                .getLeft().getConfiguration().getInstanceName());
        Assert.assertEquals("provider2", DriverFactory.getInstanceFor("http", "localhost:8080", "/provider2/test")
                .getLeft().getConfiguration().getInstanceName());

    }

    public void testNoMapping() {
        Properties properties = new Properties();

        // Setup provider1
        properties.setProperty("provider1." + Parameters.REMOTE_URL_BASE.getName(), "http://example1.com");
        properties.setProperty("provider1." + Parameters.MAPPINGS.getName(), "/provider1/*");

        // Configure Esigate using the previous configuration
        DriverFactory.configure(properties);

        // Assert requests go to the right provider
        try {
            DriverFactory.getInstanceFor("http", "localhost:8080", "/");
            fail("Should throw HttpErrorPage");
        } catch (HttpErrorPage e) {

            // Success
        }

    }

}
