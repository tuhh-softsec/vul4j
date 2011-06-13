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

import org.apache.commons.digester3.Rule;
import org.xml.sax.Attributes;

/**
 * 
 */
class PatternRule
    extends Rule
{

    private final String attributeName;

    private final PatternStack patternStack;

    private String pattern;

    public PatternRule( PatternStack patternStack )
    {
        this( "value", patternStack );
    }

    public PatternRule( String attributeName, PatternStack patternStack )
    {
        this.attributeName = attributeName;
        this.patternStack = patternStack;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void begin( String namespace, String name, Attributes attributes )
        throws Exception
    {
        this.pattern = attributes.getValue( this.attributeName );
        if ( this.pattern != null )
        {
            this.patternStack.push( pattern );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void end( String namespace, String name )
        throws Exception
    {
        if ( this.pattern != null )
        {
            this.patternStack.pop();
        }
    }

    /**
     * @return
     */
    protected String getMatchingPattern()
    {
        return this.patternStack.toString();
    }

}
