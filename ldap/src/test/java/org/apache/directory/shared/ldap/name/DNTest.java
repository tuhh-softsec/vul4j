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
package org.apache.directory.shared.ldap.name;


import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;

import org.apache.directory.junit.tools.Concurrent;
import org.apache.directory.junit.tools.ConcurrentJunitRunner;
import org.apache.directory.shared.ldap.exception.LdapException;
import org.apache.directory.shared.ldap.schema.normalizers.DeepTrimToLowerNormalizer;
import org.apache.directory.shared.ldap.schema.normalizers.OidNormalizer;
import org.apache.directory.shared.ldap.util.StringTools;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test the class DN
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent()
public class DNTest
{
    private Map<String, OidNormalizer> oids;
    private Map<String, OidNormalizer> oidOids;


    /**
     * Initialize OIDs maps for normalization
     */
    @Before
    public void initMapOids()
    {
        oids = new HashMap<String, OidNormalizer>();

        oids.put( "dc", new OidNormalizer( "dc", new DeepTrimToLowerNormalizer() ) );
        oids.put( "domaincomponent", new OidNormalizer( "dc", new DeepTrimToLowerNormalizer() ) );
        oids.put( "0.9.2342.19200300.100.1.25", new OidNormalizer( "dc", new DeepTrimToLowerNormalizer() ) );

        oids.put( "ou", new OidNormalizer( "ou", new DeepTrimToLowerNormalizer() ) );
        oids.put( "organizationalUnitName", new OidNormalizer( "ou", new DeepTrimToLowerNormalizer() ) );
        oids.put( "2.5.4.11", new OidNormalizer( "ou", new DeepTrimToLowerNormalizer() ) );

        // Another map where we store OIDs instead of names.
        oidOids = new HashMap<String, OidNormalizer>();

        oidOids.put( "dc", new OidNormalizer( "0.9.2342.19200300.100.1.25", new DeepTrimToLowerNormalizer() ) );
        oidOids.put( "domaincomponent", new OidNormalizer( "0.9.2342.19200300.100.1.25",
            new DeepTrimToLowerNormalizer() ) );
        oidOids.put( "0.9.2342.19200300.100.1.25", new OidNormalizer( "0.9.2342.19200300.100.1.25",
            new DeepTrimToLowerNormalizer() ) );
        oidOids.put( "ou", new OidNormalizer( "2.5.4.11", new DeepTrimToLowerNormalizer() ) );
        oidOids.put( "organizationalUnitName", new OidNormalizer( "2.5.4.11", new DeepTrimToLowerNormalizer() ) );
        oidOids.put( "2.5.4.11", new OidNormalizer( "2.5.4.11", new DeepTrimToLowerNormalizer() ) );
    }


    // ~ Methods
    // ------------------------------------------------------------------------------------
    // CONSTRUCTOR functions --------------------------------------------------

    /**
     * Test a null DN
     */
    @Test
    public void testDnNull()
    {
        DN dn = new DN();
        assertEquals( "", dn.getName() );
        assertEquals( "", dn.getNormName() );
        assertTrue( dn.isEmpty() );
    }


    /**
     * test an empty DN
     */
    @Test
    public void testDnEmpty() throws LdapException
    {
        DN dn = new DN( "" );
        assertEquals( "", dn.getName() );
        assertTrue( dn.isEmpty() );
    }


    /**
     * test a simple DN : a = b
     */
    @Test
    public void testDnSimple() throws LdapException
    {
        DN dn = new DN( "a = b" );

        assertTrue( DN.isValid( "a = b" ) );
        assertEquals( "a = b", dn.getName() );
        assertEquals( "a=b", dn.getNormName() );
    }


    /**
     * test a simple DN with some spaces : "a = b  "
     */
    @Test
    public void testDnSimpleWithSpaces() throws LdapException
    {
        DN dn = new DN( "a = b  " );

        assertTrue( DN.isValid( "a = b  " ) );
        assertEquals( "a = b  ", dn.getName() );
        assertEquals( "a=b", dn.getNormName() );
    }


    /**
     * test a composite DN : a = b, d = e
     */
    @Test
    public void testDnComposite() throws LdapException
    {
        DN dn = new DN( "a = b, c = d" );

        assertTrue( DN.isValid( "a = b, c = d" ) );
        assertEquals( "a=b,c=d", dn.getNormName() );
        assertEquals( "a = b, c = d", dn.getName() );
    }


    /**
     * test a composite DN with spaces : a = b  , d = e
     */
    @Test
    public void testDnCompositeWithSpaces() throws LdapException
    {
        DN dn = new DN( "a = b  , c = d" );

        assertTrue( DN.isValid( "a = b  , c = d" ) );
        assertEquals( "a=b,c=d", dn.getNormName() );
        assertEquals( "a = b  , c = d", dn.getName() );
    }


    /**
     * test a composite DN with or without spaces: a=b, a =b, a= b, a = b, a = b
     */
    @Test
    public void testDnCompositeWithSpace() throws LdapException
    {
        DN dn = new DN( "a=b, a =b, a= b, a = b, a  =  b" );

        assertTrue( DN.isValid( "a=b, a =b, a= b, a = b, a  =  b" ) );
        assertEquals( "a=b,a=b,a=b,a=b,a=b", dn.getNormName() );
        assertEquals( "a=b, a =b, a= b, a = b, a  =  b", dn.getName() );
    }


    /**
     * test a composite DN with differents separators : a=b;c=d,e=f It should
     * return a=b,c=d,e=f (the ';' is replaced by a ',')
     */
    @Test
    public void testDnCompositeSepators() throws LdapException
    {
        DN dn = new DN( "a=b;c=d,e=f" );

        assertTrue( DN.isValid( "a=b;c=d,e=f" ) );
        assertEquals( "a=b,c=d,e=f", dn.getNormName() );
        assertEquals( "a=b;c=d,e=f", dn.getName() );
    }


    /**
     * test a simple DN with multiple NameComponents : a = b + c = d
     */
    @Test
    public void testDnSimpleMultivaluedAttribute() throws LdapException
    {
        DN dn = new DN( "a = b + c = d" );

        assertTrue( DN.isValid( "a = b + c = d" ) );
        assertEquals( "a=b+c=d", dn.getNormName() );
        assertEquals( "a = b + c = d", dn.getName() );
    }


    /**
     * test a composite DN with multiple NC and separators : a=b+c=d, e=f + g=h +
     * i=j
     */
    @Test
    public void testDnCompositeMultivaluedAttribute() throws LdapException
    {
        DN dn = new DN( "a=b+c=d, e=f + g=h + i=j" );

        assertTrue( DN.isValid( "a=b+c=d, e=f + g=h + i=j" ) );
        assertEquals( "a=b+c=d,e=f+g=h+i=j", dn.getNormName() );
        assertEquals( "a=b+c=d, e=f + g=h + i=j", dn.getName() );
    }


    /**
    * Test to see if a DN with multiRdn values is preserved after an addAll.
    */
    @Test
    public void testAddAllWithMultivaluedAttribute() throws LdapException
    {
        DN dn = new DN( "cn=Kate Bush+sn=Bush,ou=system" );
        DN target = new DN();

        assertTrue( DN.isValid( "cn=Kate Bush+sn=Bush,ou=system" ) );
        target = target.addAll( target.size(), dn );
        assertEquals( "cn=Kate Bush+sn=Bush,ou=system", target.toString() );
        assertEquals( "cn=Kate Bush+sn=Bush,ou=system", target.getName() );
    }


    /**
     * test a simple DN with an oid prefix (uppercase) : OID.12.34.56 = azerty
     */
    @Test
    public void testDnOidUpper() throws LdapException
    {
        DN dn = new DN( "OID.12.34.56 = azerty" );

        assertTrue( DN.isValid( "OID.12.34.56 = azerty" ) );
        assertEquals( "oid.12.34.56=azerty", dn.getNormName() );
        assertEquals( "OID.12.34.56 = azerty", dn.getName() );
    }


    /**
     * test a simple DN with an oid prefix (lowercase) : oid.12.34.56 = azerty
     */
    @Test
    public void testDnOidLower() throws LdapException
    {
        DN dn = new DN( "oid.12.34.56 = azerty" );

        assertTrue( DN.isValid( "oid.12.34.56 = azerty" ) );
        assertEquals( "oid.12.34.56=azerty", dn.getNormName() );
        assertEquals( "oid.12.34.56 = azerty", dn.getName() );
    }


    /**
     * test a simple DN with an oid attribut without oid prefix : 12.34.56 =
     * azerty
     */
    @Test
    public void testDnOidWithoutPrefix() throws LdapException
    {
        DN dn = new DN( "12.34.56 = azerty" );

        assertTrue( DN.isValid( "12.34.56 = azerty" ) );
        assertEquals( "12.34.56=azerty", dn.getNormName() );
        assertEquals( "12.34.56 = azerty", dn.getName() );
    }


    /**
     * test a composite DN with an oid attribut wiithout oid prefix : 12.34.56 =
     * azerty; 7.8 = test
     */
    @Test
    public void testDnCompositeOidWithoutPrefix() throws LdapException
    {
        DN dn = new DN( "12.34.56 = azerty; 7.8 = test" );

        assertTrue( DN.isValid( "12.34.56 = azerty; 7.8 = test" ) );
        assertEquals( "12.34.56=azerty,7.8=test", dn.getNormName() );
        assertEquals( "12.34.56 = azerty; 7.8 = test", dn.getName() );
    }


    /**
     * test a simple DN with pair char attribute value : a = \,\=\+\<\>\#\;\\\"\C4\8D"
     */
    @Test
    public void testDnPairCharAttributeValue() throws LdapException
    {
        DN dn = new DN( "a = \\,\\=\\+\\<\\>\\#\\;\\\\\\\"\\C4\\8D" );

        assertTrue( DN.isValid( "a = \\,\\=\\+\\<\\>\\#\\;\\\\\\\"\\C4\\8D" ) );
        assertEquals( "a=\\,=\\+\\<\\>#\\;\\\\\\\"\u010D", dn.getNormName() );
        assertEquals( "a = \\,\\=\\+\\<\\>\\#\\;\\\\\\\"\\C4\\8D", dn.getName() );
    }


    /**
     * test a simple DN with pair char attribute value : "SN=Lu\C4\8Di\C4\87"
     */
    @Test
    public void testDnRFC253_Lucic() throws LdapException
    {
        DN dn = new DN( "SN=Lu\\C4\\8Di\\C4\\87" );

        assertTrue( DN.isValid( "SN=Lu\\C4\\8Di\\C4\\87" ) );
        assertEquals( "sn=Lu\u010Di\u0107", dn.getNormName() );
        assertEquals( "SN=Lu\\C4\\8Di\\C4\\87", dn.getName() );
    }


    /**
     * test a simple DN with hexString attribute value : a = #0010A0AAFF
     */
    @Test
    public void testDnHexStringAttributeValue() throws LdapException
    {
        DN dn = new DN( "a = #0010A0AAFF" );

        assertTrue( DN.isValid( "a = #0010A0AAFF" ) );
        assertEquals( "a=#0010A0AAFF", dn.getNormName() );
        assertEquals( "a = #0010A0AAFF", dn.getName() );
    }


    /**
     * Test for DIRSTUDIO-589, DIRSTUDIO-591, DIRSHARED-38
     *
     * Check escaped sharp followed by a hex sequence
     * (without the ESC it would be a valid hexstring).
     */
    @Test
    public void testDnEscSharpNumber() throws LdapException, LdapException
    {
        DN dn = new DN( "a = \\#123456" );

        assertTrue( DN.isValid( "a = \\#123456" ) );
        assertEquals( "a=\\#123456", dn.getNormName() );
        assertEquals( "a = \\#123456", dn.getName() );

        RDN rdn = dn.getRdn();
        assertEquals( "a = \\#123456", rdn.getName() );

        assertTrue( DN.isValid( "a = \\#00" ) );
        assertTrue( DN.isValid( "a = \\#11" ) );
        assertTrue( DN.isValid( "a = \\#99" ) );
        assertTrue( DN.isValid( "a = \\#AA" ) );
        assertTrue( DN.isValid( "a = \\#FF" ) );

        assertTrue( DN.isValid( "uid=\\#123456" ) );
        assertTrue( DN.isValid( "cn=\\#ACL_AD-Projects_Author,ou=Notes_Group,o=Contacts,c=DE" ) );
        assertTrue( DN.isValid( "cn=\\#Abraham" ) );
    }


    /**
      * test a simple DN with a # on first position
      */
    @Test
    public void testDnSharpFirst() throws LdapException, LdapException
    {
        DN dn = new DN( "a = \\#this is a sharp" );

        assertTrue( DN.isValid( "a = \\#this is a sharp" ) );
        assertEquals( "a=\\#this is a sharp", dn.getNormName() );
        assertEquals( "a = \\#this is a sharp", dn.getName() );

        RDN rdn = dn.getRdn();
        assertEquals( "a = \\#this is a sharp", rdn.getName() );
    }


    /**
     * Normalize a simple DN with a # on first position
     */
    @Test
    public void testNormalizeDnSharpFirst() throws LdapException, LdapException
    {
        DN dn = new DN( "ou = \\#this is a sharp" );

        assertTrue( DN.isValid( "ou = \\#this is a sharp" ) );
        assertEquals( "ou=\\#this is a sharp", dn.getNormName() );
        assertEquals( "ou = \\#this is a sharp", dn.getName() );

        // Check the normalization now
        DN ndn = dn.normalize( oidOids );

        assertEquals( "ou = \\#this is a sharp", ndn.getName() );
        assertEquals( "2.5.4.11=\\#this is a sharp", ndn.getNormName() );
    }


    /**
     * Normalize a DN with sequence ESC ESC HEX HEX (\\DC).
     * This is a corner case for the parser and normalizer.
     */
    @Test
    public void testNormalizeDnEscEscHexHex() throws LdapException
    {
        DN dn = new DN( "ou = AC\\\\DC" );
        assertTrue( DN.isValid( "ou = AC\\\\DC" ) );
        assertEquals( "ou=AC\\\\DC", dn.getNormName() );
        assertEquals( "ou = AC\\\\DC", dn.getName() );

        // Check the normalization now
        DN ndn = dn.normalize( oidOids );
        assertEquals( "ou = AC\\\\DC", ndn.getName() );
        assertEquals( "2.5.4.11=ac\\\\dc", ndn.getNormName() );
    }


    /**
     * test a simple DN with a wrong hexString attribute value : a = #0010Z0AAFF
     */
    @Test
    public void testDnWrongHexStringAttributeValue()
    {
        try
        {
            new DN( "a = #0010Z0AAFF" );
            fail();
        }
        catch ( LdapException ine )
        {

            assertFalse( DN.isValid( "a = #0010Z0AAFF" ) );
            assertTrue( true );
        }
    }


