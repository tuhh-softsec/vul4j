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

import static java.lang.String.format;

import org.apache.commons.digester3.Rule;

/**
 * Builder chained when invoking {@link LinkedRuleBuilderImpl#setNext(String)},
 * {@link LinkedRuleBuilderImpl#setRoot(String)} or {@link LinkedRuleBuilderImpl#setTop(String)}.
 *
 * @since 3.0
 */
abstract class AbstractParamTypeBuilder<R extends Rule>
    extends AbstractBackToLinkedRuleBuilder<R>
{

    private final String methodName;

    private final ClassLoader classLoader;

    private boolean useExactMatch = false;

    private Class<?> paramType;

    AbstractParamTypeBuilder( String keyPattern, String namespaceURI, RulesBinder mainBinder,
                              LinkedRuleBuilder mainBuilder, String methodName, ClassLoader classLoader )
    {
        super( keyPattern, namespaceURI, mainBinder, mainBuilder );
        this.methodName = methodName;
        this.classLoader = classLoader;
    }

    /**
     * Sets the Java class of the method's argument.
     * 
     * If you wish to use a primitive type, specify the corresonding
     * Java wrapper class instead, such as {@code java.lang.Boolean}
     * for a {@code boolean} parameter.
     *
     * @param paramType The Java class of the method's argument
     * @return this builder instance
     */
    public final AbstractParamTypeBuilder<R> withParameterType( Class<?> paramType )
    {
        if ( paramType == null )
        {
            reportError( format( ".%s.withParameterType( Class<?> )", methodName ), "NULL Java type not allowed" );
            return this;
        }
        return withParameterType( paramType.getName() );
    }

    /**
     * Sets the Java class name of the method's argument.
     * 
     * If you wish to use a primitive type, specify the corresonding
     * Java wrapper class instead, such as {@code java.lang.Boolean}
     * for a {@code boolean} parameter.
     *
     * @param paramType The Java class name of the method's argument
     * @return this builder instance
     */
    public final AbstractParamTypeBuilder<R> withParameterType( String paramType )
    {
        if ( paramType == null )
        {
            reportError( format( ".%s.withParameterType( Class<?> )", methodName ), "NULL Java type not allowed" );
            return this;
        }

        try
        {
            this.paramType = classLoader.loadClass( paramType );
        }
        catch ( ClassNotFoundException e )
        {
            this.reportError( format( ".%s.withParameterType( Class<?> )", methodName ),
                              format( "class '%s' cannot be load", paramType ) );
        }
        return this;
    }

    /**
     * Sets exact matching being used.
     *
     * @param useExactMatch The exact matching being used
     * @return this builder instance
     */
    public final AbstractParamTypeBuilder<R> useExactMatch( boolean useExactMatch )
    {
        this.useExactMatch = useExactMatch;
        return this;
    }

    final String getMethodName()
    {
        return methodName;
    }

    final Class<?> getParamType()
    {
        return paramType;
    }

    final boolean isUseExactMatch()
    {
        return useExactMatch;
    }

}
