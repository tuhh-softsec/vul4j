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

import org.apache.directory.junit.tools.Concurrent;
import org.apache.directory.junit.tools.ConcurrentJunitRunner;
import org.apache.directory.shared.ldap.model.exception.LdapException;
import org.apache.directory.shared.ldap.model.name.Ava;
import org.apache.directory.shared.ldap.model.name.Rdn;
import org.apache.directory.shared.ldap.model.name.RdnParser;
import org.apache.directory.shared.ldap.model.name.RdnSerializer;
import org.apache.directory.shared.util.Strings;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test the class Rdn
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent()
public class RdnTest
{
    // ~ Methods
    // ------------------------------------------------------------------------------------
    /**
     * Test a null Rdn
     */
    @Test
    public void testRdnNull()
    {
        assertEquals( "", new Rdn().toString() );
    }


    /**
     * test an empty Rdn
     * 
     * @throws LdapException
     */
    @Test
    public void testRdnEmpty() throws LdapException
    {
        assertEquals( "", new Rdn( "" ).toString() );
    }


    /**
     * test a simple Rdn : a = b
     * 
     * @throws LdapException
     */
    @Test
    public void testRdnSimple() throws LdapException
    {
        assertEquals( "a=b", new Rdn( "a = b" ).getNormName() );
    }


    /**
     * test a composite Rdn : a = b, d = e
     * 
     * @throws LdapException
     */
    @Test
    public void testRdnComposite() throws LdapException
    {
        assertEquals( "a=b+c=d", new Rdn( "a = b + c = d" ).getNormName() );
    }


    /**
     * test a composite Rdn with or without spaces: a=b, a =b, a= b, a = b, a =
     * b
     * 
     * @throws LdapException
     */
    @Test
    public void testRdnCompositeWithSpace() throws LdapException
    {
        assertEquals( "a=b", new Rdn( "a=b" ).getNormName() );
        assertEquals( "a=b", new Rdn( " a=b" ).getNormName() );
        assertEquals( "a=b", new Rdn( "a =b" ).getNormName() );
        assertEquals( "a=b", new Rdn( "a= b" ).getNormName() );
        assertEquals( "a=b", new Rdn( "a=b " ).getNormName() );
        assertEquals( "a=b", new Rdn( " a =b" ).getNormName() );
        assertEquals( "a=b", new Rdn( " a= b" ).getNormName() );
        assertEquals( "a=b", new Rdn( " a=b " ).getNormName() );
        assertEquals( "a=b", new Rdn( "a = b" ).getNormName() );
        assertEquals( "a=b", new Rdn( "a =b " ).getNormName() );
        assertEquals( "a=b", new Rdn( "a= b " ).getNormName() );
        assertEquals( "a=b", new Rdn( " a = b" ).getNormName() );
        assertEquals( "a=b", new Rdn( " a =b " ).getNormName() );
        assertEquals( "a=b", new Rdn( " a= b " ).getNormName() );
        assertEquals( "a=b", new Rdn( "a = b " ).getNormName() );
        assertEquals( "a=b", new Rdn( " a = b " ).getNormName() );
    }


    /**
     * test a simple Rdn with differents separators : a = b + c = d
     * 
     * @throws LdapException
     */
    @Test
    public void testRdnSimpleMultivaluedAttribute() throws LdapException
    {
        String result = new Rdn( "a = b + c = d" ).getNormName();
        assertEquals( "a=b+c=d", result );
    }


    /**
     * test a composite Rdn with differents separators : a=b+c=d, e=f + g=h +
     * i=j
     * 
     * @throws LdapException
     */
    @Test
    public void testRdnCompositeMultivaluedAttribute() throws LdapException
    {
        Rdn rdn = new Rdn( "a =b+c=d + e=f + g  =h + i =j " );

        // NameComponent are not ordered
        assertEquals( "b", rdn.getValue( "a" ) );
        assertEquals( "d", rdn.getValue( "c" ) );
        assertEquals( "f", rdn.getValue( "  E  " ) );
        assertEquals( "h", rdn.getValue( "g" ) );
        assertEquals( "j", rdn.getValue( "i" ) );
    }


