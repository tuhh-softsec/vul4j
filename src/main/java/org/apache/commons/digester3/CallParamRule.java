/* $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.digester3;

import java.util.Stack;

import org.xml.sax.Attributes;

/**
 * <p>Rule implementation that saves a parameter for use by a surrounding 
 * <code>CallMethodRule<code>.</p>
 *
 * <p>This parameter may be:
 * <ul>
 * <li>from an attribute of the current element
 * See {@link #CallParamRule(int paramIndex, String attributeName)}
 * <li>from current the element body
 * See {@link #CallParamRule(int paramIndex)}
 * <li>from the top object on the stack. 
 * See {@link #CallParamRule(int paramIndex, boolean fromStack)}
 * <li>the current path being processed (separate <code>Rule</code>). 
 * See {@link PathCallParamRule}
 * </ul>
 * </p>
 */
public class CallParamRule extends Rule {

    /**
     * The zero-relative index of the parameter we are saving.
     */
    private final int paramIndex;

    /**
     * The attribute from which to save the parameter value
     */
    private final String attributeName; // @Nullable

    /**
     * Is the parameter to be set from the stack?
     */
    private final boolean fromStack;

    /**
     * The position of the object from the top of the stack
     */
    private final int stackIndex;

    /** 
     * Stack is used to allow nested body text to be processed.
     * Lazy creation.
     */
    private final Stack<String> bodyTextStack = new Stack<String>();

    /**
     * Constructs a "call parameter" rule which sets a parameter from the stack.
     * If the stack contains too few objects, then the parameter will be set to null.
     *
     * @param paramIndex The zero-relative parameter number
     * @param fromStack Should this parameter be taken from the top of the stack?
     * @param stackIndex the index of the object which will be passed as a parameter
     * The zeroth object is the top of the stack, 1 is the next object down and so on
     * @param attributeName The name of the attribute to save
     */
    public CallParamRule(int paramIndex, boolean fromStack, int stackIndex, /* @Nullable */ String attributeName) {
        this.paramIndex = paramIndex;
        this.fromStack = fromStack;
        this.stackIndex = stackIndex;
        this.attributeName = attributeName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        Object param = null;

        if (this.attributeName != null) {
            param = attributes.getValue(this.attributeName);
        } else if (this.fromStack) {
            param = this.getDigester().peek(stackIndex);

            if (this.getDigester().getLog().isDebugEnabled()) {
                this.getDigester().getLog().debug(
                        String.format("[CallParamRule]{%s} Save from stack; from stack?%s; object=%s",
                                this.getDigester().getMatch(),
                                this.fromStack,
                                param));
            }
        }

        // Have to save the param object to the param stack frame here.
        // Can't wait until end(). Otherwise, the object will be lost.
        // We can't save the object as instance variables, as 
        // the instance variables will be overwritten
        // if this CallParamRule is reused in subsequent nesting.

        if (param != null) {
            Object parameters[] = (Object[]) this.getDigester().peekParams();
            parameters[this.paramIndex] = param;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void body(String namespace, String name, String text) throws Exception {
        if (this.attributeName == null && !this.fromStack) {
            // We must wait to set the parameter until end
            // so that we can make sure that the right set of parameters
            // is at the top of the stack
            this.bodyTextStack.push(text.trim());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void end(String namespace, String name) {
        if (this.bodyTextStack != null && !this.bodyTextStack.empty()) {
            // what we do now is push one parameter onto the top set of parameters
            Object parameters[] = (Object[]) this.getDigester().peekParams();
            parameters[this.paramIndex] = this.bodyTextStack.pop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("CallParamRule[paramIndex=%s, attributeName=%s, from stack=%s]",
                this.paramIndex,
                this.attributeName,
                this.fromStack);
    }

}
