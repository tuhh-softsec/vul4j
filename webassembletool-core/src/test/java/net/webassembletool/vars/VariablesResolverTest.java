/*
 *  Copyright 2010 altha.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package net.webassembletool.vars;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Alexis Thaveau
 */
public class VariablesResolverTest {

    public VariablesResolverTest() {
    }
    
    /**
     * Test of replaceAllVariables method, of class VariablesHelper.
     */
    @Test
    public void testReplaceAllVariables() {
 
        assertFalse(VariablesResolver.containsVariable("novariable"));
        assertFalse(VariablesResolver.containsVariable("$(varTest"));
        assertTrue(VariablesResolver.containsVariable("$(varTest)"));
        assertTrue(VariablesResolver.containsVariable("a string $(varTest) with variable"));
        assertTrue(VariablesResolver.containsVariable("a string $(varTest) with $(varTest) 2 variables"));

        String page = "some(unknownvar1)url with unkown $(unknownvar2) parameters";
        String result = VariablesResolver.replaceAllVariables(page);
        assertEquals(page,result);

        page = "some$(varTest)url";
        result = VariablesResolver.replaceAllVariables(page);
        assertEquals("someTesturl",result);

        page = "some$(varTest)url$(unkwownvar)";
        result = VariablesResolver.replaceAllVariables(page);
        assertEquals("someTesturl$(unkwownvar)",result);

        
    }

}