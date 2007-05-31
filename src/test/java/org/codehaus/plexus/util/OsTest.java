/**
 * 
 */
package org.codehaus.plexus.util;

import java.util.Iterator;

import junit.framework.TestCase;

/**
 * Test Case for Os
 */
public class OsTest
    extends TestCase
{
    public void testUndefinedFamily()
    {
        assertFalse( Os.isFamily( "bogus family" ) );
    }

    public void testOs()
    {
        Iterator iter = Os.getValidFamilies().iterator();
        String currentFamily = null;
        String notCurrentFamily = null;
        while ( iter.hasNext() && ( currentFamily == null || notCurrentFamily == null ) )
        {
            String fam = (String) iter.next();
            if ( Os.isFamily( fam ) )
            {
                currentFamily = fam;
            }
            else
            {
                notCurrentFamily = fam;
            }
        }

        //make sure the OS_FAMILY is set right.
        assertEquals( currentFamily, Os.OS_FAMILY );
        
        // check the current family and one of the others
        assertTrue( Os.isOs( currentFamily, null, null, null ) );
        assertFalse( Os.isOs( notCurrentFamily, null, null, null ) );

        // check for junk
        assertFalse( Os.isOs( "junk", null, null, null ) );

        // check the current name
        assertTrue( Os.isOs( currentFamily, Os.OS_NAME, null, null ) );

        // check some other name
        assertFalse( Os.isOs( currentFamily, "myos", null, null ) );

        // check the arch
        assertTrue( Os.isOs( currentFamily, Os.OS_NAME, Os.OS_ARCH, null ) );
        assertFalse( Os.isOs( currentFamily, Os.OS_NAME, "myarch", null ) );

        // check the version
        assertTrue( Os.isOs( currentFamily, Os.OS_NAME, Os.OS_ARCH, Os.OS_VERSION ) );
        assertFalse( Os.isOs( currentFamily, Os.OS_NAME, Os.OS_ARCH, "myversion"  ) );
    }
    
    public void testValidList()
    {
        assertTrue(Os.isValidFamily( "dos" ) );
        
        assertFalse( Os.isValidFamily( "" ) );
        assertFalse( Os.isValidFamily( null ) );
        assertFalse( Os.isValidFamily( "something" ) );
    }
}
