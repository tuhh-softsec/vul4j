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

package org.apache.directory.shared.ldap.ldif;

import org.apache.directory.shared.ldap.codec.controls.AbstractControl;
import org.apache.directory.shared.ldap.util.StringTools;

/**
 * The LdifControl class stores a control defined for an entry found in a ldif
 * file.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdifControl extends AbstractControl
{
    private static final long serialVersionUID = 1L;

    /**
     * Create a new Control
     * 
     * @param oid OID of the created control
     */
    public LdifControl( String oid )
    {
        super( oid );
    }

    public String toString()
    {
        return "LdifControl : {" + getOid() + ", " + isCritical() + ", " + StringTools.dumpBytes( getValue() ) + "}";
    }
}