    /**
     * test a simple Rdn with an oid prefix (uppercase) : OID.12.34.56 = azerty
     * 
     * @throws LdapException
     */
    @Test
    public void testRdnOidUpper() throws LdapException
    {
        assertEquals( "oid.12.34.56=azerty", new Rdn( "OID.12.34.56 =  azerty" ).getNormName() );
    }


    /**
     * test a simple Rdn with an oid prefix (lowercase) : oid.12.34.56 = azerty
     * 
     * @throws LdapException
     */
    @Test
    public void testRdnOidLower() throws LdapException
    {
        assertEquals( "oid.12.34.56=azerty", new Rdn( "oid.12.34.56 = azerty" ).getNormName() );
    }


    /**
     * test a simple Rdn with an oid attribut wiithout oid prefix : 12.34.56 =
     * azerty
     * 
     * @throws LdapException
     */
    @Test
    public void testRdnOidWithoutPrefix() throws LdapException
    {
        assertEquals( "12.34.56=azerty", new Rdn( "12.34.56 = azerty" ).getNormName() );
    }


    /**
     * test a composite Rdn with an oid attribut wiithout oid prefix : 12.34.56 =
     * azerty; 7.8 = test
     * 
     * @throws LdapException
     */
    @Test
    public void testRdnCompositeOidWithoutPrefix() throws LdapException
    {
        String result = new Rdn( "12.34.56 = azerty + 7.8 = test" ).getNormName();
        assertEquals( "12.34.56=azerty+7.8=test", result );
    }


    /**
     * test a simple Rdn with pair char attribute value : a = \,\=\+\<\>\#\;\\\"\C3\A9"
     * 
     * @throws LdapException
     */
    @Test
    public void testRdnPairCharAttributeValue() throws LdapException
    {
        String rdn = Strings.utf8ToString(new byte[]
                {'a', '=', '\\', ',', '=', '\\', '+', '\\', '<', '\\', '>', '#', '\\', ';', '\\', '\\', '\\', '"', '\\',
                        'C', '3', '\\', 'A', '9'});
        assertEquals( "a=\\,=\\+\\<\\>#\\;\\\\\\\"\u00E9", new Rdn( rdn ).getNormName() );
    }


    /**
     * test a simple Rdn with hexString attribute value : a = #0010A0AAFF
     */
    @Test
    public void testRdnHexStringAttributeValue() throws LdapException
    {
        assertEquals( "a=#0010A0AAFF", new Rdn( "a = #0010A0AAFF" ).getNormName() );
    }

    /**
     * test exception from illegal hexString attribute value : a=#zz.
     */
    @Test
    public void testBadRdnHexStringAttributeValue() throws LdapException
    {
        try
        {
            new Rdn( "a=#zz" );
            fail();
        }
        catch ( LdapException ine )
        {
            assertTrue( true );
        }
    }

    /**
     * test a simple Rdn with quoted attribute value : a = "quoted \"value"
     * 
     * @throws LdapException
     */
    @Test
    public void testRdnQuotedAttributeValue() throws LdapException
    {
        assertEquals( "a=quoted \\\"value", new Rdn( "a = quoted \\\"value" ).getNormName() );
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
            RdnParser.parse( "c=d", rdn );
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
        Rdn rdn = new Rdn( "a", "a", "b", "b" );

        Rdn rdnClone = (Rdn) rdn.clone();

        RdnParser.parse( "c=d", rdn );

        assertEquals( "b", rdnClone.getValue( "a" ) );
    }


    /**
     * Test teh creation of a new Rdn
     * 
     * @throws org.apache.directory.shared.ldap.model.exception.LdapException
     */
    @Test
    public void testRDNCreation() throws LdapException
    {
        Rdn rdn = new Rdn( "A", "  b  " );
        assertEquals( "a=\\  b \\ ", rdn.getNormName() );
        assertEquals( "A=  b  ", rdn.getName() );
    }


    /**
     * Test the clone method for a Rdn.
     * 
     * @throws LdapException
     */
    @Test
    public void testRDNCloningTwoNameComponent() throws LdapException
    {
        Rdn rdn = new Rdn( "a = b + aa = bb" );

        Rdn rdnClone = (Rdn) rdn.clone();

        rdn.clear();
        RdnParser.parse( "c=d", rdn );

        assertEquals( "b", rdnClone.getValue( "a" ) );
        assertEquals( "bb", rdnClone.getValue( "aa" ) );
        assertEquals( "", rdnClone.getValue( "c" ) );
    }


