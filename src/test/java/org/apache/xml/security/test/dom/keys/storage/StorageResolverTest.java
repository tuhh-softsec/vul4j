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
package org.apache.xml.security.test.dom.keys.storage;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.xml.security.keys.storage.StorageResolver;

/**
 * KeyStore StorageResolver test.
 */
public class StorageResolverTest extends org.junit.Assert {

    private static final String BASEDIR = 
        System.getProperty("basedir") == null ? "./": System.getProperty("basedir");
    private static final String SEP = System.getProperty("file.separator");

    @org.junit.Test
    public void testStorageResolver() throws Exception {

        String inputDir = BASEDIR + SEP + "src/test/resources" + SEP
            + "org" + SEP + "apache" + SEP + "xml" + SEP + "security" + SEP
            + "samples" + SEP + "input";

        FileInputStream inStream = new FileInputStream(inputDir + SEP + "keystore.jks");
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(inStream, "xmlsecurity".toCharArray());

        FileInputStream inStream2 = new FileInputStream(inputDir + SEP + "keystore2.jks");
        KeyStore ks2 = KeyStore.getInstance("JCEKS");
        ks2.load(inStream2, "xmlsecurity".toCharArray());

        StorageResolver storage = new StorageResolver(ks);
        storage.add(ks2);

        Iterator<?> iter = storage.getIterator();
        checkIterator(iter);

        // check new iterator starts from the beginning
        Iterator<?> iter2 = storage.getIterator();
        checkIterator(iter2);

        // check the iterators are independent
        // check calling next() without calling hasNext()
        iter = storage.getIterator();
        iter2 = storage.getIterator();

        while (iter.hasNext()) {
            X509Certificate cert = (X509Certificate) iter.next();
            X509Certificate cert2 = (X509Certificate) iter2.next();
            if (!cert.equals(cert2)) {
                fail("StorageResolver iterators are not independent");
            }
        }
        assertFalse(iter2.hasNext());
    }

    private void checkIterator(Iterator<?> iter) {
        int count = 0;
        iter.hasNext(); // hasNext() is idempotent

        while (iter.hasNext()) {
            X509Certificate cert = (X509Certificate) iter.next();
            cert.getSubjectX500Principal().getName();
            count++;
        }

        // The iterator skipped over symmetric keys
        assertEquals(4, count);

        // Cannot go beyond last element
        try {
            iter.next();
            fail("Expecting NoSuchElementException");
        } catch (NoSuchElementException e) {
            //
        }
    }
}

