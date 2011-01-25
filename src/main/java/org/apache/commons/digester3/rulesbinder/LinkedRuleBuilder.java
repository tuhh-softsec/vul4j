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
package org.apache.commons.digester3.rulesbinder;

import org.apache.commons.digester3.ObjectCreationFactory;

/**
 * Builder invoked to bind one or more rules to a pattern.
 */
public interface LinkedRuleBuilder {

    /**
     * Construct rule that automatically sets a property from the body text, taking the property
     * name the same as the current element.
     */
    void beanPropertySetter();

    /**
     * Construct rule that sets the given property from the body text.
     *
     * @param propertyName The name of property to set.
     */
    void beanPropertySetter(String propertyName);

    /**
     * Calls a method on an object on the stack (normally the top/parent object), passing arguments collected from
     * subsequent {@link #callParam(int)} rule or from the body of this element.
     *
     * @param methodName Method name of the parent object to call
     */
    void callMethod(String methodName);

    /**
     * Saves a parameter for use by a surrounding {@link #callMethod(String)}.
     *
     * @param paramIndex The zero-relative parameter number
     */
    void callParam(int paramIndex);

    /**
     * Construct a "call parameter" rule that will save the body text of this element as the parameter value.
     *
     * @param paramIndex The zero-relative parameter number
     */
    void callParamPath(int paramIndex);

    /**
     * Uses an {@link ObjectCreationFactory} to create a new object which it pushes onto the object stack.
     *
     * When the element is complete, the object will be popped.
     *
     * @param className Java class name of the object creation factory class
     */
    void factoryCreate(String className);

    /**
     * Construct a factory create rule that will use the specified class to create an {@link ObjectCreationFactory}
     * which will then be used to create an object and push it on the stack.
     *
     * @param clazz Java class of the object creation factory class
     */
    void factoryCreate(Class<?> clazz);

    /**
     * Construct a factory create rule using the given, already instantiated, {@link ObjectCreationFactory}.
     *
     * @param <T> the type of created object by the given factory
     * @param creationFactory called on to create the object
     */
    <T> void factoryCreate(ObjectCreationFactory<T> creationFactory);

    /**
     * Construct an object with the specified class name.
     *
     * @param className Java class name of the object to be created
     */
    void objectCreate(String className);

    /**
     * Construct an object with the specified class.
     *
     * @param clazz Java class of the object to be created.
     */
    void objectCreate(Class<?> clazz);

    /**
     * Saves a parameter for use by a surrounding {@link #callMethod(String)}.
     *
     * @param <T> The parameter type to pass along
     * @param paramObj The parameter to pass along
     */
    <T> void objectParam(T paramObj);

    /**
     * Sets properties on the object at the top of the stack,
     * based on child elements with names matching properties on that  object.
     */
    void setNestedProperties();

    /**
     * Calls a method on the (top-1) (parent) object, passing the top object (child) as an argument,
     * commonly used to establish parent-child relationships.
     *
     * @param methodName Method name of the parent method to call
     */
    void setNext(String methodName);

    /**
     * Sets properties on the object at the top of the stack, based on attributes with corresponding names.
     */
    void setProperties();

    /**
     * Sets an individual property on the object at the top of the stack, based on attributes with specified names.
     *
     * @param name Name of the attribute that will contain the name of the property to be set
     * @param value Name of the attribute that will contain the value to which the property should be set
     */
    void setProperty(String name, String value);

    /**
     * Calls a method on the root object on the stack, passing the top object (child) as an argument.
     *
     * @param methodName Method name of the parent method to call
     */
    void setRoot(String methodName);

    /**
     * Calls a "set parent" method on the top (child) object, passing the (top-1) (parent) object as an argument.
     *
     * @param methodName Method name of the "set parent" method to call
     */
    void setTop(String methodName);

}
