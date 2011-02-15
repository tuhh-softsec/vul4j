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
package org.apache.commons.digester3.xmlrules;

import static org.apache.commons.digester3.DigesterLoader.newLoader;
import static org.apache.commons.digester3.utils.InputSources.createInputSourceFromFile;
import static org.apache.commons.digester3.utils.InputSources.createInputSourceFromInputStream;
import static org.apache.commons.digester3.utils.InputSources.createInputSourceFromReader;
import static org.apache.commons.digester3.utils.InputSources.createInputSourceFromURL;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import org.apache.commons.digester3.DigesterLoadingException;
import org.apache.commons.digester3.RulesBinder;
import org.apache.commons.digester3.RulesModule;
import org.apache.commons.digester3.xmlrules.metaparser.XmlRulesModule;
import org.xml.sax.InputSource;

/**
 * 
 */
public final class FromXmlRulesModule implements RulesModule {

    private static final String DIGESTER_PUBLIC_ID = "-//Apache Commons //DTD digester-rules XML V2.0//EN";

    private  static final String DIGESTER_DTD_PATH = "digester-rules.dtd";

    private final InputSource xmlRules;

    private URL xmlRulesDtdUrl = this.getClass().getResource(DIGESTER_DTD_PATH);

    /**
     * 
     * @param path
     * @throws IOException
     */
    public FromXmlRulesModule(String path) throws IOException {
        this(path, Thread.currentThread().getContextClassLoader());
    }

    /**
     * 
     * @param path
     * @param classLoader
     * @throws IOException
     */
    public FromXmlRulesModule(String path, ClassLoader classLoader) throws IOException {
        if (path == null) {
            throw new DigesterLoadingException("Parameter 'path' must not be null");
        }
        if (classLoader == null) {
            classLoader = this.getClass().getClassLoader();
        }
        URL xmlRules = classLoader.getResource(path);
        if (xmlRules == null) {
            throw new DigesterLoadingException(String.format("XML Rules '%s' not found on ", path));
        }
        this.xmlRules = createInputSourceFromURL(xmlRules);
    }

    /**
     * 
     * @param file
     * @throws IOException
     */
    public FromXmlRulesModule(File file) throws IOException {
        this(createInputSourceFromFile(file));
    }

    /**
     * 
     * @param xmlRulesURL
     * @throws IOException
     */
    public FromXmlRulesModule(URL xmlRulesURL) throws IOException {
        this(createInputSourceFromURL(xmlRulesURL));
    }

    /**
     * 
     * @param input
     */
    public FromXmlRulesModule(InputStream input) {
        this(createInputSourceFromInputStream(input));
    }

    /**
     * 
     * @param reader
     */
    public FromXmlRulesModule(Reader reader) {
        this(createInputSourceFromReader(reader));
    }

    /**
     * 
     * @param xmlRules
     */
    public FromXmlRulesModule(InputSource xmlRules) {
        if (xmlRules == null) {
            throw new DigesterLoadingException("Parameter 'xmlRules' must not be null");
        }
        this.xmlRules = xmlRules;
    }

    public void setXmlRulesDtdUrl(URL xmlRulesDtdUrl) {
        if (xmlRulesDtdUrl == null) {
            throw new IllegalArgumentException("Parameter 'xmlRulesDtdUrl' must be not null");
        }
        this.xmlRulesDtdUrl = xmlRulesDtdUrl;
    }

    /**
     * {@inheritDoc}
     */
    public void configure(RulesBinder rulesBinder) {
        try {
            newLoader(new XmlRulesModule(rulesBinder))
                .register(DIGESTER_PUBLIC_ID, this.xmlRulesDtdUrl.toString())
                .setXIncludeAware(true)
                .newDigester()
                .parse(this.xmlRules);
        } catch (Exception e) {
            rulesBinder.addError("Impossible to load XML defined in the URL '%s': %s", this.xmlRules, e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("FromXmlRulesModule[%s]",
                this.xmlRules.getSystemId() != null ? this.xmlRules.getSystemId() : this.xmlRules.toString());
    }

}
