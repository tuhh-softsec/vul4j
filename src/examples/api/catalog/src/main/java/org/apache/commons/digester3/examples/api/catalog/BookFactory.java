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

import org.apache.commons.digester3.AbstractObjectCreationFactory;
import org.xml.sax.Attributes;

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
public class BookFactory
    extends AbstractObjectCreationFactory<Book>
{

    @Override
    public Book createObject( Attributes attributes )
        throws Exception
    {
        String isbn = attributes.getValue( "isbn" );

        if ( isbn == null )
        {
            throw new Exception( "Mandatory isbn attribute not present on book tag." );
        }

        return new Book( isbn );
    }

}
