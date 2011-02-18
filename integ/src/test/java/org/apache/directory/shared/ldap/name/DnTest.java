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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;

import org.apache.directory.shared.ldap.model.exception.LdapException;
import org.apache.directory.shared.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.model.name.Ava;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.shared.ldap.model.name.DnParser;
import org.apache.directory.shared.ldap.model.name.DnSerializer;
import org.apache.directory.shared.ldap.model.name.Rdn;
import org.apache.directory.shared.ldap.model.schema.SchemaManager;
import org.apache.directory.shared.ldap.schemamanager.impl.DefaultSchemaManager;
import org.apache.directory.shared.util.Strings;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mycila.junit.concurrent.Concurrency;
import com.mycila.junit.concurrent.ConcurrentJunitRunner;


/**
 * Test the class Dn
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrency()
public class DnTest
{
    private static SchemaManager schemaManager;

    /**
     * Initialize OIDs maps for normalization
     */
    @BeforeClass
    public static void setup() throws Exception
    {
        schemaManager = new DefaultSchemaManager();
    }


    // ------------------------------------------------------------------------------------
    // CONSTRUCTOR functions --------------------------------------------------

    /**
     * Test a null Dn
     */
    @Test
    public void testDnNull()
    {
        Dn dn = new Dn();
        assertEquals( "", dn.getName() );
        assertEquals( "", dn.getNormName() );
        assertTrue( dn.isEmpty() );
    }


    /**
     * test an empty Dn
     */
    @Test
    public void testDnEmpty() throws LdapException
    {
        Dn dn = new Dn( "" );
        assertEquals( "", dn.getName() );
        assertTrue( dn.isEmpty() );
    }


    /**
     * test a simple Dn : a = b
     */
    @Test
    public void testDnSimple() throws LdapException
    {
        Dn dn = new Dn( "a = b" );

        assertTrue( Dn.isValid("a = b") );
        assertEquals( "a = b", dn.getName() );
        assertEquals( "a=b", dn.getNormName() );
    }


    /**
     * test a simple Dn with some spaces : "a = b  "
     */
    @Test
    public void testDnSimpleWithSpaces() throws LdapException
    {
        Dn dn = new Dn( "a = b  " );

        assertTrue( Dn.isValid("a = b  ") );
        assertEquals( "a = b  ", dn.getName() );
        assertEquals( "a=b", dn.getNormName() );
    }


    /**
     * test a composite Dn : a = b, d = e
     */
    @Test
    public void testDnComposite() throws LdapException
    {
        Dn dn = new Dn( "a = b, c = d" );

        assertTrue( Dn.isValid("a = b, c = d") );
        assertEquals( "a=b,c=d", dn.getNormName() );
        assertEquals( "a = b, c = d", dn.getName() );
    }


    /**
     * test a composite Dn with spaces : a = b  , d = e
     */
    @Test
    public void testDnCompositeWithSpaces() throws LdapException
    {
        Dn dn = new Dn( "a = b  , c = d" );

        assertTrue( Dn.isValid("a = b  , c = d") );
        assertEquals( "a=b,c=d", dn.getNormName() );
        assertEquals( "a = b  , c = d", dn.getName() );
    }


    /**
     * test a composite Dn with or without spaces: a=b, a =b, a= b, a = b, a = b
     */
    @Test
    public void testDnCompositeWithSpace() throws LdapException
    {
        Dn dn = new Dn( "a=b, a =b, a= b, a = b, a  =  b" );

        assertTrue( Dn.isValid("a=b, a =b, a= b, a = b, a  =  b") );
        assertEquals( "a=b,a=b,a=b,a=b,a=b", dn.getNormName() );
        assertEquals( "a=b, a =b, a= b, a = b, a  =  b", dn.getName() );
    }


    /**
     * test a composite Dn with differents separators : a=b;c=d,e=f It should
     * return a=b,c=d,e=f (the ';' is replaced by a ',')
     */
    @Test
    public void testDnCompositeSepators() throws LdapException
    {
        Dn dn = new Dn( "a=b;c=d,e=f" );

        assertTrue( Dn.isValid("a=b;c=d,e=f") );
        assertEquals( "a=b,c=d,e=f", dn.getNormName() );
        assertEquals( "a=b;c=d,e=f", dn.getName() );
    }


    /**
     * test a simple Dn with multiple NameComponents : a = b + c = d
     */
    @Test
    public void testDnSimpleMultivaluedAttribute() throws LdapException
    {
        Dn dn = new Dn( "a = b + c = d" );

        assertTrue( Dn.isValid("a = b + c = d") );
        assertEquals( "a=b+c=d", dn.getNormName() );
        assertEquals( "a = b + c = d", dn.getName() );
    }


    /**
     * test a composite Dn with multiple NC and separators : a=b+c=d, e=f + g=h +
     * i=j
     */
    @Test
    public void testDnCompositeMultivaluedAttribute() throws LdapException
    {
        Dn dn = new Dn( "a=b+c=d, e=f + g=h + i=j" );

        assertTrue( Dn.isValid("a=b+c=d, e=f + g=h + i=j") );
        assertEquals( "a=b+c=d,e=f+g=h+i=j", dn.getNormName() );
        assertEquals( "a=b+c=d, e=f + g=h + i=j", dn.getName() );
    }


    /**
    * Test to see if a Dn with multiRdn values is preserved after an addAll.
    */
    @Test
    public void testAddAllWithMultivaluedAttribute() throws LdapException
    {
        Dn dn = new Dn( "cn=Kate Bush+sn=Bush,ou=system" );
        Dn target = new Dn();

        assertTrue( Dn.isValid("cn=Kate Bush+sn=Bush,ou=system") );
        target = target.addAll( target.size(), dn );
        assertEquals( "cn=Kate Bush+sn=Bush,ou=system", target.toString() );
        assertEquals( "cn=Kate Bush+sn=Bush,ou=system", target.getName() );
    }


    /**
     * test a simple Dn with an oid prefix (uppercase) : OID.12.34.56 = azerty
     */
    @Test
    public void testDnOidUpper() throws LdapException
    {
        Dn dn = new Dn( "OID.12.34.56 = azerty" );

        assertTrue( Dn.isValid("OID.12.34.56 = azerty") );
        assertEquals( "oid.12.34.56=azerty", dn.getNormName() );
        assertEquals( "OID.12.34.56 = azerty", dn.getName() );
    }


    /**
     * test a simple Dn with an oid prefix (lowercase) : oid.12.34.56 = azerty
     */
    @Test
    public void testDnOidLower() throws LdapException
    {
        Dn dn = new Dn( "oid.12.34.56 = azerty" );

        assertTrue( Dn.isValid("oid.12.34.56 = azerty") );
        assertEquals( "oid.12.34.56=azerty", dn.getNormName() );
        assertEquals( "oid.12.34.56 = azerty", dn.getName() );
    }


    /**
     * test a simple Dn with an oid attribut without oid prefix : 12.34.56 =
     * azerty
     */
    @Test
    public void testDnOidWithoutPrefix() throws LdapException
    {
        Dn dn = new Dn( "12.34.56 = azerty" );

        assertTrue( Dn.isValid("12.34.56 = azerty") );
        assertEquals( "12.34.56=azerty", dn.getNormName() );
        assertEquals( "12.34.56 = azerty", dn.getName() );
    }


    /**
     * test a composite Dn with an oid attribut wiithout oid prefix : 12.34.56 =
     * azerty; 7.8 = test
     */
    @Test
    public void testDnCompositeOidWithoutPrefix() throws LdapException
    {
        Dn dn = new Dn( "12.34.56 = azerty; 7.8 = test" );

        assertTrue( Dn.isValid("12.34.56 = azerty; 7.8 = test") );
        assertEquals( "12.34.56=azerty,7.8=test", dn.getNormName() );
        assertEquals( "12.34.56 = azerty; 7.8 = test", dn.getName() );
    }


    /**
     * test a simple Dn with pair char attribute value : a = \,\=\+\<\>\#\;\\\"\C4\8D"
     */
    @Test
    public void testDnPairCharAttributeValue() throws LdapException
    {
        Dn dn = new Dn( "a = \\,\\=\\+\\<\\>\\#\\;\\\\\\\"\\C4\\8D" );

        assertTrue( Dn.isValid("a = \\,\\=\\+\\<\\>\\#\\;\\\\\\\"\\C4\\8D") );
        assertEquals( "a=\\,=\\+\\<\\>#\\;\\\\\\\"\u010D", dn.getNormName() );
        assertEquals( "a = \\,\\=\\+\\<\\>\\#\\;\\\\\\\"\\C4\\8D", dn.getName() );
    }


    /**
     * test a simple Dn with pair char attribute value : "SN=Lu\C4\8Di\C4\87"
     */
    @Test
    public void testDnRFC253_Lucic() throws LdapException
    {
        Dn dn = new Dn( "SN=Lu\\C4\\8Di\\C4\\87" );

        assertTrue( Dn.isValid("SN=Lu\\C4\\8Di\\C4\\87") );
        assertEquals( "sn=Lu\u010Di\u0107", dn.getNormName() );
        assertEquals( "SN=Lu\\C4\\8Di\\C4\\87", dn.getName() );
    }


    /**
     * test a simple Dn with hexString attribute value : a = #0010A0AAFF
     */
    @Test
    public void testDnHexStringAttributeValue() throws LdapException
    {
        Dn dn = new Dn( "a = #0010A0AAFF" );

        assertTrue( Dn.isValid("a = #0010A0AAFF") );
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
        Dn dn = new Dn( "a = \\#123456" );

        assertTrue( Dn.isValid("a = \\#123456") );
        assertEquals( "a=\\#123456", dn.getNormName() );
        assertEquals( "a = \\#123456", dn.getName() );

        Rdn rdn = dn.getRdn();
        assertEquals( "a = \\#123456", rdn.getName() );

        assertTrue( Dn.isValid("a = \\#00") );
        assertTrue( Dn.isValid("a = \\#11") );
        assertTrue( Dn.isValid("a = \\#99") );
        assertTrue( Dn.isValid("a = \\#AA") );
        assertTrue( Dn.isValid("a = \\#FF") );

        assertTrue( Dn.isValid("uid=\\#123456") );
        assertTrue( Dn.isValid("cn=\\#ACL_AD-Projects_Author,ou=Notes_Group,o=Contacts,c=DE") );
        assertTrue( Dn.isValid("cn=\\#Abraham") );
    }


    /**
      * test a simple Dn with a # on first position
      */
    @Test
    public void testDnSharpFirst() throws LdapException, LdapException
    {
        Dn dn = new Dn( "a = \\#this is a sharp" );

        assertTrue( Dn.isValid("a = \\#this is a sharp") );
        assertEquals( "a=\\#this is a sharp", dn.getNormName() );
        assertEquals( "a = \\#this is a sharp", dn.getName() );

        Rdn rdn = dn.getRdn();
        assertEquals( "a = \\#this is a sharp", rdn.getName() );
    }


    /**
     * Normalize a simple Dn with a # on first position
     */
    @Test
    public void testNormalizeDnSharpFirst() throws LdapException, LdapException
    {
        Dn dn = new Dn( "ou = \\#this is a sharp" );

        assertTrue( Dn.isValid("ou = \\#this is a sharp") );
        assertEquals( "ou=\\#this is a sharp", dn.getNormName() );
        assertEquals( "ou = \\#this is a sharp", dn.getName() );

        // Check the normalization now
        Dn ndn = dn.normalize( schemaManager );

        assertEquals( "ou = \\#this is a sharp", ndn.getName() );
        assertEquals( "2.5.4.11=\\#this is a sharp", ndn.getNormName() );
    }


    /**
     * Normalize a Dn with sequence ESC ESC HEX HEX (\\DC).
     * This is a corner case for the parser and normalizer.
     */
    @Test
    public void testNormalizeDnEscEscHexHex() throws LdapException
    {
        Dn dn = new Dn( "ou = AC\\\\DC" );
        assertTrue( Dn.isValid("ou = AC\\\\DC") );
        assertEquals( "ou=AC\\\\DC", dn.getNormName() );
        assertEquals( "ou = AC\\\\DC", dn.getName() );

        // Check the normalization now
        Dn ndn = dn.normalize( schemaManager );
        assertEquals( "ou = AC\\\\DC", ndn.getName() );
        assertEquals( "2.5.4.11=ac\\\\dc", ndn.getNormName() );
    }


    /**
     * test a simple Dn with a wrong hexString attribute value : a = #0010Z0AAFF
     */
    @Test
    public void testDnWrongHexStringAttributeValue()
    {
        try
        {
            new Dn( "a = #0010Z0AAFF" );
            fail();
        }
        catch ( LdapException ine )
        {

            assertFalse( Dn.isValid("a = #0010Z0AAFF") );
            assertTrue( true );
        }
    }


    /**
     * test a simple Dn with a wrong hexString attribute value : a = #AABBCCDD3
     */
    @Test
    public void testDnWrongHexStringAttributeValue2()
    {
        try
        {
            new Dn( "a = #AABBCCDD3" );
            fail();
        }
        catch ( LdapException ine )
        {
            assertFalse( Dn.isValid("a = #AABBCCDD3") );
            assertTrue( true );
        }
    }


    /**
     * test a simple Dn with a quote in attribute value : a = quoted \"value\"
     */
    @Test
    public void testDnQuoteInAttributeValue() throws LdapException
    {
        Dn dn = new Dn( "a = quoted \\\"value\\\"" );

        assertTrue( Dn.isValid("a = quoted \\\"value\\\"") );
        assertEquals( "a=quoted \\\"value\\\"", dn.getNormName() );
        assertEquals( "a = quoted \\\"value\\\"", dn.getName() );
    }


    /**
     * test a simple Dn with quoted attribute value : a = \" quoted value \"
     */
    @Test
    public void testDnQuotedAttributeValue() throws LdapException
    {
        Dn dn = new Dn( "a = \\\" quoted value \\\"" );

        assertTrue( Dn.isValid("a = \\\" quoted value \\\"") );
        assertEquals( "a=\\\" quoted value \\\"", dn.getNormName() );
        assertEquals( "a = \\\" quoted value \\\"", dn.getName() );
    }


    /**
     * test a simple Dn with a comma at the end
     */
    @Test
    public void testDnComaAtEnd()
    {
        assertFalse( Dn.isValid("a = b,") );
        assertFalse( Dn.isValid("a = b, ") );

        try
        {
            new Dn( "a = b," );
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
        Dn dn = new Dn( "a=b, c=d, e=f" );

        assertTrue( Dn.isValid("a=b, c=d, e=f") );
        // now remove method returns a modified cloned Dn
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
        Dn dn = new Dn( "a=b, c=d, e=f" );

        assertTrue( Dn.isValid("a=b, c=d, e=f") );
        assertEquals( "a=b, c=d, e=f", dn.getName() );
    }


    /**
     * test a remove from position 2
     */
    @Test
    public void testDnRemove2() throws LdapException
    {
        Dn dn = new Dn( "a=b, c=d, e=f" );

        assertTrue( Dn.isValid("a=b, c=d, e=f") );
        dn = dn.remove( 2 );
        assertEquals( " c=d, e=f", dn.getName() );
    }


    /**
     * test a remove from position 1 whith semi colon
     */
    @Test
    public void testDnRemove1WithSemiColon() throws LdapException
    {
        Dn dn = new Dn( "a=b, c=d; e=f" );

        assertTrue( Dn.isValid("a=b, c=d; e=f") );
        dn = dn.remove( 1 );
        assertEquals( "a=b, e=f", dn.getName() );
    }


    /**
     * test a remove out of bound
     */
    @Test
    public void testDnRemoveOutOfBound() throws LdapException
    {
        Dn dn = new Dn( "a=b, c=d; e=f" );

        assertTrue( Dn.isValid("a=b, c=d; e=f") );

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
        Dn dn = new Dn();

        assertTrue( Dn.isValid("") );
        assertEquals( 0, dn.size() );
    }


    /**
     * test a 1 size
     */
    @Test
    public void testDnSize1() throws LdapException
    {
        Dn dn = new Dn( "a=b" );

        assertTrue( Dn.isValid("a=b") );
        assertEquals( 1, dn.size() );
    }


    /**
     * test a 3 size
     */
    @Test
    public void testDnSize3() throws LdapException
    {
        Dn dn = new Dn( "a=b, c=d, e=f" );

        assertTrue( Dn.isValid("a=b, c=d, e=f") );
        assertEquals( 3, dn.size() );
    }


    /**
     * test a 3 size with NameComponents
     */
    @Test
    public void testDnSize3NC() throws LdapException
    {
        Dn dn = new Dn( "a=b+c=d, c=d, e=f" );

        assertTrue( Dn.isValid("a=b+c=d, c=d, e=f") );
        assertEquals( 3, dn.size() );
    }


    /**
     * test size after operations
     */
    @Test
    public void testLdapResizing() throws LdapException
    {
        Dn dn = new Dn();
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
     * test Add on a new Dn
     */
    @Test
    public void testLdapEmptyAdd() throws LdapException
    {
        Dn dn = new Dn();

        dn = dn.add( "e = f" );
        assertEquals( "e=f", dn.getNormName() );
        assertEquals( "e = f", dn.getName() );
        assertEquals( 1, dn.size() );
    }


    /**
     * test Add to an existing Dn
     */
    @Test
    public void testDnAdd() throws LdapException
    {
        Dn dn = new Dn( "a=b, c=d" );

        dn = dn.add( "e = f" );
        assertEquals( "e=f,a=b,c=d", dn.getNormName() );
        assertEquals( "e = f,a=b, c=d", dn.getName() );
        assertEquals( 3, dn.size() );
    }


    /**
     * test Add a composite Rdn to an existing Dn
     */
    @Test
    public void testDnAddComposite() throws LdapException
    {
        Dn dn = new Dn( "a=b, c=d" );

        dn = dn.add( "e = f + g = h" );

        // Warning ! The order of AVAs has changed during the parsing
        // This has no impact on the correctness of the Dn, but the
        // String used to do the comparizon should be inverted.
        assertEquals( "e=f+g=h,a=b,c=d", dn.getNormName() );
        assertEquals( 3, dn.size() );
    }


    /**
     * test Add at the end of an existing Dn
     */
    @Test
    public void testDnAddEnd() throws LdapException
    {
        Dn dn = new Dn( "a=b, c=d" );

        dn = dn.add( "e = f" );
        assertEquals( "e = f,a=b, c=d", dn.getName() );
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
        Dn dn = new Dn( "a = b" );
        Dn dn2 = new Dn( "c = d" );
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
        Dn dn = new Dn( "a = b" );
        Dn dn2 = new Dn();
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
        Dn dn = new Dn();
        Dn dn2 = new Dn( "a = b" );
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
        Dn dn = new Dn( "a = b" );
        Dn dn2 = new Dn( "c = d" );
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
        Dn dn = new Dn( "a = b" );
        Dn dn2 = new Dn( "c = d" );
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
        Dn dn = new Dn( "a = b, c = d" );
        Dn dn2 = new Dn( "e = f" );
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
        Dn dn = new Dn( "a = b" );
        Dn dn2 = new Dn();
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
        Dn dn = new Dn();
        Dn dn2 = new Dn( "a = b" );
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
        Dn dn = new Dn( "a=b, c=d,e = f" );
        Dn newDn = ( dn.getAncestorOf( "" ) );
        assertEquals( "a=b, c=d,e = f", newDn.getName() );
    }


    /**
     * Get the prefix at pos 1
     */
    @Test
    public void testDnGetPrefixPos1() throws LdapException
    {
        Dn dn = new Dn( "a=b, c=d,e = f" );
        Dn newDn = ( dn.getAncestorOf( "a=b" ) );
        assertEquals( " c=d,e = f", newDn.getName() );
    }


    /**
     * Get the prefix at pos 2
     */
    @Test
    public void testDnGetPrefixPos2() throws LdapException
    {
        Dn dn = new Dn( "a=b, c=d,e = f" );
        Dn newDn = ( dn.getAncestorOf( "a=b, c=d" ) );
        assertEquals( "e = f", newDn.getName() );
    }


    /**
     * Get the prefix at pos 3
     */
    @Test
    public void testDnGetPrefixPos3() throws LdapException
    {
        Dn dn = new Dn( "a=b, c=d,e = f" );
        Dn newDn = ( dn.getAncestorOf( "a=b, c=d,e = f" ) );
        assertEquals( "", newDn.getName() );
    }


    /**
     * Get the prefix out of bound
     */
    @Test( expected=LdapInvalidDnException.class)
    public void testDnGetPrefixPos4() throws LdapException
    {
        Dn dn = new Dn( "a=b, c=d,e = f" );

        Dn res = dn.getAncestorOf( "a=z" );
    }


    /**
     * Get the prefix of an empty LdapName
     */
    @Test
    public void testDnGetPrefixEmptyDN() throws LdapInvalidDnException
    {
        Dn dn = new Dn();
        Dn newDn = ( dn.getAncestorOf( "" ) );
        assertEquals( "", newDn.getName() );
    }


    // GET SUFFIX operations
    /**
     * Get the suffix at pos 0
     */
    @Test
    public void testDnGetSuffixPos0() throws LdapException
    {
        Dn dn = new Dn( "a=b, c=d,e = f" );
        Dn newDn = ( dn.getSuffix( 0 ) );
        assertEquals( "a=b, c=d,e = f", newDn.getName() );
    }


    /**
     * Get the suffix at pos 1
     */
    @Test
    public void testDnGetSuffixPos1() throws LdapException
    {
        Dn dn = new Dn( "a=b, c=d,e = f" );
        Dn newDn = ( dn.getSuffix( 1 ) );
        assertEquals( "a=b, c=d", newDn.getName() );
    }


    /**
     * Get the suffix at pos 2
     */
    @Test
    public void testDnGetSuffixPos2() throws LdapException
    {
        Dn dn = new Dn( "a=b, c=d,e = f" );
        Dn newDn = ( dn.getSuffix( 2 ) );
        assertEquals( "a=b", newDn.getName() );
    }


    /**
     * Get the suffix at pos 3
     */
    @Test
    public void testDnGetSuffixPos3() throws LdapException
    {
        Dn dn = new Dn( "a=b, c=d,e = f" );
        Dn newDn = ( dn.getSuffix( 3 ) );
        assertEquals( "", newDn.getName() );
    }


    /**
     * Get the suffix out of bound
     */
    @Test
    public void testDnGetSuffixPos4() throws LdapException
    {
        Dn dn = new Dn( "a=b, c=d,e = f" );

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
        Dn dn = new Dn();
        Dn newDn = ( dn.getSuffix( 0 ) );
        assertEquals( "", newDn.getName() );
    }


    // IS EMPTY operations
    /**
     * Test that a Dn is empty
     */
    @Test
    public void testDnIsEmpty()
    {
        Dn dn = new Dn();
        assertEquals( true, dn.isEmpty() );
    }


    /**
     * Test that a Dn is empty
     */
    @Test
    public void testDnNotEmpty() throws LdapException
    {
        Dn dn = new Dn( "a=b" );
        assertEquals( false, dn.isEmpty() );
    }


    /**
     * Test that a Dn is empty
     */
    @Test
    public void testDnRemoveIsEmpty() throws LdapException
    {
        Dn dn = new Dn( "a=b, c=d" );
        Dn clonedDn = dn.remove( 0 );

        assertFalse( dn == clonedDn );

        clonedDn = clonedDn.remove( 0 );

        assertEquals( true, clonedDn.isEmpty() );
    }


    // STARTS WITH operations
    /**
     * Test a startsWith a null Dn
     */
    @Test
    public void testDnStartsWithNull() throws LdapException
    {
        Dn dn = new Dn( "a=b, c=d,e = f" );
        assertEquals( true, dn.isDescendantOf( (Dn) null ) );
    }


    /**
     * Test a startsWith an empty Dn
     */
    @Test
    public void testDnStartsWithEmpty() throws LdapException
    {
        Dn dn = new Dn( "a=b, c=d,e = f" );
        assertEquals( true, dn.isDescendantOf( new Dn() ) );
    }


    /**
     * Test a startsWith an simple Dn
     */
    @Test
    public void testDnStartsWithSimple() throws LdapException
    {
        Dn dn = new Dn( "a=b, c=d,e = f" );
        assertEquals( true, dn.isDescendantOf( new Dn( "e=f" ) ) );
    }


    /**
     * Test a startsWith a complex Dn
     */
    @Test
    public void testDnStartsWithComplex() throws LdapException
    {
        Dn dn = new Dn( "a=b, c=d,e = f" );
        assertEquals( true, dn.isDescendantOf( new Dn( "c =  d, e =  f" ) ) );
    }


    /**
     * Test a startsWith a complex Dn
     */
    @Test
    public void testDnStartsWithComplexMixedCase() throws LdapException
    {
        Dn dn = new Dn( "a=b, c=d,e = f" );
        assertEquals( false, dn.isDescendantOf( new Dn( "c =  D, E =  f" ) ) );
    }


    /**
     * Test a startsWith a full Dn
     */
    @Test
    public void testDnStartsWithFull() throws LdapException
    {
        Dn dn = new Dn( "a=b, c=d,e = f" );
        assertEquals( true, dn.isDescendantOf( new Dn( "a=  b; c =  d, e =  f" ) ) );
    }


    /**
     * Test a startsWith which returns false
     */
    @Test
    public void testDnStartsWithWrong() throws LdapException
    {
        Dn dn = new Dn( "a=b, c=d,e = f" );
        assertEquals( false, dn.isDescendantOf( new Dn( "c =  t, e =  f" ) ) );
    }


    // ENDS WITH operations
    /**
     * Test a endsWith a null Dn
     */
    @Test
    public void testDnEndsWithNull() throws LdapException
    {
        Dn dn = new Dn( "a=b, c=d,e = f" );
        assertEquals( true, dn.isDescendantOf( (Dn) null ) );
    }


    // GET ALL operations
    /**
     * test a getAll operation on a null Dn
     *
    @Test
    public void testDnGetAllNull()
    {
        Dn dn = new Dn();
        Enumeration<String> nc = dn.getAll();

        assertEquals( false, nc.hasMoreElements() );
    }


    /**
     * test a getAll operation on an empty Dn
     *
    @Test
    public void testDnGetAllEmpty() throws LdapException
    {
        Dn dn = new Dn( "" );
        Enumeration<String> nc = dn.getAll();

        assertEquals( false, nc.hasMoreElements() );
    }


    /**
     * test a getAll operation on a simple Dn
     *
    @Test
    public void testDnGetAllSimple() throws LdapException
    {
        Dn dn = new Dn( "a=b" );
        Enumeration<String> nc = dn.getAll();

        assertEquals( true, nc.hasMoreElements() );
        assertEquals( "a=b", nc.nextElement() );
        assertEquals( false, nc.hasMoreElements() );
    }


    /**
     * test a getAll operation on a complex Dn
     *
    @Test
    public void testDnGetAllComplex() throws LdapException
    {
        Dn dn = new Dn( "e=f+g=h,a=b,c=d" );
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
     * test a getAll operation on a complex Dn
     *
    @Test
    public void testDnGetAllComplexOrdered() throws LdapException
    {
        Dn dn = new Dn( "g=h+e=f,a=b,c=d" );
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
     * test a clone operation on a empty Dn
     *
    @Test
    public void testDnCloneEmpty()
    {
        Dn dn = new Dn();
        Dn clone = (Dn) dn.clone();

        assertEquals( "", clone.getName() );
    }


    /**
     * test a clone operation on a simple Dn
     *
    @Test
    public void testDnCloneSimple() throws LdapException
    {
        Dn dn = new Dn( "a=b" );
        Dn clone = (Dn) dn.clone();

        assertEquals( "a=b", clone.getName() );
        dn.remove( 0 );
        assertEquals( "a=b", clone.getName() );
    }


    /**
     * test a clone operation on a complex Dn
     *
    @Test
    public void testDnCloneComplex() throws LdapException
    {
        Dn dn = new Dn( "e=f+g=h,a=b,c=d" );
        Dn clone = (Dn) dn.clone();

        assertEquals( "e=f+g=h,a=b,c=d", clone.getName() );
        dn.remove( 2 );
        assertEquals( "e=f+g=h,a=b,c=d", clone.getName() );
    }


    // GET operations
    /**
     * test a get in a null Dn
     */
    @Test
    public void testDnGetNull()
    {
        Dn dn = new Dn();
        assertEquals( "", dn.get( 0 ) );
    }


    /**
     * test a get in an empty Dn
     */
    @Test
    public void testDnGetEmpty() throws LdapException
    {
        Dn dn = new Dn( "" );
        assertEquals( "", dn.get( 0 ) );
    }


    /**
     * test a get in a simple Dn
     */
    @Test
    public void testDnGetSimple() throws LdapException
    {
        Dn dn = new Dn( "a = b" );
        assertEquals( "a=b", dn.get( 0 ) );
    }


    /**
     * test a get in a complex Dn
     */
    @Test
    public void testDnGetComplex() throws LdapException
    {
        Dn dn = new Dn( "a = b + c= d, e= f; g =h" );
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
        Dn dn = new Dn( "a = b + c= d, e= f; g =h" );

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
        Dn name = new Dn( "cn=John,ou=People,ou=Marketing" );

        // Remove the second component from the head: ou=People
        name = name.remove( 1 );
        String out = name.toString();

        assertEquals( "cn=John,ou=Marketing", out );

        // Add to the head (first): cn=John,ou=Marketing,ou=East
        name = new Dn( out, "ou=East" );
        out = name.toString();

        assertEquals( "cn=John,ou=Marketing,ou=East", out );

        // Add to the tail (last): cn=HomeDir,cn=John,ou=Marketing,ou=East
        out = name.add( "cn=HomeDir" ).toString();

        assertEquals( "cn=HomeDir,cn=John,ou=Marketing,ou=East", out );
    }


    @Test
    public void testAttributeEqualsIsCaseInSensitive() throws Exception
    {
        Dn name1 = new Dn( "cn=HomeDir" );
        Dn name2 = new Dn( "CN=HomeDir" );

        assertTrue( name1.equals( name2 ) );
    }


    @Test
    public void testAttributeTypeEqualsIsCaseInsensitive() throws Exception
    {
        Dn name1 = new Dn( "cn=HomeDir+cn=WorkDir" );
        Dn name2 = new Dn( "cn=HomeDir+CN=WorkDir" );

        assertTrue( name1.equals( name2 ) );
    }


    @Test
    public void testNameEqualsIsInsensitiveToAttributesOrder() throws Exception
    {

        Dn name1 = new Dn( "cn=HomeDir+cn=WorkDir" );
        Dn name2 = new Dn( "cn=WorkDir+cn=HomeDir" );

        assertTrue( name1.equals( name2 ) );
    }


    
    @Test
    public void testAttributeComparisonIsCaseInSensitive() throws Exception
    {
        Dn name1 = new Dn( "cn=HomeDir" );
        Dn name2 = new Dn( "CN=HomeDir" );

        assertEquals( name1, name2 );
    }


    @Test
    public void testAttributeTypeComparisonIsCaseInsensitive() throws Exception
    {
        Dn name1 = new Dn( "cn=HomeDir+cn=WorkDir" );
        Dn name2 = new Dn( "cn=HomeDir+CN=WorkDir" );

        assertEquals( name1, name2 );
    }


    @Test
    public void testNameComparisonIsInsensitiveToAttributesOrder() throws Exception
    {

        Dn name1 = new Dn( "cn=HomeDir+cn=WorkDir" );
        Dn name2 = new Dn( "cn=WorkDir+cn=HomeDir" );

        assertEquals( name1, name2 );
    }


    @Test
    public void testNameComparisonIsInsensitiveToAttributesOrderFailure() throws Exception
    {

        Dn name1 = new Dn( "cn= HomeDir+cn=Workdir" );
        Dn name2 = new Dn( "cn = Work+cn=HomeDir" );

        assertNotSame( name1, name2 );
    }


    /**
     * Test the encoding of a LdanDN
     */
    @Test
    public void testNameToBytes() throws Exception
    {
        Dn dn = new Dn( "cn = John, ou = People, OU = Marketing" );

        byte[] bytes = Dn.getBytes(dn);

        assertEquals( 30, Dn.getNbBytes(dn) );
        assertEquals( "cn=John,ou=People,ou=Marketing", new String( bytes, "UTF-8" ) );
    }


    @Test
    public void testStringParser() throws Exception
    {
        String dn = Strings.utf8ToString(new byte[]
                {'C', 'N', ' ', '=', ' ', 'E', 'm', 'm', 'a', 'n', 'u', 'e', 'l', ' ', ' ', 'L', (byte) 0xc3,
                        (byte) 0xa9, 'c', 'h', 'a', 'r', 'n', 'y'});

        Dn name = DnParser.getNameParser().parse( dn );

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
        Dn name = new Dn( "" );
        Dn name50 = new Dn();
        assertEquals( name50, name );

        Dn name0 = new Dn( "ou=Marketing,ou=East" );
        Dn copy = new Dn( "ou=Marketing,ou=East" );
        Dn name1 = new Dn( "cn=John,ou=Marketing,ou=East" );
        Dn name2 = new Dn( "cn=HomeDir,cn=John,ou=Marketing,ou=East" );
        Dn name3 = new Dn( "cn=HomeDir,cn=John,ou=Marketing,ou=West" );
        Dn name4 = new Dn( "cn=Website,cn=John,ou=Marketing,ou=West" );
        Dn name5 = new Dn( "cn=Airline,cn=John,ou=Marketing,ou=West" );

        assertEquals( name0, copy );
        assertTrue( name0.isAncestorOf( name1 ) );
        assertTrue( name0.isAncestorOf( name2 ) );
        assertTrue( name1.isAncestorOf( name2 ) );
        assertTrue( name2.isDescendantOf( name1 ) );
        assertTrue( name2.isDescendantOf( name0 ) );
        assertNotSame( name2, name3 );
        assertNotSame( name2, name4 );
        assertNotSame( name3, name4 );
        assertNotSame( name3, name5 );
        assertNotSame( name4, name5 );
        assertNotSame( name2, name5 );
    }


    /**
     * Class to test for void LdapName()
     */
    @Test
    public void testLdapName()
    {
        Dn name = new Dn();
        assertTrue( name.toString().equals( "" ) );
    }


    /**
     * Class to test for Object clone()
     *
     * @throws Exception
     *             if anything goes wrong.
     *
    @Test
    public void testClone() throws Exception
    {
        String strName = "cn=HomeDir,cn=John,ou=Marketing,ou=East";
        Dn name = new Dn( strName );
        assertEquals( name, name.clone() );
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
        Dn name0 = new Dn( "" );
        Dn name1 = new Dn( "ou=East" );
        Dn name2 = new Dn( "ou=Marketing,ou=East" );
        Dn name3 = new Dn( "cn=John,ou=Marketing,ou=East" );
        Dn name4 = new Dn( "cn=HomeDir,cn=John,ou=Marketing,ou=East" );
        Dn name5 = new Dn( "cn=Website,cn=HomeDir,cn=John,ou=Marketing,ou=West" );
        Dn name6 = new Dn( "cn=Airline,cn=Website,cn=HomeDir,cn=John,ou=Marketing,ou=West" );

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
        Dn name0 = new Dn( "" );
        Dn name1 = new Dn( "ou=East" );
        Dn name2 = new Dn( "ou=Marketing,ou=East" );
        Dn name3 = new Dn( "cn=John,ou=Marketing,ou=East" );
        Dn name4 = new Dn( "cn=HomeDir,cn=John,ou=Marketing,ou=East" );
        Dn name5 = new Dn( "cn=Website,cn=HomeDir,cn=John,ou=Marketing,ou=West" );
        Dn name6 = new Dn( "cn=Airline,cn=Website,cn=HomeDir,cn=John,ou=Marketing,ou=West" );

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
        Dn name0 = new Dn( "" );
        Dn name1 = new Dn( "ou=East" );
        Dn name2 = new Dn( "ou=Marketing,ou=East" );
        Dn name3 = new Dn( "cn=John,ou=Marketing,ou=East" );
        Dn name4 = new Dn( "cn=HomeDir,cn=John,ou=Marketing,ou=East" );
        Dn name5 = new Dn( "cn=Website,cn=HomeDir,cn=John,ou=Marketing,ou=West" );
        Dn name6 = new Dn( "cn=Airline,cn=Website,cn=HomeDir,cn=John,ou=Marketing,ou=West" );

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
        Dn dn = new Dn( "cn=Airline,cn=Website,cn=HomeDir,cn=John,ou=Marketing,ou=West" );
        String[] expected = new String[]
            { "ou=West", "ou=Marketing", "cn=John", "cn=HomeDir", "cn=Website", "cn=Airline" };
        int count = 0;

        for ( Rdn rdn : dn )
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
        Dn name = new Dn( "cn=HomeDir,cn=John,ou=Marketing,ou=East" );
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
        Dn name = new Dn( "cn=HomeDir,cn=John,ou=Marketing,ou=East" );
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
        Dn dn = new Dn( "cn=HomeDir,cn=John,ou=Marketing,ou=East" );

        String[] expected = new String[]
            { "cn=HomeDir", "cn=John", "ou=Marketing", "ou=East" };

        int i = 0;

        for ( Rdn rdn : dn.getRdns() )
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
        Dn name = new Dn( "cn=HomeDir,cn=John,ou=Marketing,ou=East" );
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
        Dn name = new Dn( "cn=HomeDir,cn=John,ou=Marketing,ou=East" );

        assertEquals( "cn=HomeDir,cn=John,ou=Marketing,ou=East", name.getAncestorOf( "" ).toString() );
        assertEquals( "cn=John,ou=Marketing,ou=East", name.getAncestorOf( "cn=HomeDir" ).toString() );
        assertEquals( "ou=Marketing,ou=East", name.getAncestorOf( "cn=HomeDir,cn=John" ).toString() );
        assertEquals( "ou=East", name.getAncestorOf( "cn=HomeDir,cn=John,ou=Marketing" ).toString() );
        assertEquals( "", name.getAncestorOf( "cn=HomeDir,cn=John,ou=Marketing,ou=East" ).toString() );
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
        Dn n0 = new Dn( "cn=HomeDir,cn=John,ou=Marketing,ou=East" );
        Dn n1 = new Dn( "cn=HomeDir,cn=John,ou=Marketing,ou=East" );
        Dn n2 = new Dn( "cn=John,ou=Marketing,ou=East" );
        Dn n3 = new Dn( "ou=Marketing,ou=East" );
        Dn n4 = new Dn( "ou=East" );
        Dn n5 = new Dn( "" );

        Dn n6 = new Dn( "cn=HomeDir" );
        Dn n7 = new Dn( "cn=HomeDir,cn=John" );
        Dn n8 = new Dn( "cn=HomeDir,cn=John,ou=Marketing" );

        // Check with Dn
        assertTrue( n0.isDescendantOf( n1 ) );
        assertTrue( n0.isDescendantOf( n2 ) );
        assertTrue( n0.isDescendantOf( n3 ) );
        assertTrue( n0.isDescendantOf( n4 ) );
        assertTrue( n0.isDescendantOf( n5 ) );

        assertTrue( !n0.isDescendantOf( n6 ) );
        assertTrue( !n0.isDescendantOf( n7 ) );
        assertTrue( !n0.isDescendantOf( n8 ) );

        Dn nn0 = new Dn( "cn=zero" );
        Dn nn10 = new Dn( "cn=one,cn=zero" );
        Dn nn210 = new Dn( "cn=two,cn=one,cn=zero" );
        Dn nn3210 = new Dn( "cn=three,cn=two,cn=one,cn=zero" );

        assertTrue( nn0.isDescendantOf( nn0 ) );
        assertTrue( nn10.isDescendantOf( nn0 ) );
        assertTrue( nn210.isDescendantOf( nn0 ) );
        assertTrue( nn3210.isDescendantOf( nn0 ) );

        assertTrue( nn10.isDescendantOf( nn10 ) );
        assertTrue( nn210.isDescendantOf( nn10 ) );
        assertTrue( nn3210.isDescendantOf( nn10 ) );

        assertTrue( nn210.isDescendantOf( nn210 ) );
        assertTrue( nn3210.isDescendantOf( nn210 ) );

        assertTrue( nn3210.isDescendantOf( nn3210 ) );

        assertTrue( "Starting Dn fails with ADS Dn",
            new Dn( "ou=foo,dc=apache,dc=org" ).isDescendantOf( new Dn( "dc=apache,dc=org" ) ) );

        assertTrue( "Starting Dn fails with Java LdapName",
            new Dn( "ou=foo,dc=apache,dc=org" ).isDescendantOf( new Dn( "dc=apache,dc=org" ) ) );

        assertTrue( "Starting Dn fails with Java LdapName",
            new Dn( "dc=apache,dc=org" ).isDescendantOf( new Dn( "dc=apache,dc=org" ) ) );
    }


    /**
     * Class to test for Dn addAll(Dn)
     *
     * @throws Exception
     *             when anything goes wrong
     */
    @Test
    public void testAddAllName0() throws Exception
    {
        Dn name = new Dn();
        Dn name0 = new Dn( "cn=HomeDir,cn=John,ou=Marketing,ou=East" );
        assertTrue( name0.equals( name.addAll( name0 ) ) );
    }


    /**
     * Class to test for Dn addAll(Dn)
     *
     * @throws Exception
     *             when anything goes wrong
     */
    @Test
    public void testAddAllNameExisting0() throws Exception
    {
        Dn name1 = new Dn( "ou=Marketing,ou=East" );
        Dn name2 = new Dn( "cn=HomeDir,cn=John" );
        Dn nameAdded = new Dn( "cn=HomeDir,cn=John, ou=Marketing,ou=East" );
        assertTrue( nameAdded.equals( name1.addAll( name2 ) ) );
    }


    /**
     * Class to test for Dn addAll(Dn)
     *
     * @throws Exception
     *             when anything goes wrong
     */
    @Test
    public void testAddAllName1() throws Exception
    {
        Dn name = new Dn();
        Dn name0 = new Dn( "ou=Marketing,ou=East" );
        Dn name1 = new Dn( "cn=HomeDir,cn=John" );
        Dn name2 = new Dn( "cn=HomeDir,cn=John,ou=Marketing,ou=East" );

        name = name.addAll( name0 );
        assertTrue( name0.equals( name ) );
        assertTrue( name2.equals( name.addAll( name1 ) ) );
    }


    /**
     * Class to test for Dn addAll(int, Dn)
     *
     * @throws Exception
     *             when something goes wrong
     */
    @Test
    public void testAddAllintName0() throws Exception
    {
        Dn name = new Dn();
        Dn name0 = new Dn( "ou=Marketing,ou=East" );
        Dn name1 = new Dn( "cn=HomeDir,cn=John" );
        Dn name2 = new Dn( "cn=HomeDir,cn=John,ou=Marketing,ou=East" );

        name = name.addAll( name0 );
        assertTrue( name0.equals( name ) );
        assertTrue( name2.equals( name.addAll( 2, name1 ) ) );
    }


    /**
     * Class to test for Dn addAll(int, Dn)
     *
     * @throws Exception
     *             when something goes wrong
     */
    @Test
    public void testAddAllintName1() throws Exception
    {
        Dn name = new Dn();
        Dn name0 = new Dn( "cn=HomeDir,ou=Marketing,ou=East" );
        Dn name1 = new Dn( "cn=John" );
        Dn name2 = new Dn( "cn=HomeDir,cn=John,ou=Marketing,ou=East" );

        name = name.addAll( name0 );
        assertTrue( name0.equals( name ) );

        name = name.addAll( 2, name1 );
        assertTrue( name2.equals( name ) );

        Dn name3 = new Dn( "cn=Airport" );
        Dn name4 = new Dn( "cn=Airport,cn=HomeDir,cn=John,ou=Marketing,ou=East" );

        name = name.addAll( 4, name3 );
        assertTrue( name4.equals( name ) );

        Dn name5 = new Dn( "cn=ABC123" );
        Dn name6 = new Dn( "cn=Airport,cn=HomeDir,cn=ABC123,cn=John,ou=Marketing,ou=East" );

        assertTrue( name6.equals( name.addAll( 3, name5 ) ) );
    }


    /**
     * Class to test for Dn add(String)
     *
     * @throws Exception
     *             when something goes wrong
     */
    @Test
    public void testAddString() throws Exception
    {
        Dn name = new Dn();
        assertEquals( name, new Dn( "" ) );

        Dn name4 = new Dn( "ou=East" );

        assertTrue( name.isNormalized() );

        name = name.add( "ou=East" );

        assertFalse( name.isNormalized() );

        assertEquals( name4, name );

        Dn name3 = new Dn( "ou=Marketing,ou=East" );
        name = name.add( "ou=Marketing" );
        assertEquals( name3, name );

        Dn name2 = new Dn( "cn=John,ou=Marketing,ou=East" );
        name = name.add( "cn=John" );
        assertEquals( name2, name );

        Dn name0 = new Dn( "cn=HomeDir,cn=John,ou=Marketing,ou=East" );
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
        Dn name = new Dn();
        assertEquals( name, new Dn( "" ) );

        Dn name4 = new Dn( "ou=East" );
        name = name.add( "ou=East" );
        assertEquals( name4, name );

        Dn name3 = new Dn( "ou=Marketing,ou=East" );
        name = name.add( "ou=Marketing" );
        assertEquals( name3, name );

        Dn name2 = new Dn( "cn=John,ou=Marketing,ou=East" );
        name = name.add( "cn=John" );
        assertEquals( name2, name );

        Dn name0 = new Dn( "cn=HomeDir,cn=John,ou=Marketing,ou=East" );
        name = name.add( "cn=HomeDir" );
        assertEquals( name0, name );
    }


    /**
     * Class to test for String toString()
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void testToString() throws Exception
    {
        Dn name = new Dn();
        assertEquals( "", name.toString() );

        name = name.add( "ou=East" );
        assertEquals( "ou=East", name.toString() );

        name = name.add( "ou=Marketing" );
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
        Dn empty = new Dn();
        assertNull( empty.getParent() );

        Dn one = new Dn( "cn=test" );
        assertNotNull( one.getParent() );
        assertTrue( one.getParent().isEmpty() );

        Dn two = new Dn( "cn=test,o=acme" );
        assertNotNull( two.getParent() );
        assertFalse( two.getParent().isNormalized() );
        assertFalse( two.getParent().isEmpty() );
        assertEquals( "o=acme", two.getParent().getName() );

        Dn three = new Dn( "cn=test,dc=example,dc=com" );
        three.normalize( schemaManager );
        Dn threeParent = three.getParent();
        assertNotNull( threeParent );
        assertTrue( threeParent.isNormalized() );
        assertFalse( threeParent.isEmpty() );
        assertEquals( "dc=example,dc=com", threeParent.getName() );
        assertEquals( 2, threeParent.getRdns().size() );

        Dn five = new Dn( "uid=user1,ou=sales,ou=users,dc=example,dc=com" );
        Dn fiveParent = five.getParent();
        assertNotNull( fiveParent );
        assertFalse( fiveParent.isNormalized() );
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
        assertTrue( new Dn( "ou=People" ).equals( new Dn( "ou=People" ) ) );

        assertTrue( !new Dn( "ou=People,dc=example,dc=com" ).equals( new Dn( "ou=People" ) ) );
        assertTrue( !new Dn( "ou=people" ).equals( new Dn( "ou=People" ) ) );
        assertTrue( !new Dn( "ou=Groups" ).equals( new Dn( "ou=People" ) ) );
    }


    @Test
    public void testNameFrenchChars() throws Exception
    {
        String cn = new String( new byte[]
            { 'c', 'n', '=', 0x4A, ( byte ) 0xC3, ( byte ) 0xA9, 0x72, ( byte ) 0xC3, ( byte ) 0xB4, 0x6D, 0x65 },
            "UTF-8" );

        Dn name = new Dn( cn );

        assertEquals( "cn=J\u00e9r\u00f4me", name.toString() );
    }


    @Test
    public void testNameGermanChars() throws Exception
    {
        String cn = new String( new byte[]
            { 'c', 'n', '=', ( byte ) 0xC3, ( byte ) 0x84, ( byte ) 0xC3, ( byte ) 0x96, ( byte ) 0xC3, ( byte ) 0x9C,
                ( byte ) 0xC3, ( byte ) 0x9F, ( byte ) 0xC3, ( byte ) 0xA4, ( byte ) 0xC3, ( byte ) 0xB6,
                ( byte ) 0xC3, ( byte ) 0xBC }, "UTF-8" );

        Dn name = new Dn( cn );

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

        Dn name = new Dn( cn );

        assertEquals( "cn=\u0130\u0131\u015E\u015F\u00D6\u00F6\u00DC\u00FC\u011E\u011F", name.toString() );
    }


    /**
     * Class to test for toOid( Dn, Map)
     */
    @Test
    public void testLdapNameToName() throws Exception
    {
        Dn name = new Dn( "ou= Some   People   ", "dc = eXample", "dc= cOm" );

        assertTrue( name.getName().equals( "ou= Some   People   ,dc = eXample,dc= cOm" ) );

        Dn result = name.normalize( schemaManager );

        assertEquals( "2.5.4.11=some people,0.9.2342.19200300.100.1.25=example,0.9.2342.19200300.100.1.25=com", result.getNormName() );
    }


    @Test
    public void testRdnGetTypeUpName() throws Exception
    {
        Dn name = new Dn( "ou= Some   People   ", "dc = eXample", "dc= cOm" );

        assertTrue( name.getName().equals( "ou= Some   People   ,dc = eXample,dc= cOm" ) );

        Rdn rdn = name.getRdn();

        assertEquals( "ou= Some   People   ", rdn.getName() );
        assertEquals( "ou", rdn.getNormType() );
        assertEquals( "ou", rdn.getUpType() );

        Dn result = name.normalize( schemaManager );

        assertTrue( result.getNormName().equals(
            "2.5.4.11=some people,0.9.2342.19200300.100.1.25=example,0.9.2342.19200300.100.1.25=com" ) );
        assertTrue( name.getName().equals( "ou= Some   People   ,dc = eXample,dc= cOm" ) );

        Rdn rdn2 = result.getRdn();

        assertEquals( "ou= Some   People   ", rdn2.getName() );
        assertEquals( "2.5.4.11", rdn2.getNormType() );
        assertEquals( "ou", rdn2.getUpType() );
    }


    /**
     * Class to test for toOid( Dn, Map) with a NULL dn
     */
    @Test
    public void testLdapNameToNameEmpty() throws Exception
    {
        Dn name = new Dn();

        Dn result = name.normalize( schemaManager );
        assertTrue( result.toString().equals( "" ) );
    }


    /**
     * Class to test for toOid( Dn, Map) with a multiple NameComponent
     */
    @Test
    public void testLdapNameToNameMultiNC() throws Exception
    {
        Dn name = new Dn(
            "2.5.4.11= Some   People   + 0.9.2342.19200300.100.1.25=  And   Some anImAls,0.9.2342.19200300.100.1.25 = eXample,dc= cOm" );

        Dn result = name.normalize( schemaManager );

        assertEquals(
            ( result ).getNormName(),
            "0.9.2342.19200300.100.1.25=and some animals+2.5.4.11=some people,0.9.2342.19200300.100.1.25=example,0.9.2342.19200300.100.1.25=com" );
        assertTrue( ( result )
            .getName()
            .equals(
                "2.5.4.11= Some   People   + 0.9.2342.19200300.100.1.25=  And   Some anImAls,0.9.2342.19200300.100.1.25 = eXample,dc= cOm" ) );
    }


    /**
     * Class to test for toOid( Dn, Map) with a multiple NameComponent
     */
    @Test
    public void testLdapNameToNameAliasMultiNC() throws Exception
    {
        Dn name = new Dn(
            "2.5.4.11= Some   People   + domainComponent=  And   Some anImAls,DomainComponent = eXample,0.9.2342.19200300.100.1.25= cOm" );

        Dn result = name.normalize( schemaManager);

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
        Dn name1 = Dn
            .normalize( schemaManager,
                    "2.5.4.11= Some   People   + domainComponent=  And   Some anImAls,DomainComponent = eXample,0.9.2342.19200300.100.1.25= cOm" );

        Dn name2 = Dn
            .normalize( schemaManager,
                    "2.5.4.11=some people+domainComponent=and some animals,DomainComponent=example,0.9.2342.19200300.100.1.25=com" );

        assertEquals( name1.hashCode(), name2.hashCode() );
    }


    /**
     * Test for DIRSERVER-191
     */
    @Test
    public void testName() throws LdapException, InvalidNameException
    {
        LdapName jName = new javax.naming.ldap.LdapName( "cn=four,cn=three,cn=two,cn=one" );
        Dn aName = new Dn( "cn=four,cn=three,cn=two,cn=one" );
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
        Dn aName = new Dn( "cn=four,cn=three,cn=two,cn=one" );

        assertEquals( jName.getPrefix( 0 ).toString(), aName.getAncestorOf( "cn=four,cn=three,cn=two,cn=one" ).toString() );
        assertEquals( jName.getPrefix( 1 ).toString(), aName.getAncestorOf( "cn=four,cn=three,cn=two" ).toString() );
        assertEquals( jName.getPrefix( 2 ).toString(), aName.getAncestorOf( "cn=four,cn=three" ).toString() );
        assertEquals( jName.getPrefix( 3 ).toString(), aName.getAncestorOf( "cn=four" ).toString() );
        assertEquals( jName.getPrefix( 4 ).toString(), aName.getAncestorOf( "" ).toString() );
    }


    /**
     * Test for DIRSERVER-191
     */
    @Test
    public void testGetSuffix() throws LdapException, InvalidNameException
    {
        LdapName jName = new LdapName( "cn=four,cn=three,cn=two,cn=one" );
        Dn aName = new Dn( "cn=four,cn=three,cn=two,cn=one" );

        assertEquals( jName.getSuffix( 0 ).toString(), aName.getSuffix( 0 ).toString() );
        assertEquals( jName.getSuffix( 1 ).toString(), aName.getSuffix( 1 ).toString() );
        assertEquals( jName.getSuffix( 2 ).toString(), aName.getSuffix( 2 ).toString() );
        assertEquals( jName.getSuffix( 3 ).toString(), aName.getSuffix( 3 ).toString() );
        assertEquals( jName.getSuffix( 4 ).toString(), aName.getSuffix( 4 ).toString() );
    }


    /**
     * Test for DIRSERVER-191. The Dn is immutable, thus we can't add a new Rdn
     * to a Dn, it simply creates a new one.
     */
    @Test
    public void testAddStringName() throws LdapException, InvalidNameException
    {
        LdapName jName = new LdapName( "cn=four,cn=three,cn=two,cn=one" );
        Dn aName = new Dn( "cn=four,cn=three,cn=two,cn=one" );

        assertSame( jName, jName.add( "cn=five" ) );
        assertNotSame( aName, aName.add( "cn=five" ) );
        assertNotSame( jName.toString(), aName.toString() );
    }


    /**
     * Test for DIRSERVER-191
     */
    @Test
    public void testAddAllName() throws LdapException, InvalidNameException
    {
        LdapName jName = new LdapName( "cn=four,cn=three,cn=two,cn=one" );
        Dn aName = new Dn( "cn=four,cn=three,cn=two,cn=one" );

        assertSame( jName, jName.addAll( new LdapName( "cn=seven,cn=six" ) ) );
        assertNotSame( aName, aName.addAll( new Dn( "cn=seven,cn=six" ) ) );
        assertNotSame( jName.toString(), aName.toString() );
    }


    /**
     * Test for DIRSERVER-191
     */
    @Test
    public void testAddAllIntName() throws LdapException, InvalidNameException
    {
        LdapName jName = new LdapName( "cn=four,cn=three,cn=two,cn=one" );
        Dn aName = new Dn( "cn=four,cn=three,cn=two,cn=one" );

        assertSame( jName, jName.addAll( 0, new LdapName( "cn=zero,cn=zero.5" ) ) );
        assertNotSame( aName, aName.addAll( 0, new Dn( "cn=zero,cn=zero.5" ) ) );
        assertNotSame( jName.toString(), aName.toString() );

        assertSame( jName, jName.addAll( 2, new LdapName( "cn=zero,cn=zero.5" ) ) );
        assertNotSame( aName, aName.addAll( 2, new Dn( "cn=zero,cn=zero.5" ) ) );
        assertNotSame( jName.toString(), aName.toString() );

        assertSame( jName, jName.addAll( jName.size(), new LdapName( "cn=zero,cn=zero.5" ) ) );
        assertNotSame( aName, aName.addAll( aName.size(), new Dn( "cn=zero,cn=zero.5" ) ) );
        assertNotSame( jName.toString(), aName.toString() );
    }


    /**
     * Test for DIRSERVER-191
     */
    @Test
    public void testStartsWithName() throws LdapException, InvalidNameException
    {
        LdapName jName = new LdapName( "cn=four,cn=three,cn=two,cn=one" );
        Dn aName = new Dn( "cn=four,cn=three,cn=two,cn=one" );

        assertEquals( jName.startsWith( new LdapName( "cn=seven,cn=six,cn=five" ) ),
            aName.isDescendantOf( new Dn( "cn=seven,cn=six,cn=five" ) ) );
        assertEquals( jName.startsWith( new LdapName( "cn=three,cn=two,cn=one" ) ),
            aName.isDescendantOf( new Dn( "cn=three,cn=two,cn=one" ) ) );
    }


    /**
     * Test for DIRSERVER-191
     */
    @Test
    public void testRemoveName() throws LdapException, InvalidNameException
    {
        LdapName jName = new LdapName( "cn=four,cn=three,cn=two,cn=one" );
        Dn aName = new Dn( "cn=four,cn=three,cn=two,cn=one" );
        jName.remove( 0 );

        assertEquals( jName.toString(), aName.remove( 0 ).toString() );
        assertNotSame( jName.toString(), aName.toString() );

        jName.remove( jName.size() - 1 );
        assertEquals( jName.toString(), aName.remove( aName.size() - 1 ).remove( 0 ).toString() );
        assertNotSame( jName.toString(), aName.toString() );
    }


    /**
     * Test for DIRSERVER-642
     * @throws LdapException
     */
    @Test
    public void testDoubleQuoteInNameDIRSERVER_642() throws LdapException, InvalidNameException
    {
        Dn name1 = new Dn( "cn=\"Kylie Minogue\",dc=example,dc=com" );

        String[] expected = new String[]
            { "cn=\"Kylie Minogue\"", "dc=example", "dc=com" };

        List<Rdn> j = name1.getRdns();
        int count = 0;

        for ( Rdn rdn : j )
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
        Dn dn = new Dn( "cn=\" Kylie Minogue \",dc=example,dc=com" );

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
        Dn dn = new Dn( "a=\"b,c\"" );

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
        Dn name = new Dn( "dn= \\ four spaces leading and 3 trailing \\  " );

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
            new Dn( "dn=middle\\ spaces" );
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
            new Dn( "dn=# a leading pound" );
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
        Dn name = new Dn( "dn=\\# a leading pound" );

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
        Dn name = new Dn( "dn=a middle \\# pound" );

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
        Dn name = new Dn( "dn=a trailing pound \\#" );

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
            new Dn( "dn=a middle # pound" );
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
            new Dn( "dn=a trailing pound #" );
        }
        catch ( LdapException ine )
        {
            assertTrue( true );
        }
    }


    @Test
    public void testDIRSERVER_631_1() throws LdapException
    {
        Dn name = new Dn( "cn=Bush\\, Kate,dc=example,dc=com" );

        assertEquals( "cn=Bush\\, Kate,dc=example,dc=com", name.toString() );
        assertEquals( "cn=Bush\\, Kate,dc=example,dc=com", name.getName() );

    }


    /**
     * Added a test to check the parsing of a Dn with more than one Rdn
     * which are OIDs, and with one Rdn which has more than one atav.
     * @throws LdapException
     */
    @Test
    public void testDNWithMultiOidsRDN() throws LdapException
    {
        Dn name = new Dn(
            "0.9.2342.19200300.100.1.1=00123456789+2.5.4.3=pablo picasso,2.5.4.11=search,2.5.4.10=imc,2.5.4.6=us" );
        assertEquals(
            "0.9.2342.19200300.100.1.1=00123456789+2.5.4.3=pablo picasso,2.5.4.11=search,2.5.4.10=imc,2.5.4.6=us",
            name.toString() );
        assertEquals(
            "0.9.2342.19200300.100.1.1=00123456789+2.5.4.3=pablo picasso,2.5.4.11=search,2.5.4.10=imc,2.5.4.6=us",
            name.getName() );
    }


    @Test
    public void testDNEquals() throws LdapException
    {
        Dn dn1 = new Dn( "a=b,c=d,e=f" );
        Dn dn2 = new Dn( "a=b\\,c\\=d,e=f" );

        assertFalse( dn1.getNormName().equals( dn2.getNormName() ) );
    }


    @Test
    public void testDNAddEmptyString() throws LdapException
    {
        Dn dn = new Dn();
        assertTrue( dn.size() == 0 );
        assertTrue( dn.add( "" ).size() == 0 );
    }


    /**
     * This leads to the bug in DIRSERVER-832.
     */
    @Test
    public void testPreserveAttributeIdCase() throws LdapException
    {
        Dn dn = new Dn( "uID=kevin" );
        assertEquals( "uID", dn.getRdn().getUpType() );
    }


    /**
     * Tests the Dn.isValid() method.
     */
    @Test
    public void testIsValid()
    {
        assertTrue( Dn.isValid("") );

        assertFalse( Dn.isValid("a") );
        assertFalse( Dn.isValid("a ") );

        assertTrue( Dn.isValid("a=") );
        assertTrue( Dn.isValid("a= ") );

        assertFalse( Dn.isValid("=") );
        assertFalse( Dn.isValid(" = ") );
        assertFalse( Dn.isValid(" = a") );
    }


    /**
     * Test the serialization of a Dn
     *
     * @throws Exception
     */
    @Test
    public void testNameSerialization() throws Exception
    {
        Dn dn = new Dn( "ou= Some   People   + dc=  And   Some anImAls,dc = eXample,dc= cOm" );
        dn.normalize( schemaManager );

        assertEquals( dn, DnSerializer.deserialize( DnSerializer.serialize( dn ) ) );
    }


    @Test
    public void testSerializeEmptyDN() throws Exception
    {
        Dn dn = Dn.EMPTY_DN;

        assertEquals( dn, DnSerializer.deserialize( DnSerializer.serialize( dn ) ) );
    }


    /**
     * Test the serialization of a Dn
     *
     * @throws Exception
     */
    @Test
    public void testNameStaticSerialization() throws Exception
    {
        Dn dn = new Dn( "ou= Some   People   + dc=  And   Some anImAls,dc = eXample,dc= cOm" );
        dn.normalize( schemaManager );

        byte[] data = DnSerializer.serialize( dn );

        ObjectInputStream in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        assertEquals( dn, DnSerializer.deserialize( in ) );
    }


    /*
    @Test public void testSerializationPerfs() throws Exception
    {
        Dn dn = new Dn( "ou= Some   People   + dc=  And   Some anImAls,dc = eXample,dc= cOm" );
        dn.normalize( oids );

        long t0 = System.currentTimeMillis();

        for ( int i = 0; i < 1000; i++ )
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream( baos );

            DnSerializer.serialize( dn, out );

            byte[] data = baos.toByteArray();
            ObjectInputStream in = new ObjectInputStream( new ByteArrayInputStream( data ) );

            Dn dn1 = DnSerializer.deserialize( in );
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

            //Dn dn1 = DnSerializer.deserializeString( in, oids );
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
        Dn dn = Dn.EMPTY_DN;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        DnSerializer.serialize( dn, out );
        out.flush();

        byte[] data = baos.toByteArray();
        ObjectInputStream in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        assertEquals( dn, DnSerializer.deserialize(in) );
        assertEquals( dn, DnSerializer.deserialize( DnSerializer.serialize( dn ) ) );
    }


    @Test
    public void testCompositeRDN() throws LdapException
    {
        assertTrue( Dn.isValid("a=b+c=d+e=f,g=h") );

        Dn dn = new Dn( "a=b+c=d+e=f,g=h" );

        assertEquals( "a=b+c=d+e=f,g=h", dn.toString() );
    }


    @Test
    public void testCompositeRDNOids() throws LdapException
    {
        assertTrue( Dn
            .isValid("1.2.3.4.5=0+1.2.3.4.6=0+1.2.3.4.7=omnischmomni,2.5.4.3=subtree,0.9.2342.19200300.100.1.25=example,0.9.2342.19200300.100.1.25=com") );

        Dn dn = new Dn(
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
        Dn dn1 = new Dn( " cn = Amos\\,Tori , ou=system " );
        assertEquals( " cn = Amos\\,Tori ", dn1.getRdn().getName() );
        Ava atav1 = dn1.getRdn().getAVA();
        assertEquals( "cn", atav1.getUpType() );
        assertEquals( "Amos,Tori", atav1.getUpValue().getString() );

        // antlr parser: hexstring with trailing spaces
        Dn dn3 = new Dn( " cn = #414243 , ou=system " );
        assertEquals( " cn = #414243 ", dn3.getRdn().getName() );
        Ava atav3 = dn3.getRdn().getAVA();
        assertEquals( "cn", atav3.getUpType() );
        assertEquals( "ABC", atav3.getUpValue().getString() );
        assertTrue( Arrays.equals( Strings.getBytesUtf8("ABC"), atav3.getNormValue().getBytes() ) );

        // antlr parser:
        Dn dn4 = new Dn( " cn = \\41\\42\\43 , ou=system " );
        assertEquals( " cn = \\41\\42\\43 ", dn4.getRdn().getName() );
        Ava atav4 = dn4.getRdn().getAVA();
        assertEquals( "cn", atav4.getUpType() );
        assertEquals( "ABC", atav4.getUpValue().getString() );
        assertEquals( "ABC", atav4.getNormValue().getString() );

        // antlr parser: quotestring with trailing spaces
        Dn dn5 = new Dn( " cn = \"ABC\" , ou=system " );
        assertEquals( " cn = \"ABC\" ", dn5.getRdn().getName() );
        Ava atav5 = dn5.getRdn().getAVA();
        assertEquals( "cn", atav5.getUpType() );
        assertEquals( "ABC", atav5.getUpValue().getString() );
        assertEquals( "ABC", atav5.getNormValue().getString() );

        // fast parser: string value with trailing spaces
        Dn dn2 = new Dn( " cn = Amos Tori , ou=system " );
        assertEquals( " cn = Amos Tori ", dn2.getRdn().getName() );
        Ava atav2 = dn2.getRdn().getAVA();
        assertEquals( "cn", atav2.getUpType() );
        assertEquals( "Amos Tori", atav2.getUpValue().getString() );
    }


    /**
     * Test for DIRSHARED-39.
     * (Trailing escaped space not parsed correctly by the Dn parser(
     */
    @Test
    public void testTrailingEscapedSpace() throws Exception
    {
        Dn dn1 = new Dn( "ou=A\\ ,ou=system" );
        dn1.normalize( schemaManager );
        assertEquals( "ou=A\\ ,ou=system", dn1.getName() );
        assertEquals( "2.5.4.11=a,2.5.4.11=system", dn1.getNormName() );
        assertEquals( "ou=A\\ ", dn1.getRdn().getName() );
        assertEquals( "2.5.4.11=a", dn1.getRdn().getNormName() );

        Dn dn2 = new Dn( "ou=A\\20,ou=system" );
        dn2.normalize( schemaManager );
        assertEquals( "ou=A\\20,ou=system", dn2.getName() );
        assertEquals( "2.5.4.11=a,2.5.4.11=system", dn2.getNormName() );
        assertEquals( "ou=A\\20", dn2.getRdn().getName() );
        assertEquals( "2.5.4.11=a", dn2.getRdn().getNormName() );

        Dn dn3 = new Dn( "ou=\\ ,ou=system" );
        dn3.normalize( schemaManager );
        assertEquals( "ou=\\ ,ou=system", dn3.getName() );
        assertEquals( "2.5.4.11=\\ ,2.5.4.11=system", dn3.getNormName() );
        assertEquals( "ou=\\ ", dn3.getRdn().getName() );
        assertEquals( "2.5.4.11=\\ ", dn3.getRdn().getNormName() );

        Dn dn4 = new Dn( "ou=\\20,ou=system" );
        dn4.normalize( schemaManager );
        assertEquals( "ou=\\20,ou=system", dn4.getName() );
        assertEquals( "2.5.4.11=\\ ,2.5.4.11=system", dn4.getNormName() );
        assertEquals( "ou=\\20", dn4.getRdn().getName() );
        assertEquals( "2.5.4.11=\\ ", dn4.getRdn().getNormName() );
    }


    /**
     * Test for DIRSHARED-41, DIRSTUDIO-603.
     * (Dn parser fails to parse names containing an numeric OID value)
     */
    @Test
    public void testNumericOid() throws Exception
    {
        // numeric OID only
        Dn dn1 = new Dn( "cn=loopback+ipHostNumber=127.0.0.1,ou=Hosts,dc=mygfs,dc=com" );
        assertEquals( "cn=loopback+ipHostNumber=127.0.0.1,ou=Hosts,dc=mygfs,dc=com", dn1.getName() );
        assertEquals( "cn=loopback+iphostnumber=127.0.0.1,ou=Hosts,dc=mygfs,dc=com", dn1.getNormName() );
        assertEquals( "cn=loopback+ipHostNumber=127.0.0.1", dn1.getRdn().getName() );
        assertEquals( "cn=loopback+iphostnumber=127.0.0.1", dn1.getRdn().getNormName() );
        assertEquals( "127.0.0.1", dn1.getRdn().getAttributeTypeAndValue( "ipHostNumber" ).getUpValue().get() );

        // numeric OID with suffix
        Dn dn2 = new Dn( "cn=loopback+ipHostNumber=X127.0.0.1,ou=Hosts,dc=mygfs,dc=com" );
        assertEquals( "cn=loopback+ipHostNumber=X127.0.0.1,ou=Hosts,dc=mygfs,dc=com", dn2.getName() );
        assertEquals( "cn=loopback+iphostnumber=X127.0.0.1,ou=Hosts,dc=mygfs,dc=com", dn2.getNormName() );
        assertEquals( "cn=loopback+ipHostNumber=X127.0.0.1", dn2.getRdn().getName() );
        assertEquals( "cn=loopback+iphostnumber=X127.0.0.1", dn2.getRdn().getNormName() );

        // numeric OID with prefix
        Dn dn3 = new Dn( "cn=loopback+ipHostNumber=127.0.0.1Y,ou=Hosts,dc=mygfs,dc=com" );
        assertEquals( "cn=loopback+ipHostNumber=127.0.0.1Y,ou=Hosts,dc=mygfs,dc=com", dn3.getName() );
        assertEquals( "cn=loopback+iphostnumber=127.0.0.1Y,ou=Hosts,dc=mygfs,dc=com", dn3.getNormName() );
        assertEquals( "cn=loopback+ipHostNumber=127.0.0.1Y", dn3.getRdn().getName() );
        assertEquals( "cn=loopback+iphostnumber=127.0.0.1Y", dn3.getRdn().getNormName() );

        // numeric OID with special characters
        Dn dn4 = new Dn( "cn=loopback+ipHostNumber=\\#127.0.0.1 Z,ou=Hosts,dc=mygfs,dc=com" );
        assertEquals( "cn=loopback+ipHostNumber=\\#127.0.0.1 Z,ou=Hosts,dc=mygfs,dc=com", dn4.getName() );
        assertEquals( "cn=loopback+iphostnumber=\\#127.0.0.1 Z,ou=Hosts,dc=mygfs,dc=com", dn4.getNormName() );
        assertEquals( "cn=loopback+ipHostNumber=\\#127.0.0.1 Z", dn4.getRdn().getName() );
        assertEquals( "cn=loopback+iphostnumber=\\#127.0.0.1 Z", dn4.getRdn().getNormName() );
    }


    @Test
    public void testNormalizeAscii() throws Exception
    {
        Dn dn = new Dn( "  ou  =  Example ,  ou  =  COM " );

        dn.normalize( schemaManager );
        assertEquals( "2.5.4.11=example,2.5.4.11=com", dn.getNormName() );
        assertEquals( "  ou  =  Example ,  ou  =  COM ", dn.getName() );

        Rdn rdn = dn.getRdn();
        assertEquals( "2.5.4.11", rdn.getNormType() );
        assertEquals( "example", rdn.getNormValue().getString() );
        assertEquals( "2.5.4.11=example", rdn.getNormName() );
        assertEquals( "ou", rdn.getUpType() );
        assertEquals( "Example", rdn.getUpValue().getString() );
        assertEquals( "  ou  =  Example ", rdn.getName() );

        Ava atav = rdn.getAVA();

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
        Dn dn = new Dn( "  ou  =  Example + ou = TEST ,  ou  =  COM " );

        dn.normalize( schemaManager );
        assertEquals( "2.5.4.11=example+2.5.4.11=test,2.5.4.11=com", dn.getNormName() );
        assertEquals( "  ou  =  Example + ou = TEST ,  ou  =  COM ", dn.getName() );

        Rdn rdn = dn.getRdn();
        assertEquals( "2.5.4.11", rdn.getNormType() );
        assertEquals( "example", rdn.getNormValue().getString() );
        assertEquals( "2.5.4.11=example+2.5.4.11=test", rdn.getNormName() );
        assertEquals( "ou", rdn.getUpType() );
        assertEquals( "Example", rdn.getUpValue().getString() );
        assertEquals( "  ou  =  Example + ou = TEST ", rdn.getName() );

        // The first ATAV
        Ava atav = rdn.getAVA();

        assertEquals( "2.5.4.11=example", atav.getNormName() );
        assertEquals( "2.5.4.11", atav.getNormType() );
        assertEquals( "example", atav.getNormValue().get() );

        assertEquals( "ou", atav.getUpType() );
        assertEquals( "Example", atav.getUpValue().get() );

        assertEquals( "  ou  =  Example ", atav.getUpName() );

        assertEquals( 2, rdn.getNbAtavs() );

        // The second ATAV
        for ( Ava ava : rdn )
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
        Dn dn = new Dn( "  ou  =  Ex\\+mple ,  ou  =  COM " );

        dn.normalize( schemaManager );
        assertEquals( "2.5.4.11=ex\\+mple,2.5.4.11=com", dn.getNormName() );
        assertEquals( "  ou  =  Ex\\+mple ,  ou  =  COM ", dn.getName() );

        Rdn rdn = dn.getRdn();
        assertEquals( "2.5.4.11", rdn.getNormType() );
        assertEquals( "ex+mple", rdn.getNormValue().getString() );
        assertEquals( "2.5.4.11=ex\\+mple", rdn.getNormName() );
        assertEquals( "ou", rdn.getUpType() );
        assertEquals( "Ex+mple", rdn.getUpValue().getString() );
        assertEquals( "  ou  =  Ex\\+mple ", rdn.getName() );

        Ava atav = rdn.getAVA();

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
        Dn dn = new Dn( "  OU  =  Ex\\+mple + ou = T\\+ST\\  ,  ou  =  COM " );

        // ------------------------------------------------------------------
        // Before normalization
        assertEquals( "  OU  =  Ex\\+mple + ou = T\\+ST\\  ,  ou  =  COM ", dn.getName() );
        assertEquals( "ou=Ex\\+mple+ou=T\\+ST\\ ,ou=COM", dn.getNormName() );

        // Check the first Rdn
        Rdn rdn = dn.getRdn();
        assertEquals( "  OU  =  Ex\\+mple + ou = T\\+ST\\  ", rdn.getName() );
        assertEquals( "ou=Ex\\+mple+ou=T\\+ST\\ ", rdn.getNormName() );

        assertEquals( "OU", rdn.getUpType() );
        assertEquals( "ou", rdn.getNormType() );

        assertEquals( "Ex+mple", rdn.getUpValue().getString() );
        assertEquals( "Ex+mple", rdn.getNormValue().getString() );

        // The first ATAV
        Ava atav = rdn.getAVA();

        assertEquals( "  OU  =  Ex\\+mple ", atav.getUpName() );
        assertEquals( "ou=Ex\\+mple", atav.getNormName() );

        assertEquals( "ou", atav.getNormType() );
        assertEquals( "OU", atav.getUpType() );

        assertEquals( "Ex+mple", atav.getUpValue().get() );
        assertEquals( "Ex+mple", atav.getNormValue().get() );

        assertEquals( 2, rdn.getNbAtavs() );

        // The second ATAV
        for ( Ava ava : rdn )
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
        // Now normalize the Dn
        dn.normalize( schemaManager );

        assertEquals( "  OU  =  Ex\\+mple + ou = T\\+ST\\  ,  ou  =  COM ", dn.getName() );
        assertEquals( "2.5.4.11=ex\\+mple+2.5.4.11=t\\+st,2.5.4.11=com", dn.getNormName() );

        // Check the first Rdn
        rdn = dn.getRdn();
        assertEquals( "  OU  =  Ex\\+mple + ou = T\\+ST\\  ", rdn.getName() );
        assertEquals( "2.5.4.11=ex\\+mple+2.5.4.11=t\\+st", rdn.getNormName() );

        assertEquals( "OU", rdn.getUpType() );
        assertEquals( "2.5.4.11", rdn.getNormType() );

        assertEquals( "Ex+mple", rdn.getUpValue().getString() );
        assertEquals( "ex+mple", rdn.getNormValue().getString() );

        // The first ATAV
        atav = rdn.getAVA();

        assertEquals( "  OU  =  Ex\\+mple ", atav.getUpName() );
        assertEquals( "2.5.4.11=ex\\+mple", atav.getNormName() );

        assertEquals( "2.5.4.11", atav.getNormType() );
        assertEquals( "OU", atav.getUpType() );

        assertEquals( "Ex+mple", atav.getUpValue().get() );
        assertEquals( "ex+mple", atav.getNormValue().get() );

        assertEquals( 2, rdn.getNbAtavs() );

        // The second ATAV
        for ( Ava ava : rdn )
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
        Dn dn = Dn.EMPTY_DN;

        for ( Rdn rdn : dn )
        {
            fail( "Should not be there: rdn = " + rdn );
        }

        assertTrue( true );
    }


    @Test
    public void testIteratorOneRDN() throws Exception
    {
        Dn dn = new Dn( "ou=example" );
        int count = 0;

        for ( Rdn rdn : dn )
        {
            count++;
            assertEquals( "ou=example", rdn.getName() );
        }

        assertEquals( 1, count );
    }


    @Test
    public void testIteratorMultipleRDN() throws Exception
    {
        Dn dn = new Dn( "sn=joe+cn=doe,dc=apache,dc=org" );
        int count = 0;

        String[] expected = new String[]
            { "sn=joe+cn=doe", "dc=apache", "dc=org" };

        for ( Rdn rdn : dn.getRdns() )
        {
            assertEquals( expected[count], rdn.getName() );
            count++;
        }

        assertEquals( 3, count );
    }


    @Test
    public void testIsParentOfTrue() throws Exception
    {
        Dn dn = new Dn( "ou=example, dc=apache, dc=org" );
        Dn parent1 = new Dn( "ou=example,dc=apache, dc=org" );
        Dn parent2 = new Dn( "dc=apache, dc=org" );
        Dn parent3 = new Dn( "dc=org" );
        Dn notParent = new Dn( "ou=example,dc=apache, dc=com" );

        assertTrue( parent1.isAncestorOf( dn ) );
        assertTrue( parent2.isAncestorOf( dn ) );
        assertTrue( parent3.isAncestorOf( dn ) );
        assertFalse( notParent.isAncestorOf( dn ) );
    }


    @Test
    public void testIsDescendantOfTrue() throws Exception
    {
        Dn dn = new Dn( "ou=example, dc=apache, dc=org" );
        Dn parent1 = new Dn( "ou=example,dc=apache, dc=org" );
        Dn parent2 = new Dn( "dc=apache, dc=org" );
        Dn parent3 = new Dn( "dc=org" );
        Dn notParent = new Dn( "dc=apache, dc=com" );

        assertTrue( dn.isDescendantOf( parent1 ) );
        assertTrue( dn.isDescendantOf( parent2 ) );
        assertTrue( dn.isDescendantOf( parent3 ) );
        assertFalse( notParent.isDescendantOf( dn ) );
    }


    @Test
    public void testNormalize() throws Exception
    {
        Dn dn = new Dn( "ou=system" );
        assertFalse( dn.isNormalized() );

        dn = dn.add( "ou=users" );
        assertFalse( dn.isNormalized() );

        dn.normalize( schemaManager );
        assertTrue( dn.isNormalized() );

        dn = dn.add( "ou=x" );
        assertTrue( dn.isNormalized() );

        assertEquals( "2.5.4.11=x,2.5.4.11=users,2.5.4.11=system", dn.getNormName() );
        assertEquals( "ou=x,ou=users,ou=system", dn.getName() );

        dn.normalize( schemaManager );
        assertEquals( "2.5.4.11=x,2.5.4.11=users,2.5.4.11=system", dn.getNormName() );
        assertEquals( "ou=x,ou=users,ou=system", dn.getName() );

        Rdn rdn = new Rdn( "ou=system" );
        dn = new Dn();
        assertTrue( dn.isNormalized() );

        dn = dn.add( rdn );
        assertFalse( dn.isNormalized() );

        dn.normalize( schemaManager );
        assertTrue( dn.isNormalized() );

        Dn anotherDn = new Dn( "ou=x,ou=users" );

        dn = dn.addAll( anotherDn );
        assertTrue( dn.isNormalized() );

        dn.normalize( schemaManager );
        assertTrue( dn.isNormalized() );

        dn = dn.remove( 0 );
        assertTrue( dn.isNormalized() );
    }


    @Test
    public void testParseDnWithSlash() throws Exception
    {
        String dnStr = "dc=/vehicles/v1/";

        Dn dn = new Dn( dnStr );
        dn.normalize( schemaManager );

        assertEquals( dnStr, dn.toString() );
    }
    
    
    @Test
    public void testCreateDnFromRdnParent() throws Exception
    {
        String rdn = "cn=test";
        String parentDn = "ou=apache,ou=org";
        
        Dn dn = new Dn( rdn, parentDn );
        
        assertEquals( "cn=test,ou=apache,ou=org", dn.getName() );
    }
}