    /**
     * test a simple DN with a wrong hexString attribute value : a = #AABBCCDD3
     */
    @Test
    public void testDnWrongHexStringAttributeValue2()
    {
        try
        {
            new DN( "a = #AABBCCDD3" );
            fail();
        }
        catch ( LdapException ine )
        {
            assertFalse( DN.isValid( "a = #AABBCCDD3" ) );
            assertTrue( true );
        }
    }


    /**
     * test a simple DN with a quote in attribute value : a = quoted \"value\"
     */
    @Test
    public void testDnQuoteInAttributeValue() throws LdapException
    {
        DN dn = new DN( "a = quoted \\\"value\\\"" );

        assertTrue( DN.isValid( "a = quoted \\\"value\\\"" ) );
        assertEquals( "a=quoted \\\"value\\\"", dn.getNormName() );
        assertEquals( "a = quoted \\\"value\\\"", dn.getName() );
    }


    /**
     * test a simple DN with quoted attribute value : a = \" quoted value \"
     */
    @Test
    public void testDnQuotedAttributeValue() throws LdapException
    {
        DN dn = new DN( "a = \\\" quoted value \\\"" );

        assertTrue( DN.isValid( "a = \\\" quoted value \\\"" ) );
        assertEquals( "a=\\\" quoted value \\\"", dn.getNormName() );
        assertEquals( "a = \\\" quoted value \\\"", dn.getName() );
    }


    /**
     * test a simple DN with a comma at the end
     */
    @Test
    public void testDnComaAtEnd()
    {
        assertFalse( DN.isValid( "a = b," ) );
        assertFalse( DN.isValid( "a = b, " ) );

        try
        {
            new DN( "a = b," );
            fail();
        }
        catch ( LdapException ine )
        {
            assertTrue( true );
        }
    }


    // REMOVE operation -------------------------------------------------------

    /**
     * test a remove from position 0
     */
    @Test
    public void testDnRemove0() throws LdapException
    {
        DN dn = new DN( "a=b, c=d, e=f" );

        assertTrue( DN.isValid( "a=b, c=d, e=f" ) );
        // now remove method reurns a modified cloned DN
        dn = dn.remove( 0 );
        assertEquals( "a=b,c=d", dn.getNormName() );
        assertEquals( "a=b, c=d", dn.getName() );
    }


    /**
     * test a remove from position 1
     */
    @Test
    public void testDnRemove1() throws LdapException
    {
        DN dn = new DN( "a=b, c=d, e=f" );

        assertTrue( DN.isValid( "a=b, c=d, e=f" ) );
        assertEquals( "a=b, c=d, e=f", dn.getName() );
    }


    /**
     * test a remove from position 2
     */
    @Test
    public void testDnRemove2() throws LdapException
    {
        DN dn = new DN( "a=b, c=d, e=f" );

        assertTrue( DN.isValid( "a=b, c=d, e=f" ) );
        dn = dn.remove( 2 );
        assertEquals( " c=d, e=f", dn.getName() );
    }


    /**
     * test a remove from position 1 whith semi colon
     */
    @Test
    public void testDnRemove1WithSemiColon() throws LdapException
    {
        DN dn = new DN( "a=b, c=d; e=f" );

        assertTrue( DN.isValid( "a=b, c=d; e=f" ) );
        dn = dn.remove( 1 );
        assertEquals( "a=b, e=f", dn.getName() );
    }


    /**
     * test a remove out of bound
     */
    @Test
    public void testDnRemoveOutOfBound() throws LdapException
    {
        DN dn = new DN( "a=b, c=d; e=f" );

        assertTrue( DN.isValid( "a=b, c=d; e=f" ) );

        try
        {
            dn.remove( 4 );
            // We whould never reach this point
            fail();
        }
        catch ( ArrayIndexOutOfBoundsException aoobe )
        {
            assertTrue( true );
        }
    }


    // SIZE operations
    /**
     * test a 0 size
     */
    @Test
    public void testDnSize0()
    {
        DN dn = new DN();

        assertTrue( DN.isValid( "" ) );
        assertEquals( 0, dn.size() );
    }


    /**
     * test a 1 size
     */
    @Test
    public void testDnSize1() throws LdapException
    {
        DN dn = new DN( "a=b" );

        assertTrue( DN.isValid( "a=b" ) );
        assertEquals( 1, dn.size() );
    }


    /**
     * test a 3 size
     */
    @Test
    public void testDnSize3() throws LdapException
    {
        DN dn = new DN( "a=b, c=d, e=f" );

        assertTrue( DN.isValid( "a=b, c=d, e=f" ) );
        assertEquals( 3, dn.size() );
    }


    /**
     * test a 3 size with NameComponents
     */
    @Test
    public void testDnSize3NC() throws LdapException
    {
        DN dn = new DN( "a=b+c=d, c=d, e=f" );

        assertTrue( DN.isValid( "a=b+c=d, c=d, e=f" ) );
        assertEquals( 3, dn.size() );
    }


    /**
     * test size after operations
     */
    @Test
    public void testLdapResizing() throws LdapException
    {
        DN dn = new DN();
        assertEquals( 0, dn.size() );

        dn = dn.add( "e = f" );
        assertEquals( 1, dn.size() );

        dn = dn.add( "c = d" );
        assertEquals( 2, dn.size() );

        dn = dn.remove( 0 );
        assertEquals( 1, dn.size() );

        dn = dn.remove( 0 );
        assertEquals( 0, dn.size() );
    }


    // ADD Operations
    /**
     * test Add on a new DN
     */
    @Test
    public void testLdapEmptyAdd() throws LdapException
    {
        DN dn = new DN();

        dn = dn.add( "e = f" );
        assertEquals( "e=f", dn.getNormName() );
        assertEquals( "e = f", dn.getName() );
        assertEquals( 1, dn.size() );
    }


    /**
     * test Add to an existing DN
     */
    @Test
    public void testDnAdd() throws LdapException
    {
        DN dn = new DN( "a=b, c=d" );

        dn = dn.add( "e = f" );
        assertEquals( "e=f,a=b,c=d", dn.getNormName() );
        assertEquals( "e = f,a=b, c=d", dn.getName() );
        assertEquals( 3, dn.size() );
    }


    /**
     * test Add a composite RDN to an existing DN
     */
    @Test
    public void testDnAddComposite() throws LdapException
    {
        DN dn = new DN( "a=b, c=d" );

        dn = dn.add( "e = f + g = h" );

        // Warning ! The order of AVAs has changed during the parsing
        // This has no impact on the correctness of the DN, but the
        // String used to do the comparizon should be inverted.
        assertEquals( "e=f+g=h,a=b,c=d", dn.getNormName() );
        assertEquals( 3, dn.size() );
    }


    /**
     * test Add at the end of an existing DN
     */
    @Test
    public void testDnAddEnd() throws LdapException
    {
        DN dn = new DN( "a=b, c=d" );

        dn = dn.add( dn.size(), "e = f" );
        assertEquals( "e = f,a=b, c=d", dn.getName() );
        assertEquals( 3, dn.size() );
    }


    /**
     * test Add at the start of an existing DN
     */
    @Test
    public void testDnAddStart() throws LdapException
    {
        DN dn = new DN( "a=b, c=d" );

        dn = dn.add( 0, "e = f" );
        assertEquals( "a=b, c=d,e = f", dn.getName() );
        assertEquals( 3, dn.size() );
    }


    /**
     * test Add at the middle of an existing DN
     */
    @Test
    public void testDnAddMiddle() throws LdapException
    {
        DN dn = new DN( "a=b, c=d" );

        dn = dn.add( 1, "e = f" );
        assertEquals( "a=b,e = f, c=d", dn.getName() );
        assertEquals( 3, dn.size() );
    }


    // ADD ALL Operations
    /**
     * Test AddAll
     *
     * @throws LdapException
     */
    @Test
    public void testDnAddAll() throws LdapException
    {
        DN dn = new DN( "a = b" );
        DN dn2 = new DN( "c = d" );
        dn = dn.addAll( dn2 );
        assertEquals( "c = d,a = b", dn.getName() );
    }


    /**
     * Test AddAll with an empty added name
     *
     * @throws LdapException
     */
    @Test
    public void testDnAddAllAddedNameEmpty() throws LdapException
    {
        DN dn = new DN( "a = b" );
        DN dn2 = new DN();
        dn = dn.addAll( dn2 );
        assertEquals( "a=b", dn.getNormName() );
        assertEquals( "a = b", dn.getName() );
    }


    /**
     * Test AddAll to an empty name
     *
     * @throws LdapException
     */
    @Test
    public void testDnAddAllNameEmpty() throws LdapException
    {
        DN dn = new DN();
        DN dn2 = new DN( "a = b" );
        dn = dn.addAll( dn2 );
        assertEquals( "a = b", dn.getName() );
    }


    /**
     * Test AddAll at position 0
     *
     * @throws LdapException
     */
    @Test
    public void testDnAt0AddAll() throws LdapException
    {
        DN dn = new DN( "a = b" );
        DN dn2 = new DN( "c = d" );
        dn = dn.addAll( 0, dn2 );
        assertEquals( "a = b,c = d", dn.getName() );
    }


    /**
     * Test AddAll at position 1
     *
     * @throws LdapException
     */
    @Test
    public void testDnAt1AddAll() throws LdapException
    {
        DN dn = new DN( "a = b" );
        DN dn2 = new DN( "c = d" );
        dn = dn.addAll( 1, dn2 );
        assertEquals( "c = d,a = b", dn.getName() );
    }


    /**
     * Test AddAll at the middle
     *
     * @throws LdapException
     */
    @Test
    public void testDnAtTheMiddleAddAll() throws LdapException
    {
        DN dn = new DN( "a = b, c = d" );
        DN dn2 = new DN( "e = f" );
        dn = dn.addAll( 1, dn2 );
        assertEquals( "a = b,e = f, c = d", dn.getName() );
    }


    /**
     * Test AddAll with an empty added name at position 0
     *
     * @throws LdapException
     */
    @Test
    public void testDnAddAllAt0AddedNameEmpty() throws LdapException
    {
        DN dn = new DN( "a = b" );
        DN dn2 = new DN();
        dn = dn.addAll( 0, dn2 );
        assertEquals( "a=b", dn.getNormName() );
        assertEquals( "a = b", dn.getName() );
    }


    /**
     * Test AddAll to an empty name at position 0
     *
     * @throws LdapException
     */
    @Test
    public void testDnAddAllAt0NameEmpty() throws LdapException
    {
        DN dn = new DN();
        DN dn2 = new DN( "a = b" );
        dn = dn.addAll( 0, dn2 );
        assertEquals( "a = b", dn.getName() );
    }


    // GET PREFIX actions
    /**
     * Get the prefix at pos 0
     */
    @Test
    public void testDnGetPrefixPos0() throws LdapException
    {
        DN dn = new DN( "a=b, c=d,e = f" );
        DN newDn = ( dn.getPrefix( 0 ) );
        assertEquals( "", newDn.getName() );
    }


    /**
     * Get the prefix at pos 1
     */
    @Test
    public void testDnGetPrefixPos1() throws LdapException
    {
        DN dn = new DN( "a=b, c=d,e = f" );
        DN newDn = ( dn.getPrefix( 1 ) );
        assertEquals( "e = f", newDn.getName() );
    }


    /**
     * Get the prefix at pos 2
     */
    @Test
    public void testDnGetPrefixPos2() throws LdapException
    {
        DN dn = new DN( "a=b, c=d,e = f" );
        DN newDn = ( dn.getPrefix( 2 ) );
        assertEquals( " c=d,e = f", newDn.getName() );
    }


    /**
     * Get the prefix at pos 3
     */
    @Test
    public void testDnGetPrefixPos3() throws LdapException
    {
        DN dn = new DN( "a=b, c=d,e = f" );
        DN newDn = ( dn.getPrefix( 3 ) );
        assertEquals( "a=b, c=d,e = f", newDn.getName() );
    }


    /**
     * Get the prefix out of bound
     */
    @Test
    public void testDnGetPrefixPos4() throws LdapException
    {
        DN dn = new DN( "a=b, c=d,e = f" );

        try
        {
            dn.getPrefix( 4 );
            // We should not reach this point.
            fail();
        }
        catch ( ArrayIndexOutOfBoundsException aoobe )
        {
            assertTrue( true );
        }
    }


    /**
     * Get the prefix of an empty LdapName
     */
    @Test
    public void testDnGetPrefixEmptyDN()
    {
        DN dn = new DN();
        DN newDn = ( dn.getPrefix( 0 ) );
        assertEquals( "", newDn.getName() );
    }


    // GET SUFFIX operations
    /**
     * Get the suffix at pos 0
     */
    @Test
    public void testDnGetSuffixPos0() throws LdapException
    {
        DN dn = new DN( "a=b, c=d,e = f" );
        DN newDn = ( dn.getSuffix( 0 ) );
        assertEquals( "a=b, c=d,e = f", newDn.getName() );
    }


    /**
     * Get the suffix at pos 1
     */
    @Test
    public void testDnGetSuffixPos1() throws LdapException
    {
        DN dn = new DN( "a=b, c=d,e = f" );
        DN newDn = ( dn.getSuffix( 1 ) );
        assertEquals( "a=b, c=d", newDn.getName() );
    }


    /**
     * Get the suffix at pos 2
     */
    @Test
    public void testDnGetSuffixPos2() throws LdapException
    {
        DN dn = new DN( "a=b, c=d,e = f" );
        DN newDn = ( dn.getSuffix( 2 ) );
        assertEquals( "a=b", newDn.getName() );
    }


    /**
     * Get the suffix at pos 3
     */
    @Test
    public void testDnGetSuffixPos3() throws LdapException
    {
        DN dn = new DN( "a=b, c=d,e = f" );
        DN newDn = ( dn.getSuffix( 3 ) );
        assertEquals( "", newDn.getName() );
    }


    /**
     * Get the suffix out of bound
     */
    @Test
    public void testDnGetSuffixPos4() throws LdapException
    {
        DN dn = new DN( "a=b, c=d,e = f" );

        try
        {
            dn.getSuffix( 4 );
            // We should not reach this point.
            fail();
        }
        catch ( ArrayIndexOutOfBoundsException aoobe )
        {
            assertTrue( true );
        }
    }


    /**
     * Get the suffix of an empty LdapName
     */
    @Test
    public void testDnGetSuffixEmptyDN()
    {
        DN dn = new DN();
        DN newDn = ( dn.getSuffix( 0 ) );
        assertEquals( "", newDn.getName() );
    }


    // IS EMPTY operations
    /**
     * Test that a DN is empty
     */
    @Test
    public void testDnIsEmpty()
    {
        DN dn = new DN();
        assertEquals( true, dn.isEmpty() );
    }