    /**
     * Test the compareTo method for a Rdn.
     * 
     * @throws LdapException
     */
    @Test
    public void testRDNCompareToNull() throws LdapException
    {
        Rdn rdn1 = new Rdn( " a = b + c = d + a = f + g = h " );
        Rdn rdn2 = null;
        assertTrue( rdn1.compareTo( rdn2 ) > 0 );
    }


    /**
     * Compares a composite NC to a single NC.
     * 
     * @throws LdapException
     */
    @Test
    public void testRDNCompareToNCS2NC() throws LdapException
    {
        Rdn rdn1 = new Rdn( " a = b + c = d + a = f + g = h " );
        Rdn rdn2 = new Rdn( " a = b " );
        assertTrue( rdn1.compareTo( rdn2 ) > 0 );
    }


    /**
     * Compares a single NC to a composite NC.
     * 
     * @throws LdapException
     */
    @Test
    public void testRDNCompareToNC2NCS() throws LdapException
    {
        Rdn rdn1 = new Rdn( " a = b " );
        Rdn rdn2 = new Rdn( " a = b + c = d + a = f + g = h " );

        assertTrue( rdn1.compareTo( rdn2 ) < 0 );
    }


    /**
     * Compares a composite NCS to a composite NCS in the same order.
     * 
     * @throws LdapException
     */
    @Test
    public void testRDNCompareToNCS2NCSOrdered() throws LdapException
    {
        Rdn rdn1 = new Rdn( " a = b + c = d + a = f + g = h " );
        Rdn rdn2 = new Rdn( " a = b + c = d + a = f + g = h " );

        assertEquals( 0, rdn1.compareTo( rdn2 ) );
    }


    /**
     * Compares a composite NCS to a composite NCS in a different order.
     * 
     * @throws LdapException
     */
    @Test
    public void testRDNCompareToNCS2NCSUnordered() throws LdapException
    {
        Rdn rdn1 = new Rdn( " a = b + a = f + g = h + c = d " );
        Rdn rdn2 = new Rdn( " a = b + c = d + a = f + g = h " );

        assertEquals( 0, rdn1.compareTo( rdn2 ) );
    }


    /**
     * Compares a composite NCS to a different composite NCS.
     * 
     * @throws LdapException
     */
    @Test
    public void testRDNCompareToNCS2NCSNotEquals() throws LdapException
    {
        Rdn rdn1 = new Rdn( " a = f + g = h + c = d " );
        Rdn rdn2 = new Rdn( " c = d + a = h + g = h " );

        assertTrue( rdn1.compareTo( rdn2 ) < 0 );
        assertTrue( rdn2.compareTo( rdn1 ) > 0 );
        assertEquals( 0, rdn1.compareTo( rdn2 ) + rdn2.compareTo( rdn1 ) );
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
        Rdn rdn1 = new Rdn( " a = b + c = d " );
        Rdn rdn2 = new Rdn( " a = b + c = y " );
        assertTrue( rdn1.compareTo( rdn2 ) < 0 );
        assertTrue( rdn2.compareTo( rdn1 ) > 0 );
        assertEquals( 0, rdn1.compareTo( rdn2 ) + rdn2.compareTo( rdn1 ) );

        // the third ATAV differs
        Rdn rdn3 = new Rdn( " a = b + c = d + e = f " );
        Rdn rdn4 = new Rdn( " a = b + c = d + e = y " );
        assertTrue( rdn3.compareTo( rdn4 ) < 0 );
        assertTrue( rdn4.compareTo( rdn3 ) > 0 );
        assertEquals( 0, rdn3.compareTo( rdn4 ) + rdn4.compareTo( rdn3 ) );

        // the second ATAV differs in value only
        Rdn rdn5 = new Rdn( " a = b + a = c " );
        Rdn rdn6 = new Rdn( " a = b + a = y " );
        assertTrue( rdn5.compareTo( rdn6 ) < 0 );
        assertTrue( rdn6.compareTo( rdn5 ) > 0 );
        assertEquals( 0, rdn5.compareTo( rdn6 ) + rdn6.compareTo( rdn5 ) );
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
        Rdn rdn1 = new Rdn( " a = b + c = d " );
        Rdn rdn2 = new Rdn( " c = d + a = b " );
        assertEquals( 0, rdn1.compareTo( rdn2 ) );

        rdn1 = new Rdn( " a = b + c = e " );
        rdn2 = new Rdn( " c = d + a = b " );
        assertTrue( rdn1.compareTo( rdn2 ) > 0 );
        assertTrue( rdn2.compareTo( rdn1 ) < 0 );
        assertEquals( 0, rdn1.compareTo( rdn2 ) + rdn2.compareTo( rdn1 ) );

        rdn1 = new Rdn( " a = b + c = d " );
        rdn2 = new Rdn( " e = f + g = h " );
        assertTrue( rdn1.compareTo( rdn2 ) < 0 );
        assertTrue( rdn2.compareTo( rdn1 ) > 0 );
        assertEquals( 0, rdn1.compareTo( rdn2 ) + rdn2.compareTo( rdn1 ) );
    }


