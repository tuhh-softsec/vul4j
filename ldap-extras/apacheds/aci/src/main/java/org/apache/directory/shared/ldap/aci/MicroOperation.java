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
package org.apache.directory.shared.ldap.aci;


/**
 * An enumeration that represents all micro-operations that makes up LDAP
 * operations.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum MicroOperation
{
    // Permissions that may be used in conjunction with any component of <tt>ProtectedItem</tt>s.
    /** The Add permission, may be used in conjunction with any component of {@link ProtectedItem}s. */
    ADD("Add"),

    /** The DiscloseOnError permission, may be used in conjunction with any component of {@link ProtectedItem}s. */
    DISCLOSE_ON_ERROR("DiscloseOnError"),

    /** The Read permission, may be used in conjunction with any component of {@link ProtectedItem}s. */
    READ("Read"),

    /** The Remove permission, may be used in conjunction with any component of {@link ProtectedItem}s. */
    REMOVE("Remove"),

    // Permissions that may be used only in conjunction with the entry component.
    /** The Browse permission, may be used only in conjunction with the entry component. */
    BROWSE("Browse"),

    /** The Export permission, may be used only in conjunction with the entry component. */
    EXPORT("Export"),

    /** The Import permission, may be used only in conjunction with the entry component. */
    IMPORT("Import"),

    /** The Modify permission, may be used only in conjunction with the entry component. */
    MODIFY("Modify"),

    /** The Rename permission, may be used only in conjunction with the entry component. */
    RENAME("Rename"),

    /** The ReturnDN permission, may be used only in conjunction with the entry component. */
    RETURN_DN("ReturnDN"),

    // Permissions that may be used in conjunction with any component, except entry, of <tt>ProtectedItem</tt>s.
    /** The Compare permission, may be used in conjunction with any component, except entry. */
    COMPARE("Compare"),

    /** The FilterMatch permission, may be used in conjunction with any component, except entry. */
    FILTER_MATCH("FilterMatch"),

    /** The Invoke permission, may be used in conjunction with any component, except entry. */
    INVOKE("Invoke");

    /** The name. */
    private final String name;


    private MicroOperation( String name )
    {
        this.name = name;
    }


    /**
     * Gets the name of this micro-operation.
     *
     * @return the name
     */
    public String getName()
    {
        return name;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return name;
    }
}
