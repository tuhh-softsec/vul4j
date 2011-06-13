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
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;

import org.apache.commons.digester3.Rule;

/**
 * Meta-annotation that marks an annotation as part of commons-digester.
 * 
 * @since 2.1
 */
@Documented
@Target( ElementType.ANNOTATION_TYPE )
@Retention( RetentionPolicy.RUNTIME )
public @interface DigesterRule
{

    /**
     * The reflected commons-digester rule.
     */
    Class<? extends Rule> reflectsRule();

    /**
     * The handler that takes care on converting this annotation in the related
     * {@link AnnotationHandler} and adds it to the {@link FromAnnotationsRuleModule}
     */
    Class<? extends AnnotationHandler<? extends Annotation, ? extends AnnotatedElement>> handledBy();

}
