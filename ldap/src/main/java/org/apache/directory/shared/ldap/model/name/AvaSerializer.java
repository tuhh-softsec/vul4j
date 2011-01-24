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

import org.apache.directory.shared.ldap.model.entry.BinaryValue;
import org.apache.directory.shared.ldap.model.entry.StringValue;
import org.apache.directory.shared.ldap.model.entry.Value;
import org.apache.directory.shared.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.shared.util.Strings;
import org.apache.directory.shared.util.Unicode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A helper class which serialize and deserialize an AttributeTypeAndValue
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public final class AvaSerializer
{
    /** The LoggerFactory used by this class */
    protected static final Logger LOG = LoggerFactory.getLogger( AvaSerializer.class );


    /**
     * Private constructor.
     */
    private AvaSerializer()
    {
    }


    /**
     * Serialize an AttributeTypeAndValue object.
     * 
     * An AttributeTypeAndValue is composed of  a type and a value.
     * The data are stored following the structure :
     * 
     * <li>upName</li> The User provided ATAV
     * <li>start</li> The position of this ATAV in the Dn
     * <li>length</li> The ATAV length
     * <li>upType</li> The user Provided Type
     * <li>normType</li> The normalized AttributeType
     * <li>isHR<li> Tells if the value is a String or not
     * <p>
     * if the value is a String :
     * <li>upValue</li> The User Provided value.
     * <li>value</li> The normalized value.
     * <p>
     * if the value is binary :
     * <li>upValueLength</li>
     * <li>upValue</li> The User Provided value.
     * <li>valueLength</li>
     * <li>value</li> The normalized value.
     *
     * @param atav the AttributeTypeAndValue to serialize
     * @param out the OutputStream in which the atav will be serialized
     * @throws IOException If we can't serialize the atav
     */
    public static void serialize( Ava atav, ObjectOutput out ) throws IOException
    {
        if ( Strings.isEmpty(atav.getUpName())
            || Strings.isEmpty(atav.getUpType())
            || Strings.isEmpty(atav.getNormType())
            || ( atav.getStart() < 0 )
            || ( atav.getLength() < 2 ) // At least a type and '='
            || ( atav.getUpValue().isNull() )
            || ( atav.getNormValue().isNull() ) )
        {
            String message = "Cannot serialize an wrong ATAV, ";
            
            if ( Strings.isEmpty(atav.getUpName()) )
            {
                message += "the upName should not be null or empty";
            }
            else if ( Strings.isEmpty(atav.getUpType()) )
            {
                message += "the upType should not be null or empty";
            }
            else if ( Strings.isEmpty(atav.getNormType()) )
            {
                message += "the normType should not be null or empty";
            }
            else if ( atav.getStart() < 0 )
            {
                message += "the start should not be < 0";
            }
            else if ( atav.getLength() < 2 )
            {
                message += "the length should not be < 2";
            }
            else if ( atav.getUpValue().isNull() )
            {
                message += "the upValue should not be null";
            }
            else if ( atav.getNormValue().isNull() )
            {
                message += "the value should not be null";
            }
                
            LOG.error( message );
            throw new IOException( message );
        }
        
        Unicode.writeUTF(out, atav.getUpName());
        out.writeInt( atav.getStart() );
        out.writeInt( atav.getLength() );
        Unicode.writeUTF(out, atav.getUpType());
        Unicode.writeUTF(out, atav.getNormType());
        
        boolean isHR = !atav.getNormValue().isBinary();
        
        out.writeBoolean( isHR );
        
        if ( isHR )
        {
            Unicode.writeUTF(out, atav.getUpValue().getString());
            Unicode.writeUTF(out, atav.getNormValue().getString());
        }
        else
        {
            out.writeInt( atav.getUpValue().length() );
            out.write( atav.getUpValue().getBytes() );
            out.writeInt( atav.getNormValue().length() );
            out.write( atav.getNormValue().getBytes() );
        }
    }
    
    
    /**
     * Deserialize an AttributeTypeAndValue object
     * 
     * We read back the data to create a new ATAV. The structure 
     * read is exposed in the {@link Ava#writeExternal(ObjectOutput)}
     * method<p>
     * 
     * @param in the input stream
     * @throws IOException If the input stream can't be read
     * @return The constructed AttributeTypeAndValue
     */
    public static Ava deserialize( ObjectInput in ) throws IOException
    {
        String upName = Unicode.readUTF(in);
        in.readInt(); // start
        in.readInt(); // length
        String upType = Unicode.readUTF(in);
        String normType = Unicode.readUTF(in);
        
        boolean isHR = in.readBoolean();

        try
        {
            if ( isHR )
            {
                Value<String> upValue = new StringValue( Unicode.readUTF(in) );
                Value<String> normValue = new StringValue( Unicode.readUTF(in) );
                
                Ava atav =
                    new Ava( upType, normType, upValue, normValue, upName );
                
                return atav;
            }
            else
            {
                int upValueLength = in.readInt();
                byte[] upValue = new byte[upValueLength];
                in.readFully( upValue );
    
                int valueLength = in.readInt();
                byte[] normValue = new byte[valueLength];
                in.readFully( normValue );
    
                Ava atav =
                    new Ava( upType, normType,
                        new BinaryValue( upValue) ,
                        new BinaryValue( normValue ), upName );
                
                return atav;
            }
        }
        catch ( LdapInvalidDnException ine )
        {
            throw new IOException( ine.getMessage() );
        }
    }
}