    /**
     * Test for DIRSHARED-3.
     * Tests that compareTo() is invertable for single-valued RDNs.
     * 
     * @throws LdapException
     */
    @Test
    public void testCompareInvertableNC2NC() throws LdapException
    {
        Rdn rdn1 = new Rdn( " a = b " );
        Rdn rdn2 = new Rdn( " a = c " );
        assertTrue( rdn1.compareTo( rdn2 ) < 0 );
        assertTrue( rdn2.compareTo( rdn1 ) > 0 );
        assertEquals( 0, rdn1.compareTo( rdn2 ) + rdn2.compareTo( rdn1 ) );

    }


    /**
     * Test for DIRSHARED-3.
     * Tests that compareTo() is invertable for multi-valued RDNs with different values.
     * 
     * @throws LdapException
     */
    @Test
    public void testCompareInvertableNCS2NCSDifferentValues() throws LdapException
    {
        Rdn rdn1 = new Rdn( " a = b + a = c " );
        Rdn rdn2 = new Rdn( " a = b + a = y " );
        assertTrue( rdn1.compareTo( rdn2 ) < 0 );
        assertTrue( rdn2.compareTo( rdn1 ) > 0 );
        assertEquals( 0, rdn1.compareTo( rdn2 ) + rdn2.compareTo( rdn1 ) );
    }


    /**
     * Test for DIRSHARED-3.
     * Tests that compareTo() is invertable for multi-valued RDNs with different types.
     * 
     * @throws org.apache.directory.shared.ldap.model.exception.LdapException
     */
    @Test
    public void testCompareInvertableNCS2NCSDifferentTypes() throws LdapException
    {
        Rdn rdn1 = new Rdn( " a = b + c = d  " );
        Rdn rdn2 = new Rdn( " e = f + g = h " );
        assertTrue( rdn1.compareTo( rdn2 ) < 0 );
        assertTrue( rdn2.compareTo( rdn1 ) > 0 );
        assertEquals( 0, rdn1.compareTo( rdn2 ) + rdn2.compareTo( rdn1 ) );
    }


    /**
     * Test for DIRSHARED-3.
     * Tests that compareTo() is invertable for multi-valued RDNs with different order.
     * 
     * @throws LdapException
     */
    @Test
    public void testCompareInvertableNCS2NCSUnordered() throws LdapException
    {
        Rdn rdn1 = new Rdn( " c = d + a = b " );
        Rdn rdn2 = new Rdn( " a = b + e = f " );
        assertTrue( rdn1.compareTo( rdn2 ) < 0 );
        assertTrue( rdn2.compareTo( rdn1 ) > 0 );
        assertEquals( 0, rdn1.compareTo( rdn2 ) + rdn2.compareTo( rdn1 ) );
    }


    /**
     * Compares with a null Rdn.
     * 
     * @throws LdapException
     */
    @Test
    public void testRDNCompareToNullRdn() throws LdapException
    {
        Rdn rdn1 = new Rdn( " a = b " );

        assertEquals( 1, rdn1.compareTo( null ) );
    }


    /**
     * Compares a simple NC to a simple NC.
     * 
     * @throws LdapException
     */
    @Test
    public void testRDNCompareToNC2NC() throws LdapException
    {
        Rdn rdn1 = new Rdn( " a = b " );
        Rdn rdn2 = new Rdn( " a = b " );

        assertEquals( 0, rdn1.compareTo( rdn2 ) );
    }


