/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
/*
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 */
/*
 * $Id$
 */
package javax.xml.crypto.dsig;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.util.*;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;

/**
 * A Service Provider Interface for transform and canonicalization algorithms.
 *
 * <p>Each instance of <code>TransformService</code> supports a specific 
 * transform or canonicalization algorithm and XML mechanism type. To create a 
 * <code>TransformService</code>, call one of the static 
 * {@link #getInstance getInstance} methods, passing in the algorithm URI and 
 * XML mechanism type desired, for example:
 *
 * <blockquote><code>
 * TransformService ts = TransformService.getInstance(Transform.XPATH2, "DOM");
 * </code></blockquote>
 *
 * <p><code>TransformService</code> implementations are registered and loaded 
 * using the {@link java.security.Provider} mechanism.  Each 
 * <code>TransformService</code> service provider implementation should include 
 * a <code>MechanismType</code> service attribute that identifies the XML 
 * mechanism type that it supports. If the attribute is not specified,
 * "DOM" is assumed. For example, a service provider that supports the
 * XPath Filter 2 Transform and DOM mechanism would be specified in the 
 * <code>Provider</code> subclass as:
 * <pre>
 *     put("TransformService." + Transform.XPATH2,
 *         "org.example.XPath2TransformService");
 *     put("TransformService." + Transform.XPATH2 + " MechanismType", "DOM");
 * </pre>
 * <code>TransformService</code> implementations that support the DOM
 * mechanism type must abide by the DOM interoperability requirements defined 
 * in the 
 * <a href="../../../../overview-summary.html#DOM Mechanism Requirements">DOM
 * Mechanism Requirements</a> section of the API overview. See the
 * <a href="../../../../overview-summary.html#Service Provider">Service
 * Providers</a> section of the API overview for a list of standard mechanism
 * types.
 * <p>
 * Once a <code>TransformService</code> has been created, it can be used
 * to process <code>Transform</code> or <code>CanonicalizationMethod</code>
 * objects. If the <code>Transform</code> or <code>CanonicalizationMethod</code>
 * exists in XML form (for example, when validating an existing 
 * <code>XMLSignature</code>), the {@link #init(XMLStructure, XMLCryptoContext)}
 * method must be first called to initialize the transform and provide document
 * context (even if there are no parameters). Alternatively, if the 
 * <code>Transform</code> or <code>CanonicalizationMethod</code> is being
 * created from scratch, the {@link #init(TransformParameterSpec)} method
 * is called to initialize the transform with parameters and the 
 * {@link #marshalParams marshalParams} method is called to marshal the 
 * parameters to XML and provide the transform with document context. Finally, 
 * the {@link #transform transform} method is called to perform the 
 * transformation.
 * <p>
 * <b>Concurrent Access</b>
 * <p>The static methods of this class are guaranteed to be thread-safe.
 * Multiple threads may concurrently invoke the static methods defined in this
 * class with no ill effects.
 *
 * <p>However, this is not true for the non-static methods defined by this
 * class. Unless otherwise documented by a specific provider, threads that
 * need to access a single <code>TransformService</code> instance
 * concurrently should synchronize amongst themselves and provide the
 * necessary locking. Multiple threads each manipulating a different
 * <code>TransformService</code> instance need not synchronize.
 *
 * @author Sean Mullan
 * @author JSR 105 Expert Group
 */
public abstract class TransformService implements Transform {

    private String algorithm;
    private String mechanism;
    private Provider provider;

    /**
     * Default constructor, for invocation by subclasses.
     */
    protected TransformService() {}