    /**
     * Test that a DN is empty
     */
    @Test
    public void testDnNotEmpty() throws LdapException
    {
        DN dn = new DN( "a=b" );
        assertEquals( false, dn.isEmpty() );
    }


    /**
     * Test that a DN is empty
     */
    @Test
    public void testDnRemoveIsEmpty() throws LdapException
    {
        DN dn = new DN( "a=b, c=d" );
        DN clonedDn = dn.remove( 0 );

        assertFalse( dn == clonedDn );

        clonedDn = clonedDn.remove( 0 );

        assertEquals( true, clonedDn.isEmpty() );
    }


    // STARTS WITH operations
    /**
     * Test a startsWith a null DN
     */
    @Test
    public void testDnStartsWithNull() throws LdapException
    {
        DN dn = new DN( "a=b, c=d,e = f" );
        assertEquals( true, dn.isChildOf( ( DN ) null ) );
    }


    /**
     * Test a startsWith an empty DN
     */
    @Test
    public void testDnStartsWithEmpty() throws LdapException
    {
        DN dn = new DN( "a=b, c=d,e = f" );
        assertEquals( true, dn.isChildOf( new DN() ) );
    }


    /**
     * Test a startsWith an simple DN
     */
    @Test
    public void testDnStartsWithSimple() throws LdapException
    {
        DN dn = new DN( "a=b, c=d,e = f" );
        assertEquals( true, dn.isChildOf( new DN( "e=f" ) ) );
    }


    /**
     * Test a startsWith a complex DN
     */
    @Test
    public void testDnStartsWithComplex() throws LdapException
    {
        DN dn = new DN( "a=b, c=d,e = f" );
        assertEquals( true, dn.isChildOf( new DN( "c =  d, e =  f" ) ) );
    }


    /**
     * Test a startsWith a complex DN
     */
    @Test
    public void testDnStartsWithComplexMixedCase() throws LdapException
    {
        DN dn = new DN( "a=b, c=d,e = f" );
        assertEquals( false, dn.isChildOf( new DN( "c =  D, E =  f" ) ) );
    }


    /**
     * Test a startsWith a full DN
     */
    @Test
    public void testDnStartsWithFull() throws LdapException
    {
        DN dn = new DN( "a=b, c=d,e = f" );
        assertEquals( true, dn.isChildOf( new DN( "a=  b; c =  d, e =  f" ) ) );
    }


    /**
     * Test a startsWith which returns false
     */
    @Test
    public void testDnStartsWithWrong() throws LdapException
    {
        DN dn = new DN( "a=b, c=d,e = f" );
        assertEquals( false, dn.isChildOf( new DN( "c =  t, e =  f" ) ) );
    }


    // ENDS WITH operations
    /**
     * Test a endsWith a null DN
     */
    @Test
    public void testDnEndsWithNull() throws LdapException
    {
        DN dn = new DN( "a=b, c=d,e = f" );
        assertEquals( true, dn.hasSuffix( ( DN ) null ) );
    }


    /**
     * Test a endsWith an empty DN
     */
    @Test
    public void testDnEndsWithEmpty() throws LdapException
    {
        DN dn = new DN( "a=b, c=d,e = f" );
        assertEquals( true, dn.hasSuffix( new DN() ) );
    }


    /**
     * Test a endsWith an simple DN
     */
    @Test
    public void testDnEndsWithSimple() throws LdapException
    {
        DN dn = new DN( "a=b, c=d,e = f" );
        assertEquals( true, dn.hasSuffix( new DN( "a=b" ) ) );
    }


    /**
     * Test a endsWith a complex DN
     */
    @Test
    public void testDnEndsWithComplex() throws LdapException
    {
        DN dn = new DN( "a=b, c=d,e = f" );
        assertEquals( true, dn.hasSuffix( new DN( "a =  b, c =  d" ) ) );
    }


    /**
     * Test a endsWith a complex DN
     */
    @Test
    public void testDnEndsWithComplexMixedCase() throws LdapException
    {
        DN dn = new DN( "a=b, c=d,e = f" );
        assertEquals( false, dn.hasSuffix( new DN( "a =  B, C =  d" ) ) );
    }


    /**
     * Test a endsWith a full DN
     */
    @Test
    public void testDnEndsWithFull() throws LdapException
    {
        DN dn = new DN( "a=b, c=d,e = f" );
        assertEquals( true, dn.hasSuffix( new DN( "a=  b; c =  d, e =  f" ) ) );
    }


    /**
     * Test a endsWith which returns false
     */
    @Test
    public void testDnEndsWithWrong() throws LdapException
    {
        DN dn = new DN( "a=b, c=d,e = f" );
        assertEquals( false, dn.hasSuffix( new DN( "a =  b, e =  f" ) ) );
    }


    // GET ALL operations
    /**
     * test a getAll operation on a null DN
     *
    @Test
    public void testDnGetAllNull()
    {
        DN dn = new DN();
        Enumeration<String> nc = dn.getAll();

        assertEquals( false, nc.hasMoreElements() );
    }


    /**
     * test a getAll operation on an empty DN
     *
    @Test
    public void testDnGetAllEmpty() throws LdapException
    {
        DN dn = new DN( "" );
        Enumeration<String> nc = dn.getAll();

        assertEquals( false, nc.hasMoreElements() );
    }


    /**
     * test a getAll operation on a simple DN
     *
    @Test
    public void testDnGetAllSimple() throws LdapException
    {
        DN dn = new DN( "a=b" );
        Enumeration<String> nc = dn.getAll();

        assertEquals( true, nc.hasMoreElements() );
        assertEquals( "a=b", nc.nextElement() );
        assertEquals( false, nc.hasMoreElements() );
    }


    /**
     * test a getAll operation on a complex DN
     *
    @Test
    public void testDnGetAllComplex() throws LdapException
    {
        DN dn = new DN( "e=f+g=h,a=b,c=d" );
        Enumeration<String> nc = dn.getAll();

        assertEquals( true, nc.hasMoreElements() );
        assertEquals( "c=d", nc.nextElement() );
        assertEquals( true, nc.hasMoreElements() );
        assertEquals( "a=b", nc.nextElement() );
        assertEquals( true, nc.hasMoreElements() );
        assertEquals( "e=f+g=h", nc.nextElement() );
        assertEquals( false, nc.hasMoreElements() );
    }


    /**
     * test a getAll operation on a complex DN
     *
    @Test
    public void testDnGetAllComplexOrdered() throws LdapException
    {
        DN dn = new DN( "g=h+e=f,a=b,c=d" );
        Enumeration<String> nc = dn.getAll();

        assertEquals( true, nc.hasMoreElements() );
        assertEquals( "c=d", nc.nextElement() );
        assertEquals( true, nc.hasMoreElements() );
        assertEquals( "a=b", nc.nextElement() );
        assertEquals( true, nc.hasMoreElements() );

        assertEquals( "e=f+g=h", nc.nextElement() );
        assertEquals( false, nc.hasMoreElements() );
    }


    // CLONE Operation
    /**
     * test a clone operation on a empty DN
     */
    @Test
    public void testDnCloneEmpty()
    {
        DN dn = new DN();
        DN clone = ( DN ) dn.clone();

        assertEquals( "", clone.getName() );
    }


    /**
     * test a clone operation on a simple DN
     */
    @Test
    public void testDnCloneSimple() throws LdapException
    {
        DN dn = new DN( "a=b" );
        DN clone = ( DN ) dn.clone();

        assertEquals( "a=b", clone.getName() );
        dn.remove( 0 );
        assertEquals( "a=b", clone.getName() );
    }


    /**
     * test a clone operation on a complex DN
     */
    @Test
    public void testDnCloneComplex() throws LdapException
    {
        DN dn = new DN( "e=f+g=h,a=b,c=d" );
        DN clone = ( DN ) dn.clone();

        assertEquals( "e=f+g=h,a=b,c=d", clone.getName() );
        dn.remove( 2 );
        assertEquals( "e=f+g=h,a=b,c=d", clone.getName() );
    }


    // GET operations
    /**
     * test a get in a null DN
     */
    @Test
    public void testDnGetNull()
    {
        DN dn = new DN();
        assertEquals( "", dn.get( 0 ) );
    }


    /**
     * test a get in an empty DN
     */
    @Test
    public void testDnGetEmpty() throws LdapException
    {
        DN dn = new DN( "" );
        assertEquals( "", dn.get( 0 ) );
    }


    /**
     * test a get in a simple DN
     */
    @Test
    public void testDnGetSimple() throws LdapException
    {
        DN dn = new DN( "a = b" );
        assertEquals( "a=b", dn.get( 0 ) );
    }


    /**
     * test a get in a complex DN
     */
    @Test
    public void testDnGetComplex() throws LdapException
    {
        DN dn = new DN( "a = b + c= d, e= f; g =h" );
        assertEquals( "g=h", dn.get( 0 ) );
        assertEquals( "e=f", dn.get( 1 ) );
        assertEquals( "a=b+c=d", dn.get( 2 ) );
    }


    /**
     * test a get out of bound
     */
    @Test
    public void testDnGetOutOfBound() throws LdapException
    {
        DN dn = new DN( "a = b + c= d, e= f; g =h" );

        try
        {
            dn.get( 4 );
            fail();
        }
        catch ( IndexOutOfBoundsException aioob )
        {
            assertTrue( true );
        }
    }


    /**
     * Tests the examples from the JNDI tutorials to make sure LdapName behaves
     * appropriately. The example can be found online <a href="">here</a>.
     *
     * @throws Exception
     *             if anything goes wrong
     */
    @Test
    public void testJNDITutorialExample() throws Exception
    {
        // Parse the name
        DN name = new DN( "cn=John,ou=People,ou=Marketing" );

        // Remove the second component from the head: ou=People
        name = name.remove( 1 );
        String out = name.toString();

        assertEquals( "cn=John,ou=Marketing", out );

        // Add to the head (first): cn=John,ou=Marketing,ou=East
        name = name.add( 0, "ou=East" );
        out = name.toString();

        assertEquals( "cn=John,ou=Marketing,ou=East", out );

        // Add to the tail (last): cn=HomeDir,cn=John,ou=Marketing,ou=East
        out = name.add( "cn=HomeDir" ).toString();

        assertEquals( "cn=HomeDir,cn=John,ou=Marketing,ou=East", out );
    }


    @Test
    public void testAttributeEqualsIsCaseInSensitive() throws Exception
    {
        DN name1 = new DN( "cn=HomeDir" );
        DN name2 = new DN( "CN=HomeDir" );

        assertTrue( name1.equals( name2 ) );
    }


    @Test
    public void testAttributeTypeEqualsIsCaseInsensitive() throws Exception
    {
        DN name1 = new DN( "cn=HomeDir+cn=WorkDir" );
        DN name2 = new DN( "cn=HomeDir+CN=WorkDir" );

        assertTrue( name1.equals( name2 ) );
    }


    @Test
    public void testNameEqualsIsInsensitiveToAttributesOrder() throws Exception
    {

        DN name1 = new DN( "cn=HomeDir+cn=WorkDir" );
        DN name2 = new DN( "cn=WorkDir+cn=HomeDir" );

        assertTrue( name1.equals( name2 ) );
    }


    @Test
    public void testAttributeComparisonIsCaseInSensitive() throws Exception
    {
        DN name1 = new DN( "cn=HomeDir" );
        DN name2 = new DN( "CN=HomeDir" );

        assertEquals( 0, name1.compareTo( name2 ) );
    }


    @Test
    public void testAttributeTypeComparisonIsCaseInsensitive() throws Exception
    {
        DN name1 = new DN( "cn=HomeDir+cn=WorkDir" );
        DN name2 = new DN( "cn=HomeDir+CN=WorkDir" );

        assertEquals( 0, name1.compareTo( name2 ) );
    }


    @Test
    public void testNameComparisonIsInsensitiveToAttributesOrder() throws Exception
    {

        DN name1 = new DN( "cn=HomeDir+cn=WorkDir" );
        DN name2 = new DN( "cn=WorkDir+cn=HomeDir" );

        assertEquals( 0, name1.compareTo( name2 ) );
    }


    @Test
    public void testNameComparisonIsInsensitiveToAttributesOrderFailure() throws Exception
    {

        DN name1 = new DN( "cn= HomeDir+cn=Workdir" );
        DN name2 = new DN( "cn = Work+cn=HomeDir" );

        assertEquals( 1, name1.compareTo( name2 ) );
    }


    /**
     * Test the encoding of a LdanDN
     */
    @Test
    public void testNameToBytes() throws Exception
    {
        DN dn = new DN( "cn = John, ou = People, OU = Marketing" );

        byte[] bytes = DN.getBytes( dn );

        assertEquals( 30, DN.getNbBytes( dn ) );
        assertEquals( "cn=John,ou=People,ou=Marketing", new String( bytes, "UTF-8" ) );
    }


    @Test
    public void testStringParser() throws Exception
    {
        String dn = StringTools.utf8ToString( new byte[]
            { 'C', 'N', ' ', '=', ' ', 'E', 'm', 'm', 'a', 'n', 'u', 'e', 'l', ' ', ' ', 'L', ( byte ) 0xc3,
                ( byte ) 0xa9, 'c', 'h', 'a', 'r', 'n', 'y' } );

        DN name = DnParser.getNameParser().parse( dn );

        assertEquals( dn, ( name ).getName() );
        assertEquals( "cn=Emmanuel  L\u00E9charny", ( name ).getNormName() );
    }


    /**
     * Class to test for void LdapName(String)
     *
     * @throws Exception
     *             if anything goes wrong.
     */
    @Test
    public void testLdapNameString() throws Exception
    {
        DN name = new DN( "" );
        DN name50 = new DN();
        assertEquals( name50, name );

        DN name0 = new DN( "ou=Marketing,ou=East" );
        DN copy = new DN( "ou=Marketing,ou=East" );
        DN name1 = new DN( "cn=John,ou=Marketing,ou=East" );
        DN name2 = new DN( "cn=HomeDir,cn=John,ou=Marketing,ou=East" );
        DN name3 = new DN( "cn=HomeDir,cn=John,ou=Marketing,ou=West" );
        DN name4 = new DN( "cn=Website,cn=John,ou=Marketing,ou=West" );
        DN name5 = new DN( "cn=Airline,cn=John,ou=Marketing,ou=West" );

        assertTrue( name0.compareTo( copy ) == 0 );
        assertTrue( name0.compareTo( name1 ) < 0 );
        assertTrue( name0.compareTo( name2 ) < 0 );
        assertTrue( name1.compareTo( name2 ) < 0 );
        assertTrue( name2.compareTo( name1 ) > 0 );
        assertTrue( name2.compareTo( name0 ) > 0 );
        assertTrue( name2.compareTo( name3 ) < 0 );
        assertTrue( name2.compareTo( name4 ) < 0 );
        assertTrue( name3.compareTo( name4 ) < 0 );
        assertTrue( name3.compareTo( name5 ) > 0 );
        assertTrue( name4.compareTo( name5 ) > 0 );
        assertTrue( name2.compareTo( name5 ) < 0 );
    }


