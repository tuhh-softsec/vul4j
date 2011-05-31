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

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.plugins.PluginException;
import org.apache.commons.digester3.plugins.RuleFinder;
import org.apache.commons.digester3.plugins.RuleLoader;

/**
 * A rule-finding algorithm which looks for a resource file in the classpath whose name is derived from the plugin class
 * name plus a specified suffix.
 * <p>
 * If the resource-file is found, then it is expected to define a set of Digester rules in xmlrules format.
 * 
 * @since 1.6
 */
public class FinderFromDfltResource
    extends RuleFinder
{

    private static final String DFLT_RESOURCE_SUFFIX = "RuleInfo.xml";

    private final String resourceSuffix;

    /** See {@link #findLoader}. */
    public FinderFromDfltResource()
    {
        this( DFLT_RESOURCE_SUFFIX );
    }

    /**
     * Create a rule-finder which can load an xmlrules file, cache the rules away, and later add them as a plugin's
     * custom rules when that plugin is referenced.
     * 
     * @param resourceSuffix must be non-null.
     */
    public FinderFromDfltResource( String resourceSuffix )
    {
        this.resourceSuffix = resourceSuffix;
    }

    /**
     * If there exists a resource file whose name is equal to the plugin class name + the suffix specified in the
     * constructor, then load that file, run it through the xmlrules module and return an object encapsulating those
     * rules.
     * <p>
     * If there is no such resource file, then just return null.
     * <p>
     * The returned object (when non-null) will add the selected rules to the digester whenever its addRules method is
     * invoked.
     *
     * @param d The digester instance where locating plugin classes
     * @param pluginClass The plugin Java class
     * @param p The properties object that holds any xml attributes the user may have specified on the plugin
     *          declaration in order to indicate how to locate the plugin rules.
     * @return a source of digester rules for the specified plugin class.
     * @throws PluginException if the algorithm finds a source of rules, but there is something invalid
     *         about that source.
     */
    @Override
    public RuleLoader findLoader( Digester d, Class<?> pluginClass, Properties p )
        throws PluginException
    {

        String resourceName = pluginClass.getName().replace( '.', '/' ) + resourceSuffix;

        InputStream is = pluginClass.getClassLoader().getResourceAsStream( resourceName );

        if ( is == null )
        {
            // ok, no such resource
            return null;
        }

        return FinderFromResource.loadRules( d, pluginClass, is, resourceName );
    }

}
