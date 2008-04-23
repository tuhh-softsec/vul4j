package org.codehaus.plexus.util;

import java.util.Locale;

import junit.framework.TestCase;

/**
 * Test string utils.
 *
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 * @version $Id$
 */
public class StringUtilsTest
    extends TestCase
{

    public void testIsEmpty()
    {
        assertEquals( true, StringUtils.isEmpty( null ) );
        assertEquals( true, StringUtils.isEmpty( "" ) );
        /* TODO: Some day in the future, make this pass (PLXUTILS-73)
        assertEquals( false, StringUtils.isEmpty( " " ) );
        //*/
        assertEquals( false, StringUtils.isEmpty( "foo" ) );
        assertEquals( false, StringUtils.isEmpty( "  foo  " ) );
    }

    public void testIsNotEmpty()
    {
        assertEquals( false, StringUtils.isNotEmpty( null ) );
        assertEquals( false, StringUtils.isNotEmpty( "" ) );
        assertEquals( true, StringUtils.isNotEmpty( " " ) );
        assertEquals( true, StringUtils.isNotEmpty( "foo" ) );
        assertEquals( true, StringUtils.isNotEmpty( "  foo  " ) );
    }

    public void testIsBlank()
    {
        assertEquals( true, StringUtils.isBlank( null ) );
        assertEquals( true, StringUtils.isBlank( "" ) );
        assertEquals( true, StringUtils.isBlank( " \t\r\n" ) );
        assertEquals( false, StringUtils.isBlank( "foo" ) );
        assertEquals( false, StringUtils.isBlank( "  foo  " ) );
    }

    public void testIsNotBlank()
    {
        assertEquals( false, StringUtils.isNotBlank( null ) );
        assertEquals( false, StringUtils.isNotBlank( "" ) );
        assertEquals( false, StringUtils.isNotBlank( " \t\r\n" ) );
        assertEquals( true, StringUtils.isNotBlank( "foo" ) );
        assertEquals( true, StringUtils.isNotBlank( "  foo  " ) );
    }

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

    public void testQuote_EscapeEmbeddedSingleQuotes()
    {
        String src = "This \'is a\' test";
        String check = "\'This \\\'is a\\\' test\'";

        char[] escaped = { '\'', '\"' };
        String result = StringUtils.quoteAndEscape( src, '\'', escaped, '\\', false );

        assertEquals( check, result );
    }

    public void testQuote_EscapeEmbeddedDoubleQuotesAndSpaces()
    {
        String src = "This \"is a\" test";
        String check = "\'This\\ \\\"is\\ a\\\"\\ test\'";

        char[] escaped = { '\'', '\"', ' ' };
        String result = StringUtils.quoteAndEscape( src, '\'', escaped, '\\', false );

        assertEquals( check, result );
    }

    public void testQuote_DontQuoteIfUnneeded()
    {
        String src = "ThisIsATest";

        char[] escaped = { '\'', '\"' };
        String result = StringUtils.quoteAndEscape( src, '\'', escaped, '\\', false );

        assertEquals( src, result );
    }

    public void testQuote_WrapWithSingleQuotes()
    {
        String src = "This is a test";
        String check = "\'This is a test\'";

        char[] escaped = { '\'', '\"' };
        String result = StringUtils.quoteAndEscape( src, '\'', escaped, '\\', false );

        assertEquals( check, result );
    }

    public void testQuote_PreserveExistingQuotes()
    {
        String src = "\'This is a test\'";

        char[] escaped = { '\'', '\"' };
        String result = StringUtils.quoteAndEscape( src, '\'', escaped, '\\', false );

        assertEquals( src, result );
    }

    public void testQuote_WrapExistingQuotesWhenForceIsTrue()
    {
        String src = "\'This is a test\'";
        String check = "\'\\\'This is a test\\\'\'";

        char[] escaped = { '\'', '\"' };
        String result = StringUtils.quoteAndEscape( src, '\'', escaped, '\\', true );

        assertEquals( check, result );
    }

    public void testQuote_ShortVersion_SingleQuotesPreserved()
    {
        String src = "\'This is a test\'";

        String result = StringUtils.quoteAndEscape( src, '\'' );

        assertEquals( src, result );
    }

}
