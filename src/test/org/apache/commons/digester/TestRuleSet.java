/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//digester/src/test/org/apache/commons/digester/TestRuleSet.java,v 1.2 2002/01/04 02:34:08 sanders Exp $
 * $Revision: 1.2 $
 * $Date: 2002/01/04 02:34:08 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
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


/**
 * RuleSet that mimics the rules set used for Employee and Address creation,
 * optionally associated with a particular namespace URI.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.2 $ $Date: 2002/01/04 02:34:08 $
 */

public class TestRuleSet extends RuleSetBase {


    // ----------------------------------------------------------- Constructors


    /**
     * Construct an instance of this RuleSet with default values.
     */
    public TestRuleSet() {

        this(null, null);

    }


    /**
     * Construct an instance of this RuleSet associated with the specified
     * prefix, associated with no namespace URI.
     *
     * @param prefix Matching pattern prefix (must end with '/') or null.
     */
    public TestRuleSet(String prefix) {

        this(prefix, null);

    }


    /**
     * Construct an instance of this RuleSet associated with the specified
     * prefix and namespace URI.
     *
     * @param prefix Matching pattern prefix (must end with '/') or null.
     * @param namespaceURI The namespace URI these rules belong to
     */
    public TestRuleSet(String prefix, String namespaceURI) {

        super();
        if (prefix == null)
            this.prefix = "";
        else
            this.prefix = prefix;
        this.namespaceURI = namespaceURI;

    }


    // ----------------------------------------------------- Instance Variables


    /**
     * The prefix for each matching pattern added to the Digester instance,
     * or an empty String for no prefix.
     */
    protected String prefix = null;


    // --------------------------------------------------------- Public Methods


    /**
     * Add the set of Rule instances defined in this RuleSet to the
     * specified <code>Digester</code> instance, associating them with
     * our namespace URI (if any).  This method should only be called
     * by a Digester instance.
     *
     * @param digester Digester instance to which the new Rule instances
     *  should be added.
     */
    public void addRuleInstances(Digester digester) {

        digester.addObjectCreate(prefix + "employee", Employee.class);
        digester.addSetProperties(prefix + "employee");
        digester.addObjectCreate("employee/address",
                                 "org.apache.commons.digester.Address");
        digester.addSetProperties(prefix + "employee/address");
        digester.addSetNext(prefix + "employee/address",
                            "addAddress");

    }


}
