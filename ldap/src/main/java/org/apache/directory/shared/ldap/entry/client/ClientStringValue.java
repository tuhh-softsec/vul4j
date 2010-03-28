/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.directory.shared.ldap.entry.client;


import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.apache.directory.shared.ldap.exception.LdapException;

import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.NotImplementedException;
import org.apache.directory.shared.ldap.entry.AbstractValue;
import org.apache.directory.shared.ldap.entry.Value;
import org.apache.directory.shared.ldap.schema.AttributeType;
import org.apache.directory.shared.ldap.schema.LdapComparator;
import org.apache.directory.shared.ldap.schema.MatchingRule;
import org.apache.directory.shared.ldap.schema.Normalizer;
import org.apache.directory.shared.ldap.util.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A server side schema aware wrapper around a String attribute value.
 * This value wrapper uses schema information to syntax check values,
 * and to compare them for equality and ordering.  It caches results
 * and invalidates them when the wrapped value changes.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ClientStringValue extends AbstractValue<String>
{
    /** Used for serialization */
    private static final long serialVersionUID = 2L;
    
    /** logger for reporting errors that might not be handled properly upstream */
    protected static final Logger LOG = LoggerFactory.getLogger( ClientStringValue.class );

    /** reference to the attributeType which is not serialized */
    protected transient AttributeType attributeType;


    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------
    /**
     * Creates a ServerStringValue without an initial wrapped value.
     */
    public ClientStringValue()
    {
        normalized = false;
        valid = null;
    }


    /**
     * Creates a ServerStringValue with an initial wrapped String value.
     *
     * @param wrapped the value to wrap which can be null
     */
    public ClientStringValue( String wrapped )
    {
        this.wrapped = wrapped;
        normalized = false;
        valid = null;
    }


    // -----------------------------------------------------------------------
    // Value<String> Methods
    // -----------------------------------------------------------------------
    /**
     * Get a copy of the stored value.
     *
     * @return A copy of the stored value.
     */
    public String getCopy()
    {
        // The String is immutable, we can safely return the internal
        // object without copying it.
        return wrapped;
    }
    
    
    /**
     * Sets the wrapped String value.  Has the side effect of setting the
     * normalizedValue and the valid flags to null if the wrapped value is
     * different than what is already set.  These cached values must be
     * recomputed to be correct with different values.
     *
     * @see ServerValue#set(Object)
     */
    public final void set( String wrapped )
    {
        // Why should we invalidate the normalized value if it's we're setting the
        // wrapper to it's current value?
        if ( !StringTools.isEmpty( wrapped ) && wrapped.equals( getString() ) )
        {
            return;
        }

        normalizedValue = null;
        normalized = false;
        valid = null;
        this.wrapped = wrapped;
    }


    /**
     * Gets the normalized (canonical) representation for the wrapped string.
     * If the wrapped String is null, null is returned, otherwise the normalized
     * form is returned.  If the normalizedValue is null, then this method
     * will attempt to generate it from the wrapped value: repeated calls to
     * this method do not unnecessarily normalize the wrapped value.  Only changes
     * to the wrapped value result in attempts to normalize the wrapped value.
     *
     * @return gets the normalized value
     */
    public String getNormalizedValue()
    {
        if ( isNull() )
        {
            return null;
        }

        if ( normalizedValue == null )
        {
            return wrapped;
        }

        return normalizedValue;
    }


    /**
     * Gets a copy of the the normalized (canonical) representation 
     * for the wrapped value.
     *
     * @return gets a copy of the normalized value
     */
    public String getNormalizedValueCopy()
    {
        return getNormalizedValue();
    }


    /**
     * Normalize the value. For a client String value, applies the given normalizer.
     * 
     * It supposes that the client has access to the schema in order to select the
     * appropriate normalizer.
     * 
     * @param Normalizer The normalizer to apply to the value
     * @exception LdapException If the value cannot be normalized
     */
    public final void normalize( Normalizer normalizer ) throws LdapException
    {
        if ( normalizer != null )
        {
            normalizedValue = (String)normalizer.normalize( wrapped );
            normalized = true;
        }
    }

    
    // -----------------------------------------------------------------------
    // Comparable<String> Methods
    // -----------------------------------------------------------------------
    /**
     * @see ServerValue#compareTo(ServerValue)
     * @throws IllegalStateException on failures to extract the comparator, or the
     * normalizers needed to perform the required comparisons based on the schema
     */
    public int compareTo( Value<String> value )
    {
        if ( isNull() )
        {
            if ( ( value == null ) || value.isNull() )
            {
                return 0;
            }
            else
            {
                return -1;
            }
        }
        else if ( ( value == null ) || value.isNull() )
        {
            return 1;
        }

        if ( value instanceof ClientStringValue )
        {
            ClientStringValue stringValue = ( ClientStringValue ) value;
            
            return getNormalizedValue().compareTo( stringValue.getNormalizedValue() );
        }
        else 
        {
            String message = I18n.err( I18n.ERR_04128, toString(), value.getClass() );
            LOG.error( message );
            throw new NotImplementedException( message );
        }
    }


    // -----------------------------------------------------------------------
    // Cloneable methods
    // -----------------------------------------------------------------------
    /**
     * Get a clone of the Client Value
     * 
     * @return a copy of the current value
     */
    public ClientStringValue clone()
    {
        return (ClientStringValue)super.clone();
    }


    // -----------------------------------------------------------------------
    // Object Methods
    // -----------------------------------------------------------------------
    /**
     * @see Object#hashCode()
     * @return the instance's hashcode 
     */
    public int hashCode()
    {
        // return zero if the value is null so only one null value can be
        // stored in an attribute - the binary version does the same 
        if ( isNull() )
        {
            if ( attributeType != null )
            {
                // return the OID hashcode if the value is null. 
                return attributeType.getOid().hashCode();
            }
            
            return 0;
        }

        // If the normalized value is null, will default to wrapped
        // which cannot be null at this point.
        // If the normalized value is null, will default to wrapped
        // which cannot be null at this point.
        int h = 0;

        String normalized = getNormalizedValue();
        
        if ( normalized != null )
        {
            h = normalized.hashCode();
        }
        else
        {
            h = 17;
        }
        
        // Add the OID hashcode if we have an AttributeType
        if ( attributeType != null )
        {
            h = h*37 + attributeType.getOid().hashCode();
        }
        
        return h;
    }


    /**
     * @see Object#equals(Object)
     * 
     * Two ClientStringValue are equals if their normalized values are equal
     */
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }

        if ( ! ( obj instanceof ClientStringValue ) )
        {
            return false;
        }

        ClientStringValue other = ( ClientStringValue ) obj;
        
        if ( this.isNull() )
        {
            return other.isNull();
        }
        
        // Test the normalized values
        return this.getNormalizedValue().equals( other.getNormalizedValue() );
    }
    
    
    /**
     * Tells if the current value is Binary or String
     * 
     * @return <code>true</code> if the value is Binary, <code>false</code> otherwise
     */
    public boolean isBinary()
    {
        return false;
    }

    
    /**
     * Uses the syntaxChecker associated with the attributeType to check if the
     * value is valid.  Repeated calls to this method do not attempt to re-check
     * the syntax of the wrapped value every time if the wrapped value does not
     * change. Syntax checks only result on the first check, and when the wrapped
     * value changes.
     *
     * @see Value#isValid()
     */
    public final boolean isValid()
    {
        if ( valid != null )
        {
            return valid;
        }

        if ( attributeType != null )
        {
            valid = attributeType.getSyntax().getSyntaxChecker().isValidSyntax( get() );
        }
        else
        {
            valid = false;
        }
        
        return valid;
    }
    
    
    /**
     * @return The length of the interned value
     */
    public int length()
    {
        return wrapped != null ? wrapped.length() : 0;
    }
    
    
    /**
     * Get the wrapped value as a byte[].
     * @return the wrapped value as a byte[]
     */
    public byte[] getBytes()
    {
        return StringTools.getBytesUtf8( wrapped );
    }
    
    
    /**
     * Get the wrapped value as a String.
     *
     * @return the wrapped value as a String
     */
    public String getString()
    {
        return wrapped != null ? wrapped : "";
    }
    
    
    /**
     * @see Externalizable#readExternal(ObjectInput)
     */
    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException
    {
        // Read the wrapped value, if it's not null
        if ( in.readBoolean() )
        {
            wrapped = in.readUTF();
        }
        
        // Read the isNormalized flag
        normalized = in.readBoolean();
        
        if ( normalized )
        {
            // Read the normalized value, if not null
            if ( in.readBoolean() )
            {
                normalizedValue = in.readUTF();
            }
        }
    }

    
    /**
     * @see Externalizable#writeExternal(ObjectOutput)
     */
    public void writeExternal( ObjectOutput out ) throws IOException
    {
        // Write the wrapped value, if it's not null
        if ( wrapped != null )
        {
            out.writeBoolean( true );
            out.writeUTF( wrapped );
        }
        else
        {
            out.writeBoolean( false );
        }
        
        // Write the isNormalized flag
        if ( normalized )
        {
            out.writeBoolean( true );
            
            // Write the normalized value, if not null
            if ( normalizedValue != null )
            {
                out.writeBoolean( true );
                out.writeUTF( normalizedValue );
            }
            else
            {
                out.writeBoolean( false );
            }
        }
        else
        {
            out.writeBoolean( false );
        }
        
        // and flush the data
        out.flush();
    }

    
    /**
     * Get the associated AttributeType
     * @return The AttributeType
     */
    public AttributeType getAttributeType()
    {
        return attributeType;
    }

    
    /**
     * Check if the value is stored into an instance of the given 
     * AttributeType, or one of its ascendant.
     * 
     * For instance, if the Value is associated with a CommonName,
     * checking for Name will match.
     * 
     * @param attributeType The AttributeType we are looking at
     * @return <code>true</code> if the value is associated with the given
     * attributeType or one of its ascendant
     */
    public boolean instanceOf( AttributeType attributeType ) throws LdapException
    {
        if ( attributeType != null )
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
     *  Check the attributeType member. It should not be null, 
     *  and it should contains a syntax.
     */
    protected String checkAttributeType( AttributeType attributeType )
    {
        if ( attributeType == null )
        {
            return "The AttributeType parameter should not be null";
        }
        
        if ( attributeType.getSyntax() == null )
        {
            return "There is no Syntax associated with this attributeType";
        }

        return null;
    }

    
    /**
     * Gets a comparator using getMatchingRule() to resolve the matching
     * that the comparator is extracted from.
     *
     * @return a comparator associated with the attributeType or null if one cannot be found
     * @throws LdapException if resolution of schema entities fail
     */
    protected LdapComparator<? super Object> getLdapComparator() throws LdapException
    {
        if ( attributeType != null )
        {
            MatchingRule mr = getMatchingRule();
    
            if ( mr == null )
            {
                return null;
            }
    
            return mr.getLdapComparator();
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
    protected MatchingRule getMatchingRule() throws LdapException
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
    protected Normalizer getNormalizer() throws LdapException
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
     * We will write the value and the normalized value, only
     * if the normalized value is different.
     * 
     * If the value is empty, a flag is written at the beginning with 
     * the value true, otherwise, a false is written.
     * 
     * The data will be stored following this structure :
     *  [empty value flag]
     *  [UP value]
     *  [normalized] (will be false if the value can't be normalized)
     *  [same] (a flag set to true if the normalized value equals the UP value)
     *  [Norm value] (the normalized value if different from the UP value)
     *  
     *  @param out the buffer in which we will stored the serialized form of the value
     *  @throws IOException if we can't write into the buffer
     */
    public void serialize( ObjectOutput out ) throws IOException
    {
        if ( wrapped != null )
        {
            // write a flag indicating that the value is not null
            out.writeBoolean( true );
            
            // Write the data
            out.writeUTF( wrapped );
            
            // Normalize the data
            try
            {
                normalize();
                out.writeBoolean( true );
                
                if ( wrapped.equals( normalizedValue ) )
                {
                    out.writeBoolean( true );
                }
                else
                {
                    out.writeBoolean( false );
                    out.writeUTF( normalizedValue );
                }
            }
            catch ( LdapException ne )
            {
                // The value can't be normalized, we don't write the 
                // normalized value.
                normalizedValue = null;
                out.writeBoolean( false );
            }
        }
        else
        {
            // Write a flag indicating that the value is null
            out.writeBoolean( false );
        }
        
        out.flush();
    }

    
    /**
     * Deserialize a ServerStringValue. 
     *
     * @param in the buffer containing the bytes with the serialized value
     * @throws IOException 
     * @throws ClassNotFoundException
     */
    public void deserialize( ObjectInput in ) throws IOException, ClassNotFoundException
    {
        // If the value is null, the flag will be set to false
        if ( !in.readBoolean() )
        {
            set( null );
            normalizedValue = null;
            return;
        }
        
        // Read the value
        String wrapped = in.readUTF();
        
        set( wrapped );
        
        // Read the normalized flag
        normalized = in.readBoolean();
        
        if ( normalized )
        {
            normalized = true;

            // Read the 'same' flag
            if ( in.readBoolean() )
            {
                normalizedValue = wrapped;
            }
            else
            {
                // The normalized value is different. Read it
                normalizedValue = in.readUTF();
            }
        }
    }


    /**
     * @see Object#toString()
     */
    public String toString()
    {
        return wrapped == null ? "null": wrapped;
    }
}
