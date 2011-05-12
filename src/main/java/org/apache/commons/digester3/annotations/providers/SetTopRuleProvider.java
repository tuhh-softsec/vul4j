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
package org.apache.commons.digester3.annotations.providers;

import java.lang.reflect.Method;

import org.apache.commons.digester3.SetTopRule;
import org.apache.commons.digester3.annotations.AnnotationRuleProvider;
import org.apache.commons.digester3.annotations.rules.SetTop;

/**
 * Provides instances of {@link SetTopRule}.
 * 
 * @since 2.1
 */
public final class SetTopRuleProvider
    implements AnnotationRuleProvider<SetTop, Method, SetTopRule>
{

    private String methodName;

    private String paramType;

    /**
     * {@inheritDoc}
     */
    public void init( SetTop annotation, Method element )
    {
        this.methodName = element.getName();
        this.paramType = element.getParameterTypes()[0].getName();
    }

    /**
     * {@inheritDoc}
     */
    public SetTopRule get()
    {
        return new SetTopRule( this.methodName, this.paramType );
    }

}
