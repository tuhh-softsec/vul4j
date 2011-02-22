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
package org.apache.directory.shared.ldap.model.name;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.apache.directory.shared.util.Strings;
import org.apache.directory.shared.util.Unicode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A helper class which serialize and deserialize a Rdn
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public final class RdnSerializer
{
    /** The LoggerFactory used by this class */
    protected static final Logger LOG = LoggerFactory.getLogger( RdnSerializer.class );


    /**
     * Private constructor.
     */
    private RdnSerializer()
    {
    }


    /**
     * Serialize a Rdn instance
     * 
     * A Rdn is composed of on to many ATAVs (AttributeType And Value).
     * We should write all those ATAVs sequencially, following the 
     * structure :
     * 
     * <li>nbAtavs</li> The number of ATAVs to write. Can't be 0.
     * <li>upName</li> The User provided Rdn
     * <li>normName</li> The normalized Rdn. It can be empty if the normalized
     * name equals the upName.
     * <li>atavs</li>
     * <p>
     * For each ATAV :<p>
     * <li>start</li> The position of this ATAV in the upName string
     * <li>length</li> The ATAV user provided length
     * <li>Call the ATAV write method</li> The ATAV itself
     *  
     * @param rdn The Rdn to serialize
     * @param out the stream in which the Rdn will be serialized
     * @throws IOException If we can't write in this stream
     */
    public static void serialize( Rdn rdn, ObjectOutput out ) throws IOException
    {
        out.writeInt( rdn.getNbAtavs() );
        Unicode.writeUTF(out, rdn.getName());
        Unicode.writeUTF(out, rdn.getNormName());
        
        switch ( rdn.getNbAtavs() )
        {
            case 0 :
                break;

            case 1 :
                AvaSerializer.serialize(rdn.getAVA(), out);
                break;
                
            default :
                for ( Ava atav:rdn )
                {
                    AvaSerializer.serialize(atav, out);
                }
            
                break;
        }
    }
    
    
    /**
     * Deserialize a Rdn instance
     * 
     * We read back the data to create a new RDB. The structure 
     * read is exposed in the {@link Rdn#writeExternal(ObjectOutput)}
     * method<p>
     * 
     * @param in The input stream from which the Rdn is read
     * @return a deserialized Rdn
     * @throws IOException If the stream can't be read
     */
    public static Rdn deserialize( ObjectInput in ) throws IOException
    {
        // Read the ATAV number
        int nbAtavs = in.readInt();
        
        // Read the UPName
        String upName = Unicode.readUTF(in);
        
        // Read the normName
        String normName = Unicode.readUTF(in);
        
        if ( Strings.isEmpty(normName) )
        {
            normName = upName;
        }
        
        // Now creates the Rdn
        Rdn rdn = new Rdn( 0, 0, upName, normName );

        // Read through the Atavs
        switch ( nbAtavs )
        {
            case 0 :
                return rdn;
                
            case 1 :
                Ava atav = AvaSerializer.deserialize(in);
                
                rdn.addAVA( atav );

                return rdn;
                
            default :
                for ( int i = 0; i < nbAtavs; i++  )
                {
                    atav = AvaSerializer.deserialize(in);
                    rdn.addAVA( atav );
                }
            
                return rdn;
        }
    }
}
