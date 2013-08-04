package org.codehaus.plexus.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A list of patterns to be matched
 *
 * @author Kristian Rosenvold
 */
public class MatchPatterns
{
    private final MatchPattern[] patterns;

    private MatchPatterns( MatchPattern[] patterns )
    {
        this.patterns = patterns;
    }

    /**
     * Checks these MatchPatterns against a specified string.
     * <p/>
     * Uses far less string tokenization than any of the alternatives.
     *
     * @param name            The name to look for
     * @param isCaseSensitive If the comparison is case sensitive
     * @return true if any of the supplied patterns match
     */
    public boolean matches( String name, boolean isCaseSensitive )
    {
        String[] tokenized = MatchPattern.tokenizePathToString( name, File.separator );
        return matches(  name, tokenized, isCaseSensitive );
    }

    public boolean matches( String name, String[] tokenizedName, boolean isCaseSensitive )
    {
        char[][] tokenizedNameChar = new char[tokenizedName.length][];
        for(int i = 0;  i < tokenizedName.length; i++){
        tokenizedNameChar[i] = tokenizedName[i].toCharArray();
        }
        for ( MatchPattern pattern : patterns )
        {
            if ( pattern.matchPath( name, tokenizedNameChar, isCaseSensitive ) )
            {
                return true;
            }
        }
        return false;
    }

    public boolean matchesPatternStart( String name, boolean isCaseSensitive )
    {
        for ( MatchPattern includesPattern : patterns )
        {
            if ( includesPattern.matchPatternStart( name, isCaseSensitive ) )
            {
                return true;
            }
        }
        return false;
    }

    public static MatchPatterns from( String... sources )
    {
        final int length = sources.length;
        MatchPattern[] result = new MatchPattern[length];
        for ( int i = 0; i < length; i++ )
        {
            result[i] = MatchPattern.fromString( sources[i] );
        }
        return new MatchPatterns( result );
    }

    public static MatchPatterns from( Iterable<String> strings )
    {
        return new MatchPatterns( getMatchPatterns( strings ) );
    }

    private static MatchPattern[] getMatchPatterns( Iterable<String> items )
    {
        List<MatchPattern> result = new ArrayList<MatchPattern>();
        for ( String string : items )
        {
            result.add( MatchPattern.fromString( string ) );
        }
        return result.toArray( new MatchPattern[result.size()] );
    }

}
