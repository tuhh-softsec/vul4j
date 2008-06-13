package org.codehaus.plexus.util.interpolation;

import java.util.Properties;

/**
 *
 * @author jdcasey
 * @deprecated Use plexus-interpolation APIs instead.
 */
public class PropertiesBasedValueSource
    extends org.codehaus.plexus.interpolation.PropertiesBasedValueSource
{

    public PropertiesBasedValueSource( Properties properties )
    {
        super( properties );
    }

}
