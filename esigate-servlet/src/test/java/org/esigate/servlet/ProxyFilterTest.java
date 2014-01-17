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

package org.esigate.servlet;

import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import junit.framework.Assert;

import org.esigate.DriverFactory;
import org.esigate.HttpErrorPage;
import org.esigate.Parameters;
import org.esigate.servlet.impl.DriverSelector;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test the proxy servlet for driver selection (url mapping) for both web.xml configuration (legacy) and
 * esigate.properties.
 * 
 * @author Nicolas Richeton
 * 
 */
public class ProxyFilterTest {

    protected class TestServletConfig implements FilterConfig {
        @Override
        public String getFilterName() {
            return "aggregator";
        }

        @Override
        public ServletContext getServletContext() {
            return null;
        }

        @Override
        public Enumeration getInitParameterNames() {
            return null;
        }

        @Override
        public String getInitParameter(String name) {
            if (name.equals("provider")) {
                return "single";
            }

            if (name.equals("providers")) {
                return "suB.domaiN.com=provider1,sub2.domAin.com=provider2";
            }

            return null;
        }
    }

    /**
     * Test provider selection based on esigate.properties configuration.
     * 
     * @throws HttpErrorPage
     */
    @Test
    public void testProviderSelectionEsigate() throws HttpErrorPage {

        // Setup Esigate
        Properties p = new Properties();
        p.setProperty("provider1." + Parameters.REMOTE_URL_BASE, "http://test");
        p.setProperty("provider1." + Parameters.MAPPINGS, "http://sub.domain.com/*");
        p.setProperty("provider2." + Parameters.REMOTE_URL_BASE, "http://test");
        p.setProperty("provider2." + Parameters.MAPPINGS, "http://sub2.domain.com/*");
        p.setProperty("single." + Parameters.REMOTE_URL_BASE, "http://test");
        p.setProperty("single." + Parameters.MAPPINGS, "*");
        DriverFactory.configure(p);

        ProxyFilter proxy = new ProxyFilter();
        FilterConfig conf = Mockito.mock(FilterConfig.class);
        Mockito.when(conf.getInitParameter("useMappings")).thenReturn("true");
        proxy.init(conf);

        DriverSelector driverSelector = new DriverSelector();

        // Do testing
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getContextPath()).thenReturn("/test");
        Mockito.when(request.getRequestURI()).thenReturn("/test/servlet/request");
        Mockito.when(request.getProtocol()).thenReturn("HTTP/1.1");
        Mockito.when(request.getMethod()).thenReturn("GET");
        Mockito.when(request.getServletPath()).thenReturn("servlet");
        Mockito.when(request.getHeader("Host")).thenReturn("sub2.domain.com");
        Mockito.when(request.getScheme()).thenReturn("http");
        Assert.assertEquals("provider2", driverSelector.selectProvider(request, true).getLeft().getConfiguration()
                .getInstanceName());

        request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getContextPath()).thenReturn("/test");
        Mockito.when(request.getRequestURI()).thenReturn("/test/servlet/request");
        Mockito.when(request.getProtocol()).thenReturn("HTTP/1.1");
        Mockito.when(request.getMethod()).thenReturn("GET");
        Mockito.when(request.getServletPath()).thenReturn("servlet");
        Mockito.when(request.getHeader("Host")).thenReturn("sub.domain.com");
        Mockito.when(request.getScheme()).thenReturn("http");
        Assert.assertEquals("provider1", driverSelector.selectProvider(request, true).getLeft().getConfiguration()
                .getInstanceName());

        request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getContextPath()).thenReturn("/test");
        Mockito.when(request.getRequestURI()).thenReturn("/test/servlet/request");
        Mockito.when(request.getProtocol()).thenReturn("HTTP/1.1");
        Mockito.when(request.getMethod()).thenReturn("GET");
        Mockito.when(request.getServletPath()).thenReturn("servlet");
        Mockito.when(request.getScheme()).thenReturn("http");
        Assert.assertEquals("single", driverSelector.selectProvider(request, true).getLeft().getConfiguration()
                .getInstanceName());

    }

}
