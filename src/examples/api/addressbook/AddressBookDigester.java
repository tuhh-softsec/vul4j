/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//digester/src/examples/api/addressbook/Attic/AddressBookDigester.java,v 1.2 2003/10/05 15:21:25 rdonkin Exp $
 * $Revision: 1.2 $
 * $Date: 2003/10/05 15:21:25 $
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
 * Usage: java Example1 example.xml
 *
 * @author Simon Kitching
 */
public class AddressBookDigester {
    
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
        Digester d = new Digester();
        
        // Prime the digester stack with an object for rules to
        // operate on. Note that it is quite common for "this"
        // to be the object pushed.
        AddressBook book = new AddressBook();
        d.push(book);
        
        // Add rules to the digester that will be triggered while
        // parsing occurs.
        addRules(d);
        
        // Process the input file.
        try {
            java.io.File srcfile = new java.io.File(filename);
            d.parse(srcfile);
        }
        catch(java.io.IOException ioe) {
            System.out.println("Error reading input file:" + ioe.getMessage());
            System.exit(-1);
        }
        catch(org.xml.sax.SAXException se) {
            System.out.println("Error parsing input file:" + se.getMessage());
            System.exit(-1);
        }
        
        
        // Print out all the contents of the address book, as loaded from
        // the input file.
        book.print();
    }
    
    private static void addRules(Digester d) {

        //--------------------------------------------------        
        // when we encounter a "person" tag, do the following:

        // create a new instance of class Person, and push that
        // object onto the digester stack of objects
        d.addObjectCreate("address-book/person", Person.class);
        
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
        d.addSetProperties("address-book/person");

        // call the addPerson method on the second-to-top object on
        // the stack (the AddressBook object), passing the top object
        // on the stack (the recently created Person object).
        d.addSetNext("address-book/person", "addPerson");        
        
        //--------------------------------------------------        
        // when we encounter a "name" tag, call setName on the top
        // object on the stack, passing the text contained within the
        // body of that name element [specifying a zero parameter count
        // implies one actual parameter, being the body text]. 
        // The top object on the stack will be a person object, because 
        // the pattern address-book/person always triggers the 
        // ObjectCreateRule we added previously.
        d.addCallMethod("address-book/person/name", "setName", 0);
        
        //--------------------------------------------------        
        // when we encounter an "email" tag, call addEmail on the top
        // object on the stack, passing two parameters: the "type"
        // attribute, and the text within the tag body.
        d.addCallMethod("address-book/person/email", "addEmail", 2);
        d.addCallParam("address-book/person/email", 0, "type");
        d.addCallParam("address-book/person/email", 1);
    }

    private static void usage() {
        System.out.println("Usage: java Example1 example.xml");
    }
}