    /**
     * Class to test for void LdapName()
     */
    @Test
    public void testLdapName()
    {
        DN name = new DN();
        assertTrue( name.toString().equals( "" ) );
    }


    /**
     * Class to test for Object clone()
     *
     * @throws Exception
     *             if anything goes wrong.
     */
    @Test
    public void testClone() throws Exception
    {
        String strName = "cn=HomeDir,cn=John,ou=Marketing,ou=East";
        DN name = new DN( strName );
        assertEquals( name, name.clone() );
    }


    /**
     * Class to test for compareTo
     *
     * @throws Exception
     *             if anything goes wrong.
     */
    @Test
    public void testCompareTo() throws Exception
    {
        DN name0 = new DN( "ou=Marketing,ou=East" );
        DN copy = new DN( "ou=Marketing,ou=East" );
        DN name1 = new DN( "cn=John,ou=Marketing,ou=East" );
        DN name2 = new DN( "cn=HomeDir,cn=John,ou=Marketing,ou=East" );
        DN name3 = new DN( "cn=HomeDir,cn=John,ou=Marketing,ou=West" );
        DN name4 = new DN( "cn=Website,cn=John,ou=Marketing,ou=West" );
        DN name5 = new DN( "cn=Airline,cn=John,ou=Marketing,ou=West" );

        assertTrue( name0.compareTo( copy ) == 0 );
        assertTrue( name0.compareTo( name1 ) < 0 );
        assertTrue( name0.compareTo( name2 ) < 0 );
        assertTrue( name1.compareTo( name2 ) < 0 );
        assertTrue( name2.compareTo( name1 ) > 0 );
        assertTrue( name2.compareTo( name0 ) > 0 );
        assertTrue( name2.compareTo( name3 ) < 0 );
        assertTrue( name2.compareTo( name4 ) < 0 );
        assertTrue( name3.compareTo( name4 ) < 0 );
        assertTrue( name3.compareTo( name5 ) > 0 );
        assertTrue( name4.compareTo( name5 ) > 0 );
        assertTrue( name2.compareTo( name5 ) < 0 );

        List<DN> list = new ArrayList<DN>();

        Comparator<DN> comparator = new Comparator<DN>()
        {
            public int compare( DN obj1, DN obj2 )
            {
                DN n1 = obj1;
                DN n2 = obj2;
                return n1.compareTo( n2 );
            }


            public boolean equals( Object obj )
            {
                return super.equals( obj );
            }


            /**
             * Compute the instance's hash code
             * @return the instance's hash code
             */
            public int hashCode()
            {
                return super.hashCode();
            }
        };

        list.add( name0 );
        list.add( name1 );
        list.add( name2 );
        list.add( name3 );
        list.add( name4 );
        list.add( name5 );
        Collections.sort( list, comparator );

        assertEquals( name0, list.get( 0 ) );
        assertEquals( name1, list.get( 1 ) );
        assertEquals( name2, list.get( 2 ) );
        assertEquals( name5, list.get( 3 ) );
        assertEquals( name3, list.get( 4 ) );
        assertEquals( name4, list.get( 5 ) );
    }


    /**
     * Class to test for size
     *
     * @throws Exception
     *             if anything goes wrong.
     */
    @Test
    public void testSize() throws Exception
    {
        DN name0 = new DN( "" );
        DN name1 = new DN( "ou=East" );
        DN name2 = new DN( "ou=Marketing,ou=East" );
        DN name3 = new DN( "cn=John,ou=Marketing,ou=East" );
        DN name4 = new DN( "cn=HomeDir,cn=John,ou=Marketing,ou=East" );
        DN name5 = new DN( "cn=Website,cn=HomeDir,cn=John,ou=Marketing,ou=West" );
        DN name6 = new DN( "cn=Airline,cn=Website,cn=HomeDir,cn=John,ou=Marketing,ou=West" );

        assertEquals( 0, name0.size() );
        assertEquals( 1, name1.size() );
        assertEquals( 2, name2.size() );
        assertEquals( 3, name3.size() );
        assertEquals( 4, name4.size() );
        assertEquals( 5, name5.size() );
        assertEquals( 6, name6.size() );
    }


    /**
     * Class to test for isEmpty
     *
     * @throws Exception
     *             if anything goes wrong.
     */
    @Test
    public void testIsEmpty() throws Exception
    {
        DN name0 = new DN( "" );
        DN name1 = new DN( "ou=East" );
        DN name2 = new DN( "ou=Marketing,ou=East" );
        DN name3 = new DN( "cn=John,ou=Marketing,ou=East" );
        DN name4 = new DN( "cn=HomeDir,cn=John,ou=Marketing,ou=East" );
        DN name5 = new DN( "cn=Website,cn=HomeDir,cn=John,ou=Marketing,ou=West" );
        DN name6 = new DN( "cn=Airline,cn=Website,cn=HomeDir,cn=John,ou=Marketing,ou=West" );

        assertEquals( true, name0.isEmpty() );
        assertEquals( false, name1.isEmpty() );
        assertEquals( false, name2.isEmpty() );
        assertEquals( false, name3.isEmpty() );
        assertEquals( false, name4.isEmpty() );
        assertEquals( false, name5.isEmpty() );
        assertEquals( false, name6.isEmpty() );
    }


    /**
     * Class to test for getAll
     *
     * @throws Exception
     *             if anything goes wrong.
     *
    @Test
    public void testGetAll() throws Exception
    {
        DN name0 = new DN( "" );
        DN name1 = new DN( "ou=East" );
        DN name2 = new DN( "ou=Marketing,ou=East" );
        DN name3 = new DN( "cn=John,ou=Marketing,ou=East" );
        DN name4 = new DN( "cn=HomeDir,cn=John,ou=Marketing,ou=East" );
        DN name5 = new DN( "cn=Website,cn=HomeDir,cn=John,ou=Marketing,ou=West" );
        DN name6 = new DN( "cn=Airline,cn=Website,cn=HomeDir,cn=John,ou=Marketing,ou=West" );

        Enumeration<String> enum0 = name0.getAll();
        assertEquals( false, enum0.hasMoreElements() );

        Enumeration<String> enum1 = name1.getAll();
        assertEquals( true, enum1.hasMoreElements() );

        for ( int i = 0; enum1.hasMoreElements(); i++ )
        {
            String element = ( String ) enum1.nextElement();

            if ( i == 0 )
            {
                assertEquals( "ou=East", element );
            }
        }

        Enumeration<String> enum2 = name2.getAll();
        assertEquals( true, enum2.hasMoreElements() );

        for ( int i = 0; enum2.hasMoreElements(); i++ )
        {
            String element = ( String ) enum2.nextElement();

            if ( i == 0 )
            {
                assertEquals( "ou=East", element );
            }

            if ( i == 1 )
            {
                assertEquals( "ou=Marketing", element );
            }
        }

        Enumeration<String> enum3 = name3.getAll();
        assertEquals( true, enum3.hasMoreElements() );

        for ( int i = 0; enum3.hasMoreElements(); i++ )
        {
            String element = ( String ) enum3.nextElement();

            if ( i == 0 )
            {
                assertEquals( "ou=East", element );
            }

            if ( i == 1 )
            {
                assertEquals( "ou=Marketing", element );
            }

            if ( i == 2 )
            {
                assertEquals( "cn=John", element );
            }
        }

        Enumeration<String> enum4 = name4.getAll();
        assertEquals( true, enum4.hasMoreElements() );

        for ( int i = 0; enum4.hasMoreElements(); i++ )
        {
            String element = ( String ) enum4.nextElement();

            if ( i == 0 )
            {
                assertEquals( "ou=East", element );
            }

            if ( i == 1 )
            {
                assertEquals( "ou=Marketing", element );
            }

            if ( i == 2 )
            {
                assertEquals( "cn=John", element );
            }

            if ( i == 3 )
            {
                assertEquals( "cn=HomeDir", element );
            }
        }

        Enumeration<String> enum5 = name5.getAll();
        assertEquals( true, enum5.hasMoreElements() );

        for ( int i = 0; enum5.hasMoreElements(); i++ )
        {
            String element = ( String ) enum5.nextElement();

            if ( i == 0 )
            {
                assertEquals( "ou=West", element );
            }

            if ( i == 1 )
            {
                assertEquals( "ou=Marketing", element );
            }

            if ( i == 2 )
            {
                assertEquals( "cn=John", element );
            }

            if ( i == 3 )
            {
                assertEquals( "cn=HomeDir", element );
            }

            if ( i == 4 )
            {
                assertEquals( "cn=Website", element );
            }
        }

        Enumeration<String> enum6 = name6.getAll();
        assertEquals( true, enum6.hasMoreElements() );

        for ( int i = 0; enum6.hasMoreElements(); i++ )
        {
            String element = ( String ) enum6.nextElement();

            if ( i == 0 )
            {
                assertEquals( "ou=West", element );
            }

            if ( i == 1 )
            {
                assertEquals( "ou=Marketing", element );
            }

            if ( i == 2 )
            {
                assertEquals( "cn=John", element );
            }

            if ( i == 3 )
            {
                assertEquals( "cn=HomeDir", element );
            }

            if ( i == 4 )
            {
                assertEquals( "cn=Website", element );
            }

            if ( i == 5 )
            {
                assertEquals( "cn=Airline", element );
            }
        }
    }


    /**
     * Class to test for getAllRdn
     *
     * @throws Exception
     *             if anything goes wrong.
     */
    @Test
    public void testIterator() throws Exception
    {
        DN dn = new DN( "cn=Airline,cn=Website,cn=HomeDir,cn=John,ou=Marketing,ou=West" );
        String[] expected = new String[]
            { "ou=West", "ou=Marketing", "cn=John", "cn=HomeDir", "cn=Website", "cn=Airline" };
        int count = 0;

        for ( RDN rdn : dn )
        {
            assertEquals( expected[count], rdn.toString() );
            count++;
        }
    }


    /**
     * Test the get( int ) method
     */
    @Test
    public void testGet() throws Exception
    {
        DN name = new DN( "cn=HomeDir,cn=John,ou=Marketing,ou=East" );
        assertEquals( "cn=HomeDir", name.get( 3 ) );
        assertEquals( "cn=John", name.get( 2 ) );
        assertEquals( "ou=Marketing", name.get( 1 ) );
        assertEquals( "ou=East", name.get( 0 ) );
    }


    /**
     * Test the getRdn( int ) method
     */
    @Test
    public void testGetRdn() throws Exception
    {
        DN name = new DN( "cn=HomeDir,cn=John,ou=Marketing,ou=East" );
        assertEquals( "cn=HomeDir", name.getRdn( 3 ).getName() );
        assertEquals( "cn=John", name.getRdn( 2 ).getName() );
        assertEquals( "ou=Marketing", name.getRdn( 1 ).getName() );
        assertEquals( "ou=East", name.getRdn( 0 ).getName() );
    }


    /**
     * Test the getRdns() method
     */
    @Test
    public void testGetRdns() throws Exception
    {
        DN dn = new DN( "cn=HomeDir,cn=John,ou=Marketing,ou=East" );

        String[] expected = new String[]
            { "cn=HomeDir", "cn=John", "ou=Marketing", "ou=East" };

        int i = 0;

        for ( RDN rdn : dn.getRdns() )
        {
            assertEquals( expected[i], rdn.getName() );
            i++;
        }
    }


    /**
     * Class to test for getSuffix
     *
     * @throws Exception
     *             anything goes wrong
     */
    @Test
    public void testGetXSuffix() throws Exception
    {
        DN name = new DN( "cn=HomeDir,cn=John,ou=Marketing,ou=East" );
        assertEquals( "", name.getSuffix( 4 ).toString() );
        assertEquals( "cn=HomeDir", name.getSuffix( 3 ).toString() );
        assertEquals( "cn=HomeDir,cn=John", name.getSuffix( 2 ).toString() );
        assertEquals( "cn=HomeDir,cn=John,ou=Marketing", name.getSuffix( 1 ).toString() );
        assertEquals( "cn=HomeDir,cn=John,ou=Marketing,ou=East", name.getSuffix( 0 ).toString() );
    }


    /**
     * Class to test for getPrefix
     *
     * @throws Exception
     *             anything goes wrong
     */
    @Test
    public void testGetPrefix() throws Exception
    {
        DN name = new DN( "cn=HomeDir,cn=John,ou=Marketing,ou=East" );

        assertEquals( "cn=HomeDir,cn=John,ou=Marketing,ou=East", name.getPrefix( 4 ).toString() );
        assertEquals( "cn=John,ou=Marketing,ou=East", name.getPrefix( 3 ).toString() );
        assertEquals( "ou=Marketing,ou=East", name.getPrefix( 2 ).toString() );
        assertEquals( "ou=East", name.getPrefix( 1 ).toString() );
        assertEquals( "", name.getPrefix( 0 ).toString() );
    }


    /**
     * Class to test for startsWith
     *
     * @throws Exception
     *             anything goes wrong
     */
    @Test
    public void testStartsWith() throws Exception
    {
        DN n0 = new DN( "cn=HomeDir,cn=John,ou=Marketing,ou=East" );
        DN n1 = new DN( "cn=HomeDir,cn=John,ou=Marketing,ou=East" );
        DN n2 = new DN( "cn=John,ou=Marketing,ou=East" );
        DN n3 = new DN( "ou=Marketing,ou=East" );
        DN n4 = new DN( "ou=East" );
        DN n5 = new DN( "" );

        DN n6 = new DN( "cn=HomeDir" );
        DN n7 = new DN( "cn=HomeDir,cn=John" );
        DN n8 = new DN( "cn=HomeDir,cn=John,ou=Marketing" );

        // Check with DN
        assertTrue( n0.isChildOf( n1 ) );
        assertTrue( n0.isChildOf( n2 ) );
        assertTrue( n0.isChildOf( n3 ) );
        assertTrue( n0.isChildOf( n4 ) );
        assertTrue( n0.isChildOf( n5 ) );

        assertTrue( !n0.isChildOf( n6 ) );
        assertTrue( !n0.isChildOf( n7 ) );
        assertTrue( !n0.isChildOf( n8 ) );

        DN nn0 = new DN( "cn=zero" );
        DN nn10 = new DN( "cn=one,cn=zero" );
        DN nn210 = new DN( "cn=two,cn=one,cn=zero" );
        DN nn3210 = new DN( "cn=three,cn=two,cn=one,cn=zero" );

        assertTrue( nn0.isChildOf( nn0 ) );
        assertTrue( nn10.isChildOf( nn0 ) );
        assertTrue( nn210.isChildOf( nn0 ) );
        assertTrue( nn3210.isChildOf( nn0 ) );

        assertTrue( nn10.isChildOf( nn10 ) );
        assertTrue( nn210.isChildOf( nn10 ) );
        assertTrue( nn3210.isChildOf( nn10 ) );

        assertTrue( nn210.isChildOf( nn210 ) );
        assertTrue( nn3210.isChildOf( nn210 ) );

        assertTrue( nn3210.isChildOf( nn3210 ) );

        assertTrue( "Starting DN fails with ADS DN", new DN( "ou=foo,dc=apache,dc=org" ).isChildOf( new DN(
            "dc=apache,dc=org" ) ) );

        assertTrue( "Starting DN fails with Java LdapName", new DN( "ou=foo,dc=apache,dc=org" ).isChildOf( new DN(
            "dc=apache,dc=org" ) ) );

        assertTrue( "Starting DN fails with Java LdapName", new DN( "dc=apache,dc=org" ).isChildOf( new DN(
            "dc=apache,dc=org" ) ) );
    }


