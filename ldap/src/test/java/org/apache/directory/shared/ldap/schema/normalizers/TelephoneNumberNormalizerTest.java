/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package org.apache.directory.shared.ldap.schema.normalizers;

import org.apache.directory.junit.tools.Concurrent;
import org.apache.directory.junit.tools.ConcurrentJunitRunner;
import org.apache.directory.shared.ldap.exception.LdapException;
import org.apache.directory.shared.ldap.schema.Normalizer;
import org.apache.directory.shared.ldap.schema.normalizers.TelephoneNumberNormalizer;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;


/**
 * Test the Telephone Number normalizer class
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent()
public class TelephoneNumberNormalizerTest
{
    @Test
    public void testTelephoneNumberNormalizerNull() throws LdapException
    {
        Normalizer normalizer = new TelephoneNumberNormalizer();
        assertEquals( "", normalizer.normalize( ( String ) null ) );
    }


    @Test
    public void testTelephoneNumberNormalizerEmpty() throws LdapException
    {
        Normalizer normalizer = new TelephoneNumberNormalizer();
        assertEquals( "", normalizer.normalize( "" ) );
    }


    @Test
    public void testTelephoneNumberNormalizerOneSpace() throws LdapException
    {
        Normalizer normalizer = new TelephoneNumberNormalizer();
        assertEquals( "", normalizer.normalize( " " ) );
    }


    @Test
    public void testTelephoneNumberNormalizerTwoSpaces() throws LdapException
    {
        Normalizer normalizer = new TelephoneNumberNormalizer();
        assertEquals( "", normalizer.normalize( "  " ) );
    }


    @Test
    public void testTelephoneNumberNormalizerNSpaces() throws LdapException
    {
        Normalizer normalizer = new TelephoneNumberNormalizer();
        assertEquals( "", normalizer.normalize( "      " ) );
    }


    @Test
    public void testTelephoneNumberNormalizerOneHyphen() throws LdapException
    {
        Normalizer normalizer = new TelephoneNumberNormalizer();
        assertEquals( "", normalizer.normalize( "-" ) );
    }


    @Test
    public void testTelephoneNumberNormalizerTwoHyphen() throws LdapException
    {
        Normalizer normalizer = new TelephoneNumberNormalizer();
        assertEquals( "", normalizer.normalize( "--" ) );
    }


    @Test
    public void testTelephoneNumberNormalizerHyphensSpaces() throws LdapException
    {
        Normalizer normalizer = new TelephoneNumberNormalizer();
        assertEquals( "", normalizer.normalize( " -- - -- " ) );
    }


    @Test
    public void testInsignifiantSpacesStringOneChar() throws LdapException
    {
        Normalizer normalizer = new TelephoneNumberNormalizer();
        assertEquals( "1", normalizer.normalize( "1" ) );
    }


    @Test
    public void testInsignifiantSpacesStringTwoChars() throws LdapException
    {
        Normalizer normalizer = new TelephoneNumberNormalizer();
        assertEquals( "11", normalizer.normalize( "11" ) );
    }


    @Test
    public void testInsignifiantSpacesStringNChars() throws LdapException
    {
        Normalizer normalizer = new TelephoneNumberNormalizer();
        assertEquals( "123456", normalizer.normalize( "123456" ) );
    }


    @Test
    public void testInsignifiantTelephoneNumberCharsSpaces() throws LdapException
    {
        Normalizer normalizer = new TelephoneNumberNormalizer();
        assertEquals( "1", normalizer.normalize( " 1" ) );
        assertEquals( "1", normalizer.normalize( "1 " ) );
        assertEquals( "1", normalizer.normalize( " 1 " ) );
        assertEquals( "11", normalizer.normalize( "1 1" ) );
        assertEquals( "11", normalizer.normalize( " 1 1" ) );
        assertEquals( "11", normalizer.normalize( "1 1 " ) );
        assertEquals( "11", normalizer.normalize( "1  1" ) );
        assertEquals( "11", normalizer.normalize( " 1   1 " ) );
        assertEquals( "123456789", normalizer.normalize( "  123   456   789  " ) );
        assertEquals( "1", normalizer.normalize( "-1" ) );
        assertEquals( "1", normalizer.normalize( "1-" ) );
        assertEquals( "1", normalizer.normalize( "-1-" ) );
        assertEquals( "11", normalizer.normalize( "1-1" ) );
        assertEquals( "11", normalizer.normalize( "-1-1" ) );
        assertEquals( "11", normalizer.normalize( "1-1-" ) );
        assertEquals( "11", normalizer.normalize( "1--1" ) );
        assertEquals( "11", normalizer.normalize( "-1---1-" ) );
        assertEquals( "1(2)+3456789", normalizer.normalize( "---1(2)+3   456-  789 --" ) );
    }
}