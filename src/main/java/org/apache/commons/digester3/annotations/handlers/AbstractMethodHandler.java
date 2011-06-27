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

import static org.apache.commons.digester3.annotations.utils.AnnotationUtils.getAnnotationNamespaceURI;
import static org.apache.commons.digester3.annotations.utils.AnnotationUtils.getAnnotationPattern;
import static org.apache.commons.digester3.annotations.utils.AnnotationUtils.getAnnotationValue;
import static org.apache.commons.digester3.annotations.utils.AnnotationUtils.getAnnotationsArrayValue;
import static org.apache.commons.digester3.annotations.utils.AnnotationUtils.getFireOnBegin;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.commons.digester3.annotations.AnnotationHandler;
import org.apache.commons.digester3.annotations.DigesterRule;
import org.apache.commons.digester3.annotations.DigesterRuleList;
import org.apache.commons.digester3.annotations.FromAnnotationsRuleModule;
import org.apache.commons.digester3.annotations.rules.CreationRule;
import org.apache.commons.digester3.binder.RulesBinder;

/**
 * Handler that takes care to create the
 * {@link org.apache.commons.digester3.annotations.rules.SetNext}
 * and
 * {@link org.apache.commons.digester3.annotations.rules.SetRoot}.
 *
 * @since 3.0
 */
abstract class AbstractMethodHandler<A extends Annotation> implements AnnotationHandler<A, Method>
{

    /**
     * The default args size the method has to have in order to be analyzed.
     */
    private static final int SUPPORTED_ARGS = 1;

    /**
     * {@inheritDoc}
     */
    public void handle( A annotation, Method element, RulesBinder rulesBinder )
    {
        if ( SUPPORTED_ARGS != element.getParameterTypes().length )
        {
            DigesterRule rule = annotation.annotationType().getAnnotation( DigesterRule.class );

            rulesBinder.addError( "Methods annotated with digester annotation rule @%s must have just one argument",
                                  rule.reflectsRule().getName() );
            return;
        }

        Object explicitTypesObject = getAnnotationValue( annotation );
        if ( explicitTypesObject == null || !explicitTypesObject.getClass().isArray()
            || Class.class != explicitTypesObject.getClass().getComponentType() )
        {
            rulesBinder.addError( "Impossible to apply this handler, @%s.value() has to be of type 'Class<?>[]'",
                                  annotation.getClass().getName() );
            return;
        }

        Class<?>[] explicitTypes = (Class<?>[]) explicitTypesObject;
        Class<?> paramType = element.getParameterTypes()[0];
        boolean fireOnBegin = getFireOnBegin( annotation );

        if ( explicitTypes.length > 0 )
        {
            for ( Class<?> explicitType : explicitTypes )
            {
                if ( !paramType.isAssignableFrom( explicitType ) )
                {
                    rulesBinder.addError( "Impossible to handle annotation %s on method, %s has to be a %s",
                                          annotation, element.toGenericString(), explicitType.getName(),
                                          paramType.getName() );
                    return;
                }

                doHandle( annotation, element, explicitType, fireOnBegin, rulesBinder );
            }
        }
        else
        {
            doHandle( annotation, element, paramType, fireOnBegin, rulesBinder );
        }
    }

    private void doHandle( A methodAnnotation, Method method, Class<?> type, boolean fireOnBegin,
                           RulesBinder rulesBinder )
    {
        if ( type.isInterface() && Modifier.isAbstract( type.getModifiers() ) )
        {
            rulesBinder.addError( "Impossible to proceed analyzing %s, specified type '%s' is an interface/abstract",
                                  methodAnnotation, type.getName() );
            return;
        }

        for ( Annotation annotation : type.getAnnotations() )
        {
            doHandle( methodAnnotation, annotation, method, type, fireOnBegin, rulesBinder );
        }
    }

    private void doHandle( A methodAnnotation, Annotation annotation, Method method, final Class<?> type,
                           boolean fireOnBegin, RulesBinder rulesBinder )
    {
        if ( annotation.annotationType().isAnnotationPresent( DigesterRule.class )
            && annotation.annotationType().isAnnotationPresent( CreationRule.class ) )
        {
            rulesBinder.install( new FromAnnotationsRuleModule()
            {

                @Override
                protected void configureRules()
                {
                    bindRulesFrom( type );
                }

            } );

            String pattern = getAnnotationPattern( annotation );
            String namespaceURI = getAnnotationNamespaceURI( annotation );
            doBind( pattern, namespaceURI, method, type, fireOnBegin, rulesBinder );
        }
        else if ( annotation.annotationType().isAnnotationPresent( DigesterRuleList.class ) )
        {
            // check if it is one of the *.List annotation
            Annotation[] annotations = getAnnotationsArrayValue( annotation );
            if ( annotations != null )
            {
                // if it is an annotations array, process them
                for ( Annotation ptr : annotations )
                {
                    doHandle( methodAnnotation, ptr, method, type, fireOnBegin, rulesBinder );
                }
            }
        }
    }

    protected abstract void doBind( String pattern, String namespaceURI, Method method, Class<?> type,
                                    boolean fireOnBegin, RulesBinder rulesBinder );

}
