/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/test/org/apache/commons/functor/core/collection/TestIsElementOf.java,v 1.7 2003/12/02 17:43:11 rwaldhoff Exp $
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

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.functor.BaseFunctorTest;
import org.apache.commons.functor.UnaryPredicate;
import org.apache.commons.functor.core.Constant;

/**
 * @version $Revision: 1.7 $ $Date: 2003/12/02 17:43:11 $
 * @author Rodney Waldhoff
 * @author Jason Horman
 */
public class TestIsElementOf extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestIsElementOf(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestIsElementOf.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new IsElementOf();
    }

    // Lifecycle
    // ------------------------------------------------------------------------

    // Tests
    // ------------------------------------------------------------------------

    public void testTestCollection() throws Exception {
        ArrayList list = new ArrayList();
        list.add(new Integer(5));
        list.add(new Integer(10));
        list.add(new Integer(15));

        UnaryPredicate p = IsElementOf.instance(list);
        assertTrue(p.test(new Integer(5)));
        assertTrue(p.test(new Integer(10)));
        assertTrue(p.test(new Integer(15)));

        assertTrue(!p.test(new Integer(4)));
        assertTrue(!p.test(new Integer(11)));

    }

    public void testTestArray() throws Exception {
        int[] list = new int[] { 5, 10, 15 };

        UnaryPredicate p = IsElementOf.instance(list);
        assertTrue(p.test(new Integer(5)));
        assertTrue(p.test(new Integer(10)));
        assertTrue(p.test(new Integer(15)));

        assertTrue(!p.test(new Integer(4)));
        assertTrue(!p.test(new Integer(11)));
    }

    public void testTestArrayWithNull() throws Exception {
        assertTrue(! IsElementOf.instance().test(null,new int[] { 5, 10, 15 }));
        assertTrue(IsElementOf.instance().test(null,new Integer[] { new Integer(5), null, new Integer(15) }));
        assertTrue(IsElementOf.instance().test(new Integer(15),new Integer[] { new Integer(5), null, new Integer(15) }));
    }

    public void testWrapNull() {
        try {
            IsElementOf.instance(null);
            fail("expected NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
    }

    public void testWrapNonCollection() {
        try {
            IsElementOf.instance(new Integer(3));
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testTestNull() {
        try {
            IsElementOf.instance().test(new Integer(5),null);
            fail("expected NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
    }

    public void testTestNonCollection() {
        try {
            IsElementOf.instance().test(new Integer(5),new Long(5));
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testEquals() throws Exception {
        IsElementOf p1 = new IsElementOf();
        assertObjectsAreEqual(p1, p1);
        assertObjectsAreEqual(p1, new IsElementOf());
        assertObjectsAreEqual(p1, IsElementOf.instance());
        assertSame(IsElementOf.instance(), IsElementOf.instance());
        assertObjectsAreNotEqual(p1, Constant.falseInstance());
    }
}
