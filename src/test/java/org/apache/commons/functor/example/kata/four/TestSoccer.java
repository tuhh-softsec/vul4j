/*
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
package org.apache.commons.functor.example.kata.four;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * See http://pragprog.com/pragdave/Practices/Kata/KataFour.rdoc,v
 * for more information on this Kata.
 * 
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestSoccer extends TestCase {
    public TestSoccer(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestSoccer.class);
    }
    
    public void testProcess() {
    	// for our soccer example, we want to select the second column of the
    	// line with the minimal difference between the seventh and ninth columns.
        assertEquals(
            "Aston_Villa",
            DataMunger.process(getClass().getResourceAsStream("soccer.txt"),1,6,8));            
    }

}

