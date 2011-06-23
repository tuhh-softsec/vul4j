package org.apache.commons.digester3;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import static java.lang.String.format;

import org.xml.sax.Attributes;

/**
 * <p>
 * Rule implementation that saves a parameter for use by a surrounding <code>CallMethodRule<code>.
 * </p>
 * <p>
 * This parameter may be:
 * <ul>
 * <li>an arbitrary Object defined programatically, assigned when the element pattern associated with the Rule is
 * matched. See {@link #ObjectParamRule(int paramIndex, Object param)}.
 * <li>an arbitrary Object defined programatically, assigned if the element pattern AND specified attribute name are
 * matched. See {@link #ObjectParamRule(int paramIndex, String attributeName, Object param)}.
 * </ul>
 * </p>
 * 
 * @since 1.4
 */
public class ObjectParamRule
    extends Rule
{
    // ----------------------------------------------------------- Constructors
    /**
     * Construct a "call parameter" rule that will save the given Object as the parameter value.
     * 
     * @param paramIndex The zero-relative parameter number
     * @param param the parameter to pass along
     */
    public ObjectParamRule( int paramIndex, Object param )
    {
        this( paramIndex, null, param );
    }

    /**
     * Construct a "call parameter" rule that will save the given Object as the parameter value, provided that the
     * specified attribute exists.
     * 
     * @param paramIndex The zero-relative parameter number
     * @param attributeName The name of the attribute to match
     * @param param the parameter to pass along
     */
    public ObjectParamRule( int paramIndex, String attributeName, Object param )
    {
        this.paramIndex = paramIndex;
        this.attributeName = attributeName;
        this.param = param;
    }

    // ----------------------------------------------------- Instance Variables

    /**
     * The attribute which we are attempting to match
     */
    protected String attributeName = null;

    /**
     * The zero-relative index of the parameter we are saving.
     */
    protected int paramIndex = 0;

    /**
     * The parameter we wish to pass to the method call
     */
    protected Object param = null;

    // --------------------------------------------------------- Public Methods

    /**
     * {@inheritDoc}
     */
    @Override
    public void begin( String namespace, String name, Attributes attributes )
        throws Exception
    {
        Object anAttribute = null;
        Object parameters[] = getDigester().peekParams();

        if ( attributeName != null )
        {
            anAttribute = attributes.getValue( attributeName );
            if ( anAttribute != null )
            {
                parameters[paramIndex] = param;
            }
            // note -- if attributeName != null and anAttribute == null, this rule
            // will pass null as its parameter!
        }
        else
        {
            parameters[paramIndex] = param;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return format( "ObjectParamRule[paramIndex=%s, attributeName=%s, param=%s]", paramIndex, attributeName, param );
    }

}
