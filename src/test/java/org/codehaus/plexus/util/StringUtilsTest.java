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
        assertEquals( "Id", StringUtils.capitalizeFirstLetter( "Id" ) );
    }

    public void testCapitalizeFirstLetterTurkish()
    {
        Locale l = Locale.getDefault();
        Locale.setDefault( new Locale( "tr" ) );
        assertEquals( "Id", StringUtils.capitalizeFirstLetter( "id" ) );
        assertEquals( "Id", StringUtils.capitalizeFirstLetter( "Id" ) );
        Locale.setDefault( l );
    }

    public void testLowerCaseFirstLetter()
    {
        assertEquals( "id", StringUtils.lowercaseFirstLetter( "id" ) );
        assertEquals( "id", StringUtils.lowercaseFirstLetter( "Id" ) );
    }

    public void testLowerCaseFirstLetterTurkish()
    {
        Locale l = Locale.getDefault();
        Locale.setDefault( new Locale( "tr" ) );
        assertEquals( "id", StringUtils.lowercaseFirstLetter( "id" ) );
        assertEquals( "id", StringUtils.lowercaseFirstLetter( "Id" ) );
        Locale.setDefault( l );
    }

    public void testRemoveAndHump()
    {
        assertEquals( "Id", StringUtils.removeAndHump( "id", "-" ) );
        assertEquals( "SomeId", StringUtils.removeAndHump( "some-id", "-" ) );
    }

    public void testRemoveAndHumpTurkish()
    {
        Locale l = Locale.getDefault();
        Locale.setDefault( new Locale( "tr" ) );
        assertEquals( "Id", StringUtils.removeAndHump( "id", "-" ) );
        assertEquals( "SomeId", StringUtils.removeAndHump( "some-id", "-" ) );
        Locale.setDefault( l );
    }

}
