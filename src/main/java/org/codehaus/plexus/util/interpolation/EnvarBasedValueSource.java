package org.codehaus.plexus.util.interpolation;

import java.io.IOException;



/**
 * @version $Id$
 *
 * @deprecated Use plexus-interpolation APIs instead.
 * @version $Id$
 */
public class EnvarBasedValueSource
    extends org.codehaus.plexus.interpolation.EnvarBasedValueSource
{

    public EnvarBasedValueSource()
        throws IOException
    {
    }

    public EnvarBasedValueSource( boolean caseSensitive )
        throws IOException
    {
        super( caseSensitive );
    }

}
