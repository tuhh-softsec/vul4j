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
package org.apache.xml.security.stax.crypto;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.security.stax.config.ConfigurationProperties;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

public class Merlin extends MerlinBase {

    private static final Log log = LogFactory.getLog(Merlin.class.getName());

    /**
     * This allows providing a custom class loader to load the resources, etc
     *
     * @throws java.io.IOException
     */
    public Merlin() {
        super();

        if (truststore == null) {
            InputStream cacertsIs = null;

            try {
                String cacertsPath = System.getProperty("java.home") + "/lib/security/cacerts";
                cacertsIs = new FileInputStream(cacertsPath);
                String cacertsPasswd = ConfigurationProperties.getProperty("CACertKeyStorePassword");

                truststore = KeyStore.getInstance(KeyStore.getDefaultType());
                truststore.load(cacertsIs, cacertsPasswd.toCharArray());
                loadCACerts = true;
            } catch (Exception e) {
                log.warn("CA certs could not be loaded: " + e.getMessage());
            } finally {
                if (cacertsIs != null) {
                    try {
                        cacertsIs.close();
                    } catch (IOException e) {
                        //ignore
                    }
                }
            }
        }
    }

    public String getCryptoProvider() {
        return ConfigurationProperties.getProperty("CertProvider");
    }

    public String getDefaultX509Alias() {
        return ConfigurationProperties.getProperty("DefaultX509Alias");
    }

}
