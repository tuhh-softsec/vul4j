/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//digester/src/java/org/apache/commons/digester/plugins/Declaration.java,v 1.4 2003/10/27 13:37:35 rdonkin Exp $
 * $Revision: 1.4 $
 * $Date: 2003/10/27 13:37:35 $
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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.lang.reflect.Method;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Simple structure to store the set of attributes that can be present on 
 * a plugin declaration.
 * 
 * @author Simon Kitching
 */
public class Declaration {
    private static Log log = LogFactory.getLog(Declaration.class);

    /** 
     * The name of the method looked for on the plugin class and any
     * specific rule class.
     */
    public final static String DFLT_RULE_METHOD_NAME = "addRules";
   
    /** The class of the object to be instantiated. */
    private Class pluginClass_;

    /** The name of the class of the object to be instantiated. */
    private String pluginClassName_;
    
    /** See {@link #setId}. */ 
    private String id_;
    
    /** See {@link #setRuleMethod}. */
    private String ruleMethodName_ = DFLT_RULE_METHOD_NAME;
    
    /** See {@link #setRuleClass}. */ 
    private Class ruleClass_;
    
    /** See {@link #setRuleResource}. */
    private String ruleResource_;
    
    /** See {@link #setRuleFile}. */
    private File ruleFile_;
    
    /** See {@link #setAutoSetProperties}. */
    private boolean autoSetProperties_ = true;

    /** See {@link #init}. */
    private boolean initialised_ = false;
    
    //---------------------- constructors ----------------------------------

    /**
     * Constructor.
     */
    public Declaration(Class pluginClass) {
        pluginClass_ = pluginClass;
        pluginClassName_ = pluginClass_.getName();
    }
    
    /**
     * Constructor.
     */
    public Declaration(String pluginClassName) {
        pluginClassName_ = pluginClassName;
    }
    
    //---------------------- properties -----------------------------------

    /** 
     * The id of the object defined in a plugin declaration.
     * For plugins declared "in-line", the id is null.
     */
    public void setId(String id) {
        id_ = id;
    }
    
    /** 
     * Sets the name of a method which defines custom rules. May be null. 
     */
    public void setRuleMethod(String ruleMethodName) {
        ruleMethodName_ = ruleMethodName;
    }
    
    /** 
     * The name of a class containing a method which defines custom rules
     * for the plugin class. May be null. 
     */
    public void setRuleClass(Class ruleClass) {
        ruleClass_ = ruleClass;
    }
    
    /**
     * The name of a resource file in the classpath containg xmlrules
     * specifications of custom rules for the plugin class. May be null.
     */
    public void setRuleResource(String ruleResource) {
        ruleResource_ = ruleResource;
    }
    
    /**
     * The name of a file containg xmlrules specifications of custom rules 
     * for the plugin class. May be null.
     */
    public void setRuleFile(File ruleFile) {
        ruleFile_ = ruleFile;
    }
    
    /** See {@link #autoSetProperties}. */
    public void setAutoSetProperties(boolean autoSetProperties) {
        autoSetProperties_ = autoSetProperties;
    }
    
    /**
     * Return the id associated with this declaration.
     * 
     * @return The id value. May be null.
     */
    public String getId() {
        return id_;
    }

    /**
     * Return plugin class associated with this declaration.
     * 
     * @return The pluginClass.
     */
    public Class getPluginClass() {
        return pluginClass_;
    }

    /**
     * return class which specifies custom rules for this plugin.
     * 
     * @return The ruleClass value. May be null.
     */
    public Class getRuleClass() {
        return ruleClass_;
    }

    /**
     * Indicates whether plugins which do <i>not</i> implement custom rules
     * should have a SetProperties rule automatically associated with the
     * parent tag. In almost all cases this is desirable, so autoSetProperties
     * defaults to true. If for some reason you are plugging in a class 
     * without custom rules and you do not want xml attributes to be mapped
     * to bean properties, you can pass <i>false</i> here to disable this.
     * 
     * @return true if SetPropertiesRule is automatically applied.
     */
    public boolean autoSetProperties() {
        return autoSetProperties_;
    }

