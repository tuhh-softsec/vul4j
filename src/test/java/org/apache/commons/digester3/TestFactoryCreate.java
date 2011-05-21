/* $Id$
 *
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

package org.apache.commons.digester3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.digester3.AbstractObjectCreationFactory;
import org.apache.commons.digester3.Digester;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.xml.sax.Attributes;

/**
 * Test case for factory create rules.
 * 
 * @author Robert Burrell Donkin
 */
@RunWith(value = Parameterized.class)
public class TestFactoryCreate
{

    private final boolean ignoreCreateExceptions;

    public TestFactoryCreate( boolean ignoreCreateExceptions )
    {
        this.ignoreCreateExceptions = ignoreCreateExceptions;
    }

    @Parameters
    public static Collection<Object[]> data()
    {
        Collection<Object[]> data = new ArrayList<Object[]>(2);

        data.add( new Object[] { true } );
        data.add( new Object[] { false } );

        return data;
    }

    // --------------------------------------------------------------- Test cases

    @Test
    public void testPropagateException()
        throws Exception
    {

        // only used with this method
        class ThrowExceptionCreateRule
            extends AbstractObjectCreationFactory<Object>
        {
            @Override
            public Object createObject( Attributes attributes )
                throws Exception
            {
                throw new RuntimeException();
            }
        }

        // now for the tests
        String xml = "<?xml version='1.0' ?><root><element/></root>";

        // test default - which is to propagate the exception
        Digester digester = new Digester();
        digester.addFactoryCreate( "root", new ThrowExceptionCreateRule(), ignoreCreateExceptions );
        try
        {

            digester.parse( new StringReader( xml ) );
            if ( !ignoreCreateExceptions )
            {
                fail( "Exception should be propagated from create rule" );
            }

        }
        catch ( Exception e )
        {
            if ( ignoreCreateExceptions )
            {
                fail( "Exception should not be propagated" );
            }
        }
    }

