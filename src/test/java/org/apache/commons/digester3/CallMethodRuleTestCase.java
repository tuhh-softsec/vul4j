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

import static org.apache.commons.digester3.DigesterLoader.newLoader;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.digester3.rule.CallMethodRule;
import org.apache.commons.digester3.rules.ExtendedBaseRules;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * <p>Tests for the <code>CallMethodRule</code> and associated 
 * <code>CallParamRule</code>.
 *
 * @author Christopher Lenz 
 */
public class CallMethodRuleTestCase extends AbstractTestCase {

    /**
     * Test method calls with the CallMethodRule rule. It should be possible
     * to call a method with no arguments using several rule syntaxes.
     */
    @Test
    public void testBasic() throws SAXException, IOException {
        Object root1 = newBasicDigester(new AbstractRulesModule() {

            @Override
            protected void configure() {
                forPattern("employee")
                    .createObject().ofType(Employee.class)
                    .then()
                    .callMethod("toString")
                    .then()
                    .callMethod("toString");
            }

        }).parse(getInputStream("Test5.xml"));
        assertNotNull(root1);
    }


    /**
     * Test method calls with the CallMethodRule reading from the element
     * body, with no CallParamMethod rules added.
     */
    @Test
    public void testCallMethodOnly() throws Throwable {
        Employee employee = (Employee) newBasicDigesterAndParse("Test9.xml", new AbstractRulesModule() {

            @Override
            protected void configure() {
                forPattern("employee").createObject().ofType(Employee.class);
                forPattern("employee/firstName").callMethod("setFirstName").usingElementBodyAsArgument();
                forPattern("employee/lastName").callMethod("setLastName").usingElementBodyAsArgument();
            }

        });

        assertNotNull("parsed an employee", employee);

        // Validate that the property setters were called
        assertEquals("Set first name", "First Name", employee.getFirstName());
        assertEquals("Set last name", "Last Name", employee.getLastName());
    }


    /**
     * Test CallMethodRule variants which specify the classes of the
     * parameters to target methods. String, int, boolean, float should all 
     * be acceptable as parameter types.
     */
    @Test
    public void testSetLastNameProperty() throws Throwable {
        Employee employee = (Employee) newBasicDigesterAndParse("Test5.xml", new AbstractRulesModule() {

            @Override
            protected void configure() {
                forPattern("employee")
                    .createObject().ofType(Employee.class)
                    .then()
                    .callMethod("setLastName").withParamTypes(String.class);
                forPattern("employee/lastName").callParam();
            }

        });
        assertEquals("Failed to call Employee.setLastName", "Last Name", employee.getLastName());
    }

    @Test
    public void testSetAgeProperty() throws Throwable {
        Employee employee = (Employee) newBasicDigesterAndParse("Test5.xml", new AbstractRulesModule() {

            @Override
            protected void configure() {
                forPattern("employee")
                    .createObject().ofType(Employee.class)
                    .then()
                    .callMethod("setAge").withParamTypes(int.class);
                forPattern("employee/age").callParam().ofIndex(0);
            }

        });
        assertEquals("Failed to call Employee.setAge", 21, employee.getAge());
    }

    @Test
    public void testSetActiveProperty() throws Throwable {
        Employee employee = (Employee) newBasicDigesterAndParse("Test5.xml", new AbstractRulesModule() {

            @Override
            protected void configure() {
                forPattern("employee")
                    .createObject().ofType(Employee.class)
                    .then()
                    .callMethod("setActive").withParamTypes(boolean.class);
                forPattern("employee/active").callParam();
            }

        });
        assertEquals("Failed to call Employee.setActive", 
                        true, employee.isActive());
    }

    @Test
    public void testSetSalaryProperty() throws Throwable {
        Employee employee = (Employee) newBasicDigesterAndParse("Test5.xml", new AbstractRulesModule() {

            @Override
            protected void configure() {
                forPattern("employee")
                    .createObject().ofType(Employee.class)
                    .then()
                    .callMethod("setSalary").withParamTypes(float.class);
                forPattern("employee/salary").callParam();
            }

        });
        assertEquals("Failed to call Employee.setSalary", 
                        1000000.0f, employee.getSalary(), 0.1f); 
    }


