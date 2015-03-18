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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Properties;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.esigate.http.IncomingRequest;
import org.esigate.impl.DriverRequest;
import org.esigate.impl.UriMapping;
import org.esigate.test.TestUtils;
import org.junit.Test;

public class DriverFactoryTest extends TestCase {

    @Override
    protected void setUp() {
        DriverFactory.configure(new Properties());
    }

    public void testConfigureStringProperties1() throws HttpErrorPage {
        String id = "foo";
        try {
            DriverFactory.getInstance(id);
            fail("should throw ConfigurationException as there should be no provider named " + id);
        } catch (ConfigurationException e) {
            // expected behavior
        }

        Properties props = new Properties();
        props.setProperty(Parameters.REMOTE_URL_BASE.getName(), "http://base.url");
        DriverFactory.configure(id, props);
        Driver instance = DriverFactory.getInstance(id);
        assertNotNull(instance);

        DriverRequest request = TestUtils.createDriverRequest(instance);
        assertEquals("http://base.url", request.getBaseUrl().toString());
    }

    public void testMergeProperties() throws UnsupportedEncodingException {
        String defaultBaseUrl = "http://basedefault.url";
        String extendedBaseUrl = "http://baseextended.url";

        Properties defaultProps = new Properties();
        defaultProps.setProperty("default." + Parameters.REMOTE_URL_BASE.getName(), defaultBaseUrl);
        defaultProps.setProperty("default." + Parameters.URI_ENCODING.getName(), "ISO-8859-1");

        Properties extendedProps = new Properties();
        extendedProps.setProperty("default." + Parameters.REMOTE_URL_BASE.getName(), extendedBaseUrl);
        extendedProps.setProperty("default." + Parameters.URI_ENCODING.getName(), "UTF-8");

        URL dir = this.getClass().getResource("DriverFactoryTest.class");
        File file = new File(URLDecoder.decode(dir.getPath(), "UTF-8"));
        File classPathFile = file.getParentFile();
        File extFolder = classPathFile.getParentFile().getParentFile();

        File driverPropsFile = new File(classPathFile + File.separator + "driver.properties");
        File extendedPropsFile = new File(extFolder + File.separator + "driver-ext.properties");

        try {
            FileOutputStream defaultOutputStream = new FileOutputStream(driverPropsFile);
            FileOutputStream extOutputStream = new FileOutputStream(extendedPropsFile);
            defaultProps.store(defaultOutputStream, "driver.properties");
            extendedProps.store(extOutputStream, "driver-ext.properties");

            defaultOutputStream.close();
            extOutputStream.close();

        } catch (IOException e) {
            fail(e.getMessage());
        }

        try {
            DriverFactory.configure();
            Driver driver = DriverFactory.getInstance();
            assertEquals("UTF-8", driver.getConfiguration().getUriEncoding());
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            driverPropsFile.deleteOnExit();
            extendedPropsFile.deleteOnExit();
            DriverFactory.configure();
        }

    }

    public void testStripMappingPath() throws Exception {
        UriMapping mapping = UriMapping.create("/url/to/resource");
        String relUrl = DriverFactory.stripMappingPath("/mapping/path/test", mapping);
        assertEquals("/mapping/path/test", relUrl);

        mapping = UriMapping.create("/mapping/path/test");
        relUrl = DriverFactory.stripMappingPath("/mapping/path/test/url/to/resource", mapping);
        assertEquals("/url/to/resource", relUrl);
    }

