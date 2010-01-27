/* $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 


package org.apache.commons.digester;


import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.xml.sax.Locator;


/**
 * Tests that StackAction can be used to track the source location
 * of objects created from input xml stream.
 */

public class LocationTrackerTestCase extends TestCase {

    private static class LocationTracker implements StackAction {
        public Map<Object, String> locations = new HashMap<Object, String>();

        public Object onPush(Digester d, String stackName, Object o) {
            if (stackName == null) {
                // we only care about the real object stack
                
                // note that a Locator object can also provide 
                // publicId and systemId info.
                Locator l = d.getDocumentLocator();
                StringBuffer locn = new StringBuffer();
                locn.append("line=");
                locn.append(l.getLineNumber());
                locations.put(o, locn.toString());
            }
            return o;
        }

        public Object onPop(Digester d, String stackName, Object o) {
            return o;
        }
    }

    public void testAll() throws Exception {
        final String TEST_XML =
            "<?xml version='1.0'?>\n"
            + "<box id='root'>\n"
            + "  <subBox id='box1'/>\n" 
            + "  <ignoreme/>\n"
            + "  <subBox id='box2'/> <subBox id='box3'/>\n" 
            + "</box>";
        
        LocationTracker locnTracker = new LocationTracker();

        Digester digester = new Digester();
        digester.setStackAction(locnTracker);
        digester.addObjectCreate("box", Box.class);
        digester.addSetProperties("box");
        digester.addObjectCreate("box/subBox", Box.class);
        digester.addSetProperties("box/subBox");
        digester.addSetNext("box/subBox", "addChild");

        Object result = digester.parse(new StringReader(TEST_XML));
        assertNotNull(result);
        Box root = (Box) result;
        List<Box> children = root.getChildren();
        assertEquals(3, children.size());
        Box box1 = children.get(0);
        Box box2 = children.get(1);
        Box box3 = children.get(2);
        
        assertEquals("line=2", locnTracker.locations.get(root));
        assertEquals("line=3", locnTracker.locations.get(box1));
        assertEquals("line=5", locnTracker.locations.get(box2));
        assertEquals("line=5", locnTracker.locations.get(box3));
    }
}
