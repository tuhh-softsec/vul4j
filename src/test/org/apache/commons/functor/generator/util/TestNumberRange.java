/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/test/org/apache/commons/functor/generator/util/Attic/TestNumberRange.java,v 1.1 2003/06/30 11:00:13 rwaldhoff Exp $
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

package org.apache.commons.functor.generator.util;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.functor.BaseFunctorTest;

/**
 * @author Jason Horman (jason@jhorman.org)
 */

public class TestNumberRange extends BaseFunctorTest {
    // Conventional
    // ------------------------------------------------------------------------

    public TestNumberRange(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TestNumberRange.class);
    }

    protected Object makeFunctor() throws Exception {
        return NumberRange.from(10, 20);
    }

    // Tests
    // ------------------------------------------------------------------------

    public void testCreation() {
        assertEquals(NumberRange.from(10, 20), NumberRange.from(new Integer(10), new Integer(20)));
        assertEquals(NumberRange.from(10, 20), new NumberRange.IntegerRange(10, 20));
        assertEquals(new NumberRange.IntegerRange(10, 20), new NumberRange.IntegerRange(new Integer(10), new Integer(20)));

        assertEquals(NumberRange.from((long)10, (long)20), NumberRange.from(new Long(10), new Long(20)));
        assertEquals(NumberRange.from((long)10, (long)20), new NumberRange.LongRange(10, 20));
        assertEquals(new NumberRange.LongRange(10, 20), new NumberRange.LongRange(new Long(10), new Long(20)));

        NumberRange range = NumberRange.from(10, 20);
        assertEquals(new Integer(10), range.getMin());
        assertEquals(new Integer(20), range.getMax());

        assertTrue(!NumberRange.from(10, 20).equals(new NumberRange.IntegerRange(11, 21)));
        assertTrue(!NumberRange.from(10, 20).equals(new NumberRange.IntegerRange(11, 20)));
        assertTrue(!NumberRange.from(10, 20).equals(new NumberRange.IntegerRange(10, 21)));

        try {
            new NumberRange.IntegerRange(null, null);
            fail("should have thrown IllegalArgumentException");
        } catch(IllegalArgumentException e) {
            // good
        }
    }

    public void testIntRange() {
        NumberRange range = NumberRange.from(0, 5);
        assertEquals("[0, 1, 2, 3, 4, 5]", range.toCollection().toString());
    }

    public void testIntIsWithinRange() {
        NumberRange range = NumberRange.from(0, 5);
        assertTrue(range.isWithinRange(new Integer(3)));
        assertTrue(range.isWithinRange(new Integer(0)));
        assertTrue(range.isWithinRange(new Integer(5)));
        assertTrue(!range.isWithinRange(new Integer(-1)));
        assertTrue(!range.isWithinRange(new Integer(6)));

        range = NumberRange.from(0, -5);
        assertTrue(range.isWithinRange(new Integer(-3)));
        assertTrue(range.isWithinRange(new Integer(-5)));
        assertTrue(range.isWithinRange(new Integer(0)));
        assertTrue(!range.isWithinRange(new Integer(-6)));
        assertTrue(!range.isWithinRange(new Integer(1)));
    }

    public void testIntReverseRange() {
        NumberRange range = NumberRange.from(0, -5);
        assertEquals("[0, -1, -2, -3, -4, -5]", range.toCollection().toString());
    }

    public void testLongRange() {
        NumberRange range = NumberRange.from((long)0, (long)5);
        assertEquals("[0, 1, 2, 3, 4, 5]", range.toCollection().toString());
    }

    public void testLongIsWithinRange() {
        NumberRange range = NumberRange.from((long)0, (long)5);
        assertTrue(range.isWithinRange(new Long(3)));
        assertTrue(range.isWithinRange(new Long(0)));
        assertTrue(range.isWithinRange(new Long(5)));
        assertTrue(!range.isWithinRange(new Long(-1)));
        assertTrue(!range.isWithinRange(new Long(6)));

        range = NumberRange.from((long)0, (long)-5);
        assertTrue(range.isWithinRange(new Long(-3)));
        assertTrue(range.isWithinRange(new Long(-5)));
        assertTrue(range.isWithinRange(new Long(0)));
        assertTrue(!range.isWithinRange(new Long(-6)));
        assertTrue(!range.isWithinRange(new Long(1)));
    }

    public void testLongReverseRange() {
        NumberRange range = NumberRange.from((long)0, (long)-5);
        assertEquals("[0, -1, -2, -3, -4, -5]", range.toCollection().toString());
    }

    public void testLongStringIsOverridden() throws Exception {
        Object obj = new NumberRange.LongRange(10, 20);
        assertTrue(obj.toString().indexOf("LongRange") != -1);
    }
}