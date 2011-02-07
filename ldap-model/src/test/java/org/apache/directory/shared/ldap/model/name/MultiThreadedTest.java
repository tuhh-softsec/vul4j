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
package org.apache.directory.shared.ldap.model.name;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import com.mycila.junit.concurrent.Concurrency;
import com.mycila.junit.concurrent.ConcurrentJunitRunner;
import org.apache.directory.junit.tools.MultiThreadedMultiInvoker;
import org.apache.directory.shared.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.model.schema.normalizers.DeepTrimToLowerNormalizer;
import org.apache.directory.shared.ldap.model.schema.normalizers.OidNormalizer;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Multi-threaded 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrency()
public class MultiThreadedTest
{
    @Rule
    public MultiThreadedMultiInvoker i = new MultiThreadedMultiInvoker( 100, 1000 );

    private static Map<String, OidNormalizer> oidsMap;

    private static Dn referenceDn;
    private static Dn sharedDn;
    private static Rdn referenceRdn;
    private static Rdn sharedRdn;
    private static Ava referenceAva;
    private static Ava sharedAva;


    @BeforeClass
    public static void initMapOids() throws LdapInvalidDnException
    {
        // Another map where we store OIDs instead of names.
        oidsMap = new HashMap<String, OidNormalizer>();
        oidsMap.put( "dc", new OidNormalizer( "0.9.2342.19200300.100.1.25", new DeepTrimToLowerNormalizer() ) );
        oidsMap.put( "domaincomponent", new OidNormalizer( "0.9.2342.19200300.100.1.25",
            new DeepTrimToLowerNormalizer() ) );
        oidsMap.put( "0.9.2342.19200300.100.1.25", new OidNormalizer( "0.9.2342.19200300.100.1.25",
            new DeepTrimToLowerNormalizer() ) );
        oidsMap.put( "ou", new OidNormalizer( "2.5.4.11", new DeepTrimToLowerNormalizer() ) );
        oidsMap.put( "organizationalUnitName", new OidNormalizer( "2.5.4.11", new DeepTrimToLowerNormalizer() ) );
        oidsMap.put( "2.5.4.11", new OidNormalizer( "2.5.4.11", new DeepTrimToLowerNormalizer() ) );

        referenceDn = new Dn( "dc=example,dc=com" );
        referenceDn.normalize( oidsMap );
        sharedDn = new Dn( "dc=example,dc=com" );
        sharedDn.normalize( oidsMap );

        referenceRdn = new Rdn( "ou=system" );
        referenceRdn.normalize( oidsMap );
        sharedRdn = new Rdn( "ou=system" );
        sharedRdn.normalize( oidsMap );

        referenceAva = new Ava( "ou", "2.5.4.11", "System", "system" );
        referenceAva.normalize();
        sharedAva = new Ava( "ou", "2.5.4.11", "System", "system" );
        sharedAva.normalize();
    }


    @Test
    public void testNormalize() throws Exception
    {
        sharedAva.normalize();

        sharedRdn.normalize( oidsMap );
        assertTrue( sharedRdn.isNormalized() );

        sharedDn.normalize( oidsMap );
        assertTrue( sharedDn.isNormalized() );
    }


    @Test
    public void testNormalizeHashCode() throws Exception
    {
        sharedAva.normalize();
        assertEquals( referenceAva.hashCode(), sharedAva.hashCode() );

        sharedRdn.normalize( oidsMap );
        assertEquals( referenceRdn.hashCode(), sharedRdn.hashCode() );

        sharedDn.normalize( oidsMap );
        assertEquals( referenceDn.hashCode(), sharedDn.hashCode() );
    }


    @Test
    public void testNormalizeEquals() throws Exception
    {
        sharedAva.normalize();
        assertEquals( referenceAva, sharedAva );
        assertTrue( referenceAva.equals( sharedAva ) );
        assertTrue( sharedAva.equals( referenceAva ) );

        sharedRdn.normalize( oidsMap );
        assertEquals( referenceRdn, sharedRdn );
        assertTrue( referenceRdn.equals( sharedRdn ) );
        assertTrue( sharedRdn.equals( referenceRdn ) );

        sharedDn.normalize( oidsMap );
        assertEquals( referenceDn, sharedDn );
        assertTrue( referenceDn.equals( sharedDn ) );
        assertTrue( sharedDn.equals( referenceDn ) );
    }


    @Test
    public void testNormalizeCompare() throws Exception
    {
        sharedAva.normalize();
        assertEquals( 0, sharedAva.compareTo( referenceAva ) );
        assertEquals( 0, referenceAva.compareTo( sharedAva ) );

        sharedRdn.normalize( oidsMap );
        assertEquals( 0, referenceRdn.compareTo( sharedRdn ) );
        assertEquals( 0, sharedRdn.compareTo( referenceRdn ) );

        sharedDn.normalize( oidsMap );
        assertEquals( 0, referenceDn.compareTo( sharedDn ) );
        assertEquals( 0, sharedDn.compareTo( referenceDn ) );
    }

}
