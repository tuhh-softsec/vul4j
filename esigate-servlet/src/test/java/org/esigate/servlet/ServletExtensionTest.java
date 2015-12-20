package org.esigate.servlet;

import java.util.Properties;

import org.esigate.ConfigurationException;
import org.esigate.Driver;
import org.esigate.Parameters;
import org.junit.Assert;
import org.junit.Test;

public class ServletExtensionTest {

    @Test
    public void configurationWithLocalProviderAndBackgroudRevalidation() {
        Properties properties = new Properties();
        properties.setProperty(Parameters.EXTENSIONS.getName(), "org.esigate.servlet.ServletExtension");
        properties.setProperty(Parameters.MAPPINGS.getName(), "/*");
        properties.setProperty(Parameters.REMOTE_URL_BASE.getName(), "http://localhost");
        properties.setProperty(Parameters.STALE_WHILE_REVALIDATE.getName(), "3600");
        properties.setProperty(Parameters.MAX_ASYNCHRONOUS_WORKERS.getName(), "5");
        try {
            Driver.builder().setName("test").setProperties(properties).build();
            Assert.fail("ServletExtension should not accept staleWhileRevalidate");
        } catch (ConfigurationException e) {
            // Just what we expected
        }
    }

}
