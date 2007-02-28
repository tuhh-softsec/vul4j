package org.codehaus.plexus.util;

/*
 * Copyright 2007 The Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:mmaczka@interia.pl">Michal Maczka</a>
 *
 * @version $Id$
 */
public class PropertyUtils
{

    public static Properties loadProperties( URL url )
    {
        try
        {
            return loadProperties( url.openStream() );
        }
        catch ( Exception e )
        {
            // ignore
        }

        return null;
    }

    public static Properties loadProperties( File file )
    {
        try
        {
            return loadProperties( new FileInputStream( file ) );
        }
        catch ( Exception e )
        {
            // ignore
        }

        return null;
    }

    public static Properties loadProperties( InputStream is )
    {
        try
        {
            Properties properties = new Properties();

            // Make sure the properties stream is valid
            if ( is != null )
            {
                properties.load( is );
            }

            return properties;
        }
        catch ( IOException e )
        {
            // ignore
        }
        finally
        {
            try
            {
                if ( is != null )
                {
                    is.close();
                }
            }
            catch ( IOException e )
            {
                // ignore
            }
        }

        return null;
    }
}
