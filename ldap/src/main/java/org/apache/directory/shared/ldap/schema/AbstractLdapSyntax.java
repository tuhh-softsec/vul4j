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
package org.apache.directory.shared.ldap.schema;


/**
 * An abstract Syntax class.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev: 664290 $
 */
public abstract class AbstractLdapSyntax extends AbstractSchemaObject implements LdapSyntax
{
    /** the human readable flag */
    private boolean isHumanReadable = false;


    // ------------------------------------------------------------------------
    // C O N S T R U C T O R S
    // ------------------------------------------------------------------------

    /**
     * Creates a Syntax object using a unique OID.
     * 
     * @param oid
     *            the OID for this Syntax
     */
    protected AbstractLdapSyntax(String oid)
    {
        super( oid );
        
        
        objectType = SchemaObjectType.LDAP_SYNTAX;
    }


    /**
     * Creates a Syntax object using a unique OID.
     * 
     * @param oid
     *            the OID for this Syntax
     * @param isHumanReadable
     *            whether or not Syntax is human readable
     */
    protected AbstractLdapSyntax(String oid, boolean isHumanReadable)
    {
        super( oid );
        this.isHumanReadable = isHumanReadable;
        
        objectType = SchemaObjectType.LDAP_SYNTAX;
    }


    /**
     * Creates a Syntax object using a unique OID.
     * 
     * @param oid
     *            the OID for this Syntax
     * @param description
     *            the description for this Syntax
     */
    protected AbstractLdapSyntax(String oid, String description)
    {
        super( oid, description );
        objectType = SchemaObjectType.LDAP_SYNTAX;
    }


    /**
     * Creates a Syntax object using a unique OID.
     * 
     * @param oid
     *            the OID for this Syntax
     * @param isHumanReadable
     *            whether or not Syntax is human readable
     * @param description
     *            the description for this Syntax
     */
    protected AbstractLdapSyntax(String oid, String description, boolean isHumanReadable)
    {
        super( oid, description );
        this.isHumanReadable = isHumanReadable;
        objectType = SchemaObjectType.LDAP_SYNTAX;
    }


    // ------------------------------------------------------------------------
    // Syntax interface methods
    // ------------------------------------------------------------------------

    /**
     * @see org.apache.directory.shared.ldap.schema.LdapSyntax#isHumanReadable()
     * @return true if the syntax can be interpreted by humans, false otherwise
     */
    public final boolean isHumanReadable()
    {
        return isHumanReadable;
    }


    // ------------------------------------------------------------------------
    // Protected setters
    // ------------------------------------------------------------------------

    /**
     * Sets the human readable flag value.
     * 
     * @param isHumanReadable
     *            the human readable flag value to set
     */
    protected void setHumanReadable( boolean isHumanReadable )
    {
        this.isHumanReadable = isHumanReadable;
    }


    // ------------------------------------------------------------------------
    // Object overloads
    // ------------------------------------------------------------------------

    /**
     * Based on the hashCode of the oid property.
     * 
     * @return the hashCode of the oid String
     */
    public int hashCode()
    {
        return oid.hashCode();
    }


    /**
     * If the object implements Syntax and has the same OID as this Syntax then
     * they are equal.
     * 
     * @param obj
     *            the object to test for equality
     * @return true if obj is a Syntax and OID's match
     */
    public boolean equals( Object obj )
    {
        if ( !super.equals( obj ) )
        {
            return false;
        }

        if ( obj instanceof LdapSyntax )
        {
            return true;
        }

        return false;
    }
}
