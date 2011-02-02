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
package org.apache.directory.shared.ldap.model.exception;


import org.apache.directory.shared.ldap.model.schema.SchemaManager;
import org.apache.directory.shared.ldap.model.schema.SchemaObject;


/**
 * A subclass of {@link LdapException} which is used to report issues 
 * during the integrity check of the schema by the {@link SchemaManager}.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdapSchemaException extends LdapException
{
    /** The serial version UUID */
    static final long serialVersionUID = 1L;

    /** The code of the exception */
    private LdapSchemaExceptionCodes code;

    /** The 'source' schema object */
    private SchemaObject sourceObject;

    /** The 'other' schema object */
    private SchemaObject otherObject;

    /** The related ID (name or OID) of the exception */
    private String relatedId;


    /**
     * Creates a new instance of LdapSchemaException.
     */
    public LdapSchemaException()
    {
        super();
    }


    /**
     * Creates a new instance of LdapSchemaException.
     *
     * @param code
     *      The code of the exception
     */
    public LdapSchemaException( LdapSchemaExceptionCodes code )
    {
        super();
        this.code = code;
    }


    /**
     * Creates a new instance of LdapSchemaException.
     *
     * @param explanation
     *      The message associated with the exception
     */
    public LdapSchemaException( String explanation )
    {
        super( explanation );
    }


    /**
     * Creates a new instance of LdapSchemaException.
     *
     * @param code
     *      The code of the exception
     * @param explanation
     *      The message associated with the exception
     */
    public LdapSchemaException( LdapSchemaExceptionCodes code, String explanation )
    {
        super( explanation );
        this.code = code;
    }


    /**
     * Gets the code of the exception.
     *
     * @return
     *      the code of the exception
     */
    public LdapSchemaExceptionCodes getCode()
    {
        return code;
    }


    /**
     * Sets the code of the exception.
     *
     * @param code
     *      the code of the exception
     */
    public void setCode( LdapSchemaExceptionCodes code )
    {
        this.code = code;
    }


    /**
     * Gets the 'source' schema object.
     *
     * @return
     *      the 'source' schema object
     */
    public SchemaObject getSourceObject()
    {
        return sourceObject;
    }


    /**
     * Sets the 'source' schema object.
     *
     * @param source
     *      the 'source' schema object
     */
    public void setSourceObject( SchemaObject source )
    {
        this.sourceObject = source;
    }


    /**
     * Gets the 'other' schema object.
     *
     * @return
     *      the 'other' schema object
     */
    public SchemaObject getOtherObject()
    {
        return otherObject;
    }


    /**
     * Sets the 'other' schema object.
     *
     * @param other
     *      the 'other' schema object
     */
    public void setOtherObject( SchemaObject other )
    {
        this.otherObject = other;
    }


    /**
     * Gets the related ID (name or OID) of the exception.
     *
     * @return
     *      the related ID (name or OID)
     */
    public String getRelatedId()
    {
        return relatedId;
    }


    /**
     * Sets the related ID (name or OID) of the exception.
     *
     * @param relatedId
     *      the related ID (name or OID)
     */
    public void setRelatedId( String relatedId )
    {
        this.relatedId = relatedId;
    }
}
