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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;

import org.apache.commons.digester3.rules.BaseRules;
import org.apache.commons.digester3.spi.Rules;
import org.apache.commons.digester3.spi.Substitutor;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

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
    private RulesBinderImpl rulesBinder;

    /**
     * The URLs of entityValidator that have been registered, keyed by the public
     * identifier that corresponds.
     */
    private final Map<String, URL> entityValidator = new HashMap<String, URL>();

    /**
     * The SAXParserFactory to create new default {@link Digester} instances.
     */
    private final SAXParserFactory factory = SAXParserFactory.newInstance();

    private final Object $lock = new Object();

    private final Collection<RulesModule> rulesModules;

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
        this.rulesModules = rulesModules;
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
     * Return the "namespace aware" flag for parsers we create.
     */
    public boolean isNamespaceAware() {
        return this.factory.isNamespaceAware();
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
     * Return the XInclude-aware flag for parsers we create;
     *
     * @return true, if the XInclude-aware flag for parsers we create is set,
     *         false otherwise
     */
    public boolean isXIncludeAware() {
        return this.factory.isXIncludeAware();
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
     * Return the validating parser flag.
     *
     * @return true, if the validating parser flag is set, false otherwise
     */
    public boolean isValidating() {
        return this.factory.isValidating();
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

    /**
     * Creates a new {@link Digester} instance that relies on the default {@link Rules} implementation.
     *
     * @return a new {@link Digester} instance
     */
    public Digester newDigester() {
        return this.newDigester(new BaseRules());
    }

    /**
     * Creates a new {@link Digester} instance that relies on the custom user define {@link Rules} implementation
     *
     * @param rules The custom user define {@link Rules} implementation
     * @return a new {@link Digester} instance
     */
    public Digester newDigester(Rules rules) {
        try {
            return this.newDigester(this.factory.newSAXParser(), rules);
        } catch (ParserConfigurationException e) {
            throw new DigesterLoadingException("SAX Parser misconfigured", e);
        } catch (SAXException e) {
            throw new DigesterLoadingException("An error occurred while initializing the SAX Parser", e);
        }
    }

    /**
     * Creates a new {@link Digester} instance that relies on the given {@code SAXParser}
     * and the default {@link Rules} implementation.
     *
     * @param parser the user defined {@code SAXParser}
     * @return a new {@link Digester} instance
     */
    public Digester newDigester(SAXParser parser) {
        return this.newDigester(parser, new BaseRules());
    }

    /**
     * Creates a new {@link Digester} instance that relies on the given {@code SAXParser}
     * and custom user define {@link Rules} implementation.
     *
     * @param parser The user defined {@code SAXParser}
     * @param rules The custom user define {@link Rules} implementation
     * @return a new {@link Digester} instance
     */
    public Digester newDigester(SAXParser parser, Rules rules) {
        if (parser == null) {
            throw new DigesterLoadingException("SAXParser must be not null");
        }

        try {
            return this.newDigester(parser.getXMLReader(), rules);
        } catch (SAXException e) {
            throw new DigesterLoadingException("An error occurred while creating the XML Reader", e);
        }
    }

    /**
     * Creates a new {@link XMLReader} instance that relies on the given {@code XMLReader}
     * and the default {@link Rules} implementation.
     *
     * @param reader The user defined {@code XMLReader}
     * @return a new {@link Digester} instance
     */
    public Digester newDigester(XMLReader reader) {
        return this.newDigester(reader, new BaseRules());
    }

    /**
     * Creates a new {@link XMLReader} instance that relies on the given {@code XMLReader}
     * and custom user define {@link Rules} implementation.
     *
     * @param reader The user defined {@code XMLReader}
     * @param rules The custom user define {@link Rules} implementation
     * @return a new {@link Digester} instance
     */
    public Digester newDigester(XMLReader reader, Rules rules) {
        if (reader == null) {
            throw new DigesterLoadingException("XMLReader must be not null");
        }
        if (rules == null) {
            throw new DigesterLoadingException("Impossible to create a new Digester with null Rules");
        }

        ClassLoader classLoader = this.classLoader != null ? this.classLoader :
            (this.useContextClassLoader ? Thread.currentThread().getContextClassLoader() : this.getClass().getClassLoader());

        synchronized (this.$lock) {
            if (this.rulesBinder == null) {
                this.rulesBinder = new RulesBinderImpl(classLoader);
                for (RulesModule rulesModule : rulesModules) {
                    rulesModule.configure(this.rulesBinder);
                }
            }
        }

        // check if there were errors while binding rules
        if (this.rulesBinder.containsErrors()) {
            throw new DigesterLoadingException(this.rulesBinder.getErrors());
        }

        this.rulesBinder.populateRules(rules);

        return new DigesterImpl(reader, rules, classLoader, this.substitutor, this.entityValidator);
    }

}
