package org.codehaus.plexus.util.interpolation;

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

import org.codehaus.plexus.interpolation.InterpolationException;

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
        throws InterpolationException
    {
        RegexBasedInterpolator rbi = new RegexBasedInterpolator();
        rbi.addValueSource( new ObjectBasedValueSource( this ) );
        String result = rbi.interpolate( "this is a ${this.var}", "this" );

        assertEquals( "this is a testVar", result );
    }

    public void testShouldResolveByContextValue()
        throws InterpolationException
    {
        RegexBasedInterpolator rbi = new RegexBasedInterpolator();

        Map context = new HashMap();
        context.put( "var", "testVar" );

        rbi.addValueSource( new MapBasedValueSource( context ) );

        String result = rbi.interpolate( "this is a ${this.var}", "this" );

        assertEquals( "this is a testVar", result );
    }

    public void testShouldResolveByEnvar()
        throws IOException, InterpolationException
    {
        RegexBasedInterpolator rbi = new RegexBasedInterpolator();

        rbi.addValueSource( new EnvarBasedValueSource() );

        String result = rbi.interpolate( "this is a ${env.HOME}", "this" );

        assertFalse( "this is a ${HOME}".equals( result ) );
    }

    public void testUseAlternateRegex()
        throws Exception
    {
        RegexBasedInterpolator rbi = new RegexBasedInterpolator("\\@\\{(", ")?([^}]+)\\}@");

        Map context = new HashMap();
        context.put( "var", "testVar" );

        rbi.addValueSource( new MapBasedValueSource( context ) );

        String result = rbi.interpolate( "this is a @{this.var}@", "this" );

        assertEquals( "this is a testVar", result );
    }
}
