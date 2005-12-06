package org.codehaus.plexus.util.interpolation;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

public class EnvarBasedValueSource
    implements ValueSource
{
    
    private Properties envars;

    public EnvarBasedValueSource() throws IOException
    {
        this.envars = getEnvironmentVariables();
    }

    public Object getValue( String expression )
    {
        return envars.getProperty( expression );
    }

    private Properties getEnvironmentVariables()
        throws IOException
    {
        String osName = System.getProperty( "os.name" );

        String listEnvCommand;
        if ( osName.startsWith( "Windows" ) )
        {
            listEnvCommand = "cmd /C SET";
        }
        else
        {
            listEnvCommand = "/usr/bin/printenv";
        }

        Properties envProperties = new Properties();
        envProperties.load( Runtime.getRuntime().exec( listEnvCommand ).getInputStream() );

        Properties mavenEnvProperties = new Properties();
        for ( Iterator keys = envProperties.keySet().iterator(); keys.hasNext(); )
        {
            String key = (String) keys.next();
            mavenEnvProperties.setProperty( "env." + key, envProperties.getProperty( key ) );
        }
        
        return mavenEnvProperties;
    }
}
