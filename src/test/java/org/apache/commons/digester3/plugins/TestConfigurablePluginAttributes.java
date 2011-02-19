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

import static org.apache.commons.digester3.DigesterLoader.newLoader;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.digester3.AbstractRulesModule;
import org.apache.commons.digester3.Digester;
import org.junit.Test;

/**
 * Test cases for functionality which sets what xml attributes specify
 * the plugin class or plugin declaration id.
 */
public class TestConfigurablePluginAttributes extends AbstractPluginTestCase {

    @Test
    public void testDefaultBehaviour() throws Exception {
        // tests that by default the attributes used are 
        // named "plugin-class" and "plugin-id"

        Digester digester = newPluginDigester(new ConfigurablePluginAttributesModule());

        MultiContainer root = new MultiContainer();
        digester.push(root);

        digester.parse(getInputStream("test7.xml"));

        List<Widget> widgets = root.getWidgets();
        assertNotNull(widgets);
        assertEquals(4, widgets.size());

        assertEquals(TextLabel.class, widgets.get(0).getClass());
        assertEquals(TextLabel.class, widgets.get(1).getClass());
        assertEquals(TextLabel.class, widgets.get(2).getClass());
        assertEquals(TextLabel.class, widgets.get(3).getClass());

        List<Widget> gadgets = root.getGadgets();
        assertNotNull(gadgets);
        assertEquals(4, gadgets.size());

        assertEquals(TextLabel.class, gadgets.get(0).getClass());
        assertEquals(TextLabel.class, gadgets.get(1).getClass());
        assertEquals(TextLabel.class, gadgets.get(2).getClass());
        assertEquals(TextLabel.class, gadgets.get(3).getClass());
    }

    @Test
    public void testGlobalOverride() throws Exception {
        // Tests that using setDefaultPluginXXXX overrides behaviour for all
        // PluginCreateRule instances. Also tests specifying attributes
        // with "null" for namespace (ie attributes not in any namespace).
        //
        // note that in order not to screw up all other tests, we need
        // to reset the global names after we finish here!

        PluginRules rc = new PluginRules();
        rc.setPluginIdAttribute(null, "id");
        rc.setPluginClassAttribute(null, "class");

        Digester digester = newLoader(new ConfigurablePluginAttributesModule())
            .setNamespaceAware(true)
            .newDigester(rc);

        MultiContainer root = new MultiContainer();
        digester.push(root);

        digester.parse(getInputStream("test7.xml"));

        List<Widget> widgets = root.getWidgets();
        assertNotNull(widgets);
        assertEquals(4, widgets.size());

        assertEquals(Slider.class, widgets.get(0).getClass());
        assertEquals(Slider.class, widgets.get(1).getClass());
        assertEquals(Slider.class, widgets.get(2).getClass());
        assertEquals(Slider.class, widgets.get(3).getClass());

        List<Widget> gadgets = root.getGadgets();
        assertNotNull(gadgets);
        assertEquals(4, gadgets.size());

        assertEquals(Slider.class, gadgets.get(0).getClass());
        assertEquals(Slider.class, gadgets.get(1).getClass());
        assertEquals(Slider.class, gadgets.get(2).getClass());
        assertEquals(Slider.class, gadgets.get(3).getClass());
    }

    @Test
    public void testInstanceOverride() throws Exception {
        // Tests that using setPluginXXXX overrides behaviour for only
        // that particular PluginCreateRule instance. Also tests that
        // attributes can be in namespaces.

        Digester digester = newPluginDigester(new AbstractRulesModule() {

                @Override
                protected void configure() {
                    forPattern("root/plugin").declarePlugin();

                    // for plugins at pattern "root/widget", use xml attributes "id" and
                    // "class" in the custom namespace as the values for plugin id and
                    // class, not the default (and non-namespaced) values of 
                    // "plugin-id" and "plugin-class".
                    forPattern("root/widget")
                        .createPlugin().ofType(Widget.class)
                            .setPluginIdAttribute("http://commons.apache.org/digester/plugins", "id")
                            .setPluginClassAttribute("http://commons.apache.org/digester/plugins", "class")
                        .then()
                        .setNext("addWidget");

                    forPattern("root/gadget")
                        .createPlugin().ofType(Widget.class)
                        .then()
                        .setNext("addGadget");
                }

            });

        MultiContainer root = new MultiContainer();
        digester.push(root);

        digester.parse(getInputStream("test7.xml"));

        List<Widget> widgets = root.getWidgets();
        assertNotNull(widgets);
        assertEquals(4, widgets.size());

        assertEquals(TextLabel2.class, widgets.get(0).getClass());
        assertEquals(TextLabel2.class, widgets.get(1).getClass());
        assertEquals(TextLabel2.class, widgets.get(2).getClass());
        assertEquals(TextLabel2.class, widgets.get(3).getClass());

        List<Widget> gadgets = root.getGadgets();
        assertNotNull(gadgets);
        assertEquals(4, gadgets.size());

        assertEquals(TextLabel.class, gadgets.get(0).getClass());
        assertEquals(TextLabel.class, gadgets.get(1).getClass());
        assertEquals(TextLabel.class, gadgets.get(2).getClass());
        assertEquals(TextLabel.class, gadgets.get(3).getClass());
    }

    // inner classes used for testing

    public static class MultiContainer {

        private LinkedList<Widget> widgets = new LinkedList<Widget>();

        private LinkedList<Widget> gadgets = new LinkedList<Widget>();

        public MultiContainer() {}

        public void addWidget(Widget child) {
            widgets.add(child);
        }

        public List<Widget> getWidgets() {
            return widgets;
        }

        public void addGadget(Widget child) {
            gadgets.add(child);
        }

        public List<Widget> getGadgets() {
            return gadgets;
        }

    }

}