    /**
     * Class to test for endsWith
     *
     * @throws Exception
     *             anything goes wrong
     */
    @Test
    public void testEndsWith() throws Exception
    {
        DN name0 = new DN( "cn=HomeDir,cn=John,ou=Marketing,ou=East" );
        DN name1 = new DN( "cn=HomeDir,cn=John,ou=Marketing,ou=East" );
        DN name2 = new DN( "cn=John,ou=Marketing,ou=East" );
        DN name3 = new DN( "ou=Marketing,ou=East" );
        DN name4 = new DN( "ou=East" );
        DN name5 = new DN( "" );

        DN name6 = new DN( "cn=HomeDir" );
        DN name7 = new DN( "cn=HomeDir,cn=John" );
        DN name8 = new DN( "cn=HomeDir,cn=John,ou=Marketing" );

        assertTrue( name0.hasSuffix( name1 ) );
        assertTrue( !name0.hasSuffix( name2 ) );
        assertTrue( !name0.hasSuffix( name3 ) );
        assertTrue( !name0.hasSuffix( name4 ) );
        assertTrue( name0.hasSuffix( name5 ) );

        assertTrue( name0.hasSuffix( name6 ) );
        assertTrue( name0.hasSuffix( name7 ) );
        assertTrue( name0.hasSuffix( name8 ) );
    }


    /**
     * Class to test for DN addAll(DN)
     *
     * @throws Exception
     *             when anything goes wrong
     */
    @Test
    public void testAddAllName0() throws Exception
    {
        DN name = new DN();
        DN name0 = new DN( "cn=HomeDir,cn=John,ou=Marketing,ou=East" );
        assertTrue( name0.equals( name.addAll( name0 ) ) );
    }


    /**
     * Class to test for DN addAll(DN)
     *
     * @throws Exception
     *             when anything goes wrong
     */
    @Test
    public void testAddAllNameExisting0() throws Exception
    {
        DN name1 = new DN( "ou=Marketing,ou=East" );
        DN name2 = new DN( "cn=HomeDir,cn=John" );
        DN nameAdded = new DN( "cn=HomeDir,cn=John, ou=Marketing,ou=East" );
        assertTrue( nameAdded.equals( name1.addAll( name2 ) ) );
    }


    /**
     * Class to test for DN addAll(DN)
     *
     * @throws Exception
     *             when anything goes wrong
     */
    @Test
    public void testAddAllName1() throws Exception
    {
        DN name = new DN();
        DN name0 = new DN( "ou=Marketing,ou=East" );
        DN name1 = new DN( "cn=HomeDir,cn=John" );
        DN name2 = new DN( "cn=HomeDir,cn=John,ou=Marketing,ou=East" );

        name = name.addAll( name0 );
        assertTrue( name0.equals( name ) );
        assertTrue( name2.equals( name.addAll( name1 ) ) );
    }


    /**
     * Class to test for DN addAll(int, DN)
     *
     * @throws Exception
     *             when something goes wrong
     */
    @Test
    public void testAddAllintName0() throws Exception
    {
        DN name = new DN();
        DN name0 = new DN( "ou=Marketing,ou=East" );
        DN name1 = new DN( "cn=HomeDir,cn=John" );
        DN name2 = new DN( "cn=HomeDir,cn=John,ou=Marketing,ou=East" );

        name = name.addAll( name0 );
        assertTrue( name0.equals( name ) );
        assertTrue( name2.equals( name.addAll( 2, name1 ) ) );
    }


    /**
     * Class to test for DN addAll(int, DN)
     *
     * @throws Exception
     *             when something goes wrong
     */
    @Test
    public void testAddAllintName1() throws Exception
    {
        DN name = new DN();
        DN name0 = new DN( "cn=HomeDir,ou=Marketing,ou=East" );
        DN name1 = new DN( "cn=John" );
        DN name2 = new DN( "cn=HomeDir,cn=John,ou=Marketing,ou=East" );

        name = name.addAll( name0 );
        assertTrue( name0.equals( name ) );

        name = name.addAll( 2, name1 );
        assertTrue( name2.equals( name ) );

        DN name3 = new DN( "cn=Airport" );
        DN name4 = new DN( "cn=Airport,cn=HomeDir,cn=John,ou=Marketing,ou=East" );

        name = name.addAll( 4, name3 );
        assertTrue( name4.equals( name ) );

        DN name5 = new DN( "cn=ABC123" );
        DN name6 = new DN( "cn=Airport,cn=HomeDir,cn=ABC123,cn=John,ou=Marketing,ou=East" );

        assertTrue( name6.equals( name.addAll( 3, name5 ) ) );
    }


    /**
     * Class to test for DN add(String)
     *
     * @throws Exception
     *             when something goes wrong
     */
    @Test
    public void testAddString() throws Exception
    {
        DN name = new DN();
        assertEquals( name, new DN( "" ) );

        DN name4 = new DN( "ou=East" );

        assertTrue( name.isNormalized() );

        name = name.add( "ou=East" );

        assertFalse( name.isNormalized() );

        assertEquals( name4, name );

        DN name3 = new DN( "ou=Marketing,ou=East" );
        name = name.add( "ou=Marketing" );
        assertEquals( name3, name );

        DN name2 = new DN( "cn=John,ou=Marketing,ou=East" );
        name = name.add( "cn=John" );
        assertEquals( name2, name );

        DN name0 = new DN( "cn=HomeDir,cn=John,ou=Marketing,ou=East" );
        name = name.add( "cn=HomeDir" );
        assertEquals( name0, name );
    }


    /**
     * Class to test for Name add(int, String)
     *
     * @throws Exception
     *             if anything goes wrong
     */
    @Test
    public void testAddintString() throws Exception
    {
        DN name = new DN();
        assertEquals( name, new DN( "" ) );

        DN name4 = new DN( "ou=East" );
        name = name.add( "ou=East" );
        assertEquals( name4, name );

        DN name3 = new DN( "ou=Marketing,ou=East" );
        name = name.add( 1, "ou=Marketing" );
        assertEquals( name3, name );

        DN name2 = new DN( "cn=John,ou=Marketing,ou=East" );
        name = name.add( 2, "cn=John" );
        assertEquals( name2, name );

        DN name0 = new DN( "cn=HomeDir,cn=John,ou=Marketing,ou=East" );
        name = name.add( 3, "cn=HomeDir" );
        assertEquals( name0, name );

        DN name5 = new DN( "cn=HomeDir,cn=John,ou=Marketing,ou=East,o=LL " + "Bean Inc." );
        name = name.add( 0, "o=LL Bean Inc." );
        assertEquals( name5, name );

        DN name6 = new DN( "cn=HomeDir,cn=John,ou=Marketing,ou=East,c=US,o=LL " + "Bean Inc." );
        name = name.add( 1, "c=US" );
        assertEquals( name6, name );

        DN name7 = new DN( "cn=HomeDir,cn=John,ou=Advertising,ou=Marketing," + "ou=East,c=US,o=LL " + "Bean Inc." );
        name = name.add( 4, "ou=Advertising" );
        assertEquals( name7, name );
    }


    /**
     * Class to test for remove
     *
     * @throws Exception
     *             if anything goes wrong
     */
    @Test
    public void testRemove() throws Exception
    {
        DN name = new DN();
        assertEquals( new DN( "" ), name );

        DN name3 = new DN( "ou=Marketing" );
        name = name.add( "ou=East" );
        name = name.add( 1, "ou=Marketing" );
        name = name.remove( 0 );
        assertEquals( name3, name );

        DN name2 = new DN( "cn=HomeDir,ou=Marketing,ou=East" );
        name = name.add( 0, "ou=East" );
        name = name.add( 2, "cn=John" );
        name = name.add( "cn=HomeDir" );
        name = name.remove( 2 );
        assertEquals( name2, name );

        name = name.remove( 1 );
        DN name1 = new DN( "cn=HomeDir,ou=East" );
        assertEquals( name1, name );

        name = name.remove( 1 );
        DN name0 = new DN( "ou=East" );
        assertEquals( name0, name );

        name = name.remove( 0 );
        assertEquals( new DN( "" ), name );
    }


    /**
     * Class to test for String toString()
     *
     * @throws Exception
     *             if anything goes wrong
     */
    @Test
    public void testToString() throws Exception
    {
        DN name = new DN();
        assertEquals( "", name.toString() );

        name = name.add( "ou=East" );
        assertEquals( "ou=East", name.toString() );

        name = name.add( 1, "ou=Marketing" );
        assertEquals( "ou=Marketing,ou=East", name.toString() );

        name = name.add( "cn=John" );
        assertEquals( "cn=John,ou=Marketing,ou=East", name.toString() );

        name = name.add( "cn=HomeDir" );
        assertEquals( "cn=HomeDir,cn=John,ou=Marketing,ou=East", name.toString() );
    }


    /**
     * Tests getParent().
     */
    @Test
    public void testGetParent() throws Exception
    {
        DN empty = new DN();
        assertNull( empty.getParent() );

        DN one = new DN( "cn=test" );
        assertNotNull( one.getParent() );
        assertTrue( one.getParent().isEmpty() );

        DN two = new DN( "cn=test,o=acme" );
        assertNotNull( two.getParent() );
        assertFalse( two.getParent().isEmpty() );
        assertEquals( "o=acme", two.getParent().getName() );

        DN three = new DN( "cn=test,dc=example,dc=com" );
        three.normalize( oids );
        DN threeParent = three.getParent();
        assertNotNull( threeParent );
        assertFalse( threeParent.isEmpty() );
        assertEquals( "dc=example,dc=com", threeParent.getName() );
        assertEquals( 2, threeParent.getRdns().size() );

        DN five = new DN( "uid=user1,ou=sales,ou=users,dc=example,dc=com" );
        DN fiveParent = five.getParent();
        assertNotNull( fiveParent );
        assertFalse( fiveParent.isEmpty() );
        assertEquals( "ou=sales,ou=users,dc=example,dc=com", fiveParent.getName() );
        assertEquals( 4, fiveParent.getRdns().size() );
    }


    /**
     * Class to test for boolean equals(Object)
     *
     * @throws Exception
     *             if anything goes wrong
     */
    @Test
    public void testEqualsObject() throws Exception
    {
        assertTrue( new DN( "ou=People" ).equals( new DN( "ou=People" ) ) );

        assertTrue( !new DN( "ou=People,dc=example,dc=com" ).equals( new DN( "ou=People" ) ) );
        assertTrue( !new DN( "ou=people" ).equals( new DN( "ou=People" ) ) );
        assertTrue( !new DN( "ou=Groups" ).equals( new DN( "ou=People" ) ) );
    }


    @Test
    public void testNameFrenchChars() throws Exception
    {
        String cn = new String( new byte[]
            { 'c', 'n', '=', 0x4A, ( byte ) 0xC3, ( byte ) 0xA9, 0x72, ( byte ) 0xC3, ( byte ) 0xB4, 0x6D, 0x65 },
            "UTF-8" );

        DN name = new DN( cn );

        assertEquals( "cn=J\u00e9r\u00f4me", name.toString() );
    }


    @Test
    public void testNameGermanChars() throws Exception
    {
        String cn = new String( new byte[]
            { 'c', 'n', '=', ( byte ) 0xC3, ( byte ) 0x84, ( byte ) 0xC3, ( byte ) 0x96, ( byte ) 0xC3, ( byte ) 0x9C,
                ( byte ) 0xC3, ( byte ) 0x9F, ( byte ) 0xC3, ( byte ) 0xA4, ( byte ) 0xC3, ( byte ) 0xB6,
                ( byte ) 0xC3, ( byte ) 0xBC }, "UTF-8" );

        DN name = new DN( cn );

        assertEquals( "cn=\u00C4\u00D6\u00DC\u00DF\u00E4\u00F6\u00FC", name.toString() );
    }


    @Test
    public void testNameTurkishChars() throws Exception
    {
        String cn = new String( new byte[]
            { 'c', 'n', '=', ( byte ) 0xC4, ( byte ) 0xB0, ( byte ) 0xC4, ( byte ) 0xB1, ( byte ) 0xC5, ( byte ) 0x9E,
                ( byte ) 0xC5, ( byte ) 0x9F, ( byte ) 0xC3, ( byte ) 0x96, ( byte ) 0xC3, ( byte ) 0xB6,
                ( byte ) 0xC3, ( byte ) 0x9C, ( byte ) 0xC3, ( byte ) 0xBC, ( byte ) 0xC4, ( byte ) 0x9E,
                ( byte ) 0xC4, ( byte ) 0x9F }, "UTF-8" );

        DN name = new DN( cn );

        assertEquals( "cn=\u0130\u0131\u015E\u015F\u00D6\u00F6\u00DC\u00FC\u011E\u011F", name.toString() );
    }


    /**
     * Class to test for toOid( DN, Map)
     */
    @Test
    public void testLdapNameToName() throws Exception
    {
        DN name = new DN( "ou= Some   People   ", "dc = eXample", "dc= cOm" );

        assertTrue( name.getName().equals( "ou= Some   People   ,dc = eXample,dc= cOm" ) );

        DN result = DN.normalize( name, oids );

        assertTrue( ( result ).getNormName().equals( "ou=some people,dc=example,dc=com" ) );
    }


