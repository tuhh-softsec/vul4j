/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//digester/src/test/org/apache/commons/digester/plugins/TestConfigurablePluginAttributes.java,v 1.1 2003/11/12 23:20:29 rdonkin Exp $
 * $Revision: 1.1 $
 * $Date: 2003/11/12 23:20:29 $
 *
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
 *    if any, must include the following acknowledgement:  
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "Apache", "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache" nor may "Apache" appear in their names without prior 
 *    written permission of the Apache Software Foundation.
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


package org.apache.commons.digester.plugins;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.digester.*;
import org.apache.commons.digester.plugins.*;

/**
 * Test cases for functionality which sets what xml attributes specify
 * the plugin class or plugin declaration id.
 */

public class TestConfigurablePluginAttributes extends TestCase {
    /** Standard constructor */
    public TestConfigurablePluginAttributes(String name) { 
        super(name);
    }

    /** Set up instance variables required by this test case. */
    public void setUp() {}

    /** Return the tests included in this test suite. */
    public static Test suite() {

        return (new TestSuite(TestConfigurablePluginAttributes.class));

    }

    /** Tear down instance variables required by this test case.*/
    public void tearDown() {}
        
    // --------------------------------------------------------------- Test cases
    
    public void testDefaultBehaviour() throws Exception {
        // tests that by default the attributes used are 
        // named "plugin-class" and "plugin-id"

        Digester digester = new Digester();
        digester.setNamespaceAware(true);
        PluginRules rc = new PluginRules();
        digester.setRules(rc);
        
        PluginDeclarationRule pdr = new PluginDeclarationRule();
        digester.addRule("root/plugin", pdr);
        
        PluginCreateRule widgetPluginRule = new PluginCreateRule(Widget.class);
        digester.addRule("root/widget", widgetPluginRule);
        digester.addSetNext("root/widget", "addWidget");

        PluginCreateRule gadgetPluginRule = new PluginCreateRule(Widget.class);
        digester.addRule("root/gadget", gadgetPluginRule);
        digester.addSetNext("root/gadget", "addGadget");

        MultiContainer root = new MultiContainer();
        digester.push(root);
        
        try {
            digester.parse(
                TestAll.getInputStream(this, "test7.xml"));

        } catch(Exception e) {
            throw e;
        }

        Object child;
        
        List widgets = root.getWidgets();
        assertTrue(widgets != null);
        assertEquals(4, widgets.size());

        assertEquals(TextLabel.class, widgets.get(0).getClass());
        assertEquals(TextLabel.class, widgets.get(1).getClass());
        assertEquals(TextLabel.class, widgets.get(2).getClass());
        assertEquals(TextLabel.class, widgets.get(3).getClass());
        
        List gadgets = root.getGadgets();
        assertTrue(gadgets != null);
        assertEquals(4, gadgets.size());

        assertEquals(TextLabel.class, gadgets.get(0).getClass());
        assertEquals(TextLabel.class, gadgets.get(1).getClass());
        assertEquals(TextLabel.class, gadgets.get(2).getClass());
        assertEquals(TextLabel.class, gadgets.get(3).getClass());
    }
    
