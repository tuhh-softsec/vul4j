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
package org.apache.directory.shared.ldap.extras;


import org.apache.directory.shared.ldap.codec.api.ControlFactory;
import org.apache.directory.shared.ldap.codec.api.ExtendedOpFactory;
import org.apache.directory.shared.ldap.codec.api.LdapCodecService;
import org.apache.directory.shared.ldap.extras.controls.ppolicy_impl.PasswordPolicyFactory;
import org.apache.directory.shared.ldap.extras.controls.syncrepl_impl.SyncDoneValueFactory;
import org.apache.directory.shared.ldap.extras.controls.syncrepl_impl.SyncInfoValueFactory;
import org.apache.directory.shared.ldap.extras.controls.syncrepl_impl.SyncModifyDnFactory;
import org.apache.directory.shared.ldap.extras.controls.syncrepl_impl.SyncRequestValueFactory;
import org.apache.directory.shared.ldap.extras.controls.syncrepl_impl.SyncStateValueFactory;
import org.apache.directory.shared.ldap.extras.extended.CancelExtendedOpFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;


/**
 * A BundleActivator for the ldap codec extras extension: extra ApacheDS and 
 * Apache Directory Studio specific controls and extended operations. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ExtrasBundleActivator implements BundleActivator
{
    /**
     * {@inheritDoc}
     */
    public void start( BundleContext context ) throws Exception
    {
        ServiceReference reference = 
            context.getServiceReference( LdapCodecService.class.getName() );
        
        LdapCodecService codec = ( LdapCodecService ) context.getService( reference );
        registerExtrasControls( codec );
        registerExtrasExtendedOps( codec );
    }
    
    
    /**
     * Registers all the extras controls present in this control pack.
     *
     * @param codec The codec service.
     */
    private void registerExtrasControls( LdapCodecService codec )
    {
        ControlFactory<?,?> factory = new SyncDoneValueFactory( codec );
        codec.registerControl( factory );
        
        factory = new SyncInfoValueFactory( codec );
        codec.registerControl( factory );
        
        factory = new SyncModifyDnFactory( codec );
        codec.registerControl( factory );
        
        factory = new SyncRequestValueFactory( codec );
        codec.registerControl( factory );

        factory = new SyncStateValueFactory( codec );
        codec.registerControl( factory );
        
        factory = new PasswordPolicyFactory( codec );
        codec.registerControl( factory );
    }


    /**
     * Registers all the extras extended operations present in this control pack.
     *
     * @param codec The codec service.
     */
    private void registerExtrasExtendedOps( LdapCodecService codec )
    {
        ExtendedOpFactory<?> factory = new CancelExtendedOpFactory();
        codec.registerExtendedOp( factory );
    }
    
    
    
    /**
     * {@inheritDoc}
     */
    public void stop( BundleContext context ) throws Exception
    {
    }
}
