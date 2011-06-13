package org.apache.commons.digester3.annotations;

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

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * An object capable of providing instances of {@link AnnotationHandler}.
 *
 * @since 3.0
 */
public interface AnnotationHandlerFactory
{

    /**
     * Return an instance of the specified type.
     *
     * @param <H> The {@link AnnotationHandler} type has to be created
     * @param type the class of the object to be returned.
     * @return an instance of the specified class.
     * @throws Exception if any error occurs while creating the {@link AnnotationHandler} instance.
     */
    <H extends AnnotationHandler<? extends Annotation, ? extends AnnotatedElement>> H newInstance( Class<H> type )
        throws Exception;

}
