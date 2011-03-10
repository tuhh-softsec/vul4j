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
package org.apache.directory.shared.ldap.model.entry;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.model.exception.LdapException;
import org.apache.directory.shared.ldap.model.schema.AttributeType;
import org.apache.directory.shared.ldap.model.schema.SchemaManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An internal implementation for a ModificationItem. The name has been
 * chosen so that it does not conflict with @see ModificationItem
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DefaultModification implements Modification
{
    /** The modification operation */
    private ModificationOperation operation;
    
    /** The attribute which contains the modification */
    private EntryAttribute attribute;
    
    /** The schemaManager */
    private AttributeType attributeType;
 
    /** logger for reporting errors that might not be handled properly upstream */
    protected static final Logger LOG = LoggerFactory.getLogger( Modification.class );

    
    /**
     * Creates a new instance of DefaultModification.
     */
    public DefaultModification()
    {
    }

    
    /**
     * Creates a new instance of DefaultModification.
     */
    public DefaultModification( AttributeType attributeType )
    {
        this.attributeType = attributeType;
    }

    /**
     * Creates a new instance of DefaultModification.
     *
     * @param operation The modification operation
     * @param attribute The associated attribute 
     */
    public DefaultModification( ModificationOperation operation, EntryAttribute attribute )
    {
        this.operation = operation;
        this.attribute = attribute;
    }

    /**
     * Creates a new instance of DefaultModification.
     *
     * @param attributeType The attributeType 
     * @param operation The modification operation
     * @param attribute The associated attribute 
     */
    public DefaultModification( AttributeType attributeType, ModificationOperation operation, EntryAttribute attribute )
    {
        this.attributeType = attributeType;
        this.operation = operation;
        this.attribute = attribute;
    }
    
    
    /**
     * Creates a new instance of DefaultModification.
     *
     * @param schemaManager The schema manager 
     * @param modification The modification
     */
    public DefaultModification( SchemaManager schemaManager, Modification modification )
    {
        operation = modification.getOperation();
        
        EntryAttribute modAttribute = modification.getAttribute();
        
        try
        {
            AttributeType at = modAttribute.getAttributeType();
            
            if ( at == null )
            {
                at = schemaManager.lookupAttributeTypeRegistry( modAttribute.getId() );
            }
            
            attribute = new DefaultEntryAttribute( at, modAttribute );
        }
        catch ( LdapException ne )
        {
            // The attributeType is incorrect. Log, but do nothing otherwise.
            LOG.error( I18n.err( I18n.ERR_04472, modAttribute.getId() ) );
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    public ModificationOperation getOperation()
    {
        return operation;
    }
    
    
    /**
     * Store the modification operation
     *
     * @param operation The DirContext value to assign
     */
    public void setOperation( int operation )
    {
        this.operation = ModificationOperation.getOperation( operation );
    }

    
    /**
     * {@inheritDoc}
     */
    public void setOperation( ModificationOperation operation )
    {
        this.operation = operation;
    }
        
    
    /**
     * {@inheritDoc}
     */
    public EntryAttribute getAttribute()
    {
        return attribute;
    }
    
    
    /**
     * Set the attribute's modification
     *
     * @param attribute The modified attribute 
     */
    public void setAttribute( EntryAttribute attribute )
    {
        this.attribute = attribute;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public void applyAttributeType( AttributeType attributeType )
    {
        this.attributeType = attributeType;
        this.attribute.setAttributeType( attributeType );
    }
    
    
    /**
     * @see Object#equals(Object)
     * @return <code>true</code> if both values are equal
     */
    public boolean equals( Object that )
    {
        // Basic equals checks
        if ( this == that )
        {
            return true;
        }
        
        if ( ! (that instanceof Modification ) )
        {
            return false;
        }
        
        Modification otherModification = (Modification)that;
        
        // Check the operation
        if ( operation != otherModification.getOperation() )
        {
            return false;
        }

        // Check the attribute
        if ( attribute == null )
        {
            return otherModification.getAttribute() == null;
        }
        
        return attribute.equals( otherModification.getAttribute() );
    }
    
    
    /**
     * Compute the modification @see Object#hashCode
     * @return the instance's hash code 
     */
    public int hashCode()
    {
        int h = 37;
        
        h += h*17 + operation.getValue();
        h += h*17 + attribute.hashCode();
        
        return h;
    }
    

    /**
     * @see java.io.Externalizable#readExternal(ObjectInput)
     */
    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException
    {
        // The operation
        operation = ModificationOperation.getOperation( in.readInt() );

        // The EntryAttribute if we have some
        boolean hasAttribute = in.readBoolean();
        
        if ( hasAttribute )
        {
            attribute = new DefaultEntryAttribute( attributeType );
            attribute.readExternal( in );
        }
    }
    
    
    /**
     * @see java.io.Externalizable#writeExternal(ObjectOutput)
     */
    public void writeExternal( ObjectOutput out ) throws IOException
    {
        // The operation
        out.writeInt( operation.getValue() );
        
        // The EntryAttribute if not null
        if ( attribute != null )
        {
            out.writeBoolean( true );
            attribute.writeExternal( out );
        }
        else
        {
            out.writeBoolean( false );
        }
        
        out.flush();
    }
    
    
    /**
     * Clone a modification
     * 
     * @return  a copied instance of the current modification
     */
    public DefaultModification clone()
    {
        try
        {
            DefaultModification clone = (DefaultModification)super.clone();
            
            clone.attribute = this.attribute.clone();
            return clone;
        }
        catch ( CloneNotSupportedException cnse )
        {
            return null;
        }
    }
    
    
    /**
     * @see Object#toString()
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append( "Modification: " ).
            append( operation ).
            append( "\n" ).
            append( ", attribute : " ).
            append( attribute );
        
        return sb.toString();
    }
}
