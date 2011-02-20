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
package org.apache.commons.digester3.examples.catalog;

import static org.apache.commons.digester3.DigesterLoader.newLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXException;

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
public class Main {

    /**
     * Main method : entry point for running this example program.
     * <p>
     * Usage: java CatalogDigester example.xml
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            usage();
            System.exit(-1);
        }

        String filename = args[0];

        // Create a Digester instance
        Digester d = newLoader(new CatalogModel()).newDigester();

        // Process the input file.
        try {
            Reader reader = getInputData(filename);
            d.parse(reader);
        } catch (IOException ioe) {
            System.out.println("Error reading input file:" + ioe.getMessage());
            System.exit(-1);
        } catch (SAXException se) {
            System.out.println("Error parsing input file:" + se.getMessage());
            System.exit(-1);
        }

        // Get the first object created by the digester's rules
        // (the "root" object). Note that this is exactly the same object
        // returned by the Digester.parse method; either approach works.
        Catalog catalog = (Catalog) d.getRoot();
        
        // Print out all the contents of the catalog, as loaded from
        // the input file.
        catalog.print();
    }

    /*
     * Reads the specified file into memory, and returns a StringReader
     * object which reads from that in-memory buffer.
     * <p>
     * This method exists just to demonstrate that the input to the
     * digester doesn't need to be from a file; for example, xml could
     * be read from a database or generated dynamically; any old buffer
     * in memory can be processed by the digester.
     * <p>
     * Clearly, if the data is always coming from a file, then calling
     * the Digester.parse method that takes a File object would be
     * more sensible (see AddressBook example).
     */
    private static Reader getInputData(String filename) throws IOException {
        File srcfile = new File(filename);

        ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
        byte[] buf = new byte[100];
        FileInputStream fis = new FileInputStream(srcfile);
        for(;;) {
            int nread = fis.read(buf);
            if (nread == -1) {
                break;
            }
            baos.write(buf, 0, nread);
        }
        fis.close();

        return new StringReader( baos.toString() );
    }

    private static void usage() {
        System.out.println("Usage: java Main example.xml");
    }

}
