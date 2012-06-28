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
package org.apache.xml.security.test.dom.version;

import java.security.Provider;
import java.security.Security;

/**
 * Version test.
 */
public class VersionTest extends org.junit.Assert {

    /**
     * A unit test for the algorithm below to convert a version number
     * to a double.
     */
    @org.junit.Test
    public void testConvertVersion() throws Exception {
        String version = convertVersion("1.4.4");
        assertTrue("1.44".equals(version));

        version = convertVersion("1.4.4-SNAPSHOT");
        assertTrue("1.44".equals(version));

        version = convertVersion("1.4");
        assertTrue("1.4".equals(version));
    }

    @org.junit.Test
    public void testVersion() throws Exception {
        Security.addProvider(new org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI());

        Provider provider = Security.getProvider("ApacheXMLDSig");
        assertTrue(provider != null);

        String version = System.getProperty("product.version");
        assertTrue(version != null);

        version = convertVersion(version);

        double versionD = Double.parseDouble(version);
        assertTrue(versionD == provider.getVersion());

        assertTrue(provider.getInfo().contains("Santuario"));
    }

    /**
     * Convert the version to a number that can be parsed to a double.
     * Namely, remove the "-SNAPSHOT" from the end, and convert version
     * numbers like 1.4.4 to 1.44.
     */
    private String convertVersion(String version) {

        // Remove the "-SNAPSHOT" version if it exists
        int dash = version.indexOf('-');
        if (dash != -1) {
            version = version.substring(0, dash);
        }

        // Remove the second decimal point if it exists
        int lastDot = version.lastIndexOf('.');
        if (version.indexOf('.') != lastDot) {
            String prefix = version.substring(0, lastDot);
            String suffix = version.substring(lastDot + 1, version.length());
            version = prefix.concat(suffix);
        }

        return version;
    }

}
