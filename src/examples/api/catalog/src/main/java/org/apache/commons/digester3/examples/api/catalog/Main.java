package org.apache.commons.digester3.examples.api.catalog;

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

import org.apache.commons.digester3.Digester;

/**
 * A simple program to demonstrate some of the functionality of the
 * Commons Digester module.
 * <p>
 * This code will parse the provided "example.xml" file to build a tree
 * of java objects, then cause those objects to print out their values
 * to demonstrate that the input file has been processed correctly. The
 * input file represents a catalog of items in a library.
 * <p>
 * As with all code, there are many ways of achieving the same goal;
 * the solution here is only one possible implementation.
* <p> 
 * Very verbose comments are included here, as this class is intended
 * as a tutorial; if you look closely at method "addRules", you will
 * see that the amount of code required to use the Digester is actually
 * quite low.
 * <p>
 * Usage: java Main example.xml
 */
public class Main
{

    /**
     * Main method : entry point for running this example program.
     * <p>
     * Usage: java CatalogDigester example.xml
     */
    public static void main( String[] args )
    {
        if ( args.length != 1 )
        {
            usage();
            System.exit( -1 );
        }

        String filename = args[0];

        // Create a Digester instance
        Digester d = new Digester();

        // Add rules to the digester that will be triggered while
        // parsing occurs.
        addRules( d );

        // Process the input file.
        try
        {
            java.io.Reader reader = getInputData( filename );
            d.parse( reader );
        }
        catch ( java.io.IOException ioe )
        {
            System.out.println( "Error reading input file:" + ioe.getMessage() );
            System.exit( -1 );
        }
        catch ( org.xml.sax.SAXException se )
        {
            System.out.println( "Error parsing input file:" + se.getMessage() );
            System.exit( -1 );
        }

        // Get the first object created by the digester's rules
        // (the "root" object). Note that this is exactly the same object
        // returned by the Digester.parse method; either approach works.
        Catalog catalog = (Catalog) d.getRoot();

        // Print out all the contents of the catalog, as loaded from
        // the input file.
        catalog.print();
    }

    private static void addRules( Digester d )
    {

        // --------------------------------------------------

        // when we encounter the root "catalog" tag, create an
        // instance of the Catalog class.
        //
        // Note that this approach is different from the approach taken in
        // the AddressBook example, where an initial "root" object was
        // explicitly created and pushed onto the digester stack before
        // parsing started instead
        //
        // Either approach is fine.

        d.addObjectCreate( "catalog", Catalog.class );

        // --------------------------------------------------

        // when we encounter a book tag, we want to create a Book
        // instance. However the Book class doesn't have a default
        // constructor (one with no arguments), so we can't use
        // the ObjectCreateRule. Instead, we use the FactoryCreateRule.

        BookFactory factory = new BookFactory();
        d.addFactoryCreate( "catalog/book", factory );

        // and add the book to the parent catalog object (which is
        // the next-to-top object on the digester object stack).
        d.addSetNext( "catalog/book", "addItem" );

        // we want each subtag of book to map the text contents of
        // the tag into a bean property with the same name as the tag.
        // eg <title>foo</title> --> setTitle("foo")
        d.addSetNestedProperties( "catalog/book" );

        // -----------------------------------------------

        // We are using the "AudioVisual" class to represent both
        // dvds and videos, so when the "dvd" tag is encountered,
        // create an AudioVisual object.

        d.addObjectCreate( "catalog/dvd", AudioVisual.class );

        // add this dvd to the parent catalog object

        d.addSetNext( "catalog/dvd", "addItem" );

        // We want to map every xml attribute onto a corresponding
        // property-setter method on the Dvd class instance. However
        // this doesn't work with the xml attribute "year-made", because
        // of the internal hyphen. We could use explicit CallMethodRule
        // rules instead, or use a version of the SetPropertiesRule that
        // allows us to override any troublesome mappings...
        //
        // If there was more than one troublesome mapping, we could
        // use the method variant that takes arrays of xml-attribute-names
        // and bean-property-names to override multiple mappings.
        //
        // For any attributes not explicitly mapped here, the default
        // processing is applied, so xml attribute "category" --> setCategory.

        d.addSetProperties( "catalog/dvd", "year-made", "yearMade" );

        // We also need to tell this AudioVisual object that it is actually
        // a dvd; we can use the ObjectParamRule to pass a string to any
        // method. This usage is a little artificial - normally in this
        // situation there would be separate Dvd and Video classes.
        // Note also that equivalent behaviour could be implemented by
        // using factory objects to create & initialise the AudioVisual
        // objects with their type rather than using ObjectCreateRule.

        d.addCallMethod( "catalog/dvd", "setType", 1 );
        d.addObjectParam( "catalog/dvd", 0, "dvd" ); // pass literal "dvd" string

        // Each tag of form "<attr id="foo" value="bar"/> needs to map
        // to a call to setFoo("bar").
        //
        // This is an alternative to the syntax used for books above (see
        // method addSetNestedProperties), where the name of the subtag
        // indicated which property to set. Using this syntax in the xml has
        // advantages and disadvantages both for the user and the application
        // developer. It is commonly used with the FactoryCreateRule variant
        // which allows the target class to be created to be specified in an
        // xml attribute; this feature of FactoryCreateRule is not demonstrated
        // in this example, but see the Apache Tomcat configuration files for
        // an example of this usage.
        //
        // Note that despite the name similarity, there is no link
        // between SetPropertyRule and SetPropertiesRule.

        d.addSetProperty( "catalog/dvd/attr", "id", "value" );

        // -----------------------------------------------

        // and here we repeat the dvd rules, but for the video tag.
        d.addObjectCreate( "catalog/video", AudioVisual.class );
        d.addSetNext( "catalog/video", "addItem" );
        d.addSetProperties( "catalog/video", "year-made", "yearMade" );
        d.addCallMethod( "catalog/video", "setType", 1 );
        d.addObjectParam( "catalog/video", 0, "video" );
        d.addSetProperty( "catalog/video/attr", "id", "value" );
    }

    /*
     * Reads the specified file into memory, and returns a StringReader object which reads from that in-memory buffer.
     * <p> This method exists just to demonstrate that the input to the digester doesn't need to be from a file; for
     * example, xml could be read from a database or generated dynamically; any old buffer in memory can be processed by
     * the digester. <p> Clearly, if the data is always coming from a file, then calling the Digester.parse method that
     * takes a File object would be more sensible (see AddressBook example).
     */
    private static java.io.Reader getInputData( String filename )
        throws java.io.IOException
    {
        java.io.File srcfile = new java.io.File( filename );

        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream( 1000 );
        byte[] buf = new byte[100];
        java.io.FileInputStream fis = new java.io.FileInputStream( srcfile );
        for ( ;; )
        {
            int nread = fis.read( buf );
            if ( nread == -1 )
            {
                break;
            }
            baos.write( buf, 0, nread );
        }
        fis.close();

        return new java.io.StringReader( baos.toString() );

    }

    private static void usage()
    {
        System.out.println( "Usage: java Main example.xml" );
    }

}
