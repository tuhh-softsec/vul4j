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

import java.util.Stack;

import org.apache.commons.digester3.spi.ObjectCreationFactory;
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
 */
public class FactoryCreateRule extends Rule {

    /** Should exceptions thrown by the factory be ignored? */
    private boolean ignoreCreateExceptions;

    /** Stock to manage */
    private Stack<Boolean> exceptionIgnoredStack;

    /**
     * The attribute containing an override class name if it is present.
     */
    private String attributeName = null;

    /**
     * The Java class name of the ObjectCreationFactory to be created.
     * This class must have a no-arguments constructor.
     */
    private String className = null;

    /**
     * The object creation factory we will use to instantiate objects
     * as required based on the attributes specified in the matched XML
     * element.
     */
    private ObjectCreationFactory<?> creationFactory = null;

    /**
     * Construct a factory create rule that will use the specified
     * class name (possibly overridden by the specified attribute if present)
     * to create an {@link ObjectCreationFactory}, which will then be used
     * to instantiate an object instance and push it onto the stack.
     *
     * @param className Default Java class name of the factory class
     * @param attributeName Attribute name which, if present, contains an
     *  override of the class name of the object creation factory to create.
     * 
     * @param ignoreCreateExceptions if true, exceptions thrown by the object
     *  creation factory will be ignored.
     */
    public <T> FactoryCreateRule(String className, String attributeName, ObjectCreationFactory<T> creationFactory, boolean ignoreCreateExceptions) {
        this.className = className;
        this.attributeName = attributeName;
        this.creationFactory = creationFactory;
        this.ignoreCreateExceptions = ignoreCreateExceptions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        if (this.ignoreCreateExceptions) {
            if (this.exceptionIgnoredStack == null) {
                this.exceptionIgnoredStack = new Stack<Boolean>();
            }

            try {
                Object instance = getFactory(attributes).createObject(attributes);

                if (this.getDigester().getLog().isDebugEnabled()) {
                    this.getDigester().getLog().debug(String.format("[FactoryCreateRule]{%s} New %s",
                            this.getDigester().getMatch(),
                            (instance == null ? "null object" : instance.getClass().getName())));
                }

                this.getDigester().push(instance);
                this.exceptionIgnoredStack.push(Boolean.FALSE);
            } catch (Exception e) {
                // log message and error
                if (this.getDigester().getLog().isInfoEnabled()) {
                    this.getDigester().getLog().info("[FactoryCreateRule] Create exception ignored: "
                            + ((e.getMessage() == null) ? e.getClass().getName() : e.getMessage()));

                    if (this.getDigester().getLog().isDebugEnabled()) {
                        this.getDigester().getLog().debug("[FactoryCreateRule] Ignored exception:", e);
                    }
                }
                this.exceptionIgnoredStack.push(Boolean.TRUE);
            }
        } else {
            Object instance = getFactory(attributes).createObject(attributes);

            if (this.getDigester().getLog().isDebugEnabled()) {
                this.getDigester().getLog().debug(String.format("[FactoryCreateRule]{%s} New %s",
                        this.getDigester().getMatch(),
                        (instance == null ? "null object" : instance.getClass().getName())));
            }

            this.getDigester().push(instance);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void end(String namespace, String name) throws Exception {
        // check if object was created 
        // this only happens if an exception was thrown and we're ignoring them
        if (this.ignoreCreateExceptions
                && this.exceptionIgnoredStack != null
                && !this.exceptionIgnoredStack.empty()) {

            if (this.exceptionIgnoredStack.pop().booleanValue()) {
                // creation exception was ignored
                // nothing was put onto the stack
                if (this.getDigester().getLog().isTraceEnabled()) {
                    this.getDigester().getLog().trace("[FactoryCreateRule] No creation so no push so no pop");
                }
                return;
            }
        }

        Object top = this.getDigester().pop();
        if (this.getDigester().getLog().isDebugEnabled()) {
            this.getDigester().getLog().debug(String.format("[FactoryCreateRule]{%s} Pop %s",
                    this.getDigester().getMatch(),
                    top.getClass().getName()));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void finish() throws Exception {
        if (this.attributeName != null) {
            this.creationFactory = null;
        }
    }

    /**
     * Render a printable version of this Rule.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("FactoryCreateRule[")
            .append("className=")
            .append(this.className)
            .append(", attributeName=")
            .append(this.attributeName);
        if (this.creationFactory != null) {
            sb.append(", creationFactory=");
            sb.append(this.creationFactory);
        }
        sb.append("]");
        return (sb.toString());
    }

    /**
     * Return an instance of our associated object creation factory,
     * creating one if necessary.
     *
     * @param attributes Attributes passed to our factory creation element
     *
     * @exception Exception if any error occurs
     */
    protected ObjectCreationFactory<?> getFactory(Attributes attributes) throws Exception {
        if (this.creationFactory == null) {
            String realClassName = this.className;
            if (this.attributeName != null) {
                String value = attributes.getValue(this.attributeName);
                if (value != null) {
                    realClassName = value;
                }
            }
            if (this.getDigester().getLog().isDebugEnabled()) {
                this.getDigester().getLog().debug(String.format("[FactoryCreateRule]{%s} New factory %s",
                        this.getDigester().getMatch(),
                        realClassName));
            }
            Class<?> clazz = this.getDigester().getClassLoader().loadClass(realClassName);
            this.creationFactory = (ObjectCreationFactory<?>) clazz.newInstance();
            this.creationFactory.setDigester(this.getDigester());
        }
        return this.creationFactory;
    }

}
