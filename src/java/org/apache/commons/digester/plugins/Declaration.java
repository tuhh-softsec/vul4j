/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

/**
 * Simple structure to store the set of attributes that can be present on 
 * a plugin declaration.
 */
public class Declaration {

    /** 
     * The name of the method looked for on the plugin class and any
     * specific rule class.
     */
    public final static String DFLT_RULE_METHOD_NAME = "addRules";
   
    /** The class of the object to be instantiated. */
    private Class pluginClass;

    /** The name of the class of the object to be instantiated. */
    private String pluginClassName;
    
    /** See {@link #setId}. */ 
    private String id;
    
    /** See {@link #setRuleMethod}. */
    private String ruleMethodName = DFLT_RULE_METHOD_NAME;
    
    /** See {@link #setRuleClass}. */ 
    private Class ruleClass;
    
    /** See {@link #setRuleResource}. */
    private String ruleResource;
    
    /** See {@link #setRuleFile}. */
    private File ruleFile;
    
    /** See {@link #setAutoSetProperties}. */
    private boolean autoSetProperties = true;

    /** See {@link #init}. */
    private boolean initialized = false;
    
    //---------------------- constructors ----------------------------------

    /**
     * Constructor.
     */
    public Declaration(Class pluginClass) {
        this.pluginClass = pluginClass;
        this.pluginClassName = pluginClass.getName();
    }
    
    /**
     * Constructor.
     */
    public Declaration(String pluginClassName) {
        this.pluginClassName = pluginClassName;
    }
    
    //---------------------- properties -----------------------------------

    /** 
     * The id of the object defined in a plugin declaration.
     * For plugins declared "in-line", the id is null.
     */
    public void setId(String id) {
        this.id = id;
    }
    
    /** 
     * Sets the name of a method which defines custom rules. May be null. 
     */
    public void setRuleMethod(String ruleMethodName) {
        this.ruleMethodName = ruleMethodName;
    }
    
    /** 
     * The name of a class containing a method which defines custom rules
     * for the plugin class. May be null. 
     */
    public void setRuleClass(Class ruleClass) {
        this.ruleClass = ruleClass;
    }
    
    /**
     * The name of a resource file in the classpath containg xmlrules
     * specifications of custom rules for the plugin class. May be null.
     */
    public void setRuleResource(String ruleResource) {
        this.ruleResource = ruleResource;
    }
    
    /**
     * The name of a file containg xmlrules specifications of custom rules 
     * for the plugin class. May be null.
     */
    public void setRuleFile(File ruleFile) {
        this.ruleFile = ruleFile;
    }
    
    /** See {@link #autoSetProperties}. */
    public void setAutoSetProperties(boolean autoSetProperties) {
        this.autoSetProperties = autoSetProperties;
    }
    
    /**
     * Return the id associated with this declaration.
     * 
     * @return The id value. May be null.
     */
    public String getId() {
        return id;
    }

    /**
     * Return plugin class associated with this declaration.
     * 
     * @return The pluginClass.
     */
    public Class getPluginClass() {
        return pluginClass;
    }

    /**
     * return class which specifies custom rules for this plugin.
     * 
     * @return The ruleClass value. May be null.
     */
    public Class getRuleClass() {
        return ruleClass;
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
        return autoSetProperties;
    }

    //---------------------- methods -----------------------------------
    
