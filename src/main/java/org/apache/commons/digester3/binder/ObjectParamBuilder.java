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

import org.apache.commons.digester3.ObjectParamRule;

/**
 * Builder chained when invoking {@link LinkedRuleBuilder#objectParam(Object)}.
 *
 * @param <T> The object type represented by this builder
 * @since 3.0
 */
public final class ObjectParamBuilder<T>
    extends AbstractBackToLinkedRuleBuilder<ObjectParamRule>
{

    private final T paramObj;

    private int paramIndex = 0;

    private String attributeName;

    ObjectParamBuilder( String keyPattern, String namespaceURI, RulesBinder mainBinder, LinkedRuleBuilder mainBuilder,
                        /* @Nullable */T paramObj )
    {
        super( keyPattern, namespaceURI, mainBinder, mainBuilder );
        this.paramObj = paramObj;
    }

    /**
     * The zero-relative index of the parameter we are saving.
     *
     * @param paramIndex The zero-relative index of the parameter we are saving
     * @return this builder instance
     */
    public ObjectParamBuilder<T> ofIndex( int paramIndex )
    {
        if ( paramIndex < 0 )
        {
            this.reportError( "objectParam( %s ).ofIndex( int )", "negative index argument not allowed" );
        }

        this.paramIndex = paramIndex;
        return this;
    }

    /**
     * The attribute which we are attempting to match.
     *
     * @param attributeName The attribute which we are attempting to match
     * @return this builder instance
     */
    public ObjectParamBuilder<T> matchingAttribute( /* @Nullable */String attributeName )
    {
        this.attributeName = attributeName;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ObjectParamRule createRule()
    {
        return new ObjectParamRule( paramIndex, attributeName, paramObj );
    }

}
