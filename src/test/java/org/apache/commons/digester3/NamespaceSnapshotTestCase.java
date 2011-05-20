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

package org.apache.commons.digester3;

import static org.apache.commons.digester3.binder.DigesterLoader.newLoader;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.digester3.binder.AbstractRulesModule;
import org.apache.commons.digester3.binder.RuleProvider;
import org.junit.Test;
import org.xml.sax.Attributes;

/**
 * Tests namespace snapshotting.
 */

public class NamespaceSnapshotTestCase
{

    /**
     * A test case specific helper rule.
     */
    static class NamespaceSnapshotRule
        extends Rule
    {
        /**
         * @see Rule#begin(String, String, Attributes)
         */
        @Override
        public final void begin( final String namespace, final String name, final Attributes attributes )
        {
            Digester d = getDigester();
            Map<String, String> namespaces = d.getCurrentNamespaces();
            ( (NamespacedBox) d.peek() ).setNamespaces( namespaces );
        }

        public static class Provider implements RuleProvider<NamespaceSnapshotRule>
        {

            /**
             * {@inheritDoc}
             */
            public NamespaceSnapshotRule get()
            {
                return new NamespaceSnapshotRule();
            }

        }

    }

    /**
     * Namespace snapshot test case.
     */
    @Test
    public void testNamespaceSnapshots()
        throws Exception
    {

        Digester digester = newLoader( new AbstractRulesModule()
        {

            @Override
            protected void configure()
            {
                forPattern( "box" ).createObject().ofType( NamespacedBox.class )
                    .then()
                    .setProperties()
                    .then()
                    .addRuleCreatedBy( new NamespaceSnapshotRule.Provider() );
                forPattern( "box/subBox" ).createObject().ofType( NamespacedBox.class )
                    .then()
                    .setProperties()
                    .then()
                    .addRuleCreatedBy( new NamespaceSnapshotRule.Provider() )
                    .then()
                    .setNext( "addChild" );
            }

        }).setNamespaceAware( true ).newDigester();

        NamespacedBox root = digester.parse( getInputStream( "Test11.xml" ) );

        Map<String, String> nsmap = root.getNamespaces();
        assertEquals( 3, nsmap.size() );
        assertEquals( "", nsmap.get( "" ) );
        assertEquals( "http://commons.apache.org/digester/Foo", nsmap.get( "foo" ) );
        assertEquals( "http://commons.apache.org/digester/Bar", nsmap.get( "bar" ) );

        List<Box> children = root.getChildren();
        assertEquals( 3, children.size() );

        NamespacedBox child1 = (NamespacedBox) children.get( 0 );
        nsmap = child1.getNamespaces();
        assertEquals( 3, nsmap.size() );
        assertEquals( "", nsmap.get( "" ) );
        assertEquals( "http://commons.apache.org/digester/Foo1", nsmap.get( "foo" ) );
        assertEquals( "http://commons.apache.org/digester/Bar1", nsmap.get( "bar" ) );

        NamespacedBox child2 = (NamespacedBox) children.get( 1 );
        nsmap = child2.getNamespaces();
        assertEquals( 5, nsmap.size() );
        assertEquals( "", nsmap.get( "" ) );
        assertEquals( "http://commons.apache.org/digester/Foo", nsmap.get( "foo" ) );
        assertEquals( "http://commons.apache.org/digester/Bar", nsmap.get( "bar" ) );
        assertEquals( "http://commons.apache.org/digester/Alpha", nsmap.get( "alpha" ) );
        assertEquals( "http://commons.apache.org/digester/Beta", nsmap.get( "beta" ) );

        NamespacedBox child3 = (NamespacedBox) children.get( 2 );
        nsmap = child3.getNamespaces();
        assertEquals( 4, nsmap.size() );
        assertEquals( "", nsmap.get( "" ) );
        assertEquals( "http://commons.apache.org/digester/Foo3", nsmap.get( "foo" ) );
        assertEquals( "http://commons.apache.org/digester/Alpha", nsmap.get( "alpha" ) );
        assertEquals( "http://commons.apache.org/digester/Bar", nsmap.get( "bar" ) );

    }

    // ------------------------------------------------ Utility Support Methods

    /**
     * Return an appropriate InputStream for the specified test file (which must be inside our current package.
     * 
     * @param name Name of the test file we want
     * @exception IOException if an input/output error occurs
     */
    protected InputStream getInputStream( String name )
        throws IOException
    {

        return ( this.getClass().getResourceAsStream( "/org/apache/commons/digester3/" + name ) );

    }

}
