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
package org.apache.commons.digester3.annotations.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import org.apache.commons.digester3.Rule;
import org.apache.commons.digester3.annotations.AnnotationRuleProvider;
import org.apache.commons.digester3.annotations.DigesterLoadingException;
import org.apache.commons.digester3.annotations.spi.AnnotationRuleProviderFactory;

/**
 * Default {@link AnnotationRuleProviderFactory} implementation.
 * 
 * @since 2.1
 */
public final class DefaultAnnotationRuleProviderFactory
    implements AnnotationRuleProviderFactory
{

    /**
     * {@inheritDoc}
     */
    public <T extends AnnotationRuleProvider<? extends Annotation, ? extends AnnotatedElement, ? extends Rule>> T newInstance( Class<T> type )
        throws DigesterLoadingException
    {
        try
        {
            return type.newInstance();
        }
        catch ( Exception e )
        {
            throw new DigesterLoadingException( "An error occurred while creating '" + type + "' instance", e );
        }
    }

}
