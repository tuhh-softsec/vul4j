/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//digester/src/java/org/apache/commons/digester/plugins/PluginCreateRule.java,v 1.9 2003/11/18 22:14:22 rdonkin Exp $
 * $Revision: 1.9 $
 * $Date: 2003/11/18 22:14:22 $
 *
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgement:  
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "Apache", "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache" nor may "Apache" appear in their names without prior 
 *    written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */ 
 
package org.apache.commons.digester.plugins;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.List;
import java.io.File;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.digester.Rules;
import org.apache.commons.logging.Log;

/**
 * Allows the original rules for parsing the configuration file to define
 * points at which plugins are allowed, by configuring a PluginCreateRule
 * with the appropriate pattern.
 * 
 * @author Simon Kitching
 */
public class PluginCreateRule extends Rule implements InitializableRule {

    // the xml attribute the user uses on an xml element to specify
    // the plugin's class
    public static final String GLOBAL_PLUGIN_CLASS_ATTR_NS = null;
    public static final String GLOBAL_PLUGIN_CLASS_ATTR = "plugin-class";

    // the xml attribute the user uses on an xml element to specify
    // the plugin's class
    public static final String GLOBAL_PLUGIN_ID_ATTR_NS = null;
    public static final String GLOBAL_PLUGIN_ID_ATTR = "plugin-id";
    
    // see setGlobalPluginClassAttribute
    private static String globalPluginClassAttrNs = GLOBAL_PLUGIN_CLASS_ATTR_NS;
    private static String globalPluginClassAttr = GLOBAL_PLUGIN_CLASS_ATTR;

    // see setGlobalPluginIdAttribute
    private static String globalPluginIdAttrNs = GLOBAL_PLUGIN_ID_ATTR_NS;
    private static String globalPluginIdAttr = GLOBAL_PLUGIN_ID_ATTR;
    
    // see setPluginClassAttribute
    private String pluginClassAttrNs = globalPluginClassAttrNs;
    private String pluginClassAttr = globalPluginClassAttr;
    
    // see setPluginIdAttribute
    private String pluginIdAttrNs = globalPluginIdAttrNs;
    private String pluginIdAttr = globalPluginIdAttr;
    
    /**
     * In order to invoke the addRules method on the plugin class correctly,
     * we need to know the pattern which this rule is matched by.
     */
    private String pattern;

    /** A base class that any plugin must derive from. */
    private Class baseClass = null;

    /**
     * Info about optional default plugin to be used if no plugin-id is
     * specified in the input data. This can simplify the syntax where one
     * particular plugin is usually used.
     */
    private Declaration defaultPlugin;

    /**
     * Currently, none of the Rules methods allow exceptions to be thrown.
     * Therefore if this class cannot initialise itself properly, it cannot
     * cause the digester to stop. Instead, we cache the exception and throw
     * it the first time the begin() method is called.
     */
    private PluginConfigurationException initException;

    /**
     * Our private set of rules associated with the concrete class that
     * the user requested to be instantiated. This object is only valid
     * between a call to begin() and the corresponding call to end().
     */
    private PluginRules localRules; 
    
    //-------------------- static methods -----------------------------------
    
    /**
     * Sets the xml attribute which the input xml uses to indicate to a 
     * PluginCreateRule which class should be instantiated.
     * <p>
     * Example:
     * <pre>
     * PluginCreateRule.setGlobalPluginClassAttribute(null, "class");
     * </pre>
     * will allow this in the input xml:
     * <pre>
     *  [root]
     *    [some-plugin class="com.acme.widget"] ......
     * </pre>
     *
     * Note that this changes the default for <i>all</i> PluginCreateRule
     * instances. To override just specific PluginCreateRule instances (which
     * may be more friendly in container-based environments), see method
     * setPluginClassAttribute.
     *
     * @param namespaceUri is the namespace uri that the specified attribute
     * is in. If the attribute is in no namespace, then this should be null.
     * Note that if a namespace is used, the attrName value should <i>not</i>
     * contain any kind of namespace-prefix. Note also that if you are using
     * a non-namespace-aware parser, this parameter <i>must</i> be null.
     *
     * @param attrName is the attribute whose value contains the name of the
     * class to be instantiated.
     */
    public static void setGlobalPluginClassAttribute(String namespaceUri, 
                                                     String attrName) {
        globalPluginClassAttrNs = namespaceUri;
        globalPluginClassAttr = attrName;
    }

