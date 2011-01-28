/*
 * Copyright  1999-2004 The Apache Software Foundation.
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
package org.apache.xml.security.utils.resolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.xml.security.signature.XMLSignatureInput;
import org.w3c.dom.Attr;

/**
 * During reference validation, we have to retrieve resources from somewhere.
 * This is done by retrieving a Resolver. The resolver needs two arguments: The
 * URI in which the link to the new resource is defined and the baseURI of the
 * file/entity in which the URI occurs (the baseURI is the same as the SystemId).
 *
 * @author $Author$
 */
public class ResourceResolver {

    /** {@link org.apache.commons.logging} logging facility */
    private static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(ResourceResolver.class.getName());

    /** these are the system-wide resolvers */
    private static List<ResourceResolver> resolverVector = new ArrayList<ResourceResolver>(10);

    /** Field transformSpi */
    protected ResourceResolverSpi resolverSpi = null;

    /**
     * Constructor ResourceResolver
     *
     * @param className
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private ResourceResolver(String className)
        throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        this.resolverSpi =
            (ResourceResolverSpi) Class.forName(className).newInstance();
    }

    /**
     * Constructor ResourceResolver
     *
     * @param resourceResolver
     */
    public ResourceResolver(ResourceResolverSpi resourceResolver) {
        this.resolverSpi = resourceResolver;
    }

    /**
     * Method getInstance
     *
     * @param uri
     * @param baseURI
     * @return the instance
     *
     * @throws ResourceResolverException
     */
    public synchronized static final ResourceResolver getInstance(Attr uri, String baseURI)
        throws ResourceResolverException {
        for (int i = 0; i < resolverVector.size(); i++) {
            ResourceResolver resolver = resolverVector.get(i);
            ResourceResolver resolverTmp = null;
            
            if (resolver.resolverSpi.engineIsThreadSafe()) {
                resolverTmp = resolver;
            } else {
                try {
                    resolverTmp = 
                        new ResourceResolver(
                            (ResourceResolverSpi)resolver.resolverSpi.getClass().newInstance()
                        );
                } catch (InstantiationException e) {
                    throw new ResourceResolverException("", e, uri, baseURI);
                } catch (IllegalAccessException e) {
                    throw new ResourceResolverException("", e, uri, baseURI);			
                }
            }

            if (log.isDebugEnabled()) {
                log.debug(
                    "check resolvability by class " + resolver.resolverSpi.getClass().getName()
                );
            }

            if ((resolverTmp != null) && resolverTmp.canResolve(uri, baseURI)) {
                if (i != 0) {
                    // update resolver.
                    resolverVector.remove(i);
                    resolverVector.add(0, resolver);        		 
                }

                return resolverTmp;
            }
        }

        Object exArgs[] = { ((uri != null) ? uri.getNodeValue() : "null"), baseURI };

        throw new ResourceResolverException("utils.resolver.noClass", exArgs, uri, baseURI);
    }
    
    /**
     * Method getInstance
     *
     * @param uri
     * @param baseURI
     * @param individualResolvers
     * @return the instance
     *
     * @throws ResourceResolverException
     */
    public static final ResourceResolver getInstance(
        Attr uri, String baseURI, List<ResourceResolver> individualResolvers
    ) throws ResourceResolverException {
        if (log.isDebugEnabled()) {
            log.debug(
                "I was asked to create a ResourceResolver and got " 
                + (individualResolvers == null ? 0 : individualResolvers.size())
            );
        }

        // first check the individual Resolvers
        if (individualResolvers != null) {
            for (int i = 0; i < individualResolvers.size(); i++) {
                ResourceResolver resolver = individualResolvers.get(i);

                if (resolver != null) {
                    String currentClass = resolver.resolverSpi.getClass().getName();
                    if (log.isDebugEnabled()) {
                        log.debug("check resolvability by class " + currentClass);
                    }

                    if (resolver.canResolve(uri, baseURI)) {
                        return resolver;
                    }
                }
            }
        }

        return getInstance(uri, baseURI);
    }

    /**
     * Registers a ResourceResolverSpi class. This method logs a warning if
     * the class cannot be registered.
     *
     * @param className the name of the ResourceResolverSpi class to be registered
     */
    public static void register(String className) {
        register(className, false);
    }

    /**
     * Registers a ResourceResolverSpi class at the beginning of the provider
     * list. This method logs a warning if the class cannot be registered.
     *
     * @param className the name of the ResourceResolverSpi class to be registered
     */
    public static void registerAtStart(String className) {
        register(className, true);
    }

    private synchronized static void register(String className, boolean start) {
        try {
            ResourceResolver resolver = new ResourceResolver(className);
            if (start) {
                resolverVector.add(0, resolver);
            } else {	       
                resolverVector.add(resolver);
            }
            if (log.isDebugEnabled()) {
                log.debug("Registered resolver: " + className);
            }
        } catch (Exception e) {
            log.warn("Error loading resolver " + className +" disabling it");
        } catch (NoClassDefFoundError e) {
            log.warn("Error loading resolver " + className +" disabling it");
        }
    }

    /**
     * Method resolveStatic
     *
     * @param uri
     * @param baseURI
     * @return the resource
     *
     * @throws ResourceResolverException
     */
    public static XMLSignatureInput resolveStatic(Attr uri, String baseURI)
        throws ResourceResolverException {
        ResourceResolver myResolver = ResourceResolver.getInstance(uri, baseURI);

        return myResolver.resolve(uri, baseURI);
    }

    /**
     * Method resolve
     *
     * @param uri
     * @param baseURI
     * @return the resource
     *
     * @throws ResourceResolverException
     */
    public XMLSignatureInput resolve(Attr uri, String baseURI)
        throws ResourceResolverException {
        return this.resolverSpi.engineResolve(uri, baseURI);
    }

    /**
     * Method setProperty
     *
     * @param key
     * @param value
     */
    public void setProperty(String key, String value) {
        this.resolverSpi.engineSetProperty(key, value);
    }

    /**
     * Method getProperty
     *
     * @param key
     * @return the value of the property
     */
    public String getProperty(String key) {
        return this.resolverSpi.engineGetProperty(key);
    }

    /**
     * Method addProperties
     *
     * @param properties
     */
    public void addProperties(Map<String, String> properties) {
        this.resolverSpi.engineAddProperies(properties);
    }

    /**
     * Method getPropertyKeys
     *
     * @return all property keys.
     */
    public String[] getPropertyKeys() {
        return this.resolverSpi.engineGetPropertyKeys();
    }

    /**
     * Method understandsProperty
     *
     * @param propertyToTest
     * @return true if the resolver understands the property
     */
    public boolean understandsProperty(String propertyToTest) {
        return this.resolverSpi.understandsProperty(propertyToTest);
    }

    /**
     * Method canResolve
     *
     * @param uri
     * @param baseURI
     * @return true if it can resolve the uri
     */
    private boolean canResolve(Attr uri, String baseURI) {
        return this.resolverSpi.engineCanResolve(uri, baseURI);
    }
}
