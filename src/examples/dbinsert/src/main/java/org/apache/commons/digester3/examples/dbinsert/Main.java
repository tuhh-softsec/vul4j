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
package org.apache.commons.digester3.examples.dbinsert;

import static org.apache.commons.digester3.DigesterLoader.newLoader;

import java.io.File;
import java.io.IOException;

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
public class Main {
    
    /**
     * Main method : entry point for running this example program.
     * <p>
     * Usage: java Main example.xml
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            usage();
            System.exit(-1);
        }

        String filename = args[0];

        // Create a Digester instance
        Digester d = newLoader(new DBInsertModule()).newDigester();

        // Process the input file.
        System.out.println("Parsing commencing...");
        try {
            File srcfile = new File(filename);
            d.parse(srcfile);
        } catch (IOException ioe) {
            System.out.println("Error reading input file:" + ioe.getMessage());
            System.exit(-1);
        } catch (SAXException se) {
            System.out.println("Error parsing input file:" + se.getMessage());
            System.exit(-1);
        }

        // And here there is nothing to do. The digester rules have
        // (deliberately) not built a representation of the input, but
        // instead processed the data as it was read.
        System.out.println("Parsing complete.");
    }

    private static void usage() {
        System.out.println("Usage: java Main example.xml");
    }

}
