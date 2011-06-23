package org.apache.commons.digester3.substitution;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.commons.digester3.Substitutor;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

public final class CompoundSubstitutorTestCase
{


    private static class SubstitutorStub
        extends Substitutor
    {

        private String newBodyText;

        private String uri;

        private String localName;

        private String type;

        private String value;

        public SubstitutorStub( String bodyText, String uri, String localName, String type, String value )
        {
            this.newBodyText = bodyText;
            this.uri = uri;
            this.localName = localName;
            this.type = type;
            this.value = value;
        }

        /**
         * @see org.apache.commons.digester.Substitutor#substitute(org.xml.sax.Attributes)
         */
        @Override
        public Attributes substitute( Attributes attributes )
        {
            AttributesImpl attribs = new AttributesImpl( attributes );
            attribs.addAttribute( uri, localName, uri + ":" + localName, type, value );
            return attribs;
        }

        /**
         * @see org.apache.commons.digester.Substitutor#substitute(java.lang.String)
         */
        @Override
        public String substitute( String bodyText )
        {
            return newBodyText;
        }

    }

    private Attributes attrib;

    private String bodyText;

    @Before
    public void setUp()
    {
        AttributesImpl aImpl = new AttributesImpl();
        aImpl.addAttribute( "", "b", ":b", "", "bcd" );
        aImpl.addAttribute( "", "c", ":c", "", "cde" );
        aImpl.addAttribute( "", "d", ":d", "", "def" );

        attrib = aImpl;
        bodyText = "Amazing Body Text!";
    }

    @Test
    public void testConstructors()
    {
        try
        {
            new CompoundSubstitutor( null, null );
            fail();
        }
        catch ( IllegalArgumentException e )
        {
            // OK
        }

        Substitutor a = new SubstitutorStub( "XYZ", "", "a", "", "abc" );

        try
        {
            new CompoundSubstitutor( a, null );
            fail();
        }
        catch ( IllegalArgumentException e )
        {
            // OK
        }

        try
        {
            new CompoundSubstitutor( null, a );
            fail();
        }
        catch ( IllegalArgumentException e )
        {
            // OK
        }
    }

    @Test
    public void testChaining()
    {
        Substitutor a = new SubstitutorStub( "XYZ", "", "a", "", "abc" );
        Substitutor b = new SubstitutorStub( "STU", "", "b", "", "bcd" );

        Substitutor test = new CompoundSubstitutor( a, b );

        AttributesImpl attribFixture = new AttributesImpl( attrib );
        attribFixture.addAttribute( "", "a", ":a", "", "abc" );
        attribFixture.addAttribute( "", "b", ":b", "", "bcd" );

        assertTrue( areEqual( test.substitute( attrib ), attribFixture ) );
        assertEquals( test.substitute( bodyText ), "STU" );
    }

    private boolean areEqual( Attributes a, Attributes b )
    {
        if ( a.getLength() != b.getLength() )
        {
            return false;
        }

        boolean success = true;
        for ( int i = 0; i < a.getLength() && success; i++ )
        {
            success = a.getLocalName( i ).equals( b.getLocalName( i ) )
                    && a.getQName( i ).equals( b.getQName( i ) )
                    && a.getType( i ).equals( b.getType( i ) )
                    && a.getURI( i ).equals( b.getURI( i ) )
                    && a.getValue( i ).equals( b.getValue( i ) );
        }

        return success;
    }

}
