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
import org.apache.directory.shared.ldap.model.schema.LdapComparator;
import org.apache.directory.shared.ldap.model.schema.MatchingRule;
import org.apache.directory.shared.ldap.model.schema.Normalizer;
import org.apache.directory.shared.ldap.model.schema.SchemaManager;
import org.apache.directory.shared.ldap.model.schema.SyntaxChecker;
import org.apache.directory.shared.util.StringConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A wrapper around byte[] values in entries.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class AbstractValue<T> implements Value<T>
{
    /** logger for reporting errors that might not be handled properly upstream */
    private static final Logger LOG = LoggerFactory.getLogger( AbstractValue.class );

    /** reference to the attributeType zssociated with the value */
    protected transient AttributeType attributeType;

    /** the wrapped binary value */
    protected T wrappedValue;
    
    /** the canonical representation of the wrapped value */
    protected T normalizedValue;

    /** A flag set when the value has been normalized */
    protected boolean normalized;

    /** cached results of the isValid() method call */
    protected Boolean valid;

    /** A flag set if the normalized data is different from the wrapped data */
    protected boolean same;
    
    /** The computed hashcode. We don't want to compute it each time the hashcode() method is called */
    protected volatile int h;

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Value<T> clone()
    {
        try
        {
            return (Value<T>)super.clone();
        }
        catch ( CloneNotSupportedException cnse )
        {
            // Do nothing
            return null;
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    public T getReference()
    {
        return wrappedValue;
    }

    
    /**
     * {@inheritDoc}
     */
    public AttributeType getAttributeType()
    {
        return attributeType;
    }

    
    /**
     * {@inheritDoc}
     */
    public void apply( AttributeType attributeType )
    {
        if ( this.attributeType != null ) 
        {
            if ( !attributeType.equals( this.attributeType ) )
            {
                String message = I18n.err( I18n.ERR_04476, attributeType.getName(), this.attributeType.getName() );
                LOG.info( message );
                throw new IllegalArgumentException( message );
            }
            else
            {
                return;
            }
        }
        
        // First, check that the value is syntaxically correct
        try
        {
            if ( ! isValid( attributeType.getSyntax().getSyntaxChecker() ) )
            {
                String message = I18n.err( I18n.ERR_04476, attributeType.getName(), this.attributeType.getName() );
                LOG.info( message );
                throw new IllegalArgumentException( message );
            }
        }
        catch ( LdapException le )
        {
            String message = I18n.err( I18n.ERR_04447, le.getLocalizedMessage() );
            LOG.info( message );
            normalized = false;
        }
        
        this.attributeType = attributeType;
        
        try
        {
            normalize();
        }
        catch ( LdapException ne )
        {
            String message = I18n.err( I18n.ERR_04447, ne.getLocalizedMessage() );
            LOG.info( message );
            normalized = false;
        }
        
        h=0;
        hashCode();
    }


    /**
     * Gets a comparator using getMatchingRule() to resolve the matching
     * that the comparator is extracted from.
     *
     * @return a comparator associated with the attributeType or null if one cannot be found
     * @throws LdapException if resolution of schema entities fail
     */
    @SuppressWarnings("unchecked")
    protected final LdapComparator<T> getLdapComparator() throws LdapException
    {
        if ( attributeType != null )
        {
            MatchingRule mr = getMatchingRule();
    
            if ( mr == null )
            {
                return null;
            }
    
            return (LdapComparator<T>)mr.getLdapComparator();
        }
        else
        {
            return null;
        }
    }
    
    
    /**
     * Find a matchingRule to use for normalization and comparison.  If an equality
     * matchingRule cannot be found it checks to see if other matchingRules are
     * available: SUBSTR, and ORDERING.  If a matchingRule cannot be found null is
     * returned.
     *
     * @return a matchingRule or null if one cannot be found for the attributeType
     * @throws LdapException if resolution of schema entities fail
     */
    protected final MatchingRule getMatchingRule() throws LdapException
    {
        if ( attributeType != null )
        {
            MatchingRule mr = attributeType.getEquality();
    
            if ( mr == null )
            {
                mr = attributeType.getOrdering();
            }
    
            if ( mr == null )
            {
                mr = attributeType.getSubstring();
            }
    
            return mr;
        }
        else
        {
            return null;
        }
    }


    /**
     * Gets a normalizer using getMatchingRule() to resolve the matchingRule
     * that the normalizer is extracted from.
     *
     * @return a normalizer associated with the attributeType or null if one cannot be found
     * @throws LdapException if resolution of schema entities fail
     */
    protected final Normalizer getNormalizer() throws LdapException
    {
        if ( attributeType != null )
        {
            MatchingRule mr = getMatchingRule();
    
            if ( mr == null )
            {
                return null;
            }
    
            return mr.getNormalizer();
        }
        else
        {
            return null;
        }
    }

    
    /**
     * {@inheritDoc}
     */
    public boolean instanceOf( AttributeType attributeType ) throws LdapException
    {
        if ( ( attributeType != null ) && this.attributeType.equals( attributeType ) )
        {
            if ( this.attributeType.equals( attributeType ) )
            {
                return true;
            }
            
            return this.attributeType.isDescendantOf( attributeType );
        }

        return false;
    }


    /**
     * {@inheritDoc}
     */
    public T getNormalizedValueReference()
    {
        if ( isNull() )
        {
            return null;
        }

        if ( normalizedValue == null )
        {
            return wrappedValue;
        }

        return normalizedValue;

    }

    
    /**
     * {@inheritDoc}
     */
    public final boolean isNull()
    {
        return wrappedValue == null; 
    }
    
    
    /**
     * This method is only used for serialization/deserialization
     * 
     * @return Tells if the wrapped value and the normalized value are the same 
     */
    /* no qualifier */ final boolean isSame()
    {
        return same;
    }

    
    /**
     * {@inheritDoc}
     */
    public final boolean isValid()
    {
        if ( valid != null )
        {
            return valid;
        }

        if ( attributeType != null )
        {
            SyntaxChecker syntaxChecker = attributeType.getSyntax().getSyntaxChecker();
            T value = getNormalizedValue();
            valid = syntaxChecker.isValidSyntax( value );
        }
        else
        {
            valid = false;
        }
        
        return valid;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public final boolean isValid( SyntaxChecker syntaxChecker ) throws LdapException
    {
        if ( syntaxChecker == null )
        {
            String message = I18n.err( I18n.ERR_04139, toString() );
            LOG.error( message );
            throw new LdapException( message );
        }
        
        valid = syntaxChecker.isValidSyntax( getReference() );
        
        return valid;
    }


    /**
     * {@inheritDoc}
     */
    public void normalize() throws LdapException
    {
        normalized = true;
        normalizedValue = wrappedValue;
        h = 0;
        hashCode();
    }


    /**
     * {@inheritDoc}
     */
    public final boolean isNormalized()
    {
        return normalized;
    }

    
    /**
     * {@inheritDoc}
     */
    public final void setNormalized( boolean normalized )
    {
        this.normalized = normalized;
    }


    /**
     * Serializes a Value instance.
     * 
     * @param value The Value instance to serialize
     * @param out The stream into which we will write the serialized instance
     * @throws IOException If the stream can't be written
     */
    @SuppressWarnings("unchecked")
    public static void serialize( Value<?> value, ObjectOutput out ) throws IOException
    {
        // The Value type
        out.writeBoolean( value.isBinary() );

        // The AttributeType's OID if we have one
        if ( value.getAttributeType() != null )
        {
            out.writeBoolean( true );
            out.writeUTF( value.getAttributeType().getOid() );
        }
        else
        {
            out.writeBoolean( false );
        }
        
        // The UP value and norm value
        if ( value.isBinary() )
        {
            byte[] upValue = (byte[])value.getReference();
            
            if ( upValue == null )
            {
                out.writeInt( -1 );
            }
            else
            {
                out.writeInt( upValue.length );
                
                if ( upValue.length > 0 )
                {
                    out.write( upValue );
                }
            }

            byte[] normValue = (byte[])value.getNormalizedValueReference();
            
            if ( normValue == null )
            {
                out.writeInt( -1 );
            }
            else
            {
                out.writeInt( normValue.length );
                
                if ( normValue.length > 0 )
                {
                    out.write( normValue );
                }
            }
        }
        else
        {
            if ( ((AbstractValue<String>)value).wrappedValue != null )
            {
                out.writeBoolean( true );
                out.writeUTF( ((AbstractValue<String>)value).wrappedValue );
            }
            else
            {
                out.writeBoolean( false );
            }
            
            if ( ((AbstractValue<String>)value).normalizedValue != null )
            {
                out.writeBoolean( true );
                out.writeUTF( ((AbstractValue<String>)value).normalizedValue );
            }
            else
            {
                out.writeBoolean( false );
            }
        }
        
        // The normalized flag
        out.writeBoolean( value.isNormalized() );
        
        // The valid flag
        out.writeBoolean( value.isValid() );
        
        // The same flag
        if ( value.isBinary() )
        {   
            out.writeBoolean( ((BinaryValue)value).isSame() );
        }
        else
        {
            out.writeBoolean( ((StringValue)value).isSame() );
        }

        // The computed hashCode
        out.writeInt( value.hashCode() );
        
        out.flush();
    }


    /**
     * Deserializes a Value instance.
     * 
     * @param schemaManager The schemaManager instance
     * @param in The input stream from which the Value is read
     * @return a deserialized Value
     * @throws IOException If the stream can't be read
     */
    @SuppressWarnings("unchecked")
    public static Value<?> deserialize( SchemaManager schemaManager, ObjectInput in ) throws IOException
    {
        // The value type
        boolean isBinary = in.readBoolean();
        
        Value<?> value = null;

        if ( isBinary )
        {
            value = new BinaryValue();
        }
        else
        {
            value = new StringValue();
        }

        // The attributeType presence's flag
        boolean hasAttributeType = in.readBoolean();
        
        if ( hasAttributeType )
        {
            String oid = in.readUTF();
            
            if ( schemaManager != null )
            {
                ((AbstractValue<?>)value).attributeType = schemaManager.getAttributeType( oid );
            }
        }
        
        if ( isBinary )
        {
            int upValueSize = in.readInt();
            
            switch ( upValueSize )
            {
                case -1 :
                    break;
                    
                case 0 :
                    ((AbstractValue<byte[]>)value).wrappedValue = StringConstants.EMPTY_BYTES;
                    break;
                    
                default :
                    ((AbstractValue<byte[]>)value).wrappedValue = new byte[upValueSize];
                    in.read( ((AbstractValue<byte[]>)value).wrappedValue );
                    break;
            }

            int normValueSize = in.readInt();
            
            switch ( normValueSize )
            {
                case -1 :
                    break;
                    
                case 0 :
                    ((AbstractValue<byte[]>)value).normalizedValue = StringConstants.EMPTY_BYTES;
                    break;
                   
                default :
                    ((AbstractValue<byte[]>)value).normalizedValue = new byte[normValueSize];
                    in.read( ((AbstractValue<byte[]>)value).normalizedValue );
                    break;
            }
        }
        else
        {
            boolean notNull = in.readBoolean();
            
            if ( notNull )
            {
                ((AbstractValue<String>)value).wrappedValue = in.readUTF();
            }

            notNull = in.readBoolean();
            
            if ( notNull )
            {
                ((AbstractValue<String>)value).normalizedValue = in.readUTF();
            }
        }

        // The normalized flag
        ((AbstractValue<?>)value).normalized = in.readBoolean();
        
        // The valid flag
        ((AbstractValue<?>)value).valid = in.readBoolean();
        
        // The same flag
        ((AbstractValue<?>)value).same = in.readBoolean();

        // The computed hashCode
        ((AbstractValue<?>)value).h = in.readInt();
        
        return value;
    }
}