    //---------------------- methods -----------------------------------
    
    /**
     * Must be called exactly once, and must be called before any call
     * to the configure method.
     */
    public void init(Digester digester)
    throws PluginWrappedException {
        log.debug("init being called!");
        
        if (initialised_) {
            throw new PluginAssertionError("Init called multiple times.");
        }

        if ((pluginClass_ == null) && (pluginClassName_ != null)) {
            try {
                // load the plugin class object
                pluginClass_ = 
                    digester.getClassLoader().loadClass(pluginClassName_);
            }
            catch(ClassNotFoundException cnfe) {
                throw new PluginWrappedException(
                    "Unable to load class " + pluginClassName_, cnfe);
            }
        }
        initialised_ = true;        
    }
    
    /**
     * Attempt to load custom rules for the target class at the specified
     * pattern.
     * <p>
     * <ol>
     * <li>If there is an explicit File, load from that file.</li>
     * <li>If there is an explicit Resource, load from that resource.</li>
     * <li>If there is an explicit RuleClass, load from that class.</li>
     * <li>If there is an explicit RuleMethod, load from that method.</li>
     * <li>If there is a default method, load from that method.</li>
     * <li>If there is a default RuleInfo class, load from that class.</li>
     * <li>If there is a default resource, load from that resource.</li>
     * </ol>
     * <p>
     * When loading from a File or Resource (a file in the classpath), the
     * contents of the file are expected to be xml in xmlrules format.
     * <p>
     * When loading from a RuleClass, that class is expected to have a
     * method with the signature <code>public static void addRules(Digester, 
     * String)</code>.
     * <p>
     * When loading from a specified Method on the plugin class, that method
     * is expected to have signature <code> public static void xxx(Digester, 
     * String)</code> where xxx is the specified method name.
     * <p>
     * When loading from the default method on the plugin class, the method
     * is expected to have signature <code>public static void addRules(Digester,
     * String)</code>.
     * <p>
     * When looking for a default RuleInfo class, the plugin class name has
     * the suffix "RuleInfo" applied to it. If there exists a class of that
     * name, then that class is expected to have an addRules method on it.
     * <p>
     * When looking for a default resource file, the plugin class name has
     * the suffix "RuleInfo.xml" applied to it. If there exists a resource
     * file of that name, then that file is expected to contain xmlrules
     * format rules.
     * <p>
     * The first source of rules found is used, and searching stops.
     * <p>
     * On return, any custom rules associated with the plugin class have
     * been loaded into the Rules object currently associated with the
     * specified digester object.
     */
     