    /**
     * Compares a simple NC to a simple NC in UperCase.
     * 
     * @throws LdapException
     */
    @Test
    public void testRDNCompareToNC2NCUperCase() throws LdapException
    {
        Rdn rdn1 = new Rdn( " a = b " );
        Rdn rdn2 = new Rdn( " A = b " );

        assertEquals( 0, rdn1.compareTo( rdn2 ) );
        assertEquals( true, rdn1.equals( rdn2 ) );
    }


    /**
     * Compares a simple NC to a different simple NC.
     * 
     * @throws LdapException
     */
    @Test
    public void testRDNCompareToNC2NCNotEquals() throws LdapException
    {
        Rdn rdn1 = new Rdn( " a = b " );
        Rdn rdn2 = new Rdn( " A = d " );

        assertTrue( rdn1.compareTo( rdn2 ) < 0 );
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
        Rdn rdn = new Rdn( " a = b + a = f + g = h + c = d " );

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
        Rdn rdn = new Rdn( " a = b + a = f + g = h + c = d " );

        assertEquals( "a", rdn.getNormType() );
    }


    /**
     * Test the getSize method.
     *
     * @throws LdapException
     */
    @Test
    public void testGetSize() throws LdapException
    {
        Rdn rdn = new Rdn( " a = b + a = f + g = h + c = d " );

        assertEquals( 4, rdn.size() );
    }


    /**
     * Test the getSize method.
     *
     */
    @Test
    public void testGetSize0()
    {
        Rdn rdn = new Rdn();

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
        Rdn rdn = new Rdn( "a=b + c=d + a=f" );

        assertFalse( rdn.equals( null ) );
        assertFalse( rdn.equals( "test" ) );
        assertFalse( rdn.equals( new Rdn( "a=c + c=d + a=f" ) ) );
        assertFalse( rdn.equals( new Rdn( "a=b" ) ) );
        assertTrue( rdn.equals( new Rdn( "a=b + c=d + a=f" ) ) );
        assertTrue( rdn.equals( new Rdn( "a=b + C=d + A=f" ) ) );
        assertTrue( rdn.equals( new Rdn( "c=d + a=f + a=b" ) ) );
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
        Rdn rdn = new Rdn( "cn=Kate Bush+sn=Bush" );
        assertEquals( "cn=Kate Bush+sn=Bush", rdn.getName() );
    }


    @Test
    public void testMultiValuedIterator() throws LdapException
    {
        Rdn rdn = new Rdn( "cn=Kate Bush+sn=Bush" );
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
        Rdn rdn = new Rdn( "cn=Kate Bush" );
        Iterator<Ava> iterator = rdn.iterator();
        assertNotNull( iterator );
        assertTrue( iterator.hasNext() );
        assertNotNull( iterator.next() );
        assertFalse( iterator.hasNext() );
    }


    @Test
    public void testEmptyIterator()
    {
        Rdn rdn = new Rdn();
        Iterator<Ava> iterator = rdn.iterator();
        assertNotNull( iterator );
        assertFalse( iterator.hasNext() );
    }


    @Test
    public void testRdnWithSpaces() throws LdapException
    {
        Rdn rdn = new Rdn( "cn=a\\ b\\ c" );
        assertEquals( "cn=a b c", rdn.getNormName() );
    }


    @Test
    public void testEscapedSpaceInValue() throws LdapException
    {
        Rdn rdn1 = new Rdn( "cn=a b c" );
        Rdn rdn2 = new Rdn( "cn=a\\ b\\ c" );
        assertEquals( "cn=a b c", rdn1.getNormName() );
        assertEquals( "cn=a b c", rdn2.getNormName() );
        assertTrue( rdn1.equals( rdn2 ) );

        Rdn rdn3 = new Rdn( "cn=\\ a b c\\ " );
        Rdn rdn4 = new Rdn( "cn=\\ a\\ b\\ c\\ " );
        assertEquals( "cn=\\ a b c\\ ", rdn3.getNormName() );
        assertEquals( "cn=\\ a b c\\ ", rdn4.getNormName() );
        assertTrue( rdn3.equals( rdn4 ) );
    }


