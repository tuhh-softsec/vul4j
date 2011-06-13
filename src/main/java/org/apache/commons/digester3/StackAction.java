package org.apache.commons.digester3;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * An interface that can be implemented in order to get notifications of objects being pushed onto a digester stack or
 * popped from one.
 * <p>
 * Because objects are pushed onto the main object stack when a rule has created a new object, this gives the ability to
 * intercept such operations and perform modifications on created objects.
 * <p>
 * One use expected for this interface is to store information about the xml line that a particular object was created
 * from. An implementation of this interface can detect whenever an object is pushed onto the digester object stack,
 * call Digester.getDocumentLocator() to get the location within the current xml file, and store this either on the
 * object on the stack (if it supports some user-specific interface for this purpose), or build a map of
 * (object->locationinfo) separately.
 * <p>
 * It is recommended that objects implementing this interface provide a method to set a "next" action, and invoke it
 * from the callback methods. This allows multiple actions to be "chained" together.
 * <p>
 * See also Digester.setStackAction.
 * 
 * @since 1.8
 */
public interface StackAction
{

    /**
     * Invoked just before an object is to be pushed onto a digester stack.
     *
     * @param <T> whatever type is accepted
     * @param d is the digester instance.
     * @param stackName is the name of the stack onto which the object has been pushed. Null is passed to indicate the
     *            default stack.
     * @param o is the object that has just been pushed. Calling peek on the specified stack will return
     *        the same object.
     * @return the object to be pushed. Normally, parameter o is returned but this method could return an alternate
     *         object to be pushed instead (eg a proxy for the provided object).
     */
    <T> T onPush( Digester d, String stackName, T o );

    /**
     * Invoked just after an object has been popped from a digester stack.
     *
     * @param <T> whatever type is accepted
     * @param d is the digester instance.
     * @param stackName is the name of the stack from which the object has been popped. Null is passed to indicate the
     *            default stack.
     * @param o is the object that has just been popped.
     * @return the object to be returned to the called. Normally, parameter o is returned but this method could return
     *         an alternate object.
     */
    <T> T onPop( Digester d, String stackName, T o );

}
