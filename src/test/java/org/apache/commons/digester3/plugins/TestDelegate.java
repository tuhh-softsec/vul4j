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

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.plugins.PluginRules;
import org.junit.Test;

/**
 * Test cases for Delegate behaviour.
 */

public class TestDelegate
{
    // --------------------------------------------------------------- Test cases
    @Test
    public void testDummy()
    {
        // it is an error if a TestSuite doesn't have at least one test,
        // so here is one...
    }

    public void ignoretestDelegate()
        throws Exception
    {
        // this method tests the Delegate functionality by capturing all
        // data below the specified pattern, and printing it to stdout.
        // I can't for the moment think how to turn this into a unit test,
        // so this test is disabled.
        Digester digester = new Digester();
        PluginRules rc = new PluginRules();
        digester.setRules( rc );

        DumperRule dr = new DumperRule();
        digester.addRule( "root", dr );

        try
        {
            digester.parse( Utils.getInputStream( this, "test1.xml" ) );
        }
        catch ( Exception e )
        {
            throw e;
        }
    }
}
