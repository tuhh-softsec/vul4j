/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */

package org.apache.directory.shared.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import com.mycila.junit.concurrent.Concurrency;
import com.mycila.junit.concurrent.ConcurrentJunitRunner;
import org.apache.directory.shared.util.MethodUtils;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test case for {@link MethodUtils}.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrency()
public class MethodUtilsTest
{
    private static class TestClass
    {
        @SuppressWarnings("unused")
        public static void methodA( String str )
        {
            
        }
        
        @SuppressWarnings("unused")
        public static void methodB( Collection<?> c )
        {
            
        }
    }
    
    @Test
    public void testSameBehaviourOfStandardGetMethod()
    {
        Method m1 = null;
        Method m2 = null;
        
        try
        {
            m1 = TestClass.class.getMethod( "methodA", new Class[] { String.class } );
        }
        catch ( SecurityException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch ( NoSuchMethodException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        try
        {
            m2 = MethodUtils.getAssignmentCompatibleMethod(TestClass.class, "methodA", new Class[]{String.class});
        }
        catch ( NoSuchMethodException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        assertEquals( m1, m2 );
        
    }
    
    @Test
    public void testNewBehaviourOfAssignmentCompatibleGetMethod()
    {
        Method m2 = null;
        
        try
        {
            TestClass.class.getMethod( "methodB", new Class[] { ArrayList.class } );
            fail( "We should not have come here." );
        }
        catch ( SecurityException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch ( NoSuchMethodException e )
        {
            assertNotNull( e );
        }
        
        try
        {
            m2 = MethodUtils.getAssignmentCompatibleMethod(TestClass.class, "methodB", new Class[]{ArrayList.class});
        }
        catch ( NoSuchMethodException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        assertNotNull( m2 );
        
    }

}
