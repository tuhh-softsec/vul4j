package org.apache.commons.digester3.plugins.strategies;

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

import static org.apache.commons.beanutils.MethodUtils.getAccessibleMethod;

import java.lang.reflect.Method;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.plugins.PluginException;
import org.apache.commons.digester3.plugins.RuleLoader;
import org.apache.commons.logging.Log;

/**
 * A RuleLoader which invokes a static method on a target class, leaving that method to actually instantiate and add new
 * rules to a Digester instance.
 * 
 * @since 1.6
 */
public class LoaderFromClass
    extends RuleLoader
{

    private final Class<?> rulesClass;

    private final Method rulesMethod;

    /**
     * Constructor.
     *
     * @param rulesClass The target class
     * @param rulesMethod The method has to be invoked
     */
    public LoaderFromClass( Class<?> rulesClass, Method rulesMethod )
    {
        this.rulesClass = rulesClass;
        this.rulesMethod = rulesMethod;
    }

    /**
     * Constructor.
     *
     * @param rulesClass The target class
     * @param methodName The method name has to be invoked
     * @throws PluginException if input method can't be located inside the given class
     */
    public LoaderFromClass( Class<?> rulesClass, String methodName )
        throws PluginException
    {

        Method method = locateMethod( rulesClass, methodName );

        if ( method == null )
        {
            throw new PluginException( "rule class " + rulesClass.getName() + " does not have method " + methodName
                + " or that method has an invalid signature." );
        }

        this.rulesClass = rulesClass;
        this.rulesMethod = method;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addRules( Digester d, String path )
        throws PluginException
    {
        Log log = d.getLogger();
        boolean debug = log.isDebugEnabled();
        if ( debug )
        {
            log.debug( "LoaderFromClass loading rules for plugin at path [" + path + "]" );
        }

        try
        {
            Object[] params = { d, path };
            rulesMethod.invoke( null, params );
        }
        catch ( Exception e )
        {
            throw new PluginException(
                                       "Unable to invoke rules method " + rulesMethod + " on rules class " + rulesClass,
                                       e );
        }
    }

    /**
     * Find a method on the specified class whose name matches methodName, and whose signature is:
     * <code> public static void foo(Digester d, String patternPrefix);</code>.
     *
     * @param rulesClass The target class
     * @param methodName The method name has to be invoked
     * @return The method name has to be invoked, or null if no such method exists.
     * @throws PluginException if any error occurs while discovering the method
     */
    public static Method locateMethod( Class<?> rulesClass, String methodName )
        throws PluginException
    {
        Class<?>[] paramSpec = { Digester.class, String.class };
        Method rulesMethod = getAccessibleMethod( rulesClass, methodName, paramSpec );

        return rulesMethod;
    }

}
