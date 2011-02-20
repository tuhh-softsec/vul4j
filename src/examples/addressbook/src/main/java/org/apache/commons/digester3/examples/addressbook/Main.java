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
package org.apache.commons.digester3.examples.addressbook;

import static org.apache.commons.digester3.DigesterLoader.newLoader;

import java.io.File;
import java.io.IOException;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXException;

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
public class Main {

    /**
     * Main method : entry point for running this example program.
     * <p>
     * Usage: java Example example.xml
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            usage();
            System.exit(-1);
        }

        String filename = args[0];

        // Create a Digester instance
        Digester d = newLoader(new AddressBookModule()).newDigester();

        // Prime the digester stack with an object for rules to
        // operate on. Note that it is quite common for "this"
        // to be the object pushed.
        AddressBook book = new AddressBook();
        d.push(book);

        // Process the input file.
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

        // Print out all the contents of the address book, as loaded from
        // the input file.
        book.print();
    }

    private static void usage() {
        System.out.println("Usage: java Main example.xml");
    }

}