    @Test
    public void testRdnGetTypeUpName() throws Exception
    {
        DN name = new DN( "ou= Some   People   ", "dc = eXample", "dc= cOm" );

        assertTrue( name.getName().equals( "ou= Some   People   ,dc = eXample,dc= cOm" ) );

        RDN rdn = name.getRdn();

        assertEquals( "ou= Some   People   ", rdn.getName() );
        assertEquals( "ou", rdn.getNormType() );
        assertEquals( "ou", rdn.getUpType() );

        DN result = DN.normalize( name, oidOids );

        assertTrue( result.getNormName().equals(
            "2.5.4.11=some people,0.9.2342.19200300.100.1.25=example,0.9.2342.19200300.100.1.25=com" ) );
        assertTrue( name.getName().equals( "ou= Some   People   ,dc = eXample,dc= cOm" ) );

        RDN rdn2 = result.getRdn();

        assertEquals( "ou= Some   People   ", rdn2.getName() );
        assertEquals( "2.5.4.11", rdn2.getNormType() );
        assertEquals( "ou", rdn2.getUpType() );
    }


    /**
     * Class to test for toOid( DN, Map) with a NULL dn
     */
    @Test
    public void testLdapNameToNameEmpty() throws Exception
    {
        DN name = new DN();

        DN result = DN.normalize( name, oids );
        assertTrue( result.toString().equals( "" ) );
    }


    /**
     * Class to test for toOid( DN, Map) with a multiple NameComponent
     */
    @Test
    public void testLdapNameToNameMultiNC() throws Exception
    {
        DN name = new DN(
            "2.5.4.11= Some   People   + 0.9.2342.19200300.100.1.25=  And   Some anImAls,0.9.2342.19200300.100.1.25 = eXample,dc= cOm" );

        DN result = DN.normalize( name, oidOids );

        assertEquals(
            ( result ).getNormName(),
            "0.9.2342.19200300.100.1.25=and some animals+2.5.4.11=some people,0.9.2342.19200300.100.1.25=example,0.9.2342.19200300.100.1.25=com" );
        assertTrue( ( result )
            .getName()
            .equals(
                "2.5.4.11= Some   People   + 0.9.2342.19200300.100.1.25=  And   Some anImAls,0.9.2342.19200300.100.1.25 = eXample,dc= cOm" ) );
    }


    /**
     * Class to test for toOid( DN, Map) with a multiple NameComponent
     */
    @Test
    public void testLdapNameToNameAliasMultiNC() throws Exception
    {
        DN name = new DN(
            "2.5.4.11= Some   People   + domainComponent=  And   Some anImAls,DomainComponent = eXample,0.9.2342.19200300.100.1.25= cOm" );

        DN result = DN.normalize( name, oidOids );

        assertTrue( result
            .getNormName()
            .equals(
                "0.9.2342.19200300.100.1.25=and some animals+2.5.4.11=some people,0.9.2342.19200300.100.1.25=example,0.9.2342.19200300.100.1.25=com" ) );
        assertTrue( result
            .getName()
            .equals(
                "2.5.4.11= Some   People   + domainComponent=  And   Some anImAls,DomainComponent = eXample,0.9.2342.19200300.100.1.25= cOm" ) );
    }


    /**
     * Class to test for hashCode().
     */
    @Test
    public void testLdapNameHashCode() throws Exception
    {
        DN name1 = DN
            .normalize(
                "2.5.4.11= Some   People   + domainComponent=  And   Some anImAls,DomainComponent = eXample,0.9.2342.19200300.100.1.25= cOm",
                oids );

        DN name2 = DN
            .normalize(
                "2.5.4.11=some people+domainComponent=and some animals,DomainComponent=example,0.9.2342.19200300.100.1.25=com",
                oids );

        assertEquals( name1.hashCode(), name2.hashCode() );
    }


    /**
     * Test for DIRSERVER-191
     */
    @Test
    public void testName() throws LdapException, InvalidNameException
    {
        LdapName jName = new javax.naming.ldap.LdapName( "cn=four,cn=three,cn=two,cn=one" );
        DN aName = new DN( "cn=four,cn=three,cn=two,cn=one" );
        assertEquals( jName.toString(), "cn=four,cn=three,cn=two,cn=one" );
        assertEquals( aName.toString(), "cn=four,cn=three,cn=two,cn=one" );
        assertEquals( jName.toString(), aName.toString() );
    }


    /**
     * Test for DIRSERVER-191
     */
    @Test
    public void testGetPrefixName() throws LdapException, InvalidNameException
    {
        LdapName jName = new LdapName( "cn=four,cn=three,cn=two,cn=one" );
        DN aName = new DN( "cn=four,cn=three,cn=two,cn=one" );

        assertEquals( jName.getPrefix( 0 ).toString(), aName.getPrefix( 0 ).toString() );
        assertEquals( jName.getPrefix( 1 ).toString(), aName.getPrefix( 1 ).toString() );
        assertEquals( jName.getPrefix( 2 ).toString(), aName.getPrefix( 2 ).toString() );
        assertEquals( jName.getPrefix( 3 ).toString(), aName.getPrefix( 3 ).toString() );
        assertEquals( jName.getPrefix( 4 ).toString(), aName.getPrefix( 4 ).toString() );
    }


    /**
     * Test for DIRSERVER-191
     */
    @Test
    public void testGetSuffix() throws LdapException, InvalidNameException
    {
        LdapName jName = new LdapName( "cn=four,cn=three,cn=two,cn=one" );
        DN aName = new DN( "cn=four,cn=three,cn=two,cn=one" );

        assertEquals( jName.getSuffix( 0 ).toString(), aName.getSuffix( 0 ).toString() );
        assertEquals( jName.getSuffix( 1 ).toString(), aName.getSuffix( 1 ).toString() );
        assertEquals( jName.getSuffix( 2 ).toString(), aName.getSuffix( 2 ).toString() );
        assertEquals( jName.getSuffix( 3 ).toString(), aName.getSuffix( 3 ).toString() );
        assertEquals( jName.getSuffix( 4 ).toString(), aName.getSuffix( 4 ).toString() );
    }


    /**
     * Test for DIRSERVER-191
     */
    @Test
    @Ignore("from now onwards DN is not mutable")
    public void testAddStringName() throws LdapException, InvalidNameException
    {
        LdapName jName = new LdapName( "cn=four,cn=three,cn=two,cn=one" );
        DN aName = new DN( "cn=four,cn=three,cn=two,cn=one" );

        assertSame( jName, jName.add( "cn=five" ) );
        assertSame( aName, aName.add( "cn=five" ) );
        assertEquals( jName.toString(), aName.toString() );
    }


    /**
     * Test for DIRSERVER-191
     */
    @Test
    @Ignore("from now onwards DN is not mutable")
    public void testAddIntString() throws LdapException, InvalidNameException
    {
        LdapName jName = new LdapName( "cn=four,cn=three,cn=two,cn=one" );
        DN aName = new DN( "cn=four,cn=three,cn=two,cn=one" );

        assertSame( jName, jName.add( 0, "cn=zero" ) );
        assertSame( aName, aName.add( 0, "cn=zero" ) );
        assertEquals( jName.toString(), aName.toString() );

        assertSame( jName, jName.add( 2, "cn=one.5" ) );
        assertSame( aName, aName.add( 2, "cn=one.5" ) );
        assertEquals( jName.toString(), aName.toString() );

        assertSame( jName, jName.add( jName.size(), "cn=five" ) );
        assertSame( aName, aName.add( aName.size(), "cn=five" ) );
        assertEquals( jName.toString(), aName.toString() );
    }


    /**
     * Test for DIRSERVER-191
     */
    @Test
    @Ignore("from now onwards DN is not mutable")
    public void testAddAllName() throws LdapException, InvalidNameException
    {
        LdapName jName = new LdapName( "cn=four,cn=three,cn=two,cn=one" );
        DN aName = new DN( "cn=four,cn=three,cn=two,cn=one" );

        assertSame( jName, jName.addAll( new LdapName( "cn=seven,cn=six" ) ) );
        assertSame( aName, aName.addAll( new DN( "cn=seven,cn=six" ) ) );
        assertEquals( jName.toString(), aName.toString() );
    }


    /**
     * Test for DIRSERVER-191
     */
    @Test
    @Ignore("from now onwards DN is not mutable")
    public void testAddAllIntName() throws LdapException, InvalidNameException
    {
        LdapName jName = new LdapName( "cn=four,cn=three,cn=two,cn=one" );
        DN aName = new DN( "cn=four,cn=three,cn=two,cn=one" );

        assertSame( jName, jName.addAll( 0, new LdapName( "cn=zero,cn=zero.5" ) ) );
        assertSame( aName, aName.addAll( 0, new DN( "cn=zero,cn=zero.5" ) ) );
        assertEquals( jName.toString(), aName.toString() );

        assertSame( jName, jName.addAll( 2, new LdapName( "cn=zero,cn=zero.5" ) ) );
        assertSame( aName, aName.addAll( 2, new DN( "cn=zero,cn=zero.5" ) ) );
        assertEquals( jName.toString(), aName.toString() );

        assertSame( jName, jName.addAll( jName.size(), new LdapName( "cn=zero,cn=zero.5" ) ) );
        assertSame( aName, aName.addAll( aName.size(), new DN( "cn=zero,cn=zero.5" ) ) );
        assertEquals( jName.toString(), aName.toString() );
    }


    /**
     * Test for DIRSERVER-191
     */
    @Test
    public void testStartsWithName() throws LdapException, InvalidNameException
    {
        LdapName jName = new LdapName( "cn=four,cn=three,cn=two,cn=one" );
        DN aName = new DN( "cn=four,cn=three,cn=two,cn=one" );

        assertEquals( jName.startsWith( new LdapName( "cn=seven,cn=six,cn=five" ) ), aName.isChildOf( new DN(
            "cn=seven,cn=six,cn=five" ) ) );
        assertEquals( jName.startsWith( new LdapName( "cn=three,cn=two,cn=one" ) ), aName.isChildOf( new DN(
            "cn=three,cn=two,cn=one" ) ) );
    }


    /**
     * Test for DIRSERVER-191
     */
    @Test
    public void testEndsWithName() throws LdapException, InvalidNameException
    {
        // Check with DN
        DN n0 = new DN( "cn=zero" );
        DN n10 = new DN( "cn=one,cn=zero" );
        DN n210 = new DN( "cn=two,cn=one,cn=zero" );
        DN n3210 = new DN( "cn=three,cn=two,cn=one,cn=zero" );
        DN n321 = new DN( "cn=three,cn=two,cn=one" );
        DN n32 = new DN( "cn=three,cn=two" );
        DN n3 = new DN( "cn=three" );
        DN n21 = new DN( "cn=two,cn=one" );
        DN n2 = new DN( "cn=two" );
        DN n1 = new DN( "cn=one" );

        assertTrue( n3210.hasSuffix( n3 ) );
        assertTrue( n3210.hasSuffix( n32 ) );
        assertTrue( n3210.hasSuffix( n321 ) );
        assertTrue( n3210.hasSuffix( n3210 ) );

        assertTrue( n210.hasSuffix( n2 ) );
        assertTrue( n210.hasSuffix( n21 ) );
        assertTrue( n210.hasSuffix( n210 ) );

        assertTrue( n10.hasSuffix( n1 ) );
        assertTrue( n10.hasSuffix( n10 ) );

        assertTrue( n0.hasSuffix( n0 ) );
    }


    /**
     * Test for DIRSERVER-191
     */
    @Test
    @Ignore("from now onwards DN is not mutable")
    public void testRemoveName() throws LdapException, InvalidNameException
    {
        LdapName jName = new LdapName( "cn=four,cn=three,cn=two,cn=one" );
        DN aName = new DN( "cn=four,cn=three,cn=two,cn=one" );

        assertEquals( jName.remove( 0 ).toString(), aName.remove( 0 ).toString() );
        assertEquals( jName.toString(), aName.toString() );

        assertEquals( jName.remove( jName.size() - 1 ).toString(), aName.remove( aName.size() - 1 ).toString() );
        assertEquals( jName.toString(), aName.toString() );
    }


    /**
     * Test for DIRSERVER-191
     *
    @Test
    public void testGetAllName() throws LdapException, InvalidNameException
    {
        LdapName jName = new LdapName( "cn=four,cn=three,cn=two,cn=one" );
        DN aName = new DN( "cn=four,cn=three,cn=two,cn=one" );

        Enumeration<String> j = jName.getAll();
        Enumeration<String> a = aName.getAll();
        while ( j.hasMoreElements() )
        {
            assertTrue( j.hasMoreElements() );
            assertEquals( j.nextElement(), a.nextElement() );
        }
    }


    /**
     * Test for DIRSERVER-642
     * @throws LdapException
     */
    @Test
    public void testDoubleQuoteInNameDIRSERVER_642() throws LdapException, InvalidNameException
    {
        DN name1 = new DN( "cn=\"Kylie Minogue\",dc=example,dc=com" );

        String[] expected = new String[]
            { "cn=\"Kylie Minogue\"", "dc=example", "dc=com" };

        List<RDN> j = name1.getRdns();
        int count = 0;

        for ( RDN rdn : j )
        {
            assertEquals( expected[count], rdn.getName() );
            count++;
        }
    }


    /**
     * Test for DIRSERVER-642
     * @throws LdapException
     */
    @Test
    public void testDoubleQuoteInNameDIRSERVER_642_1() throws LdapException
    {
        DN dn = new DN( "cn=\" Kylie Minogue \",dc=example,dc=com" );

        assertEquals( "cn=\" Kylie Minogue \",dc=example,dc=com", dn.getName() );
        assertEquals( "cn=\\ Kylie Minogue\\ ,dc=example,dc=com", dn.getNormName() );
    }


    /**
     * Test for DIRSTUDIO-250
     * @throws LdapException
     */
    @Test
    public void testDoubleQuoteWithSpecialCharsInNameDIRSERVER_250() throws LdapException
    {
        DN dn = new DN( "a=\"b,c\"" );

        assertEquals( "a=\"b,c\"", dn.getName() );
        assertEquals( "a=b\\,c", dn.getNormName() );
    }


    /**
     * Test for DIRSERVER-184
     * @throws LdapException
     */
    @Test
    public void testLeadingAndTrailingSpacesDIRSERVER_184() throws LdapException
    {
        DN name = new DN( "dn= \\ four spaces leading and 3 trailing \\  " );

        assertEquals( "dn=\\ four spaces leading and 3 trailing \\ ", name.getNormName() );
        assertEquals( "dn= \\ four spaces leading and 3 trailing \\  ", name.getName() );
    }


    /**
     * Test for DIRSERVER-184
     * @throws LdapException
     */
    @Test
    public void testDIRSERVER_184_1()
    {
        try
        {
            new DN( "dn=middle\\ spaces" );
        }
        catch ( LdapException ine )
        {
            assertTrue( true );
        }
    }


    /**
     * Test for DIRSERVER-184
     * @throws LdapException
     */
    @Test
    public void testDIRSERVER_184_2()
    {
        try
        {
            new DN( "dn=# a leading pound" );
        }
        catch ( LdapException ine )
        {
            assertTrue( true );
        }
    }


