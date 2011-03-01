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
package org.apache.directory.shared.ldap.aci.protectedItem;


import org.apache.directory.shared.ldap.model.schema.AttributeType;


/**
 * An element of {@link RestrictedByItem}.
 */
public class RestrictedByElem
{
    /** The AttributeType on which the restriction is applied */
    private AttributeType attributeType;

    /** The list of allowed AttributeType values */
    private AttributeType valuesIn;


    /**
     * Creates a new instance.
     * 
     * @param attributeType the attribute type to restrict
     * @param valuesIn the attribute type only whose values are allowed in <tt>attributeType</tt>.
     */
    public RestrictedByElem( AttributeType attributeType, AttributeType valuesIn )
    {
        this.attributeType = attributeType;
        this.valuesIn = valuesIn;
    }


    /**
     * Gets the attribute type to restrict.
     *
     * @return the attribute type
     */
    public AttributeType getAttributeType()
    {
        return attributeType;
    }


    /**
     * Gets the attribute type only whose values are allowed in
     * <tt>attributeType</tt>.
     *
     * @return the list of allowed AttributeType values
     */
    public AttributeType getValuesIn()
    {
        return valuesIn;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        int hash = 37;
        hash = hash * 17 + attributeType.hashCode();
        hash = hash * 17 + valuesIn.hashCode();
        return hash;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object o )
    {
        if (o == null)
        {
            return false;
        }
        
        if ( this == o )
        {
            return true;
        }
        
        if ( o instanceof RestrictedByElem )
        {
            RestrictedByElem that = ( RestrictedByElem ) o;
            if ( this.attributeType == null )
            {
                if ( that.attributeType == null )
                {
                    if ( this.valuesIn == null )
                    {
                        return that.valuesIn == null;
                    }
                    else
                    {
                        return this.valuesIn.equals( that.valuesIn );
                    }
                }
                return false;
            }
            else
            {
                if ( this.attributeType.equals( that.attributeType ))
                {
                    if ( this.valuesIn == null )
                    {
                        return that.valuesIn == null;
                    }
                    else
                    {
                        return this.valuesIn.equals( that.valuesIn );
                    }
                }
                return false;
            }
        }
        return false;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "{ type " + attributeType.getName() + ", valuesIn " + valuesIn.getName() + " }";
    }
}
