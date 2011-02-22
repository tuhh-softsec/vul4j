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
 * <p>Rule implementation that calls a "set parent" method on the top (child)
 * object, passing the (top-1) (parent) object as an argument.</p>
 *
 * <p>This rule now supports more flexible method matching by default.
 * It is possible that this may break (some) code 
 * written against release 1.1.1 or earlier.
 * See {@link #isExactMatch()} for more details.</p>
 */
public class SetTopRule extends AbstractMethodRule {

    /**
     * Construct a "set parent" rule with the specified method name.
     *
     * @param methodName Method name of the "set parent" method to call
     * @param paramType Java class of the "set parent" method's argument
     */
    public SetTopRule(String methodName, Class<?> paramType, boolean useExactMatch) {
        super(methodName, paramType, useExactMatch);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void end(String namespace, String name) throws Exception {
        this.invoke(this.getDigester().peek(0), this.getDigester().peek(1), "CHILD");
    }

}