    /**
     * Returns a <code>TransformService</code> that supports the specified 
     * algorithm URI (ex: {@link Transform#XPATH2}) and mechanism type 
     * (ex: DOM).
     *
     * <p>This method uses the standard JCA provider lookup mechanism to
     * locate and instantiate a <code>TransformService</code> implementation
     * of the desired algorithm and <code>MechanismType</code> service 
     * attribute. It traverses the list of registered security 
     * <code>Provider</code>s, starting with the most preferred 
     * <code>Provider</code>. A new <code>TransformService</code> object
     * from the first <code>Provider</code> that supports the specified 
     * algorithm and mechanism type is returned. 
     * 
     * <p> Note that the list of registered providers may be retrieved via 
     * the {@link Security#getProviders() Security.getProviders()} method. 
     *
     * @param algorithm the URI of the algorithm
     * @param mechanismType the type of the XML processing mechanism and 
     *   representation
     * @return a new <code>TransformService</code>
     * @throws NullPointerException if <code>algorithm</code> or 
     *   <code>mechanismType</code> is  <code>null</code>
     * @throws NoSuchAlgorithmException if no <code>Provider</code> supports a
     *   <code>TransformService</code> implementation for the specified 
     *   algorithm and mechanism type
     * @see Provider
     */
    public static TransformService getInstance
        (String algorithm, String mechanismType) 
        throws NoSuchAlgorithmException {
        if (mechanismType == null || algorithm == null) {
            throw new NullPointerException();
        }
        return findInstance(algorithm, mechanismType, null);
    }

    /**
     * Returns a <code>TransformService</code> that supports the specified 
     * algorithm URI (ex: {@link Transform#XPATH2}) and mechanism type 
     * (ex: DOM) as supplied by the specified provider. Note that the specified 
     * <code>Provider</code> object does not have to be registered in the 
     * provider list. 
     *
     * @param algorithm the URI of the algorithm
     * @param mechanismType the type of the XML processing mechanism and 
     *   representation
     * @param provider the <code>Provider</code> object
     * @return a new <code>TransformService</code>
     * @throws NullPointerException if <code>provider</code>, 
     *   <code>algorithm</code>, or <code>mechanismType</code> is 
     *   <code>null</code>
     * @throws NoSuchAlgorithmException if a <code>TransformService</code> 
     *   implementation for the specified algorithm and mechanism type is not 
     *   available from the specified <code>Provider</code> object
     * @see Provider
     */
    public static TransformService getInstance
        (String algorithm, String mechanismType, Provider provider) 
        throws NoSuchAlgorithmException {
        if (mechanismType == null || algorithm == null || provider == null) {
            throw new NullPointerException();
        }
        return findInstance(algorithm, mechanismType, provider);
    }

    /**
     * Returns a <code>TransformService</code> that supports the specified 
     * algorithm URI (ex: {@link Transform#XPATH2}) and mechanism type 
     * (ex: DOM) as supplied by the specified provider. The specified provider 
     * must be registered in the security provider list. 
     *
     * <p>Note that the list of registered providers may be retrieved via 
     * the {@link Security#getProviders() Security.getProviders()} method.
     *
     * @param algorithm the URI of the algorithm
     * @param mechanismType the type of the XML processing mechanism and 
     *   representation
     * @param provider the string name of the provider
     * @return a new <code>TransformService</code>
     * @throws NoSuchProviderException if the specified provider is not 
     *   registered in the security provider list
     * @throws NullPointerException if <code>provider</code>, 
     *   <code>mechanismType</code>, or <code>algorithm</code> is 
     *   <code>null</code>
     * @throws NoSuchAlgorithmException if a <code>TransformService</code> 
     *   implementation for the specified algorithm and mechanism type is not 
     *   available from the specified provider
     * @see Provider
     */
    public static TransformService getInstance
        (String algorithm, String mechanismType, String provider) 
        throws NoSuchAlgorithmException, NoSuchProviderException {
        if (mechanismType == null || algorithm == null || provider == null) {
            throw new NullPointerException();
        }
        Provider prov = Security.getProvider(provider);
        if (prov == null) {
            throw new NoSuchProviderException("cannot find provider named "
                + provider);
        }
        return findInstance(algorithm, mechanismType, prov);
    }