    /**
     * Test provider selection based on esigate.properties configuration.
     * 
     * @throws HttpErrorPage
     */
    @Test
    public void testSelectProvider() throws HttpErrorPage {

        // Setup Esigate
        Properties p = new Properties();
        p.setProperty("provider1." + Parameters.REMOTE_URL_BASE, "http://test");
        p.setProperty("provider1." + Parameters.MAPPINGS, "http://sub.domain.com/*");
        p.setProperty("provider2." + Parameters.REMOTE_URL_BASE, "http://test");
        p.setProperty("provider2." + Parameters.MAPPINGS, "http://sub2.domain.com/*");
        p.setProperty("single." + Parameters.REMOTE_URL_BASE, "http://test");
        p.setProperty("single." + Parameters.MAPPINGS, "*");
        DriverFactory.configure(p);

        // Do testing
        IncomingRequest request =
                IncomingRequest.builder("http://sub2.domain.com/test/servlet/test/servlet/request")
                        .addHeader("Host", "sub2.domain.com").setContextPath("/test").build();
        Assert.assertEquals("provider2", DriverFactory.selectProvider(request).getDriver().getConfiguration()
                .getInstanceName());

        request =
                IncomingRequest.builder("http://sub.domain.com/test/servlet/test/servlet/request")
                        .addHeader("Host", "sub.domain.com").setContextPath("/test").build();
        Assert.assertEquals("provider1", DriverFactory.selectProvider(request).getDriver().getConfiguration()
                .getInstanceName());

        request =
                IncomingRequest.builder("http://foo.com/test/servlet/test/servlet/request")
                        .addHeader("Host", "foo.com").setContextPath("/test").build();
        Assert.assertEquals("single", DriverFactory.selectProvider(request).getDriver().getConfiguration()
                .getInstanceName());

    }

    /**
     * Test simple mappings /provider1/* and /provider2/* , no host.
     * 
     * @throws HttpErrorPage
     */
    public void testSelectProviderBasicUrlMapping() throws HttpErrorPage {
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
        IncomingRequest request = IncomingRequest.builder("http://localhost:8080/provider1/test").build();
        Assert.assertEquals("provider1", DriverFactory.selectProvider(request).getDriver().getConfiguration()
                .getInstanceName());
        request = IncomingRequest.builder("http://localhost:8080/provider2/test").build();
        Assert.assertEquals("provider2", DriverFactory.selectProvider(request).getDriver().getConfiguration()
                .getInstanceName());

    }

    /**
     * Ensure virtual-host marching.
     * 
     * @throws HttpErrorPage
     */
    public void testSelectProviderHostUrlMapping() throws HttpErrorPage {
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
        IncomingRequest request = IncomingRequest.builder("http://www.remote.com/provider2/test").build();
        Assert.assertEquals("provider1", DriverFactory.selectProvider(request).getDriver().getConfiguration()
                .getInstanceName());
        request = IncomingRequest.builder("http://localhost:8080/provider1/test").build();
        Assert.assertEquals("provider2", DriverFactory.selectProvider(request).getDriver().getConfiguration()
                .getInstanceName());

    }

    /**
     * Ensure a default, "catch-all" mapping can be defined with.
     * 
     * @throws HttpErrorPage
     * 
     */
    public void testSelectProviderExplicitDefaultMapping() throws HttpErrorPage {
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
        IncomingRequest request = IncomingRequest.builder("http://www.remote.com/notMatching").build();
        Assert.assertEquals("provider1", DriverFactory.selectProvider(request).getDriver().getConfiguration()
                .getInstanceName());
        request = IncomingRequest.builder("http://localhost:8080/provider2/test").build();
        Assert.assertEquals("provider2", DriverFactory.selectProvider(request).getDriver().getConfiguration()
                .getInstanceName());

    }

    public void testSelectProviderNoMapping() {
        Properties properties = new Properties();

        // Setup provider1
        properties.setProperty("provider1." + Parameters.REMOTE_URL_BASE.getName(), "http://example1.com");
        properties.setProperty("provider1." + Parameters.MAPPINGS.getName(), "/provider1/*");

        // Configure Esigate using the previous configuration
        DriverFactory.configure(properties);

        // Assert requests go to the right provider
        IncomingRequest request = IncomingRequest.builder("http://localhost:8080/").build();
        try {
            DriverFactory.selectProvider(request);
            fail("Should throw HttpErrorPage");
        } catch (HttpErrorPage e) {

            // Success
        }

    }

}
