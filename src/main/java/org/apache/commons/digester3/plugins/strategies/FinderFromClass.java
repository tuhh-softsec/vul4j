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
 * A rule-finding algorithm which expects the caller to specify a classname and methodname as plugin properties.
 * 
 * @since 1.6
 */
public class FinderFromClass
    extends RuleFinder
{
    private static final String DFLT_RULECLASS_ATTR = "ruleclass";

    private static final String DFLT_METHOD_ATTR = "method";

    private static final String DFLT_METHOD_NAME = "addRules";

    private final String ruleClassAttr;

    private final String methodAttr;

    private final String dfltMethodName;

    /**
     * See {@link #findLoader}.
     */
    public FinderFromClass()
    {
        this( DFLT_RULECLASS_ATTR, DFLT_METHOD_ATTR, DFLT_METHOD_NAME );
    }

    /**
     * Create a rule-finder which invokes a user-specified method on a user-specified class whenever dynamic rules for a
     * plugin need to be loaded. See the findRules method for more info.
     * 
     * @param ruleClassAttr must be non-null.
     * @param methodAttr may be null.
     * @param dfltMethodName may be null.
     */
    public FinderFromClass( String ruleClassAttr, String methodAttr, String dfltMethodName )
    {
        this.ruleClassAttr = ruleClassAttr;
        this.methodAttr = methodAttr;
        this.dfltMethodName = dfltMethodName;
    }

    /**
     * If there exists a property with the name matching constructor param ruleClassAttr, then load the specified class,
     * locate the appropriate rules-adding method on that class, and return an object encapsulating that info.
     * <p>
     * If there is no matching property provided, then just return null.
     * <p>
     * The returned object (when non-null) will invoke the target method on the selected class whenever its addRules
     * method is invoked. The target method is expected to have the following prototype:
     * <code> public static void xxxxx(Digester d, String patternPrefix); </code>
     * <p>
     * The target method can be specified in several ways. If this object's constructor was passed a non-null methodAttr
     * parameter, and the properties defines a value with that key, then that is taken as the target method name. If
     * there is no matching property, or the constructor was passed null for methodAttr, then the dfltMethodName passed
     * to the constructor is used as the name of the method on the target class. And if that was null, then
     * DFLT_METHOD_NAME will be used.
     * <p>
     * When the user explicitly declares a plugin in the input xml, the xml attributes on the declaration tag are passed
     * here as properties, so the user can select any class in the classpath (and any method on that class provided it
     * has the correct prototype) as the source of dynamic rules for the plugged-in class.
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
        String ruleClassName = p.getProperty( ruleClassAttr );
        if ( ruleClassName == null )
        {
            // nope, user hasn't requested dynamic rules to be loaded
            // from a specific class.
            return null;
        }

        // ok, we are in business
        String methodName = null;
        if ( methodAttr != null )
        {
            methodName = p.getProperty( methodAttr );
        }
        if ( methodName == null )
        {
            methodName = dfltMethodName;
        }
        if ( methodName == null )
        {
            methodName = DFLT_METHOD_NAME;
        }

        Class<?> ruleClass;
        try
        {
            // load the plugin class object
            ruleClass = digester.getClassLoader().loadClass( ruleClassName );
        }
        catch ( ClassNotFoundException cnfe )
        {
            throw new PluginException( "Unable to load class " + ruleClassName, cnfe );
        }

        return new LoaderFromClass( ruleClass, methodName );
    }

}
