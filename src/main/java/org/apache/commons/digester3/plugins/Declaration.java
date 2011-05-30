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

import org.apache.commons.logging.Log;
import org.apache.commons.digester3.Digester;

/**
 * Represents a Class that can be instantiated by a PluginCreateRule, plus info on how to load custom digester rules for
 * mapping xml into that plugged-in class.
 * 
 * @since 1.6
 */
public class Declaration
{

    /** The class of the object to be instantiated. */
    private Class<?> pluginClass;

    /** The name of the class of the object to be instantiated. */
    private String pluginClassName;

    /** See {@link #setId}. */
    private String id;

    /** See {@link #setProperties}. */
    private Properties properties = new Properties();

    /** See {@link #init}. */
    private boolean initialized = false;

    /**
     * Class which is responsible for dynamically loading this plugin's rules on demand.
     */
    private RuleLoader ruleLoader = null;

    // ---------------------- constructors ----------------------------------

    /**
     * Constructor.
     *
     * @param pluginClassName The name of the class of the object to be instantiated (will be load in the init method)
     */
    public Declaration( String pluginClassName )
    {
        // We can't load the pluginClass at this time, because we don't
        // have a digester instance yet to load it through. So just
        // save the name away, and we'll load the Class object in the
        // init method.
        this.pluginClassName = pluginClassName;
    }

    /**
     * Constructor.
     *
     * @param pluginClass The class of the object to be instantiated (will be load in the init method)
     */
    public Declaration( Class<?> pluginClass )
    {
        this.pluginClass = pluginClass;
        this.pluginClassName = pluginClass.getName();
    }

    /**
     * Create an instance where a fully-initialised ruleLoader instance is provided by the caller instead of having the
     * PluginManager "discover" an appropriate one.
     *
     * @param pluginClass The class of the object to be instantiated (will be load in the init method)
     * @param ruleLoader Class which is responsible for dynamically loading this plugin's rules on demand
     */
    public Declaration( Class<?> pluginClass, RuleLoader ruleLoader )
    {
        this.pluginClass = pluginClass;
        this.pluginClassName = pluginClass.getName();
        this.ruleLoader = ruleLoader;
    }

    // ---------------------- properties -----------------------------------

    /**
     * The id that the user associated with a particular plugin declaration in the input xml. This id is later used in
     * the input xml to refer back to the original declaration.
     * <p>
     * For plugins declared "in-line", the id is null.
     *
     * @param id The id that the user associated with a particular plugin declaration in the input xml
     */
    public void setId( String id )
    {
        this.id = id;
    }

    /**
     * Return the id associated with this declaration. For plugins declared "inline", null will be returned.
     * 
     * @return The id value. May be null.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Copy all (key,value) pairs in the param into the properties member of this object.
     * <p>
     * The declaration properties cannot be explicit member variables, because the set of useful properties a user can
     * provide on a declaration depends on what RuleFinder classes are available - and extra RuleFinders can be added by
     * the user. So here we keep a map of the settings, and let the RuleFinder objects look for whatever properties they
     * consider significant.
     * <p>
     * The "id" and "class" properties are treated differently.
     *
     * @param p The properties have to be copied into the properties member of this object
     */
    public void setProperties( Properties p )
    {
        properties.putAll( p );
    }

    /**
     * Return plugin class associated with this declaration.
     * 
     * @return The pluginClass.
     */
    public Class<?> getPluginClass()
    {
        return pluginClass;
    }

    // ---------------------- methods -----------------------------------

    /**
     * Must be called exactly once, and must be called before any call to the configure method.
     *
     * @param digester The Digester instance where plugin has to be plugged
     * @param pm The plugin manager reference
     * @throws PluginException if any error occurs while loading the rules
     */
    public void init( Digester digester, PluginManager pm )
        throws PluginException
    {
        Log log = digester.getLogger();
        boolean debug = log.isDebugEnabled();
        if ( debug )
        {
            log.debug( "init being called!" );
        }

        if ( initialized )
        {
            throw new PluginAssertionFailure( "Init called multiple times." );
        }

        if ( ( pluginClass == null ) && ( pluginClassName != null ) )
        {
            try
            {
                // load the plugin class object
                pluginClass = digester.getClassLoader().loadClass( pluginClassName );
            }
            catch ( ClassNotFoundException cnfe )
            {
                throw new PluginException( "Unable to load class " + pluginClassName, cnfe );
            }
        }

        if ( ruleLoader == null )
        {
            // the caller didn't provide a ruleLoader to the constructor,
            // so get the plugin manager to "discover" one.
            log.debug( "Searching for ruleloader..." );
            ruleLoader = pm.findLoader( digester, id, pluginClass, properties );
        }
        else
        {
            log.debug( "This declaration has an explicit ruleLoader." );
        }

        if ( debug )
        {
            if ( ruleLoader == null )
            {
                log.debug( "No ruleLoader found for plugin declaration" + " id [" + id + "]" + ", class ["
                    + pluginClass.getClass().getName() + "]." );
            }
            else
            {
                log.debug( "RuleLoader of type [" + ruleLoader.getClass().getName()
                    + "] associated with plugin declaration" + " id [" + id + "]" + ", class ["
                    + pluginClass.getClass().getName() + "]." );
            }
        }

        initialized = true;
    }

    /**
     * Attempt to load custom rules for the target class at the specified pattern.
     * <p>
     * On return, any custom rules associated with the plugin class have been loaded into the Rules object currently
     * associated with the specified digester object.
     *
     * @param digester The Digester instance where plugin has to be plugged
     * @param pattern The pattern the custom rules have to be bound
     * @throws PluginException if any error occurs
     */
    public void configure( Digester digester, String pattern )
        throws PluginException
    {
        Log log = digester.getLogger();
        boolean debug = log.isDebugEnabled();
        if ( debug )
        {
            log.debug( "configure being called!" );
        }

        if ( !initialized )
        {
            throw new PluginAssertionFailure( "Not initialized." );
        }

        if ( ruleLoader != null )
        {
            ruleLoader.addRules( digester, pattern );
        }
    }

}
