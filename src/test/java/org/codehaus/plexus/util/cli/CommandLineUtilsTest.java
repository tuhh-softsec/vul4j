package org.codehaus.plexus.util.cli;

import junit.framework.TestCase;

public class CommandLineUtilsTest
    extends TestCase
{

    public void testQuoteArguments()
    {
        try
        {
            String result = CommandLineUtils.quote( "Hello" );
            System.out.println( result );
            assertEquals( "Hello", result );
            result = CommandLineUtils.quote( "Hello World" );
            System.out.println( result );
            assertEquals( "\"Hello World\"", result );
            result = CommandLineUtils.quote( "\"Hello World\"" );
            System.out.println( result );
            assertEquals( "\'\"Hello World\"\'", result );
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }
        try
        {
            CommandLineUtils.quote( "\"Hello \'World\'\'" );
            fail();
        }
        catch ( Exception e )
        {
        }
    }

}
