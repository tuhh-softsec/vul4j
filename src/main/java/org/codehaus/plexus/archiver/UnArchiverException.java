package org.codehaus.plexus.archiver;

/**
 * @author Jason van Zyl
 */
public class UnArchiverException
    extends Exception
{
public UnArchiverException( String string )
    {
        super( string );
    }

    public UnArchiverException( String string,
                                Throwable throwable )
    {
        super( string, throwable );
    }

    public UnArchiverException( Throwable throwable )
    {
        super( throwable );
    }
}
