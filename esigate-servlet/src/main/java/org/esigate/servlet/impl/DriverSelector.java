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
package org.esigate.servlet.impl;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.tuple.Pair;
import org.esigate.Driver;
import org.esigate.DriverFactory;
import org.esigate.HttpErrorPage;
import org.esigate.impl.UriMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles support of legacy options for Driver selection.
 * <p/>
 * Configuration is evaluated in the following order :
 * <ol>
 * <li>providers mapping (host-based) in web.xml</li>
 * <li>provider mapping (servlet based) in web.xml</li>
 * <li>provider mapping in configuration file (eg. instance.mappings=/* in esigate.properties)</li>
 * </ol>
 * 
 * @author Nicolas Richeton
 */
public final class DriverSelector {
    private static final Logger LOG = LoggerFactory.getLogger(DriverSelector.class);

    public DriverSelector() {
    }

    /**
     * Select the provider for this request.
     * <p/>
     * Perform selection based on the Host header.
     * 
     * @param request
     *            incoming request
     * @return Pair Driver/UriMapping
     * @throws HttpErrorPage
     */
    public ProviderContext selectProvider(HttpServletRequest request) throws HttpErrorPage {
        return selectProvider(request, false);
    }

    /**
     * Select the provider for this request.
     * <p/>
     * Perform selection based on the incoming request url.
     * 
     * @param request
     *            incoming request
     * @param servlet
     * @return provider name or null.
     * @throws HttpErrorPage
     */
    public ProviderContext selectProvider(HttpServletRequest request, boolean servlet) throws HttpErrorPage {

        String host = request.getHeader("Host");
        String scheme = request.getScheme();

        String relUrl = RequestUrl.getRelativeUrl(request, servlet);

        Pair<Driver, UriMapping> result = DriverFactory.getInstanceFor(scheme, host, relUrl);

        ProviderContext context = new ProviderContext();
        Driver driver = result.getLeft();
        UriMapping uriMapping = result.getRight();

        if (driver.getConfiguration().isStripMappingPath()) {
            relUrl = RequestUrl.stripMappingPath(relUrl, uriMapping);
        }
        context.driver = driver;
        context.relUrl = relUrl;
        LOG.debug("Selected {} for scheme:{} host:{} relUrl:{}", result, scheme, host, relUrl);

        return context;
    }

    /**
     * The provider context, composed of driver and remote relative url
     */
    public class ProviderContext {
        private Driver driver;
        private String relUrl;

        public String getRelUrl() {
            return relUrl;
        }

        public Driver getDriver() {
            return driver;
        }

    }
}
