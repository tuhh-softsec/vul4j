package org.codehaus.plexus.util.dag;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class CycleDetectedExceptionTest
        extends TestCase
{
    public void testException()
    {
        final List cycle = new ArrayList();

        cycle.add( "a" );

        cycle.add( "b" );

        cycle.add( "a" );

        final CycleDetectedException e = new CycleDetectedException( "Cycle detected", cycle );

        assertEquals( "Cycle detected a --> b --> a", e.getMessage() );
    }
}
