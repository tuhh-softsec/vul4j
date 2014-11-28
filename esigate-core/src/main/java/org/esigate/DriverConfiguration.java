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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.esigate.api.BaseUrlRetrieveStrategy;
import org.esigate.impl.UriMapping;
import org.esigate.url.IpHashBaseUrlRetrieveStrategy;
import org.esigate.url.RoundRobinBaseUrlRetrieveStrategy;
import org.esigate.url.SingleBaseUrlRetrieveStrategy;
import org.esigate.url.StickySessionBaseUrlRetrieveStrategy;

/**
 * Driver configuration parameters.
 * 
 * @author Francois-Xavier Bonnet
 * @author Nicolas Richeton
 */
public class DriverConfiguration {

    private final String instanceName;
    private final String uriEncoding;
    private final String visibleBaseURL;
    private final Properties properties;
    private final boolean preserveHost;
    private final BaseUrlRetrieveStrategy baseUrlRetrieveStrategy;
    private final List<UriMapping> uriMappings;
    private boolean stripMappingPath;

    public DriverConfiguration(String instanceName, Properties props) {
        this.instanceName = instanceName;
        this.baseUrlRetrieveStrategy = getBaseUrlRetrieveSession(props);
        this.uriEncoding = Parameters.URI_ENCODING.getValue(props);
        this.preserveHost = Parameters.PRESERVE_HOST.getValue(props);
        this.visibleBaseURL = Parameters.VISIBLE_URL_BASE.getValue(props);
        this.stripMappingPath = Parameters.STRIP_MAPPING_PATH.getValue(props);
        this.uriMappings = parseMappings(props);
        properties = props;
    }

    /**
     * Read the "Mappings" parameter and create the corresponding UriMapping rules.
     * 
     * @param props
     * @return The mapping rules for this driver instance.
     */
    private static List<UriMapping> parseMappings(Properties props) {
        List<UriMapping> mappings = new ArrayList<UriMapping>();

        Collection<String> mappingsParam = Parameters.MAPPINGS.getValue(props);
        for (String mappingParam : mappingsParam) {
            mappings.add(UriMapping.create(mappingParam));
        }

        return mappings;
    }

    private BaseUrlRetrieveStrategy getBaseUrlRetrieveSession(Properties props) {
        BaseUrlRetrieveStrategy urlStrategy;
        String[] baseURLs = Parameters.REMOTE_URL_BASE.getValue(props);
        if (baseURLs.length == 0) {
            throw new ConfigurationException(Parameters.REMOTE_URL_BASE.getName()
                    + " property cannot be empty for instance '" + instanceName + "'");
        } else if (baseURLs.length == 1) {
            urlStrategy = new SingleBaseUrlRetrieveStrategy(baseURLs[0]);
        } else {
            String strategy = Parameters.REMOTE_URL_BASE_STRATEGY.getValue(props);
            if (Parameters.ROUNDROBIN.equalsIgnoreCase(strategy)) {
                urlStrategy = new RoundRobinBaseUrlRetrieveStrategy(baseURLs);
            } else if (Parameters.IPHASH.equalsIgnoreCase(strategy)) {
                urlStrategy = new IpHashBaseUrlRetrieveStrategy(baseURLs);
            } else if (Parameters.STICKYSESSION.equalsIgnoreCase(strategy)) {
                urlStrategy = new StickySessionBaseUrlRetrieveStrategy(baseURLs);
            } else {
                throw new ConfigurationException("No such BaseUrlRetrieveStrategy '" + strategy + "'");
            }
        }
        return urlStrategy;
    }

    public String getVisibleBaseURL() {
        return visibleBaseURL;
    }

    public boolean isPreserveHost() {
        return preserveHost;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public String getUriEncoding() {
        return uriEncoding;
    }

    public Properties getProperties() {
        return properties;
    }

    public BaseUrlRetrieveStrategy getBaseUrlRetrieveStrategy() {
        return baseUrlRetrieveStrategy;
    }

    /**
     * Get the URI mappings for this driver instance.
     * 
     * @return The URI mappings for this driver instance.
     */
    public List<UriMapping> getUriMappings() {
        return this.uriMappings;
    }

    /**
     * 
     * @return stripMappingPath
     */
    public boolean isStripMappingPath() {
        return stripMappingPath;
    }
}
