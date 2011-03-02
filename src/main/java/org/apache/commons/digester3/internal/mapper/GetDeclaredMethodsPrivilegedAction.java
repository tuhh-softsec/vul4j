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
package org.apache.commons.digester3.internal.mapper;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Extracts all declared methods from a class via the SecurityManager.
 */
final class GetDeclaredMethodsPrivilegedAction implements PrivilegedAction<Method[]> {

    public static Method[] getDeclaredMethods(final Class<?> mapper) {
        GetDeclaredMethodsPrivilegedAction action = new GetDeclaredMethodsPrivilegedAction(mapper);
        if (System.getSecurityManager() != null) {
            return AccessController.doPrivileged(action);
        }
        return action.run();
    }

    private final Class<?> mapper;

    private GetDeclaredMethodsPrivilegedAction(final Class<?> mapper) {
        this.mapper = mapper;
    }

    /**
     * {@inheritDoc}
     */
    public Method[] run() {
        return this.mapper.getDeclaredMethods();
    }

}
