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

package org.apache.directory.ldap.client.api;


import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A class to hold the configuration for creating an LdapConnection.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdapConnectionConfig
{

    /** Default ports for LDAP */
    public static final int DEFAULT_LDAP_PORT = 389;

    /** Default port for LDAPS */
    public static final int DEFAULT_LDAPS_PORT = 636;

    /** The default host : localhost */
    public static final String DEFAULT_LDAP_HOST = "127.0.0.1";

    /** The LDAP version */
    public static final int LDAP_V3 = 3;

    /** The default timeout for operation : 30 seconds */
    public static final long DEFAULT_TIMEOUT = 30000L;

    /** the default protocol used for creating SSL context */
    public static final String DEFAULT_SSL_PROTOCOL = "TLS";

    // --- private members ----

    /** A flag indicating if we are using SSL or not, default value is false */
    private boolean useSsl = false;

    /** The selected LDAP port */
    private int ldapPort;

    /** the remote LDAP host */
    private String ldapHost;

    /** a valid Dn to authenticate the user */
    private String name;

    /** user's credentials ( current implementation supports password only); it must be a non-null value */
    private String credentials;

    /** an array of key managers, if set, will be used while initializing the SSL context */
    private KeyManager[] keyManagers;

    /** an instance of SecureRandom, if set, will be used while initializing the SSL context */
    private SecureRandom secureRandom;

    /** an array of certificate trust managers, if set, will be used while initializing the SSL context */
    private TrustManager[] trustManagers;

    /** name of the protocol used for creating SSL context, default value is "TLS" */
    private String sslProtocol = DEFAULT_SSL_PROTOCOL;

    private static final Logger LOG = LoggerFactory.getLogger( LdapConnectionConfig.class );
    
    public LdapConnectionConfig()
    {
        setDefaultTrustManager();
    }

    
    /**
     * sets the default trust manager based on the SunX509 trustManagement algorithm
     */
    private void setDefaultTrustManager()
    {
        String trustMgmtAlgo = "SunX509";

        try
        {
            TrustManagerFactory tmFactory = TrustManagerFactory.getInstance( trustMgmtAlgo );
            tmFactory.init( KeyStore.getInstance( KeyStore.getDefaultType() ) );

            TrustManager factoryTrustManagers[] = tmFactory.getTrustManagers();

            for ( int i = 0; i < factoryTrustManagers.length; i++ )
            {
                if ( factoryTrustManagers[i] instanceof X509TrustManager )
                {
                    trustManagers = new TrustManager[] { factoryTrustManagers[i] };
                    LOG.debug( "found X509TrustManager {}", factoryTrustManagers[i] );
                    break;
                }
            }
        }
        catch( Exception e )
        {
            LOG.warn( "couldn't find any default X509 TrustManager with algorithm {}", trustMgmtAlgo );
        }
    }
    
    
    /**
     * Checks if SSL (ldaps://) is used.
     *
     * @return true, if SSL is used
     */
    public boolean isUseSsl()
    {
        return useSsl;
    }


    /**
     * Sets whether SSL should be used.
     *
     * @param useSsl true to use SSL
     */
    public void setUseSsl( boolean useSsl )
    {
        this.useSsl = useSsl;
    }


    /**
     * Gets the LDAP port.
     *
     * @return the LDAP port
     */
    public int getLdapPort()
    {
        return ldapPort;
    }


    /**
     * Sets the LDAP port.
     *
     * @param ldapPort the new LDAP port
     */
    public void setLdapPort( int ldapPort )
    {
        this.ldapPort = ldapPort;
    }


    /**
     * Gets the LDAP host.
     *
     * @return the LDAP host
     */
    public String getLdapHost()
    {
        return ldapHost;
    }


    /**
     * Sets the LDAP host.
     *
     * @param ldapHost the new LDAP host
     */
    public void setLdapHost( String ldapHost )
    {
        this.ldapHost = ldapHost;
    }


    /**
     * Gets the name that is used to authenticate the user.
     *
     * @return the name
     */
    public String getName()
    {
        return name;
    }


    /**
     * Sets the name which is used to authenticate the user.
     *
     * @param name the new name
     */
    public void setName( String name )
    {
        this.name = name;
    }


    /**
     * Gets the credentials.
     *
     * @return the credentials
     */
    public String getCredentials()
    {
        return credentials;
    }


    /**
     * Sets the credentials.
     *
     * @param credentials the new credentials
     */
    public void setCredentials( String credentials )
    {
        this.credentials = credentials;
    }


    /**
     * Gets the default LDAP port.
     *
     * @return the default LDAP port
     */
    public int getDefaultLdapPort()
    {
        return DEFAULT_LDAP_PORT;
    }


    /**
     * Gets the default LDAPS port.
     *
     * @return the default LDAPS port
     */
    public int getDefaultLdapsPort()
    {
        return DEFAULT_LDAPS_PORT;
    }


    /**
     * Gets the default LDAP host.
     *
     * @return the default LDAP host
     */
    public String getDefaultLdapHost()
    {
        return DEFAULT_LDAP_HOST;
    }


    /**
     * Gets the default timeout.
     *
     * @return the default timeout
     */
    public long getDefaultTimeout()
    {
        return DEFAULT_TIMEOUT;
    }


    /**
     * Gets the supported LDAP version.
     *
     * @return the supported LDAP version
     */
    public int getSupportedLdapVersion()
    {
        return LDAP_V3;
    }


    /**
     * Gets the trust managers.
     *
     * @return the trust managers
     */
    public TrustManager[] getTrustManagers()
    {
        return trustManagers;
    }


    /**
     * Sets the trust managers.
     *
     * @param trustManagers the new trust managers
     */
    public void setTrustManagers( TrustManager... trustManagers )
    {
        this.trustManagers = trustManagers;
    }


    /**
     * Gets the SSL protocol.
     *
     * @return the SSL protocol
     */
    public String getSslProtocol()
    {
        return sslProtocol;
    }


    /**
     * Sets the SSL protocol.
     *
     * @param sslProtocol the new SSL protocol
     */
    public void setSslProtocol( String sslProtocol )
    {
        this.sslProtocol = sslProtocol;
    }


    /**
     * Gets the key managers.
     *
     * @return the key managers
     */
    public KeyManager[] getKeyManagers()
    {
        return keyManagers;
    }


    /**
     * Sets the key managers.
     *
     * @param keyManagers the new key managers
     */
    public void setKeyManagers( KeyManager[] keyManagers )
    {
        this.keyManagers = keyManagers;
    }


    /**
     * Gets the secure random.
     *
     * @return the secure random
     */
    public SecureRandom getSecureRandom()
    {
        return secureRandom;
    }


    /**
     * Sets the secure random.
     *
     * @param secureRandom the new secure random
     */
    public void setSecureRandom( SecureRandom secureRandom )
    {
        this.secureRandom = secureRandom;
    }

}
