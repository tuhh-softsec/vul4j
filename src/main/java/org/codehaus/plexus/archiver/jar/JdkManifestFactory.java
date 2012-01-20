package org.codehaus.plexus.archiver.jar;
/**
 *
 * Copyright 2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.jar.Attributes;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.util.IOUtil;

/**
 * Not part of any public API
 * @author Kristian Rosenvold
 */
class JdkManifestFactory
{
    public static java.util.jar.Manifest getDefaultManifest()
        throws ArchiverException
    {
        try
        {
            String defManifest = "/org/codehaus/plexus/archiver/jar/defaultManifest.mf";
            InputStream in = JdkManifestFactory.class.getResourceAsStream( defManifest );
            if ( in == null )
            {
                throw new ArchiverException( "Could not find default manifest: " + defManifest );
            }
            try
            {
                java.util.jar.Manifest defaultManifest = new java.util.jar.Manifest( in );

                defaultManifest.getMainAttributes().putValue( "Created-By",
                                                              System.getProperty( "java.vm.version" ) + " ("
                                                                  + System.getProperty( "java.vm.vendor" ) + ")" );
                return defaultManifest;
            }
            finally
            {
                IOUtil.close( in );
            }
        }
        catch ( IOException e )
        {
            throw new ArchiverException( "Unable to read default manifest", e );
        }
    }

    public static void merge( java.util.jar.Manifest target, java.util.jar.Manifest other, boolean overwriteMain )
    {
        if ( other != null )
        {
            final Attributes mainAttributes = target.getMainAttributes();
            if ( overwriteMain )
            {
                mainAttributes.clear();
                mainAttributes.putAll( other.getMainAttributes() );
            }
            else
            {
                mergeAttributes( mainAttributes, other.getMainAttributes() );
            }

            for ( Map.Entry<String, Attributes> o : other.getEntries().entrySet() )
            {
                Attributes ourSection = target.getAttributes( o.getKey() );
                Attributes otherSection = o.getValue();
                if ( ourSection == null )
                {
                    if ( otherSection != null )
                    {
                        target.getEntries().put( o.getKey(), (Attributes) otherSection.clone() );
                    }
                }
                else
                {
                    mergeAttributes( ourSection, otherSection );
                }
            }
        }
    }

    /**
     * Merge in another section
     *
     * @param target The target manifest of the merge
     * @param section the section to be merged with this one.
     */
    public static void mergeAttributes( java.util.jar.Attributes target, java.util.jar.Attributes section )
    {
        for ( Object o : section.keySet() )
        {
            java.util.jar.Attributes.Name key = (Attributes.Name) o;
            final Object value = section.get( o );
            // the merge file always wins
            target.put( key, value );
        }
    }


}
