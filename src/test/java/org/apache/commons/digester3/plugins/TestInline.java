/* $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.digester3.plugins;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.plugins.PluginCreateRule;
import org.apache.commons.digester3.plugins.PluginRules;
import org.junit.Test;

/**
 * Test cases for declaration of plugin classes "inline" (ie by specifying plugin-class).
 */

public class TestInline
{

    // --------------------------------------------------------------- Test cases
    @Test
    public void testInlineDeclaration()
        throws Exception
    {
        // * tests that plugins can be specified by class, and that the
        // correct class gets loaded.
        // * tests that autosetproperties works
        // * tests that multiple different classes can be loaded via the
        // same plugin rule (ie at the same pattern).
        Digester digester = new Digester();
        PluginRules rc = new PluginRules();
        digester.setRules( rc );

        PluginCreateRule pcr = new PluginCreateRule( Widget.class );
        digester.addRule( "root/widget", pcr );
        digester.addSetNext( "root/widget", "addChild" );

        Container root = new Container();
        digester.push( root );

        try
        {
            digester.parse( Utils.getInputStream( this, "test1.xml" ) );
        }
        catch ( Exception e )
        {
            throw e;
        }

        Object child;
        List<Widget> children = root.getChildren();
        assertNotNull( children );
        assertEquals( 2, children.size() );

        child = children.get( 0 );
        assertNotNull( child );
        assertEquals( TextLabel.class, child.getClass() );
        TextLabel label1 = (TextLabel) child;
        assertEquals( "anonymous", label1.getId() );
        assertEquals( "1", label1.getLabel() );

        child = children.get( 1 );
        assertNotNull( child );
        assertEquals( TextLabel.class, child.getClass() );
        TextLabel label2 = (TextLabel) child;
        assertEquals( "L1", label2.getId() );
        assertEquals( "2", label2.getLabel() );
    }

    @Test
    public void testLeadingSlash()
        throws Exception
    {
        // Tests that PluginRules handles patterns with a leading slash.
        //
        // This test doesn't really belong in this class. If a separate test
        // case class is created for PluginRules, then this method should be
        // moved there.

        Digester digester = new Digester();
        PluginRules rc = new PluginRules();
        digester.setRules( rc );

        PluginCreateRule pcr = new PluginCreateRule( Widget.class );
        digester.addRule( "/root/widget", pcr );
        digester.addSetNext( "/root/widget", "addChild" );

        Container root = new Container();
        digester.push( root );

        try
        {
            digester.parse( Utils.getInputStream( this, "test1.xml" ) );
        }
        catch ( Exception e )
        {
            throw e;
        }

        Object child;
        List<Widget> children = root.getChildren();
        assertNotNull( children );
        assertEquals( 2, children.size() );

        child = children.get( 0 );
        assertNotNull( child );
        assertEquals( TextLabel.class, child.getClass() );
        TextLabel label1 = (TextLabel) child;
        assertEquals( "anonymous", label1.getId() );
        assertEquals( "1", label1.getLabel() );

        child = children.get( 1 );
        assertNotNull( child );
        assertEquals( TextLabel.class, child.getClass() );
        TextLabel label2 = (TextLabel) child;
        assertEquals( "L1", label2.getId() );
        assertEquals( "2", label2.getLabel() );
    }

}
