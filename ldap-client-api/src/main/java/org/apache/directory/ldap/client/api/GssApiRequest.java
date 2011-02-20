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


import javax.security.auth.login.Configuration;

import org.apache.directory.shared.ldap.model.constants.SupportedSaslMechanisms;


/**
 * Holds the data required to complete the GSS-API SASL operation
 *  
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class GssApiRequest extends SaslRequest
{
    /** The KDC host*/
    protected String kdcHost;

    /** The KDC port */
    protected int kdcPort = 0;

    /** The krb5.conf file absolute path */
    protected String krb5ConfFilePath;

    /** The {@link javax.security.auth.login.Configuration} object for Login Module */
    protected Configuration loginModuleConfiguration;


    /**
     * Creates a new instance of GssApiRequest.
     */
    public GssApiRequest()
    {
        super( SupportedSaslMechanisms.GSSAPI );
    }


    /**
     * Gets the {@link javax.security.auth.login.Configuration} object for Login Module.
     *
     * @return the {@link javax.security.auth.login.Configuration} object for Login Module
     */
    public Configuration getLoginModuleConfiguration()
    {
        return loginModuleConfiguration;
    }


    /**
     * Gets the KDC host.
     *
     * @return the KDC host
     */
    public String getKdcHost()
    {
        return kdcHost;
    }


    /**
     * Gets the KDC port.
     *
     * @return the KDC port
     */
    public int getKdcPort()
    {
        return kdcPort;
    }


    /**
     * Gets the (absolute) path to the 'krb5.conf' file.
     *
     * @return the (absolute) path to the 'krb5.conf' file
     */
    public String getKrb5ConfFilePath()
    {
        return krb5ConfFilePath;
    }


    /**
     * Sets the {@link javax.security.auth.login.Configuration} object for Login Module.
     *
     * @param loginModuleConfiguration the {@link javax.security.auth.login.Configuration} object for Login Module
     */
    public void setLoginModuleConfiguration( Configuration loginModuleConfiguration )
    {
        this.loginModuleConfiguration = loginModuleConfiguration;
    }


    /**
     * Sets the KDC host.
     *
     * @param kdcHost the KDC host
     */
    public void setKdcHost( String kdcHost )
    {
        this.kdcHost = kdcHost;
    }


    /**
     * Sets the KDC port.
     *
     * @param kdcPort the KDC port
     */
    public void setKdcPort( int kdcPort )
    {
        this.kdcPort = kdcPort;
    }


    /**
     * Sets the (absolute) path to the 'krb5.conf' file.
     *
     * @param krb5ConfFilePath the (absolute) path to the 'krb5.conf' file
     */
    public void setKrb5ConfFilePath( String krb5ConfFilePath )
    {
        this.krb5ConfFilePath = krb5ConfFilePath;
    }


    /**
     * {@inheritDoc}
     */
    // Overriding the visibility of the method to public
    public void setRealmName( String realmName )
    {
        super.setRealmName( realmName );
    }
}
