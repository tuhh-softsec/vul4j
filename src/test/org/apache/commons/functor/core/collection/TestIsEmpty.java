/* 
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/test/org/apache/commons/functor/core/collection/TestIsEmpty.java,v 1.6 2003/12/02 17:43:11 rwaldhoff Exp $
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
package org.apache.commons.functor.core.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.functor.BaseFunctorTest;
import org.apache.commons.functor.UnaryPredicate;
import org.apache.commons.functor.core.Constant;
import org.apache.commons.functor.core.composite.UnaryNot;

/**
 * @version $Revision: 1.6 $ $Date: 2003/12/02 17:43:11 $
 * @author Rodney Waldhoff
 */
public class TestIsEmpty extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestIsEmpty(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestIsEmpty.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new IsEmpty();
    }

    // Lifecycle
    // ------------------------------------------------------------------------

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    // Tests
    // ------------------------------------------------------------------------
    
    public void testTest() throws Exception {
        assertTrue(IsEmpty.instance().test(Collections.EMPTY_LIST));
        assertTrue(IsEmpty.instance().test(Collections.EMPTY_SET));
        {
            List list = new ArrayList();
            assertTrue(IsEmpty.instance().test(list));
            list.add("Xyzzy");
            assertTrue(!IsEmpty.instance().test(list));
        }
        {
            Set set = new HashSet();
            assertTrue(IsEmpty.instance().test(set));
            set.add("Xyzzy");
            assertTrue(!IsEmpty.instance().test(set));
        }
    }

    public void testTestNull() throws Exception {
        try {
            IsEmpty.instance().test(null);
            fail("Expected NullPointerException");
        } catch(NullPointerException e) {
            // expected
        }
    }
    
    public void testTestNonCollection() throws Exception {
        try {
            IsEmpty.instance().test(new Integer(3));
            fail("Expected IllegalArgumentException");
        } catch(IllegalArgumentException e) {
            // expected
        }
    }
    
    public void testTestArray() throws Exception {
        assertTrue(! IsEmpty.instance().test(new int[10]));
        assertTrue(! IsEmpty.instance().test(new Object[10]));
        assertTrue(IsEmpty.instance().test(new int[0]));
        assertTrue(IsEmpty.instance().test(new Object[0]));
    }

    public void testTestString() throws Exception {
        assertTrue(! IsEmpty.instance().test("xyzzy"));
        assertTrue(IsEmpty.instance().test(""));
    }

    public void testTestMap() throws Exception {
        Map map = new HashMap();
        assertTrue(IsEmpty.instance().test(map));
        map.put("x","y");
        assertTrue(! IsEmpty.instance().test(map));
    }

    public void testEquals() throws Exception {
        UnaryPredicate p = new IsEmpty();
        assertEquals(p,p);
        assertObjectsAreEqual(p,new IsEmpty());
        assertObjectsAreEqual(p,IsEmpty.instance());
        assertSame(IsEmpty.instance(),IsEmpty.instance());
        assertObjectsAreNotEqual(p,new Constant(true));
        assertObjectsAreNotEqual(p,new UnaryNot(null));
    }

}
