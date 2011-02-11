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
package org.apache.commons.digester3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.digester3.rule.AbstractObjectCreationFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.xml.sax.Attributes;

/**
 * Test case for factory create rules.
 *
 * @author Robert Burrell Donkin
 */
@RunWith(Parameterized.class)
public class TestFactoryCreate extends AbstractTestCase {

    private final boolean propagateExceptions;

    public TestFactoryCreate(boolean propagateExceptions) {
        this.propagateExceptions = propagateExceptions;
    }

    @Test
    public void testPropagateException() throws Exception {
        // only used with this method
        class ThrowExceptionCreateRule extends AbstractObjectCreationFactory<Object> {

            @Override
            public Object createObject(Attributes attributes) throws Exception {
                throw new RuntimeException();
            }

        }

        // now for the tests
        String xml = "<?xml version='1.0' ?><root><element/></root>";

        // test default - which is to propagate the exception
        Digester digester = newBasicDigester(new AbstractRulesModule() {

            @Override
            protected void configure() {
                forPattern("root").factoryCreate().usingFactory(new ThrowExceptionCreateRule());
            }

        });
        try {
            digester.parse(new StringReader(xml));
            fail("Exception not propagated from create rule (1)");
        } catch (Exception e) { 
            /* This is what's expected */ 
        }

        // test propagate exception
        digester = newBasicDigester(new AbstractRulesModule() {

            @Override
            protected void configure() {
                forPattern("root")
                    .factoryCreate()
                    .usingFactory(new ThrowExceptionCreateRule())
                    .ignoreCreateExceptions(false);
            }

        });
        try {
            digester.parse(new StringReader(xml));
            fail("Exception not propagated from create rule (1)");
        } catch (Exception e) { 
            /* This is what's expected */ 
        }

        // test don't propagate exception
        digester = newBasicDigester(new AbstractRulesModule() {

            @Override
            protected void configure() {
                forPattern("root")
                    .factoryCreate()
                    .usingFactory(new ThrowExceptionCreateRule())
                    .ignoreCreateExceptions(true);
            }

        });
        try {
            digester.parse(new StringReader(xml));
        } catch (Exception e) {
            // this shouldn't happen
            fail("Exception should not be propagated");
        }
    }

    @Test
    public void firstVariation() throws Exception {
        final ObjectCreationFactoryTestImpl factory = new ObjectCreationFactoryTestImpl();

        // test passing object create
        Digester digester = newBasicDigester(new AbstractRulesModule() {

            @Override
            protected void configure() {
                forPattern("root").factoryCreate().usingFactory(factory).ignoreCreateExceptions(propagateExceptions);
            }

        });

        String xml = "<?xml version='1.0' ?><root one='good' two='bad' three='ugly'><element/></root>";
        digester.parse(new StringReader(xml));

        assertEquals("Object create not called(1)[" + propagateExceptions + "]", factory.called , true);
        assertEquals(
                    "Attribute not passed (1)[" + propagateExceptions + "]",
                    factory.attributes.getValue("one"), 
                    "good");
        assertEquals(
                    "Attribute not passed (2)[" + propagateExceptions + "]",
                    factory.attributes.getValue("two"), 
                    "bad");
        assertEquals(
                    "Attribute not passed (3)[" + propagateExceptions + "]",
                    factory.attributes.getValue("three"),
                    "ugly");
    }

    @Test
    public void secondVariation() throws Exception {
        Digester digester = newBasicDigester(new AbstractRulesModule() {

            @Override
            protected void configure() {
                forPattern("root")
                    .factoryCreate()
                    .ofType("org.apache.commons.digester3.ObjectCreationFactoryTestImpl")
                    .ignoreCreateExceptions(propagateExceptions)
                    .then()
                    .setNext("add");
            }

        });
        String xml = "<?xml version='1.0' ?><root one='good' two='bad' three='ugly'><element/></root>";
        List<ObjectCreationFactoryTestImpl> list = new ArrayList<ObjectCreationFactoryTestImpl>();
        digester.push(list);
        digester.parse(new StringReader(xml));

        assertEquals("List should contain only the factory object", list.size() , 1);
        ObjectCreationFactoryTestImpl actualFactory = list.get(0);
        assertEquals("Object create not called(2)[" + propagateExceptions + "]", actualFactory.called , true);
        assertEquals(
                    "Attribute not passed (4)[" + propagateExceptions + "]",
                    actualFactory.attributes.getValue("one"), 
                    "good");
        assertEquals(
                    "Attribute not passed (5)[" + propagateExceptions + "]",
                    actualFactory.attributes.getValue("two"),
                    "bad");
        assertEquals(
                    "Attribute not passed (6)[" + propagateExceptions + "]",
                    actualFactory.attributes.getValue("three"), 
                    "ugly");
    }

    @Test
    public void thirdVariant() throws Exception {
        Digester digester = newBasicDigester(new AbstractRulesModule() {

            @Override
            protected void configure() {
                forPattern("root")
                    .factoryCreate()
                    .ofType("org.apache.commons.digester3.ObjectCreationFactoryTestImpl")
                    .ignoreCreateExceptions(propagateExceptions)
                    .overriddenByAttribute("override")
                    .then()
                    .setNext("add");
            }

        });
        String xml = "<?xml version='1.0' ?><root one='good' two='bad' three='ugly'><element/></root>";
        List<ObjectCreationFactoryTestImpl> list = new ArrayList<ObjectCreationFactoryTestImpl>();
        digester.push(list);
        digester.parse(new StringReader(xml));

        assertEquals("List should contain only the factory object", list.size() , 1);
        ObjectCreationFactoryTestImpl actualFactory = list.get(0);
        assertEquals("Object create not called(3)[" + propagateExceptions + "]", actualFactory.called , true);
        assertEquals(
                    "Attribute not passed (7)[" + propagateExceptions + "]",
                    actualFactory.attributes.getValue("one"), 
                    "good");
        assertEquals(
                    "Attribute not passed (8)[" + propagateExceptions + "]",
                    actualFactory.attributes.getValue("two"), 
                    "bad");
        assertEquals(
                    "Attribute not passed (8)[" + propagateExceptions + "]",
                    actualFactory.attributes.getValue("three"), 
                    "ugly");
    }

