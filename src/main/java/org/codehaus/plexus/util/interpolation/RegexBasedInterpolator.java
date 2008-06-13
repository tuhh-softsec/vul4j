package org.codehaus.plexus.util.interpolation;

import java.util.List;


/**
 *
 * @version $Id$
 * @deprecated Use plexus-interpolation APIs instead.
 */
public class RegexBasedInterpolator
    extends org.codehaus.plexus.interpolation.RegexBasedInterpolator
    implements Interpolator
{

    public RegexBasedInterpolator()
    {
        super();
    }

    public RegexBasedInterpolator( List valueSources )
    {
        super( valueSources );
    }

    public RegexBasedInterpolator( String startRegex,
                                   String endRegex,
                                   List valueSources )
    {
        super( startRegex, endRegex, valueSources );
    }

    public RegexBasedInterpolator( String startRegex,
                                   String endRegex )
    {
        super( startRegex, endRegex );
    }

}
