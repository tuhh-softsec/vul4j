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

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.digester3.binder.LinkedRuleBuilder;
import org.apache.commons.digester3.binder.RulesBinder;
import org.apache.commons.digester3.binder.RulesModule;

/**
 * A {@link RulesBinder} implementation with memory to maintain
 * which classes have already been analyzed.
 *
 * @since 3.0
 */
final class WithMemoryRulesBinder
    implements RulesBinder
{

    /**
     * Maintains all the classes that this RuleSet produces mapping for.
     */
    private final Set<Class<?>> boundClasses = new HashSet<Class<?>>();

    private final RulesBinder wrappedRulesBinder;

    public WithMemoryRulesBinder( final RulesBinder wrappedRulesBinder )
    {
        this.wrappedRulesBinder = wrappedRulesBinder;
    }

    /**
     * {@inheritDoc}
     */
    public ClassLoader getContextClassLoader()
    {
        return wrappedRulesBinder.getContextClassLoader();
    }

    /**
     * {@inheritDoc}
     */
    public void addError( String messagePattern, Object... arguments )
    {
        wrappedRulesBinder.addError( messagePattern, arguments );
    }

    /**
     * {@inheritDoc}
     */
    public void addError( Throwable t )
    {
        wrappedRulesBinder.addError( t );
    }

    /**
     * {@inheritDoc}
     */
    public void install( RulesModule rulesModule )
    {
        wrappedRulesBinder.install( rulesModule );
    }

    /**
     * {@inheritDoc}
     */
    public LinkedRuleBuilder forPattern( String pattern )
    {
        return wrappedRulesBinder.forPattern( pattern );
    }

    /**
     * 
     *
     * @param bindingClass
     * @return
     */
    public boolean markAsBound( Class<?> bindingClass )
    {
        return boundClasses.add( bindingClass );
    }

    /**
     * 
     *
     * @param bindingClass
     * @return
     */
    public boolean isAlreadyBound( Class<?> bindingClass )
    {
        return boundClasses.contains( bindingClass );
    }

}
