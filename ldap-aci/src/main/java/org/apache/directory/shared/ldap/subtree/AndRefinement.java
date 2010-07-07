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
package org.apache.directory.shared.ldap.subtree;

import java.util.ArrayList;
import java.util.List;

/**
 * A class holding a AND refinement, as defined in RFC 3672
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class AndRefinement implements Refinement
{
    /** The set of refinements */
    private List<Refinement> refinements = new ArrayList<Refinement>();

    /**
     * Creates a new instance of AndRefinement.
     *
     * @param refinements The refinements. We may have more than one
     */
    public AndRefinement( List<Refinement> refinements )
    {
        this.refinements = refinements;
    }
    
    
    /**
     * @return Gets the set of refinements
     */
    public List<Refinement> getRefinements()
    {
        return refinements;
    }
    
    
    /**
     * @see Object#toString()
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append( "and: { " );

        boolean isFirst = true;
        
        for ( Refinement refinement:refinements )
        {
            if ( isFirst )
            {
                isFirst = false;
            }
            else
            {
                sb.append( ", " );
            }

            sb.append( refinement );
        }
     
        sb.append( " }" );
        return sb.toString();
    }
}