    /**
     * This tests the call methods params enhancement that provides 
     * for more complex stack-based calls.
     */
    @Test
    public void testParamsFromStack() throws SAXException, IOException {
        StringBuffer xml = new StringBuffer().
            append("<?xml version='1.0'?>").
            append("<map>").
            append("  <key name='The key'/>").
            append("  <value name='The value'/>").
            append("</map>");

        HashMap<AlphaBean, BetaBean> map = (HashMap<AlphaBean, BetaBean>) newBasicDigester(new AbstractRulesModule() {

            @Override
            protected void configure() {
                forPattern("map")
                    .createObject().ofType(HashMap.class)
                    .then()
                    .callMethod("put").withParamTypes(AlphaBean.class, BetaBean.class);
                forPattern("map/key")
                    .createObject().ofType(AlphaBean.class)
                    .then()
                    .setProperties()
                    .then()
                    .callParam().fromStack(true);
                forPattern("map/value")
                    .createObject().ofType(BetaBean.class)
                    .then()
                    .setProperties()
                    .then()
                    .callParam().fromStack(true).ofIndex(1);
            }

        }).parse(new StringReader(xml.toString()));

        assertNotNull(map);
        assertEquals(1, map.size());
        assertEquals("The key",
                     map.keySet().iterator().next().getName());
        assertEquals("The value",
                     map.values().iterator().next().getName());
    }


    /**
     * Test that the target object for a CallMethodRule is the object that was
     * on top of the object stack when the CallMethodRule fired, even when other
     * rules fire between the CallMethodRule and its associated CallParamRules.
     * <p>
     * The current implementation of CallMethodRule ensures this works by
     * firing only at the end of the tag that CallMethodRule triggered on.
     */
    @Test
    public void testOrderNestedPartA() throws Exception {
        NamedBean root1 = null;
        try {
            // an exception will be thrown if the method can't be found
            root1 = (NamedBean) newBasicDigesterAndParse("Test8.xml", new AbstractRulesModule() {

                @Override
                protected void configure() {
                    // Here, we use the "grandchild element name" as a parameter to
                    // the created element, to ensure that all the params aren't
                    // avaiable to the CallMethodRule until some other rules have fired,
                    // in particular an ObjectCreateRule. The CallMethodRule should still
                    // function correctly in this scenario.
                    forPattern("toplevel/element")
                        .createObject().ofType(NamedBean.class)
                        .then()
                        .callMethod("setName").withParamCount(1);
                    forPattern("toplevel/element/element/element").callParam().fromAttribute("name");
                    forPattern("toplevel/element/element").createObject().ofType(NamedBean.class);
                }

            });
        } catch (Throwable t) {
            // this means that the method can't be found and so the test fails
            fail("Digester threw Exception:  " + t);
        }

        // if the CallMethodRule were to incorrectly invoke the method call
        // on the second-created NamedBean instance, then the root one would
        // have a null name. If it works correctly, the target element will
        // be the first-created (root) one, despite the fact that a second
        // object instance was created between the firing of the 
        // CallMethodRule and its associated CallParamRule.
        assertEquals("Wrong method call order", "C", root1.getName());
    }

    /**
     * Test nested CallMethod rules.
     * <p>
     * The current implementation of CallMethodRule, in which the method is
     * invoked in its end() method, causes behaviour which some users find
     * non-intuitive. In this test it can be seen to "reverse" the order of
     * data processed. However this is the way CallMethodRule has always 
     * behaved, and it is expected that apps out there rely on this call order 
     * so this test is present to ensure that no-one changes this behaviour.
     */
    @Test
    public void testOrderNestedPartB() throws Exception {
        // Configure the digester as required
        StringBuffer word = new StringBuffer();

        Digester digester = newBasicDigester(new AbstractRulesModule() {

            @Override
            protected void configure() {
                forPattern("*/element")
                    .callMethod("append").withParamCount(1)
                    .then()
                    .callParam().fromAttribute("name");
            }

        });

        digester.push(word);

        // Parse our test input
        Object root1 = null;
        try {
            // an exception will be thrown if the method can't be found
            root1 = digester.parse(getInputStream("Test8.xml"));
            assertNotNull(root1);
        } catch (Throwable t) {
            // this means that the method can't be found and so the test fails
            fail("Digester threw Exception:  " + t);
        }

        assertEquals("Wrong method call order", "CBA", word.toString());
    }

