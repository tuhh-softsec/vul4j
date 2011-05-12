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
import org.apache.commons.digester3.plugins.PluginDeclarationRule;
import org.apache.commons.digester3.plugins.PluginRules;
import org.junit.Test;

/**
 * Test cases for basic PluginDeclarationRule behaviour.
 */

public class TestDeclaration
{

    // --------------------------------------------------------------- Test cases
    @Test
    public void testPredeclaration()
        throws Exception
    {
        // * tests that rules can be declared via a PluginDeclarationRule

        Digester digester = new Digester();
        PluginRules rc = new PluginRules();
        digester.setRules( rc );

        PluginDeclarationRule pdr = new PluginDeclarationRule();
        digester.addRule( "root/plugin", pdr );

        PluginCreateRule pcr = new PluginCreateRule( Widget.class );
        digester.addRule( "root/widget", pcr );
        digester.addSetNext( "root/widget", "addChild" );

        Container root = new Container();
        digester.push( root );

        try
        {
            digester.parse( Utils.getInputStream( this, "test3.xml" ) );
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
        assertEquals( "label1", ( (TextLabel) child ).getLabel() );

        child = children.get( 1 );
        assertNotNull( child );
        assertEquals( TextLabel.class, child.getClass() );
        assertEquals( "label2", ( (TextLabel) child ).getLabel() );
    }
}
