package org.apache.commons.digester3.binder;

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

import org.apache.commons.digester3.FactoryCreateRule;
import org.apache.commons.digester3.ObjectCreationFactory;

/**
 * Builder chained when invoking {@link LinkedRuleBuilder#factoryCreate()}.
 *
 * @since 3.0
 */
public final class FactoryCreateBuilder
    extends AbstractBackToLinkedRuleBuilder<FactoryCreateRule>
{

    private final ClassLoader classLoader;

    private Class<? extends ObjectCreationFactory<?>> type;

    private String attributeName;

    private boolean ignoreCreateExceptions;

    private ObjectCreationFactory<?> creationFactory;

    FactoryCreateBuilder( String keyPattern, String namespaceURI, RulesBinder mainBinder,
                          LinkedRuleBuilder mainBuilder, ClassLoader classLoader )
    {
        super( keyPattern, namespaceURI, mainBinder, mainBuilder );
        this.classLoader = classLoader;
    }

    /**
     * Construct a factory create rule that will use the specified class name to create an {@link ObjectCreationFactory}
     * which will then be used to create an object and push it on the stack.
     *
     * @param className Java class name of the object creation factory class
     * @return this builder instance
     */
    @SuppressWarnings( "unchecked" ) // if class not assignable, will be notified via exception
    public FactoryCreateBuilder ofType( String className )
    {
        if ( className == null )
        {
            reportError( "factoryCreate().ofType( String )", "NULL Java type not allowed" );
        }

        try
        {
            Class<?> type = this.classLoader.loadClass( className );
            if ( !ObjectCreationFactory.class.isAssignableFrom( type ) )
            {
                reportError( "factoryCreate().ofType( String )", "NULL Java type not allowed" );
                return this;
            }

            this.type = (Class<? extends ObjectCreationFactory<?>>) type;
        }
        catch ( ClassNotFoundException e )
        {
            reportError( "factoryCreate().ofType( String )", String.format( "class '%s' cannot be load", className ) );
        }

        return this;
    }

    /**
     * Construct a factory create rule that will use the specified class to create an {@link ObjectCreationFactory}
     * which will then be used to create an object and push it on the stack.
     *
     * @param type Java class of the object creation factory class
     * @return this builder instance
     */
    public FactoryCreateBuilder ofType( Class<? extends ObjectCreationFactory<?>> type )
    {
        if ( type == null )
        {
            reportError( "factoryCreate().ofType( Class<? extends ObjectCreationFactory<?>> )",
                              "NULL Java type not allowed" );
        }

        this.type = type;

        return this;
    }

    /**
     * Construct a factory create rule using the given, already instantiated, {@link ObjectCreationFactory}.
     *
     * @param <T> the type of created object by the given factory
     * @param creationFactory called on to create the object
     * @return this builder instance
     */
    public <T> FactoryCreateBuilder usingFactory( /* @Nullable */ObjectCreationFactory<T> creationFactory )
    {
        this.creationFactory = creationFactory;
        return this;
    }

    /**
     * Allows specify the attribute containing an override class name if it is present.
     *
     * @param attributeName The attribute containing an override class name if it is present
     * @return this builder instance
     */
    public FactoryCreateBuilder overriddenByAttribute( /* @Nullable */String attributeName )
    {
        this.attributeName = attributeName;
        return this;
    }

    /**
     * Exceptions thrown by the object creation factory will be ignored or not.
     *
     * @param ignoreCreateExceptions if true, exceptions thrown by the object creation factory will be ignored
     * @return this builder instance
     */
    public FactoryCreateBuilder ignoreCreateExceptions( boolean ignoreCreateExceptions )
    {
        this.ignoreCreateExceptions = ignoreCreateExceptions;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected FactoryCreateRule createRule()
    {
        if ( type == null && attributeName == null && creationFactory == null )
        {
            reportError( "factoryCreate()",
                         "at least one between 'className', 'attributeName' or 'creationFactory' has to be specified" );
        }

        if ( type != null || attributeName != null )
        {
            return new FactoryCreateRule( type, attributeName, ignoreCreateExceptions );
        }

        return new FactoryCreateRule( creationFactory, ignoreCreateExceptions );
    }

}
