package org.codehaus.plexus.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Convenience class to handle throwable stacktraces
 *
 * <p>Created on 18/06/2003</p>
 *
 * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
 * @version $Revision$
 */
public class Tracer
{

    /**
     * Constructor
     *
     *
     */
    private Tracer()
    {
        super();
    }

    /**
     * Return the throwable stack trace as a string
     * */
    public static String traceToString( Throwable t )
    {
        if ( t == null )
        {
            return null;
        }
        StringWriter sw = new StringWriter();
        t.printStackTrace( new PrintWriter( sw ) );
        return sw.toString();
    }

}
