package org.apache.commons.digester3.examples.api.dbinsert;

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

import java.io.File;
import java.io.IOException;
import java.sql.Connection;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXException;

/**
 * A simple program to demonstrate that the Commons Digester module can be
 * used to trigger actions as the xml is parsed, rather than just build
 * up in-memory representations of the parsed data. This example also shows
 * how to write a custom Rule class.
 * <p>
 * This code will parse the provided "example.xml" file, and immediately 
 * insert the processed data into a database as each row tag is parsed,
 * instead of building up an in-memory representation. Actually, in order 
 * to keep this example simple and easy to run, sql insert statements are 
 * printed out rather than actually performing database inserts, but the 
 * principle remains.
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
     * Usage: java Main example.xml
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

        // Here you would establish a real connection.
        // There would also be a finally clause to ensure it is
        // closed after parsing terminates, etc.
        Connection connection = null;

        // Add rules to the digester that will be triggered while
        // parsing occurs.
        addRules( d, connection );

        // Process the input file.
        System.out.println( "Parsing commencing..." );
        try
        {
            File srcfile = new File( filename );
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

        // And here there is nothing to do. The digester rules have
        // (deliberately) not built a representation of the input, but
        // instead processed the data as it was read.
        System.out.println( "Parsing complete." );
    }

    private static void addRules( Digester d, java.sql.Connection conn )
    {

        // --------------------------------------------------
        // when we encounter a "table" tag, do the following:

        // Create a new instance of class Table, and push that
        // object onto the digester stack of objects. We only need
        // this so that when a row is inserted, it can find out what
        // the enclosing tablename was.
        //
        // Note that the object is popped off the stack at the end of the
        // "table" tag (normal behaviour for ObjectCreateRule). Because we
        // never added the table object to some parent object, when it is
        // popped off the digester stack it becomes garbage-collected. That
        // is fine in this situation; we've done all the necessary work and
        // don't need the table object any more.
        d.addObjectCreate( "database/table", Table.class );

        // Map *any* attributes on the table tag to appropriate
        // setter-methods on the top object on the stack (the Table
        // instance created by the preceeding rule). We only expect one
        // attribute, though: a 'name' attribute specifying what table
        // we are inserting rows into.
        d.addSetProperties( "database/table" );

        // --------------------------------------------------
        // When we encounter a "row" tag, invoke methods on the provided
        // RowInserterRule instance.
        //
        // This rule creates a Row instance and pushes it on the digester
        // object stack, rather like ObjectCreateRule, so that the column
        // tags have somewhere to store their information. And when the
        // </row> end tag is found, the rule will trigger to remove this
        // object from the stack, and also do an actual database insert.
        //
        // Note that the rule instance we are passing to the digester has
        // been initialised with some useful data (the SQL connection).
        //
        // Note also that in this case we are not using the digester's
        // factory methods to create the rule instance; that's just a
        // convenience - and obviously not an option for Rule classes
        // that are not part of the digester core implementation.
        RowInserterRule rowInserterRule = new RowInserterRule( conn );
        d.addRule( "database/table/row", rowInserterRule );

        // --------------------------------------------------
        // when we encounter a "column" tag, call setColumn on the top
        // object on the stack, passing two parameters: the "name"
        // attribute, and the text within the tag body.
        d.addCallMethod( "database/table/row/column", "addColumn", 2 );
        d.addCallParam( "database/table/row/column", 0, "name" );
        d.addCallParam( "database/table/row/column", 1 );
    }

    private static void usage()
    {
        System.out.println( "Usage: java Main example.xml" );
    }

}
