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
public class PresentFilter extends Filter
{
    /** The attribute description. */
    private String attributeDescription;


    /**
     * Get the attribute
     * 
     * @return Returns the attributeDescription.
     */
    public String getAttributeDescription()
    {
        return attributeDescription;
    }


    /**
     * Set the attributeDescription
     * 
     * @param attributeDescription The attributeDescription to set.
     */
    public void setAttributeDescription( String attributeDescription )
    {
        this.attributeDescription = attributeDescription;
    }


    /**
     * Return a string compliant with RFC 2254 representing a Present filter
     * 
     * @return The Present filter string
     */
    public String toString()
    {

        StringBuffer sb = new StringBuffer();

        sb.append( attributeDescription ).append( "=*" );

        return sb.toString();
    }
}
