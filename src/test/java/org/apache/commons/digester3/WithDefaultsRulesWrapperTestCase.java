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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.apache.commons.digester3.Rule;
import org.apache.commons.digester3.RulesBase;
import org.apache.commons.digester3.WithDefaultsRulesWrapper;
import org.junit.Test;

/**
 * Test case for WithDefaultsRulesWrapper
 * 
 * @author Robert Burrell Donkin
 * @version $Revision$ $Date$
 */

public class WithDefaultsRulesWrapperTestCase
{

    @Test
    public void testClear()
    {
        // test clear wrapped
        WithDefaultsRulesWrapper rules = new WithDefaultsRulesWrapper( new RulesBase() );
        rules.add( "alpha", new TestRule( "Tom" ) );
        rules.add( "alpha", new TestRule( "Dick" ) );
        rules.add( "alpha", new TestRule( "Harry" ) );

        assertNotNull( "Rules should not be null", rules.rules() );
        assertEquals( "Wrong number of rules registered (1)", 3, rules.rules().size() );
        rules.clear();
        assertEquals( "Clear Failed (1)", 0, rules.rules().size() );

        // mixed
        rules.add( "alpha", new TestRule( "Tom" ) );
        rules.add( "alpha", new TestRule( "Dick" ) );
        rules.add( "alpha", new TestRule( "Harry" ) );
        rules.addDefault( new TestRule( "Roger" ) );
        assertEquals( "Wrong number of rules registered (2)", 4, rules.rules().size() );
        rules.clear();
        assertEquals( "Clear Failed (2)", 0, rules.rules().size() );

        rules.addDefault( new TestRule( "Roger" ) );
        assertEquals( "Wrong number of rules registered (3)", 1, rules.rules().size() );
        rules.clear();
        assertEquals( "Clear Failed (3)", 0, rules.rules().size() );
    }

    @Test
    public void testRules()
    {
        // test rules
        WithDefaultsRulesWrapper rules = new WithDefaultsRulesWrapper( new RulesBase() );
        rules.add( "alpha", new TestRule( "Tom" ) );
        rules.add( "alpha", new TestRule( "Dick" ) );
        rules.addDefault( new TestRule( "Roger" ) );
        rules.add( "alpha", new TestRule( "Harry" ) );

        assertNotNull( "Rules should not be null", rules.rules() );
        assertEquals( "Wrong order (1)", "Tom", ( (TestRule) rules.rules().get( 0 ) ).getIdentifier() );
        assertEquals( "Wrong order (2)", "Dick", ( (TestRule) rules.rules().get( 1 ) ).getIdentifier() );
        assertEquals( "Wrong order (3)", "Roger", ( (TestRule) rules.rules().get( 2 ) ).getIdentifier() );
        assertEquals( "Wrong order (4)", "Harry", ( (TestRule) rules.rules().get( 3 ) ).getIdentifier() );
    }

    @Test
    public void testMatch()
    {
        // test no defaults
        WithDefaultsRulesWrapper rules = new WithDefaultsRulesWrapper( new RulesBase() );
        rules.add( "alpha", new TestRule( "Tom" ) );
        rules.add( "alpha", new TestRule( "Dick" ) );
        rules.add( "alpha", new TestRule( "Harry" ) );
        rules.addDefault( new TestRule( "Roger" ) );
        rules.addDefault( new TestRule( "Rabbit" ) );

        List<Rule> matches = rules.match( "", "alpha", null, null );
        assertEquals( "Wrong size (1)", 3, matches.size() );
        assertEquals( "Wrong order (1)", "Tom", ( (TestRule) matches.get( 0 ) ).getIdentifier() );
        assertEquals( "Wrong order (2)", "Dick", ( (TestRule) matches.get( 1 ) ).getIdentifier() );
        assertEquals( "Wrong order (3)", "Harry", ( (TestRule) matches.get( 2 ) ).getIdentifier() );

        matches = rules.match( "", "not-alpha", null, null );
        assertEquals( "Wrong size (2)", 2, matches.size() );
        assertEquals( "Wrong order (4)", "Roger", ( (TestRule) matches.get( 0 ) ).getIdentifier() );
        assertEquals( "Wrong order (5)", "Rabbit", ( (TestRule) matches.get( 1 ) ).getIdentifier() );
    }
}
