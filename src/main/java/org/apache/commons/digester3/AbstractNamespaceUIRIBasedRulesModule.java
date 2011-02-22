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

import org.apache.commons.digester3.rulesbinder.LinkedRuleBuilder;

/**
 * A support class for RulesModule which reduces repetition and results in a more readable configuration, that
 * sets rules binding for a defined namespace URI (it can be overridden while binding).
 */
public abstract class AbstractNamespaceUIRIBasedRulesModule extends AbstractRulesModule {

    private final String namespaceURI;

    public AbstractNamespaceUIRIBasedRulesModule(/* @Nullable */ String namespaceURI) {
        this.namespaceURI = namespaceURI;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected LinkedRuleBuilder forPattern(String pattern) {
        return super.forPattern(pattern).withNamespaceURI(this.namespaceURI);
    }

}
