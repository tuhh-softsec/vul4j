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
package org.apache.directory.shared.ldap.model.message.controls;


import org.apache.directory.shared.ldap.model.message.Control;
import org.apache.directory.shared.util.Strings;


/**
 * A final {@link Control} implementation intended specifically for handling
 * controls who's values cannot be encoded or decoded by the codec service. 
 * This situation results when no Control factory is found to be
 * registered for this control's OID. Hence additional opaque value handling
 * methods are included to manage the opaque control value.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public final class OpaqueControl extends AbstractControl implements Control
{
	/** The opaque encoded value */
	private byte[] value;
	
    /**
     * Creates a Control with a specific OID.
     *
     * @param oid The OID of this Control.
     */
    public OpaqueControl( String oid )
    {
        super( oid );
    }


    /**
     * Creates a Control with a specific OID, and criticality set.
     *
     * @param oid The OID of this Control.
     * @param criticality true if this Control is critical, false otherwise. 
     */
    public OpaqueControl( String oid, boolean criticality )
    {
        super( oid, criticality);
    }


    /**
     * @return The encoded value
     */
    public byte[] getEncodedValue()
    {
    	return value;
    }
    
    
    /**
     * Stores an opaque value into the control.
     * 
     * @param value The opaque value to store
     */
    public void setEncodedValue( byte[] value )
    {
    	this.value = Strings.copy( value );
    }
    
    
    /**
     * Tells if the control has a stored value. Note that if the 
     * control has an empty value, this method will return true.
     * 
     * @return true if the control has a value
     */
    public boolean hasEncodedValue()
    {
    	return value != null;
    }
}
