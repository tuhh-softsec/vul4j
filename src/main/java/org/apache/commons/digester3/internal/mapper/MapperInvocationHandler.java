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

import static org.apache.commons.digester3.internal.mapper.GetDeclaredMethodsPrivilegedAction.getDeclaredMethods;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.DigesterLoader;
import org.xml.sax.InputSource;

/**
 * Plays the role of the mapper to wrap the Digester.
 */
public final class MapperInvocationHandler implements InvocationHandler {

    private final Set<Method> mapperMethods = new HashSet<Method>();

    private final DigesterLoader loader;

    public MapperInvocationHandler(final Class<?> mapper, final DigesterLoader loader) {
        // collect all the valid mapper method
        this.collectMethodsFrom(mapper);

        for (Class<?> type : mapper.getInterfaces()) {
            this.collectMethodsFrom(type);
        }

        // store the related loader
        this.loader = loader;
    }

    private <T> void collectMethodsFrom(Class<T> type) {
        for (Method method : getDeclaredMethods(type)) {
            // check if the current method is a valid mapper method
            if (method.getParameterTypes().length != 1) { // at least the input has to be specified
                continue; // ignore it
            }

            Class<?> parameterType = method.getParameterTypes()[0];
            if (!File.class.isAssignableFrom(parameterType)
                    && !InputSource.class.isAssignableFrom(parameterType)
                    && !InputStream.class.isAssignableFrom(parameterType)
                    && !Reader.class.isAssignableFrom(parameterType)
                    && !String.class.isAssignableFrom(parameterType)
                    && !URL.class.isAssignableFrom(parameterType)) {
                continue; // ignore it
            }

            this.mapperMethods.add(method);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (!this.mapperMethods.contains(method)) {
            return null;
        }

        Digester digester = this.loader.newDigester();

        Object arg = args[0];
        if (File.class.isInstance(arg)) {
            return digester.parse((File) arg);
        } else if (InputSource.class.isInstance(arg)) {
            return digester.parse((InputSource) arg);
        } else if (InputStream.class.isInstance(arg)) {
            return digester.parse((InputStream) arg);
        } else if (Reader.class.isInstance(arg)) {
            return digester.parse((Reader) arg);
        } else if (String.class.isInstance(arg)) {
            return digester.parse((String) arg);
        } else if (URL.class.isInstance(arg)) {
            return digester.parse((URL) arg);
        }

        return null;
    }

}
