/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/test/org/apache/commons/functor/generator/util/TestIntegerRange.java,v 1.1 2003/11/24 23:39:17 rwaldhoff Exp $
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
 * @version $Revision: 1.1 $ $Date: 2003/11/24 23:39:17 $
 * @author Jason Horman (jason@jhorman.org)
 * @author Rodney Waldhoff
 */
public class TestIntegerRange extends BaseFunctorTest {
    // Conventional
    // ------------------------------------------------------------------------

    public TestIntegerRange(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TestIntegerRange.class);
    }

    protected Object makeFunctor() throws Exception {
        return new IntegerRange(10, 20);
    }

    // Tests
    // ------------------------------------------------------------------------

    public void testStepChecking() {
        {
            new IntegerRange(2, 2, 0); // step of 0 is ok when range is empty
        }
        {
            new IntegerRange(2, 2, 1); // positive step is ok when range is empty
        }
        {
            new IntegerRange(2, 2, -1); // negative step is ok when range is empty
        }
        {
            new IntegerRange(0, 1, 10); // big steps are ok
        }
        {
            new IntegerRange(1, 0, -10); // big steps are ok
        }
        try {
            new IntegerRange(0, 1, 0);
            fail("Expected IllegalArgumentException");
        } catch(IllegalArgumentException e) {
            // expected 
        }  
        try {
            new IntegerRange(0, 1, -1);
            fail("Expected IllegalArgumentException");
        } catch(IllegalArgumentException e) {
            // expected 
        }  
        try {
            new IntegerRange(0, -1, 1);
            fail("Expected IllegalArgumentException");
        } catch(IllegalArgumentException e) {
            // expected 
        }  
    }

    public void testObjectConstructor() {
        IntegerRange range = new IntegerRange(new Integer(0), new Integer(5));
        assertEquals("[0, 1, 2, 3, 4, 5]", range.toCollection().toString());
        range = new IntegerRange(new Integer(0), new Integer(5), new Integer(1));
        assertEquals("[0, 1, 2, 3, 4, 5]", range.toCollection().toString());
    }


    public void testReverseStep() {
        IntegerRange range = new IntegerRange(10, 0, -2);
        assertEquals("[10, 8, 6, 4, 2, 0]", range.toCollection().toString());
        assertEquals("[10, 8, 6, 4, 2, 0]", range.toCollection().toString());
    }

    public void testStep() {
        IntegerRange range = new IntegerRange(0, 10, 2);
        assertEquals("[0, 2, 4, 6, 8, 10]", range.toCollection().toString());
        assertEquals("[0, 2, 4, 6, 8, 10]", range.toCollection().toString());
    }

    public void testForwardRange() {
        IntegerRange range = new IntegerRange(0, 5);
        assertEquals("[0, 1, 2, 3, 4, 5]", range.toCollection().toString());
        assertEquals("[0, 1, 2, 3, 4, 5]", range.toCollection().toString());
    }

    public void testReverseRange() {
        IntegerRange range = new IntegerRange(5, 0);
        assertEquals("[5, 4, 3, 2, 1, 0]", range.toCollection().toString());
        assertEquals("[5, 4, 3, 2, 1, 0]", range.toCollection().toString());
    }

    public void testEquals() {
        IntegerRange range = new IntegerRange(1, 5);
        assertObjectsAreEqual(range, range);
        assertObjectsAreEqual(range, new IntegerRange(1, 5));
        assertObjectsAreEqual(range, new IntegerRange(1, 5, 1));
        assertObjectsAreEqual(range, new IntegerRange(new Long(1), new Long(5)));
        assertObjectsAreEqual(range, new IntegerRange(new Long(1), new Long(5), new Long(1)));
    }

}