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

import java.util.HashMap;

import org.apache.commons.digester.Digester;

import org.apache.commons.logging.Log;

/**
 * Coordinates between PluginDeclarationRule and PluginCreateRule objects,
 * providing a place to share data between instances of these rules.
 * <p>
 * One instance of this class exists per PluginRules instance,
 * ie one per Digester instance.
 *
 * @author Simon Kitching
 */

public class PluginManager {

    /** Map of classname->Declaration */
    private HashMap declarationsByClass = new HashMap();

    /** Map of id->Declaration  */
    private HashMap declarationsById = new HashMap();

    /** the parent manager to which this one may delegate lookups. */
    private PluginManager parent;

    //------------------- constructors ---------------------------------------
    
    /** Constructor. */
    public PluginManager() {
    }

    /** Constructor. */
    public PluginManager(PluginManager parent) {
        this.parent = parent;
    }
    
    //------------------- methods --------------------------------------------

    /**
     * Add the declaration to the set of known declarations.
     * <p>
     * TODO: somehow get a reference to a Digester object
     * so that we can really log here. Currently, all
     * logging is disabled from this method.
     *
     *@param decl an object representing a plugin class.
     */
    public void addDeclaration(Declaration decl) {
        Log log = LogUtils.getLogger(null);
        boolean debug = log.isDebugEnabled();
        
        Class pluginClass = decl.getPluginClass();
        String id = decl.getId();
        
        declarationsByClass.put(pluginClass.getName(), decl);
            
        if (id != null) {
            declarationsById.put(id, decl);
            if (debug) {
                log.debug(
                    "Indexing plugin-id [" + id + "]" +
                    " -> class [" + pluginClass.getName() + "]");
            }
        }
    }

    /**
     * Return the declaration object with the specified class.
     * If no such plugin is known, null is returned.
     */
    public Declaration getDeclarationByClass(String className) {
        Declaration decl = 
            (Declaration) declarationsByClass.get(className);
            
        if ((decl == null) && (parent != null)) {
            decl = parent.getDeclarationByClass(className);
        }

        return decl;
    }

    /**
     * Return the declaration object with the specified id.
     * If no such plugin is known, null is returned.
     *
     *@param id Description of the Parameter
     *@return The declaration value
     */
    public Declaration getDeclarationById(String id) {
        Declaration decl = (Declaration) declarationsById.get(id);

        if ((decl == null) && (parent != null)) {
            decl = parent.getDeclarationById(id);
        }

        return decl;
    }
}
