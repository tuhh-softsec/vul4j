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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.Rule;
import org.apache.commons.digester3.RuleSet;

/**
 * A {@link RuleSet} implementation that's able to inject {@link Rule}s created with the annotations analysis.
 * 
 * @since 2.1
 */
public final class FromAnnotationsRuleSet
    implements RuleSet
{

    /**
     * The data structure that stores the patterns/{@link AnnotationRuleProvider} pairs.
     */
    private final Map<String, List<AnnotationRuleProvider<Annotation, AnnotatedElement, Rule>>> rules =
        new LinkedHashMap<String, List<AnnotationRuleProvider<Annotation, AnnotatedElement, Rule>>>();

    /**
     * Maintains all the classes that this RuleSet produces mapping for.
     */
    private final Set<Class<?>> mappedClasses = new HashSet<Class<?>>();

    private final DigesterLoader digesterLoader;

    /**
     * The namespace URI.
     */
    private volatile String namespaceURI;

    /**
     * Created a new {@code FromAnnotationsRuleSet} instance.
     * 
     * @param digesterLoader the parent DigesterLoader.
     */
    protected FromAnnotationsRuleSet( DigesterLoader digesterLoader )
    {
        this.digesterLoader = digesterLoader;
    }

    /**
     * {@inheritDoc}
     */
    public void addRuleInstances( Digester digester )
    {
        String pattern;
        Rule rule;
        for ( Entry<String, List<AnnotationRuleProvider<Annotation, AnnotatedElement, Rule>>> entry : this.rules.entrySet() )
        {
            pattern = entry.getKey();
            for ( AnnotationRuleProvider<Annotation, AnnotatedElement, Rule> provider : entry.getValue() )
            {
                rule = provider.get();
                if ( this.namespaceURI != null )
                {
                    rule.setNamespaceURI( this.namespaceURI );
                }
                digester.addRule( pattern, rule );
            }
        }
    }

    /**
     * Analyzes the target class and adds the {@link AnnotationRuleProvider}s to this {@link FromAnnotationsRuleSet}.
     * 
     * @param target the class has to be analyzed.
     */
    public void addRules( Class<?> target )
    {
        this.digesterLoader.addRulesTo( target, this );
    }

    /**
     * Builds and register an {@link AnnotationRuleProvider} for a specific pattern.
     * 
     * @param <T> the {@link AnnotationRuleProvider} type.
     * @param pattern the pattern has to be associated to the rule provider.
     * @param klass the {@link AnnotationRuleProvider} type has to be instantiated.
     * @param annotation the current visited annotation.
     * @param element the current visited element.
     */
    public <A extends Annotation, E extends AnnotatedElement, R extends Rule, T extends AnnotationRuleProvider<A, E, R>> void addRuleProvider( String pattern,
                                                                                                                                               Class<T> klass,
                                                                                                                                               A annotation,
                                                                                                                                               E element )
    {

        T annotationRuleProvider = this.digesterLoader.getAnnotationRuleProviderFactory().newInstance( klass );
        annotationRuleProvider.init( annotation, element );
        this.addRuleProvider( pattern, annotationRuleProvider );
    }

    /**
     * Register an {@link AnnotationRuleProvider} for a specific pattern.
     * 
     * @param pattern the pattern has to be associated to the rule provider.
     * @param ruleProvider the provider that builds the digester rule.
     */
    @SuppressWarnings( "unchecked" )
    public void addRuleProvider( String pattern,
                                 AnnotationRuleProvider<? extends Annotation, ? extends AnnotatedElement, ? extends Rule> ruleProvider )
    {
        List<AnnotationRuleProvider<Annotation, AnnotatedElement, Rule>> rules;

        if ( this.rules.containsKey( pattern ) )
        {
            rules = this.rules.get( pattern );
        }
        else
        {
            rules = new ArrayList<AnnotationRuleProvider<Annotation, AnnotatedElement, Rule>>();
            this.rules.put( pattern, rules );
        }

        rules.add( (AnnotationRuleProvider<Annotation, AnnotatedElement, Rule>) ruleProvider );
    }

    /**
     * Retrieves a specific instance of the {@link AnnotationRuleProvider} for the input pattern.
     * 
     * @param <T> the {@link AnnotationRuleProvider} type
     * @param pattern the input pattern
     * @param providerClass the {@link AnnotationRuleProvider} class
     * @return an {@link AnnotationRuleProvider} for the input pattern if found, null otherwise.
     */
    public <T extends AnnotationRuleProvider<? extends Annotation, ? extends AnnotatedElement, ? extends Rule>> T getProvider( String pattern,
                                                                                                                               Class<T> providerClass )
    {

        if ( !this.rules.containsKey( pattern ) )
        {
            return null;
        }

        for ( AnnotationRuleProvider<Annotation, AnnotatedElement, Rule> rule : this.rules.get( pattern ) )
        {
            if ( providerClass.isInstance( rule ) )
            {
                return providerClass.cast( rule );
            }
        }

        return null;
    }

    /**
     * Add created {@link AnnotationRuleProvider}s created in another analysis session.
     * 
     * @param ruleSet the {@code RuleSet} created in another analysis session.
     */
    public void addRulesProviderFrom( final FromAnnotationsRuleSet ruleSet )
    {
        this.rules.putAll( ruleSet.getRules() );
    }

    /**
     * Checks if this RuleSet builds Digester mapping rules for the input type.
     * 
     * @param clazz the input type.
     * @return true, if this RuleSet builds Digester mapping rules for the input type, false otherwise.
     */
    protected boolean mapsClass( Class<?> clazz )
    {
        return this.mappedClasses.contains( clazz );
    }

    /**
     * Remember that this RuleSet is able to build Digester mapping rules for the input type.
     * 
     * @param clazz the input type.
     */
    protected void addMappedClass( Class<?> clazz )
    {
        this.mappedClasses.add( clazz );
    }

    /**
     * Returns the data structure the patterns/{@link AnnotationRuleProvider} pairs.
     * 
     * @return the data structure the patterns/{@link AnnotationRuleProvider} pairs.
     */
    private Map<String, List<AnnotationRuleProvider<Annotation, AnnotatedElement, Rule>>> getRules()
    {
        return this.rules;
    }

    /**
     * {@inheritDoc}
     */
    public String getNamespaceURI()
    {
        return this.namespaceURI;
    }

    /**
     * Sets the namespace URI that will be applied to all Rule instances created from this RuleSet.
     * 
     * @param namespaceURI the namespace URI that will be applied to all Rule instances created from this RuleSet.
     */
    public void setNamespaceURI( String namespaceURI )
    {
        this.namespaceURI = namespaceURI;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "{ mappedClasses=" + this.mappedClasses + ", rules=" + this.rules.toString() + ", namespaceURI="
            + this.namespaceURI + " }";
    }

}
