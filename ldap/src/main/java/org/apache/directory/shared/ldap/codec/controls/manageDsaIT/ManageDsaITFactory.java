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
package org.apache.directory.shared.ldap.codec.controls.manageDsaIT;


import org.apache.directory.shared.ldap.codec.IControlFactory;
import org.apache.directory.shared.ldap.codec.ILdapCodecService;
import org.apache.directory.shared.ldap.model.message.controls.ManageDsaIT;
import org.apache.directory.shared.ldap.model.message.controls.ManageDsaITImpl;


/**
 * A codec {@link IControlFactory} implementation for {@link ManageDsaIT} control.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ManageDsaITFactory implements IControlFactory<ManageDsaIT, ManageDsaITDecorator>
{
    /** The LDAP codec responsible for encoding and decoding Cascade Controls */
    private ILdapCodecService codec;
    
    
    /**
     * Creates a new instance of CascadeFactory.
     *
     * @param codec The LDAP codec
     */
    public ManageDsaITFactory( ILdapCodecService codec )
    {
        this.codec = codec;
    }

    
    /**
     * {@inheritDoc}
     */
    public String getOid()
    {
        return ManageDsaIT.OID;
    }

    
    /**
     * {@inheritDoc}
     */
    public ManageDsaITDecorator newCodecControl()
    {
        return new ManageDsaITDecorator( codec, new ManageDsaITImpl() );
    }
    

    /**
     * {@inheritDoc}
     */
    public ManageDsaITDecorator decorate( ManageDsaIT control )
    {
        return new ManageDsaITDecorator( codec, control );
    }
    

    /**
     * {@inheritDoc}
     */
    public ManageDsaIT newControl()
    {
        return new ManageDsaITImpl();
    }
}
