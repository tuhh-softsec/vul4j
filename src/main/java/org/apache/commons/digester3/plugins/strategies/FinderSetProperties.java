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
 * A rule-finding algorithm which expects the user to specify whether "automatic property setting" is desired. If this
 * class discovers that this is in fact the case for a declaration, then a RuleLoader is returned which, when invoked,
 * adds a single SetPropertiesRule instance to the digester.
 * <p>
 * This allows ordinary JavaBean classes to be used as plugins, and have xml attributes be mapped to bean properties of
 * the same name, without any custom plugin rules being created for them.
 * <p>
 * This RuleFinder is typically used as the <i>last</i> RuleFinder, so that automatic property setting only occurs if
 * there is no other source of custom rules available.
 * 
 * @since 1.6
 */
public class FinderSetProperties
    extends RuleFinder
{

    private static final String DFLT_PROPS_ATTR = "setprops";

    private static final String DFLT_FALSEVAL = "false";

    private final String propsAttr;

    private final String falseval;

    /** See {@link #findLoader}. */
    public FinderSetProperties()
    {
        this( DFLT_PROPS_ATTR, DFLT_FALSEVAL );
    }

    /**
     * Create a rule-finder which will arrange for a SetPropertiesRule to be defined for each instance of a plugin, so
     * that xml attributes map to bean properties.
     * <p>
     * Param falseval will commonly be the string "false" for config files written in English.
     * 
     * @param propsAttr must be non-null.
     * @param falseval must be non-null.
     */
    public FinderSetProperties( String propsAttr, String falseval )
    {
        this.propsAttr = propsAttr;
        this.falseval = falseval;
    }

    /**
     * Returns a RuleLoader <i>unless</i> the properties contain an entry with the name matching constructor param
     * propsAttr, and the value matching what is in falseval.
     * <p>
     * If no custom source of rules for a plugin is found, then the user almost always wants xml attributes to map to
     * java bean properties, so this is the default behaviour unless the user explicitly indicates that they do
     * <i>not</i> want a SetPropertiesRule to be provided for the plugged-in class.
     * <p>
     * The returned object (when non-null) will add a SetPropertiesRule to the digester whenever its addRules method is
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
        String state = p.getProperty( propsAttr );
        if ( ( state != null ) && state.equals( falseval ) )
        {
            // user has explicitly disabled automatic setting of properties.
            // this is not expected to be common, but allowed.
            return null;
        }

        return new LoaderSetProperties();
    }

}
