/*
 * Copyright 2001-2004 The Apache Software Foundation.
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


package org.apache.commons.digester;


import java.util.List;

import org.xml.sax.Attributes;


/**
 * <p>This rule implementation is intended to help test digester.
 * The idea is that you can test which rule matches by looking
 * at the identifier.</p>
 *
 * @author Robert Burrell Donkin
 * @revision $Revision: 1.12 $ $Date: 2004/02/28 13:32:54 $
 */

public class TestRule extends Rule {

    // ----------------------------------------------------- Instance Variables


    /** String identifing this particular <code>TestRule</code> */
    private String identifier;

    /** Used when testing body text */
    private String bodyText;

    /** Used when testing call orders */
    private List order;

    // ----------------------------------------------------------- Constructors

    /**
     * Base constructor.
     *
     * @param identifier Used to tell which TestRule is which
     */
    public TestRule(String identifier) {
        
        this.identifier = identifier;
    }

    /**
     * Constructor sets namespace URI.
     *
     * @param digester The digester with which this rule is associated
     * @param identifier Used to tell which TestRule is which
     * @param namespaceURI Set rule namespace
     */
    public TestRule(
                    String identifier,
                    String namespaceURI) {

        this.identifier = identifier;
        setNamespaceURI(namespaceURI);

    }


    // ------------------------------------------------ Rule Implementation


    /**
     * 'Begin' call.
     */
    public void begin(Attributes attributes) {
        appendCall();
    }


    /**
     * 'Body' call.
     */
    public void body(String text) {
        this.bodyText = text;
        appendCall();
    }


    /**
     * 'End' call.
     */
    public void end() {
        appendCall();
    }


    // ------------------------------------------------ Methods


    /**
     * If a list has been set, append this to the list.
     */
    protected void appendCall() {
        if (order != null)
            order.add(this);
    }


    /**
     * Get the body text that was set.
     */
    public String getBodyText() {
        return bodyText;
    }


    /**
     * Get the identifier associated with this test.
     */
    public String getIdentifier() {
        return identifier;
    }


    /**
     * Get call order list.
     */
    public List getOrder() {
        return order;
    }


    /**
     * Set call order list
     */
    public void setOrder(List order) {
        this.order = order;
    }


    /**
     * Return the identifier.
     */
    public String toString() {
        return identifier;
    }


}
