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

import org.apache.commons.digester3.SetNestedPropertiesRule;

/**
 * Builder chained when invoking {@link LinkedRuleBuilder#setNestedProperties()}.
 */
public interface NestedPropertiesBuilder extends BackToLinkedRuleBuilder<SetNestedPropertiesRule> {

    /**
     * Allows ignore a matching element.
     *
     * @param elementName The child xml element to be ignored
     * @return this builder instance
     */
    NestedPropertiesBuilder ignoreElement(String elementName);

    /**
     * Allows element2property mapping to be overridden.
     *
     * @param elementName The child xml element to match
     * @param propertyName The java bean property to be assigned the value
     * @return this builder instance
     */
    NestedPropertiesBuilder addAlias(String elementName, String propertyName);

    /**
     * When set to true, any text within child elements will have leading
     * and trailing whitespace removed before assignment to the target
     * object.
     *
     * @param trimData
     * @return this builder instance
     */
    NestedPropertiesBuilder trimData(boolean trimData);

    /**
     * 
     *
     * @param allowUnknownChildElements
     * @return
     */
    NestedPropertiesBuilder allowUnknownChildElements(boolean allowUnknownChildElements);

}
