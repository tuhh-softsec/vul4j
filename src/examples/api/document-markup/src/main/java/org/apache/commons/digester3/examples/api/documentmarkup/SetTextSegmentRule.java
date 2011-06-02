package org.apache.commons.digester3.examples.api.documentmarkup;

/*
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

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.digester3.Rule;

/**
 * When a text segment is discovered, it calls a specific method on the top
 * object on the stack.
 */
public class SetTextSegmentRule
    extends Rule
    implements TextSegmentHandler
{

    // ----------------------------------------------------------- Constructors

    public SetTextSegmentRule( String methodName )
    {
        this.methodName = methodName;
    }

    // ----------------------------------------------------- Instance Variables

    /**
     * The method name to call on the parent object.
     */
    protected String methodName = null;

    // --------------------------------------------------------- Public Methods

    /**
     * Process the end of this element.
     */
    public void textSegment( String text )
        throws Exception
    {
        Object target = getDigester().peek( 0 );

        // Call the specified method
        Class<?> paramTypes[] = new Class[] { String.class };
        MethodUtils.invokeMethod( target, methodName, new Object[] { text }, paramTypes );
    }

}