    @Test
    public void testPrimitiveReading() throws Exception {
        StringReader reader = new StringReader(
            "<?xml version='1.0' ?><root><bean good='true'/><bean good='false'/><bean/>"
            + "<beanie bad='Fee Fie Foe Fum' good='true'/><beanie bad='Fee Fie Foe Fum' good='false'/>"
            + "<beanie bad='Fee Fie Foe Fum'/></root>");

        Digester digester = newBasicDigester(new AbstractRulesModule() {

            @Override
            protected void configure() {
                forPattern("root/bean")
                    .createObject().ofType(PrimitiveBean.class)
                    .then()
                    .callMethod("setBoolean").withParamTypes(Boolean.TYPE)
                    .then()
                    .callParam().fromAttribute("good")
                    .then()
                    .setNext("add");
                forPattern("root/beanie")
                    .createObject().ofType(PrimitiveBean.class)
                    .then()
                    .callMethod("testSetBoolean").withParamTypes(String.class, Boolean.TYPE)
                    .then()
                    .callParam().ofIndex(0).fromAttribute("bad")
                    .then()
                    .callParam().ofIndex(1).fromAttribute("good")
                    .then()
                    .setNext("add");
            }

        });

        ArrayList<PrimitiveBean> list = new ArrayList<PrimitiveBean>();
        digester.push(list);
        digester.parse(reader);

        assertEquals("Wrong number of beans in list", 6, list.size());
        PrimitiveBean bean = list.get(0);
        assertTrue("Bean 0 property not called", bean.getSetBooleanCalled());
        assertEquals("Bean 0 property incorrect", true, bean.getBoolean());
        bean = list.get(1);
        assertTrue("Bean 1 property not called", bean.getSetBooleanCalled());
        assertEquals("Bean 1 property incorrect", false, bean.getBoolean());
        bean = list.get(2);
        // no attibute, no call is what's expected
        assertTrue("Bean 2 property called", !bean.getSetBooleanCalled());
        bean = list.get(3);
        assertTrue("Bean 3 property not called", bean.getSetBooleanCalled());
        assertEquals("Bean 3 property incorrect", true, bean.getBoolean());
        bean = list.get(4);
        assertTrue("Bean 4 property not called", bean.getSetBooleanCalled());
        assertEquals("Bean 4 property incorrect", false, bean.getBoolean());
        bean = list.get(5);
        assertTrue("Bean 5 property not called", bean.getSetBooleanCalled());
        assertEquals("Bean 5 property incorrect", false, bean.getBoolean());       
    }

    @Test
    public void testFromStack() throws Exception {
        StringReader reader = new StringReader(
            "<?xml version='1.0' ?><root><one/><two/><three/><four/><five/></root>");

        Digester digester = newBasicDigester(new AbstractRulesModule() {

            @Override
            protected void configure() {
                int stackIndex = 2;
                for (String element : new String[]{ "one", "two", "three", "four" }) {
                    forPattern(String.format("root/%s", element))
                        .createObject().ofType(NamedBean.class)
                        .then()
                        .setNext("add")
                        .then()
                        .callMethod("setName").withParamTypes(String.class)
                        .then()
                        .callParam().withStackIndex(stackIndex++);
                }

                forPattern("root/five")
                    .createObject().ofType(NamedBean.class)
                    .then()
                    .setNext("add")
                    .then()
                    .callMethod("test").withParamTypes(String.class, String.class)
                    .then()
                    .callParam().ofIndex(0).withStackIndex(10)
                    .then()
                    .callParam().ofIndex(1).withStackIndex(3);
            }

        });

        // prepare stack
        digester.push("That lamb was sure to go.");
        digester.push("And everywhere that Mary went,");
        digester.push("It's fleece was white as snow.");
        digester.push("Mary had a little lamb,");

        ArrayList<NamedBean> list = new ArrayList<NamedBean>();
        digester.push(list);
        digester.parse(reader);

        assertEquals("Wrong number of beans in list", 5, list.size());
        NamedBean bean = list.get(0);
        assertEquals("Parameter not set from stack (1)", "Mary had a little lamb,", bean.getName());
        bean = list.get(1);
        assertEquals("Parameter not set from stack (2)", "It's fleece was white as snow.", bean.getName());
        bean = list.get(2);
        assertEquals("Parameter not set from stack (3)", "And everywhere that Mary went,", bean.getName());
        bean = list.get(3);
        assertEquals("Parameter not set from stack (4)", "That lamb was sure to go.", bean.getName());
        bean = list.get(4);
        assertEquals("Out of stack not set to null", null , bean.getName());
    }

