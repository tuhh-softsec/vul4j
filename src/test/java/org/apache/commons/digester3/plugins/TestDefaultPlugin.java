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
import org.xml.sax.SAXParseException;

public class TestDefaultPlugin extends AbstractPluginTestCase {

    @Test
    public void testDefaultPlugins1() throws Exception {
        // * tests that when a PluginCreateRule is defined with a default
        //   class, that the default class is instantiated when no class
        //   or id is specified in the xml file.
        Digester digester = newPluginDigester(new AbstractRulesModule() {

            @Override
            protected void configure() {
                forPattern("root/widget")
                    .createPlugin().ofType(Widget.class).usingDefaultPluginClass(TextLabel.class)
                    .then()
                    .setNext("addChild");
            }

        });

        Container root = new Container();
        digester.push(root);

        digester.parse(getInputStream("test2.xml"));

        Object child;
        List<Widget> children = root.getChildren();
        assertNotNull(children);
        assertEquals(3, children.size());

        child = children.get(0);
        assertNotNull(child);
        assertEquals(TextLabel.class, child.getClass());
        TextLabel label1 = (TextLabel) child;
        assertEquals("label1", label1.getLabel());

        child = children.get(1);
        assertNotNull(child);
        assertEquals(TextLabel.class, child.getClass());
        TextLabel label2 = (TextLabel) child;
        assertEquals("label2", label2.getLabel());

        child = children.get(2);
        assertNotNull(child);
        assertEquals(Slider.class, child.getClass());
        Slider slider1 = (Slider) child;
        assertEquals("slider1", slider1.getLabel());
    }

    public void testDefaultPlugins2() throws Exception {
        // * tests that when there is no default plugin, it is an error
        //   not to have one of plugin-class or plugin-id specified
        Digester digester = newPluginDigester(new AbstractRulesModule() {

            @Override
            protected void configure() {
                forPattern("root/widget")
                    .addRule(new PluginCreateRule(Widget.class))
                    .then()
                    .setNext("addChild");
            }

        });
        PluginRules rc = new PluginRules();
        digester.setRules(rc);

        Container root = new Container();
        digester.push(root);

        Exception exception = null;
        try {
            digester.parse(getInputStream("test2.xml"));
        } catch(Exception e) {
            exception = e;
        }

        assertNotNull(exception);
        assertEquals(SAXParseException.class, exception.getClass());
        assertEquals(
            PluginInvalidInputException.class, 
            ((SAXParseException)exception).getException().getClass());
    }

    public void testDefaultPlugins3() throws Exception {
        // * tests that the default plugin must implement or extend the
        //   plugin base class.
        Digester digester = newPluginDigester(new AbstractRulesModule() {

            @Override
            protected void configure() {
                forPattern("root/widget")
                    .addRule(new PluginCreateRule(Widget.class, Object.class))
                    .then()
                    .setNext("addChild");
            }

        });

        Container root = new Container();
        digester.push(root);

        Exception exception = null;
        try {
            digester.parse(getInputStream("test2.xml"));
        } catch(Exception e) {
            exception = e;
        }

        assertNotNull(exception);
        assertEquals(SAXParseException.class, exception.getClass());
        assertEquals(
            PluginConfigurationException.class, 
            ((SAXParseException)exception).getException().getClass());
    }

}
