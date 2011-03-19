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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;

import org.apache.directory.shared.ldap.model.exception.LdapException;
import org.apache.directory.shared.ldap.model.schema.SchemaManager;
import org.apache.directory.shared.ldap.schemamanager.impl.DefaultSchemaManager;
import org.apache.directory.shared.util.Strings;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mycila.junit.concurrent.Concurrency;
import com.mycila.junit.concurrent.ConcurrentJunitRunner;


/**
 * Test the Schema aware Rdn class
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrency()
public class SchemaAwareRdnTest
{
    /** A null schemaManager used in tests */
    private static SchemaManager schemaManager;
    
    @BeforeClass
    public static void setup() throws Exception
    {
        schemaManager = new DefaultSchemaManager();
    }
    

    /**
     * Test a null Rdn
     */
    @Test
    public void testRdnNull()
    {
        assertEquals( "", new Rdn( schemaManager ).toString() );
    }


    /**
     * test an empty Rdn
     * 
     * @throws LdapException
     */
    @Test
    public void testRdnEmpty() throws LdapException
    {
        assertEquals( "", new Rdn( schemaManager, "" ).toString() );
    }


    /**
     * test a simple Rdn : cn = b
     * 
     * @throws LdapException
     */
    @Test
    public void testRdnSimple() throws LdapException
    {
        assertEquals( "2.5.4.3=b", new Rdn( schemaManager, "cn = b" ).getNormName() );
    }


    /**
     * test a composite Rdn : cn = b, sn = e
     * 
     * @throws LdapException
     */
    @Test
    public void testRdnComposite() throws LdapException
    {
        assertEquals( "2.5.4.3=b+2.5.4.4=d", new Rdn( schemaManager, "cn = b + sn = d" ).getNormName() );
    }


    /**
     * test a composite Rdn with or without spaces: cn=b, cn =b, cn= b, cn = b, cn =
     * b
     * 
     * @throws LdapException
     */
    @Test
    public void testRdnCompositeWithSpace() throws LdapException
    {
        assertEquals( "2.5.4.3=b", new Rdn( schemaManager, "cn=b" ).getNormName() );
        assertEquals( "2.5.4.3=b", new Rdn( schemaManager, " cn=b" ).getNormName() );
        assertEquals( "2.5.4.3=b", new Rdn( schemaManager, "cn =b" ).getNormName() );
        assertEquals( "2.5.4.3=b", new Rdn( schemaManager, "cn= b" ).getNormName() );
        assertEquals( "2.5.4.3=b", new Rdn( schemaManager, "cn=b " ).getNormName() );
        assertEquals( "2.5.4.3=b", new Rdn( schemaManager, " cn =b" ).getNormName() );
        assertEquals( "2.5.4.3=b", new Rdn( schemaManager, " cn= b" ).getNormName() );
        assertEquals( "2.5.4.3=b", new Rdn( schemaManager, " cn=b " ).getNormName() );
        assertEquals( "2.5.4.3=b", new Rdn( schemaManager, "cn = b" ).getNormName() );
        assertEquals( "2.5.4.3=b", new Rdn( schemaManager, "cn =b " ).getNormName() );
        assertEquals( "2.5.4.3=b", new Rdn( schemaManager, "cn= b " ).getNormName() );
        assertEquals( "2.5.4.3=b", new Rdn( schemaManager, " cn = b" ).getNormName() );
        assertEquals( "2.5.4.3=b", new Rdn( schemaManager, " cn =b " ).getNormName() );
        assertEquals( "2.5.4.3=b", new Rdn( schemaManager, " cn= b " ).getNormName() );
        assertEquals( "2.5.4.3=b", new Rdn( schemaManager, "cn = b " ).getNormName() );
        assertEquals( "2.5.4.3=b", new Rdn( schemaManager, " cn = b " ).getNormName() );
    }


    /**
     * test a simple Rdn with differents separators : cn = b + sn = d
     * 
     * @throws LdapException
     */
    @Test
    public void testRdnSimpleMultivaluedAttribute() throws LdapException
    {
        String result = new Rdn( schemaManager, "cn = b + sn = d" ).getNormName();
        assertEquals( "2.5.4.3=b+2.5.4.4=d", result );
    }


    /**
     * test a composite Rdn with differents separators : cn=b+sn=d, gn=f + l=h +
     * c=j
     * 
     * @throws LdapException
     */
    @Test
    public void testRdnCompositeMultivaluedAttribute() throws LdapException
    {
        Rdn rdn = new Rdn( schemaManager, "cn =b+sn=d + gn=f + l  =h + c =j " );

        // NameComponent are not ordered
        assertEquals( "b", rdn.getValue( "CommonName" ) );
        assertEquals( "d", rdn.getValue( "2.5.4.4" ) );
        assertEquals( "f", rdn.getValue( "  gn  " ) );
        assertEquals( "h", rdn.getValue( "L" ) );
        assertEquals( "j", rdn.getValue( "c" ) );
    }


    /**
     * test a simple Rdn with an oid prefix (uppercase) : OID.2.5.4.3 = azerty
     * 
     * @throws LdapException
     */
    @Test
    public void testRdnOidUpper() throws LdapException
    {
        assertEquals( "2.5.4.3=azerty", new Rdn( schemaManager, "OID.2.5.4.3 =  azerty" ).getNormName() );
    }


    /**
     * test a simple Rdn with an oid prefix (lowercase) : oid.12.34.56 = azerty
     * 
     * @throws LdapException
     */
    @Test
    public void testRdnOidLower() throws LdapException
    {
        assertEquals( "2.5.4.3=azerty", new Rdn( schemaManager, "oid.2.5.4.3 = azerty" ).getNormName() );
    }


    /**
     * test a simple Rdn with an oid attribut wiithout oid prefix : 2.5.4.3 =
     * azerty
     * 
     * @throws LdapException
     */
    @Test
    public void testRdnOidWithoutPrefix() throws LdapException
    {
        assertEquals( "2.5.4.3=azerty", new Rdn( schemaManager, "2.5.4.3 = azerty" ).getNormName() );
    }


    /**
     * test a composite Rdn with an oid attribut wiithout oid prefix : 2.5.4.3 =
     * azerty; 2.5.4.4 = test
     * 
     * @throws LdapException
     */
    @Test
    public void testRdnCompositeOidWithoutPrefix() throws LdapException
    {
        String result = new Rdn( schemaManager, "2.5.4.3 = azerty + 2.5.4.4 = test" ).getNormName();
        assertEquals( "2.5.4.3=azerty+2.5.4.4=test", result );
    }


    /**
     * test a simple Rdn with pair char attribute value : l = \,\=\+\<\>\#\;\\\"\C3\A9"
     * 
     * @throws LdapException
     */
    @Test
    public void testRdnPairCharAttributeValue() throws LdapException
    {
        String rdn = Strings.utf8ToString(new byte[]
                {'l', '=', '\\', ',', '=', '\\', '+', '\\', '<', '\\', '>', '#', '\\', ';', '\\', '\\', '\\', '"', '\\',
                        'C', '3', '\\', 'A', '9'});
        assertEquals( "2.5.4.7=\\,=\\+\\<\\>#\\;\\\\\\\"\u00E9", new Rdn( schemaManager, rdn ).getNormName() );
    }


    /**
     * test a simple Rdn with hexString attribute value : userCertificate = #0010A0AAFF
     */
    @Test
    public void testRdnHexStringAttributeValue() throws LdapException
    {
        assertEquals( "2.5.4.36=#0010A0AAFF", new Rdn( schemaManager, "userCertificate = #0010A0AAFF" ).getNormName() );
    }

    /**
     * test exception from illegal hexString attribute value : cn=#zz.
     */
    @Test
    public void testBadRdnHexStringAttributeValue() throws LdapException
    {
        try
        {
            new Rdn( schemaManager, "cn=#zz" );
            fail();
        }
        catch ( LdapException ine )
        {
            assertTrue( true );
        }
    }

    /**
     * test a simple Rdn with quoted attribute value : cn = "quoted \"value"
     * 
     * @throws LdapException
     */
    @Test
    public void testRdnQuotedAttributeValue() throws LdapException
    {
        assertEquals( "2.5.4.3=quoted \\\"value", new Rdn( schemaManager, "cn = quoted \\\"value" ).getNormName() );
    }


    /**
     * Test the clone method for a Rdn.
     */
    @Test
    public void testParseRDNNull()
    {
        Rdn rdn = null;

        try
        {
            RdnParser.parse( "cn=d", rdn );
            fail();
        }
        catch ( LdapException ine )
        {
            assertTrue( true );
        }
    }


    /**
     * Test the clone method for a Rdn.
     * 
     * @throws LdapException
     */
    @Test
    public void testRDNCloningOneNameComponent() throws LdapException
    {
        Rdn rdn = new Rdn( schemaManager, "CN", "cn", "B", "b" );

        Rdn rdnClone = (Rdn) rdn.clone();

        RdnParser.parse( "cn=d", rdn );

        assertEquals( "b", rdnClone.getValue( "Cn" ) );
    }


    /**
     * Test teh creation of a new Rdn
     * 
     * @throws org.apache.directory.shared.ldap.model.exception.LdapException
     */
    @Test
    public void testRDNCreation() throws LdapException
    {
        Rdn rdn = new Rdn( schemaManager, "CN", "  b  " );
        assertEquals( "2.5.4.3=b", rdn.getNormName() );
        assertEquals( "CN=  b  ", rdn.getName() );
    }


    /**
     * Test the clone method for a Rdn.
     * 
     * @throws LdapException
     */
    @Test
    public void testRDNCloningTwoNameComponent() throws LdapException
    {
        Rdn rdn = new Rdn( schemaManager, "cn = b + sn = bb" );

        Rdn rdnClone = (Rdn) rdn.clone();

        rdn.clear();
        RdnParser.parse( "l=d", rdn );

        assertEquals( "b", rdnClone.getValue( "2.5.4.3" ) );
        assertEquals( "bb", rdnClone.getValue( "SN" ) );
        assertEquals( "", rdnClone.getValue( "l" ) );
    }


    /**
     * Test the equals method for a Rdn.
     * 
     * @throws LdapException
     */
    @Test
    public void testRDNCompareToNull() throws LdapException
    {
        Rdn rdn1 = new Rdn( schemaManager, " cn = b + sn = d + l = f + gn = h " );
        Rdn rdn2 = null;
        assertFalse( rdn1.equals( rdn2 ) );
    }


    /**
     * Compares a composite NC to a single NC.
     * 
     * @throws LdapException
     */
    @Test
    public void testRDNCompareToNCS2NC() throws LdapException
    {
        Rdn rdn1 = new Rdn( schemaManager, " cn = b + sn = d + l = f + gn = h " );
        Rdn rdn2 = new Rdn( schemaManager, " cn = b " );
        assertFalse( rdn1.equals( rdn2 ) );
    }


    /**
     * Compares a single NC to a composite NC.
     * 
     * @throws LdapException
     */
    @Test
    public void testRDNCompareToNC2NCS() throws LdapException
    {
        Rdn rdn1 = new Rdn( schemaManager, " sn = b " );
        Rdn rdn2 = new Rdn( schemaManager, " cn = b + sn = d + l = f + gn = h " );

        assertFalse( rdn1.equals( rdn2 ) );
    }


    /**
     * Compares a composite NCS to a composite NCS in the same order.
     * 
     * @throws LdapException
     */
    @Test
    public void testRDNCompareToNCS2NCSOrdered() throws LdapException
    {
        Rdn rdn1 = new Rdn( schemaManager, " cn = b + sn = d + gn = f + l = h " );
        Rdn rdn2 = new Rdn( schemaManager, " cn = b + sn = d + gn = f + l = h " );

        assertTrue( rdn1.equals( rdn2 ) );
    }


    /**
     * Compares a composite NCS to a composite NCS in a different order.
     * 
     * @throws LdapException
     */
    @Test
    public void testRDNCompareToNCS2NCSUnordered() throws LdapException
    {
        Rdn rdn1 = new Rdn( schemaManager, " cn = b + gn = f + l = h + sn = d " );
        Rdn rdn2 = new Rdn( schemaManager, " cn = b + sn = d + gn = f + l = h " );

        assertTrue( rdn1.equals( rdn2 ) );
    }


    /**
     * Compares a composite NCS to a different composite NCS.
     * 
     * @throws LdapException
     */
    @Test
    public void testRDNCompareToNCS2NCSNotEquals() throws LdapException
    {
        Rdn rdn1 = new Rdn( schemaManager, " cn = f + sn = h + l = d " );
        Rdn rdn2 = new Rdn( schemaManager, " l = d + cn = h + sn = h " );

        assertFalse( rdn1.equals( rdn2 ) );
        assertFalse( rdn2.equals( rdn1 ) );
    }


    /**
     * Test for DIRSHARED-2.
     * The first ATAV is equal, the second or following ATAV differs.
     * 
     * @throws LdapException
     */
    @Test
    public void testCompareSecondAtav() throws LdapException
    {
        // the second ATAV differs
        Rdn rdn1 = new Rdn( schemaManager,  " cn = b + sn = d " );
        Rdn rdn2 = new Rdn( schemaManager,  " cn = b + sn = y " );
        assertFalse( rdn1.equals( rdn2 ) );
        assertFalse( rdn2.equals( rdn1 ) );

        // the third ATAV differs
        Rdn rdn3 = new Rdn( schemaManager,  " cn = b + sn = d + l = f " );
        Rdn rdn4 = new Rdn( schemaManager,  " cn = b + sn = d + l = y " );
        assertFalse( rdn3.equals( rdn4 ) );
        assertFalse( rdn4.equals( rdn3 ) );

        // the second ATAV differs in value only
        Rdn rdn5 = new Rdn( schemaManager,  " cn = b + sn = c " );
        Rdn rdn6 = new Rdn( schemaManager,  " cn = b + sn = y " );
        assertFalse( rdn5.equals( rdn6 ) );
        assertFalse( rdn6.equals( rdn5 ) );
    }


    /**
     * Test for DIRSHARED-2.
     * The compare operation should return a correct value (1 or -1)
     * depending on the ATAVs, not on their position.
     * 
     * @throws LdapException
     */
    @Test
    public void testCompareIndependentFromOrder() throws LdapException
    {
        Rdn rdn1 = new Rdn( schemaManager,  " cn = b + sn = d " );
        Rdn rdn2 = new Rdn( schemaManager,  " sn = d + cn = b " );
        assertTrue( rdn1.equals( rdn2 ) );

        rdn1 = new Rdn( schemaManager,  " cn = b + sn = e " );
        rdn2 = new Rdn( schemaManager,  " sn = d + cn = b " );
        assertFalse( rdn1.equals( rdn2 ) );
        assertFalse( rdn2.equals( rdn1 ) );

        rdn1 = new Rdn( schemaManager,  " cn = b + sn = d " );
        rdn2 = new Rdn( schemaManager,  " l = f + gn = h " );
        assertFalse( rdn1.equals( rdn2 ) );
        assertFalse( rdn2.equals( rdn1 ) );
    }


    /**
     * Test for DIRSHARED-3.
     * Tests that equals() is invertable for single-valued RDNs.
     * 
     * @throws LdapException
     */
    @Test
    public void testCompareInvertableNC2NC() throws LdapException
    {
        Rdn rdn1 = new Rdn( schemaManager,  " cn = b " );
        Rdn rdn2 = new Rdn( schemaManager,  " cn = c " );
        assertFalse( rdn1.equals( rdn2 ) );
        assertFalse( rdn2.equals( rdn1 ) );

    }


    /**
     * Test for DIRSHARED-3.
     * Tests that equals() is invertable for multi-valued RDNs with different values.
     * 
     * @throws LdapException
     */
    @Test
    public void testCompareInvertableNCS2NCSDifferentValues() throws LdapException
    {
        Rdn rdn1 = new Rdn( schemaManager,  " cn = b + sn = c " );
        Rdn rdn2 = new Rdn( schemaManager,  " cn = b + sn = y " );
        assertFalse( rdn1.equals( rdn2 ) );
        assertFalse( rdn2.equals( rdn1 ) );
    }


    /**
     * Test for DIRSHARED-3.
     * Tests that equals() is invertable for multi-valued RDNs with different types.
     * 
     * @throws org.apache.directory.shared.ldap.model.exception.LdapException
     */
    @Test
    public void testCompareInvertableNCS2NCSDifferentTypes() throws LdapException
    {
        Rdn rdn1 = new Rdn( schemaManager,  " cn = b + sn = d  " );
        Rdn rdn2 = new Rdn( schemaManager,  " l = f + gn = h " );
        assertFalse( rdn1.equals( rdn2 ) );
        assertFalse( rdn2.equals( rdn1 ) );
    }


    /**
     * Test for DIRSHARED-3.
     * Tests that equals() is invertable for multi-valued RDNs with different order.
     * 
     * @throws LdapException
     */
    @Test
    public void testCompareInvertableNCS2NCSUnordered() throws LdapException
    {
        Rdn rdn1 = new Rdn( schemaManager,  " sn = d + cn = b " );
        Rdn rdn2 = new Rdn( schemaManager,  " cn = b + l = f " );
        assertFalse( rdn1.equals( rdn2 ) );
        assertFalse( rdn2.equals( rdn1 ) );
    }


    /**
     * Compares with a null Rdn.
     * 
     * @throws LdapException
     */
    @Test
    public void testRDNCompareToNullRdn() throws LdapException
    {
        Rdn rdn1 = new Rdn( schemaManager,  " cn = b " );

        assertFalse( rdn1.equals( null ) );
    }


    /**
     * Compares a simple NC to a simple NC.
     * 
     * @throws LdapException
     */
    @Test
    public void testRDNCompareToNC2NC() throws LdapException
    {
        Rdn rdn1 = new Rdn( schemaManager,  " cn = b " );
        Rdn rdn2 = new Rdn( schemaManager,  " cn = b " );

        assertTrue( rdn1.equals( rdn2 ) );
    }


    /**
     * Compares a simple NC to a simple NC in UperCase.
     * 
     * @throws LdapException
     */
    @Test
    public void testRDNCompareToNC2NCUperCase() throws LdapException
    {
        Rdn rdn1 = new Rdn( schemaManager,  " cn = b " );
        Rdn rdn2 = new Rdn( schemaManager,  " CN = b " );

        assertTrue( rdn1.equals( rdn2 ) );
    }


    /**
     * Compares a simple NC to a different simple NC.
     * 
     * @throws LdapException
     */
    @Test
    public void testRDNCompareToNC2NCNotEquals() throws LdapException
    {
        Rdn rdn1 = new Rdn( schemaManager,  " cn = b " );
        Rdn rdn2 = new Rdn( schemaManager,  " CN = d " );

        assertFalse( rdn1.equals( rdn2 ) );
    }


    /**
     * 
     * Test the getValue method.
     *
     * @throws LdapException
     */
    @Test
    public void testGetValue() throws LdapException
    {
        Rdn rdn = new Rdn( schemaManager,  " cn = b + sn = f + gn = h + l = d " );

        assertEquals( "b", rdn.getNormValue().getString() );
    }


    /**
     * 
     * Test the getType method.
     *
     * @throws LdapException
     */
    @Test
    public void testGetType() throws LdapException
    {
        Rdn rdn = new Rdn( schemaManager,  " cn = b + sn = f + gn = h + l = d " );

        assertEquals( "2.5.4.3", rdn.getNormType() );
    }


    /**
     * Test the getSize method.
     *
     * @throws LdapException
     */
    @Test
    public void testGetSize() throws LdapException
    {
        Rdn rdn = new Rdn( schemaManager,  " cn = b + sn = f + gn = h + l = d " );

        assertEquals( 4, rdn.size() );
    }


    /**
     * Test the getSize method.
     *
     */
    @Test
    public void testGetSize0()
    {
        Rdn rdn = new Rdn( schemaManager );

        assertEquals( 0, rdn.size() );
    }


    /**
     * Test the equals method
     *
     * @throws LdapException
     */
    @Test
    public void testEquals() throws LdapException
    {
        Rdn rdn = new Rdn( schemaManager,  "cn=b + sn=d + gn=f" );

        assertFalse( rdn.equals( null ) );
        assertFalse( rdn.equals( "test" ) );
        assertFalse( rdn.equals( new Rdn( schemaManager,  "cn=c + sn=d + gn=f" ) ) );
        assertFalse( rdn.equals( new Rdn( schemaManager,  "cn=b" ) ) );
        assertTrue( rdn.equals( new Rdn( schemaManager,  "cn=b + sn=d + gn=f" ) ) );
        assertTrue( rdn.equals( new Rdn( schemaManager,  "cn=b + SN=d + GN=f" ) ) );
        assertTrue( rdn.equals( new Rdn( schemaManager,  "sn=d + gn=f + CN=b" ) ) );
    }


    @Test
    public void testUnescapeValueHexa()
    {
        byte[] res = ( byte[] ) Rdn.unescapeValue("#fF");

        assertEquals( "0xFF ", Strings.dumpBytes(res) );

        res = ( byte[] ) Rdn.unescapeValue("#0123456789aBCDEF");
        assertEquals( "0x01 0x23 0x45 0x67 0x89 0xAB 0xCD 0xEF ", Strings.dumpBytes(res) );
    }


    @Test
    public void testUnescapeValueHexaWrong()
    {
        try
        {
            Rdn.unescapeValue("#fF1");
            fail(); // Should not happen
        }
        catch ( IllegalArgumentException iae )
        {
            assertTrue( true );
        }
    }


    @Test
    public void testUnescapeValueString()
    {
        String res = ( String ) Rdn.unescapeValue("azerty");

        assertEquals( "azerty", res );
    }


    @Test
    public void testUnescapeValueStringSpecial()
    {
        String res = ( String ) Rdn.unescapeValue("\\\\\\#\\,\\+\\;\\<\\>\\=\\\"\\ ");

        assertEquals( "\\#,+;<>=\" ", res );
    }


    @Test
    public void testUnescapeValueStringWithSpaceInTheMiddle()
    {
        String res = ( String ) Rdn.unescapeValue("a b");

        assertEquals( "a b", res );
    }


    @Test
    public void testUnescapeValueStringWithSpaceInAtTheBeginning()
    {
        String res = ( String ) Rdn.unescapeValue("\\ a b");

        assertEquals( " a b", res );
    }


    @Test
    public void testUnescapeValueStringWithSpaceInAtTheEnd()
    {
        String res = ( String ) Rdn.unescapeValue("a b\\ ");

        assertEquals( "a b ", res );
    }
    
    
    @Test
    public void testUnescapeValueStringWithPoundInTheMiddle()
    {
        String res = ( String ) Rdn.unescapeValue("a#b");

        assertEquals( "a#b", res );
    }
    
    
    @Test
    public void testUnescapeValueStringWithPoundAtTheEnd()
    {
        String res = ( String ) Rdn.unescapeValue("ab#");

        assertEquals( "ab#", res );
    }
    
    
    @Test
    public void testEscapeValueString()
    {
        String res = Rdn.escapeValue(Strings.getBytesUtf8("azerty"));

        assertEquals( "azerty", res );
    }


    @Test
    public void testEscapeValueStringSpecial()
    {
        String res = Rdn.escapeValue(Strings.getBytesUtf8("\\#,+;<>=\" "));

        assertEquals( "\\\\#\\,\\+\\;\\<\\>\\=\\\"\\ ", res );
    }


    @Test
    public void testEscapeValueNumeric()
    {
        String res = Rdn.escapeValue(new byte[]
                {'-', 0x00, '-', 0x1F, '-', 0x7F, '-'});

        assertEquals( "-\\00-\\1F-\\7F-", res );
    }


    @Test
    public void testEscapeValueMix()
    {
        String res = Rdn.escapeValue(new byte[]
                {'\\', 0x00, '-', '+', '#', 0x7F, '-'});

        assertEquals( "\\\\\\00-\\+#\\7F-", res );
    }


    @Test
    public void testDIRSERVER_703() throws LdapException
    {
        Rdn rdn = new Rdn( schemaManager,  "cn=Kate Bush+sn=Bush" );
        assertEquals( "cn=Kate Bush+sn=Bush", rdn.getName() );
    }


    @Test
    public void testMultiValuedIterator() throws LdapException
    {
        Rdn rdn = new Rdn( schemaManager,  "cn=Kate Bush+sn=Bush" );
        Iterator<Ava> iterator = rdn.iterator();
        assertNotNull( iterator );
        assertTrue( iterator.hasNext() );
        assertNotNull( iterator.next() );
        assertTrue( iterator.hasNext() );
        assertNotNull( iterator.next() );
        assertFalse( iterator.hasNext() );
    }


    @Test
    public void testSingleValuedIterator() throws LdapException
    {
        Rdn rdn = new Rdn( schemaManager,  "cn=Kate Bush" );
        Iterator<Ava> iterator = rdn.iterator();
        assertNotNull( iterator );
        assertTrue( iterator.hasNext() );
        assertNotNull( iterator.next() );
        assertFalse( iterator.hasNext() );
    }


    @Test
    public void testEmptyIterator()
    {
        Rdn rdn = new Rdn( schemaManager );
        Iterator<Ava> iterator = rdn.iterator();
        assertNotNull( iterator );
        assertFalse( iterator.hasNext() );
    }


    @Test
    public void testRdnWithSpaces() throws LdapException
    {
        Rdn rdn = new Rdn( schemaManager,  "cn=a\\ b\\ c" );
        assertEquals( "2.5.4.3=a b c", rdn.getNormName() );
    }


    @Test
    public void testEscapedSpaceInValue() throws LdapException
    {
        Rdn rdn1 = new Rdn( schemaManager,  "cn=a b c" );
        Rdn rdn2 = new Rdn( schemaManager,  "cn=a\\ b\\ c" );
        assertEquals( "2.5.4.3=a b c", rdn1.getNormName() );
        assertEquals( "2.5.4.3=a b c", rdn2.getNormName() );
        assertTrue( rdn1.equals( rdn2 ) );

        Rdn rdn3 = new Rdn( schemaManager,  "cn=\\ a b c\\ " );
        Rdn rdn4 = new Rdn( schemaManager,  "cn=\\ a\\ b\\ c\\ " );
        assertEquals( "2.5.4.3=a b c", rdn3.getNormName() );
        assertEquals( "cn=\\ a b c\\ ", rdn3.getName() );
        assertEquals( "2.5.4.3=a b c", rdn4.getNormName() );
        assertEquals( "cn=\\ a\\ b\\ c\\ ", rdn4.getName() );
        assertTrue( rdn3.equals( rdn4 ) );
    }


    @Test
    public void testEscapedHashInValue() throws LdapException
    {
        Rdn rdn1 = new Rdn( schemaManager,  "cn=a#b#c" );
        Rdn rdn2 = new Rdn( schemaManager,  "cn=a\\#b\\#c" );
        assertEquals( "2.5.4.3=a#b#c", rdn1.getNormName() );
        assertEquals( "2.5.4.3=a#b#c", rdn2.getNormName() );
        assertTrue( rdn1.equals( rdn2 ) );

        Rdn rdn3 = new Rdn( schemaManager,  "cn=\\#a#b#c\\#" );
        Rdn rdn4 = new Rdn( schemaManager,  "cn=\\#a\\#b\\#c\\#" );
        assertEquals( "2.5.4.3=\\#a#b#c#", rdn3.getNormName() );
        assertEquals( "2.5.4.3=\\#a#b#c#", rdn4.getNormName() );
        assertTrue( rdn3.equals( rdn4 ) );
    }


    @Test
    public void testEscapedAttributeValue()
    {
        // space doesn't need to be escaped in the middle of a string
        assertEquals( "a b", Rdn.escapeValue("a b") );
        assertEquals( "a b c", Rdn.escapeValue("a b c") );
        assertEquals( "a b c d", Rdn.escapeValue("a b c d") );

        // space must be escaped at the beginning and the end of a string
        assertEquals( "\\ a b", Rdn.escapeValue(" a b") );
        assertEquals( "a b\\ ", Rdn.escapeValue("a b ") );
        assertEquals( "\\ a b\\ ", Rdn.escapeValue(" a b ") );
        assertEquals( "\\  a  b \\ ", Rdn.escapeValue("  a  b  ") );

        // hash doesn't need to be escaped in the middle and the end of a string
        assertEquals( "a#b", Rdn.escapeValue("a#b") );
        assertEquals( "a#b#", Rdn.escapeValue("a#b#") );
        assertEquals( "a#b#c", Rdn.escapeValue("a#b#c") );
        assertEquals( "a#b#c#", Rdn.escapeValue("a#b#c#") );
        assertEquals( "a#b#c#d", Rdn.escapeValue("a#b#c#d") );
        assertEquals( "a#b#c#d#", Rdn.escapeValue("a#b#c#d#") );

        // hash must be escaped at the beginning of a string
        assertEquals( "\\#a#b", Rdn.escapeValue("#a#b") );
        assertEquals( "\\##a#b", Rdn.escapeValue("##a#b") );
    }


    /** Serialization tests ------------------------------------------------- */

    /**
     * Test serialization of an empty Rdn
     */
    @Test
    public void testEmptyRDNSerialization() throws LdapException, IOException, ClassNotFoundException
    {
        Rdn rdn = new Rdn( schemaManager,  "" );

        rdn.normalize();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        out.writeObject( rdn );

        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        Rdn rdn2 = (Rdn)in.readObject();

        assertEquals( rdn, rdn2 );
    }


    @Test
    public void testNullRdnSerialization() throws IOException, ClassNotFoundException
    {
        Rdn rdn = new Rdn( schemaManager );

        rdn.normalize();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        out.writeObject( rdn );

        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        Rdn rdn2 = (Rdn)in.readObject();

        assertEquals( rdn, rdn2 );
    }


    /**
     * Test serialization of a simple Rdn
     */
    @Test
    public void testSimpleRdnSerialization() throws LdapException, IOException, ClassNotFoundException
    {
        Rdn rdn = new Rdn( schemaManager,  "cn=b" );
        rdn.normalize();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        rdn.writeExternal( out );

        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        Rdn rdn2 = new Rdn( schemaManager );
        rdn2.readExternal( in );

        assertEquals( rdn, rdn2 );
    }


    /**
     * Test serialization of a simple Rdn
     */
    @Test
    public void testSimpleRdn2Serialization() throws LdapException, IOException, ClassNotFoundException
    {
        Rdn rdn = new Rdn( schemaManager,  " CN  = DEF " );
        rdn.normalize();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        rdn.writeExternal( out );

        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        Rdn rdn2 = new Rdn( schemaManager );
        rdn2.readExternal( in );

        assertEquals( rdn, rdn2 );
    }


    /**
     * Test serialization of a simple Rdn with no value
     */
    @Test
    public void testSimpleRdnNoValueSerialization() throws LdapException, IOException, ClassNotFoundException
    {
        Rdn rdn = new Rdn( schemaManager,  " CN  =" );
        rdn.normalize();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        rdn.writeExternal( out );

        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        Rdn rdn2 = new Rdn( schemaManager );
        rdn2.readExternal( in );

        assertEquals( rdn, rdn2 );
    }


    /**
     * Test serialization of a simple Rdn with one value
     */
    @Test
    public void testSimpleRdnOneValueSerialization() throws LdapException, IOException, ClassNotFoundException
    {
        Rdn rdn = new Rdn( schemaManager,  " CN  = def " );
        rdn.normalize();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        rdn.writeExternal( out );

        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        Rdn rdn2 = new Rdn( schemaManager );
        rdn2.readExternal( in );

        assertEquals( rdn, rdn2 );
    }


    /**
     * Test serialization of a simple Rdn with three values
     */
    @Test
    public void testSimpleRdnThreeValuesSerialization() throws LdapException, IOException, ClassNotFoundException
    {
        Rdn rdn = new Rdn( schemaManager,  " CN = a + SN = b + GN = c " );
        rdn.normalize();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        rdn.writeExternal( out );

        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        Rdn rdn2 = new Rdn( schemaManager );
        rdn2.readExternal( in );

        assertEquals( rdn, rdn2 );
    }


    /**
     * Test serialization of a simple Rdn with three unordered values
     */
    @Test
    public void testSimpleRdnThreeValuesUnorderedSerialization() throws LdapException, IOException,
        ClassNotFoundException
    {
        Rdn rdn = new Rdn( schemaManager,  " CN = b + SN = a + GN = c " );
        rdn.normalize();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        rdn.writeExternal( out );

        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        Rdn rdn2 = new Rdn( schemaManager );
        rdn2.readExternal( in );

        assertEquals( rdn, rdn2 );
    }


    /**
     * test an Rdn with empty value
     */
    @Test
    public void testRdnWithEmptyValue() throws LdapException
    {
        assertTrue( RdnParser.isValid("cn=") );
        assertTrue( RdnParser.isValid( "cn=\"\"" ) );
        assertEquals( "2.5.4.3=", new Rdn( schemaManager,  "cn=\"\"" ).getNormName() );
        assertEquals( "2.5.4.3=", new Rdn( schemaManager,  "cn=" ).getNormName() );
    }


    /**
     * test an Rdn with escaped comma
     */
    @Test
    public void testRdnWithEscapedComa() throws LdapException
    {
        assertTrue( RdnParser.isValid( "cn=b\\,c" ) );
        assertEquals( "2.5.4.3=b\\,c", new Rdn( schemaManager,  "cn=b\\,c" ).getNormName() );

        assertTrue( RdnParser.isValid( "cn=\"b,c\"" ) );
        assertEquals( "2.5.4.3=b\\,c", new Rdn( schemaManager,  "cn=\"b,c\"" ).getNormName() );
        assertEquals( "cn=\"b,c\"", new Rdn( schemaManager,  "cn=\"b,c\"" ).getName() );

        assertTrue( RdnParser.isValid( "cn=\"b\\,c\"" ) );
        Rdn rdn = new Rdn( schemaManager,  "cn=\"b\\,c\"" );
        assertEquals( "cn=\"b\\,c\"", rdn.getName() );
        assertEquals( "2.5.4.3=b\\,c", rdn.getNormName() );
    }


    /**
     * Tests the equals and equals results of cloned multi-valued RDNs.
     * Test for DIRSHARED-9.
     * 
     * @throws LdapException
     */
    @Test
    public void testComparingOfClonedMultiValuedRDNs() throws LdapException
    {
        // Use upper case attribute types to test if normalized types are used 
        // for comparison
        Rdn rdn = new Rdn( schemaManager,  " CN = b + SN = d" );
        Rdn clonedRdn = (Rdn) rdn.clone();

        assertTrue( rdn.equals( clonedRdn ) );
    }


    /**
     * Tests the equals and equals results of copy constructed multi-valued RDNs.
     * Test for DIRSHARED-9.
     * 
     * @throws LdapException
     */
    @Test
    public void testComparingOfCopyConstructedMultiValuedRDNs() throws LdapException
    {
        // Use upper case attribute types to test if normalized types are used 
        // for comparison
        Rdn rdn = new Rdn( schemaManager,  " CN = b + SN = d" );
        Rdn copiedRdn = new Rdn( rdn );

        assertTrue( rdn.equals( copiedRdn ) );
    }


    /**
     * test the UpName method on a Rdn with more than one atav
     */
    @Test 
    public void testGetUpNameMultipleAtav() throws LdapException
    {
        Rdn rdn = new Rdn( schemaManager,  " CN = b + SN = d " );
        
        assertEquals( " CN = b + SN = d ", rdn.getName() );
    }
    
    
    @Test
    public void testSchemaAware() throws LdapException
    {
        Rdn rdn = new Rdn( "cn=John" );
        
        assertFalse( rdn.isSchemaAware() );

        rdn.applySchemaManager( schemaManager );

        assertTrue( rdn.isSchemaAware() );
    }
}