    @Test
    public void testTwoCalls() throws Exception {
        StringReader reader = new StringReader(
            "<?xml version='1.0' ?><root>"
            + "<param class='int' coolness='true'>25</param>"
            + "<param class='long'>50</param>"
            + "<param class='float' coolness='false'>90</param></root>");

        Digester digester = newBasicDigester(new AbstractRulesModule() {

            @Override
            protected void configure() {
                forPattern("root/param")
                    .createObject().ofType(ParamBean.class).then()
                    .setNext("add").then()
                    .callMethod("setThisAndThat").withParamCount(2).then()
                    .callParam().ofIndex(0).fromAttribute("class").then()
                    .callParam().ofIndex(1).then()
                    .callMethod("setCool").withParamTypes(boolean.class).then()
                    .callParam().ofIndex(0).fromAttribute("coolness");
            }

        });

        ArrayList<ParamBean> list = new ArrayList<ParamBean>();
        digester.push(list);
        digester.parse(reader);

        assertEquals("Wrong number of objects created", 3, list.size());
        ParamBean bean = list.get(0);
        assertEquals("Coolness wrong (1)", true, bean.isCool());
        assertEquals("This wrong (1)", "int", bean.getThis());
        assertEquals("That wrong (1)", "25", bean.getThat());
        bean = list.get(1);
        assertEquals("Coolness wrong (2)", false, bean.isCool());
        assertEquals("This wrong (2)", "long", bean.getThis());
        assertEquals("That wrong (2)", "50", bean.getThat());
        bean = list.get(2);
        assertEquals("Coolness wrong (3)", false, bean.isCool());
        assertEquals("This wrong (3)", "float", bean.getThis());
        assertEquals("That wrong (3)", "90", bean.getThat());
    }

    @Test
    public void testNestedBody() throws Exception {
        StringReader reader = new StringReader(
            "<?xml version='1.0' ?><root>"
            + "<spam>Simple</spam>"
            + "<spam>Complex<spam>Deep<spam>Deeper<spam>Deepest</spam></spam></spam></spam>"
            + "</root>");

        Digester digester = newBasicDigester(new AbstractRulesModule() {

            @Override
            protected void configure() {
                forPattern("root/spam")
                    .createObject().ofType(NamedBean.class)
                    .then()
                    .callMethod("setName").withParamCount(1)
                    .then()
                    .callParam()
                    .then()
                    .setRoot("add");

                forPattern("root/spam/spam")
                    .createObject().ofType(NamedBean.class)
                    .then()
                    .callMethod("setName").withParamCount(1)
                    .then()
                    .callParam()
                    .then()
                    .setRoot("add");

                forPattern("root/spam/spam/spam")
                    .createObject().ofType(NamedBean.class)
                    .then()
                    .callMethod("setName").withParamCount(1)
                    .then()
                    .callParam()
                    .then()
                    .setRoot("add");

                forPattern("root/spam/spam/spam/spam")
                    .createObject().ofType(NamedBean.class)
                    .then()
                    .callMethod("setName").withParamCount(1)
                    .then()
                    .callParam()
                    .then()
                    .setRoot("add");
            }

        });

        ArrayList<NamedBean> list = new ArrayList<NamedBean>();
        digester.push(list);
        digester.parse(reader);

        NamedBean bean = list.get(0);
        assertEquals("Wrong name (1)", "Simple", bean.getName());
        // these are added in deepest first order by the addRootRule
        bean = list.get(4);
        assertEquals("Wrong name (2)", "Complex", bean.getName());
        bean = list.get(3);
        assertEquals("Wrong name (3)", "Deep", bean.getName());
        bean = list.get(2);
        assertEquals("Wrong name (4)", "Deeper", bean.getName());
        bean = list.get(1);
        assertEquals("Wrong name (5)", "Deepest", bean.getName());
    }

    @Test
    public void testProcessingHook() throws Exception {
        class TestCallMethodRule extends CallMethodRule {

            Object result;

            TestCallMethodRule(String methodName, int paramCount) {
                super(0, methodName, paramCount, new Class<?>[]{ String.class, String.class }, false);
            }

            @Override
            protected void processMethodCallResult(Object result) {
                this.result = result;
            }

        }

        final TestCallMethodRule rule = new TestCallMethodRule( "setThisAndThat" , 2 );
        StringReader reader = new StringReader(
            "<?xml version='1.0' ?><root>"
            + "<param class='float' coolness='false'>90</param></root>");

        Digester digester = newBasicDigester(new AbstractRulesModule() {

            @Override
            protected void configure() {
                forPattern("root/param")
                    .createObject().ofType(ParamBean.class).then()
                    .setNext("add").then()
                    .addRule(rule).then()
                    .callParam().fromAttribute("class").then()
                    .callParam().ofIndex(1).fromAttribute("coolness");
            }

        });

        ArrayList<ParamBean> list = new ArrayList<ParamBean>();
        digester.push(list);
        digester.parse(reader);

        assertEquals("Wrong number of objects created", 1, list.size());
        assertEquals("Result not passed into hook", "The Other", rule.result);
    }

