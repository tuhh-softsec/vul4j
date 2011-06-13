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

import java.util.Properties;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.plugins.PluginException;
import org.apache.commons.digester3.plugins.RuleFinder;
import org.apache.commons.digester3.plugins.RuleLoader;

/**
 * A rule-finding algorithm which looks for a method with a specific name on a class whose name is derived from the
 * plugin class name.
 * 
 * @since 1.6
 */
public class FinderFromDfltClass
    extends RuleFinder
{
    private static final String DFLT_RULECLASS_SUFFIX = "RuleInfo";

    private static final String DFLT_METHOD_NAME = "addRules";

    private final String rulesClassSuffix;

    private final String methodName;

    /** See {@link #findLoader}. */
    public FinderFromDfltClass()
    {
        this( DFLT_RULECLASS_SUFFIX, DFLT_METHOD_NAME );
    }

    /**
     * Create a rule-finder which invokes a method on a class whenever dynamic rules for a plugin need to be loaded. See
     * the findRules method for more info.
     * 
     * @param rulesClassSuffix must be non-null.
     * @param methodName may be null.
     */
    public FinderFromDfltClass( String rulesClassSuffix, String methodName )
    {
        this.rulesClassSuffix = rulesClassSuffix;
        this.methodName = methodName;
    }

    /**
     * If there exists a class whose name is the plugin class name + the suffix specified to the constructor, then load
     * that class, locate the appropriate rules-adding method on that class, and return an object encapsulating that
     * info.
     * <p>
     * If there is no such class, then just return null.
     * <p>
     * The returned object (when non-null) will invoke the target method on the selected class whenever its addRules
     * method is invoked. The target method is expected to have the following prototype:
     * <code> public static void xxxxx(Digester d, String patternPrefix); </code>
     *
     * @param digester The digester instance where locating plugin classes
     * @param pluginClass The plugin Java class
     * @param p The properties object that holds any xml attributes the user may have specified on the plugin
     *          declaration in order to indicate how to locate the plugin rules.
     * @return a source of digester rules for the specified plugin class.
     * @throws PluginException if the algorithm finds a source of rules, but there is something invalid
     *         about that source.
     */
    @Override
    public RuleLoader findLoader( Digester digester, Class<?> pluginClass, Properties p )
        throws PluginException
    {
        String rulesClassName = pluginClass.getName() + rulesClassSuffix;

        Class<?> rulesClass = null;
        try
        {
            rulesClass = digester.getClassLoader().loadClass( rulesClassName );
        }
        catch ( ClassNotFoundException cnfe )
        {
            // nope, no rule-info class in the classpath
            return null;
        }

        if ( methodName == null )
        {
            return new LoaderFromClass( rulesClass, DFLT_METHOD_NAME );
        }

        return new LoaderFromClass( rulesClass, methodName );
    }

}
