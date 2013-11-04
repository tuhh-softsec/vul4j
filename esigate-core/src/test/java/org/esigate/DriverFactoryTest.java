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
import java.net.URL;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.http.HttpRequest;
import org.esigate.test.TestUtils;
import org.esigate.util.HttpRequestHelper;

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

        HttpRequest request = TestUtils.createRequest();
        instance.initHttpRequestParams(request, null);
        assertEquals("http://base.url", HttpRequestHelper.getBaseUrl(request).toString());
    }

    public void testMergeProperties() {
        String defaultBaseUrl = "http://basedefault.url";
        String extendedBaseUrl = "http://baseextended.url";

        Properties defaultProps = new Properties();
        defaultProps.setProperty("default." + Parameters.REMOTE_URL_BASE.getName(), defaultBaseUrl);
        defaultProps.setProperty("default." + Parameters.URI_ENCODING.getName(), "ISO-8859-1");

        Properties extendedProps = new Properties();
        extendedProps.setProperty("default." + Parameters.REMOTE_URL_BASE.getName(), extendedBaseUrl);
        extendedProps.setProperty("default." + Parameters.URI_ENCODING.getName(), "UTF-8");

        URL dir = this.getClass().getResource("DriverFactoryTest.class");
        File file = new File(dir.getPath());
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
}