    @Test
    public void testEscapedHashInValue() throws LdapException
    {
        Rdn rdn1 = new Rdn( "cn=a#b#c" );
        Rdn rdn2 = new Rdn( "cn=a\\#b\\#c" );
        assertEquals( "cn=a#b#c", rdn1.getNormName() );
        assertEquals( "cn=a#b#c", rdn2.getNormName() );
        assertTrue( rdn1.equals( rdn2 ) );

        Rdn rdn3 = new Rdn( "cn=\\#a#b#c\\#" );
        Rdn rdn4 = new Rdn( "cn=\\#a\\#b\\#c\\#" );
        assertEquals( "cn=\\#a#b#c#", rdn3.getNormName() );
        assertEquals( "cn=\\#a#b#c#", rdn4.getNormName() );
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
        Rdn rdn = new Rdn( "" );

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
        Rdn rdn = new Rdn();

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
        Rdn rdn = new Rdn( "a=b" );
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
    public void testSimpleRdn2Serialization() throws LdapException, IOException, ClassNotFoundException
    {
        Rdn rdn = new Rdn( " ABC  = DEF " );
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
     * Test serialization of a simple Rdn with no value
     */
    @Test
    public void testSimpleRdnNoValueSerialization() throws LdapException, IOException, ClassNotFoundException
    {
        Rdn rdn = new Rdn( " ABC  =" );
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
     * Test serialization of a simple Rdn with one value
     */
    @Test
    public void testSimpleRdnOneValueSerialization() throws LdapException, IOException, ClassNotFoundException
    {
        Rdn rdn = new Rdn( " ABC  = def " );
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
     * Test serialization of a simple Rdn with three values
     */
    @Test
    public void testSimpleRdnThreeValuesSerialization() throws LdapException, IOException, ClassNotFoundException
    {
        Rdn rdn = new Rdn( " A = a + B = b + C = c " );
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
     * Test serialization of a simple Rdn with three unordered values
     */
    @Test
    public void testSimpleRdnThreeValuesUnorderedSerialization() throws LdapException, IOException,
        ClassNotFoundException
    {
        Rdn rdn = new Rdn( " B = b + A = a + C = c " );
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


    /** Static Serialization tests ------------------------------------------------- */

    /**
     * Test serialization of an empty Rdn
     */
    @Test
    public void testEmptyRDNStaticSerialization() throws LdapException, IOException, ClassNotFoundException
    {
        Rdn rdn = new Rdn( "" );

        rdn.normalize();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        RdnSerializer.serialize( rdn, out );
        out.flush();

        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        Rdn rdn2 = RdnSerializer.deserialize( in );

        assertEquals( rdn, rdn2 );
    }


    @Test
    public void testNullRdnStaticSerialization() throws IOException, ClassNotFoundException
    {
        Rdn rdn = new Rdn();

        rdn.normalize();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        RdnSerializer.serialize( rdn, out );
        out.flush();

        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        Rdn rdn2 = RdnSerializer.deserialize( in );

        assertEquals( rdn, rdn2 );
    }


    /**
     * Test serialization of a simple Rdn
     */
    @Test
    public void testSimpleRdnStaticSerialization() throws LdapException, IOException, ClassNotFoundException
    {
        Rdn rdn = new Rdn( "a=b" );
        rdn.normalize();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        RdnSerializer.serialize( rdn, out );
        out.flush();

        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        Rdn rdn2 = RdnSerializer.deserialize( in );

        assertEquals( rdn, rdn2 );
    }


    /**
     * Test serialization of a simple Rdn
     */
    @Test
    public void testSimpleRdn2StaticSerialization() throws LdapException, IOException, ClassNotFoundException
    {
        Rdn rdn = new Rdn( " ABC  = DEF " );
        rdn.normalize();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        RdnSerializer.serialize( rdn, out );
        out.flush();

        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        Rdn rdn2 = RdnSerializer.deserialize( in );

        assertEquals( rdn, rdn2 );
    }


    /**
     * Test serialization of a simple Rdn with no value
     */
    @Test
    public void testSimpleRdnNoValueStaticSerialization() throws LdapException, IOException, ClassNotFoundException
    {
        Rdn rdn = new Rdn( " ABC  =" );
        rdn.normalize();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        RdnSerializer.serialize( rdn, out );
        out.flush();

        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        Rdn rdn2 = RdnSerializer.deserialize( in );

        assertEquals( rdn, rdn2 );
    }


    /**
     * Test serialization of a simple Rdn with one value
     */
    @Test
    public void testSimpleRdnOneValueStaticSerialization() throws LdapException, IOException, ClassNotFoundException
    {
        Rdn rdn = new Rdn( " ABC  = def " );
        rdn.normalize();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        RdnSerializer.serialize(rdn, out);
        out.flush();

        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        Rdn rdn2 = RdnSerializer.deserialize( in );

        assertEquals( rdn, rdn2 );
    }


    /**
     * Test serialization of a simple Rdn with three values
     */
    @Test
    public void testSimpleRdnThreeValuesStaticSerialization() throws LdapException, IOException,
        ClassNotFoundException
    {
        Rdn rdn = new Rdn( " A = a + B = b + C = c " );
        rdn.normalize();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        RdnSerializer.serialize( rdn, out );
        out.flush();

        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        Rdn rdn2 = RdnSerializer.deserialize( in );

        assertEquals( rdn, rdn2 );
    }


    /**
     * Test serialization of a simple Rdn with three unordered values
     */
    @Test
    public void testSimpleRdnThreeValuesUnorderedStaticSerialization() throws LdapException, IOException,
        ClassNotFoundException
    {
        Rdn rdn = new Rdn( " B = b + A = a + C = c " );
        rdn.normalize();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        RdnSerializer.serialize( rdn, out );
        out.flush();

        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        Rdn rdn2 = RdnSerializer.deserialize( in );

        assertEquals( rdn, rdn2 );
    }


    /**
     * test an Rdn with empty value
     */
    @Test
    public void testRdnWithEmptyValue() throws LdapException
    {
        assertTrue( RdnParser.isValid("a=") );
        assertTrue( RdnParser.isValid( "a=\"\"" ) );
        assertEquals( "a=", new Rdn( "a=\"\"" ).getNormName() );
        assertEquals( "a=", new Rdn( "a=" ).getNormName() );
    }


    /**
     * test an Rdn with escaped comma
     */
    @Test
    public void testRdnWithEscapedComa() throws LdapException
    {
        assertTrue( RdnParser.isValid( "a=b\\,c" ) );
        assertEquals( "a=b\\,c", new Rdn( "a=b\\,c" ).getNormName() );

        assertTrue( RdnParser.isValid( "a=\"b,c\"" ) );
        assertEquals( "a=b\\,c", new Rdn( "a=\"b,c\"" ).getNormName() );
        assertEquals( "a=\"b,c\"", new Rdn( "a=\"b,c\"" ).getName() );

        assertTrue( RdnParser.isValid( "a=\"b\\,c\"" ) );
        Rdn rdn = new Rdn( "a=\"b\\,c\"" );
        assertEquals( "a=\"b\\,c\"", rdn.getName() );
        assertEquals( "a=b\\,c", rdn.getNormName() );
    }


    /**
     * Tests the equals and compareTo results of cloned multi-valued RDNs.
     * Test for DIRSHARED-9.
     * 
     * @throws LdapException
     */
    @Test
    public void testComparingOfClonedMultiValuedRDNs() throws LdapException
    {
        // Use upper case attribute types to test if normalized types are used 
        // for comparison
        Rdn rdn = new Rdn( " A = b + C = d" );
        Rdn clonedRdn = (Rdn) rdn.clone();

        assertEquals( 0, rdn.compareTo( clonedRdn ) );
        assertEquals( true, rdn.equals( clonedRdn ) );
    }


    /**
     * Tests the equals and compareTo results of copy constructed multi-valued RDNs.
     * Test for DIRSHARED-9.
     * 
     * @throws LdapException
     */
    @Test
    public void testComparingOfCopyConstructedMultiValuedRDNs() throws LdapException
    {
        // Use upper case attribute types to test if normalized types are used 
        // for comparison
        Rdn rdn = new Rdn( " A = b + C = d" );
        Rdn copiedRdn = new Rdn( rdn );

        assertEquals( 0, rdn.compareTo( copiedRdn ) );
        assertEquals( true, rdn.equals( copiedRdn ) );
    }


    /**
     * test the UpName method on a Rdn with more than one atav
     */
    @Test 
    public void testGetUpNameMultipleAtav() throws LdapException
    {
        Rdn rdn = new Rdn( " A = b + C = d " );
        
        assertEquals( " A = b + C = d ", rdn.getName() );
    }
}
