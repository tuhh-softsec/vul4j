/* 
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/test/org/apache/commons/functor/example/map/TestFixedSizeMap.java,v 1.1 2003/11/26 01:18:28 rwaldhoff Exp $
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived 
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.apache.commons.functor.example.map;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * @version $Revision: 1.1 $ $Date: 2003/11/26 01:18:28 $
 * @author Rodney Waldhoff
 */
public class TestFixedSizeMap extends TestCase {

    public TestFixedSizeMap(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestFixedSizeMap.class);
    }
    
    private Map baseMap = null;
    private Map fixedMap = null;
    
    public void setUp() throws Exception {
        super.setUp();
        baseMap = new HashMap();
        baseMap.put(new Integer(1),"one");
        baseMap.put(new Integer(2),"two");
        baseMap.put(new Integer(3),"three");
        baseMap.put(new Integer(4),"four");
        baseMap.put(new Integer(5),"five");
        
        fixedMap = new FixedSizeMap(baseMap);
    }
    
    public void tearDown() throws Exception {
        super.tearDown();
        baseMap = null;
        fixedMap = null;
    }

    // tests
    
    public void testCantPutNewPair() {
        try {
            fixedMap.put("xyzzy","xyzzy");
            fail("Expected IllegalArgumentException");
        } catch(IllegalArgumentException e) {
            // expected
        }    }

    public void testCantPutNewPairViaPutAll() {
        Map map = new HashMap();
        map.put(new Integer(1),"uno");
        map.put("xyzzy","xyzzy");
        map.put(new Integer(2),"dos");
        
        try {
            fixedMap.putAll(map);
            fail("Expected IllegalArgumentException");
        } catch(IllegalArgumentException e) {
            // expected
        }
        
        assertEquals("one",fixedMap.get(new Integer(1)));        
        assertEquals("two",fixedMap.get(new Integer(2)));
    }

    public void testCantClear() {
        try {
            fixedMap.clear();
            fail("Expected UnsupportedOperationException");
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    public void testCantRemove() {
        try {
            fixedMap.remove(new Integer(1));
            fail("Expected UnsupportedOperationException");
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }
        
    public void testCanAssociateNewValueWithOldKey() {
        fixedMap.put(new Integer(1),"uno");
        assertEquals("uno",fixedMap.get(new Integer(1)));        
        assertEquals("two",fixedMap.get(new Integer(2)));
        assertEquals("three",fixedMap.get(new Integer(3)));
    }

    public void testCanAssociateNewValueWithOldKeyViaPutAll() {
        Map map = new HashMap();
        map.put(new Integer(1),"uno");
        map.put(new Integer(2),"dos");

        fixedMap.putAll(map);
        
        assertEquals("uno",fixedMap.get(new Integer(1)));        
        assertEquals("dos",fixedMap.get(new Integer(2)));
        assertEquals("three",fixedMap.get(new Integer(3)));
    }


}
