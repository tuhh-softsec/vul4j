package org.codehaus.plexus.util.interpolation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;


public class RegexBasedInterpolatorTest
    extends TestCase
{
    
    public String getVar()
    {
        return "testVar";
    }
    
    public void testShouldResolveByMy_getVar_Method()
    {
        RegexBasedInterpolator rbi = new RegexBasedInterpolator();
        rbi.addValueSource( new ObjectBasedValueSource( this ) );
        String result = rbi.interpolate( "this is a ${this.var}", "this" );
        
        assertEquals( "this is a testVar", result );
    }

    public void testShouldResolveByContextValue()
    {
        RegexBasedInterpolator rbi = new RegexBasedInterpolator();
        
        Map context = new HashMap();
        context.put( "var", "testVar" );
        
        rbi.addValueSource( new MapBasedValueSource( context ) );
        
        String result = rbi.interpolate( "this is a ${this.var}", "this" );
        
        assertEquals( "this is a testVar", result );
    }

    public void testShouldResolveByEnvar() throws IOException
    {
        RegexBasedInterpolator rbi = new RegexBasedInterpolator();
        
        rbi.addValueSource( new EnvarBasedValueSource() );
        
        String result = rbi.interpolate( "this is a ${env.HOME}", "this" );
        
        assertFalse( "this is a ${HOME}".equals( result ) );
    }

}
