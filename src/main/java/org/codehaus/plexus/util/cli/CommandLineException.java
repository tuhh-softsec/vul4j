package org.codehaus.plexus.util.cli;

/*
 * LICENSE
 */

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class CommandLineException
	extends Exception
{
    public CommandLineException( String message )
    {
        super( message );
    }

    public CommandLineException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
