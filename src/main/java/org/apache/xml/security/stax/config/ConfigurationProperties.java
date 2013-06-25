/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.xml.security.stax.config;

import org.apache.xml.security.configuration.PropertiesType;
import org.apache.xml.security.configuration.PropertyType;

import java.util.List;
import java.util.Properties;

/**
 * Configuration Properties
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class ConfigurationProperties {

    private static Properties properties;

    private ConfigurationProperties() {
        super();
    }

    protected static synchronized void init(PropertiesType propertiesType) throws Exception {
        properties = new Properties();
        List<PropertyType> handlerList = propertiesType.getProperty();
        for (int i = 0; i < handlerList.size(); i++) {
            PropertyType propertyType = handlerList.get(i);
            properties.setProperty(propertyType.getNAME(), propertyType.getVAL());
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
