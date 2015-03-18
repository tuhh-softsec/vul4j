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

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.esigate.Driver.DriverBuilder;
import org.esigate.http.IncomingRequest;
import org.esigate.impl.IndexedInstances;
import org.esigate.impl.UriMapping;
import org.esigate.util.UriUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory class used to configure and retrieve {@linkplain Driver} INSTANCIES.
 * 
 * @author Stanislav Bernatskyi
 * @author Francois-Xavier Bonnet
 * @author Nicolas Richeton
 */
public final class DriverFactory {

    /**
     * The provider context, composed of driver and remote relative url.
     */
    static final class MatchedRequest {
        private final Driver driver;
        private final String relativeUri;

        private MatchedRequest(Driver driver, String relativeUri) {
            this.driver = driver;
            this.relativeUri = relativeUri;
        }

        String getRelativeUri() {
            return relativeUri;
        }

        Driver getDriver() {
            return driver;
        }

    }

    /**
     * System property used to specify of esigate configuration, outside of the classpath.
     */
    public static final String PROP_CONF_LOCATION = "esigate.config";
    private static IndexedInstances instances = new IndexedInstances(new HashMap<String, Driver>());
    private static final String DEFAULT_INSTANCE_NAME = "default";
    private static final Logger LOG = LoggerFactory.getLogger(DriverFactory.class);

    static {
        String version =
                defaultIfBlank(DriverFactory.class.getPackage().getSpecificationVersion(), "development version");
        String rev = defaultIfBlank(DriverFactory.class.getPackage().getImplementationVersion(), "unknown");
        LOG.info("Starting esigate {} rev. {}", version, rev);
    }

    private DriverFactory() {
        // Do not instantiate
    }

    /**
     * Returns the collection of all {@link Driver} instances.
     * 
     * @return All configured driver
     */
    public static Collection<Driver> getInstances() {
        DriverFactory.ensureConfigured();
        return instances.getInstances().values();

    }

    /**
     * Loads all instances according to default configuration file.
     */
    public static void configure() {
        InputStream inputStream = null;
        InputStream extInputStream = null;

        try {
            URL configUrl = getConfigUrl();

            if (configUrl == null) {
                throw new ConfigurationException("esigate.properties configuration file "
                        + "was not found in the classpath");
            }

            inputStream = configUrl.openStream();

            // load driver-ext.properties if exists
            LOG.info("Scanning configuration {}", "/esigate-ext.properties");
            extInputStream = DriverFactory.class.getClassLoader().getResourceAsStream("/esigate-ext.properties");

            // For backward compatibility
            if (extInputStream == null) {
                LOG.info("Scanning configuration {}", "/driver-ext.properties");
                extInputStream = DriverFactory.class.getResourceAsStream("/driver-ext.properties");
            }
            if (extInputStream == null) {
                LOG.info("Scanning configuration /{}/{}", DriverFactory.class.getPackage().getName().replace(".", "/"),
                        "driver-ext.properties");
                extInputStream = DriverFactory.class.getResourceAsStream("driver-ext.properties");
            }

            Properties merged = new Properties();
            if (inputStream != null) {
                Properties props = new Properties();
                props.load(inputStream);
                merged.putAll(props);
            }

            if (extInputStream != null) {
                Properties extProps = new Properties();
                extProps.load(extInputStream);
                merged.putAll(extProps);
            }

            configure(merged);
        } catch (IOException e) {
            throw new ConfigurationException("Error loading configuration", e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (extInputStream != null) {
                    extInputStream.close();
                }
            } catch (IOException e) {
                throw new ConfigurationException("failed to close stream", e);
            }
        }
    }