    /**
     * Test for DIRSERVER-184
     * @throws LdapException
     */
    @Test
    public void testDIRSERVER_184_3() throws LdapException
    {
        DN name = new DN( "dn=\\# a leading pound" );

        assertEquals( "dn=\\# a leading pound", name.toString() );
        assertEquals( "dn=\\# a leading pound", name.getName() );
    }


    /**
     * Test for DIRSERVER-184
     * @throws LdapException
     */
    @Test
    public void testDIRSERVER_184_4() throws LdapException
    {
        DN name = new DN( "dn=a middle \\# pound" );

        assertEquals( "dn=a middle # pound", name.getNormName() );
        assertEquals( "dn=a middle \\# pound", name.getName() );
    }


    /**
     * Test for DIRSERVER-184
     * @throws LdapException
     */
    @Test
    public void testDIRSERVER_184_5() throws LdapException
    {
        DN name = new DN( "dn=a trailing pound \\#" );

        assertEquals( "dn=a trailing pound #", name.getNormName() );
        assertEquals( "dn=a trailing pound \\#", name.getName() );
    }


    /**
     * Test for DIRSERVER-184
     * @throws LdapException
     */
    @Test
    public void testDIRSERVER_184_6()
    {
        try
        {
            new DN( "dn=a middle # pound" );
        }
        catch ( LdapException ine )
        {
            assertTrue( true );
        }
    }


    /**
     * Test for DIRSERVER-184
     * @throws LdapException
     */
    @Test
    public void testDIRSERVER_184_7()
    {
        try
        {
            new DN( "dn=a trailing pound #" );
        }
        catch ( LdapException ine )
        {
            assertTrue( true );
        }
    }


    @Test
    public void testDIRSERVER_631_1() throws LdapException
    {
        DN name = new DN( "cn=Bush\\, Kate,dc=example,dc=com" );

        assertEquals( "cn=Bush\\, Kate,dc=example,dc=com", name.toString() );
        assertEquals( "cn=Bush\\, Kate,dc=example,dc=com", name.getName() );

    }


    /**
     * Added a test to check the parsing of a DN with more than one RDN
     * which are OIDs, and with one RDN which has more than one atav.
     * @throws LdapException
     */
    @Test
    public void testDNWithMultiOidsRDN() throws LdapException
    {
        DN name = new DN(
            "0.9.2342.19200300.100.1.1=00123456789+2.5.4.3=pablo picasso,2.5.4.11=search,2.5.4.10=imc,2.5.4.6=us" );
        assertEquals(
            "0.9.2342.19200300.100.1.1=00123456789+2.5.4.3=pablo picasso,2.5.4.11=search,2.5.4.10=imc,2.5.4.6=us", name
                .toString() );
        assertEquals(
            "0.9.2342.19200300.100.1.1=00123456789+2.5.4.3=pablo picasso,2.5.4.11=search,2.5.4.10=imc,2.5.4.6=us", name
                .getName() );
    }


    @Test
    public void testDNEquals() throws LdapException
    {
        DN dn1 = new DN( "a=b,c=d,e=f" );
        DN dn2 = new DN( "a=b\\,c\\=d,e=f" );

        assertFalse( dn1.getNormName().equals( dn2.getNormName() ) );
    }


    @Test
    public void testDNAddEmptyString() throws LdapException
    {
        DN dn = new DN();
        assertTrue( dn.size() == 0 );
        assertTrue( dn.add( "" ).size() == 0 );
    }


    /**
     * This leads to the bug in DIRSERVER-832.
     */
    @Test
    public void testPreserveAttributeIdCase() throws LdapException
    {
        DN dn = new DN( "uID=kevin" );
        assertEquals( "uID", dn.getRdn().getUpType() );
    }


    /**
     * Tests the DN.isValid() method.
     */
    @Test
    public void testIsValid()
    {
        assertTrue( DN.isValid( "" ) );

        assertFalse( DN.isValid( "a" ) );
        assertFalse( DN.isValid( "a " ) );

        assertTrue( DN.isValid( "a=" ) );
        assertTrue( DN.isValid( "a= " ) );

        assertFalse( DN.isValid( "=" ) );
        assertFalse( DN.isValid( " = " ) );
        assertFalse( DN.isValid( " = a" ) );
    }


    private ByteArrayOutputStream serializeDN( DN dn ) throws IOException
    {
        ObjectOutputStream oOut = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try
        {
            oOut = new ObjectOutputStream( out );
            oOut.writeObject( dn );
        }
        catch ( IOException ioe )
        {
            throw ioe;
        }
        finally
        {
            try
            {
                if ( oOut != null )
                {
                    oOut.flush();
                    oOut.close();
                }
            }
            catch ( IOException ioe )
            {
                throw ioe;
            }
        }

        return out;
    }


    private DN deserializeDN( ByteArrayOutputStream out ) throws IOException, ClassNotFoundException
    {
        ObjectInputStream oIn = null;
        ByteArrayInputStream in = new ByteArrayInputStream( out.toByteArray() );

        try
        {
            oIn = new ObjectInputStream( in );

            DN dn = ( DN ) oIn.readObject();

            return dn;
        }
        catch ( IOException ioe )
        {
            throw ioe;
        }
        finally
        {
            try
            {
                if ( oIn != null )
                {
                    oIn.close();
                }
            }
            catch ( IOException ioe )
            {
                throw ioe;
            }
        }
    }


    /**
     * Test the serialization of a DN
     *
     * @throws Exception
     */
    @Test
    public void testNameSerialization() throws Exception
    {
        DN dn = new DN( "ou= Some   People   + dc=  And   Some anImAls,dc = eXample,dc= cOm" );
        dn.normalize( oids );

        assertEquals( dn, deserializeDN( serializeDN( dn ) ) );
    }


    @Test
    public void testSerializeEmptyDN() throws Exception
    {
        DN dn = DN.EMPTY_DN;

        assertEquals( dn, deserializeDN( serializeDN( dn ) ) );
    }


    /**
     * Test the serialization of a DN
     *
     * @throws Exception
     */
    @Test
    public void testNameStaticSerialization() throws Exception
    {
        DN dn = new DN( "ou= Some   People   + dc=  And   Some anImAls,dc = eXample,dc= cOm" );
        dn.normalize( oids );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        DnSerializer.serialize( dn, out );
        out.flush();

        byte[] data = baos.toByteArray();
        ObjectInputStream in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        assertEquals( dn, DnSerializer.deserialize( in ) );
    }


    /*
    @Test public void testSerializationPerfs() throws Exception
    {
        DN dn = new DN( "ou= Some   People   + dc=  And   Some anImAls,dc = eXample,dc= cOm" );
        dn.normalize( oids );

        long t0 = System.currentTimeMillis();

        for ( int i = 0; i < 1000; i++ )
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream( baos );

            DnSerializer.serialize( dn, out );

            byte[] data = baos.toByteArray();
            ObjectInputStream in = new ObjectInputStream( new ByteArrayInputStream( data ) );

            DN dn1 = DnSerializer.deserialize( in );
        }

        long t1 = System.currentTimeMillis();

        System.out.println( "delta :" + ( t1 - t0) );

        long t2 = System.currentTimeMillis();

        for ( int i = 0; i < 1000000; i++ )
        {
            //ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //ObjectOutputStream out = new ObjectOutputStream( baos );

            //DnSerializer.serializeString( dn, out );

            //byte[] data = baos.toByteArray();
            //ObjectInputStream in = new ObjectInputStream( new ByteArrayInputStream( data ) );

            //DN dn1 = DnSerializer.deserializeString( in, oids );
            dn.normalize( oids );
        }

        long t3 = System.currentTimeMillis();

        System.out.println( "delta :" + ( t3 - t2) );

        //assertEquals( dn, DnSerializer.deserialize( in ) );
    }
    */

    @Test
    public void testStaticSerializeEmptyDN() throws Exception
    {
        DN dn = DN.EMPTY_DN;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        DnSerializer.serialize( dn, out );
        out.flush();

        byte[] data = baos.toByteArray();
        ObjectInputStream in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        assertEquals( dn, DnSerializer.deserialize( in ) );
        assertEquals( dn, deserializeDN( serializeDN( dn ) ) );
    }


    @Test
    public void testCompositeRDN() throws LdapException
    {
        assertTrue( DN.isValid( "a=b+c=d+e=f,g=h" ) );

        DN dn = new DN( "a=b+c=d+e=f,g=h" );

        assertEquals( "a=b+c=d+e=f,g=h", dn.toString() );
    }


    @Test
    public void testCompositeRDNOids() throws LdapException
    {
        assertTrue( DN
            .isValid( "1.2.3.4.5=0+1.2.3.4.6=0+1.2.3.4.7=omnischmomni,2.5.4.3=subtree,0.9.2342.19200300.100.1.25=example,0.9.2342.19200300.100.1.25=com" ) );

        DN dn = new DN(
            "1.2.3.4.5=0+1.2.3.4.6=0+1.2.3.4.7=omnischmomni,2.5.4.3=subtree,0.9.2342.19200300.100.1.25=example,0.9.2342.19200300.100.1.25=com" );

        assertEquals(
            "1.2.3.4.5=0+1.2.3.4.6=0+1.2.3.4.7=omnischmomni,2.5.4.3=subtree,0.9.2342.19200300.100.1.25=example,0.9.2342.19200300.100.1.25=com",
            dn.toString() );
    }


    /**
     * Tests that AttributeTypeAndValues are correctly trimmed.
     */
    @Test
    public void testTrimAtavs() throws LdapException
    {
        // antlr parser: string value with trailing spaces
        DN dn1 = new DN( " cn = Amos\\,Tori , ou=system " );
        assertEquals( " cn = Amos\\,Tori ", dn1.getRdn().getName() );
        AVA atav1 = dn1.getRdn().getAtav();
        assertEquals( "cn", atav1.getUpType() );
        assertEquals( "Amos,Tori", atav1.getUpValue().getString() );

        // antlr parser: hexstring with trailing spaces
        DN dn3 = new DN( " cn = #414243 , ou=system " );
        assertEquals( " cn = #414243 ", dn3.getRdn().getName() );
        AVA atav3 = dn3.getRdn().getAtav();
        assertEquals( "cn", atav3.getUpType() );
        assertEquals( "ABC", atav3.getUpValue().getString() );
        assertTrue( Arrays.equals( StringTools.getBytesUtf8( "ABC" ), atav3.getNormValue().getBytes() ) );

        // antlr parser:
        DN dn4 = new DN( " cn = \\41\\42\\43 , ou=system " );
        assertEquals( " cn = \\41\\42\\43 ", dn4.getRdn().getName() );
        AVA atav4 = dn4.getRdn().getAtav();
        assertEquals( "cn", atav4.getUpType() );
        assertEquals( "ABC", atav4.getUpValue().getString() );
        assertEquals( "ABC", atav4.getNormValue().getString() );

        // antlr parser: quotestring with trailing spaces
        DN dn5 = new DN( " cn = \"ABC\" , ou=system " );
        assertEquals( " cn = \"ABC\" ", dn5.getRdn().getName() );
        AVA atav5 = dn5.getRdn().getAtav();
        assertEquals( "cn", atav5.getUpType() );
        assertEquals( "ABC", atav5.getUpValue().getString() );
        assertEquals( "ABC", atav5.getNormValue().getString() );

        // fast parser: string value with trailing spaces
        DN dn2 = new DN( " cn = Amos Tori , ou=system " );
        assertEquals( " cn = Amos Tori ", dn2.getRdn().getName() );
        AVA atav2 = dn2.getRdn().getAtav();
        assertEquals( "cn", atav2.getUpType() );
        assertEquals( "Amos Tori", atav2.getUpValue().getString() );
    }


    /**
     * Test for DIRSHARED-39.
     * (Trailing escaped space not parsed correctly by the DN parser(
     */
    @Test
    public void testTrailingEscapedSpace() throws Exception
    {
        DN dn1 = new DN( "ou=A\\ ,ou=system" );
        dn1.normalize( oids );
        assertEquals( "ou=A\\ ,ou=system", dn1.getName() );
        assertEquals( "ou=a,ou=system", dn1.getNormName() );
        assertEquals( "ou=A\\ ", dn1.getRdn().getName() );
        assertEquals( "ou=a", dn1.getRdn().getNormName() );

        DN dn2 = new DN( "ou=A\\20,ou=system" );
        dn2.normalize( oids );
        assertEquals( "ou=A\\20,ou=system", dn2.getName() );
        assertEquals( "ou=a,ou=system", dn2.getNormName() );
        assertEquals( "ou=A\\20", dn2.getRdn().getName() );
        assertEquals( "ou=a", dn2.getRdn().getNormName() );

        DN dn3 = new DN( "ou=\\ ,ou=system" );
        dn3.normalize( oids );
        assertEquals( "ou=\\ ,ou=system", dn3.getName() );
        assertEquals( "ou=,ou=system", dn3.getNormName() );
        assertEquals( "ou=\\ ", dn3.getRdn().getName() );
        assertEquals( "ou=", dn3.getRdn().getNormName() );

        DN dn4 = new DN( "ou=\\20,ou=system" );
        dn4.normalize( oids );
        assertEquals( "ou=\\20,ou=system", dn4.getName() );
        assertEquals( "ou=,ou=system", dn4.getNormName() );
        assertEquals( "ou=\\20", dn4.getRdn().getName() );
        assertEquals( "ou=", dn4.getRdn().getNormName() );
    }


