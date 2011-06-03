package org.apache.commons.digester3.examples.xmlrules.addressbook;

/*
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

import static org.apache.commons.digester3.binder.DigesterLoader.newLoader;

import java.io.File;
import java.io.IOException;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.xmlrules.FromXmlRulesModule;
import org.xml.sax.SAXException;

/**
 * A simple program to demonstrate the basic functionality of the
 * Commons Digester module with the xmlrules extension.
 * <p>
 * This code will parse the provided "example.xml" file to build a tree
 * of java objects, then cause those objects to print out their values
 * to demonstrate that the input file has been processed correctly.
 * <p>
 * Unlike the "addressbook" example in the "api" section, this implementation
 * has no parsing rules hard-wired into the code in this class. Instead, the
 * parsing rules are loaded from an external file at runtime. This allows
 * the parsing rules to be modified without compiling the code, and 
 * potentially makes it somewhat easier to review the parsing rules.
 * <p>
 * Note, however, that there is tyically quite a tight coupling between
 * the parsing rules and the <i>purpose</i> of the application which means
 * that it may not be all that common for parsing rules to be altered
 * without the application code also being altered, so only in some cases
 * will this prove of benefit. As with all software, it must be determined
 * whether this feature provides a true benefit in the context of the 
 * application it is being applied to.
 * <p>
 * Usage: java Main xmlrules.xml example.xml
 */
public class Main
{

    /**
     * Main method : entry point for running this example program.
     * <p>
     * Usage: java Example example.xml
     */
    public static void main( String[] args )
        throws Exception
    {
        if ( args.length != 2 )
        {
            usage();
            System.exit( -1 );
        }

        final String rulesfileName = args[0];
        String datafileName = args[1];

        // Create a Digester instance which has been initialised with
        // rules loaded from the specified file.
        Digester d = newLoader( new FromXmlRulesModule()
        {

            @Override
            protected void loadRules()
            {
                loadXMLRules( rulesfileName );
            }

        } ).newDigester();

        // Prime the digester stack with an object for rules to
        // operate on. Note that it is quite common for "this"
        // to be the object pushed.
        AddressBook book = new AddressBook();
        d.push( book );

        // Process the input file.
        try
        {
            File srcfile = new java.io.File( datafileName );
            d.parse( srcfile );
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

        // Print out all the contents of the address book, as loaded from
        // the input file.
        book.print();
    }

    private static void usage()
    {
        System.out.println( "Usage: java Main xmlrules.xml example.xml" );
    }

}
