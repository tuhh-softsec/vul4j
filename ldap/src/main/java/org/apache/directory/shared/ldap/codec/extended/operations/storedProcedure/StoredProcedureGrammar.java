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

package org.apache.directory.shared.ldap.codec.extended.operations.storedProcedure;


import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.ber.Asn1Container;
import org.apache.directory.shared.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.shared.asn1.ber.grammar.Grammar;
import org.apache.directory.shared.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.extended.operations.storedProcedure.StoredProcedure.StoredProcedureParameter;
import org.apache.directory.shared.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * ASN.1 BER Grammar for Stored Procedure Extended Operation
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public final class StoredProcedureGrammar extends AbstractGrammar
{
    //~ Static fields/initializers -----------------------------------------------------------------

    /** The logger */
    //private static final Logger log = LoggerFactory.getLogger( StoredProcedureGrammar.class );
    static final Logger LOG = LoggerFactory.getLogger( StoredProcedureGrammar.class );

    /** The instance of grammar. StoredProcedureGrammar is a singleton. */
    private static Grammar instance = new StoredProcedureGrammar();


    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new StoredProcedureGrammar object.
     */
    private StoredProcedureGrammar()
    {
        setName( StoredProcedureGrammar.class.getName() );

        // Create the transitions table
        super.transitions = new GrammarTransition[StoredProcedureStatesEnum.LAST_STORED_PROCEDURE_STATE.ordinal()][256];

        //============================================================================================
        // StoredProcedure Message
        //============================================================================================
        // StoredProcedure ::= SEQUENCE {
        //   ...
        // Nothing to do.
        super.transitions[StoredProcedureStatesEnum.START_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = 
            new GrammarTransition( StoredProcedureStatesEnum.START_STATE, 
                                    StoredProcedureStatesEnum.STORED_PROCEDURE_STATE, 
                                    UniversalTag.SEQUENCE.getValue(), 
                                    null );

        //    language OCTETSTRING, (Tag)
        //    ...
        //
        // Creates the storeProcedure and stores the language
        super.transitions[StoredProcedureStatesEnum.STORED_PROCEDURE_STATE.ordinal()][UniversalTag.OCTET_STRING.getValue()] = 
            new GrammarTransition( StoredProcedureStatesEnum.STORED_PROCEDURE_STATE, 
                                    StoredProcedureStatesEnum.LANGUAGE_STATE, 
                                    UniversalTag.OCTET_STRING.getValue(),
                new GrammarAction( "Stores the language" )
            {
                public void action( Asn1Container container ) throws DecoderException
                {

                    StoredProcedureContainer storedProcedureContainer = ( StoredProcedureContainer ) container;

                    TLV tlv = storedProcedureContainer.getCurrentTLV();

                    StoredProcedure storedProcedure = null;

                    // Store the value.
                    if ( tlv.getLength() == 0 )
                    {
                        // We can't have a void language !
                        String msg = I18n.err( I18n.ERR_04038 );
                        LOG.error( msg );
                        throw new DecoderException( msg );
                    }
                    else
                    {
                        // Only this field's type is String by default
                        String language = Strings.utf8ToString(tlv.getValue().getData());

                        if ( LOG.isDebugEnabled() )
                        {
                            LOG.debug( "SP language found: " + language );
                        }

                        storedProcedure = new StoredProcedure();
                        storedProcedure.setLanguage( language );
                        storedProcedureContainer.setStoredProcedure( storedProcedure );
                    }
                }
            } );

        //    procedure OCTETSTRING, (Value)
        //    ...
        // Stores the procedure.
        super.transitions[StoredProcedureStatesEnum.LANGUAGE_STATE.ordinal()][UniversalTag.OCTET_STRING.getValue()] = 
            new GrammarTransition( StoredProcedureStatesEnum.LANGUAGE_STATE, 
                                    StoredProcedureStatesEnum.PROCEDURE_STATE, 
                                    UniversalTag.OCTET_STRING.getValue(),
                new GrammarAction(
                "Stores the procedure" )
            {
                public void action( Asn1Container container ) throws DecoderException
                {

                    StoredProcedureContainer storedProcedureContainer = ( StoredProcedureContainer ) container;

                    TLV tlv = storedProcedureContainer.getCurrentTLV();

                    StoredProcedure storedProcedure = storedProcedureContainer.getStoredProcedure();

                    // Store the value.
                    if ( tlv.getLength() == 0 )
                    {
                        // We can't have a void procedure !
                        String msg = I18n.err( I18n.ERR_04039 );
                        LOG.error( msg );
                        throw new DecoderException( msg );
                    }
                    else
                    {
                        byte[] procedure = tlv.getValue().getData();

                        storedProcedure.setProcedure( procedure );
                    }

                    if ( LOG.isDebugEnabled() )
                    {
                        LOG.debug( "Procedure found : " + Strings.utf8ToString(storedProcedure.getProcedure()) );
                    }
                }
            } );

        // parameters SEQUENCE OF Parameter { (Value)
        //    ...
        // The list of parameters will be created with the first parameter.
        // We can have an empty list of parameters, so the PDU can be empty
        super.transitions[StoredProcedureStatesEnum.PROCEDURE_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = 
            new GrammarTransition( StoredProcedureStatesEnum.PROCEDURE_STATE, 
                                    StoredProcedureStatesEnum.PARAMETERS_STATE, 
                                    UniversalTag.SEQUENCE.getValue(), 
            new GrammarAction(
                "Stores the parameters" )
            {
                public void action( Asn1Container container ) throws DecoderException
                {
                    StoredProcedureContainer storedProcedureContainer = ( StoredProcedureContainer ) container;
                    storedProcedureContainer.setGrammarEndAllowed( true );
                }
            } );
        
        // parameter SEQUENCE OF { (Value)
        //    ...
        // Nothing to do. 
        super.transitions[StoredProcedureStatesEnum.PARAMETERS_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = 
            new GrammarTransition( StoredProcedureStatesEnum.PARAMETERS_STATE, 
                                    StoredProcedureStatesEnum.PARAMETER_STATE, 
                                    UniversalTag.SEQUENCE.getValue(), 
                                    null );

        // Parameter ::= {
        //    type OCTETSTRING, (Value)
        //    ...
        //
        // We can create a parameter, and store its type
        super.transitions[StoredProcedureStatesEnum.PARAMETER_STATE.ordinal()][UniversalTag.OCTET_STRING.getValue()] = 
            new GrammarTransition( StoredProcedureStatesEnum.PARAMETER_STATE, 
                                    StoredProcedureStatesEnum.PARAMETER_TYPE_STATE, 
                                    UniversalTag.OCTET_STRING.getValue(),
                new GrammarAction( "Store parameter type" )
            {
                public void action( Asn1Container container ) throws DecoderException
                {
                    StoredProcedureContainer storedProcedureContainer = ( StoredProcedureContainer ) container;

                    TLV tlv = storedProcedureContainer.getCurrentTLV();
                    StoredProcedure storedProcedure = storedProcedureContainer.getStoredProcedure();

                    // Store the value.
                    if ( tlv.getLength() == 0 )
                    {
                        // We can't have a void parameter type !
                        String msg = I18n.err( I18n.ERR_04040 );
                        LOG.error( msg );
                        throw new DecoderException( msg );
                    }
                    else
                    {
                        StoredProcedureParameter parameter = new StoredProcedureParameter();

                        byte[] parameterType = tlv.getValue().getData();

                        parameter.setType( parameterType );

                        // We store the type in the current parameter.
                        storedProcedure.setCurrentParameter( parameter );

                        if ( LOG.isDebugEnabled() )
                        {
                            LOG.debug( "Parameter type found : " + Strings.dumpBytes(parameterType) );
                        }

                    }
                }
            } );

        // Parameter ::= {
        //    ...
        //    value OCTETSTRING (Tag)
        // }
        // Store the parameter value
        super.transitions[StoredProcedureStatesEnum.PARAMETER_TYPE_STATE.ordinal()][UniversalTag.OCTET_STRING.getValue()] = 
            new GrammarTransition( StoredProcedureStatesEnum.PARAMETER_TYPE_STATE, 
                                    StoredProcedureStatesEnum.PARAMETER_VALUE_STATE, 
                                    UniversalTag.OCTET_STRING.getValue(),
                new GrammarAction( "Store parameter value" )
            {
                public void action( Asn1Container container ) throws DecoderException
                {
                    StoredProcedureContainer storedProcedureContainer = ( StoredProcedureContainer ) container;

                    TLV tlv = storedProcedureContainer.getCurrentTLV();
                    StoredProcedure storedProcedure = storedProcedureContainer.getStoredProcedure();

                    // Store the value.
                    if ( tlv.getLength() == 0 )
                    {
                        // We can't have a void parameter value !
                        String msg = I18n.err( I18n.ERR_04041 );
                        LOG.error( msg );
                        throw new DecoderException( msg );
                    }
                    else
                    {
                        byte[] parameterValue = tlv.getValue().getData();

                        if ( parameterValue.length != 0 )
                        {
                            StoredProcedureParameter parameter = storedProcedure.getCurrentParameter();
                            parameter.setValue( parameterValue );

                            // We can now add a new Parameter to the procedure
                            storedProcedure.addParameter( parameter );

                            if ( LOG.isDebugEnabled() )
                            {
                                LOG.debug( "Parameter value found : " + Strings.dumpBytes(parameterValue) );
                            }
                        }
                        else
                        {
                            String msg = I18n.err( I18n.ERR_04042 );
                            LOG.error( msg );
                            throw new DecoderException( msg );
                        }
                    }

                    // The only possible END state for the grammar is here
                    container.setGrammarEndAllowed( true );
                }
            } );
        
        // Parameters ::= SEQUENCE OF Parameter
        // 
        // Loop on next parameter
        super.transitions[StoredProcedureStatesEnum.PARAMETER_VALUE_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = 
            new GrammarTransition( StoredProcedureStatesEnum.PARAMETER_VALUE_STATE, 
                                    StoredProcedureStatesEnum.PARAMETER_STATE, 
                                    UniversalTag.SEQUENCE.getValue(),
                                    null );
    }


    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Get the instance of this grammar
     *
     * @return An instance on the StoredProcedure Grammar
     */
    public static Grammar getInstance()
    {
        return instance;
    }
}