    /**
     * Loads all instances according to the properties parameter.
     * 
     * @param props
     *            properties to use for configuration
     */
    public static void configure(Properties props) {
        Properties defaultProperties = new Properties();

        HashMap<String, Properties> driversProps = new HashMap<String, Properties>();
        for (Enumeration<?> enumeration = props.propertyNames(); enumeration.hasMoreElements();) {
            String propertyName = (String) enumeration.nextElement();
            String value = props.getProperty(propertyName);
            int idx = propertyName.lastIndexOf('.');
            if (idx < 0) {
                defaultProperties.put(propertyName, value);
            } else {
                String prefix = propertyName.substring(0, idx);
                String name = propertyName.substring(idx + 1);
                Properties driverProperties = driversProps.get(prefix);
                if (driverProperties == null) {
                    driverProperties = new Properties();
                    driversProps.put(prefix, driverProperties);
                }
                driverProperties.put(name, value);
            }
        }

        // Merge with default properties
        Map<String, Driver> newInstances = new HashMap<String, Driver>();
        for (Entry<String, Properties> entry : driversProps.entrySet()) {
            String name = entry.getKey();
            Properties properties = new Properties();
            properties.putAll(defaultProperties);
            properties.putAll(entry.getValue());
            newInstances.put(name, createDriver(name, properties));
        }
        if (newInstances.get(DEFAULT_INSTANCE_NAME) == null
                && Parameters.REMOTE_URL_BASE.getValue(defaultProperties) != null) {

            newInstances.put(DEFAULT_INSTANCE_NAME, createDriver(DEFAULT_INSTANCE_NAME, defaultProperties));
        }

        instances = new IndexedInstances(newInstances);
    }

    private static Driver createDriver(String name, Properties properties) {
        DriverBuilder builder = Driver.builder().setName(name).setProperties(properties);
        return builder.build();
    }

    /**
     * Registers new {@linkplain Driver} under provided name with specified properties.
     * 
     * @param name
     *            the name of the instance
     * @param props
     *            the {@link Properties} for the instance
     */
    public static void configure(String name, Properties props) {
        put(name, createDriver(name, props));
    }

    /**
     * Retrieves the default instance of this class that is configured according to the properties file
     * (driver.properties).
     * 
     * @param instanceName
     *            The name of the instance (corresponding to the prefix in the driver.properties file)
     * 
     * @return the named instance
     */
    public static Driver getInstance(String instanceName) {
        if (instanceName == null) {
            instanceName = DEFAULT_INSTANCE_NAME;
        }
        if (instances.getInstances().isEmpty()) {
            throw new ConfigurationException("Driver has not been configured and driver.properties file was not found");
        }
        Driver instance = instances.getInstances().get(instanceName);
        if (instance == null) {
            throw new ConfigurationException("No configuration properties found for factory : " + instanceName);
        }
        return instance;
    }

    /**
     * Selects the Driver instance for this request based on the mappings declared in the configuration.
     * 
     * @param request
     *            the incoming request
     * 
     * @return a {@link MatchedRequest} containing the {@link Driver} instance and the relative URI
     * 
     * @throws HttpErrorPage
     *             if no instance was found for this request
     */
    static MatchedRequest selectProvider(IncomingRequest request) throws HttpErrorPage {
        URI requestURI = UriUtils.createURI(request.getRequestLine().getUri());
        String host = UriUtils.extractHost(requestURI).toHostString();
        Header hostHeader = request.getFirstHeader(HttpHeaders.HOST);
        if (hostHeader != null) {
            host = hostHeader.getValue();
        }
        String scheme = requestURI.getScheme();
        String relativeUri = requestURI.getPath();
        String contextPath = request.getContextPath();
        if (!StringUtils.isEmpty(contextPath) && relativeUri.startsWith(contextPath)) {
            relativeUri = relativeUri.substring(contextPath.length());
        }

        Driver driver = null;
        UriMapping uriMapping = null;
        for (UriMapping mapping : instances.getUrimappings().keySet()) {
            if (mapping.matches(scheme, host, relativeUri)) {
                driver = getInstance(instances.getUrimappings().get(mapping));
                uriMapping = mapping;
                break;
            }
        }
        if (driver == null) {
            throw new HttpErrorPage(HttpStatus.SC_NOT_FOUND, "Not found", "No mapping defined for this URI.");
        }

        if (driver.getConfiguration().isStripMappingPath()) {
            relativeUri = DriverFactory.stripMappingPath(relativeUri, uriMapping);
        }

        MatchedRequest context = new MatchedRequest(driver, relativeUri);
        LOG.debug("Selected {} for scheme:{} host:{} relUrl:{}", driver, scheme, host, relativeUri);
        return context;
    }

