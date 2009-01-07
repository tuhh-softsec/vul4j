package org.codehaus.plexus.util.xml;

/*
 * Copyright The Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

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

    public void testEqualsIsNullSafe()
        throws XmlPullParserException, IOException
    {
        String testDom = "<configuration><items thing='blah'><item>one</item><item>two</item></items></configuration>";
        Xpp3Dom dom = Xpp3DomBuilder.build( new StringReader( testDom ) );
        Xpp3Dom dom2 = Xpp3DomBuilder.build( new StringReader( testDom ) );

        try
        {
            dom2.attributes = new HashMap();
            dom2.attributes.put( "nullValue", null );
            dom2.attributes.put( null, "nullKey" );
            dom2.childList.clear();
            dom2.childList.add( null );

            assertFalse( dom.equals( dom2 ) );
            assertFalse( dom2.equals( dom ) );

        }
        catch ( NullPointerException ex )
        {
            ex.printStackTrace();
            fail( "\nNullPointerExceptions should not be thrown." );
        }
    }

    public void testShouldOverwritePluginConfigurationSubItemsByDefault()
        throws XmlPullParserException, IOException
    {
        String parentConfigStr = "<configuration><items><item>one</item><item>two</item></items></configuration>";
        Xpp3Dom parentConfig = Xpp3DomBuilder.build( new StringReader( parentConfigStr ) );

        String childConfigStr = "<configuration><items><item>three</item></items></configuration>";
        Xpp3Dom childConfig = Xpp3DomBuilder.build( new StringReader( childConfigStr ) );

        Xpp3Dom result = Xpp3Dom.mergeXpp3Dom( childConfig, parentConfig );
        Xpp3Dom items = result.getChild( "items" );

        assertEquals( 1, items.getChildCount() );

        Xpp3Dom item = items.getChild( 0 );
        assertEquals( "three", item.getValue() );
    }

    public void testShouldMergePluginConfigurationSubItemsWithMergeAttributeSet()
        throws XmlPullParserException, IOException
    {
        String parentConfigStr = "<configuration><items><item>one</item><item>two</item></items></configuration>";
        Xpp3Dom parentConfig = Xpp3DomBuilder.build( new StringReader( parentConfigStr ) );

        String childConfigStr = "<configuration><items combine.children=\"append\"><item>three</item></items></configuration>";
        Xpp3Dom childConfig = Xpp3DomBuilder.build( new StringReader( childConfigStr ) );

        Xpp3Dom result = Xpp3Dom.mergeXpp3Dom( childConfig, parentConfig );
        Xpp3Dom items = result.getChild( "items" );

        assertEquals( 3, items.getChildCount() );

        Xpp3Dom[] item = items.getChildren();

        assertEquals( "one", item[0].getValue() );
        assertEquals( "two", item[1].getValue() );
        assertEquals( "three", item[2].getValue() );
    }
}
