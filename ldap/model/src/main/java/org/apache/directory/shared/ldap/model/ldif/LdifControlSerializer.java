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
package org.apache.directory.shared.ldap.model.ldif;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A helper class which serialize and deserialize a LdifControl.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdifControlSerializer
{
    /** The LoggerFactory used by this class */
    protected static final Logger LOG = LoggerFactory.getLogger( LdifControlSerializer.class );

    /**
     * Private constructor.
     */
    private LdifControlSerializer()
    {
    }

    
    /**
     * Serializes a LdifControl instance.
     * 
     * @param principal The LdifControl instance to serialize
     * @param out The stream into which we will write the serialized instance
     * @throws IOException If the stream can't be written
     */
    public static void serialize( LdifControl ldifControl, ObjectOutput out ) throws IOException
    {
        // The OID
        out.writeUTF( ldifControl.getOid() );
        
        // The criticality
        out.writeBoolean( ldifControl.isCritical() );
        
        // The value if any
        if ( ldifControl.hasValue() )
        {
            out.writeInt( ldifControl.getValue().length );
            out.write( ldifControl.getValue() );
        }
        else
        {
            out.writeInt( -1 );
        }
        
        out.flush();
    }
    
    
    /**
     * Deserializes a LdifControl instance.
     * 
     * @param in The input stream from which the LdifControl is read
     * @return a deserialized LdifControl
     * @throws IOException If the stream can't be read
     */
    public static LdifControl deserialize( ObjectInput in ) throws IOException
    {
        // The OID
        String oid = in.readUTF();
        LdifControl ldifControl = new LdifControl( oid );
        
        // The criticality
        ldifControl.setCritical( in.readBoolean() );
        
        int valueSize = in.readInt();
        
        if ( valueSize >=0 )
        {
            byte[] value = new byte[ valueSize ];
            in.read( value );
            ldifControl.setValue( value );
        }

        return ldifControl;
    }
}
