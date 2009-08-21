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

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;


/**
 * The base class for the ObjectClass object. It describes an ObjectClass data structure,
 * from the client perspective (the Schema is not available).</br>
 * An LDAP ObjectClass contains those elements :
 * <li>numericOid (inherited from {@link AbstractSchemaObject})
 * <li>names (inherited from {@link AbstractSchemaObject})
 * <li>description (inherited from {@link AbstractSchemaObject})
 * <li>isObsolete (inherited from {@link AbstractSchemaObject})
 * <li>extension (inherited from {@link AbstractSchemaObject})
 * <li>supOids : The list of inherited ObjectClasses OIDs
 * <li>objectClassType : The ObjectClass type (ABSTRACT, AUXILIARY, STRUCTURAL)
 * <li>mayOids : The list of AttributeType OIDs the ObjectClass instances may use
 * <li>mustOids : The list of AttributeType OIDs the ObjectClass instances must use
 * <br>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev: 664290 $
 */
public class ClientObjectClass extends SchemaObject
{
    /** The serialVersionUID */
    public static final long serialVersionUID = 1L;
    
    /** The list of superiors, if any */
    private List<String> supOids;
    
    /** The OC kind, one of ABSTRACT, STRUCTURAL or AUXILIARY */
    private ObjectClassTypeEnum objectClassType;
    
    /** The list of MUST attributeTypes oids */
    private List<String> mustOids;
    
    /** The list of MAY attributeTypes oids */
    private List<String> mayOids;
    
    
    /**
     * The base constructor for a ClientObjectClass.
     * 
     * @param oid The ObjectClass's OID
     */
    protected ClientObjectClass( String oid )
    {
        super( oid );
    }


    /**
     * {@inheritDoc}
     */
    public List<String> getMayOids() throws NamingException
    {
        return mayOids;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public void setMayOids( List<String> oids ) throws NamingException
    {
        if ( oids == null )
        {
            mayOids = new ArrayList<String>();
        }
        else
        {
            mayOids = oids;
        }
    }


    /**
     * {@inheritDoc}
     */
    public List<String> getMustOids() throws NamingException
    {
        return mustOids;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public void setMustOids( List<String> oids ) throws NamingException
    {
        if ( oids == null )
        {
            mustOids = new ArrayList<String>();
        }
        else
        {
            mustOids = oids;
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    public ObjectClassTypeEnum getType()
    {
        return objectClassType;
    }

    
    /**
     * {@inheritDoc}
     */
    public void setType( ObjectClassTypeEnum objectClassType )
    {
        this.objectClassType = objectClassType;
    }

    
    /**
     * {@inheritDoc}
     */
    public boolean isAbstract()
    {
        return objectClassType == ObjectClassTypeEnum.ABSTRACT;
    }

    
    /**
     * {@inheritDoc}
     */
    public boolean isAuxiliary()
    {
        return objectClassType == ObjectClassTypeEnum.AUXILIARY;
    }

    
    /**
     * {@inheritDoc}
     */
    public boolean isStructural()
    {
        return objectClassType == ObjectClassTypeEnum.STRUCTURAL;
    }

    
    
    
    
    
    
    
    public ObjectClass[] getSuperClasses() throws NamingException
    {
        // TODO Auto-generated method stub
        return null;
    }
}
