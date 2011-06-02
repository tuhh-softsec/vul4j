package org.apache.commons.digester3.examples.api.addressbook;

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
 * A simple program to demonstrate the basic functionality of the
 * Commons Digester module.
 * <p>
 * This code will parse the provided "example.xml" file to build a tree
 * of java objects, then cause those objects to print out their values
 * to demonstrate that the input file has been processed correctly.
 * <p>
 * As with all code, there are many ways of achieving the same goal;
 * the solution here is only one possible solution to the problem.
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
     * Usage: java Example example.xml
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

        // Prime the digester stack with an object for rules to
        // operate on. Note that it is quite common for "this"
        // to be the object pushed.
        AddressBook book = new AddressBook();
        d.push( book );

        // Add rules to the digester that will be triggered while
        // parsing occurs.
        addRules( d );

        // Process the input file.
        try
        {
            java.io.File srcfile = new java.io.File( filename );
            d.parse( srcfile );
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

        // Print out all the contents of the address book, as loaded from
        // the input file.
        book.print();
    }

    private static void addRules( Digester d )
    {

        // --------------------------------------------------
        // when we encounter a "person" tag, do the following:

        // create a new instance of class Person, and push that
        // object onto the digester stack of objects
        d.addObjectCreate( "address-book/person", Person.class );

        // map *any* attributes on the tag to appropriate
        // setter-methods on the top object on the stack (the Person
        // instance created by the preceeding rule).
        //
        // For example:
        // if attribute "id" exists on the xml tag, and method setId
        // with one parameter exists on the object that is on top of
        // the digester object stack, then a call will be made to that
        // method. The value will be type-converted from string to
        // whatever type the target method declares (where possible),
        // using the commons ConvertUtils functionality.
        //
        // Attributes on the xml tag for which no setter methods exist
        // on the top object on the stack are just ignored.
        d.addSetProperties( "address-book/person" );

        // call the addPerson method on the second-to-top object on
        // the stack (the AddressBook object), passing the top object
        // on the stack (the recently created Person object).
        d.addSetNext( "address-book/person", "addPerson" );

        // --------------------------------------------------
        // when we encounter a "name" tag, call setName on the top
        // object on the stack, passing the text contained within the
        // body of that name element [specifying a zero parameter count
        // implies one actual parameter, being the body text].
        // The top object on the stack will be a person object, because
        // the pattern address-book/person always triggers the
        // ObjectCreateRule we added previously.
        d.addCallMethod( "address-book/person/name", "setName", 0 );

        // --------------------------------------------------
        // when we encounter an "email" tag, call addEmail on the top
        // object on the stack, passing two parameters: the "type"
        // attribute, and the text within the tag body.
        d.addCallMethod( "address-book/person/email", "addEmail", 2 );
        d.addCallParam( "address-book/person/email", 0, "type" );
        d.addCallParam( "address-book/person/email", 1 );

        // --------------------------------------------------
        // When we encounter an "address" tag, create an instance of class
        // Address and push it on the digester stack of objects. After
        // doing that, call addAddress on the second-to-top object on the
        // digester stack (a "Person" object), passing the top object on
        // the digester stack (the "Address" object). And also set things
        // up so that for each child xml element encountered between the start
        // of the address tag and the end of the address tag, the text
        // contained in that element is passed to a setXXX method on the
        // Address object where XXX is the name of the xml element found.
        d.addObjectCreate( "address-book/person/address", Address.class );
        d.addSetNext( "address-book/person/address", "addAddress" );
        d.addSetNestedProperties( "address-book/person/address" );
    }

    private static void usage()
    {
        System.out.println( "Usage: java Main example.xml" );
    }

}