    /**
     * Must be called exactly once, and must be called before any call
     * to the configure method.
     */
    public void init(Digester digester) throws PluginWrappedException {
        Log log = digester.getLogger();
        boolean debug = log.isDebugEnabled();
        if (debug) {
            log.debug("init being called!");
        }
        
        if (initialized) {
            throw new PluginAssertionFailure("Init called multiple times.");
        }

        if ((pluginClass == null) && (pluginClassName != null)) {
            try {
                // load the plugin class object
                pluginClass = 
                    digester.getClassLoader().loadClass(pluginClassName);
            } catch(ClassNotFoundException cnfe) {
                throw new PluginWrappedException(
                    "Unable to load class " + pluginClassName, cnfe);
            }
        }
        initialized = true;        
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
        Log log = digester.getLogger();
        boolean debug = log.isDebugEnabled();
        if (debug) {
            log.debug("configure being called!");
        }
        
        if (!initialized) {
            throw new PluginAssertionFailure("Not initialized.");
        }
        
        // load from explicit file
        if (ruleFile != null) {
            InputStream is = null;
            try {
                is = new FileInputStream(ruleFile);
            } catch(IOException ioe) {
                throw new PluginWrappedException(
                    "Unable to process file [" + ruleFile + "]", ioe);
            }
            loadRulesFromStream(is, digester, pattern);
            return;
        }
        
        // load from explicit resource in classpath
        if (ruleResource != null) {
            InputStream is = 
                pluginClass.getClassLoader().getResourceAsStream(
                    ruleResource);
            if (is != null) {
                loadRulesFromStream(is, digester, pattern);
                return;
            }
        }

        // load via method on explicit Rule Class        
        if (ruleClass != null) {
            loadRulesFromClass(ruleClass, digester, pattern);
            return;
        }

        // load via method on plugin class        
        {
            Class[] paramSpec = { Digester.class, String.class };
            Method ruleMethod = MethodUtils.getAccessibleMethod(
                pluginClass, ruleMethodName, paramSpec);
            if (ruleMethod != null) 
            {
                try {
                    Object[] params = {digester, pattern};
                    Object none = ruleMethod.invoke(null, params);
                } catch (Exception e) {
                    throw new PluginWrappedException(
                        "Unable to configure class [" + pluginClass + "]" +
                        " using method [" + ruleMethodName + "]", e);
                }
                return;
            }
        }

        // look for rule class
        {
            if (debug) {
                log.debug("plugin class type:" + pluginClass.getName());
            }
            String ruleClassName = pluginClass.getName() + "RuleInfo";

            Class ruleClass;
            try {
                ruleClass = digester.getClassLoader().loadClass(ruleClassName);
            } catch(ClassNotFoundException cnfe) {
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
                pluginClass.getClass().getName().replace('.', '/') +
                "RuleInfo.xml";
            InputStream is = 
                pluginClass.getClassLoader().getResourceAsStream(
                    resourceName);
            if (is != null) {
                loadRulesFromStream(is, digester, pattern);
                return;
            }
        }
        
        // try autoSetProperties
        if (autoSetProperties) {
            if (debug) {
                log.debug("adding autoset for pattern [" + pattern + "]");
            }
            digester.addSetProperties(pattern);
        }
    }

    /**
     * Load custom rules from a specified stream of xml data.
     */
    private void loadRulesFromStream(InputStream is, Digester digester,
                                     String pattern) 
                                     throws PluginWrappedException {
        try
        {
            throw new PluginAssertionFailure(
                "Load from stream not yet supported.");
        }
        finally {
            try {
                is.close();
            } catch(IOException ioe) {
                Log log = digester.getLogger();
                log.warn("Unable to close stream after reading rules", ioe);
            }
        }
    }
    
    /**
     * Load custom rules from a specified class.
     */
    private void loadRulesFromClass(Class ruleClass, Digester digester,
                                    String pattern)
                                    throws PluginWrappedException {
        Class[] paramSpec = { Digester.class, String.class };
        Method ruleMethod = MethodUtils.getAccessibleMethod(
            ruleClass, ruleMethodName, paramSpec);
        if (ruleMethod == null) {
            throw new PluginWrappedException(
                "rule class specified, but rules method not found on it.");
        }
        try {
            Object[] params = {digester, pattern};
            Object none = ruleMethod.invoke(null, params);
        } catch (Exception e) {
            throw new PluginWrappedException(
                "Unable to configure class [" + pluginClass + "]" +
                " using rule class [" + ruleClass + "]" +
                " method [" + ruleMethodName + "]", e);
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
        if (different(id, d.id)) return false;
        if (pluginClass != d.pluginClass) return false;
        if (different(ruleMethodName, d.ruleMethodName)) return false;
        if (ruleClass != d.ruleClass) return false;
        if (different(ruleResource, d.ruleResource)) return false;
        if (different(ruleFile, d.ruleFile)) return false;
        if (autoSetProperties != d.autoSetProperties) return false;

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
