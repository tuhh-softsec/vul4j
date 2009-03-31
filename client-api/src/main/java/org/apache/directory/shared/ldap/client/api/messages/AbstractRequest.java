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
 * An abstract class containing the Controls and timeout for all the requests.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class AbstractRequest implements Message
{
    /** The set of controls */
    private Map<String, Control> controls;
    
    /** The client request timeout */
    private long timeout = 0;


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
    public long getTimeout()
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
    public void setTimeout( long timeout )
    {
        this.timeout = timeout;
    }
}
