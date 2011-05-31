package org.apache.commons.digester3.plugins.strategies;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.plugins.PluginException;
import org.apache.commons.digester3.plugins.RuleFinder;
import org.apache.commons.digester3.plugins.RuleLoader;

/**
 * A rule-finding algorithm which expects the user to specify an absolute or relative path in the plugin declaration.
 * <p>
 * The file is expected to contain Digester rules in xmlrules format.
 * 
 * @since 1.6
 */
public class FinderFromFile
    extends RuleFinder
{

    /**
     * The default XML attribute that needs to be present on a plugin declaration in order to specify the file to
     * load rules from.
     */
    private static final String DFLT_FILENAME_ATTR = "file";

    /** See {@link #findLoader}. */
    private final String filenameAttr;

    /** See {@link #findLoader}. */
    public FinderFromFile()
    {
        this( DFLT_FILENAME_ATTR );
    }

    /**
     * See {@link #findLoader}.
     *
     * @param filenameAttr the XML attribute that needs to be present on a plugin declaration in order to specify the
     *        file to load rules from.
     */
    public FinderFromFile( String filenameAttr )
    {
        this.filenameAttr = filenameAttr;
    }

    /**
     * If there exists a property with the name specified in the constructor, then load that file, run it through the
     * xmlrules module and return an object encapsulating those rules.
     * <p>
     * If there is no matching property provided, then just return null.
     * <p>
     * The returned object (when non-null) will add the selected rules to the digester whenever its addRules method is
     * invoked.
     *
     * @param d The digester instance where locating plugin classes
     * @param pluginClass The plugin Java class
     * @param p The properties object that holds any xml attributes the user may have specified on the plugin
     *          declaration in order to indicate how to locate the plugin rules.
     * @return a source of digester rules for the specified plugin class.
     * @throws PluginException if the algorithm finds a source of rules, but there is something invalid
     *         about that source.
     */
    @Override
    public RuleLoader findLoader( Digester d, Class<?> pluginClass, Properties p )
        throws PluginException
    {

        String rulesFileName = p.getProperty( filenameAttr );
        if ( rulesFileName == null )
        {
            // nope, user hasn't requested dynamic rules to be loaded
            // from a specific file.
            return null;
        }

        InputStream is = null;
        try
        {
            is = new FileInputStream( rulesFileName );
        }
        catch ( IOException ioe )
        {
            throw new PluginException( "Unable to process file [" + rulesFileName + "]", ioe );
        }

        try
        {
            RuleLoader loader = new LoaderFromStream( is );
            return loader;
        }
        catch ( Exception e )
        {
            throw new PluginException( "Unable to load xmlrules from file [" + rulesFileName + "]", e );
        }
        finally
        {
            try
            {
                is.close();
            }
            catch ( IOException ioe )
            {
                throw new PluginException( "Unable to close stream for file [" + rulesFileName + "]", ioe );
            }
        }
    }

}
