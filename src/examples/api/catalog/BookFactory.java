/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//digester/src/examples/api/catalog/BookFactory.java,v 1.3 2003/10/09 21:09:45 rdonkin Exp $
 * $Revision: 1.3 $
 * $Date: 2003/10/09 21:09:45 $
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
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
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

import org.apache.commons.digester.AbstractObjectCreationFactory;

/**
 * The Book class doesn't have a no-argument constructor, so the
 * standard digester ObjectCreateRule can't be used to create instances
 * of it.
 * <p>
 * To resolve this issue, the FactoryCreateRule can be used in 
 * conjunction with an appropriate factory class, like this one.
 * The "createObject" method of the factory is invoked to generate
 * object instances when required.
 * <p>
 * The factory object can access any xml attributes, plus of course
 * any values set up within it before digester parsing starts (like
 * JNDI references, database connections, etc) that it may in the
 * process of generating an appropriate object.
 * <p>
 * Note that it is <i>not</i> possible for any data to be extracted
 * from the body or subelements of the xml element that caused the
 * createObject method on this factory to be invoked. For example:
 * <pre>
 *  [book isdn="12345"]
 * </pre>
 * is fine; the isdn value can be accessed during the createObject method.
 * However, given the xml:
 * <pre>
 * [book]
 *   [isdn]12345[/isdn]
 *   ...
 * </pre>
 * it is not possible to access the isdn number until after the
 * Book instance has been created.
 * <p>
 * Note that even if the class to be created does have a default constructor,
 * you may wish to use a factory class, in order to initialise the created
 * object in specific ways, or insert created objects into a central
 * register, etc.
 * <p>
 * And don't forget, either, that factories may be implemented as
 * inner classes or anonymous classes if appropriate, reducing the
 * overhead of using this functionality in many cases. 
 */
public class BookFactory extends AbstractObjectCreationFactory {
	
	public Object createObject(org.xml.sax.Attributes attributes) 
    throws Exception {
        String isbn = attributes.getValue("isbn");
        
        if (isbn == null) {
            throw new Exception(
                "Mandatory isbn attribute not present on book tag.");
        }
        
        return new Book(isbn);
	}
}
