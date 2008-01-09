package org.codehaus.plexus.util.interpolation;

/**
 * Interpolator interface. Based on existing RegexBasedInterpolator interface.
 * 
 * @author cstamas
 */
public interface Interpolator
{
    void addValueSource( ValueSource valueSource );

    void removeValuesSource( ValueSource valueSource );

    String interpolate( String input, String thisPrefixPattern );
}
