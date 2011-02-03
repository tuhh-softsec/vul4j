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

import java.util.List;

import org.xml.sax.Attributes;

/**
 * <p>This rule implementation is intended to help test digester.
 * The idea is that you can test which rule matches by looking
 * at the identifier.</p>
 */
public class OrderRule extends Rule {

    /** String identifing this particular <code>TestRule</code> */
    private String identifier;

    /** Used when testing body text */
    private String bodyText;

    /** Used when testing call orders */
    private List<Rule> order;

    /**
     * Base constructor.
     *
     * @param identifier Used to tell which TestRule is which
     */
    public OrderRule(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Constructor sets namespace URI.
     *
     * @param identifier Used to tell which TestRule is which
     * @param namespaceURI Set rule namespace
     */
    public OrderRule(String identifier, String namespaceURI) {
        this.identifier = identifier;
        setNamespaceURI(namespaceURI);
    }

    /**
     * {@inheritDoc}
     */
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        appendCall();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void body(String namespace, String name, String text) throws Exception {
        this.bodyText = text;
        appendCall();
    }

    /**
     * {@inheritDoc}
     */
    public void end(String namespace, String name) throws Exception {
        appendCall();
    }

    /**
     * If a list has been set, append this to the list.
     */
    private void appendCall() {
        if (this.order != null) {
            this.order.add(this);
        }
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
    public List<Rule> getOrder() {
        return order;
    }

    /**
     * Set call order list
     */
    public void setOrder(List<Rule> order) {
        this.order = order;
    }

    /**
     * Return the identifier.
     */
    @Override
    public String toString() {
        return identifier;
    }

}