    /**
     * Test for the PathCallParamRule
     */
    @Test
    public void testPathCallParam() throws Exception {
        String xml = "<?xml version='1.0'?><main>"
            + "<alpha><beta>Ignore this</beta></alpha>"
            + "<beta><epsilon><gamma>Ignore that</gamma></epsilon></beta>"
            + "</main>";

        SimpleTestBean bean = new SimpleTestBean();
        bean.setAlphaBeta("[UNSET]", "[UNSET]");

        StringReader in = new StringReader(xml);
        Digester digester = newLoader(new AbstractRulesModule() {

            @Override
            protected void configure() {
                forPattern("*/alpha/?").callParamPath().ofIndex(0);
                forPattern("*/epsilon/?").callParamPath().ofIndex(1);
                forPattern("main").callMethod("setAlphaBeta").withParamCount(2);
            }

        }).newDigester(new ExtendedBaseRules());

        digester.push(bean);

        digester.parse(in);

        assertEquals("Test alpha property setting", "main/alpha/beta" , bean.getAlpha());
        assertEquals("Test beta property setting", "main/beta/epsilon/gamma" , bean.getBeta());
    }

    /** 
     * Test invoking an object which does not exist on the stack.
     */
    @Test
    public void testCallInvalidTarget() throws Exception {
        Digester digester = newBasicDigester(new AbstractRulesModule() {

            @Override
            protected void configure() {
                forPattern("employee")
                    .createObject().ofType(HashMap.class).then()
                    .callMethod("put").withTargetOffset(1).withParamCount(0);
            }

        });

        try {
            digester.parse(getInputStream("Test5.xml"));
            fail("Exception should be thrown for invalid target offset");
        }
        catch(SAXException e) {
            // ok, exception expected
        }
    }

    /** 
     * Test invoking an object which is at top-1 on the stack, like
     * SetNextRule does...
     */
    @Test
    public void testCallNext() throws Exception {
        Digester digester = newBasicDigester(new AbstractRulesModule() {

            @Override
            protected void configure() {
                forPattern("employee").createObject().ofType(HashMap.class);
                forPattern("employee/address")
                    .createObject().ofType(Address.class)
                    .then()
                    .setNestedProperties()
                    .then()
                    .callMethod("put").withParamTypes(String.class, Address.class).withTargetOffset(1)
                    .then()
                    .callParam().ofIndex(1).fromStack(true);
                forPattern("employee/address/type").callParam();
            }

        });

        HashMap<String, Address> map = (HashMap<String, Address>) digester.parse(getInputStream("Test5.xml"));

        assertNotNull(map);
        Set<String> keys = map.keySet();
        assertEquals(2, keys.size());
        Address home = map.get("home");
        assertNotNull(home);
        assertEquals("HmZip", home.getZipCode());
        Address office = map.get("office");
        assertNotNull(office);
        assertEquals("OfZip", office.getZipCode());
    }

    /** 
     * Test invoking an object which is at the root of the stack, like
     * SetRoot does...
     */
    @Test
    public void testCallRoot() throws Exception {
        Digester digester = newBasicDigester(new AbstractRulesModule() {

            @Override
            protected void configure() {
                forPattern("employee").createObject().ofType(HashMap.class);
                forPattern("employee/address")
                    .createObject().ofType(Address.class)
                    .then()
                    .setNestedProperties()
                    .then()
                    .callMethod("put").withParamTypes(String.class, Address.class).withTargetOffset(-1)
                    .then()
                    .callParam().ofIndex(1).fromStack(true);
                forPattern("employee/address/type").callParam().ofIndex(0);
            }

        });

        HashMap<String, Address> map = (HashMap<String, Address>) digester.parse(getInputStream("Test5.xml"));

        assertNotNull(map);
        Set<String> keys = map.keySet();
        assertEquals(2, keys.size());
        Address home = map.get("home");
        assertNotNull(home);
        assertEquals("HmZip", home.getZipCode());
        Address office = map.get("office");
        assertNotNull(office);
        assertEquals("OfZip", office.getZipCode());
    }

}
