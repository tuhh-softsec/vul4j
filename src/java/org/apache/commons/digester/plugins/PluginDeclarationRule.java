/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//digester/src/java/org/apache/commons/digester/plugins/PluginDeclarationRule.java,v 1.7 2004/01/10 17:23:47 rdonkin Exp $
 * $Revision: 1.7 $
 * $Date: 2004/01/10 17:23:47 $
 *
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2004 The Apache Software Foundation.  All rights
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

import org.apache.commons.digester.Rule;
import org.apache.commons.digester.Digester;
import org.apache.commons.beanutils.MethodUtils;

import org.apache.commons.logging.Log;

/**
 * A Digester rule which allows the user to pre-declare a class which is to
 * be referenced later at a plugin point by a PluginCreateRule.
 * <p>
 * Normally, a PluginDeclarationRule is added to a Digester instance with
 * the pattern "{root}/plugin" or "* /plugin" where {root} is the name of 
 * the root tag in the input document.
 *
 * @author Simon Kitching
 */

public class PluginDeclarationRule extends Rule {

    //------------------- constructors ---------------------------------------

    /** constructor  */
    public PluginDeclarationRule() {
        super();
    }

    //------------------- methods --------------------------------------------

    /**
     * Invoked upon reading a tag defining a plugin declaration. The tag
     * must have the following mandatory attributes:
     * <ul>
     *   <li> id </li>
     *   <li> class </li>
     * </ul>
     *
     *@param namespace The xml namespace in which the xml element which
     * triggered this rule resides.
     *@param name The name of the xml element which triggered this rule.
     *@param attributes The set of attributes on the xml element which
     * triggered this rule.
     *@exception java.lang.Exception
     */

    public void begin(String namespace, String name,
                      org.xml.sax.Attributes attributes)
                      throws java.lang.Exception {
                 
        Log log = digester.getLogger();
        boolean debug = log.isDebugEnabled();
        
        String id = attributes.getValue("id");
        String pluginClassName = attributes.getValue("class");
        String ruleMethodName = attributes.getValue("method");
        String ruleClassName = attributes.getValue("ruleclass");
        String ruleResource = attributes.getValue("resource");
        String ruleFile = attributes.getValue("file");
        String autoSetPropertiesStr = attributes.getValue("setprops");

        if (debug) {
            log.debug(
                "mapping id [" + id + "] -> [" + pluginClassName + "]");
        }

        if (id == null) {
            throw new PluginInvalidInputException(
                    "mandatory attribute id not present on tag" +
                       " <" + name + ">");
        }

        if (pluginClassName == null) {
            throw new PluginInvalidInputException(
                    "mandatory attribute class not present on tag" +
                       " <" + name + ">");
        }

        Declaration newDecl = new Declaration(pluginClassName);
        newDecl.setId(id);
        
        if (ruleMethodName != null) {
            newDecl.setRuleMethod(ruleMethodName);
        }
        
        if (ruleClassName != null) {
            Class ruleClass;
            try {
                ruleClass = digester.getClassLoader().loadClass(ruleClassName);
            } catch(ClassNotFoundException cnfe) {
                throw new ClassNotFoundException(
                    "Rule class [" + ruleClassName + "] not found.");
            }
            newDecl.setRuleClass(ruleClass);
        }
        
        if (ruleResource != null) {
            newDecl.setRuleResource(ruleResource);
        }
        
        if (ruleFile != null) {
            newDecl.setRuleFile(new File(ruleFile));
        }
        
        if (autoSetPropertiesStr != null) {
            newDecl.setAutoSetProperties(
                Boolean.valueOf(autoSetPropertiesStr).booleanValue());
        }
        
        newDecl.init(digester);

        PluginRules rc = (PluginRules) digester.getRules();
        PluginManager pm = rc.getPluginManager();
        Declaration oldDecl = pm.getDeclarationById(id);
        if (oldDecl != null) {
            if (oldDecl.isEquivalent(newDecl)) {
                // this is a redeclaration of the same plugin mapping.
                // this could happen when using xml Entities to include
                // external files into the main config file, or to include
                // the same external file at multiple locations within a
                // parent document. if the declaration is identical,
                // then we just ignore it.
                if (debug) {
                    log.debug("plugin redeclaration is identical: ignoring");
                }
                return;
            } else {
                throw new PluginInvalidInputException(
                    "Plugin id [" + id + "] is not unique");
            }
        }

        // check whether this class has already been mapped to a different
        // name. It might be nice someday to allow this but lets keep it
        // simple for now.
        if (pm.getDeclarationByClass(pluginClassName) != null) {
            throw new PluginInvalidInputException(
                    "Plugin id [" + id + "] maps to class [" + pluginClassName + "]" +
                     " which has already been mapped by some other id.");
        }

        pm.addDeclaration(newDecl);
    }
}