    /**
     * Sets the xml attribute which the input xml uses to indicate to a 
     * PluginCreateRule which plugin declaration is being referenced.
     * <p>
     * Example:
     * <pre>
     * PluginCreateRule.setGlobalPluginIdAttribute(null, "id");
     * </pre>
     * will allow this in the input xml:
     * <pre>
     *  [root]
     *    [some-plugin id="widget"] ......
     * </pre>
     *
     * Note that this changes the default for <i>all</i> PluginCreateRule
     * instances. To override just specific PluginCreateRule instances (which
     * may be more friendly in container-based environments), see method
     * setPluginIdAttribute.
     *
     * @param namespaceUri is the namespace uri that the specified attribute
     * is in. If the attribute is in no namespace, then this should be null.
     * Note that if a namespace is used, the attrName value should <i>not</i>
     * contain any kind of namespace-prefix. Note also that if you are using
     * a non-namespace-aware parser, this parameter <i>must</i> be null.
     *
     * @param attrName is the attribute whose value contains the id of the
     * plugin declaration to be used when instantiating an object.
     */
    public static void setGlobalPluginIdAttribute(String namespaceUri, 
                                                  String attrName) {
        globalPluginIdAttrNs = namespaceUri;
        globalPluginIdAttr = attrName;
    }

    //-------------------- constructors -------------------------------------

    /**
     * Create a plugin rule where the user <i>must</i> specify a plugin-class
     * or plugin-id.
     * 
     * @param baseClass is the class which any specified plugin <i>must</i> be
     * descended from.
     */
    public PluginCreateRule(Class baseClass) {
        super();
        this.baseClass = baseClass;
    }

    /**
     * Create a plugin rule where the user <i>may</i> specify a plugin.
     * If the user doesn't specify a plugin, then the default class specified 
     * in this constructor is used.
     * 
     * @param baseClass is the class which any specified plugin <i>must</i> be
     * descended from.
     * @param dfltPluginClass is the class which will be used if the user
     * doesn't specify any plugin-class or plugin-id. This class will have
     * custom rules installed for it just like a declared plugin.
     */
    public PluginCreateRule(Class baseClass, Class dfltPluginClass) {
        super();
        this.baseClass = baseClass;
        if (dfltPluginClass != null) {
            defaultPlugin = new Declaration(dfltPluginClass);
        }
    }

    //------------------- properties ---------------------------------------

    public void setDefaultRuleMethod(String dfltPluginRuleMethod) {
        if (defaultPlugin != null) {
            defaultPlugin.setRuleMethod(dfltPluginRuleMethod);
        }
    }
    
    public void setDefaultRuleClass(Class dfltPluginRuleClass) {
        if (defaultPlugin != null) {
            defaultPlugin.setRuleClass(dfltPluginRuleClass);
        }
    }
    
    public void setDefaultRuleResource(String dfltPluginRuleResource) {
        if (defaultPlugin != null) {
            defaultPlugin.setRuleResource(dfltPluginRuleResource);
        }
    }
    
    public void setDefaultRuleFile(String dfltPluginRuleFile) {
        if (defaultPlugin != null) {
            defaultPlugin.setRuleFile(new File(dfltPluginRuleFile));
        }
    }

    public void setDefaultRuleAutoSetProperties(boolean enabled) {
        if (defaultPlugin != null) {
            defaultPlugin.setAutoSetProperties(enabled);
        }
    }
    
    /**
     * Sets the xml attribute which the input xml uses to indicate to a 
     * PluginCreateRule which class should be instantiated.
     * <p>
     * See setGlobalPluginClassAttribute for more info.
     */
    public void setPluginClassAttribute(String namespaceUri, String attrName) {
        pluginClassAttrNs = namespaceUri;
        pluginClassAttr = attrName;
    }

