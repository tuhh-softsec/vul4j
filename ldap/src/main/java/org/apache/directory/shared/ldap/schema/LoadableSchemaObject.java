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
 * An abstract class used to manage the ADS specific SchemaObject, which can
 * contain some compiled Java class to implement the specific logic.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev: 437007 $
 */
public abstract class LoadableSchemaObject extends SchemaObject
{
    /** The serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** The Full Qualified Class Name */
    private String fqcn;

    /** The base64 encoded bytecode for this schema */
    private String bytecode;


    /**
     * Constructor to use when the OID is known in advance.
     * 
     * @param objectType The SchemaObject type
     * @param oid The SchemaObject OID
     */
    protected LoadableSchemaObject( SchemaObjectType objectType, String oid )
    {
        super( objectType, oid );

        fqcn = "";
        bytecode = null;
    }


    /**
     * Constructor to use when the OID is not known until after instantiation.
     * 
     * @param objectType The SchemaObject type
     */
    protected LoadableSchemaObject( SchemaObjectType objectType )
    {
        super( objectType );

        fqcn = "";
        bytecode = null;
    }


    /**
     * @return The associated bytecode of this SchemaObject instance
     */
    public String getBytecode()
    {
        return bytecode;
    }


    /**
     * Stores some bytecode representing the compiled Java class for this
     * SchemaObject instance.
     * 
     * @param bytecode The bytecode to store
     */
    public void setBytecode( String bytecode )
    {
        if ( ! isReadOnly )
        {
            this.bytecode = bytecode;
        }
    }


    /**
     * @return The chemaObject instance Fully Qualified Class Name
     */
    public String getFqcn()
    {
        return fqcn;
    }


    /**
     * Set the Fully Qualified Class Name for this SchemaObject instance
     * class stored in the bytecode attribute
     * @param fqcn The Fully Qualified Class Name
     */
    public void setFqcn( String fqcn )
    {
        if ( ! isReadOnly )
        {
            this.fqcn = fqcn;
        }
    }


    /**
     * Clone a LoadableSchemaObject
     */
    public LoadableSchemaObject clone() throws CloneNotSupportedException
    {
        LoadableSchemaObject clone = (LoadableSchemaObject)super.clone();
        
        return clone;
    }
}
