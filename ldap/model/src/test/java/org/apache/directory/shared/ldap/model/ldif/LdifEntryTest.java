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
package org.apache.directory.shared.ldap.model.ldif;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.directory.shared.ldap.model.entry.Entry;
import org.apache.directory.shared.ldap.model.entry.EntryAttribute;
import org.apache.directory.shared.ldap.model.entry.Modification;
import org.apache.directory.shared.ldap.model.entry.ModificationOperation;
import org.apache.directory.shared.ldap.model.exception.LdapInvalidAttributeValueException;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.shared.util.Strings;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mycila.junit.concurrent.Concurrency;
import com.mycila.junit.concurrent.ConcurrentJunitRunner;


/**
 * Test the LdifEntry class
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrency()
public class LdifEntryTest
{
    /**
     * Check that we can't create an empty LdifEntry
     */
    @Test( expected=LdapInvalidAttributeValueException.class)
    public void testLdifEntryEmpty() throws Exception
    {
        new LdifEntry( "", "" );
    }


    /**
     * Check that we can create an LdifEntry with an Empty Dn
     */
    @Test
    public void testLdifEntryEmptyDn() throws Exception
    {
        Entry entry = LdifUtils.createEntry( "", "cn: test" );
        LdifEntry ldifEntry = new LdifEntry( "", "cn: test" );
        
        assertNotNull( ldifEntry );
        assertEquals( Dn.EMPTY_DN, ldifEntry.getDn() );
        assertEquals( ChangeType.None, ldifEntry.getChangeType() );
        assertEquals( entry, ldifEntry.getEntry() );
    }


    /**
     * Check that we can create an LdifEntry with a null Dn
     */
    @Test
    public void testLdifEntryNullDn() throws Exception
    {
        Entry entry = LdifUtils.createEntry( "", "cn: test" );
        LdifEntry ldifEntry = new LdifEntry( (Dn)null, "cn: test" );
        
        assertNotNull( ldifEntry );
        assertEquals( Dn.EMPTY_DN, ldifEntry.getDn() );
        assertEquals( ChangeType.None, ldifEntry.getChangeType() );
        assertEquals( entry, ldifEntry.getEntry() );
    }


    /**
     * Test a simple LdifEntry
     * @throws Exception
     */
    @Test
    public void testSimpleLdifEntry() throws Exception
    {
        String ldif = 
            "cn: app1\n" + 
            "objectClass: top\n" + 
            "objectClass: apApplication\n" + 
            "displayName:   app1   \n" +
            "dependencies:\n" + 
            "envVars:";

        LdifEntry ldifEntry = new LdifEntry( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldif );

        assertNotNull( ldifEntry );
        assertTrue( ldifEntry.isLdifContent() );
        assertEquals( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldifEntry.getDn().getName() );

        EntryAttribute attr = ldifEntry.get( "displayname" );
        assertTrue( attr.contains( "app1" ) );
    }
    
    
    /**
     * Test a Delete changeType LdifEntry with no control
     * 
     * @throws Exception
     */
    @Test
    public void testLdifParserChangeTypeDeleteNoControl() throws Exception
    {
        String ldif = 
            "# Delete an entry. The operation will attach the LDAPv3\n" +
            "# Tree Delete Control defined in [9]. The criticality\n" +
            "# field is \"true\" and the controlValue field is\n" + 
            "# absent, as required by [9].\n" +
            "changetype: delete\n";

        LdifEntry ldifEntry = new LdifEntry( "ou=Product Development, dc=airius, dc=com", ldif );
        
        assertNotNull( ldifEntry );
        assertEquals( ChangeType.Delete, ldifEntry.getChangeType() );
        assertNull( ldifEntry.getEntry() );
        assertEquals( "ou=Product Development, dc=airius, dc=com", ldifEntry.getDn().getName() );
        assertFalse( ldifEntry.hasControls() );
    }
    
    
    /**
     * Test a Delete changeType LdifEntry with no control and following Attrs :
     * should get an exception
     * 
     * @throws Exception
     */
    @Test( expected = LdapLdifException.class )
    public void testLdifParserChangeTypeDeleteNoControlAttribute() throws Exception
    {
        String ldif = 
            "# Delete an entry. The operation will attach the LDAPv3\n" +
            "# Tree Delete Control defined in [9]. The criticality\n" +
            "# field is \"true\" and the controlValue field is\n" + 
            "# absent, as required by [9].\n" +
            "changetype: delete\n"+
            "cn: bad !!\n";

        new LdifEntry( "ou=Product Development, dc=airius, dc=com", ldif );
    }


    /**
     * Test a Delete changeType LdifEntry with one control
     * 
     * @throws Exception
     */
    @Test
    public void testLdifParserChangeTypeDeleteWithControl() throws Exception
    {
        String ldif = 
            "# Delete an entry. The operation will attach the LDAPv3\n" +
            "# Tree Delete Control defined in [9]. The criticality\n" +
            "# field is \"true\" and the controlValue field is\n" + 
            "# absent, as required by [9].\n" +
            "control: 1.2.840.113556.1.4.805 true\n" +
            "changetype: delete\n";

        LdifEntry ldifEntry = new LdifEntry( "ou=Product Development, dc=airius, dc=com", ldif );
        
        assertNotNull( ldifEntry );
        assertEquals( ChangeType.Delete, ldifEntry.getChangeType() );
        assertNull( ldifEntry.getEntry() );
        assertEquals( "ou=Product Development, dc=airius, dc=com", ldifEntry.getDn().getName() );
        assertTrue( ldifEntry.hasControls() );
        
        LdifControl ldifControl = ldifEntry.getControl( "1.2.840.113556.1.4.805" );
        assertNotNull( ldifControl );
        assertEquals( "1.2.840.113556.1.4.805", ldifControl.getOid() );
        assertTrue( ldifControl.isCritical() );
        assertNull( ldifControl.getValue() );
    }
    

    /**
     * Test a Delete changeType LdifEntry with controls
     * 
     * @throws Exception
     */
    @Test
    public void testLdifParserChangeTypeDeleteWithControls() throws Exception
    {
        String ldif = 
            "# Delete an entry. The operation will attach the LDAPv3\n" +
            "# Tree Delete Control defined in [9]. The criticality\n" +
            "# field is \"true\" and the controlValue field is\n" + 
            "# absent, as required by [9].\n" +
            "control: 1.2.840.113556.1.4.805 true\n" +
            "control: 1.2.840.113556.1.4.806 false: test\n" +
            "changetype: delete\n";

        LdifEntry ldifEntry = new LdifEntry( "ou=Product Development, dc=airius, dc=com", ldif );
        
        assertNotNull( ldifEntry );
        assertEquals( ChangeType.Delete, ldifEntry.getChangeType() );
        assertNull( ldifEntry.getEntry() );
        assertEquals( "ou=Product Development, dc=airius, dc=com", ldifEntry.getDn().getName() );
        assertTrue( ldifEntry.hasControls() );
        
        LdifControl ldifControl = ldifEntry.getControl( "1.2.840.113556.1.4.805" );
        assertNotNull( ldifControl );
        assertEquals( "1.2.840.113556.1.4.805", ldifControl.getOid() );
        assertTrue( ldifControl.isCritical() );
        assertNull( ldifControl.getValue() );
        
        ldifControl = ldifEntry.getControl( "1.2.840.113556.1.4.806" );
        assertNotNull( ldifControl );
        assertEquals( "1.2.840.113556.1.4.806", ldifControl.getOid() );
        assertFalse( ldifControl.isCritical() );
        assertNotNull( ldifControl.getValue() );
        assertEquals( "test", Strings.utf8ToString( ldifControl.getValue() ) );
    }


    /**
     * Test a Add changeType LdifEntry with no control
     * @throws Exception
     */
    @Test
    public void testLdifEntryChangeTypeAddNoControl() throws Exception
    {
        String ldif = 
            "changetype: add\n" +
            "cn: app1\n" + 
            "objectClass: top\n" + 
            "objectClass: apApplication\n" + 
            "displayName:   app1   \n" +
            "dependencies:\n" + 
            "envVars:";

        LdifEntry ldifEntry = new LdifEntry( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldif );

        assertNotNull( ldifEntry );
        assertEquals( ChangeType.Add, ldifEntry.getChangeType() );
        assertNotNull( ldifEntry.getEntry() );
        assertEquals( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldifEntry.getDn().getName() );
        assertFalse( ldifEntry.hasControls() );
        assertTrue( ldifEntry.isLdifChange() );

        EntryAttribute attr = ldifEntry.get( "displayname" );
        assertTrue( attr.contains( "app1" ) );
    }
    
    
    /**
     * Test a Add changeType LdifEntry with a control
     * @throws Exception
     */
    @Test
    public void testLdifEntryChangeTypeAddWithControl() throws Exception
    {
        String ldif = 
            "control: 1.2.840.113556.1.4.805 true\n" +
            "changetype: add\n" +
            "cn: app1\n" + 
            "objectClass: top\n" + 
            "objectClass: apApplication\n" + 
            "displayName:   app1   \n" +
            "dependencies:\n" + 
            "envVars:";

        LdifEntry ldifEntry = new LdifEntry( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldif );

        assertNotNull( ldifEntry );
        assertEquals( ChangeType.Add, ldifEntry.getChangeType() );
        assertNotNull( ldifEntry.getEntry() );
        assertEquals( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldifEntry.getDn().getName() );
        assertTrue( ldifEntry.isLdifChange() );

        EntryAttribute attr = ldifEntry.get( "displayname" );
        assertTrue( attr.contains( "app1" ) );
        assertTrue( ldifEntry.hasControls() );
        
        LdifControl ldifControl = ldifEntry.getControl( "1.2.840.113556.1.4.805" );
        assertNotNull( ldifControl );
        assertEquals( "1.2.840.113556.1.4.805", ldifControl.getOid() );
        assertTrue( ldifControl.isCritical() );
        assertNull( ldifControl.getValue() );
    }
    
    
    /**
     * Test a Add changeType LdifEntry with controls
     * @throws Exception
     */
    @Test
    public void testLdifEntryChangeTypeAddWithControls() throws Exception
    {
        String ldif = 
            "control: 1.2.840.113556.1.4.805 true\n" +
            "control: 1.2.840.113556.1.4.806 false: test\n" +
            "changetype: add\n" +
            "cn: app1\n" + 
            "objectClass: top\n" + 
            "objectClass: apApplication\n" + 
            "displayName:   app1   \n" +
            "dependencies:\n" + 
            "envVars:";

        LdifEntry ldifEntry = new LdifEntry( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldif );

        assertNotNull( ldifEntry );
        assertEquals( ChangeType.Add, ldifEntry.getChangeType() );
        assertNotNull( ldifEntry.getEntry() );
        assertEquals( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldifEntry.getDn().getName() );
        assertTrue( ldifEntry.isLdifChange() );

        EntryAttribute attr = ldifEntry.get( "displayname" );
        assertTrue( attr.contains( "app1" ) );
        assertTrue( ldifEntry.hasControls() );
        
        LdifControl ldifControl = ldifEntry.getControl( "1.2.840.113556.1.4.805" );
        assertNotNull( ldifControl );
        assertEquals( "1.2.840.113556.1.4.805", ldifControl.getOid() );
        assertTrue( ldifControl.isCritical() );
        assertNull( ldifControl.getValue() );
        
        ldifControl = ldifEntry.getControl( "1.2.840.113556.1.4.806" );
        assertNotNull( ldifControl );
        assertEquals( "1.2.840.113556.1.4.806", ldifControl.getOid() );
        assertFalse( ldifControl.isCritical() );
        assertNotNull( ldifControl.getValue() );
        assertEquals( "test", Strings.utf8ToString( ldifControl.getValue() ) );
    }


    /**
     * Test a ModDn changeType LdifEntry with no control
     */
    @Test
    public void testLdifEntryChangeTypeModDnNoControl() throws Exception
    {
        String ldif = 
            "changetype: moddn\n" +
            "newrdn: cn=app2\n" + 
            "deleteoldrdn: 1\n"; 

        LdifEntry ldifEntry = new LdifEntry( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldif );

        assertNotNull( ldifEntry );
        assertEquals( ChangeType.ModDn, ldifEntry.getChangeType() );
        assertNull( ldifEntry.getEntry() );
        assertEquals( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldifEntry.getDn().getName() );
        assertFalse( ldifEntry.hasControls() );
        assertTrue( ldifEntry.isLdifChange() );
        assertEquals( "cn=app2", ldifEntry.getNewRdn() );
        assertTrue( ldifEntry.isDeleteOldRdn() );
        assertNull( ldifEntry.getNewSuperior() );
    }
    
    
    /**
     * Test a ModDn changeType LdifEntry with no newRdn
     */
    @Test( expected = LdapLdifException.class )
    public void testLdifEntryChangeTypeModDnNoNewRdn() throws Exception
    {
        String ldif = 
            "changetype: moddn\n" +
            "deleteoldrdn: 1\n"; 

        new LdifEntry( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldif );
    }


    /**
     * Test a ModDn changeType LdifEntry with no deleteOldRdn flag
     */
    @Test( expected = LdapLdifException.class )
    public void testLdifEntryChangeTypeModDnNoDeleteOldRdn() throws Exception
    {
        String ldif = 
            "changetype: moddn\n" +
            "newrdn: cn=app2\n"; 

        new LdifEntry( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldif );
    }


    /**
     * Test a ModDn changeType LdifEntry with no control and a newSuperior
     */
    @Test
    public void testLdifEntryChangeTypeModDnRenameNoControlNewSuperior() throws Exception
    {
        String ldif = 
            "changetype: moddn\n" +
            "newrdn: cn=app2\n" + 
            "deleteoldrdn: 1\n" +
            "newsuperior: dc=example, dc=com"; 

        LdifEntry ldifEntry = new LdifEntry( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldif );

        assertNotNull( ldifEntry );
        assertEquals( ChangeType.ModDn, ldifEntry.getChangeType() );
        assertNull( ldifEntry.getEntry() );
        assertEquals( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldifEntry.getDn().getName() );
        assertFalse( ldifEntry.hasControls() );
        assertTrue( ldifEntry.isLdifChange() );
        assertEquals( "cn=app2", ldifEntry.getNewRdn() );
        assertTrue( ldifEntry.isDeleteOldRdn() );
        assertEquals( "dc=example, dc=com", ldifEntry.getNewSuperior() );
    }

    
    /**
     * Test a ModDn changeType LdifEntry with a control
     * @throws Exception
     */
    @Test
    public void testLdifEntryChangeTypeModdnWithControl() throws Exception
    {
        String ldif = 
            "control: 1.2.840.113556.1.4.805 true\n" +
            "changetype: moddn\n" +
            "newrdn: cn=app2\n" + 
            "deleteoldrdn: 1\n"; 

        LdifEntry ldifEntry = new LdifEntry( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldif );

        assertNotNull( ldifEntry );
        assertEquals( ChangeType.ModDn, ldifEntry.getChangeType() );
        assertNull( ldifEntry.getEntry() );
        assertEquals( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldifEntry.getDn().getName() );
        assertTrue( ldifEntry.isLdifChange() );
        assertEquals( "cn=app2", ldifEntry.getNewRdn() );
        assertNull( ldifEntry.getNewSuperior() );
        assertTrue( ldifEntry.isDeleteOldRdn() );

        assertTrue( ldifEntry.hasControls() );
        
        LdifControl ldifControl = ldifEntry.getControl( "1.2.840.113556.1.4.805" );
        assertNotNull( ldifControl );
        assertEquals( "1.2.840.113556.1.4.805", ldifControl.getOid() );
        assertTrue( ldifControl.isCritical() );
        assertNull( ldifControl.getValue() );
    }
    
    
    /**
     * Test a ModDN changeType LdifEntry with controls
     * @throws Exception
     */
    @Test
    public void testLdifEntryChangeTypeModddnWithControls() throws Exception
    {
        String ldif = 
            "control: 1.2.840.113556.1.4.805 true\n" +
            "control: 1.2.840.113556.1.4.806 false: test\n" +
            "changetype: moddn\n" +
            "newrdn: cn=app2\n" + 
            "deleteoldrdn: 1\n"; 

        LdifEntry ldifEntry = new LdifEntry( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldif );

        assertNotNull( ldifEntry );
        assertEquals( ChangeType.ModDn, ldifEntry.getChangeType() );
        assertNull( ldifEntry.getEntry() );
        assertEquals( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldifEntry.getDn().getName() );
        assertTrue( ldifEntry.isLdifChange() );
        assertEquals( "cn=app2", ldifEntry.getNewRdn() );
        assertTrue( ldifEntry.isDeleteOldRdn() );
        assertNull( ldifEntry.getNewSuperior() );
        assertTrue( ldifEntry.hasControls() );
        
        LdifControl ldifControl = ldifEntry.getControl( "1.2.840.113556.1.4.805" );
        assertNotNull( ldifControl );
        assertEquals( "1.2.840.113556.1.4.805", ldifControl.getOid() );
        assertTrue( ldifControl.isCritical() );
        assertNull( ldifControl.getValue() );
        
        ldifControl = ldifEntry.getControl( "1.2.840.113556.1.4.806" );
        assertNotNull( ldifControl );
        assertEquals( "1.2.840.113556.1.4.806", ldifControl.getOid() );
        assertFalse( ldifControl.isCritical() );
        assertNotNull( ldifControl.getValue() );
        assertEquals( "test", Strings.utf8ToString( ldifControl.getValue() ) );
    }
    
    
    /**
     * Test a Modify changeType LdifEntry with no control
     */
    @Test
    public void testLdifEntryChangeTypeModifySimple() throws Exception
    {
        String ldif = 
            "changetype: modify\n" +
            "add: cn\n" +
            "cn: v1\n" + 
            "cn: v2\n" +
            "-"; 

        LdifEntry ldifEntry = new LdifEntry( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldif );

        assertNotNull( ldifEntry );
        assertEquals( ChangeType.Modify, ldifEntry.getChangeType() );
        assertNull( ldifEntry.getEntry() );
        assertEquals( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldifEntry.getDn().getName() );
        assertFalse( ldifEntry.hasControls() );
        assertTrue( ldifEntry.isLdifChange() );
        
        // Check the modification
        assertNotNull( ldifEntry.getModificationItems() );
        
        for ( Modification modification : ldifEntry.getModificationItems() )
        {
            assertEquals( ModificationOperation.ADD_ATTRIBUTE, modification.getOperation() );
            EntryAttribute attribute = modification.getAttribute();

            assertNotNull( attribute );
            assertEquals( "cn", attribute.getId() );
            assertTrue( attribute.contains( "v1", "v2" ) );
            
        }
    }
    
    
    /**
     * Test a Modify changeType LdifEntry with no end separator ("-")
     */
    @Test( expected = LdapLdifException.class )
    public void testLdifEntryChangeTypeModifyNoEndSeparator() throws Exception
    {
        String ldif = 
            "changetype: modify\n" +
            "add: cn\n" +
            "cn: v1\n" + 
            "cn: v2\n";

        new LdifEntry( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldif );
    }


    /**
     * Test a Modify changeType LdifEntry with no operation
     */
    @Test( expected = LdapLdifException.class )
    public void testLdifEntryChangeTypeModifyNoOperator() throws Exception
    {
        String ldif = 
            "changetype: modify\n" +
            "cn: v1\n" + 
            "dn: v2\n" +
            "-";

        new LdifEntry( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldif );
    }


    /**
     * Test a Modify changeType LdifEntry with no attributes
     */
    @Test
    public void testLdifEntryChangeTypeModifyNoAttribute() throws Exception
    {
        String ldif = 
            "changetype: modify\n" +
            "add: cn\n" +
            "-"; 

        LdifEntry ldifEntry = new LdifEntry( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldif );

        assertNotNull( ldifEntry );
        assertEquals( ChangeType.Modify, ldifEntry.getChangeType() );
        assertNull( ldifEntry.getEntry() );
        assertEquals( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldifEntry.getDn().getName() );
        assertFalse( ldifEntry.hasControls() );
        assertTrue( ldifEntry.isLdifChange() );
        
        // Check the modification
        assertNotNull( ldifEntry.getModificationItems() );
        
        for ( Modification modification : ldifEntry.getModificationItems() )
        {
            assertEquals( ModificationOperation.ADD_ATTRIBUTE, modification.getOperation() );
            EntryAttribute attribute = modification.getAttribute();

            assertNotNull( attribute );
            assertEquals( "cn", attribute.getId() );
            assertNotNull( attribute.get() );
            assertTrue( attribute.get().isNull() );
        }
    }
    
    
    /**
     * Test a Modify changeType LdifEntry with a different attribute used
     */
    @Test( expected = LdapLdifException.class )
    public void testLdifEntryChangeTypeModifyNotSameAttr() throws Exception
    {
        String ldif = 
            "changetype: modify\n" +
            "add: cn\n" +
            "sn: v1\n" + 
            "sn: v2\n" +
            "-"; 

        new LdifEntry( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldif );
    }
    
    
    /**
     * Test a Modify changeType LdifEntry with a different attribute used
     */
    @Test( expected = LdapLdifException.class )
    public void testLdifEntryChangeTypeModifyNotSameAttr2() throws Exception
    {
        String ldif = 
            "changetype: modify\n" +
            "add: cn\n" +
            "cn: v1\n" + 
            "sn: v2\n" +
            "-"; 

        new LdifEntry( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldif );
    }
    
    
    /**
     * Test a Modify changeType LdifEntry with no attributes and controls
     */
    @Test
    public void testLdifEntryChangeTypeModifyNoAttributeWithControls() throws Exception
    {
        String ldif = 
            "control: 1.2.840.113556.1.4.805 true\n" +
            "control: 1.2.840.113556.1.4.806 false: test\n" +
            "changetype: modify\n" +
            "add: cn\n" +
            "-"; 

        LdifEntry ldifEntry = new LdifEntry( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldif );

        assertNotNull( ldifEntry );
        assertEquals( ChangeType.Modify, ldifEntry.getChangeType() );
        assertNull( ldifEntry.getEntry() );
        assertEquals( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldifEntry.getDn().getName() );
        assertTrue( ldifEntry.isLdifChange() );
        
        // Check the modification
        assertNotNull( ldifEntry.getModificationItems() );
        
        for ( Modification modification : ldifEntry.getModificationItems() )
        {
            assertEquals( ModificationOperation.ADD_ATTRIBUTE, modification.getOperation() );
            EntryAttribute attribute = modification.getAttribute();

            assertNotNull( attribute );
            assertEquals( "cn", attribute.getId() );
            assertEquals( 1, attribute.size() );
            assertTrue(  attribute.get().isNull() );
        }
        
        assertTrue( ldifEntry.hasControls() );
        
        LdifControl ldifControl = ldifEntry.getControl( "1.2.840.113556.1.4.805" );
        assertNotNull( ldifControl );
        assertEquals( "1.2.840.113556.1.4.805", ldifControl.getOid() );
        assertTrue( ldifControl.isCritical() );
        assertNull( ldifControl.getValue() );
        
        ldifControl = ldifEntry.getControl( "1.2.840.113556.1.4.806" );
        assertNotNull( ldifControl );
        assertEquals( "1.2.840.113556.1.4.806", ldifControl.getOid() );
        assertFalse( ldifControl.isCritical() );
        assertNotNull( ldifControl.getValue() );
        assertEquals( "test", Strings.utf8ToString( ldifControl.getValue() ) );
    }
}
