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
package org.apache.directory.shared.ldap.extras.controls.ppolicy_impl;


import org.apache.directory.shared.ldap.codec.IControlFactory;
import org.apache.directory.shared.ldap.codec.ILdapCodecService;
import org.apache.directory.shared.ldap.extras.controls.PasswordPolicy;
import org.apache.directory.shared.ldap.extras.controls.PasswordPolicyImpl;


/**
 * A {@link IControlFactory} which creates {@link PasswordPolicy} controls.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class PasswordPolicyFactory implements IControlFactory<PasswordPolicy, PasswordPolicyDecorator>
{
    
    private ILdapCodecService codec;
    

    /**
     * Creates a new instance of PasswordPolicyFactory.
     *
     */
    public PasswordPolicyFactory( ILdapCodecService codec )
    {
        this.codec = codec;
    }
    

    /**
     * 
     * {@inheritDoc}
     */
    public String getOid()
    {
        return PasswordPolicy.OID;
    }

    
    /**
     * 
     * {@inheritDoc}
     */
    public PasswordPolicyDecorator newCodecControl()
    {
        return new PasswordPolicyDecorator( codec );
    }
    

    /**
     * 
     * {@inheritDoc}
     */
    public PasswordPolicyDecorator newCodecControl( PasswordPolicy control )
    {
        PasswordPolicyDecorator decorator = null;
        
        // protect against double decoration
        if ( control instanceof PasswordPolicyDecorator )
        {
            decorator = ( PasswordPolicyDecorator ) control;
        }
        else
        {
            decorator = new PasswordPolicyDecorator( codec, control );
        }
        
        return decorator;
    }

    
    /**
     * 
     * {@inheritDoc}
     */
    public PasswordPolicy newControl()
    {
        return new PasswordPolicyImpl();
    }
}
