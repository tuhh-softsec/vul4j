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
 * An enumeration that represents grants or denials of {@link MicroOperation}s.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum GrantAndDenial
{
    // Permissions that may be used in conjunction with any component of
    // <tt>ProtectedItem</tt>s.
    /** Grant for {@link MicroOperation#ADD} */
    GRANT_ADD ( MicroOperation.ADD, 0, true ),

    /** Denial for {@link MicroOperation#ADD} */
    DENY_ADD ( MicroOperation.ADD, 1, false ),

    /** Grant for {@link MicroOperation#DISCLOSE_ON_ERROR} */
    GRANT_DISCLOSE_ON_ERROR ( MicroOperation.DISCLOSE_ON_ERROR, 2, true ),

    /** Denial for {@link MicroOperation#DISCLOSE_ON_ERROR} */
    DENY_DISCLOSE_ON_ERROR ( MicroOperation.DISCLOSE_ON_ERROR, 3, false ),

    /** Grant for {@link MicroOperation#READ} */
    GRANT_READ ( MicroOperation.READ, 4, true ),

    /** Denial for {@link MicroOperation#READ} */
    DENY_READ ( MicroOperation.READ, 5, false ),

    /** Grant for {@link MicroOperation#REMOVE} */
    GRANT_REMOVE ( MicroOperation.REMOVE, 6, true ),

    /** Denial for {@link MicroOperation#REMOVE} */
    DENY_REMOVE ( MicroOperation.REMOVE, 7, false ),

    // Permissions that may be used only in conjunction with the entry
    // component.
    /** Grant for {@link MicroOperation#BROWSE} */
    GRANT_BROWSE ( MicroOperation.BROWSE, 8, true ),

    /** Denial for {@link MicroOperation#BROWSE} */
    DENY_BROWSE ( MicroOperation.BROWSE, 9, false ),

    /** Grant for {@link MicroOperation#EXPORT} */
    GRANT_EXPORT ( MicroOperation.EXPORT, 10, true ),

    /** Denial for {@link MicroOperation#EXPORT} */
    DENY_EXPORT ( MicroOperation.EXPORT, 11, false ),

    /** Grant for {@link MicroOperation#IMPORT} */
    GRANT_IMPORT ( MicroOperation.IMPORT, 12, true ),

    /** Denial for {@link MicroOperation#IMPORT} */
    DENY_IMPORT ( MicroOperation.IMPORT, 13, false ),

    /** Grant for {@link MicroOperation#MODIFY} */
    GRANT_MODIFY ( MicroOperation.MODIFY, 14, true ),

    /** Denial for {@link MicroOperation#MODIFY} */
    DENY_MODIFY ( MicroOperation.MODIFY, 15, false ),

    /** Grant for {@link MicroOperation#RENAME} */
    GRANT_RENAME ( MicroOperation.RENAME, 16, true ),

    /** Denial for {@link MicroOperation#RENAME} */
    DENY_RENAME ( MicroOperation.RENAME, 17, false ),

    /** Grant for {@link MicroOperation#RETURN_DN} */
    GRANT_RETURN_DN ( MicroOperation.RETURN_DN, 18, true ),

    /** Denial for {@link MicroOperation#RETURN_DN} */
    DENY_RETURN_DN ( MicroOperation.RETURN_DN, 19, false ),

    // Permissions that may be used in conjunction with any component,
    // except entry, of <tt>ProtectedItem</tt>s.
    /** Grant for {@link MicroOperation#COMPARE} */
    GRANT_COMPARE ( MicroOperation.COMPARE, 20, true ),

    /** Deny for {@link MicroOperation#COMPARE} */
    DENY_COMPARE ( MicroOperation.COMPARE, 21, false ),

    /** Grant for {@link MicroOperation#FILTER_MATCH} */
    GRANT_FILTER_MATCH ( MicroOperation.FILTER_MATCH, 22, true ),

    /** Denial for {@link MicroOperation#FILTER_MATCH} */
    DENY_FILTER_MATCH ( MicroOperation.FILTER_MATCH, 23, false ),

    /** Grant for {@link MicroOperation#INVOKE} */
    GRANT_INVOKE ( MicroOperation.INVOKE, 24, true ),

    /** Denial for {@link MicroOperation#INVOKE} */
    DENY_INVOKE ( MicroOperation.INVOKE, 25, false );

    /** The micro operation. */
    private final MicroOperation microOperation;

    /** The code number. */
    private final int code;

    /** The name. */
    private final String name;

    /** The grant flag. */
    private final boolean grant;


    private GrantAndDenial( MicroOperation microOperation, int code, boolean grant )
    {
        this.microOperation = microOperation;
        this.code = code;
        this.name = ( grant ? "grant" : "deny" ) + microOperation.getName();
        this.grant = grant;
    }


    /**
     * Gets the {@link MicroOperation} related with this grant or denial.
     *
     * @return the micro operation
     */
    public MicroOperation getMicroOperation()
    {
        return microOperation;
    }


    /**
     * Gets the code number of this grant or denial.
     *
     * @return the code number
     */
    public int getCode()
    {
        return code;
    }


    /**
     * Gets the name of this grant or denial.
     *
     * @return the name
     */
    public String getName()
    {
        return name;
    }


    /**
     * Returns <tt>true</tt> if and only if this is grant.
     *
     * @return <tt>true</tt> if and only if this is grant
     */
    public boolean isGrant()
    {
        return grant;
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