    /**
     * Sets the xml attribute which the input xml uses to indicate to a 
     * PluginCreateRule which plugin declaration is being referenced.
     * <p>
     * See setGlobalPluginIdAttribute for more info.
     */
    public void setPluginIdAttribute(String namespaceUri, String attrName) {
        pluginIdAttrNs = namespaceUri;
        pluginIdAttr = attrName;
    }

    //------------------- methods --------------------------------------------

    /**
     * Invoked after this rule has been added to the set of digester rules,
     * associated with the specified pattern. Check all configuration data is
     * valid and remember the pattern for later.
     * 
     * @param matchPattern is the digester match pattern that is associated 
     * with this rule instance, eg "root/widget".
     * @exception PluginConfigurationException
     */
    public void postRegisterInit(String matchPattern)
                                 throws PluginConfigurationException {
        Log log = LogUtils.getLogger(digester);
        boolean debug = log.isDebugEnabled();
        if (debug) {
            log.debug("PluginCreateRule.postRegisterInit" + 
                      ": rule registered for pattern [" + pattern + "]");
        }

        if (digester == null) {
            // We require setDigester to be called before this method.
            // Note that this means that PluginCreateRule cannot be added
            // to a Rules object which has not yet been added to a
            // Digester object.
            initException = new PluginConfigurationException(
                 "Invalid invocation of postRegisterInit" + 
                 ": digester not set.");
            throw initException;
        }

        if (pattern != null) {
            // We have been called twice, ie a single instance has been
            // associated with multiple patterns.
            //
            // Generally, Digester Rule instances can be associated with 
            // multiple patterns. However for plugins, this creates some 
            // complications. Some day this may be supported; however for 
            // now we just reject this situation.
            initException = new PluginConfigurationException(
               "A single PluginCreateRule instance has been mapped to" + 
                 " multiple patterns; this is not supported.");
            throw initException;
        }

        if (matchPattern.indexOf('*') != -1) {
            // having wildcards in patterns is extremely difficult to
            // deal with. For now, we refuse to allow this.
            //
            // TODO: check for any chars not valid in xml element name
            // rather than just *.
            //
            // Reasons include:
            // (a) handling recursive plugins, and
            // (b) determining whether one pattern is "below" another,
            //     as done by PluginRules. Without wildcards, "below"
            //     just means startsWith, which is easy to check.
            initException = new PluginConfigurationException(
                 "A PluginCreateRule instance has been mapped to" + 
                 " pattern [" + matchPattern + "]." + 
                 " This pattern includes a wildcard character." + 
                 " This is not supported by the plugin architecture.");
            throw initException;
        }

        if (baseClass == null) {
            baseClass = Object.class;
        }
        
        // check default class is valid
        if (defaultPlugin != null) {
            if (!baseClass.isAssignableFrom(defaultPlugin.getPluginClass())) {
                initException = new PluginConfigurationException(
                     "Default class [" + 
                     defaultPlugin.getPluginClass().getName() + 
                     "] does not inherit from [" + 
                     baseClass.getName() + "].");
                throw initException;
            }

            try {
                defaultPlugin.init(digester);
                
            } catch(PluginWrappedException pwe) {
            
                throw new PluginConfigurationException(
                    pwe.getMessage(), pwe.getCause());
            }
        }

        // remember the pattern for later
        pattern = matchPattern;
    }

