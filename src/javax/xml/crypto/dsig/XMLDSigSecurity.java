/*
 * Copyright 2005 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
/*
 * =========================================================================== 
 *
 * (C) Copyright IBM Corp. 2003 All Rights Reserved.
 *
 * ===========================================================================
 */
/*
 * Portions copyright 2005 Sun Microsystems, Inc. All rights reserved.
 */
/*
 * $Id$
 */
package javax.xml.crypto.dsig;

import java.lang.reflect.*;
import java.security.Provider;
import java.security.Security;
import java.util.*;
import java.security.NoSuchAlgorithmException;
import javax.xml.crypto.NoSuchMechanismException;

/**
 * @author Joyce Leung
 */

final class XMLDSigSecurity {

    // An element in the cache
    private static class ProviderProperty {
	String className;
	Provider provider;
    }

    /**
     * Don't let anyone instantiate this. 
     */
    private XMLDSigSecurity() { }                    

    /*
     * Lookup the mechanism type in our list of providers. Process
     * each provider in priority order one at a time looking for
     * either the direct engine property or a matching alias.
     */
    private static ProviderProperty getEngineClassName(String alg, 
	Map.Entry attr, String engineType, String key, boolean mech) 
	throws NoSuchAlgorithmException
    {
	// get all currently installed providers
	Provider[] provs = Security.getProviders();

        // get the implementation class from the first provider
        // that supplies an implementation that we can load
        boolean found = false;
        ProviderProperty entry = null;
        for (int i = 0; (i < provs.length) && (!found); i++) {
            try {
                entry = getEngineClassName
		    (alg, attr, engineType, key, provs[i], mech);
                found = true;
            } catch (Exception e) {
	        // do nothing, check the next provider
            }
        }
	if (!found) {
	    if (mech) {
                throw new NoSuchMechanismException("Mechanism type " + alg
					   + " not available");
	    } else {
                throw new NoSuchAlgorithmException("Algorithm type " + alg
					   + " not available");
	    }
        }
        return entry;
    }

    /**
     * The parameter provider cannot be null.
     */
    private static ProviderProperty getEngineClassName(String alg,
	Map.Entry attr, String engineType, String key, Provider provider, 
	boolean mech) throws NoSuchAlgorithmException
    {
	String className = getProviderProperty(key, attr, provider);
	if (className == null) {
            // try alg as alias name
	    String stdName = getStandardName(alg, engineType, provider);
	    if (stdName != null) {
                key = engineType + "." + stdName;
	    }
	    if ((stdName == null)
	        || (className = getProviderProperty(key, attr, provider)) == null) {
		if (mech) {
	            throw new NoSuchMechanismException
			("no such mechanism type: " + alg + " for provider " +
			 provider.getName());
		} else {
	            throw new NoSuchAlgorithmException
			("no such algorithm: " + alg + " for provider " +
		         provider.getName());
		}
	    }
        }
        
        ProviderProperty entry = new ProviderProperty();
	entry.className = className;
	entry.provider = provider;
	return entry;
    } 
                                          
    private static boolean checkSuperclass(Class subclass, Class superclass) {
	return superclass.isAssignableFrom(subclass);
    }

    /*
     * Returns an array of objects: the first object in the array is
     * an instance of an implementation of the requested mechanism type
     * and type, and the second object in the array identifies the provider
     * of that implementation.
     * The <code>provider</code> argument can be null, in which case all
     * configured providers will be searched in order of preference.
     */
    static Object[] getImpl(String mechType, String type, Provider provider)
	throws NoSuchAlgorithmException
    {
	return getImpl(mechType, null, type, provider);
    }

