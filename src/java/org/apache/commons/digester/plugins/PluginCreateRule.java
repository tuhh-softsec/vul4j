/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//digester/src/java/org/apache/commons/digester/plugins/PluginCreateRule.java,v 1.6 2003/11/02 23:26:59 rdonkin Exp $
 * $Revision: 1.6 $
 * $Date: 2003/11/02 23:26:59 $
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

    private static final String PLUGIN_CLASS_ATTR = "plugin-class";
    private static final String PLUGIN_ID_ATTR = "plugin-id";
    
    /**
     * In order to invoke the addRules method on the plugin class correctly,
     * we need to know the pattern which this rule is matched by.
     */
    private String pattern_;

    /** A base class that any plugin must derive from. */
    private Class baseClass_ = null;

    /**
     * Info about optional default plugin to be used if no plugin-id is
     * specified in the input data. This can simplify the syntax where one
     * particular plugin is usually used.
     */
    private Declaration defaultPlugin_;

    /**
     * Currently, none of the Rules methods allow exceptions to be thrown.
     * Therefore if this class cannot initialise itself properly, it cannot
     * cause the digester to stop. Instead, we cache the exception and throw
     * it the first time the begin() method is called.
     */
    private PluginConfigurationException initException_;

    /**
     * Our private set of rules associated with the concrete class that
     * the user requested to be instantiated. This object is only valid
     * between a call to begin() and the corresponding call to end().
     */
    private PluginRules localRules_; 
    
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
        baseClass_ = baseClass;
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
        baseClass_ = baseClass;
        if (dfltPluginClass != null) {
            defaultPlugin_ = new Declaration(dfltPluginClass);
        }
    }

    //------------------- properties ---------------------------------------

    public void setDefaultRuleMethod(String dfltPluginRuleMethod) {
        if (defaultPlugin_ != null) {
            defaultPlugin_.setRuleMethod(dfltPluginRuleMethod);
        }
    }
    
    public void setDefaultRuleClass(Class dfltPluginRuleClass) {
        if (defaultPlugin_ != null) {
            defaultPlugin_.setRuleClass(dfltPluginRuleClass);
        }
    }
    
    public void setDefaultRuleResource(String dfltPluginRuleResource) {
        if (defaultPlugin_ != null) {
            defaultPlugin_.setRuleResource(dfltPluginRuleResource);
        }
    }
    
    public void setDefaultRuleFile(String dfltPluginRuleFile) {
        if (defaultPlugin_ != null) {
            defaultPlugin_.setRuleFile(new File(dfltPluginRuleFile));
        }
    }

    public void setDefaultRuleAutoSetProperties(boolean enabled) {
        if (defaultPlugin_ != null) {
            defaultPlugin_.setAutoSetProperties(enabled);
        }
    }
    
    //------------------- methods --------------------------------------------

    /**
     * Invoked after this rule has been added to the set of digester rules,
     * associated with the specified pattern. Check all configuration data is
     * valid and remember the pattern for later.
     * 
     * @param pattern is the digester match pattern that is associated with
     * this rule instance, eg "root/widget".
     * @exception PluginConfigurationException
     */
    public void postRegisterInit(String pattern)
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
            initException_ = new PluginConfigurationException(
                 "Invalid invocation of postRegisterInit" + 
                 ": digester not set.");
            throw initException_;
        }

        if (pattern_ != null) {
            // We have been called twice, ie a single instance has been
            // associated with multiple patterns.
            //
            // Generally, Digester Rule instances can be associated with 
            // multiple patterns. However for plugins, this creates some 
            // complications. Some day this may be supported; however for 
            // now we just reject this situation.
            initException_ = new PluginConfigurationException(
               "A single PluginCreateRule instance has been mapped to" + 
                 " multiple patterns; this is not supported.");
            throw initException_;
        }

        if (pattern.indexOf('*') != -1) {
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
            initException_ = new PluginConfigurationException(
                 "A PluginCreateRule instance has been mapped to" + 
                 " pattern [" + pattern + "]." + 
                 " This pattern includes a wildcard character." + 
                 " This is not supported by the plugin architecture.");
            throw initException_;
        }

        if (baseClass_ == null) {
            baseClass_ = Object.class;
        }
        
        // check default class is valid
        if (defaultPlugin_ != null) {
            if (!baseClass_.isAssignableFrom(defaultPlugin_.getPluginClass())) {
                initException_ = new PluginConfigurationException(
                     "Default class [" + 
                     defaultPlugin_.getPluginClass().getName() + 
                     "] does not inherit from [" + 
                     baseClass_.getName() + "].");
                throw initException_;
            }

            try {
                defaultPlugin_.init(digester);
            }
            catch(PluginWrappedException pwe) {
                throw new PluginConfigurationException(
                    pwe.getMessage(), pwe.getCause());
            }
        }

        // remember the pattern for later
        pattern_ = pattern;
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
    public void begin(
    String namespace, String name, 
    org.xml.sax.Attributes attributes)
    throws java.lang.Exception {
        Log log = digester.getLogger();
        boolean debug = log.isDebugEnabled();
        if (debug) {
            log.debug("PluginCreateRule.begin" + ": pattern=[" + pattern_ + "]" + 
                  " match=[" + digester.getMatch() + "]");
        }

        if (initException_ != null) {
            // we had a problem during initialisation that we could
            // not report then; report it now.
            throw initException_;
        }
        
        String currMatch = digester.getMatch();
        if (currMatch.length() == pattern_.length()) {
            // ok here we are actually instantiating a new plugin object,
            // and storing its rules into a new Rules object
            if (localRules_ != null) {
                throw new PluginAssertionFailure(
                    "Begin called when localRules_ is not null.");
            }
                      
            PluginRules oldRules = (PluginRules) digester.getRules();
            localRules_ = new PluginRules(this, oldRules);
            PluginManager pluginManager = localRules_.getPluginManager();
            Declaration currDeclaration = null;
            
            if (debug) {
                log.debug("PluginCreateRule.begin: installing new plugin: " 
                    + "oldrules=" + oldRules.toString()
                    + ", localrules=" + localRules_.toString());
            }
              
            String pluginClassName = attributes.getValue(PLUGIN_CLASS_ATTR);
            String pluginId = attributes.getValue(PLUGIN_ID_ATTR);
            
            if (pluginClassName != null) {
                currDeclaration = pluginManager.getDeclarationByClass(
                    pluginClassName);
    
                if (currDeclaration == null) {
                    currDeclaration = new Declaration(pluginClassName);
                    try {
                        currDeclaration.init(digester);
                    }
                    catch(PluginWrappedException pwe) {
                        throw new PluginInvalidInputException(
                            pwe.getMessage(), pwe.getCause());
                    }
                    pluginManager.addDeclaration(currDeclaration);
                }
            }
            else if (pluginId != null) {
                currDeclaration = pluginManager.getDeclarationById(pluginId);
                
                if (currDeclaration == null) {
                    throw new PluginInvalidInputException(
                        "Plugin id [" + pluginId + "] is not defined.");
                }
            }
            else if (defaultPlugin_ != null) {
                currDeclaration = defaultPlugin_;
            }
            else {
                throw new PluginInvalidInputException(
                    "No plugin class specified for element "
                    + pattern_);
            }
            
            // now load up the custom rules into a private Rules instance
            digester.setRules(localRules_);
            {
                currDeclaration.configure(digester, pattern_);
        
                Class pluginClass = currDeclaration.getPluginClass();
                
                Object instance = pluginClass.newInstance();
                getDigester().push(instance);
                if (debug) {
                    log.debug(
                        "PluginCreateRule.begin" + ": pattern=[" + pattern_ + "]" + 
                        " match=[" + digester.getMatch() + "]" + 
                        " pushed instance of plugin [" + pluginClass.getName() + "]");
                }
            }
            digester.setRules(oldRules);

            ((PluginRules) oldRules).beginPlugin(this);
        }
        
        // fire the begin method of all custom rules
        Rules oldRules = digester.getRules();
        
        if (debug) {
            log.debug("PluginCreateRule.begin: firing nested rules: " 
                + "oldrules=" + oldRules.toString()
                + ", localrules=" + localRules_.toString());
        }

        // assert oldRules = localRules_.oldRules
        digester.setRules(localRules_);
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
        // assert oldRules == localRules_.oldRules
        digester.setRules(localRules_);
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
        // assert oldRules == localRules_.parentRules
        digester.setRules(localRules_);
        delegateEnd(namespace, name);
        digester.setRules(oldRules);

        String currMatch = digester.getMatch();
        if (currMatch.length() == pattern_.length()) {
            // the end of the element on which the PluginCreateRule has
            // been mounted has been reached.
            localRules_ = null;
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
        return pattern_;
    }
    
    /**
     * Here we act like Digester.begin, finding a match for the pattern
     * in our private rules object, then executing the begin method of
     * each matching rule.
     */
    public void delegateBegin(
    String namespace, String name, 
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
