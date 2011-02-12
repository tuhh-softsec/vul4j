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
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.apache.commons.digester3.DigesterLoadingException;
import org.apache.commons.digester3.Rule;
import org.apache.commons.digester3.RulesBinder;
import org.apache.commons.digester3.RulesModule;
import org.apache.commons.digester3.annotations.reflect.MethodArgument;
import org.apache.commons.digester3.annotations.spi.AnnotationHandlerFactory;
import org.apache.commons.digester3.annotations.utils.Annotations;

/**
 * 
 */
public final class FromAnnotationsRuleModule implements RulesModule {

    private static final String JAVA_PACKAGE = "java";

    private final Class<?> bindingClass;

    private final AnnotationHandlerFactory digesterLoaderHandlerFactory;

    private WithMemoryRulesBinder rulesBinder;

    public FromAnnotationsRuleModule(final Class<?> bindingClass) {
        this(bindingClass, new DefaultAnnotationHandlerFactory());
    }

    public FromAnnotationsRuleModule(final Class<?> bindingClass,
            final AnnotationHandlerFactory digesterLoaderHandlerFactory) {
        if (bindingClass == null) {
            throw new DigesterLoadingException("Parameter 'bindingClass' must be not null");
        }
        if (digesterLoaderHandlerFactory == null) {
            throw new DigesterLoadingException("Parameter 'digesterLoaderHandlerFactory' must be not null");
        }
        this.bindingClass = bindingClass;
        this.digesterLoaderHandlerFactory = digesterLoaderHandlerFactory;
    }

    protected Class<?> getBindingClass() {
        return this.bindingClass;
    }

    /**
     * {@inheritDoc}
     */
    public void configure(RulesBinder rulesBinder) {
        if (rulesBinder instanceof WithMemoryRulesBinder) {
            this.rulesBinder = (WithMemoryRulesBinder) rulesBinder;
        } else {
            this.rulesBinder = new WithMemoryRulesBinder(rulesBinder);
        }

        try {
            this.installRulesFrom(this.bindingClass);
        } finally {
            this.rulesBinder = null;
        }
    }

    /**
     * 
     *
     * IMPLEMENTATION NOTE: this method MUST NOT be called before {@link #configure()} is invoked!!!
     *
     * @param type
     */
    private void installRulesFrom(final Class<?> type) {
        if (type == null
                || type.getPackage().getName().startsWith(JAVA_PACKAGE)
                || this.rulesBinder.isAlreadyBound(type)) {
            return;
        }

        // TYPE
        this.visitElements(type);

        if (!type.isInterface()) {
            // CONSTRUCTOR
            this.visitElements(new PrivilegedAction<Constructor<?>[]>() {
                public Constructor<?>[] run() {
                    return type.getDeclaredConstructors();
                }
            });

            // FIELD
            this.visitElements(new PrivilegedAction<Field[]>() {
                public Field[] run() {
                    return type.getDeclaredFields();
                }
            });
        }

        // METHOD
        this.visitElements(new PrivilegedAction<Method[]>() {
            public Method[] run() {
                return type.getDeclaredMethods();
            }
        });

        this.rulesBinder.markAsBound(type);
        this.installRulesFrom(type.getSuperclass());
    }

    /**
     * 
     *
     * @param <AE>
     * @param action
     */
    private <AE extends AnnotatedElement> void visitElements(PrivilegedAction<AE[]> action) {
        AE[] annotatedElements = null;
        if (System.getSecurityManager() != null) {
            annotatedElements = AccessController.doPrivileged(action);
        } else {
            annotatedElements = action.run();
        }
        this.visitElements(annotatedElements);
    }

    /**
     * 
     *
     * @param annotatedElements
     */
    private void visitElements(AnnotatedElement...annotatedElements) {
        for (AnnotatedElement element : annotatedElements) {
            for (Annotation annotation : element.getAnnotations()) {
                this.handle(annotation, element);
            }

            if (element instanceof Method) {
                // method args
                Method method = (Method) element;

                Annotation[][] parameterAnnotations = method.getParameterAnnotations();
                Class<?>[] parameterTypes = method.getParameterTypes();
                for (int i = 0; i < parameterTypes.length; i++) {
                    this.visitElements(new MethodArgument(i, parameterTypes[i], parameterAnnotations[i]));
                }
            }
        }
    }

    /**
     * Handles the current visited element and related annotation, invoking the
     * right handler putting the rule provider in the rule set.
     *
     * @param annotation the current visited annotation.
     * @param element the current visited element.
     */
    @SuppressWarnings("unchecked")
    private <A extends Annotation, E extends AnnotatedElement, R extends Rule> void handle(A annotation, E element) {
        Class<?> annotationType = annotation.annotationType();

        // check if it is one of the @*.List annotation
        if (annotationType.isAnnotationPresent(DigesterRuleList.class)) {
            Annotation[] annotations = Annotations.getAnnotationsArrayValue(annotation);
            if (annotations != null && annotations.length > 0) {
                // if it is an annotations array, process them
                for (Annotation ptr : annotations) {
                    this.handle(ptr, element);
                }
            }
        } else if (annotationType.isAnnotationPresent(DigesterRule.class)) {
            DigesterRule digesterRule = annotationType.getAnnotation(DigesterRule.class);

            // the default behavior if the handler is not specified
            Class<? extends AnnotationHandler<Annotation, AnnotatedElement>> handlerType =
                (Class<? extends AnnotationHandler<Annotation, AnnotatedElement>>) digesterRule.handledBy();
            try {
                AnnotationHandler<Annotation, AnnotatedElement> handler =
                    this.digesterLoaderHandlerFactory.newInstance(handlerType);

                // run!
                handler.handle(annotation, element, this.rulesBinder);
            } catch (Exception e) {
                this.rulesBinder.addError(e);
            }
        }
    }

}
