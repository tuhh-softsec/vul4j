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
package org.apache.commons.digester3.annotations.failingtests;

import static java.lang.String.format;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import org.apache.commons.digester3.annotations.AnnotationHandler;
import org.apache.commons.digester3.annotations.AnnotationHandlerFactory;

public class FailingDigesterLoaderHandlerFactory
    implements AnnotationHandlerFactory
{

    public <H extends AnnotationHandler<? extends Annotation, ? extends AnnotatedElement>> H newInstance( Class<H> type )
        throws Exception
    {
        throw new Exception( format( "Impossible to create '%s' instances", type.getName() ) );
    }

}
