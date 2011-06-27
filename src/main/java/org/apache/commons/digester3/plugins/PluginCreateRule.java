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

import java.util.List;

import org.apache.commons.digester3.Rule;
import org.apache.commons.logging.Log;
import org.xml.sax.Attributes;

/**
 * Allows the original rules for parsing the configuration file to define points at which plugins are allowed, by
 * configuring a PluginCreateRule with the appropriate pattern.
 * 
 * @since 1.6
 */
public class PluginCreateRule
    extends Rule
    implements InitializableRule
{

    // see setPluginClassAttribute
    private String pluginClassAttrNs = null;

    private String pluginClassAttr = null;

    // see setPluginIdAttribute
    private String pluginIdAttrNs = null;

    private String pluginIdAttr = null;

    /**
     * In order to invoke the addRules method on the plugin class correctly, we need to know the pattern which this rule
     * is matched by.
     */
    private String pattern;

    /** A base class that any plugin must derive from. */
    private Class<?> baseClass = null;

    /**
     * Info about optional default plugin to be used if no plugin-id is specified in the input data. This can simplify
     * the syntax where one particular plugin is usually used.
     */
    private Declaration defaultPlugin;

    /**
     * Currently, none of the Rules methods allow exceptions to be thrown. Therefore if this class cannot initialise
     * itself properly, it cannot cause the digester to stop. Instead, we cache the exception and throw it the first
     * time the begin() method is called.
     */
    private PluginConfigurationException initException;

    // -------------------- constructors -------------------------------------

    /**
     * Create a plugin rule where the user <i>must</i> specify a plugin-class or plugin-id.
     * 
     * @param baseClass is the class which any specified plugin <i>must</i> be descended from.
     */
    public PluginCreateRule( Class<?> baseClass )
    {
        this.baseClass = baseClass;
    }

    /**
     * Create a plugin rule where the user <i>may</i> specify a plugin. If the user doesn't specify a plugin, then the
     * default class specified in this constructor is used.
     * 
     * @param baseClass is the class which any specified plugin <i>must</i> be descended from.
     * @param dfltPluginClass is the class which will be used if the user doesn't specify any plugin-class or plugin-id.
     *            This class will have custom rules installed for it just like a declared plugin.
     */
    public PluginCreateRule( Class<?> baseClass, Class<?> dfltPluginClass )
    {
        this.baseClass = baseClass;
        if ( dfltPluginClass != null )
        {
            defaultPlugin = new Declaration( dfltPluginClass );
        }
    }

    /**
     * Create a plugin rule where the user <i>may</i> specify a plugin. If the user doesn't specify a plugin, then the
     * default class specified in this constructor is used.
     * 
     * @param baseClass is the class which any specified plugin <i>must</i> be descended from.
     * @param dfltPluginClass is the class which will be used if the user doesn't specify any plugin-class or plugin-id.
     *            This class will have custom rules installed for it just like a declared plugin.
     * @param dfltPluginRuleLoader is a RuleLoader instance which knows how to load the custom rules associated with
     *            this default plugin.
     */
    public PluginCreateRule( Class<?> baseClass, Class<?> dfltPluginClass, RuleLoader dfltPluginRuleLoader )
    {
        this.baseClass = baseClass;
        if ( dfltPluginClass != null )
        {
            defaultPlugin = new Declaration( dfltPluginClass, dfltPluginRuleLoader );
        }
    }

    // ------------------- properties ---------------------------------------

    /**
     * Sets the xml attribute which the input xml uses to indicate to a PluginCreateRule which class should be
     * instantiated.
     * <p>
     * See {@link PluginRules#setPluginClassAttribute} for more info.
     *
     * @param namespaceUri is the namespace uri that the specified attribute is in. If the attribute is in no namespace,
     *            then this should be null. Note that if a namespace is used, the attrName value should <i>not</i>
     *            contain any kind of namespace-prefix. Note also that if you are using a non-namespace-aware parser,
     *            this parameter <i>must</i> be null.
     * @param attrName is the attribute whose value contains the name of the class to be instantiated.
     */
    public void setPluginClassAttribute( String namespaceUri, String attrName )
    {
        pluginClassAttrNs = namespaceUri;
        pluginClassAttr = attrName;
    }

    /**
     * Sets the xml attribute which the input xml uses to indicate to a PluginCreateRule which plugin declaration is
     * being referenced.
     * <p>
     * See {@link PluginRules#setPluginIdAttribute} for more info.
     * 
     * @param namespaceUri is the namespace uri that the specified attribute is in. If the attribute is in no namespace,
     *            then this should be null. Note that if a namespace is used, the attrName value should <i>not</i>
     *            contain any kind of namespace-prefix. Note also that if you are using a non-namespace-aware parser,
     *            this parameter <i>must</i> be null.
     * @param attrName is the attribute whose value contains the id of the plugin declaration to be used when
     *            instantiating an object.
     */
    public void setPluginIdAttribute( String namespaceUri, String attrName )
    {
        pluginIdAttrNs = namespaceUri;
        pluginIdAttr = attrName;
    }

    // ------------------- methods --------------------------------------------

    /**
     * {@inheritDoc}
     */
    public void postRegisterInit( String matchPattern )
    {
        Log log = LogUtils.getLogger( getDigester() );
        boolean debug = log.isDebugEnabled();
        if ( debug )
        {
            log.debug( "PluginCreateRule.postRegisterInit" + ": rule registered for pattern [" + matchPattern + "]" );
        }

        if ( getDigester() == null )
        {
            // We require setDigester to be called before this method.
            // Note that this means that PluginCreateRule cannot be added
            // to a Rules object which has not yet been added to a
            // Digester object.
            initException =
                new PluginConfigurationException( "Invalid invocation of postRegisterInit" + ": digester not set." );
            throw initException;
        }

        if ( pattern != null )
        {
            // We have been called twice, ie a single instance has been
            // associated with multiple patterns.
            //
            // Generally, Digester Rule instances can be associated with
            // multiple patterns. However for plugins, this creates some
            // complications. Some day this may be supported; however for
            // now we just reject this situation.
            initException =
                new PluginConfigurationException( "A single PluginCreateRule instance has been mapped to"
                    + " multiple patterns; this is not supported." );
            throw initException;
        }

        if ( matchPattern.indexOf( '*' ) != -1 )
        {
            // having wildcards in patterns is extremely difficult to
            // deal with. For now, we refuse to allow this.
            //
            // TODO: check for any chars not valid in xml element name
            // rather than just *.
            //
            // Reasons include:
            // (a) handling recursive plugins, and
            // (b) determining whether one pattern is "below" another,
            // as done by PluginRules. Without wildcards, "below"
            // just means startsWith, which is easy to check.
            initException =
                new PluginConfigurationException( "A PluginCreateRule instance has been mapped to" + " pattern ["
                    + matchPattern + "]." + " This pattern includes a wildcard character."
                    + " This is not supported by the plugin architecture." );
            throw initException;
        }

        if ( baseClass == null )
        {
            baseClass = Object.class;
        }

        PluginRules rules = (PluginRules) getDigester().getRules();
        PluginManager pm = rules.getPluginManager();

        // check default class is valid
        if ( defaultPlugin != null )
        {
            if ( !baseClass.isAssignableFrom( defaultPlugin.getPluginClass() ) )
            {
                initException =
                    new PluginConfigurationException( "Default class [" + defaultPlugin.getPluginClass().getName()
                        + "] does not inherit from [" + baseClass.getName() + "]." );
                throw initException;
            }

            try
            {
                defaultPlugin.init( getDigester(), pm );

            }
            catch ( PluginException pwe )
            {

                throw new PluginConfigurationException( pwe.getMessage(), pwe.getCause() );
            }
        }

        // remember the pattern for later
        pattern = matchPattern;

        if ( pluginClassAttr == null )
        {
            // the user hasn't set explicit xml attr names on this rule,
            // so fetch the default values
            pluginClassAttrNs = rules.getPluginClassAttrNs();
            pluginClassAttr = rules.getPluginClassAttr();

            if ( debug )
            {
                log.debug( "init: pluginClassAttr set to per-digester values [" + "ns=" + pluginClassAttrNs + ", name="
                    + pluginClassAttr + "]" );
            }
        }
        else
        {
            if ( debug )
            {
                log.debug( "init: pluginClassAttr set to rule-specific values [" + "ns=" + pluginClassAttrNs
                    + ", name=" + pluginClassAttr + "]" );
            }
        }

        if ( pluginIdAttr == null )
        {
            // the user hasn't set explicit xml attr names on this rule,
            // so fetch the default values
            pluginIdAttrNs = rules.getPluginIdAttrNs();
            pluginIdAttr = rules.getPluginIdAttr();

            if ( debug )
            {
                log.debug( "init: pluginIdAttr set to per-digester values [" + "ns=" + pluginIdAttrNs + ", name="
                    + pluginIdAttr + "]" );
            }
        }
        else
        {
            if ( debug )
            {
                log.debug( "init: pluginIdAttr set to rule-specific values [" + "ns=" + pluginIdAttrNs + ", name="
                    + pluginIdAttr + "]" );
            }
        }
    }

    /**
     * Invoked when the Digester matches this rule against an xml element.
     * <p>
     * A new instance of the target class is created, and pushed onto the stack. A new "private" PluginRules object is
     * then created and set as the digester's default Rules object. Any custom rules associated with the plugin class
     * are then loaded into that new Rules object. Finally, any custom rules that are associated with the current
     * pattern (such as SetPropertiesRules) have their begin methods executed.
     * 
     * @param namespace the namespace URI of the matching element, or an empty string if the parser is not namespace
     *            aware or the element has no namespace
     * @param name the local name if the parser is namespace aware, or just the element name otherwise
     * @param attributes The attribute list of this element
     * @throws Exception if any error occurs
     */
    @Override
    public void begin( String namespace, String name, org.xml.sax.Attributes attributes )
        throws Exception
    {
        Log log = getDigester().getLogger();
        boolean debug = log.isDebugEnabled();
        if ( debug )
        {
            log.debug( "PluginCreateRule.begin" + ": pattern=[" + pattern + "]" + " match=[" + getDigester().getMatch()
                + "]" );
        }

        if ( initException != null )
        {
            // we had a problem during initialisation that we could
            // not report then; report it now.
            throw initException;
        }

        // load any custom rules associated with the plugin
        PluginRules oldRules = (PluginRules) getDigester().getRules();
        PluginManager pluginManager = oldRules.getPluginManager();
        Declaration currDeclaration = null;

        String pluginClassName;
        if ( pluginClassAttrNs == null )
        {
            // Yep, this is ugly.
            //
            // In a namespace-aware parser, the one-param version will
            // return attributes with no namespace.
            //
            // In a non-namespace-aware parser, the two-param version will
            // never return any attributes, ever.
            pluginClassName = attributes.getValue( pluginClassAttr );
        }
        else
        {
            pluginClassName = attributes.getValue( pluginClassAttrNs, pluginClassAttr );
        }

        String pluginId;
        if ( pluginIdAttrNs == null )
        {
            pluginId = attributes.getValue( pluginIdAttr );
        }
        else
        {
            pluginId = attributes.getValue( pluginIdAttrNs, pluginIdAttr );
        }

        if ( pluginClassName != null )
        {
            // The user is using a plugin "inline", ie without a previous
            // explicit declaration. If they have used the same plugin class
            // before, we have already gone to the effort of creating a
            // Declaration object, so retrieve it. If there is no existing
            // declaration object for this class, then create one.

            currDeclaration = pluginManager.getDeclarationByClass( pluginClassName );

            if ( currDeclaration == null )
            {
                currDeclaration = new Declaration( pluginClassName );
                try
                {
                    currDeclaration.init( getDigester(), pluginManager );
                }
                catch ( PluginException pwe )
                {
                    throw new PluginInvalidInputException( pwe.getMessage(), pwe.getCause() );
                }
                pluginManager.addDeclaration( currDeclaration );
            }
        }
        else if ( pluginId != null )
        {
            currDeclaration = pluginManager.getDeclarationById( pluginId );

            if ( currDeclaration == null )
            {
                throw new PluginInvalidInputException( "Plugin id [" + pluginId + "] is not defined." );
            }
        }
        else if ( defaultPlugin != null )
        {
            currDeclaration = defaultPlugin;
        }
        else
        {
            throw new PluginInvalidInputException( "No plugin class specified for element " + pattern );
        }

        // get the class of the user plugged-in type
        Class<?> pluginClass = currDeclaration.getPluginClass();

        String path = getDigester().getMatch();

        // create a new Rules object and effectively push it onto a stack of
        // rules objects. The stack is actually a linked list; using the
        // PluginRules constructor below causes the new instance to link
        // to the previous head-of-stack, then the Digester.setRules() makes
        // the new instance the new head-of-stack.
        PluginRules newRules = new PluginRules( getDigester(), path, oldRules, pluginClass );
        getDigester().setRules( newRules );

        if ( debug )
        {
            log.debug( "PluginCreateRule.begin: installing new plugin: " + "oldrules=" + oldRules.toString()
                + ", newrules=" + newRules.toString() );
        }

        // load up the custom rules
        currDeclaration.configure( getDigester(), pattern );

        // create an instance of the plugin class
        Object instance = pluginClass.newInstance();
        getDigester().push( instance );
        if ( debug )
        {
            log.debug( "PluginCreateRule.begin" + ": pattern=[" + pattern + "]" + " match=[" + getDigester().getMatch()
                + "]" + " pushed instance of plugin [" + pluginClass.getName() + "]" );
        }

        // and now we have to fire any custom rules which would have
        // been matched by the same path that matched this rule, had
        // they been loaded at that time.
        List<Rule> rules = newRules.getDecoratedRules().match( namespace, path, name, attributes );
        fireBeginMethods( rules, namespace, name, attributes );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void body( String namespace, String name, String text )
        throws Exception
    {

        // While this class itself has no work to do in the body method,
        // we do need to fire the body methods of all dynamically-added
        // rules matching the same path as this rule. During begin, we had
        // to manually execute the dynamic rules' begin methods because they
        // didn't exist in the digester's Rules object when the match begin.
        // So in order to ensure consistent ordering of rule execution, the
        // PluginRules class deliberately avoids returning any such rules
        // in later calls to the match method, instead relying on this
        // object to execute them at the appropriate time.
        //
        // Note that this applies only to rules matching exactly the path
        // which is also matched by this PluginCreateRule.

        String path = getDigester().getMatch();
        PluginRules newRules = (PluginRules) getDigester().getRules();
        List<Rule> rules = newRules.getDecoratedRules().match( namespace, path, name, null );
        fireBodyMethods( rules, namespace, name, text );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void end( String namespace, String name )
        throws Exception
    {
        // see body method for more info
        String path = getDigester().getMatch();
        PluginRules newRules = (PluginRules) getDigester().getRules();
        List<Rule> rules = newRules.getDecoratedRules().match( namespace, path, name, null );
        fireEndMethods( rules, namespace, name );

        // pop the stack of PluginRules instances, which
        // discards all custom rules associated with this plugin
        getDigester().setRules( newRules.getParent() );

        // and get rid of the instance of the plugin class from the
        // digester object stack.
        getDigester().pop();
    }

    /**
     * Return the pattern that this Rule is associated with.
     * <p>
     * In general, Rule instances <i>can</i> be associated with multiple patterns. A PluginCreateRule, however, will
     * only function correctly when associated with a single pattern. It is possible to fix this, but I can't be
     * bothered just now because this feature is unlikely to be used.
     * </p>
     * 
     * @return The pattern value
     */
    public String getPattern()
    {
        return pattern;
    }

    /**
     * Duplicate the processing that the Digester does when firing the begin methods of rules. It would be really nice
     * if the Digester class provided a way for this functionality to just be invoked directly.
     *
     * @param rules The rules which {@link Rule#begin(String, String, Attributes)} method has to be fired
     * @param namespace the namespace URI of the matching element, or an empty string if the parser is not namespace
     *            aware or the element has no namespace
     * @param name the local name if the parser is namespace aware, or just the element name otherwise
     * @param list The attribute list of this element
     * @throws Exception if any error occurs
     */
    public void fireBeginMethods( List<Rule> rules, String namespace, String name, Attributes list )
        throws Exception
    {

        if ( ( rules != null ) && ( !rules.isEmpty() ) )
        {
            Log log = getDigester().getLogger();
            boolean debug = log.isDebugEnabled();
            for ( Rule rule : rules )
            {
                if ( debug )
                {
                    log.debug( "  Fire begin() for " + rule );
                }
                try
                {
                    rule.begin( namespace, name, list );
                }
                catch ( Exception e )
                {
                    throw getDigester().createSAXException( e );
                }
                catch ( Error e )
                {
                    throw e;
                }
            }
        }
    }

    /**
     * Duplicate the processing that the Digester does when firing the {@link Rule#body(String, String, String)} methods
     * of rules.
     *
     * It would be really nice if the Digester class provided a way for this functionality to just be invoked directly.
     *
     * @param rules The rules which {@link Rule#body(String, String, String)} method has to be fired
     * @param namespace the namespace URI of the matching element, or an empty string if the parser is not namespace
     *            aware or the element has no namespace
     * @param name the local name if the parser is namespace aware, or just the element name otherwise
     * @param text The text of the body of this element
     * @throws Exception if any error occurs
     */
    private void fireBodyMethods( List<Rule> rules, String namespaceURI, String name, String text )
        throws Exception
    {
        if ( ( rules != null ) && ( !rules.isEmpty() ) )
        {
            Log log = getDigester().getLogger();
            boolean debug = log.isDebugEnabled();
            for ( Rule rule : rules )
            {
                if ( debug )
                {
                    log.debug( "  Fire body() for " + rule );
                }
                try
                {
                    rule.body( namespaceURI, name, text );
                }
                catch ( Exception e )
                {
                    throw getDigester().createSAXException( e );
                }
                catch ( Error e )
                {
                    throw e;
                }
            }
        }
    }

    /**
     * Duplicate the processing that the Digester does when firing the end methods of rules.
     *
     * It would be really nice if the Digester class provided a way for this functionality to just be invoked directly.
     *
     * @param rules The rules which {@link Rule#end(String, String)} method has to be fired
     * @param namespaceURI the namespace URI of the matching element, or an empty string if the parser is not namespace
     *            aware or the element has no namespace
     * @param name the local name if the parser is namespace aware, or just the element name otherwise
     * @throws Exception if any error occurs
     */
    public void fireEndMethods( List<Rule> rules, String namespaceURI, String name )
        throws Exception
    {
        // Fire "end" events for all relevant rules in reverse order
        if ( rules != null )
        {
            Log log = getDigester().getLogger();
            boolean debug = log.isDebugEnabled();
            for ( int i = 0; i < rules.size(); i++ )
            {
                int j = ( rules.size() - i ) - 1;
                Rule rule = rules.get( j );
                if ( debug )
                {
                    log.debug( "  Fire end() for " + rule );
                }
                try
                {
                    rule.end( namespaceURI, name );
                }
                catch ( Exception e )
                {
                    throw getDigester().createSAXException( e );
                }
                catch ( Error e )
                {
                    throw e;
                }
            }
        }
    }

}
