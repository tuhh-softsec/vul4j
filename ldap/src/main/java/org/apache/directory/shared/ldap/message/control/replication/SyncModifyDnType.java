/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */

package org.apache.directory.shared.ldap.message.control.replication;

/**
 * TODO SyncModifyDnControlEnum.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum SyncModifyDnType
{
    MOVE( 0 ),
    RENAME( 1 ),
    MOVEANDRENAME( 2 );
    
    /** Internal value for each tag */
    private int value;
    
    private SyncModifyDnType( int value )
    {
        this.value = value;
    }
    
    
    /**
     * @return The value associated with the current element.
     */
    public int getValue()
    {
        return value;
    }
    
    
    public static SyncModifyDnType getModifyDnType( int value )
    {
        switch( value )
        {
            case 0 : return MOVE;
            
            case 1 : return RENAME;
            
            case 2 : return MOVEANDRENAME;
        }
        
        throw new IllegalArgumentException( "unknown modify dn operantion type " + value );
    }
}
