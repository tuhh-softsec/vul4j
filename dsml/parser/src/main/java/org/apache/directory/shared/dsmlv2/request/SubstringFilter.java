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
package org.apache.directory.shared.dsmlv2.request;


import java.util.ArrayList;
import java.util.List;


/**
 * A Object that stores the substring filter. 
 * 
 * A substring filter follow this
 * grammar : 
 * 
 * substring = attr "=" ( ([initial] any [final] | 
 *                        (initial [any] [final) | 
 *                        ([initial] [any] final) ) 
 *                       
 * initial = value 
 * any = "*" *(value "*")
 * final = value
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SubstringFilter extends Filter
{
    /** The substring filter type (an attributeDescription) */
    private String type;

    /** The initial filter */
    private String initialSubstrings;

    /** The any filter. It's a list of LdapString */
    private List<String> anySubstrings = new ArrayList<String>( 1 );

    /** The final filter */
    private String finalSubstrings;


    /**
     * Get the internal substrings
     * 
     * @return Returns the anySubstrings.
     */
    public List<String> getAnySubstrings()
    {
        return anySubstrings;
    }


    /**
     * Add a internal substring
     * 
     * @param any The anySubstrings to set.
     */
    public void addAnySubstrings( String any )
    {
        this.anySubstrings.add( any );
    }


    /**
     * Get the final substring
     * 
     * @return Returns the finalSubstrings.
     */
    public String getFinalSubstrings()
    {
        return finalSubstrings;
    }


    /**
     * Set the final substring
     * 
     * @param finalSubstrings The finalSubstrings to set.
     */
    public void setFinalSubstrings( String finalSubstrings )
    {
        this.finalSubstrings = finalSubstrings;
    }


    /**
     * Get the initial substring
     * 
     * @return Returns the initialSubstrings.
     */
    public String getInitialSubstrings()
    {
        return initialSubstrings;
    }


    /**
     * Set the initial substring
     * 
     * @param initialSubstrings The initialSubstrings to set.
     */
    public void setInitialSubstrings( String initialSubstrings )
    {
        this.initialSubstrings = initialSubstrings;
    }


    /**
     * Get the attribute
     * 
     * @return Returns the type.
     */
    public String getType()
    {
        return type;
    }


    /**
     * Set the attribute to match
     * 
     * @param type The type to set.
     */
    public void setType( String type )
    {
        this.type = type;
    }


    /**
     * Return a string compliant with RFC 2254 representing a Substring filter
     * 
     * @return The substring filter string
     */
    public String toString()
    {

        StringBuffer sb = new StringBuffer();

        if ( initialSubstrings != null )
        {
            sb.append( initialSubstrings );
        }

        sb.append( '*' );

        if ( anySubstrings != null )
        {
            for ( String any:anySubstrings )
            {
                sb.append( any ).append( '*' );
            }
        }

        if ( finalSubstrings != null )
        {
            sb.append( finalSubstrings );
        }

        return sb.toString();
    }
}
