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
package org.apache.directory.shared.ldap.codec.controls.ppolicy;


import org.apache.directory.shared.ldap.model.message.Control;


/**
 * A simple {@link IPasswordPolicy} {@link Control} implementation.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class PasswordPolicy implements IPasswordPolicy
{
    /** The criticality of this {@link Control} */
    private boolean criticality;
    
    /** The password policy response component if this is a response control */
    private IPasswordPolicyResponse response;
    
    
    /**
     * Creates a new instance of a PasswordPolicy request Control without any
     * response data associated with it.
     */
    public PasswordPolicy()
    {
        response = null;
    }


    /**
     * Creates a new instance of a PasswordPolicy request Control without any
     * response data associated with it.
     */
    public PasswordPolicy( boolean hasResponse )
    {
        if ( hasResponse )
        {
            response = new PasswordPolicyResponse();
        }
        else
        {
            response = null;
        }
    }


    /**
     * Creates a new instance of PasswordPolicy response Control with response 
     * information packaged into the control.
     */
    public PasswordPolicy( IPasswordPolicyResponse response )
    {
        this.response = response;
    }


    /**
     * {@inheritDoc}
     */
    public String getOid()
    {
        return IPasswordPolicy.OID;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isCritical()
    {
        return criticality;
    }


    /**
     * {@inheritDoc}
     */
    public void setCritical( boolean isCritical )
    {
        this.criticality = isCritical;
    }

    
    /**
     * 
     * {@inheritDoc}
     */
    public void setResponse( IPasswordPolicyResponse response )
    {
        this.response = response;
    }
    

    /**
     * {@inheritDoc}
     */
    public boolean hasResponse()
    {
        return response != null;
    }

    
    /**
     * 
     * {@inheritDoc}
     */
    public IPasswordPolicyResponse setResponse( boolean hasResponse )
    {
        IPasswordPolicyResponse old = this.response;
        
        if ( hasResponse )
        {
            this.response = new PasswordPolicyResponse();
        }
        else
        {
            this.response = null;
        }
        
        return old;
    }
    

    /**
     * {@inheritDoc}
     */
    public IPasswordPolicyResponse getResponse()
    {
        return response;
    }
}
