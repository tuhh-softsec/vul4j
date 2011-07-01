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
import java.util.Stack;

import org.xml.sax.Attributes;

/**
 * <p>
 * Rule implementation that saves a parameter for use by a surrounding <code>CallMethodRule<code>.
 * </p>
 * <p>
 * This parameter may be:
 * <ul>
 * <li>from an attribute of the current element See {@link #CallParamRule(int paramIndex, String attributeName)}
 * <li>from current the element body See {@link #CallParamRule(int paramIndex)}
 * <li>from the top object on the stack. See {@link #CallParamRule(int paramIndex, boolean fromStack)}
 * <li>the current path being processed (separate <code>Rule</code>). See {@link PathCallParamRule}
 * </ul>
 * </p>
 */
public class CallParamRule
    extends Rule
{

    // ----------------------------------------------------------- Constructors

    /**
     * Construct a "call parameter" rule that will save the body text of this element as the parameter value.
     * <p>
     * Note that if the element is empty the an <i>empty string</i> is passed to the target method, not null. And if
     * automatic type conversion is being applied (ie if the target function takes something other than a string as a
     * parameter) then the conversion will fail if the converter class does not accept an empty string as valid input.
     * </p>
     * 
     * @param paramIndex The zero-relative parameter number
     */
    public CallParamRule( int paramIndex )
    {
        this( paramIndex, null );
    }

    /**
     * Construct a "call parameter" rule that will save the value of the specified attribute as the parameter value.
     * 
     * @param paramIndex The zero-relative parameter number
     * @param attributeName The name of the attribute to save
     */
    public CallParamRule( int paramIndex, String attributeName )
    {
        this.paramIndex = paramIndex;
        this.attributeName = attributeName;
    }

    /**
     * Construct a "call parameter" rule.
     * 
     * @param paramIndex The zero-relative parameter number
     * @param fromStack should this parameter be taken from the top of the stack?
     */
    public CallParamRule( int paramIndex, boolean fromStack )
    {
        this.paramIndex = paramIndex;
        this.fromStack = fromStack;
    }

    /**
     * Constructs a "call parameter" rule which sets a parameter from the stack. If the stack contains too few objects,
     * then the parameter will be set to null.
     * 
     * @param paramIndex The zero-relative parameter number
     * @param stackIndex the index of the object which will be passed as a parameter. The zeroth object is the top of
     *            the stack, 1 is the next object down and so on.
     */
    public CallParamRule( int paramIndex, int stackIndex )
    {
        this.paramIndex = paramIndex;
        this.fromStack = true;
        this.stackIndex = stackIndex;
    }

    // ----------------------------------------------------- Instance Variables

    /**
     * The attribute from which to save the parameter value
     */
    protected String attributeName = null;

    /**
     * The zero-relative index of the parameter we are saving.
     */
    protected int paramIndex = 0;

    /**
     * Is the parameter to be set from the stack?
     */
    protected boolean fromStack = false;

    /**
     * The position of the object from the top of the stack
     */
    protected int stackIndex = 0;

    /**
     * Stack is used to allow nested body text to be processed. Lazy creation.
     */
    protected Stack<String> bodyTextStack;

    // --------------------------------------------------------- Public Methods

    /**
     * Set the attribute from which to save the parameter value.
     *
     * @param attributeName The attribute from which to save the parameter value
     * @since 3.0
     */
    public void setAttributeName( String attributeName )
    {
        this.attributeName = attributeName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void begin( String namespace, String name, Attributes attributes )
        throws Exception
    {
        Object param = null;

        if ( attributeName != null )
        {

            param = attributes.getValue( attributeName );

        }
        else if ( fromStack )
        {

            param = getDigester().peek( stackIndex );

            if ( getDigester().getLogger().isDebugEnabled() )
            {
                getDigester()
                    .getLogger().debug( format( "[CallParamRule]{%s} Save from stack; from stack?%s; object=%s",
                                                getDigester().getMatch(), fromStack, param ) );
            }
        }

        // Have to save the param object to the param stack frame here.
        // Can't wait until end(). Otherwise, the object will be lost.
        // We can't save the object as instance variables, as
        // the instance variables will be overwritten
        // if this CallParamRule is reused in subsequent nesting.

        if ( param != null )
        {
            Object parameters[] = getDigester().peekParams();
            parameters[paramIndex] = param;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void body( String namespace, String name, String text )
        throws Exception
    {
        if ( attributeName == null && !fromStack )
        {
            // We must wait to set the parameter until end
            // so that we can make sure that the right set of parameters
            // is at the top of the stack
            if ( bodyTextStack == null )
            {
                bodyTextStack = new Stack<String>();
            }
            bodyTextStack.push( text.trim() );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void end( String namespace, String name )
    {
        if ( bodyTextStack != null && !bodyTextStack.empty() )
        {
            // what we do now is push one parameter onto the top set of parameters
            Object parameters[] = getDigester().peekParams();
            parameters[paramIndex] = bodyTextStack.pop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return format( "CallParamRule[paramIndex=%s, attributeName=%s, from stack=%s]",
                       paramIndex, attributeName, fromStack );
    }

}
