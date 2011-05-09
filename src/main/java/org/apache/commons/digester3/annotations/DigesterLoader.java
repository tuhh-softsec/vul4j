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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.Rule;
import org.apache.commons.digester3.RuleSet;
import org.apache.commons.digester3.annotations.handlers.DefaultLoaderHandler;
import org.apache.commons.digester3.annotations.internal.GetDeclaredFieldsPrivilegedAction;
import org.apache.commons.digester3.annotations.internal.GetDeclaredMethodsPrivilegedAction;
import org.apache.commons.digester3.annotations.internal.RuleSetCache;
import org.apache.commons.digester3.annotations.reflect.MethodArgument;
import org.apache.commons.digester3.annotations.spi.AnnotationRuleProviderFactory;
import org.apache.commons.digester3.annotations.spi.DigesterLoaderHandlerFactory;
import org.apache.commons.digester3.annotations.utils.AnnotationUtils;

/**
 * This class manages the creation of Digester instances analyzing target classes
 * annotated with digester annotations.
 *
 * @since 2.1
 */
public final class DigesterLoader {

    /**
     * In-memory LRU cache that stores already analyzed classes and relative
     * {@link RuleSet}.
     */
    private final RuleSetCache cachedRuleSet = new RuleSetCache();

    private final AnnotationRuleProviderFactory annotationRuleProviderFactory;

    private final DigesterLoaderHandlerFactory digesterLoaderHandlerFactory;

    /**
     * Creates a new {@link DigesterLoader} instance.
     *
     * @param annotationRuleProviderFactory
     * @param digesterLoaderHandlerFactory
     */
    protected DigesterLoader(AnnotationRuleProviderFactory annotationRuleProviderFactory,
            DigesterLoaderHandlerFactory digesterLoaderHandlerFactory) {
        this.annotationRuleProviderFactory = annotationRuleProviderFactory;
        this.digesterLoaderHandlerFactory = digesterLoaderHandlerFactory;
    }

    protected AnnotationRuleProviderFactory getAnnotationRuleProviderFactory() {
        return annotationRuleProviderFactory;
    }

    protected DigesterLoaderHandlerFactory getDigesterLoaderHandlerFactory() {
        return digesterLoaderHandlerFactory;
    }

    /**
     * Creates a new digester which rules are defined by analyzing the digester
     * annotations in the target class.
     *
     * @param target the class has to be analyzed.
     * @return a new Digester instance.
     */
    public Digester createDigester(final Class<?> target) {
        Digester digester = new Digester();
        digester.setClassLoader(target.getClassLoader());
        addRules(target, digester);
        return digester;
    }

    /**
     * Add rules to an already created Digester instance, analyzing the digester
     * annotations in the target class.
     *
     * @param target the class has to be analyzed.
     * @param digester the Digester instance reference.
     */
    public void addRules(final Class<?> target, final Digester digester) {
        RuleSet ruleSet = getRuleSet(target);
        ruleSet.addRuleInstances(digester);
    }

    /**
     * Builds a new {@link RuleSet} analyzing the digester annotations in the
     * target class.
     *
     * It avoids iterate the annotations analysis for already analyzed classes,
     * using an in-memory LRU cache.
     *
     * @param target the class has to be analyzed.
     * @return a new {@link RuleSet}.
     */
    public RuleSet getRuleSet(final Class<?> target) {
        if (this.cachedRuleSet.containsKey(target)) {
            return this.cachedRuleSet.get(target);
        }

        FromAnnotationsRuleSet ruleSet = new FromAnnotationsRuleSet(this);
        addRulesTo(target, ruleSet);
        this.cachedRuleSet.put(target, ruleSet);

        return ruleSet;
    }

    /**
     * Analyzes the target class and adds the {@link AnnotationRuleProvider}s to
     * the existing {@link FromAnnotationsRuleSet}.
     *
     * @param target the class has to be analyzed.
     * @param ruleSet the RuleSet where adding the providers.
     */
    public void addRulesTo(final Class<?> target, FromAnnotationsRuleSet ruleSet) {
        if (target == Object.class
                || target.isInterface()
                || ruleSet.mapsClass(target)) {
            return;
        }

        if (this.cachedRuleSet.containsKey(target)) {
            ruleSet.addRulesProviderFrom(this.cachedRuleSet.get(target));
            ruleSet.addMappedClass(target);
            return;
        }

        // current analyzed class
        handle(target, ruleSet);

        // class fields
        for (Field field : run(new GetDeclaredFieldsPrivilegedAction(target))) {
            handle(field, ruleSet);
        }

        // class methods
        for (Method method : run(new GetDeclaredMethodsPrivilegedAction(target))) {
            handle(method, ruleSet);

            // method args
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                handle(new MethodArgument(i, parameterTypes[i], parameterAnnotations[i]), ruleSet);
            }
        }

        ruleSet.addMappedClass(target);
        addRulesTo(target.getSuperclass(), ruleSet);
    }

    /**
     * Executes an analysis for each annotation present in the element.
     *
     * @param element the current element under analysis.
     * @param ruleSet the ruleSet where add providers.
     */
    private void handle(AnnotatedElement element, FromAnnotationsRuleSet ruleSet) {
        for (Annotation annotation : element.getAnnotations()) {
            handle(annotation, element, ruleSet);
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
    private <A extends Annotation, E extends AnnotatedElement, R extends Rule> void handle(A annotation,
            E element,
            FromAnnotationsRuleSet ruleSet) {
        Class<?> annotationType = annotation.annotationType();

        // check if it is one of the @*.List annotation
        if (annotationType.isAnnotationPresent(DigesterRuleList.class)) {
            Annotation[] annotations = AnnotationUtils.getAnnotationsArrayValue(annotation);
            if (annotations != null && annotations.length > 0) {
                // if it is an annotations array, process them
                for (Annotation ptr : annotations) {
                    handle(ptr, element, ruleSet);
                }
            }
        } else if (annotationType.isAnnotationPresent(DigesterRule.class)) {
            DigesterRule digesterRule = annotationType.getAnnotation(DigesterRule.class);

            if (DefaultLoaderHandler.class == digesterRule.handledBy()) {
                Class<? extends AnnotationRuleProvider<A, E, R>> providerType =
                    (Class<? extends AnnotationRuleProvider<A, E, R>>) digesterRule.providedBy();
                ruleSet.addRuleProvider(AnnotationUtils.getAnnotationPattern(annotation),
                        providerType,
                        annotation,
                        element);
            } else {
                Class<? extends DigesterLoaderHandler<Annotation, AnnotatedElement>> handlerType =
                    (Class<? extends DigesterLoaderHandler<Annotation, AnnotatedElement>>) digesterRule.handledBy();
                DigesterLoaderHandler<Annotation, AnnotatedElement> handler =
                    this.digesterLoaderHandlerFactory.newInstance(handlerType);

                // run!
                handler.handle(annotation, element, ruleSet);
            }
        }
    }

    /**
     * Perform action with AccessController.doPrivileged() if possible.
     *
     * @param action - the action to run
     * @return result of running the action
     */
    private static <T> T run(PrivilegedAction<T> action) {
        if (System.getSecurityManager() != null) {
            return AccessController.doPrivileged(action);
        }
        return action.run();
    }

}
