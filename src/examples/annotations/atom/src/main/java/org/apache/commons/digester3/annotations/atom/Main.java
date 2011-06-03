package org.apache.commons.digester3.annotations.atom;

import static org.apache.commons.digester3.binder.DigesterLoader.newLoader;

import java.io.IOException;
import java.util.Date;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.annotations.FromAnnotationsRuleModule;
import org.xml.sax.SAXException;

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

public final class Main
{

    /**
     * @param args
     */
    public static void main( String[] args )
    {
        if ( args.length != 1 )
        {
            usage();
            System.exit( -1 );
        }

        // Drive commons-beanutils how to convert dates
        DateConverter dateConverter = new DateConverter();
        dateConverter.setPatterns( new String[] { "yyyy-MM-dd'T'HH:mm" } );
        ConvertUtils.register( dateConverter, Date.class );

        String filename = args[0];

        Digester digester = newLoader( new FromAnnotationsRuleModule()
        {

            @Override
            protected void configureRules()
            {
                bindRulesFrom( Feed.class );
            }

        } ).newDigester();

        try
        {
            Feed feed = digester.parse( filename );
            System.out.println( feed );
        }
        catch ( IOException ioe )
        {
            System.out.println( "Error reading input file:" + ioe.getMessage() );
            System.exit( -1 );
        }
        catch ( SAXException se )
        {
            System.out.println( "Error parsing input file:" + se.getMessage() );
            System.exit( -1 );
        }
    }

    private static void usage()
    {
        System.out.println( "Usage: java org.apache.commons.digester3.edsl.atom.Main xmlcontent.xml" );
    }

}
