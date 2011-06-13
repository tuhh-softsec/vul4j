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
import org.apache.commons.digester3.annotations.rules.BeanPropertySetter;
import org.apache.commons.digester3.binder.RulesBinder;

/**
 * {@link BeanPropertySetter} handler.
 *
 * @since 3.0
 */
public final class BeanPropertySetterHandler
    implements AnnotationHandler<BeanPropertySetter, Field>
{

    /**
     * {@inheritDoc}
     */
    public void handle( BeanPropertySetter annotation, Field element, RulesBinder rulesBinder )
    {
        rulesBinder.forPattern( annotation.pattern() )
            .withNamespaceURI( annotation.namespaceURI() )
            .setBeanProperty()
            .withName( element.getName() );
    }

}
