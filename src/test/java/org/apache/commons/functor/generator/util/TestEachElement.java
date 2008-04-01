/*
 * Copyright 2003,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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