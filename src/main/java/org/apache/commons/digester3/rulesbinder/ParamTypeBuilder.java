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
package org.apache.commons.digester3.rulesbinder;

import org.apache.commons.digester3.Rule;

/**
 * Builder chained when invoking {@link LinkedRuleBuilder#setNext(String)},
 * {@link LinkedRuleBuilder#setRoot(String)} or {@link LinkedRuleBuilder#setTop(String)}.
 */
public interface ParamTypeBuilder<R extends Rule> extends BackToLinkedRuleBuilder<R> {

    /**
     * Sets the Java class of the method's argument.
     * 
     * If you wish to use a primitive type, specify the corresonding
     * Java wrapper class instead, such as {@code java.lang.Boolean}
     * for a {@code boolean} parameter.
     *
     * @param paramType The Java class of the method's argument
     * @return this builder instance
     */
    ParamTypeBuilder<R> withParameterType(Class<?> paramType);

    /**
     * Sets the Java class name of the method's argument.
     * 
     * If you wish to use a primitive type, specify the corresonding
     * Java wrapper class instead, such as {@code java.lang.Boolean}
     * for a {@code boolean} parameter.
     *
     * @param paramType The Java class name of the method's argument
     * @return this builder instance
     */
    ParamTypeBuilder<R> withParameterType(String paramType);

    /**
     * Sets exact matching being used.
     *
     * @param useExactMatch The exact matching being used
     * @return this builder instance
     */
    ParamTypeBuilder<R> useExactMatch(boolean useExactMatch);

}
