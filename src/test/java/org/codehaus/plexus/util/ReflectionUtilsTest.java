package org.codehaus.plexus.util;

import junit.framework.TestCase;

import java.util.Map;
import java.util.HashMap;

/*
 * Copyright 2005 The Codehaus.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * This is used to test ReflectionUtils for correctness.
 *
 * @author Jesse McConnell
 * @version $Id:$
 * @see org.codehaus.plexus.util.ReflectionUtils
 */
public final class ReflectionUtilsTest
    extends TestCase
{
    public ReflectionUtilsTestClass testClass = new ReflectionUtilsTestClass();

    protected void setUp() throws Exception
    {

    }

    public void testSimpleVariableAccess()
        throws IllegalAccessException
    {
        assertEquals("woohoo", (String)ReflectionUtils.getValueIncludingSuperclasses( "myString", testClass ) );
    }

    public void testComplexVariableAccess()
        throws IllegalAccessException
    {
        Map map = (Map)ReflectionUtils.getVariablesAndValuesIncludingSuperclasses( testClass );

        Map myMap = (Map)map.get( "myMap" );

        assertEquals( "myValue", (String)myMap.get( "myKey" ) );
        assertEquals( "myOtherValue", (String)myMap.get( "myOtherKey") );

    }

    public void testSuperClassVariableAccess()
        throws IllegalAccessException
    {
        assertEquals("super-duper", (String)ReflectionUtils.getValueIncludingSuperclasses( "mySuperString", testClass ) );
    }

    public void testSettingVariableValue()
        throws IllegalAccessException
    {
        ReflectionUtils.setVariableValueInObject( testClass, "mySettableString", "mySetString" );

        assertEquals("mySetString", (String)ReflectionUtils.getValueIncludingSuperclasses( "mySettableString", testClass ) );


        ReflectionUtils.setVariableValueInObject( testClass, "myParentsSettableString", "myParentsSetString" );

        assertEquals("myParentsSetString", (String)ReflectionUtils.getValueIncludingSuperclasses( "myParentsSettableString", testClass ) );
    }


    private class ReflectionUtilsTestClass
        extends AbstractReflectionUtilsTestClass

    {
        private String myString = "woohoo";
        private String mySettableString;
        private Map myMap = new HashMap();

        public ReflectionUtilsTestClass()
        {
           myMap.put("myKey", "myValue");
           myMap.put( "myOtherKey", "myOtherValue" );
        }
    }


    private class AbstractReflectionUtilsTestClass
    {
        private String mySuperString = "super-duper";
        private String myParentsSettableString;
    }
}