    private static TransformService findInstance(String algorithm, 
        String mechanismType, Provider provider) 
        throws NoSuchAlgorithmException {

        if (provider == null) {
            provider = getProvider("TransformService", algorithm,
                                   mechanismType);
        }
        Provider.Service ps = provider.getService("TransformService",
                                                  algorithm);
        if (ps == null) {
            throw new NoSuchAlgorithmException("no such algorithm: " +
                                               algorithm + " for provider " +
                                               provider.getName());
        }
        TransformService ts = (TransformService)ps.newInstance(null);
        ts.algorithm = algorithm;
        ts.mechanism = mechanismType;
        ts.provider = provider;
        return ts;
    }

    private static Provider getProvider(String engine, String alg, String mech)
        throws NoSuchAlgorithmException
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put(engine + "." + alg, "");
        map.put(engine + "." + alg + " " + "MechanismType", mech);
        Provider[] providers = Security.getProviders(map);
        if (providers == null) {
            if (mech.equals("DOM")) {
                // look for providers without MechanismType specified
                map.clear();
                map.put(engine + "." + alg, "");
                providers = Security.getProviders(map);
                if (providers != null) {
                    return providers[0];
                }
            }
            throw new NoSuchAlgorithmException("Algorithm type " + alg +
                                               " not available");
        }
        return providers[0];
    }

    /**
     * Returns the mechanism type supported by this <code>TransformService</code>.
     *
     * @return the mechanism type
     */
    public final String getMechanismType() {
        return mechanism;
    }

    /**
     * Returns the URI of the algorithm supported by this
     * <code>TransformService</code>.
     *
     * @return the algorithm URI
     */
    public final String getAlgorithm() {
        return algorithm;
    }

    /**
     * Returns the provider of this <code>TransformService</code>.
     *
     * @return the provider
     */
    public final Provider getProvider() {
        return provider;
    }

    /**
     * Initializes this <code>TransformService</code> with the specified 
     * parameters.
     *
     * <p>If the parameters exist in XML form, the 
     * {@link #init(XMLStructure, XMLCryptoContext)} method should be used to 
     * initialize the <code>TransformService</code>.
     *
     * @param params the algorithm parameters (may be <code>null</code> if 
     *   not required or optional)
     * @throws InvalidAlgorithmParameterException if the specified parameters 
     *   are invalid for this algorithm
     */
    public abstract void init(TransformParameterSpec params)
        throws InvalidAlgorithmParameterException;

    /**
     * Marshals the algorithm-specific parameters. If there are no parameters
     * to be marshalled, this method returns without throwing an exception.
     *
     * @param parent a mechanism-specific structure containing the parent
     *    node that the marshalled parameters should be appended to
     * @param context the <code>XMLCryptoContext</code> containing
     *    additional context (may be <code>null</code> if not applicable)
     * @throws ClassCastException if the type of <code>parent</code> or
     *    <code>context</code> is not compatible with this
     *    <code>TransformService</code>
     * @throws NullPointerException if <code>parent</code> is <code>null</code>
     * @throws MarshalException if the parameters cannot be marshalled
     */
    public abstract void marshalParams
        (XMLStructure parent, XMLCryptoContext context)
        throws MarshalException;

    /**
     * Initializes this <code>TransformService</code> with the specified 
     * parameters and document context. 
     *
     * @param parent a mechanism-specific structure containing the parent
     *    structure
     * @param context the <code>XMLCryptoContext</code> containing
     *    additional context (may be <code>null</code> if not applicable)
     * @throws ClassCastException if the type of <code>parent</code> or
     *    <code>context</code> is not compatible with this
     *    <code>TransformService</code>
     * @throws NullPointerException if <code>parent</code> is <code>null</code>
     * @throws InvalidAlgorithmParameterException if the specified parameters 
     *   are invalid for this algorithm
     */
    public abstract void init(XMLStructure parent, XMLCryptoContext context)
        throws InvalidAlgorithmParameterException;
}
