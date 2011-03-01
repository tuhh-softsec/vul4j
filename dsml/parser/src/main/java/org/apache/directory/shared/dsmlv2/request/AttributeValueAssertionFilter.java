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
 * Object to store the filter. A filter is seen as a tree with a root.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AttributeValueAssertionFilter extends Filter
{
    /** The assertion. */
    private AttributeValueAssertion assertion;

    /** The filter type */
    private int filterType;


    /**
     * The constructor.
     * 
     * @param filterType The filter type
     */
    public AttributeValueAssertionFilter( int filterType )
    {
        this.filterType = filterType;
    }


    /**
     * Get the assertion
     * 
     * @return Returns the assertion.
     */
    public AttributeValueAssertion getAssertion()
    {
        return assertion;
    }


    /**
     * Set the assertion
     * 
     * @param assertion The assertion to set.
     */
    public void setAssertion( AttributeValueAssertion assertion )
    {
        this.assertion = assertion;
    }


    /**
     * Get the filter type
     * 
     * @return Returns the filterType.
     */
    public int getFilterType()
    {
        return filterType;
    }


    /**
     * Set the filter type
     * 
     * @param filterType The filterType to set.
     */
    public void setFilterType( int filterType )
    {
        this.filterType = filterType;
    }


    /**
     * Return a string compliant with RFC 2254 representing an item filter
     * 
     * @return The item filter string
     */
    public String toString()
    {
        return assertion != null ? assertion.toStringRFC2254( filterType ) : "";
    }
}
