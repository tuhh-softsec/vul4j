/* $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.digester3;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;

import org.apache.commons.digester3.spi.Substitutor;

/**
 * This class manages the creation of Digester instances from digester rules modules.
 */
public final class DigesterLoader {

    /**
     * Creates a new {@link DigesterLoader} instance given one or more {@link RulesModule} instance.
     *
     * @param rulesModules The modules containing the {@code Rule} binding
     * @return A new {@link DigesterLoader} instance
     */
    public static DigesterLoader newLoader(RulesModule...rulesModules) {
        if (rulesModules == null || rulesModules.length == 0) {
            throw new DigesterLoadingException("At least one RulesModule has to be specified");
        }
        return newLoader(Arrays.asList(rulesModules));
    }

    /**
     * Creates a new {@link DigesterLoader} instance given a collection of {@link RulesModule} instance.
     *
     * @param rulesModules The modules containing the {@code Rule} binding
     * @return A new {@link DigesterLoader} instance
     */
    public static DigesterLoader newLoader(Collection<RulesModule> rulesModules) {
        if (rulesModules == null || rulesModules.isEmpty()) {
            throw new DigesterLoadingException("At least one RulesModule has to be specified");
        }

        return new DigesterLoader(rulesModules);
    }

    /**
     * The concrete {@link RulesBinder} implementation.
     */
    private final RulesBinderImpl rulesBinder = new RulesBinderImpl();

    /**
     * The URLs of entityValidator that have been registered, keyed by the public
     * identifier that corresponds.
     */
    private final Map<String, URL> entityValidator = new HashMap<String, URL>();

    /**
     * The SAXParserFactory to create new default {@link Digester} instances.
     */
    private final SAXParserFactory factory = SAXParserFactory.newInstance();

    private boolean useContextClassLoader;

    /**
     * The class loader to use for instantiating application objects.
     * If not specified, the context class loader, or the class loader
     * used to load Digester itself, is used, based on the value of the
     * <code>useContextClassLoader</code> variable.
     */
    private ClassLoader classLoader;

    private Substitutor substitutor;

    /**
     * Creates a new {@link DigesterLoader} instance given a collection of {@link RulesModule} instance.
     *
     * @param rulesModules The modules containing the {@code Rule} binding
     */
    private DigesterLoader(Collection<RulesModule> rulesModules) {
        for (RulesModule rulesModule : rulesModules) {
            rulesModule.configure(this.rulesBinder);
        }

        // check if there were errors while binding rules
        if (this.rulesBinder.containsErrors()) {
            throw new DigesterLoadingException(this.rulesBinder.getErrors());
        }
    }

    /**
     * Determine whether to use the Context ClassLoader (the one found by
     * calling <code>Thread.currentThread().getContextClassLoader()</code>)
     * to resolve/load classes that are defined in various rules.  If not
     * using Context ClassLoader, then the class-loading defaults to
     * using the calling-class' ClassLoader.
     *
     * @param use determines whether to use Context ClassLoader.
     */
    public DigesterLoader setUseContextClassLoader(boolean use) {
        this.useContextClassLoader = use;
        return this;
    }

    /**
     * Set the class loader to be used for instantiating application objects when required.
     *
     * @param classLoader the class loader to be used for instantiating application objects when required.
     */
    public DigesterLoader setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        return this;
    }

    /** 
     * Sets the <code>Substitutor</code> to be used to convert attributes and body text.
     *
     * @param substitutor the Substitutor to be used to convert attributes and body text
     * or null if not substitution of these values is to be performed.
     */
    public DigesterLoader setSubstitutor(Substitutor substitutor) {
        this.substitutor = substitutor;
        return this;
    }

    /**
     * Set the "namespace aware" flag for parsers we create.
     *
     * @param namespaceAware The new "namespace aware" flag
     */
    public DigesterLoader setNamespaceAware(boolean namespaceAware) {
        this.factory.setNamespaceAware(namespaceAware);
        return this;
    }

    /**
     * Set the XInclude-aware flag for parsers we create. This additionally
     * requires namespace-awareness.
     *
     * @param xincludeAware The new XInclude-aware flag
     * @see #setNamespaceAware(boolean)
     */
    public DigesterLoader setXIncludeAware(boolean xIncludeAware) {
        this.factory.setXIncludeAware(xIncludeAware);
        return this;
    }

    /**
     * Set the validating parser flag.
     *
     * @param validating The new validating parser flag.
     */
    public DigesterLoader setValidating(boolean validating) {
        this.factory.setValidating(validating);
        return this;
    }

    /**
     * Set the XML Schema to be used when parsing.
     *
     * @param schema The {@link Schema} instance to use.
     */
    public DigesterLoader setSchema(Schema schema) {
        this.factory.setSchema(schema);
        return this;
    }

    /**
     * <p>Register the specified DTD URL for the specified public identifier.
     * This must be called before the first call to <code>parse()</code>.
     * </p><p>
     * <code>Digester</code> contains an internal <code>EntityResolver</code>
     * implementation. This maps <code>PUBLICID</code>'s to URLs 
     * (from which the resource will be loaded). A common use case for this
     * method is to register local URLs (possibly computed at runtime by a 
     * classloader) for DTDs. This allows the performance advantage of using
     * a local version without having to ensure every <code>SYSTEM</code>
     * URI on every processed xml document is local. This implementation provides
     * only basic functionality. If more sophisticated features are required,
     * using {@link #setEntityResolver} to set a custom resolver is recommended.
     * </p><p>
     * <strong>Note:</strong> This method will have no effect when a custom 
     * <code>EntityResolver</code> has been set. (Setting a custom 
     * <code>EntityResolver</code> overrides the internal implementation.) 
     * </p>
     * @param publicId Public identifier of the DTD to be resolved
     * @param entityURL The URL to use for reading this DTD
     */
    public DigesterLoader register(String publicId, URL entityURL) {
        this.entityValidator.put(publicId, entityURL);
        return this;
    }

    /**
     * <p>Convenience method that registers the string version of an entity URL
     * instead of a URL version.</p>
     *
     * @param publicId Public identifier of the entity to be resolved
     * @param entityURL The URL to use for reading this entity
     */
    public DigesterLoader register(String publicId, String entityURL) {
        try {
            this.register(publicId, new URL(entityURL));
            return this;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Malformed URL '"
                    + entityURL
                    + "' : "
                    + e.getMessage());
        }
    }

    /**
     * Return the set of DTD URL registrations, keyed by public identifier.
     */
    public Map<String, URL> getRegistrations() {
        return Collections.unmodifiableMap(this.entityValidator);
    }

}
