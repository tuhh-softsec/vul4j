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

import java.util.Properties;

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
 * @since 1.6
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

        int nAttrs = attributes.getLength();
        Properties props = new Properties();
        for(int i=0; i<nAttrs; ++i) {
            String key = attributes.getLocalName(i);
            if ((key == null) || (key.length() == 0)) {
                key = attributes.getQName(i);
            }
            String value = attributes.getValue(i);
            props.setProperty(key, value);
        }
        
        Declaration newDecl = new Declaration(pluginClassName);
        newDecl.setId(id);
        newDecl.setProperties(props);

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

        newDecl.init(digester, pm);
        pm.addDeclaration(newDecl);
    }
}

