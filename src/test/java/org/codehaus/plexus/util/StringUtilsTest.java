package org.codehaus.plexus.util;

import junit.framework.TestCase;

import java.util.Locale;

/**
 * Test string utils.
 *
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 * @version $Id$
 */
public class StringUtilsTest
    extends TestCase
{
    public void testCapitalizeFirstLetter()
    {
        assertEquals( "Id", StringUtils.capitalizeFirstLetter( "id" ) );
    }

    public void testCapitalizeFirstLetterTurkish()
    {
        Locale l = Locale.getDefault();
        Locale.setDefault( new Locale( "tr" ) );
        assertEquals( "Id", StringUtils.capitalizeFirstLetter( "id" ) );
        Locale.setDefault( l );
    }
}
