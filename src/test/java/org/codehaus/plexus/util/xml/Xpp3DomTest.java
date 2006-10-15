package org.codehaus.plexus.util.xml;

import junit.framework.TestCase;

public class Xpp3DomTest
    extends TestCase
{

    public void testShouldPerformAppendAtFirstSubElementLevel()
    {
        // create the dominant DOM
        Xpp3Dom t1 = new Xpp3Dom( "top" );
        t1.setAttribute( Xpp3Dom.CHILDREN_COMBINATION_MODE_ATTRIBUTE, Xpp3Dom.CHILDREN_COMBINATION_APPEND );

        Xpp3Dom t1s1 = new Xpp3Dom( "topsub1" );
        t1s1.setValue( "t1s1Value" );

        t1.addChild( t1s1 );

        // create the recessive DOM
        Xpp3Dom t2 = new Xpp3Dom( "top" );

        Xpp3Dom t2s1 = new Xpp3Dom( "topsub1" );
        t2s1.setValue( "t2s1Value" );

        t2.addChild( t2s1 );

        // merge and check results.
        Xpp3Dom result = Xpp3Dom.mergeXpp3Dom( t1, t2 );

        assertEquals( 2, result.getChildren( "topsub1" ).length );
    }

    public void testShouldOverrideAppendAndDeepMerge()
    {
        // create the dominant DOM
        Xpp3Dom t1 = new Xpp3Dom( "top" );
        t1.setAttribute( Xpp3Dom.CHILDREN_COMBINATION_MODE_ATTRIBUTE, Xpp3Dom.CHILDREN_COMBINATION_APPEND );

        Xpp3Dom t1s1 = new Xpp3Dom( "topsub1" );
        t1s1.setValue( "t1s1Value" );

        t1.addChild( t1s1 );

        // create the recessive DOM
        Xpp3Dom t2 = new Xpp3Dom( "top" );

        Xpp3Dom t2s1 = new Xpp3Dom( "topsub1" );
        t2s1.setValue( "t2s1Value" );

        t2.addChild( t2s1 );

        // merge and check results.
        Xpp3Dom result = Xpp3Dom.mergeXpp3Dom( t1, t2, Boolean.TRUE );

        assertEquals( 1, result.getChildren( "topsub1" ).length );
    }

    public void testShouldPerformSelfOverrideAtTopLevel()
    {
        // create the dominant DOM
        Xpp3Dom t1 = new Xpp3Dom( "top" );
        t1.setAttribute( "attr", "value" );

        t1.setAttribute( Xpp3Dom.SELF_COMBINATION_MODE_ATTRIBUTE, Xpp3Dom.SELF_COMBINATION_OVERRIDE );

        // create the recessive DOM
        Xpp3Dom t2 = new Xpp3Dom( "top" );
        t2.setAttribute( "attr2", "value2" );
        t2.setValue( "t2Value" );

        // merge and check results.
        Xpp3Dom result = Xpp3Dom.mergeXpp3Dom( t1, t2 );

        assertEquals( 2, result.getAttributeNames().length );
        assertNull( result.getValue() );
    }

    public void testShouldMergeValuesAtTopLevelByDefault()
    {
        // create the dominant DOM
        Xpp3Dom t1 = new Xpp3Dom( "top" );
        t1.setAttribute( "attr", "value" );

        // create the recessive DOM
        Xpp3Dom t2 = new Xpp3Dom( "top" );
        t2.setAttribute( "attr2", "value2" );
        t2.setValue( "t2Value" );

        // merge and check results.
        Xpp3Dom result = Xpp3Dom.mergeXpp3Dom( t1, t2 );

        // this is still 2, since we're not using the merge-control attribute.
        assertEquals( 2, result.getAttributeNames().length );
        
        assertEquals( result.getValue(), t2.getValue() );
    }

    public void testShouldMergeValuesAtTopLevel()
    {
        // create the dominant DOM
        Xpp3Dom t1 = new Xpp3Dom( "top" );
        t1.setAttribute( "attr", "value" );

        t1.setAttribute( Xpp3Dom.SELF_COMBINATION_MODE_ATTRIBUTE, Xpp3Dom.SELF_COMBINATION_MERGE );

        // create the recessive DOM
        Xpp3Dom t2 = new Xpp3Dom( "top" );
        t2.setAttribute( "attr2", "value2" );
        t2.setValue( "t2Value" );

        // merge and check results.
        Xpp3Dom result = Xpp3Dom.mergeXpp3Dom( t1, t2 );

        assertEquals( 3, result.getAttributeNames().length );
        assertEquals( result.getValue(), t2.getValue() );
    }

    public void testNullAttributeNameOrValue()
    {
        Xpp3Dom t1 = new Xpp3Dom( "top" );
        try
        {
            t1.setAttribute( "attr", null );
            fail( "null attribute values shouldn't be allowed" );
        }
        catch ( NullPointerException e )
        {
        }
        t1.toString();
        try
        {
            t1.setAttribute( null, "value" );
            fail( "null attribute names shouldn't be allowed" );
        }
        catch ( NullPointerException e )
        {
        }
        t1.toString();
    }

    public void testEquals()
    {
        Xpp3Dom dom = new Xpp3Dom( "top" );

        assertEquals( dom, dom );
        assertFalse( dom.equals( null ) );
        assertFalse( dom.equals( new Xpp3Dom( (String) null ) ) );
    }
}
