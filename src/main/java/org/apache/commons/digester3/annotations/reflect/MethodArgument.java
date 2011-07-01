package org.apache.commons.digester3.annotations.reflect;

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

import static java.lang.System.arraycopy;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * Class to supply the missing Java {@code AnnotatedElement} for method arguments.
 * 
 * @since 2.1
 */
public final class MethodArgument
    implements AnnotatedElement
{

    /**
     * The method argument index.
     */
    private final int index;

    /**
     * The method argument type.
     */
    private final Class<?> parameterType;

    /**
     * The method argument annotations.
     */
    private final Annotation[] annotations;

    /**
     * Creates a new method argument as {@code AnnotatedElement}.
     * 
     * @param index the method argument index.
     * @param parameterType the method argument type.
     * @param annotations the method argument annotations.
     */
    public MethodArgument( int index, Class<?> parameterType, Annotation[] annotations )
    {
        if ( parameterType == null )
        {
            throw new IllegalArgumentException( "Argument 'parameterType' must be not null" );
        }
        if ( annotations == null )
        {
            throw new IllegalArgumentException( "Argument 'annotations' must be not null" );
        }

        this.index = index;
        this.parameterType = parameterType;
        this.annotations = new Annotation[annotations.length];

        arraycopy( annotations, 0, this.annotations, 0, annotations.length );
    }

    /**
     * Returns the method argument index.
     * 
     * @return the method argument index.
     */
    public int getIndex()
    {
        return this.index;
    }

    /**
     * Returns the method argument type.
     * 
     * @return the method argument type.
     */
    public Class<?> getParameterType()
    {
        return this.parameterType;
    }

    /**
     * {@inheritDoc}
     */
    public <T extends Annotation> T getAnnotation( Class<T> annotationType )
    {
        for ( Annotation annotation : this.annotations )
        {
            if ( annotationType == annotation.annotationType() )
            {
                return annotationType.cast( annotation );
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Annotation[] getAnnotations()
    {
        return this.getAnnotationsArrayCopy();
    }

    /**
     * {@inheritDoc}
     */
    public Annotation[] getDeclaredAnnotations()
    {
        return this.getAnnotationsArrayCopy();
    }

    /**
     * Returns an annotations array, copy of the declared annotations in this method argument.
     * 
     * @return an annotations array, copy of the declared annotations in this method argument.
     */
    private Annotation[] getAnnotationsArrayCopy()
    {
        Annotation[] annotations = new Annotation[this.annotations.length];
        System.arraycopy( this.annotations, 0, annotations, 0, annotations.length );
        return annotations;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAnnotationPresent( Class<? extends Annotation> annotationType )
    {
        for ( Annotation annotation : this.annotations )
        {
            if ( annotationType == annotation.annotationType() )
            {
                return true;
            }
        }
        return false;
    }

}
