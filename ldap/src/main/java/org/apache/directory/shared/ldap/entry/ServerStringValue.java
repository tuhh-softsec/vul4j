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
package org.apache.directory.shared.ldap.entry;


import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.NotImplementedException;
import org.apache.directory.shared.ldap.entry.client.ClientStringValue;
import org.apache.directory.shared.ldap.exception.LdapException;
import org.apache.directory.shared.ldap.schema.AttributeType;
import org.apache.directory.shared.ldap.schema.LdapComparator;
import org.apache.directory.shared.ldap.schema.Normalizer;


/**
 * A server side schema aware wrapper around a String attribute value.
 * This value wrapper uses schema information to syntax check values,
 * and to compare them for equality and ordering.  It caches results
 * and invalidates them when the wrapped value changes.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ServerStringValue extends ClientStringValue
{
    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------
    /**
     * Creates a ServerStringValue without an initial wrapped value.
     *
     * @param attributeType the schema type associated with this ServerStringValue
     */
    public ServerStringValue( AttributeType attributeType )
    {
        super();
        
        if ( attributeType == null )
        {
            throw new IllegalArgumentException( I18n.err( I18n.ERR_04442 ) );
        }

        if ( attributeType.getSyntax() == null )
        {
            throw new IllegalArgumentException( I18n.err( I18n.ERR_04445 ) );
        }

        if ( ! attributeType.getSyntax().isHumanReadable() )
        {
            LOG.warn( "Treating a value of a binary attribute {} as a String: " +
                    "\nthis could cause data corruption!", attributeType.getName() );
        }

        this.attributeType = attributeType;
    }


    /**
     * Creates a ServerStringValue with an initial wrapped String value.
     *
     * @param attributeType the schema type associated with this ServerStringValue
     * @param wrapped the value to wrap which can be null
     */
    public ServerStringValue( AttributeType attributeType, String wrapped )
    {
        this( attributeType );
        this.wrapped = wrapped;
    }


    /**
     * Creates a ServerStringValue with an initial wrapped String value and
     * a normalized value.
     *
     * @param attributeType the schema type associated with this ServerStringValue
     * @param wrapped the value to wrap which can be null
     * @param normalizedValue the normalized value
     */
    /** No protection */ ServerStringValue( AttributeType attributeType, String wrapped, String normalizedValue, boolean valid )
    {
        super( wrapped );
        this.normalized = true;
        this.attributeType = attributeType;
        this.normalizedValue = normalizedValue;
        this.valid = valid;
    }


    // -----------------------------------------------------------------------
    // Value<String> Methods, overloaded
    // -----------------------------------------------------------------------
    /**
     * @return a copy of the current value
     */
    public ServerStringValue clone()
    {
        ServerStringValue clone = (ServerStringValue)super.clone();
        
        return clone;
    }
    
    


    // -----------------------------------------------------------------------
    // ServerValue<String> Methods
    // -----------------------------------------------------------------------
    /**
     * Compute the normalized (canonical) representation for the wrapped string.
     * If the wrapped String is null, the normalized form will be null too.  
     *
     * @throws LdapException if the value cannot be properly normalized
     */
    public void normalize() throws LdapException
    {
        // If the value is already normalized, get out.
        if ( normalized )
        {
            return;
        }
        
        Normalizer normalizer = getNormalizer();

        if ( normalizer == null )
        {
            normalizedValue = wrapped;
        }
        else
        {
            normalizedValue = ( String ) normalizer.normalize( wrapped );
        }

        normalized = true;
    }
    

    /**
     * Gets the normalized (canonical) representation for the wrapped string.
     * If the wrapped String is null, null is returned, otherwise the normalized
     * form is returned.  If no the normalizedValue is null, then this method
     * will attempt to generate it from the wrapped value: repeated calls to
     * this method do not unnecessarily normalize the wrapped value.  Only changes
     * to the wrapped value result in attempts to normalize the wrapped value.
     *
     * @return gets the normalized value
     * @throws LdapException if the value cannot be properly normalized
     */
    public String getNormalizedValue() 
    {
        if ( isNull() )
        {
            normalized = true;
            return null;
        }

        if ( !normalized )
        {
            try
            {
                normalize();
            }
            catch ( LdapException ne )
            {
                String message = "Cannot normalize the value :" + ne.getLocalizedMessage();
                LOG.info( message );
                normalized = false;
            }
        }

        return normalizedValue;
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

        valid = attributeType.getSyntax().getSyntaxChecker().isValidSyntax( get() );
        
        return valid;
    }


    /**
     * @see Value#compareTo(Object)
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

        if ( value instanceof ServerStringValue )
        {
            ServerStringValue stringValue = ( ServerStringValue ) value;
            
            // Normalizes the compared value
            try
            {
                stringValue.normalize();
            }
            catch ( LdapException ne )
            {
                String message = I18n.err( I18n.ERR_04447, stringValue ); 
                LOG.error( message );
            }
            
            // Normalizes the value
            try
            {
                normalize();
            }
            catch ( LdapException ne )
            {
                String message = I18n.err( I18n.ERR_04447, this );
                LOG.error( message );
            }

            try
            {
                //noinspection unchecked
                return getLdapComparator().compare( getNormalizedValue(), stringValue.getNormalizedValue() );
            }
            catch ( LdapException e )
            {
                String msg = I18n.err( I18n.ERR_04443, this, value );
                LOG.error( msg, e );
                throw new IllegalStateException( msg, e );
            }
        }

        String message = I18n.err( I18n.ERR_04448 );
        LOG.error( message );
        throw new NotImplementedException( message );
    }


    // -----------------------------------------------------------------------
    // Object Methods
    // -----------------------------------------------------------------------
    /**
     * Checks to see if this ServerStringValue equals the supplied object.
     *
     * This equals implementation overrides the StringValue implementation which
     * is not schema aware.
     * 
     * Two ServerStringValues are equal if they have the same AttributeType,
     * they are both null, their value are equal or their normalized value 
     * are equal. If the AttributeType has a comparator, we use it to
     * compare both values.
     * @throws IllegalStateException on failures to extract the comparator, or the
     * normalizers needed to perform the required comparisons based on the schema
     */
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        
        if ( ! ( obj instanceof ServerStringValue ) )
        {
            return false;
        }

        ServerStringValue other = ( ServerStringValue ) obj;
        
        if ( !attributeType.equals( other.attributeType ) )
        {
            return false;
        }
        
        if ( isNull() )
        {
            return other.isNull();
        }

        // Shortcut : compare the values without normalization
        // If they are equal, we may avoid a normalization.
        // Note : if two values are equal, then their normalized
        // value are equal too if their attributeType are equal. 
        if ( get().equals( other.get() ) )
        {
            return true;
        }
        else 
        {
            try
            {
                LdapComparator<? super Object> comparator = getLdapComparator();

                // Compare normalized values
                if ( comparator == null )
                {
                    return getNormalizedValue().equals( other.getNormalizedValue() );
                }
                else
                {
                    if ( isNormalized() )
                    {
                        return comparator.compare( getNormalizedValue(), other.getNormalizedValue() ) == 0;
                    }
                    else
                    {
                        Normalizer normalizer = attributeType.getEquality().getNormalizer();
                        return comparator.compare( normalizer.normalize( get() ), normalizer.normalize( other.get() ) ) == 0;
                    }
                }
            }
            catch ( LdapException ne )
            {
                return false;
            }
        }
    }
}
