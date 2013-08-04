package org.codehaus.plexus.util;
/*
 * Copyright The Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Describes a match target for SelectorUtils.
 * <p/>
 * Significantly more efficient than using strings, since re-evaluation and re-tokenizing is avoided.
 *
 * @author Kristian Rosenvold
 */
public class MatchPattern
{
    private final String source;

    private final String regexPattern;

    private final String separator;

    private final String[] tokenized;
    private final char[][] tokenizedChar;

    private MatchPattern( String source, String separator )
    {
        regexPattern = SelectorUtils.isRegexPrefixedPattern( source ) ? source.substring(
            SelectorUtils.REGEX_HANDLER_PREFIX.length(),
            source.length() - SelectorUtils.PATTERN_HANDLER_SUFFIX.length() ) : null;
        this.source =
            SelectorUtils.isAntPrefixedPattern( source )
                ? source.substring( SelectorUtils.ANT_HANDLER_PREFIX.length(), source.length()
                - SelectorUtils.PATTERN_HANDLER_SUFFIX.length() )
                : source;
        this.separator = separator;
        tokenized = tokenizePathToString( this.source, separator );
        tokenizedChar = new char[tokenized.length][];
        for (int i = 0; i < tokenized.length; i++){
            tokenizedChar[i] = tokenized[i].toCharArray();
        }

    }



    public boolean matchPath( String str, boolean isCaseSensitive )
    {
        if ( regexPattern != null )
        {
            return str.matches( regexPattern );
        }
        else
        {
            return SelectorUtils.matchAntPathPattern( this, str, separator, isCaseSensitive );
        }
    }

    boolean matchPath( String str, char[][] strDirs, boolean isCaseSensitive )
    {
        if ( regexPattern != null )
        {
            return str.matches( regexPattern );
        }
        else
        {
            return SelectorUtils.matchAntPathPattern( getTokenizedPathChars(), strDirs, isCaseSensitive );
        }
    }

    public boolean matchPatternStart( String str, boolean isCaseSensitive )
    {
        if ( regexPattern != null )
        {
            // FIXME: ICK! But we can't do partial matches for regex, so we have to reserve judgement until we have
            // a file to deal with, or we can definitely say this is an exclusion...
            return true;
        }
        else
        {
            String altStr = source.replace( '\\', '/' );

            return SelectorUtils.matchAntPathPatternStart( this, str, File.separator, isCaseSensitive )
                || SelectorUtils.matchAntPathPatternStart( this, altStr, "/", isCaseSensitive );
        }
    }

    public String[] getTokenizedPathString()
    {
        return tokenized;
    }

    public char[][] getTokenizedPathChars()
    {
        return tokenizedChar;
    }

    public boolean startsWith( String string )
    {
        return source.startsWith( string );
    }


    static String[] tokenizePathToString( String path, String separator )
    {
        List<String> ret = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer( path, separator );
        while ( st.hasMoreTokens() )
        {
            ret.add( st.nextToken() );
        }
        return ret.toArray( new String[ret.size()] );
    }

    public static MatchPattern fromString( String source )
    {
        return new MatchPattern( source, File.separator );
    }

}
