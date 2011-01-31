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
package org.apache.directory.shared.ldap.codec.search.controls.subentries;


import javax.naming.ldap.Control;

import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.ldap.codec.IControlFactory;
import org.apache.directory.shared.ldap.codec.ILdapCodecService;
import org.apache.directory.shared.ldap.model.message.controls.Subentries;
import org.apache.directory.shared.ldap.model.message.controls.SubentriesImpl;


/**
 * A factory for creating {@link Subentries} Controls and their 
 * {@link SubentriesDecorator} objects.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SubentriesFactory implements IControlFactory<Subentries, SubentriesDecorator>
{
    private ILdapCodecService codec;
    
    
    public SubentriesFactory( ILdapCodecService codec )
    {
        this.codec = codec;
    }
    
    public String getOid()
    {
        return Subentries.OID;
    }

    
    public SubentriesDecorator newCodecControl()
    {
        return new SubentriesDecorator( codec );
    }


    public Subentries newControl()
    {
        return new SubentriesImpl();
    }

    
    public SubentriesDecorator decorate( Subentries control )
    {
        if ( ! control.getOid().equals( Subentries.OID ) )
        {
            throw new IllegalArgumentException( "Bad control provided: " + control );
        }
        
        return new SubentriesDecorator( codec, control );
    }
    

    public Control toJndiControl( Subentries control ) throws EncoderException
    {
        return null;
    }

    public Subentries fromJndiControl( Control control ) throws DecoderException
    {
        return null;
    }
}
