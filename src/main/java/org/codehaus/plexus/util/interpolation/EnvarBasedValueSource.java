package org.codehaus.plexus.util.interpolation;

import org.codehaus.plexus.util.cli.CommandLineUtils;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

public class EnvarBasedValueSource
    implements ValueSource
{
    
    private Properties envars;

    public EnvarBasedValueSource() throws IOException
    {
        Properties props = CommandLineUtils.getSystemEnvVars();
        
        envars = new Properties();
        
        for ( Iterator it = props.keySet().iterator(); it.hasNext(); )
        {
            String key = ( String ) it.next();
            String value = props.getProperty( key );
            
            envars.setProperty( key.toLowerCase(), value );
        }
    }

    public Object getValue( String expression )
    {
        String expr = expression;
        
        if ( expr.startsWith( "env." ) )
        {
            expr = expr.substring( "env.".length() );
        }
        
        return envars.getProperty( expr.toLowerCase() );
    }
}