    public void configure(Digester digester, String pattern)
    throws PluginWrappedException {
        log.debug("configure being called!");
        
        if (!initialised_) {
            throw new PluginAssertionError("Not initialised.");
        }
        
        // load from explicit file
        if (ruleFile_ != null) {
            InputStream is = null;
            try {
                is = new FileInputStream(ruleFile_);
            }
            catch(IOException ioe) {
                throw new PluginWrappedException(
                    "Unable to process file [" + ruleFile_ + "]", ioe);
            }
            loadRulesFromStream(is, digester, pattern);
            return;
        }
        
        // load from explicit resource in classpath
        if (ruleResource_ != null) {
            InputStream is = 
                pluginClass_.getClassLoader().getResourceAsStream(
                    ruleResource_);
            if (is != null) {
                loadRulesFromStream(is, digester, pattern);
                return;
            }
        }

        // load via method on explicit Rule Class        
        if (ruleClass_ != null) {
            loadRulesFromClass(ruleClass_, digester, pattern);
            return;
        }

        // load via method on plugin class        
        {
            Class[] paramSpec = { Digester.class, String.class };
            Method ruleMethod = MethodUtils.getAccessibleMethod(
                pluginClass_, ruleMethodName_, paramSpec);
            if (ruleMethod != null) 
            {
                try {
                    Object[] params = {digester, pattern};
                    Object none = ruleMethod.invoke(null, params);
                } catch (Exception e) {
                    throw new PluginWrappedException(
                        "Unable to configure class [" + pluginClass_ + "]"
                        + " using method [" + ruleMethodName_ + "]", e);
                }
                return;
            }
        }

        // look for rule class
        {
            if (log.isDebugEnabled()) {
                log.debug("plugin class type:" + pluginClass_.getName());
            }
            String ruleClassName = pluginClass_.getName() + "RuleInfo";

            Class ruleClass;
            try {
                ruleClass = digester.getClassLoader().loadClass(ruleClassName);
            }
            catch(ClassNotFoundException cnfe) {
                ruleClass = null;
            }

            if (ruleClass != null) {
                loadRulesFromClass(ruleClass, digester, pattern);
                return;
            }
        }
        
        // look for  resource
        {
            String resourceName = 
                pluginClass_.getClass().getName().replace('.', '/')
                + "RuleInfo.xml";
            InputStream is = 
                pluginClass_.getClassLoader().getResourceAsStream(
                    resourceName);
            if (is != null) {
                loadRulesFromStream(is, digester, pattern);
                return;
            }
        }
        
        // try autoSetProperties
        if (autoSetProperties_) {
            if (log.isDebugEnabled()) {
                log.debug("adding autoset for pattern [" + pattern + "]");
            }
            digester.addSetProperties(pattern);
        }
    }

    /**
     * Load custom rules from a specified stream of xml data.
     */
    private void loadRulesFromStream(
    InputStream is, 
    Digester digester,
    String pattern) 
    throws PluginWrappedException {
        try
        {
            throw new PluginAssertionError(
                "Load from stream not yet supported.");
        }
        finally {
            try {
                is.close();
            }
            catch(IOException ioe) {
                log.warn("Unable to close stream after reading rules", ioe);
            }
        }
    }
    
    /**
     * Load custom rules from a specified class.
     */
    private void loadRulesFromClass(
    Class ruleClass, 
    Digester digester,
    String pattern)
    throws PluginWrappedException {
        Class[] paramSpec = { Digester.class, String.class };
        Method ruleMethod = MethodUtils.getAccessibleMethod(
            ruleClass, ruleMethodName_, paramSpec);
        if (ruleMethod == null) {
            throw new PluginWrappedException(
                "rule class specified, but rules method not found on it.");
        }
        try {
            Object[] params = {digester, pattern};
            Object none = ruleMethod.invoke(null, params);
        } catch (Exception e) {
            throw new PluginWrappedException(
                "Unable to configure class [" + pluginClass_ + "]"
                + " using rule class [" + ruleClass_ + "]"
                + " method [" + ruleMethodName_ + "]", e);
        } 
    }
    
    /**
     * Returns true if the declarations are equivalent. Perhaps this would be
     * better as overriding equals, but then I should really override hash as
     * well and I can't be bothered.
     * 
     * @param d the Declaration object to be compared to this object.
     * @return true if the specified object has the same options as this one.
     */
    public boolean isEquivalent(Declaration d) {
        if (different(id_, d.id_)) return false;
        if (pluginClass_ != d.pluginClass_) return false;
        if (different(ruleMethodName_, d.ruleMethodName_)) return false;
        if (ruleClass_ != d.ruleClass_) return false;
        if (different(ruleResource_, d.ruleResource_)) return false;
        if (different(ruleFile_, d.ruleFile_)) return false;
        if (autoSetProperties_ != d.autoSetProperties_) return false;

        // all significant fields match; these declarations are identical.
        return true;
    }
    
    /**
     * Returns true if the two objects are both null or both equal.
     */
    private static boolean different(Object o1, Object o2) {
        if (o1 == null) {
            return o1 == null;
        }
        
        return o1.equals(o2);
    }
}
