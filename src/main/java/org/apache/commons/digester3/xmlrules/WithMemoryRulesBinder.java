package org.apache.commons.digester3.xmlrules;

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
 * 
 */
class WithMemoryRulesBinder
    implements RulesBinder
{

    /**
     * A stack used to maintain the current pattern. The Rules XML document type allows nesting of patterns. If an
     * element defines a matching pattern, the resulting pattern is a concatenation of that pattern with all the
     * ancestor elements' patterns. Hence the need for a stack.
     */
    private final PatternStack patternStack = new PatternStack();

    /**
     * Used to detect circular includes
     */
    private final Set<String> includedFiles = new HashSet<String>();

    private final RulesBinder wrappedRulesBinder;

    public WithMemoryRulesBinder( RulesBinder wrappedRulesBinder )
    {
        this.wrappedRulesBinder = wrappedRulesBinder;
    }

    /**
     * {@inheritDoc}
     */
    public ClassLoader getContextClassLoader()
    {
        return this.wrappedRulesBinder.getContextClassLoader();
    }

    /**
     * {@inheritDoc}
     */
    public void addError( String messagePattern, Object... arguments )
    {
        this.wrappedRulesBinder.addError( messagePattern, arguments );
    }

    /**
     * {@inheritDoc}
     */
    public void addError( Throwable t )
    {
        this.wrappedRulesBinder.addError( t );
    }

    /**
     * {@inheritDoc}
     */
    public void install( RulesModule rulesModule )
    {
        this.wrappedRulesBinder.install( rulesModule );
    }

    /**
     * {@inheritDoc}
     */
    public LinkedRuleBuilder forPattern( String pattern )
    {
        return this.wrappedRulesBinder.forPattern( pattern );
    }

    /**
     * @return
     */
    public PatternStack getPatternStack()
    {
        return this.patternStack;
    }

    /**
     * @return
     */
    public Set<String> getIncludedFiles()
    {
        return this.includedFiles;
    }

}
