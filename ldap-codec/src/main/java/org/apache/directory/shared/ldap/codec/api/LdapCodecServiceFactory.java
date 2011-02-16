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
package org.apache.directory.shared.ldap.codec.api;


import org.apache.directory.shared.ldap.codec.osgi.DefaultLdapCodecService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A factory that allows callers a means to get a handle on an LdapCodecService
 * implementation regardless of the environment in which they're accessing it.
 * In an OSGi environment, the BundleActivator binds the LdapCodecService 
 * class member forever to the {@link DefaultLdapCodecService}. If in 
 * 
 * In a standard standalone mode, the Bundle
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdapCodecServiceFactory
{
    /** Logger for this class */
    private static final Logger LOG = LoggerFactory.getLogger( LdapCodecServiceFactory.class );
    
    /** The LdapCodecService singleton bound to this factory */
    private static LdapCodecService ldapCodecService;
    
    /** Whether or not the standalone implementation is being used */
    private static boolean usingStandaloneImplementation;
    
    
    /**
     * Checks to see if the factory is initialized.
     *
     * @return true if initialized, false otherwise
     */
    public static boolean isInitialized()
    {
        return ldapCodecService != null;
    }
    
    
    /**
     * Checks to see if the factory is using the standalone implementation.
     *
     * @return true if using the standalone implementation, false otherwise.
     */
    public static boolean isUsingStandaloneImplementation()
    {
        if ( ! isInitialized() )
        {
            String msg = "Not initialized yet!";
            LOG.error( msg );
            throw new IllegalStateException( msg );
        }
        
        return usingStandaloneImplementation;
    }
    
    
    /**
     * Gets the singleton instance of the LdapCodecService.
     *
     * @return a valid instance implementation based on environment and the 
     * availability of bindings.
     */
    public static LdapCodecService getSingleton()
    {
        if ( ldapCodecService == null )
        {
            initialize( null );
        }
        
        return ldapCodecService;
    }
    
    
    /**
     * Initialization can only take place once. There after an exception 
     * results.
     * 
     * @param ldapCodecService The LDAP Codec Service to initialize with.
     */
    public static void initialize( LdapCodecService ldapCodecService )
    {
        /*
         * If the class member is already set we have problems.
         */
        
        if ( LdapCodecServiceFactory.ldapCodecService != null )
        {
            StringBuilder sb = new StringBuilder( "The LdapCodecService is already set to an instance of " );
            sb.append( LdapCodecServiceFactory.class.getName() );
            LOG.error( sb.toString() );
            throw new IllegalStateException( sb.toString() );
        }

        
        /*
         * If the argument is null, then we attempt discovery
         */

        if ( ldapCodecService == null )
        {
            try
            {
                @SuppressWarnings("unchecked")
                Class<? extends LdapCodecService> serviceClass = ( Class<? extends LdapCodecService> ) 
                    Class.forName( "org.apache.directory.shared.ldap.codec.standalone.StandaloneLdapCodecService" );
                LdapCodecServiceFactory.ldapCodecService = serviceClass.newInstance();
                usingStandaloneImplementation = true;
            }
            catch ( Exception e )
            {
                LOG.error( "Failed to instantiate a viable instance, instantiating new instance of ", e );
                
            }
        }
        else
        {
            usingStandaloneImplementation = false;
            LdapCodecServiceFactory.ldapCodecService = ldapCodecService;
        }
    }
}
