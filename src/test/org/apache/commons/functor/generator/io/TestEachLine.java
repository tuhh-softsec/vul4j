/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/test/org/apache/commons/functor/generator/io/Attic/TestEachLine.java,v 1.1 2003/06/30 11:00:18 rwaldhoff Exp $
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

package org.apache.commons.functor.generator.io;

import org.apache.commons.functor.BaseFunctorTest;
import org.apache.commons.functor.generator.util.MaxIterations;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.io.StringReader;
import java.io.File;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.Collection;

/**
 * @author Jason Horman (jason@jhorman.org)
 */

public class TestEachLine extends BaseFunctorTest {

    private String testString = null;

    // Conventional
    // ------------------------------------------------------------------------

    public TestEachLine(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TestEachLine.class);
    }

    protected Object makeFunctor() throws Exception {
        return new EachLine(new StringReader("test string"));
    }

    // Lifecycle
    // ------------------------------------------------------------------------

    public void setUp() throws Exception {
        super.setUp();
        testString = "test1f1|test1f2|test1f3\n" +
                     "test2f1|test2f2|test2f3\n" +
                     "test3f1|test3f2|test3f3\n";
    }

    public void testConstructors() throws Exception {
        EachLine gen = EachLine.from(new File("test"));
        gen = EachLine.from(new File("test"), "encoding");

        Reader reader = new StringReader("test");
        gen = EachLine.from(reader);
        reader = gen.getReader();
        assertTrue("reader should have been wrapped by bufferedreader",
                   reader instanceof BufferedReader);
        assertEquals("test", ((BufferedReader)reader).readLine());

        reader = new BufferedReader(new StringReader("test"));
        gen = EachLine.from(reader);
        assertSame(reader, gen.getReader());
        reader = gen.getReader();
        assertEquals("test", ((BufferedReader)reader).readLine());
    }

    public void testBasicRead() {
        Collection c = EachLine.from(new StringReader(testString)).toCollection();
        assertEquals(c.toString(), "[test1f1|test1f2|test1f3, test2f1|test2f2|test2f3, test3f1|test3f2|test3f3]");
    }

    public void testStopping() {
        Collection c = EachLine.from(new StringReader(testString)).until(new MaxIterations(2)).toCollection();
        assertEquals(c.toString(), "[test1f1|test1f2|test1f3, test2f1|test2f2|test2f3]");
    }
}