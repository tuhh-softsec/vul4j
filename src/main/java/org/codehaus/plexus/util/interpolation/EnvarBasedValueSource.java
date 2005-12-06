package org.codehaus.plexus.util.interpolation;

import org.codehaus.plexus.util.cli.CommandLineUtils;

import java.io.IOException;
import java.util.Properties;

public class EnvarBasedValueSource
    implements ValueSource
{
    
    private Properties envars;

    public EnvarBasedValueSource() throws IOException
    {
        this.envars = CommandLineUtils.getSystemEnvVars();
    }

    public Object getValue( String expression )
    {
        String expr = expression;
        
        if ( expr.startsWith( "env." ) )
        {
            expr = expr.substring( "env.".length() );
        }
        
        return envars.getProperty( expr );
    }
}
