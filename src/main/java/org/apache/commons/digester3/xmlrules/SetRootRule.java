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

import org.apache.commons.digester3.binder.LinkedRuleBuilder;
import org.apache.commons.digester3.binder.RulesBinder;
import org.apache.commons.digester3.binder.SetRootBuilder;

/**
 * 
 */
final class SetRootRule
    extends AbstractXmlMethodRule
{

    /**
     * @param targetRulesBinder
     * @param patternStack
     */
    public SetRootRule( RulesBinder targetRulesBinder, PatternStack patternStack )
    {
        super( targetRulesBinder, patternStack );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void bindRule( LinkedRuleBuilder linkedRuleBuilder, String methodName, String paramType,
                             boolean exactMatch, boolean fireOnBegin )
    {
        SetRootBuilder builder = linkedRuleBuilder.setRoot( methodName );

        if ( paramType != null && paramType.length() > 0 )
        {
            builder.withParameterType( paramType );
        }

        builder.useExactMatch( exactMatch );
        builder.fireOnBegin( fireOnBegin );
    }

}
