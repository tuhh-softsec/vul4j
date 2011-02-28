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


/**
 * The search request filter Matching Rule assertion
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ExtensibleMatchFilter extends Filter
{
    /** Matching rule */
    private String matchingRule;

    /** Matching rule type */
    private String type;

    /** Matching rule value */
    private org.apache.directory.shared.ldap.model.entry.Value<?> matchValue;

    /** The dnAttributes flag */
    private boolean dnAttributes = false;

    

    /**
     * Get the dnAttributes flag
     * 
     * @return Returns the dnAttributes.
     */
    public boolean isDnAttributes()
    {
        return dnAttributes;
    }


    /**
     * Set the dnAttributes flag
     * 
     * @param dnAttributes The dnAttributes to set.
     */
    public void setDnAttributes( boolean dnAttributes )
    {
        this.dnAttributes = dnAttributes;
    }


    /**
     * Get the matchingRule
     * 
     * @return Returns the matchingRule.
     */
    public String getMatchingRule()
    {
        return matchingRule;
    }


    /**
     * Set the matchingRule
     * 
     * @param matchingRule The matchingRule to set.
     */
    public void setMatchingRule( String matchingRule )
    {
        this.matchingRule = matchingRule;
    }


    /**
     * Get the matchValue
     * 
     * @return Returns the matchValue.
     */
    public org.apache.directory.shared.ldap.model.entry.Value<?> getMatchValue()
    {
        return matchValue;
    }


    /**
     * Set the matchValue
     * 
     * @param matchValue The matchValue to set.
     */
    public void setMatchValue( org.apache.directory.shared.ldap.model.entry.Value<?> matchValue )
    {
        this.matchValue = matchValue;
    }


    /**
     * Get the type
     * 
     * @return Returns the type.
     */
    public String getType()
    {
        return type;
    }


    /**
     * Set the type
     * 
     * @param type The type to set.
     */
    public void setType( String type )
    {
        this.type = type;
    }


    /**
     * Return a String representing an extended filter as of RFC 2254
     * 
     * @return An Extened Filter String
     */
    public String toString()
    {

        StringBuffer sb = new StringBuffer();

        if ( type != null )
        {
            sb.append( type );
        }

        if ( dnAttributes )
        {
            sb.append( ":dn" );
        }

        if ( matchingRule == null )
        {

            if ( type == null )
            {
                return "Extended Filter wrong syntax";
            }
        }
        else
        {
            sb.append( ':' ).append( matchingRule );
        }

        sb.append( ":=" ).append( matchValue );

        return sb.toString();
    }
}
