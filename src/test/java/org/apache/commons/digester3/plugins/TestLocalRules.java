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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for defining custom rules on the plugin class itself.
 */
public class TestLocalRules extends AbstractPluginTestCase {

    private Digester digester;

    @Before
    public void setUp() throws Exception {
        digester = newPluginDigester(new AbstractRulesModule() {

            @Override
            protected void configure() {
                forPattern("root/plugin").declarePlugin();
                forPattern("root/widget")
                    .createPlugin().ofType(Widget.class)
                    .then()
                    .setNext("addChild");
            }

        });
    }

    @After
    public void tearDown() {
        this.digester = null;
    }

    @Test
    public void testLocalRules() throws Exception {
        // * tests that the plugin class can define an addRules method,
        //   which gets detected and executed.

        Container root = new Container();
        digester.push(root);

        digester.parse(getInputStream("test4a.xml"));

        Object child;
        List<Widget> children = root.getChildren();
        assertNotNull(children);
        assertEquals(3, children.size());

        // min/max rules should be in effect
        // setproperties should be in effect
        child = children.get(0);
        assertNotNull(child);
        assertEquals(Slider.class, child.getClass());
        Slider slider1 = (Slider) child;
        assertEquals("slider1", slider1.getLabel());
        assertEquals(1, slider1.getMin());
        assertEquals(2, slider1.getMax());

        // range rules should not be in effect
        // setproperties should be in effect
        child = children.get(1);
        assertNotNull(child);
        assertEquals(Slider.class, child.getClass());
        Slider slider2 = (Slider) child;
        assertEquals("slider2", slider2.getLabel());
        assertEquals(0, slider2.getMin());
        assertEquals(0, slider2.getMax());

        // setproperties should be working on text label
        child = children.get(2);
        assertNotNull(child);
        assertEquals(TextLabel.class, child.getClass());
        assertEquals("text1", ((TextLabel)child).getLabel());
    }

    @Test
    public void testNonStandardLocalRules() throws Exception {
        // * tests that using PluginDeclarationRule to declare an alternate
        //   rule method name invokes that alternate method instead (the
        //   input xml specifies that a method other than addRules is to
        //   be used)
        // * tests that if a rule method is defined, then a SetProperties
        //   rule is not automatically added.
        // * tests that a SetProperties rule applying to one class doesn't
        //   apply to different plugin classes mounted at the same rule.

        Container root = new Container();
        digester.push(root);

        digester.parse(getInputStream("test4b.xml"));

        Object child;
        List<Widget> children = root.getChildren();
        assertNotNull(children);
        assertEquals(3, children.size());

        // min/max rules should not  be in effect
        // setproperties should not be in effect
        child = children.get(0);
        assertNotNull(child);
        assertEquals(Slider.class, child.getClass());
        Slider slider1 = (Slider) child;
        assertEquals("nolabel", slider1.getLabel());
        assertEquals(0, slider1.getMin());
        assertEquals(0, slider1.getMax());

        // range rules should be in effect
        // setproperties should not be in effect
        child = children.get(1);
        assertNotNull(child);
        assertEquals(Slider.class, child.getClass());
        Slider slider2 = (Slider) child;
        assertEquals("nolabel", slider2.getLabel());
        assertEquals(10, slider2.getMin());
        assertEquals(20, slider2.getMax());

        // setproperties should be working on text label
        child = children.get(2);
        assertNotNull(child);
        assertEquals(TextLabel.class, child.getClass());
        assertEquals("text1", ((TextLabel)child).getLabel());
    }

}
