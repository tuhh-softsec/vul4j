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
package org.apache.commons.digester3.annotations.providers;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.digester3.SetPropertiesRule;
import org.apache.commons.digester3.annotations.AnnotationRuleProvider;
import org.apache.commons.digester3.annotations.rules.SetProperty;

/**
 * Provides instances of {@code SetPropertiesRule}.
 *
 * @since 2.1
 */
public final class SetPropertiesRuleProvider implements AnnotationRuleProvider<SetProperty, Field, SetPropertiesRule> {

    /**
     * The data structure that stores the aliases.
     */
    private final Map<String, String> aliases = new HashMap<String, String>();

    /**
     * {@inheritDoc}
     */
    public void init(SetProperty annotation, Field element) {
        this.addAlias(annotation, element);
    }

    /**
     * Adds a new alias attribute/property name; if the attribute name is not
     * specified, the alias will be considered as property name identity.
     *
     * @param annotation the {@link SetProperty} reference.
     * @param element the annotated element reference.
     */
    public void addAlias(SetProperty annotation, Field element) {
        String attributeName = annotation.attributeName();
        String propertyName = element.getName();

        if (attributeName.length() > 0) {
            this.aliases.put(attributeName, propertyName);
        } else {
            this.aliases.put(propertyName, propertyName);
        }
    }

    /**
     * {@inheritDoc}
     */
    public SetPropertiesRule get() {
        String[] attributeNames = new String[this.aliases.size()];
        String[] propertyNames = new String[this.aliases.size()];

        int i = 0;
        for (Entry<String, String> alias : this.aliases.entrySet()) {
            attributeNames[i] = alias.getKey();
            propertyNames[i++] = alias.getValue();
        }

        return new SetPropertiesRule(attributeNames, propertyNames);
    }

}
