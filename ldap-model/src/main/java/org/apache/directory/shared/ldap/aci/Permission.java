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


import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * An abstract base class for {@link ItemPermission} and {@link UserPermission}.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class Permission implements Serializable
{
    /** The serialVersionUID. */
    private static final long serialVersionUID = 8923801399021825709L;

    /** The precedence. */
    private final Integer precedence;

    /** The grants and denials. */
    private final Set<GrantAndDenial> grantsAndDenials;

    /** The grants. */
    private final Set<GrantAndDenial> grants;

    /** The denials. */
    private final Set<GrantAndDenial> denials;


    /**
     * Creates a new instance
     * 
     * @param precedence
     *            the precedence of this permission (<tt>-1</tt> to use the
     *            default)
     * @param grantsAndDenials
     *            the set of {@link GrantAndDenial}s
     */
    protected Permission( Integer precedence, Collection<GrantAndDenial> grantsAndDenials )
    {
        this.precedence = precedence;

        Set<GrantAndDenial> tmpGrantsAndDenials = new HashSet<GrantAndDenial>();
        Set<GrantAndDenial> tmpGrants = new HashSet<GrantAndDenial>();
        Set<GrantAndDenial> tmpDenials = new HashSet<GrantAndDenial>();

        for ( GrantAndDenial gad : grantsAndDenials )
        {
            if ( gad.isGrant() )
            {
                tmpGrants.add( gad );
            }
            else
            {
                tmpDenials.add( gad );
            }

            tmpGrantsAndDenials.add( gad );
        }

        this.grants = Collections.unmodifiableSet( tmpGrants );
        this.denials = Collections.unmodifiableSet( tmpDenials );
        this.grantsAndDenials = Collections.unmodifiableSet( tmpGrantsAndDenials );
    }


    /**
     * Gets the precedence of this permission.
     *
     * @return the precedence
     */
    public Integer getPrecedence()
    {
        return precedence;
    }


    /**
     * Gets the set of {@link GrantAndDenial}s.
     *
     * @return the grants and denials
     */
    public Set<GrantAndDenial> getGrantsAndDenials()
    {
        return grantsAndDenials;
    }


    /**
     * Gets the set of grants only.
     *
     * @return the grants
     */
    public Set<GrantAndDenial> getGrants()
    {
        return grants;
    }


    /**
     * Gets the set of denials only.
     *
     * @return the denials
     */
    public Set<GrantAndDenial> getDenials()
    {
        return denials;
    }
}
