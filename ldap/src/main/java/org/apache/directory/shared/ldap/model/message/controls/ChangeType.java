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
package org.apache.directory.shared.ldap.model.message.controls;


import org.apache.directory.shared.i18n.I18n;


/**
 * Enumeration type for entry changes associates with the persistent search
 * control and the entry change control. Used for the following ASN1
 * enumeration:
 * 
 * <pre>
 *   changeType ENUMERATED 
 *   {
 *       add             (1),
 *       delete          (2),
 *       modify          (4),
 *       modDN           (8)
 *   }
 * </pre>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum ChangeType
{
    ADD(1),

    DELETE(2),

    MODIFY(4),

    MODDN(8);

    private int value;


    /**
     * 
     * Creates a new instance of ChangeType.
     *
     * @param value
     */
    private ChangeType(int value)
    {
        this.value = value;
    }


    /**
     * @return The int value of the ChangeType
     */
    public int getValue()
    {
        return value;
    }


    /**
     * Gets the changeType enumeration type for an integer value.
     * 
     * @param value the value to get the enumeration for
     * @return the enueration type for the value if the value is valid
     * @throws IllegalArgumentException if the value is undefined
     */
    public static ChangeType getChangeType( int value )
    {
        switch ( value )
        {
            case ( 1 ):
                return ADD;
            case ( 2 ):
                return DELETE;
            case ( 4 ):
                return MODIFY;
            case ( 8 ):
                return MODDN;
            default:
                throw new IllegalArgumentException( I18n.err( I18n.ERR_04055, value ) );
        }
    }
}
