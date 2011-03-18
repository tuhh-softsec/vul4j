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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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
public class LdifEntrySerializationTest
{
    @Test
    public void testLdifEntrySimple() throws Exception
    {
        String ldif = 
            "cn: app1\n" + 
            "objectClass: top\n" + 
            "objectClass: apApplication\n" + 
            "displayName:   app1   \n" +
            "dependencies:\n" + 
            "envVars:";

        LdifEntry ldifEntry1 = new LdifEntry( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldif );
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        ldifEntry1.writeExternal( out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        LdifEntry ldifEntry2 = new LdifEntry();
        ldifEntry2.readExternal( in );

        assertEquals( ldifEntry1, ldifEntry2 );
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

        LdifEntry ldifEntry1 = new LdifEntry( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldif );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        ldifEntry1.writeExternal( out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        LdifEntry ldifEntry2 = new LdifEntry();
        ldifEntry2.readExternal( in );

        assertEquals( ldifEntry1, ldifEntry2 );
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

        LdifEntry ldifEntry1 = new LdifEntry( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldif );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        ldifEntry1.writeExternal( out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        LdifEntry ldifEntry2 = new LdifEntry();
        ldifEntry2.readExternal( in );

        assertEquals( ldifEntry1, ldifEntry2 );
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

        LdifEntry ldifEntry1 = new LdifEntry( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldif );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        ldifEntry1.writeExternal( out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        LdifEntry ldifEntry2 = new LdifEntry();
        ldifEntry2.readExternal( in );

        assertEquals( ldifEntry1, ldifEntry2 );
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

        LdifEntry ldifEntry1 = new LdifEntry( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldif );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        ldifEntry1.writeExternal( out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        LdifEntry ldifEntry2 = new LdifEntry();
        ldifEntry2.readExternal( in );

        assertEquals( ldifEntry1, ldifEntry2 );
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

        LdifEntry ldifEntry1 = new LdifEntry( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldif );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        ldifEntry1.writeExternal( out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        LdifEntry ldifEntry2 = new LdifEntry();
        ldifEntry2.readExternal( in );

        assertEquals( ldifEntry1, ldifEntry2 );
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

        LdifEntry ldifEntry1 = new LdifEntry( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldif );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        ldifEntry1.writeExternal( out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        LdifEntry ldifEntry2 = new LdifEntry();
        ldifEntry2.readExternal( in );

        assertEquals( ldifEntry1, ldifEntry2 );
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

        LdifEntry ldifEntry1 = new LdifEntry( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldif );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        ldifEntry1.writeExternal( out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        LdifEntry ldifEntry2 = new LdifEntry();
        ldifEntry2.readExternal( in );

        assertEquals( ldifEntry1, ldifEntry2 );
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

        LdifEntry ldifEntry1 = new LdifEntry( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldif );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        ldifEntry1.writeExternal( out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        LdifEntry ldifEntry2 = new LdifEntry();
        ldifEntry2.readExternal( in );

        assertEquals( ldifEntry1, ldifEntry2 );
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

        LdifEntry ldifEntry1 = new LdifEntry( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldif );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        ldifEntry1.writeExternal( out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        LdifEntry ldifEntry2 = new LdifEntry();
        ldifEntry2.readExternal( in );

        assertEquals( ldifEntry1, ldifEntry2 );
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

        LdifEntry ldifEntry1 = new LdifEntry( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldif );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        ldifEntry1.writeExternal( out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        LdifEntry ldifEntry2 = new LdifEntry();
        ldifEntry2.readExternal( in );

        assertEquals( ldifEntry1, ldifEntry2 );
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

        LdifEntry ldifEntry1 = new LdifEntry( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldif );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        ldifEntry1.writeExternal( out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        LdifEntry ldifEntry2 = new LdifEntry();
        ldifEntry2.readExternal( in );

        assertEquals( ldifEntry1, ldifEntry2 );
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

        LdifEntry ldifEntry1 = new LdifEntry( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldif );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        ldifEntry1.writeExternal( out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        LdifEntry ldifEntry2 = new LdifEntry();
        ldifEntry2.readExternal( in );

        assertEquals( ldifEntry1, ldifEntry2 );
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

        LdifEntry ldifEntry1 = new LdifEntry( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldif );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        ldifEntry1.writeExternal( out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        LdifEntry ldifEntry2 = new LdifEntry();
        ldifEntry2.readExternal( in );

        assertEquals( ldifEntry1, ldifEntry2 );
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

        LdifEntry ldifEntry1 = new LdifEntry( "cn=app1,ou=applications,ou=conf,dc=apache,dc=org", ldif );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        ldifEntry1.writeExternal( out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        LdifEntry ldifEntry2 = new LdifEntry();
        ldifEntry2.readExternal( in );

        assertEquals( ldifEntry1, ldifEntry2 );
    }
}
