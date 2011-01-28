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
package org.apache.directory.shared.dsmlv2.request;


import org.apache.directory.shared.dsmlv2.DsmlDecorator;
import org.apache.directory.shared.dsmlv2.ParserUtils;
import org.apache.directory.shared.ldap.codec.decorators.RequestDecorator;
import org.apache.directory.shared.ldap.model.message.Message;
import org.dom4j.Element;


/**
 * Abstract class for DSML requests.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class AbstractRequestDsml extends RequestDecorator implements DsmlDecorator
{
    /**
     * Creates a new instance of AbstractRequestDsml.
     *
     * @param ldapMessage
     *      the message to decorate
     */
    public AbstractRequestDsml( Message ldapMessage )
    {
        super( ldapMessage );
    }


    /**
     * Creates the Request Element and adds RequestID and Controls.
     *
     * @param root
     *      the root element
     * @return
     *      the Request Element of the given name containing
     */
    public Element toDsml( Element root )
    {
        Element element = root.addElement( getRequestName() );

        // Request ID
        int requestID = getDecoratedMessage().getMessageId();
        if ( requestID > 0 )
        {
            element.addAttribute( "requestID", "" + requestID );
        }

        // Controls
        ParserUtils.addControls( element, getDecoratedMessage().getControls().values() );

        return element;
    }


    /**
     * Gets the name of the request according to the type of the decorated element.
     *
     * @return
     *      the name of the request according to the type of the decorated element.
     */
    private String getRequestName()
    {
        switch ( getDecoratedMessage().getType() )
        {
            case ABANDON_REQUEST:
                return "abandonRequest";

            case ADD_REQUEST:
                return "addRequest";

            case BIND_REQUEST:
                return "authRequest";

            case COMPARE_REQUEST:
                return "compareRequest";

            case DEL_REQUEST:
                return "delRequest";

            case EXTENDED_REQUEST:
                return "extendedRequest";

            case MODIFYDN_REQUEST:
                return "modDNRequest";

            case MODIFY_REQUEST:
                return "modifyRequest";

            case SEARCH_REQUEST:
                return "searchRequest";

            default:
                return "error";
        }
    }
}