    static Object[] getImpl(String alg, Map.Entry attr, String type, 
	Provider provider) throws NoSuchAlgorithmException {
        Class typeClass = null;
	boolean m = true;
	if (type.equals("XMLSignatureFactory")) {
	    typeClass = javax.xml.crypto.dsig.XMLSignatureFactory.class;
	} else if (type.equals("KeyInfoFactory")) {
	    typeClass = javax.xml.crypto.dsig.keyinfo.KeyInfoFactory.class;
	} else if (type.equals("TransformService")) {
	    typeClass = javax.xml.crypto.dsig.TransformService.class;
	    m = false;
	}
	String key = type + "." + alg;
        if (provider == null) {
            return doGetImpl
		(type, typeClass, 
		 getEngineClassName(alg, attr, type, key, m), m);
        } else {
            return doGetImpl
		(type, typeClass, getEngineClassName(alg, attr, type, key,
		 provider, m), m);
        }
    }
   
    private static Object[] doGetImpl(String type, Class typeClass, 
	ProviderProperty pp, boolean mech) throws NoSuchAlgorithmException
    {
        String className = pp.className;
        String providerName = pp.provider.getName();

    	try {
            // Load the implementation class using the class loader of 
	    // typeClass. This insures checkSuperClass won't fail due to
	    // typeClass and the implementation class being loaded from
	    // two different class loaders.
	    ClassLoader cl = typeClass.getClassLoader();
	    Class implClass;
	    if (cl != null) {
		implClass = cl.loadClass(className);
	    } else {
		implClass = Class.forName(className);
	    }

	    if (checkSuperclass(implClass, typeClass)) {
		return new Object[] { implClass.newInstance(), pp.provider };
	    } else {
		if (mech) {
		    throw new NoSuchMechanismException
			("class configured for " + type + ": " + className + 
		         " not a " + type);
		} else {
		    throw new NoSuchAlgorithmException
			("class configured for " + type + ": " + className + 
		         " not a " + type);
		}
	    }
	} catch (ClassNotFoundException e) {
	    if (mech) {
	        throw new NoSuchMechanismException
		    ("class configured for " + type + "(provider: " + 
	             providerName + ")" + "cannot be found.\n", e);
	    } else {
	        throw (NoSuchAlgorithmException) new NoSuchAlgorithmException
		    ("class configured for " + type + "(provider: " + 
	             providerName + ")" + "cannot be found.\n").initCause(e);
	    }
	} catch (InstantiationException e) {
	    if (mech) {
	        throw new NoSuchMechanismException
		    ("class " + className + " configured for " + type +
                     "(provider: " + providerName + ") cannot be " +
                     "instantiated. ", e);
	    } else {
	        throw (NoSuchAlgorithmException) new NoSuchAlgorithmException
		    ("class " + className + " configured for " + type +
                     "(provider: " + providerName + ") cannot be " +
                     "instantiated. ").initCause(e);
	    }
	} catch (IllegalAccessException e) {
	    if (mech) {
	        throw new NoSuchMechanismException
		    ("class " + className + " configured for " + type +
                     "(provider: " + providerName +
                     ") cannot be accessed.\n", e);
	    } else {
	        throw (NoSuchAlgorithmException) new NoSuchAlgorithmException
		    ("class " + className + " configured for " + type +
                     "(provider: " + providerName +
                     ") cannot be accessed.\n").initCause(e);
	    }
	}
    }  
    
    /*
     * Retrieves the property with the given key from the given provider.
     */
    private static String getProviderProperty(String key, Map.Entry attr, 
	Provider prov) {
	String prop = prov.getProperty(key);
	if (prop == null) {
	    // Is there a match if we do a case-insensitive property name
	    // comparison? Let's try ...
	    for (Enumeration e = prov.keys(); e.hasMoreElements(); ) {
	        String matchKey = (String)e.nextElement();
	        if (key.equalsIgnoreCase(matchKey)) {
		    prop = prov.getProperty(matchKey);
		    break;
	        }
	    }
	}

	if (prop != null && attr != null) {
	    if (!prov.entrySet().contains(attr)) {
		// if no attribute, assume DOM default if requested
		if (!attr.getValue().equals("DOM") || 
		    prov.get(attr.getKey()) != null) {
		    prop = null;
		}
	    }
	}

	return prop;
    }

    /*
     * Converts an alias name to the standard name.
     */
    private static String getStandardName(String alias, String engineType,
					  Provider prov) {
	return getProviderProperty("Alg.Alias." + engineType + "." + alias,
				   null, prov);
    }
}
