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

package org.esigate.extension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.esigate.ConfigurationException;
import org.esigate.Driver;
import org.esigate.util.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for all ESIGate extension classes (authenticator, cookie store).
 * 
 * @author Nicolas Richeton
 * @author Francois-Xavier Bonnet
 * 
 */
public final class ExtensionFactory {
    private static final Logger LOG = LoggerFactory.getLogger(ExtensionFactory.class);

    private ExtensionFactory() {

    }

    /**
     * Get an extension as configured in properties.
     * 
     * @param properties
     * @param parameter
     * @param d
     * 
     * @param <T>
     *            class which extends Extension class which extends Extension
     * @return instance of {@link Extension} or null.
     */
    public static <T extends Extension> T getExtension(Properties properties, Parameter parameter, Driver d) {
        T result = null;
        String className = parameter.getValueString(properties);
        if (className == null) {
            return null;
        }
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Creating  extension " + className);
            }
            result = (T) Class.forName(className).newInstance();
            result.init(d, properties);
        } catch (InstantiationException e) {
            throw new ConfigurationException(e);
        } catch (IllegalAccessException e) {
            throw new ConfigurationException(e);
        } catch (ClassNotFoundException e) {
            throw new ConfigurationException(e);
        }
        return result;
    }

    /**
     * Get an extension list, as defined in the properties (comma-separated list).
     * 
     * @param properties
     * @param parameter
     * @param d
     * @return the extension list
     */
    public static <T extends Extension> List<T> getExtensions(Properties properties, Parameter parameter, Driver d) {
        Collection<String> className = parameter.getValueList(properties);
        if (className == null) {
            return null;
        }
        List<T> finalResult = new ArrayList<T>();
        for (String cName : className) {
            try {
                T result = null;

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Creating  extension " + className);
                }
                result = (T) Class.forName(cName).newInstance();
                result.init(d, properties);
                finalResult.add(result);
            } catch (InstantiationException e) {
                throw new ConfigurationException(e);
            } catch (IllegalAccessException e) {
                throw new ConfigurationException(e);
            } catch (ClassNotFoundException e) {
                throw new ConfigurationException(e);
            }
        }
        return finalResult;
    }

}
