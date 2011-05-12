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
package org.apache.commons.digester3.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * A {@code DigesterLoaderHandler} intercept a {@code Class} visit performed by the {@link DigesterLoader} and performs
 * the {@link DigesterLoaderHandler#handle(Annotation, AnnotatedElement, FromAnnotationsRuleSet)} method.
 * 
 * @since 2.1
 */
public interface DigesterLoaderHandler<A extends Annotation, E extends AnnotatedElement>
{

    /**
     * Handles the current visited element with the related current annotation.
     * 
     * @param annotation the current visited annotation.
     * @param element the current visited element.
     * @param ruleSet the annotations {@code RuleSet} where providers have to be added.
     */
    void handle( A annotation, E element, FromAnnotationsRuleSet ruleSet );

}
