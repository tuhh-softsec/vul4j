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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.util.Strings;
import org.apache.directory.shared.util.Unicode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A helper class which serialize and deserialize a Dn
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public final class DnSerializer
{
    /** The LoggerFactory used by this class */
    protected static final Logger LOG = LoggerFactory.getLogger( DnSerializer.class );


    /**
     * Private constructor.
     */
    private DnSerializer()
    {
    }


    /**
     * Serialize a Dn
     *
     * We have to store a Dn data efficiently. Here is the structure :
     *
     * <li>upName</li> The User provided Dn<p>
     * <li>normName</li> May be null if the normName is equivalent to
     * the upName<p>
     * <li>rdns</li> The rdn's List.<p>
     *
     * for each rdn :
     * <li>call the Rdn write method</li>
     *
     * @param dn The Dn to serialize
     * @return a byte[] containing the serialized DN
     * @throws IOException If we can't write in this stream
     */
    public static byte[] serialize( Dn dn ) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );
        
        serialize( dn, out );
        
        out.flush();
        
        return baos.toByteArray();
    }


    /**
     * Serialize a Dn
     *
     * We have to store a Dn data efficiently. Here is the structure :
     *
     * <li>upName</li> The User provided Dn<p>
     * <li>normName</li> May be null if the normName is equivalent to
     * the upName<p>
     * <li>rdns</li> The rdn's List.<p>
     *
     * for each rdn :
     * <li>call the Rdn write method</li>
     *
     * @param dn The Dn to serialize
     * @param out the stream in which the Dn will be serialized
     * @throws IOException If we can't write in this stream
     */
    public static void serialize( Dn dn, ObjectOutput out ) throws IOException
    {
        if ( dn.getName() == null )
        {
            String message = "Cannot serialize a NULL Dn";
            LOG.error( message );
            throw new IOException( message );
        }

        // Write the UPName
        Unicode.writeUTF(out, dn.getName());

        // Write the NormName if different
        if ( dn.isNormalized() )
        {
            if ( dn.getName().equals( dn.getNormName() ) )
            {
                Unicode.writeUTF(out, "");
            }
            else
            {
                Unicode.writeUTF(out, dn.getNormName());
            }
        }
        else
        {
            String message = I18n.err( I18n.ERR_04212, dn );
            LOG.error( message );
            throw new IOException( message );
        }

        // Should we store the byte[] ???

        // Write the RDNs.
        // First the number of RDNs
        out.writeInt( dn.size() );

        // Loop on the RDNs
        for ( Rdn rdn:dn.getRdns() )
        {
            RdnSerializer.serialize( rdn, out );
        }
    }


    /**
     * Deserialize a Dn
     *
     * We read back the data to create a new Dn. The structure
     * read is exposed in the {@link DnSerializer#serialize(Dn, ObjectOutput)}
     * method<p>
     *
     * @param in The input bytes from which the Dn is read
     * @return a deserialized Dn
     * @throws IOException If the stream can't be read
     */
    public static Dn deserialize( byte[] bytes ) throws IOException
    {
        ByteArrayInputStream bais = new ByteArrayInputStream( bytes );
        ObjectInputStream in = new ObjectInputStream( bais );
    
        return deserialize( in );
    }
    

    /**
     * Deserialize a Dn
     *
     * We read back the data to create a new Dn. The structure
     * read is exposed in the {@link DnSerializer#serialize(Dn, ObjectOutput)}
     * method<p>
     *
     * @param in The input stream from which the Dn is read
     * @return a deserialized Dn
     * @throws IOException If the stream can't be read
     */
    public static Dn deserialize( ObjectInput in ) throws IOException
    {
        // Read the UPName
        String upName = Unicode.readUTF(in);

        // Read the NormName
        String normName = Unicode.readUTF(in);

        if ( normName.length() == 0 )
        {
            // As the normName is equal to the upName,
            // we didn't saved the nbnormName on disk.
            // restore it by copying the upName.
            normName = upName;
        }

        // Should we read the byte[] ???
        byte[] bytes = Strings.getBytesUtf8(upName);

        // Read the RDNs. Is it's null, the number will be -1.
        int nbRdns = in.readInt();
        Dn dn = new Dn( upName, normName, bytes );

        for ( int i = 0; i < nbRdns; i++ )
        {
            Rdn rdn = RdnSerializer.deserialize( in );
            dn = dn.addInternal( 0, rdn );
        }

        return dn;
    }
}
