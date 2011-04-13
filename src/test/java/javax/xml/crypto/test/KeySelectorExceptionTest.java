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
/*
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 */
package javax.xml.crypto.test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import javax.xml.crypto.KeySelectorException;

/**
 * Unit test for javax.xml.crypto.KeySelectorException
 *
 * @author Valerie Peng
 */
public class KeySelectorExceptionTest extends org.junit.Assert {

    @org.junit.Test
    public void testConstructor() {
        // test KeySelectorException()
        KeySelectorException kse = new KeySelectorException();
        assertNull(kse.getMessage());
        assertNull(kse.getCause());
        
        // test KeySelectorException(String)
        kse = new KeySelectorException("test");
        assertEquals("test", kse.getMessage());
        assertNull(kse.getCause());
        
        // test KeySelectorException(String, Throwable)
        IllegalArgumentException iae = new IllegalArgumentException("iae");
        kse = new KeySelectorException("random", iae);
        assertEquals("random", kse.getMessage());
        assertTrue(compareThrowable(iae, kse.getCause()));

        // test KeySelectorException(Throwable)
        kse = new KeySelectorException(iae);
        assertEquals(iae.toString(), kse.getMessage());
        assertTrue(compareThrowable(iae, kse.getCause()));
    }
        
    private static boolean compareThrowable(Throwable t1, Throwable t2) {
        boolean result = false;
        // first compare their toString presentation
        if (t1.toString().equals(t2.toString())) {
            ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
            ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
            // then compare their StackTrace
            PrintStream ps = new PrintStream(baos1);
            t1.printStackTrace(ps);
            ps.close();
            ps = new PrintStream(baos2);
            t2.printStackTrace(ps);
            ps.close();

            if (Arrays.equals(baos1.toByteArray(), baos2.toByteArray())) {
                result = true;
            } else {
                System.out.println("StackTrace comparison failed");
                t1.printStackTrace(System.out);
                t2.printStackTrace(System.out);
            }
        } else {
            System.out.println("ToString comparison failed");
            System.out.println(t1);
            System.out.println(t2);
        }
        return result;
    }
}
