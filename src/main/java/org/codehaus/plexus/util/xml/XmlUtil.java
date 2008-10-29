package org.codehaus.plexus.util.xml;

/*
 * Copyright 2008 The Codehaus Foundation.
 *
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

import java.io.File;
import java.io.Reader;

import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.xml.pull.MXParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;

/**
 * Common XML utilities methods.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.5.7
 */
public class XmlUtil
{
    /**
     * Determines if a given File shall be handled as XML.
     *
     * @param f not null file
     * @return <code>true</code> if the given file has XML content, <code>false</code> otherwise.
     */
    public static boolean isXml( File f )
    {
        if ( f == null )
        {
            throw new IllegalArgumentException( "f could not be null." );
        }

        if ( !f.isFile() )
        {
            throw new IllegalArgumentException( "The file '" + f.getAbsolutePath() + "' is not a file." );
        }

        Reader reader = null;
        try
        {
            reader = ReaderFactory.newXmlReader( f );
            XmlPullParser parser = new MXParser();
            parser.setInput( reader );
            parser.nextToken();

            return true;
        }
        catch ( Exception e )
        {
            return false;
        }
        finally
        {
            IOUtil.close( reader );
        }
    }
}
