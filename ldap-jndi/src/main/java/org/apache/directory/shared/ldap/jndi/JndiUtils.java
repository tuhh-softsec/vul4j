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
package org.apache.directory.shared.ldap.jndi;

import javax.naming.NamingException;
import javax.naming.ldap.BasicControl;

import org.apache.directory.shared.ldap.message.control.Control;
import org.apache.directory.shared.ldap.message.control.ControlImpl;

/**
 * An utility class to convert back and forth JNDI classes to ADS classes.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class JndiUtils
{
    public static javax.naming.ldap.Control toJndiControl( Control control )
    {
        byte[] value = control.getValue();
        javax.naming.ldap.Control jndiControl = new BasicControl( control.getOid(), control.isCritical(), value );
        
        return jndiControl;
    }
    
    
    public static javax.naming.ldap.Control[] toJndiControls( Control... controls )
    {
        if ( controls != null )
        {
            javax.naming.ldap.Control[] jndiControls = new javax.naming.ldap.Control[controls.length];
            int i = 0;
            
            for ( Control control : controls )
            {
                jndiControls[i++] = toJndiControl( control );
            }
            
            return jndiControls;
        }
        else
        {
            return null;
        }
    }
    
    
    public static Control fromJndiControl( javax.naming.ldap.Control jndiControl )
    {
        Control control = new ControlImpl( jndiControl.getID() );
        
        control.setOid( jndiControl.getID() );
        control.setValue( jndiControl.getEncodedValue() );

        return control;
    }
    
    
    public static Control[] fromJndiControls( javax.naming.ldap.Control... jndiControls )
    {
        if ( jndiControls != null )
        {
            Control[] controls = new Control[jndiControls.length];
            int i = 0;
            
            for ( javax.naming.ldap.Control jndiControl : jndiControls )
            {
                controls[i++] = fromJndiControl( jndiControl );
            }
            
            return controls;
        }
        else
        {
            return null;
        }
    }
    
    
    public static void wrap( Throwable t ) throws NamingException
    {
        if ( t instanceof NamingException )
        {
            throw ( NamingException ) t;
        }
        
        NamingException ne = new NamingException( t.getLocalizedMessage() );
        ne.setRootCause( t );
        throw ne;
    }

}
