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
package org.apache.directory.shared.ldap.subtree;

import org.apache.directory.shared.ldap.util.StringTools;

/**
*
* @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
*/
public enum AdministrativeRole
{
    /** The AutonomousArea role */
    AutonomousArea( "autonomousArea" ),

    /** The AccessControlSpecificArea role */
    AccessControlSpecificArea( "accessControlSpecificArea" ),

    /** The AccessControlInnerArea role */
    AccessControlInnerArea( "accessControlInnerArea" ),

    /** The CollectiveAttributeSpecificArea role */
    CollectiveAttributeSpecificArea( "collectiveAttributeSpecificArea" ),

    /** The CollectiveAttributeInnerArea role */
    CollectiveAttributeInnerArea( "collectiveAttributeInnerArea" ),

    /** The SubSchemaSpecificArea role */
    SubSchemaSpecificArea( "subSchemaSpecificArea" ),

    /** The TriggerExecutionSpecificArea role */
    TriggerExecutionSpecificArea( "triggerExecutionSpecificArea" ),

    /** The TriggerExecutionInnerArea role */
    TriggerExecutionInnerArea( "triggerExecutionInnerArea" );

    /** The AdministrativeRole as a String */
    private String role;

    /**
     * Creates an instance of AdministrativeRole
     */
    private AdministrativeRole( String role )
    {
        this.role = StringTools.toLowerCase( StringTools.trim( role ) );
    }


    /**
     * @return The AdministrativeRole as a String
     */
    public String getRole()
    {
        return role;
    }
}
