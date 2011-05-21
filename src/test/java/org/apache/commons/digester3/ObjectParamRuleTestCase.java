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

import static org.apache.commons.digester3.binder.DigesterLoader.newLoader;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.apache.commons.digester3.binder.AbstractRulesModule;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * <p>
 * Tests for the <code>ObjectParamRuleTestCase</code>
 * 
 * @author Mark Huisman
 */
public class ObjectParamRuleTestCase
{

    // ------------------------------------------------ Individual Test Methods

    private StringBuilder sb =
        new StringBuilder().append( "<arraylist><A/><B/><C/><D desc=\"the fourth\"/><E/></arraylist>" );

    /**
     * Test method calls with the ObjectParamRule rule. It should be possible to pass any subclass of Object as a
     * parameter, provided that either the element or the element + attribute has been matched.
     */
    @Test
    public void testBasic()
        throws SAXException, IOException
    {
        Digester digester = newLoader( new AbstractRulesModule()
        {

            @Override
            protected void configure()
            {
                forPattern( "arraylist" ).createObject().ofType( ArrayList.class );
                forPattern( "arraylist/A" ).callMethod( "add" ).withParamCount( 1 )
                    .then()
                    .objectParam( new Integer( -9 ) );
                forPattern( "arraylist/B" ).callMethod( "add" ).withParamCount( 1 )
                    .then()
                    .objectParam( new Float( 3.14159 ) );
                forPattern( "arraylist/C" ).callMethod( "add" ).withParamCount( 1 )
                    .then()
                    .objectParam( new Long( 999999999 ) );
                forPattern( "arraylist/D" ).callMethod( "add" ).withParamCount( 1 )
                    .then()
                    .objectParam( new String( "foobarbazbing" ) ).matchingAttribute( "desc" );
                forPattern( "arraylist/E" ).callMethod( "add" ).withParamCount( 1 )
                    .then()
                    .objectParam( new String( "ignore" ) ).matchingAttribute( "nonexistentattribute" );
            }

        }).newDigester();

        // Parse it and obtain the ArrayList
        ArrayList<?> al = digester.parse( new StringReader( sb.toString() ) );
        assertNotNull( al );
        assertEquals( al.size(), 4 );
        assertTrue( al.contains( new Integer( -9 ) ) );
        assertTrue( al.contains( new Float( 3.14159 ) ) );
        assertTrue( al.contains( new Long( 999999999 ) ) );
        assertTrue( al.contains( new String( "foobarbazbing" ) ) );
        assertTrue( !( al.contains( new String( "ignore" ) ) ) );
    }

}
