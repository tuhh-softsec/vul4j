package org.apache.commons.digester3.plugins;

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

/**
 * Each concrete implementation of RuleFinder is an algorithm for locating a source of digester rules for a plugin. The
 * algorithm may use info explicitly provided by the user as part of the plugin declaration, or not (in which case the
 * concrete RuleFinder subclass typically has Dflt as part of its name).
 * <p>
 * Instances of this class can also be regarded as a Factory for RuleLoaders, except that an instance of a RuleLoader is
 * only created if the particular finder algorithm can locate a suitable source of rules given the plugin class and
 * associated properties.
 * <p>
 * This is an abstract class rather than an interface in order to make it possible to enhance this class in future
 * without breaking binary compatibility; it is possible to add methods to an abstract class, but not to an interface.
 * 
 * @since 1.6
 */
public abstract class RuleFinder
{

    /**
     * Apply the finder algorithm to attempt to locate a source of digester rules for the specified plugin class.
     * <p>
     * This method is invoked when a plugin is declared by the user, either via an explicit use of
     * PluginDeclarationRule, or implicitly via an "inline declaration" where the declaration and use are simultaneous.
     * <p>
     * If dynamic rules for the specified plugin class are located, then the RuleFinder will return a RuleLoader object
     * encapsulating those rules, and this object will be invoked each time the user actually requests an instance of
     * the declared plugin class, to load the custom rules associated with that plugin instance.
     * <p>
     * If no dynamic rules can be found, null is returned. This is not an error; merely an indication that this
     * particular algorithm found no matches.
     * <p>
     * The properties object holds any xml attributes the user may have specified on the plugin declaration in order to
     * indicate how to locate the plugin rules.
     * <p>
     *
     * @param d The digester instance where locating plugin classes
     * @param pluginClass The plugin Java class
     * @param p The properties object that holds any xml attributes the user may have specified on the plugin
     *          declaration in order to indicate how to locate the plugin rules.
     * @return a source of digester rules for the specified plugin class.
     * @throws PluginException if the algorithm finds a source of rules, but there is something invalid
     *         about that source.
     */
    public abstract RuleLoader findLoader( Digester d, Class<?> pluginClass, Properties p )
        throws PluginException;

}
