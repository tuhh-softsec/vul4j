/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/test/org/apache/commons/functor/generator/util/TestEachElement.java,v 1.3 2003/11/25 17:49:35 rwaldhoff Exp $
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.functor.BaseFunctorTest;
import org.apache.commons.functor.core.Offset;

/**
 * @author Jason Horman (jason@jhorman.org)
 */

public class TestEachElement extends BaseFunctorTest {

    private List list = null;
    private Map map = null;
    private Object[] array = null;

    // Conventional
    // ------------------------------------------------------------------------

    public TestEachElement(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TestEachElement.class);
    }

    protected Object makeFunctor() throws Exception {
        return EachElement.from(new ArrayList());
    }

    // Lifecycle
    // ------------------------------------------------------------------------

    public void setUp() throws Exception {
        super.setUp();

        list = new ArrayList();
        list.add(new Integer(0));
        list.add(new Integer(1));
        list.add(new Integer(2));
        list.add(new Integer(3));
        list.add(new Integer(4));

        map = new HashMap();
        map.put("1", "1-1");
        map.put("2", "2-1");
        map.put("3", "3-1");
        map.put("4", "4-1");
        map.put("5", "5-1");

        array = new String[5];
        array[0] = "1";
        array[1] = "2";
        array[2] = "3";
        array[3] = "4";
        array[4] = "5";
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    // Tests
    // ------------------------------------------------------------------------

    public void testFromNull() {
        assertNull(EachElement.from((Collection)null));
        assertNull(EachElement.from((Map)null));
        assertNull(EachElement.from((Iterator)null));
        assertNull(EachElement.from((Object[])null));
    }


    public void testWithList() {
        Collection col = EachElement.from(list).toCollection();
        assertEquals("[0, 1, 2, 3, 4]", col.toString());
    }

    public void testWithMap() {
        List col = (List) EachElement.from(map).toCollection();
        int i = 0;
        for (;i<col.size();i++) {
            Map.Entry entry = (Map.Entry) col.get(i);
            if (entry.getKey().equals("1")) {
                assertEquals("1-1", entry.getValue());
            } else if (entry.getKey().equals("2")) {
                assertEquals("2-1", entry.getValue());
            } else if (entry.getKey().equals("3")) {
                assertEquals("3-1", entry.getValue());
            } else if (entry.getKey().equals("4")) {
                assertEquals("4-1", entry.getValue());
            } else if (entry.getKey().equals("5")) {
                assertEquals("5-1", entry.getValue());
            }
        }

        assertEquals(5, i);
    }

    public void testWithArray() {
        Collection col = EachElement.from(array).toCollection();
        assertEquals("[1, 2, 3, 4, 5]", col.toString());
    }

    public void testWithStop() {
        Collection col = EachElement.from(list).until(new Offset(3)).toCollection();
        assertEquals("[0, 1, 2]", col.toString());

    }

    public void testWithIterator() {
        Collection col = EachElement.from(list.iterator()).toCollection();
        assertEquals("[0, 1, 2, 3, 4]", col.toString());
    }

}