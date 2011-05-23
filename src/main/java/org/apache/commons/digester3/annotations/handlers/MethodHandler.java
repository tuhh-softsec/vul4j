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
package org.apache.commons.digester3.annotations.handlers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.commons.digester3.Rule;
import org.apache.commons.digester3.annotations.AnnotationRuleProvider;
import org.apache.commons.digester3.annotations.DigesterLoaderHandler;
import org.apache.commons.digester3.annotations.DigesterLoadingException;
import org.apache.commons.digester3.annotations.DigesterRule;
import org.apache.commons.digester3.annotations.DigesterRuleList;
import org.apache.commons.digester3.annotations.FromAnnotationsRuleSet;
import org.apache.commons.digester3.annotations.rules.CreationRule;
import org.apache.commons.digester3.annotations.utils.AnnotationUtils;

/**
 * Handler that takes care to create the {@link org.apache.commons.digester3.annotations.providers.SetNextRuleProvider}
 * and {@link org.apache.commons.digester3.annotations.providers.SetRootRuleProvider}.
 * 
 * @since 2.1
 */
public final class MethodHandler
    implements DigesterLoaderHandler<Annotation, Method>
{

    /**
     * The default args size the method has to have in order to be analyzed.
     */
    private static final int SUPPORTED_ARGS = 1;

    /**
     * {@inheritDoc}
     */
    public void handle( Annotation annotation, Method element, FromAnnotationsRuleSet ruleSet )
    {
        if ( SUPPORTED_ARGS != element.getParameterTypes().length )
        {
            DigesterRule rule = annotation.annotationType().getAnnotation( DigesterRule.class );

            throw new DigesterLoadingException( "Methods annotated with digester annotation rule @"
                + rule.reflectsRule().getName() + " must have just one argument" );
        }

        Object explicitTypesObject = AnnotationUtils.getAnnotationValue( annotation );
        if ( explicitTypesObject == null || !explicitTypesObject.getClass().isArray()
            || Class.class != explicitTypesObject.getClass().getComponentType() )
        {
            throw new DigesterLoadingException( "Impossible to apply this handler, @" + annotation.getClass().getName()
                + ".value() has to be of type 'Class<?>[]'" );
        }

        Class<?>[] explicitTypes = (Class<?>[]) explicitTypesObject;
        Class<?> paramType = element.getParameterTypes()[0];

        if ( explicitTypes.length > 0 )
        {
            for ( Class<?> explicitType : explicitTypes )
            {
                if ( !paramType.isAssignableFrom( explicitType ) )
                {
                    throw new DigesterLoadingException( "Impossible to handle annotation " + annotation + " on method "
                        + element.toGenericString() + ", " + explicitType.getName() + " has to be a "
                        + paramType.getName() );
                }

                this.doHandle( annotation, element, explicitType, ruleSet );
            }
        }
        else
        {
            this.doHandle( annotation, element, paramType, ruleSet );
        }
    }

    private void doHandle( Annotation methodAnnotation, Method method, Class<?> type, FromAnnotationsRuleSet ruleSet )
    {
        if ( type.isInterface() && Modifier.isAbstract( type.getModifiers() ) )
        {
            throw new DigesterLoadingException( "Impossible to proceed analyzing " + methodAnnotation
                + ", specified type '" + type.getName() + "' is an interface/abstract" );
        }

        for ( Annotation annotation : type.getAnnotations() )
        {
            this.doHandle( methodAnnotation, annotation, method, type, ruleSet );
        }
    }

    @SuppressWarnings( "unchecked" )
    private <A extends Annotation, R extends Rule> void doHandle( A methodAnnotation, Annotation annotation,
                                                                  Method method, Class<?> type,
                                                                  FromAnnotationsRuleSet ruleSet )
    {
        if ( annotation.annotationType().isAnnotationPresent( DigesterRule.class )
            && annotation.annotationType().isAnnotationPresent( CreationRule.class ) )
        {
            ruleSet.addRules( type );

            DigesterRule digesterRule = methodAnnotation.annotationType().getAnnotation( DigesterRule.class );
            Class<? extends AnnotationRuleProvider<A, Method, R>> providerType =
                (Class<? extends AnnotationRuleProvider<A, Method, R>>) digesterRule.providedBy();
            ruleSet.addRuleProvider( AnnotationUtils.getAnnotationPattern( annotation ), providerType,
                                     methodAnnotation, method );
        }
        else if ( annotation.annotationType().isAnnotationPresent( DigesterRuleList.class ) )
        {
            // check if it is one of the *.List annotation
            Annotation[] annotations = AnnotationUtils.getAnnotationsArrayValue( annotation );
            if ( annotations != null )
            {
                // if it is an annotations array, process them
                for ( Annotation ptr : annotations )
                {
                    this.doHandle( methodAnnotation, ptr, method, type, ruleSet );
                }
            }
        }
    }

}
