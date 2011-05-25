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

import static java.util.Collections.unmodifiableSet;
import static org.apache.commons.digester3.binder.DigesterLoader.newLoader;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.binder.AbstractRulesModule;
import org.apache.commons.digester3.binder.RulesModule;
import org.apache.commons.digester3.xmlrules.metaparser.XmlRulesModule;
import org.xml.sax.InputSource;

/**
 * {@link RulesModule} implementation that allows loading rules from
 * XML files.
 *
 * @since 3.0
 */
public abstract class FromXmlRulesModule
    extends AbstractRulesModule
{

    private static final String DIGESTER_PUBLIC_ID = "-//Apache Commons //DTD digester-rules XML V1.0//EN";

    private static final String DIGESTER_DTD_PATH = "digester-rules.dtd";

    private final URL xmlRulesDtdUrl = this.getClass().getResource(DIGESTER_DTD_PATH);

    private final List<InputSource> inputSource = new ArrayList<InputSource>();

    private final Set<String> systemIds = new HashSet<String>();

    private String rootPath;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure()
    {
        if ( !inputSource.isEmpty() )
        {
            throw new IllegalStateException( "Re-entry is not allowed." );
        }

        try
        {
            loadRules();

            XmlRulesModule xmlRulesModule = new XmlRulesModule( rulesBinder(), getSystemIds(), rootPath );
            Digester digester = newLoader( xmlRulesModule )
                    .register( DIGESTER_PUBLIC_ID, xmlRulesDtdUrl.toString() )
                    .setXIncludeAware( true )
                    .newDigester();

            for ( InputSource source : inputSource )
            {
                try
                {
                    digester.parse( source );
                }
                catch ( Exception e )
                {
                    addError( "Impossible to load XML defined in the InputSource '%s': %s", source.getSystemId(),
                              e.getMessage() );
                }
            }
        }
        finally
        {
            inputSource.clear();
        }
    }

    /**
     * 
     */
    protected abstract void loadRules();

    /**
     * Reads the XML rules from the given {@code org.xml.sax.InputSource}.
     *
     * @param inputSource The {@code org.xml.sax.InputSource} where reading the XML rules from.
     */
    protected final void loadXMLRules( InputSource inputSource )
    {
        if ( inputSource == null )
        {
            throw new IllegalArgumentException( "Argument 'inputSource' must be not null" );
        }

        this.inputSource.add( inputSource );

        String systemId = inputSource.getSystemId();
        if ( systemId != null )
        {
            if ( !systemIds.add( systemId ) )
            {
                addError( "XML rules file '%s' already bound", systemId );
            }
        }
    }

    /**
     * Opens a new {@code org.xml.sax.InputSource} given a {@code java.io.InputStream}.
     *
     * @param input The {@code java.io.InputStream} where reading the XML rules from.
     */
    protected final void loadXMLRules( InputStream input )
    {
        if ( input == null )
        {
            throw new IllegalArgumentException( "Argument 'input' must be not null" );
        }

        loadXMLRules( new InputSource( input ) );
    }

    /**
     * Opens a new {@code org.xml.sax.InputSource} given a {@code java.io.Reader}.
     *
     * @param reader The {@code java.io.Reader} where reading the XML rules from.
     */
    protected final void loadXMLRules( Reader reader )
    {
        if ( reader == null )
        {
            throw new IllegalArgumentException( "Argument 'input' must be not null" );
        }

        loadXMLRules( new InputSource( reader ) );
    }

    /**
     * Opens a new {@code org.xml.sax.InputSource} given a {@code java.io.File}.
     *
     * @param file The {@code java.io.File} where reading the XML rules from.
     */
    protected final void loadXMLRules( File file )
    {
        if ( file == null )
        {
            throw new IllegalArgumentException( "Argument 'input' must be not null" );
        }

        try
        {
            loadXMLRules( file.toURI().toURL() );
        }
        catch ( MalformedURLException e )
        {
            rulesBinder().addError( e );
        }
    }

    /**
     * Opens a new {@code org.xml.sax.InputSource} given a URI in String representation.
     *
     * @param uri The URI in String representation where reading the XML rules from.
     */
    protected final void loadXMLRules( String uri )
    {
        if ( uri == null )
        {
            throw new IllegalArgumentException( "Argument 'uri' must be not null" );
        }

        try
        {
            loadXMLRules( new URL( uri ) );
        }
        catch ( MalformedURLException e )
        {
            rulesBinder().addError( e );
        }
    }

    /**
     * Opens a new {@code org.xml.sax.InputSource} given a {@code java.net.URL}.
     *
     * @param url The {@code java.net.URL} where reading the XML rules from.
     */
    protected final void loadXMLRules( URL url )
    {
        if ( url == null )
        {
            throw new IllegalArgumentException( "Argument 'url' must be not null" );
        }

        try
        {
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            InputStream stream = connection.getInputStream();
            InputSource source = new InputSource(stream);
            source.setSystemId(url.toExternalForm());

            loadXMLRules( source );
        }
        catch (Exception e) {
            rulesBinder().addError( e );
        }
    }

    /**
     * Opens a new {@code org.xml.sax.InputSource} given an XML document in textual form.
     *
     * @param xmlText The XML document in textual form where reading the XML rules from.
     */
    protected final void loadXMLRulesFromText( String xmlText )
    {
        if ( xmlText == null )
        {
            throw new IllegalArgumentException( "Argument 'xmlText' must be not null" );
        }

        loadXMLRules( new StringReader( xmlText ) );
    }

    /**
     * Set the root path (will be used when composing modules).
     *
     * @param rootPath The root path
     */
    protected final void useRootPath( String rootPath )
    {
        this.rootPath = rootPath;
    }

    /**
     * Returns the XML source SystemIds load by this module.
     *
     * @return The XML source SystemIds load by this module
     */
    public final Set<String> getSystemIds()
    {
        return unmodifiableSet( systemIds );
    }

}
