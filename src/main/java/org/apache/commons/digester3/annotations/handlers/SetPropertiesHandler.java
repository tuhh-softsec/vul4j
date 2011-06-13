package org.apache.commons.digester3.annotations.handlers;

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

import java.lang.reflect.Field;

import org.apache.commons.digester3.annotations.AnnotationHandler;
import org.apache.commons.digester3.annotations.rules.SetProperty;
import org.apache.commons.digester3.binder.RulesBinder;
import org.apache.commons.digester3.binder.SetPropertiesBuilder;

/**
 * {@link SetProperty} handler.
 *
 * @since 3.0
 */
public final class SetPropertiesHandler
    implements AnnotationHandler<SetProperty, Field>
{

    /**
     * {@inheritDoc}
     */
    public void handle( SetProperty annotation, Field element, RulesBinder rulesBinder )
    {
        SetPropertiesBuilder builder = rulesBinder
            .forPattern( annotation.pattern() )
            .withNamespaceURI( annotation.namespaceURI() )
            .setProperties();

        if ( annotation.attributeName() != null && annotation.attributeName().length() > 0
            && !element.getName().equals( annotation.attributeName() ) )
        {
            builder.addAlias( annotation.attributeName(), element.getName() );
        }
    }

}
