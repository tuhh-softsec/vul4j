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
import org.apache.directory.shared.asn1.ber.tlv.IntegerDecoder;
import org.apache.directory.shared.asn1.ber.tlv.IntegerDecoderException;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.asn1.ber.tlv.Value;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.decorators.MessageDecorator;
import org.apache.directory.shared.ldap.model.message.LdapResult;
import org.apache.directory.shared.ldap.model.message.Message;
import org.apache.directory.shared.ldap.model.message.ResultCodeEnum;
import org.apache.directory.shared.ldap.model.message.ResultResponse;
import org.apache.directory.shared.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The action used to set the LdapResult result code.
 * <pre>
 * LDAPResult ::= SEQUENCE {
 *     resultCode ENUMERATED {
 *         ...
 * </pre>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StoreResultCode extends GrammarAction<LdapMessageContainer<MessageDecorator<? extends Message>>>
{
    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger( StoreResultCode.class );

    /** Speedup for logs */
    private static final boolean IS_DEBUG = LOG.isDebugEnabled();


    /**
     * Instantiates a new result code action.
     */
    public StoreResultCode()
    {
        super( "Store resultCode" );
    }


    /**
     * {@inheritDoc}
     */
    public void action( LdapMessageContainer<MessageDecorator<? extends Message>> container ) throws DecoderException
    {
        // The current TLV should be a integer
        // We get it and store it in MessageId
        TLV tlv = container.getCurrentTLV();

        Value value = tlv.getValue();
        ResultCodeEnum resultCode = ResultCodeEnum.SUCCESS;

        try
        {
            resultCode = ResultCodeEnum.getResultCode( IntegerDecoder.parse(value, 0, ResultCodeEnum.UNKNOWN
                    .getResultCode()) );
        }
        catch ( IntegerDecoderException ide )
        {
            LOG.error( I18n.err( I18n.ERR_04018, Strings.dumpBytes(value.getData()), ide.getMessage() ) );

            throw new DecoderException( ide.getMessage() );
        }

        // Treat the 'normal' cases !
        switch ( resultCode )
        {
            case SUCCESS:
            case OPERATIONS_ERROR:
            case PROTOCOL_ERROR:
            case TIME_LIMIT_EXCEEDED:
            case SIZE_LIMIT_EXCEEDED:
            case COMPARE_FALSE:
            case COMPARE_TRUE:
            case AUTH_METHOD_NOT_SUPPORTED:
            case STRONG_AUTH_REQUIRED:
            case REFERRAL:
            case ADMIN_LIMIT_EXCEEDED:
            case UNAVAILABLE_CRITICAL_EXTENSION:
            case CONFIDENTIALITY_REQUIRED:
            case SASL_BIND_IN_PROGRESS:
            case NO_SUCH_ATTRIBUTE:
            case UNDEFINED_ATTRIBUTE_TYPE:
            case INAPPROPRIATE_MATCHING:
            case CONSTRAINT_VIOLATION:
            case ATTRIBUTE_OR_VALUE_EXISTS:
            case INVALID_ATTRIBUTE_SYNTAX:
            case NO_SUCH_OBJECT:
            case ALIAS_PROBLEM:
            case INVALID_DN_SYNTAX:
            case ALIAS_DEREFERENCING_PROBLEM:
            case INAPPROPRIATE_AUTHENTICATION:
            case INVALID_CREDENTIALS:
            case INSUFFICIENT_ACCESS_RIGHTS:
            case BUSY:
            case UNAVAILABLE:
            case UNWILLING_TO_PERFORM:
            case LOOP_DETECT:
            case NAMING_VIOLATION:
            case OBJECT_CLASS_VIOLATION:
            case NOT_ALLOWED_ON_NON_LEAF:
            case NOT_ALLOWED_ON_RDN:
            case ENTRY_ALREADY_EXISTS:
            case AFFECTS_MULTIPLE_DSAS:
            case CANCELED:
            case CANNOT_CANCEL:
            case TOO_LATE:
            case NO_SUCH_OPERATION:
                break;

            default:
                LOG.warn( "The resultCode " + resultCode + " is unknown." );
                resultCode = ResultCodeEnum.OTHER;
                break;
        }

        if ( IS_DEBUG )
        {
            LOG.debug( "The result code is set to " + resultCode );
        }

        ResultResponse response = ( ResultResponse ) container.getMessage();
        LdapResult ldapResult = response.getLdapResult();
        ldapResult.setResultCode( resultCode );
    }
}
