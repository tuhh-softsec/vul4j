/*
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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


package org.apache.commons.digester;


import java.util.List;

import org.xml.sax.Attributes;


/**
 * <p>This rule implementation is intended to help test digester.
 * The idea is that you can test which rule matches by looking
 * at the identifier.</p>
 *
 * @author Robert Burrell Donkin
 * @revision $Revision: 1.9 $ $Date: 2003/02/02 15:52:14 $
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
