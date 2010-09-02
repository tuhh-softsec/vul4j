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
package org.apache.commons.digester.annotations.handlers;

import java.lang.reflect.Field;

import org.apache.commons.digester.annotations.DigesterLoaderHandler;
import org.apache.commons.digester.annotations.FromAnnotationsRuleSet;
import org.apache.commons.digester.annotations.providers.SetPropertiesRuleProvider;
import org.apache.commons.digester.annotations.rules.SetProperty;

/**
 * Handler that takes care to create the {@link SetPropertiesRuleProvider}.
 *
 * @since 2.1
 */
public final class SetPropertiesLoaderHandler implements DigesterLoaderHandler<SetProperty, Field> {

    /**
     * {@inheritDoc}
     */
    public void handle(SetProperty annotation, Field element, FromAnnotationsRuleSet ruleSet) {
        SetPropertiesRuleProvider ruleProvider =
            ruleSet.getProvider(annotation.pattern(), SetPropertiesRuleProvider.class);

        if (ruleProvider == null) {
            ruleProvider = new SetPropertiesRuleProvider();
            ruleSet.addRuleProvider(annotation.pattern(), ruleProvider);
        }

        ruleProvider.addAlias(annotation, element);
    }

}
