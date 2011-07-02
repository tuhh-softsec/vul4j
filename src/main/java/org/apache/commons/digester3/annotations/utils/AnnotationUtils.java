package org.apache.commons.digester3.annotations.utils;

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

import static org.apache.commons.beanutils.MethodUtils.invokeExactMethod;

import java.lang.annotation.Annotation;

/**
 * Simple utility class to introspect annotations.
 * 
 * @since 2.1
 */
public class AnnotationUtils
{

    /**
     * The {@code value} string constant.
     */
    private static final String VALUE = "value";

    /**
     * The {@code pattern} string constant.
     */
    private static final String PATTERN = "pattern";

    /**
     * The {@code namespaceURI} string constant.
     */
    private static final String NAMESPACE_URI = "namespaceURI";

    /**
     * The {@code namespaceURI} string constant.
     */
    private static final String FIRE_ON_BEGIN = "fireOnBegin";

    /**
     * This class can't be instantiated.
     */
    private AnnotationUtils()
    {
        // this class can' be instantiated
    }

    /**
     * Extract the {@code value()} from annotation.
     * 
     * @param annotation the annotation has to be introspected.
     * @return the annotation {@code value()}.
     */
    public static Object getAnnotationValue( Annotation annotation )
    {
        return invokeAnnotationMethod( annotation, VALUE );
    }

    /**
     * Extract the {@code pattern()} from annotation.
     * 
     * @param annotation the annotation has to be introspected.
     * @return the annotation {@code pattern()}.
     */
    public static String getAnnotationPattern( Annotation annotation )
    {
        Object ret = invokeAnnotationMethod( annotation, PATTERN );
        if ( ret != null )
        {
            return (String) ret;
        }
        return null;
    }

    /**
     * Extract the {@code namespaceURI()} from annotation.
     *
     * @param annotation The annotation has to be introspected
     * @return The annotation {@code namespaceURI()}
     */
    public static String getAnnotationNamespaceURI( Annotation annotation )
    {
        Object ret = invokeAnnotationMethod( annotation, NAMESPACE_URI );
        if ( ret != null )
        {
            return (String) ret;
        }
        return null;
    }

    /**
     * Extract the {@code fireOnBegin()} from annotation.
     *
     * @param annotation The annotation has to be introspected
     * @return The annotation {@code fireOnBegin()}
     */
    public static boolean getFireOnBegin( Annotation annotation )
    {
        Object ret = invokeAnnotationMethod( annotation, FIRE_ON_BEGIN );
        if ( ret != null )
        {
            return (Boolean) ret;
        }
        return false;
    }

    /**
     * Extract the Annotations array {@code value()} from annotation if present, nul otherwise.
     * 
     * @param annotation the annotation has to be introspected.
     * @return the annotation {@code value()} as Annotations array.
     */
    public static Annotation[] getAnnotationsArrayValue( Annotation annotation )
    {
        Object value = getAnnotationValue( annotation );
        if ( value != null && value.getClass().isArray()
            && Annotation.class.isAssignableFrom( value.getClass().getComponentType() ) )
        {
            return (Annotation[]) value;
        }
        return null;
    }

    /**
     * Invokes an annotation method.
     * 
     * @param annotationn the annotation has to be introspected.
     * @param method the method name to execute.
     * @return the annotation method value, null if any error occurs.
     */
    private static Object invokeAnnotationMethod( Annotation annotation, String method )
    {
        try
        {
            return invokeExactMethod( annotation, method, null );
        }
        catch ( Throwable t )
        {
            return null;
        }
    }

}