    public void testGlobalOverride() throws Exception {
        // Tests that using setDefaultPluginXXXX overrides behaviour for all
        // PluginCreateRule instances. Also tests specifying attributes
        // with "null" for namespace (ie attributes not in any namespace).
        //
        // note that in order not to screw up all other tests, we need
        // to reset the global names after we finish here!

        Digester digester = new Digester();
        digester.setNamespaceAware(true);
        PluginRules rc = new PluginRules();
        digester.setRules(rc);

        PluginCreateRule.setGlobalPluginIdAttribute(null, "id");
        PluginCreateRule.setGlobalPluginClassAttribute(null, "class");
     
        try {
            PluginDeclarationRule pdr = new PluginDeclarationRule();
            digester.addRule("root/plugin", pdr);
            
            PluginCreateRule widgetPluginRule = new PluginCreateRule(Widget.class);
            digester.addRule("root/widget", widgetPluginRule);
            digester.addSetNext("root/widget", "addWidget");
    
            PluginCreateRule gadgetPluginRule = new PluginCreateRule(Widget.class);
            digester.addRule("root/gadget", gadgetPluginRule);
            digester.addSetNext("root/gadget", "addGadget");
    
            MultiContainer root = new MultiContainer();
            digester.push(root);
            
            try {
                digester.parse(
                    TestAll.getInputStream(this, "test7.xml"));
                    
            } catch(Exception e) {
                throw e;
            }
    
            Object child;
            
            List widgets = root.getWidgets();
            assertTrue(widgets != null);
            assertEquals(4, widgets.size());
    
            assertEquals(Slider.class, widgets.get(0).getClass());
            assertEquals(Slider.class, widgets.get(1).getClass());
            assertEquals(Slider.class, widgets.get(2).getClass());
            assertEquals(Slider.class, widgets.get(3).getClass());
            
            List gadgets = root.getGadgets();
            assertTrue(gadgets != null);
            assertEquals(4, gadgets.size());
    
            assertEquals(Slider.class, gadgets.get(0).getClass());
            assertEquals(Slider.class, gadgets.get(1).getClass());
            assertEquals(Slider.class, gadgets.get(2).getClass());
            assertEquals(Slider.class, gadgets.get(3).getClass());
        } finally {
            // reset the global values to their defaults
            PluginCreateRule.setGlobalPluginIdAttribute(
                PluginCreateRule.GLOBAL_PLUGIN_ID_ATTR_NS,
                PluginCreateRule.GLOBAL_PLUGIN_ID_ATTR);
                
            PluginCreateRule.setGlobalPluginClassAttribute(
                PluginCreateRule.GLOBAL_PLUGIN_CLASS_ATTR_NS,
                PluginCreateRule.GLOBAL_PLUGIN_CLASS_ATTR);
        }
    }
    
    public void testInstanceOverride() throws Exception {
        // Tests that using setPluginXXXX overrides behaviour for only
        // that particular PluginCreateRule instance. Also tests that
        // attributes can be in namespaces.

        Digester digester = new Digester();
        digester.setNamespaceAware(true);
        PluginRules rc = new PluginRules();
        digester.setRules(rc);

        PluginDeclarationRule pdr = new PluginDeclarationRule();
        digester.addRule("root/plugin", pdr);
        
        PluginCreateRule widgetPluginRule = new PluginCreateRule(Widget.class);
        widgetPluginRule.setPluginIdAttribute(
            "http://jakarta.apache.org/digester/plugins", "id");
        widgetPluginRule.setPluginClassAttribute(
            "http://jakarta.apache.org/digester/plugins", "class");
        digester.addRule("root/widget", widgetPluginRule);
        digester.addSetNext("root/widget", "addWidget");

        PluginCreateRule gadgetPluginRule = new PluginCreateRule(Widget.class);
        digester.addRule("root/gadget", gadgetPluginRule);
        digester.addSetNext("root/gadget", "addGadget");

        MultiContainer root = new MultiContainer();
        digester.push(root);
        
        try {
            digester.parse(
                TestAll.getInputStream(this, "test7.xml"));
        } catch(Exception e) {
            throw e;
        }

        Object child;
        
        List widgets = root.getWidgets();
        assertTrue(widgets != null);
        assertEquals(4, widgets.size());

        assertEquals(TextLabel2.class, widgets.get(0).getClass());
        assertEquals(TextLabel2.class, widgets.get(1).getClass());
        assertEquals(TextLabel2.class, widgets.get(2).getClass());
        assertEquals(TextLabel2.class, widgets.get(3).getClass());
        
        List gadgets = root.getGadgets();
        assertTrue(gadgets != null);
        assertEquals(4, gadgets.size());

        assertEquals(TextLabel.class, gadgets.get(0).getClass());
        assertEquals(TextLabel.class, gadgets.get(1).getClass());
        assertEquals(TextLabel.class, gadgets.get(2).getClass());
        assertEquals(TextLabel.class, gadgets.get(3).getClass());
    }
    
    // inner classes used for testing
    
    public static class MultiContainer {
        private LinkedList widgets = new LinkedList();
        private LinkedList gadgets = new LinkedList();
    
        public MultiContainer() {}
        
        public void addWidget(Widget child) {
            widgets.add(child);
        }
    
        public List getWidgets() {
            return widgets;
        }

        public void addGadget(Widget child) {
            gadgets.add(child);
        }
    
        public List getGadgets() {
            return gadgets;
        }
    }
}
