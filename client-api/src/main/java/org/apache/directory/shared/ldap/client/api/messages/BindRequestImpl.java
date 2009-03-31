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
package org.apache.directory.shared.ldap.client.api.messages;

import java.util.HashMap;
import java.util.Map;

import javax.naming.ldap.Control;

import org.apache.directory.shared.ldap.message.MessageException;

/**
 * A client implementation of the client BindRequest LDAP message.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class BindRequestImpl implements BindRequest
{
    /**
     * Distinguished name identifying the name of the authenticating subject -
     * defaults to the empty string
     */
    private String name;
    
    /** The passwords, keys or tickets used to verify user identity */
    private byte[] credentials;
    
    /** The mechanism used to decode user identity */
    private String saslMechanism;
    
    /** Simple vs. SASL authentication mode flag */
    private boolean isSimple = true;

    /** Returns the protocol version */
    private int version = 3;
    
    /** The set of controls */
    private Map<String, Control> controls;
    
    /** The client request timeout */
    private int timeout = 0;


    /**
     * Creates a new instance of BindRequestImpl.
     */
    public BindRequestImpl()
    {
    }
    
    
    /**
     * {@inheritDoc}
     */
    public byte[] getCredentials()
    {
        return credentials;
    }

    
    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return name;
    }

    
    /**
     * {@inheritDoc}
     */
    public String getSaslMechanism()
    {
        return saslMechanism;
    }

    
    /**
     * {@inheritDoc}
     */
    public int getVersion()
    {
        return version;
    }

    
    /**
     * {@inheritDoc}
     */
    public boolean isSimple()
    {
        return isSimple;
    }

    
    /**
     * {@inheritDoc}
     */
    public boolean isVersion3()
    {
        return version == 3;
    }

    
    /**
     * {@inheritDoc}
     */
    public void setCredentials( byte[] credentials )
    {
        this.credentials = credentials;
    }

    
    /**
     * {@inheritDoc}
     */
    public void setName( String name )
    {
        this.name = name;
    }
    

    /**
     * {@inheritDoc}
     */
    public void setSasl()
    {
        isSimple = false;
    }

    
    /**
     * {@inheritDoc}
     */
    public void setSaslMechanism( String saslMechanism )
    {
        this.saslMechanism = saslMechanism;
    }

    
    /**
     * {@inheritDoc}
     */
    public void setVersion( int version )
    {
        this.version = version;
    }


    /**
     * {@inheritDoc}
     */
    public void add( Control control ) throws MessageException
    {
        if ( controls == null )
        {
            controls = new HashMap<String, Control>();
        }
        
        controls.put( control.getID(), control );
    }


    /**
     * {@inheritDoc}
     */
    public void addAll( Control[] controls ) throws MessageException
    {
        if ( controls == null )
        {
            this.controls = new HashMap<String, Control>();
        }
        
        for ( Control control:controls )
        {
            this.controls.put( control.getID(), control );
        }
    }


    /**
     * {@inheritDoc}
     */
    public Map<String, Control> getControls()
    {
        return controls;
    }


    /**
     * {@inheritDoc}
     */
    public int getTimeout()
    {
        return timeout;
    }


    /**
     * {@inheritDoc}
     */
    public boolean hasControl( String oid )
    {
        return ( controls != null ) && ( controls.size() > 0 );
    }


    /**
     * {@inheritDoc}
     */
    public void remove( Control control ) throws MessageException
    {
        if ( controls != null )
        {
            controls.remove( control.getID() );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void setTimeout( int timeout )
    {
        this.timeout = timeout;
    }
}