    @Test
    public void fourthVariant() throws Exception {
        Digester digester = newBasicDigester(new AbstractRulesModule() {

            @Override
            protected void configure() {
                forPattern("root")
                    .factoryCreate()
                    .ofType("org.apache.commons.digester3.ObjectCreationFactoryTestImpl")
                    .ignoreCreateExceptions(propagateExceptions)
                    .overriddenByAttribute("override")
                    .then()
                    .setNext("add");
            }

        });
        String xml ="<?xml version='1.0' ?><root one='good' two='bad' three='ugly' "
            + " override='org.apache.commons.digester3.OtherTestObjectCreationFactory' >"
            + "<element/></root>";
        List<ObjectCreationFactoryTestImpl> list = new ArrayList<ObjectCreationFactoryTestImpl>();
        digester.push(list);
        digester.parse(new StringReader(xml));

        assertEquals("List should contain only the factory object", list.size() , 1);
        ObjectCreationFactoryTestImpl actualFactory = list.get(0);
        assertEquals(
                    "Attribute Override Failed (1)",
                    actualFactory.getClass().getName(),
                    "org.apache.commons.digester3.OtherTestObjectCreationFactory");
        assertEquals("Object create not called(4)[" + propagateExceptions + "]", actualFactory.called , true);
        assertEquals(
                    "Attribute not passed (10)[" + propagateExceptions + "]",
                    actualFactory.attributes.getValue("one"),
                    "good");
        assertEquals(
                    "Attribute not passed (11)[" + propagateExceptions + "]",
                    actualFactory.attributes.getValue("two"),
                    "bad");
        assertEquals(
                    "Attribute not passed (12)[" + propagateExceptions + "]",
                    actualFactory.attributes.getValue("three"),
                    "ugly");
    }

    @Test
    public void fifthVariant() throws Exception {
        Digester digester = newBasicDigester(new AbstractRulesModule() {

            @Override
            protected void configure() {
                forPattern("root")
                    .factoryCreate()
                    .ofType(ObjectCreationFactoryTestImpl.class)
                    .ignoreCreateExceptions(propagateExceptions)
                    .overriddenByAttribute("override")
                    .then()
                    .setNext("add");
            }

        });
        String xml = "<?xml version='1.0' ?><root one='good' two='bad' three='ugly'><element/></root>";
        List<ObjectCreationFactoryTestImpl> list = new ArrayList<ObjectCreationFactoryTestImpl>();
        digester.push(list);
        digester.parse(new StringReader(xml));

        assertEquals("List should contain only the factory object", list.size() , 1);
        ObjectCreationFactoryTestImpl actualFactory = list.get(0);
        assertEquals("Object create not called(5)[" + propagateExceptions + "]", actualFactory.called , true);
        assertEquals(
                    "Attribute not passed (13)[" + propagateExceptions + "]", 
                    actualFactory.attributes.getValue("one"), 
                    "good");
        assertEquals(
                    "Attribute not passed (14)[" + propagateExceptions + "]", 
                    actualFactory.attributes.getValue("two"), 
                    "bad");
        assertEquals(
                    "Attribute not passed (15)[" + propagateExceptions + "]", 
                    actualFactory.attributes.getValue("three"), 
                    "ugly");
    }

    @Test
    public void sixthVariant() throws Exception {
        Digester digester = newBasicDigester(new AbstractRulesModule() {

            @Override
            protected void configure() {
                forPattern("root")
                    .factoryCreate()
                    .ofType(ObjectCreationFactoryTestImpl.class)
                    .ignoreCreateExceptions(propagateExceptions)
                    .overriddenByAttribute("override")
                    .then()
                    .setNext("add");
            }

        });
        String xml = "<?xml version='1.0' ?><root one='good' two='bad' three='ugly' "
            + " override='org.apache.commons.digester3.OtherTestObjectCreationFactory' >"
            + "<element/></root>";
        List<ObjectCreationFactoryTestImpl> list = new ArrayList<ObjectCreationFactoryTestImpl>();
        digester.push(list);
        digester.parse(new StringReader(xml));

        assertEquals("List should contain only the factory object", list.size() , 1);
        ObjectCreationFactoryTestImpl actualFactory = list.get(0);
        assertEquals(
                    "Attribute Override Failed (2)",
                    actualFactory.getClass().getName(),
                    "org.apache.commons.digester3.OtherTestObjectCreationFactory");
        assertEquals("Object create not called(6)[" + propagateExceptions + "]", actualFactory.called , true);
        assertEquals(
                    "Attribute not passed (16)[" + propagateExceptions + "]",
                    actualFactory.attributes.getValue("one"),
                    "good");
        assertEquals(
                    "Attribute not passed (17)[" + propagateExceptions + "]",
                    actualFactory.attributes.getValue("two"), 
                    "bad");
        assertEquals(
                    "Attribute not passed (18)[" + propagateExceptions + "]",
                    actualFactory.attributes.getValue("three"), 
                    "ugly");
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{{ true }, { false }});
    }

}
