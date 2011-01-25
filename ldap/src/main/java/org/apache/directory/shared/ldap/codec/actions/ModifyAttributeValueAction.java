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
package org.apache.directory.shared.ldap.codec.actions;


import org.apache.directory.shared.asn1.ber.Asn1Container;
import org.apache.directory.shared.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.ldap.codec.LdapMessageContainer;
import org.apache.directory.shared.ldap.model.message.ModifyRequest;
import org.apache.directory.shared.ldap.model.message.ModifyRequestImpl;
import org.apache.directory.shared.util.StringConstants;
import org.apache.directory.shared.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The action used to store a Value to an modifyRequest
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ModifyAttributeValueAction extends GrammarAction
{
    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger( ModifyAttributeValueAction.class );

    /** Speedup for logs */
    private static final boolean IS_DEBUG = LOG.isDebugEnabled();


    /**
     * Instantiates a new modify attribute value action.
     */
    public ModifyAttributeValueAction()
    {
        super( "Stores AttributeValue" );
    }


    /**
     * {@inheritDoc}
     */
    public void action( Asn1Container container )
    {
        LdapMessageContainer ldapMessageContainer = ( LdapMessageContainer ) container;
        ModifyRequest modifyRequest = ldapMessageContainer.getModifyRequest();

        TLV tlv = ldapMessageContainer.getCurrentTLV();

        // Store the value. It can't be null
        byte[] value = StringConstants.EMPTY_BYTES;

        if ( tlv.getLength() == 0 )
        {
            ( (ModifyRequestImpl) modifyRequest ).addAttributeValue( "" );
        }
        else
        {
            value = tlv.getValue().getData();

            if ( ldapMessageContainer.isBinary( ( ( ModifyRequestImpl ) modifyRequest ).getCurrentAttributeType() ) )
            {
                ( ( ModifyRequestImpl ) modifyRequest ).addAttributeValue( value );
            }
            else
            {
                ( ( ModifyRequestImpl ) modifyRequest )
                    .addAttributeValue( Strings.utf8ToString((byte[]) value) );
            }
        }

        // We can have an END transition
        ldapMessageContainer.setGrammarEndAllowed( true );

        if ( IS_DEBUG )
        {
            LOG.debug( "Value modified : {}", value );
        }
    }
}
