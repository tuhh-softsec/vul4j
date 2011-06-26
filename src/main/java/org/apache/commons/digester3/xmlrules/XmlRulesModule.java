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

import java.util.Set;

import org.apache.commons.digester3.binder.LinkedRuleBuilder;
import org.apache.commons.digester3.binder.RulesBinder;
import org.apache.commons.digester3.binder.RulesModule;

/**
 * 
 */
final class XmlRulesModule
    implements RulesModule
{

    private final NameSpaceURIRulesBinder targetRulesBinder;

    private final Set<String> rootSystemIds;

    private final String rootPath;

    private WithMemoryRulesBinder memoryRulesBinder;

    public XmlRulesModule( final NameSpaceURIRulesBinder targetRulesBinder, Set<String> rootSystemIds,
    /* @Nullable */String rootPath )
    {
        this.targetRulesBinder = targetRulesBinder;
        this.rootSystemIds = rootSystemIds;
        this.rootPath = rootPath;
    }

    /**
     * {@inheritDoc}
     */
    public void configure( RulesBinder rulesBinder )
    {
        if ( rulesBinder instanceof WithMemoryRulesBinder )
        {
            memoryRulesBinder = (WithMemoryRulesBinder) rulesBinder;
        }
        else
        {
            memoryRulesBinder = new WithMemoryRulesBinder( rulesBinder );
            if ( !rootSystemIds.isEmpty() )
            {
                memoryRulesBinder.getIncludedFiles().addAll( rootSystemIds );
            }
        }

        PatternStack patternStack = memoryRulesBinder.getPatternStack();

        if ( rootPath != null )
        {
            patternStack.push( rootPath );
        }

        try
        {
            forPattern( "digester-rules" ).addRule( new SetNamespaceURIRule( targetRulesBinder ) );

            forPattern( "*/pattern" ).addRule( new PatternRule( patternStack ) );
            forPattern( "*/include" ).addRule( new IncludeRule( memoryRulesBinder, targetRulesBinder ) );

            forPattern( "*/bean-property-setter-rule" ).addRule( new BeanPropertySetterRule( targetRulesBinder,
                                                                                             patternStack ) );

            forPattern( "*/call-method-rule" ).addRule( new CallMethodRule( targetRulesBinder, patternStack ) );
            forPattern( "*/call-param-rule" ).addRule( new CallParamRule( targetRulesBinder, patternStack ) );
            forPattern( "*/object-param-rule" ).addRule( new ObjectParamRule( targetRulesBinder, patternStack ) );

            forPattern( "*/factory-create-rule" ).addRule( new FactoryCreateRule( targetRulesBinder, patternStack ) );
            forPattern( "*/node-create-rule" ).addRule( new NodeCreateRule( targetRulesBinder, patternStack ) );
            forPattern( "*/object-create-rule" ).addRule( new ObjectCreateRule( targetRulesBinder, patternStack ) );

            forPattern( "*/set-properties-rule" ).addRule( new SetPropertiesRule( targetRulesBinder, patternStack ) );
            forPattern( "*/set-properties-rule/alias" )
                .addRule( new SetPropertiesAliasRule( targetRulesBinder, patternStack ) );
            forPattern( "*/set-properties-rule/ignore" )
                .addRule( new SetPropertiesIgnoreRule( targetRulesBinder, patternStack ) );

            forPattern( "*/set-property-rule" ).addRule( new SetPropertyRule( targetRulesBinder, patternStack ) );

            forPattern( "*/set-nested-properties-rule" )
                .addRule( new SetNestedPropertiesRule( targetRulesBinder, patternStack ) );
            forPattern( "*/set-nested-properties-rule/alias" )
                .addRule( new SetNestedPropertiesAliasRule( targetRulesBinder, patternStack ) );
            forPattern( "*/set-nested-properties-rule/ignore" )
                .addRule( new SetPropertiesIgnoreRule( targetRulesBinder, patternStack ) );

            forPattern( "*/set-top-rule" ).addRule( new SetTopRule( targetRulesBinder, patternStack ) );
            forPattern( "*/set-next-rule" ).addRule( new SetNextRule( targetRulesBinder, patternStack ) );
            forPattern( "*/set-root-rule" ).addRule( new SetRootRule( targetRulesBinder, patternStack ) );
        }
        finally
        {
            memoryRulesBinder = null;
        }
    }

    /**
     * @param pattern
     * @return
     */
    protected LinkedRuleBuilder forPattern( String pattern )
    {
        return memoryRulesBinder.forPattern( pattern );
    }

}
