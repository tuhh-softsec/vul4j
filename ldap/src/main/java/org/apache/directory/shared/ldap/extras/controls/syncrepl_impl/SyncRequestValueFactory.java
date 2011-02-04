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
package org.apache.directory.shared.ldap.extras.controls.syncrepl_impl;


import org.apache.directory.shared.ldap.codec.IControlFactory;
import org.apache.directory.shared.ldap.codec.ILdapCodecService;
import org.apache.directory.shared.ldap.extras.controls.SyncRequestValue;


/**
 * A {@link IControlFactory} which creates {@link SyncRequestValue} controls.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SyncRequestValueFactory implements IControlFactory<SyncRequestValue, SyncRequestValueDecorator>
{
    /** The codec for this factory */
    private ILdapCodecService codec;
    

    /**
     * Creates a new instance of SyncRequestValueFactory.
     *
     * @param codec The codec for this factory.
     */
    public SyncRequestValueFactory( ILdapCodecService codec )
    {
        this.codec = codec;
    }
    

    /**
     * {@inheritDoc}
     */
    public String getOid()
    {
        return SyncRequestValue.OID;
    }

    
    /**
     * {@inheritDoc}
     */
    public SyncRequestValueDecorator newCodecControl()
    {
        return new SyncRequestValueDecorator( codec );
    }
    

    /**
     * {@inheritDoc}
     */
    public SyncRequestValueDecorator newCodecControl( SyncRequestValue control )
    {
        return new SyncRequestValueDecorator( codec, control );
    }
}
