/*
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


import java.lang.ClassLoader;
import org.xml.sax.Attributes;


/**
 * <p>Rule implementation that uses an {@link ObjectCreationFactory} to create
 * a new object which it pushes onto the object stack.  When the element is
 * complete, the object will be popped.</p>
 *
 * <p>This rule is intended in situations where the element's attributes are
 * needed before the object can be created.  A common senario is for the
 * ObjectCreationFactory implementation to use the attributes  as parameters
 * in a call to either a factory method or to a non-empty constructor.
 *
 * @author Robert Burrell Donkin
 * @version $Revision: 1.3 $ $Date: 2001/08/20 16:10:13 $
 */

public class FactoryCreateRule extends Rule {


    // ----------------------------------------------------------- Constructors


    /**
     * Construct a factory create rule that will use the specified
     * class name to create an {@link ObjectCreationFactory} which will
     * then be used to create an object and push it on the stack.
     *
     * @param digester The associated Digester
     * @param className Java class name of the object creation factory class
     */
    public FactoryCreateRule(Digester digester, String className) {

        this(digester, className, null);

    }


    /**
     * Construct a factory create rule that will use the specified
     * class name (possibly overridden by the specified attribute if present)
     * to create an {@link ObjectCreationFactory}, which will then be used
     * to instantiate an object instance and push it onto the stack.
     *
     * @param digester The associated Digester
     * @param className Default Java class name of the factory class
     * @param attributeName Attribute name which, if present, contains an
     *  override of the class name of the object creation factory to create.
     */
    public FactoryCreateRule(Digester digester,
                             String className, String attributeName) {

        super(digester);
        this.className = className;
        this.attributeName = attributeName;

    }


    /**
     * Construct a factory create rule using the given, already instantiated,
     * {@link ObjectCreationFactory}.
     *
     * @param digester The associated Digester
     * @param creationFactory called on to create the object.
     */
    public FactoryCreateRule(Digester digester,
                             ObjectCreationFactory creationFactory) {

        super(digester);
        this.creationFactory = creationFactory;

    }


    // ----------------------------------------------------- Instance Variables


    /**
     * The attribute containing an override class name if it is present.
     */
    protected String attributeName = null;


    /**
     * The Java class name of the ObjectCreationFactory to be created.
     * This class must have a no-arguments constructor.
     */
    protected String className = null;


    /**
     * The object creation factory we will use to instantiate objects
     * as required based on the attributes specified in the matched XML
     * element.
     */
    protected ObjectCreationFactory creationFactory = null;


    // --------------------------------------------------------- Public Methods


    /**
     * Process the beginning of this element.
     *
     * @param attributes The attribute list of this element
     */
    public void begin(Attributes attributes) throws Exception {

        Object instance = getFactory(attributes).createObject(attributes);
        if (digester.getDebug() >= 1)
            digester.log("New " + instance.getClass().getName());
        digester.push(instance);

    }


    /**
     * Process the end of this element.
     */
    public void end() throws Exception {

        Object top = digester.pop();
        if (digester.getDebug() >= 1)
            digester.log("Pop " + top.getClass().getName());

    }


    /**
     * Clean up after parsing is complete.
     */
    public void finish() throws Exception {

        if (attributeName != null)
            creationFactory = null;

    }


    // ------------------------------------------------------ Protected Methods


    /**
     * Return an instance of our associated object creation factory,
     * creating one if necessary.
     *
     * @param attributes Attributes passed to our factory creation element
     *
     * @exception Exception if any error occurs
     */
    protected ObjectCreationFactory getFactory(Attributes attributes)
        throws Exception {

        if (creationFactory == null) {
            String realClassName = className;
            if (attributeName != null) {
                String value = attributes.getValue(attributeName);
                if (value != null)
                    realClassName = value;
            }
            if (digester.getDebug() >= 1)
                digester.log("New factory " + realClassName);
            Class clazz = digester.getClassLoader().loadClass(realClassName);
            creationFactory = (ObjectCreationFactory)
                clazz.newInstance();
            creationFactory.setDigester(digester);
        }
        return (creationFactory);

    }


}
