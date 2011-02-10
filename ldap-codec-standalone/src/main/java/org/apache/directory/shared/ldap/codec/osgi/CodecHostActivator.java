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
package org.apache.directory.shared.ldap.codec.osgi;


import java.io.File;
import java.io.FileFilter;

import org.apache.directory.shared.ldap.codec.api.DefaultLdapCodecService;
import org.apache.directory.shared.ldap.codec.api.LdapCodecService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;


/**
 * The {@link BundleActivator} for the codec. This implementation class is 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CodecHostActivator implements BundleActivator
{
    private DefaultLdapCodecService codec; 
    private ServiceRegistration registration;
    private BundleContext bundleContext;
    
    
    public CodecHostActivator( DefaultLdapCodecService codec )
    {
        this.codec = codec;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public void start( BundleContext bundleContext ) throws Exception
    {
        this.bundleContext = bundleContext;
        
        registration = bundleContext.registerService( LdapCodecService.class.getName(), codec, null );
        
        if ( codec.getPluginDirectory() != null )
        {
            File[] files = codec.getPluginDirectory().listFiles( new FileFilter()
            {
                public boolean accept( File pathname )
                {
                    return 
                        pathname.canRead()
                        &&
                        pathname.isFile() 
                        && 
                        pathname.getAbsolutePath().endsWith( ".jar" );
                }
            });
            
            for ( File file : files )
            {
                Bundle bundle = bundleContext.installBundle( file.toURI().toURL().toExternalForm() );
                bundle.start();
            }
        }
    }
    

    /**
     * {@inheritDoc}
     */
    public void stop( BundleContext bundleContext ) throws Exception
    {
        registration.unregister();
        
        this.bundleContext = null;
    }
    
    
    /**
     * Gets the Bundles installed.
     * 
     * @return The Bundles installed.
     */
    public Bundle[] getBundles()
    {
        if ( bundleContext != null )
        {
            return bundleContext.getBundles();
        }
        
        return null;
    }
}
