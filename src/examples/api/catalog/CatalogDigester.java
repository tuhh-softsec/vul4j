/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//digester/src/examples/api/catalog/Attic/CatalogDigester.java,v 1.2 2003/10/05 15:21:36 rdonkin Exp $
 * $Revision: 1.2 $
 * $Date: 2003/10/05 15:21:36 $
 *
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgement:  
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "Apache", "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache" nor may "Apache" appear in their names without prior 
 *    written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */ 

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.AbstractObjectCreationFactory;
import org.apache.commons.digester.ExtendedBaseRules;

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
 * Usage: java CatalogDigester example.xml
 *
 * @author Simon Kitching
 */
public class CatalogDigester {
    
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
        Digester d = new Digester();
        
        // some of the rules use "extended pattern matching", so we
        // need to configure the digester with the corresponding
        // pattern-matching implementation.
        d.setRules( new ExtendedBaseRules() );

        // Add rules to the digester that will be triggered while
        // parsing occurs.
        addRules(d);
        
        // Process the input file.
        try {
            java.io.Reader reader = getInputData(filename);
            d.parse(reader);
        }
        catch(java.io.IOException ioe) {
            System.out.println("Error reading input file:" + ioe.getMessage());
            System.exit(-1);
        }
        catch(org.xml.sax.SAXException se) {
            System.out.println("Error parsing input file:" + se.getMessage());
            System.exit(-1);
        }

        // get the first object created by the digester's rules
        // (the "root" object).
        Catalog catalog = (Catalog) d.getRoot();
        
        // Print out all the contents of the catalog, as loaded from
        // the input file.
        catalog.print();
    }
    
    private static void addRules(Digester d) {

        //--------------------------------------------------

        // when we encounter the root "catalog" tag, create an
        // instance of the Catalog class. 
        //
        // Note that this approach is different from the approach taken in 
        // the AddressBook example, where an initial "root" object was 
        // explicitly created and pushed onto the digester stack before 
        // parsing started instead
        //
        // Either approach is fine.
        
        d.addObjectCreate("catalog", Catalog.class);
        
        //--------------------------------------------------

        // when we encounter a book tag, we want to create a Book
        // instance. However the Book class doesn't have a default
        // constructor (one with no arguments), so we can't use
        // the ObjectCreateRule. Instead, we use the FactoryCreateRule.
        
        BookFactory factory = new BookFactory();
        d.addFactoryCreate("catalog/book", factory);
        
        // and add the book to the parent catalog object (which is
        // the next-to-top object on the digester object stack).
        d.addSetNext("catalog/book", "addItem");
        
        // we want each subtag of book to map the text contents of
        // the tag into a bean property with the same name as the tag.
        // eg <title>foo</title> --> setTitle("foo")
        //
        // In order to use the wildcard "?" match, an ExtendedBaseRules
        // instance must be set as the digester's rule matcher.
        
        d.addBeanPropertySetter("catalog/book/?");
        
        
        //-----------------------------------------------
        
        // We are using the "AudioVisual" class to represent both
        // dvds and videos, so when the "dvd" tag is encountered,
        // create an AudioVisual object.
        
        d.addObjectCreate("catalog/dvd", AudioVisual.class);
        
        // add this dvd to the parent catalog object
        
        d.addSetNext("catalog/dvd", "addItem");
        
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
        
        d.addSetProperties("catalog/dvd", "year-made", "yearMade");
        
        // We also need to tell this AudioVisual object that it is actually
        // a dvd; we can use the ObjectParamRule to pass a string to any
        // method. This usage is a little artificial - normally in this
        // situation there would be separate Dvd and Video classes.
        // Note also that equivalent behaviour could be implemented by
        // using factory objects to create & initialise the AudioVisual
        // objects with their type rather than using ObjectCreateRule.
        
        d.addCallMethod("catalog/dvd", "setType", 1);
        d.addObjectParam("catalog/dvd", 0, "dvd"); // pass literal "dvd" string
        
        // Each tag of form "<attr id="foo" value="bar"/> needs to map
        // to a call to setFoo("bar").
        //
        // This is an alternative to the BeanPropertySetter syntax of
        // having the tag name indicate the attribute to set. It has
        // advantages (like not requiring ExtendedBaseRules support),
        // and disadvantages. It is commonly used with the FactoryCreateRule
        // variant which allows the target class to be created to be
        // specified in an xml attribute; this feature of FactoryCreateRule
        // is not demonstrated in this example, but see the Apache Tomcat
        // configuration files for an example of this usage.
        //
        // Note that despite the name similarity, there is no link
        // between SetPropertyRule and SetPropertiesRule.
        
        d.addSetProperty("catalog/dvd/attr", "id", "value");
        
        //-----------------------------------------------
        
        // and here we repeat the dvd rules, but for the video tag.
        d.addObjectCreate("catalog/video", AudioVisual.class);
        d.addSetNext("catalog/video", "addItem");
        d.addSetProperties("catalog/video", "year-made", "yearMade");
        d.addCallMethod("catalog/video", "setType", 1);
        d.addObjectParam("catalog/video", 0, "video");
        d.addSetProperty("catalog/video/attr", "id", "value");
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
    private static java.io.Reader getInputData(String filename) 
    throws java.io.IOException {
        java.io.File srcfile = new java.io.File(filename);
        
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream(1000);
        byte[] buf = new byte[100];
        java.io.FileInputStream fis = new java.io.FileInputStream(srcfile);
        for(;;) {
            int nread = fis.read(buf);
            if (nread == -1) {
                break;
            }
            baos.write(buf, 0, nread);
        }
        fis.close();
        
        return new java.io.StringReader( baos.toString() );
        
    }
    
    private static void usage() {
        System.out.println("Usage: java CatalogDigester example.xml");
    }
}