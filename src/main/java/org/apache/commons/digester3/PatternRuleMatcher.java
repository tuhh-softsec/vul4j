package org.apache.commons.digester3;

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

import org.xml.sax.Attributes;

/**
 * @since 3.0
 */
final class PatternRuleMatcher
    implements RuleMatcher
{

    private final String pattern;

    private String namespaceURI;

    public PatternRuleMatcher( String pattern )
    {
        this( pattern, null );
    }

    public PatternRuleMatcher( String pattern, /* @Nullable */String namespaceURI )
    {
        if ( pattern == null )
        {
            throw new IllegalArgumentException( "Input pattern must be not null" );
        }

        this.pattern = pattern;
        this.namespaceURI = namespaceURI;
    }

    /**
     * {@inheritDoc}
     */
    public boolean match( String namespace, String pattern, String name, Attributes attributes )
    {
        if ( namespaceURI != null && !namespace.equals( namespaceURI ) )
        {
            return false;
        }
        return this.pattern.equals( pattern );
    }

    public String getPattern()
    {
        return pattern;
    }

    public String getNamespaceURI()
    {
        return namespaceURI;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( namespaceURI == null ) ? 0 : namespaceURI.hashCode() );
        result = prime * result + pattern.hashCode();
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }

        if ( obj == null )
        {
            return false;
        }

        if ( getClass() != obj.getClass() )
        {
            return false;
        }

        PatternRuleMatcher other = (PatternRuleMatcher) obj;
        if ( namespaceURI == null )
        {
            if ( other.getNamespaceURI() != null )
            {
                return false;
            }
        }
        else if ( !namespaceURI.equals( other.getNamespaceURI() ) )
        {
            return false;
        }

        if ( !pattern.equals( other.getPattern() ) )
        {
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return format( "%s (%s)", pattern, namespaceURI );
    }

}
