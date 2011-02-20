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
package org.apache.directory.shared.ldap.model.constants;


/**
 * This enums contains values for SASL QoP (Quality of Protection).
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum SaslQoP
{
    /** Authentication only */
    AUTH("auth"),

    /** Authentication with integrity protection */
    AUTH_INT("auth-int"),

    /** Authentication with integrity and privacy protection */
    AUTH_CONF("auth-conf");

    /** The equivalent string value */
    private String value;


    /**
     * Creates a new instance of SaslQoP.
     *
     * @param value the equivalent string value
     */
    private SaslQoP( String value )
    {
        this.value = value;
    }


    /**
     * Gets the equivalent string value.
     *
     * @return the equivalent string value
     */
    public String getValue()
    {
        return value;
    }
}
