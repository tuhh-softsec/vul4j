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
package org.apache.commons.digester3.rule;

/**
 * <p>Rule implementation that calls a method on the (top-1) (parent)
 * object, passing the top object (child) as an argument.  It is
 * commonly used to establish parent-child relationships.</p>
 *
 * <p>This rule now supports more flexible method matching by default.
 * It is possible that this may break (some) code 
 * written against release 1.1.1 or earlier.
 * See {@link #isExactMatch()} for more details.</p> 
 *
 * <p>Note that while CallMethodRule uses commons-beanutils' data-conversion
 * functionality (ConvertUtils class) to convert parameter values into
 * the appropriate type for the parameter to the called method, this
 * rule does not. Needing to use ConvertUtils functionality when building
 * parent-child relationships is expected to be very rare; however if you 
 * do need this then instead of using this rule, create a CallMethodRule
 * specifying targetOffset of 1 in the constructor.</p>
 */
public class SetNextRule extends AbstractMethodRule {

    /**
     * Construct a "set next" rule with the specified method name.
     *
     * @param methodName Method name of the parent method to call
     * @param paramType Java class of the parent method's argument
     *  (if you wish to use a primitive type, specify the corresonding
     *  Java wrapper class instead, such as <code>java.lang.Boolean</code>
     *  for a <code>boolean</code> parameter)
     */
    public SetNextRule(String methodName, Class<?> paramType, boolean useExactMatch) {
        super(methodName, paramType, useExactMatch);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void end(String namespace, String name) throws Exception {
        this.invoke(this.getDigester().peek(1), this.getDigester().peek(0), "PARENT");
    }

}
