/**
 * 
 */
package org.codehaus.plexus.util;

import junit.framework.TestCase;

/**
 * Test Case for Os
 */
public class OsTest extends TestCase
{
    public void testUndefinedFamily()
    {
        assertFalse( Os.isFamily( "bogus family" ) );
    }
}
