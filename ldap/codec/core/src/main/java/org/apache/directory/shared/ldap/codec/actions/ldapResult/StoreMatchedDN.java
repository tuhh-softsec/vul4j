/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package org.apache.directory.shared.ldap.codec.actions.ldapResult;


import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.api.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.api.MessageDecorator;
import org.apache.directory.shared.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.model.message.LdapResult;
import org.apache.directory.shared.ldap.model.message.Message;
import org.apache.directory.shared.ldap.model.message.ResultCodeEnum;
import org.apache.directory.shared.ldap.model.message.ResultResponse;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.shared.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The action used to set the LdapResult matched Dn.
 *
 * <pre>
 * LDAPResult ::= SEQUENCE {
 *     ...
 *     matchedDN LDAPDN,
 *     ...
 * </pre>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StoreMatchedDN extends GrammarAction<LdapMessageContainer<MessageDecorator<? extends Message>>>
{
    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger( StoreMatchedDN.class );

    /** Speedup for logs */
    private static final boolean IS_DEBUG = LOG.isDebugEnabled();


    /**
     * Instantiates a new matched dn action.
     */
    public StoreMatchedDN()
    {
        super( "Store matched Dn" );
    }


    /**
     * {@inheritDoc}
     */
    public void action( LdapMessageContainer<MessageDecorator<? extends Message>> container ) throws DecoderException
    {
        // Get the Value and store it in the BindResponse
        TLV tlv = container.getCurrentTLV();
        Dn matchedDn = null;
        ResultCodeEnum resultCode = null;

        ResultResponse response = ( ResultResponse ) container.getMessage();
        LdapResult ldapResult = response.getLdapResult();
        resultCode = ldapResult.getResultCode();

        // We have to handle the special case of a 0 length matched
        // Dn
        if ( tlv.getLength() == 0 )
        {
            matchedDn = Dn.EMPTY_DN;
        }
        else
        {
            // A not null matchedDn is valid for resultCodes
            // NoSuchObject, AliasProblem, InvalidDNSyntax and
            // AliasDreferencingProblem.

            switch ( resultCode )
            {
                case NO_SUCH_OBJECT:
                case ALIAS_PROBLEM:
                case INVALID_DN_SYNTAX:
                case ALIAS_DEREFERENCING_PROBLEM:
                    byte[] dnBytes = tlv.getValue().getData();
                    String dnStr = Strings.utf8ToString(dnBytes);

                    try
                    {
                        matchedDn = new Dn( dnStr );
                    }
                    catch ( LdapInvalidDnException ine )
                    {
                        // This is for the client side. We will never decode LdapResult on the server
                        String msg = I18n.err( I18n.ERR_04013, dnStr, Strings.dumpBytes(dnBytes), ine
                            .getLocalizedMessage() );
                        LOG.error( msg );

                        throw new DecoderException( I18n.err( I18n.ERR_04014, ine.getLocalizedMessage() ) );
                    }

                    break;

                default:
                    LOG.warn( "The matched Dn should not be set when the result code is one of NoSuchObject,"
                        + " AliasProblem, InvalidDNSyntax or AliasDreferencingProblem" );

                    matchedDn = Dn.EMPTY_DN;
                    break;
            }
        }

        if ( IS_DEBUG )
        {
            LOG.debug( "The matchedDn is " + matchedDn);
        }

        ldapResult.setMatchedDn(matchedDn);
    }
}