    /**
     * Invoked when the Digester matches this rule against an xml element.
     * <p>
     * A new instance of the target class is created, and pushed onto the
     * stack. A new "private" PluginRules object is then created and set as
     * the digester's default Rules object. Any custom rules associated with
     * the plugin class are then loaded into that new Rules object.
     * Finally, any custom rules that are associated with the current pattern
     * (such as SetPropertiesRules) have their begin methods executed.
     * <p>
     * Because a PluginCreateRule is also a Delegate, this method is also
     * called on the start of any element occurring below the pattern
     * associated with this rule. In this case, this method acts like the
     * Digester's startElement method: it fires the begin() method of every
     * custom rule associated with the plugin class that matches that pattern.
     * See {@link #delegateBegin}.
     * 
     * @param namespace 
     * @param name 
     * @param attributes
     *
     * @throws ClassNotFoundException
     * @throws PluginInvalidInputException
     * @throws PluginConfigurationException
     */
    public void begin(String namespace, String name,
                      org.xml.sax.Attributes attributes)
                      throws java.lang.Exception {
        Log log = digester.getLogger();
        boolean debug = log.isDebugEnabled();
        if (debug) {
            log.debug("PluginCreateRule.begin" + ": pattern=[" + pattern + "]" + 
                  " match=[" + digester.getMatch() + "]");
        }

        if (initException != null) {
            // we had a problem during initialisation that we could
            // not report then; report it now.
            throw initException;
        }
        
        String currMatch = digester.getMatch();
        if (currMatch.length() == pattern.length()) {
            // ok here we are actually instantiating a new plugin object,
            // and storing its rules into a new Rules object
            if (localRules != null) {
                throw new PluginAssertionFailure(
                    "Begin called when localRules is not null.");
            }
                      
            PluginRules oldRules = (PluginRules) digester.getRules();
            localRules = new PluginRules(this, oldRules);
            PluginManager pluginManager = localRules.getPluginManager();
            Declaration currDeclaration = null;
            
            if (debug) {
                log.debug("PluginCreateRule.begin: installing new plugin: " 
                    + "oldrules=" + oldRules.toString()
                    + ", localrules=" + localRules.toString());
            }
              
            String pluginClassName; 
            if (pluginClassAttrNs == null) {
                // Yep, this is ugly.
                //
                // In a namespace-aware parser, the one-param version will 
                // return attributes with no namespace.
                //
                // In a non-namespace-aware parser, the two-param version will 
                // never return any attributes, ever.
                pluginClassName = attributes.getValue(pluginClassAttr);
            } else {
                pluginClassName = 
                    attributes.getValue(pluginClassAttrNs, pluginClassAttr);
            }

            String pluginId; 
            if (pluginIdAttrNs == null) {
                pluginId = attributes.getValue(pluginIdAttr);
            }
            else {
                pluginId = 
                    attributes.getValue(pluginIdAttrNs, pluginIdAttr);
            }
            
            if (pluginClassName != null) {
                currDeclaration = pluginManager.getDeclarationByClass(
                    pluginClassName);
    
                if (currDeclaration == null) {
                    currDeclaration = new Declaration(pluginClassName);
                    try {
                        currDeclaration.init(digester);
                    } catch(PluginWrappedException pwe) {
                        throw new PluginInvalidInputException(
                            pwe.getMessage(), pwe.getCause());
                    }
                    pluginManager.addDeclaration(currDeclaration);
                }
            } else if (pluginId != null) {
                currDeclaration = pluginManager.getDeclarationById(pluginId);
                
                if (currDeclaration == null) {
                    throw new PluginInvalidInputException(
                        "Plugin id [" + pluginId + "] is not defined.");
                }
            } else if (defaultPlugin != null) {
                currDeclaration = defaultPlugin;
            }
            else {
                throw new PluginInvalidInputException(
                    "No plugin class specified for element "
                    + pattern);
            }
            
            // now load up the custom rules into a private Rules instance
            digester.setRules(localRules);
        
            currDeclaration.configure(digester, pattern);
    
            Class pluginClass = currDeclaration.getPluginClass();
            
            Object instance = pluginClass.newInstance();
            getDigester().push(instance);
            if (debug) {
                log.debug(
                    "PluginCreateRule.begin" + ": pattern=[" + pattern + "]" + 
                    " match=[" + digester.getMatch() + "]" + 
                    " pushed instance of plugin [" + pluginClass.getName() + "]");
            }
        
            digester.setRules(oldRules);

            ((PluginRules) oldRules).beginPlugin(this);
        }
        
        // fire the begin method of all custom rules
        Rules oldRules = digester.getRules();
        
        if (debug) {
            log.debug("PluginCreateRule.begin: firing nested rules: " 
                + "oldrules=" + oldRules.toString()
                + ", localrules=" + localRules.toString());
        }

        // assert oldRules = localRules.oldRules
        digester.setRules(localRules);
        delegateBegin(namespace, name, attributes);
        digester.setRules(oldRules);

        if (debug) {
            log.debug("PluginCreateRule.begin: restored old rules to " 
                + "oldrules=" + oldRules.toString());
        }
    }