    /**
     * Retrieves the default instance of this class that is configured according to the properties file
     * (driver.properties).
     * 
     * @return the default instance
     */
    public static Driver getInstance() {
        return getInstance(DEFAULT_INSTANCE_NAME);
    }

    /**
     * Add/replace instance in current instance map. Work on a copy of the current map and replace it atomically.
     * 
     * @param instanceName
     *            The name of the provider
     * @param instance
     *            The instance
     */
    public static void put(String instanceName, Driver instance) {
        // Copy current instances
        Map<String, Driver> newInstances = new HashMap<String, Driver>();
        synchronized (instances) {
            Set<String> keys = instances.getInstances().keySet();

            for (String key : keys) {
                newInstances.put(key, instances.getInstances().get(key));
            }
        }

        // Add new instance
        newInstances.put(instanceName, instance);

        instances = new IndexedInstances(newInstances);
    }

    /**
     * Ensure configuration has been loaded at least once. Helps to prevent delay on first call because of
     * initialization.
     */
    public static void ensureConfigured() {
        if (instances.getInstances().isEmpty()) {
            // Load default settings
            configure();
        }
    }

    /**
     * Returns the {@link URL} of the configuration file.
     * 
     * @return The URL of the configuration file.
     */
    public static URL getConfigUrl() {
        URL configUrl = null;
        // Load from environment
        String envPath = System.getProperty(PROP_CONF_LOCATION);
        if (envPath != null) {
            try {
                LOG.info("Scanning configuration {}", envPath);
                configUrl = new File(envPath).toURI().toURL();
            } catch (MalformedURLException e) {
                LOG.error("Can't read file {} (from -D" + PROP_CONF_LOCATION + ")", envPath, e);
            }
        }

        if (configUrl == null) {
            LOG.info("Scanning configuration {}", "/esigate.properties");
            configUrl = DriverFactory.class.getResource("/esigate.properties");
        }

        // For backward compatibility
        if (configUrl == null) {
            LOG.info("Scanning configuration /{}/{}", DriverFactory.class.getPackage().getName().replace(".", "/"),
                    "driver.properties");
            configUrl = DriverFactory.class.getResource("driver.properties");
        }
        if (configUrl == null) {
            LOG.info("Scanning configuration {}", "/net/webassembletool/driver.properties");
            configUrl = DriverFactory.class.getResource("/net/webassembletool/driver.properties");
        }
        return configUrl;
    }

    /**
     * Get the relative url without the mapping url.
     * <p/>
     * Uses the url and remove the mapping path.
     * 
     * @param url
     *            incoming relative url
     * @return the url, relative to the driver remote url.
     */
    static String stripMappingPath(String url, UriMapping mapping) {
        String relativeUrl = url;
        // Uri mapping
        String mappingPath;
        if (mapping == null) {
            mappingPath = null;
        } else {
            mappingPath = mapping.getPath();
        }

        // Remove mapping path
        if (mappingPath != null && url.startsWith(mappingPath)) {
            relativeUrl = relativeUrl.substring(mappingPath.length());
        }
        return relativeUrl;
    }

    /**
     * Selects the Driver instance for this request based on the mappings declared in the configuration and executes it
     * against the selected {@link Driver} instance.
     * 
     * @param incomingRequest
     *            the incoming request
     * 
     * @return a {@link MatchedRequest} containing the {@link Driver} instance and the relative URI
     * 
     * @throws HttpErrorPage
     *             if no instance was found for this request or if an error occurs
     * @throws IOException
     *             if an error occurs
     */
    public static CloseableHttpResponse proxy(IncomingRequest incomingRequest) throws IOException, HttpErrorPage {
        MatchedRequest matchedRequest = selectProvider(incomingRequest);
        return matchedRequest.getDriver().proxy(matchedRequest.getRelativeUri(), incomingRequest);
    }

}
