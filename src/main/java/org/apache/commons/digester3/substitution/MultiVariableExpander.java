package org.apache.commons.digester3.substitution;

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

import java.util.Map;
import java.util.ArrayList;

/**
 * <p>
 * Expands variable references from multiple sources.
 * </p>
 * 
 * @since 1.6
 */
public class MultiVariableExpander
    implements VariableExpander
{

    private int nEntries = 0;

    private final ArrayList<String> markers = new ArrayList<String>( 2 );

    private final ArrayList<Map<String, Object>> sources = new ArrayList<Map<String, Object>>( 2 );

    /**
     * Add a new variables source, identified by the input marker
     *
     * @param marker The input variables marker
     * @param source The variables source
     */
    public void addSource( String marker, Map<String, Object> source )
    {
        ++nEntries;
        markers.add( marker );
        sources.add( source );
    }

    /**
     * {@inheritDoc}
     */
    public String expand( String param )
    {
        for ( int i = 0; i < nEntries; ++i )
        {
            param = expand( param, markers.get( i ), sources.get( i ) );
        }
        return param;
    }

    /**
     * Replace any occurrences within the string of the form "marker{key}" with the value from source[key].
     * <p>
     * Commonly, the variable marker is "$", in which case variables are indicated by ${key} in the string.
     * <p>
     * Returns the string after performing all substitutions.
     * <p>
     * If no substitutions were made, the input string object is returned (not a copy).
     *
     * @param str The input string containing placeholders
     * @param marker The input variables marker
     * @param source The variables source
     * @return The input string where variables have been expanded by replacing values found in source
     */
    public String expand( String str, String marker, Map<String, Object> source )
    {
        String startMark = marker + "{";
        int markLen = startMark.length();

        int index = 0;
        for ( ;; )
        {
            index = str.indexOf( startMark, index );
            if ( index == -1 )
            {
                return str;
            }

            int startIndex = index + markLen;
            if ( startIndex > str.length() )
            {
                throw new IllegalArgumentException( "var expression starts at end of string" );
            }

            int endIndex = str.indexOf( "}", index + markLen );
            if ( endIndex == -1 )
            {
                throw new IllegalArgumentException( "var expression starts but does not end" );
            }

            String key = str.substring( index + markLen, endIndex );
            Object value = source.get( key );
            if ( value == null )
            {
                throw new IllegalArgumentException( "parameter [" + key + "] is not defined." );
            }
            String varValue = value.toString();

            str = str.substring( 0, index ) + varValue + str.substring( endIndex + 1 );
            index += varValue.length();
        }
    }

}
