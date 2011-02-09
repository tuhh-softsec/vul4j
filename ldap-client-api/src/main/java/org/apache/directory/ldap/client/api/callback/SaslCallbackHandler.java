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

package org.apache.directory.ldap.client.api.callback;


import java.io.IOException;
import java.text.MessageFormat;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.RealmCallback;
import javax.security.sasl.RealmChoiceCallback;

import org.apache.directory.ldap.client.api.SaslRequest;
import org.apache.directory.shared.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The CallbackHandler implementation used by the LdapConnection during SASL mechanism based bind operations.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SaslCallbackHandler implements CallbackHandler
{

    /** The sasl request. */
    private SaslRequest saslReq;

    /** The logger. */
    private static final Logger LOG = LoggerFactory.getLogger( SaslCallbackHandler.class );


    /**
     * Instantiates a new SASL callback handler.
     *
     * @param saslReq the SASL request
     */
    public SaslCallbackHandler( SaslRequest saslReq )
    {
        this.saslReq = saslReq;
    }


    /**
     * {@inheritDoc}
     */
    public void handle( Callback[] callbacks ) throws IOException, UnsupportedCallbackException
    {
        for ( Callback cb : callbacks )
        {
            if ( cb instanceof NameCallback )
            {
                NameCallback ncb = ( NameCallback ) cb;

                String name = saslReq.getUsername();
                LOG.debug( "sending name {} in the NameCallback", name );
                ncb.setName( name );
            }
            else if ( cb instanceof PasswordCallback )
            {
                PasswordCallback pcb = ( PasswordCallback ) cb;

                LOG.debug( "sending credentials in the PasswordCallback" );
                pcb.setPassword( Strings.utf8ToString( saslReq.getCredentials() ).toCharArray() );
            }
            else if ( cb instanceof RealmCallback )
            {
                RealmCallback rcb = ( RealmCallback ) cb;

                if ( saslReq.getRealmName() != null )
                {
                    LOG.debug( "sending the user specified realm value {} in the RealmCallback", saslReq.getRealmName() );
                    rcb.setText( saslReq.getRealmName() );
                }
                else
                {
                    LOG.debug(
                        "No user specified relam value, sending the default realm value {} in the RealmCallback",
                        rcb.getDefaultText() );
                    rcb.setText( rcb.getDefaultText() );
                }
            }
            else if ( cb instanceof RealmChoiceCallback )
            {
                RealmChoiceCallback rccb = ( RealmChoiceCallback ) cb;

                boolean foundRealmName = false;

                String[] realmNames = rccb.getChoices();
                for ( int i = 0; i < realmNames.length; i++ )
                {
                    String realmName = realmNames[i];
                    if ( realmName.equals( saslReq.getRealmName() ) )
                    {
                        foundRealmName = true;

                        LOG.debug( "sending the user specified realm value {} in the RealmChoiceCallback", realmName );
                        rccb.setSelectedIndex( i );
                        break;
                    }
                }

                if ( !foundRealmName )
                {
                    throw new IOException(
                        MessageFormat
                            .format(
                                "Cannot match ''java.naming.security.sasl.realm'' property value ''{0}'' with choices ''{1}'' in RealmChoiceCallback.",
                                saslReq.getRealmName(), getRealmNamesAsString( realmNames ) ) );
                }
            }
        }
    }


    /**
     * Gets the realm names as a string.
     *
     * @param realmNames the array of realm names
     * @return  the realm names as a string
     */
    private String getRealmNamesAsString( String[] realmNames )
    {
        StringBuilder sb = new StringBuilder();

        if ( ( realmNames != null ) && ( realmNames.length > 0 ) )
        {
            for ( String realmName : realmNames )
            {
                sb.append( realmName );
                sb.append( ',' );
            }
            sb.deleteCharAt( sb.length() - 1 );
        }

        return sb.toString();
    }
}