    @Test
    public void testFactoryCreateRule()
        throws Exception
    {

        // test passing object create
        Digester digester = new Digester();
        ObjectCreationFactoryTestImpl factory = new ObjectCreationFactoryTestImpl();
        digester.addFactoryCreate( "root", factory, ignoreCreateExceptions );
        String xml = new String( "<?xml version='1.0' ?><root one='good' two='bad' three='ugly'><element/></root>" );
        digester.parse( new StringReader( xml ) );

        assertEquals( "Object create not called(1)[" + ignoreCreateExceptions + "]", factory.called, true );
        assertEquals( "Attribute not passed (1)[" + ignoreCreateExceptions + "]", factory.attributes.getValue( "one" ),
                      "good" );
        assertEquals( "Attribute not passed (2)[" + ignoreCreateExceptions + "]", factory.attributes.getValue( "two" ),
                      "bad" );
        assertEquals( "Attribute not passed (3)[" + ignoreCreateExceptions + "]", factory.attributes.getValue( "three" ),
                      "ugly" );

        digester = new Digester();
        digester.addFactoryCreate( "root", "org.apache.commons.digester3.ObjectCreationFactoryTestImpl",
                                   ignoreCreateExceptions );
        digester.addSetNext( "root", "add" );
        xml = new String( "<?xml version='1.0' ?><root one='good' two='bad' three='ugly'><element/></root>" );
        List<ObjectCreationFactoryTestImpl> list = new ArrayList<ObjectCreationFactoryTestImpl>();
        digester.push( list );
        digester.parse( new StringReader( xml ) );

        assertEquals( "List should contain only the factory object", list.size(), 1 );
        factory = list.get( 0 );
        assertEquals( "Object create not called(2)[" + ignoreCreateExceptions + "]", factory.called, true );
        assertEquals( "Attribute not passed (4)[" + ignoreCreateExceptions + "]", factory.attributes.getValue( "one" ),
                      "good" );
        assertEquals( "Attribute not passed (5)[" + ignoreCreateExceptions + "]", factory.attributes.getValue( "two" ),
                      "bad" );
        assertEquals( "Attribute not passed (6)[" + ignoreCreateExceptions + "]", factory.attributes.getValue( "three" ),
                      "ugly" );

        digester = new Digester();
        digester.addFactoryCreate( "root", "org.apache.commons.digester3.ObjectCreationFactoryTestImpl", "override",
                                   ignoreCreateExceptions );
        digester.addSetNext( "root", "add" );
        xml = new String( "<?xml version='1.0' ?><root one='good' two='bad' three='ugly'><element/></root>" );
        list = new ArrayList<ObjectCreationFactoryTestImpl>();
        digester.push( list );
        digester.parse( new StringReader( xml ) );

        assertEquals( "List should contain only the factory object", list.size(), 1 );
        factory = list.get( 0 );
        assertEquals( "Object create not called(3)[" + ignoreCreateExceptions + "]", factory.called, true );
        assertEquals( "Attribute not passed (7)[" + ignoreCreateExceptions + "]", factory.attributes.getValue( "one" ),
                      "good" );
        assertEquals( "Attribute not passed (8)[" + ignoreCreateExceptions + "]", factory.attributes.getValue( "two" ),
                      "bad" );
        assertEquals( "Attribute not passed (8)[" + ignoreCreateExceptions + "]", factory.attributes.getValue( "three" ),
                      "ugly" );

        digester = new Digester();
        digester.addFactoryCreate( "root", "org.apache.commons.digester3.ObjectCreationFactoryTestImpl", "override",
                                   ignoreCreateExceptions );
        digester.addSetNext( "root", "add" );
        xml =
            new String( "<?xml version='1.0' ?><root one='good' two='bad' three='ugly' "
                + " override='org.apache.commons.digester3.OtherTestObjectCreationFactory' >" + "<element/></root>" );
        list = new ArrayList<ObjectCreationFactoryTestImpl>();
        digester.push( list );
        digester.parse( new StringReader( xml ) );

        assertEquals( "List should contain only the factory object", list.size(), 1 );
        factory = list.get( 0 );
        assertEquals( "Attribute Override Failed (1)", factory.getClass().getName(),
                      "org.apache.commons.digester3.OtherTestObjectCreationFactory" );
        assertEquals( "Object create not called(4)[" + ignoreCreateExceptions + "]", factory.called, true );
        assertEquals( "Attribute not passed (10)[" + ignoreCreateExceptions + "]", factory.attributes.getValue( "one" ),
                      "good" );
        assertEquals( "Attribute not passed (11)[" + ignoreCreateExceptions + "]", factory.attributes.getValue( "two" ),
                      "bad" );
        assertEquals( "Attribute not passed (12)[" + ignoreCreateExceptions + "]", factory.attributes.getValue( "three" ),
                      "ugly" );

        digester = new Digester();
        digester.addFactoryCreate( "root", ObjectCreationFactoryTestImpl.class, "override", ignoreCreateExceptions );
        digester.addSetNext( "root", "add" );
        xml = new String( "<?xml version='1.0' ?><root one='good' two='bad' three='ugly'><element/></root>" );
        list = new ArrayList<ObjectCreationFactoryTestImpl>();
        digester.push( list );
        digester.parse( new StringReader( xml ) );

        assertEquals( "List should contain only the factory object", list.size(), 1 );
        factory = list.get( 0 );
        assertEquals( "Object create not called(5)[" + ignoreCreateExceptions + "]", factory.called, true );
        assertEquals( "Attribute not passed (13)[" + ignoreCreateExceptions + "]", factory.attributes.getValue( "one" ),
                      "good" );
        assertEquals( "Attribute not passed (14)[" + ignoreCreateExceptions + "]", factory.attributes.getValue( "two" ),
                      "bad" );
        assertEquals( "Attribute not passed (15)[" + ignoreCreateExceptions + "]", factory.attributes.getValue( "three" ),
                      "ugly" );

        digester = new Digester();
        digester.addFactoryCreate( "root", ObjectCreationFactoryTestImpl.class, "override", ignoreCreateExceptions );
        digester.addSetNext( "root", "add" );
        xml =
            new String( "<?xml version='1.0' ?><root one='good' two='bad' three='ugly' "
                + " override='org.apache.commons.digester3.OtherTestObjectCreationFactory' >" + "<element/></root>" );
        list = new ArrayList<ObjectCreationFactoryTestImpl>();
        digester.push( list );
        digester.parse( new StringReader( xml ) );

        assertEquals( "List should contain only the factory object", list.size(), 1 );
        factory = list.get( 0 );
        assertEquals( "Attribute Override Failed (2)", factory.getClass().getName(),
                      "org.apache.commons.digester3.OtherTestObjectCreationFactory" );
        assertEquals( "Object create not called(6)[" + ignoreCreateExceptions + "]", factory.called, true );
        assertEquals( "Attribute not passed (16)[" + ignoreCreateExceptions + "]", factory.attributes.getValue( "one" ),
                      "good" );
        assertEquals( "Attribute not passed (17)[" + ignoreCreateExceptions + "]", factory.attributes.getValue( "two" ),
                      "bad" );
        assertEquals( "Attribute not passed (18)[" + ignoreCreateExceptions + "]", factory.attributes.getValue( "three" ),
                      "ugly" );
    }
}