    /**
     * Invoked by the digester when the closing tag matching this Rule's
     * pattern is encountered. See {@link #delegateBody}.
     *
     * @see #begin
     */
    public void body(String namespace, String name, String text)
                     throws Exception {
            
        Rules oldRules = digester.getRules();
        // assert oldRules == localRules.oldRules
        digester.setRules(localRules);
        delegateBody(namespace, name, text);
        digester.setRules(oldRules);
    }
    
    /**
     * Invoked by the digester when the closing tag matching this Rule's
     * pattern is encountered.
     * </p>
     * As noted on method begin, because PluginCreateRule is a Delegate,
     * this method is also called at the end tag of every pattern that
     * is "below" the pattern associated with this rule. In this case, we
     * fire the end method of every custom rule associated with the 
     * current plugin class. See {@link #delegateEnd}.
     * <p>
     * If we are really encountering the end tag associated with this rule
     * (rather than the end of an element "below" that tag), then we
     * remove the object we pushed onto the digester stack when the
     * opening tag was encountered.
     * 
     * @param namespace Description of the Parameter
     * @param name Description of the Parameter
     * @exception Exception Description of the Exception
     *
     * @see #begin
     */
    public void end(String namespace, String name)
                    throws Exception {
            
        Rules oldRules = digester.getRules();
        // assert oldRules == localRules.parentRules
        digester.setRules(localRules);
        delegateEnd(namespace, name);
        digester.setRules(oldRules);

        String currMatch = digester.getMatch();
        if (currMatch.length() == pattern.length()) {
            // the end of the element on which the PluginCreateRule has
            // been mounted has been reached.
            localRules = null;
            ((PluginRules) oldRules).endPlugin(this);
            digester.pop();
        }
    }

    /**
     * Return the pattern that this Rule is associated with.
     * <p>
     * In general, Rule instances <i>can</i> be associated with multiple
     * patterns. A PluginCreateRule, however, will only function correctly
     * when associated with a single pattern. It is possible to fix this, but
     * I can't be bothered just now because this feature is unlikely to be
     * used.
     * </p>
     * 
     * @return The pattern value
     */
    public String getPattern() {
        return pattern;
    }
    
    /**
     * Here we act like Digester.begin, finding a match for the pattern
     * in our private rules object, then executing the begin method of
     * each matching rule.
     */
    public void delegateBegin(String namespace, String name, 
                              org.xml.sax.Attributes attributes)
                              throws java.lang.Exception {
        
        // Fire "begin" events for all relevant rules
        Log log = digester.getLogger();
        boolean debug = log.isDebugEnabled();
        String match = digester.getMatch();
        List rules = digester.getRules().match(namespace, match);
        Iterator ri = rules.iterator();
        while (ri.hasNext()) {
            Rule rule = (Rule) ri.next();
            if (debug) {
                log.debug("  Fire begin() for " + rule);
            }
            rule.begin(namespace, name, attributes);
        }
    }
    
    /**
     * Here we act like Digester.body, except against our private rules.
     */
    public void delegateBody(String namespace, String name, String text)
                             throws Exception {
        // Fire "body" events for all relevant rules
        Log log = digester.getLogger();
        boolean debug = log.isDebugEnabled();
        String match = digester.getMatch();
        List rules = digester.getRules().match(namespace, match);
        Iterator ri = rules.iterator();
        while (ri.hasNext()) {
            Rule rule = (Rule) ri.next();
            if (debug) {
                log.debug("  Fire body() for " + rule);
            }
            rule.body(namespace, name, text);
        }
    }
    
    /**
     * Here we act like Digester.end.
     */
    public void delegateEnd(String namespace, String name)
                            throws Exception {
        // Fire "end" events for all relevant rules in reverse order
        Log log = digester.getLogger();
        boolean debug = log.isDebugEnabled();
        String match = digester.getMatch();
        List rules = digester.getRules().match(namespace, match);
        ListIterator ri = rules.listIterator();
        while (ri.hasNext()) {
            ri.next();
        }
        
        while (ri.hasPrevious()) {
            Rule rule = (Rule) ri.previous();
            if (debug) {
                log.debug("  Fire end() for " + rule);
            }
            rule.end(namespace, name);
        }
    }
}
