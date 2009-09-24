package net.webassembletool;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

/**
 * Factory class used to configure and retrieve {@linkplain Driver} INSTANCIES.
 * 
 * @author Stanislav Bernatskyi
 */
public class DriverFactory {
    private static final String DEFAULT_INSTANCE = "default";
    private static final Map<String, Driver> INSTANCIES = new HashMap<String, Driver>();

    static {
	// Load default settings
	configure();
    }

    /** Loads all instancies according to default configuration file */
    public final static void configure() {
	try {
	    InputStream inputStream = Driver.class
		    .getResourceAsStream("driver.properties");
	    if (inputStream != null) {
		Properties props = new Properties();
		props.load(inputStream);
		configure(props);
	    }
	} catch (IOException e) {
	    throw new ConfigurationException(e);
	}
    }

    /**
     * Loads all instancies according to the properties parameter
     * 
     * @param props properties to use for configuration
     */
    public final static void configure(Properties props) {
	INSTANCIES.clear();
	HashMap<String, Properties> driversProps = new HashMap<String, Properties>();
	for (Enumeration<?> enumeration = props.propertyNames(); enumeration
		.hasMoreElements();) {
	    String propertyName = (String) enumeration.nextElement();
	    String prefix;
	    String name;
	    if (propertyName.indexOf(".") < 0) {
		prefix = DEFAULT_INSTANCE;
		name = propertyName;
	    } else {
		prefix = propertyName.substring(0, propertyName
			.lastIndexOf("."));
		name = propertyName
			.substring(propertyName.lastIndexOf(".") + 1);
	    }
	    Properties driverProperties = driversProps.get(prefix);
	    if (driverProperties == null) {
		driverProperties = new Properties();
		driversProps.put(prefix, driverProperties);
	    }
	    driverProperties.put(name, props.getProperty(propertyName));
	}
	for (Entry<String, Properties> entry : driversProps.entrySet()) {
	    String name = entry.getKey();
	    Properties driverProperties = entry.getValue();
	    INSTANCIES.put(name, new Driver(name, driverProperties));
	}
    }

    /**
     * Retrieves the default instance of this class that is configured according
     * to the properties file (driver.properties)
     * 
     * @return the default instance
     */
    public final static Driver getInstance() {
	return getInstance(DEFAULT_INSTANCE);
    }

    /**
     * Retrieves the default instance of this class that is configured according
     * to the properties file (driver.properties)
     * 
     * @param instanceName The name of the instance (corresponding to the prefix
     *            in the driver.properties file)
     * 
     * @return the named instance
     */
    public final static Driver getInstance(String instanceName) {
	if (INSTANCIES.isEmpty())
	    throw new ConfigurationException(
		    "Driver has not been configured and driver.properties file was not found");
	String effectiveInstanceName = instanceName;
	if (effectiveInstanceName == null)
		effectiveInstanceName = DEFAULT_INSTANCE;
	Driver instance = INSTANCIES.get(effectiveInstanceName);
	if (instance == null)
	    throw new ConfigurationException(
		    "No configuration properties found for factory : "
			    + effectiveInstanceName);
	return instance;
    }
    
    /**
     * Method used to inject providers. Usefull mainly for unit testing purpose
     * 
     * @param instanceName The name of the provider
     * @param instance The instance
     */
    final static void put(String instanceName, Driver instance){
    	INSTANCIES.put(instanceName, instance);
    }
}
