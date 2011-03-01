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
package org.apache.directory.shared.ldap.extras.extended;


import org.apache.directory.shared.util.Strings;


/**
 * Bean for representing a Stored Procedure Parameter
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StoredProcedureParameter
{
    /** the type of the parameter */
    private byte[] type;
    /** the value of the parameter */
    private byte[] value;


    /**
     * Gets the type as a UTF8 String.
     *
     * @return The type as a UTF8 String.
     */
    public String getTypeString()
    {
        return Strings.utf8ToString( type );
    }
    
    
    /**
     * Gets the value as a UTF8 String.
     *
     * @return The value as a UTF8 String.
     */
    public String getValueString()
    {
        return Strings.utf8ToString( value );
    }
    
    
    /**
     * Gets the type as a byte[].
     *
     * @return The type as a byte[].
     */
    public byte[] getType()
    {
        if ( type == null )
        {
            return null;
        }

        final byte[] copy = new byte[ type.length ];
        System.arraycopy( type, 0, copy, 0, type.length );
        return copy;
    }


    /**
     * Sets the type.
     * 
     * @param type The type as a byte[].
     */
    public void setType( byte[] type )
    {
        if ( type != null )
        {
            this.type = new byte[ type.length ];
            System.arraycopy( type, 0, this.type, 0, type.length );
        } 
        else 
        {
            this.type = null;
        }
    }


    /**
     * Gets the value as a byte[].
     *
     * @return The value as a byte[].
     */
    public byte[] getValue()
    {
        if ( value == null )
        {
            return null;
        }

        final byte[] copy = new byte[ value.length ];
        System.arraycopy( value, 0, copy, 0, value.length );
        return copy;
    }


    /**
     * Sets the value.
     * 
     * @param value The value as a byte[].
     */
    public void setValue( byte[] value )
    {
        if ( value != null )
        {
            this.value = new byte[ value.length ];
            System.arraycopy( value, 0, this.value, 0, value.length );
        } 
        else 
        {
            this.value = null;
        }
    }
}