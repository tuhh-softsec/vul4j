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
package org.apache.commons.digester3.plugins;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.apache.commons.digester3.AbstractRulesModule;
import org.apache.commons.digester3.Digester;
import org.junit.Test;

/**
 * Test cases for basic PluginDeclarationRule behaviour.
 */
public class TestDeclaration extends AbstractPluginTestCase {

    @Test
    public void testPredeclaration() throws Exception {
        // * tests that rules can be declared via a PluginDeclarationRule

        Digester digester = newPluginDigester(new AbstractRulesModule() {

            @Override
            protected void configure() {
                forPattern("root/plugin").declarePlugin();
                forPattern("root/widget")
                    .createPlugin().ofType(Widget.class)
                    .then()
                    .setNext("addChild");
            }

        });

        Container root = new Container();
        digester.push(root);

        digester.parse(getInputStream("test3.xml"));

        Object child;
        List<Widget> children = root.getChildren();
        assertNotNull(children);
        assertEquals(2, children.size());

        child = children.get(0);
        assertNotNull(child);
        assertEquals(TextLabel.class, child.getClass());
        assertEquals("label1", ((TextLabel)child).getLabel());

        child = children.get(1);
        assertNotNull(child);
        assertEquals(TextLabel.class, child.getClass());
        assertEquals("label2", ((TextLabel)child).getLabel());
    }

}
