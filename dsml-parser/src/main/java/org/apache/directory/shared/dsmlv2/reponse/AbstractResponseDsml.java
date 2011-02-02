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
package org.apache.directory.shared.dsmlv2.reponse;


import java.nio.ByteBuffer;

import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.dsmlv2.DsmlDecorator;
import org.apache.directory.shared.ldap.codec.ICodecControl;
import org.apache.directory.shared.ldap.codec.ILdapCodecService;
import org.apache.directory.shared.ldap.codec.decorators.MessageDecorator;
import org.apache.directory.shared.ldap.model.message.Control;
import org.apache.directory.shared.ldap.model.message.Response;
import org.dom4j.Element;


/**
 * Base class for all DSML responses.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class AbstractResponseDsml<E extends Response> extends MessageDecorator<E> implements DsmlDecorator
{

    /**
     * Instantiates a new abstract DSML response.
     *
     * @param ldapMessage the LDAP message to decorate
     */
    public AbstractResponseDsml( ILdapCodecService codec, E ldapMessage )
    {
        super( codec, ldapMessage );
    }


    /**
     * {@inheritDoc}
     */
    public abstract Element toDsml( Element root );


    /**
     * {@inheritDoc}
     */
    public void addAllControls( Control[] controls )
    {
        // TODO Auto-generated method stub

    }


    /**
     * {@inheritDoc}
     */
    public Object get( Object key )
    {
        // TODO Auto-generated method stub
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public ICodecControl<? extends Control> getCurrentControl()
    {
        // TODO Auto-generated method stub
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public boolean hasControl( String oid )
    {
        // TODO Auto-generated method stub
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public Object put( Object key, Object value )
    {
        // TODO Auto-generated method stub
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public void removeControl( Control control )
    {
        // TODO Auto-generated method stub

    }


    public int computeLength()
    {
        return 0;
    }


    public ByteBuffer encode( ByteBuffer buffer ) throws EncoderException
    {
        return null;
    }

}
