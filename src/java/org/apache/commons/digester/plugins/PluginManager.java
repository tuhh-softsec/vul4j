/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//digester/src/java/org/apache/commons/digester/plugins/PluginManager.java,v 1.7 2004/01/10 17:42:09 rdonkin Exp $
 * $Revision: 1.7 $
 * $Date: 2004/01/10 17:42:09 $
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
 *    nor may "Apache" appear in their names without prior 
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