    /**
     * Test for DIRSHARED-41, DIRSTUDIO-603.
     * (DN parser fails to parse names containing an numeric OID value)
     */
    @Test
    public void testNumericOid() throws Exception
    {
        // numeric OID only
        DN dn1 = new DN( "cn=loopback+ipHostNumber=127.0.0.1,ou=Hosts,dc=mygfs,dc=com" );
        assertEquals( "cn=loopback+ipHostNumber=127.0.0.1,ou=Hosts,dc=mygfs,dc=com", dn1.getName() );
        assertEquals( "cn=loopback+iphostnumber=127.0.0.1,ou=Hosts,dc=mygfs,dc=com", dn1.getNormName() );
        assertEquals( "cn=loopback+ipHostNumber=127.0.0.1", dn1.getRdn().getName() );
        assertEquals( "cn=loopback+iphostnumber=127.0.0.1", dn1.getRdn().getNormName() );
        assertEquals( "127.0.0.1", dn1.getRdn().getAttributeTypeAndValue( "ipHostNumber" ).getUpValue().get() );

        // numeric OID with suffix
        DN dn2 = new DN( "cn=loopback+ipHostNumber=X127.0.0.1,ou=Hosts,dc=mygfs,dc=com" );
        assertEquals( "cn=loopback+ipHostNumber=X127.0.0.1,ou=Hosts,dc=mygfs,dc=com", dn2.getName() );
        assertEquals( "cn=loopback+iphostnumber=X127.0.0.1,ou=Hosts,dc=mygfs,dc=com", dn2.getNormName() );
        assertEquals( "cn=loopback+ipHostNumber=X127.0.0.1", dn2.getRdn().getName() );
        assertEquals( "cn=loopback+iphostnumber=X127.0.0.1", dn2.getRdn().getNormName() );

        // numeric OID with prefix
        DN dn3 = new DN( "cn=loopback+ipHostNumber=127.0.0.1Y,ou=Hosts,dc=mygfs,dc=com" );
        assertEquals( "cn=loopback+ipHostNumber=127.0.0.1Y,ou=Hosts,dc=mygfs,dc=com", dn3.getName() );
        assertEquals( "cn=loopback+iphostnumber=127.0.0.1Y,ou=Hosts,dc=mygfs,dc=com", dn3.getNormName() );
        assertEquals( "cn=loopback+ipHostNumber=127.0.0.1Y", dn3.getRdn().getName() );
        assertEquals( "cn=loopback+iphostnumber=127.0.0.1Y", dn3.getRdn().getNormName() );

        // numeric OID with special characters
        DN dn4 = new DN( "cn=loopback+ipHostNumber=\\#127.0.0.1 Z,ou=Hosts,dc=mygfs,dc=com" );
        assertEquals( "cn=loopback+ipHostNumber=\\#127.0.0.1 Z,ou=Hosts,dc=mygfs,dc=com", dn4.getName() );
        assertEquals( "cn=loopback+iphostnumber=\\#127.0.0.1 Z,ou=Hosts,dc=mygfs,dc=com", dn4.getNormName() );
        assertEquals( "cn=loopback+ipHostNumber=\\#127.0.0.1 Z", dn4.getRdn().getName() );
        assertEquals( "cn=loopback+iphostnumber=\\#127.0.0.1 Z", dn4.getRdn().getNormName() );
    }


    @Test
    public void testNormalizeAscii() throws Exception
    {
        DN dn = new DN( "  ou  =  Example ,  ou  =  COM " );

        dn.normalize( oidOids );
        assertEquals( "2.5.4.11=example,2.5.4.11=com", dn.getNormName() );
        assertEquals( "  ou  =  Example ,  ou  =  COM ", dn.getName() );

        RDN rdn = dn.getRdn();
        assertEquals( "2.5.4.11", rdn.getNormType() );
        assertEquals( "example", rdn.getNormValue().getString() );
        assertEquals( "2.5.4.11=example", rdn.getNormName() );
        assertEquals( "ou", rdn.getUpType() );
        assertEquals( "Example", rdn.getUpValue().getString() );
        assertEquals( "  ou  =  Example ", rdn.getName() );

        AVA atav = rdn.getAtav();

        assertEquals( "2.5.4.11=example", atav.getNormName() );
        assertEquals( "2.5.4.11", atav.getNormType() );
        assertEquals( "example", atav.getNormValue().get() );

        assertEquals( "ou", atav.getUpType() );
        assertEquals( "Example", atav.getUpValue().get() );

        assertEquals( "  ou  =  Example ", atav.getUpName() );
    }


    @Test
    public void testNormalizeAsciiComposite() throws Exception
    {
        DN dn = new DN( "  ou  =  Example + ou = TEST ,  ou  =  COM " );

        dn.normalize( oidOids );
        assertEquals( "2.5.4.11=example+2.5.4.11=test,2.5.4.11=com", dn.getNormName() );
        assertEquals( "  ou  =  Example + ou = TEST ,  ou  =  COM ", dn.getName() );

        RDN rdn = dn.getRdn();
        assertEquals( "2.5.4.11", rdn.getNormType() );
        assertEquals( "example", rdn.getNormValue().getString() );
        assertEquals( "2.5.4.11=example+2.5.4.11=test", rdn.getNormName() );
        assertEquals( "ou", rdn.getUpType() );
        assertEquals( "Example", rdn.getUpValue().getString() );
        assertEquals( "  ou  =  Example + ou = TEST ", rdn.getName() );

        // The first ATAV
        AVA atav = rdn.getAtav();

        assertEquals( "2.5.4.11=example", atav.getNormName() );
        assertEquals( "2.5.4.11", atav.getNormType() );
        assertEquals( "example", atav.getNormValue().get() );

        assertEquals( "ou", atav.getUpType() );
        assertEquals( "Example", atav.getUpValue().get() );

        assertEquals( "  ou  =  Example ", atav.getUpName() );

        assertEquals( 2, rdn.getNbAtavs() );

        // The second ATAV
        for ( AVA ava : rdn )
        {
            if ( "example".equals( ava.getNormValue().get() ) )
            {
                // Skip the first one
                continue;
            }

            assertEquals( "2.5.4.11=test", ava.getNormName() );
            assertEquals( "2.5.4.11", ava.getNormType() );
            assertEquals( "test", ava.getNormValue().get() );

            assertEquals( "ou", ava.getUpType() );
            assertEquals( "TEST", ava.getUpValue().get() );
            assertEquals( " ou = TEST ", ava.getUpName() );
        }
    }


    @Test
    public void testNormalizeAsciiWithEscaped() throws Exception
    {
        DN dn = new DN( "  ou  =  Ex\\+mple ,  ou  =  COM " );

        dn.normalize( oidOids );
        assertEquals( "2.5.4.11=ex\\+mple,2.5.4.11=com", dn.getNormName() );
        assertEquals( "  ou  =  Ex\\+mple ,  ou  =  COM ", dn.getName() );

        RDN rdn = dn.getRdn();
        assertEquals( "2.5.4.11", rdn.getNormType() );
        assertEquals( "ex+mple", rdn.getNormValue().getString() );
        assertEquals( "2.5.4.11=ex\\+mple", rdn.getNormName() );
        assertEquals( "ou", rdn.getUpType() );
        assertEquals( "Ex+mple", rdn.getUpValue().getString() );
        assertEquals( "  ou  =  Ex\\+mple ", rdn.getName() );

        AVA atav = rdn.getAtav();

        assertEquals( "2.5.4.11=ex\\+mple", atav.getNormName() );
        assertEquals( "2.5.4.11", atav.getNormType() );
        assertEquals( "ex+mple", atav.getNormValue().get() );

        assertEquals( "ou", atav.getUpType() );
        assertEquals( "Ex+mple", atav.getUpValue().get() );

        assertEquals( "  ou  =  Ex\\+mple ", atav.getUpName() );
    }


    @Test
    public void testNormalizeCompositeWithEscaped() throws Exception
    {
        DN dn = new DN( "  OU  =  Ex\\+mple + ou = T\\+ST\\  ,  ou  =  COM " );

        // ------------------------------------------------------------------
        // Before normalization
        assertEquals( "  OU  =  Ex\\+mple + ou = T\\+ST\\  ,  ou  =  COM ", dn.getName() );
        assertEquals( "ou=Ex\\+mple+ou=T\\+ST\\ ,ou=COM", dn.getNormName() );

        // Check the first RDN
        RDN rdn = dn.getRdn();
        assertEquals( "  OU  =  Ex\\+mple + ou = T\\+ST\\  ", rdn.getName() );
        assertEquals( "ou=Ex\\+mple+ou=T\\+ST\\ ", rdn.getNormName() );

        assertEquals( "OU", rdn.getUpType() );
        assertEquals( "ou", rdn.getNormType() );

        assertEquals( "Ex+mple", rdn.getUpValue().getString() );
        assertEquals( "Ex+mple", rdn.getNormValue().getString() );

        // The first ATAV
        AVA atav = rdn.getAtav();

        assertEquals( "  OU  =  Ex\\+mple ", atav.getUpName() );
        assertEquals( "ou=Ex\\+mple", atav.getNormName() );

        assertEquals( "ou", atav.getNormType() );
        assertEquals( "OU", atav.getUpType() );

        assertEquals( "Ex+mple", atav.getUpValue().get() );
        assertEquals( "Ex+mple", atav.getNormValue().get() );

        assertEquals( 2, rdn.getNbAtavs() );

        // The second ATAV
        for ( AVA ava : rdn )
        {
            if ( "Ex+mple".equals( ava.getNormValue().get() ) )
            {
                // Skip the first one
                continue;
            }

            assertEquals( " ou = T\\+ST\\  ", ava.getUpName() );
            assertEquals( "ou=T\\+ST\\ ", ava.getNormName() );

            assertEquals( "ou", ava.getUpType() );
            assertEquals( "ou", ava.getNormType() );

            assertEquals( "T+ST ", ava.getUpValue().get() );
            assertEquals( "T+ST ", ava.getNormValue().get() );
        }

        // ------------------------------------------------------------------
        // Now normalize the DN
        dn.normalize( oidOids );

        assertEquals( "  OU  =  Ex\\+mple + ou = T\\+ST\\  ,  ou  =  COM ", dn.getName() );
        assertEquals( "2.5.4.11=ex\\+mple+2.5.4.11=t\\+st,2.5.4.11=com", dn.getNormName() );

        // Check the first RDN
        rdn = dn.getRdn();
        assertEquals( "  OU  =  Ex\\+mple + ou = T\\+ST\\  ", rdn.getName() );
        assertEquals( "2.5.4.11=ex\\+mple+2.5.4.11=t\\+st", rdn.getNormName() );

        assertEquals( "OU", rdn.getUpType() );
        assertEquals( "2.5.4.11", rdn.getNormType() );

        assertEquals( "Ex+mple", rdn.getUpValue().getString() );
        assertEquals( "ex+mple", rdn.getNormValue().getString() );

        // The first ATAV
        atav = rdn.getAtav();

        assertEquals( "  OU  =  Ex\\+mple ", atav.getUpName() );
        assertEquals( "2.5.4.11=ex\\+mple", atav.getNormName() );

        assertEquals( "2.5.4.11", atav.getNormType() );
        assertEquals( "OU", atav.getUpType() );

        assertEquals( "Ex+mple", atav.getUpValue().get() );
        assertEquals( "ex+mple", atav.getNormValue().get() );

        assertEquals( 2, rdn.getNbAtavs() );

        // The second ATAV
        for ( AVA ava : rdn )
        {
            if ( "ex+mple".equals( ava.getNormValue().get() ) )
            {
                // Skip the first one
                continue;
            }

            assertEquals( " ou = T\\+ST\\  ", ava.getUpName() );
            assertEquals( "2.5.4.11=t\\+st", ava.getNormName() );

            assertEquals( "ou", ava.getUpType() );
            assertEquals( "2.5.4.11", ava.getNormType() );

            assertEquals( "T+ST ", ava.getUpValue().get() );
            assertEquals( "t+st", ava.getNormValue().get() );
        }
    }


    //-------------------------------------------------------------------------
    // test the iterator
    //-------------------------------------------------------------------------
    @Test
    public void testIteratorNullDN()
    {
        DN dn = DN.EMPTY_DN;

        for ( RDN rdn : dn )
        {
            fail( "Should not be there" );
        }

        assertTrue( true );
    }


    @Test
    public void testIteratorOneRDN() throws Exception
    {
        DN dn = new DN( "ou=example" );
        int count = 0;

        for ( RDN rdn : dn )
        {
            count++;
            assertEquals( "ou=example", rdn.getName() );
        }

        assertEquals( 1, count );
    }


    @Test
    public void testIteratorMultipleRDN() throws Exception
    {
        DN dn = new DN( "sn=joe+cn=doe,dc=apache,dc=org" );
        int count = 0;

        String[] expected = new String[]
            { "sn=joe+cn=doe", "dc=apache", "dc=org" };

        for ( RDN rdn : dn.getRdns() )
        {
            assertEquals( expected[count], rdn.getName() );
            count++;
        }

        assertEquals( 3, count );
    }


    @Test
    public void testIsParentOfTrue() throws Exception
    {
        DN dn = new DN( "ou=example, dc=apache, dc=org" );
        DN parent1 = new DN( "ou=example,dc=apache, dc=org" );
        DN parent2 = new DN( "dc=apache, dc=org" );
        DN parent3 = new DN( "dc=org" );
        DN notParent = new DN( "ou=example,dc=apache, dc=com" );

        assertTrue( parent1.isParentOf( dn ) );
        assertTrue( parent2.isParentOf( dn ) );
        assertTrue( parent3.isParentOf( dn ) );
        assertFalse( notParent.isParentOf( dn ) );
    }


    @Test
    public void testIsChildOfTrue() throws Exception
    {
        DN dn = new DN( "ou=example, dc=apache, dc=org" );
        DN parent1 = new DN( "ou=example,dc=apache, dc=org" );
        DN parent2 = new DN( "dc=apache, dc=org" );
        DN parent3 = new DN( "dc=org" );
        DN notParent = new DN( "dc=apache, dc=com" );

        assertTrue( dn.isChildOf( parent1 ) );
        assertTrue( dn.isChildOf( parent2 ) );
        assertTrue( dn.isChildOf( parent3 ) );
        assertFalse( notParent.isChildOf( dn ) );
    }


    @Test
    public void testNormalize() throws Exception
    {
        DN dn = new DN( "ou=system" );
        assertFalse( dn.isNormalized() );

        dn = dn.add( "ou=users" );
        assertFalse( dn.isNormalized() );

        dn.normalize( oidOids );
        assertTrue( dn.isNormalized() );

        dn = dn.add( "ou=x" );
        assertFalse( dn.isNormalized() );

        assertEquals( "ou=x,2.5.4.11=users,2.5.4.11=system", dn.getNormName() );
        assertEquals( "ou=x,ou=users,ou=system", dn.getName() );

        dn.normalize( oidOids );
        assertEquals( "2.5.4.11=x,2.5.4.11=users,2.5.4.11=system", dn.getNormName() );
        assertEquals( "ou=x,ou=users,ou=system", dn.getName() );

        RDN rdn = new RDN( "ou=system" );
        dn = new DN();
        assertTrue( dn.isNormalized() );

        dn = dn.add( rdn );
        assertFalse( dn.isNormalized() );

        dn.normalize( oidOids );
        assertTrue( dn.isNormalized() );

        DN anotherDn = new DN( "ou=x,ou=users" );

        dn = dn.addAll( anotherDn );
        assertFalse( dn.isNormalized() );

        dn.normalize( oidOids );
        assertTrue( dn.isNormalized() );

        dn = dn.remove( 0 );
        assertTrue( dn.isNormalized() );
    }


    @Test
    public void testParseDnWithSlash() throws Exception
    {
        String dnStr = "dc=/vehicles/v1/";

        DN dn = new DN( dnStr );
        dn.normalize( oids );

        assertEquals( dnStr, dn.toString() );
    }
}
